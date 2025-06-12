package com.rebound.login;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;


public class OTPVerificationActivity extends AppCompatActivity {

    ImageView imgBackOTPVerification;

    TextView txtResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);

        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEvents() {
        imgBackOTPVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPVerificationActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OTPVerificationActivity.this, "Resend OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addViews() {
        imgBackOTPVerification = findViewById(R.id.imgBackOTPVerification);
        txtResend = findViewById(R.id.txtResend);
    }

    public void do_verify_otp(View view) {
        Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(OTPVerificationActivity.this, CreateNewPasswordActivity.class);
        startActivity(intent);
    }
}