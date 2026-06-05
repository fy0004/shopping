const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const User = require('./User');
const Product = require('./Product');
const Order = require('./Order');

const Review = sequelize.define('Review', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  user_id: { type: DataTypes.BIGINT, allowNull: false },
  product_id: { type: DataTypes.BIGINT, allowNull: false },
  order_id: { type: DataTypes.BIGINT, allowNull: false },
  rating: { type: DataTypes.INTEGER, allowNull: false },
  content: { type: DataTypes.TEXT, defaultValue: '' },
  created_at: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
}, {
  tableName: 'reviews',
  timestamps: false
});

Review.belongsTo(User, { foreignKey: 'user_id' });
Review.belongsTo(Product, { foreignKey: 'product_id' });
Review.prototype.toJSON = function () {
  const values = Object.assign({}, this.get());
  if (values.created_at instanceof Date) values.created_at = values.created_at.getTime();
  return values;
};

Review.belongsTo(Order, { foreignKey: 'order_id' });

module.exports = Review;
