DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `salt` varchar(32) NOT NULL,
  `head_image_url` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `type` int DEFAULT 3,
  `user_id` int(11) NOT NULL,
  `like_count` int(11) DEFAULT 0,
  `comment_count` int(11) DEFAULT 0,
  `title` varchar(256) NOT NULL,
  `link` text NOT NULL,
  `image_link` varchar(256),
  `create_date` datetime NOT NULL,
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `login_ticket`;
CREATE TABLE `login_ticket`(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `status` int NOT NULL,
  `ticket` varchar(256) NOT NULL,
  `expired` datetime NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE INDEX `ticket_index`(`ticket`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `entity_type` int NOT NULL,
  `entity_id` int(11) NOT NULL,
  `status` int DEFAULT 0,
  `create_date` datetime NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY(`id`),
  INDEX `comment_index`(`entity_type` ASC, `entity_id` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`(
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `from_id` int(11) unsigned NOT NULL,
  `to_id` int(11) unsigned NOT NULL,
  `has_read` int DEFAULT 0,
  `conversation_id` varchar(256) NOT NULL,
  `content` text NOT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY(`id`),
  INDEX `conversation_index`(`conversation_id` ASC),
  INDEX `create_date_index`(`create_date` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;