package com.example.shoppingsystem.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Banner>> banners = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> goodsList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel() {
        loadHomeData();
    }

    public void loadHomeData() {
        // 加载 Banner
        RetrofitClient.getInstance().getApiService().getBanners()
                .enqueue(new Callback<ApiResponse<List<Banner>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Banner>>> call,
                                           Response<ApiResponse<List<Banner>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            banners.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Banner>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });

        // 加载分类
        RetrofitClient.getInstance().getApiService().getCategories()
                .enqueue(new Callback<ApiResponse<List<Category>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Category>>> call,
                                           Response<ApiResponse<List<Category>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            categories.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });

        // 加载热销商品
        RetrofitClient.getInstance().getApiService().getHotProducts(20)
                .enqueue(new Callback<ApiResponse<List<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Product>>> call,
                                           Response<ApiResponse<List<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            goodsList.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public LiveData<List<Banner>> getBanners() { return banners; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Product>> getGoodsList() { return goodsList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
