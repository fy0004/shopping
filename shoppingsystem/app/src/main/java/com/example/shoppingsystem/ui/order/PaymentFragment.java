package com.example.shoppingsystem.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.util.PriceFormatter;

public class PaymentFragment extends Fragment {

    private TextView tvAmount;
    private Button btnAlipay, btnWechat;
    private OrderViewModel viewModel;
    private long orderId;
    private double totalAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        tvAmount = view.findViewById(R.id.tv_payment_amount);
        btnAlipay = view.findViewById(R.id.btn_alipay);
        btnWechat = view.findViewById(R.id.btn_wechat);

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId", -1);
            totalAmount = getArguments().getFloat("totalAmount", 0.0f);
        }

        tvAmount.setText("支付金额: " + PriceFormatter.format(totalAmount));

        btnAlipay.setOnClickListener(v -> doPay("ALIPAY"));
        btnWechat.setOnClickListener(v -> doPay("WECHAT"));

        return view;
    }

    private void doPay(String method) {
        viewModel.payOrder(orderId, method);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "支付成功！", Toast.LENGTH_SHORT).show();
                // Navigate back to orders
                Navigation.findNavController(requireView()).popBackStack(R.id.orderDetailFragment, false);
            }
        });
    }
}
