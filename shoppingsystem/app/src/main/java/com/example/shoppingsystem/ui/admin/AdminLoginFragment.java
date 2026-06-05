package com.example.shoppingsystem.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.User;
import com.example.shoppingsystem.util.PasswordUtils;
import com.example.shoppingsystem.util.SessionManager;

public class AdminLoginFragment extends Fragment {

    private EditText etPhone, etPassword;
    private Button btnLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        etPhone = view.findViewById(R.id.et_admin_phone);
        etPassword = view.findViewById(R.id.et_admin_password);
        btnLogin = view.findViewById(R.id.btn_admin_login);

        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String pwd = etPassword.getText().toString().trim();
            if (phone.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(requireContext(), "请填写手机号和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = ShoppingApplication.getInstance().getUserRepository()
                    .login(phone, PasswordUtils.hash(pwd));
            if (user != null && "ADMIN".equals(user.getRole())) {
                SessionManager.getInstance(requireContext()).saveAdminSession(
                        user.getId(), user.getPhone(), user.getNickname());
                Toast.makeText(requireContext(), "管理员登录成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(v).navigate(R.id.action_adminLogin_to_dashboard);
            } else {
                Toast.makeText(requireContext(), "管理员账号或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
