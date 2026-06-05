package com.example.shoppingsystem.ui.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.data.repository.OrderRepository;
import com.example.shoppingsystem.data.repository.ProductRepository;
import com.example.shoppingsystem.data.repository.UserRepository;

import java.util.List;

public class AdminViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AdminViewModel() {
        orderRepository = ShoppingApplication.getInstance().getOrderRepository();
        productRepository = ShoppingApplication.getInstance().getProductRepository();
        userRepository = ShoppingApplication.getInstance().getUserRepository();
    }

    public LiveData<Integer> getTotalCount() { return orderRepository.getTotalCount(); }
    public LiveData<Double> getTotalRevenue() { return orderRepository.getTotalRevenue(); }
    public LiveData<List<Product>> getAllProducts() { return productRepository.getAllProducts(); }
    public LiveData<List<Order>> getAllOrders() { return orderRepository.getAllOrders(); }
    public LiveData<List<User>> getAllUsers() { return userRepository.getUsersByRole("USER"); }
    public LiveData<Boolean> getActionResult() { return actionResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void deleteProduct(Product product) {
        productRepository.delete(product, new ProductRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }
            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    public void saveProduct(Product product) {
        ProductRepository.OnResultCallback<Void> cb = new ProductRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }
            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        };
        if (product.getId() > 0) {
            productRepository.update(product, cb);
        } else {
            productRepository.insert(product, new ProductRepository.OnResultCallback<Long>() {
                @Override
                public void onSuccess(Long res) {
                    if (res > 0) actionResult.postValue(true);
                }
                @Override
                public void onError(String error) {
                    errorMessage.postValue(error);
                }
            });
        }
    }

    public void updateOrderStatus(long orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status, new OrderRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }
            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }

    public void updateUserStatus(long userId, String status) {
        userRepository.updateUserStatus(userId, status, new UserRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) { actionResult.postValue(true); }
            @Override
            public void onError(String error) { errorMessage.postValue(error); }
        });
    }
}
