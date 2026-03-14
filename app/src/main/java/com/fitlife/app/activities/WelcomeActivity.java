package com.fitlife.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.fitlife.app.R;
import com.fitlife.app.utils.PreferenceManager;


public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (preferenceManager.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}