package co.in.prodigyschool.passiton.Adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.Data.Messages;
import co.in.prodigyschool.passiton.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {


    private Context context;
    private List<Chat> chatList;
    private String lastMessage;
    private String curUserPhone;


    public interface OnChatSelectedListener {

        void OnChatSelected(Chat chat);

    }

    private OnChatSelectedListener mListener;


    public ChatsAdapter(Context context, List<Chat> chatList,OnChatSelectedListener listener){
        this.context = context;
        this.chatList = chatList;
        mListener = listener;
        curUserPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

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
        setLastMessage(chatItem.getDocumentId(),holder.userStatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnChatSelected(chatItem);
                }
            }
        });


    }

    private void setLastMessage(String userId, final TextView lastMessageView){
            lastMessage = "default";
       DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(curUserPhone).child(userId);

       messageRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages message = snapshot.getValue(Messages.class);
                    if(message != null){
                        lastMessage = message.getMessage();
                    }
                }

                switch (lastMessage){
                    case "default":
                        lastMessageView.setText("");
                        break;
                        default: lastMessageView.setText(lastMessage);
                }

                lastMessage = "default";
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


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.custom_profile_image);
            userStatus = itemView.findViewById(R.id.custom_user_last_seen);
            userName = itemView.findViewById(R.id.custom_profile_name);
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
