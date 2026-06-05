package com.example.shoppingsystem.data.remote.dto;

import com.example.shoppingsystem.data.local.entity.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 商品分页列表响应
 */
public class ProductListResponse {

    @SerializedName("list")
    private List<Product> list;

    @SerializedName("total")
    private int total;

    @SerializedName("page")
    private int page;

    @SerializedName("size")
    private int size;

    public List<Product> getList() { return list; }
    public void setList(List<Product> list) { this.list = list; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}
