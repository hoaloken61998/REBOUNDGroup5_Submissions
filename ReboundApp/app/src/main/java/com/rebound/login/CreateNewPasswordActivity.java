package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.utils.SharedPrefManager;

public class CreateNewPasswordActivity extends AppCompatActivity {

    ImageView imgBackCreateNewPassword;
    EditText edtCreateNewPassword, edtConfirmPassword;
    ListCustomer listCustomer;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_new_password);

        email = getIntent().getStringExtra("email");
        listCustomer = SharedPrefManager.getCustomerList(this);

        if (listCustomer == null) {
            listCustomer = new ListCustomer();
            SharedPrefManager.saveCustomerList(this, listCustomer);
        }

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        imgBackCreateNewPassword = findViewById(R.id.imgBackCreateNewPassword);
        edtCreateNewPassword = findViewById(R.id.edtCreateNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
    }

    private void addEvents() {
        imgBackCreateNewPassword.setOnClickListener(v -> {
            Intent intent = new Intent(CreateNewPasswordActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    public void do_reset_password(View view) {
        String newPass = edtCreateNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, getString(R.string.create_password_fill_all), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, getString(R.string.create_password_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = false;
        for (Customer customer : listCustomer.getCustomers()) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                try {
                    customer.setPassword(Long.parseLong(newPass));
                } catch (NumberFormatException e) {
                    customer.setPassword(0L);
                }
                updated = true;

                getSharedPreferences("user_data", MODE_PRIVATE)
                        .edit()
                        .putString("email", email)
                        .putString("password", newPass)
                        .apply();
                break;
            }
        }

        if (updated) {
            SharedPrefManager.saveCustomerList(this, listCustomer);
            Toast.makeText(this, getString(R.string.create_password_success), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PasswordChangedActivity.class));
        } else {
            Toast.makeText(this, getString(R.string.create_password_user_not_found), Toast.LENGTH_SHORT).show();
        }
    }
}
