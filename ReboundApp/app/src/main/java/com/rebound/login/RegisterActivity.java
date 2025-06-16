package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.rebound.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.rebound.R;
import com.rebound.models.Customer.Customer;

public class RegisterActivity extends AppCompatActivity {

    MaterialButton btnRegister;
    ImageView imgBackRegister;
    TextView txtRegisterBottom;
    MaterialCheckBox chkRegisterTerms;
    EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;

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

        btnRegister.setEnabled(false);
        btnRegister.setAlpha(0.5f);

        chkRegisterTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnRegister.setEnabled(isChecked);
            btnRegister.setAlpha(isChecked ? 1.0f : 0.5f);
        });
    }

    private void addViews() {
        btnRegister = findViewById(R.id.btnRegister);
        imgBackRegister = findViewById(R.id.imgBackRegister);
        txtRegisterBottom = findViewById(R.id.txtRegisterBottom);
        chkRegisterTerms = findViewById(R.id.chkRegisterTerms);

        edtUsername = findViewById(R.id.edtRegisterUsername);
        edtEmail = findViewById(R.id.edtRegisterEmail);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtRegisterConfirmPassword);
    }

    private void addEvents() {
        imgBackRegister.setOnClickListener(view -> {
            Intent intent = getIntent();
            String previousActivity = intent.getStringExtra("previous_activity");

            if ("main".equals(previousActivity)) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, WelcomeActivity.class));
            }
            finish();
        });

        txtRegisterBottom.setOnClickListener(view ->
                startActivity(new Intent(this, MainActivity.class))
        );

        btnRegister.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!chkRegisterTerms.isChecked()) {
                Toast.makeText(this, "Please agree to the terms to continue.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (SharedPrefManager.isUsernameTaken(this, username)) {
                Toast.makeText(this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (SharedPrefManager.isEmailTaken(this, email)) {
                Toast.makeText(this, "Email already exists. Please use another one.", Toast.LENGTH_SHORT).show();
                return;
            }

            Customer newCustomer = new Customer(username, email, password);
            SharedPrefManager.addCustomer(this, newCustomer);
            SharedPrefManager.setCurrentCustomer(this, newCustomer);

            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CompleteProfileActivity.class);
            intent.putExtra("email", email); // truyền sang hồ sơ nếu cần
            startActivity(intent);
        });
    }
}
