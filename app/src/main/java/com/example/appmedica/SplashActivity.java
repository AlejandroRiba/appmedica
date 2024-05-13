package com.example.appmedica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Aquí puedes realizar cualquier inicialización necesaria
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la actividad principal
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finalizar esta actividad
            }
        }, SPLASH_DELAY);
    }
}