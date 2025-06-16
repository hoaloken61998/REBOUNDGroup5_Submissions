package com.rebound.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.CartAdapter;
import com.rebound.models.Cart.ProductItem;
import com.rebound.utils.CartManager;

import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView txtSubtotal, txtDelivery, txtDiscount, txtTotal;
    EditText editPromo;
    Button btnApply, btnCheckout;
    ImageView imgBackShoppingCart;
    private static final int DELIVERY_FEE = 20000;
    private int discountAmount = 0;

    private CartAdapter adapter;
    private List<ProductItem> cartItems;

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
        imgBackShoppingCart = findViewById(R.id.imgBackShoppingCart);


        cartItems = CartManager.getInstance().getCartItems();

        adapter = new CartAdapter(cartItems, this::updateSummary, false, this);
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
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty. Please add items before checking out.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, CheckOutShippingActivity.class);
            intent.putExtra("totalAmount", getTotal());
            startActivity(intent);
        });

        imgBackShoppingCart.setOnClickListener(v -> {
            finish();
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
        return cartItems.stream()
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
