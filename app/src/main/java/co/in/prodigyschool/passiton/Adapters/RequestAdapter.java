package co.in.prodigyschool.passiton.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.BookRequest;
import co.in.prodigyschool.passiton.R;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private static String TAG = "REQUEST_ADAPTER";

    private Context context;
    private List<BookRequest> requestList;
    private FirebaseFirestore mFireStore;
    private double latA,lngA;
    private SharedPreferences userPref;
    private OnRequestSelectedListener mListener;

    public interface OnRequestSelectedListener {
        void onRequestSelected(BookRequest request);
    }

    public RequestAdapter() {
    }

    public RequestAdapter(Context context, List<BookRequest> requestList, OnRequestSelectedListener listener) {
        this.context = context;
        this.requestList = requestList;
        mFireStore = FirebaseFirestore.getInstance();
        userPref = context.getSharedPreferences(context.getString(R.string.UserPref),0);
        latA = userPref.getFloat(context.getString(R.string.p_lat),0.0f);
        lngA = userPref.getFloat(context.getString(R.string.p_lng),0.0f);
        this.mListener = listener;
    }

    public List<BookRequest> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<BookRequest> requestList) {
        this.requestList = requestList;
        notifyDataSetChanged();
        onDataChanged();
    }

    public void onDataChanged(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.req_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookRequest requestItem = requestList.get(position);

        holder.nameView.setText(requestItem.getTitle());
        holder.cityView.setText(requestItem.getBookAddress());
        holder.timeSinceView.setText(addBookTime(requestItem.getTime()));
        //Log.d(TAG, "onBindViewHolder: "+addDistance(requestItem.getLat(),requestItem.getLng()));
        //Log.d(TAG, "onBindViewHolder: "+latA+" "+lngA);
        holder.cityView.append("  "+addDistance(requestItem.getLat(),requestItem.getLng()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onRequestSelected(requestItem);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }


    //determine books distance
    private String addDistance(double latitude,double longitude){
        float res;
        if(latA != 0.0 && lngA != 0.0 && latitude != 0.0 && longitude != 0.0) {
            Log.d(TAG, "addDistance: enter");
            Location locationA = new Location("point A");
            Location locationB = new Location("point B");
            locationA.setLatitude(latA);
            locationA.setLongitude(lngA);
            locationB.setLatitude(latitude);
            locationB.setLongitude(longitude);
            res = locationA.distanceTo(locationB);
            Log.d(TAG, "addDistance: res:"+res);
            if (res > 0.0f && res < 1000f) {
                res = Math.round(res);
                if (res > 0.0f)
                    //holder.cityView.append("  " + (int)res + " m");
                    Log.d(TAG, "addDistance: "+res);
                    return String.valueOf((int)res+" m");
            }
            else if(res > 1000f){
                res = Math.round(res / 100);
                res = res / 10;
                if (res > 0.0f)
                    //cityView.append("\n" + res + " KM");
                    Log.d(TAG, "addDistance: "+res);
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
                float daysBetween = (float)(difference / (1000 * 60 * 60 * 24));
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


        TextView nameView;
        TextView cityView;
        TextView timeSinceView;

         ViewHolder(View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.bookMaterialName_r);
            cityView = itemView.findViewById(R.id.locationAndDistance_r);
            timeSinceView = itemView.findViewById(R.id.timeSinceRequest);


        }
    }
}
