package com.booksyndy.academics.android.Adapters;

import android.content.res.Resources;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DonationAdapter extends FirestoreRecyclerAdapter<Donation, DonationAdapter.ViewHolder> {



    public interface OnDonationSelectedListener {

        void onDonationSelected(DocumentSnapshot snapshot);

    }

    public interface OnDonationLongSelectedListener {

        void onDonationLongSelected(DocumentSnapshot snapshot);

    }
    private DonationAdapter.OnDonationSelectedListener mListener;
    private DonationAdapter.OnDonationLongSelectedListener mLongListener;

    public DonationAdapter(@NonNull FirestoreRecyclerOptions<Donation> options, DonationAdapter.OnDonationSelectedListener listener, DonationAdapter.OnDonationLongSelectedListener longListener) {
        super(options);
        mListener = listener;
        mLongListener = longListener;
//        mFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DonationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DonationAdapter.ViewHolder(inflater.inflate(R.layout.donation_item, parent, false));

    }

    @Override
    protected void onBindViewHolder(@NonNull DonationAdapter.ViewHolder holder, int position, @NonNull Donation model) {
        holder.bind(getSnapshots().getSnapshot(position), mListener, mLongListener);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        TextView statusView;
        TextView dateView;
        double latA,lngA;
        private FirebaseFirestore mFirestore;

        private ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.donationPicture);
            nameView = itemView.findViewById(R.id.donationName);
            statusView = itemView.findViewById(R.id.statusTV);
            dateView = itemView.findViewById(R.id.donationDate);
//            mFirestore = FirebaseFirestore.getInstance();

        }



        private void bind(final DocumentSnapshot snapshot,
                          final DonationAdapter.OnDonationSelectedListener listener, final DonationAdapter.OnDonationLongSelectedListener longListener) {
            Donation donation = snapshot.toObject(Donation.class);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            Resources resources = itemView.getResources();
            dateView.setVisibility(View.GONE);
            // Load image
            Glide.with(imageView.getContext())
                    .load(donation.getDonationPhoto())
                    .into(imageView);

            nameView.setText(donation.getDonationName());

            if (donation.getStatus() == 1) {
                statusView.setText("Submitted");
            }
            else if(donation.getStatus() == 0){
                statusView.setText("Cancelled/rejected");
            }
            else if(donation.getStatus() == 2){
                statusView.setText("Accepted by volunteer");
            }
            else if(donation.getStatus() == 3){
                statusView.setText("Completed");
            }

            addBookTime(donation.getDonationListingTime());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDonationSelected(snapshot);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(longListener != null){
                        longListener.onDonationLongSelected(snapshot);
                    }
                    return true;
                }
            });

        }

        private void addBookTime(String bookTime) {
            if (bookTime != null && !bookTime.isEmpty()) {
                dateView.setVisibility(View.VISIBLE);
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.getDefault());
                String currentDate = myFormat.format(new Date());

                try {
                    Date dateBefore = myFormat.parse(currentDate);
                    Date dateAfter = myFormat.parse(bookTime);
                    long difference = dateBefore.getTime() - dateAfter.getTime();
//                    Log.d("BookAdapter", "addBookTime: " + difference);
                    /* You can also convert the milliseconds to days using this method
                     * float daysBetween =
                     *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
                     */

                    String date = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault()).format(new Date(dateAfter.getTime()));
                    dateView.setText(String.format("%s",date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}