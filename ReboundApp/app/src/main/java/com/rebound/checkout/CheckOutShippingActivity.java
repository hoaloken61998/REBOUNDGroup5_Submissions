package com.rebound.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;
import com.rebound.models.Cart.ShippingAddress;
import com.google.android.material.button.MaterialButton;
import com.rebound.R;

public class CheckOutShippingActivity extends AppCompatActivity {

    Spinner spinnerShipping, spinnerPayment;
    MaterialButton btnCheckoutShipping;
    ImageView imgBack;
    LinearLayout btnAddPayment;
    LinearLayout layoutShippingInfo;
    TextView txtName, txtAddress, txtPhone;
    TextView btnCheckOutShippingAddPayment;
    TextView txtCheckOutShippingPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_shipping);

        spinnerShipping = findViewById(R.id.spinnerShipping);

        ArrayAdapter<CharSequence> shippingAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.shipping_methods,
                android.R.layout.simple_spinner_item
        );
        shippingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShipping.setAdapter(shippingAdapter);

        String selectedShipping = spinnerShipping.getSelectedItem().toString();

        TextView txtTotal = findViewById(R.id.txtCheckOutInfoTotalAmount);
        int totalAmount = getIntent().getIntExtra("totalAmount", 0);
        String formattedTotal = String.format("%,d VND", totalAmount).replace(',', '.');
        txtTotal.setText(formattedTotal);

        LinearLayout btnAddAddress = findViewById(R.id.btnCheckOutInfoAddAddress);
        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckOutShippingActivity.this, AddShippingAddressActivity.class);
            startActivity(intent);
        });

        btnCheckoutShipping = findViewById(R.id.btnCheckoutShipping);
        imgBack = findViewById(R.id.imgBackCheckOutShipping);

        imgBack.setOnClickListener(v -> finish());

        btnCheckoutShipping.setOnClickListener(v -> {
            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
            if (currentCustomer == null) {
                showToast("User not found.");
                return;
            }

            ShippingAddress address = SharedPrefManager.getShippingAddress(this, currentCustomer.getEmail());
            String nameOnCard = SharedPrefManager.getNameOnCard(this, currentCustomer.getEmail());

            if (address == null) {
                showToast("Please add a shipping address.");
                return;
            }

            if (nameOnCard == null || nameOnCard.isEmpty()) {
                showToast("Please add a payment method.");
                return;
            }

            // Nếu mọi thứ hợp lệ -> tiếp tục
            Intent intent = new Intent(CheckOutShippingActivity.this, CheckOutActivity.class);
            intent.putExtra("totalAmount", totalAmount);
            startActivity(intent);
        });

        btnAddPayment = findViewById(R.id.layoutCheckOutShippingAddPayment);
        btnAddPayment.setOnClickListener(v -> {
            Intent intent = new Intent(CheckOutShippingActivity.this, CreateNewCardActivity.class);
            startActivity(intent);
        });

        layoutShippingInfo = findViewById(R.id.layoutCheckOutShippingInfo);
        txtName = findViewById(R.id.txtCheckOutInfoRecipientName);
        txtAddress = findViewById(R.id.txtCheckOutInfoAddress);
        txtPhone = findViewById(R.id.txtCheckOutInfoPhoneNumber);
        txtCheckOutShippingPaymentMethod = findViewById(R.id.txtCheckOutShippingPaymentMethod);

        updateShippingInfoAndCardName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateShippingInfoAndCardName();
    }

    private void updateShippingInfoAndCardName() {
        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);

        if (currentCustomer != null) {
            // Lấy địa chỉ giao hàng
            ShippingAddress address = SharedPrefManager.getShippingAddress(this, currentCustomer.getEmail());
            if (address != null) {
                layoutShippingInfo.setVisibility(View.VISIBLE);
                txtName.setText(address.getName());
                txtAddress.setText(address.getAddress());
                txtPhone.setText(address.getPhone());
            } else {
                layoutShippingInfo.setVisibility(View.GONE);
            }

            // Lấy tên trên thẻ
            String nameOnCard = SharedPrefManager.getNameOnCard(this, currentCustomer.getEmail());
            if (!nameOnCard.isEmpty()) {
                txtCheckOutShippingPaymentMethod.setText(nameOnCard);
            } else {
                txtCheckOutShippingPaymentMethod.setText("No card added");
            }

        } else {
            layoutShippingInfo.setVisibility(View.GONE);
            txtCheckOutShippingPaymentMethod.setText("No user");
        }
    }
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}



