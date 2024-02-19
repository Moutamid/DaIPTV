package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.adapters.MyListAdapter;
import com.moutamid.daiptv.databinding.ActivityMyListBinding;
import com.moutamid.daiptv.models.ChannelsModel;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

import java.util.ArrayList;

public class MyListActivity extends AppCompatActivity {
    ActivityMyListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(v -> onBackPressed());

        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
        ArrayList<ChannelsModel> list  = Stash.getArrayList(userModel.id, ChannelsModel.class);

        MyListAdapter adapter = new MyListAdapter(this, list);
        binding.myList.setAdapter(adapter);

    }
}