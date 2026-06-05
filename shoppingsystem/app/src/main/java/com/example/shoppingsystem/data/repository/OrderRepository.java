package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.OrderDao;
import com.example.shoppingsystem.data.local.dao.OrderItemDao;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.DateUtils;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    public OrderRepository(AppDatabase database) {
        this.database = database;
        this.orderDao = database.orderDao();
        this.orderItemDao = database.orderItemDao();
    }

    /**
     * 创建订单（事务：插入订单 + 批量插入订单明细 + 删除购物车对应项 + 减少库存）。
     */
    public void createOrder(long userId, Address address, List<CartItem> selectedItems,
                            String paymentMethod, OnResultCallback<Long> callback) {
        executor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    // 1. 计算总价
                    double total = 0;
                    List<OrderItem> orderItems = new ArrayList<>();
                    for (CartItem ci : selectedItems) {
                        Product p = database.productDao().findByIdSync(ci.getProductId());
                        if (p == null) throw new RuntimeException("商品不存在");
                        if (p.getStock() < ci.getQuantity()) {
                            throw new RuntimeException(p.getName() + " 库存不足");
                        }
                        double itemTotal = p.getPrice() * ci.getQuantity();
                        total += itemTotal;
                        orderItems.add(new OrderItem(
                                0, ci.getProductId(),
                                p.getName(),
                                ImageUrlUtil.getFirstImage(p.getImageUrls()),
                                p.getPrice(),
                                ci.getQuantity()
                        ));
                    }

                    // 2. 创建订单
                    String orderNo = DateUtils.generateOrderNo();
                    String addressJson = gson.toJson(address);
                    Order order = new Order(orderNo, userId, addressJson, "PENDING", total);
                    order.setPaymentMethod(paymentMethod);
                    long orderId = database.orderDao().insert(order);

                    // 3. 插入订单明细
                    for (OrderItem oi : orderItems) {
                        oi.setOrderId(orderId);
                    }
                    database.orderItemDao().insertAll(orderItems);

                    // 4. 删除购物车选中项 + 减库存
                    for (CartItem ci : selectedItems) {
                        database.cartDao().delete(ci);
                        database.productDao().decreaseStock(ci.getProductId(), ci.getQuantity());
                    }
                });

                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public LiveData<Order> getOrderById(long orderId) {
        return orderDao.findById(orderId);
    }

    public Order getOrderByIdSync(long orderId) {
        return orderDao.findByIdSync(orderId);
    }

    public LiveData<List<Order>> getOrdersByUser(long userId) {
        return orderDao.findByUser(userId);
    }

    public LiveData<List<Order>> getOrdersByUserAndStatus(long userId, String status) {
        return orderDao.findByUserAndStatus(userId, status);
    }

    public LiveData<List<OrderItem>> getOrderItems(long orderId) {
        return orderItemDao.findByOrder(orderId);
    }

    /**
     * 更新订单状态。
     */
    public void updateOrderStatus(long orderId, String status, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                orderDao.updateStatus(orderId, status, System.currentTimeMillis());
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 支付订单：更新状态为 PAID。
     */
    public void payOrder(long orderId, String paymentMethod, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                Order order = orderDao.findByIdSync(orderId);
                if (order == null) {
                    if (callback != null) callback.onError("订单不存在");
                    return;
                }
                order.setStatus("PAID");
                order.setPaymentMethod(paymentMethod);
                order.setUpdatedAt(System.currentTimeMillis());
                orderDao.update(order);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 取消订单：恢复库存。
     */
    public void cancelOrder(long orderId, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    Order order = orderDao.findByIdSync(orderId);
                    if (order == null) throw new RuntimeException("订单不存在");
                    if (!"PENDING".equals(order.getStatus())) {
                        throw new RuntimeException("只能取消待付款的订单");
                    }
                    // 恢复库存
                    List<OrderItem> items = orderItemDao.findByOrderSync(orderId);
                    for (OrderItem oi : items) {
                        database.productDao().increaseStock(oi.getProductId(), oi.getQuantity());
                    }
                    orderDao.updateStatus(orderId, "CANCELLED", System.currentTimeMillis());
                });
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 确认收货。
     */
    public void confirmReceipt(long orderId, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    Order order = orderDao.findByIdSync(orderId);
                    if (order == null) throw new RuntimeException("订单不存在");
                    if (!"SHIPPED".equals(order.getStatus())) {
                        throw new RuntimeException("只能确认已发货的订单");
                    }
                    orderDao.updateStatus(orderId, "COMPLETED", System.currentTimeMillis());
                    // 增加销量
                    List<OrderItem> items = orderItemDao.findByOrderSync(orderId);
                    for (OrderItem oi : items) {
                        database.productDao().increaseSalesCount(oi.getProductId(), oi.getQuantity());
                    }
                });
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    // ========== Admin operations ==========

    public LiveData<List<Order>> getAllOrders() {
        return orderDao.findAll();
    }

    public LiveData<List<Order>> getOrdersByStatus(String status) {
        return orderDao.findByStatus(status);
    }

    public LiveData<Integer> getTotalCount() {
        return orderDao.getTotalCount();
    }

    public LiveData<Double> getTotalRevenue() {
        return orderDao.getTotalRevenue();
    }

    // ========== Callback Interface ==========

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
