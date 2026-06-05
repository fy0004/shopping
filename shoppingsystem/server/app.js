const express = require('express');
const cors = require('cors');
const path = require('path');
require('dotenv').config();

const sequelize = require('./config/database');

// 路由
const authRoutes = require('./routes/auth');
const productRoutes = require('./routes/product');
const categoryRoutes = require('./routes/category');
const bannerRoutes = require('./routes/banner');
const cartRoutes = require('./routes/cart');
const addressRoutes = require('./routes/address');
const orderRoutes = require('./routes/order');
const reviewRoutes = require('./routes/review');
const adminRoutes = require('./routes/admin');

const app = express();

// 中间件
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 静态文件（上传临时目录）
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// 路由注册
app.use('/api/auth', authRoutes);
app.use('/api/products', productRoutes);
app.use('/api/product', productRoutes); // 兼容 getProduct/:id 等变体
app.use('/api/categories', categoryRoutes);
app.use('/api/banners', bannerRoutes);
app.use('/api/cart', cartRoutes);
app.use('/api/addresses', addressRoutes);
app.use('/api/orders', orderRoutes);
app.use('/api/reviews', reviewRoutes);
app.use('/api/admin', adminRoutes);

// 健康检查
app.get('/api/health', (req, res) => {
  res.json({ code: 200, message: 'OK', timestamp: new Date().toISOString() });
});

// 404
app.use((req, res) => {
  res.status(404).json({ code: 404, message: '接口不存在' });
});

// 错误处理
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ code: 500, message: '服务器内部错误' });
});

const PORT = process.env.PORT || 3000;

// 同步数据库并启动
sequelize.sync({ alter: false }).then(() => {
  console.log('Database synced.');
  app.listen(PORT, () => {
    console.log(`Shopping server running on port ${PORT}`);
    console.log(`API: http://localhost:${PORT}/api`);
  });
}).catch(err => {
  console.error('Database sync failed:', err);
});

module.exports = app;
