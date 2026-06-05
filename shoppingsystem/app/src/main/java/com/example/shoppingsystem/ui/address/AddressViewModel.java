package com.example.shoppingsystem.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressViewModel extends ViewModel {

    private final MutableLiveData<List<Address>> addresses = new MutableLiveData<>();
    private final MutableLiveData<Address> currentAddress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Address>> getAddresses() { return addresses; }
    public LiveData<Address> getCurrentAddress() { return currentAddress; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadAddresses(long userId) {
        RetrofitClient.getInstance().getApiService().getAddresses()
                .enqueue(new Callback<ApiResponse<List<Address>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Address>>> call,
                                           Response<ApiResponse<List<Address>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            addresses.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void saveAddress(Address address) {
        if (address.getReceiverName().isEmpty() || address.getPhone().isEmpty()) {
            errorMessage.postValue("请填写完整的地址信息");
            return;
        }
        if (address.getId() > 0) {
            RetrofitClient.getInstance().getApiService().updateAddress(address.getId(), address)
                    .enqueue(new AddressCallback());
        } else {
            RetrofitClient.getInstance().getApiService().createAddress(address)
                    .enqueue(new AddressCallback());
        }
    }

    public void deleteAddress(long addressId) {
        RetrofitClient.getInstance().getApiService().deleteAddress(addressId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                            saveSuccess.postValue(true);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    private class AddressCallback implements Callback<ApiResponse<Address>> {
        @Override
        public void onResponse(Call<ApiResponse<Address>> call, Response<ApiResponse<Address>> r) {
            if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                saveSuccess.postValue(true);
            } else {
                String msg = r.body() != null ? r.body().getMessage() : "操作失败";
                errorMessage.postValue(msg);
            }
        }
        @Override
        public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
            errorMessage.postValue(t.getMessage());
        }
    }
}
