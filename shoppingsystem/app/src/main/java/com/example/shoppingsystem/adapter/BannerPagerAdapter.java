package com.example.shoppingsystem.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.shoppingsystem.data.local.entity.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.ViewHolder> {

    private final Context context;
    private List<Banner> banners = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ViewPager2 viewPager;
    private Runnable autoScrollRunnable;

    public BannerPagerAdapter(Context context, List<Banner> banners) {
        this.context = context;
        if (banners != null) this.banners.addAll(banners);
    }

    public void setBanners(List<Banner> newBanners) {
        this.banners.clear();
        if (newBanners != null) this.banners.addAll(newBanners);
        notifyDataSetChanged();
        if (viewPager != null && !banners.isEmpty()) {
            viewPager.setCurrentItem(getMiddlePosition(), false);
            startAutoScroll();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (banners.isEmpty()) return;
        Banner banner = banners.get(position % banners.size());
        Glide.with(context).load(banner.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return banners.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    private int getMiddlePosition() {
        if (banners.isEmpty()) return 0;
        int half = Integer.MAX_VALUE / 2;
        return half - (half % banners.size());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        // ViewPager2 内部 RecyclerView 继承自 RecyclerView，所以用 parent 判断
        ViewGroup parent = (ViewGroup) recyclerView.getParent();
        if (parent instanceof ViewPager2) {
            viewPager = (ViewPager2) parent;
        }
        if (viewPager != null && !banners.isEmpty()) {
            viewPager.setCurrentItem(getMiddlePosition(), false);
            startAutoScroll();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        stopAutoScroll();
        viewPager = null;
    }

    private void startAutoScroll() {
        stopAutoScroll();
        if (banners.size() <= 1) return;
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager != null && !banners.isEmpty()) {
                    int next = viewPager.getCurrentItem() + 1;
                    viewPager.setCurrentItem(next, true);
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(autoScrollRunnable, 3000);
    }

    public void stopAutoScroll() {
        if (autoScrollRunnable != null) {
            handler.removeCallbacks(autoScrollRunnable);
            autoScrollRunnable = null;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
