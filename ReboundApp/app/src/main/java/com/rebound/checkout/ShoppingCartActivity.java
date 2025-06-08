package com.rebound.checkout;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.adapters.CartAdapter;
import com.rebound.data.CartData;
import com.rebound.R;

public class ShoppingCartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtSubtotal, txtDelivery, txtDiscount, txtTotal;
    CartData cartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartData = new CartData();
        recyclerView = findViewById(R.id.recyclerView);
        txtSubtotal = findViewById(R.id.txtShoppingCartSummarySubtotal);
        txtDelivery = findViewById(R.id.txtShoppingCartSummaryDelivery);
        txtDiscount = findViewById(R.id.txtShoppingCartSummaryDiscount);
        txtTotal = findViewById(R.id.txtShoppingCartSummaryTotal);

        EditText editPromo = findViewById(R.id.edtShoppingCartPromo);
        Button btnApply = findViewById(R.id.btnShoppingCartApply);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        CartAdapter adapter = new CartAdapter(cartData.getItems(), this::updateSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateSummary();

        btnApply.setOnClickListener(v -> {
            String code = editPromo.getText().toString().trim();
            if (code.equalsIgnoreCase("SALE10")) {
                cartData.setDiscount(100000);
                Toast.makeText(this, "Applied 100.000 discount", Toast.LENGTH_SHORT).show();
            } else {
                cartData.setDiscount(0);
                Toast.makeText(this, "Invalid promo", Toast.LENGTH_SHORT).show();
            }
            updateSummary();
        });

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Total: " + format(cartData.getTotal()), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSummary() {
        txtSubtotal.setText(format(cartData.getSubTotal()));
        txtDelivery.setText(format(cartData.getDeliveryFee()));
        txtDiscount.setText(format(cartData.getDiscount()));
        txtTotal.setText(format(cartData.getTotal()));
    }

    private String format(int amount) {
        return String.format("%,d", amount).replace(',', '.');
    }
}
