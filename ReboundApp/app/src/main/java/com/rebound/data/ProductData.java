package com.rebound.data;

import com.rebound.R;
import com.rebound.models.Cart.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class ProductData {

    public static List<ProductItem> getNecklaceList() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("Pearl Necklace", "250.000 VND", R.mipmap.lastcollection2, "4.5", "132 SOLD", "Elegant pearl necklace made from freshwater pearls.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Gold Choker", "300.000 VND", R.mipmap.lastcollection2, "4.7", "85 SOLD", "Stylish gold choker with 18K plated stainless steel.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Layered Chain", "180.000 VND", R.mipmap.lastcollection2, "4.2", "203 SOLD", "Modern layered chain for casual or evening wear.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        return list;
    }

    public static List<ProductItem> getEarringList() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("Tote Earrings", "150.000 VND", R.mipmap.lastcollection2, "4.3", "423 SOLD", "Crafted in recycled sterling silver with unique trapeze shape.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Hoop Earrings", "150.000 VND", R.mipmap.lastcollection2, "4.3", "312 SOLD", "Classic lightweight hoop earrings with minimalist design.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Crystal Studs", "120.000 VND", R.mipmap.lastcollection2, "4.6", "570 SOLD", "Elegant crystal studs perfect for daily wear.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        return list;
    }

    public static List<ProductItem> getRingList() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("Silver Band", "130.000 VND", R.mipmap.lastcollection2, "4.4", "267 SOLD", "Timeless silver band for solo or stacking wear.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Gold Statement Ring", "220.000 VND", R.mipmap.lastcollection2, "4.8", "198 SOLD", "Bold ring with intricate detailing.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Couple Rings", "200.000 VND", R.mipmap.lastcollection2, "4.5", "341 SOLD", "Matching rings symbolizing love and connection.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        return list;
    }

    public static List<ProductItem> getBodyPiercingList() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem("Nose Ring", "100.000 VND", R.mipmap.lastcollection2, "4.1", "289 SOLD", "Elegant hypoallergenic nose ring.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Belly Button Ring", "170.000 VND", R.mipmap.lastcollection2, "4.3", "156 SOLD", "Sparkling jewel belly ring with secure clasp.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        list.add(new ProductItem("Eyebrow Piercing", "150.000 VND", R.mipmap.lastcollection2, "4.0", "112 SOLD", "Curved titanium eyebrow barbell.", R.mipmap.lastcollection2, R.mipmap.lastcollection2));
        return list;
    }
    public static List<ProductItem> getLastCollectionList() {
        List<ProductItem> list = new ArrayList<>();
        list.add(new ProductItem(
                "Top 14kt Eternal Cz Buddha",
                "1.950.000 VND",
                R.mipmap.lastcollection1,
                "4.9",
                "87 SOLD",
                "An elegant pendant crafted in 14kt gold with a CZ Buddha design symbolizing peace and eternity.",
                R.mipmap.lastcollection1,
                R.mipmap.lastcollection1
        ));
        list.add(new ProductItem(
                "Top 14kt Crux Xanh Ember",
                "12.400.000 VND",
                R.mipmap.lastcollection2,
                "4.8",
                "42 SOLD",
                "Crux Xanh Ember necklace blends 14kt gold with deep green stones for a celestial glow.",
                R.mipmap.lastcollection2,
                R.mipmap.lastcollection2
        ));
        list.add(new ProductItem(
                "Top 14kt Celestial Diamond",
                "6.480.000 VND",
                R.mipmap.lastcollection3,
                "5.0",
                "103 SOLD",
                "A breathtaking celestial diamond piece designed for timeless elegance and grace.",
                R.mipmap.lastcollection3,
                R.mipmap.lastcollection1
        ));

        return list;
    }
    public static List<ProductItem> getAllProducts() {
        List<ProductItem> all = new ArrayList<>();
        all.addAll(getNecklaceList());
        all.addAll(getEarringList());
        all.addAll(getRingList());
        all.addAll(getBodyPiercingList());
        all.addAll(getLastCollectionList());
        return all;
    }
}
