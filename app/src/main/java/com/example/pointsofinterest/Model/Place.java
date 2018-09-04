package com.example.pointsofinterest.Model;

import java.io.Serializable;
import java.util.ArrayList;

//*********** this is a place Class to manage saving to the DB and getting the data from the DB and show it in the view, the id is the api id

public class Place implements Serializable {

    private String id;
    private String place_id;
    private String photo_reference;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String icon_url;
    private double rating;
    private ArrayList<String> type;
    private String pic_url;
    private float distanceFromCurrentLocation;
    private boolean openNow;


    // new place to show in search
    public Place(String id, String place_id, String photo_reference, String name, String address, double lat, double lng, String icon_url, double rating, ArrayList<String> type, String pic_url, float distanceFromCurrentLocation, boolean openNow) {
        this.id = id;
        this.place_id = place_id;
        this.photo_reference = photo_reference;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.icon_url = icon_url;
        this.rating = rating;
        this.type = type;
        this.pic_url = pic_url;
        this.distanceFromCurrentLocation = distanceFromCurrentLocation;
        this.openNow = openNow;
    }

    public Place(String id, String place_id, String name, String address, double lat, double lng) {
        this.id = id;
        this.place_id = place_id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public double getRating() {
        return rating;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public String getPic_url() {
        return pic_url;
    }

    public float getDistanceFromCurrentLocation() {
        return distanceFromCurrentLocation;
    }


    public boolean isOpenNow() {
        return openNow;
    }

    public String isOpenNowToString() {

        if (openNow) return "Open now";
        return "Closed at this time";
    }
}
