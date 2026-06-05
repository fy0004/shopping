package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingsystem.data.local.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    LiveData<Product> findById(long productId);

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    Product findByIdSync(long productId);

    @Query("SELECT * FROM products WHERE category_id = :categoryId AND is_on_sale = 1 ORDER BY sales_count DESC")
    LiveData<List<Product>> findByCategory(long categoryId);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' AND is_on_sale = 1 ORDER BY sales_count DESC")
    LiveData<List<Product>> search(String keyword);

    @Query("SELECT * FROM products WHERE is_on_sale = 1 ORDER BY sales_count DESC LIMIT :limit")
    LiveData<List<Product>> findHotProducts(int limit);

    @Query("SELECT * FROM products WHERE is_on_sale = 1 ORDER BY created_at DESC")
    LiveData<List<Product>> findAllOnSale();

    @Query("SELECT * FROM products ORDER BY created_at DESC")
    LiveData<List<Product>> findAll();

    @Query("UPDATE products SET stock = stock - :delta WHERE id = :productId AND stock >= :delta")
    int decreaseStock(long productId, int delta);

    @Query("UPDATE products SET stock = stock + :delta WHERE id = :productId")
    void increaseStock(long productId, int delta);

    @Query("UPDATE products SET sales_count = sales_count + :delta WHERE id = :productId")
    void increaseSalesCount(long productId, int delta);
}
