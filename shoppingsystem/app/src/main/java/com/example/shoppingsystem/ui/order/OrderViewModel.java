package com.example.shoppingsystem.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
    private final MutableLiveData<Order> currentOrder = new MutableLiveData<>();
    private final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Order>> getOrders() { return orders; }
    public LiveData<Order> getCurrentOrder() { return currentOrder; }
    public LiveData<List<OrderItem>> getOrderItems() { return orderItems; }
    public LiveData<Boolean> getActionResult() { return actionResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // 创建订单
    public void createOrder(long userId, Address address, List<CartItem> selectedItems, String method) {
        Map<String, Object> body = new HashMap<>();
        for (CartItem ci : selectedItems) {
            body.put("addressId", Long.valueOf(address.getId()));
            body.put("paymentMethod", method);
            break; // just need one pair, the API reads from cart
        }
        RetrofitClient.getInstance().getApiService().createOrder(body)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            actionResult.postValue(true);
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : "下单失败";
                            errorMessage.postValue(msg);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        errorMessage.postValue(t.getMessage());
                    }
                });
    }

    // 加载订单列表
    public void loadOrders(long userId) {
        RetrofitClient.getInstance().getApiService().getOrders(null)
                .enqueue(new Callback<ApiResponse<List<Order>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Order>>> call,
                                           Response<ApiResponse<List<Order>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            orders.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {}
                });
    }

    // 按状态加载
    public void loadOrdersByStatus(long userId, String status) {
        RetrofitClient.getInstance().getApiService().getOrders(status)
                .enqueue(new Callback<ApiResponse<List<Order>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Order>>> call,
                                           Response<ApiResponse<List<Order>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess()) {
                            orders.postValue(response.body().getData());
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {}
                });
    }

    // 加载订单详情
    public void loadOrderDetail(long orderId) {
        RetrofitClient.getInstance().getApiService().getOrderDetail(orderId)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().isSuccess() && response.body().getData() != null) {
                            Map<String, Object> data = response.body().getData();
                            currentOrder.postValue((Order) data.get("order"));
                            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
                            List<OrderItem> oiList = new ArrayList<>();
                            if (items != null) {
                                for (Map<String, Object> m : items) {
                                    OrderItem oi = new OrderItem();
                                    oi.setProductName((String) m.get("product_name"));
                                    Number price = (Number) m.get("price");
                                    oi.setPrice(price != null ? price.doubleValue() : 0);
                                    Number qty = (Number) m.get("quantity");
                                    oi.setQuantity(qty != null ? qty.intValue() : 1);
                                    oi.setProductImage((String) m.get("product_image"));
                                    oiList.add(oi);
                                }
                            }
                            orderItems.postValue(oiList);
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {}
                });
    }

    // 支付
    public void payOrder(long orderId, String paymentMethod) {
        Map<String, String> body = new HashMap<>();
        body.put("paymentMethod", paymentMethod);
        RetrofitClient.getInstance().getApiService().payOrder(orderId, body)
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

    // 取消
    public void cancelOrder(long orderId) {
        RetrofitClient.getInstance().getApiService().cancelOrder(orderId)
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

    // 确认收货
    public void confirmReceipt(long orderId) {
        RetrofitClient.getInstance().getApiService().confirmReceipt(orderId)
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
}
