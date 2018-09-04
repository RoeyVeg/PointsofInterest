package com.example.pointsofinterest.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pointsofinterest.Activities.MainActivity;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.AdaptersNServices.PlaceDetailsListAdapter;
import com.example.pointsofinterest.AdaptersNServices.PlacePicturesAdapter;
import com.example.pointsofinterest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;
import static com.example.pointsofinterest.AdaptersNServices.DetailsIntentService.ACTION_PLACE_DETAILS;
import static com.example.pointsofinterest.AdaptersNServices.DetailsIntentService.RESPONSE_DETAILS;
import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.DETAILS_SENT;
import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.SENT_FROM_FAVORITE;


//************* Place details fragment is one of the 2 main fragments, it shows the selected place location on a Map and shows additional details
// on the place from the map-api search, such as opening hours, phone number, web page (not available any longer but it worked!!!)
// the fragment identify from where it was called : favorites, Search activity etc. and show the data accordignly
// there is a receiver to get the Place Details Intent service which extract the data with a JSON and upload it to the list, but the fragment first searches if the details data is available in the
// DB to avoid handling the search request, the place details are saved as a PlaceDetails class


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceDetailsFragment extends Fragment implements View.OnClickListener, AbsListView.OnScrollListener, OnMapReadyCallback, Serializable {

    private ListView listView;
    private GoogleMap mMap;

    private PlacesDBHelper placesDBHelper;
    private String webUrl, name, formatted_address, id;

    private TextView headerTextName;
    private ImageView shareBtn,favBtn;

    private LinearLayout stickyView;
    private View stickyViewSpacer;

    private RecyclerView picRecyclerView;
    private PlacePicturesAdapter picturesAdapter;

    LatLng userPosition;
    private Place selectedPlace;
    private PlaceDetails selectedPlaceDetails;
    private boolean calledFromFavorites;
    SharedPreferences sp;


    PlaceDetailsListAdapter adapter;
    DetailsReceiver detailsReceiver;

    private FloatingActionButton backToSearch;

    boolean isTablet;


    OnDetailsResultReceivedListener detailsListener;
    OnBackToSearchButtonClicked backToSearchListener;


    public PlaceDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_place_details, container, false);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        placesDBHelper = new PlacesDBHelper(getContext());

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        userPosition = new LatLng(sp.getFloat(USER_LAT, 0), sp.getFloat(USER_LNG, 0));


        // Inflate the layout for this fragment
        listView = (ListView) view.findViewById(R.id.listView);

        // Inflate list header layout
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = layoutInflater.inflate(R.layout.list_header, null); // place holder for the header
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);

        // find the picture recycler view

        picRecyclerView = listHeader.findViewById(R.id.picRecyclerView);

        picturesAdapter = new PlacePicturesAdapter(getContext());
        picRecyclerView.setAdapter(picturesAdapter);

        picRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.HORIZONTAL);
      //  picRecyclerView.addItemDecoration(itemDecoration);
//
//


        adapter = new PlaceDetailsListAdapter(getContext(), R.layout.list_row);
        listView.setAdapter(adapter);

        stickyView = (LinearLayout) view.findViewById(R.id.stickyView); // sticky view is the list header view which will be inserted to the place holder

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager() // create the map fragment
                .findFragmentById(R.id.mapInSearch);
        mapFragment.getMapAsync(this);

        // modify buttons

        backToSearch = view.findViewById(R.id.backToSearchButton); // floating action button to go back to the search result fragment, requires a listener to notify Search result activity
        if (isTablet) backToSearch.setVisibility(View.GONE); // not visible in Tablet because both fragments are showing
        backToSearch.setOnClickListener(this);

        shareBtn = view.findViewById(R.id.headerShareBtn);
        shareBtn.setOnClickListener(this);

        favBtn = view.findViewById(R.id.headerFavoriteBtn);
        headerTextName = view.findViewById(R.id.detailsHeaderName);

        getBasicData(); // get the argument sent by Search Activity

        // Add list view header
        listView.addHeaderView(listHeader);

        listView.setOnScrollListener(this); // manage view apperance

        detailsListener = (OnDetailsResultReceivedListener) getActivity(); // listener to identify when the search query result were received
        backToSearchListener = (OnBackToSearchButtonClicked)getActivity(); // listener to identify when the floating button was clicked

        if (getActivity() instanceof MainActivity) // for Tablet
        favBtn.setEnabled(false); // prevent the user to click before data was received from the receiver
        favBtn.setOnClickListener(this);

        detailsReceiver = new DetailsReceiver(); // register a receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(detailsReceiver, new IntentFilter(ACTION_PLACE_DETAILS));


        return view;
    }

    private void getBasicData() { // get the data from the Arguments and save them in the Fragment

        Bundle bundle = getArguments();

        if (bundle != null) {

            favBtn.setVisibility(View.VISIBLE);
            selectedPlace = (Place) bundle.getSerializable("place");
            selectedPlaceDetails = (PlaceDetails) bundle.getSerializable(DETAILS_SENT);
            updateDetailsList(selectedPlace, selectedPlaceDetails);

            if (bundle.getBoolean(SENT_FROM_FAVORITE)) {

                favBtn.setVisibility(View.GONE);

            }

            if (detailsReceiver!=null && selectedPlaceDetails!=null) LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(detailsReceiver);

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy(); // unregister the receiver

        if (detailsReceiver!=null) LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(detailsReceiver);

    }



    @Override
    public void onClick(View v) { // identify which button on the list header / or floating button was pressed - share intent, favorites

        switch (v.getId()) {


            case R.id.headerShareBtn:

                Intent shareIntent=new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                shareIntent.putExtra(Intent.EXTRA_SUBJECT,R.string.check_out_this_place);
                shareIntent.putExtra(Intent.EXTRA_TEXT,name + " " + formatted_address);

                getActivity().startActivity(Intent.createChooser(shareIntent,"which app to use?"));

                break;

            case R.id.headerFavoriteBtn:

                placesDBHelper.favoritesNewPlaceItem(selectedPlace);

                Toast.makeText(getContext(), selectedPlace.getName() + " " + getString(R.string.was_added_to_fav), Toast.LENGTH_SHORT).show();

                break;

            case R.id.backToSearchButton:

                backToSearchListener.backToSearchButtonClicked(true);

                break;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { // the On Scroll listener is to identify where is the first item of the list is located and to set the header accordignly

        // Check if the first item is already reached to top
//        if (listView.getFirstVisiblePosition() == 0) {
//            View firstChild = listView.getChildAt(0);
//            int topY = 0;
//            if (firstChild != null) {
//                topY = firstChild.getTop();
//            }
//
//            int heroTopY = stickyViewSpacer.getTop();
//            stickyView.setY(Math.max(0, heroTopY + topY));
//
//        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // when map loads I show a marker of current position and selected place

        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().
                title(getString(R.string.your_locatoin)).
                position(userPosition).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).
                alpha(0.5f));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition,14f));

        if (selectedPlace!=null) {

            LatLng newPosition = new LatLng(selectedPlace.getLat(),selectedPlace.getLng());
//

            mMap.addMarker(new MarkerOptions().
                    position(newPosition).
                    title(selectedPlace.getName() + " " + selectedPlace.getAddress()));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition,14f));

        }

    }


    public class DetailsReceiver extends BroadcastReceiver { // the broadcast Receiver class that mange the Place details Intent service - get the string and extract it with JSON
        // creates a PlaceDetails class and save it, then notify the Search Activity the data was received and Search activity can react as needed




        @Override
        public void onReceive(Context context, Intent intent) {

            adapter.clear();

            JSONObject rootObject;

            try {
                rootObject = new JSONObject(intent.getStringExtra(RESPONSE_DETAILS));

                if (rootObject.has("result")) {

                    JSONObject result = rootObject.getJSONObject("result");

                    //,,opening_hours,photos,,

                    String place_id = result.getString("place_id");
                    id = result.getString("id");
                    name = result.getString("name");
                    formatted_address = result.getString("formatted_address");
                    String formattedPhoneNumber = result.getString("formatted_phone_number");

                    String website = getString(R.string.no_site_available);

                    if (result.has("website"))
                        website = getString(R.string.website);
                        webUrl = result.getString("website");
                        adapter.setWebSiteUrl(webUrl);

                    String mapUrl;

                    if (result.has("url"))
                        mapUrl = result.getString("url");

                    StringBuilder builder = new StringBuilder();

                    if (result.has("opening_hours")) { // to get the weekdays I need to get the opening_hours object, and then the weekday_text Array
                        // I use a string builder to go over the array and sort it as needed (Sunday first and Saturday last)

                        JSONArray jsonArray = result.getJSONObject("opening_hours").getJSONArray("weekday_text");
                        boolean isOpenNow = result.getJSONObject("opening_hours").getBoolean("open_now");
                        String openNow = getString(R.string.open_now);
                        if (!isOpenNow) openNow = getString(R.string.not_open_now);
                        if (jsonArray.length()>0) {

                            builder.append(openNow+"\n");
                            builder.append(jsonArray.getString(6)+"\n");
                            builder.append(jsonArray.getString(0)+"\n");
                            builder.append(jsonArray.getString(1)+"\n");
                            builder.append(jsonArray.getString(2)+"\n");
                            builder.append(jsonArray.getString(3)+"\n");
                            builder.append(jsonArray.getString(4)+"\n");
                            builder.append(jsonArray.getString(5)+"\n");

                        }


                    }

                    double rating = 0;

                    if (result.has("rating")) rating = result.getDouble("rating");

                    String ratingAsText = getString(R.string.rating) + " " + String.valueOf(String.format("%.2f",rating));

                    String openingHours = builder.toString();

                    ArrayList<String> photos = new ArrayList<String>();

                    if (result.has("photos")) {

                        JSONArray photosArray = result.getJSONArray("photos");

                        for (int i = 0; i < photosArray.length(); i++) {

                            JSONObject newPhoto = photosArray.getJSONObject(i);
                            photos.add(newPhoto.getString("photo_reference"));
                            
                        }
                    }

                    headerTextName.setText(name);

                    adapter.add(ratingAsText);
                    adapter.add(formatted_address);
                    adapter.add(formattedPhoneNumber);
                    adapter.add(website);
                    adapter.add(openingHours);

                    picturesAdapter.setPictures(photos);

                    PlaceDetails newPlaceDetails = new PlaceDetails(place_id,rating,formatted_address,formattedPhoneNumber,openingHours,website,webUrl,photos);

                    placesDBHelper.newPlaceDetailsItem(newPlaceDetails);

                    shareBtn.setEnabled(true);
                    favBtn.setEnabled(true);
                    updateMap(placesDBHelper.getPlace(id)); // after getting the data I can update the map with the location
                    detailsListener.detailsResultReceived(true, newPlaceDetails); // tells the Search activity data was received
                }

                else  { // if there is an error - no data in result or errors in reading the JSON object the user get's a Toast

                    Toast.makeText(context, "There was an error getting the data from the web", Toast.LENGTH_LONG).show();
                    detailsListener.detailsResultReceived(true,null);

                }


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "There was an error getting the data from the web", Toast.LENGTH_LONG).show();
                detailsListener.detailsResultReceived(true, null);


            }

        }
    }

    public void updateDetailsList(Place place,PlaceDetails placeDetails) { //***** this method update the place details data in the list

        if (place!=null)headerTextName.setText(place.getName());

        if (placeDetails==null) return;

        picturesAdapter.setPictures(placeDetails.getPhotos_reference());


        adapter.clear();

        adapter.add(getString(R.string.rating) + " " + String.valueOf(String.format("%.2f",placeDetails.getRating())));
        adapter.add(placeDetails.getFormattedAddress());
        adapter.add(placeDetails.getFormattedPhoneNumber());
        adapter.add(placeDetails.getWebSite());
        adapter.add(placeDetails.getWeekdayText());


    }

    public void updateMap(Place place) { // ************ this method clear all markers on map and shows the location of the selected place

            mMap.clear();

            mMap.addMarker(new MarkerOptions().
                    title(getString(R.string.your_locatoin)).
                    position(userPosition).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).
                    alpha(0.5f));

            LatLng newPosition = new LatLng(place.getLat(),place.getLng());
//

            mMap.addMarker(new MarkerOptions().
                    position(newPosition).
                    title(place.getName() + " " + place.getAddress()));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPosition,14f));
//


    }


    public interface OnDetailsResultReceivedListener { // ************ listener to update SearchResultActivity that the intent service has got the results

        public void detailsResultReceived (boolean isReceived, PlaceDetails placeDetails);
    }

    public interface OnBackToSearchButtonClicked { // ************ listener to update SearchResultActivity that back to search result button was pressed, this will trigger replacing the fragments

        public void backToSearchButtonClicked (boolean clicked);
    }
}
