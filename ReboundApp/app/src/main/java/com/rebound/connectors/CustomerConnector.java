package com.rebound.connectors;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rebound.callback.FirebaseSingleCallback;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Customer.ListCustomer;
import com.rebound.utils.SharedPrefManager;


import java.util.ArrayList;

public class CustomerConnector {
    private ListCustomer listCustomer;

    public CustomerConnector(Context context) {
        listCustomer = SharedPrefManager.getCustomerList(context);

        if (listCustomer == null) {
            listCustomer = new ListCustomer(); // lần đầu chưa có, tạo mẫu
            SharedPrefManager.saveCustomerList(context, listCustomer); // lưu vào
        }
    }

    public CustomerConnector() {

    }

    public ArrayList<Customer> get_all_customers() {
        return listCustomer.getCustomers();
    }

    public void loginWithFirebase(String email, String pwd, com.rebound.callback.FirebaseLoginCallback callback) {
        com.rebound.connectors.FirebaseConnector.getAllItems(
                "User",
                com.rebound.models.Customer.Customer.class,
                new com.rebound.callback.FirebaseListCallback<com.rebound.models.Customer.Customer>() {
                    @Override
                    public void onSuccess(java.util.ArrayList<com.rebound.models.Customer.Customer> customers) {
                        for (com.rebound.models.Customer.Customer c : customers) {
                            if (c != null && c.getEmail() != null &&
                                (c.getEmail().equalsIgnoreCase(email)) &&
                                (String.valueOf(c.getPassword()).equals(pwd) || String.valueOf(c.getPassword()).equalsIgnoreCase(pwd))) {
                                callback.onSuccess(c);
                                return;
                            }
                        }
                        callback.onFailure("Invalid email or password.");
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        callback.onFailure(errorMessage);
                    }
                }
        );
    }

    public void addCustomerToFirebaseWithId(long userId, Customer customer, com.rebound.callback.FirebaseSingleCallback<Void> callback) {
        com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("User")
            .child(String.valueOf(userId))
            .setValue(customer)
            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateCustomerInFirebase(Customer updatedCustomer, FirebaseSingleCallback<Void> callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        final Long userIdLong;
        try {
            Object idObj = updatedCustomer.getUserID();
            if (idObj instanceof Long) {
                userIdLong = (Long) idObj;
            } else if (idObj instanceof String) {
                userIdLong = Long.parseLong((String) idObj);
            } else if (idObj != null) {
                userIdLong = Long.parseLong(idObj.toString());
            } else {
                callback.onFailure("UserID is null");
                return;
            }
        } catch (Exception e) {
            callback.onFailure("Invalid userID");
            return;
        }
        // Find the node with matching UserID and update it
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                    Object userIdObj = userSnap.child("UserID").getValue();
                    if (userIdObj != null && userIdObj instanceof Number && ((Number) userIdObj).longValue() == userIdLong) {
                        userSnap.getRef().setValue(updatedCustomer)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    callback.onFailure("No matching user found to update.");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }
}
