package co.in.prodigyschool.passiton.Adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;

public class BookAdapter extends FirestoreAdapter<BookAdapter.ViewHolder> {


    public interface OnBookSelectedListener {

        void onBookSelected(DocumentSnapshot snapshot);

    }

    private OnBookSelectedListener mListener;


    public BookAdapter(Query query, OnBookSelectedListener listener) {
        super(query);
        mListener = listener;

    }

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.home_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(getSnapshot(position),mListener);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView cityView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookPicture);
            nameView = itemView.findViewById(R.id.bookMaterialName);
            priceView = itemView.findViewById(R.id.bookMaterialPrice);
            cityView = itemView.findViewById(R.id.locationAndDistance);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnBookSelectedListener listener) {

            Book book = snapshot.toObject(Book.class);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Resources resources = itemView.getResources();

            // Load image
                if(!book.getUserId().equalsIgnoreCase(userId)) {
                    Glide.with(imageView.getContext())
                            .load(book.getBookPhoto())
                            .into(imageView);

                    nameView.setText(book.getBookName());
                    cityView.setText(book.getBookAddress());
                    priceView.setText("₹" + book.getBookPrice());
                    // Click listener
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null) {
                                listener.onBookSelected(snapshot);
                            }
                        }
                    });

                }
        }

    }


}
