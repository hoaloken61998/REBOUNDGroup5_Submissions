package com.rebound.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.CartAdapter;
import com.rebound.utils.CartManager;

public class ShoppingCartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtSubtotal, txtDelivery, txtDiscount, txtTotal;
    EditText editPromo;
    Button btnApply, btnCheckout;

    private static final int DELIVERY_FEE = 20000;
    private int discountAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        addViews();
        addEvents();
        updateSummary();
    }

    private void addViews() {
        recyclerView = findViewById(R.id.recyclerView);
        txtSubtotal = findViewById(R.id.txtShoppingCartSummarySubtotal);
        txtDelivery = findViewById(R.id.txtShoppingCartSummaryDelivery);
        txtDiscount = findViewById(R.id.txtShoppingCartSummaryDiscount);
        txtTotal = findViewById(R.id.txtShoppingCartSummaryTotal);
        editPromo = findViewById(R.id.edtShoppingCartPromo);
        btnApply = findViewById(R.id.btnShoppingCartApply);
        btnCheckout = findViewById(R.id.btnCheckout);

        CartAdapter adapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateSummary);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addEvents() {
        btnApply.setOnClickListener(v -> {
            String code = editPromo.getText().toString().trim();
            if (code.equalsIgnoreCase("SALE10")) {
                discountAmount = 100000;
                Toast.makeText(this, "Applied 100.000 discount", Toast.LENGTH_SHORT).show();
            } else {
                discountAmount = 0;
                Toast.makeText(this, "Invalid promo", Toast.LENGTH_SHORT).show();
            }
            updateSummary();
        });

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckOutShippingActivity.class); // 👈 Mở trang mới
            intent.putExtra("totalAmount", getTotal()); // Nếu muốn truyền tổng tiền
            startActivity(intent);
        });
    }

    private void updateSummary() {
        int subtotal = getSubTotal();
        txtSubtotal.setText(format(subtotal));
        txtDelivery.setText(format(DELIVERY_FEE));
        txtDiscount.setText(format(discountAmount));
        txtTotal.setText(format(getTotal()));
    }

    private int getSubTotal() {
        return CartManager.getInstance().getCartItems().stream()
                .mapToInt(item -> {
                    try {
                        int unitPrice = Integer.parseInt(item.price.replace(".", "").replace(" VND", "").trim());
                        return unitPrice * item.quantity;
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .sum();
    }

    private int getTotal() {
        return getSubTotal() + DELIVERY_FEE - discountAmount;
    }

    private String format(int amount) {
        return String.format("%,d", amount).replace(',', '.') + " VND";
    }
}
