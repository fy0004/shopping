package com.example.shoppingsystem.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.model.CartWithProduct;
import com.example.shoppingsystem.util.PriceFormatter;
import com.example.shoppingsystem.util.SessionManager;

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvEmpty, tvTotalPrice, tvEditMode;
    private CheckBox cbSelectAll;
    private Button btnEdit, btnSettle;
    private CartAdapter adapter;
    private CartViewModel viewModel;
    private SessionManager sessionManager;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sessionManager = SessionManager.getInstance(requireContext());

        rvCart = view.findViewById(R.id.rv_cart);
        tvEmpty = view.findViewById(R.id.tv_empty_cart);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        tvEditMode = view.findViewById(R.id.tv_edit_mode);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        btnEdit = view.findViewById(R.id.btn_edit);
        btnSettle = view.findViewById(R.id.btn_settle);

        adapter = new CartAdapter(requireContext());
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Only load cart if logged in
        if (sessionManager.isLoggedIn()) {
            loadCart();
        }

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), total -> {
            tvTotalPrice.setText(PriceFormatter.format(total != null ? total : 0.0));
        });

        viewModel.getSelectedCount().observe(getViewLifecycleOwner(), count -> {
            btnSettle.setText("结算(" + (count != null ? count : 0) + ")");
        });

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 全选功能简化：不做远程同步，只做本地刷新 UI
        });

        btnEdit.setOnClickListener(v -> {
            isEditMode = !isEditMode;
            if (isEditMode) {
                btnEdit.setText("完成");
                tvEditMode.setText("编辑模式");
                btnSettle.setText("删除选中");
                btnSettle.setOnClickListener(v2 -> {
                    if (sessionManager.isLoggedIn()) {
                        viewModel.deleteSelectedItems();
                        Toast.makeText(requireContext(), "已删除选中商品", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                btnEdit.setText("编辑");
                tvEditMode.setText("购物车");
                btnSettle.setText("结算(0)");
                btnSettle.setOnClickListener(v2 -> checkout());
            }
        });

        btnSettle.setOnClickListener(v2 -> checkout());

        adapter.setOnItemChangeListener(new CartAdapter.OnItemChangeListener() {
            @Override
            public void onToggleSelected(long itemId, boolean selected) {
                viewModel.toggleSelected(itemId, selected);
            }
            @Override
            public void onQuantityChanged(long itemId, int quantity) {
                viewModel.updateQuantity(itemId, quantity);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            loadCart();
        }
    }

    private void loadCart() {
        long userId = sessionManager.getUserId();
        if (userId <= 0) return;
        viewModel.loadCart(userId);
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            viewModel.calculateTotal(items);
            if (items == null || items.isEmpty()) {
                rvCart.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                rvCart.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }
        });
        // 操作完成后自动刷新
        viewModel.getActionDone().observe(getViewLifecycleOwner(), done -> {
            if (done != null && done) {
                viewModel.loadCart(userId);
            }
        });
    }

    private void checkout() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        List<CartWithProduct> items = adapter.getCurrentItems();
        boolean hasSelected = false;
        if (items != null) {
            for (CartWithProduct item : items) {
                if (item.isSelected()) { hasSelected = true; break; }
            }
        }
        if (!hasSelected) {
            Toast.makeText(requireContext(), "请选择要结算的商品", Toast.LENGTH_SHORT).show();
            return;
        }
        Navigation.findNavController(requireView()).navigate(R.id.action_cart_to_orderConfirm);
    }
}
