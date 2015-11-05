package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.preference.PreferenceFragment;


public class SettingsActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        // Display the fragment
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsActivityFragment())
                .commit();


    }



    // the settings fragment
    public static class SettingsActivityFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener
    {


        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // load preferences from XML resource
            addPreferencesFromResource(R.xml.preferences);

            // initialize the summaries
            initSummary(getPreferenceScreen());

        }

        @Override
        public void onResume()
        {
            super.onResume();
            // register the SharedPreferences listener
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause()
        {
            super.onPause();
            // unregister the listener
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Log.d("Settings", key);

            // when a SharedPreference changes, update its summary
            updatePrefSummary(findPreference(key));

        }

        // run through the list of preferences, and update the summary for all
        // preferences that are not derived from PreferenceGroup
        private void initSummary(Preference p)
        {
            if ( p instanceof PreferenceGroup)
            {   PreferenceGroup pg = (PreferenceGroup)p;
                for( int i=0; i < pg.getPreferenceCount(); i++ )
                    initSummary(pg.getPreference(i));
            }
            else
            {
                updatePrefSummary(p);
            }
        }

        // update the summary field for different types of preferences
        private void updatePrefSummary(Preference p)
        {
            if ( p instanceof ListPreference )
            {
                ListPreference lp = (ListPreference)p;
                p.setSummary(lp.getEntry());
            }
            if ( p instanceof EditTextPreference)
            {   EditTextPreference etp = (EditTextPreference)p;
                p.setSummary(etp.getText());
            }
        }
    }
}
