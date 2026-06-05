package com.example.shoppingsystem.ui.product;

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
import androidx.viewpager2.widget.ViewPager2;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.BannerPagerAdapter;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.example.shoppingsystem.util.PriceFormatter;
import com.example.shoppingsystem.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment {

    private ViewPager2 viewPagerImages;
    private TextView tvPrice, tvOriginalPrice, tvName, tvStock, tvSales, tvDesc;
    private Button btnAddToCart, btnBuyNow;
    private ProductViewModel viewModel;
    private Product currentProduct;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        sessionManager = SessionManager.getInstance(requireContext());

        viewPagerImages = view.findViewById(R.id.viewpager_images);
        tvPrice = view.findViewById(R.id.tv_product_price);
        tvOriginalPrice = view.findViewById(R.id.tv_product_original_price);
        tvName = view.findViewById(R.id.tv_product_name);
        tvStock = view.findViewById(R.id.tv_stock);
        tvSales = view.findViewById(R.id.tv_sales_count);
        tvDesc = view.findViewById(R.id.tv_product_desc);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        btnBuyNow = view.findViewById(R.id.btn_buy_now);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        long productId = getArguments() != null ? getArguments().getLong("productId", -1) : -1;

        viewModel.getProductById(productId).observe(getViewLifecycleOwner(), product -> {
            if (product != null) {
                currentProduct = product;
                bindProduct(product);
            }
        });

        // 评价入口
        view.findViewById(R.id.layout_reviews_entry).setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("productId", productId);
            Navigation.findNavController(view).navigate(R.id.action_productDetail_to_reviews, args);
        });

        // 加入购物车
        btnAddToCart.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct != null) {
                viewModel.addToCart(sessionManager.getUserId(), currentProduct.getId(), 1);
                Toast.makeText(requireContext(), "已加入购物车", Toast.LENGTH_SHORT).show();
            }
        });

        // 立即购买: 先加入购物车，等 addToCart 确认后再跳转
        btnBuyNow.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(requireContext(), "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentProduct == null) return;
            viewModel.addToCart(sessionManager.getUserId(), currentProduct.getId(), 1);
            // 不等待结果，直接跳转到确认订单（addToCart 已经在后台执行）
            try {
                Navigation.findNavController(view).navigate(R.id.action_productDetail_to_orderConfirm);
            } catch (Exception e) {
                // ignore navigation error
            }
        });

        return view;
    }

    private void bindProduct(Product product) {
        tvPrice.setText(PriceFormatter.format(product.getPrice()));
        if (product.getOriginalPrice() > product.getPrice()) {
            tvOriginalPrice.setText("原价: " + PriceFormatter.format(product.getOriginalPrice()));
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
        }
        tvName.setText(product.getName());
        tvStock.setText(String.valueOf(product.getStock()));
        tvSales.setText(String.valueOf(product.getSalesCount()));
        tvDesc.setText(product.getDescription());

        List<String> imageUrls = ImageUrlUtil.fromJson(product.getImageUrls());
        if (imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = new ArrayList<>();
            imageUrls.add("https://picsum.photos/400/500?random=999");
        }
        List<Banner> imageBanners = new ArrayList<>();
        for (String url : imageUrls) {
            Banner b = new Banner();
            b.setImageUrl(url);
            imageBanners.add(b);
        }
        BannerPagerAdapter imageAdapter = new BannerPagerAdapter(requireContext(), imageBanners);
        viewPagerImages.setAdapter(imageAdapter);
    }
}
