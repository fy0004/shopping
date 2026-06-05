const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const User = require('./User');

const Address = sequelize.define('Address', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  user_id: { type: DataTypes.BIGINT, allowNull: false },
  receiver_name: { type: DataTypes.STRING(50), allowNull: false },
  phone: { type: DataTypes.STRING(20), allowNull: false },
  province: { type: DataTypes.STRING(50), defaultValue: '' },
  city: { type: DataTypes.STRING(50), defaultValue: '' },
  district: { type: DataTypes.STRING(50), defaultValue: '' },
  detail: { type: DataTypes.STRING(200), defaultValue: '' },
  is_default: { type: DataTypes.BOOLEAN, defaultValue: false }
}, {
  tableName: 'addresses',
  timestamps: false
});

Address.belongsTo(User, { foreignKey: 'user_id' });

module.exports = Address;
