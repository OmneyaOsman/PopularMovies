package com.omni.moviewdb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.omni.moviewdb.R;
import com.omni.moviewdb.utils.AppConfig;

import java.util.ArrayList;



public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    private Context context;

    private ArrayList<String> imageList;
    final  private OnItemClickListener listener ;


    public interface OnItemClickListener {
        void setOnItemClickListener(int position);
    }



    public ImageAdapter(Context context, ArrayList<String> imageList , OnItemClickListener listener) {
        this.imageList = imageList;
        this.listener = listener ;
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item_list, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        Glide.with(context)
                .load(AppConfig.BaseIMAGEURL+AppConfig.IMAGE_SIZE+imageList.get(position))
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
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
