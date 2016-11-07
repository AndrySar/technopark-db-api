CREATE TABLE `User` (
  `about` text,
  `email` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isAnonymous` tinyint(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`email`),
  UNIQUE KEY `User_id_uindex` (`id`),
  UNIQUE KEY `User_email_uindex` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `Followers` (
  `user` varchar(255) NOT NULL,
  `follower` varchar(255) NOT NULL,
  PRIMARY KEY (`user`,`follower`),
  KEY `Followers_User_email_fol_fk` (`follower`),
  CONSTRAINT `Followers_User_email_fk` FOREIGN KEY (`user`) REFERENCES `User` (`email`),
  CONSTRAINT `Followers_User_email_fol_fk` FOREIGN KEY (`follower`) REFERENCES `User` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Forum` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `short_name` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  PRIMARY KEY (`short_name`),
  UNIQUE KEY `Forum_name_uindex` (`name`),
  UNIQUE KEY `Forum_id_uindex` (`id`),
  UNIQUE KEY `Forum_short_name_uindex` (`short_name`),
  KEY `fk_Forum_1_idx` (`user`),
  CONSTRAINT `Forum_User_email_fk` FOREIGN KEY (`user`) REFERENCES `User` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `Thread` (
  `date` datetime NOT NULL,
  `dislikes` int(11) NOT NULL DEFAULT '0',
  `forum` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isClosed` tinyint(1) NOT NULL,
  `isDeleted` tinyint(1) NOT NULL,
  `likes` int(11) NOT NULL DEFAULT '0',
  `message` text NOT NULL,
  `points` int(11) NOT NULL DEFAULT '0',
  `posts` int(11) NOT NULL DEFAULT '0',
  `slug` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Thread_id_uindex` (`id`),
  KEY `Thread_User_email_fk` (`user`),
  KEY `Thread_Forum_short_name_fk` (`forum`),
  CONSTRAINT `Thread_Forum_short_name_fk` FOREIGN KEY (`forum`) REFERENCES `Forum` (`short_name`),
  CONSTRAINT `Thread_User_email_fk` FOREIGN KEY (`user`) REFERENCES `User` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `Post` (
  `date` datetime NOT NULL,
  `dislikes` int(11) NOT NULL DEFAULT '0',
  `forum` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isApproved` tinyint(1) NOT NULL,
  `isDeleted` tinyint(1) NOT NULL,
  `isEdited` tinyint(1) NOT NULL,
  `isHighlighted` tinyint(1) NOT NULL,
  `isSpam` tinyint(1) NOT NULL,
  `likes` int(11) NOT NULL DEFAULT '0',
  `message` text,
  `parent` int(11) DEFAULT NULL,
  `points` int(11) NOT NULL DEFAULT '0',
  `thread` int(11) NOT NULL,
  `user` varchar(255) NOT NULL,
  `patch` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Post_id_uindex` (`id`),
  KEY `Post_User_email_fk` (`user`),
  KEY `Post_Thread_id_fk` (`thread`),
  KEY `Post_Forum_short_name_fk` (`forum`),
  CONSTRAINT `Post_Forum_short_name_fk` FOREIGN KEY (`forum`) REFERENCES `Forum` (`short_name`),
  CONSTRAINT `Post_Thread_id_fk` FOREIGN KEY (`thread`) REFERENCES `Thread` (`id`),
  CONSTRAINT `Post_User_email_fk` FOREIGN KEY (`user`) REFERENCES `User` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;



CREATE TABLE `Subscriptions` (
  `user` varchar(255) NOT NULL,
  `thread` int(11) NOT NULL,
  PRIMARY KEY (`user`,`thread`),
  KEY `Subscriptions_Thread_id_fk` (`thread`),
  CONSTRAINT `Subscriptions_Thread_id_fk` FOREIGN KEY (`thread`) REFERENCES `Thread` (`id`),
  CONSTRAINT `Subscriptions_User_email_fk` FOREIGN KEY (`user`) REFERENCES `User` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;