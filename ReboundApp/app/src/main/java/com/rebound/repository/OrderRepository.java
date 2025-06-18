package com.rebound.repository;



import com.rebound.models.Orders.TimelineStep;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public List<TimelineStep> getTimelineSteps(String orderId) {
        List<TimelineStep> steps = new ArrayList<>();

        // TODO: Truy xuất từ DB hoặc API theo orderId
        steps.add(new TimelineStep("Order Placed", "30 June, 2025", false));
        steps.add(new TimelineStep("Scheduled for pick-up", "01 July, 2025", false));
        steps.add(new TimelineStep("Picked up by courier", "02 July, 2025", false));
        steps.add(new TimelineStep("Delivery started", "03 July, 2025", false));
        steps.add(new TimelineStep("Out for delivery", "04 July, 2025", true));

        return steps;
    }

    public String getEstimatedDate(String orderId) {
        return "05 July, 2025"; // hoặc lấy từ DB
    }

    public String getCity(String orderId) {
        return "Ho Chi Minh, Viet Nam";
    }

    public String getFullAddress(String orderId) {
        return "10 Nguyen Van Dau, Ward 5, Phu Nhuan District";
    }

    public String getPhone(String orderId) {
        return "0999999999";
    }
}
