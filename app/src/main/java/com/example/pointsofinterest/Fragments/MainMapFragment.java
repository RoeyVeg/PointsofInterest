package com.example.pointsofinterest.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pointsofinterest.Activities.MainActivity;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;
import static com.example.pointsofinterest.AdaptersNServices.AroundUserIntentService.ACTION_SEARCH_ALL_PLACES;
import static com.example.pointsofinterest.AdaptersNServices.AroundUserIntentService.RESPONSE_ALL_PLACES;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng userPosition;
    private SharedPreferences sp;
    private TextView radiusText;
    private JSONArray resultArray;
    private AroundUserReceiver aroundUserReceiver;
    private Circle searchCircle;
    private PlacesDBHelper placesDBHelper;


//************ a Google map fragment, shows all the places around the user - with markers, use location in blue and a circle based on the current search radius


    public MainMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_main_map, container, false);

        aroundUserReceiver = new AroundUserReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(aroundUserReceiver, new IntentFilter(ACTION_SEARCH_ALL_PLACES)); // initiate a Intent service to web search api to get all placs around user

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager() // create the map fragment
                .findFragmentById(R.id.mapInMain);
        mapFragment.getMapAsync(this);


        radiusText = v.findViewById(R.id.mapCircleRadius);
        radiusText.setText(getString(R.string.search_radius_text) + " " + sp.getString("searchRadius","500")+ " meter"); // a text view indicating the user what is the circle radius

        userPosition = new LatLng(sp.getFloat(USER_LAT,0),sp.getFloat(USER_LNG,0));

        placesDBHelper = new PlacesDBHelper(getContext());
        updateSearchCircleOnMap(); // create and update the search circle on the map



        return v;
    }

    @Override
    public void onStart() { // see above
        super.onStart();

        radiusText.setText(getString(R.string.search_radius_text) + " " + sp.getString("searchRadius","500")+ " meter");
        updateSearchCircleOnMap();


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    // unregister the receiver when fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(aroundUserReceiver);
    }

    @Override
    public void onMapLoaded() {



    }

    @Override
    public void onMapReady(GoogleMap googleMap) { // when map is ready show the current user location - in blue, create the search circle, add markers of all places in the map

        mMap = googleMap;
        mMap.addMarker(new MarkerOptions(). // user location
                title(getString(R.string.your_locatoin)).
                position(userPosition).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).
                alpha(0.5f));

        List<PatternItem> pattern = Arrays.<PatternItem>asList( // circle pattern
                new Dot(), new Gap(20), new Dash(30), new Gap(20));

        CircleOptions searchCircleOptions = new CircleOptions().
                center(userPosition).
                radius(Float.parseFloat(sp.getString("searchRadius","500"))).
                strokePattern(pattern).
                strokeColor(Color.BLUE);

        searchCircle = mMap.addCircle(searchCircleOptions); // create a search circle object that can be identified and changed

        float zoom = 15f;
        if (Integer.parseInt(sp.getString("searchRadius","500"))>500) zoom = 14f;

        addMapMarker(placesDBHelper.showPlacesResultsAll()); // call add marker method with all the places results

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition,zoom));




    }

    // method update the circle on the map if the user changed the search radius in preferences
    public void updateSearchCircleOnMap(){

        if (searchCircle!=null) {
            searchCircle.remove();

            List<PatternItem> pattern = Arrays.<PatternItem>asList(
                    new Dot(), new Gap(20), new Dash(30), new Gap(20));


            CircleOptions searchCircleOptions = new CircleOptions().
                    center(userPosition).
                    radius(Float.parseFloat(sp.getString("searchRadius","500"))).
                    strokePattern(pattern).
                    strokeColor(Color.BLUE);

            searchCircle = mMap.addCircle(searchCircleOptions);

        }

    }

    public class AroundUserReceiver extends BroadcastReceiver { // the Around user Intent service receiver, get's the result as string and extract the data with Json

        ArrayList<Place> jsonPlaces = new ArrayList<>();


        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                resultArray = new JSONObject(intent.getStringExtra(RESPONSE_ALL_PLACES)).getJSONArray("results"); // the result is an array

                if (resultArray.length()>0) {
                    for (int i = 0; i < resultArray.length(); i++) { // this for loop populate the places results into places list


                        String id = resultArray.getJSONObject(i).getString("id");
                        String place_id = resultArray.getJSONObject(i).getString("place_id");

                        String name = resultArray.getJSONObject(i).getString("name");
                        String address = resultArray.getJSONObject(i).getString("vicinity");
                        double lat = resultArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double lng = resultArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                        Place newPlace = new Place(id,place_id,name,address,lat,lng);
                        jsonPlaces.add(newPlace);
                        placesDBHelper.allTableNewPlaceItem(newPlace); // add the place to the DB

                    }


                } else if (resultArray.length()<=0) {
                    Toast.makeText(context, "No results found, please search again", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

                addMapMarker(placesDBHelper.showPlacesResultsAll()); // after getting all results pass it to the addMapMarker to update the map


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "There was an error getting the data from the web", Toast.LENGTH_LONG).show();


            }


        }

    }

    public void addMapMarker(ArrayList<Place> aroundPlaces) { // this method iterate over the result list, get's the latitude and longtitude and put a marker on the map

        if (aroundPlaces.size()>0) {

            for (Place place : aroundPlaces) {

                LatLng newPosition = new LatLng(place.getLat(),place.getLng());

                mMap.addMarker(new MarkerOptions().
                        position(newPosition).
                        title(place.getName() + " " + place.getAddress()));
            }

        }

    }
}
