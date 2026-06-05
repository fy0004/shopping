package com.example.shoppingsystem.model;

import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;

import java.util.List;

/**
 * 订单关联查询结果：Order + List<OrderItem>。
 */
public class OrderWithItems {

    private Order order;
    private List<OrderItem> items;

    public OrderWithItems() {}

    public OrderWithItems(Order order, List<OrderItem> items) {
        this.order = order;
        this.items = items;
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
