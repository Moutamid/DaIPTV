package com.moutamid.daiptv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.ChanelsAdapter;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.FragmentChannelsBinding;
import com.moutamid.daiptv.models.ChannelsGroupModel;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChannelsFragment extends Fragment {
    FragmentChannelsBinding binding;
    AppDatabase database;

    public ChannelsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(requireContext());

        addButton();

        List<ChannelsModel> list = database.channelsDAO().getAll();

        ChanelsAdapter adapter = new ChanelsAdapter(requireContext(), list);
        binding.channelsRC.setAdapter(adapter);

        return binding.getRoot();
    }

    private void addButton() {
        List<ChannelsGroupModel> list = database.groupDAO().getAll();
        for (ChannelsGroupModel model : list) {
            MaterialButton button = new MaterialButton(requireContext());
            button.setText(model.getChannelGroup());
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundColor(getResources().getColor(R.color.transparent));
            button.setCornerRadius(12);
            button.setGravity(Gravity.START | Gravity.CENTER);
            binding.sidePanel.addView(button);

            button.setOnClickListener(v -> {
                List<ChannelsModel> channels = database.channelsDAO().getAllByGroup(button.getText().toString());
                ChanelsAdapter adapter = new ChanelsAdapter(requireContext(), channels);
                binding.channelsRC.setAdapter(adapter);
            });
        }
    }
}