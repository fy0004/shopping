const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const User = require('./User');

const Order = sequelize.define('Order', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  order_no: { type: DataTypes.STRING(30), allowNull: false, unique: true },
  user_id: { type: DataTypes.BIGINT, allowNull: false },
  address_snapshot: { type: DataTypes.TEXT, defaultValue: '' },
  status: { type: DataTypes.ENUM('PENDING', 'PAID', 'SHIPPED', 'COMPLETED', 'CANCELLED'), defaultValue: 'PENDING' },
  total_amount: { type: DataTypes.DECIMAL(10, 2), allowNull: false },
  payment_method: { type: DataTypes.ENUM('ALIPAY', 'WECHAT'), allowNull: true },
  created_at: { type: DataTypes.DATE, defaultValue: DataTypes.NOW },
  updated_at: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
}, {
  tableName: 'orders',
  timestamps: false
});

Order.prototype.toJSON = function () {
  const values = Object.assign({}, this.get());
  if (values.created_at instanceof Date) values.created_at = values.created_at.getTime();
  if (values.updated_at instanceof Date) values.updated_at = values.updated_at.getTime();
  return values;
};

Order.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Order;
