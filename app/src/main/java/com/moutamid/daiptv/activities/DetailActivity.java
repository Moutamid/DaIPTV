package com.moutamid.daiptv.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.CastsAdapter;
import com.moutamid.daiptv.databinding.ActivityDetailBinding;
import com.moutamid.daiptv.models.CastModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    ActivityDetailBinding binding;
    ChannelsModel model;
    Dialog dialog;
    private RequestQueue requestQueue;
    MovieModel movieModel;

    ArrayList<CastModel> cast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = (ChannelsModel) Stash.getObject(Constants.PASS, ChannelsModel.class);

        cast = new ArrayList<>();

        binding.back.setOnClickListener(v -> onBackPressed());

        initializeDialog();

        requestQueue = VolleySingleton.getInstance(DetailActivity.this).getRequestQueue();

        fetchID();

    }

    private void fetchID() {
        String name = model.getChannelName().replace("|FR| ", "");
        name = name.replaceAll("\\(\\d{4}\\)", "").trim();
        Log.d(TAG, "fetchID: " + name);
        String url;
       // name = "Interstellar"; // for testing
        if (model.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieData(this, name, Constants.TYPE_TV);
        } else {
            url = Constants.getMovieData(this, name, Constants.TYPE_MOVIE);
        }

        Log.d(TAG, "fetchID: URL  " + url);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        JSONObject object = array.getJSONObject(0);
                        int id = object.getInt("id");
                        getDetails(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }, error -> {
            error.printStackTrace();
            runOnUiThread(() -> {
                dialog.dismiss();
            });
        });
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id) {
        String url;
        if (model.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieDetails(this, id, Constants.TYPE_TV);
        } else {
            url = Constants.getMovieDetails(this, id, Constants.TYPE_MOVIE);
        }
        Log.d(TAG, "fetchID: ID  " + id);
        Log.d(TAG, "fetchID: URL  " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        movieModel = new MovieModel();

                        movieModel.original_title = response.getString("original_title");
                        movieModel.release_date = response.getString("release_date");
                        movieModel.overview = response.getString("overview");
                        movieModel.vote_average = String.valueOf(response.getDouble("vote_average"));
                        movieModel.genres = response.getJSONArray("genres").getJSONObject(0).getString("name");

                        JSONArray videos = response.getJSONObject("videos").getJSONArray("results");
                        JSONArray images = response.getJSONObject("images").getJSONArray("backdrops");
                        JSONArray credits = response.getJSONObject("credits").getJSONArray("cast");

                        Random r = new Random();
                        int index = r.nextInt(images.length());

                        movieModel.banner = images.getJSONObject(index).getString("file_path");

                        for (int i = 0; i < videos.length(); i++) {
                            JSONObject object = videos.getJSONObject(i);
                            boolean official = object.getBoolean("official");
                            String type = object.getString("type");
                            if (type.equals("Trailer")) {
                                movieModel.trailer = "https://www.youtube.com/watch?v=" + object.getString("key");
                                break;
                            }
                        }

                        cast.clear();
                        for (int i = 0; i < credits.length(); i++) {
                            JSONObject object = credits.getJSONObject(i);
                            String name = object.getString("name");
                            String profile_path = object.getString("profile_path");
                            String character = object.getString("character");
                            cast.add(new CastModel(name, character, profile_path));
                        }

                        setUI();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                }, error -> {
            error.printStackTrace();
            runOnUiThread(() -> {
                dialog.dismiss();
            });
        });
        requestQueue.add(objectRequest);
    }

    private void setUI() {
        dialog.dismiss();
        binding.name.setText(movieModel.original_title);
        binding.desc.setText(movieModel.overview);
        binding.tmdbRating.setText("TMBD " + movieModel.vote_average);
        binding.filmType.setText(movieModel.genres);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());

        try {
            Date date = inputFormat.parse(movieModel.release_date);
            String formattedDate = outputFormat.format(date);
            binding.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setUI: " + Constants.getImageLink(movieModel.banner));
        Glide.with(this).load(Constants.getImageLink(movieModel.banner)).into(binding.banner);

        binding.trailer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieModel.trailer));
            startActivity(intent);
        });

        binding.play.setOnClickListener(v -> {
            startActivity(new Intent(this, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", movieModel.original_title));
        });

        CastsAdapter adapter = new CastsAdapter(this, cast);
        binding.castRC.setAdapter(adapter);
    }

    private void initializeDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }
}