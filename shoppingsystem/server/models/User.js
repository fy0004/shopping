const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const User = sequelize.define('User', {
  id: { type: DataTypes.BIGINT, primaryKey: true, autoIncrement: true },
  phone: { type: DataTypes.STRING(20), allowNull: false, unique: true },
  password_hash: { type: DataTypes.STRING(255), allowNull: false },
  nickname: { type: DataTypes.STRING(50), allowNull: false },
  avatar_url: { type: DataTypes.STRING(500), defaultValue: '' },
  role: { type: DataTypes.ENUM('USER', 'ADMIN'), defaultValue: 'USER' },
  status: { type: DataTypes.ENUM('ACTIVE', 'DISABLED'), defaultValue: 'ACTIVE' },
  created_at: { type: DataTypes.DATE, defaultValue: DataTypes.NOW }
}, {
  tableName: 'users',
  timestamps: false
});

User.prototype.toJSON = function () {
  const values = Object.assign({}, this.get());
  if (values.created_at instanceof Date) values.created_at = values.created_at.getTime();
  delete values.password_hash;
  return values;
};

module.exports = User;
