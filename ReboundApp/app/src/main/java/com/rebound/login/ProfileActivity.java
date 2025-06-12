package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout optionHelpCenter;
    LinearLayout optionYourProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        optionHelpCenter = findViewById(R.id.optionHelpCenter);
        optionYourProfile = findViewById(R.id.optionYourProfile);

    }

    private void addEvents() {
        optionHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpCenterActivity.class);
            startActivity(intent);
        });
        optionYourProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
