package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = {
                @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "order_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class, parentColumns = "id", childColumns = "product_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("order_id"), @Index("product_id")})
public class OrderItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "order_id")
    private long orderId;

    @ColumnInfo(name = "product_id")
    private long productId;

    @ColumnInfo(name = "product_name")
    private String productName; // snapshot

    @ColumnInfo(name = "product_image")
    private String productImage; // snapshot (first image URL)

    @ColumnInfo(name = "price")
    private double price; // snapshot

    @ColumnInfo(name = "quantity")
    private int quantity;

    public OrderItem() {}

    public OrderItem(long orderId, long productId, String productName, String productImage,
                     double price, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getOrderId() { return orderId; }
    public void setOrderId(long orderId) { this.orderId = orderId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
