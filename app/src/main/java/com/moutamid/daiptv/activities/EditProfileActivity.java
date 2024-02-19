package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.databinding.ActivityEditProfileBinding;
import com.moutamid.daiptv.models.UserModel;
import com.moutamid.daiptv.utilis.Constants;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);

        binding.username.getEditText().setText(userModel.username);
        binding.password.getEditText().setText(userModel.password);
        binding.url.getEditText().setText(userModel.url);

        binding.back.setOnClickListener(v -> onBackPressed());

        binding.signin.setOnClickListener(v -> {
            if (valid()) {
                UserModel userModel = new UserModel(
                        this.userModel.id,
                        binding.username.getEditText().getText().toString(),
                        binding.password.getEditText().getText().toString(),
                        binding.url.getEditText().getText().toString()
                );
                Stash.put(Constants.USER, userModel);
                startActivity(new Intent(this, CreateActivity.class));
                finish();
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