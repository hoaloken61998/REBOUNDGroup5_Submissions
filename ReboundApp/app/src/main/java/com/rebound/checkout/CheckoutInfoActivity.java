//package com.rebound.checkout;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.rebound.R;
//
//public class CheckoutInfoActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_checkout_info);
//
//        // Xử lý padding hệ thống nếu cần
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // 1. Ánh xạ các TextView từ layout
//        TextView txtName = findViewById(R.id.txtCheckoutUsername);
//        TextView txtAddress = findViewById(R.id.txtCheckoutAddress);
//        TextView txtPhone = findViewById(R.id.txtCheckoutPhone);
//
//        // 2. Gán dữ liệu user (giả lập)
//        txtName.setText("Giang Bao Tran");
//        txtAddress.setText("Tower B, Sky Garden 2, District 7, HCM City");
//        txtPhone.setText("0101819898");
//
//
//    }
//}
