package com.example.pointsofinterest.AdaptersNServices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pointsofinterest.Activities.MainActivity;
import com.example.pointsofinterest.Activities.SearchResultActivity;
import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;
import com.example.pointsofinterest.Model.PlacesDBHelper;
import com.example.pointsofinterest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewAdapter> {


    /* Recycler view Adapter - one Adapter for both Favorite list and search result list

    - the adapter set up the layout view for each item - picture, texts, and change the color of open now to green
    - calculate the distance to current user location
    - the adapter identify it's current location (with the context) and sets the action accordignly
    - the adapter has listeners to notify Main and Search activity that a certain Item was clicked and passes the relevant data


     */

    public static final String PLACE_ID = "placeId";
    public static final String SELECTED_PLACE = "place";
    public static final String SENT_FROM_FAVORITE = "sent_from_favorites";
    public static final String DETAILS_SENT = "detailsSent";

    private ArrayList<Place> places = new ArrayList<>();
    private Context context;
    private PlacesDBHelper placesDBHelper;
    private SharedPreferences sp;
    private Place selectedPlace;
    boolean isTablet;
    OnPlaceClickListener listener;


    public SearchRecyclerViewAdapter(Context context) { // constructor
        this.context = context;
        this.placesDBHelper = new PlacesDBHelper(context);
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.isTablet = context.getResources().getBoolean(R.bool.isTablet);
        this.listener = (OnPlaceClickListener) context;


    }

    @NonNull
    @Override
    public SearchViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_view_adapter_item,parent,false);
        return new SearchViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewAdapter holder, int position) {

            holder.bind(places.get(position));


    }


    @Override
    public int getItemCount() {

        if (places.isEmpty()) return 0;
        return places.size();
    }

    public void updatePlacesList(ArrayList<Place> places) { // load a new data set to list
        this.places.clear();
        this.places.addAll(places);
        notifyDataSetChanged();

    }

    public void removePlaceItem(Place place){

        this.places.remove(place);
        notifyDataSetChanged();
    }

    public class SearchViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private static final String DISTANCE_UNITS = "distanceUnits";
        private ImageView mapImage, icon;
        private TextView placeName, address, distance, rating, openNow;

        public SearchViewAdapter(View itemView) { // identify all the view elements
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mapImage = itemView.findViewById(R.id.searchItemImageView);

            placeName = itemView.findViewById(R.id.searchItemName);

            address = itemView.findViewById(R.id.searchItemAddress);
            distance = itemView.findViewById(R.id.searchItemDistance);

            rating = itemView.findViewById(R.id.searchItemRating);
            icon = itemView.findViewById(R.id.searchItemIcon);

            openNow = itemView.findViewById(R.id.searchItemOpenNow);

        }

        public void bind(Place place) { // connect the view item to the data


                placeName.setText(place.getName());
                address.setText(place.getAddress());

                Location currentLocation = new Location("");
                currentLocation.setLatitude(sp.getFloat(USER_LAT, 0));
                currentLocation.setLongitude(sp.getFloat(USER_LNG, 0));

                Location placeLoc = new Location("");
                placeLoc.setLatitude(place.getLat());
                placeLoc.setLongitude(place.getLng());

                boolean inMiles = sp.getBoolean(DISTANCE_UNITS, false);
                String distanceSign = "km";
                if (inMiles) distanceSign = "mile";


                distance.setText(String.valueOf(String.format("%.2f", calculateDistance(currentLocation, placeLoc, inMiles))) + " " + distanceSign);

                String openNowModifiedText = ""; // open now is not shown in favorites and colored green if open now
                if (context instanceof SearchResultActivity) {
                    openNowModifiedText = place.isOpenNowToString();
                    if (place.isOpenNow()) {openNow.setTextColor(Color.parseColor("#3CB371"));}
                    else {openNow.setTextColor(Color.GRAY);}
                }
                openNow.setText(openNowModifiedText);

                rating.setText("Rating " + String.valueOf(String.format("%.2f",place.getRating())));

                // add the place image to image

                Picasso.with(context) // upload the place picture
                        .load(place.getPic_url())
                        .resize(100, 100)
                        .centerCrop()
                        .error(R.mipmap.ic_no_pic_round)
                        .into(mapImage);

                // add the icon image to icon

                Picasso.with(context) // upload the type icon
                        .load(place.getIcon_url())
                        .into(icon);



        }


        @Override
        public void onClick(View v) { // on clicks moves to show the place details and the map, identify the current context and trigger the listeners

            String id = places.get(getAdapterPosition()).getPlace_id();

            PlaceDetails placeDetailsFromDB = placesDBHelper.placeDetailInDB(id);


            if (context instanceof MainActivity ) { // am I in favorites ?

                if (placeDetailsFromDB==null) { // if I don't have the data in DB go get it

                    Intent detailsServiceIntent = new Intent(context, PlaceDetailsIntentService.class);

                    // passing the place ID
                    detailsServiceIntent.putExtra(PLACE_ID, places.get(getAdapterPosition()).getPlace_id());
                    context.startService(detailsServiceIntent);

                }


                if (context.getResources().getBoolean(R.bool.isTablet)) { // if I'm in tablet stay in Main


                    listener.prepareForPlaceDetailsFragment(places.get(getAdapterPosition()),placeDetailsFromDB);


                } else { // if I'm in mobile move to search activity


                    Intent intentToSearchActivity = new Intent(context, SearchResultActivity.class);
                    Place place = (Place) places.get(getAdapterPosition());
                    intentToSearchActivity.putExtra("place",place);
                    intentToSearchActivity.putExtra(DETAILS_SENT,placeDetailsFromDB);
                    intentToSearchActivity.putExtra(SENT_FROM_FAVORITE,true);
                    context.startActivity(intentToSearchActivity);
                }



            } else if (context instanceof SearchResultActivity ){ // meaning the user initiated a search



                if (placeDetailsFromDB==null) { // if I don't have the data in DB go get it

                    Intent detailsServiceIntent = new Intent(context, PlaceDetailsIntentService.class);

                    // passing the place ID
                    detailsServiceIntent.putExtra(PLACE_ID, places.get(getAdapterPosition()).getPlace_id());
                    context.startService(detailsServiceIntent);

                }

                listener.prepareForPlaceDetailsFragment(places.get(getAdapterPosition()),placeDetailsFromDB);


            }




        }


        @Override
        public boolean onLongClick(View v) { // long click open a dialog to delete from favorites or share it's available only in favorites - to remove from fav or to share, the add to favorite was implemented in Placedetails fragment


            if (context instanceof SearchResultActivity) return false;

            int addOrDeleteText = R.string.add_to_fav;

            if (context instanceof MainActivity) addOrDeleteText = R.string.delete_from_fav;

            final int finalAddOrDeleteText = addOrDeleteText;
            AlertDialog favOrShareDialog = new AlertDialog.Builder(context).
                    setTitle(R.string.add_to_fav_or_share).
                    setPositiveButton(addOrDeleteText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (finalAddOrDeleteText == R.string.add_to_fav) {

                                placesDBHelper.favoritesNewPlaceItem(places.get(getLayoutPosition()));

                                Toast.makeText(context, places.get(getLayoutPosition()).getName() + " was added to favorites", Toast.LENGTH_SHORT).show();


                            } else {

                                placesDBHelper.deleteFromFav(places.get(getLayoutPosition()),places.get(getLayoutPosition()).getId());
                                Toast.makeText(context, places.get(getLayoutPosition()).getName() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                                removePlaceItem(places.get(getLayoutPosition()));

                            }


                        }
                    }).
                    setNegativeButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent shareIntent=new Intent(android.content.Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,R.string.check_out_this_place);
                            shareIntent.putExtra(Intent.EXTRA_TEXT,places.get(getLayoutPosition()).getName() + " " + places.get(getLayoutPosition()).getAddress());

                            context.startActivity(Intent.createChooser(shareIntent,"which app to use?"));

                        }
                    }).setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create();

            favOrShareDialog.show();


            return false;
        }

        public float calculateDistance(Location currentLoc, Location placeLoc, boolean inMiles) { // method calculate the distance from current user location according to user choice (miles / km)

            float distance = currentLoc.distanceTo(placeLoc);

            if (!inMiles) {
                distance = distance / 1000;
            } else {
                distance = (float) (distance / 1609.344);
            }


            return distance;

        }


    }

    public interface OnPlaceClickListener { //click interface

        public void prepareForPlaceDetailsFragment(Place place,PlaceDetails placeDetails);

    }


}
