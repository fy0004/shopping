package com.example.shoppingsystem.data.remote;

import android.content.Context;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.util.SessionManager;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ECS 公网 IP
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
                String path = original.url().encodedPath();
                if (path.contains("auth/login") || path.contains("auth/register")
                        || path.contains("admin/login") || path.contains("health")) {
                    return chain.proceed(original);
                }
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

        // Gson: snake_case → camelCase 自动转换
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

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
                .addConverterFactory(GsonConverterFactory.create(gson))
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
