package com.example.shoppingsystem.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.model.CartWithProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<CartWithProduct>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> selectedCount = new MutableLiveData<>(0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> cartCount = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> actionDone = new MutableLiveData<>();

    public LiveData<List<CartWithProduct>> getCartItems() { return cartItems; }
    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<Integer> getSelectedCount() { return selectedCount; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Integer> getCartCount() { return cartCount; }
    public LiveData<Boolean> getActionDone() { return actionDone; }

    public void loadCart(long userId) {
        RetrofitClient.getInstance().getApiService().getCart()
                .enqueue(new Callback<ApiResponse<List<Map<String, Object>>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Map<String, Object>>>> call,
                                           Response<ApiResponse<List<Map<String, Object>>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            List<Map<String, Object>> rawList = response.body().getData();
                            List<CartWithProduct> items = parseCartItems(rawList);
                            cartItems.postValue(items);
                            calculateTotal(items);
                            updateCount(rawList);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Map<String, Object>>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    public void loadCartCount(long userId) {
        RetrofitClient.getInstance().getApiService().getCartCount()
                .enqueue(new Callback<ApiResponse<Integer>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Integer>> call,
                                           Response<ApiResponse<Integer>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            cartCount.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {}
                });
    }

    public void updateQuantity(long itemId, int quantity) {
        Map<String, Object> body = java.util.Collections.singletonMap("quantity", quantity);
        RetrofitClient.getInstance().getApiService().updateCartItem(itemId, body)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            actionDone.postValue(true);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {}
                });
    }

    public void toggleSelected(long itemId, boolean selected) {
        Map<String, Object> body = java.util.Collections.singletonMap("isSelected", selected);
        RetrofitClient.getInstance().getApiService().updateCartItem(itemId, body)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful()) actionDone.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {}
                });
    }

    public void deleteItem(long itemId) {
        RetrofitClient.getInstance().getApiService().deleteCartItem(itemId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) actionDone.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
                });
    }

    public void deleteSelectedItems() {
        RetrofitClient.getInstance().getApiService().deleteSelectedCartItems()
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful()) actionDone.postValue(true);
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
                });
    }

    public void toggleAllSelected(long userId, boolean selected) {
        // Not supported directly by API; toggle individually
    }

    private void updateCount(List<Map<String, Object>> rawList) {
        if (rawList != null) {
            cartCount.postValue(rawList.size());
        }
    }

    private List<CartWithProduct> parseCartItems(List<Map<String, Object>> rawList) {
        List<CartWithProduct> result = new ArrayList<>();
        if (rawList == null) return result;
        for (Map<String, Object> map : rawList) {
            CartWithProduct item = new CartWithProduct();
            Number id = (Number) map.get("id");
            item.setId(id != null ? id.longValue() : 0);
            Number productId = (Number) map.get("product_id");
            item.setProductId(productId != null ? productId.longValue() : 0);
            Number quantity = (Number) map.get("quantity");
            item.setQuantity(quantity != null ? quantity.intValue() : 1);
            Boolean selected = (Boolean) map.get("is_selected");
            // Sequelize returns is_selected as 0/1, so check both
            if (selected == null) {
                Number sel = (Number) map.get("is_selected");
                item.setSelected(sel != null && sel.intValue() == 1);
            } else {
                item.setSelected(selected);
            }

            Map<String, Object> product = (Map<String, Object>) map.get("Product");
            if (product != null) {
                item.setProductName((String) product.get("name"));
                Number price = (Number) product.get("price");
                item.setProductPrice(price != null ? price.doubleValue() : 0);
                item.setProductImage((String) product.get("image_urls"));
                Number stock = (Number) product.get("stock");
                item.setProductStock(stock != null ? stock.intValue() : 0);
            }
            result.add(item);
        }
        return result;
    }

    public void calculateTotal(List<CartWithProduct> items) {
        double total = 0;
        int count = 0;
        if (items != null) {
            for (CartWithProduct item : items) {
                if (item.isSelected()) {
                    total += item.getSubtotal();
                    count++;
                }
            }
        }
        totalPrice.postValue(total);
        selectedCount.postValue(count);
    }
}
