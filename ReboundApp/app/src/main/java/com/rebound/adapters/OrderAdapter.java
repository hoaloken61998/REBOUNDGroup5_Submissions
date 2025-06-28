package com.rebound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rebound.R;
import com.rebound.models.Orders.Order;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onDelete(Order order);
        void onBuyAgain(Order order);
        void onTrackOrderClicked(Order order);
        void onOrderReceived(Order order);
    }

    private final Context context;
    private final List<Order> orderList;
    private final String screenType;
    private final OnOrderClickListener listener;

    public OrderAdapter(Context context, List<Order> orderList, String screenType, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.screenType = screenType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        String status = screenType.equals("ongoing") ? "To Receive" : "Shipped";

        holder.txtStatus.setText(status);
        holder.txtTotal.setText("Total: " + order.getSubtotal());
        holder.groupToReceive.setVisibility(View.GONE);
        holder.groupShipped.setVisibility(View.GONE);
        holder.layoutOrderItems.removeAllViews();

        List<ProductItem> products = null;
        try {
            products = order.getProductList();
        } catch (Exception e) {
            // Ignore error temporarily
        }
        if (products != null) {
            for (ProductItem product : products) {
                View item = LayoutInflater.from(context).inflate(R.layout.item_product_order, holder.layoutOrderItems, false);
                ((TextView) item.findViewById(R.id.txtProductName)).setText(product.ProductName != null ? product.ProductName.toString() : "");
                ((TextView) item.findViewById(R.id.txtProductDesc)).setText(""); // Default value
                String priceText = product.ProductPrice != null ? product.ProductPrice.toString() : "";
                ((TextView) item.findViewById(R.id.txtProductPrice)).setText(priceText);
                // Load image from URL using Glide
                String imageUrl = product.ImageLink != null ? product.ImageLink.toString() : "";
                ImageView imgView = item.findViewById(R.id.imgProduct);
                if (!imageUrl.isEmpty()) {
                    Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(imgView);
                } else {
                    imgView.setImageResource(R.drawable.ic_placeholder);
                }
                holder.layoutOrderItems.addView(item);
            }
        }

        if (screenType.equals("ongoing")) {
            holder.groupToReceive.setVisibility(View.VISIBLE);

            boolean isToReceive = "To Receive".equals(order.getStatus());
            holder.btnOrderReceived.setEnabled(isToReceive);
            holder.btnOrderReceived.setBackgroundResource(isToReceive ? R.drawable.button_outline : R.drawable.button_disabled);
            holder.btnOrderReceived.setTextColor(ContextCompat.getColor(context, isToReceive ? R.color.outline_text : android.R.color.darker_gray));
            holder.btnOrderReceived.setOnClickListener(v -> {
                if (isToReceive && listener != null) listener.onOrderReceived(order);
            });

            holder.btnTrackOrder.setEnabled(true);
            holder.btnTrackOrder.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.accent_dark));
            holder.btnTrackOrder.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.btnTrackOrder.setOnClickListener(v -> {
                if (listener != null) listener.onTrackOrderClicked(order);
            });

        } else if (screenType.equals("completed")) {
            holder.groupShipped.setVisibility(View.VISIBLE);

            holder.btnDelete.setBackgroundResource(R.drawable.button_outline);
            holder.btnDelete.setTextColor(ContextCompat.getColor(context, R.color.outline_text));
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(order);
            });

            holder.btnBuyAgain.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.accent_dark));
            holder.btnBuyAgain.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.btnBuyAgain.setOnClickListener(v -> {
                if (listener != null) listener.onBuyAgain(order);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtStatus, txtTotal;
        LinearLayout layoutOrderItems, groupShipped, groupToReceive;
        Button btnDelete, btnBuyAgain, btnOrderReceived, btnTrackOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtTotal = itemView.findViewById(R.id.txtOrderItemTotal);
            layoutOrderItems = itemView.findViewById(R.id.layoutOrderItems);
            groupShipped = itemView.findViewById(R.id.groupShipped);
            groupToReceive = itemView.findViewById(R.id.groupToReceive);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnBuyAgain = itemView.findViewById(R.id.btnBuyAgain);
            btnOrderReceived = itemView.findViewById(R.id.btnOrderReceived);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }
    }
}
