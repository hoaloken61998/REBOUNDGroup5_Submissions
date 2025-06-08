package com.rebound.checkout;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.rebound.R;


public class CheckOutShippingActivity extends AppCompatActivity {


    Spinner spinnerShipping, spinnerPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_shipping); // Đảm bảo layout này có spinnerShipping & spinnerPayment


        spinnerShipping = findViewById(R.id.spinnerShipping);
        spinnerPayment = findViewById(R.id.spinnerPayment);


        // Adapter cho Shipping Method
        ArrayAdapter<CharSequence> shippingAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.shipping_methods,
                android.R.layout.simple_spinner_item
        );
        shippingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShipping.setAdapter(shippingAdapter);


        // Adapter cho Payment Method
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(paymentAdapter);


        // (Tuỳ chọn) Lấy lựa chọn người dùng
        String selectedShipping = spinnerShipping.getSelectedItem().toString();
        String selectedPayment = spinnerPayment.getSelectedItem().toString();
    }
}

