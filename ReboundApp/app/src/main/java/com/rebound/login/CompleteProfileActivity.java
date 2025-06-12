package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.main.MainPageActivity;
import com.rebound.main.OnBoardingActivity;

public class CompleteProfileActivity extends AppCompatActivity {

    MaterialButton btnCompleteProfile;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);
        imgBack = findViewById(R.id.imgBack);

        btnCompleteProfile.setOnClickListener(view -> {
            Intent intent = new Intent(CompleteProfileActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish(); // Optional: kết thúc màn hình hiện tại
        });

        imgBack.setOnClickListener(view -> {
            finish(); // Quay lại màn hình trước
        });
    }
}