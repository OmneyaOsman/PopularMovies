package com.omni.moviewdb.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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


    private static String keySort = "";

    @Override
    protected void onResume() {
        super.onResume();

        String currentSort = getSortKey();

        if (currentSort.equals(keySort))
            return;
        else {
            keySort = currentSort;
            if (keySort.equals(getString(R.string.pref_sort_by_favorites_value))) {
                FavoriteMoviesFragment fragment = new FavoriteMoviesFragment();
                fragment.setMovieListener(this);
                startFragment(fragment);
            } else {

                Homefragment homefragment = new Homefragment();
                homefragment.setMovieListener(this);
                startFragment(homefragment);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        keySort = getSortKey();
        if (keySort.equals(getString(R.string.pref_sort_by_favorites_value))) {
            FavoriteMoviesFragment fragment = new FavoriteMoviesFragment();
            fragment.setMovieListener(this);
            startFragment(fragment);
        } else {

            Homefragment homefragment = new Homefragment();
            homefragment.setMovieListener(this);
            startFragment(homefragment);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
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
