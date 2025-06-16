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
        // Nhận email và listCustomer từ màn trước
        email = getIntent().getStringExtra("email");

        listCustomer = SharedPrefManager.getCustomerList(this);

// Nếu chưa từng lưu danh sách (lần đầu mở app)
        if (listCustomer == null) {
            listCustomer = new ListCustomer();
            listCustomer.addSampleCustomers();  // chỉ gọi 1 lần duy nhất
            SharedPrefManager.saveCustomerList(this, listCustomer); // lưu lại để dùng sau
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
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm đúng user và update password
        boolean updated = false;
        for (Customer customer : listCustomer.getCustomers()) {
            if (customer.getEmail().equalsIgnoreCase(email)) {
                customer.setPassword(newPass);
                updated = true;

                // ✅ Lưu lại password mới bằng SharedPreferences
                getSharedPreferences("user_data", MODE_PRIVATE)
                        .edit()
                        .putString("email", email)
                        .putString("password", newPass)
                        .apply();

                break;
            }
        }

        if (updated) {
            // ✅ Lưu danh sách người dùng mới sau khi cập nhật password
            SharedPrefManager.saveCustomerList(this, listCustomer);

            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateNewPasswordActivity.this, PasswordChangedActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }
}
