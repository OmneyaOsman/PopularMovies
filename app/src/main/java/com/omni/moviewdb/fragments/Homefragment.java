package com.omni.moviewdb.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


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


    private GridLayoutManager manager ;

    private static String StoredKey = "";


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
        if (movies != null ) {
            outState.putParcelableArrayList("movies", (ArrayList<MovieItem>) movies);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
                int mScrollPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                outState.putInt("mScrollPosition", mScrollPosition);

            }
        }


    }


//

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Unbinder unbinder = ButterKnife.bind(this, rootView);

        setMovieListener((MainActivity) getActivity());


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    if (getActivity() != null) {

            manager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(manager);

            if (savedInstanceState == null) {
                String sortKey = getSortKey();
                StoredKey = sortKey;
                Log.d(TAG, "onViewCreated: "+StoredKey);
                makeCall(sortKey);
            } else {

                if(savedInstanceState.containsKey("movies")) {
                    movies = savedInstanceState.getParcelableArrayList("movies");

                    if (movies != null) {

                        int mScrollPosition = savedInstanceState.getInt("mScrollPosition");
                        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            int count = layoutManager.getChildCount();
                            if (mScrollPosition != RecyclerView.NO_POSITION && mScrollPosition < count) {
                                layoutManager.scrollToPosition(mScrollPosition);
                            }
                        }

                        mAdapter = new ImageAdapter(getActivity(),
                                getPostersList((ArrayList<MovieItem>) movies), Homefragment.this);
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }


        }
    }

    private void makeCall(String sortKey){


        if (isNetworkConnected()) {



            if (sortKey.equals(getString(R.string.pref_sort_by_popular_value))) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                getMovies = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
                senMoviesRequest(getMovies);
            } else if (sortKey.equals(getString(R.string.pref_sort_by_top_rated_value))) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                getMovies = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
                senMoviesRequest(getMovies);

            }



        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: "+StoredKey);
        Log.d(TAG, "onResume: "+getSortKey());

        if(getSortKey().equals(StoredKey))
        {
            Log.d(TAG, "onResume: "+"equal");
            return;
        }
        else {
            Log.d(TAG, "onResume: "+"notequal");
            StoredKey = getSortKey();
            if (StoredKey.equals(getString(R.string.pref_sort_by_favorites_value)))
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new FavoriteMoviesFragment()).commit();
            else
                makeCall(StoredKey);
        }
    }
}
