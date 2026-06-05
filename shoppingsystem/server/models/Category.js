const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Category = sequelize.define('Category', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  name: { type: DataTypes.STRING(50), allowNull: false },
  icon_res_name: { type: DataTypes.STRING(100), defaultValue: '' },
  sort_order: { type: DataTypes.INTEGER, defaultValue: 0 }
}, {
  tableName: 'categories',
  timestamps: false
});

module.exports = Category;
