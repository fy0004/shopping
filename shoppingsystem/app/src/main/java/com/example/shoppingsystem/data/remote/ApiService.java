package com.example.shoppingsystem.data.remote;

import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.data.remote.dto.DashboardResponse;
import com.example.shoppingsystem.data.remote.dto.LoginRequest;
import com.example.shoppingsystem.data.remote.dto.LoginResponse;
import com.example.shoppingsystem.data.remote.dto.ProductListResponse;
import com.example.shoppingsystem.data.remote.dto.RegisterRequest;
import com.example.shoppingsystem.model.ReviewWithUser;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==================== 用户认证 ====================
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<Map<String, Object>>> register(@Body RegisterRequest request);

    @PUT("auth/password")
    Call<ApiResponse<Void>> changePassword(@Body Map<String, String> body);

    @GET("auth/profile")
    Call<ApiResponse<User>> getProfile();

    // ==================== 商品 ====================
    @GET("products")
    Call<ApiResponse<ProductListResponse>> getProducts(
            @Query("categoryId") Long categoryId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/hot")
    Call<ApiResponse<List<Product>>> getHotProducts(@Query("limit") int limit);

    @GET("products/search")
    Call<ApiResponse<ProductListResponse>> searchProducts(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductDetail(@Path("id") long productId);

    // ==================== 分类 ====================
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    // ==================== Banner ====================
    @GET("banners")
    Call<ApiResponse<List<Banner>>> getBanners();

    // ==================== 购物车 ====================
    @GET("cart")
    Call<ApiResponse<List<Map<String, Object>>>> getCart();

    @GET("cart/count")
    Call<ApiResponse<Integer>> getCartCount();

    @POST("cart")
    Call<ApiResponse<Map<String, Object>>> addToCart(@Body Map<String, Object> body);

    @PUT("cart/{id}")
    Call<ApiResponse<Map<String, Object>>> updateCartItem(
            @Path("id") long itemId,
            @Body Map<String, Object> body
    );

    @DELETE("cart/{id}")
    Call<ApiResponse<Void>> deleteCartItem(@Path("id") long itemId);

    @DELETE("cart/batch/selected")
    Call<ApiResponse<Void>> deleteSelectedCartItems();

    // ==================== 地址 ====================
    @GET("addresses")
    Call<ApiResponse<List<Address>>> getAddresses();

    @POST("addresses")
    Call<ApiResponse<Address>> createAddress(@Body Address address);

    @PUT("addresses/{id}")
    Call<ApiResponse<Address>> updateAddress(
            @Path("id") long addressId,
            @Body Address address
    );

    @DELETE("addresses/{id}")
    Call<ApiResponse<Void>> deleteAddress(@Path("id") long addressId);

    // ==================== 订单 ====================
    @POST("orders")
    Call<ApiResponse<Map<String, Object>>> createOrder(@Body Map<String, Object> body);

    @GET("orders")
    Call<ApiResponse<List<Order>>> getOrders(@Query("status") String status);

    @GET("orders/{id}")
    Call<ApiResponse<Map<String, Object>>> getOrderDetail(@Path("id") long orderId);

    @PUT("orders/{id}/pay")
    Call<ApiResponse<Void>> payOrder(
            @Path("id") long orderId,
            @Body Map<String, String> body
    );

    @PUT("orders/{id}/cancel")
    Call<ApiResponse<Void>> cancelOrder(@Path("id") long orderId);

    @PUT("orders/{id}/confirm")
    Call<ApiResponse<Void>> confirmReceipt(@Path("id") long orderId);

    // ==================== 评价 ====================
    @GET("reviews/product/{productId}")
    Call<ApiResponse<List<ReviewWithUser>>> getProductReviews(@Path("productId") long productId);

    @POST("reviews")
    Call<ApiResponse<Void>> submitReview(@Body Map<String, Object> body);

    // ==================== 管理后台 ====================
    @POST("admin/login")
    Call<ApiResponse<LoginResponse>> adminLogin(@Body LoginRequest request);

    @GET("admin/dashboard")
    Call<ApiResponse<DashboardResponse>> getDashboard();

    @GET("admin/products")
    Call<ApiResponse<List<Product>>> getAdminProducts();

    @POST("admin/products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);

    @PUT("admin/products/{id}")
    Call<ApiResponse<Product>> updateProduct(
            @Path("id") long productId,
            @Body Product product
    );

    @DELETE("admin/products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") long productId);

    @GET("admin/orders")
    Call<ApiResponse<List<Order>>> getAdminOrders();

    @PUT("admin/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("id") long orderId,
            @Body Map<String, String> body
    );

    @GET("admin/users")
    Call<ApiResponse<List<User>>> getAdminUsers();

    @PUT("admin/users/{id}/status")
    Call<ApiResponse<User>> updateUserStatus(
            @Path("id") long userId,
            @Body Map<String, String> body
    );

    // ==================== 图片上传 ====================
    @Multipart
    @POST("products/upload")
    Call<ApiResponse<Map<String, String>>> uploadImage(@Part MultipartBody.Part image);

    // ==================== 健康检查 ====================
    @GET("health")
    Call<ApiResponse<Map<String, Object>>> health();
}
