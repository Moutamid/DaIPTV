package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.databinding.ActivityCreateBinding;
import com.moutamid.daiptv.models.UserModel;

public class CreateActivity extends AppCompatActivity {
    UserModel userModel;
    ActivityCreateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userModel = (UserModel) Stash.getObject(Constants.PASS_USER, UserModel.class);

        new Handler().postDelayed(() -> {
            binding.message.setText("Creating account...");
            create();
        }, 3000);

    }

    private void create() {
        new Handler().postDelayed(() -> {
            Stash.put(Constants.USER, userModel);
            startActivity(new Intent(CreateActivity.this, MainActivity.class));
            finish();
        }, 2000);
    }
}