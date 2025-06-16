package com.rebound.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import com.rebound.utils.SharedPrefManager;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.models.Customer.Customer;
import com.rebound.R;
import com.rebound.main.NavBarActivity;


public class WelcomeActivity extends AppCompatActivity {

    Button btnWelcomeLogin;
    Button btnWelcomeRegister;
    TextView txtWelcomeGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        addViews();
        addEvents();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListCustomer listCustomer = SharedPrefManager.getCustomerList(this);

        if (listCustomer == null) {
            listCustomer = new ListCustomer();
            listCustomer.addSampleCustomers(); // chỉ gọi 1 lần duy nhất
            SharedPrefManager.saveCustomerList(this, listCustomer);
        }
    }

    private void addEvents() {
        btnWelcomeLogin.setOnClickListener(view -> {
            // Handle login button click
            // Start LoginActivity
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        });

        btnWelcomeRegister.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            intent.putExtra("previous_activity", "welcome");
            startActivity(intent);
        });
        txtWelcomeGuest.setOnClickListener(view -> {
            // Gán khách vãng lai làm current user bằng setCurrentCustomer
            Customer guest = new Customer();
            guest.setUsername("guest");
            guest.setEmail("guest@guest.com");

            SharedPrefManager.setCurrentCustomer(this, guest); // Lưu 'guest' vào user_session

            Intent intent = new Intent(WelcomeActivity.this, NavBarActivity.class);
            startActivity(intent);
        });
    }

    private void addViews() {
        txtWelcomeGuest = findViewById(R.id.txtWelcomeGuest);
        btnWelcomeLogin = findViewById(R.id.btnWelcomeLogin);
        btnWelcomeRegister = findViewById(R.id.btnWelcomeRegister);
    }
}