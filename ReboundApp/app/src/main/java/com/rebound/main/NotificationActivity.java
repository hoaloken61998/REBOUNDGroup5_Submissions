package com.rebound.main;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.NotificationAdapter;
import com.rebound.models.Main.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {


    RecyclerView recyclerViewNotification;
    NotificationAdapter adapter;
    List<NotificationItem> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerViewNotification = findViewById(R.id.recyclerViewNotification);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();

        // Add header "Latest"
        notificationList.add(new NotificationItem(NotificationItem.TYPE_HEADER, "Latest", "", ""));
        // Add notifications
        notificationList.add(new NotificationItem(NotificationItem.TYPE_NOTIFICATION, "Order Successfully!", "Your order is confirmed.", "30 minutes ago"));

        // Add header "Older"
        notificationList.add(new NotificationItem(NotificationItem.TYPE_HEADER, "Older", "", ""));
        // Add older notifications
        notificationList.add(new NotificationItem(NotificationItem.TYPE_NOTIFICATION, "Freeshipping", "Special offer from June 1st to June 30th", "2 days ago"));

        adapter = new NotificationAdapter(notificationList);
        recyclerViewNotification.setAdapter(adapter);
    }
}
