package com.example.shoppingsystem.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.data.remote.RetrofitClient;
import com.example.shoppingsystem.data.remote.dto.ApiResponse;
import com.example.shoppingsystem.data.remote.dto.LoginRequest;
import com.example.shoppingsystem.data.remote.dto.LoginResponse;
import com.example.shoppingsystem.data.remote.dto.RegisterRequest;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
    public LiveData<Boolean> getRegisterResult() { return registerResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void login(String phone, String password) {
        if (phone.isEmpty() || password.isEmpty()) {
            errorMessage.postValue("手机号和密码不能为空");
            return;
        }

        LoginRequest request = new LoginRequest(phone, password);
        RetrofitClient.getInstance().getApiService().login(request)
                .enqueue(new Callback<ApiResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<LoginResponse>> call,
                                           Response<ApiResponse<LoginResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<LoginResponse> apiResp = response.body();
                            if (apiResp.isSuccess() && apiResp.getData() != null) {
                                loginResult.postValue(apiResp.getData());
                            } else {
                                errorMessage.postValue(apiResp.getMessage());
                            }
                        } else {
                            errorMessage.postValue("网络连接失败，请检查服务器");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                        errorMessage.postValue("网络错误: " + t.getMessage());
                    }
                });
    }

    public void register(String phone, String password, String nickname) {
        if (phone.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
            errorMessage.postValue("请填写所有字段");
            return;
        }
        if (phone.length() != 11) {
            errorMessage.postValue("请输入正确的手机号");
            return;
        }
        if (password.length() < 6) {
            errorMessage.postValue("密码至少6位");
            return;
        }

        RegisterRequest request = new RegisterRequest(phone, password, nickname);
        RetrofitClient.getInstance().getApiService().register(request)
                .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                                           Response<ApiResponse<Map<String, Object>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Map<String, Object>> apiResp = response.body();
                            if (apiResp.isSuccess()) {
                                registerResult.postValue(true);
                            } else {
                                errorMessage.postValue(apiResp.getMessage());
                            }
                        } else {
                            errorMessage.postValue("网络连接失败，请检查服务器");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                        errorMessage.postValue("网络错误: " + t.getMessage());
                    }
                });
    }
}
