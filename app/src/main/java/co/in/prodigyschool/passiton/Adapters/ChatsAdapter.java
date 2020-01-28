package co.in.prodigyschool.passiton.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.Data.Messages;
import co.in.prodigyschool.passiton.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {

    public static String TAG = "CHATADAPTER";

    private Context context;
    private List<Chat> chatList;
    private String lastMessage,lastMsgTime;
    private String curUserPhone;
    private SharedPreferences chatPref;
    private SimpleDateFormat dateFormat;
    private CollectionReference RootRef;


    public interface OnChatSelectedListener {

        void OnChatSelected(Chat chat);

    }

    private OnChatSelectedListener mListener;


    public ChatsAdapter(Context context, List<Chat> chatList,OnChatSelectedListener listener){
        this.context = context;
        this.chatList = chatList;
        mListener = listener;
        curUserPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        chatPref = context.getSharedPreferences(context.getString(R.string.ChatPref),0);
        dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        RootRef = FirebaseFirestore.getInstance().collection("chats").document(curUserPhone).collection("receiver_chats");

    }

    public void setChatList(List<Chat> chatList){
        this.chatList = chatList;
        notifyDataSetChanged();
        onDataChanged();
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        final Chat chatItem = chatList.get(position);
        final  String imageUrl = chatItem.getImageUrl();
        final String username = chatItem.getUserName();
        //final String userStatus = chatItem.getUserStatus();


        Glide.with(holder.profileImage.getContext())
                .load(imageUrl)
                .into(holder.profileImage);
        holder.userName.setText(username);
        holder.userStatus.setText("offline");
        setLastMessage(chatItem.getDocumentId(),chatItem.getUserName(),holder.userStatus,holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnChatSelected(chatItem);
                }
            }
        });


    }



    private void setLastMessage(String userId, final String userName, final TextView lastMessageView, final ChatsViewHolder holder){
            lastMessage = "default";
        final String curTimeStamp = chatPref.getString(userName,null);
        lastMsgTime = null;
       DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(curUserPhone).child(userId);

       messageRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               boolean fromMe=false;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages message = snapshot.getValue(Messages.class);

                    if(message != null){
                        lastMessage = message.getMessage();
                        lastMsgTime = message.getLstMsgTime();
                        if(!message.getType().equals("text")){
                            lastMessage = "Image";
                        }
                        fromMe = message.getFrom().equals(curUserPhone);
                    }
                }

                switch (lastMessage){
                    case "default":
                        lastMessageView.setText("");
                        break;
                        default:
                            if (fromMe) {
                                lastMessageView.setText("You: "+lastMessage);
                            }
                            else {
                                lastMessageView.setText(lastMessage);
                                if(curTimeStamp != null && lastMsgTime != null){
                                    try {
//                                        Log.d(TAG, "showUnreadChats: userid"+userName+" lastSeen: "+curTimeStamp+" lastMessage: "+lastMsgTime);
                                        Date lastSeen = dateFormat.parse(curTimeStamp);
                                        Date lastMsg = dateFormat.parse(lastMsgTime);

                                        if(lastSeen.before(lastMsg)){
                                            holder.userName.setTypeface(holder.userName.getTypeface(), Typeface.BOLD);
                                            holder.userStatus.setTypeface(holder.userStatus.getTypeface(), Typeface.BOLD);
                                            holder.userName.setTypeface(Typeface.DEFAULT_BOLD);
                                            holder.userStatus.setTypeface(Typeface.DEFAULT_BOLD);
//                               holder.userName.setTextColor(context.getResources().getColor(R.color.green));
                                            holder.unread_icon.setVisibility(View.VISIBLE);
                                        }
                                        else{
                                            holder.userName.setTypeface(holder.userName.getTypeface(), Typeface.NORMAL);
                                            holder.userStatus.setTypeface(holder.userName.getTypeface(), Typeface.NORMAL);
                                            holder.userName.setTypeface(Typeface.DEFAULT);
                                            holder.userStatus.setTypeface(Typeface.DEFAULT);
//                               holder.userName.setTextColor(context.getResources().getColor(R.color.colorTextBlack));
                                            holder.unread_icon.setVisibility(View.GONE);
                                        }

                                    }
                                    catch (Exception exception){
                                        Log.d("CHATSADAPTER", "showUnreadChats: error",exception);
                                    }
                                }


                            }
                }

                lastMessage = "default";
                lastMsgTime = null;
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });



    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void onDataChanged(){

    }

    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;
        ImageView unread_icon;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.custom_profile_image);
            userStatus = itemView.findViewById(R.id.custom_user_last_seen);
            userName = itemView.findViewById(R.id.custom_profile_name);
            unread_icon = itemView.findViewById(R.id.chat_unread_icon);
        }
    }

    public List<Chat> getChatList(){
        return this.chatList;
    }

    public void deleteChat(int position){
        chatList.remove(position);
        notifyDataSetChanged();
        onDataChanged();

    }

    public void restoreChat(int position,Chat chatItem){
        chatList.add(position,chatItem);
        notifyDataSetChanged();
        onDataChanged();
    }



}
