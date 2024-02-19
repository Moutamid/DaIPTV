package com.moutamid.daiptv.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.databinding.ActivityLoginBinding;
import com.moutamid.daiptv.models.UserModel;

import java.util.ArrayList;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        Constants.getPermissions(this);

        boolean addProfile;

        if (getIntent() != null){
            addProfile = getIntent().getBooleanExtra("addProfile", false);
        } else {
            addProfile = false;
        }

        if (addProfile){
            ArrayList<UserModel> userList = Stash.getArrayList(Constants.USER_LIST, UserModel.class);
            UserModel model = userList.get(0);
            binding.url.getEditText().setText(model.url);
            binding.url.setEnabled(false);
        }

        binding.signin.setOnClickListener(v -> {
            if (valid()) {
                UserModel userModel = new UserModel(
                        UUID.randomUUID().toString(),
                        binding.username.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString(),
                        binding.url.getEditText().getText().toString()
                );
                ArrayList<UserModel> userList = Stash.getArrayList(Constants.USER_LIST, UserModel.class);
                userList.add(userModel);
                Stash.put(Constants.USER, userModel);
                Stash.put(Constants.USER_LIST, userList);
                if (addProfile) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(this, CreateActivity.class));
                    finish();
                }
            }
        });
    }

    private boolean valid() {
        if (binding.username.getEditText().getText().toString().isEmpty()) {
            binding.username.getEditText().setError("Username is empty");
            binding.username.getEditText().requestFocus();
            return false;
        }
        if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.getEditText().setError("Password is empty");
            binding.password.getEditText().requestFocus();
            return false;
        }
        if (binding.url.getEditText().getText().toString().isEmpty()) {
            binding.url.getEditText().setError("URL is empty");
            binding.url.getEditText().requestFocus();
            return false;
        }
        if (!Patterns.WEB_URL.matcher(binding.url.getEditText().getText().toString()).matches()){
            binding.url.getEditText().setError("URL is invalid");
            binding.url.getEditText().requestFocus();
            return false;
        }
        return true;
    }
}