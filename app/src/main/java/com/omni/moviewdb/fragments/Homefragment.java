package com.omni.moviewdb.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.omni.moviewdb.Api.ApiClient;
import com.omni.moviewdb.Api.ApiService;
import com.omni.moviewdb.BuildConfig;
import com.omni.moviewdb.MainActivity;
import com.omni.moviewdb.R;
import com.omni.moviewdb.adapter.ImageAdapter;
import com.omni.moviewdb.event.MovieClickListener;
import com.omni.moviewdb.model.RealmMovie;
import com.omni.moviewdb.model.movieResponse.Movie;
import com.omni.moviewdb.model.movieResponse.MovieResponse;
import com.omni.moviewdb.utils.BaseFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class Homefragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener , ImageAdapter.OnItemClickListener {


    private  Call<MovieResponse> getMovies ;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView ;

    @BindView(R.id.progressBar)
    ProgressBar progressBar ;

    @BindView(R.id.movies_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout ;

    private List<Movie> movies  = null;

    private static String key = "";
    private MovieClickListener movieListener ;

    private Realm realm ;




    public void setMovieListener( MovieClickListener movieClickListener){
        this.movieListener = movieClickListener ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "onCreate: ");

        key = getSortKey();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        if(!key.equals(getSortKey()))
            getActivity().recreate();
}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies" ,(ArrayList<Movie>) movies);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Unbinder unbinder = ButterKnife.bind(this, rootView);

        realm = Realm.getDefaultInstance();
        setMovieListener((MainActivity) getActivity());

        GridLayoutManager manager = new GridLayoutManager(getActivity(), numberOfColumns());
        recyclerView.setLayoutManager(manager);

        return rootView;
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 185;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getActivity() != null) {
            if (savedInstanceState != null) {
                movies = savedInstanceState.getParcelableArrayList("movies");

                if (movies != null) {
                    ImageAdapter adapter = new ImageAdapter(getActivity(),
                            getPostersList((ArrayList<Movie>) movies), Homefragment.this);
                    recyclerView.setAdapter(adapter);
                }
            } else {

                if (isNetworkConnected()) {

                    if (getSortKey().equals(getString(R.string.pref_sort_by_popular_value))) {
                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        getMovies = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
                        senMoviesRequest(getMovies, 0);
                    } else if(getSortKey().equals(getString(R.string.pref_sort_by_top_rated_value))){
                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        getMovies = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
                        senMoviesRequest(getMovies, 0);
                    }else if(getSortKey().equals(getString(R.string.pref_sort_by_favorites_value))){

                        RealmResults<RealmMovie> realmMovies = realm.where(RealmMovie.class).findAll();
                        if(realmMovies!=null  && realmMovies.size()!=0){
                            ImageAdapter adapter = new ImageAdapter(getActivity(),
                                    getPostersRealmList(realmMovies), Homefragment.this);
                            recyclerView.setAdapter(adapter);
                            movies = migrateRealmToMovie(realmMovies);
                        }
                        progressBar.setVisibility(View.GONE);
                    }


                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
                }
            }
            swipeRefreshLayout.setOnRefreshListener(this);
        }

    }


    private String getSortKey() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        return sharedPreferences.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
    }

    private void senMoviesRequest(Call<MovieResponse> getMovies, final int refresh) {


        getMovies.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {

                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    movies = response.body().getMovies();
                    ImageAdapter adapter = new ImageAdapter(getActivity(),
                            getPostersList((ArrayList<Movie>) movies), Homefragment.this);
                    recyclerView.setAdapter(adapter);

                }


                if (refresh == 1)
                    swipeRefreshLayout.setRefreshing(false);
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

                if (refresh == 1)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private ArrayList<String> getPostersList(ArrayList<Movie> movies) {
        ArrayList<String> posters = new ArrayList<>();

        for (Movie movie : movies) {

            posters.add(movie.getPosterPath());

        }

        return posters;
    }

    private ArrayList<String> getPostersRealmList(RealmResults<RealmMovie> movies) {
        ArrayList<String> posters = new ArrayList<>();

        for (Movie movie : migrateRealmToMovie(movies)) {

            posters.add(movie.getPosterPath());

        }

        return posters;
    }


    private ArrayList<Movie> migrateRealmToMovie(RealmResults<RealmMovie> movies) {
        ArrayList<Movie> moviesList = new ArrayList<>();

        for (RealmMovie movie : movies) {
            moviesList.add(new Movie(movie.getmImageResource() , movie.getmOriginalTitle() , movie.getmOverView() ,
                    movie.getmReleaseDate() , movie.getmVoteAverage() , movie.getmId() , movie.getBackdropPath() ,movie.getFavorite()));


        }
        return moviesList ;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getMovies != null)
            getMovies.cancel();
    }

    @Override
    public void onRefresh() {

        if (isNetworkConnected()) {


            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            if (getSortKey().equals(getString(R.string.pref_sort_by_popular_value)))
                getMovies = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
            else if (getSortKey().equals(getString(R.string.pref_sort_by_top_rated_value)))
                getMovies = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
            else
                realm.refresh();

            senMoviesRequest(getMovies, 1);

        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.check_network, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void setOnItemClickListener(int position) {
        Movie currentMovie = movies.get(position);
        movieListener.onMovieClickListener(currentMovie);

    }
}
