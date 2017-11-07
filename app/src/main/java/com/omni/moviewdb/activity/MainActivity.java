package com.omni.moviewdb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.omni.moviewdb.R;
import com.omni.moviewdb.event.MovieClickListener;
import com.omni.moviewdb.fragments.FavoriteMoviesFragment;
import com.omni.moviewdb.fragments.Homefragment;
import com.omni.moviewdb.model.movieResponse.MovieItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    FavoriteMoviesFragment
            favoriteFragment ;

    Homefragment homeFragment ;

    @Override
    protected void onResume() {
        super.onResume();


//        String currentSort = getSortKey();
//
//        if (currentSort.equals(keySort))
//            return;
//        else {
//            keySort = currentSort;
//            if (keySort.equals(getString(R.string.pref_sort_by_favorites_value))) {
//                FavoriteMoviesFragment fragment;
//                fragment = (FavoriteMoviesFragment) getSupportFragmentManager().findFragmentByTag("favoriteTag");
//                if (fragment == null)
//                    fragment = new FavoriteMoviesFragment();
//                fragment.setMovieListener(this);
//                startFragment(fragment, "favoriteTag");
//                Log.d("GifHeaderParser", "onResumeActivity: "+keySort);
//            } else {
//
//                Homefragment homefragment;
//                homefragment = (Homefragment) getSupportFragmentManager().findFragmentByTag("homeTag");
//                if(homefragment==null)
//                    homefragment= new Homefragment();
//                homefragment.setMovieListener(this);
//                startFragment(homefragment, "homeTag");
//            }
//        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if(favoriteFragment!=null)
            outState.putString("fav" , favoriteFragment.getTag());
        else if(homeFragment!=null)
            outState.putString("home" , homeFragment.getTag());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            String keySort = getSortKey();
            if (keySort.equals(getString(R.string.pref_sort_by_favorites_value))) {
                Log.d("GifHeaderParser", "onCreateActivity: " + keySort);

                favoriteFragment = new FavoriteMoviesFragment();
                favoriteFragment.setMovieListener(this);
                startFragment(favoriteFragment, "favoriteTag");
            } else {

                homeFragment = new Homefragment();
                homeFragment.setMovieListener(this);
                startFragment(homeFragment, "homeTag");
            }
        } else {
            if (savedInstanceState.containsKey("fav"))
                favoriteFragment = (FavoriteMoviesFragment) getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString("fav"));

            if (savedInstanceState.containsKey("home"))
                homeFragment = (Homefragment) getSupportFragmentManager().findFragmentByTag(savedInstanceState.getString("home"));
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startFragment(Fragment fragment , String tag) {

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment,tag).commit();
    }

    private String getSortKey() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                        .FLAG_ACTIVITY_CLEAR_TOP));

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMovieClickListener(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("current MovieItem", movieItem);
        startActivity(intent);
    }
}
