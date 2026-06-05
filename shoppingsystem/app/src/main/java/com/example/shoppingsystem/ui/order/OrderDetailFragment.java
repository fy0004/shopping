package com.example.shoppingsystem.ui.order;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.util.DateUtils;
import com.example.shoppingsystem.util.OrderStatusConverter;
import com.example.shoppingsystem.util.PriceFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class OrderDetailFragment extends Fragment {

    private TextView tvOrderStatus, tvOrderNo, tvAddress, tvTotal;
    private RecyclerView rvOrderItems;
    private Button btnAction1, btnAction2;
    private OrderViewModel viewModel;
    private long orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        tvOrderStatus = view.findViewById(R.id.tv_order_status);
        tvOrderNo = view.findViewById(R.id.tv_order_no);
        tvAddress = view.findViewById(R.id.tv_address);
        tvTotal = view.findViewById(R.id.tv_total);
        rvOrderItems = view.findViewById(R.id.rv_order_items);
        btnAction1 = view.findViewById(R.id.btn_action1);
        btnAction2 = view.findViewById(R.id.btn_action2);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        orderId = getArguments() != null ? getArguments().getLong("orderId", -1) : -1;
        if (orderId <= 0) return view;

        viewModel.loadOrderDetail(orderId);
        viewModel.getCurrentOrder().observe(getViewLifecycleOwner(), order -> {
            if (order != null) bindOrder(order);
        });

        viewModel.getOrderItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                rvOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()) {
                    @Override
                    public boolean canScrollVertically() { return false; }
                });
                rvOrderItems.setAdapter(new OrderItemAdapter(items));
            }
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        return view;
    }

    private void bindOrder(Order order) {
        tvOrderStatus.setText(OrderStatusConverter.toDisplayName(order.getStatus()));
        tvOrderNo.setText("订单号: " + order.getOrderNo());
        tvTotal.setText(PriceFormatter.format(order.getTotalAmount()));

        // Parse address JSON
        try {
            Gson gson = new Gson();
            Map<String, Object> addr = gson.fromJson(order.getAddressSnapshot(),
                    new TypeToken<Map<String, Object>>() {}.getType());
            String addrStr = addr.get("receiverName") + " " + addr.get("phone") + "\n"
                    + addr.get("province") + " " + addr.get("city") + " "
                    + addr.get("district") + " " + addr.get("detail");
            tvAddress.setText(addrStr);
        } catch (Exception e) {
            tvAddress.setText(order.getAddressSnapshot());
        }

        // Show action buttons
        btnAction1.setVisibility(View.GONE);
        btnAction2.setVisibility(View.GONE);

        switch (order.getStatus()) {
            case "PENDING":
                btnAction2.setText("去支付");
                btnAction2.setVisibility(View.VISIBLE);
                btnAction2.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("orderId", order.getId());
                    args.putDouble("totalAmount", order.getTotalAmount());
                    Navigation.findNavController(requireView()).navigate(R.id.action_orderDetail_to_payment, args);
                });
                btnAction1.setText("取消订单");
                btnAction1.setVisibility(View.VISIBLE);
                btnAction1.setOnClickListener(v -> viewModel.cancelOrder(order.getId()));
                break;
            case "SHIPPED":
                btnAction2.setText("确认收货");
                btnAction2.setVisibility(View.VISIBLE);
                btnAction2.setOnClickListener(v -> viewModel.confirmReceipt(order.getId()));
                break;
            case "COMPLETED":
                btnAction1.setText("评价");
                btnAction1.setVisibility(View.VISIBLE);
                btnAction1.setOnClickListener(v -> {
                    // Navigate to review with order items' first product
                    viewModel.loadOrderDetail(order.getId());
                    viewModel.getOrderItems().observe(getViewLifecycleOwner(), items -> {
                        if (items != null && !items.isEmpty()) {
                            Bundle args = new Bundle();
                            args.putLong("productId", items.get(0).getProductId());
                            Navigation.findNavController(requireView()).navigate(
                                    R.id.action_orderDetail_to_reviews, args);
                        }
                    });
                });
                break;
        }
    }

    // Inner adapter
    private static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> {
        private final List<OrderItem> items;

        OrderItemAdapter(List<OrderItem> items) { this.items = items; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            OrderItem oi = items.get(position);
            holder.tvName.setText(oi.getProductName());
            holder.tvPrice.setText(PriceFormatter.format(oi.getPrice()));
            holder.tvQuantity.setText("x" + oi.getQuantity());
            Glide.with(holder.itemView.getContext()).load(oi.getProductImage()).into(holder.ivImage);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            android.widget.ImageView ivImage;
            TextView tvName, tvPrice, tvQuantity;

            VH(View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_product);
                tvName = v.findViewById(R.id.tv_product_name);
                tvPrice = v.findViewById(R.id.tv_price);
                tvQuantity = v.findViewById(R.id.tv_quantity);
            }
        }
    }
}
