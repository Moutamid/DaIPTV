package com.moutamid.daiptv.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.EpisodesAdapter;
import com.moutamid.daiptv.adapters.SeasonsAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.ActivitySeriesBinding;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.EpisodesModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.SeasonsItem;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeriesActivity extends AppCompatActivity {
    ActivitySeriesBinding binding;
    ChannelsModel model;
    String output;
    private static final String TAG = "SeriesActivity";
    AppDatabase database;
    Dialog dialog;
    RequestQueue requestQueue;
    int id;
    String searchQuery;

    private void initializeDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        //dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = AppDatabase.getInstance(this);

        model = (ChannelsModel) Stash.getObject(Constants.PASS, ChannelsModel.class);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        binding.seasonsRC.setLayoutManager(new LinearLayoutManager(this));
        binding.seasonsRC.setHasFixedSize(false);

        binding.episodeRC.setLayoutManager(new LinearLayoutManager(this));
        binding.episodeRC.setHasFixedSize(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeDialog();
        output = Constants.regexName(model.getChannelName());
        Log.d(TAG, "onResume: " + model.getChannelName());
        Log.d(TAG, "onResume: " + output);
        if (!output.isEmpty()) {
            getList();
        }
    }

    private void getSeasonEpisodes() {
        dialog.show();
        fetchID(output);
    }

    private void fetchID(String output) {
        String url;
        url = Constants.getMovieData(output, Constants.TYPE_TV);

        Log.d(TAG, "fetchID: URL  " + url);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        JSONObject object = array.getJSONObject(0);
                        id = object.getInt("id");
                        getDetails(id, 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error: " + e.getMessage());
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            Log.d(TAG, "Error: " + error.getMessage());
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id, int count) {
        String url = Constants.getEpisodeDetails(id, count);
        ArrayList<EpisodesModel> episodesModelArrayList = new ArrayList<>();
        Log.d(TAG, "fetchID: ID  " + id);
        Log.d(TAG, "fetchID: URL  " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    dialog.dismiss();
                    try {
                        JSONArray episodes = response.getJSONArray("episodes");
                        for (int i = 0; i < episodes.length(); i++) {
                            JSONObject object = episodes.getJSONObject(i);
                            int season_number = object.getInt("season_number");
                            int episode_number = object.getInt("episode_number");
                            String name = object.getString("name");
                            String overview = object.getString("overview");
                            String still_path = object.getString("still_path");
                            String se = String.format("S%02d E%02d", season_number, episode_number);
                            EpisodesModel episodesModel = new EpisodesModel(se, name, overview, still_path);
                            episodesModelArrayList.add(episodesModel);
                        }
                        Log.d(TAG, "getDetails: " + episodesModelArrayList.size());
                        EpisodesAdapter episodesAdapter = new EpisodesAdapter(SeriesActivity.this, episodesModelArrayList, episodeModel -> {
                            Log.d(TAG, "Episode Clicked: ");
                            searchQuery = Constants.queryName(model.getChannelName());
                            searchQuery += " " + episodeModel.se;
                            Log.d(TAG, "searchQuery: " + searchQuery);
                            ChannelsModel channelsModel = database.channelsDAO().getSearchChannel(searchQuery);
                            try {
                                Log.d(TAG, "name: " + channelsModel.channelName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(SeriesActivity.this, VideoPlayerActivity.class).putExtra("url", channelsModel.getChannelUrl()).putExtra("name", channelsModel.getChannelName()));
                        });
                        binding.episodeRC.setAdapter(episodesAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                }, error -> {
            Log.e(TAG, "error: " + error.getMessage());
            error.printStackTrace();
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private void getList() {
        // dialog.show();
        List<ChannelsModel> list = database.channelsDAO().getSeasons(output);
        Log.d(TAG, "getList: " + list.size());
        Map<String, Integer> seasonCountMap = new HashMap<>();

        for (ChannelsModel channel : list) {
            String channelName = channel.getChannelName();
            String season = extractSeasonFromChannelName(channelName.trim());
            if (!season.isEmpty())
                seasonCountMap.put(season, seasonCountMap.getOrDefault(season, 0) + 1);
        }

        ArrayList<SeasonsItem> seasonSummaries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : seasonCountMap.entrySet()) {
            SeasonsItem summary = new SeasonsItem(entry.getKey(), entry.getValue());
            seasonSummaries.add(summary);
        }

        seasonSummaries.sort(Comparator.comparing(seasonsItem -> seasonsItem.season));

        SeasonsAdapter seasonsAdapter = new SeasonsAdapter(this, seasonSummaries, pos -> {
            getDetails(id, pos + 1);
        });
        binding.seasonsRC.setAdapter(seasonsAdapter);
        getSeasonEpisodes();
    }

    private static String extractSeasonFromChannelName(String channelName) {
        Log.d(TAG, "extractSeasonFromChannelName: " + channelName);
        try {
            int endIndex = channelName.lastIndexOf("E");
            Log.d(TAG, "endIndex: " + endIndex);
            int startIndex = channelName.lastIndexOf("S");
            Log.d(TAG, "startIndex: " + startIndex);
            return channelName.substring(startIndex, endIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}