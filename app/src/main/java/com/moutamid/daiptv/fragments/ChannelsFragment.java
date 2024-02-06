package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.ChanelsAdapter;
import com.moutamid.daiptv.databinding.FragmentChannelsBinding;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;

public class ChannelsFragment extends Fragment {
    FragmentChannelsBinding binding;

    public ChannelsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(getLayoutInflater(), container, false);

        addButton("France FHD | TV(95)");
        addButton("France HD-SD | TV(156)");
        addButton("France H256 | TV(73)");
        addButton("FR | FR Sports | TV(226)");

        ArrayList<ChannelsModel> list = new ArrayList<>();
        list.add(new ChannelsModel("Viking", "123"));
        list.add(new ChannelsModel("Game of throne", "25645"));
        list.add(new ChannelsModel("Shazam", "123"));
        list.add(new ChannelsModel("Batman", "123"));
        list.add(new ChannelsModel("Thor", "123"));

        ChanelsAdapter adapter = new ChanelsAdapter(requireContext(), list);
        binding.channelsRC.setAdapter(adapter);

        return binding.getRoot();
    }

    private void addButton(String s) {
        MaterialButton button = new MaterialButton(requireContext());
        button.setText(s);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setBackgroundColor(getResources().getColor(R.color.transparent));
        button.setCornerRadius(12);
        button.setGravity(Gravity.START|Gravity.CENTER);
        binding.sidePanel.addView(button);
    }
}