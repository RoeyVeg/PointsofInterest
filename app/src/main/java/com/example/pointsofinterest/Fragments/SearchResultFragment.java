package com.example.pointsofinterest.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pointsofinterest.Activities.MainActivity;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;
import com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter;
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

import java.util.ArrayList;

import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;
import static com.example.pointsofinterest.AdaptersNServices.SearchIntentService.ACTION_SEARCH_PLACES;
import static com.example.pointsofinterest.AdaptersNServices.SearchIntentService.KEY;
import static com.example.pointsofinterest.Fragments.MainSearchFragment.LAST_SEARCH;

//************* search result fragment is one of the 2 main fragments, it shows all places received from the api call on a recycler view
// the recycler view is set up in the fragment - including fragment and all
// there is a receiver to get the Search Intent service which extract the data with a JSON and upload it to the list, if from Favorite this is already available in the DB
// there is a listener to notify Search Activity the query was received and can be proccessed


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private Switch showOpenSwitch;
    private RecyclerView recyclerView;
    private SearchRecyclerViewAdapter adapter;
    private ArrayList<Place> places = new ArrayList<>();
    private PlacesDBHelper placesDBHelper;
    private JSONArray resultArray;
    private SearchResultReceiver resultReceiver;
    private boolean receivedResult;
    private OnSearchResultReceivedListener listener;
    private GoogleMap mMap;

    private LatLng userPosition;
    private SharedPreferences sp;





    public SearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        placesDBHelper = new PlacesDBHelper(getContext());
        receivedResult = false;
        listener = (OnSearchResultReceivedListener) getActivity();
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        userPosition = new LatLng(sp.getFloat(USER_LAT, 0), sp.getFloat(USER_LNG, 0));

        //switch



        //receiver
        resultReceiver = new SearchResultReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(resultReceiver, new IntentFilter(ACTION_SEARCH_PLACES));

        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        showOpenSwitch = view.findViewById(R.id.openNowSwitch);
        showOpenSwitch.setOnCheckedChangeListener(this);

        adapter = new SearchRecyclerViewAdapter(getContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        if (getActivity().getIntent().getBooleanExtra(LAST_SEARCH,false)) getLastSearchResult();

        // Inflate the layout for this fragment
        return view;
    }

    public void getLastSearchResult() { // if the user pressed last search button in Main activity the recycler view will ulpoad the last serach from the DB

        if (placesDBHelper.showPlacesResults().isEmpty()) {
            Toast.makeText(getContext(), "" + R.string.last_result_empty, Toast.LENGTH_SHORT).show();

        } else {

            adapter.updatePlacesList(placesDBHelper.showPlacesResults());
            adapter.notifyDataSetChanged();
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultReceiver);

        }
    }



    // unregister the receiver when fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultReceiver);


    }

    @Override
    public void onStart() {
        super.onStart();

        if (!placesDBHelper.showPlacesResults().isEmpty()) {
            adapter.updatePlacesList(placesDBHelper.showPlacesResults());
            adapter.notifyDataSetChanged();

        }

    }

    private float calculateDistance(double lat, double lng) { // calculate current distance

        double myLat = 0;
        double myLng = 0;
        Location myLocation = new Location("");
        myLocation.setLatitude(myLat);
        myLocation.setLongitude(myLng);

        Location searchLocation = new Location("");
        searchLocation.setLatitude(lat);
        searchLocation.setLongitude(lng);



        return myLocation.distanceTo(searchLocation);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            adapter.updatePlacesList(placesDBHelper.showOpenPlacesResults());
            adapter.notifyDataSetChanged();

        } else {
            adapter.updatePlacesList(placesDBHelper.showPlacesResults());
            adapter.notifyDataSetChanged();
        }

    }


    public interface OnSearchResultReceivedListener {

        public void searchResultReceived (boolean isReceived);
    }

    public class SearchResultReceiver extends BroadcastReceiver {

        //      ArrayList<Place> jsonPlaces;

        @Override
        public void onReceive(Context context, Intent intent) {
            // jsonPlaces = new ArrayList<>();

            try {
                resultArray = new JSONObject(intent.getStringExtra("responsePlace")).getJSONArray("results");

                if (resultArray.length()>0) {
                    for (int i = 0; i < resultArray.length(); i++) { // this for loop populate the places results into places list

//
//                        private ArrayList<String> type;
//                        private ;

                        String id = resultArray.getJSONObject(i).getString("id");
                        String place_id = resultArray.getJSONObject(i).getString("place_id");

                        String photo_reference = "";
                        if (resultArray.getJSONObject(i).has("photos"))
                            photo_reference = resultArray.getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference");

                        String name = resultArray.getJSONObject(i).getString("name");
                        String address = resultArray.getJSONObject(i).getString("vicinity");
                        double lat = resultArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double lng = resultArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        String icon_url = resultArray.getJSONObject(i).getString("icon");

                        double rating = 0;
                        if (resultArray.getJSONObject(i).has("rating")) rating = resultArray.getJSONObject(i).getDouble("rating");

                        ArrayList<String> types = new ArrayList<>();
                        JSONArray typeArray = resultArray.getJSONObject(i).getJSONArray("types");

                        // getting all type values from JSON
                        if (typeArray.length()>0) {

                            for (int j = 0; j < typeArray.length(); j++) {

                                types.add(typeArray.getString(j));

                            }

                        }

                        String pic_url = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s",photo_reference,KEY);
                        float distanceFromCurrentLocation = calculateDistance(lat, lng);

                        boolean openNow = false;
                        if (resultArray.getJSONObject(i).has("opening_hours") && resultArray.getJSONObject(i).getJSONObject("opening_hours").has("open_now")) openNow = resultArray.getJSONObject(i).getJSONObject("opening_hours").getBoolean("open_now");

                        Place newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,types,pic_url,distanceFromCurrentLocation,openNow);
                        placesDBHelper.newPlaceItem(newPlace);


                    }


                    adapter.updatePlacesList(placesDBHelper.showPlacesResults());
                    adapter.notifyDataSetChanged();
                    receivedResult = true;
                    listener.searchResultReceived(receivedResult);




                } else if (resultArray.length()<=0) {
                    Toast.makeText(context, "No results found, please search again", Toast.LENGTH_SHORT).show();
                    listener.searchResultReceived(receivedResult);

                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "There was an error getting the data from the web", Toast.LENGTH_LONG).show();
                listener.searchResultReceived(receivedResult);


            }



        }


    }


}
