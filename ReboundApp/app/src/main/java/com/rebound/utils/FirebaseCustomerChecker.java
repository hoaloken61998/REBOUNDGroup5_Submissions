package com.rebound.utils;

import com.rebound.models.Customer.Customer;
import com.rebound.callback.FirebaseListCallback;
import java.util.ArrayList;

public class FirebaseCustomerChecker {
    public interface TakenCallback {
        void onResult(boolean isTaken);
        void onError(String error);
    }

    // Only check PascalCase for Username
    public static void isUsernameTaken(String username, TakenCallback callback) {
        com.google.firebase.database.DatabaseReference userRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("User");
        userRef.orderByChild("Username").equalTo(username)
            .limitToFirst(1)
            .get()
            .addOnSuccessListener(snapshot -> callback.onResult(snapshot.exists()))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Only check PascalCase for Email
    public static void isEmailTaken(String email, TakenCallback callback) {
        com.google.firebase.database.DatabaseReference userRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("User");
        userRef.orderByChild("Email").equalTo(email)
            .limitToFirst(1)
            .get()
            .addOnSuccessListener(snapshot -> callback.onResult(snapshot.exists()))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public static void isPhoneTaken(String phone, String exceptEmail, TakenCallback callback) {
        SharedPrefManager.getAllCustomersFromFirebase(new FirebaseListCallback<Customer>() {
            @Override
            public void onSuccess(ArrayList<Customer> customers) {
                for (Customer c : customers) {
                    if (String.valueOf(c.getPhoneNumber()).equals(phone)) {
                        if (exceptEmail == null || (c.getEmail() != null && !c.getEmail().equalsIgnoreCase(exceptEmail))) {
                            callback.onResult(true);
                            return;
                        }
                    }
                }
                callback.onResult(false);
            }
            @Override
            public void onFailure(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
}
