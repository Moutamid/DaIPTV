package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.ParentAdapter;
import com.moutamid.daiptv.databinding.FragmentFilmBinding;
import com.moutamid.daiptv.models.ParentItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FilmFragment extends Fragment {
    FragmentFilmBinding binding;
    int[] images = {R.drawable.imag1,R.drawable.imag12,R.drawable.imag13,R.drawable.imag4,R.drawable.imag5};

    public FilmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFilmBinding.inflate(getLayoutInflater(), container, false);

        ArrayList<Integer> items = new ArrayList<>();
        ArrayList<ParentItemModel> parent = new ArrayList<>();
        for (int i=0; i<=10; i++) {
            int j = new Random().nextInt(images.length);
            items.add(images[j]);
        }

        parent.add(new ParentItemModel("Horror", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Drama", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Movie", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Sci-Fi", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("News", items));
        Collections.shuffle(items);
        parent.add(new ParentItemModel("Sports", items));

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.recycler.setAdapter(new ParentAdapter(requireContext(), parent));

        return binding.getRoot();
    }
}