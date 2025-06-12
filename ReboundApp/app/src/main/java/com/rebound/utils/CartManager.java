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

    private CartManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCart(); // load từ SharedPreferences vào list cartItems
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
        for (ProductItem item : cartItems) {
            if (item.title.equals(newItem.title)) {
                item.quantity += newItem.quantity;
                saveCart();
                return;
            }
        }
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
        return new ArrayList<>(cartItems); // bản sao tránh thao tác trực tiếp
    }

    public int getCartSize() {
        return cartItems.size();
    }

    public int getTotalPrice() {
        int total = 0;
        for (ProductItem item : cartItems) {
            try {
                String cleaned = item.price.replace(".", "").replace(" VND", "").trim();
                int unitPrice = Integer.parseInt(cleaned);
                total += unitPrice * item.quantity;
            } catch (Exception ignored) {}
        }
        return total;
    }

    // ---------------- Private: Save / Load ---------------- //

    private void saveCart() {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(CART_KEY, json).apply();
    }

    private void loadCart() {
        String json = sharedPreferences.getString(CART_KEY, null);
        if (json != null) {
            List<ProductItem> saved = gson.fromJson(json, new TypeToken<List<ProductItem>>() {}.getType());
            if (saved != null) cartItems.addAll(saved);
        }
    }
}
