package com.example.pointsofinterest.Model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.pointsofinterest.Model.Place;
import com.example.pointsofinterest.Model.PlaceDetails;

import java.util.ArrayList;

public class PlacesDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "placesDB";

    // places columns - all places near the user, same columns as PLACES  /**************



    //********* this is the all places around me table for the whats around me Map

    public static final String ALL_PLACES_TABLE_NAME = "all_places";

    public static final String ALL_PLACES_COL_ID = "id";
    public static final String ALL_PLACES_COL_ID_PLACE = "place_id";
    public static final String ALL_PLACES_COL_PHOTO_REF = "photo_reference";
    public static final String ALL_PLACES_COL_NAME = "name";
    public static final String ALL_PLACES_COL_ADDRESS = "address";
    public static final String ALL_PLACES_COL_LAT = "lat";
    public static final String ALL_PLACES_COL_LNG = "lng";
    public static final String ALL_PLACES_COL_ICON_URL = "icon_url";
    public static final String ALL_PLACES_COL_RATING = "rating";
    public static final String ALL_PLACES_COL_TYPE = "type";
    public static final String ALL_PLACES_COL_PIC_URL = "pic_url";



    //********* this is the last - current search result table


    public static final String PLACES_TABLE_NAME = "places";

    public static final String PLACES_COL_ID = "id";
    public static final String PLACES_COL_ID_PLACE = "place_id";
    public static final String PLACES_COL_PHOTO_REF = "photo_reference";
    public static final String PLACES_COL_NAME = "name";
    public static final String PLACES_COL_ADDRESS = "address";
    public static final String PLACES_COL_LAT = "lat";
    public static final String PLACES_COL_LNG = "lng";
    public static final String PLACES_COL_ICON_URL = "icon_url";
    public static final String PLACES_COL_RATING = "rating";
    public static final String PLACES_COL_TYPE = "type";
    public static final String PLACES_COL_PIC_URL = "pic_url";
    public static final String PLACES_COL_OPEN_NOW = "open_now"; // open now is only relevant in search not for favorites


    // places_details - current search /**************

    public static final String DETAILS_TABLE_NAME = "places_details";

    public static final String DETAILS_COL_ID = "place_id";
    public static final String DETAILS_COL_RATING = "rating";
    public static final String DETAILS_COL_FORMATTED_ADDRESS = "formatted_address";
    public static final String DETAILS_COL_FORMATTED_PHONE = "formatted_phone_number";
    public static final String DETAILS_COL_WEEKDAY_TEXT = "weekday_text";
    public static final String DETAILS_COL_WEB_SITE = "web_site";
    public static final String DETAILS_COL_PLACE_URL = "place_url";
    public static final String DETAILS_COL_PHOTOS_URL = "photos_url";


    // places columns for favorites - same columns /**************

    public static final String PLACES_FAV_TABLE_NAME = "favorite_places";

    public static final String FAV_PLACES_COL_ID = "id";
    public static final String FAV_PLACES_COL_ID_PLACE = "place_id";
    public static final String FAV_PLACES_COL_PHOTO_REF = "photo_reference";
    public static final String FAV_PLACES_COL_NAME = "name";
    public static final String FAV_PLACES_COL_ADDRESS = "address";
    public static final String FAV_PLACES_COL_LAT = "lat";
    public static final String FAV_PLACES_COL_LNG = "lng";
    public static final String FAV_PLACES_COL_ICON_URL = "icon_url";
    public static final String FAV_PLACES_COL_RATING = "rating";
    public static final String FAV_PLACES_COL_TYPE = "type";
    public static final String FAV_PLACES_COL_PIC_URL = "pic_url";


    //******* History table is a preparation for... I wanted to enable a history searches functionallity which I didn't enabled at the end, but the infrastructure is there!

    public static final String HISTORY_PLACES_TABLE_NAME = "historical_places";


    SharedPreferences sp;
    Geocoder geocoder;



// creating the DB
    public PlacesDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        geocoder = new Geocoder(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

// creating the  5 TABLES

        String sql_all_places = String.format("create table %s (%s TEXT PRIMARY KEY, %s text, %s text, %s text, %s text, %s real, %s real, %s text, %s real, %s text, %s text)", ALL_PLACES_TABLE_NAME,ALL_PLACES_COL_ID,ALL_PLACES_COL_ID_PLACE,ALL_PLACES_COL_PHOTO_REF,ALL_PLACES_COL_NAME,ALL_PLACES_COL_ADDRESS,ALL_PLACES_COL_LAT,ALL_PLACES_COL_LNG,ALL_PLACES_COL_ICON_URL,ALL_PLACES_COL_RATING,ALL_PLACES_COL_TYPE,ALL_PLACES_COL_PIC_URL);
        db.execSQL(sql_all_places);

        String sql_history_places = String.format("create table %s (%s TEXT PRIMARY KEY, %s text, %s text, %s text, %s text, %s real, %s real, %s text, %s real, %s text, %s text, %s integer)", HISTORY_PLACES_TABLE_NAME,PLACES_COL_ID,PLACES_COL_ID_PLACE,PLACES_COL_PHOTO_REF,PLACES_COL_NAME,PLACES_COL_ADDRESS,PLACES_COL_LAT,PLACES_COL_LNG,PLACES_COL_ICON_URL,PLACES_COL_RATING,PLACES_COL_TYPE,PLACES_COL_PIC_URL,PLACES_COL_OPEN_NOW);
        db.execSQL(sql_history_places);

        String sql_places = String.format("create table %s (%s TEXT PRIMARY KEY, %s text, %s text, %s text, %s text, %s real, %s real, %s text, %s real, %s text, %s text, %s integer)", PLACES_TABLE_NAME,PLACES_COL_ID,PLACES_COL_ID_PLACE,PLACES_COL_PHOTO_REF,PLACES_COL_NAME,PLACES_COL_ADDRESS,PLACES_COL_LAT,PLACES_COL_LNG,PLACES_COL_ICON_URL,PLACES_COL_RATING,PLACES_COL_TYPE,PLACES_COL_PIC_URL,PLACES_COL_OPEN_NOW);
        db.execSQL(sql_places);

        String sql_places_fav = String.format("create table %s (%s TEXT PRIMARY KEY, %s text, %s text, %s text, %s text, %s real, %s real, %s text, %s real, %s text, %s text)", PLACES_FAV_TABLE_NAME,FAV_PLACES_COL_ID,FAV_PLACES_COL_ID_PLACE,FAV_PLACES_COL_PHOTO_REF,FAV_PLACES_COL_NAME,FAV_PLACES_COL_ADDRESS,FAV_PLACES_COL_LAT,FAV_PLACES_COL_LNG,FAV_PLACES_COL_ICON_URL,FAV_PLACES_COL_RATING,FAV_PLACES_COL_TYPE,FAV_PLACES_COL_PIC_URL);
        db.execSQL(sql_places_fav);

        String sql_places_details = String.format("create table %s (%s TEXT PRIMARY KEY, %s real, %s text, %s text, %s text, %s text, %s text, %s text)", DETAILS_TABLE_NAME,DETAILS_COL_RATING,DETAILS_COL_ID,DETAILS_COL_FORMATTED_ADDRESS,DETAILS_COL_FORMATTED_PHONE,DETAILS_COL_WEEKDAY_TEXT,DETAILS_COL_WEB_SITE,DETAILS_COL_PLACE_URL,DETAILS_COL_PHOTOS_URL);
        db.execSQL(sql_places_details);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // check if place was already saved to favorites
    public boolean placeIsAlreadyFavorite(String id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(PLACES_FAV_TABLE_NAME,null,FAV_PLACES_COL_ID + " =?",new String[] {id},null,null,null);

        if (cursor.moveToNext()) {

            db.close();
            return true;
        }
        db.close();

        return false;
    }


    public void favoritesNewPlaceItem (Place place) { //******** add a new place to favorites

        SQLiteDatabase db = getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        ContentValues valuesPlace = new ContentValues();

       // adding place into Places Table

        valuesPlace.put(FAV_PLACES_COL_ID,place.getId());
        valuesPlace.put(FAV_PLACES_COL_ID_PLACE,place.getPlace_id());
        valuesPlace.put(FAV_PLACES_COL_PHOTO_REF,place.getPhoto_reference());
        valuesPlace.put(FAV_PLACES_COL_NAME,place.getName());
        valuesPlace.put(FAV_PLACES_COL_ADDRESS,place.getAddress());
        valuesPlace.put(FAV_PLACES_COL_LAT,place.getLat());
        valuesPlace.put(FAV_PLACES_COL_LNG,place.getLng());
        valuesPlace.put(FAV_PLACES_COL_ICON_URL,place.getIcon_url());
        valuesPlace.put(FAV_PLACES_COL_RATING,place.getRating());

        if (place.getType()!=null) {
            for (String type : place.getType()) {

                builder.append(type + ";");

            }
        }


        valuesPlace.put(FAV_PLACES_COL_TYPE,builder.toString());
        valuesPlace.put(FAV_PLACES_COL_PIC_URL,place.getPic_url());

        // favorites don't show if the place is open now

        db.insert(PLACES_FAV_TABLE_NAME,null,valuesPlace);

        db.close();


    }

    public void newPlaceItem (Place place) { //******** add a new place to current search

        SQLiteDatabase db = getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        ContentValues valuesPlace = new ContentValues();

        // adding place into Places Table

        valuesPlace.put(PLACES_COL_ID,place.getId());
        valuesPlace.put(PLACES_COL_ID_PLACE,place.getPlace_id());
        valuesPlace.put(PLACES_COL_PHOTO_REF,place.getPhoto_reference());
        valuesPlace.put(PLACES_COL_NAME,place.getName());
        valuesPlace.put(PLACES_COL_ADDRESS,place.getAddress());
        valuesPlace.put(PLACES_COL_LAT,place.getLat());
        valuesPlace.put(PLACES_COL_LNG,place.getLng());
        valuesPlace.put(PLACES_COL_ICON_URL,place.getIcon_url());
        valuesPlace.put(PLACES_COL_RATING,place.getRating());

        if (place.getType()!=null) {
            for (String type : place.getType()) {

                builder.append(type + ";");

            }
        }


        valuesPlace.put(PLACES_COL_TYPE,builder.toString());
        valuesPlace.put(PLACES_COL_PIC_URL,place.getPic_url());

        // favorites don't show if the place is open now



            valuesPlace.put(PLACES_COL_OPEN_NOW,place.isOpenNow());

            db.insert(PLACES_TABLE_NAME,null,valuesPlace);
       //     db.insert(HISTORY_PLACES_TABLE_NAME,null,valuesPlace);


        db.close();

    }

    public Place getPlace(String placeToSearchId) { //********** this Method gets a place ID and return a Place object if in DB

        SQLiteDatabase db = getReadableDatabase();
        Place newPlace=null;

        Cursor cursor = db.query(PLACES_TABLE_NAME,null,PLACES_COL_ID + " = ?",new String[] {placeToSearchId},null,null,null);

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID));
            String place_id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID_PLACE));
            String photo_reference = cursor.getString(cursor.getColumnIndex(PLACES_COL_PHOTO_REF));
            String name = cursor.getString(cursor.getColumnIndex(PLACES_COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PLACES_COL_ADDRESS));

            String icon_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_ICON_URL));
            double rating = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_RATING));

            // change the type stores as concencated string to Array list

            ArrayList<String> type = new ArrayList<>();

            String[] typeAsArray = (cursor.getString(cursor.getColumnIndex(PLACES_COL_TYPE))).split(";");
            for (String typeText : typeAsArray) {

                type.add(typeText);

            }

            String pic_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_PIC_URL));

            //  handle distance - calculated


            double currentLat = sp.getFloat("currentLat",0);
            double currentLng = sp.getFloat("currentLng",0);

            Location myLocation = new Location("");
            myLocation.setLatitude(currentLat);
            myLocation.setLongitude(currentLng);

            Location placeLocation = new Location("");

            double lat = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LNG));

            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);

            // the distance will be shown in KM

            float distanceFromCurrentLocation = placeLocation.distanceTo(myLocation)/1000;

            boolean openNow = false;

            newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,type,pic_url,distanceFromCurrentLocation,openNow);

        }

        db.close();


        return newPlace;
    }
    //********** this Method checks if the place is already in the all item TABLE not to have a unique constraint exception

    public boolean checkIfPlaceIsInAllItemsDB (String id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(ALL_PLACES_TABLE_NAME,null,PLACES_COL_ID + " = ?",new String[] {id},null,null,null);

        if (cursor.moveToNext()) {

            db.close();
            return true;
        }
        db.close();

        return false;

    }
    //********** this Method return the all place around me as a list to present in the MAP
    public void allTableNewPlaceItem(Place place ) {


        if (checkIfPlaceIsInAllItemsDB(place.getId())) return;

        SQLiteDatabase db = getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        ContentValues valuesPlace = new ContentValues();

        // adding place into Places Table

        valuesPlace.put(ALL_PLACES_COL_ID,place.getId());
        valuesPlace.put(ALL_PLACES_COL_ID_PLACE,place.getPlace_id());
        valuesPlace.put(ALL_PLACES_COL_PHOTO_REF,place.getPhoto_reference());
        valuesPlace.put(ALL_PLACES_COL_NAME,place.getName());
        valuesPlace.put(ALL_PLACES_COL_ADDRESS,place.getAddress());
        valuesPlace.put(ALL_PLACES_COL_LAT,place.getLat());
        valuesPlace.put(ALL_PLACES_COL_LNG,place.getLng());
        valuesPlace.put(ALL_PLACES_COL_ICON_URL,place.getIcon_url());
        valuesPlace.put(ALL_PLACES_COL_RATING,place.getRating());

        if (place.getType()!=null) {
            for (String type : place.getType()) {

                builder.append(type + ";");

            }
        }


        valuesPlace.put(ALL_PLACES_COL_TYPE,builder.toString());
        valuesPlace.put(ALL_PLACES_COL_PIC_URL,place.getPic_url());

        // favorites don't show if the place is open now


        db.insert(ALL_PLACES_TABLE_NAME,null,valuesPlace);



        db.close();

    }

    //********** this Method insert a new Place Details item the the DETAILS TABLE
    public void newPlaceDetailsItem (PlaceDetails placeDetails) {

        if (placeDetailInDB(placeDetails.getPlace_id())!=null) return; // check if already in DB to avoid errors

        SQLiteDatabase db = getWritableDatabase();

        StringBuilder builder = new StringBuilder();

        ContentValues valuesPlaceDetails = new ContentValues();

        // adding place details into Places Table

        valuesPlaceDetails.put(DETAILS_COL_ID,placeDetails.getPlace_id());
        valuesPlaceDetails.put(DETAILS_COL_RATING,placeDetails.getRating());
        valuesPlaceDetails.put(DETAILS_COL_FORMATTED_ADDRESS,placeDetails.getFormattedAddress());
        valuesPlaceDetails.put(DETAILS_COL_FORMATTED_PHONE,placeDetails.getFormattedPhoneNumber());
        valuesPlaceDetails.put(DETAILS_COL_WEEKDAY_TEXT,placeDetails.getWeekdayText());
        valuesPlaceDetails.put(DETAILS_COL_WEB_SITE,placeDetails.getWebSite());
        valuesPlaceDetails.put(DETAILS_COL_PLACE_URL,placeDetails.getPlaceUrl());

        if (placeDetails.getPhotos_reference()!=null) {
            for (String url : placeDetails.getPhotos_reference()) {

                builder.append(url + "\n");

            }
        }

        valuesPlaceDetails.put(DETAILS_COL_PHOTOS_URL,builder.toString());



        db.insert(DETAILS_TABLE_NAME,null,valuesPlaceDetails);


        db.close();

    }

    // check if the PLace Details data is already in the DB to avoid unique constraint exception
    public PlaceDetails placeDetailInDB(String id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(DETAILS_TABLE_NAME,null,DETAILS_COL_ID + " = ?",new String[] {id},null,null,null);

        if (cursor.moveToFirst()) {

            String place_id = cursor.getString(cursor.getColumnIndex(DETAILS_COL_ID));
            double rating = cursor.getDouble(cursor.getColumnIndex(DETAILS_COL_RATING));
            String formatted_address = cursor.getString(cursor.getColumnIndex(DETAILS_COL_FORMATTED_ADDRESS));
            String formattedPhoneNumber = cursor.getString(cursor.getColumnIndex(DETAILS_COL_FORMATTED_PHONE));
            String openingHours = cursor.getString(cursor.getColumnIndex(DETAILS_COL_WEEKDAY_TEXT));
            String website = cursor.getString(cursor.getColumnIndex(DETAILS_COL_WEB_SITE));
            String webUrl = cursor.getString(cursor.getColumnIndex(DETAILS_COL_PLACE_URL));


            PlaceDetails newPlaceDetails = new PlaceDetails(place_id,rating,formatted_address,formattedPhoneNumber,openingHours,website,webUrl,new ArrayList<String>());

            return newPlaceDetails;

        }

        db.close();

        return null;
    }


    // will be used by the search query Adapter to get all results and present on recycler view sorted by rating
    public ArrayList<Place> showPlacesResultsFavorites () {

        ArrayList<Place> places = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(PLACES_FAV_TABLE_NAME,null,null,null,null,null,null);


        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_ID));
            String place_id = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_ID_PLACE));
            String photo_reference = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_PHOTO_REF));
            String name = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_ADDRESS));

            String icon_url = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_ICON_URL));
            double rating = cursor.getDouble(cursor.getColumnIndex(FAV_PLACES_COL_RATING));

            // change the type stores as concencated string to Array list

            ArrayList<String> type = new ArrayList<>();

            String[] typeAsArray = (cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_TYPE))).split(";");
            for (String typeText : typeAsArray) {

                type.add(typeText);

            }

            String pic_url = cursor.getString(cursor.getColumnIndex(FAV_PLACES_COL_PIC_URL));

            //  handle distance - calculated


            double currentLat = sp.getFloat("currentLat",0);
            double currentLng = sp.getFloat("currentLng",0);

            Location myLocation = new Location("");
            myLocation.setLatitude(currentLat);
            myLocation.setLongitude(currentLng);

            Location placeLocation = new Location("");

            double lat = cursor.getDouble(cursor.getColumnIndex(FAV_PLACES_COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(FAV_PLACES_COL_LNG));

            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);

            // the distance will be shown in KM

            float distanceFromCurrentLocation = placeLocation.distanceTo(myLocation)/1000;

            boolean openNow = false;

            Place newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,type,pic_url,distanceFromCurrentLocation,openNow);
            places.add(newPlace);

        }

        db.close();


        return places;
    }


    // will be used by the search query Adapter to get all results and present on recycler view sorted by rating
    public ArrayList<Place> showPlacesResultsAll () {

        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor=null;

        SQLiteDatabase db = getReadableDatabase();

       cursor = db.query(ALL_PLACES_TABLE_NAME,null,null,null,null,null,PLACES_COL_RATING);

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID));
            String place_id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID_PLACE));
            String photo_reference = cursor.getString(cursor.getColumnIndex(PLACES_COL_PHOTO_REF));
            String name = cursor.getString(cursor.getColumnIndex(PLACES_COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PLACES_COL_ADDRESS));

            String icon_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_ICON_URL));
            double rating = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_RATING));

            // change the type stores as concencated string to Array list

            ArrayList<String> type = new ArrayList<>();

            String[] typeAsArray = (cursor.getString(cursor.getColumnIndex(PLACES_COL_TYPE))).split(";");
            for (String typeText : typeAsArray) {

                type.add(typeText);

            }

            String pic_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_PIC_URL));

            //  handle distance - calculated


            double currentLat = sp.getFloat("currentLat",0);
            double currentLng = sp.getFloat("currentLng",0);

            Location myLocation = new Location("");
            myLocation.setLatitude(currentLat);
            myLocation.setLongitude(currentLng);

            Location placeLocation = new Location("");

            double lat = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LNG));

            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);

            // the distance will be shown in KM

            float distanceFromCurrentLocation = placeLocation.distanceTo(myLocation)/1000;

            boolean openNow = false;

            Place newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,type,pic_url,distanceFromCurrentLocation,openNow);
            places.add(newPlace);

        }

        db.close();


        return places;
    }

    // will be used by the search query Adapter to get all results and present on recycler view sorted by rating
    public ArrayList<Place> showPlacesResults () {

        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor=null;

        SQLiteDatabase db = getReadableDatabase();

        cursor = db.query(PLACES_TABLE_NAME,null,null,null,null,null,PLACES_COL_RATING + " DESC");

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID));
            String place_id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID_PLACE));
            String photo_reference = cursor.getString(cursor.getColumnIndex(PLACES_COL_PHOTO_REF));
            String name = cursor.getString(cursor.getColumnIndex(PLACES_COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PLACES_COL_ADDRESS));

            String icon_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_ICON_URL));
            double rating = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_RATING));

            // change the type stores as concencated string to Array list

            ArrayList<String> type = new ArrayList<>();

            String[] typeAsArray = (cursor.getString(cursor.getColumnIndex(PLACES_COL_TYPE))).split(";");
            for (String typeText : typeAsArray) {

                type.add(typeText);

            }

            String pic_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_PIC_URL));

            //  handle distance - calculated


            double currentLat = sp.getFloat("currentLat",0);
            double currentLng = sp.getFloat("currentLng",0);

            Location myLocation = new Location("");
            myLocation.setLatitude(currentLat);
            myLocation.setLongitude(currentLng);

            Location placeLocation = new Location("");

            double lat = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LNG));

            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);

            // the distance will be shown in KM

            float distanceFromCurrentLocation = placeLocation.distanceTo(myLocation)/1000;

            boolean openNow = false;


                int openNowAsInt = cursor.getInt(cursor.getColumnIndex(PLACES_COL_OPEN_NOW));

                if (openNowAsInt==1) openNow = true;


            Place newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,type,pic_url,distanceFromCurrentLocation,openNow);
            places.add(newPlace);

        }

        db.close();


        return places;
    }

    // will be used by the search query Adapter to get all results that are currently open and present on recycler view sorted by rating
    public ArrayList<Place> showOpenPlacesResults () {

        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor=null;

        SQLiteDatabase db = getReadableDatabase();

        cursor = db.query(PLACES_TABLE_NAME,null,PLACES_COL_OPEN_NOW + " = ?",new String[] {"1"},null,null,PLACES_COL_RATING + " DESC");

        while (cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID));
            String place_id = cursor.getString(cursor.getColumnIndex(PLACES_COL_ID_PLACE));
            String photo_reference = cursor.getString(cursor.getColumnIndex(PLACES_COL_PHOTO_REF));
            String name = cursor.getString(cursor.getColumnIndex(PLACES_COL_NAME));
            String address = cursor.getString(cursor.getColumnIndex(PLACES_COL_ADDRESS));

            String icon_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_ICON_URL));
            double rating = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_RATING));

            // change the type stores as concencated string to Array list

            ArrayList<String> type = new ArrayList<>();

            String[] typeAsArray = (cursor.getString(cursor.getColumnIndex(PLACES_COL_TYPE))).split(";");
            for (String typeText : typeAsArray) {

                type.add(typeText);

            }

            String pic_url = cursor.getString(cursor.getColumnIndex(PLACES_COL_PIC_URL));

            //  handle distance - calculated


            double currentLat = sp.getFloat("currentLat",0);
            double currentLng = sp.getFloat("currentLng",0);

            Location myLocation = new Location("");
            myLocation.setLatitude(currentLat);
            myLocation.setLongitude(currentLng);

            Location placeLocation = new Location("");

            double lat = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(PLACES_COL_LNG));

            placeLocation.setLatitude(lat);
            placeLocation.setLongitude(lng);

            // the distance will be shown in KM

            float distanceFromCurrentLocation = placeLocation.distanceTo(myLocation)/1000;

            boolean openNow = false;


            int openNowAsInt = cursor.getInt(cursor.getColumnIndex(PLACES_COL_OPEN_NOW));

            if (openNowAsInt==1) openNow = true;


            Place newPlace = new Place(id,place_id,photo_reference,name,address,lat,lng,icon_url,rating,type,pic_url,distanceFromCurrentLocation,openNow);
            places.add(newPlace);

        }

        db.close();


        return places;
    }

    public void deleteSearchTables() {


        SQLiteDatabase db = getWritableDatabase();

        db.delete(PLACES_TABLE_NAME,null,null);

        db.close();


    }

    public void deleteSearchAllTables() {


        SQLiteDatabase db = getWritableDatabase();

        db.delete(ALL_PLACES_TABLE_NAME,null,null);

        db.close();


    }

    public void deleteFavTable() {

        SQLiteDatabase db = getWritableDatabase();

        db.delete(PLACES_FAV_TABLE_NAME,null,null);

        db.close();


    }
// delete a specific place from the favorite list
    public int deleteFromFav (Place place, String id) {

        SQLiteDatabase db = getWritableDatabase();
        int numOfItemsDeleted = 0;

        String place_ID = place.getPlace_id();

        // deleting row from PLACES FAV TABLE
        numOfItemsDeleted+=db.delete(PLACES_FAV_TABLE_NAME,PLACES_COL_ID + " = ?",new String[] {id});


        db.close();

        return numOfItemsDeleted;
    }
// return a place details based on place_id
    public ArrayList<PlaceDetails> showPlaceDetails(String place_id) {

        ArrayList<PlaceDetails> details = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(DETAILS_TABLE_NAME,null,DETAILS_COL_ID + " = ? ",new String[] {place_id},null,null,null);

        while (cursor.moveToNext()) {


            String id = cursor.getString(cursor.getColumnIndex(DETAILS_COL_ID));
            double rating = cursor.getDouble(cursor.getColumnIndex(DETAILS_COL_RATING));
            String formatted_address = cursor.getString(cursor.getColumnIndex(DETAILS_COL_FORMATTED_ADDRESS));
            String formatted_phone_number = cursor.getString(cursor.getColumnIndex(DETAILS_COL_FORMATTED_PHONE));
            String weekday_text = cursor.getString(cursor.getColumnIndex(DETAILS_COL_WEEKDAY_TEXT));
            String web_site = cursor.getString(cursor.getColumnIndex(DETAILS_COL_WEB_SITE));
            String place_url = cursor.getString(cursor.getColumnIndex(DETAILS_COL_PLACE_URL));

            PlaceDetails newDetails = new PlaceDetails(id,rating,formatted_address,formatted_phone_number,weekday_text,web_site,place_url,new ArrayList<String>());
            details.add(newDetails);

        }

        db.close();



        return details;
    }

   // check if place was searched in the past ******* not in use
    public boolean placeIsInHistory(String id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(HISTORY_PLACES_TABLE_NAME,null,PLACES_COL_ID + " =?",new String[] {id},null,null,null);

        if (cursor.moveToNext()) {

            db.close();
            return true;
        }
        db.close();

        return false;
    }


}
