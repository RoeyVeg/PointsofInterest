package com.example.pointsofinterest.AdaptersNServices;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AroundUserIntentService extends IntentService {


    //********** Intent service to maps-api nearby search with no Key word to get all places around user location with defined radius which will be presented in the whats around me Map

    public static final String ACTION_SEARCH_ALL_PLACES = "search_all_placess_broadcast";
    public static final String RESPONSE_ALL_PLACES = "responseAllPlaces";

    public AroundUserIntentService() {
        super("AroundUserIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String locationCoordinates = intent.getFloatExtra(USER_LAT,0)+","+intent.getFloatExtra(USER_LNG,0);
        String radius = intent.getStringExtra("searchRadius");
        String urlPlace = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&key=%s",locationCoordinates,radius,KEY);


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlPlace)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // broadcast

            Intent broadcastIntent = new Intent(ACTION_SEARCH_ALL_PLACES);
            broadcastIntent.putExtra(RESPONSE_ALL_PLACES,response.body().string());
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
