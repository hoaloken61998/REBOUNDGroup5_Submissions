package com.rebound.checkout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.adapters.CartAdapter;
import com.rebound.utils.CartManager;

public class CheckOutActivity extends AppCompatActivity {
    TextView txtTotal, txtName, txtAddress, txtPhone, txtCardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        // Ánh xạ View
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCheckout);
        txtTotal = findViewById(R.id.txtCheckoutTotal);
        txtName = findViewById(R.id.txtCheckoutUsername);
        txtAddress = findViewById(R.id.txtCheckoutAddress);
        txtPhone = findViewById(R.id.txtCheckoutPhone);
        txtCardInfo = findViewById(R.id.txtCheckoutCard);
        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);

        // Giả lập thông tin người dùng
        txtName.setText("Giang Bao Tran");
        txtAddress.setText("Tower B, Sky Garden 2, District 7, HCM City");
        txtPhone.setText("0101819898");
        txtCardInfo.setText("Master Card ending ••••89");

        // Setup RecyclerView với CartManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CartAdapter(
                CartManager.getInstance().getCartItems(),
                this::updateTotal
        ));

        updateTotal();

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Checked out: " + format(CartManager.getInstance().getTotalPrice()) + " VND",
                    Toast.LENGTH_SHORT).show();

            CartManager.getInstance().clearCart(); // clear cart sau khi mua
            finish(); // hoặc chuyển sang trang xác nhận thanh toán
        });
    }

    private void updateTotal() {
        int total = CartManager.getInstance().getTotalPrice();
        txtTotal.setText(format(total) + " VND");
    }

    private String format(int amount) {
        return String.format("%,d", amount).replace(',', '.');
    }
}
