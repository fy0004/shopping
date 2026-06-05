package com.example.shoppingsystem.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.ui.main.MainActivity;
import com.example.shoppingsystem.util.SessionManager;

public class LoginFragment extends Fragment {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etPhone = view.findViewById(R.id.et_phone);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvGoRegister = view.findViewById(R.id.tv_go_register);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            viewModel.login(phone, password);
        });

        tvGoRegister.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_login_to_register);
        });

        // 登录结果（含 JWT Token）
        viewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResp -> {
            if (loginResp != null) {
                SessionManager sessionManager = SessionManager.getInstance(requireContext());
                sessionManager.saveUserSession(
                        loginResp.getId(),
                        loginResp.getPhone(),
                        loginResp.getNickname(),
                        loginResp.getRole(),
                        loginResp.getToken()
                );
                Toast.makeText(requireContext(), "登录成功", Toast.LENGTH_SHORT).show();

                // 回到首页
                Navigation.findNavController(requireView())
                        .popBackStack(R.id.homeFragment, false);

                // 延迟刷新角标
                requireView().postDelayed(() -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).refreshCartBadge();
                    }
                }, 300);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
