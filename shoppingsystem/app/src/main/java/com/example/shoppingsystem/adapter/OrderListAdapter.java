package com.example.shoppingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.util.DateUtils;
import com.example.shoppingsystem.util.OrderStatusConverter;
import com.example.shoppingsystem.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private final Context context;
    private List<Order> orders = new ArrayList<>();
    private OnOrderActionListener listener;

    public OrderListAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderNo.setText("订单号: " + order.getOrderNo());
        holder.tvOrderStatus.setText(OrderStatusConverter.toDisplayName(order.getStatus()));
        holder.tvTotalAmount.setText("合计: " + PriceFormatter.format(order.getTotalAmount()));
        holder.tvGoodsSummary.setText("下单时间: " + DateUtils.formatFull(order.getCreatedAt()));

        // Show action buttons based on status
        holder.btnAction1.setVisibility(View.GONE);
        holder.btnAction2.setVisibility(View.GONE);

        switch (order.getStatus()) {
            case "PENDING":
                holder.btnAction2.setText("去支付");
                holder.btnAction2.setVisibility(View.VISIBLE);
                holder.btnAction2.setOnClickListener(v -> {
                    if (listener != null) listener.onPay(order);
                });
                holder.btnAction1.setText("取消订单");
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setOnClickListener(v -> {
                    if (listener != null) listener.onCancel(order);
                });
                break;
            case "PAID":
                // Waiting for shipping - no user action
                break;
            case "SHIPPED":
                holder.btnAction2.setText("确认收货");
                holder.btnAction2.setVisibility(View.VISIBLE);
                holder.btnAction2.setOnClickListener(v -> {
                    if (listener != null) listener.onConfirmReceipt(order);
                });
                break;
            case "COMPLETED":
                holder.btnAction1.setText("评价");
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setOnClickListener(v -> {
                    if (listener != null) listener.onReview(order);
                });
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(order);
        });
    }

    @Override
    public int getItemCount() { return orders.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNo, tvOrderStatus, tvGoodsSummary, tvTotalAmount;
        Button btnAction1, btnAction2;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvGoodsSummary = itemView.findViewById(R.id.tv_goods_summary);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            btnAction1 = itemView.findViewById(R.id.btn_action1);
            btnAction2 = itemView.findViewById(R.id.btn_action2);
        }
    }

    public interface OnOrderActionListener {
        void onClick(Order order);
        void onPay(Order order);
        void onCancel(Order order);
        void onConfirmReceipt(Order order);
        void onReview(Order order);
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) { this.listener = listener; }
}
