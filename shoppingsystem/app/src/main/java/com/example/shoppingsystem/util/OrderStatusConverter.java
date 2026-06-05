package com.example.shoppingsystem.util;

import com.example.shoppingsystem.model.OrderStatus;

/**
 * 订单状态字符串与枚举之间的转换工具。
 */
public class OrderStatusConverter {

    public static String toString(OrderStatus status) {
        if (status == null) return OrderStatus.PENDING.name();
        return status.name();
    }

    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null) return OrderStatus.PENDING;
        try {
            return OrderStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            return OrderStatus.PENDING;
        }
    }

    /**
     * 获取订单状态的中文显示名称。
     */
    public static String toDisplayName(String statusStr) {
        OrderStatus status = fromString(statusStr);
        switch (status) {
            case PENDING:
                return "待付款";
            case PAID:
                return "待发货";
            case SHIPPED:
                return "待收货";
            case COMPLETED:
                return "已完成";
            case CANCELLED:
                return "已取消";
            default:
                return "未知";
        }
    }
}
