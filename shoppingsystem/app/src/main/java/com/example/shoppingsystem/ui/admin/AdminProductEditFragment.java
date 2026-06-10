package com.example.shoppingsystem.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

public class AdminProductEditFragment extends Fragment {

    private EditText etName, etDesc, etPrice, etOrigPrice, etStock, etCategoryId, etImageUrls;
    private Button btnSave, btnPickImage, btnUpload;
    private ImageView ivPreview;
    private AdminViewModel viewModel;
    private long productId = -1;
    private Uri selectedImageUri = null;
    private boolean formFilled = false;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivPreview.setVisibility(View.VISIBLE);
                    Glide.with(this).load(uri).into(ivPreview);
                    btnUpload.setEnabled(true);
                }
            });

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
        btnPickImage = view.findViewById(R.id.btn_pick_image);
        btnUpload = view.findViewById(R.id.btn_upload_image);
        ivPreview = view.findViewById(R.id.iv_preview);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        if (getArguments() != null) {
            productId = getArguments().getLong("productId", -1);
        }

        if (productId > 0) {
            viewModel.loadAllProducts();
            viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
                if (!formFilled && products != null) {
                    for (Product p : products) {
                        if (p.getId() == productId) {
                            formFilled = true;
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

        // 选择图片
        btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // 上传图片
        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                btnUpload.setEnabled(false);
                btnUpload.setText("上传中...");
                uploadImage(selectedImageUri);
            }
        });

        // 保存
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

        // 监听上传结果
        viewModel.getUploadedUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                String existing = etImageUrls.getText().toString().trim();
                if (existing.isEmpty()) {
                    etImageUrls.setText(url);
                } else {
                    // 新图片放在最前面，作为首图显示
                    etImageUrls.setText(url + "," + existing);
                }
                Toast.makeText(requireContext(), "图片上传成功", Toast.LENGTH_SHORT).show();
                btnUpload.setEnabled(true);
                btnUpload.setText("上传");
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
            btnUpload.setEnabled(true);
            btnUpload.setText("上传");
        });

        return view;
    }

    private void uploadImage(Uri uri) {
        try {
            // Copy URI content to temp file
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            File tempFile = new File(requireContext().getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            fos.close();
            is.close();

            viewModel.uploadImage(tempFile);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "读取图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnUpload.setEnabled(true);
            btnUpload.setText("上传");
        }
    }
}
