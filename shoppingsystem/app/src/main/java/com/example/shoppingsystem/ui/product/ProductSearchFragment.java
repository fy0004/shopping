package com.example.shoppingsystem.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.adapter.ProductLinearAdapter;

public class ProductSearchFragment extends Fragment {

    private RecyclerView rvResults;
    private ProductLinearAdapter adapter;
    private ProductViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_search, container, false);

        SearchView searchView = view.findViewById(R.id.search_view);
        rvResults = view.findViewById(R.id.rv_results);

        adapter = new ProductLinearAdapter(requireContext());
        adapter.setOnProductClickListener(product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.getId());
            Navigation.findNavController(view).navigate(R.id.action_productSearch_to_productDetail, args);
        });
        rvResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvResults.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.searchProducts(query).observe(getViewLifecycleOwner(),
                            products -> adapter.setProducts(products));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    viewModel.searchProducts(newText).observe(getViewLifecycleOwner(),
                            products -> adapter.setProducts(products));
                }
                return true;
            }
        });

        return view;
    }
}
