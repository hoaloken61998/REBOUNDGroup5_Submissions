package com.rebound.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final List<ProductItem> cartList;
    private final Runnable updateTotal;

    public CartAdapter(List<ProductItem> cartList, Runnable updateTotal) {
        this.cartList = cartList;
        this.updateTotal = updateTotal;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, variant, price, quantity;
        ImageView btnPlus, btnMinus, image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtProductName);
            variant = itemView.findViewById(R.id.txtProductVariant);
            price = itemView.findViewById(R.id.txtProductPrice);
            quantity = itemView.findViewById(R.id.txtProductQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            image = itemView.findViewById(R.id.imgProduct);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem item = cartList.get(position);

        holder.name.setText(item.title);
        holder.variant.setText(item.description); // Hiển thị mô tả/màu sắc
        holder.quantity.setText(String.valueOf(item.quantity));

        int unitPrice = extractPrice(item.price);
        int totalPrice = unitPrice * item.quantity;
        holder.price.setText(String.format("%,d VND", totalPrice).replace(',', '.'));
        holder.image.setImageResource(item.imageRes);

        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            notifyItemChanged(position);
            if (updateTotal != null) updateTotal.run();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                notifyItemChanged(position);
                if (updateTotal != null) updateTotal.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private int extractPrice(String priceString) {
        try {
            return Integer.parseInt(priceString.replace(".", "").replace(" VND", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
