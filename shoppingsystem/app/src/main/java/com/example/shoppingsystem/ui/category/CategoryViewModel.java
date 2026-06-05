package com.example.shoppingsystem.ui.category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.data.remote.dto.ProductListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CategoryViewModel() {
        loadCategories();
    }

    private void loadCategories() {
        RetrofitClient.getInstance().getApiService().getCategories()
                .enqueue(new Callback<ApiResponse<List<Category>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Category>>> call,
                                           Response<ApiResponse<List<Category>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<Category> list = response.body().getData();
                            categories.postValue(list);
                            if (list != null && !list.isEmpty()) {
                                loadProducts(list.get(0).getId());
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void loadProducts(long categoryId) {
        RetrofitClient.getInstance().getApiService().getProducts(categoryId, 1, 100)
                .enqueue(new Callback<ApiResponse<ProductListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ProductListResponse>> call,
                                           Response<ApiResponse<ProductListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess() && response.body().getData() != null) {
                            products.postValue(response.body().getData().getList());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
}
