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
    ChannelViewModel itemViewModel;
    ParentAdapter parentAdapter;
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
      //  getTopFilms();
    }
    ArrayList<MovieModel> films;
    ArrayList<TopItems> list;

    List<MoviesGroupModel> items = new ArrayList<>();
    ArrayList<ParentItemModel> parent = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFilmBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(mContext);

        list = new ArrayList<>();
        films = new ArrayList<>();

        database = AppDatabase.getInstance(mContext);

        itemViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(mContext));

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
//        adapter = new HomeParentAdapter(mContext, list, selected);

        items = database.moviesGroupDAO().getAll();
        Log.d(TAG, "onCreateView: " + items.size());
        parent.add(new ParentItemModel("Top Films", false));
        for (MoviesGroupModel model : items){
            String group = model.getChannelGroup();
            parent.add(new ParentItemModel(group, true));
        }
        parentAdapter = new ParentAdapter(mContext, parent, Constants.TYPE_MOVIE, itemViewModel, getViewLifecycleOwner(), new ItemSelected() {
            @Override
            public void selected(ChannelsModel model) {
                randomChannel = model;
                fetchID();
            }
        });
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recycler);
        binding.recycler.setAdapter(parentAdapter);

        return binding.getRoot();
    }

    public void refreshList(){
        Collections.shuffle(films);
        adapter.notifyDataSetChanged();
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
                        movieModel.tagline = response.getString("tagline");
                        movieModel.vote_average = String.valueOf(response.getDouble("vote_average"));
                        movieModel.genres = response.getJSONArray("genres").getJSONObject(0).getString("name");

                        JSONArray videos = response.getJSONObject("videos").getJSONArray("results");
                        JSONArray images = response.getJSONObject("images").getJSONArray("backdrops");
                        JSONArray credits = response.getJSONObject("credits").getJSONArray("cast");

                        int index = 0;
                        if  (images.length() > 1) {
                            String lang = "NULL";
                            for (int i = 0; i < images.length(); i++) {
                                JSONObject object = images.getJSONObject(i);
                                lang = object.getString("iso_639_1");
                                if (lang == null || lang.isEmpty()) {
                                    index = i;
                                    lang = "FILL";
                                    break;
                                } else {
                                    lang = "NULL";
                                }
                            }
                            if (index == 0 && lang.equals("NULL")){
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject object = images.getJSONObject(i);
                                    lang = object.getString("iso_639_1");
                                    if (lang.equals("fr")) {
                                        index = i;
                                        lang = "FILL";
                                        break;
                                    } else {
                                        lang = "NULL";
                                    }
                                }
                            }
                            if (index == 0 && lang.equals("NULL")){
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject object = images.getJSONObject(i);
                                    lang = object.getString("iso_639_1");
                                    if (lang.equals("en")) {
                                        index = i;
                                        break;
                                    }
                                }
                            }
                            movieModel.banner = images.getJSONObject(index).getString("file_path");
                        }
                        int logoIndex = 0;
                        JSONArray logos = response.getJSONObject("images").getJSONArray("logos");
                        if (logos.length() > 1) {
                            String lang = "";
                            for (int i = 0; i < logos.length(); i++) {
                                JSONObject object = logos.getJSONObject(i);
                                lang = object.getString("iso_639_1");
                                if (lang.equals("fr")) {
                                    logoIndex = i;
                                    break;
                                } else {
                                    lang = "";
                                }
                            }
                            if (logoIndex == 0 && lang.isEmpty()) {
                                for (int i = 0; i < logos.length(); i++) {
                                    JSONObject object = logos.getJSONObject(i);
                                    lang = object.getString("iso_639_1");
                                    if (lang.equals("en")) {
                                        logoIndex = i;
                                        break;
                                    }
                                }
                            }
                            String path = logos.getJSONObject(logoIndex).getString("file_path");
                            Log.d(TAG, "getlogo: " + path);
                            try {
                                Glide.with(mContext).load(Constants.getImageLink(path)).placeholder(R.color.transparent).into(binding.logo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Glide.with(mContext).load(R.color.transparent).placeholder(R.color.transparent).into(binding.logo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
       // String name = movieModel.tagline.isEmpty() ? movieModel.original_title : movieModel.tagline;
        binding.name.setText(movieModel.original_title);
        binding.desc.setText(movieModel.tagline);
        double d = Double.parseDouble(movieModel.vote_average);
        binding.tmdbRating.setText(String.format("%.1f", d));
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
        Glide.with(mContext).load(Constants.getImageLink(movieModel.banner)).into(binding.banner);
        try {
            if (!movieModel.tagline.isEmpty()) {
                TranslateAPI desc = new TranslateAPI(
                        Language.AUTO_DETECT,   //Source Language
                        Language.FRENCH,         //Target Language
                        movieModel.tagline);
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
            }


            TranslateAPI type = new TranslateAPI(
                    Language.AUTO_DETECT,   //Source Language
                    Language.FRENCH,         //Target Language
                    movieModel.genres);           //Query Text
            TranslateAPI tagline = new TranslateAPI(
                    Language.AUTO_DETECT,   //Source Language
                    Language.FRENCH,         //Target Language
                    movieModel.original_title);           //Query Text

            tagline.setTranslateListener(new TranslateAPI.TranslateListener() {
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
        } catch (ClassCastException e){
            e.printStackTrace();
        }

    }

}