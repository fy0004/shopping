package com.example.shoppingsystem.model;

import androidx.room.ColumnInfo;

/**
 * 评价关联查询结果：Review JOIN User。
 */
public class ReviewWithUser {

    // Review fields
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "product_id")
    private long productId;

    @ColumnInfo(name = "order_id")
    private long orderId;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // User fields (from JOIN)
    @ColumnInfo(name = "userName")
    private String userName;

    @ColumnInfo(name = "userAvatar")
    private String userAvatar;

    public ReviewWithUser() {}

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}
