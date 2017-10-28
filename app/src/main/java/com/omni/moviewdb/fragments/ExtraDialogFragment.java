package com.omni.moviewdb.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.moviewdb.Api.ApiClient;
import com.omni.moviewdb.Api.ApiService;
import com.omni.moviewdb.BuildConfig;
import com.omni.moviewdb.R;
import com.omni.moviewdb.adapter.ReviewsAdapter;
import com.omni.moviewdb.adapter.TrailersAdapter;
import com.omni.moviewdb.model.reviewsResponse.ReviewResponse;
import com.omni.moviewdb.model.trailerResponse.TrailerResponse;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ExtraDialogFragment extends DialogFragment {



    @BindView(R.id.extras_list_view)
    ListView listView ;
    private String movieId , type ;

    @BindView(R.id.extra_empty_view)
    TextView emptyView ;

    private TrailersAdapter adapter ;

    public static ExtraDialogFragment newInstance(String id , String type){
        ExtraDialogFragment fragment = new ExtraDialogFragment();
        Bundle args = new Bundle();
        args.putString("id" , id);
        args.putString("type" , type);
        fragment.setArguments(args);
        return  fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            if(getArguments().containsKey("id"))
                movieId = getArguments().getString("id");
            if(getArguments().containsKey("type"))
                type = getArguments().getString("type");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater inflator = getActivity().getLayoutInflater();
        View view = inflator.inflate(R.layout.dialog_fragment, null);

        ButterKnife.bind(this, view);

        if (type.equals("review")) {
            reviewRequest();

        } else
            trailerRequest();


        builder.setView(view);
        return builder.create();
    }



    private void reviewRequest(){


            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ReviewResponse> reviewsCall = apiService.getReviews(movieId , BuildConfig.MOVIE_DB_API_KEY);
            reviewsCall.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {

                    if(response.body()!=null && response.body().getResults()!=null) {
                        ReviewsAdapter adapter = new ReviewsAdapter(getActivity(), response.body().getResults());
                        listView.setAdapter(adapter);
                    }else
                        emptyView.setVisibility(View.VISIBLE);

                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {



                    String errorType;
                    String errorDesc;
                    if (t instanceof IOException) {
                        errorType = getString(R.string.time_out);
                        errorDesc = String.valueOf(t.getCause());
                    } else if (t instanceof IllegalStateException) {
                        errorType = getString(R.string.conversion_time);
                        errorDesc = String.valueOf(t.getCause());
                    } else {
                        errorType = getString(R.string.other_error);
                        errorDesc = String.valueOf(t.getLocalizedMessage());
                    }

                    Toast.makeText(getActivity(), errorType, Toast.LENGTH_SHORT).show();
                    call.cancel();

                }
            });

    }


    private void trailerRequest(){


            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<TrailerResponse> reviewsCall = apiService.getVideos(movieId ,BuildConfig.MOVIE_DB_API_KEY);
            reviewsCall.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(@NonNull Call<TrailerResponse> call, @NonNull Response<TrailerResponse> response) {

                    if(response.body()!=null && response.body().getResults()!=null) {
                         adapter = new TrailersAdapter(getActivity(), response.body().getResults());
                        listView.setAdapter(adapter);
                    }
                    else
                        emptyView.setVisibility(View.VISIBLE);

                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {



                    String errorType;
                    String errorDesc;
                    if (t instanceof IOException) {
                        errorType = getString(R.string.time_out);
                        errorDesc = String.valueOf(t.getCause());
                    } else if (t instanceof IllegalStateException) {
                        errorType = getString(R.string.conversion_time);
                        errorDesc = String.valueOf(t.getCause());
                    } else {
                        errorType = getString(R.string.other_error);
                        errorDesc = String.valueOf(t.getLocalizedMessage());
                    }

                    Toast.makeText(getActivity(), errorType, Toast.LENGTH_SHORT).show();
                    call.cancel();

                }
            });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                watchYoutubeVideo(adapter.getItem(i).getKey());
            }
        });

    }



    public  void watchYoutubeVideo(String key){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
