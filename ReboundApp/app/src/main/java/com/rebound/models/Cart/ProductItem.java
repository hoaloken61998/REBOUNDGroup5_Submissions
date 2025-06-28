package com.rebound.models.Cart;

import com.rebound.callback.FirebaseListCallback;
import com.rebound.callback.FirebaseSingleCallback;

import java.io.Serializable;

public class ProductItem implements Serializable {
    // --- Firebase fields ---
    public Object ProductName;
    public Object ProductDescription;
    public Object ProductPrice;
    public Object ImageLink;

    // Assuming these are numbers (Longs) in Firebase
    public Long ProductID;
    public Long CategoryID;
    public Long ProductStockQuantity; // Or Integer if the quantity is smaller
    public Long StatusID;          // Or Integer
    public Long SoldQuantity;       // Or Integer
    public Double Rating;      // Or Integer, depending on how you store ratings

    public ProductItem() {
        // Default constructor required for Firebase
    }


}
