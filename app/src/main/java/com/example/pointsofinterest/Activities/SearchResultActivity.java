package com.example.pointsofinterest.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter;
import com.example.pointsofinterest.Fragments.PlaceDetailsFragment;
import com.example.pointsofinterest.Fragments.SearchResultFragment;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.DETAILS_SENT;
import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.SELECTED_PLACE;
import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.SENT_FROM_FAVORITE;
import static com.example.pointsofinterest.Fragments.MainSearchFragment.LAST_SEARCH;

public class SearchResultActivity extends AppCompatActivity implements SearchRecyclerViewAdapter.OnPlaceClickListener, SearchResultFragment.OnSearchResultReceivedListener, PlaceDetailsFragment.OnDetailsResultReceivedListener, PlaceDetailsFragment.OnBackToSearchButtonClicked {

    public static final String SELECTED_PLACE_NAME = "placeName";
    private SearchResultFragment searchResultFragment;
    private PlaceDetailsFragment placeDetailsFragment;
    private FrameLayout fragmentContainer;
    private FragmentManager fragmentManager= getSupportFragmentManager();
    private FragmentTransaction transaction;
    private PlacesDBHelper placesDBHelper;

    private ProgressDialog searchProgressDialog;

    private Place selectedPlace;
    private PlaceDetails selectedPlaceDetails;

    // flags

    boolean isTablet;
    boolean isFromFav;
    private boolean isLastSearchRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        isTablet = getResources().getBoolean(R.bool.isTablet); // I uses boolean values to identify if I'm in tablet according to screen size
        isFromFav = getIntent().getBooleanExtra(SENT_FROM_FAVORITE,false); // check if the trigger was favorites if it was I will start with place details fragment and not search result fragment
        isLastSearchRequest = getIntent().getBooleanExtra(LAST_SEARCH,false); // if users pressed last result in Main activity i will load the last search in the DB
        placesDBHelper = new PlacesDBHelper(this);

        // dialog


        searchProgressDialog = new ProgressDialog(this);
        searchProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        fragmentContainer = findViewById(R.id.searchResultContainer);

        setInitialView(savedInstanceState); // this method control which fragment to show and manage orientation changes



    }


    private void setInitialView(Bundle savedInstanceState){

        if (!isTablet && isFromFav) { // if I'm not using tablet and the activity was initiated from Main-favorites I start with place detail fragment


            Bundle bundle = new Bundle();
            bundle.putBoolean(SENT_FROM_FAVORITE,isFromFav);
            bundle.putSerializable("place",(Place) getIntent().getSerializableExtra("place")); // the intent from Main activity sends the data
            bundle.putSerializable(DETAILS_SENT,(PlaceDetails) getIntent().getSerializableExtra(DETAILS_SENT)); // the intent from Main activity sends the data
            showHideFragment(false,bundle); // show hide manage the switch between fragments and passes the bundle (content) to present

        }

        else if (isLastSearchRequest) {

            showHideFragment(true,null);

        }

        else {

            if (savedInstanceState!=null) { // this if manages the orientation changes

                selectedPlace = (Place) savedInstanceState.getSerializable("place");
                selectedPlaceDetails = (PlaceDetails) savedInstanceState.getSerializable(DETAILS_SENT);

                if (!savedInstanceState.getBoolean("searchFragmentHidden")) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("place",selectedPlace);
                    bundle.putSerializable(DETAILS_SENT,selectedPlaceDetails);
                    showHideFragment(false,bundle);

                } else {

                    showHideFragment(true,null);
                }

            } else {
                showHideFragment(true,null);
                searchProgressDialog.show();
            }

        }




    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {  // when changing the orientation I save the last fragment presented and the data
        super.onSaveInstanceState(outState);

        boolean searchFragmentIsHidden = false;
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.searchResultContainer);
        if (currentFragment instanceof SearchResultFragment ) searchFragmentIsHidden = true;

        outState.putBoolean("searchFragmentHidden",searchFragmentIsHidden);
        outState.putSerializable("place",selectedPlace);
        outState.putSerializable(DETAILS_SENT,selectedPlaceDetails);

    }

    @Override
    public void prepareForPlaceDetailsFragment(Place place, PlaceDetails placeDetails) { // listener implementation, triggered when an item in the recycler view is clicked, the listener passes the place and place details (if from favorite) then it can be passed to place details fragments

        this.selectedPlace = place;
        if (placeDetails!=null) this.selectedPlaceDetails = placeDetails;

        if (place!=null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("place",selectedPlace);
            bundle.putSerializable(DETAILS_SENT,selectedPlaceDetails);
            showHideFragment(false,bundle);

        }

        if (placeDetails==null) searchProgressDialog.show();

    }


    public void showHideFragment(boolean showSearch, Bundle bundle){ // show hide manage the switch between fragments and passes the bundle (content) to present

        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

        if (getResources().getBoolean(R.bool.isTablet)) {

            if (showSearch) {
                searchResultFragment = new SearchResultFragment();
                transaction.replace(R.id.searchResultTabletContainer,searchResultFragment);
            }
            placeDetailsFragment = new PlaceDetailsFragment();
            if (bundle!=null) placeDetailsFragment.setArguments(bundle);
            transaction.replace(R.id.placeDetailsTabletContainer,placeDetailsFragment);

        } else {

            if (!showSearch) {
                placeDetailsFragment = new PlaceDetailsFragment();
                if (bundle!=null) placeDetailsFragment.setArguments(bundle);
                transaction.replace(R.id.searchResultContainer,placeDetailsFragment);
            } else {
                searchResultFragment = new SearchResultFragment();
                transaction.replace(R.id.searchResultContainer,searchResultFragment);
            }

        }


        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_activity_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.backToHome:

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);

                break;
        }
        return true;
    }

    @Override
    public void searchResultReceived(boolean isReceived) { // listener to identify when the receiver got the data and presented in the recycle view  - this will stop the progress dialog

        if (isReceived) searchProgressDialog.dismiss();

    }

    @Override
    public void detailsResultReceived(boolean isReceived, PlaceDetails placeDetails) {  // listener to identify when the receiver got the data and presented in the recycle view  - this will stop the progress dialog

        if (isReceived) {

            selectedPlaceDetails = placeDetails;
            searchProgressDialog.dismiss();
        }

    }

    @Override
    public void backToSearchButtonClicked(boolean clicked) { // listener to identify  when the back to search result button in place details fragment was clicked

        if (clicked) if (isFromFav) finish();
            showHideFragment(true,null);
    }
}
