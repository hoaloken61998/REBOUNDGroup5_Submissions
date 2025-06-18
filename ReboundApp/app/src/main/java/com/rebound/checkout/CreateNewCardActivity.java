package com.rebound.checkout;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.adapters.ViewPaperAdapter;
import com.rebound.utils.SharedPrefManager;
import com.rebound.models.Customer.Customer;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class CreateNewCardActivity extends AppCompatActivity {

    private ViewPager2 viewPagerCards;
    private DotsIndicator dotsIndicator;
    private EditText edtNameOnCard, edtCardNumber, edtExpMonth, edtExpYear, edtCVV;
    private MaterialButton btnCreateNewCardAddCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);

        viewPagerCards = findViewById(R.id.viewPagerCards);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        edtNameOnCard = findViewById(R.id.edtNameOnCard);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtExpMonth = findViewById(R.id.edtExpMonth);
        edtExpYear = findViewById(R.id.edtExpYear);
        edtCVV = findViewById(R.id.edtCVV);
        btnCreateNewCardAddCard = findViewById(R.id.btnCreateNewCardAddCard);

        int[] imageResIds = {
                R.mipmap.visa_logo,
                R.mipmap.mastercard_logo,
                R.mipmap.momo_logo
        };

        ViewPaperAdapter adapter = new ViewPaperAdapter(this, imageResIds);
        viewPagerCards.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPagerCards);

        findViewById(R.id.imgBack).setOnClickListener(v -> finish());

        btnCreateNewCardAddCard.setOnClickListener(v -> {
            String nameOnCard = edtNameOnCard.getText().toString().trim();
            String cardNumber = edtCardNumber.getText().toString().trim();
            String expMonth = edtExpMonth.getText().toString().trim();
            String expYear = edtExpYear.getText().toString().trim();
            String cvv = edtCVV.getText().toString().trim();

            // RÀNG BUỘC
            if (nameOnCard.isEmpty() || cardNumber.isEmpty() ||
                    expMonth.isEmpty() || expYear.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cardNumber.length() != 16) {
                Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            int month = Integer.parseInt(expMonth);
            if (month < 1 || month > 12) {
                Toast.makeText(this, "Invalid expiration month", Toast.LENGTH_SHORT).show();
                return;
            }

            if (expYear.length() != 2) {
                Toast.makeText(this, "Year must be 2 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cvv.length() < 3 || cvv.length() > 4) {
                Toast.makeText(this, "CVV must be 3 or 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
            if (currentCustomer != null) {
                String email = currentCustomer.getEmail();
                SharedPrefManager.setNameOnCard(this, email, nameOnCard);
                SharedPrefManager.setCardNumber(this, email, cardNumber); // ✅ Thêm dòng này
            }

            Toast.makeText(this, "Card added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}



