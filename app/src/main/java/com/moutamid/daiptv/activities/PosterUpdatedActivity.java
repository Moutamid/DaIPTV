package com.moutamid.daiptv.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PosterUpdatedActivity extends AppCompatActivity {
    public RequestQueue requestQueue;
    public int totalItems = 0;
    public int completedItems = 0;
    AppDatabase database;
    private static final String TAG = "PosterUpdatedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_poster_updated);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        database = AppDatabase.getInstance(this);
        new Thread(() -> {

            List<String> includedTypes = Arrays.asList(Constants.TYPE_MOVIE, Constants.TYPE_SERIES);
            List<String> excludedType = Collections.singletonList(Constants.TYPE_CHANNEL);
            List<ChannelsModel> channels = database.channelsDAO().getAllFiltered(includedTypes, excludedType);
            Log.d(TAG, "channels: " + channels.size());

            totalItems = 100; // channels.size()
            int c = 0;
            for (ChannelsModel item : channels) {
                if (c == 100)
                    break;
                if (!item.channelName.startsWith("|XXX|") || !item.channelName.startsWith("|xxx|") || !item.channelName.startsWith("|XxX|"))
                    makeApiCall(item);
                c++;
            }

        }).start();

    }

    private void makeApiCall(ChannelsModel item) {
        String name = Constants.regexName(item.getChannelName());
        String type = item.type.equals(Constants.TYPE_SERIES) ? Constants.TYPE_TV : Constants.TYPE_MOVIE;
        String url = Constants.getMovieData(name, type, type);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "makeApiCall:  " + response.toString());
                        JSONArray array = response.getJSONArray("results");
                        JSONObject object = array.getJSONObject(0);
                        int id = object.getInt("id");
                        getDetails(id, item);
                    } catch (JSONException e) {
                        Log.d(TAG, "makeApiCall: Error ");
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
        });
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id, ChannelsModel item) {
        String type = item.type.equals(Constants.TYPE_SERIES) ? Constants.TYPE_TV : Constants.TYPE_MOVIE;
        String url = Constants.getMovieDetails(id, type, "");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray images = response.getJSONObject("images").getJSONArray("posters");
                        String lang = "NULL";
                        int index = 0;

                        for (int i = 0; i < images.length(); i++) {
                            JSONObject object = images.getJSONObject(i);
                            lang = object.getString("iso_639_1");

                            if (lang.equals("fr")) {
                                Log.d(TAG, "getDetails: FR");
                                index = i;
                                break;
                            } else if (lang.equals("en") && index == 0) {
                                Log.d(TAG, "getDetails: ENG");
                                index = i;
                            } else if (lang.equals("null") && index == 0) {
                                Log.d(TAG, "getDetails: NULL");
                                index = i;
                            }
                        }

                        String poster = images.getJSONObject(index).getString("file_path");
                        String link = poster.isEmpty() ? item.getChannelImg() : poster;
                        database.channelsDAO().update(item.getID(), link);

                        completedItems++;
                        Log.d(TAG, "completedItems: " + completedItems);
                        if (completedItems == totalItems) {
                            // Execute the Runnable when all items are processed
                            Stash.put(Constants.IS_POSTER_UPDATED, true);
                            startActivity(new Intent(PosterUpdatedActivity.this, MainActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        requestQueue.add(objectRequest);
    }
}