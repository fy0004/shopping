const express = require('express');
const { userAuth } = require('../middleware/auth');
const CartItem = require('../models/CartItem');
const Product = require('../models/Product');
const router = express.Router();

// 我的购物车
router.get('/', userAuth, async (req, res) => {
  try {
    const items = await CartItem.findAll({
      where: { user_id: req.userId },
      include: [{ model: Product, attributes: ['id', 'name', 'price', 'image_urls', 'stock'] }],
      order: [['id', 'DESC']]
    });
    res.json({ code: 200, data: items });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 加入购物车
router.post('/', userAuth, async (req, res) => {
  try {
    const { productId, quantity = 1 } = req.body;

    const product = await Product.findByPk(productId);
    if (!product || !product.is_on_sale) {
      return res.json({ code: 404, message: '商品不存在或已下架' });
    }

    // 检查是否已存在
    let item = await CartItem.findOne({
      where: { user_id: req.userId, product_id: productId }
    });
    if (item) {
      item.quantity += quantity;
      await item.save();
    } else {
      item = await CartItem.create({
        user_id: req.userId,
        product_id: productId,
        quantity,
        is_selected: true
      });
    }

    res.json({ code: 200, message: '加入购物车成功', data: item });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 更新购物车项（数量/选中状态）
router.put('/:id', userAuth, async (req, res) => {
  try {
    const item = await CartItem.findOne({
      where: { id: req.params.id, user_id: req.userId }
    });
    if (!item) return res.json({ code: 404, message: '购物车项不存在' });

    if (req.body.quantity !== undefined) item.quantity = req.body.quantity;
    if (req.body.isSelected !== undefined) item.is_selected = req.body.isSelected;
    await item.save();

    res.json({ code: 200, data: item });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 删除购物车项
router.delete('/:id', userAuth, async (req, res) => {
  try {
    await CartItem.destroy({
      where: { id: req.params.id, user_id: req.userId }
    });
    res.json({ code: 200, message: '已删除' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 批量删除选中项
router.delete('/batch/selected', userAuth, async (req, res) => {
  try {
    await CartItem.destroy({
      where: { user_id: req.userId, is_selected: true }
    });
    res.json({ code: 200, message: '已删除选中项' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 购物车数量
router.get('/count', userAuth, async (req, res) => {
  try {
    const count = await CartItem.count({ where: { user_id: req.userId } });
    res.json({ code: 200, data: count });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
