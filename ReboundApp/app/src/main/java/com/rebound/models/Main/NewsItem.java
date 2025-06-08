package com.rebound.models.Main;

public class NewsItem {
    private String title;
    private String subtitle;
    private String date;
    private int imageResId;

    public NewsItem(String title, String subtitle, String date, int imageResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDate() { return date; }
    public int getImageResId() { return imageResId; }
}
