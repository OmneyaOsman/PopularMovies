package com.omni.moviewdb.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omni.moviewdb.R;
import com.omni.moviewdb.model.trailerResponse.Trailer;

import java.util.ArrayList;


public class TrailersAdapter extends ArrayAdapter<Trailer> {

    private Context context = getContext();



    public TrailersAdapter(Context context, ArrayList<Trailer> objects) {
        super(context, 0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.trailers_list_item , parent , false);
        }

        Trailer current = getItem(position);
        TextView title =  listItemView.findViewById(R.id.trailer_title);
        title.setText(current.getName());

        return listItemView ;
    }
}
