package co.in.prodigyschool.passiton.Adapters;


import android.content.res.Resources;
import android.location.Location;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;

public class BookMarkAdapter extends FirestoreRecyclerAdapter<Book,BookMarkAdapter.ViewHolder> {


    double latA,lngA;
    private FirebaseFirestore mFirestore;
    public interface OnBookSelectedListener {

        void onBookSelected(DocumentSnapshot snapshot);

    }

    public interface OnBookLongSelectedListener {

        void onBookLongSelected(DocumentSnapshot snapshot);

    }

    private OnBookSelectedListener mListener;
    private OnBookLongSelectedListener mLongListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookMarkAdapter(@NonNull FirestoreRecyclerOptions<Book> options, BookMarkAdapter.OnBookSelectedListener listener, BookMarkAdapter.OnBookLongSelectedListener longListener) {
        super(options);
        mListener = listener;
        mLongListener = longListener;
        mFirestore = FirebaseFirestore.getInstance();
        getUserLocation();
    }

    public void getUserLocation() {
        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mFirestore.collection("address").document(curUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("BOOK_ADAPTER_INNER", "onEvent: exception", e);
                    return;
                }
                if(snapshot.getDouble("lat") != null && snapshot.getDouble("lng") != null) {
                    latA = snapshot.getDouble("lat");
                    lngA = snapshot.getDouble("lng");
                }

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Book model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener,mLongListener,latA,lngA);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
        return new BookMarkAdapter.ViewHolder(v);

    }

    //code to delete the bookmark
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }




    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView cityView;
        TextView timeSinceView;
        double latA,lngA;
        private FirebaseFirestore mFirestore;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookPicture);
            nameView = itemView.findViewById(R.id.bookMaterialName);
            priceView = itemView.findViewById(R.id.bookMaterialPrice);
            cityView = itemView.findViewById(R.id.locationAndDistance);
            timeSinceView = itemView.findViewById(R.id.timeSinceListing);
            mFirestore = FirebaseFirestore.getInstance();

        }


        public void bind(final DocumentSnapshot snapshot,
                         final OnBookSelectedListener listener, final OnBookLongSelectedListener mLongListener, double latitude, double longitude) {
            Book book = snapshot.toObject(Book.class);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Resources resources = itemView.getResources();
            timeSinceView.setVisibility(View.GONE);
            // Load image
            latA = latitude;
            lngA = longitude;

            Glide.with(imageView.getContext())
                    .load(book.getBookPhoto())
                    .into(imageView);

            nameView.setText(book.getBookName());
            cityView.setText(book.getBookAddress());
            addBookTime(book.getBookTime());
            if(!userId.equalsIgnoreCase(book.getUserId()))
                addDistance(book.getLat(),book.getLng());
            if(book.getBookPrice() == 0){
                priceView.setText("Free");
            }
            else{
                priceView.setText("₹" + book.getBookPrice());
            }
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onBookSelected(snapshot);
                    }
                }
            });

            //longClick Listener
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mLongListener != null){
                        mLongListener.onBookLongSelected(snapshot);
                    }
                    return true;
                }
            });




        }

        private void addBookTime(String bookTime) {
            if( bookTime != null && !bookTime.isEmpty()) {
                timeSinceView.setVisibility(View.VISIBLE);
                SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy HH", Locale.getDefault());
                String currentDate = myFormat.format(new Date());

                try {
                    Date dateBefore = myFormat.parse(currentDate);
                    Date dateAfter = myFormat.parse(bookTime);
                    long difference = dateBefore.getTime() - dateAfter.getTime();
                    Log.d("BookAdapter", "addBookTime: " + difference);
                    float daysBetween = (difference / (1000 * 60 * 60 * 24));
                    /* You can also convert the milliseconds to days using this method
                     * float daysBetween =
                     *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
                     */
                    if (daysBetween < 1.0f) {
                        timeSinceView.setText("New");
                    } else if(daysBetween > 1.0f && daysBetween < 7.0f) {
                        if (daysBetween == 1.0f)
                            timeSinceView.setText(String.format("%s day ago", Math.round(daysBetween)));
                        else
                            timeSinceView.setText(String.format("%s days ago", Math.round(daysBetween)));
                    }
                    else{
                        String date = new SimpleDateFormat("MMM dd, yy",Locale.getDefault()).format(new Date(dateAfter.getTime()));
                        timeSinceView.setText(String.format("%s",date));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void addDistance(double latitude,double longitude){
            float res;
            if(latA != 0.0 && lngA != 0.0 && latitude != 0.0 && longitude != 0.0) {
                Location locationA = new Location("point A");
                Location locationB = new Location("point B");

                locationA.setLatitude(latA);
                locationA.setLongitude(lngA);
                locationB.setLatitude(latitude);
                locationB.setLongitude(longitude);
                res = locationA.distanceTo(locationB);
                if (res > 0.0f && res < 1000f) {
                    res = Math.round(res);
                    if (res > 0.0f)
                        cityView.append("\n" + (int)res + " m");
                }
                else if(res > 1000f){
                    res = Math.round(res / 100);
                    res = res / 10;
                    if (res > 0.0f)
                        cityView.append("\n" + res + " KM");
                }
            }
        }
    }//end of view holder class
}

