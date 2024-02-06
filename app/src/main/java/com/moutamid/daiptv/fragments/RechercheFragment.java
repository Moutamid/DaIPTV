package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.databinding.FragmentRechercheBinding;

public class RechercheFragment extends Fragment {
    FragmentRechercheBinding binding;
    public RechercheFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRechercheBinding.inflate(getLayoutInflater(), container, false);



        return binding.getRoot();
    }
}