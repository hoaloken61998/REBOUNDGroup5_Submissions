package com.rebound.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.main.ProductDetailActivity;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductItem> list;

    public ProductAdapter(List<ProductItem> list) {
        this.list = list;
    }

    public void updateList(List<ProductItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductItem item = list.get(position);
        holder.title.setText(item.title);
        holder.price.setText(item.price);
        holder.rating.setText(item.rating);
        holder.image.setImageResource(item.imageRes);

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", item); // Gửi object đã Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgProduct);
            title = itemView.findViewById(R.id.txtProduct);
            price = itemView.findViewById(R.id.txtProductPrice);
            rating = itemView.findViewById(R.id.txtProductRating);
        }
    }
}
