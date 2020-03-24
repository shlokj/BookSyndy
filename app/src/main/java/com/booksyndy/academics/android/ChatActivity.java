package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Adapters.MessageAdapter;
import com.booksyndy.academics.android.Data.Chat;
import com.booksyndy.academics.android.Data.Messages;
import com.booksyndy.academics.android.Data.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, EventListener<DocumentSnapshot>, MessageAdapter.OnMessageSelectedListener, MessageAdapter.OnMessageLongSelectedListerner {
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    private static final String TAG = "CHAT_ACTIVITY";
    /*firebase */
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private DatabaseReference RootRef,NotificationRef;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private DocumentReference messageSenderRef;
    private CircleImageView visitor_profile_picture;
    private String receiver_user_id,visit_user_name,visit_image,message_sender_id,curUserName,defaultMessage;
    private TextView visitor_name,userLastSeen;
    private ImageView mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;
    private Menu menu;

    private final List<Messages> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private SharedPreferences userPref,chatPref;
    private SharedPreferences.Editor editor;

    private String saveCurrentTime, saveCurrentDate;

    //chats entry
    private Map chatBodyDetails;
    private User user = null;
    private ListenerRegistration  messageSenderRegistration;

    private boolean firstOpen=true,phoneNumberPublic;


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
        toolbar.setOnClickListener(this);
        userPref = getSharedPreferences(getString(R.string.UserPref),0);
        chatPref = getSharedPreferences(getString(R.string.ChatPref),0);
        curUserName = userPref.getString(getString(R.string.p_userid),"");
        visitor_profile_picture = findViewById(R.id.visit_profile_image);
        mPhotoPickerButton =  findViewById(R.id.photoPickerButton);
        mMessageEditText = findViewById(R.id.messageEditText);
        visitor_name = findViewById(R.id.visit_profile_name);
        userLastSeen =  findViewById(R.id.user_last_seen);
        userLastSeen.setVisibility(View.GONE);
        mSendButton = findViewById(R.id.sendButton);
        userMessagesList =  findViewById(R.id.private_messages_list_of_users);
        mSendButton.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        NotificationRef = RootRef.child("Notifications");
        mFirebaseStorage = FirebaseStorage.getInstance();
        bookPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // to change status bar color
        Window window = ChatActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ChatActivity.this,R.color.colorPrimaryDark));

        initializeChatRoom();
        displayMessages();
        //DisplayLastSeen();

        mMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstOpen) {
                    userMessagesList.scrollToPosition(messagesList.size()- 1);
                    firstOpen=false;
                }
            }
        });
    }

    private void displayMessages() {

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

    private void initializeChatRoom() {
        /* populate visitors details */
        visit_image = getIntent().getStringExtra("visit_image");
        receiver_user_id = getIntent().getStringExtra("visit_user_id");
        visit_user_name = getIntent().getStringExtra("visit_user_name");
        defaultMessage = getIntent().getStringExtra("default_message");
        phoneNumberPublic = getIntent().getBooleanExtra("PUBLIC_PHONE",false);
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
        messageAdapter = new MessageAdapter(messagesList,this,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        if (defaultMessage!=null && defaultMessage.length()!=0) {
            mMessageEditText.setText(defaultMessage);
            mMessageEditText.setSelectAllOnFocus(true);
        }

    }

    private void updateChatPage(){
        if(user != null){
            final String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
           final DocumentReference chat_sender_ref = mFireStore.collection("chats").document(message_sender_id).collection("receiver_chats").document(receiver_user_id);
           final DocumentReference chat_receiver_ref = mFireStore.collection("chats").document(receiver_user_id).collection("receiver_chats").document(message_sender_id);

            final Chat senderchat = new Chat(visit_image, visit_user_name, "online",receiver_user_id,timeStamp,timeStamp);

            final Chat receiverchat = new Chat(user.getImageUrl(), user.getUserId(), "online",message_sender_id,timeStamp,null);

            chat_sender_ref.set(senderchat);
            chat_receiver_ref.set(receiverchat);
//
//                      chat_sender_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        //update here
//                        chat_sender_ref.update("imageUrl",visit_image,"userName",visit_user_name,"timestamp",timeStamp);
//                    }
//                    else{
//                        //set here
//                        chat_sender_ref.set(senderchat);
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "sender: udpateChatPage",e);
//                }
//            });
//
//            chat_receiver_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        //update here
//                        chat_receiver_ref.update("imageUrl",user.getImageUrl(),"userName",user.getUserId(),"timestamp",timeStamp,"lstMsgTime",timeStamp);
//                    }
//                    else{
//                        //set here
//                        chat_receiver_ref.set(receiverchat);
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d(TAG, "sender: udpateChatPage",e);
//                }
//            });

        }


    }



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
            case R.id.chat_toolbar:
                Intent viewProfile = new Intent(ChatActivity.this, ViewUserProfileActivity.class);
                viewProfile.putExtra("USER_PHONE", receiver_user_id);
                viewProfile.putExtra("USER_NAME", "");
                viewProfile.putExtra("USER_ID", visit_user_name);
                viewProfile.putExtra("USER_PHOTO", visit_image);
                viewProfile.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                startActivity(viewProfile);
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
    protected void onStart() {
        super.onStart();
        if(messageSenderRegistration == null){
            messageSenderRef.addSnapshotListener(this);
        }
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());

        editor = chatPref.edit();
        editor.putString(visit_user_name,timeStamp);
        editor.apply();

    }

    @Override
    protected void onStop() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());

        editor = chatPref.edit();
        editor.putString(visit_user_name,timeStamp);
        editor.apply();
        super.onStop();
        if(messageSenderRegistration != null){
            messageSenderRegistration.remove();
            messageSenderRegistration = null;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_indiv_chat, menu);

        return true;
    }


        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteChat:
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(ChatActivity.this);
                dBuilder.setTitle("Delete chat");
                dBuilder.setIcon(R.drawable.ic_delete_24px_black);
                dBuilder.setMessage("Permanently delete this chat for you?");
                dBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //delte chat
                        String messageSenderRef = "Messages/" + message_sender_id + "/" + receiver_user_id;
                        DocumentReference chat_sender_ref = mFireStore.collection("chats").document(message_sender_id).collection("receiver_chats").document(receiver_user_id);

                        RootRef.child(messageSenderRef).removeValue();
                        chat_sender_ref.delete();


                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                dBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dBuilder.show();
                break;
            case R.id.reportUserChat:
                AlertDialog.Builder rBuilder = new AlertDialog.Builder(ChatActivity.this);
                rBuilder.setTitle("Report listing");
                rBuilder.setIcon(R.drawable.ic_report_24px_outlined);
                rBuilder.setMessage(Html.fromHtml("Are you sure you want to report <b>"+visit_user_name+"</b>?"));
                rBuilder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // code to report
                        reportUser();
                        Toast.makeText(getApplicationContext(),"Reported",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                rBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                rBuilder.show();
                break;


            case android.R.id.home:
                this.finish();
                break;
        }


        return true;
    }

    private void reportUser() {
        if(!checkConnection(this)){
            Toast.makeText(this, "InterNet Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if( mFireStore!= null) {

            final DocumentReference reportRef = mFireStore.collection("report_user").document(receiver_user_id);
            final DocumentReference userRef = mFireStore.collection("users").document(receiver_user_id);

            reportRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if(snapshot.exists()){
                        //update
                        reportRef.update("Report Count", FieldValue.increment(1));
                    }
                    else{
                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                            if(snapshot != null && snapshot.exists()){
                                Map<String,Object> userDetails = new HashMap<>();
                                userDetails.put("userRef",snapshot.toObject(User.class));
                                userDetails.put("Reported By",message_sender_id);
                                userDetails.put("Report Count", FieldValue.increment(1));
                                reportRef.set(userDetails);
                            }
                            }
                        });
                        //create

                    }
                }
            });



        }

    }

    private void SendMessage(String msg, String messageType)
    {
        final String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        final String messageText =  msg;

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Nothing to send", Toast.LENGTH_SHORT).show();
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
            messageTextBody.put("LstMsgTime", timeStamp);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            mMessageEditText.setText("");

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
//                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        //populateChatDetails();
                        updateChatPage();
                        HashMap<String,String> mNotification = new HashMap<>();
                        mNotification.put("from",message_sender_id);
                        mNotification.put("type","message");
                        mNotification.put("message",messageText);
                        mNotification.put("fname",curUserName);

                        NotificationRef.child(receiver_user_id).push().setValue(mNotification);
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
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


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    @Override
    public void OnMessageSelected(Messages message) {
        if(message.getType().equals("text")){

        }
        else{

            Intent viewPic = new Intent(ChatActivity.this,ViewPictureActivity.class);
            viewPic.putExtra("IMAGE_STR",message.getMessage());
            startActivity(viewPic);
        }


    }

    @Override
    public void OnMessagesLongSelected(Messages message) {
        if (message.getType().equals("text")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("message", message.getMessage());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Message copied!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}