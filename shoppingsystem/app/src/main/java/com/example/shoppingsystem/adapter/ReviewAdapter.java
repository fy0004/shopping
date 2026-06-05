package com.example.shoppingsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.model.ReviewWithUser;
import com.example.shoppingsystem.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Context context;
    private List<ReviewWithUser> reviews = new ArrayList<>();

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(List<ReviewWithUser> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewWithUser review = reviews.get(position);
        holder.tvUserName.setText(review.getUserName() != null ? review.getUserName() : "匿名用户");
        holder.tvContent.setText(review.getContent());
        holder.tvDate.setText(DateUtils.formatShort(review.getCreatedAt()));
        holder.tvRating.setText(String.valueOf(review.getRating()));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvRating, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_review_user);
            tvRating = itemView.findViewById(R.id.tv_review_rating);
            tvContent = itemView.findViewById(R.id.tv_review_content);
            tvDate = itemView.findViewById(R.id.tv_review_date);
        }
    }
}
