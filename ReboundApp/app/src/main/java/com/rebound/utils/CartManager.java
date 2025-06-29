package com.rebound.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rebound.models.Cart.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static final String PREFS_NAME = "CartPrefs";
    private static final String CART_KEY = "cart_items";

    private static CartManager instance;
    private final List<ProductItem> cartItems = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();
    private String userEmail = "";

    private String getCartKey() {
        return "cart_items_" + userEmail;
    }

    private CartManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCart();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
    }

    public static CartManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CartManager chưa được init(Context) trước khi dùng");
        }
        return instance;
    }



    public void addToCart(ProductItem newItem) {
        if (newItem == null || newItem.getProductID() == null) {
            // Cannot add an invalid item
            return;
        }

        // Ensure newItem.ProductStockQuantity is not null, default to 1 if it is.
        // This represents the quantity of the newItem being added in this transaction.
        long quantityToAdd = 1L; // Default to adding 1
        if (newItem.getProductStockQuantity() != null && newItem.getProductStockQuantity() > 0) {
            quantityToAdd = newItem.getProductStockQuantity();
        } else if (newItem.getProductStockQuantity() == null) {
            // If the incoming item has a null quantity, explicitly set it to 1 for this add operation
            newItem.setProductStockQuantity(1L); // Default to 1 if null
        }


        for (ProductItem existingItem : cartItems) {
            // Compare ProductID. Make sure ProductID type is consistent (e.g., both Long)
            // If ProductID in ProductItem is Object, you'll need careful casting and null checks here.
            // Assuming ProductID is Long in ProductItem for this example.
            if (existingItem.getProductID() != null && existingItem.getProductID().equals(newItem.getProductID())) {

                // Item already exists, update its quantity
                long currentQuantityInCart = 0L;
                if (existingItem.getProductStockQuantity() != null) {
                    currentQuantityInCart = existingItem.getProductStockQuantity();
                }
                existingItem.setProductStockQuantity(currentQuantityInCart + quantityToAdd);
                saveCart();
                return; // Item found and updated, exit
            }
        }

        // If item was not found in the cart, add it as a new entry.
        // The newItem.ProductStockQuantity should already be set (e.g., to 1 or the passed value).
        // If it was null initially and defaulted to 1 above, that's what will be added.
        cartItems.add(newItem);
        saveCart();
    }

    public void removeFromCart(ProductItem item) {
        cartItems.remove(item);
        saveCart();
    }

    public void clearCart() {
        cartItems.clear();
        saveCart();
    }

    public List<ProductItem> getCartItems() {
        return new ArrayList<>(cartItems); //
    }

    public int getCartSize() {
        return cartItems.size();
    }

    // In CartManager.java

    public int getTotalPrice() {
        int total = 0;
        for (ProductItem item : cartItems) {
            int unitPrice = 0; // Initialize unitPrice here, outside the inner try
            int quantity = 1;  // Initialize quantity here

            try {
                // Price parsing
                if (item.getProductPrice() != null && !item.getProductPrice().toString().isEmpty()) {
                    String cleanedPrice = item.getProductPrice().toString().replace(".", "").replace(" VND", "").trim();
                    unitPrice = Integer.parseInt(cleanedPrice); // Assign to the outer scope unitPrice
                } else {
                    // Handle case where price is null or empty, perhaps log it or default differently
                    System.err.println("Warning: ProductPrice is null or empty for item: " + (item.getProductName() != null ? item.getProductName() : "Unknown"));
                    // unitPrice remains 0, so this item won't add to total if price is invalid
                }

                // Quantity retrieval and conversion
                try {
                    Long stockQuantityLong = item.getProductStockQuantity();
                    if (stockQuantityLong != null) {
                        if (stockQuantityLong > Integer.MAX_VALUE) {
                            quantity = Integer.MAX_VALUE;
                        } else if (stockQuantityLong < Integer.MIN_VALUE) {
                            quantity = Integer.MIN_VALUE;
                        } else {
                            quantity = stockQuantityLong.intValue();
                        }
                        if (quantity < 1) {
                            quantity = 1;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error retrieving quantity for item: " + (item.getProductName() != null ? item.getProductName() : "null item") + " - " + e.getMessage());
                    // quantity remains 1 (default)
                }

                total += unitPrice * quantity; // Now unitPrice is in scope

            } catch (NumberFormatException e) {
                // This specifically catches errors from Integer.parseInt(cleanedPrice)
                System.err.println("Error parsing price for item: " + (item.getProductName() != null ? item.getProductName() : "Unknown") + " - " + e.getMessage());
                // This item effectively adds 0 to the total if its price can't be parsed
            } catch (Exception e) {
                // Catch-all for any other unexpected error with this item
                System.err.println("Unexpected error processing item for total price: " + (item.getProductName() != null ? item.getProductName() : "Unknown") + " - " + e.getMessage());
                // This item also effectively adds 0 to the total in case of other errors
            }
        }
        return total;
    }


    private void saveCart() {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(getCartKey(), json).apply();
    }

    private void loadCart() {
        cartItems.clear(); // clear cart trước khi load mới
        String json = sharedPreferences.getString(getCartKey(), null);
        if (json != null) {
            List<ProductItem> saved = gson.fromJson(json, new TypeToken<List<ProductItem>>() {}.getType());
            if (saved != null) cartItems.addAll(saved);
        }
    }
    public void setUserEmail(String email) {
        this.userEmail = email;
        loadCart(); // Load lại cart của user này
    }
}