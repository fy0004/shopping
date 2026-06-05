const bcrypt = require('bcryptjs');
const sequelize = require('./config/database');

// 导入所有模型
const User = require('./models/User');
const Category = require('./models/Category');
const Product = require('./models/Product');
const Banner = require('./models/Banner');

async function seed() {
  // 同步数据库表
  await sequelize.sync({ force: false });

  console.log('Seeding data...');

  // ========== 用户 ==========
  const adminHash = await bcrypt.hash('admin123', 10);
  await User.findOrCreate({
    where: { phone: '13900000000' },
    defaults: { phone: '13900000000', password_hash: adminHash, nickname: '管理员', role: 'ADMIN', status: 'ACTIVE' }
  });

  const userHash = await bcrypt.hash('123456', 10);
  await User.findOrCreate({
    where: { phone: '13800000000' },
    defaults: { phone: '13800000000', password_hash: userHash, nickname: '测试用户', role: 'USER', status: 'ACTIVE' }
  });

  // ========== 分类 ==========
  const catData = [
    { name: '手机数码', icon_res_name: 'ic_category_phone', sort_order: 1 },
    { name: '电脑办公', icon_res_name: 'ic_category_pc', sort_order: 2 },
    { name: '家用电器', icon_res_name: 'ic_category_appliance', sort_order: 3 },
    { name: '服饰鞋包', icon_res_name: 'ic_category_clothes', sort_order: 4 },
    { name: '美妆护肤', icon_res_name: 'ic_category_beauty', sort_order: 5 },
    { name: '食品生鲜', icon_res_name: 'ic_category_food', sort_order: 6 },
    { name: '图书文娱', icon_res_name: 'ic_category_book', sort_order: 7 },
    { name: '运动户外', icon_res_name: 'ic_category_sport', sort_order: 8 }
  ];
  for (const c of catData) {
    await Category.findOrCreate({ where: { name: c.name }, defaults: c });
  }

  // ========== Banner ==========
  const bannerData = [
    { image_url: 'https://picsum.photos/800/300?random=200', link_type: 'CATEGORY', link_value: 1, sort_order: 1 },
    { image_url: 'https://picsum.photos/800/300?random=201', link_type: 'CATEGORY', link_value: 3, sort_order: 2 },
    { image_url: 'https://picsum.photos/800/300?random=202', link_type: 'CATEGORY', link_value: 5, sort_order: 3 }
  ];
  for (const b of bannerData) {
    await Banner.findOrCreate({ where: { image_url: b.image_url }, defaults: b });
  }

  // ========== 商品 ==========
  const productData = [
    [1, 'iPhone 15 Pro Max 256GB', 'A17 Pro芯片，钛金属设计，4800万主摄', 8999.00, 9999.00, 50],
    [1, '华为 Mate 60 Pro', '麒麟9000S，卫星通话，昆仑玻璃', 6999.00, 6999.00, 30],
    [1, '小米 14 Ultra', '骁龙8Gen3，徕卡光学，120W快充', 5999.00, 6499.00, 80],
    [1, 'OPPO Find X7 Ultra', '双潜望四摄影，哈苏人像', 4999.00, 5499.00, 40],
    [2, 'MacBook Pro 14 M3 Pro', '18GB内存，512GB SSD，XDR屏', 14999.00, 16999.00, 20],
    [2, '华为 MateBook X Pro', 'Ultra 9，32GB，2TB，3.1K OLED', 11999.00, 12999.00, 15],
    [2, 'ThinkPad X1 Carbon', 'i7-1365U，16GB，2.8K OLED', 9999.00, 10999.00, 25],
    [2, 'iPad Pro 12.9 M2', 'M2芯片，Mini-LED，支持笔', 8499.00, 8999.00, 35],
    [3, '戴森 V15 无绳吸尘器', '激光探测，LCD屏，60分钟续航', 4990.00, 5490.00, 45],
    [3, '小米空气净化器 4 Max', 'CADR 800m³/h，OLED触控', 2999.00, 3299.00, 60],
    [3, '海尔 500L 多门冰箱', '风冷无霜，一级能效', 4599.00, 5299.00, 10],
    [3, '美的 1.5匹变频空调', '全直流变频，自清洁，WiFi', 2899.00, 3299.00, 55],
    [4, 'Nike AJ1 Retro High OG', '经典高帮，Air Sole气垫', 1299.00, 1499.00, 100],
    [4, 'Adidas Ultraboost Light', 'Light BOOST，Continental外底', 1099.00, 1299.00, 80],
    [4, '优衣库 高级轻型羽绒服', '750蓬松度，轻量保暖', 499.00, 799.00, 200],
    [4, 'Coach 经典帆布手袋', 'PVC配皮，可拆卸肩带', 3200.00, 4200.00, 12],
    [5, '兰蔻 小黑瓶 50ml', '二裂酵母，修护肌底', 1100.00, 1200.00, 90],
    [5, 'SK-II 神仙水 230ml', 'PITERA精华，水润透亮', 1590.00, 1790.00, 40],
    [5, '雅诗兰黛 小棕瓶眼霜', '抗蓝光，淡化黑眼圈', 530.00, 580.00, 75],
    [5, 'MAC 子弹头 #316', '柔雾质地，砖红色', 170.00, 190.00, 150],
    [6, '三只松鼠 坚果大礼包', '1818g，每日坚果10袋', 139.00, 199.00, 500],
    [6, '农夫山泉 550ml×24瓶', '天然弱碱性水', 29.90, 39.90, 999],
    [6, '蒙牛 特仑苏 250ml×16', '3.6g优质乳蛋白', 65.00, 75.00, 800],
    [7, '《三体》全集', '刘慈欣科幻巨著，雨果奖', 68.00, 99.00, 500],
    [7, '《深入理解JVM》第3版', '周志明，JVM原理与实践', 59.00, 79.00, 250],
    [8, '迪卡侬 登山包 40L', '轻量防水，透气背负', 199.00, 299.00, 120],
    [8, 'Keep 瑜伽垫 10mm', 'TPE环保，双面防滑', 89.00, 129.00, 400],
    [8, 'YONEX 天斧AX100ZZ', '全碳素纤维，进攻型', 680.00, 880.00, 30],
    [8, '探路者 三合一冲锋衣', '防水防风透气，可拆卸', 599.00, 899.00, 60]
  ];

  for (const [catId, name, desc, price, origPrice, stock] of productData) {
    const urls = JSON.stringify([
      `https://picsum.photos/400/500?random=${catId * 100 + productData.indexOf([catId, name])}`,
      `https://picsum.photos/400/500?random=${catId * 100 + productData.indexOf([catId, name]) + 1}`
    ]);
    await Product.findOrCreate({
      where: { name },
      defaults: {
        category_id: catId, name, description: desc,
        price, original_price: origPrice, stock,
        image_urls: urls,
        sales_count: Math.floor(Math.random() * 500),
        rating: 4.0 + Math.random(),
        rating_count: Math.floor(Math.random() * 200) + 10,
        is_on_sale: true
      }
    });
  }

  console.log('Seed data completed!');
  process.exit(0);
}

seed().catch(err => {
  console.error('Seed failed:', err);
  process.exit(1);
});
