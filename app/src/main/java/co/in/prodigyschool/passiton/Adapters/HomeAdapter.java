package co.in.prodigyschool.passiton.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import co.in.prodigyschool.passiton.Data.HomeItem;
import co.in.prodigyschool.passiton.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    private String[] mTextSet;
    private int[] mImageSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.home_text_view);
            mImageView = itemView.findViewById(R.id.home_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            mTextView.setText(HomeItem.mText[position]);
            mImageView.setImageResource(HomeItem.picPath[position]);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HomeAdapter(String[] myDataset,int[] myImageset) {
        mTextSet = myDataset;
        mImageSet = myImageset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_item, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bindView(position);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTextSet.length;
    }
}