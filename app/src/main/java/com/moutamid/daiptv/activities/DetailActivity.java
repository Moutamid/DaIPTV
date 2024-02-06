package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.CastsAdapter;
import com.moutamid.daiptv.adapters.ChanelsAdapter;
import com.moutamid.daiptv.databinding.ActivityDetailBinding;
import com.moutamid.daiptv.models.CastModel;
import com.moutamid.daiptv.models.ChannelsModel;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> onBackPressed());

        ArrayList<CastModel> list = new ArrayList<>();
        list.add(new CastModel("Katheryn Winnick", "Lagertha", "76 Episode"));
        list.add(new CastModel("Alexander Ludwig", "Bjorn Lothbro", "76 Episode"));
        list.add(new CastModel("Georgia Hirst", "Torvi", "21 Episode"));
        list.add(new CastModel("Alex Høgh Andersen", "Ivar", "76 Episode"));
        list.add(new CastModel("Marco Ilsø", "Hvitserk", "65 Episode"));
        list.add(new CastModel("Gustaf Skarsgård", "Floki", "76 Episode"));

        CastsAdapter adapter = new CastsAdapter(this, list);
        binding.castRC.setAdapter(adapter);

    }
}