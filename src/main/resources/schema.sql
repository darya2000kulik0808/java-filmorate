CREATE TABLE IF NOT EXISTS `FILMORATE_USER` (
  `USER_ID` bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  `USER_EMAIL`  varchar UNIQUE ,
  `USER_LOGIN` varchar,
  `USER_NAME` varchar,
  `USER_BIRTHDAY` date
);

CREATE TABLE IF NOT EXISTS `FILM` (
  `FILM_ID` bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  `FILM_NAME` varchar,
  `FILM_DESCRIPTION` varchar,
  `FILM_RELEASE_DATE` date,
  `FILM_DURATION` integer,
  `FILM_MPA_ID` bigint,
  UNIQUE (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION)
);

CREATE TABLE IF NOT EXISTS `GENRE` (
  `GENRE_ID` bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  `GENRE_NAME` varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS `GENRE_FILM` (
  `GF_FILM_ID` bigint,
  `GF_GENRE_ID` bigint,
  PRIMARY KEY (`GF_FILM_ID`, `GF_GENRE_ID`)
);

CREATE TABLE IF NOT EXISTS `MPA` (
  `MPA_ID` bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  `MPA_NAME` varchar UNIQUE
);

CREATE TABLE IF NOT EXISTS `FRIENDSHIP` (
  `USER_1_ID` bigint,
  `USER_2_ID` bigint,
  `FRIENDSHIP_STATUS` boolean,
  PRIMARY KEY (`USER_1_ID`, `USER_2_ID`)
);

CREATE TABLE IF NOT EXISTS `LIKES` (
  `USER_ID` bigint,
  `FILM_ID` bigint,
  PRIMARY KEY (`USER_ID`, `FILM_ID`)
);

ALTER TABLE `GENRE_FILM` ADD FOREIGN KEY (`GF_FILM_ID`) REFERENCES `FILM` (`FILM_ID`);

ALTER TABLE `GENRE_FILM` ADD FOREIGN KEY (`GF_GENRE_ID`) REFERENCES `GENRE` (`GENRE_ID`);

ALTER TABLE `FILM` ADD FOREIGN KEY (`FILM_MPA_ID`) REFERENCES `MPA` (`MPA_ID`);

ALTER TABLE `FRIENDSHIP` ADD FOREIGN KEY (`USER_1_ID`) REFERENCES `FILMORATE_USER` (`USER_ID`);

ALTER TABLE `FRIENDSHIP` ADD FOREIGN KEY (`USER_2_ID`) REFERENCES `FILMORATE_USER` (`USER_ID`);

ALTER TABLE LIKES ADD FOREIGN KEY (`USER_ID`) REFERENCES `FILMORATE_USER` (`USER_ID`);

ALTER TABLE LIKES ADD FOREIGN KEY (`FILM_ID`) REFERENCES `FILM` (`FILM_ID`);
