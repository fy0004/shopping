package com.example.shoppingsystem.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.BannerPagerAdapter;
import com.example.shoppingsystem.adapter.CategoryGridAdapter;
import com.example.shoppingsystem.adapter.ProductWaterfallAdapter;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.example.shoppingsystem.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BANNER = 0;
    private static final int TYPE_CATEGORY = 1;
    private static final int TYPE_PRODUCT = 2;

    private final Context context;
    private List<Banner> banners = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    private OnProductClickListener productClickListener;
    private OnBannerClickListener bannerClickListener;
    private OnCategoryClickListener categoryClickListener;

    public HomeAdapter(Context context) {
        this.context = context;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners != null ? banners : new ArrayList<>();
        notifyItemChanged(0);
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyItemChanged(1);
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_BANNER;
        if (position == 1) return TYPE_CATEGORY;
        return TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case TYPE_BANNER:
                return new BannerViewHolder(inflater.inflate(R.layout.item_home_banner, parent, false));
            case TYPE_CATEGORY:
                return new CategoryViewHolder(inflater.inflate(R.layout.item_category_grid, parent, false));
            default:
                View view = inflater.inflate(R.layout.item_product_waterfall, parent, false);
                return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).bind(banners);
                break;
            case TYPE_CATEGORY:
                ((CategoryViewHolder) holder).bind(categories);
                break;
            case TYPE_PRODUCT:
                int productIndex = position - 2;
                ((ProductViewHolder) holder).bind(products.get(productIndex));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 2 + products.size(); // banner + category + products
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // Banner 和分类头部占满整行
        int viewType = holder.getItemViewType();
        if (viewType == TYPE_BANNER || viewType == TYPE_CATEGORY) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
        }
    }

    // ========== ViewHolders ==========

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ViewPager2 viewPager;

        BannerViewHolder(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewpager_banner);
        }

        void bind(List<Banner> banners) {
            if (banners.isEmpty()) return;
            BannerPagerAdapter adapter = new BannerPagerAdapter(context, banners);
            viewPager.setAdapter(adapter);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvCategories;

        CategoryViewHolder(View itemView) {
            super(itemView);
            rvCategories = itemView.findViewById(R.id.rv_categories);
        }

        void bind(List<Category> categories) {
            CategoryGridAdapter adapter = new CategoryGridAdapter(context, categories, category -> {
                if (categoryClickListener != null) categoryClickListener.onClick(category);
            });
            rvCategories.setLayoutManager(new GridLayoutManager(context, 4));
            rvCategories.setAdapter(adapter);
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvSales;

        ProductViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvSales = itemView.findViewById(R.id.tv_product_sales);
        }

        void bind(Product product) {
            tvName.setText(product.getName());
            tvPrice.setText(PriceFormatter.format(product.getPrice()));
            tvSales.setText("已售 " + product.getSalesCount());

            String imgUrl = ImageUrlUtil.getFirstImage(product.getImageUrls());
            Glide.with(context).load(imgUrl).placeholder(R.drawable.ic_launcher_foreground).into(ivImage);

            itemView.setOnClickListener(v -> {
                if (productClickListener != null) productClickListener.onClick(product);
            });
        }
    }

    // ========== Listener Interfaces ==========

    public interface OnProductClickListener {
        void onClick(Product product);
    }

    public interface OnBannerClickListener {
        void onClick(Banner banner);
    }

    public interface OnCategoryClickListener {
        void onClick(Category category);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.bannerClickListener = listener;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.categoryClickListener = listener;
    }
}
