package com.example.shoppingsystem.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.repository.AddressRepository;

import java.util.List;

public class AddressViewModel extends ViewModel {

    private final AddressRepository repository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public AddressViewModel() {
        repository = ShoppingApplication.getInstance().getAddressRepository();
    }

    public LiveData<List<Address>> getAddresses(long userId) {
        return repository.getAddressesByUser(userId);
    }

    public LiveData<Address> getAddressById(long addressId) {
        return repository.getAddressById(addressId);
    }

    public Address getAddressByIdSync(long addressId) {
        return repository.getAddressByIdSync(addressId);
    }

    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }

    public void saveAddress(Address address) {
        if (address.getReceiverName().isEmpty() || address.getPhone().isEmpty() ||
                address.getProvince().isEmpty() || address.getCity().isEmpty()) {
            errorMessage.postValue("请填写完整的地址信息");
            return;
        }
        AddressRepository.OnResultCallback<Void> callback = new AddressRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        };

        if (address.getId() > 0) {
            repository.update(address, callback);
        } else {
            repository.insert(address, new AddressRepository.OnResultCallback<Long>() {
                @Override
                public void onSuccess(Long res) {
                    if (res > 0) saveSuccess.postValue(true);
                }
                @Override
                public void onError(String error) {
                    errorMessage.postValue(error);
                }
            });
        }
    }

    public void deleteAddress(Address address) {
        repository.delete(address, new AddressRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveSuccess.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }
}
