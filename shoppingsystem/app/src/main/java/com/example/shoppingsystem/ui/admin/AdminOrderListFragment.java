package com.example.shoppingsystem.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.OrderListAdapter;

public class AdminOrderListFragment extends Fragment {

    private RecyclerView rvOrders;
    private OrderListAdapter adapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_list, container, false);

        rvOrders = view.findViewById(R.id.rv_admin_orders);
        adapter = new OrderListAdapter(requireContext());
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        viewModel.getAllOrders().observe(getViewLifecycleOwner(), orders -> adapter.setOrders(orders));

        adapter.setOnOrderActionListener(new OrderListAdapter.OnOrderActionListener() {
            @Override
            public void onClick(com.example.shoppingsystem.data.local.entity.Order order) {
                Bundle args = new Bundle();
                args.putLong("orderId", order.getId());
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_adminOrderList_to_orderDetail, args);
            }

            @Override
            public void onPay(com.example.shoppingsystem.data.local.entity.Order order) {}

            @Override
            public void onCancel(com.example.shoppingsystem.data.local.entity.Order order) {
                viewModel.updateOrderStatus(order.getId(), "CANCELLED");
            }

            @Override
            public void onConfirmReceipt(com.example.shoppingsystem.data.local.entity.Order order) {
                showStatusDialog(order.getId());
            }

            @Override
            public void onReview(com.example.shoppingsystem.data.local.entity.Order order) {}
        });

        // Long press to change order status
        adapter.setOnOrderActionListener(new OrderListAdapter.OnOrderActionListener() {
            @Override
            public void onClick(com.example.shoppingsystem.data.local.entity.Order order) {
                Bundle args = new Bundle();
                args.putLong("orderId", order.getId());
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_adminOrderList_to_orderDetail, args);
            }

            @Override
            public void onPay(com.example.shoppingsystem.data.local.entity.Order order) {}

            @Override
            public void onCancel(com.example.shoppingsystem.data.local.entity.Order order) {}

            @Override
            public void onConfirmReceipt(com.example.shoppingsystem.data.local.entity.Order order) {
                showStatusDialog(order.getId());
            }

            @Override
            public void onReview(com.example.shoppingsystem.data.local.entity.Order order) {
                showStatusDialog(order.getId());
            }
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.getAllOrders().observe(getViewLifecycleOwner(), orders -> adapter.setOrders(orders));
            }
        });

        return view;
    }

    private void showStatusDialog(long orderId) {
        String[] statuses = {"PENDING", "PAID", "SHIPPED", "COMPLETED", "CANCELLED"};
        String[] displayNames = {"待付款", "待发货", "待收货", "已完成", "已取消"};
        new AlertDialog.Builder(requireContext())
                .setTitle("修改订单状态")
                .setItems(displayNames, (dialog, which) -> viewModel.updateOrderStatus(orderId, statuses[which]))
                .show();
    }
}
