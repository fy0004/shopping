const express = require('express');
const Category = require('../models/Category');
const router = express.Router();

// 分类列表
router.get('/', async (req, res) => {
  try {
    const categories = await Category.findAll({ order: [['sort_order', 'ASC']] });
    res.json({ code: 200, data: categories });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
