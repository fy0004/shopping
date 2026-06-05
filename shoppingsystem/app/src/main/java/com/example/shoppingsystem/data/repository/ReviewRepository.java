package com.example.shoppingsystem.data.repository;

import androidx.lifecycle.LiveData;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.dao.ProductDao;
import com.example.shoppingsystem.data.local.dao.ReviewDao;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.Review;
import com.example.shoppingsystem.model.ReviewWithUser;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewRepository {

    private final ReviewDao reviewDao;
    private final ProductDao productDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ReviewRepository(AppDatabase database) {
        this.reviewDao = database.reviewDao();
        this.productDao = database.productDao();
    }

    public LiveData<List<ReviewWithUser>> getReviewsByProduct(long productId) {
        return reviewDao.findByProductWithUser(productId);
    }

    /**
     * 提交评价：插入评价 + 更新商品评分。
     */
    public void submitReview(Review review, OnResultCallback<Void> callback) {
        executor.execute(() -> {
            try {
                // Check if already reviewed for this order
                int count = reviewDao.countByProductAndOrder(review.getProductId(), review.getOrderId());
                if (count > 0) {
                    if (callback != null) callback.onError("该订单已评价过此商品");
                    return;
                }

                // Insert review
                review.setCreatedAt(System.currentTimeMillis());
                reviewDao.insert(review);

                // Update product rating
                Product product = productDao.findByIdSync(review.getProductId());
                if (product != null) {
                    int total = product.getRatingCount() + 1;
                    float newRating = (product.getRating() * product.getRatingCount() + review.getRating()) / total;
                    product.setRating(newRating);
                    product.setRatingCount(total);
                    productDao.update(product);
                }

                if (callback != null) callback.onSuccess(null);
            } catch (Exception e) {
                if (callback != null) callback.onError(e.getMessage());
            }
        });
    }

    /**
     * 检查是否已评价。
     */
    public int countByProductAndOrder(long productId, long orderId) {
        return reviewDao.countByProductAndOrder(productId, orderId);
    }

    // ========== Callback Interface ==========

    public interface OnResultCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
