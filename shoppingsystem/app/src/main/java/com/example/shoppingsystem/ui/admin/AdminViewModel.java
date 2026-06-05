package com.example.shoppingsystem.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.data.remote.dto.DashboardResponse;
import com.example.shoppingsystem.data.remote.dto.LoginRequest;
import com.example.shoppingsystem.data.remote.dto.LoginResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<DashboardResponse> dashboard = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> allProducts = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> allOrders = new MutableLiveData<>();
    private final MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> adminLoginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<DashboardResponse> getDashboard() { return dashboard; }
    public LiveData<List<Product>> getAllProducts() { return allProducts; }
    public LiveData<List<Order>> getAllOrders() { return allOrders; }
    public LiveData<List<User>> getAllUsers() { return allUsers; }
    public LiveData<LoginResponse> getAdminLoginResult() { return adminLoginResult; }
    public LiveData<Boolean> getActionResult() { return actionResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // 管理员登录
    public void adminLogin(String phone, String password) {
        LoginRequest req = new LoginRequest(phone, password);
        RetrofitClient.getInstance().getApiService().adminLogin(req)
                .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LoginResponse>> call,
                                           Response<ApiResponse<LoginResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            adminLoginResult.postValue(response.body().getData());
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : "登录失败";
                            errorMessage.postValue(msg);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void loadDashboard() {
        RetrofitClient.getInstance().getApiService().getDashboard()
                .enqueue(new Callback<ApiResponse<DashboardResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<DashboardResponse>> call,
                                           Response<ApiResponse<DashboardResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            dashboard.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<DashboardResponse>> call, Throwable t) {}
                });
    }

    public void loadAllProducts() {
        RetrofitClient.getInstance().getApiService().getAdminProducts()
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call,
                                           Response<ApiResponse<List<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            allProducts.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {}
                });
    }

    public void loadAllOrders() {
        RetrofitClient.getInstance().getApiService().getAdminOrders()
                .enqueue(new Callback<ApiResponse<List<Order>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Order>>> call,
                                           Response<ApiResponse<List<Order>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            allOrders.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {}
                });
    }

    public void loadAllUsers() {
        RetrofitClient.getInstance().getApiService().getAdminUsers()
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call,
                                           Response<ApiResponse<List<User>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            allUsers.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {}
                });
    }

    public void saveProduct(Product product) {
        ApiResponseCallback<Void> cb = new ApiResponseCallback<Void>() {
            @Override
            public void onSuccess(Void v) { actionResult.postValue(true); }
        };
        if (product.getId() > 0) {
            RetrofitClient.getInstance().getApiService().updateProduct(product.getId(), product)
                    .enqueue(new ProductCallback());
        } else {
            RetrofitClient.getInstance().getApiService().createProduct(product)
                    .enqueue(new ProductCallback());
        }
    }

    public void deleteProduct(long productId) {
        RetrofitClient.getInstance().getApiService().deleteProduct(productId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                            actionResult.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
                });
    }

    public void updateOrderStatus(long orderId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        RetrofitClient.getInstance().getApiService().updateOrderStatus(orderId, body)
                .enqueue(new Callback<ApiResponse<Order>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                            actionResult.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {}
                });
    }

    public void updateUserStatus(long userId, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        RetrofitClient.getInstance().getApiService().updateUserStatus(userId, body)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> r) {
                        if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                            actionResult.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
                });
    }

    private class ProductCallback implements Callback<ApiResponse<Product>> {
        @Override
        public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> r) {
            if (r.isSuccessful() && r.body() != null && r.body().isSuccess())
                actionResult.postValue(true);
        }
        @Override
        public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {}
    }

    private interface ApiResponseCallback<T> {
        void onSuccess(T v);
    }
}
