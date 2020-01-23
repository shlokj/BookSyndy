package co.in.prodigyschool.passiton.ui.myChats;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
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

public class ChatsFragment extends Fragment implements EventListener<QuerySnapshot>, ChatsAdapter.OnChatSelectedListener {

    private static final String TAG = "CHAT_FRAGMENT";
    private SlideshowViewModel slideshowViewModel;
    private View PrivateChatsView;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private CollectionReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private String currentUserID = "";
    private ChatsAdapter mAdapter;
    private List<Chat> chatList;
    private List<Chat> chatListFull;
    private ListenerRegistration chatsRegistration;
    private Snackbar snackbar;

    // TODO: show newest chat on top

    public ChatsFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        recyclerView = PrivateChatsView.findViewById(R.id.chat_recycler_view);
        mEmptyView = PrivateChatsView.findViewById(R.id.chat_view_empty);
        initFireStore();

        chatList = new ArrayList<>();
        chatListFull = new ArrayList<>();
        mAdapter = new ChatsAdapter(getContext(), chatList, this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                Log.d(TAG, "onDataChanged: entered");
                if (getItemCount() == 0) {
                    Log.d(TAG, "onDataChanged: empty");
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {

                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        };


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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

        } catch (Exception e) {
            Log.e(TAG, "initFireStore: failed", e);
        }
    }

    public void setupChatAdapter(QuerySnapshot queryDocumentSnapshots) {

        if (!queryDocumentSnapshots.isEmpty()) {
            chatList.clear();
            chatList.addAll(queryDocumentSnapshots.toObjects(Chat.class));
            mAdapter.setChatList(chatList);
            chatListFull = new ArrayList<>(chatList);
            mAdapter.onDataChanged();
        }

/*        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
               final int position = viewHolder.getAdapterPosition();
               final Chat chat = mAdapter.getChatList().get(position);
                mAdapter.deleteChat(viewHolder.getAdapterPosition());
                ChatsRef.document(chat.getUserId()).delete();
                snackbar = Snackbar
                        .make(getView(),  " Book Mark Removed", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // undo is selected, restore the deleted item
                        mAdapter.restoreChat(position,chat);
                        ChatsRef.document(chat.getUserId()).set(chat);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }


        }).attachToRecyclerView(recyclerView);*/

    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatsRegistration == null) {
            chatsRegistration = ChatsRef.addSnapshotListener(this);
            mAdapter.onDataChanged();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onDataChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatsRegistration != null) {
            chatsRegistration.remove();
            chatsRegistration = null;
            mAdapter.onDataChanged();
        }

    }


    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.d(TAG, "onEvent: chats error", e);
            return;
        }
        setupChatAdapter(queryDocumentSnapshots);
    }

    @Override
    public void OnChatSelected(Chat chat) {
        if (chat.hasAllFields()) {
            //Toast.makeText(getContext(),"selectd :"+chat.getUserName(),Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
            chatIntent.putExtra("visit_user_id", chat.getUserId());
            chatIntent.putExtra("visit_user_name", chat.getUserName());
            chatIntent.putExtra("visit_image", chat.getImageUrl());
            startActivity(chatIntent);
        } else {
            Snackbar.make(getView(), "Internal Error Occurred", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        menu.findItem(R.id.filter).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Chat> filteredList = new ArrayList<>();
                if (newText == null || newText.length() == 0) {
                    filteredList = chatListFull;
                } else {
                    String filterPattern = newText.toLowerCase().trim();
                    for (Chat chat : chatListFull) {
                        if (chat.getUserName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(chat);
                        }
                    }
                }
                mAdapter.setChatList(filteredList);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


}