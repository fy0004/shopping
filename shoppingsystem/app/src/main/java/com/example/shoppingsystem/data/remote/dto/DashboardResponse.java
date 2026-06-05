package com.example.shoppingsystem.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 管理后台仪表盘响应
 */
public class DashboardResponse {

    @SerializedName("totalOrders")
    private int totalOrders;

    @SerializedName("totalRevenue")
    private double totalRevenue;

    @SerializedName("totalUsers")
    private int totalUsers;

    @SerializedName("totalProducts")
    private int totalProducts;

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalProducts() { return totalProducts; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
}
