package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;

public class UpdatePasswordActivity extends AppCompatActivity {
    Button btnSend;
    TextView txtBottomUpdatePassword;
    ImageView imgBackUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addViews();
        addEvents();
    }

    private void addViews() {
        btnSend = findViewById(R.id.btnSend);
//        txtBottomUpdatePassword = findViewById(R.id.txtBottomForgotPasswordLogin);
//        imgBackUpdatePassword = findViewById(R.id.imgBackUpdatePassword);
    }

    private void addEvents() {
        btnSend.setOnClickListener(view -> {
            Intent intent = new Intent(UpdatePasswordActivity.this, CreateNewPasswordActivity.class);
            startActivity(intent);
        });

//        imgBackUpdatePassword.setOnClickListener(view -> {
//            Intent intent = new Intent(UpdatePasswordActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//        });

//        txtBottomUpdatePassword.setOnClickListener(view -> {
//            finish(); // Trở về màn hình trước
//        });
    }
}