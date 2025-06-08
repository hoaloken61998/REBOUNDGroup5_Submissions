package com.rebound.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
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

public class ProductARAdapter extends RecyclerView.Adapter<ProductARAdapter.ViewHolder> {

    private final List<ProductItem> productList;
    private final Context context;

    public ProductARAdapter(List<ProductItem> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_ar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem product = productList.get(position);

        // Gán hình và tên
        holder.txtProductAR.setText(product.title);
        holder.imgProductAR.setImageResource(product.imageRes);

        // Tính toán độ rộng item nếu sản phẩm <= 4
        int totalItem = getItemCount();
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();

        if (totalItem > 0 && totalItem <= 4) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            layoutParams.width = screenWidth / totalItem;
        } else {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductAR;
        TextView txtProductAR;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductAR = itemView.findViewById(R.id.imgProductAR);
            txtProductAR = itemView.findViewById(R.id.txtProductAR);
        }
    }
}