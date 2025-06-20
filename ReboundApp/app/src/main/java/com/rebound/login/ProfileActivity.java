//package com.rebound.login;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.rebound.R;
//import com.rebound.main.OrdersActivity;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    LinearLayout optionHelpCenter;
//    LinearLayout optionYourProfile;
//    LinearLayout optionMyOrders;
//
//    ImageView imgBackProfile;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile);
//
//        addViews();
//        addEvents();
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//
//    private void addViews() {
//        optionHelpCenter = findViewById(R.id.optionHelpCenter);
//        optionYourProfile = findViewById(R.id.optionYourProfile);
//        imgBackProfile = findViewById(R.id.imgBackProfile);
//        optionMyOrders = findViewById(R.id.optionMyOrders);
//        Log.d("ProfileActivity", "👉 optionMyOrders = " + optionMyOrders);
//
//    }
//
//
//    private void addEvents() {
//        optionHelpCenter.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, HelpCenterActivity.class);
//            startActivity(intent);
//        });
//        optionYourProfile.setOnClickListener(v -> {
//            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//        });
//        optionMyOrders.setOnClickListener(v -> {
//            Log.d("ProfileActivity", "optionMyOrders clicked"); // kiểm tra có log hay không
//            Intent intent = new Intent(ProfileActivity.this, OrdersActivity.class);
//            startActivity(intent);
//        });
//
//        imgBackProfile.setOnClickListener(v -> {
//            finish();
//        });
//
//    }
//}
