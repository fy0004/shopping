package com.example.shoppingsystem.model;

/**
 * 订单状态枚举。
 */
public enum OrderStatus {
    PENDING,    // 待付款
    PAID,       // 待发货（已付款）
    SHIPPED,    // 待收货（已发货）
    COMPLETED,  // 已完成
    CANCELLED   // 已取消
}
