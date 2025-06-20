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

// Nếu không có thông báo thực sự
        if (allNoti.isEmpty()) {
            startActivity(new Intent(this, NoNotificationActivity.class));
            finish();
            return;
        }

// Gán "time ago"
        for (NotificationItem item : allNoti) {
            if (item.getType() == NotificationItem.TYPE_NOTIFICATION) {
                item.setTimeAgo(formatTimeAgo(item.getTimestamp()));
            }
        }

// ✅ Phân loại thành Latest / Older
        List<NotificationItem> organizedList = categorizeNotifications(allNoti);

// Nếu không có thông báo thật sự (ngoài header)
        if (organizedList.size() <= 2) {
            startActivity(new Intent(this, NoNotificationActivity.class));
            finish();
            return;
        }

        adapter = new NotificationAdapter(organizedList);
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
    private List<NotificationItem> categorizeNotifications(List<NotificationItem> all) {
        long oneHourAgo = System.currentTimeMillis() - 3600 * 1000;

        List<NotificationItem> latest = new ArrayList<>();
        List<NotificationItem> older = new ArrayList<>();

        for (NotificationItem item : all) {
            if (item.getType() == NotificationItem.TYPE_NOTIFICATION) {
                if (item.getTimestamp() >= oneHourAgo) {
                    latest.add(item);
                } else {
                    older.add(item);
                }
            }
        }

        List<NotificationItem> result = new ArrayList<>();
        if (!latest.isEmpty()) {
            result.add(new NotificationItem(NotificationItem.TYPE_HEADER, "Latest", "", ""));
            result.addAll(latest);
        }
        if (!older.isEmpty()) {
            result.add(new NotificationItem(NotificationItem.TYPE_HEADER, "Older", "", ""));
            result.addAll(older);
        }
        return result;
    }
}
