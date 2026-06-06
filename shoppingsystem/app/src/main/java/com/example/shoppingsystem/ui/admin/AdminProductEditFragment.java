package com.example.shoppingsystem.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;

import java.util.Arrays;

public class AdminProductEditFragment extends Fragment {

    private EditText etName, etDesc, etPrice, etOrigPrice, etStock, etCategoryId, etImageUrls;
    private Button btnSave;
    private AdminViewModel viewModel;
    private long productId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_product_edit, container, false);

        etName = view.findViewById(R.id.et_product_name);
        etDesc = view.findViewById(R.id.et_product_desc);
        etPrice = view.findViewById(R.id.et_product_price);
        etOrigPrice = view.findViewById(R.id.et_product_orig_price);
        etStock = view.findViewById(R.id.et_product_stock);
        etCategoryId = view.findViewById(R.id.et_category_id);
        etImageUrls = view.findViewById(R.id.et_image_urls);
        btnSave = view.findViewById(R.id.btn_save_product);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        if (getArguments() != null) {
            productId = getArguments().getLong("productId", -1);
        }

        if (productId > 0) {
            // 从 API 加载的商品列表中查找当前商品
            viewModel.loadAllProducts();
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (products != null) {
                    for (Product p : products) {
                        if (p.getId() == productId) {
                            etName.setText(p.getName());
                            etDesc.setText(p.getDescription());
                            etPrice.setText(String.valueOf(p.getPrice()));
                            etOrigPrice.setText(String.valueOf(p.getOriginalPrice()));
                            etStock.setText(String.valueOf(p.getStock()));
                            etCategoryId.setText(String.valueOf(p.getCategoryId()));
                            etImageUrls.setText(String.join(",", ImageUrlUtil.fromJson(p.getImageUrls())));
                            break;
                        }
                    }
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            try {
                Product p = new Product();
                p.setId(productId > 0 ? productId : 0);
                p.setName(etName.getText().toString().trim());
                p.setDescription(etDesc.getText().toString().trim());
                p.setPrice(Double.parseDouble(etPrice.getText().toString().trim()));
                p.setOriginalPrice(Double.parseDouble(etOrigPrice.getText().toString().trim()));
                p.setStock(Integer.parseInt(etStock.getText().toString().trim()));
                p.setCategoryId(Long.parseLong(etCategoryId.getText().toString().trim()));
                p.setImageUrls(ImageUrlUtil.toJson(
                        Arrays.asList(etImageUrls.getText().toString().trim().split(","))));
                p.setOnSale(true);
                p.setCreatedAt(System.currentTimeMillis());
                viewModel.saveProduct(p);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "请填写正确的数字", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getActionResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        return view;
    }
}
