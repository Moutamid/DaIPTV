package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.databinding.FragmentChannelsBinding;

public class ChannelsFragment extends Fragment {
    FragmentChannelsBinding binding;

    public ChannelsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(getLayoutInflater(), container, false);

        for (int i = 0; i < 10; i++) {
            MaterialButton button = new MaterialButton(requireContext());
            button.setText("Faveriot " + i);
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundColor(getResources().getColor(R.color.transparent));
            button.setCornerRadius(12);
            binding.sidePanel.addView(button);
        }

        return binding.getRoot();
    }
}