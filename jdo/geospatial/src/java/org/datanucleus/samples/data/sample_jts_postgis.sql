-- SELECT DropGeometryColumn('samplejtspoint', 'geom');
-- DROP TABLE samplejtspoint;
-- CREATE TABLE samplejtspoint
-- (
--   samplejtspoint_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplejtspoint_pkey PRIMARY KEY (samplejtspoint_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplejtspoint', 'geom', 4326, 'POINT', 2);
INSERT INTO samplejtspoint (samplejtspoint_id, id, name, geom) VALUES (1, 1000, 'Point 0', NULL);
INSERT INTO samplejtspoint (samplejtspoint_id, id, name, geom) VALUES (2, 1001, 'Point 1', st_GeomFromText('POINT(10 10)',4326));
INSERT INTO samplejtspoint (samplejtspoint_id, id, name, geom) VALUES (3, 1002, 'Point 2', st_GeomFromText('POINT(75 75)',4326));
--
-- SELECT DropGeometryColumn('samplejtslinestring', 'geom');
-- DROP TABLE samplejtslinestring;
-- CREATE TABLE samplejtslinestring
-- (
--   samplejtslinestring_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplejtslinestring_pkey PRIMARY KEY (samplejtslinestring_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplejtslinestring', 'geom', 4326, 'LINESTRING', 2);
INSERT INTO samplejtslinestring (samplejtslinestring_id, id, name, geom) VALUES (1, 2000, 'LineString 0', NULL);
INSERT INTO samplejtslinestring (samplejtslinestring_id, id, name, geom) VALUES (2, 2001, 'LineString 1', st_GeomFromText('LINESTRING(0 50,100 50)',4326));
INSERT INTO samplejtslinestring (samplejtslinestring_id, id, name, geom) VALUES (3, 2002, 'LineString 2', st_GeomFromText('LINESTRING(50 0,50 100)',4326));
INSERT INTO samplejtslinestring (samplejtslinestring_id, id, name, geom) VALUES (4, 2003, 'LineString 3', st_GeomFromText('LINESTRING(100 25,120 25,110 10,110 45)',4326));
--
-- SELECT DropGeometryColumn('samplejtspolygon', 'geom');
-- DROP TABLE samplejtspolygon;
-- CREATE TABLE samplejtspolygon
-- (
--   samplejtspolygon_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplejtspolygon_pkey PRIMARY KEY (samplejtspolygon_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplejtspolygon', 'geom', 4326, 'POLYGON', 2);
INSERT INTO samplejtspolygon (samplejtspolygon_id, id, name, geom) VALUES (1, 3000, 'Polygon 0', NULL);
INSERT INTO samplejtspolygon (samplejtspolygon_id, id, name, geom) VALUES (2, 3001, 'Polygon 1', st_GeomFromText('POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))',4326));
INSERT INTO samplejtspolygon (samplejtspolygon_id, id, name, geom) VALUES (3, 3002, 'Polygon 2', st_GeomFromText('POLYGON((75 75,100 75,100 100,75 75))',4326));
--
-- SELECT DropGeometryColumn('samplejtsgeometrycollection', 'geom');
-- DROP TABLE samplejtsgeometrycollection;
-- CREATE TABLE samplejtsgeometrycollection
-- (
--   samplejtsgeometrycollection_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplejtsgeometrycollection_pkey PRIMARY KEY (samplejtsgeometrycollection_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplejtsgeometrycollection', 'geom', 4326, 'GEOMETRYCOLLECTION', 2);
INSERT INTO samplejtsgeometrycollection (samplejtsgeometrycollection_id, id, name, geom) VALUES (1, 7000, 'Collection 0', NULL);
INSERT INTO samplejtsgeometrycollection (samplejtsgeometrycollection_id, id, name, geom) VALUES (2, 7001, 'Collection 1', st_GeomFromText('GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))',4326));
INSERT INTO samplejtsgeometrycollection (samplejtsgeometrycollection_id, id, name, geom) VALUES (3, 7002, 'Collection 2', st_GeomFromText('GEOMETRYCOLLECTION(POINT(75 75),LINESTRING(50 0,50 100),POLYGON((75 75,100 75,100 100,75 75)))',4326));
INSERT INTO samplejtsgeometrycollection (samplejtsgeometrycollection_id, id, name, geom) VALUES (4, 7003, 'Collection 3', st_GeomFromText('GEOMETRYCOLLECTION(LINESTRING(100 25,120 25,110 10,110 45))',4326));
