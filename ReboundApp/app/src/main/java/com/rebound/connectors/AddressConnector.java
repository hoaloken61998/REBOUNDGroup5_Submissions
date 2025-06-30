package com.rebound.connectors;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rebound.callback.AddressCallback;
import com.rebound.models.Cart.Address;

public class AddressConnector {
    public void getDefaultAddressForUser(Context context, Long userId, AddressCallback callback) {
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Address");
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Address found = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object userIdObj = snapshot.child("UserID").getValue();
                    boolean userIdMatches = false;
                    if (userIdObj != null) {
                        try {
                            Long userIdLong = null;
                            if (userIdObj instanceof Long) {
                                userIdLong = (Long) userIdObj;
                            } else if (userIdObj instanceof String) {
                                userIdLong = Long.parseLong((String) userIdObj);
                            }
                            if (userIdLong != null && userIdLong.equals(userId)) {
                                userIdMatches = true;
                            }
                        } catch (Exception ignored) {}
                    }
                    if (userIdMatches) {
                        Address address = snapshot.getValue(Address.class);
                        if (address != null && String.valueOf(address.getIsDefault()).equalsIgnoreCase("true")) {
                            found = address;
                            break;
                        }
                        if (address != null && found == null) {
                            found = address; // fallback to first if no default
                        }
                    }
                }
                if (found != null) {
                    callback.onAddressLoaded(found);
                } else {
                    callback.onError("No address found for user");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }

    public void updateAddress(Address address, AddressCallback callback) {
        if (address == null || address.getAddressID() == null) {
            if (callback != null) callback.onError("Address or AddressID is null");
            return;
        }
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("Address");
        addressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean updated = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object idObj = snapshot.child("AddressID").getValue();
                    if (idObj != null && String.valueOf(idObj).equals(String.valueOf(address.getAddressID()))) {
                        snapshot.getRef().setValue(address)
                            .addOnSuccessListener(aVoid -> {
                                if (callback != null) callback.onAddressLoaded(address);
                            })
                            .addOnFailureListener(e -> {
                                if (callback != null) callback.onError(e.getMessage());
                            });
                        updated = true;
                        break;
                    }
                }
                if (!updated && callback != null) {
                    callback.onError("No address node found with AddressID: " + address.getAddressID());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (callback != null) callback.onError(databaseError.getMessage());
            }
        });
    }

}
