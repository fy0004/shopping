const express = require('express');
const { userAuth } = require('../middleware/auth');
const Review = require('../models/Review');
const Product = require('../models/Product');
const User = require('../models/User');
const router = express.Router();

// 商品评价列表
router.get('/product/:productId', async (req, res) => {
  try {
    const reviews = await Review.findAll({
      where: { product_id: req.params.productId },
      include: [{ model: User, attributes: ['id', 'nickname', 'avatar_url'] }],
      order: [['created_at', 'DESC']]
    });
    res.json({ code: 200, data: reviews });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 提交评价
router.post('/', userAuth, async (req, res) => {
  try {
    const { productId, orderId, rating, content } = req.body;

    // 检查是否已评价
    const existing = await Review.findOne({
      where: { user_id: req.userId, product_id: productId, order_id: orderId }
    });
    if (existing) {
      return res.json({ code: 400, message: '该订单已评价过此商品' });
    }

    await Review.create({
      user_id: req.userId,
      product_id: productId,
      order_id: orderId,
      rating: rating || 5,
      content: content || ''
    });

    // 更新商品平均评分
    const { count, rows } = await Review.findAndCountAll({ where: { product_id: productId } });
    const avgRating = rows.reduce((sum, r) => sum + r.rating, 0) / count;
    await Product.update(
      { rating: avgRating, rating_count: count },
      { where: { id: productId } }
    );

    res.json({ code: 200, message: '评价提交成功' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
