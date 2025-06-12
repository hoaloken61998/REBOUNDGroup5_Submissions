package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;


public class CreateNewPasswordActivity extends AppCompatActivity {

    ImageView imgBackCreateNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_password);

        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        imgBackCreateNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewPasswordActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addViews() {
        imgBackCreateNewPassword = findViewById(R.id.imgBackCreateNewPassword);
    }

    public void do_reset_password(View view) {
        Intent intent = new Intent(CreateNewPasswordActivity.this, PasswordChangedActivity.class);
        startActivity(intent);
    }
}