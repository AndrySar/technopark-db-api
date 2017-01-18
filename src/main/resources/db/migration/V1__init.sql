CREATE TABLE `User` (
  `about` text,
  `email` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `isAnonymous` tinyint(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`email`),
  KEY `User_email_id_index` (`email`,`id`),
  CONSTRAINT email_unique UNIQUE (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=104482 DEFAULT CHARSET=utf8;



CREATE TABLE `Followers` (
  `user` varchar(255) NOT NULL,
  `follower` varchar(255) NOT NULL,
  PRIMARY KEY (`follower`,`user`),
  KEY `Followers_user_index` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Forum` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `short_name` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Forum_short_name_index` (`short_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1632 DEFAULT CHARSET=utf8;


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
  KEY `Thread_forum_date_index` (`forum`,`date`),
  KEY `Thread_user_date_index` (`user`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=10979 DEFAULT CHARSET=utf8;



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
  KEY `Post_user_date_index` (`user`,`date`),
  KEY `Post_forum_date_index` (`forum`,`date`),
  KEY `Post_thread_patch_index` (`thread`,`patch`),
  KEY `Post_forum_user_index` (`forum`,`user`),
  KEY `Post_thread_date_index` (`thread`,`date`)
) ENGINE=InnoDB AUTO_INCREMENT=1010207 DEFAULT CHARSET=utf8;



CREATE TABLE `Subscriptions` (
  `user` varchar(255) NOT NULL,
  `thread` int(11) NOT NULL,
  PRIMARY KEY (`user`,`thread`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `UsersOfForum` (
  `forum` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;