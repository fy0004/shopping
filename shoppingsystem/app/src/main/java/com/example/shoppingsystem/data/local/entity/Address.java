package com.example.shoppingsystem.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "addresses",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("user_id")})
public class Address {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "receiver_name")
    private String receiverName;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "province")
    private String province;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "district")
    private String district;

    @ColumnInfo(name = "detail")
    private String detail;

    @ColumnInfo(name = "is_default")
    private boolean isDefault;

    public Address() {}

    public Address(long userId, String receiverName, String phone, String province,
                   String city, String district, String detail, boolean isDefault) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.phone = phone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    /**
     * 返回完整的地址字符串。
     */
    public String getFullAddress() {
        return province + " " + city + " " + district + " " + detail;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
