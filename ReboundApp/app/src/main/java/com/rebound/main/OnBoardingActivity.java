package com.rebound.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;

import java.util.ArrayList;
import java.util.List;


public class OnBoardingActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView descTextView;
    private ImageView leftArrow, rightArrow;


    private List<OnboardingItem> onboardingItems = new ArrayList<>();
    private int currentIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Ánh xạ view
        titleTextView = findViewById(R.id.txtOnboardingTitle);
        descTextView = findViewById(R.id.txtOnboardingDescription);
        leftArrow = findViewById(R.id.btnOnboardingBack);
        rightArrow = findViewById(R.id.btnOnboardingNext);


        // Dữ liệu onboarding
        onboardingItems.add(new OnboardingItem(
                "Seamless Shopping Experience",
                "Explore our stunning collection of ear piercings with a smooth and easy shopping experience."
        ));
        onboardingItems.add(new OnboardingItem(
                "Ear Piercing Blog: Trends, Tips & Inspiration",
                "Stay up-to-date with the latest ear piercing trends, care tips, and style inspirations through our blog"
        ));
        onboardingItems.add(new OnboardingItem(
                "Order Tracking: Stay Updated",
                "Track your order status from placement to delivery with our real-time order tracking feature."
        ));


        // Hiển thị nội dung đầu tiên
        updateContent(currentIndex);


        // Sự kiện nút mũi tên
        rightArrow.setOnClickListener(view -> {
            if (currentIndex < onboardingItems.size() - 1) {
                currentIndex++;
                updateContent(currentIndex);
            }
        });


        leftArrow.setOnClickListener(view -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateContent(currentIndex);
            }
        });
    }


    private void updateContent(int index) {
        OnboardingItem item = onboardingItems.get(index);


        // Hiển thị trực tiếp không animate
        titleTextView.setText(item.title);
        descTextView.setText(item.description);


        // Cập nhật dot
        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);


        dot1.setSelected(false);
        dot2.setSelected(false);
        dot3.setSelected(false);


        switch (index) {
            case 0: dot1.setSelected(true); break;
            case 1: dot2.setSelected(true); break;
            case 2: dot3.setSelected(true); break;
        }
    }


    class OnboardingItem {
        String title;
        String description;


        OnboardingItem(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }
}



