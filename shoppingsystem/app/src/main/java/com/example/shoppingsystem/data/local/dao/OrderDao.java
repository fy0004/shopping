package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingsystem.data.local.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    LiveData<Order> findById(long orderId);

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    Order findByIdSync(long orderId);

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    LiveData<List<Order>> findByUserAndStatus(long userId, String status);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<Order>> findByUser(long userId);

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY created_at DESC")
    LiveData<List<Order>> findByStatus(String status);

    @Query("SELECT * FROM orders ORDER BY created_at DESC")
    LiveData<List<Order>> findAll();

    @Query("UPDATE orders SET status = :status, updated_at = :updatedAt WHERE id = :orderId")
    void updateStatus(long orderId, String status, long updatedAt);

    @Query("SELECT COUNT(*) FROM orders")
    LiveData<Integer> getTotalCount();

    @Query("SELECT COUNT(*) FROM orders WHERE status = :status")
    LiveData<Integer> countByStatus(String status);

    @Query("SELECT SUM(total_amount) FROM orders WHERE status = 'COMPLETED'")
    LiveData<Double> getTotalRevenue();
}
