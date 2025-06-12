package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.rebound.connectors.CustomerConnector;
import com.rebound.main.MainPageActivity;
import com.rebound.models.Customer.Customer;

public class MainActivity extends AppCompatActivity {

    TextView txtLoginForgotPassword;
    TextView txtBottomLoginRegister;
    ImageView imgBackLogin;

    EditText edtLoginEmail;
    EditText edtLoginPassword;
    MaterialButton btnLogin;
    MaterialCheckBox chkTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vô hiệu hóa nút login ban đầu
        btnLogin.setEnabled(false);
        btnLogin.setAlpha(0.5f);

        // Lắng nghe checkbox
        chkTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnLogin.setEnabled(isChecked);
            btnLogin.setAlpha(isChecked ? 1.0f : 0.5f);
        });
    }

    private void addEvents() {
        imgBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        });

        txtBottomLoginRegister.setOnClickListener(v -> openRegisterActivity());

        txtLoginForgotPassword.setOnClickListener(v -> openForgotPasswordActivity());
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        intent.putExtra("previous_activity", "main");
        startActivity(intent);
        finish();
    }

    private void openForgotPasswordActivity() {
        Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void do_login(View view) {
        if (!chkTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the terms to continue.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = edtLoginEmail.getText().toString();
        String pwd = edtLoginPassword.getText().toString();

        CustomerConnector cc = new CustomerConnector();
        Customer cus = cc.login(email, pwd);

        if (cus != null) {
            Toast.makeText(this, "Login successful - welcome " + cus.getUsername() + "!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login failed - please check your account again!", Toast.LENGTH_LONG).show();
        }

        edtLoginEmail.setText("");
        edtLoginPassword.setText("");
    }

    private void addViews() {
        txtLoginForgotPassword = findViewById(R.id.txtLoginForgotPassword);
        txtBottomLoginRegister = findViewById(R.id.txtBottomLoginRegister);
        imgBackLogin = findViewById(R.id.imgBackLogin);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        chkTerms = findViewById(R.id.chkTerms);
    }
}
