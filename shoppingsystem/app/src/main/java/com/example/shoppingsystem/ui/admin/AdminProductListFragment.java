package com.example.shoppingsystem.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.example.shoppingsystem.util.PriceFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminProductListFragment extends Fragment {

    private RecyclerView rvProducts;
    private FloatingActionButton fabAdd;
    private AdminProductAdapter adapter;
    private AdminViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_list, container, false);

        rvProducts = view.findViewById(R.id.rv_admin_products);
        fabAdd = view.findViewById(R.id.fab_add_product);

        adapter = new AdminProductAdapter(requireContext());
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> adapter.setProducts(products));

        fabAdd.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("productId", -1);
            Navigation.findNavController(v).navigate(R.id.action_adminProductList_to_edit, args);
        });

        adapter.setOnEditListener(product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.getId());
            Navigation.findNavController(requireView()).navigate(R.id.action_adminProductList_to_edit, args);
        });

        adapter.setOnDeleteListener(product -> viewModel.deleteProduct(product));

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) refreshList();
        });

        return view;
    }

    private void refreshList() {
        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> adapter.setProducts(products));
    }

    // Inner adapter
    private static class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.VH> {

        private final android.content.Context context;
        private List<Product> products = new ArrayList<>();
        private OnEditListener editListener;
        private OnDeleteListener deleteListener;

        AdminProductAdapter(android.content.Context ctx) { this.context = ctx; }

        void setProducts(List<Product> list) { this.products = list != null ? list : new ArrayList<>(); notifyDataSetChanged(); }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_admin_product, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            Product p = products.get(pos);
            holder.tvName.setText(p.getName());
            holder.tvInfo.setText(PriceFormatter.format(p.getPrice()) + " | 库存:" + p.getStock() + " | " + (p.isOnSale() ? "在售" : "已下架"));
            String img = ImageUrlUtil.getFirstImage(p.getImageUrls());
            Glide.with(context).load(img).into(holder.ivImage);
            holder.btnEdit.setOnClickListener(v -> { if (editListener != null) editListener.onEdit(p); });
            holder.btnEdit.setOnLongClickListener(v -> { if (deleteListener != null) deleteListener.onDelete(p); return true; });
        }

        @Override
        public int getItemCount() { return products.size(); }

        static class VH extends RecyclerView.ViewHolder {
            ImageView ivImage; TextView tvName, tvInfo; Button btnEdit;
            VH(View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_product);
                tvName = v.findViewById(R.id.tv_name);
                tvInfo = v.findViewById(R.id.tv_info);
                btnEdit = v.findViewById(R.id.btn_edit);
            }
        }

        interface OnEditListener { void onEdit(Product p); }
        interface OnDeleteListener { void onDelete(Product p); }

        void setOnEditListener(OnEditListener l) { this.editListener = l; }
        void setOnDeleteListener(OnDeleteListener l) { this.deleteListener = l; }
    }
}
