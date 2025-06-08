package com.rebound.models.Main;

public class NotificationItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NOTIFICATION = 1;

    private int type;
    private String title;
    private String message;
    private String time;

    public NotificationItem(int type, String title, String message, String time) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
