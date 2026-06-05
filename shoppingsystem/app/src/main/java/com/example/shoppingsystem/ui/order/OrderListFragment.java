package com.example.shoppingsystem.ui.order;

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
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.util.SessionManager;
import com.google.android.material.tabs.TabLayout;

public class OrderListFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private OrderListAdapter adapter;
    private OrderViewModel viewModel;
    private SessionManager sessionManager;

    private static final String[] STATUS_LABELS = {"全部", "待付款", "待发货", "待收货", "已完成", "已取消"};
    private static final String[] STATUS_VALUES = {null, "PENDING", "PAID", "SHIPPED", "COMPLETED", "CANCELLED"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        sessionManager = SessionManager.getInstance(requireContext());
        long userId = sessionManager.getUserId();

        tabLayout = view.findViewById(R.id.tab_order_status);
        rvOrders = view.findViewById(R.id.rv_orders);

        for (String label : STATUS_LABELS) {
            tabLayout.addTab(tabLayout.newTab().setText(label));
        }

        adapter = new OrderListAdapter(requireContext());
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Load all orders by default
        viewModel.getOrdersByUser(userId).observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                String status = STATUS_VALUES[pos];
                if (status == null) {
                    viewModel.getOrdersByUser(userId).observe(getViewLifecycleOwner(),
                            orders -> adapter.setOrders(orders));
                } else {
                    viewModel.getOrdersByUserAndStatus(userId, status).observe(getViewLifecycleOwner(),
                            orders -> adapter.setOrders(orders));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        adapter.setOnOrderActionListener(new OrderListAdapter.OnOrderActionListener() {
            @Override
            public void onClick(Order order) {
                Bundle args = new Bundle();
                args.putLong("orderId", order.getId());
                Navigation.findNavController(requireView()).navigate(R.id.action_orderList_to_orderDetail, args);
            }

            @Override
            public void onPay(Order order) {
                Bundle args = new Bundle();
                args.putLong("orderId", order.getId());
                args.putDouble("totalAmount", order.getTotalAmount());
                Navigation.findNavController(requireView()).navigate(R.id.action_orderDetail_to_payment, args);
            }

            @Override
            public void onCancel(Order order) {
                viewModel.cancelOrder(order.getId());
            }

            @Override
            public void onConfirmReceipt(Order order) {
                viewModel.confirmReceipt(order.getId());
            }

            @Override
            public void onReview(Order order) {
                viewModel.getOrderItems(order.getId()).observe(getViewLifecycleOwner(), items -> {
                    if (items != null && !items.isEmpty()) {
                        long productId = items.get(0).getProductId();
                        Bundle args = new Bundle();
                        args.putLong("productId", productId);
                        Navigation.findNavController(requireView()).navigate(R.id.action_orderDetail_to_reviews, args);
                    }
                });
            }
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                // Refresh
                viewModel.getOrdersByUser(userId).observe(getViewLifecycleOwner(),
                        orders -> adapter.setOrders(orders));
            }
        });

        return view;
    }
}
