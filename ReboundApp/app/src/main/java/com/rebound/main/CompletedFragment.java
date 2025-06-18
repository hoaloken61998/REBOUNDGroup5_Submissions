package com.rebound.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rebound.R;
import com.rebound.adapters.OrderAdapter;
import com.rebound.models.Cart.ProductItem;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Orders.Order;
import com.rebound.models.Orders.Product;
import com.rebound.utils.CartManager;
import com.rebound.utils.OrderManager;
import com.rebound.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class CompletedFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private final List<Order> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        recyclerView = view.findViewById(R.id.recyclerCompletedOrders);

        adapter = new OrderAdapter(getContext(), orderList, "completed", new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onDelete(Order order) {
                new MaterialAlertDialogBuilder(getContext(), R.style.CustomDialogStyle)
                        .setTitle("Delete Order")
                        .setMessage("Are you sure you want to delete this order?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            orderList.remove(order);
                            OrderManager.getInstance().deleteOrder(order);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Order deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onBuyAgain(Order order) {
                for (Product product : order.getProductList()) {
                    String variant = product.getVariant() != null ? product.getVariant() : "Default";

                    // Nếu bạn muốn giả lập rating từ variant, parse số hoặc dùng mặc định
                    String fakeRating = "4.5";  // ví dụ mặc định nếu variant không hợp lệ

                    try {
                        Float.parseFloat(variant);  // kiểm tra nếu variant là số
                        fakeRating = variant;
                    } catch (NumberFormatException ignored) {}

                    ProductItem item = new ProductItem(
                            product.getName(),          // title
                            product.getPrice(),         // price
                            product.getImageResId(),    // image
                            fakeRating,                 // rating
                            "0 SOLD",                   // sold
                            "",                         // desc
                            product.getImageResId(),    // image1
                            product.getImageResId()     // image2
                    );

                    CartManager.getInstance().addToCart(item);
                }

                Toast.makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTrackOrderClicked(Order order) {
                // Không áp dụng
            }

            @Override
            public void onOrderReceived(Order order) {
                // Không áp dụng
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadCompletedOrders();

        return view;
    }

    private void loadCompletedOrders() {
        Customer current = SharedPrefManager.getCurrentCustomer(getContext());
        if (current != null) {
            OrderManager.getInstance().setUserEmail(current.getEmail());
            List<Order> allOrders = OrderManager.getInstance().getOrders();

            orderList.clear();
            for (Order o : allOrders) {
                if ("Shipped".equals(o.getStatus())) {
                    orderList.add(o);
                }
            }

            adapter.notifyDataSetChanged();
        }
    }
}
