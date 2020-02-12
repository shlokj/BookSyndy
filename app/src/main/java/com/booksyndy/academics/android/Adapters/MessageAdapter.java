package com.booksyndy.academics.android.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Data.Messages;
import com.booksyndy.academics.android.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    public static String TAG = "MESSAGE_ADAPTER";
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private  OnMessageSelectedListener mListener;
    private OnMessageLongSelectedListerner mLongListener;


    public interface OnMessageSelectedListener{
       void OnMessageSelected(Messages message);
}

public interface OnMessageLongSelectedListerner{
       void OnMessagesLongSelected(Messages message);
}


    public MessageAdapter (List<Messages> userMessagesList,OnMessageSelectedListener listener,OnMessageLongSelectedListerner longListener)
    {
        this.userMessagesList = userMessagesList;
        this.mListener = listener;
        this.mLongListener = longListener;

    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, senderMessageTime, receiverMessageTime;

        public ImageView messageSenderPicture, messageReceiverPicture,messageSenderTick;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            senderMessageTime = itemView.findViewById(R.id.sender_message_time);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverMessageTime = itemView.findViewById(R.id.receiver_message_time);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);

        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
        String messageSenderId = mAuth.getCurrentUser().getPhoneNumber();
        final Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverMessageTime.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.senderMessageTime.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);

        messageViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mLongListener != null){
                    mLongListener.OnMessagesLongSelected(messages);
                }
                return true;
            }
        });


        messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.OnMessageSelected(messages);
                }
            }
        });

        if (fromMessageType.equals("text"))
        {
            String strDate = messages.getDate();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM");
            strDate = format.format(Date.parse(strDate));




            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageTime.setVisibility(View.VISIBLE);
//                messageViewHolder.senderMessageTime.setGravity(G);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.senderMessageTime.setText(messages.getTime() + "  " + strDate);
                messageViewHolder.senderMessageText.setText(messages.getMessage()/* + "\n \n" + messages.getTime() + " - " + messages.getDate()*/);


            }
            else
            {
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageTime.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.WHITE);
                messageViewHolder.receiverMessageTime.setText(messages.getTime() + "  " + strDate);
                messageViewHolder.receiverMessageText.setText(messages.getMessage()/* + "\n \n" + messages.getTime() + " - " + messages.getDate()*/);
            }
        }
        else if(fromMessageType.equals("photo")){
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                //messageViewHolder.senderMessageText.setTextColor(Color.WHITE);
                Glide.with(messageViewHolder.messageSenderPicture.getContext())
                        .load(messages.getMessage())
                        .into(messageViewHolder.messageSenderPicture);
                //messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
            else
            {
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                Glide.with(messageViewHolder.messageReceiverPicture.getContext())
                        .load(messages.getMessage())
                        .into(messageViewHolder.messageReceiverPicture);
                //messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                //messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

}