package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rebound.utils.SharedPrefManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.R;


public class OTPVerificationActivity extends AppCompatActivity {

    ImageView imgBackOTPVerification;

    TextView txtResend;

    String receivedOTP, email;
    EditText edtOTP1, edtOTP2, edtOTP3, edtOTP4;
    ListCustomer listCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);

        email = getIntent().getStringExtra("email");
        receivedOTP = getIntent().getStringExtra("otp");

        // ✅ Load listCustomer từ SharedPreferences
        listCustomer = SharedPrefManager.getCustomerList(this);

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
        edtOTP1 = findViewById(R.id.edtOTP1);
        edtOTP2 = findViewById(R.id.edtOTP2);
        edtOTP3 = findViewById(R.id.edtOTP3);
        edtOTP4 = findViewById(R.id.edtOTP4);
    }

    public void do_verify_otp(View view) {
        String userInputOTP = edtOTP1.getText().toString().trim() +
                edtOTP2.getText().toString().trim() +
                edtOTP3.getText().toString().trim() +
                edtOTP4.getText().toString().trim();

        if (userInputOTP.equals(receivedOTP)) {
            Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(OTPVerificationActivity.this, CreateNewPasswordActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("listCustomer", listCustomer); // ✅ Bắt buộc phải có dòng này
            startActivity(intent);
        } else {
            Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
        }
    }


}