package com.moutamid.daiptv.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
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
import com.moutamid.daiptv.viewmodels.ChannelViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChannelsFragment extends Fragment {
    FragmentChannelsBinding binding;
    AppDatabase database;
    boolean isAll = true;
    String selectedGroup = "";
    ChanelsAdapter adapter;
    ChannelViewModel itemViewModel;
    private static final String TAG = "ChannelsFragment";
    public ChannelsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        Stash.put(Constants.SELECTED_PAGE, "Channels");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChannelsBinding.inflate(getLayoutInflater(), container, false);

        database = AppDatabase.getInstance(mContext);

        addButton();

        adapter = new ChanelsAdapter(mContext);
        binding.channelsRC.setAdapter(adapter);

        itemViewModel = new ViewModelProvider(this).get(ChannelViewModel.class);
        showAllItems();

        return binding.getRoot();
    }

    // Implement a method to switch between different groups or show all items
    private void switchGroup(String group) {
        binding.channelsRC.smoothScrollToPosition(0);
        itemViewModel.getItemsByGroup(group, Constants.TYPE_CHANNEL).observe(getViewLifecycleOwner(), adapter::submitList);
    }

    // Call this method when you want to show all items
    private void showAllItems() {
        itemViewModel.getAll(Constants.TYPE_CHANNEL).observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void showRecentChannels() {
        itemViewModel.getRecentChannels().observe(getViewLifecycleOwner(), adapter::submitList);
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

    private MaterialButton selectedButton = null;
    private void addButton() {
        List<ChannelsGroupModel> list = database.channelsGroupDAO().getAll();
        list.add(0, new ChannelsGroupModel("Recent Channels"));
        list.add(1, new ChannelsGroupModel("All"));

        for (ChannelsGroupModel model : list) {
            if (!model.getChannelGroup().isEmpty()){
                MaterialButton button = new MaterialButton(mContext);
                button.setText(model.getChannelGroup());
                button.setTextColor(getResources().getColor(R.color.white));
                button.setBackgroundColor(getResources().getColor(R.color.transparent));
                button.setCornerRadius(12);
                button.setNextFocusUpId(R.id.Chaines);
                button.setGravity(Gravity.START | Gravity.CENTER);
                binding.sidePanel.addView(button);
                button.setStrokeColorResource(R.color.transparent);
                button.setStrokeWidth(2);

//            if (model.getChannelGroup().equals("All")){
//                button.setStrokeColorResource(R.color.red);
//                selectedButton = button;
//            }

                if (selectedButton == null && button.getText().toString().equals("All")) {
                    button.setStrokeColorResource(R.color.red);
                    selectedButton = button;
                }

                button.setOnClickListener(v -> {
                    isAll = false;
                    selectedGroup = button.getText().toString();

                    if (selectedGroup.equals("All")){
                        showAllItems();
                    } else if (selectedGroup.equals("Recent Channels")) {
                        showRecentChannels();
                    } else {
                        switchGroup(selectedGroup);
                    }
                    if (selectedButton != null) {
                        selectedButton.setStrokeColorResource(R.color.transparent); // Remove stroke from previously selected button
                    }
                    button.setStrokeColorResource(R.color.red); // Add stroke to newly selected button
                    selectedButton = button;

                });
            }
        }
    }
}