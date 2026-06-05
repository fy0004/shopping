package com.example.shoppingsystem.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.repository.HomeRepository;
import com.example.shoppingsystem.data.repository.ProductRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private final HomeRepository homeRepository;

    public CategoryViewModel() {
        productRepository = ShoppingApplication.getInstance().getProductRepository();
        homeRepository = ShoppingApplication.getInstance().getHomeRepository();
    }

    public LiveData<List<Category>> getCategories() {
        return homeRepository.getCategories();
    }

    /**
     * 直接返回 Room LiveData，由 Fragment 直接 observe。
     * 不再使用 MediatorLiveData，避免 observe 延迟问题。
     */
    public LiveData<List<Product>> getProductsByCategory(long categoryId) {
        return productRepository.getProductsByCategory(categoryId);
    }
}
