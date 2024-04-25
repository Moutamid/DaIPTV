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

//            UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
//            if (userModel!=null){
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
//            } else {
//                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                finish();
//            }

            if (Constants.checkInternet(SplashActivity.this)){
                UserModel userModel = (UserModel) Stash.getObject(Constants.USER, UserModel.class);
                if (userModel!=null){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
//                    if (Stash.getBoolean(Constants.IS_POSTER_UPDATED, false)) {
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        finish();
//                    } else {
//                        startActivity(new Intent(SplashActivity.this, PosterUpdatedActivity.class));
//                        finish();
//                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            } else {
                new AlertDialog.Builder(SplashActivity.this)
                        .setCancelable(false)
                        .setTitle("Pas d'Internet")
                        .setMessage("Vérifiez votre connection internet")
                        .setPositiveButton("Recommencez", (dialog, which) -> {
                            dialog.dismiss();
                            recreate();
                        }).setNegativeButton("Fermer", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .show();
            }
        }, 2000);

    }
}