CREATE SCHEMA IF NOT EXISTS transactionsdb DEFAULT CHARACTER SET utf8;
CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'xdfGT45^&';
GRANT ALL ON transactionsdb.* TO 'admin'@'%';
FLUSH PRIVILEGES;

use transactionsdb;
CREATE TABLE IF NOT EXISTS user_transaction (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  amount DECIMAL(10, 2),
  created_at TIMESTAMP,
  user_id VARCHAR(32));