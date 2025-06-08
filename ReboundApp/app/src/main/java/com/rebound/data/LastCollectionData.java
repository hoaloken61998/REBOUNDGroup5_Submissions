package com.rebound.data;
import com.rebound.R;
import com.rebound.models.Main.LastCollectionItem;
import java.util.ArrayList;
import java.util.List;

public class LastCollectionData {
    public static List<LastCollectionItem> getItems() {
        List<LastCollectionItem> list = new ArrayList<>();
        list.add(new LastCollectionItem("Top 14kt Eternal Cz Buddha", "1.950.000 VND", R.mipmap.lastcollection1));
        list.add(new LastCollectionItem("Top 14kt Crux Xanh Ember", "12.400.000 VND", R.mipmap.lastcollection2));
        list.add(new LastCollectionItem("Top 14kt Celestial Diamond", "6.480.000 VND", R.mipmap.lastcollection3));
        return list;
    }
}

