package com.omni.moviewdb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.omni.moviewdb.R;
import com.omni.moviewdb.fragments.ExtraDialogFragment;
import com.omni.moviewdb.fragments.MovieProfileFragment;
import com.omni.moviewdb.model.movieResponse.MovieItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {



    private MovieItem movieItem;


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
                    ExtraDialogFragment dialogFragment = ExtraDialogFragment.newInstance(String.valueOf(movieItem.getId()), "review");
                    dialogFragment.show(getSupportFragmentManager(), "FragmentDialog");

                    break;
                }
                case R.id.navigation_trailers: {
                    ExtraDialogFragment dialogFragment = ExtraDialogFragment.newInstance(String.valueOf(movieItem.getId()), "trailer");
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

        if (intent.hasExtra("current MovieItem")) {

             movieItem = intent.getParcelableExtra("current MovieItem");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profile_container, MovieProfileFragment.newInstance(movieItem))
                    .commit();



        }

        navigationView.setOnNavigationItemSelectedListener(listener);



    }



}
