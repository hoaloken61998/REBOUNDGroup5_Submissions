package com.rebound.login;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;


public class EditProfileActivity extends AppCompatActivity {
    ImageView imgBackEditProfile;
    MaterialButton btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addEvents() {
        imgBackEditProfile.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> {
            Toast.makeText(EditProfileActivity.this, "Update profile successfully", Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(() -> finish(), 1200);
        });
    }

    private void addViews() {
        btnUpdate = findViewById(R.id.btnUpdate);
        imgBackEditProfile = findViewById(R.id.imgBackEditProfile);
    }
}