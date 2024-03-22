package com.moutamid.daiptv.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.HomeParentAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentHomeBinding;
import com.moutamid.daiptv.lisetenrs.ItemSelected;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.MovieModel;
import com.moutamid.daiptv.models.TopItems;
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

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    AppDatabase database;
    ChannelsModel randomChannel;
    Dialog dialog;
    MovieModel movieModel;
    ArrayList<MovieModel> films, series, latest, additions;
    ArrayList<ChannelsModel> filmsChan, seriesChan;
    ArrayList<TopItems> list;
    private RequestQueue requestQueue;
    String[] type = {Constants.TYPE_MOVIE, Constants.TYPE_SERIES};
    String[] movieNames = {
            "The Shawshank Redemption",
            "The Godfather",
            "12 Angry Men",
            "The Dark Knight",
            "Inception",
            "The Green Mile",
    };
    HomeParentAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    private void initializeDialog() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(mContext);

        list = new ArrayList<>();
        films = new ArrayList<>();
        series = new ArrayList<>();
        latest = new ArrayList<>();
        additions = new ArrayList<>();
        filmsChan = new ArrayList<>();
        seriesChan = new ArrayList<>();

        initializeDialog();

        Random random = new Random();
        randomChannel = database.channelsDAO().getRand(Constants.TYPE_MOVIE);

        if (randomChannel == null) {
            randomChannel = new ChannelsModel();
            randomChannel.setChannelName(movieNames[random.nextInt(movieNames.length)]);
            randomChannel.setChannelGroup(Constants.TYPE_MOVIE);
            new Handler().postDelayed(this::fetchID, 1000);
        }

        binding.recycler.setLayoutManager(new GridLayoutManager(mContext, 1));
        binding.recycler.setHasFixedSize(false);
        adapter = new HomeParentAdapter(mContext, list, selected);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recycler);
        binding.recycler.setAdapter(adapter);

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

        requestQueue = VolleySingleton.getInstance(mContext).getRequestQueue();


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Home");
        list.clear();
        Stash.clear(Constants.TOP_FILMS);
        Stash.clear(Constants.TOP_SERIES);
        getTopFilms();
    }

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        this.mContext = null;
//    }

    private void getTopFilms() {
        String url = Constants.topFILM;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        films.clear();
                        filmsChan.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            ChannelsModel channel = new ChannelsModel();
                            channel.setChannelName(object.getString("title"));
                            channel.setChannelImg(object.getString("poster_path"));
                            channel.setType(Constants.TYPE_MOVIE);
                            channel.setChannelGroup(Constants.TYPE_MOVIE);

                            MovieModel model = new MovieModel();
                            model.original_title = object.getString("title");
                            model.banner = object.getString("poster_path");
                            model.type = Constants.TYPE_MOVIE;

                            filmsChan.add(channel);
                            films.add(model);
                        }
                        list.add(new TopItems("Top Films", films));
                        Stash.put(Constants.TOP_FILMS, filmsChan);
                        adapter = new HomeParentAdapter(mContext, list, selected);
                        binding.recycler.setAdapter(adapter);
                        getSeries();
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

    private void getSeries() {
        Log.d(TAG, "getSeries: ");
        String url = Constants.topTV;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        series.clear();
                        seriesChan.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            MovieModel model = new MovieModel();
                            model.original_title = object.getString("name");
                            model.banner = object.getString("poster_path");
                            model.type = Constants.TYPE_SERIES;
                            series.add(model);


                            ChannelsModel channel = new ChannelsModel();
                            channel.setChannelName(object.getString("name"));
                            channel.setChannelImg(object.getString("poster_path"));
                            channel.setType(Constants.TYPE_SERIES);
                            channel.setChannelGroup(Constants.TYPE_SERIES);
                            seriesChan.add(channel);
                        }
                        list.add(new TopItems("Top Series", series));
                        Stash.put(Constants.TOP_SERIES, seriesChan);
                        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                        ArrayList<ChannelsModel> fvrt = Stash.getArrayList(userModel.id, ChannelsModel.class);
                        if (fvrt.size() > 0) {
                            ArrayList<MovieModel> fvrtList = new ArrayList<>();
                            for (ChannelsModel channelsModel : fvrt) {
                                MovieModel model = new MovieModel();
                                model.type = channelsModel.getType();
                                model.banner = channelsModel.getChannelImg();
                                model.original_title = channelsModel.getChannelName();
                                fvrtList.add(model);
                            }
                            list.add(new TopItems("Favourites", fvrtList));
                        }
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

    ItemSelected selected = new ItemSelected() {
        @Override
        public void selected(ChannelsModel model) {
            randomChannel = model;
            fetchID();
        }
    };

    private void fetchID() {
        String name = Constants.regexName(randomChannel.getChannelName());
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
                        if (array.length() >= 1) {
                            int id = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String original_language = object.getString("original_language");
                                if (original_language.equals("en")){
                                    id = object.getInt("id");
                                    break;
                                }
                            }
                            if (id == 0){
                                JSONObject object = array.getJSONObject(0);
                                id = object.getInt("id");
                            }
                            getDetails(id);
                        }
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
                        try {
                            movieModel.original_title = response.getString("original_title");
                        } catch (Exception e) {
                            movieModel.original_title = response.getString("original_name");
                        }
                        try {
                            movieModel.release_date = response.getString("release_date");
                        } catch (Exception e) {
                            movieModel.release_date = response.getString("first_air_date");
                        }
                        movieModel.overview = response.getString("overview");
                        movieModel.vote_average = String.valueOf(response.getDouble("vote_average"));
                        movieModel.genres = response.getJSONArray("genres").getJSONObject(0).getString("name");

                        JSONArray videos = response.getJSONObject("videos").getJSONArray("results");
                        JSONArray images = response.getJSONObject("images").getJSONArray("backdrops");

                        JSONArray credits = response.getJSONObject("credits").getJSONArray("cast");

                        Random r = new Random();
                        int index = 0, logoIndex = 0;
                        if (images.length() > 1) {
                            index = r.nextInt(images.length());
                            movieModel.banner = images.getJSONObject(index).getString("file_path");
                        }
                        JSONArray logos = response.getJSONObject("images").getJSONArray("logos");
                        if (logos.length() > 1) {
                            for (int i = 0; i < logos.length(); i++) {
                                JSONObject object = logos.getJSONObject(i);
                                String lang = object.getString("iso_639_1");
                                if (lang.equals("fr")){
                                    logoIndex = i;
                                    break;
                                }
                            }
                            if (logoIndex == 0){
                                for (int i = 0; i < logos.length(); i++) {
                                    JSONObject object = logos.getJSONObject(i);
                                    String lang = object.getString("iso_639_1");
                                    if (lang.equals("en")){
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
                            } catch (Exception e){
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
            String capitalized = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
            binding.date.setText(capitalized);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setUI: " + Constants.getImageLink(movieModel.banner));
        Glide.with(mContext).load(Constants.getImageLink(movieModel.banner)).into(binding.banner);

        try {
            TranslateAPI desc = new TranslateAPI(
                    Language.AUTO_DETECT,   //Source Language
                    Language.FRENCH,         //Target Language
                    movieModel.overview);

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