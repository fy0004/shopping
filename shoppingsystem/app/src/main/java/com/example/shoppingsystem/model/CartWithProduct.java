package com.example.shoppingsystem.model;

import androidx.room.ColumnInfo;

/**
 * 购物车关联查询结果：CartItem JOIN Product。
 */
public class CartWithProduct {

    // CartItem fields
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "product_id")
    private long productId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "is_selected")
    private boolean isSelected;

    // Product fields (from JOIN)
    @ColumnInfo(name = "productName")
    private String productName;

    @ColumnInfo(name = "productPrice")
    private double productPrice;

    @ColumnInfo(name = "productImage")
    private String productImage;

    @ColumnInfo(name = "productStock")
    private int productStock;

    public CartWithProduct() {}

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public int getProductStock() { return productStock; }
    public void setProductStock(int productStock) { this.productStock = productStock; }

    /**
     * 获取第一张图片 URL。
     */
    public String getFirstImage() {
        if (productImage == null || productImage.isEmpty()) return "";
        try {
            String cleaned = productImage.replace("[", "").replace("]", "").replace("\"", "");
            String[] urls = cleaned.split(",");
            return urls.length > 0 ? urls[0].trim() : "";
        } catch (Exception e) {
            return productImage;
        }
    }

    /**
     * 计算小计价格。
     */
    public double getSubtotal() {
        return productPrice * quantity;
    }
}
