const express = require('express');
const multer = require('multer');
const path = require('path');
const Product = require('../models/Product');
const Category = require('../models/Category');
const ossClient = require('../config/oss');

const router = express.Router();

// multer 配置（本地临时存储）
const upload = multer({
  storage: multer.diskStorage({
    destination: path.join(__dirname, '..', 'uploads'),
    filename: (req, file, cb) => {
      cb(null, Date.now() + '-' + Math.round(Math.random() * 1E9) + path.extname(file.originalname));
    }
  }),
  limits: { fileSize: 10 * 1024 * 1024 } // 10MB
});

// 商品列表（分页+分类筛选）
router.get('/', async (req, res) => {
  try {
    const { categoryId, page = 1, size = 20 } = req.query;
    const where = { is_on_sale: true };
    if (categoryId) where.category_id = categoryId;

    const { count, rows } = await Product.findAndCountAll({
      where,
      include: [{ model: Category, attributes: ['id', 'name'] }],
      order: [['sales_count', 'DESC']],
      limit: parseInt(size),
      offset: (parseInt(page) - 1) * parseInt(size)
    });

    res.json({
      code: 200,
      data: {
        list: rows,
        total: count,
        page: parseInt(page),
        size: parseInt(size)
      }
    });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 商品搜索
router.get('/search', async (req, res) => {
  try {
    const { keyword, page = 1, size = 20 } = req.query;
    const { Op } = require('sequelize');
    const { count, rows } = await Product.findAndCountAll({
      where: {
        name: { [Op.like]: `%${keyword}%` },
        is_on_sale: true
      },
      include: [{ model: Category, attributes: ['id', 'name'] }],
      order: [['sales_count', 'DESC']],
      limit: parseInt(size),
      offset: (parseInt(page) - 1) * parseInt(size)
    });

    res.json({ code: 200, data: { list: rows, total: count } });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 热销商品
router.get('/hot', async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 20;
    const products = await Product.findAll({
      where: { is_on_sale: true },
      order: [['sales_count', 'DESC']],
      limit
    });
    res.json({ code: 200, data: products });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 商品详情
router.get('/:id', async (req, res) => {
  try {
    const product = await Product.findByPk(req.params.id, {
      include: [{ model: Category, attributes: ['id', 'name'] }]
    });
    if (!product) return res.json({ code: 404, message: '商品不存在' });
    res.json({ code: 200, data: product });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 上传图片到 OSS（内部/管理用）
router.post('/upload', upload.single('image'), async (req, res) => {
  try {
    if (!req.file) return res.json({ code: 400, message: '请选择图片文件' });

    const ossName = `products/${Date.now()}-${req.file.originalname}`;
    const result = await ossClient.put(ossName, req.file.path);

    // 清理本地临时文件
    const fs = require('fs');
    fs.unlink(req.file.path, () => {});

    res.json({ code: 200, data: { url: result.url, name: ossName } });
  } catch (err) {
    res.json({ code: 500, message: '上传失败: ' + err.message });
  }
});

module.exports = router;
