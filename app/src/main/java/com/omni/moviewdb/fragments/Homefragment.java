package com.omni.moviewdb.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.omni.moviewdb.BuildConfig;
import com.omni.moviewdb.R;
import com.omni.moviewdb.activity.MainActivity;
import com.omni.moviewdb.adapter.ImageAdapter;
import com.omni.moviewdb.api.ApiClient;
import com.omni.moviewdb.api.ApiService;
import com.omni.moviewdb.event.MovieClickListener;
import com.omni.moviewdb.model.movieResponse.MovieItem;
import com.omni.moviewdb.model.movieResponse.MovieResponse;
import com.omni.moviewdb.utils.BaseFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Homefragment extends BaseFragment implements
        ImageAdapter.OnItemClickListener {


    private Call<MovieResponse> getMovies ;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView ;

    @BindView(R.id.progressBar)
    ProgressBar progressBar ;



    private List<MovieItem> movies = null;

    private MovieClickListener movieListener;

    private ImageAdapter mAdapter;

    private static final String RECYCLER_VIEW_STATE = "state";

    private GridLayoutManager manager ;


    public void setMovieListener(MovieClickListener movieClickListener) {
        this.movieListener = movieClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies" ,(ArrayList<MovieItem>) movies);
        outState.putParcelable(RECYCLER_VIEW_STATE, recyclerView.getLayoutManager().onSaveInstanceState());

    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {

            movies = savedInstanceState.getParcelableArrayList("movies");

            if (movies != null) {
                mAdapter = new ImageAdapter(getActivity(),
                        getPostersList((ArrayList<MovieItem>) movies), Homefragment.this);
                recyclerView.setAdapter(mAdapter);


            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Unbinder unbinder = ButterKnife.bind(this, rootView);

        setMovieListener((MainActivity) getActivity());



        return rootView;
    }

//    private int numberOfColumns() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        // You can change this divider to adjust the size of the poster
//        int widthDivider = 185;
//        int width = displayMetrics.widthPixels;
//        int nColumns = width / widthDivider;
//        if (nColumns < 2) return 2;
//        return nColumns;
//    }






    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getActivity() != null) {

            if (savedInstanceState == null ) {

                 manager = new GridLayoutManager(getActivity(), 2);
                recyclerView.setLayoutManager(manager);

                if (isNetworkConnected()) {

                    if (getSortKey().equals(getString(R.string.pref_sort_by_popular_value))) {
                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        getMovies = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
                        senMoviesRequest(getMovies);
                    } else if (getSortKey().equals(getString(R.string.pref_sort_by_top_rated_value))) {
                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        getMovies = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
                        senMoviesRequest(getMovies);
                    }


                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
                }
            }


        }

    }


    private String getSortKey() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return sharedPreferences.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
    }

    private void senMoviesRequest(Call<MovieResponse> getMovies) {

        progressBar.setVisibility(View.VISIBLE);
        getMovies.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {

                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    movies = response.body().getMovies();
                    ImageAdapter adapter = new ImageAdapter(getActivity(),
                            getPostersList((ArrayList<MovieItem>) movies), Homefragment.this);
                    recyclerView.setAdapter(adapter);

                }


            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {

                progressBar.setVisibility(View.GONE);


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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private ArrayList<String> getPostersList(ArrayList<MovieItem> movies) {
        ArrayList<String> posters = new ArrayList<>();

        for (MovieItem movieItem : movies) {

            posters.add(movieItem.getPosterPath());

        }

        return posters;
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getMovies != null)
            getMovies.cancel();
    }



    @Override
    public void setOnItemClickListener(int position) {
        MovieItem currentMovieItem = movies.get(position);
        movieListener.onMovieClickListener(currentMovieItem);

    }

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    @Override
    public void onPause() {
        super.onPause();


        mBundleRecyclerViewState = new Bundle();
        mListState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(mListState);

                }
            }, 50);
        }


        recyclerView.setLayoutManager(manager);
    }
}
