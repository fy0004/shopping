package com.example.shoppingsystem.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.shoppingsystem.R;
import com.example.shoppingsystem.ShoppingApplication;
import com.example.shoppingsystem.data.repository.CartRepository;
import com.example.shoppingsystem.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;

    // 不需要显示底部导航栏的页面 ID
    private final Set<Integer> hideBottomNavDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = SessionManager.getInstance(this);

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Setup navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // 顶层目的地
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.categoryFragment);
        topLevelDestinations.add(R.id.cartFragment);
        topLevelDestinations.add(R.id.profileFragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();
        NavigationUI.setupWithNavController(bottomNavigation, navController);

        // 配置所有非顶层页面 — 隐藏底部导航栏
        hideBottomNavDestinations.add(R.id.productDetailFragment);
        hideBottomNavDestinations.add(R.id.productSearchFragment);
        hideBottomNavDestinations.add(R.id.productReviewFragment);
        hideBottomNavDestinations.add(R.id.orderConfirmFragment);
        hideBottomNavDestinations.add(R.id.orderListFragment);
        hideBottomNavDestinations.add(R.id.orderDetailFragment);
        hideBottomNavDestinations.add(R.id.paymentFragment);
        hideBottomNavDestinations.add(R.id.addressListFragment);
        hideBottomNavDestinations.add(R.id.addressEditFragment);
        hideBottomNavDestinations.add(R.id.settingsFragment);
        hideBottomNavDestinations.add(R.id.loginFragment);
        hideBottomNavDestinations.add(R.id.registerFragment);
        hideBottomNavDestinations.add(R.id.adminLoginFragment);
        hideBottomNavDestinations.add(R.id.adminDashboardFragment);
        hideBottomNavDestinations.add(R.id.adminProductListFragment);
        hideBottomNavDestinations.add(R.id.adminProductEditFragment);
        hideBottomNavDestinations.add(R.id.adminOrderListFragment);
        hideBottomNavDestinations.add(R.id.adminUserListFragment);
        hideBottomNavDestinations.add(R.id.adminManagementFragment);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            if (hideBottomNavDestinations.contains(destId)) {
                bottomNavigation.setVisibility(android.view.View.GONE);
            } else {
                bottomNavigation.setVisibility(android.view.View.VISIBLE);
            }
        });

        // 延迟设置角标（等数据库准备好）
        bottomNavigation.postDelayed(this::setupCartBadge, 500);
    }

    private void setupCartBadge() {
        if (!sessionManager.isLoggedIn()) return;
        long userId = sessionManager.getUserId();
        if (userId <= 0) return;
        CartRepository cartRepo = ShoppingApplication.getInstance().getCartRepository();
        cartRepo.getCartCount(userId).observe(this, count -> {
            try {
                if (count != null && count > 0) {
                    bottomNavigation.getOrCreateBadge(R.id.cartFragment).setNumber(count);
                } else {
                    bottomNavigation.removeBadge(R.id.cartFragment);
                }
            } catch (Exception ignored) {}
        });
    }

    public void refreshCartBadge() {
        bottomNavigation.removeBadge(R.id.cartFragment);
        bottomNavigation.postDelayed(this::setupCartBadge, 500);
    }

    public NavController getNavController() {
        return navController;
    }
}
