package co.in.prodigyschool.passiton.ui.myChats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import co.in.prodigyschool.passiton.ChatActivity;
import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private static final String TAG = "CHAT FRAGMENT";
    private SlideshowViewModel slideshowViewModel;
    private View PrivateChatsView;
    private RecyclerView chatsList;
    private ViewGroup  mEmptyView ;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";

    public ChatsFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        chatsList =  PrivateChatsView.findViewById(R.id.chat_recycler_view);
        mEmptyView = PrivateChatsView.findViewById(R.id.chat_view_empty);

        initFireStore();

        chatsList =  PrivateChatsView.findViewById(R.id.chat_recycler_view);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatsView;
    }

    private void initFireStore() {
    try {
    /* firestore */
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getPhoneNumber();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("chats").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");



    }
    catch(Exception e){
        Log.e(TAG, "initFireStore: failed",e );
    }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(ChatsRef, Chat.class)
                        .build();


        FirebaseRecyclerAdapter<Chat, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Chat model)
                    {
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        ChatsRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("imageUrl"))
                                    {
                                        retImage[0] = dataSnapshot.child("imageUrl").getValue().toString();
                                        Glide.with(holder.profileImage.getContext())
                                                .load(retImage[0])
                                                .into(holder.profileImage);

                                    }

                                    final String retName = dataSnapshot.child("userName").getValue().toString();
                                    final String retStatus = dataSnapshot.child("userStatus").getValue().toString();

                                    holder.userName.setText(retName);


                                    if (dataSnapshot.child("userState").hasChild("state"))
                                    {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online"))
                                        {
                                            holder.userStatus.setText("online");
                                        }
                                        else if (state.equals("offline"))
                                        {
                                            holder.userStatus.setText("Last Seen: " + date + " " + time);
                                        }
                                    }
                                    else
                                    {
                                        holder.userStatus.setText("offline");
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", usersIDs);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            chatIntent.putExtra("visit_image", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_item, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();



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