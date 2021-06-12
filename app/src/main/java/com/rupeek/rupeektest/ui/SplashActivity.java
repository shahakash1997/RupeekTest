package com.rupeek.rupeektest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rupeek.rupeektest.R;
import com.rupeek.rupeektest.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        countDownTimer.cancel();
        countDownTimer.start();
        super.onResume();
    }

    @Override
    protected void onStop() {
        countDownTimer.cancel();
        super.onStop();
    }

    CountDownTimer countDownTimer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(SplashActivity.this, PlacesActivity.class);
            startActivity(intent);
            overridePendingTransition(-1, R.anim.activity_exit_alpha);
            finish();
        }
    };


}