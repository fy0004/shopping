const express = require('express');
const Banner = require('../models/Banner');
const router = express.Router();

// Banner 列表
router.get('/', async (req, res) => {
  try {
    const banners = await Banner.findAll({ order: [['sort_order', 'ASC']] });
    res.json({ code: 200, data: banners });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
