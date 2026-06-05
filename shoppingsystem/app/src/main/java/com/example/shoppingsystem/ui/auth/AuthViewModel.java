package com.example.shoppingsystem.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.data.repository.UserRepository;
import com.example.shoppingsystem.util.PasswordUtils;

public class AuthViewModel extends ViewModel {

    private final UserRepository repository;
    private final MutableLiveData<User> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Long> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public AuthViewModel() {
        repository = ShoppingApplication.getInstance().getUserRepository();
    }

    public LiveData<User> getLoginResult() { return loginResult; }
    public LiveData<Long> getRegisterResult() { return registerResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void login(String phone, String password) {
        if (phone.isEmpty() || password.isEmpty()) {
            errorMessage.postValue("手机号和密码不能为空");
            return;
        }
        String hash = PasswordUtils.hash(password);
        User user = repository.login(phone, hash);
        if (user != null && "ACTIVE".equals(user.getStatus())) {
            loginResult.postValue(user);
        } else if (user != null && "DISABLED".equals(user.getStatus())) {
            errorMessage.postValue("该账号已被禁用");
        } else {
            errorMessage.postValue("手机号或密码错误");
        }
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
        // Check duplicate
        User existing = repository.findByPhone(phone);
        if (existing != null) {
            errorMessage.postValue("该手机号已注册");
            return;
        }
        String hash = PasswordUtils.hash(password);
        User user = new User(phone, hash, nickname, "USER");
        repository.register(user, new UserRepository.OnResultCallback<Long>() {
            @Override
            public void onSuccess(Long id) {
                registerResult.postValue(id);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue(error);
            }
        });
    }
}
