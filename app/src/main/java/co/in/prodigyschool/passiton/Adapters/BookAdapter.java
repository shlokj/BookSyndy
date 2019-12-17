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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;

public class BookAdapter extends FirestoreAdapter<BookAdapter.ViewHolder> {

    double latA,lngA;
    private FirebaseFirestore mFirestore;
    public interface OnBookSelectedListener {

        void onBookSelected(DocumentSnapshot snapshot);

    }

    private OnBookSelectedListener mListener;

    public BookAdapter(Query query, OnBookSelectedListener listener) {
        super(query);
        mListener = listener;
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


            holder.bind(getSnapshot(position), mListener,latA,lngA);


    }



    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView cityView;
        double latA,lngA;
        private FirebaseFirestore mFirestore;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookPicture);
            nameView = itemView.findViewById(R.id.bookMaterialName);
            priceView = itemView.findViewById(R.id.bookMaterialPrice);
            cityView = itemView.findViewById(R.id.locationAndDistance);
            mFirestore = FirebaseFirestore.getInstance();

        }



        public void bind(final DocumentSnapshot snapshot,
                         final OnBookSelectedListener listener,double latitude,double longitude) {
            Book book = snapshot.toObject(Book.class);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Resources resources = itemView.getResources();
            // Load image
            latA = latitude;
            lngA = longitude;



               Glide.with(imageView.getContext())
                       .load(book.getBookPhoto())
                       .into(imageView);

               nameView.setText(book.getBookName());
               cityView.setText(book.getBookAddress());
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
                        cityView.append("  " + (int)res + " m");
                }
                else if(res > 1000f){
                    res = Math.round(res / 100);
                    res = res / 10;
                    if (res > 0.0f)
                        cityView.append("  " + res + " KM");
                }
            }
        }

    }


}
