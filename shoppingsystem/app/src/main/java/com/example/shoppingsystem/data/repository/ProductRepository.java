package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.ProductDao;
import com.example.shoppingsystem.data.local.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {

    private final ProductDao productDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProductRepository(AppDatabase database) {
        this.productDao = database.productDao();
    }

    public LiveData<Product> getProductById(long productId) {
        return productDao.findById(productId);
    }

    public Product getProductByIdSync(long productId) {
        return productDao.findByIdSync(productId);
    }

    public LiveData<List<Product>> getProductsByCategory(long categoryId) {
        return productDao.findByCategory(categoryId);
    }

    public LiveData<List<Product>> searchProducts(String keyword) {
        return productDao.search(keyword);
    }

    public LiveData<List<Product>> getHotProducts(int limit) {
        return productDao.findHotProducts(limit);
    }

    public LiveData<List<Product>> getAllOnSaleProducts() {
        return productDao.findAllOnSale();
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.findAll();
    }

    public void decreaseStock(long productId, int delta, OnResultCallback<Boolean> callback) {
        executor.execute(() -> {
            int rows = productDao.decreaseStock(productId, delta);
            if (callback != null) callback.onSuccess(rows > 0);
        });
    }

    public void increaseStock(long productId, int delta) {
        executor.execute(() -> productDao.increaseStock(productId, delta));
    }

    public void increaseSalesCount(long productId, int delta) {
        executor.execute(() -> productDao.increaseSalesCount(productId, delta));
    }

    // ========== Admin operations ==========

    public void insert(Product product, OnResultCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = productDao.insert(product);
                if (callback != null) callback.onSuccess(id);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void update(Product product, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                productDao.update(product);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void delete(Product product, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                productDao.delete(product);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    // ========== Callback Interface ==========

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
