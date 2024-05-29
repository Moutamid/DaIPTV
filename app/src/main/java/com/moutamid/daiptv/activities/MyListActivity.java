package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(binding.getRoot());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            getWindow().setDecorFitsSystemWindows(false);
//            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent, null));
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }

        binding.back.setOnClickListener(v -> onBackPressed());

        UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
        ArrayList<ChannelsModel> list  = Stash.getArrayList(userModel.id, ChannelsModel.class);

        MyListAdapter adapter = new MyListAdapter(this, list);
        binding.myList.setAdapter(adapter);

    }
}