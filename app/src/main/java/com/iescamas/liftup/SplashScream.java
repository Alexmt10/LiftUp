package com.iescamas.liftup;

import android.content.Intent;
import android.os.Looper;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

import com.iescamas.liftup.registro_inicio.MainActivity;

public class SplashScream extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_scream);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScream.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

    }
}
