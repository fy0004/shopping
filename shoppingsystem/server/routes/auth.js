const express = require('express');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const { generateToken, userAuth } = require('../middleware/auth');

const router = express.Router();

// 登录
router.post('/login', async (req, res) => {
  try {
    const { phone, password } = req.body;
    if (!phone || !password) {
      return res.json({ code: 400, message: '手机号和密码不能为空' });
    }

    const user = await User.findOne({ where: { phone } });
    if (!user) {
      return res.json({ code: 400, message: '手机号或密码错误' });
    }
    if (user.status === 'DISABLED') {
      return res.json({ code: 400, message: '该账号已被禁用' });
    }

    const valid = await bcrypt.compare(password, user.password_hash);
    if (!valid) {
      return res.json({ code: 400, message: '手机号或密码错误' });
    }

    const token = generateToken(user.id, user.role);
    res.json({
      code: 200,
      message: '登录成功',
      data: {
        id: user.id,
        phone: user.phone,
        nickname: user.nickname,
        avatarUrl: user.avatar_url,
        role: user.role,
        token
      }
    });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 注册
router.post('/register', async (req, res) => {
  try {
    const { phone, password, nickname } = req.body;
    if (!phone || !password || !nickname) {
      return res.json({ code: 400, message: '请填写所有字段' });
    }
    if (phone.length !== 11) {
      return res.json({ code: 400, message: '请输入正确的手机号' });
    }
    if (password.length < 6) {
      return res.json({ code: 400, message: '密码至少6位' });
    }

    const existing = await User.findOne({ where: { phone } });
    if (existing) {
      return res.json({ code: 400, message: '该手机号已注册' });
    }

    const hash = await bcrypt.hash(password, 10);
    const user = await User.create({
      phone,
      password_hash: hash,
      nickname,
      role: 'USER',
      status: 'ACTIVE'
    });

    res.json({
      code: 200,
      message: '注册成功',
      data: { id: user.id }
    });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 修改密码
router.put('/password', userAuth, async (req, res) => {
  try {
    const { oldPassword, newPassword } = req.body;
    const user = await User.findByPk(req.userId);
    if (!user) return res.json({ code: 404, message: '用户不存在' });

    const valid = await bcrypt.compare(oldPassword, user.password_hash);
    if (!valid) return res.json({ code: 400, message: '原密码不正确' });
    if (newPassword.length < 6) return res.json({ code: 400, message: '新密码至少6位' });

    user.password_hash = await bcrypt.hash(newPassword, 10);
    await user.save();

    res.json({ code: 200, message: '密码修改成功' });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

// 获取个人信息
router.get('/profile', userAuth, async (req, res) => {
  try {
    const user = await User.findByPk(req.userId, {
      attributes: ['id', 'phone', 'nickname', 'avatar_url', 'role']
    });
    if (!user) return res.json({ code: 404, message: '用户不存在' });
    res.json({ code: 200, data: user });
  } catch (err) {
    res.json({ code: 500, message: '服务器错误: ' + err.message });
  }
});

module.exports = router;
