package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.data.local.dao.UserDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(AppDatabase database) {
        this.userDao = database.userDao();
    }

    public LiveData<User> getUserById(long userId) {
        return userDao.findById(userId);
    }

    public User getUserByIdSync(long userId) {
        return userDao.findByIdSync(userId);
    }

    /**
     * 登录：返回用户，若密码不匹配则返回 null。
     */
    public User login(String phone, String passwordHash) {
        return userDao.login(phone, passwordHash);
    }

    /**
     * 按手机号查找用户（同步，用于查重）。
     */
    public User findByPhone(String phone) {
        return userDao.findByPhone(phone);
    }

    /**
     * 注册用户。
     */
    public void register(User user, OnResultCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = userDao.insert(user);
                if (callback != null) callback.onSuccess(id);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 更新用户信息。
     */
    public void updateProfile(User user, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                userDao.update(user);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 修改密码。
     */
    public void changePassword(long userId, String oldHash, String newHash, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                User user = userDao.findByIdSync(userId);
                if (user == null) {
                    if (callback != null) callback.onError("用户不存在");
                    return;
                }
                if (!user.getPasswordHash().equals(oldHash)) {
                    if (callback != null) callback.onError("原密码不正确");
                    return;
                }
                userDao.updatePassword(userId, newHash);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 管理员：获取指定角色的用户列表。
     */
    public LiveData<List<User>> getUsersByRole(String role) {
        return userDao.findByRole(role);
    }

    /**
     * 管理员：更新用户状态（启用/禁用）。
     */
    public void updateUserStatus(long userId, String status, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                userDao.updateStatus(userId, status);
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
