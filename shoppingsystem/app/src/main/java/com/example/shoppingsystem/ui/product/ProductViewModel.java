package com.example.shoppingsystem.ui.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.repository.CartRepository;
import com.example.shoppingsystem.data.repository.ProductRepository;
import com.example.shoppingsystem.data.repository.ReviewRepository;
import com.example.shoppingsystem.model.ReviewWithUser;
import com.example.shoppingsystem.util.SessionManager;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final ReviewRepository reviewRepository;

    private final MutableLiveData<Boolean> addToCartResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ProductViewModel() {
        productRepository = ShoppingApplication.getInstance().getProductRepository();
        cartRepository = ShoppingApplication.getInstance().getCartRepository();
        reviewRepository = ShoppingApplication.getInstance().getReviewRepository();
    }

    public LiveData<Product> getProductById(long productId) {
        return productRepository.getProductById(productId);
    }

    public Product getProductByIdSync(long productId) {
        return productRepository.getProductByIdSync(productId);
    }

    public LiveData<List<Product>> getProductsByCategory(long categoryId) {
        return productRepository.getProductsByCategory(categoryId);
    }

    public LiveData<List<Product>> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public LiveData<List<Product>> getAllOnSaleProducts() {
        return productRepository.getAllOnSaleProducts();
    }

    public LiveData<List<ReviewWithUser>> getReviews(long productId) {
        return reviewRepository.getReviewsByProduct(productId);
    }

    public LiveData<Boolean> getAddToCartResult() { return addToCartResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void addToCart(long userId, long productId, int quantity) {
        cartRepository.addToCart(userId, productId, quantity, new CartRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                addToCartResult.postValue(true);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }
}
