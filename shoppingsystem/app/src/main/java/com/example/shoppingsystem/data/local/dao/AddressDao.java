package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingsystem.data.local.entity.Address;

import java.util.List;

@Dao
public interface AddressDao {

    @Insert
    long insert(Address address);

    @Update
    void update(Address address);

    @Delete
    void delete(Address address);

    @Query("SELECT * FROM addresses WHERE user_id = :userId ORDER BY is_default DESC, id DESC")
    LiveData<List<Address>> findByUser(long userId);

    @Query("SELECT * FROM addresses WHERE id = :addressId LIMIT 1")
    LiveData<Address> findById(long addressId);

    @Query("SELECT * FROM addresses WHERE id = :addressId LIMIT 1")
    Address findByIdSync(long addressId);

    @Query("SELECT * FROM addresses WHERE user_id = :userId AND is_default = 1 LIMIT 1")
    Address findDefaultByUserSync(long userId);

    @Query("UPDATE addresses SET is_default = 0 WHERE user_id = :userId")
    void clearDefault(long userId);

    @Query("UPDATE addresses SET is_default = 1 WHERE id = :addressId")
    void setDefault(long addressId);
}
