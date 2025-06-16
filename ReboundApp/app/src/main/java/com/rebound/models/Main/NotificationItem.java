package com.rebound.models.Main;

public class NotificationItem {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NOTIFICATION = 1;
    public static final int TYPE_ITEM = TYPE_NOTIFICATION; // Alias để không bị lỗi nếu dùng TYPE_ITEM

    private int type;
    private String title;
    private String content;
    private String timeAgo;
    private long timestamp;

    // Constructor cho header
    public NotificationItem(int type, String title, String content, String timeAgo) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timeAgo = timeAgo;
    }

    // Constructor cho notification
    public NotificationItem(int type, String title, String content, String timeAgo, long timestamp) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.timeAgo = timeAgo;
        this.timestamp = timestamp;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
