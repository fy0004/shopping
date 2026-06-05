package com.example.shoppingsystem.data.remote;

/**
 * API 服务接口 — 所有后端端点定义在此（当前注释状态）。
 * 未来接入真实后端后取消注释并启用。
 */
public interface ApiService {

    // ========== 用户相关 ==========
    // @POST("user/login")
    // Call<ApiResponse<User>> login(@Body LoginRequest request);
    //
    // @POST("user/register")
    // Call<ApiResponse<User>> register(@Body RegisterRequest request);

    // ========== 商品相关 ==========
    // @GET("product/list")
    // Call<ApiResponse<List<Product>>> getProducts(@Query("page") int page, @Query("size") int size);
    //
    // @GET("product/{id}")
    // Call<ApiResponse<Product>> getProductDetail(@Path("id") long productId);
    //
    // @GET("product/search")
    // Call<ApiResponse<List<Product>>> searchProducts(@Query("keyword") String keyword);

    // ========== 订单相关 ==========
    // @POST("order/create")
    // Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest request);
    //
    // @GET("order/list")
    // Call<ApiResponse<List<Order>>> getOrders(@Query("userId") long userId, @Query("status") String status);

    // ========== 地址相关 ==========
    // @GET("address/list")
    // Call<ApiResponse<List<Address>>> getAddresses(@Query("userId") long userId);
    //
    // @POST("address/save")
    // Call<ApiResponse<Address>> saveAddress(@Body Address address);

    // ========== Banner相关 ==========
    // @GET("banner/list")
    // Call<ApiResponse<List<Banner>>> getBanners();
}
