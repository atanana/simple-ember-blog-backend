# --- !Ups

CREATE TABLE `post` (
  `id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title`   VARCHAR(255) NOT NULL,
  `text`    LONGTEXT     NOT NULL,
  `created` TIMESTAMP    NOT NULL,
  PRIMARY KEY (`id`)
);

# --- !Downs

DROP TABLE post;