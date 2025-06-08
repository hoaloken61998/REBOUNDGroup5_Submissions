// CheckoutActivity.java
package com.rebound.checkout;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.models.Cart.CartItem;
import com.google.android.material.button.MaterialButton;
import com.rebound.adapters.CartAdapter;
import com.rebound.R;
import com.rebound.data.CartData;

public class CheckOutActivity extends AppCompatActivity {
    TextView txtTotal, txtName, txtAddress, txtPhone, txtCardInfo;
    CartData cartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        cartData = new CartData();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCheckout);
        txtTotal = findViewById(R.id.txtCheckoutTotal);
        txtName = findViewById(R.id.txtCheckoutUsername);
        txtAddress = findViewById(R.id.txtCheckoutAddress);
        txtPhone = findViewById(R.id.txtCheckoutPhone);
        txtCardInfo = findViewById(R.id.txtCheckoutCard);

        // Mock user info
        txtName.setText("Giang Bao Tran");
        txtAddress.setText("Tower B, Sky Garden 2, District 7, HCM City");
        txtPhone.setText("0101819898");
        txtCardInfo.setText("Master Card ending ••••89");

        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CartAdapter(cartData.getItems(), this::updateTotal));

        updateTotal();

        btnCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Checked out: " + format(cartData.getTotal()), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateTotal() {
        txtTotal.setText(format(cartData.getTotal()));
    }

    private String format(int amount) {
        return String.format("%,d", amount).replace(',', '.');
    }
}
