package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.CartDao;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.model.CartWithProduct;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartRepository {

    private final CartDao cartDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public CartRepository(AppDatabase database) {
        this.cartDao = database.cartDao();
    }

    public LiveData<List<CartWithProduct>> getCartItems(long userId) {
        return cartDao.findByUserWithProduct(userId);
    }

    public LiveData<Integer> getCartCount(long userId) {
        return cartDao.getCartCount(userId);
    }

    /**
     * 加入购物车：若已存在则增加数量，否则新建。
     */
    public void addToCart(long userId, long productId, int quantity, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                CartItem existing = cartDao.findByUserAndProduct(userId, productId);
                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    cartDao.update(existing);
                } else {
                    CartItem item = new CartItem(userId, productId, quantity);
                    cartDao.insert(item);
                }
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void updateQuantity(long itemId, int quantity, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                cartDao.updateQuantity(itemId, quantity);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void deleteItem(long itemId, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                CartItem item = new CartItem();
                item.setId(itemId);
                cartDao.delete(item);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void deleteByIds(List<Long> itemIds) {
        executor.execute(() -> cartDao.deleteByIds(itemIds));
    }

    public void toggleSelected(long itemId, boolean selected) {
        executor.execute(() -> cartDao.updateSelected(itemId, selected));
    }

    public void toggleAllSelected(long userId, boolean selected) {
        executor.execute(() -> cartDao.updateAllSelected(userId, selected));
    }

    /**
     * 获取用户选中的购物车项（同步，供下单用）。
     */
    public List<CartItem> getSelectedItemsSync(long userId) {
        return cartDao.findSelectedSync(userId);
    }

    /**
     * 删除用户选中的购物车项。
     */
    public void deleteSelectedItems(long userId) {
        executor.execute(() -> cartDao.deleteSelectedItems(userId));
    }

    // ========== Callback Interface ==========

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
