package com.moutamid.daiptv.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.activities.DetailActivity;
import com.moutamid.daiptv.activities.VideoPlayerActivity;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentHomeBinding;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.CastModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.ParentItemModel;
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

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    AppDatabase database;
    List<MoviesGroupModel> items = new ArrayList<>();
    ArrayList<ParentItemModel> parent = new ArrayList<>();
    ChannelViewModel itemViewModel;
    ParentAdapter adapter;
    ChannelsModel randomChannel;
    Dialog dialog;
    MovieModel movieModel;
    private RequestQueue requestQueue;
    String[] type = {Constants.TYPE_MOVIE, Constants.TYPE_SERIES};
    String[] movieNames = {
            "Inception",
            "interstellar",
            "Nomadland"
    };
    public HomeFragment() {
        // Required empty public constructor
    }

    private void initializeDialog() {
        dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(requireContext());

        itemViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);

        items = database.moviesGroupDAO().getAll();

        initializeDialog();

        Random random = new Random();
        randomChannel = database.channelsDAO().getRand(Constants.TYPE_MOVIE);

        if (randomChannel == null){
            randomChannel = new ChannelsModel();
            randomChannel.setChannelName(movieNames[random.nextInt(movieNames.length)]);
            randomChannel.setChannelGroup(type[random.nextInt(type.length)]);
        }

        MainActivity mainActivity = (MainActivity) requireActivity();
        if (mainActivity != null) {
            binding.root.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        mainActivity.toolbar.setVisibility(View.GONE);
                    } else if (scrollY < oldScrollY) {
                        mainActivity.toolbar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        requestQueue = VolleySingleton.getInstance(requireContext()).getRequestQueue();

        for (MoviesGroupModel model : items){
            String group = model.getChannelGroup();
            parent.add(new ParentItemModel(group));
        }

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ParentAdapter(requireContext(), parent, Constants.TYPE_MOVIE, itemViewModel, getViewLifecycleOwner(), new ItemSelected() {
            @Override
            public void selected(ChannelsModel model) {
                randomChannel = model;
                fetchID();
            }
        });
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Home");
        new Handler().postDelayed(this::fetchID, 1000);
    }

    private void fetchID() {
        String name = randomChannel.getChannelName().replace("|FR| ", "");
        name = name.replaceAll("\\(\\d{4}\\)", "").trim();
        Log.d(TAG, "fetchID: " + name);
        String url;
        if (randomChannel.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieData(name, Constants.TYPE_TV);
        } else {
            url = Constants.getMovieData(name, Constants.TYPE_MOVIE);
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
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        });
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id) {
        String url;
        if (randomChannel.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieDetails(id, Constants.TYPE_TV);
        } else {
            url = Constants.getMovieDetails(id, Constants.TYPE_MOVIE);
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
                        int index = 0;
                        if (images.length() > 1){
                            index = r.nextInt(images.length());
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

    }
}