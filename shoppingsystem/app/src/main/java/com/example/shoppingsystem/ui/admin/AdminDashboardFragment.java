package com.example.shoppingsystem.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.util.PriceFormatter;
import com.example.shoppingsystem.util.SessionManager;

public class AdminDashboardFragment extends Fragment {

    private TextView tvTotalOrders, tvTotalRevenue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        tvTotalOrders = view.findViewById(R.id.tv_total_orders);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);

        AdminViewModel viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        viewModel.loadDashboard();
        viewModel.getDashboard().observe(getViewLifecycleOwner(), dashboard -> {
            if (dashboard != null) {
                tvTotalOrders.setText(String.valueOf(dashboard.getTotalOrders()));
                tvTotalRevenue.setText(PriceFormatter.format(dashboard.getTotalRevenue()));
            }
        });

        view.findViewById(R.id.item_admin_products).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminDashboard_to_products));

        view.findViewById(R.id.item_admin_orders).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminDashboard_to_orders));

        view.findViewById(R.id.item_admin_users).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_adminDashboard_to_users));

        view.findViewById(R.id.btn_admin_logout).setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).logoutAdmin();
            Navigation.findNavController(v).popBackStack(R.id.profileFragment, false);
        });

        return view;
    }
}
