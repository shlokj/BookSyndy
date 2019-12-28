package co.in.prodigyschool.passiton.ui.myChats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Adapters.ChatsAdapter;
import co.in.prodigyschool.passiton.ChatActivity;
import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.R;

public class ChatsFragment extends Fragment implements  EventListener<QuerySnapshot>,ChatsAdapter.OnChatSelectedListener {

    private static final String TAG = "CHAT_FRAGMENT";
    private SlideshowViewModel slideshowViewModel;
    private View PrivateChatsView;
    private RecyclerView recyclerView;
    private ViewGroup  mEmptyView ;
    private CollectionReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private String currentUserID="";
    private ChatsAdapter mAdapter;
    private List<Chat> chatList;
    private ListenerRegistration chatsRegistration;

    public ChatsFragment(){

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView =  PrivateChatsView.findViewById(R.id.chat_recycler_view);
        mEmptyView = PrivateChatsView.findViewById(R.id.chat_view_empty);

        chatList = new ArrayList<>();
        mAdapter = new ChatsAdapter(getContext(),chatList,this){
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }

            }
        };
        initFireStore();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       // recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        return PrivateChatsView;
    }

    private void initFireStore() {
    try {
    /* firestore */
        mFireStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getPhoneNumber();

        ChatsRef = mFireStore.collection("chats").document(currentUserID).collection("receiver_chats");
        chatsRegistration = ChatsRef.addSnapshotListener(this);


    }
    catch(Exception e){
        Log.e(TAG, "initFireStore: failed",e );
    }
    }

    public void setupChatAdapter(QuerySnapshot queryDocumentSnapshots){

        if(!queryDocumentSnapshots.isEmpty()) {
            chatList.clear();
            chatList.addAll(queryDocumentSnapshots.toObjects(Chat.class));
            mAdapter.notifyDataSetChanged();
        }
        for(Chat chat:chatList){
            Log.d(TAG, "setupChatAdapter: "+chat.getUserName());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(chatsRegistration == null){
            chatsRegistration = ChatsRef.addSnapshotListener(this);
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatsRegistration != null){
            chatsRegistration.remove();
            chatsRegistration = null;
        }

//adapter.stopListening();

    }


    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if(e != null){
            Log.d(TAG, "onEvent: chats error",e);
            return;
        }
        setupChatAdapter(queryDocumentSnapshots);
    }

    @Override
    public void OnChatSelected(Chat chat) {
        if(chat.hasAllFields()) {
            //Toast.makeText(getContext(),"selectd :"+chat.getUserName(),Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
            chatIntent.putExtra("visit_user_id", chat.getUserId());
            chatIntent.putExtra("visit_user_name", chat.getUserName());
            chatIntent.putExtra("visit_image", chat.getImageUrl());
            startActivity(chatIntent);
        }
        else{
            Snackbar.make(getView(),"Internal Error Occurred",Snackbar.LENGTH_SHORT).show();
        }
    }



}