package com.example.shoppingsystem.ui.profile;

import android.app.AlertDialog;
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
import com.example.shoppingsystem.ui.main.MainActivity;
import com.example.shoppingsystem.util.SessionManager;

public class SettingsFragment extends Fragment {

    private Button btnChangePassword, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).logout();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshCartBadge();
            }
            Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).popBackStack(R.id.homeFragment, false);
        });

        return view;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("修改密码");

        final EditText etOld = new EditText(requireContext());
        etOld.setHint("原密码");
        etOld.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(etOld);

        final EditText etNew = new EditText(requireContext());
        etNew.setHint("新密码（至少6位）");
        etNew.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Use a linear layout for two fields in newer implementation
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(etOld);
        layout.addView(etNew);
        builder.setView(layout);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String oldPwd = etOld.getText().toString().trim();
            String newPwd = etNew.getText().toString().trim();
            if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                Toast.makeText(requireContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPwd.length() < 6) {
                Toast.makeText(requireContext(), "新密码至少6位", Toast.LENGTH_SHORT).show();
                return;
            }
            // Use UserRepository to change password
            com.example.shoppingsystem.ShoppingApplication.getInstance()
                    .getUserRepository().changePassword(
                            SessionManager.getInstance(requireContext()).getUserId(),
                            com.example.shoppingsystem.util.PasswordUtils.hash(oldPwd),
                            com.example.shoppingsystem.util.PasswordUtils.hash(newPwd),
                            new com.example.shoppingsystem.data.repository.UserRepository.OnResultCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(requireContext(), "密码修改成功", Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onError(String error) {
                                    requireActivity().runOnUiThread(() ->
                                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
                                }
                            }
                    );
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
