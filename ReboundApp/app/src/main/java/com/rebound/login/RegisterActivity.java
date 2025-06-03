package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class RegisterActivity extends AppCompatActivity {

    ImageView imgBackRegister;
    TextView txtRegisterBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        imgBackRegister.setOnClickListener(view -> {
            // Get the intent that started this activity
            Intent intent = getIntent();
            String previousActivity = intent.getStringExtra("previous_activity");

            if ("main".equals(previousActivity)) {
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                // Default to WelcomeActivity if no previous activity specified
                Intent welcomeIntent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
            finish();
        });

        txtRegisterBottom.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });


        txtRegisterBottom.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void addViews() {
        imgBackRegister = findViewById(R.id.imgBackRegister);
        txtRegisterBottom = findViewById(R.id.txtRegisterBottom);
    }
}