package com.rebound.checkout;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.adapters.CartAdapter;
import com.rebound.main.NavBarActivity;
import com.rebound.models.Cart.ProductItem;
import com.rebound.models.Cart.ShippingAddress;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Main.NotificationItem;
import com.rebound.models.Orders.Order;
import com.rebound.utils.CartManager;
import com.rebound.utils.NotificationStorage;
import com.rebound.utils.OrderManager;
import com.rebound.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CheckOutActivity extends AppCompatActivity {

    TextView txtTotal, txtName, txtAddress, txtPhone, txtCardInfo, txtSimplePaymentMethod;
    LinearLayout layoutCardInfo, layoutSimplePaymentMethod;
    ImageView imgBack;
    int totalAmountFromIntent = 0;

    boolean fromBankTransfer = false;
    String transactionId = "";
    String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCheckout);
        txtTotal = findViewById(R.id.txtCheckoutTotal);
        txtName = findViewById(R.id.txtCheckoutUsername);
        txtAddress = findViewById(R.id.txtCheckoutAddress);
        txtPhone = findViewById(R.id.txtCheckoutPhone);
        txtCardInfo = findViewById(R.id.txtCheckoutCard);
        txtSimplePaymentMethod = findViewById(R.id.txtSimplePaymentMethod);
        layoutCardInfo = findViewById(R.id.layoutCardInfo);
        layoutSimplePaymentMethod = findViewById(R.id.layoutSimplePaymentMethod);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        MaterialButton btnCheckout = findViewById(R.id.btnCheckout);

        totalAmountFromIntent = getIntent().getIntExtra("totalAmount", 0);
        txtTotal.setText(String.format("%,d VND", totalAmountFromIntent).replace(',', '.'));

        fromBankTransfer = getIntent().getBooleanExtra("fromBankTransfer", false);
        if (fromBankTransfer) {
            transactionId = getIntent().getStringExtra("transactionId");
            time = getIntent().getStringExtra("time");
        }

        String paymentMethod = getIntent().getStringExtra("paymentMethod");
        String cardType = getIntent().getStringExtra("cardType");

        layoutCardInfo.setVisibility(View.GONE);
        layoutSimplePaymentMethod.setVisibility(View.GONE);

        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        if (currentCustomer != null) {
            String email = currentCustomer.getEmail();

            if ("Credit Card".equalsIgnoreCase(cardType) || "Debit Card".equalsIgnoreCase(cardType)) {
                layoutCardInfo.setVisibility(View.VISIBLE);
                layoutSimplePaymentMethod.setVisibility(View.GONE);

                String name = "";
                String number = "";

                if ("Credit Card".equalsIgnoreCase(cardType)) {
                    name = SharedPrefManager.getCreditCardName(this, email);
                    number = SharedPrefManager.getCreditCardNumber(this, email);
                } else {
                    name = SharedPrefManager.getDebitCardName(this, email);
                    number = SharedPrefManager.getDebitCardNumber(this, email);
                }

                if (number != null && number.length() >= 2) {
                    String lastDigits = number.substring(number.length() - 2);
                    txtCardInfo.setText(getString(R.string.checkout_card_format, cardType, name, lastDigits));
                } else {
                    txtCardInfo.setText(getString(R.string.checkout_no_card_info));
                }
            } else {
                layoutCardInfo.setVisibility(View.GONE);
                layoutSimplePaymentMethod.setVisibility(View.VISIBLE);
                txtSimplePaymentMethod.setText(paymentMethod == null || paymentMethod.isEmpty()
                        ? getString(R.string.checkout_method_not_available)
                        : paymentMethod);
            }

            ShippingAddress address = SharedPrefManager.getShippingAddress(this, email);
            if (address != null) {
                txtName.setText(address.getName() != null ? address.getName() : currentCustomer.getFullName());
                txtAddress.setText(address.getAddress() != null ? address.getAddress() : getString(R.string.checkout_shipping_not_available));
                txtPhone.setText(address.getPhone() != null ? address.getPhone() : getString(R.string.checkout_no_phone));
            } else {
                txtName.setText(currentCustomer.getFullName());
                txtAddress.setText(getString(R.string.checkout_shipping_not_available));
                txtPhone.setText(getString(R.string.checkout_no_phone));
            }
        } else {
            Toast.makeText(this, getString(R.string.checkout_user_not_found), Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CartAdapter(
                CartManager.getInstance().getCartItems(),
                null,
                true
        ));

        btnCheckout.setOnClickListener(v -> {
            Customer current = SharedPrefManager.getCurrentCustomer(this);
            if (current != null) {
                List<ProductItem> cartItems = CartManager.getInstance().getCartItems();
                List<ProductItem> orderProducts = new ArrayList<>();
                for (ProductItem p : cartItems) {
                    // Directly add ProductItem to orderProducts
                    orderProducts.add(p);
                }

                String formattedTotal = String.format(java.util.Locale.US, "%,d VND", totalAmountFromIntent).replace(',', '.');
                Order newOrder = new Order();
//                newOrder.productList = orderProducts;
                try {
                    newOrder.TotalAmount = Long.parseLong(totalAmountFromIntent + "");
                } catch (Exception e) {
                    newOrder.TotalAmount = 0L;
                }
                // Also set Subtotal to match TotalAmount for display/consistency
                newOrder.Subtotal = newOrder.TotalAmount;
                newOrder.Status = "To Receive";
                // Show formatted total in a Toast (or set to a TextView if needed)
                Toast.makeText(this, "Total: " + formattedTotal, Toast.LENGTH_SHORT).show();

                OrderManager.getInstance().setUserEmail(current.getEmail());
                OrderManager.getInstance().addOrder(newOrder);

                String title = getString(R.string.checkout_order_success_title);
                String message = fromBankTransfer
                        ? getString(R.string.checkout_order_success_bank, transactionId, formattedTotal)
                        : getString(R.string.checkout_order_success_normal);

                NotificationItem item = new NotificationItem(
                        NotificationItem.TYPE_NOTIFICATION,
                        title,
                        message,
                        "Just now",
                        System.currentTimeMillis()
                );
                NotificationStorage.saveNotification(this, current.getEmail(), item);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "order_channel")
                            .setSmallIcon(R.mipmap.ic_order)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);
                    NotificationManagerCompat.from(this).notify(1001, builder.build());
                }
            }

            CartManager.getInstance().clearCart();

            if (fromBankTransfer) {
                Intent intent = new Intent(this, PaymentSuccessActivity.class);
                intent.putExtra("transactionId", transactionId);
                intent.putExtra("time", time);
                intent.putExtra("amount", totalAmountFromIntent);
                startActivity(intent);
                finish();
            } else {
                showPaymentSuccessPopup();
            }
        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "order_channel",
                    getString(R.string.checkout_notification_channel_title),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(getString(R.string.checkout_notification_channel_description));
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showPaymentSuccessPopup() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.payment_success_popup);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView txtOrderId = dialog.findViewById(R.id.txtPaymentSuccessfulOrderId);
        txtOrderId.setText(getString(R.string.checkout_order_id, generateOrderId()));

        dialog.findViewById(R.id.imgClose).setOnClickListener(v -> {
            dialog.dismiss();
            goToMain();
        });

        MaterialButton btnBackShop = dialog.findViewById(R.id.btnPaymentSuccessfulBackShop);
        btnBackShop.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        btnBackShop.setTextColor(Color.BLACK);
        btnBackShop.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#BEB488")));
        btnBackShop.setStrokeWidth(1);
        btnBackShop.setOnClickListener(v -> {
            btnBackShop.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BEB488")));
            btnBackShop.setTextColor(Color.WHITE);
            btnBackShop.setStrokeWidth(0);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialog.dismiss();
                goToMain();
            }, 300);
        });

        sendOrderNotification();
        dialog.show();
    }

    private String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return "ORD-" + String.valueOf(timestamp).substring(6) + random;
    }

    private void goToMain() {
        Intent intent = new Intent(this, NavBarActivity.class);
        intent.putExtra("targetFragment", "main");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void sendOrderNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }

        Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
        if (currentCustomer == null) return;

        String formattedTotal = String.format("%,d VND", totalAmountFromIntent).replace(',', '.');
        String method = fromBankTransfer ? "Bank Transfer" : txtSimplePaymentMethod.getText().toString();
        String timeStr = fromBankTransfer ? time : new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(new java.util.Date());

        String title = getString(R.string.checkout_order_confirmed_title);
        String body = getString(R.string.checkout_order_confirmed_text, formattedTotal, method, timeStr);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "order_channel")
                .setSmallIcon(R.mipmap.ic_order)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1001, builder.build());

        NotificationItem item = new NotificationItem(
                NotificationItem.TYPE_NOTIFICATION,
                title,
                body,
                "Just now",
                System.currentTimeMillis()
        );
        NotificationStorage.saveNotification(this, currentCustomer.getEmail(), item);
    }
}
