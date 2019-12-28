package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import co.in.prodigyschool.passiton.Adapters.MessageAdapter;
import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.Data.Messages;
import co.in.prodigyschool.passiton.Data.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, EventListener<DocumentSnapshot> {
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    private static final String TAG = "CHAT_ACTIVITY";
    /*firebase */
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private DatabaseReference RootRef;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private DocumentReference messageSenderRef;
    private CircleImageView visitor_profile_picture;
    private String receiver_user_id,visit_user_name,visit_image,message_sender_id;
    private TextView visitor_name,userLastSeen;
    private ImageView mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private final List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;

    //chats entry
    private Map chatBodyDetails;
    private User user = null;
    private ListenerRegistration  messageSenderRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        visitor_profile_picture = findViewById(R.id.visit_profile_image);
        mPhotoPickerButton =  findViewById(R.id.photoPickerButton);
        mMessageEditText = findViewById(R.id.messageEditText);
        visitor_name = findViewById(R.id.visit_profile_name);
        userLastSeen =  findViewById(R.id.user_last_seen);
        mSendButton = findViewById(R.id.sendButton);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        bookPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        Window window = ChatActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ChatActivity.this,R.color.colorPrimaryDark));

        initializeChatRoom();
        DisplayLastSeen();

    }

    private void initializeChatRoom() {
        /* populate visitors details */
        visit_image = getIntent().getStringExtra("visit_image");
        receiver_user_id = getIntent().getStringExtra("visit_user_id");
        visit_user_name = getIntent().getStringExtra("visit_user_name");
        Glide.with(getApplicationContext()).load(visit_image).into(visitor_profile_picture);
        visitor_name.setText(visit_user_name);
        message_sender_id = Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber();
        messageSenderRef = mFireStore.collection("users").document(message_sender_id);
        messageSenderRegistration = messageSenderRef.addSnapshotListener(this);

        /* buttons and listeners */
        mSendButton.setOnClickListener(this);
        mPhotoPickerButton.setOnClickListener(this);

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        /* messages adpater */
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList =  findViewById(R.id.private_messages_list_of_users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

    }

    private void updateChatPage(){
        if(user != null){
           DocumentReference chat_sender_ref = mFireStore.collection("chats").document(message_sender_id).collection("receiver_chats").document(receiver_user_id);
           DocumentReference chat_receiver_ref = mFireStore.collection("chats").document(receiver_user_id).collection("receiver_chats").document(message_sender_id);
            Chat senderchat = new Chat(visit_image, visit_user_name, "online",receiver_user_id);
            Chat receiverchat = new Chat(user.getImageUrl(), user.getUserId(), "online",message_sender_id);

            Task t1 = chat_sender_ref.set(senderchat);
            Task t2 = chat_receiver_ref.set(receiverchat);
            Tasks.whenAll(t1,t2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: chat page updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Failed: chat page update",e);
                }
            });
        }


    }
//
//    private void populateChatDetails() {
//        if(user != null) {
//            String chat_sender_ref = "chats/" + message_sender_id + "/" + receiver_user_id;
//            String chat_receiver_ref = "chats/" + receiver_user_id + "/" + message_sender_id;
//            DatabaseReference userChatKeyRef = RootRef.child("chats").child(message_sender_id).child(receiver_user_id).push();
//            String chatPushId = userChatKeyRef.getKey();
//            Chat senderchat = new Chat(visit_image, visit_user_name, "online");
//            Chat receiverchat = new Chat(user.getImageUrl(), user.getUserId(), "online");
//
//            Map chatBodyDetails = new HashMap();
//            chatBodyDetails.put(chat_sender_ref , senderchat);
//            chatBodyDetails.put(chat_receiver_ref , receiverchat);
//
//            RootRef.updateChildren(chatBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if (task.isSuccessful()) {
//                        Log.d(TAG, "onComplete: chat update successful");
//                    } else {
//                        Log.d(TAG, "onComplete: chat update failed", task.getException());
//                    }
//                }
//            });
//        }
//        else{
//            Log.d(TAG, "populateChatDetails: failed with user object null");
//        }
//
//    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photoPickerButton:

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.setType("image/*");
                startActivityForResult(pickPhoto, RC_PHOTO_PICKER);
                break;
            case R.id.sendButton:
                SendMessage(mMessageEditText.getText().toString().trim(),"text");
                break;
                default:
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case RC_PHOTO_PICKER:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    storeBookImage(selectedImage);
                }
                break;
        }
    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(receiver_user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(message_sender_id).child(receiver_user_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesList.clear();
        if(messageSenderRegistration != null){
            messageSenderRegistration.remove();
            messageSenderRegistration = null;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        messagesList.clear();
    }


    private void SendMessage(String msg,String messageType)
    {
        String messageText =  msg;

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + message_sender_id + "/" + receiver_user_id;
            String messageReceiverRef = "Messages/" + receiver_user_id + "/" + message_sender_id;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(message_sender_id).child(receiver_user_id).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", messageType);
            messageTextBody.put("from", message_sender_id);
            messageTextBody.put("to", receiver_user_id);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        //populateChatDetails();
                        updateChatPage();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    mMessageEditText.setText("");
                }
            });



        }
    }
//funciton to send image in chats
    private void storeBookImage(Uri selectedImageUri) {
        //show progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        try {
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
            // Get a reference to store file at book_photos/<FILENAME>
            final StorageReference photoRef = bookPhotosStorageReference.child(timeStamp + "_" + selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "then: failure download url", task.getException());
                        progressDialog.dismiss();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString();
//                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        SendMessage(url,"photo");
                        Log.d(TAG, "onComplete: success url: " + url);
                    } else {
                        // Handle failures
                        progressDialog.dismiss();
                        Log.e(TAG, "onComplete: failure", task.getException());
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }



    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if(e != null){
            Log.w(TAG, "onEvent: error",e );
        return;
        }

        user = snapshot.toObject(User.class);
    }
}