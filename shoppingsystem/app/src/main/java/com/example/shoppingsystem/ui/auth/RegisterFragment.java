package com.example.shoppingsystem.ui.auth;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;

public class RegisterFragment extends Fragment {

    private EditText etPhone, etNickname, etPassword, etConfirmPassword;
    private Button btnRegister;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etPhone = view.findViewById(R.id.et_phone);
        etNickname = view.findViewById(R.id.et_nickname);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String nickname = etNickname.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPwd = etConfirmPassword.getText().toString().trim();

            if (!password.equals(confirmPwd)) {
                Toast.makeText(requireContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.register(phone, password, nickname);
        });

        viewModel.getRegisterResult().observe(getViewLifecycleOwner(), id -> {
            if (id != null && id > 0) {
                Toast.makeText(requireContext(), "注册成功，请登录", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
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
