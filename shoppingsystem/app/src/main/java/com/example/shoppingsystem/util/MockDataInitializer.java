package com.example.shoppingsystem.util;

import android.util.Log;

import com.example.shoppingsystem.data.local.AppDatabase;
import com.example.shoppingsystem.data.local.entity.Address;
import com.example.shoppingsystem.data.local.entity.Banner;
import com.example.shoppingsystem.data.local.entity.Category;
import com.example.shoppingsystem.data.local.entity.Product;
import com.example.shoppingsystem.data.local.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 首次启动时向 Room 数据库填充演示数据。
 */
public class MockDataInitializer {

    private static final String TAG = "MockDataInitializer";

    public static void init(AppDatabase database) {
        // Check if data already exists
        if (database.userDao().findByPhone("13800000000") != null) {
            Log.d(TAG, "Mock data already initialized, skipping.");
            return;
        }

        Log.d(TAG, "Initializing mock data...");

        // ========== Users ==========
        // Test user
        User testUser = new User("13800000000",
                PasswordUtils.hash("123456"),
                "测试用户",
                "USER");
        testUser.setAvatarUrl("https://picsum.photos/200/200?random=100");
        database.userDao().insert(testUser);

        // Admin user
        User adminUser = new User("13900000000",
                PasswordUtils.hash("admin123"),
                "管理员",
                "ADMIN");
        adminUser.setAvatarUrl("https://picsum.photos/200/200?random=101");
        database.userDao().insert(adminUser);

        long userId = database.userDao().login("13800000000", PasswordUtils.hash("123456")).getId();

        // ========== Categories ==========
        List<Category> categories = Arrays.asList(
                new Category("手机数码", "ic_category_phone", 1),
                new Category("电脑办公", "ic_category_pc", 2),
                new Category("家用电器", "ic_category_appliance", 3),
                new Category("服饰鞋包", "ic_category_clothes", 4),
                new Category("美妆护肤", "ic_category_beauty", 5),
                new Category("食品生鲜", "ic_category_food", 6),
                new Category("图书文娱", "ic_category_book", 7),
                new Category("运动户外", "ic_category_sport", 8)
        );
        database.categoryDao().insertAll(categories);

        // ========== Banners ==========
        List<Banner> banners = Arrays.asList(
                new Banner("https://picsum.photos/800/300?random=200", "CATEGORY", 1, 1),
                new Banner("https://picsum.photos/800/300?random=201", "CATEGORY", 3, 2),
                new Banner("https://picsum.photos/800/300?random=202", "CATEGORY", 5, 3)
        );
        database.bannerDao().insertAll(banners);

        // ========== Products (30+ items across 8 categories) ==========
        List<Product> products = new ArrayList<>();

        // Category 1: 手机数码
        addProduct(products, 1, "iPhone 15 Pro Max 256GB", "A17 Pro 芯片，钛金属设计，4800万像素主摄，USB-C 接口",
                8999.00, 9999.00, 50, urls(300, 4));
        addProduct(products, 1, "华为 Mate 60 Pro", "麒麟 9000S 芯片，卫星通话，超感知影像，昆仑玻璃",
                6999.00, 6999.00, 30, urls(301, 4));
        addProduct(products, 1, "小米 14 Ultra", "骁龙 8 Gen 3，徕卡光学镜头，120W 超级快充",
                5999.00, 6499.00, 80, urls(302, 4));
        addProduct(products, 1, "OPPO Find X7 Ultra", "双潜望四摄影，哈苏人像，100W 超级闪充",
                4999.00, 5499.00, 40, urls(303, 4));

        // Category 2: 电脑办公
        addProduct(products, 2, "MacBook Pro 14英寸 M3 Pro", "18GB 统一内存，512GB 固态硬盘，Liquid Retina XDR 显示屏",
                14999.00, 16999.00, 20, urls(304, 4));
        addProduct(products, 2, "华为 MateBook X Pro 2024", "英特尔酷睿 Ultra 9，32GB 内存，2TB SSD，3.1K OLED 屏",
                11999.00, 12999.00, 15, urls(305, 4));
        addProduct(products, 2, "联想 ThinkPad X1 Carbon", "酷睿 i7-1365U，16GB 内存，512GB SSD，2.8K OLED",
                9999.00, 10999.00, 25, urls(306, 4));
        addProduct(products, 2, "iPad Pro 12.9英寸 M2", "M2 芯片，Mini-LED 显示屏，支持 Apple Pencil",
                8499.00, 8999.00, 35, urls(307, 4));

        // Category 3: 家用电器
        addProduct(products, 3, "戴森 V15 Detect 无绳吸尘器", "激光探测微尘，LCD 屏实时显示，60分钟续航",
                4990.00, 5490.00, 45, urls(308, 4));
        addProduct(products, 3, "小米空气净化器 4 Max", "CADR 800m³/h，适用96㎡，OLED 触控屏",
                2999.00, 3299.00, 60, urls(309, 4));
        addProduct(products, 3, "海尔 500L 冰箱 多门", "风冷无霜，干湿分储，一级能效，变频静音",
                4599.00, 5299.00, 10, urls(310, 4));
        addProduct(products, 3, "美的变频空调 1.5匹 新一级能效", "全直流变频，自清洁，智能WiFi控制",
                2899.00, 3299.00, 55, urls(311, 4));

        // Category 4: 服饰鞋包
        addProduct(products, 4, "Nike Air Jordan 1 Retro High OG", "经典高帮板鞋，Air Sole 气垫，牛皮革鞋面",
                1299.00, 1499.00, 100, urls(312, 4));
        addProduct(products, 4, "Adidas Ultraboost Light 跑鞋", "Light BOOST 科技，Primeknit+ 鞋面，Continental 橡胶外底",
                1099.00, 1299.00, 80, urls(313, 4));
        addProduct(products, 4, "优衣库 高级轻型羽绒服", "750蓬松度，轻量保暖，便携收纳",
                499.00, 799.00, 200, urls(314, 4));
        addProduct(products, 4, "Coach 经典标志帆布手袋", "PVC配皮，内置拉链袋，可拆卸肩带",
                3200.00, 4200.00, 12, urls(315, 4));

        // Category 5: 美妆护肤
        addProduct(products, 5, "兰蔻 小黑瓶精华肌底液 50ml", "二裂酵母精华，修护肌底，提亮肤色",
                1100.00, 1200.00, 90, urls(316, 4));
        addProduct(products, 5, "SK-II 神仙水 230ml", "PITERA™ 精华，改善肤质，水润透亮",
                1590.00, 1790.00, 40, urls(317, 4));
        addProduct(products, 5, "雅诗兰黛 小棕瓶眼霜 15ml", "抗蓝光修护，淡化黑眼圈，滋润眼周",
                530.00, 580.00, 75, urls(318, 4));
        addProduct(products, 5, "MAC 子弹头口红 #316 Devoted to Chili", "柔雾质地，砖红色，持久不拔干",
                170.00, 190.00, 150, urls(319, 4));

        // Category 6: 食品生鲜
        addProduct(products, 6, "三只松鼠 坚果大礼包 1818g", "每日坚果混合装，10袋装，零食礼盒",
                139.00, 199.00, 500, urls(320, 4));
        addProduct(products, 6, "农夫山泉 矿泉水 550ml×24瓶", "天然弱碱性水，运动补水，整箱装",
                29.90, 39.90, 999, urls(321, 4));
        addProduct(products, 6, "良品铺子 肉肉零食大礼包 1500g", "12袋肉类零食组合，追剧解馋",
                89.00, 129.00, 300, urls(322, 4));
        addProduct(products, 6, "蒙牛 特仑苏纯牛奶 250ml×16盒", "高端纯牛奶，3.6g优质乳蛋白",
                65.00, 75.00, 800, urls(323, 4));

        // Category 7: 图书文娱
        addProduct(products, 7, "《三体》全集（三册）", "刘慈欣科幻巨著，雨果奖获奖作品，硬科幻巅峰",
                68.00, 99.00, 500, urls(324, 4));
        addProduct(products, 7, "《深入理解Java虚拟机》第3版", "周志明著，JVM原理与实践，程序员必读书籍",
                59.00, 79.00, 250, urls(325, 4));
        addProduct(products, 7, "Switch 任天堂 马力欧卡丁车8 豪华版", "多人竞速游戏，48条赛道，家庭娱乐",
                299.00, 349.00, 150, urls(326, 4));

        // Category 8: 运动户外
        addProduct(products, 8, "迪卡侬 户外登山包 40L", "轻量防水，多功能分区，透气背负系统",
                199.00, 299.00, 120, urls(327, 4));
        addProduct(products, 8, "Keep 瑜伽垫 加厚防滑 10mm", "TPE环保材质，双面防滑，附赠收纳绑带",
                89.00, 129.00, 400, urls(328, 4));
        addProduct(products, 8, "YONEX 羽毛球拍 天斧AX100ZZ", "全碳素纤维，进攻型，3U/G5",
                680.00, 880.00, 30, urls(329, 4));
        addProduct(products, 8, "探路者 冲锋衣 男 三合一", "防水防风透气，可拆卸内胆，秋冬户外",
                599.00, 899.00, 60, urls(330, 4));

        for (Product p : products) {
            database.productDao().insert(p);
        }

        // ========== Sample Address ==========
        Address addr = new Address(userId, "张三", "13800000000",
                "广东省", "深圳市", "南山区", "科技园路1号 创新大厦A座 1208室", true);
        database.addressDao().insert(addr);

        Address addr2 = new Address(userId, "张三", "13800000000",
                "北京市", "北京市", "朝阳区", "建国路88号 SOHO现代城 3号楼 502室", false);
        database.addressDao().insert(addr2);

        Log.d(TAG, "Mock data initialized successfully: " + products.size() + " products, 8 categories, 3 banners.");
    }

    private static void addProduct(List<Product> list, long categoryId, String name,
                                    String desc, double price, double origPrice, int stock, String urls) {
        Product p = new Product(categoryId, name, desc, price, origPrice, stock, urls);
        p.setSalesCount((int) (Math.random() * 500));
        p.setRating(4.0f + (float) Math.random() * 1.0f);
        p.setRatingCount((int) (Math.random() * 200) + 10);
        list.add(p);
    }

    private static String urls(int seed, int count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add("https://picsum.photos/400/500?random=" + (seed + i));
        }
        return ImageUrlUtil.toJson(list);
    }
}
