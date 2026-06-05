package com.example.shoppingsystem.ui.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.data.remote.dto.ProductListResponse;
import com.example.shoppingsystem.model.ReviewWithUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> categoryProducts = new MutableLiveData<>();
    private final MutableLiveData<List<ReviewWithUser>> reviews = new MutableLiveData<>();
    private final MutableLiveData<Boolean> addToCartResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Product> getProduct() { return product; }
    public LiveData<List<Product>> getSearchResults() { return searchResults; }
    public LiveData<List<Product>> getCategoryProducts() { return categoryProducts; }
    public LiveData<List<ReviewWithUser>> getReviews() { return reviews; }
    public LiveData<Boolean> getAddToCartResult() { return addToCartResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadProductDetail(long productId) {
        RetrofitClient.getInstance().getApiService().getProductDetail(productId)
                .enqueue(new Callback<ApiResponse<Product>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Product>> call,
                                           Response<ApiResponse<Product>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            product.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void searchProducts(String keyword) {
        if (keyword == null || keyword.isEmpty()) return;
        RetrofitClient.getInstance().getApiService().searchProducts(keyword, 1, 50)
                .enqueue(new Callback<ApiResponse<ProductListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ProductListResponse>> call,
                                           Response<ApiResponse<ProductListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess() && response.body().getData() != null) {
                            searchResults.postValue(response.body().getData().getList());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void loadProductsByCategory(long categoryId) {
        RetrofitClient.getInstance().getApiService().getProducts(categoryId, 1, 100)
                .enqueue(new Callback<ApiResponse<ProductListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ProductListResponse>> call,
                                           Response<ApiResponse<ProductListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess() && response.body().getData() != null) {
                            categoryProducts.postValue(response.body().getData().getList());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void loadReviews(long productId) {
        RetrofitClient.getInstance().getApiService().getProductReviews(productId)
                .enqueue(new Callback<ApiResponse<List<ReviewWithUser>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<ReviewWithUser>>> call,
                                           Response<ApiResponse<List<ReviewWithUser>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            reviews.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<ReviewWithUser>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void addToCart(long userId, long productId, int quantity) {
        java.util.Map<String, Object> body = java.util.Collections.singletonMap("productId", productId);
        // Actually, the API needs productId and quantity
        java.util.Map<String, Object> cartBody = new java.util.HashMap<>();
        cartBody.put("productId", productId);
        cartBody.put("quantity", quantity);

        RetrofitClient.getInstance().getApiService().addToCart(cartBody)
                .enqueue(new Callback<ApiResponse<java.util.Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<java.util.Map<String, Object>>> call,
                                           Response<ApiResponse<java.util.Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            addToCartResult.postValue(true);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<java.util.Map<String, Object>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }
}
