const express = require('express');
const bcrypt = require('bcryptjs');
const { adminAuth, generateToken } = require('../middleware/auth');
const User = require('../models/User');
const Product = require('../models/Product');
const Category = require('../models/Category');
const Order = require('../models/Order');
const OrderItem = require('../models/OrderItem');
const { Op } = require('sequelize');

const router = express.Router();

// 管理员登录
router.post('/login', async (req, res) => {
  try {
    const { phone, password } = req.body;
    const user = await User.findOne({ where: { phone, role: 'ADMIN' } });
    if (!user) return res.json({ code: 400, message: '管理员账号或密码错误' });

    const valid = await bcrypt.compare(password, user.password_hash);
    if (!valid) return res.json({ code: 400, message: '管理员账号或密码错误' });

    const token = generateToken(user.id, user.role);
    res.json({
      code: 200,
      message: '登录成功',
      data: { id: user.id, phone: user.phone, nickname: user.nickname, token }
    });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 仪表盘
router.get('/dashboard', adminAuth, async (req, res) => {
  try {
    const totalOrders = await Order.count();
    const totalRevenue = await Order.sum('total_amount', { where: { status: 'COMPLETED' } }) || 0;
    const totalUsers = await User.count({ where: { role: 'USER' } });
    const totalProducts = await Product.count();

    res.json({
      code: 200,
      data: { totalOrders, totalRevenue, totalUsers, totalProducts }
    });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// ========== 商品管理 ==========

// 全部商品
router.get('/products', adminAuth, async (req, res) => {
  try {
    const products = await Product.findAll({
      include: [{ model: Category, attributes: ['id', 'name'] }],
      order: [['created_at', 'DESC']]
    });
    res.json({ code: 200, data: products });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 获取字段（兼容 camelCase 和 snake_case）
function v(body, ...names) {
  for (const n of names) if (body[n] !== undefined) return body[n];
  return undefined;
}

// 新增商品
router.post('/products', adminAuth, async (req, res) => {
  try {
    const categoryId = v(req.body, 'categoryId', 'category_id');
    const name = v(req.body, 'name');
    const description = v(req.body, 'description') || '';
    const price = v(req.body, 'price');
    const originalPrice = v(req.body, 'originalPrice', 'original_price') || price;
    const stock = v(req.body, 'stock') || 0;
    const imageUrls = v(req.body, 'imageUrls', 'image_urls') || '[]';
    const product = await Product.create({
      category_id: categoryId,
      name,
      description: description || '',
      price,
      original_price: originalPrice || price,
      stock: stock || 0,
      image_urls: imageUrls || '[]',
      is_on_sale: true
    });
    res.json({ code: 200, data: product });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 编辑商品
router.put('/products/:id', adminAuth, async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id);
    if (!product) return res.json({ code: 404, message: '商品不存在' });

    const categoryId = v(req.body, 'categoryId', 'category_id');
    const name = v(req.body, 'name');
    const description = v(req.body, 'description');
    const price = v(req.body, 'price');
    const originalPrice = v(req.body, 'originalPrice', 'original_price');
    const stock = v(req.body, 'stock');
    const imageUrls = v(req.body, 'imageUrls', 'image_urls');
    const isOnSale = v(req.body, 'isOnSale', 'is_on_sale');
    if (categoryId !== undefined) product.category_id = categoryId;
    if (name !== undefined) product.name = name;
    if (description !== undefined) product.description = description;
    if (price !== undefined) product.price = price;
    if (originalPrice !== undefined) product.original_price = originalPrice;
    if (stock !== undefined) product.stock = stock;
    if (imageUrls !== undefined) product.image_urls = imageUrls;
    if (isOnSale !== undefined) product.is_on_sale = isOnSale;
    await product.save();

    res.json({ code: 200, data: product });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 删除商品
router.delete('/products/:id', adminAuth, async (req, res) => {
  try {
    await Product.destroy({ where: { id: req.params.id } });
    res.json({ code: 200, message: '已删除' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// ========== 订单管理 ==========

// 全部订单
router.get('/orders', adminAuth, async (req, res) => {
  try {
    const orders = await Order.findAll({
      include: [{ model: User, attributes: ['id', 'phone', 'nickname'] }],
      order: [['created_at', 'DESC']]
    });
    res.json({ code: 200, data: orders });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 修改订单状态
router.put('/orders/:id/status', adminAuth, async (req, res) => {
  try {
    const { status } = req.body;
    const order = await Order.findByPk(req.params.id);
    if (!order) return res.json({ code: 404, message: '订单不存在' });

    order.status = status;
    order.updated_at = new Date();
    await order.save();

    res.json({ code: 200, data: order });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// ========== 用户管理 ==========

// 全部用户
router.get('/users', adminAuth, async (req, res) => {
  try {
    const users = await User.findAll({
      where: { role: 'USER' },
      attributes: ['id', 'phone', 'nickname', 'avatar_url', 'status', 'created_at'],
      order: [['created_at', 'DESC']]
    });
    res.json({ code: 200, data: users });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 启用/禁用用户
router.put('/users/:id/status', adminAuth, async (req, res) => {
  try {
    const { status } = req.body;
    const user = await User.findByPk(req.params.id);
    if (!user) return res.json({ code: 404, message: '用户不存在' });

    user.status = status;
    await user.save();

    res.json({ code: 200, data: user });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
