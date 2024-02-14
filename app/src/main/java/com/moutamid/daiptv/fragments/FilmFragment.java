package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentFilmBinding;
import com.moutamid.daiptv.models.MoviesGroupModel;
import com.moutamid.daiptv.models.ParentItemModel;
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FilmFragment extends Fragment {
    FragmentFilmBinding binding;
    AppDatabase database;
    List<MoviesGroupModel> items = new ArrayList<>();
    ArrayList<ParentItemModel> parent = new ArrayList<>();
    ChannelViewModel itemViewModel;
    ParentAdapter adapter;
    public FilmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFilmBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(requireContext());

        itemViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);

        items = database.moviesGroupDAO().getAll();

        for (MoviesGroupModel model : items){
            String group = model.getChannelGroup();
            parent.add(new ParentItemModel(group));
        }

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ParentAdapter(requireContext(), parent, itemViewModel, getViewLifecycleOwner());
        binding.recycler.setAdapter(adapter);

        return binding.getRoot();
    }
}