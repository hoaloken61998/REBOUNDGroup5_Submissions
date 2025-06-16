package com.rebound.checkout;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Cart.ShippingAddress;
import com.rebound.utils.SharedPrefManager;

public class AddShippingAddressActivity extends AppCompatActivity {

    ImageView imgBack;
    MaterialButton btnAddShippingAddress;
    EditText edtName, edtAddress, edtCity, edtDistrict, edtWard, edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_address);

        // Ánh xạ view
        imgBack = findViewById(R.id.imgBackAddShippingAddress);
        btnAddShippingAddress = findViewById(R.id.btnAddShippingAddress);

        edtName = findViewById(R.id.edtAddShippingAddressName);
        edtAddress = findViewById(R.id.edtAddShippingAddress);
        edtCity = findViewById(R.id.edtAddShippingCity);
        edtDistrict = findViewById(R.id.edtAddShippingDistrict);
        edtWard = findViewById(R.id.edtAddShippingWard);
        edtPhone = findViewById(R.id.edtAddShippingPhone);

        imgBack.setOnClickListener(v -> finish());

        btnAddShippingAddress.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String city = edtCity.getText().toString().trim();
            String district = edtDistrict.getText().toString().trim();
            String ward = edtWard.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            // Kiểm tra thông tin
            if (name.isEmpty() || address.isEmpty() || city.isEmpty() || district.isEmpty() || ward.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy user hiện tại
            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
            if (currentCustomer == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gộp địa chỉ đầy đủ (nếu muốn)
            String fullAddress = address + ", " + ward + ", " + district + ", " + city;

            // Tạo đối tượng ShippingAddress
            ShippingAddress shippingAddress = new ShippingAddress(name, fullAddress, phone);

            // Lưu vào SharedPreferences
            SharedPrefManager.saveShippingAddress(this, currentCustomer.getEmail(), shippingAddress);

            Toast.makeText(this, "Shipping address saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
