package com.rebound.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.main.OnBoardingActivity;
import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;

public class CompleteProfileActivity extends AppCompatActivity {

    MaterialButton btnCompleteProfile;
    ImageView imgBack;
    EditText edtName, edtPhone;
    Spinner spGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        // Ánh xạ view
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile);
        imgBack = findViewById(R.id.imgBack);
        edtName = findViewById(R.id.edtCompleteProfileName);
        edtPhone = findViewById(R.id.edtCompleteProfilePhone);
        spGender = findViewById(R.id.spGenderCompleteProfile);

        // Nút hoàn tất
        btnCompleteProfile.setOnClickListener(view -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || gender.equals(getString(R.string.select_gender))) {
                Toast.makeText(this, getString(R.string.complete_profile_fill_all), Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy email từ intent
            String email = getIntent().getStringExtra("email");
            Customer customer = SharedPrefManager.getCustomerByEmail(this, email);
            if (customer == null) {
                Toast.makeText(this, getString(R.string.complete_profile_user_not_found), Toast.LENGTH_SHORT).show();
                return;
            }

            // Lưu session
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            prefs.edit().putString("logged_in_email", email).apply();

            // Kiểm tra số điện thoại đã tồn tại
            if (SharedPrefManager.isPhoneTaken(this, phone, email)) {
                Toast.makeText(this, getString(R.string.complete_profile_phone_exists), Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông tin
            customer.setFullName(name);
            customer.setPhone(phone);
            customer.setGender(gender);

            SharedPrefManager.updateCustomer(this, customer);
            SharedPrefManager.setCurrentCustomer(this, customer);

            // Chuyển tiếp
            Intent intent = new Intent(this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        });

        // Nút back
        imgBack.setOnClickListener(view -> finish());
    }
}
