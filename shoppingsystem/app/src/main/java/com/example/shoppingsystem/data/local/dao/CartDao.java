package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.model.CartWithProduct;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    long insert(CartItem item);

    @Update
    void update(CartItem item);

    @Delete
    void delete(CartItem item);

    @Query("DELETE FROM cart_items WHERE id IN (:itemIds)")
    void deleteByIds(List<Long> itemIds);

    @Query("SELECT * FROM cart_items WHERE user_id = :userId AND product_id = :productId LIMIT 1")
    CartItem findByUserAndProduct(long userId, long productId);

    @Query("SELECT cart_items.*, products.name AS productName, products.price AS productPrice, " +
            "products.image_urls AS productImage, products.stock AS productStock " +
            "FROM cart_items INNER JOIN products ON cart_items.product_id = products.id " +
            "WHERE cart_items.user_id = :userId ORDER BY cart_items.id DESC")
    LiveData<List<CartWithProduct>> findByUserWithProduct(long userId);

    @Query("SELECT * FROM cart_items WHERE user_id = :userId AND is_selected = 1")
    List<CartItem> findSelectedSync(long userId);

    @Query("UPDATE cart_items SET is_selected = :selected WHERE user_id = :userId")
    void updateAllSelected(long userId, boolean selected);

    @Query("UPDATE cart_items SET is_selected = :selected WHERE id = :itemId")
    void updateSelected(long itemId, boolean selected);

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :itemId")
    void updateQuantity(long itemId, int quantity);

    @Query("DELETE FROM cart_items WHERE user_id = :userId AND is_selected = 1")
    void deleteSelectedItems(long userId);

    @Query("SELECT COUNT(*) FROM cart_items WHERE user_id = :userId")
    LiveData<Integer> getCartCount(long userId);
}
