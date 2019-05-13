/*
Navicat MySQL Data Transfer

Source Server         : happy-bike
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : happy-bike

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auto_inc_no
-- ----------------------------
DROP TABLE IF EXISTS `auto_inc_no`;
CREATE TABLE `auto_inc_no` (
  `auto_inc_no` bigint(20) NOT NULL AUTO_INCREMENT,
  `what_ever` tinyint(2) NOT NULL,
  PRIMARY KEY (`auto_inc_no`)
) ENGINE=InnoDB AUTO_INCREMENT=28000066 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of auto_inc_no
-- ----------------------------
INSERT INTO `auto_inc_no` VALUES ('28000000', '1');
INSERT INTO `auto_inc_no` VALUES ('28000001', '1');
INSERT INTO `auto_inc_no` VALUES ('28000002', '1');
INSERT INTO `auto_inc_no` VALUES ('28000003', '1');
INSERT INTO `auto_inc_no` VALUES ('28000004', '1');
INSERT INTO `auto_inc_no` VALUES ('28000005', '1');
INSERT INTO `auto_inc_no` VALUES ('28000006', '1');
INSERT INTO `auto_inc_no` VALUES ('28000007', '1');
INSERT INTO `auto_inc_no` VALUES ('28000008', '1');
INSERT INTO `auto_inc_no` VALUES ('28000009', '1');
INSERT INTO `auto_inc_no` VALUES ('28000010', '1');
INSERT INTO `auto_inc_no` VALUES ('28000011', '1');
INSERT INTO `auto_inc_no` VALUES ('28000012', '1');
INSERT INTO `auto_inc_no` VALUES ('28000013', '1');
INSERT INTO `auto_inc_no` VALUES ('28000014', '1');
INSERT INTO `auto_inc_no` VALUES ('28000015', '1');
INSERT INTO `auto_inc_no` VALUES ('28000016', '1');
INSERT INTO `auto_inc_no` VALUES ('28000017', '1');
INSERT INTO `auto_inc_no` VALUES ('28000018', '1');
INSERT INTO `auto_inc_no` VALUES ('28000019', '1');
INSERT INTO `auto_inc_no` VALUES ('28000020', '1');
INSERT INTO `auto_inc_no` VALUES ('28000021', '1');
INSERT INTO `auto_inc_no` VALUES ('28000022', '1');
INSERT INTO `auto_inc_no` VALUES ('28000023', '1');
INSERT INTO `auto_inc_no` VALUES ('28000024', '1');
INSERT INTO `auto_inc_no` VALUES ('28000025', '1');
INSERT INTO `auto_inc_no` VALUES ('28000026', '1');
INSERT INTO `auto_inc_no` VALUES ('28000027', '1');
INSERT INTO `auto_inc_no` VALUES ('28000028', '1');
INSERT INTO `auto_inc_no` VALUES ('28000029', '1');
INSERT INTO `auto_inc_no` VALUES ('28000030', '1');
INSERT INTO `auto_inc_no` VALUES ('28000031', '1');
INSERT INTO `auto_inc_no` VALUES ('28000032', '1');
INSERT INTO `auto_inc_no` VALUES ('28000033', '1');
INSERT INTO `auto_inc_no` VALUES ('28000034', '1');
INSERT INTO `auto_inc_no` VALUES ('28000035', '1');
INSERT INTO `auto_inc_no` VALUES ('28000036', '1');
INSERT INTO `auto_inc_no` VALUES ('28000037', '1');
INSERT INTO `auto_inc_no` VALUES ('28000038', '1');
INSERT INTO `auto_inc_no` VALUES ('28000039', '1');
INSERT INTO `auto_inc_no` VALUES ('28000040', '1');
INSERT INTO `auto_inc_no` VALUES ('28000041', '1');
INSERT INTO `auto_inc_no` VALUES ('28000042', '1');
INSERT INTO `auto_inc_no` VALUES ('28000043', '1');
INSERT INTO `auto_inc_no` VALUES ('28000044', '1');
INSERT INTO `auto_inc_no` VALUES ('28000045', '1');
INSERT INTO `auto_inc_no` VALUES ('28000046', '1');
INSERT INTO `auto_inc_no` VALUES ('28000047', '1');
INSERT INTO `auto_inc_no` VALUES ('28000048', '1');
INSERT INTO `auto_inc_no` VALUES ('28000049', '1');
INSERT INTO `auto_inc_no` VALUES ('28000050', '1');
INSERT INTO `auto_inc_no` VALUES ('28000051', '1');
INSERT INTO `auto_inc_no` VALUES ('28000052', '1');
INSERT INTO `auto_inc_no` VALUES ('28000053', '1');
INSERT INTO `auto_inc_no` VALUES ('28000054', '1');
INSERT INTO `auto_inc_no` VALUES ('28000055', '1');
INSERT INTO `auto_inc_no` VALUES ('28000056', '1');
INSERT INTO `auto_inc_no` VALUES ('28000057', '1');
INSERT INTO `auto_inc_no` VALUES ('28000058', '1');
INSERT INTO `auto_inc_no` VALUES ('28000059', '1');
INSERT INTO `auto_inc_no` VALUES ('28000060', '1');
INSERT INTO `auto_inc_no` VALUES ('28000061', '1');
INSERT INTO `auto_inc_no` VALUES ('28000062', '1');
INSERT INTO `auto_inc_no` VALUES ('28000063', '1');
INSERT INTO `auto_inc_no` VALUES ('28000064', '1');
INSERT INTO `auto_inc_no` VALUES ('28000065', '1');

-- ----------------------------
-- Table structure for bike
-- ----------------------------
DROP TABLE IF EXISTS `bike`;
CREATE TABLE `bike` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` bigint(20) NOT NULL COMMENT '单车编号 标识唯一一辆单车',
  `type` tinyint(2) NOT NULL COMMENT '1 快乐单车  2 快乐单车Little',
  `enable_flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '1 可用  2 不可用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of bike
-- ----------------------------
INSERT INTO `bike` VALUES ('3', '28000000', '1', '1');
INSERT INTO `bike` VALUES ('4', '28000001', '1', '1');
INSERT INTO `bike` VALUES ('5', '28000002', '1', '1');
INSERT INTO `bike` VALUES ('6', '28000003', '1', '1');
INSERT INTO `bike` VALUES ('7', '28000004', '1', '1');
INSERT INTO `bike` VALUES ('8', '28000005', '1', '1');
INSERT INTO `bike` VALUES ('9', '28000006', '1', '1');
INSERT INTO `bike` VALUES ('10', '28000007', '1', '1');
INSERT INTO `bike` VALUES ('11', '28000008', '1', '1');
INSERT INTO `bike` VALUES ('12', '28000009', '1', '1');
INSERT INTO `bike` VALUES ('13', '28000010', '1', '1');
INSERT INTO `bike` VALUES ('14', '28000011', '1', '1');
INSERT INTO `bike` VALUES ('15', '28000012', '1', '1');
INSERT INTO `bike` VALUES ('16', '28000013', '1', '1');
INSERT INTO `bike` VALUES ('17', '28000014', '1', '1');
INSERT INTO `bike` VALUES ('18', '28000015', '1', '1');
INSERT INTO `bike` VALUES ('19', '28000016', '1', '1');
INSERT INTO `bike` VALUES ('20', '28000017', '1', '1');
INSERT INTO `bike` VALUES ('21', '28000018', '1', '1');
INSERT INTO `bike` VALUES ('22', '28000019', '1', '1');
INSERT INTO `bike` VALUES ('23', '28000020', '1', '1');
INSERT INTO `bike` VALUES ('24', '28000021', '1', '1');
INSERT INTO `bike` VALUES ('25', '28000022', '1', '1');
INSERT INTO `bike` VALUES ('26', '28000023', '1', '1');
INSERT INTO `bike` VALUES ('27', '28000024', '1', '1');
INSERT INTO `bike` VALUES ('28', '28000025', '2', '1');
INSERT INTO `bike` VALUES ('29', '28000026', '2', '1');
INSERT INTO `bike` VALUES ('30', '28000027', '2', '1');
INSERT INTO `bike` VALUES ('31', '28000028', '2', '1');
INSERT INTO `bike` VALUES ('32', '28000029', '2', '1');
INSERT INTO `bike` VALUES ('33', '28000030', '2', '1');
INSERT INTO `bike` VALUES ('34', '28000031', '2', '1');
INSERT INTO `bike` VALUES ('35', '28000032', '2', '1');
INSERT INTO `bike` VALUES ('36', '28000033', '2', '1');
INSERT INTO `bike` VALUES ('37', '28000034', '2', '1');
INSERT INTO `bike` VALUES ('38', '28000035', '2', '1');
INSERT INTO `bike` VALUES ('39', '28000036', '2', '1');
INSERT INTO `bike` VALUES ('40', '28000037', '2', '1');
INSERT INTO `bike` VALUES ('41', '28000038', '2', '1');
INSERT INTO `bike` VALUES ('42', '28000039', '2', '1');
INSERT INTO `bike` VALUES ('43', '28000040', '2', '1');
INSERT INTO `bike` VALUES ('44', '28000041', '2', '1');
INSERT INTO `bike` VALUES ('45', '28000042', '2', '1');
INSERT INTO `bike` VALUES ('46', '28000043', '2', '1');
INSERT INTO `bike` VALUES ('47', '28000044', '2', '1');
INSERT INTO `bike` VALUES ('48', '28000045', '2', '1');
INSERT INTO `bike` VALUES ('49', '28000046', '2', '1');
INSERT INTO `bike` VALUES ('50', '28000047', '2', '1');
INSERT INTO `bike` VALUES ('51', '28000048', '2', '1');
INSERT INTO `bike` VALUES ('52', '28000049', '2', '1');
INSERT INTO `bike` VALUES ('53', '28000050', '2', '1');
INSERT INTO `bike` VALUES ('54', '28000051', '2', '1');
INSERT INTO `bike` VALUES ('55', '28000052', '2', '1');
INSERT INTO `bike` VALUES ('56', '28000053', '2', '1');
INSERT INTO `bike` VALUES ('57', '28000054', '2', '1');
INSERT INTO `bike` VALUES ('58', '28000055', '2', '1');
INSERT INTO `bike` VALUES ('59', '28000056', '2', '1');
INSERT INTO `bike` VALUES ('60', '28000057', '2', '1');
INSERT INTO `bike` VALUES ('61', '28000058', '2', '1');
INSERT INTO `bike` VALUES ('62', '28000059', '2', '1');
INSERT INTO `bike` VALUES ('63', '28000060', '2', '1');
INSERT INTO `bike` VALUES ('64', '28000061', '2', '1');
INSERT INTO `bike` VALUES ('65', '28000062', '2', '1');
INSERT INTO `bike` VALUES ('66', '28000063', '2', '1');
INSERT INTO `bike` VALUES ('67', '28000064', '2', '1');
INSERT INTO `bike` VALUES ('68', '28000065', '2', '1');

-- ----------------------------
-- Table structure for ride_fee
-- ----------------------------
DROP TABLE IF EXISTS `ride_fee`;
CREATE TABLE `ride_fee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `min_unit` int(5) NOT NULL COMMENT '扣费时间单位（多少小时为基准）',
  `fee` decimal(10,2) NOT NULL COMMENT '每个时间单位产生多少费用',
  `bike_type` tinyint(4) NOT NULL COMMENT '单车类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ride_fee
-- ----------------------------
INSERT INTO `ride_fee` VALUES ('1', '30', '1.00', '1');
INSERT INTO `ride_fee` VALUES ('2', '30', '0.50', '2');

-- ----------------------------
-- Table structure for ride_record
-- ----------------------------
DROP TABLE IF EXISTS `ride_record`;
CREATE TABLE `ride_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userid` bigint(20) NOT NULL,
  `record_no` varchar(100) NOT NULL COMMENT '订单编号',
  `bike_no` bigint(20) NOT NULL COMMENT '单车编号',
  `start_time` datetime NOT NULL COMMENT '开始骑行时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束骑行时间',
  `ride_time` int(10) DEFAULT NULL COMMENT '骑行花费时间',
  `ride_cost` decimal(10,2) DEFAULT NULL COMMENT '骑行费用',
  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '1 骑行中  2骑行结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ride_record
-- ----------------------------
INSERT INTO `ride_record` VALUES ('1', '1', '1503396355465162725396', '28000001', '2017-08-22 18:06:30', '2017-08-22 19:42:15', '95', '4.00', '2');
INSERT INTO `ride_record` VALUES ('2', '1', '15034158110391291507520', '28000001', '2017-08-22 23:30:11', '2017-08-22 23:58:21', '28', '1.00', '2');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(20) DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(20) NOT NULL COMMENT '手机号码',
  `head_img` varchar(100) DEFAULT NULL COMMENT '头像',
  `verify_flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否实名认证 1： 否 2：已认证',
  `enable_flag` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否有效有用 1：有效  2：无效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'wang', '18980840843', null, '2', '1');

-- ----------------------------
-- Table structure for wallet
-- ----------------------------
DROP TABLE IF EXISTS `wallet`;
CREATE TABLE `wallet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userid` bigint(20) NOT NULL,
  `remain_sum` decimal(10,2) NOT NULL DEFAULT '0.00',
  `deposit` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of wallet
-- ----------------------------
INSERT INTO `wallet` VALUES ('1', '1', '8.00', '300.00');
