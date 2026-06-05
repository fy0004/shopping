package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.AddressDao;
import com.example.shoppingsystem.data.local.entity.Address;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddressRepository {

    private final AddressDao addressDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AddressRepository(AppDatabase database) {
        this.addressDao = database.addressDao();
    }

    public LiveData<List<Address>> getAddressesByUser(long userId) {
        return addressDao.findByUser(userId);
    }

    public LiveData<Address> getAddressById(long addressId) {
        return addressDao.findById(addressId);
    }

    public Address getAddressByIdSync(long addressId) {
        return addressDao.findByIdSync(addressId);
    }

    public Address getDefaultByUserSync(long userId) {
        return addressDao.findDefaultByUserSync(userId);
    }

    public void insert(Address address, OnResultCallback<Long> callback) {
        executor.execute(() -> {
            try {
                if (address.isDefault()) {
                    addressDao.clearDefault(address.getUserId());
                }
                long id = addressDao.insert(address);
                if (callback != null) callback.onSuccess(id);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void update(Address address, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                if (address.isDefault()) {
                    addressDao.clearDefault(address.getUserId());
                }
                addressDao.update(address);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void delete(Address address, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                addressDao.delete(address);
                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    public void setDefault(long userId, long addressId, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                addressDao.clearDefault(userId);
                addressDao.setDefault(addressId);
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
