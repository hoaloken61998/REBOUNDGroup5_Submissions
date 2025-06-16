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
            if (item.title.equals(newItem.title) && item.variant.equals(newItem.variant)) {
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
        return new ArrayList<>(cartItems); //
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
