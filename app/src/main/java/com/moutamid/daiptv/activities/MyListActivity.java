package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.moutamid.daiptv.R;
import com.moutamid.daiptv.databinding.ActivityMyListBinding;

public class MyListActivity extends AppCompatActivity {
    ActivityMyListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> onBackPressed());

    }
}