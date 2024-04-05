package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.CastsAdapter;
import com.moutamid.daiptv.databinding.ActivityDetailSeriesBinding;
import com.moutamid.daiptv.models.CastModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.UserModel;
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

public class DetailSeriesActivity extends AppCompatActivity {
    ActivityDetailSeriesBinding binding;
    ChannelsModel model;
    Dialog dialog;
    private RequestQueue requestQueue;
    MovieModel movieModel;
    private static final String TAG = "DetailSeriesActivity";
    ArrayList<CastModel> cast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailSeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = (ChannelsModel) Stash.getObject(Constants.PASS, ChannelsModel.class);

        cast = new ArrayList<>();

        binding.play.requestFocus();
        binding.back.setOnClickListener(v -> onBackPressed());

        initializeDialog();

        binding.reader.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getChannelUrl()));
            intent.setType("video/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Aucun lecteur externe trouvé", Toast.LENGTH_SHORT).show();
            }
        });

        binding.add.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle("Ajouter aux Favoris")
                    .setMessage("Souhaitez-vous ajouter cet article à votre liste de favoris ? Une fois ajouté, vous pourrez facilement y accéder plus tard.")
                    .setPositiveButton("Ajouter", (dialog, which) -> {
                        dialog.dismiss();
                        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                        ArrayList<ChannelsModel> list = Stash.getArrayList(userModel.id, ChannelsModel.class);
                        list.add(model);
                        Stash.put(userModel.id, list);
                    }).setNegativeButton("Fermer", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        requestQueue = VolleySingleton.getInstance(DetailSeriesActivity.this).getRequestQueue();

        if (model != null){
            fetchID();
        } else {
            Toast.makeText(this, "Chaîne introuvable", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.episodes.setOnClickListener(v -> startActivity(new Intent(this, SeriesActivity.class)));

    }

    private void fetchID() {
        String name = Constants.regexName(model.getChannelName());
        Log.d(TAG, "fetchID: " + name);
        String url = Constants.getMovieData(name, Constants.TYPE_TV);

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
        String url = Constants.getMovieDetails(id, Constants.TYPE_TV);
        Log.d(TAG, "fetchID: ID  " + id);
        Log.d(TAG, "fetchID: URL  " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        movieModel = new MovieModel();

                        try {
                            movieModel.original_title = response.getString("original_title");
                        } catch (Exception e){
                            movieModel.original_title = response.getString("original_name");
                        }
                        try {
                            movieModel.release_date = response.getString("release_date");
                        } catch (Exception e){
                            movieModel.release_date = response.getString("first_air_date");
                        }
                        movieModel.overview = response.getString("overview");
                        movieModel.vote_average = String.valueOf(response.getDouble("vote_average"));
                        movieModel.genres = response.getJSONArray("genres").getJSONObject(0).getString("name");

                        JSONArray videos = response.getJSONObject("videos").getJSONArray("results");
                        JSONArray images = response.getJSONObject("images").getJSONArray("backdrops");
                        JSONArray credits = response.getJSONObject("credits").getJSONArray("cast");

                        Random r = new Random();
                        movieModel.banner = "";
                        if (images.length() > 1){
                            int index = r.nextInt(images.length());
                            movieModel.banner = images.getJSONObject(index).getString("file_path");
                        }

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
        binding.tmdbRating.setText(movieModel.vote_average);
        binding.filmType.setText(movieModel.genres);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE);

        try {
            Date date = inputFormat.parse(movieModel.release_date);
            String formattedDate = outputFormat.format(date);
            String capitalized = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
            binding.date.setText(capitalized);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setUI: " + Constants.getImageLink(movieModel.banner));
        Glide.with(this).load(Constants.getImageLink(movieModel.banner)).into(binding.banner);

        binding.trailer.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieModel.trailer));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Aucun lecteur externe trouvé", Toast.LENGTH_SHORT).show();
            }
        });

        binding.play.setOnClickListener(v -> {
            startActivity(new Intent(this, VideoPlayerActivity.class).putExtra("url", model.getChannelUrl()).putExtra("name", movieModel.original_title));
        });

        CastsAdapter adapter = new CastsAdapter(this, cast);
        binding.castRC.setAdapter(adapter);

        TranslateAPI translateAPI = new TranslateAPI(
                Language.AUTO_DETECT,   //Source Language
                Language.FRENCH,         //Target Language
                movieModel.overview);           //Query Text

        translateAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                Log.d(TAG, "onSuccess: "+translatedText);
                binding.desc.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d(TAG, "onFailure: "+ErrorText);
            }
        });


        TranslateAPI nameAPI = new TranslateAPI(
                Language.AUTO_DETECT,   //Source Language
                Language.FRENCH,         //Target Language
                movieModel.original_title);           //Query Text

        nameAPI.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                Log.d(TAG, "onSuccess: "+translatedText);
                binding.name.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d(TAG, "onFailure: "+ErrorText);
            }
        });

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