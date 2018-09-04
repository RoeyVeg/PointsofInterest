package com.example.pointsofinterest.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.example.pointsofinterest.R;

public class UserPreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SharedPreferences userPreferences;
    private ListPreference listPreference;

    final static String KEY_SEARCH = "searchRadius";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_preferences);

         userPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
         String units = userPreferences.getString("searchRadius","500"); // switch button for KM / Miles - saved in SharedPreferences

        listPreference = (ListPreference) findPreference("searchRadius"); // option list to choose the search radius
        listPreference.setSummary("Current search radius: " + listPreference.getValue() + " M"); // shows the current selected radius in Meters
        listPreference.setOnPreferenceChangeListener(this); // listener is needed to update the text under the title



    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {


        switch (preference.getKey()) {


            case KEY_SEARCH:

                listPreference.setSummary("Current search radius: " + newValue + " M");


                break;
        }
        return true;
    }
}
