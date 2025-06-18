package com.rebound.checkout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import com.rebound.main.NavBarActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.adapters.CartAdapter;
import com.rebound.models.Cart.ProductItem;
import com.rebound.models.Orders.Order;
import com.rebound.models.Orders.Product;
import com.rebound.utils.CartManager;
import com.rebound.utils.OrderManager;
import com.rebound.utils.SharedPrefManager;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Cart.ShippingAddress;
import com.rebound.models.Main.NotificationItem;
import com.rebound.utils.NotificationStorage;

import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    TextView txtTotal, txtName, txtAddress, txtPhone, txtCardInfo;
    int totalAmountFromIntent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCheckout);
        txtTotal = findViewById(R.id.txtCheckoutTotal);
        txtName = findViewById(R.id.txtCheckoutUsername);
        txtAddress = findViewById(R.id.txtCheckoutAddress);
        txtPhone = findViewById(R.id.txtCheckoutPhone); // GẮN PHONE
        txtCardInfo = findViewById(R.id.txtCheckoutCard);
        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);

        totalAmountFromIntent = getIntent().getIntExtra("totalAmount", 0);

        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        if (currentCustomer != null) {
            String email = currentCustomer.getEmail();
            ShippingAddress address = SharedPrefManager.getShippingAddress(this, email);

            if (address != null) {
                txtName.setText(address.getName() != null ? address.getName() : currentCustomer.getFullName());
                txtAddress.setText(address.getAddress() != null ? address.getAddress() : "Shipping address not available");
                txtPhone.setText(address.getPhone() != null ? address.getPhone() : "No phone");
            } else {
                txtName.setText(currentCustomer.getFullName() != null ? currentCustomer.getFullName() : currentCustomer.getUsername());
                txtAddress.setText("Shipping address not available");
                txtPhone.setText("No phone");
            }

            // Card info
            String nameOnCard = SharedPrefManager.getNameOnCard(this, email);
            String cardNumber = SharedPrefManager.getCardNumber(this, email);

            if (cardNumber != null && !cardNumber.isEmpty() && cardNumber.length() >= 4) {
                String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
                if (nameOnCard != null && !nameOnCard.isEmpty()) {
                    txtCardInfo.setText(nameOnCard + "\n" + masked);
                } else {
                    txtCardInfo.setText(masked);
                }
            } else {
                txtCardInfo.setText("No card info");
            }

        } else {
            Toast.makeText(this, "User information not found", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CartAdapter(
                CartManager.getInstance().getCartItems(),
                null,
                true
        ));

        updateTotalFromIntent();

        btnCheckout.setOnClickListener(v -> {
            Customer current = SharedPrefManager.getCurrentCustomer(this);
            if (current != null) {
                List<ProductItem> cartItems = CartManager.getInstance().getCartItems();
                List<Product> orderProducts = new ArrayList<>();

                for (ProductItem p : cartItems) {
                    orderProducts.add(new Product(p.title, p.variant, p.price, p.imageRes));
                }

                Order newOrder = new Order(orderProducts, CartManager.getInstance().getTotalPrice() + " VND", "To Receive");

                OrderManager.getInstance().setUserEmail(current.getEmail());
                OrderManager.getInstance().addOrder(newOrder);
            }

            CartManager.getInstance().clearCart();
            showPaymentSuccessPopup();
        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "order_channel",
                    "Order Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo khi đặt hàng thành công");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void updateTotalFromIntent() {
        txtTotal.setText(format(totalAmountFromIntent) + " VND");
    }

    private String format(int amount) {
        return String.format("%,d", amount).replace(',', '.');
    }

    private void showPaymentSuccessPopup() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.payment_success_popup);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txtOrderId = dialog.findViewById(R.id.txtPaymentSuccessfulOrderId);
        String generatedOrderId = generateOrderId();
        txtOrderId.setText("Order ID: " + generatedOrderId);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(v -> {
            dialog.dismiss();
            goToMain();
        });

        MaterialButton btnBackShop = dialog.findViewById(R.id.btnPaymentSuccessfulBackShop);

// Trạng thái ban đầu: trắng, viền vàng, chữ đen
        btnBackShop.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        btnBackShop.setTextColor(Color.BLACK);
        btnBackShop.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#BEB488")));
        btnBackShop.setStrokeWidth(1);

// Khi nhấn: đổi sang vàng, không viền, chữ trắng
        btnBackShop.setOnClickListener(v -> {
            btnBackShop.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BEB488")));
            btnBackShop.setTextColor(Color.WHITE);
            btnBackShop.setStrokeWidth(0);

            // Đợi 150ms cho thấy hiệu ứng màu rồi mới đi tiếp
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialog.dismiss();
                goToMain();
            }, 300);
        });

        ImageView imgSad = dialog.findViewById(R.id.imgSad);
        ImageView imgNeutral = dialog.findViewById(R.id.imgNeutral);
        ImageView imgHappy = dialog.findViewById(R.id.imgHappy);

        imgSad.setOnClickListener(v -> {
            Toast.makeText(this, "You rated: Sad 😞", Toast.LENGTH_SHORT).show();
            highlightSelected(imgSad, imgNeutral, imgHappy);
        });

        imgNeutral.setOnClickListener(v -> {
            Toast.makeText(this, "You rated: Neutral 😐", Toast.LENGTH_SHORT).show();
            highlightSelected(imgNeutral, imgSad, imgHappy);
        });

        imgHappy.setOnClickListener(v -> {
            Toast.makeText(this, "You rated: Happy 😊", Toast.LENGTH_SHORT).show();
            highlightSelected(imgHappy, imgSad, imgNeutral);
        });
        sendOrderNotification();
        dialog.show();
    }

    private String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        String rawId = String.valueOf(timestamp).substring(6) + random;
        return "ORD-" + rawId;
    }

    private void highlightSelected(ImageView selected, ImageView... others) {
        selected.setColorFilter(getResources().getColor(R.color.teal_700), android.graphics.PorterDuff.Mode.SRC_IN);
        for (ImageView img : others) {
            img.setColorFilter(null);
        }

        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        if (currentCustomer != null) {
            String email = currentCustomer.getEmail();
            String cardNumber = SharedPrefManager.getCardNumber(this, email);
            String nameOnCard = SharedPrefManager.getNameOnCard(this, email);

            Log.d("CHECK_CARD", "Email: " + email);
            Log.d("CHECK_CARD", "Card Number: " + cardNumber);
            Log.d("CHECK_CARD", "Name on Card: " + nameOnCard);

            if (cardNumber != null && !cardNumber.isEmpty() && cardNumber.length() >= 4) {
                String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
                txtCardInfo.setText(nameOnCard + "\n" + masked);
            } else {
                txtCardInfo.setText( "No card information available");
            }
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, NavBarActivity.class);
        intent.putExtra("targetFragment", "main");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void sendOrderNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "order_channel")
                .setSmallIcon(R.mipmap.ic_order) // make sure this icon exists
                .setContentTitle("Order Placed Successfully!")
                .setContentText("Thank you for shopping at Rebound. Your order is being processed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1001, builder.build());

        long now = System.currentTimeMillis();
        NotificationItem item = new NotificationItem(
                NotificationItem.TYPE_NOTIFICATION,
                "Order Placed Successfully!",
                "Your order is being processed.",
                "Just now", // Can be formatted when displayed
                now
        );

        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        if (currentCustomer != null) {
            NotificationStorage.saveNotification(this, currentCustomer.getEmail(), item);
        }
    }
}

