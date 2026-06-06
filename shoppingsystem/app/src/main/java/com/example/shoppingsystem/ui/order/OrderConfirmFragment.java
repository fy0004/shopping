package com.example.shoppingsystem.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.model.CartWithProduct;
import com.example.shoppingsystem.ui.cart.CartViewModel;
import com.example.shoppingsystem.ui.address.AddressViewModel;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.example.shoppingsystem.util.PriceFormatter;
import com.example.shoppingsystem.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmFragment extends Fragment {

    private TextView tvAddressInfo, tvGoodsTotal, tvTotalAmount;
    private RecyclerView rvOrderItems;
    private Button btnSubmitOrder;
    private RadioGroup rgPayment;
    private OrderViewModel orderViewModel;
    private CartViewModel cartViewModel;
    private AddressViewModel addressViewModel;
    private SessionManager sessionManager;
    private Address selectedAddress;
    private List<CartWithProduct> selectedItems = new ArrayList<>();
    private double totalPrice = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_confirm, container, false);

        sessionManager = SessionManager.getInstance(requireContext());
        long userId = sessionManager.getUserId();
        if (userId <= 0) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return view;
        }

        tvAddressInfo = view.findViewById(R.id.tv_address_info);
        tvGoodsTotal = view.findViewById(R.id.tv_goods_total);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        rvOrderItems = view.findViewById(R.id.rv_order_items);
        btnSubmitOrder = view.findViewById(R.id.btn_submit_order);
        rgPayment = view.findViewById(R.id.rg_payment);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        // 1. 从API加载地址
        addressViewModel.loadAddresses(userId);
        addressViewModel.getAddresses().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses != null) {
                for (Address a : addresses) {
                    if (a.isDefault()) {
                        selectedAddress = a;
                        tvAddressInfo.setText(a.getReceiverName() + " " + a.getPhone() + "\n" + a.getFullAddress());
                        break;
                    }
                }
                // 没有默认地址就用第一个
                if (selectedAddress == null && !addresses.isEmpty()) {
                    selectedAddress = addresses.get(0);
                    tvAddressInfo.setText(addresses.get(0).getReceiverName() + " " + addresses.get(0).getPhone() + "\n" + addresses.get(0).getFullAddress());
                }
            }
        });

        // 2. 从API加载购物车
        cartViewModel.loadCart(userId);
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                selectedItems.clear();
                totalPrice = 0;
                for (CartWithProduct cwp : items) {
                    if (cwp.isSelected()) {
                        selectedItems.add(cwp);
                        totalPrice += cwp.getSubtotal();
                    }
                }
                rvOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));
                rvOrderItems.setAdapter(new OrderItemAdapter(selectedItems));
                tvGoodsTotal.setText(PriceFormatter.format(totalPrice));
                tvTotalAmount.setText(PriceFormatter.format(totalPrice));
            }
        });

        // 地址选择
        view.findViewById(R.id.layout_address).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("selectMode", true);
            Navigation.findNavController(v).navigate(R.id.action_orderConfirm_to_addressList, args);
        });

        // 监听地址选择返回
        getParentFragmentManager().setFragmentResultListener("address_selected", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    long addrId = result.getLong("selectedAddressId", -1);
                    if (addrId > 0) {
                        addressViewModel.loadAddresses(userId);
                        addressViewModel.getAddresses().observe(getViewLifecycleOwner(), addrs -> {
                            if (addrs != null) {
                                for (Address a : addrs) {
                                    if (a.getId() == addrId) {
                                        selectedAddress = a;
                                        tvAddressInfo.setText(a.getReceiverName() + " " + a.getPhone() + "\n" + a.getFullAddress());
                                        break;
                                    }
                                }
                            }
                        });
                    }
                });

        // 提交订单
        btnSubmitOrder.setOnClickListener(v -> {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "请先选择要结算的商品", Toast.LENGTH_SHORT).show();
                return;
            }
            String paymentMethod = rgPayment.getCheckedRadioButtonId() == R.id.rb_wechat ? "WECHAT" : "ALIPAY";
            orderViewModel.createOrder(userId, selectedAddress, paymentMethod);
        });

        orderViewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "下单成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack(R.id.cartFragment, false);
            }
        });

        orderViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // 订单商品适配器（直接使用 CartWithProduct）
    private static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> {
        private final List<CartWithProduct> items;

        OrderItemAdapter(List<CartWithProduct> items) {
            this.items = items != null ? items : new ArrayList<>();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CartWithProduct cwp = items.get(position);
            holder.tvName.setText(cwp.getProductName());
            holder.tvPrice.setText(PriceFormatter.format(cwp.getProductPrice()));
            holder.tvQuantity.setText("x" + cwp.getQuantity());
            String img = ImageUrlUtil.getFirstImage(cwp.getProductImage());
            Glide.with(holder.itemView.getContext()).load(img).into(holder.ivImage);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            ImageView ivImage;
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
