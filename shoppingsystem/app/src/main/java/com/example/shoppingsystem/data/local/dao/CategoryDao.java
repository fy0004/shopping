package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingsystem.data.local.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insertAll(List<Category> categories);

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    LiveData<List<Category>> findAll();

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    List<Category> findAllSync();
}
