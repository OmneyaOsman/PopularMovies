package com.omni.moviewdb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.omni.moviewdb.event.MovieClickListener;
import com.omni.moviewdb.fragments.Homefragment;
import com.omni.moviewdb.model.movieResponse.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  implements MovieClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        Homefragment homefragment = new Homefragment();
        homefragment.setMovieListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container , homefragment).commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu , menu);

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
    public void onMovieClickListener(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra("current Movie", movie);
        startActivity(intent);
    }
}
