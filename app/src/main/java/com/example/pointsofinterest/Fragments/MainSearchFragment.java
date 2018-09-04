package com.example.pointsofinterest.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pointsofinterest.Activities.SearchResultActivity;
import com.example.pointsofinterest.AdaptersNServices.SearchIntentService;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;

import static com.example.pointsofinterest.Activities.MainActivity.KEY_WORD;
import static com.example.pointsofinterest.Activities.MainActivity.RADIUS;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainSearchFragment extends Fragment implements View.OnClickListener {

    public static final String LAST_SEARCH = "lastSearch" ;
    private ImageView findFoodBtn,findHangoutBtn,findTransportButton,findFinanceBtn,lastSearch,hospitalBtn;
    private SharedPreferences sp;
    private PlacesDBHelper placesDBHelper;
    private static final String FOOD_URL = "restaurant";
    private static final String HANGOUT_URL = "bar";
    private static final String TRANSPORT_URL = "bus_station";
    private static final String FINANCE_URL = "bank";
    private static final String HOSPITAL_URL = "hospital" ;


    // the fragment of the Home tab, contains 6 image button with pre defined searches, according to types, also initiate the search intent service


    public MainSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if (placesDBHelper.showPlacesResults().isEmpty()) {
            lastSearch.setVisibility(View.GONE);
        } else {lastSearch.setVisibility(View.VISIBLE); }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_main_search, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        placesDBHelper = new PlacesDBHelper(getContext());

        findFoodBtn = v.findViewById(R.id.foodSearchBtn);
        findHangoutBtn = v.findViewById(R.id.hangoutSearchBtn);
        findTransportButton = v.findViewById(R.id.transportSearchBtn);
        findFinanceBtn = v.findViewById(R.id.financeSearchBtn);
        lastSearch = v.findViewById(R.id.lastSearchBtnInMain);

        if (placesDBHelper.showPlacesResults().isEmpty()) {
            lastSearch.setVisibility(View.GONE);
        } else {lastSearch.setVisibility(View.VISIBLE); }

        hospitalBtn = v.findViewById(R.id.hospitalBtnInMain);

        findFoodBtn.setOnClickListener(this);
        findHangoutBtn.setOnClickListener(this);
        findTransportButton.setOnClickListener(this);
        findFinanceBtn.setOnClickListener(this);
        lastSearch.setOnClickListener(this);
        hospitalBtn.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {

        String url="&type=";

        switch (v.getId()) {

            case R.id.foodSearchBtn:

                url+=FOOD_URL;

                break;

            case R.id.hangoutSearchBtn:

                url+=HANGOUT_URL;

                break;

            case R.id.transportSearchBtn:

                url+=TRANSPORT_URL;

                break;

            case R.id.financeSearchBtn:

                url+=FINANCE_URL;

                break;

            case R.id.hospitalBtnInMain:

                url+=HOSPITAL_URL;

                break;

            case R.id.lastSearchBtnInMain: // intent to the Search Activity to show the last search result

                Intent lastSearchIntent = new Intent(getContext(),SearchResultActivity.class);
                lastSearchIntent.putExtra(LAST_SEARCH,true);
                getActivity().startActivity(lastSearchIntent);

                return;
        }

        searchIntent(url); // this method initiate the Intent Service to map-api web search with a type search
    }

    private void searchIntent(String url) {

        placesDBHelper.deleteSearchTables();


        Intent serviceIntent = new Intent(getContext(), SearchIntentService.class);

        // passing the search word user typed in search view
        serviceIntent.putExtra(KEY_WORD, url);

        // passing the radius
        String radius = sp.getString("searchRadius", "500");
        serviceIntent.putExtra(RADIUS, radius);

        // getting current user location
        serviceIntent.putExtra(USER_LAT,sp.getFloat(USER_LAT,0));
        serviceIntent.putExtra(USER_LNG,sp.getFloat(USER_LNG,0));
        getActivity().startService(serviceIntent);


        Intent intentToSearchActivity = new Intent(getContext(), SearchResultActivity.class);
        startActivity(intentToSearchActivity);

    }
}
