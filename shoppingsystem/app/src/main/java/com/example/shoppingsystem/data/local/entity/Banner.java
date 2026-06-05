package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "banners")
public class Banner {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "link_type")
    private String linkType; // "PRODUCT", "CATEGORY", or "NONE"

    @ColumnInfo(name = "link_value")
    private long linkValue; // productId or categoryId

    @ColumnInfo(name = "sort_order")
    private int sortOrder;

    public Banner() {}

    public Banner(String imageUrl, String linkType, long linkValue, int sortOrder) {
        this.imageUrl = imageUrl;
        this.linkType = linkType;
        this.linkValue = linkValue;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLinkType() { return linkType; }
    public void setLinkType(String linkType) { this.linkType = linkType; }

    public long getLinkValue() { return linkValue; }
    public void setLinkValue(long linkValue) { this.linkValue = linkValue; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
