package com.example.shoppingsystem.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.repository.CartRepository;
import com.example.shoppingsystem.model.CartWithProduct;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final CartRepository repository;
    private final MutableLiveData<Double> totalPrice = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> selectedCount = new MutableLiveData<>(0);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CartViewModel() {
        repository = ShoppingApplication.getInstance().getCartRepository();
    }

    public LiveData<List<CartWithProduct>> getCartItems(long userId) {
        return repository.getCartItems(userId);
    }

    public LiveData<Integer> getCartCount(long userId) {
        return repository.getCartCount(userId);
    }

    public LiveData<Double> getTotalPrice() { return totalPrice; }
    public LiveData<Integer> getSelectedCount() { return selectedCount; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void updateQuantity(long itemId, int quantity) {
        if (quantity < 1) quantity = 1;
        repository.updateQuantity(itemId, quantity, new CartRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {}

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void deleteItem(long itemId) {
        repository.deleteItem(itemId, new CartRepository.OnResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {}

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }

    public void deleteSelectedItems(long userId) {
        repository.deleteSelectedItems(userId);
    }

    public void toggleSelected(long itemId, boolean selected) {
        repository.toggleSelected(itemId, selected);
    }

    public void toggleAllSelected(long userId, boolean selected) {
        repository.toggleAllSelected(userId, selected);
    }

    /**
     * Calculate total price for selected items.
     */
    public void calculateTotal(List<CartWithProduct> items) {
        double total = 0;
        int count = 0;
        for (CartWithProduct item : items) {
            if (item.isSelected()) {
                total += item.getSubtotal();
                count++;
            }
        }
        totalPrice.postValue(total);
        selectedCount.postValue(count);
    }

    /**
     * Get selected cart items for checkout (sync).
     */
    public List<CartItem> getSelectedItemsSync(long userId) {
        return repository.getSelectedItemsSync(userId);
    }
}
