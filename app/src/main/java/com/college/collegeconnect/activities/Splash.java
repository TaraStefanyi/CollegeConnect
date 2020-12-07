package com.college.collegeconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.college.collegeconnect.datamodels.SaveSharedPreference;
import com.google.android.gms.ads.MobileAds;


public class Splash extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (SaveSharedPreference.getCheckedItem(this) == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (SaveSharedPreference.getCheckedItem(this) == 1)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else if (SaveSharedPreference.getCheckedItem(this) == 2)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        MobileAds.initialize(this);
        if (SaveSharedPreference.getRef(this)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity( new Intent(Splash.this, OnBoardingScreenm.class));
        }
        finish();
    }
}
