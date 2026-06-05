package com.example.shoppingsystem.data.remote;

import android.content.Context;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.util.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 单例客户端。
 * BASE_URL 指向阿里云 ECS 后端服务器。
 */
public class RetrofitClient {

    // ECS 公网 IP（Android 模拟器用 10.0.2.2 访问宿主机，真机用公网 IP）
    private static final String BASE_URL = "http://120.24.74.38:3000/api/";

    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // JWT Token 拦截器
        Interceptor authInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                // 跳过登录/注册/健康检查的 Token 注入
                String path = original.url().encodedPath();
                if (path.contains("auth/login") || path.contains("auth/register")
                        || path.contains("admin/login") || path.contains("health")) {
                    return chain.proceed(original);
                }

                // 注入 Token
                try {
                    Context context = ShoppingApplication.getInstance();
                    if (context != null) {
                        SessionManager sm = SessionManager.getInstance(context);
                        String token = sm.getToken();
                        if (token != null && !token.isEmpty()) {
                            Request authorized = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(authorized);
                        }
                    }
                } catch (Exception ignored) {}
                return chain.proceed(original);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
}
