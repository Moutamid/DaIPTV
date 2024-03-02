package com.moutamid.daiptv.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.database.AppDatabase;
import com.moutamid.daiptv.databinding.ActivitySeriesBinding;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeriesActivity extends AppCompatActivity {
    ActivitySeriesBinding binding;
    ChannelsModel model;
    String output;
    private static final String TAG = "SeriesActivity";
    AppDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = AppDatabase.getInstance(this);

        model = (ChannelsModel) Stash.getObject(Constants.PASS, ChannelsModel.class);

        Log.d(TAG, "onCreate: " + model.getChannelName());

        Pattern pattern = Pattern.compile("^\\\\|\\\\w+\\\\| (.*?) S\\\\d+ E\\\\d+$");
        Matcher matcher = pattern.matcher(model.getChannelName());
        if (matcher.find()) {
            output = matcher.group(1);
            Log.d(TAG, "onCreate: " + output);
        } else {
            Log.d(TAG, "No match found");
        }

        if (output != null && !output.isEmpty()) {
            getList();
        }

    }

    private void getList() {
        List<ChannelsModel> list = database.channelsDAO().getSeasons(output);

        Log.d(TAG, "getList: " + list.size());

    }
}