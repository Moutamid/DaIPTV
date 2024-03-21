package com.moutamid.daiptv.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.adapters.HomeParentAdapter;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentFilmBinding;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.models.TopItems;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.utilis.VolleySingleton;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilmFragment extends Fragment {
    private static final String TAG = "FilmFragment";
    FragmentFilmBinding binding;
    AppDatabase database;
    ChannelsModel randomChannel;
    Dialog dialog;
    MovieModel movieModel;
    private RequestQueue requestQueue;
    String[] movieNames = {
            "The Shawshank Redemption",
            "The Godfather",
            "12 Angry Men",
            "The Dark Knight",
            "Inception",
            "The Green Mile",
    };
    public FilmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Film");
        getTopFilms();
    }
    ArrayList<MovieModel> films;
    ArrayList<TopItems> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFilmBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(mContext);

        list = new ArrayList<>();
        films = new ArrayList<>();
        initializeDialog();

//        MainActivity mainActivity = (MainActivity) requireActivity();
//        if (mainActivity != null) {
//            binding.root.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    if (scrollY > oldScrollY) {
//                        mainActivity.toolbar.setVisibility(View.GONE);
//                    } else if (scrollY < oldScrollY) {
//                        mainActivity.toolbar.setVisibility(View.VISIBLE);
//                    }
//                }
//            });
//        }


        Random random = new Random();
        randomChannel = database.channelsDAO().getRand(Constants.TYPE_MOVIE);

        if (randomChannel == null){
            randomChannel = new ChannelsModel();
            randomChannel.setChannelName(movieNames[random.nextInt(movieNames.length)]);
            randomChannel.setChannelGroup(Constants.TYPE_MOVIE);
        }

        requestQueue = VolleySingleton.getInstance(mContext).getRequestQueue();

        new Handler().postDelayed(this::fetchID, 1500);


        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new HomeParentAdapter(mContext, list, selected);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recycler);
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }

    private void getTopFilms() {
        String url = Constants.topFILM;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        films.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            MovieModel model = new MovieModel();
                            try {
                                model.original_title = object.getString("original_title");
                            } catch (Exception e){
                                model.original_title = object.getString("original_name");
                            }
                            model.banner = object.getString("poster_path");
                            model.type = Constants.TYPE_MOVIE;
                            films.add(model);
                        }
                        list.add(new TopItems("Top Films", films));
                        adapter = new HomeParentAdapter(mContext, list, selected);
                        binding.recycler.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private void initializeDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    private void fetchID() {
        String name = Constants.regexName(randomChannel.getChannelName());
        Log.d(TAG, "fetchID: " + name);
        String url = Constants.getMovieData(name, Constants.TYPE_MOVIE);

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
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private Context mContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }
    HomeParentAdapter adapter;
    ItemSelected selected = new ItemSelected() {
        @Override
        public void selected(ChannelsModel model) {
            randomChannel = model;
            fetchID();
        }
    };
    private void getDetails(int id) {
        String url = Constants.getMovieDetails(id, Constants.TYPE_MOVIE);
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
                        int index = 0;
                        if (images.length() > 1){
                            index = r.nextInt(images.length());
                        }
                        int logoIndex;
                        JSONArray logos = response.getJSONObject("images").getJSONArray("logos");
                        if (logos.length() > 1) {
                            logoIndex = r.nextInt(logos.length());
                            String path = logos.getJSONObject(logoIndex).getString("file_path");
                            Log.d(TAG, "getlogo: " + path);
                            Glide.with(this).load(Constants.getImageLink(path)).into(binding.logo);
                        }


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

                        setUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private void setUI() {
        dialog.dismiss();
        binding.name.setText(movieModel.original_title);
        binding.desc.setText(movieModel.overview);
        double d = Double.parseDouble(movieModel.vote_average);
        binding.tmdbRating.setText(String.format("%.1f", d));
        binding.filmType.setText(movieModel.genres);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE);

        try {
            Date date = inputFormat.parse(movieModel.release_date);
            String formattedDate = outputFormat.format(date);
            binding.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setUI: " + Constants.getImageLink(movieModel.banner));
        Glide.with(mContext).load(Constants.getImageLink(movieModel.banner)).into(binding.banner);
        TranslateAPI desc = new TranslateAPI(
                Language.AUTO_DETECT,   //Source Language
                Language.FRENCH,         //Target Language
                movieModel.overview);

        TranslateAPI title = new TranslateAPI(
                Language.AUTO_DETECT,   //Source Language
                Language.FRENCH,         //Target Language
                movieModel.original_title);

        TranslateAPI type = new TranslateAPI(
                Language.AUTO_DETECT,   //Source Language
                Language.FRENCH,         //Target Language
                movieModel.genres);           //Query Text

        desc.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                Log.d(TAG, "onSuccess: " + translatedText);
                binding.desc.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d(TAG, "onFailure: " + ErrorText);
            }
        });
        title.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                Log.d(TAG, "onSuccess: " + translatedText);
                binding.name.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d(TAG, "onFailure: " + ErrorText);
            }
        });
        type.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                Log.d(TAG, "onSuccess: " + translatedText);
                binding.filmType.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d(TAG, "onFailure: " + ErrorText);
            }
        });

    }

}