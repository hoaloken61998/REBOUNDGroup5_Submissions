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

import com.rebound.R;
import com.rebound.connectors.CustomerConnector;
import com.rebound.models.Customer.Customer;


public class MainActivity extends AppCompatActivity {

    TextView txtLoginForgotPassword;
    TextView txtBottomLoginRegister;
    ImageView imgBackLogin;

    EditText edtLoginEmail;
    EditText edtLoginPassword;

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
    }

    private void addEvents() {
        imgBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotPasswordActivity();
            }
        });

        txtBottomLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        // Add OnClickListener for imgBack
        if (imgBackLogin != null) { // Good practice to check for null, though addViews should initialize it
            imgBackLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            });

            txtLoginForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openForgotPasswordActivity();
                }
            });
        }
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
        String email=edtLoginEmail.getText().toString();
        String pwd=edtLoginPassword.getText().toString();
        CustomerConnector cc=new CustomerConnector();

        Customer cus=cc.login(email,pwd);
        if(cus!=null)
        {
            Toast.makeText(this,
                    "Login successful - welcome " + cus.getUsername() + "!",
                    Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this,
                    "Login failed - please check your account again!",
                    Toast.LENGTH_LONG).show();
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
    }
}