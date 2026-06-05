package com.example.shoppingsystem.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.repository.HomeRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final HomeRepository repository;
    private LiveData<List<Banner>> banners;
    private LiveData<List<Category>> categories;
    private LiveData<List<Product>> goodsList;

    public HomeViewModel() {
        repository = ShoppingApplication.getInstance().getHomeRepository();
        loadHomeData();
    }

    public void loadHomeData() {
        banners = repository.getBanners();
        categories = repository.getCategories();
        goodsList = repository.getHotProducts(20);
    }

    public LiveData<List<Banner>> getBanners() {
        return banners;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Product>> getGoodsList() {
        return goodsList;
    }
}
