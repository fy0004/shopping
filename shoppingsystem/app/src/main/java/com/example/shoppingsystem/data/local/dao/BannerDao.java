package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingsystem.data.local.entity.Banner;

import java.util.List;

@Dao
public interface BannerDao {

    @Insert
    void insertAll(List<Banner> banners);

    @Query("SELECT * FROM banners ORDER BY sort_order ASC")
    LiveData<List<Banner>> findAll();
}
