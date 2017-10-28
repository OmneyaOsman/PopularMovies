package com.omni.moviewdb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_prefrences);

            Preference sortBy = findPreference(getString(R.string.pref_sort_by_key));

            bindPreferenceToSummary(sortBy);
        }

        private void bindPreferenceToSummary(Preference sortBy) {
            sortBy.setOnPreferenceChangeListener(this);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(sortBy.getContext());
            String prefString = pref.getString(sortBy.getKey() , "");
            onPreferenceChange(sortBy , prefString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newSort)
        {

            String sort = newSort.toString();
            if(preference instanceof ListPreference)
            {
                ListPreference list = (ListPreference) preference;
                int prefIndex = list.findIndexOfValue(sort);
                if(prefIndex >0){

                    CharSequence[] labels = list.getEntries();
                    preference.setSummary(labels[prefIndex]);

                } else {
                    preference.setSummary(sort);
                }
            }
            return true;
        }
    }
}
