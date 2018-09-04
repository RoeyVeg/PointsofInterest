package com.example.pointsofinterest.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;
import com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.DETAILS_SENT;
import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.SENT_FROM_FAVORITE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFavoritesFragment extends Fragment {

    // one of the 3 fragments in main activity - contains a recycler view with places in favorite places, if it's a Tablet will show also the place detail fragment next to the recycler view (search details fragment)

    SearchRecyclerViewAdapter adapter;
    RecyclerView favoritePlacesView;
    ArrayList<Place> favoritePlacesList = new ArrayList<>();
    PlacesDBHelper placesDBHelper;
    boolean isTablet;
    public PlaceDetailsFragment placeDetailsInFavFragment;
    FragmentManager manager;
    FragmentTransaction transaction;

    Place selectedPlace;
    PlaceDetails placeDetails;



    public MainFavoritesFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        isTablet = getResources().getBoolean(R.bool.isTablet);


        adapter = new SearchRecyclerViewAdapter(getContext());

        View v = inflater.inflate(R.layout.fragment_main_favorites, container, false);

        placesDBHelper = new PlacesDBHelper(getContext());

        favoritePlacesView = v.findViewById(R.id.favoriteRecyclerView);

        favoritePlacesView.setAdapter(adapter);
        favoritePlacesView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        favoritePlacesView.addItemDecoration(dividerItemDecoration);

        favoritePlacesList.clear();
        favoritePlacesList.addAll(placesDBHelper.showPlacesResultsFavorites());
        adapter.updatePlacesList(favoritePlacesList);

        if (isTablet) { // if Tablet load the place details fragment

            if (manager==null) manager=getChildFragmentManager();
            transaction=manager.beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

            placeDetailsInFavFragment = new PlaceDetailsFragment();

            if (savedInstanceState!=null) { // manage orientation change

                Bundle bundle = new Bundle();
                bundle.putSerializable("place",savedInstanceState.getSerializable("place"));
                bundle.putSerializable(DETAILS_SENT,savedInstanceState.getSerializable(DETAILS_SENT));

                placeDetailsInFavFragment.setArguments(bundle);


            }


            transaction.replace(R.id.placeDetailsInTabFavoriteFragment,placeDetailsInFavFragment);

            transaction.commit();


        }

        return v;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) { // save the data to savedInstanceState bundle on orientation change
        super.onSaveInstanceState(outState);

        outState.putSerializable("place",this.selectedPlace);
        outState.putSerializable(DETAILS_SENT,this.placeDetails);
    }

    public void setPlaceDataToDetailsFragment(Place place, PlaceDetails placeDetails){ // implementation of the item click listener (in the recycler view) this method prepare the data for the Place detail fragment and passes it as Arguments to the fragment

        this.selectedPlace = place;
        this.placeDetails = placeDetails;

        try {


            placeDetailsInFavFragment = new PlaceDetailsFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("place",place);
            bundle.putSerializable(DETAILS_SENT,placeDetails);
            placeDetailsInFavFragment.setArguments(bundle);
            if (manager==null) return;
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            transaction.replace(R.id.placeDetailsInTabFavoriteFragment,placeDetailsInFavFragment);

            transaction.commit();


        } catch (NullPointerException e ) {

          //  Toast.makeText(getContext(), "error try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) { // we added it to solve the orientation change in Tablet since we got the error that the fragment wasn't attached, didn't solve the problem....
        super.onAttach(context);

        if(selectedPlace !=null) {
            placeDetailsInFavFragment = new PlaceDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("place", selectedPlace);
            bundle.putSerializable(DETAILS_SENT, placeDetails);
            placeDetailsInFavFragment.setArguments(bundle);
            //   if (manager==null) return;
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.placeDetailsInTabFavoriteFragment, placeDetailsInFavFragment);

            transaction.commit();
        }

    }

    @Override
    public void onStart() { // on returning to the activity (fragment) show the search result
        super.onStart();

        favoritePlacesList.clear();
        ArrayList<Place> testPlaces = placesDBHelper.showPlacesResultsFavorites();
        favoritePlacesList.addAll(placesDBHelper.showPlacesResultsFavorites());
        adapter.updatePlacesList(favoritePlacesList);

    }

    public void removeFavorites() {

        favoritePlacesList.clear();
        adapter.updatePlacesList(favoritePlacesList);
        adapter.notifyDataSetChanged();

    }


}
