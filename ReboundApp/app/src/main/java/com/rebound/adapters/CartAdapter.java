package com.rebound.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final List<ProductItem> cartList;
    private final Runnable updateTotal;
    private final boolean isReadOnly;
    private final Context context; // ✅ Thêm context để hiển thị AlertDialog

    public CartAdapter(List<ProductItem> cartList, Runnable updateTotal, boolean isReadOnly) {
        this.cartList = cartList;
        this.updateTotal = updateTotal;
        this.isReadOnly = isReadOnly;
        this.context = null; // fallback nếu không dùng context
    }

    public CartAdapter(List<ProductItem> cartList, Runnable updateTotal, boolean isReadOnly, Context context) {
        this.cartList = cartList;
        this.updateTotal = updateTotal;
        this.isReadOnly = isReadOnly;
        this.context = context;
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
        holder.variant.setText(item.variant);
        if (item.variant != null && item.variant.equals("Gold")) {
            holder.image.setImageResource(item.imageGoldRes);
        } else {
            holder.image.setImageResource(item.imageSilverRes);
        }

        int unitPrice = extractPrice(item.price);
        int totalPrice = unitPrice * item.quantity;
        holder.price.setText(String.format("%,d VND", totalPrice).replace(',', '.'));

        if (isReadOnly) {
            holder.btnPlus.setVisibility(View.GONE);
            holder.btnMinus.setVisibility(View.GONE);
            holder.quantity.setText("Qty: " + item.quantity);
        } else {
            holder.btnPlus.setVisibility(View.VISIBLE);
            holder.btnMinus.setVisibility(View.VISIBLE);
            holder.quantity.setText(String.valueOf(item.quantity));

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
                } else {
                    // Số lượng = 1 → hỏi có muốn xóa không
                    new androidx.appcompat.app.AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Remove item")
                            .setMessage("Do you want to remove this item from your cart?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Xóa trong CartManager
                                com.rebound.utils.CartManager.getInstance().removeFromCart(item);

                                // Xóa trong adapter
                                cartList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, cartList.size());

                                if (updateTotal != null) updateTotal.run();
                            })
                            .setNegativeButton("No", null)
                            .show();
                    // màu nút

                }
            });

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.quantity.getLayoutParams();
            if (isReadOnly) {
                params.setMarginStart(0);
                params.setMarginEnd(0);
            } else {
                params.setMarginStart(40);
                params.setMarginEnd(40);
            }
            holder.quantity.setLayoutParams(params);
        }
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
