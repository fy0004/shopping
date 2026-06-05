const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Banner = sequelize.define('Banner', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  image_url: { type: DataTypes.STRING(500), allowNull: false },
  link_type: { type: DataTypes.ENUM('PRODUCT', 'CATEGORY', 'NONE'), defaultValue: 'NONE' },
  link_value: { type: DataTypes.BIGINT, defaultValue: 0 },
  sort_order: { type: DataTypes.INTEGER, defaultValue: 0 }
}, {
  tableName: 'banners',
  timestamps: false
});

module.exports = Banner;
