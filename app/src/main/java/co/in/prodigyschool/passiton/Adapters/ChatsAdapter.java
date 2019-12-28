package co.in.prodigyschool.passiton.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {


    private Context context;
    private List<Chat> chatList;

    public interface OnChatSelectedListener {

        void OnChatSelected(Chat chat);

    }

    private OnChatSelectedListener mListener;


    public ChatsAdapter(Context context, List<Chat> chatList,OnChatSelectedListener listener){
        this.context = context;
        this.chatList = chatList;
        mListener = listener;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.OnChatSelected(chatItem);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void onDataChanged(){

    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
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
}
