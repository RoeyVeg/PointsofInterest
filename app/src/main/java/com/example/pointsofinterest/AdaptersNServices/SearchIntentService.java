package com.example.pointsofinterest.AdaptersNServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.pointsofinterest.Model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.pointsofinterest.Activities.MainActivity.KEY_WORD;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LAT;
import static com.example.pointsofinterest.Activities.MainActivity.USER_LNG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SearchIntentService extends IntentService {

    //********** Intent service to maps-api nearby search with a Key : word user entered or type search from the Main search screen to get all places around user location with defined radius


    public static final String ACTION_SEARCH_PLACES = "search_places_broadcast";


    public SearchIntentService() {
        super("SearchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {



        String key_word = intent.getStringExtra(KEY_WORD);
        String locationCoordinates = intent.getFloatExtra(USER_LAT,0)+","+intent.getFloatExtra(USER_LNG,0);
        String radius = intent.getStringExtra("searchRadius");
        String urlPlace = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s%s&key=%s",locationCoordinates,radius,key_word,KEY);


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlPlace)
                .build();

        try {
            Response response = client.newCall(request).execute();

            // broadcast

            Intent broadcastIntent = new Intent(ACTION_SEARCH_PLACES);
            broadcastIntent.putExtra("responsePlace",response.body().string());
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



}
