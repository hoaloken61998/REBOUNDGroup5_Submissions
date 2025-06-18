package com.rebound.main;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rebound.R;

public class OrdersActivity extends AppCompatActivity {

    private TextView tabOngoing, tabCompleted;

    private final Fragment ongoingFragment = new OngoingFragment();
    private final Fragment completedFragment = new CompletedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Gán view
        tabOngoing = findViewById(R.id.tabOngoing);
        tabCompleted = findViewById(R.id.tabCompleted);

        ImageView imgBack = findViewById(R.id.imgBack); // 👈 xử lý icon back
        imgBack.setOnClickListener(v -> finish());

        // Load mặc định Fragment ongoing
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, ongoingFragment)
                .commit();

        // Click tab Ongoing
        tabOngoing.setOnClickListener(v -> {
            switchFragment(ongoingFragment);
            tabOngoing.setBackgroundResource(R.drawable.tab_selected);
            tabCompleted.setBackgroundResource(android.R.color.transparent);
            tabOngoing.setTextColor(getColor(R.color.white));
            tabCompleted.setTextColor(getColor(R.color.accent_dark));
        });

        // Click tab Completed
        tabCompleted.setOnClickListener(v -> {
            switchFragment(completedFragment);
            tabCompleted.setBackgroundResource(R.drawable.tab_selected);
            tabOngoing.setBackgroundResource(android.R.color.transparent);
            tabCompleted.setTextColor(getColor(R.color.white));
            tabOngoing.setTextColor(getColor(R.color.accent_dark));
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
