package com.moutamid.daiptv.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.adapters.SearchAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentRechercheBinding;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;
import java.util.List;

public class RechercheFragment extends Fragment {
    FragmentRechercheBinding binding;
    private static final String TAG = "RechercheFragment";
    List<ChannelsModel> channels, film,series;
    AppDatabase database;
    SearchAdapter channelAdapter, filmAdapter, seriesAdapter;
    Thread thread;
    public RechercheFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Recherche");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRechercheBinding.inflate(getLayoutInflater(), container, false);

        channels = new ArrayList<>();
        film = new ArrayList<>();
        series = new ArrayList<>();

        database = AppDatabase.getInstance(requireContext());

        channelAdapter = new SearchAdapter(requireContext(), channels);
        filmAdapter = new SearchAdapter(requireContext(), film);
        seriesAdapter = new SearchAdapter(requireContext(), series);

        binding.chainesRC.setAdapter(channelAdapter);
        binding.filmsRC.setAdapter(filmAdapter);
        binding.seriesRC.setAdapter(seriesAdapter);

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

                        channels.add(database.channelsDAO().getSearchChannel(name, Constants.TYPE_CHANNEL));
                        film.add(database.channelsDAO().getSearchChannel(name, Constants.TYPE_MOVIE));
                        series.add(database.channelsDAO().getSearchChannel(name, Constants.TYPE_SERIES));

                        Log.d(TAG, "onTextChanged: channels : " + channels.size());
                        Log.d(TAG, "onTextChanged: film : " + film.size());
                        Log.d(TAG, "onTextChanged: series : " + series.size());

                        channelAdapter = new SearchAdapter(requireContext(), channels);
                        filmAdapter = new SearchAdapter(requireContext(), film);
                        seriesAdapter = new SearchAdapter(requireContext(), series);

                        requireActivity().runOnUiThread(() -> {
                            binding.chainesRC.setAdapter(channelAdapter);
                            binding.filmsRC.setAdapter(filmAdapter);
                            binding.seriesRC.setAdapter(seriesAdapter);
                        });
                    });
                    thread.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }
}