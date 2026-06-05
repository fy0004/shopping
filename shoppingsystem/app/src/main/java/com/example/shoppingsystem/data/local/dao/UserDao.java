package com.example.shoppingsystem.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingsystem.data.local.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE phone = :phone AND password_hash = :passwordHash LIMIT 1")
    User login(String phone, String passwordHash);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    User findByPhone(String phone);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User findByIdSync(long userId);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> findById(long userId);

    @Query("SELECT * FROM users WHERE role = :role")
    LiveData<List<User>> findByRole(String role);

    @Query("UPDATE users SET status = :status WHERE id = :userId")
    void updateStatus(long userId, String status);

    @Query("UPDATE users SET password_hash = :newHash WHERE id = :userId")
    void updatePassword(long userId, String newHash);

    @Query("UPDATE users SET nickname = :nickname, avatar_url = :avatarUrl WHERE id = :userId")
    void updateProfile(long userId, String nickname, String avatarUrl);
}
