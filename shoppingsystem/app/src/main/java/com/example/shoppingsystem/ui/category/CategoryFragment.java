package com.example.shoppingsystem.ui.category;

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
import com.example.shoppingsystem.adapter.ProductLinearAdapter;
import com.example.shoppingsystem.data.local.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView rvCategoryLeft, rvCategoryProducts;
    private CategoryLeftAdapter categoryLeftAdapter;
    private ProductLinearAdapter productLinearAdapter;
    private CategoryViewModel viewModel;
    private List<Category> allCategories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        rvCategoryLeft = view.findViewById(R.id.rv_category_left);
        rvCategoryProducts = view.findViewById(R.id.rv_category_products);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // 左侧分类列表
        categoryLeftAdapter = new CategoryLeftAdapter(requireContext(), category -> {
            categoryLeftAdapter.setSelectedCategoryId(category.getId());
            loadProductsForCategory(category.getId());
        });
        rvCategoryLeft.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCategoryLeft.setAdapter(categoryLeftAdapter);

        // 右侧商品列表
        productLinearAdapter = new ProductLinearAdapter(requireContext());
        productLinearAdapter.setOnProductClickListener(product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.getId());
            Navigation.findNavController(view).navigate(R.id.action_category_to_productDetail, args);
        });
        rvCategoryProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCategoryProducts.setAdapter(productLinearAdapter);

        // 加载分类列表
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            allCategories = categories != null ? categories : new ArrayList<>();
            categoryLeftAdapter.setCategories(allCategories);
            if (!allCategories.isEmpty()) {
                selectCategoryById(allCategories.get(0).getId());
            }
        });

        // 监听来自首页的分类选择
        getParentFragmentManager().setFragmentResultListener(
                "category_selected", getViewLifecycleOwner(),
                (requestKey, result) -> {
                    long categoryId = result.getLong("selectedCategoryId", -1);
                    if (categoryId > 0) {
                        selectCategoryById(categoryId);
                    }
                });

        return view;
    }

    /**
     * 根据分类 ID 选中并加载商品。
     */
    private void selectCategoryById(long categoryId) {
        categoryLeftAdapter.setSelectedCategoryId(categoryId);
        loadProductsForCategory(categoryId);
    }

    private void loadProductsForCategory(long categoryId) {
        viewModel.loadProducts(categoryId);
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            productLinearAdapter.setProducts(products);
        });
    }

    // ========== Inner Adapter ==========

    private static class CategoryLeftAdapter extends RecyclerView.Adapter<CategoryLeftAdapter.VH> {

        private final android.content.Context context;
        private List<Category> categories = new ArrayList<>();
        private long selectedId = -1;
        private final OnCategoryClickListener listener;

        interface OnCategoryClickListener {
            void onClick(Category category);
        }

        CategoryLeftAdapter(android.content.Context context, OnCategoryClickListener listener) {
            this.context = context;
            this.listener = listener;
        }

        void setCategories(List<Category> cats) {
            this.categories = cats != null ? cats : new ArrayList<>();
            notifyDataSetChanged();
        }

        void setSelectedCategoryId(long id) {
            this.selectedId = id;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            android.widget.TextView tv = new android.widget.TextView(context);
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    120));
            tv.setGravity(android.view.Gravity.CENTER);
            tv.setTextSize(13);
            tv.setPadding(8, 8, 8, 8);
            return new VH(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            final Category c = categories.get(position);
            holder.textView.setText(c.getName());
            if (c.getId() == selectedId) {
                holder.textView.setBackgroundColor(0xFFFFFFFF);
                holder.textView.setTextColor(0xFF6200EE);
            } else {
                holder.textView.setBackgroundColor(0xFFF5F5F5);
                holder.textView.setTextColor(0xFF333333);
            }
            holder.textView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(c);
            });
        }

        @Override
        public int getItemCount() { return categories.size(); }

        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView textView;
            VH(android.widget.TextView tv) { super(tv); this.textView = tv; }
        }
    }
}
