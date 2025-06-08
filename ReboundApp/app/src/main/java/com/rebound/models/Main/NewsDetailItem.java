
package com.rebound.models.Main;

public class NewsDetailItem {
    private String title;
    private String description;
    private String date;
    private int image;

    // Constructor
    public NewsDetailItem(String title, String description, String date, int image) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
    }

    // Getter
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }
}

