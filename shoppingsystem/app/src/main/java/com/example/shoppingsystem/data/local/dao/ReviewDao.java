package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingsystem.data.local.entity.Review;
import com.example.shoppingsystem.model.ReviewWithUser;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert
    void insert(Review review);

    @Query("SELECT reviews.*, users.nickname AS userName, users.avatar_url AS userAvatar " +
            "FROM reviews INNER JOIN users ON reviews.user_id = users.id " +
            "WHERE reviews.product_id = :productId ORDER BY reviews.created_at DESC")
    LiveData<List<ReviewWithUser>> findByProductWithUser(long productId);

    @Query("SELECT reviews.*, users.nickname AS userName, users.avatar_url AS userAvatar " +
            "FROM reviews INNER JOIN users ON reviews.user_id = users.id " +
            "WHERE reviews.user_id = :userId ORDER BY reviews.created_at DESC")
    LiveData<List<ReviewWithUser>> findByUser(long userId);

    @Query("SELECT COUNT(*) FROM reviews WHERE product_id = :productId AND order_id = :orderId")
    int countByProductAndOrder(long productId, long orderId);
}
