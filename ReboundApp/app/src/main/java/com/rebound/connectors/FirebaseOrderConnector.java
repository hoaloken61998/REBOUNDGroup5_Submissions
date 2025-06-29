package com.rebound.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rebound.callback.OrderFetchCallback;
import com.rebound.main.OrdersActivity;
import com.rebound.models.Orders.Order;

import java.util.ArrayList;
import java.util.List;

public class FirebaseOrderConnector {
    private static final String TAG = "FirebaseOrderConnector";
    private static final String PREF_NAME = "user_prefs";
    private static final String USER_ID_KEY = "user_id";


    public static void getOrdersForLoggedInUser(Context context, OrderFetchCallback orderFetchCallback) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long userId = prefs.getLong(USER_ID_KEY, -1);
        Log.d(TAG, "getOrdersForLoggedInUser: USER_ID_KEY='" + USER_ID_KEY + "', userId=" + userId);
        if (userId == -1) {
            orderFetchCallback.onOrdersFetched(new ArrayList<>());
            return;
        }
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");
        // Query with long value for UserID
        ordersRef.orderByChild("UserID").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Order> userOrders = new ArrayList<>();
                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            Order order = orderSnap.getValue(Order.class);
                            if (order != null) {
                                userOrders.add(order);
                                Log.d(TAG, "Fetched order with UserID: " + order.UserID + ", Status: " + order.Status);
                            }
                        }
                        orderFetchCallback.onOrdersFetched(userOrders);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        orderFetchCallback.onOrdersFetched(new ArrayList<>());
                    }
                });
    }

    public static void deleteOrderById(String orderId, final Runnable onSuccess, final Runnable onFailure) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Order");
        ordersRef.orderByChild("OrderID").equalTo(Double.valueOf(orderId))
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean found = false;
                    for (DataSnapshot orderSnap : snapshot.getChildren()) {
                        orderSnap.getRef().removeValue();
                        found = true;
                    }
                    if (found && onSuccess != null) onSuccess.run();
                    else if (!found && onFailure != null) onFailure.run();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (onFailure != null) onFailure.run();
                }
            });
    }
}
