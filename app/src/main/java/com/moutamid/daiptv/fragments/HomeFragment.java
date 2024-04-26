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
import java.util.Collections;
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
    ArrayList<MovieModel> films = new ArrayList<>(), series = new ArrayList<>(), latest, additions;
    static ArrayList<ChannelsModel> filmsChan;
    static ArrayList<ChannelsModel> seriesChan;
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
        Log.d(TAG, "getTopFilms: " + url);
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
                            model.id = object.getInt("id");
                            model.original_title = object.getString("title");
                            model.banner = object.getString("poster_path");
                            model.type = Constants.TYPE_MOVIE;

                            filmsChan.add(channel);
                            films.add(model);
                        }
                        Log.d(TAG, "getTopFilms: Films " + films.size());
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

    public void refreshList() {
        Collections.shuffle(series);
        Collections.shuffle(films);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
                            model.id = object.getInt("id");
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
                                if (!channelsModel.getType().equals(Constants.TYPE_CHANNEL)) {
                                    MovieModel model = new MovieModel();
                                    model.type = channelsModel.getType();
                                    model.banner = channelsModel.getChannelImg();
                                    model.original_title = channelsModel.getChannelName();
                                    fvrtList.add(model);
                                }
                            }
                            list.add(new TopItems("Favoris", fvrtList));
                        }
                        adapter = new HomeParentAdapter(mContext, list, selected);
                        binding.recycler.setAdapter(adapter);
                        updatePosters();
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

    private void updatePosters() {
        dialog.show();
        for (int j = 0; j < films.size(); j++) {
            MovieModel movie = films.get(j);
            String url = Constants.getMovieDetails(movie.id, Constants.TYPE_MOVIE, "");
            int finalJ = j;
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

                            films.get(finalJ).banner = images.getJSONObject(index).getString("file_path");
                            filmsChan.get(finalJ).setChannelImg(images.getJSONObject(index).getString("file_path"));
                            Stash.put(Constants.TOP_FILMS, filmsChan);
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
        updateSeriesPoster();
    }

    private void updateSeriesPoster() {
        for (int j = 0; j < series.size(); j++) {
            MovieModel movie = series.get(j);
            String url = Constants.getMovieDetails(movie.id, Constants.TYPE_TV, "");
            int finalJ = j;
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONArray images = response.getJSONObject("images").getJSONArray("posters");
                            String lang;
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

                            series.get(finalJ).banner = images.getJSONObject(index).getString("file_path");
                            seriesChan.get(finalJ).setChannelImg(images.getJSONObject(index).getString("file_path"));
                            Stash.put(Constants.TOP_SERIES, seriesChan);
                            dialog.dismiss();
                            list.get(0).list = new ArrayList<>(films);
                            list.get(1).list = new ArrayList<>(series);
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
    }

    ItemSelected selected = new ItemSelected() {
        @Override
        public void selected(ChannelsModel model) {
            randomChannel = model;
            fetchID();
        }

        @Override
        public void cancel() {
            requestQueue.cancelAll(Constants.FIND_ID);
            requestQueue.cancelAll(Constants.getDetails);
            requestQueue.cancelAll(Constants.Backdrop);
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
        requestQueue.cancelAll("FIND_ID");
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("results");
                        if (array.length() >= 1) {
                            int id = 0;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String original_language = object.getString("original_language");
                                if (original_language.equals("en")) {
                                    id = object.getInt("id");
                                    break;
                                }
                            }
                            if (id == 0) {
                                JSONObject object = array.getJSONObject(0);
                                id = object.getInt("id");
                            }
                            getDetails(id, Constants.lang_fr);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        });
        objectRequest.setTag(Constants.FIND_ID);
        requestQueue.add(objectRequest);
    }

    private void getDetails(int id, String language) {
        String url;
        if (randomChannel.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieDetails(id, Constants.TYPE_TV, language);
        } else {
            url = Constants.getMovieDetails(id, Constants.TYPE_MOVIE, language);
        }
        Log.d(TAG, "fetchID: ID  " + id);
        Log.d(TAG, "fetchID: URL  " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        movieModel = new MovieModel();
                        try {
                            movieModel.original_title = response.getString("title");
                        } catch (Exception e) {
                            movieModel.original_title = response.getString("name");
                        }
                        try {
                            movieModel.release_date = response.getString("release_date");
                        } catch (Exception e) {
                            movieModel.release_date = response.getString("first_air_date");
                        }
                        movieModel.overview = response.getString("overview");

                        if (movieModel.overview.isEmpty() && !language.isEmpty())
                            getDetails(id, "");

                        movieModel.isFrench = !movieModel.overview.isEmpty();
                        movieModel.tagline = response.getString("tagline");
                        movieModel.vote_average = String.valueOf(response.getDouble("vote_average"));
                        movieModel.genres = response.getJSONArray("genres").getJSONObject(0).getString("name");

                        JSONArray videos = response.getJSONObject("videos").getJSONArray("results");
                        JSONArray images = response.getJSONObject("images").getJSONArray("backdrops");

                        int index = -1, logoIndex = 0;
                        if (images.length() > 1) {
                            String[] preferredLanguages = {"null", "fr", "en"};
                            for (String lang : preferredLanguages) {
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject object = images.getJSONObject(i);
                                    String isoLang = object.getString("iso_639_1");
                                    if (isoLang.equalsIgnoreCase(lang)) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    break;
                                }
                            }
                            String banner = "";
                            if (index != -1) {
                                banner = images.getJSONObject(index).getString("file_path");
                            }
                            movieModel.banner = banner;
                        } else {
                            getBackdrop(id, "");
                        }
                        Log.d(TAG, "getDetails: after Back");

                        JSONArray logos = response.getJSONObject("images").getJSONArray("logos");
                        if (logos.length() > 1) {
                            for (int i = 0; i < logos.length(); i++) {
                                JSONObject object = logos.getJSONObject(i);
                                String lang = object.getString("iso_639_1");
                                if (lang.equals("fr") || (logoIndex == 0 && lang.isEmpty())) {
                                    logoIndex = i;
                                    break;
                                } else if (logoIndex == 0 && lang.equals("en")) {
                                    logoIndex = i;
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
        objectRequest.setTag(Constants.getDetails);
        requestQueue.add(objectRequest);
    }

    private void getBackdrop(int id, String language) {
        Log.d(TAG, "getBackdrop: ");
        String url;
        if (randomChannel.getChannelGroup().equals(Constants.TYPE_SERIES)) {
            url = Constants.getMovieDetails(id, Constants.TYPE_TV, language);
        } else {
            url = Constants.getMovieDetails(id, Constants.TYPE_MOVIE, language);
        }
        Log.d(TAG, "fetchID: ID  " + id);
        Log.d(TAG, "fetchID: URL  " + url);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                jsonObject -> {
                    try {
                        JSONArray images = jsonObject.getJSONObject("images").getJSONArray("backdrops");
                        int index = -1, logoIndex = 0;
                        if (images.length() > 1) {
                            String[] preferredLanguages = {"null", "fr", "en"};
                            for (String lang : preferredLanguages) {
                                for (int i = 0; i < images.length(); i++) {
                                    JSONObject object = images.getJSONObject(i);
                                    String isoLang = object.getString("iso_639_1");
                                    if (isoLang.equalsIgnoreCase(lang)) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    break;
                                }
                            }
                            String banner = "";
                            if (index != -1) {
                                banner = images.getJSONObject(index).getString("file_path");
                            }
                            movieModel.banner = banner;
                            Glide.with(mContext).load(Constants.getImageLink(movieModel.banner)).placeholder(R.color.transparent).into(binding.banner);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, volleyError -> {
            Log.d(TAG, "getBackdrop: " + volleyError.getLocalizedMessage());
        }
        );
        objectRequest.setTag(Constants.Backdrop);
        requestQueue.add(objectRequest);
    }

    private void setUI() {
        dialog.dismiss();
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
        Glide.with(mContext).load(Constants.getImageLink(movieModel.banner)).placeholder(R.color.transparent).into(binding.banner);

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
            if (!movieModel.isFrench) {
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
            }
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

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}