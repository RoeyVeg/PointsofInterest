package com.example.pointsofinterest.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.pointsofinterest.R;
import com.example.pointsofinterest.Fragments.UserPreferencesFragment;

public class UserPreferencesActivity extends AppCompatActivity {

    // the activity just load the preferences fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preferences);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserPreferencesFragment())
                .commit();


    }
}
