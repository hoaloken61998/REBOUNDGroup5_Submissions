package com.rebound.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.rebound.R;
import com.rebound.adapters.OrderAdapter;
import com.rebound.models.Customer.Customer;
import com.rebound.models.Orders.Order;
import com.rebound.models.Cart.ProductItem;
import com.rebound.utils.OrderManager;
import com.rebound.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class OngoingFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Order> orderList;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);
        recyclerView = view.findViewById(R.id.recyclerOngoingOrders);

        orderList = new ArrayList<>();

        adapter = new OrderAdapter(getContext(), orderList, "ongoing", new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onDelete(Order order) {
                // Không dùng trong ongoing
            }

            @Override
            public void onBuyAgain(Order order) {
                // Không dùng trong ongoing
            }

            @Override
            public void onTrackOrderClicked(Order order) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, new TrackOrderFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onOrderReceived(Order order) {
                order.setStatus("Shipped");

                OrderManager.getInstance().updateOrderStatus(order); // 🔥 Lưu vào SharedPreferences

                orderList.remove(order);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadOngoingOrders();
        return view;
    }

    private void loadOngoingOrders() {
        Customer current = SharedPrefManager.getCurrentCustomer(getContext());
        if (current != null) {
            OrderManager.getInstance().setUserEmail(current.getEmail());
            List<Order> allOrders = OrderManager.getInstance().getOrders();

            orderList.clear(); // Xoá cũ
            for (Order o : allOrders) {
                if ("To Receive".equals(o.getStatus())) {
                    orderList.add(o);
                }
            }


            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadOngoingOrders();
    }
}
