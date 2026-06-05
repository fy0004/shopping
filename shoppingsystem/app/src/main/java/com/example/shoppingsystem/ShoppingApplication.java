package com.example.shoppingsystem;

import android.app.Application;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.repository.AddressRepository;
import com.example.shoppingsystem.data.repository.CartRepository;
import com.example.shoppingsystem.data.repository.HomeRepository;
import com.example.shoppingsystem.data.repository.OrderRepository;
import com.example.shoppingsystem.data.repository.ProductRepository;
import com.example.shoppingsystem.data.repository.ReviewRepository;
import com.example.shoppingsystem.data.repository.UserRepository;
import com.example.shoppingsystem.util.MockDataInitializer;

import java.util.concurrent.Executors;

public class ShoppingApplication extends Application {

    private static ShoppingApplication instance;
    private AppDatabase database;

    // Repositories
    private UserRepository userRepository;
    private HomeRepository homeRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private AddressRepository addressRepository;
    private ReviewRepository reviewRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Room database via singleton
        database = AppDatabase.getInstance(this);

        // Initialize repositories
        userRepository = new UserRepository(database);
        homeRepository = new HomeRepository(database);
        productRepository = new ProductRepository(database);
        cartRepository = new CartRepository(database);
        orderRepository = new OrderRepository(database);
        addressRepository = new AddressRepository(database);
        reviewRepository = new ReviewRepository(database);

        // Fill mock data on first launch
        Executors.newSingleThreadExecutor().execute(() -> {
            MockDataInitializer.init(database);
        });
    }

    public static ShoppingApplication getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public HomeRepository getHomeRepository() {
        return homeRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public CartRepository getCartRepository() {
        return cartRepository;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }
}
