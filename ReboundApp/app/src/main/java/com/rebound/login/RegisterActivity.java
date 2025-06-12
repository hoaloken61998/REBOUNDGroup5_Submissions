package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.rebound.R;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnRegister;
    ImageView imgBackRegister;
    TextView txtRegisterBottom;
    MaterialCheckBox chkRegisterTerms;

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

        // Vô hiệu hóa nút đăng ký ban đầu
        btnRegister.setEnabled(false);
        btnRegister.setAlpha(0.5f);

        // Lắng nghe checkbox
        chkRegisterTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnRegister.setEnabled(isChecked);
            btnRegister.setAlpha(isChecked ? 1.0f : 0.5f);
        });
    }

    private void addEvents() {
        imgBackRegister.setOnClickListener(view -> {
            Intent intent = getIntent();
            String previousActivity = intent.getStringExtra("previous_activity");

            if ("main".equals(previousActivity)) {
                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                Intent welcomeIntent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                startActivity(welcomeIntent);
            }
            finish();
        });

        txtRegisterBottom.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            if (!chkRegisterTerms.isChecked()) {
                Toast.makeText(RegisterActivity.this, "Please agree to the terms to continue.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(RegisterActivity.this, CompleteProfileActivity.class);
            startActivity(intent);
        });
    }

    private void addViews() {
        btnRegister = findViewById(R.id.btnRegister);
        imgBackRegister = findViewById(R.id.imgBackRegister);
        txtRegisterBottom = findViewById(R.id.txtRegisterBottom);
        chkRegisterTerms = findViewById(R.id.chkRegisterTerms);
    }
}
