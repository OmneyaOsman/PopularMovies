package com.omni.moviewdb.fragments;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.omni.moviewdb.R;
import com.omni.moviewdb.data.MovieContract;
import com.omni.moviewdb.model.movieResponse.MovieItem;
import com.omni.moviewdb.utils.AppConfig;
import com.omni.moviewdb.utils.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieProfileFragment extends BaseFragment {


    @BindView(R.id.detail_img_cover)
    ImageView coverImage;

    @BindView(R.id.detail_img_movie)
    ImageView movieImage;

    @BindView(R.id.release_date)
    TextView releaseDate;

    @BindView(R.id.vote_avg)
    TextView voteAvg;

    @BindView(R.id.overView)
    TextView overViewTv;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;


    @BindView(R.id.toolbar_details)
    Toolbar toolbar;

    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLAyout;


    private MovieItem movieItem;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey("current MovieItem")) {
                movieItem = getArguments().getParcelable("current MovieItem");
            }
        }
    }

    public static MovieProfileFragment newInstance(MovieItem movieItem) {
        MovieProfileFragment fragment = new MovieProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("current MovieItem", movieItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_profile, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (isMovieFavorate(movieItem)) {
            movieItem.setFavorite(1);
            updateFab(1);
        } else {
            movieItem.setFavorite(0);
            updateFab(0);
        }


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (movieItem.getFavorite() == 0) {
                    movieItem.setFavorite(1);
                    updateFab(1);
                    insertFavoriteMovie(movieItem);
                } else {
                    movieItem.setFavorite(0);
                    updateFab(0);
                    deleteMovieFromFavorite(movieItem);
                }
            }
        });


    }


    private void deleteMovieFromFavorite(MovieItem movieItem) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movieItem.getId())).build();
        getActivity().getContentResolver().delete(uri, null, null);
        Toast.makeText(getActivity(), "Movie deleted from Favorites", Toast.LENGTH_SHORT).show();
    }

    private void insertFavoriteMovie(MovieItem movieItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.TITLE, movieItem.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.POSTER_PATH, movieItem.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.MOVIE_ID, movieItem.getId());
        contentValues.put(MovieContract.MovieEntry.COVER, movieItem.getBackdropPath());
        contentValues.put(MovieContract.MovieEntry.VOTE_AVERAGE, movieItem.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.OVERVIEW, movieItem.getOverview());
        contentValues.put(MovieContract.MovieEntry.FAVORITE, movieItem.getFavorite());
        contentValues.put(MovieContract.MovieEntry.RELEASE, movieItem.getReleaseDate());


        Uri uri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        if (uri != null)
            Toast.makeText(getActivity(), "MovieItem Added To Favorites", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (movieItem != null) {
            toolbar.setTitle(movieItem.getOriginalTitle());
            toolbarLAyout.setTitle(movieItem.getOriginalTitle());


            Glide.with(getActivity())
                    .load(AppConfig.BaseIMAGEURL + "w500/" + movieItem.getBackdropPath())
                    .into(coverImage);

            Glide.with(getActivity())
                    .load(AppConfig.BaseIMAGEURL + AppConfig.IMAGE_SIZE + movieItem.getPosterPath())
                    .into(movieImage);


            releaseDate.setText(getString(R.string.released_date).concat(movieItem.getReleaseDate()));
            voteAvg.setText(getString(R.string.vote_average).concat(String.valueOf(movieItem.getVoteAverage())));
            overViewTv.setText(movieItem.getOverview());

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


    }


    //update FloatingActionBar ImageTint

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateFab(int favorite) {
        if (favorite == 1)
            floatingActionButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
        else
            floatingActionButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
    }


    private boolean isMovieFavorate(MovieItem movieItem) {

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(movieItem.getId())).build();

        Cursor cursor = null;
        int movieId = 0;

        try {
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            assert cursor != null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                movieId = cursor.getInt(cursor.getColumnIndex("movie_id"));
            }
            return movieId == movieItem.getId();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
