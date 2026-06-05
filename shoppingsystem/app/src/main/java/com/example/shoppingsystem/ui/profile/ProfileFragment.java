package com.example.shoppingsystem.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.ui.main.MainActivity;
import com.example.shoppingsystem.util.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView tvNickname, tvPhoneDisplay;
    private ImageView ivAvatar;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = SessionManager.getInstance(requireContext());

        tvNickname = view.findViewById(R.id.tv_nickname);
        tvPhoneDisplay = view.findViewById(R.id.tv_phone_display);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        btnLogout = view.findViewById(R.id.btn_logout);

        updateProfileUI();

        // Click to go to login
        view.findViewById(R.id.layout_user_header).setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Navigation.findNavController(view).navigate(R.id.action_profile_to_login);
            }
        });

        // My orders
        view.findViewById(R.id.item_my_orders).setOnClickListener(v -> {
            if (checkLogin()) Navigation.findNavController(view).navigate(R.id.action_profile_to_orderList);
        });

        // Address
        view.findViewById(R.id.item_address).setOnClickListener(v -> {
            if (checkLogin()) Navigation.findNavController(view).navigate(R.id.action_profile_to_addressList);
        });

        // Settings
        view.findViewById(R.id.item_settings).setOnClickListener(v -> {
            if (checkLogin()) Navigation.findNavController(view).navigate(R.id.action_profile_to_settings);
        });

        // Admin
        view.findViewById(R.id.item_admin).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_profile_to_adminLogin);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            updateProfileUI();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).refreshCartBadge();
            }
            Toast.makeText(requireContext(), "已退出登录", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileUI();
    }

    private void updateProfileUI() {
        if (sessionManager.isLoggedIn()) {
            tvNickname.setText(sessionManager.getUserNickname());
            tvPhoneDisplay.setText(sessionManager.getUserPhone());
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            tvNickname.setText("请登录");
            tvPhoneDisplay.setText("");
            btnLogout.setVisibility(View.GONE);
        }
    }

    private boolean checkLogin() {
        if (!sessionManager.isLoggedIn()) {
            Navigation.findNavController(requireView()).navigate(R.id.action_profile_to_login);
            return false;
        }
        return true;
    }
}
