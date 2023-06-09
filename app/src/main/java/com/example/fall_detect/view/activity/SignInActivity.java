package com.example.fall_detect.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fall_detect.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}