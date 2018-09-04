package com.example.pointsofinterest.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pointsofinterest.AdaptersNServices.AroundUserIntentService;
import com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter;
import com.example.pointsofinterest.Fragments.MainFavoritesFragment;
import com.example.pointsofinterest.Fragments.MainMapFragment;
import com.example.pointsofinterest.Fragments.MainSearchFragment;
import com.example.pointsofinterest.AdaptersNServices.MainFragmentPagerAdapter;
import com.example.pointsofinterest.Fragments.PlaceDetailsFragment;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;
import com.example.pointsofinterest.AdaptersNServices.SearchIntentService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LocationListener, SearchRecyclerViewAdapter.OnPlaceClickListener, PlaceDetailsFragment.OnDetailsResultReceivedListener,PlaceDetailsFragment.OnBackToSearchButtonClicked {



   /*  Hi Nir, welcome to my App, hope you'll enjoy it


   I have 3 Activities:
   1. Main - has 3 fragments in tablayout :
        Map fragment - show all places around user (I show only 20 searches)
        Main fragment - preset searches button and show last search button
        Favorites - favorites list (recycler view) , on long click you can remove from favorites or share

        main has a menu to : reach preferences, erase all favorites, exit

   2. Search Activity - manages the search result (fragment 1) and place details (fragment 2) , manage the two fragments
        Search result fragment - a recycler view, on click the place details appear
        Place details fragment - contains a map fragment and and a list that shows details with header
            the fragment get's the details of a place according to the place_id, the details can arrive from intentService or from
            the DB if the details are already in the DB (I add the as I go)
            I reach the place details in 2 cases:
            1. I press a place in the place result (in Search Activity)
            2. I press a favorite place result (in Main Activity) via an intent that get the details from the Favorite DB

        Search Activity has a menu to : home button go back to main activity

   3. User preference Activity - contain a preference fragment which enable :
            1. toggle mile / KM (default is KM)
            2. choose the search radius (default is 0.5 KM)


   Intent services :
   1. Search intent service - to get search result by keyword or by pre defined searches (in Main)
   2. Place details service - to get details for specific place, each time a place details are shown I save them to the DB for quicker
      access on next search

   Model :
   1. Place class
   2. place detail class
   3. PlaceDBHelper - to manage all the tables for places, details, favorites and allPlaces (for the places around me in Main)

   Tablet : - to identify if I'm in Tablet or not I used Resource values boolean with screen size as qualifier

   1. in Main - the favorites tab includes favorite list with placed details fragment
   2. in Search activity the search result fragments and placed details fragment are both shown

   Strings - I've used strings where possible, didn't translate them to hebrew, if needed I would have done it in Strings - open editor
   Mimap and drawable - most of the images were created using Android resources

   Search view - I created a custom shape for a rounded search view under drawable



   *
   *
   *
   *
   *
   *
   *
   *
   *
   * */







    private static final int LOCATION_REQUEST_CODE = 111;
    private SharedPreferences sp;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private MainFragmentPagerAdapter mainFragmentPagerAdapter;
    private MainFavoritesFragment mainFavoritesFragment = new MainFavoritesFragment();
    private MainSearchFragment mainSearchFragment = new MainSearchFragment();
    private MainMapFragment mainMapFragment = new MainMapFragment();
    ArrayList<Fragment> fragments;
    private SearchView searchView;
    public static final String KEY_WORD = "type";
    public static final String RADIUS = "searchRadius";
    public static final String USER_LAT = "lat";
    public static final String USER_LNG = "lng";
    LocationManager locationManager;
    PlacesDBHelper placesDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        placesDBHelper = new PlacesDBHelper(this);

        searchView = findViewById(R.id.mainSearchView);
        searchView.setOnQueryTextListener(this);

        fragmentManager = getSupportFragmentManager();

        fragments = new ArrayList<>();
        fragments.add(mainSearchFragment); // fragment with pre defined searches
        fragments.add(mainFavoritesFragment);  // fragment that shows the favorites in recycler view

        fragments.add(mainMapFragment); // fragment that shows the map with the serach radius circle and markers of all places (20) around user

        mainFragmentPagerAdapter = new MainFragmentPagerAdapter(fragmentManager, fragments,this);

        viewPager = findViewById(R.id.mainViewPager);
        viewPager.setAdapter(mainFragmentPagerAdapter);
        tabLayout = findViewById(R.id.mainTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        getUserCurrentLocation(); // initiate the GPS to get current location and save it in shared preferences for global access
        getAllPlacesAroundUser(); // initiate a search of all places around user location to show on Map

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // inflate the menu

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // manage item click events

        switch (item.getItemId()) {

            case R.id.mainMenuExit: // close the app

                Toast.makeText(this, "See you next time", Toast.LENGTH_SHORT).show();
                finish();

                break;

            case R.id.mainMenuPref: // intent to preferences

                Intent intent = new Intent(this, UserPreferencesActivity.class);
                startActivity(intent);

                break;

            case R.id.mainMenuClearFavorites: // delete all favorites, pop up a dialog to verify

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.clearFavCheck)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                placesDBHelper.deleteFavTable();
                                mainFavoritesFragment.removeFavorites();

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();

                dialog.show();


                break;


        }


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) { // manage the search view event, the text is stored as KEY_WORD and the searchIntentService is initiated and also intent to search Activity


        placesDBHelper.deleteSearchTables(); // I erase the previous search result


        Intent serviceIntent = new Intent(this, SearchIntentService.class);
        getUserCurrentLocation();

        // passing the search word user typed in search view
        serviceIntent.putExtra(KEY_WORD, "&keyword=" + searchView.getQuery().toString());

        // passing the radius
        String radius = sp.getString("searchRadius", "500");
        serviceIntent.putExtra(RADIUS, radius);

        // getting current user location and passing it to the service
        serviceIntent.putExtra(USER_LAT,sp.getFloat(USER_LAT,0));
        serviceIntent.putExtra(USER_LNG,sp.getFloat(USER_LNG,0));
        startService(serviceIntent);

        Intent intentToSearchActivity = new Intent(this, SearchResultActivity.class);
        startActivity(intentToSearchActivity);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void getUserCurrentLocation() { // start GPS (with permission request)


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);

        }

    }
// starting the service intent which get's all places around user to show on map
    public void getAllPlacesAroundUser(){

        Intent aroundUserIntentService = new Intent(this,AroundUserIntentService.class);

        String radius = sp.getString("searchRadius", "500");
        aroundUserIntentService.putExtra(RADIUS, radius);

        // getting current user location
        aroundUserIntentService.putExtra(USER_LAT,sp.getFloat(USER_LAT,0));
        aroundUserIntentService.putExtra(USER_LNG,sp.getFloat(USER_LNG,0));
        startService(aroundUserIntentService);

    }

    @Override // GPS permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==LOCATION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getUserCurrentLocation();

            } else {

                Toast.makeText(this, "Can't get current location from GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override // get the current user location
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        sp.edit().putFloat(USER_LAT,(float) lat).apply();
        sp.edit().putFloat(USER_LNG,(float) lng).apply();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override // Main activity need to implement this listener due to the recyclerview list in favorites, passes the details of the
    // place and place details that were selected in the recycler view in the favorite fragment to the place fragment (for tablet only)
    public void prepareForPlaceDetailsFragment(Place place,PlaceDetails placeDetails) {


        mainFavoritesFragment.setPlaceDataToDetailsFragment(place,placeDetails);

    }


    @Override // Main activity need to implement this listener due to the recyclerview list in favorites
    public void detailsResultReceived(boolean isReceived, PlaceDetails placeDetails) {

    }

    @Override // Main activity need to implement this listener due to the recyclerview list in favorites
    public void backToSearchButtonClicked(boolean clicked) {

    }
}
