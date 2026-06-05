const express = require('express');
const { userAuth } = require('../middleware/auth');
const Order = require('../models/Order');
const OrderItem = require('../models/OrderItem');
const Product = require('../models/Product');
const CartItem = require('../models/CartItem');
const Address = require('../models/Address');
const { Op } = require('sequelize');

const router = express.Router();

// 生成订单号
function generateOrderNo() {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  const h = String(now.getHours()).padStart(2, '0');
  const mi = String(now.getMinutes()).padStart(2, '0');
  const s = String(now.getSeconds()).padStart(2, '0');
  const r = String(Math.floor(Math.random() * 10000)).padStart(4, '0');
  return `SS${y}${m}${d}${h}${mi}${s}${r}`;
}

// 创建订单
router.post('/', userAuth, async (req, res) => {
  try {
    const { addressId, paymentMethod } = req.body;

    // 获取地址
    const address = await Address.findOne({
      where: { id: addressId, user_id: req.userId }
    });
    if (!address) return res.json({ code: 400, message: '请选择收货地址' });

    // 获取购物车选中项
    const cartItems = await CartItem.findAll({
      where: { user_id: req.userId, is_selected: true },
      include: [Product]
    });
    if (cartItems.length === 0) {
      return res.json({ code: 400, message: '没有可结算的商品' });
    }

    // 计算总价并检查库存
    let total = 0;
    const orderItems = [];
    for (const ci of cartItems) {
      if (!ci.Product || ci.Product.stock < ci.quantity) {
        return res.json({ code: 400, message: `${ci.Product?.name || '商品'} 库存不足` });
      }
      total += parseFloat(ci.Product.price) * ci.quantity;
      orderItems.push({
        product_id: ci.product_id,
        product_name: ci.Product.name,
        product_image: ci.Product.image_urls,
        price: ci.Product.price,
        quantity: ci.quantity
      });
    }

    // 创建订单
    const order = await Order.create({
      order_no: generateOrderNo(),
      user_id: req.userId,
      address_snapshot: JSON.stringify(address.toJSON()),
      status: 'PENDING',
      total_amount: total,
      payment_method: paymentMethod || null
    });

    // 创建订单明细
    for (const oi of orderItems) {
      oi.order_id = order.id;
      await OrderItem.create(oi);
    }

    // 扣库存
    for (const ci of cartItems) {
      await Product.update(
        { stock: ci.Product.stock - ci.quantity },
        { where: { id: ci.product_id } }
      );
    }

    // 清空购物车选中项
    await CartItem.destroy({
      where: { user_id: req.userId, is_selected: true }
    });

    res.json({ code: 200, message: '下单成功', data: { orderId: order.id, orderNo: order.order_no } });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 我的订单列表
router.get('/', userAuth, async (req, res) => {
  try {
    const { status } = req.query;
    const where = { user_id: req.userId };
    if (status) where.status = status;

    const orders = await Order.findAll({
      where,
      order: [['created_at', 'DESC']]
    });
    res.json({ code: 200, data: orders });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 订单详情
router.get('/:id', userAuth, async (req, res) => {
  try {
    const order = await Order.findOne({
      where: { id: req.params.id, user_id: req.userId }
    });
    if (!order) return res.json({ code: 404, message: '订单不存在' });

    const items = await OrderItem.findAll({ where: { order_id: order.id } });

    res.json({ code: 200, data: { order, items } });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 支付
router.put('/:id/pay', userAuth, async (req, res) => {
  try {
    const order = await Order.findOne({
      where: { id: req.params.id, user_id: req.userId, status: 'PENDING' }
    });
    if (!order) return res.json({ code: 400, message: '订单状态不正确' });

    order.status = 'PAID';
    order.payment_method = req.body.paymentMethod || 'ALIPAY';
    order.updated_at = new Date();
    await order.save();

    res.json({ code: 200, message: '支付成功' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 取消订单
router.put('/:id/cancel', userAuth, async (req, res) => {
  try {
    const order = await Order.findOne({
      where: { id: req.params.id, user_id: req.userId, status: 'PENDING' }
    });
    if (!order) return res.json({ code: 400, message: '只能取消待付款的订单' });

    // 恢复库存
    const items = await OrderItem.findAll({ where: { order_id: order.id } });
    for (const item of items) {
      await Product.increment('stock', { by: item.quantity, where: { id: item.product_id } });
    }

    order.status = 'CANCELLED';
    order.updated_at = new Date();
    await order.save();

    res.json({ code: 200, message: '订单已取消' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 确认收货
router.put('/:id/confirm', userAuth, async (req, res) => {
  try {
    const order = await Order.findOne({
      where: { id: req.params.id, user_id: req.userId, status: 'SHIPPED' }
    });
    if (!order) return res.json({ code: 400, message: '只能确认已发货的订单' });

    // 增加销量
    const items = await OrderItem.findAll({ where: { order_id: order.id } });
    for (const item of items) {
      await Product.increment('sales_count', { by: item.quantity, where: { id: item.product_id } });
    }

    order.status = 'COMPLETED';
    order.updated_at = new Date();
    await order.save();

    res.json({ code: 200, message: '已确认收货' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
