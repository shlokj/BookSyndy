package com.booksyndy.academics.android.Adapters;

import android.content.Context;
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

import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder>{


    private Context context;
    private List<Book> bookList;
    private OnBookSelectedListener mListener;
    private FirebaseFirestore mFireStore;
    private double latA,lngA;

    public interface OnBookSelectedListener {

        void onBookSelected(Book book);

    }

    public HomeAdapter(Context context,List<Book> bookList,OnBookSelectedListener listener){
        this.context = context;
        this.bookList = bookList;
        mListener = listener;
        mFireStore = FirebaseFirestore.getInstance();
        getUserLocation();
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList = bookList;
        notifyDataSetChanged();
        onDataChanged();
    }

    public void onDataChanged(){

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }






    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Book bookItem = bookList.get(position);

        Glide.with(holder.imageView.getContext())
                .load(bookItem.getBookPhoto())
                .into(holder.imageView);

        holder.nameView.setText(bookItem.getBookName());
        String address = bookItem.getBookAddress();
        if (address.length()>50) {
            holder.cityView.setText(address.substring(0,48)+"...");
        }
        else {
            holder.cityView.setText(address);
        }
//        holder.cityView.setText(bookItem.getBookAddress());
        holder.timeSinceView.setText(addBookTime(bookItem.getBookTime()));
        holder.cityView.append("  "+addDistance(bookItem.getLat(),bookItem.getLng()));
        if(bookItem.getBookPrice() == 0){
            holder.priceView.setText("Free");
        }
        else{
            holder.priceView.setText("₹" + bookItem.getBookPrice());
        }
        // Click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onBookSelected(bookItem);
                }
            }
        });

    }

    //init user location
    public void getUserLocation() {
        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mFireStore.collection("address").document(curUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

    //determine books distance
    private String addDistance(double latitude,double longitude){
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
                    //holder.cityView.append("  " + (int)res + " m");

                    return String.valueOf((int)res+" m");
            }
            else if(res > 1000f){
                res = Math.round(res / 100);
                res = res / 10;
                if (res > 0.0f)
                    //cityView.append("\n" + res + " KM");
                    return  String.valueOf((int)res+ " KM");
            }
        }
        return "";
    }


    private String addBookTime(String bookTime) {
        String result_time = "";
        if( bookTime != null && !bookTime.isEmpty()) {
            //timeSinceView.setVisibility(View.VISIBLE);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy HH",Locale.getDefault());
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
                    result_time = "New";
                } else if(daysBetween > 1.0f && daysBetween < 7.0f) {
                    if (daysBetween == 1.0f)
                        result_time = String.format("%s day ago", Math.round(daysBetween));
                    else
                        result_time = String.format("%s days ago", Math.round(daysBetween));
                }
                else{
                    String date = new SimpleDateFormat("MMM dd, yy", Locale.getDefault()).format(new Date(dateAfter.getTime()));
                    //timeSinceView.setText(String.format("%s",date));
                    result_time = String.format("%s",date);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result_time;
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView priceView;
        TextView cityView;
        TextView timeSinceView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bookPicture);
            nameView = itemView.findViewById(R.id.bookMaterialName_r);
            priceView = itemView.findViewById(R.id.bookMaterialPrice);
            cityView = itemView.findViewById(R.id.locationAndDistance);
            timeSinceView = itemView.findViewById(R.id.timeSinceRequest);


        }
    }
}
