package com.example.pointsofinterest.Model;

import java.io.Serializable;
import java.util.ArrayList;

//*********** this is a Place Details Class to manage saving to the DB and getting the data from the DB and show it in the view, the id is the api id

public class PlaceDetails implements Serializable {


    private String place_id;
    private String formattedAddress;
    private String formattedPhoneNumber;
    private String weekdayText;
    private ArrayList<String> photos_reference;
    private String webSite;
    private String placeUrl;
    private double rating;


    public PlaceDetails(String place_id, double rating,String formattedAddress, String formattedPhoneNumber, String weekdayText, String webSite, String placeUrl, ArrayList<String> photos_reference) {
        this.place_id = place_id;
        this.formattedAddress = formattedAddress;
        this.formattedPhoneNumber = formattedPhoneNumber;
        this.weekdayText = weekdayText;
        this.photos_reference = photos_reference;
        this.webSite = webSite;
        this.placeUrl = placeUrl;
        this.rating = rating;
    }


    public String getPlace_id() {
        return place_id;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getWeekdayText() {
        return weekdayText;
    }

    public ArrayList<String> getPhotos_reference() {
        return photos_reference;
    }

    public String getWebSite() {
        return webSite;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public double getRating() {
        return rating;
    }
}
