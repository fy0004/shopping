package com.example.shoppingsystem.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.data.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private final OrderRepository repository;
    private final MutableLiveData<Boolean> createOrderResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();

    public OrderViewModel() {
        repository = ShoppingApplication.getInstance().getOrderRepository();
    }

    public LiveData<Order> getOrderById(long orderId) {
        return repository.getOrderById(orderId);
    }

    public Order getOrderByIdSync(long orderId) {
        return repository.getOrderByIdSync(orderId);
    }

    public LiveData<List<Order>> getOrdersByUser(long userId) {
        return repository.getOrdersByUser(userId);
    }

    public LiveData<List<Order>> getOrdersByUserAndStatus(long userId, String status) {
        return repository.getOrdersByUserAndStatus(userId, status);
    }

    public LiveData<List<OrderItem>> getOrderItems(long orderId) {
        return repository.getOrderItems(orderId);
    }

    public LiveData<Boolean> getCreateOrderResult() { return createOrderResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getActionResult() { return actionResult; }

    public void createOrder(long userId, Address address, List<CartItem> selectedItems, String paymentMethod) {
        repository.createOrder(userId, address, selectedItems, paymentMethod, new OrderRepository.OnResultCallback<Long>() {
            @Override
            public void onSuccess(Long orderId) {
                createOrderResult.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void payOrder(long orderId, String paymentMethod) {
        repository.payOrder(orderId, paymentMethod, new OrderRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }

            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    public void cancelOrder(long orderId) {
        repository.cancelOrder(orderId, new OrderRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }

            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    public void confirmReceipt(long orderId) {
        repository.confirmReceipt(orderId, new OrderRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }

            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    public void updateOrderStatus(long orderId, String status) {
        repository.updateOrderStatus(orderId, status, new OrderRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }

            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    // ========== Admin ==========

    public LiveData<List<Order>> getAllOrders() {
        return repository.getAllOrders();
    }

    public LiveData<Integer> getTotalCount() {
        return repository.getTotalCount();
    }

    public LiveData<Double> getTotalRevenue() {
        return repository.getTotalRevenue();
    }
}
