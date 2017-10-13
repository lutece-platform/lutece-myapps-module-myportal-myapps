
--
-- Structure for table myportal_myapps_favorites
--

DROP TABLE IF EXISTS myportal_myapps_favorites;
CREATE TABLE myportal_myapps_favorites (
id_apps_favorites int(6) NOT NULL,
id_application int(6) NOT NULL,
user_id varchar(50) NOT NULL,
apps_favorites_order int(6) default '1' NOT NULL,
PRIMARY KEY (id_apps_favorites)
);