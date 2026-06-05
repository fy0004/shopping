const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Order = require('./Order');
const Product = require('./Product');

const OrderItem = sequelize.define('OrderItem', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  order_id: { type: DataTypes.BIGINT, allowNull: false },
  product_id: { type: DataTypes.BIGINT, allowNull: false },
  product_name: { type: DataTypes.STRING(200), allowNull: false },
  product_image: { type: DataTypes.STRING(500), defaultValue: '' },
  price: { type: DataTypes.DECIMAL(10, 2), allowNull: false },
  quantity: { type: DataTypes.INTEGER, allowNull: false }
}, {
  tableName: 'order_items',
  timestamps: false
});

OrderItem.belongsTo(Order, { foreignKey: 'order_id' });
OrderItem.belongsTo(Product, { foreignKey: 'product_id' });

module.exports = OrderItem;
