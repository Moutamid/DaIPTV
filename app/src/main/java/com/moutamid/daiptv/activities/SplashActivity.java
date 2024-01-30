package com.moutamid.daiptv.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fxn.stash.Stash;
import com.moutamid.daiptv.utilis.Constants;
import com.moutamid.daiptv.MainActivity;
import com.moutamid.daiptv.R;
import com.moutamid.daiptv.models.UserModel;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (Constants.checkInternet(SplashActivity.this)){
                UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                if (userModel!=null){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            } else {
                new AlertDialog.Builder(SplashActivity.this)
                        .setCancelable(false)
                        .setTitle("No Internet")
                        .setMessage("Check your internet connection")
                        .setPositiveButton("Retry", (dialog, which) -> {
                            dialog.dismiss();
                            recreate();
                        }).setNegativeButton("Close", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .show();
            }
        }, 2000);

    }
}