package com.maulin.popularmovies.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.example.maulin.myappportfolio.R;


public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_SORT_ORDER ="movieSorting";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_prefrences);
        //set default selected sharedPreference Summery
        SharedPreferences sharedPrefrences=getPreferenceScreen().getSharedPreferences();
        setSummery(sharedPrefrences,KEY_SORT_ORDER);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(KEY_SORT_ORDER)) {
           setSummery(sharedPreferences,key);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * set preference summery
     * @param sharedPreferences shared preference
     * @param key key value
     */
    public void setSummery(SharedPreferences sharedPreferences,String key) {
        Preference moviePref = findPreference(key);
        // Set summary to be the user-description for the selected value
        String values=sharedPreferences.getString(key, "");
        switch (values) {
            case "most_popular":
                moviePref.setSummary("Most Popular");
                break;
            case "highest_rated":
                moviePref.setSummary("Highest Rated");
                break;
            default:
                moviePref.setSummary("Favourite");
                break;
        }
    }
}
