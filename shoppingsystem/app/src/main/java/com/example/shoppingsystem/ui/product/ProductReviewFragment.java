package com.example.shoppingsystem.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.ReviewAdapter;

public class ProductReviewFragment extends Fragment {

    private RecyclerView rvReviews;
    private TextView tvAvgRating;
    private ReviewAdapter adapter;
    private ProductViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_reviews, container, false);

        rvReviews = view.findViewById(R.id.rv_reviews);
        tvAvgRating = view.findViewById(R.id.tv_avg_rating);

        adapter = new ReviewAdapter(requireContext());
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReviews.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        long productId = getArguments() != null ? getArguments().getLong("productId", -1) : -1;
        if (productId > 0) {
            viewModel.loadProductDetail(productId);
            viewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
                if (product != null) {
                    tvAvgRating.setText(String.format("%.1f", product.getRating()));
                }
            });
            viewModel.loadReviews(productId);
            viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
                adapter.setReviews(reviews);
            });
        }

        return view;
    }
}
