const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const Category = require('./Category');

const Product = sequelize.define('Product', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  category_id: { type: DataTypes.BIGINT, allowNull: false },
  name: { type: DataTypes.STRING(200), allowNull: false },
  description: { type: DataTypes.TEXT, defaultValue: '' },
  price: { type: DataTypes.DECIMAL(10, 2), allowNull: false },
  original_price: { type: DataTypes.DECIMAL(10, 2), defaultValue: 0 },
  stock: { type: DataTypes.INTEGER, defaultValue: 0 },
  sales_count: { type: DataTypes.INTEGER, defaultValue: 0 },
  image_urls: { type: DataTypes.TEXT, defaultValue: '[]' },
  rating: { type: DataTypes.FLOAT, defaultValue: 5.0 },
  rating_count: { type: DataTypes.INTEGER, defaultValue: 0 },
  is_on_sale: { type: DataTypes.BOOLEAN, defaultValue: true },
  created_at: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
}, {
  tableName: 'products',
  timestamps: false
});

Product.belongsTo(Category, { foreignKey: 'category_id' });
Category.hasMany(Product, { foreignKey: 'category_id' });

module.exports = Product;
