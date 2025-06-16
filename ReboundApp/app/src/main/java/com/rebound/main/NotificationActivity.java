package com.rebound.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.NotificationAdapter;
import com.rebound.models.Main.NotificationItem;
import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;
import com.rebound.utils.NotificationStorage;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerViewNotification;
    ImageView imgBackNotification;
    NotificationAdapter adapter;
    List<NotificationItem> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerViewNotification = findViewById(R.id.recyclerViewNotification);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();

        // Lấy user hiện tại
        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        String email = currentCustomer != null ? currentCustomer.getEmail() : null;

        if (email == null || email.isEmpty()) {
            startActivity(new Intent(this, NoNotificationActivity.class));
            finish();
            return;
        }

        // Lấy tất cả thông báo theo email
        List<NotificationItem> allNoti = NotificationStorage.getNotifications(this, email);


        if (allNoti.size() <= 2) {
            startActivity(new Intent(this, NoNotificationActivity.class));
            finish();
            return;
        }

        // Gán thời gian hiển thị cho từng thông báo
        for (NotificationItem item : allNoti) {
            if (item.getType() == NotificationItem.TYPE_ITEM) {
                item.setTimeAgo(formatTimeAgo(item.getTimestamp()));
            }
        }

        adapter = new NotificationAdapter(allNoti);
        recyclerViewNotification.setAdapter(adapter);

        imgBackNotification = findViewById(R.id.imgBackNotification);
        imgBackNotification.setOnClickListener(v -> finish());
    }

    private String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long minutes = diff / (1000 * 60);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";

        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";

        long days = hours / 24;
        return days + " days ago";
    }
}
