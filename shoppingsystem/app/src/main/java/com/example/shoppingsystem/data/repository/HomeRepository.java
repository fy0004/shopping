package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.BannerDao;
import com.example.shoppingsystem.data.local.dao.CategoryDao;
import com.example.shoppingsystem.data.local.dao.ProductDao;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;

import java.util.List;

public class HomeRepository {

    private final BannerDao bannerDao;
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    public HomeRepository(AppDatabase database) {
        this.bannerDao = database.bannerDao();
        this.categoryDao = database.categoryDao();
        this.productDao = database.productDao();
    }

    public LiveData<List<Banner>> getBanners() {
        return bannerDao.findAll();
    }

    public LiveData<List<Category>> getCategories() {
        return categoryDao.findAll();
    }

    public LiveData<List<Product>> getHotProducts(int limit) {
        return productDao.findHotProducts(limit);
    }

    public LiveData<List<Product>> getAllOnSaleProducts() {
        return productDao.findAllOnSale();
    }
}
