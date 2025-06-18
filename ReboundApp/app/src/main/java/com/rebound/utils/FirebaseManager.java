//package com.rebound.utils;
//
//import android.util.Log;
//import androidx.annotation.NonNull;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.*;
//import com.rebound.models.Customer.Customer;
//import com.rebound.models.Cart.ShippingAddress;
//import com.rebound.models.Reservation.Reservation;
//
//public class FirebaseManager {
//    private static final String TAG = "FirebaseManager";
//    private static final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
//
//    // === CUSTOMER ===
//    public static void saveCustomer(Customer customer) {
//        db.child("customers").child(encodeEmail(customer.getEmail()))
//                .setValue(customer)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Customer saved"))
//                .addOnFailureListener(e -> Log.e(TAG, "Failed to save customer", e));
//    }
//
//    public static void getCustomer(String email, final FirebaseCallback<Customer> callback) {
//        db.child("customers").child(encodeEmail(email))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Customer customer = snapshot.getValue(Customer.class);
//                        callback.onSuccess(customer);
//                    }
//
//                    @Override public void onCancelled(@NonNull DatabaseError error) {
//                        callback.onFailure(error.toException());
//                    }
//                });
//    }
//
//    // ✅ NEW: Get current user từ FirebaseAuth
//    public static void getCurrentCustomer(final FirebaseCallback<Customer> callback) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) {
//            callback.onSuccess(null);
//            return;
//        }
//        getCustomer(user.getEmail(), callback);
//    }
//
//    // === SHIPPING ADDRESS ===
//    public static void saveShippingAddress(String email, ShippingAddress address) {
//        db.child("shipping_addresses").child(encodeEmail(email))
//                .setValue(address)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Shipping address saved"))
//                .addOnFailureListener(e -> Log.e(TAG, "Failed to save shipping address", e));
//    }
//
//    public static void getShippingAddress(String email, final FirebaseCallback<ShippingAddress> callback) {
//        db.child("shipping_addresses").child(encodeEmail(email))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ShippingAddress address = snapshot.getValue(ShippingAddress.class);
//                        callback.onSuccess(address);
//                    }
//
//                    @Override public void onCancelled(@NonNull DatabaseError error) {
//                        callback.onFailure(error.toException());
//                    }
//                });
//    }
//
//    // === RESERVATION ===
//    public static void saveReservation(Reservation reservation) {
//        db.child("reservations").child(encodeEmail(reservation.getEmail()))
//                .setValue(reservation)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "Reservation saved"))
//                .addOnFailureListener(e -> Log.e(TAG, "Failed to save reservation", e));
//    }
//
//    public static void getReservation(String email, final FirebaseCallback<Reservation> callback) {
//        db.child("reservations").child(encodeEmail(email))
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Reservation reservation = snapshot.getValue(Reservation.class);
//                        callback.onSuccess(reservation);
//                    }
//
//                    @Override public void onCancelled(@NonNull DatabaseError error) {
//                        callback.onFailure(error.toException());
//                    }
//                });
//    }
//
//    // ✅ NEW: Lấy tên trên thẻ từ "cards/email/nameOnCard"
//    public static void getNameOnCard(String email, final FirebaseCallback<String> callback) {
//        db.child("cards").child(encodeEmail(email)).child("nameOnCard")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String name = snapshot.getValue(String.class);
//                        callback.onSuccess(name);
//                    }
//
//                    @Override public void onCancelled(@NonNull DatabaseError error) {
//                        callback.onFailure(error.toException());
//                    }
//                });
//    }
//
//    private static String encodeEmail(String email) {
//        return email.replace(".", ",");
//    }
//
//    // Generic callback cho mọi kiểu dữ liệu
//    public interface FirebaseCallback<T> {
//        void onSuccess(T data);
//        void onFailure(Exception e);
//    }
//
//    public static String getCurrentUserEmail() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        return user != null ? user.getEmail() : null;
//    }
//}
