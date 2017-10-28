package com.omni.moviewdb.fragments;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
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
import com.omni.moviewdb.model.RealmMovie;
import com.omni.moviewdb.model.movieResponse.Movie;
import com.omni.moviewdb.utils.AppConfig;
import com.omni.moviewdb.utils.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MovieProfile extends BaseFragment {


    @BindView(R.id.detail_img_cover)
    ImageView coverImage;

    @BindView(R.id.detail_img_movie)
    ImageView movieImage;

    @BindView(R.id.release_date)
    TextView releaseDate;

    @BindView(R.id.vote_avg)
    TextView voteAvg ;

    @BindView(R.id.overView)
    TextView overViewTv ;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton ;


    @BindView(R.id.toolbar_details)
    Toolbar toolbar ;

    @BindView(R.id.app_bar)
    AppBarLayout appBar ;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLAyout ;




    private Movie movie ;
    private Realm realm ;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            if(getArguments().containsKey("current Movie")){
                movie = getArguments().getParcelable("current Movie");
            }
        }
    }

    public static MovieProfile newInstance(Movie movie){
        MovieProfile fragment = new MovieProfile();
        Bundle args = new Bundle();
        args.putParcelable("current Movie" , movie);
        fragment.setArguments(args);
        return fragment ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_profile, container, false);

        ButterKnife.bind(this , rootView);
        realm = Realm.getDefaultInstance();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

/*
 *
 */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        if(movie.getFavorite() ==0){
                            movie.setFavorite(1);
                            updateFab(1);
                            //create new RealmObject
                            createRealmObject(movie);
                        }else{
                            movie.setFavorite(0);
                            updateFab(0);
                            //delete movie from realm
                            deleteRealmObject(movie);
                    }
            }
        });


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(movie!=null) {
            toolbar.setTitle(movie.getOriginalTitle());
            toolbarLAyout.setTitle(movie.getOriginalTitle());


            Glide.with(getActivity())
                    .load(AppConfig.BaseIMAGEURL + "w500/" + movie.getBackdropPath())
                    .into(coverImage);

            Glide.with(getActivity())
                    .load(AppConfig.BaseIMAGEURL + AppConfig.IMAGE_SIZE + movie.getPosterPath())
                    .into(movieImage);


            releaseDate.setText(getString(R.string.released_date).concat(movie.getReleaseDate()));
            voteAvg.setText(getString(R.string.vote_average).concat(String.valueOf(movie.getVoteAverage())));
            overViewTv.setText(movie.getOverview());

            RealmMovie m = realm.where(RealmMovie.class).equalTo("mId", movie.getId()).findFirst();
            if (m != null) {
                movie.setFavorite(1);
                updateFab(movie.getFavorite());
            }
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


    private void createRealmObject(Movie movie) {

        realm.beginTransaction();
        RealmMovie usedMovie = new RealmMovie();
        usedMovie.setmId(movie.getId());
        usedMovie.setmImageResource(movie.getPosterPath());
        usedMovie.setmOriginalTitle(movie.getOriginalTitle());
        usedMovie.setmReleaseDate(movie.getReleaseDate());
        usedMovie.setmVoteAverage(movie.getVoteAverage());
        usedMovie.setmOverView(movie.getOverview());
        usedMovie.setBackdropPath(movie.getBackdropPath());
        usedMovie.setFavorite(movie.getFavorite());

        RealmMovie realmMovie = realm.copyToRealmOrUpdate(usedMovie);


        realm.commitTransaction();
        Toast.makeText(getActivity(), "Movie Added To Favorites", Toast.LENGTH_SHORT).show();

    }

    private void deleteRealmObject(Movie movie){

        final RealmMovie realmObject = realm.where(RealmMovie.class).equalTo("mId" , movie.getId()).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmObject.removeFromRealm();
            }
        });
        Toast.makeText(getActivity(), "Movie Deleted From Favorites", Toast.LENGTH_SHORT).show();

        realm.refresh();
    }



}
