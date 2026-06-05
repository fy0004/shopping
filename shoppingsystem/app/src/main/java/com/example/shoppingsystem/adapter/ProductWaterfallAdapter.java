package com.example.shoppingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.R;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.util.ImageUrlUtil;
import com.example.shoppingsystem.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProductWaterfallAdapter extends RecyclerView.Adapter<ProductWaterfallAdapter.ViewHolder> {

    private final Context context;
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public ProductWaterfallAdapter(Context context) {
        this.context = context;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> newProducts) {
        int startPos = this.products.size();
        this.products.addAll(newProducts);
        notifyItemRangeInserted(startPos, newProducts.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_waterfall, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(PriceFormatter.format(product.getPrice()));
        holder.tvSales.setText("已售 " + product.getSalesCount());

        String imgUrl = ImageUrlUtil.getFirstImage(product.getImageUrls());
        Glide.with(context).load(imgUrl).into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvSales;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            tvSales = itemView.findViewById(R.id.tv_product_sales);
        }
    }

    public interface OnProductClickListener {
        void onClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }
}
