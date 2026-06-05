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
import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.local.entity.Product;
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
    private OrderViewModel viewModel;
    private SessionManager sessionManager;
    private Address selectedAddress;
    private List<CartItem> selectedItems = new ArrayList<>();
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

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // Get default address safely in background
        try {
            Address defaultAddr = ShoppingApplication.getInstance()
                    .getAddressRepository().getDefaultByUserSync(userId);
            if (defaultAddr != null) {
                selectedAddress = defaultAddr;
                tvAddressInfo.setText(defaultAddr.getReceiverName() + " "
                        + defaultAddr.getPhone() + "\n" + defaultAddr.getFullAddress());
            }
        } catch (Exception ignored) {}

        // Address click -> select address
        view.findViewById(R.id.layout_address).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putBoolean("selectMode", true);
            Navigation.findNavController(v).navigate(R.id.action_orderConfirm_to_addressList, args);
        });

        // Listen for address selection result
        getParentFragmentManager().setFragmentResultListener("address_selected", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    long addrId = result.getLong("selectedAddressId", -1);
                    if (addrId > 0) {
                        try {
                            selectedAddress = ShoppingApplication.getInstance()
                                    .getAddressRepository().getAddressByIdSync(addrId);
                            if (selectedAddress != null) {
                                tvAddressInfo.setText(selectedAddress.getReceiverName() + " "
                                        + selectedAddress.getPhone() + "\n" + selectedAddress.getFullAddress());
                            }
                        } catch (Exception ignored) {}
                    }
                });

        // Get selected cart items and calculate total
        try {
            selectedItems = ShoppingApplication.getInstance()
                    .getCartRepository().getSelectedItemsSync(userId);
            if (selectedItems == null) selectedItems = new ArrayList<>();

            totalPrice = 0;
            for (CartItem ci : selectedItems) {
                try {
                    Product p = ShoppingApplication.getInstance()
                            .getProductRepository().getProductByIdSync(ci.getProductId());
                    if (p != null) {
                        totalPrice += p.getPrice() * ci.getQuantity();
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // Show order items
        rvOrderItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrderItems.setAdapter(new OrderItemPreviewAdapter(selectedItems));

        tvGoodsTotal.setText(PriceFormatter.format(totalPrice));
        tvTotalAmount.setText(PriceFormatter.format(totalPrice));

        btnSubmitOrder.setOnClickListener(v -> {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "请选择收货地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "没有可结算的商品", Toast.LENGTH_SHORT).show();
                return;
            }
            String paymentMethod = rgPayment.getCheckedRadioButtonId() == R.id.rb_wechat
                    ? "WECHAT" : "ALIPAY";
            viewModel.createOrder(userId, selectedAddress, selectedItems, paymentMethod);
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "下单成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack(R.id.cartFragment, false);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // Simple inner adapter for order item preview
    private static class OrderItemPreviewAdapter extends RecyclerView.Adapter<OrderItemPreviewAdapter.VH> {
        private final List<CartItem> items;

        OrderItemPreviewAdapter(List<CartItem> items) {
            this.items = items != null ? items : new ArrayList<>();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CartItem ci = items.get(position);
            if (ci == null) return;
            try {
                Product p = ShoppingApplication.getInstance()
                        .getProductRepository().getProductByIdSync(ci.getProductId());
                if (p != null) {
                    holder.tvName.setText(p.getName());
                    holder.tvPrice.setText(PriceFormatter.format(p.getPrice()));
                    holder.tvQuantity.setText("x" + ci.getQuantity());
                    String img = ImageUrlUtil.getFirstImage(p.getImageUrls());
                    Glide.with(holder.itemView.getContext()).load(img).into(holder.ivImage);
                }
            } catch (Exception ignored) {}
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
