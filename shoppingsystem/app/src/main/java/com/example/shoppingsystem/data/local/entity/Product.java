package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("category_id")})
public class Product {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "category_id")
    private long categoryId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "original_price")
    private double originalPrice;

    @ColumnInfo(name = "stock")
    private int stock;

    @ColumnInfo(name = "sales_count")
    private int salesCount;

    @ColumnInfo(name = "image_urls")
    private String imageUrls; // JSON array string

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "rating_count")
    private int ratingCount;

    @ColumnInfo(name = "is_on_sale")
    private boolean isOnSale;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public Product() {}

    public Product(long categoryId, String name, String description, double price, double originalPrice,
                   int stock, String imageUrls) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.stock = stock;
        this.salesCount = 0;
        this.imageUrls = imageUrls;
        this.rating = 5.0f;
        this.ratingCount = 0;
        this.isOnSale = true;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getSalesCount() { return salesCount; }
    public void setSalesCount(int salesCount) { this.salesCount = salesCount; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public boolean isOnSale() { return isOnSale; }
    public void setOnSale(boolean onSale) { isOnSale = onSale; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
