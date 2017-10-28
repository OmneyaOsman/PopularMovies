package com.omni.moviewdb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omni.moviewdb.R;
import com.omni.moviewdb.model.reviewsResponse.Review;

import java.util.List;



public class ReviewsAdapter extends ArrayAdapter<Review> {

    private Context context = getContext();



    public ReviewsAdapter(Context context, List<Review> objects) {
        super(context, 0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.review_list_item , parent , false);
        }

        Review currentReviewComment = getItem(position);
        TextView authorName =  listItemView.findViewById(R.id.author_tv);
        authorName.setText(currentReviewComment.getAuthor());
        TextView content =  listItemView.findViewById(R.id.content_tv);
        content.setText(currentReviewComment.getContent());

        return listItemView ;
    }
}
