package com.moutamid.daiptv.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.adapters.SearchAdapter;
import com.moutamid.daiptv.adapters.SearchFilmsAdapter;
import com.moutamid.daiptv.adapters.SearchSeriesAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentRechercheBinding;
import com.moutamid.daiptv.models.ChannelsFilmsModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.ChannelsSeriesModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;
import java.util.List;

public class RechercheFragment extends Fragment {
    FragmentRechercheBinding binding;
    private static final String TAG = "RechercheFragment";
    List<ChannelsModel> channels;
    List<ChannelsFilmsModel> film;
    List<ChannelsSeriesModel> series;
    AppDatabase database;
    SearchAdapter channelAdapter;
    SearchFilmsAdapter filmAdapter;
    SearchSeriesAdapter seriesAdapter;
    Thread thread;
    public RechercheFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Recherche");
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
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRechercheBinding.inflate(getLayoutInflater(), container, false);

        channels = new ArrayList<>();
        film = new ArrayList<>();
        series = new ArrayList<>();

        database = AppDatabase.getInstance(mContext);

        channelAdapter = new SearchAdapter(mContext, new ArrayList<>());
        filmAdapter = new SearchFilmsAdapter(mContext, new ArrayList<>());
        seriesAdapter = new SearchSeriesAdapter(mContext, new ArrayList<>());

        binding.chainesRC.setAdapter(channelAdapter);
        binding.filmsRC.setAdapter(filmAdapter);
        binding.seriesRC.setAdapter(seriesAdapter);

//        new Handler().postDelayed(() -> {
//            binding.searchET.requestFocus();
//        }, 1500);

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty() && s.toString().length() >= 2) {
                    if (thread != null){
                        if (thread.isAlive()){
//                            thread.destroy();
                            thread.interrupt();
                        }
                    }
                    thread = new Thread(() -> {
                        String name = "%" + s.toString().trim() + "%";
                        channels.clear();
                        film.clear();
                        series.clear();

                        channelAdapter = new SearchAdapter(mContext, new ArrayList<>());
                        filmAdapter = new SearchFilmsAdapter(mContext, new ArrayList<>());
                        seriesAdapter = new SearchSeriesAdapter(mContext, new ArrayList<>());

                        requireActivity().runOnUiThread(() -> {
                            binding.chainesRC.setAdapter(channelAdapter);
                            binding.filmsRC.setAdapter(filmAdapter);
                            binding.seriesRC.setAdapter(seriesAdapter);
                        });

                        channels.add(database.channelsDAO().getSearchChannel(name));
                        film.add(database.filmsDAO().getSearchChannel(name));
                        series.add(database.seriesDAO().getSearchChannel(name));

                        Log.d(TAG, "onTextChanged: channels : " + channels.size());
                        Log.d(TAG, "onTextChanged: film : " + film.size());
                        Log.d(TAG, "onTextChanged: series : " + series.size());

                        if (!channels.isEmpty()){
                            if (channels.get(0) !=null){
                                channelAdapter = new SearchAdapter(mContext, channels);
                            }
                        }
                        if (!series.isEmpty()){
                            if (series.get(0) !=null){
                                seriesAdapter = new SearchSeriesAdapter(mContext, series);
                            }
                        }
                        if (!film.isEmpty()){
                            if (film.get(0) !=null){
                                filmAdapter = new SearchFilmsAdapter(mContext, film);
                            }
                        }
                        requireActivity().runOnUiThread(() -> {
                            binding.chainesRC.setAdapter(channelAdapter);
                            binding.filmsRC.setAdapter(filmAdapter);
                            binding.seriesRC.setAdapter(seriesAdapter);
                        });
                    });
                    thread.start();
                } else {
                    channelAdapter = new SearchAdapter(mContext, new ArrayList<>());
                    filmAdapter = new SearchFilmsAdapter(mContext, new ArrayList<>());
                    seriesAdapter = new SearchSeriesAdapter(mContext, new ArrayList<>());

                    requireActivity().runOnUiThread(() -> {
                        binding.chainesRC.setAdapter(channelAdapter);
                        binding.filmsRC.setAdapter(filmAdapter);
                        binding.seriesRC.setAdapter(seriesAdapter);
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }
}