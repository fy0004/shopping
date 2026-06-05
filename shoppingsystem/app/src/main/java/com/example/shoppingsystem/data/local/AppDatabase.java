package com.example.shoppingsystem.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.shoppingsystem.data.local.dao.AddressDao;
import com.example.shoppingsystem.data.local.dao.BannerDao;
import com.example.shoppingsystem.data.local.dao.CartDao;
import com.example.shoppingsystem.data.local.dao.CategoryDao;
import com.example.shoppingsystem.data.local.dao.OrderDao;
import com.example.shoppingsystem.data.local.dao.OrderItemDao;
import com.example.shoppingsystem.data.local.dao.ProductDao;
import com.example.shoppingsystem.data.local.dao.ReviewDao;
import com.example.shoppingsystem.data.local.dao.UserDao;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.CartItem;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Order;
import com.example.shoppingsystem.data.local.entity.OrderItem;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.Review;
import com.example.shoppingsystem.data.local.entity.User;

@Database(entities = {
        User.class,
        Category.class,
        Product.class,
        Banner.class,
        CartItem.class,
        Address.class,
        Order.class,
        OrderItem.class,
        Review.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "shopping_system.db"
                    ).fallbackToDestructiveMigration()
                     .allowMainThreadQueries()
                     .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract BannerDao bannerDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();
    public abstract OrderItemDao orderItemDao();
    public abstract AddressDao addressDao();
    public abstract ReviewDao reviewDao();
}
