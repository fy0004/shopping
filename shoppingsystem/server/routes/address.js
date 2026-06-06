const express = require('express');
const { userAuth } = require('../middleware/auth');
const Address = require('../models/Address');
const router = express.Router();

// 地址列表
router.get('/', userAuth, async (req, res) => {
  try {
    const addresses = await Address.findAll({
      where: { user_id: req.userId },
      order: [['is_default', 'DESC'], ['id', 'DESC']]
    });
    res.json({ code: 200, data: addresses });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 获取字段值（兼容 camelCase 和 snake_case）
function f(body, ...names) {
  for (const n of names) if (body[n] !== undefined) return body[n];
  return undefined;
}

// 新增地址
router.post('/', userAuth, async (req, res) => {
  try {
    const receiverName = f(req.body, 'receiverName', 'receiver_name');
    const phone = f(req.body, 'phone') || '';
    const province = f(req.body, 'province') || '';
    const city = f(req.body, 'city') || '';
    const district = f(req.body, 'district') || '';
    const detail = f(req.body, 'detail') || '';
    const isDefault = f(req.body, 'isDefault', 'is_default');

    if (isDefault) {
      await Address.update({ is_default: false }, { where: { user_id: req.userId } });
    }

    const addr = await Address.create({
      user_id: req.userId,
      receiver_name: receiverName || '',
      phone,
      province,
      city,
      district,
      detail,
      is_default: !!isDefault
    });

    res.json({ code: 200, data: addr });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 编辑地址
router.put('/:id', userAuth, async (req, res) => {
  try {
    const addr = await Address.findOne({
      where: { id: req.params.id, user_id: req.userId }
    });
    if (!addr) return res.json({ code: 404, message: '地址不存在' });

    const receiverName = f(req.body, 'receiverName', 'receiver_name');
    const phone = f(req.body, 'phone');
    const province = f(req.body, 'province');
    const city = f(req.body, 'city');
    const district = f(req.body, 'district');
    const detail = f(req.body, 'detail');
    const isDefault = f(req.body, 'isDefault', 'is_default');

    if (isDefault) {
      await Address.update({ is_default: false }, { where: { user_id: req.userId } });
    }

    if (receiverName !== undefined) addr.receiver_name = receiverName;
    if (phone !== undefined) addr.phone = phone;
    if (province !== undefined) addr.province = province;
    if (city !== undefined) addr.city = city;
    if (district !== undefined) addr.district = district;
    if (detail !== undefined) addr.detail = detail;
    if (isDefault !== undefined) addr.is_default = isDefault;
    await addr.save();

    res.json({ code: 200, data: addr });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 删除地址
router.delete('/:id', userAuth, async (req, res) => {
  try {
    await Address.destroy({
      where: { id: req.params.id, user_id: req.userId }
    });
    res.json({ code: 200, message: '已删除' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
