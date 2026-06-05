package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("user_id")})
public class Order {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "order_no")
    private String orderNo;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "address_snapshot")
    private String addressSnapshot; // JSON snapshot of the address

    @ColumnInfo(name = "status")
    private String status; // PENDING, PAID, SHIPPED, COMPLETED, CANCELLED

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "payment_method")
    private String paymentMethod; // "ALIPAY" or "WECHAT" or null

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Order() {}

    public Order(String orderNo, long userId, String addressSnapshot, String status,
                 double totalAmount) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.addressSnapshot = addressSnapshot;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getAddressSnapshot() { return addressSnapshot; }
    public void setAddressSnapshot(String addressSnapshot) { this.addressSnapshot = addressSnapshot; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
