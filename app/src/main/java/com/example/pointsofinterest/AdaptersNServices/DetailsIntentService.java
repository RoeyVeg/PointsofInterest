package com.example.pointsofinterest.AdaptersNServices;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.pointsofinterest.AdaptersNServices.SearchRecyclerViewAdapter.PLACE_ID;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DetailsIntentService extends IntentService {

    public static final String ACTION_PLACE_DETAILS = "place_details_broadcast";
    public static final String RESPONSE_DETAILS = "responseDetails";


    public DetailsIntentService() {
        super("DetailsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //********** Intent service to maps-api nearby search with no place_id of a specific place to get additional data which will be presented in placeDetails fragment


        String place_id = intent.getStringExtra(PLACE_ID);
        String urlPlaceDetails = String.format("https://maps.googleapis.com/maps/api/place/details/json?placeid=%s&key=%s",place_id,KEY);


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlPlaceDetails)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // broadcast

            Intent broadcastIntent = new Intent(ACTION_PLACE_DETAILS);
            broadcastIntent.putExtra(RESPONSE_DETAILS,response.body().string());
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
