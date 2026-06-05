const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const User = require('./User');
const Product = require('./Product');

const CartItem = sequelize.define('CartItem', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  user_id: { type: DataTypes.BIGINT, allowNull: false },
  product_id: { type: DataTypes.BIGINT, allowNull: false },
  quantity: { type: DataTypes.INTEGER, defaultValue: 1 },
  is_selected: { type: DataTypes.BOOLEAN, defaultValue: true }
}, {
  tableName: 'cart_items',
  timestamps: false
});

CartItem.belongsTo(User, { foreignKey: 'user_id' });
CartItem.belongsTo(Product, { foreignKey: 'product_id' });

module.exports = CartItem;
