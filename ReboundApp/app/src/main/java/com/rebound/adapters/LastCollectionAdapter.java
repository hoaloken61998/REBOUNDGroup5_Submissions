package com.rebound.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.models.Main.LastCollectionItem;

import java.util.List;

public class LastCollectionAdapter extends RecyclerView.Adapter<LastCollectionAdapter.ViewHolder> {

    private List<LastCollectionItem> itemList;

    public LastCollectionAdapter(List<LastCollectionItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_last_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LastCollectionItem item = itemList.get(position);
        holder.imgLastCollection.setImageResource(item.getImageResId());
        holder.txtLastCollectionName.setText(item.getTitle());
        holder.txtLastCollectionPrice.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLastCollection;
        TextView txtLastCollectionName, txtLastCollectionPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLastCollection = itemView.findViewById(R.id.imgLastCollection);
            txtLastCollectionName = itemView.findViewById(R.id.txtLastCollectionName);
            txtLastCollectionPrice = itemView.findViewById(R.id.txtLastCollectionPrice);
        }
    }
}
