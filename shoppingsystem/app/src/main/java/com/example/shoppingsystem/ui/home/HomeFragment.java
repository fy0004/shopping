package com.example.shoppingsystem.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.shoppingsystem.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private RecyclerView rvHome;
    private HomeAdapter adapter;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvHome = view.findViewById(R.id.rv_home);

        // Toolbar + 搜索图标
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_home);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Navigation.findNavController(view).navigate(R.id.action_home_to_productSearch);
                return true;
            }
            return false;
        });

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvHome.setLayoutManager(layoutManager);

        adapter = new HomeAdapter(requireContext());
        rvHome.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getBanners().observe(getViewLifecycleOwner(), banners -> adapter.setBanners(banners));
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> adapter.setCategories(categories));
        viewModel.getGoodsList().observe(getViewLifecycleOwner(), products -> adapter.setProducts(products));

        adapter.setOnProductClickListener(product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.getId());
            Navigation.findNavController(view).navigate(R.id.action_home_to_productDetail, args);
        });

        adapter.setOnCategoryClickListener(category -> {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.categoryFragment);
            }
            view.postDelayed(() -> {
                Bundle result = new Bundle();
                result.putLong("selectedCategoryId", category.getId());
                getParentFragmentManager().setFragmentResult("category_selected", result);
            }, 200);
        });

        adapter.setOnBannerClickListener(banner -> {});

        return view;
    }

}
