package com.omni.moviewdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.omni.moviewdb.fragments.ExtraDialogFragment;
import com.omni.moviewdb.fragments.MovieProfile;
import com.omni.moviewdb.model.movieResponse.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {



    private Movie movie ;


    @BindView(R.id.navigation_view)
    BottomNavigationView navigationView;



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int id = item.getItemId();
            switch (id) {

                case R.id.navigation_reviews: {
                    ExtraDialogFragment dialogFragment = ExtraDialogFragment.newInstance(String.valueOf(movie.getId()), "review");
                    dialogFragment.show(getSupportFragmentManager(), "FragmentDialog");

                    break;
                }
                case R.id.navigation_trailers: {
                    ExtraDialogFragment dialogFragment = ExtraDialogFragment.newInstance(String.valueOf(movie.getId()), "trailer");
                    dialogFragment.show(getSupportFragmentManager(), "FragmentDialog");

                    break;
                }
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);


        Intent intent = getIntent();

        if (intent.hasExtra("current Movie")) {

             movie = intent.getParcelableExtra("current Movie");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_container, MovieProfile.newInstance(movie))
                    .commit();



        }

        navigationView.setOnNavigationItemSelectedListener(listener);



    }



}
