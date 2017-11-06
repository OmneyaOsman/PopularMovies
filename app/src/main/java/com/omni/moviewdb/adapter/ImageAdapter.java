package com.omni.moviewdb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omni.moviewdb.R;
import com.omni.moviewdb.data.MovieContract;
import com.omni.moviewdb.utils.AppConfig;

import java.util.ArrayList;



public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    private Context context;

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;

    private ArrayList<String> imageList;
    final  private OnItemClickListener listener ;

    private int key =0 ;


    public interface OnItemClickListener {
        void setOnItemClickListener(int position);
    }



    public ImageAdapter(Context context, ArrayList<String> imageList , OnItemClickListener listener) {
        this.imageList = imageList;
        this.listener = listener ;
        this.context = context;
    }

    public ImageAdapter(Context context, OnItemClickListener listener , int key) {
        this.context = context;
        this.listener = listener ;
        this.key = key ;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item_list, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        String poster ="" ;
        if(key !=0){
            // Indices for the _id, description, and priority columns
            int posterIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.POSTER_PATH);

            mCursor.moveToPosition(position); // get to the right location in the cursor

            // Determine the values of the wanted data
            poster = mCursor.getString(posterIndex);
        }else
            poster =imageList.get(position);

        Glide.with(context)
                .load(AppConfig.BaseIMAGEURL+AppConfig.IMAGE_SIZE+poster)
                .into(holder.imageView);

    }


    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount() {

        if (key != 0) {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getCount();
        } else
            return imageList.size();
    }

    class ImageViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
         private ImageView imageView;


        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_list);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {

            int pos = getAdapterPosition();
            listener.setOnItemClickListener(pos);

        }
    }







}
