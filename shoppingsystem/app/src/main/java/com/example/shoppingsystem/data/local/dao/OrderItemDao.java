package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingsystem.data.local.entity.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {

    @Insert
    void insertAll(List<OrderItem> items);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    LiveData<List<OrderItem>> findByOrder(long orderId);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    List<OrderItem> findByOrderSync(long orderId);
}
