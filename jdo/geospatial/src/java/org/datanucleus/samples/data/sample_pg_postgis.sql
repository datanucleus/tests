-- SELECT DropGeometryColumn('samplepgpoint', 'geom');
-- DROP TABLE samplepgpoint;
-- CREATE TABLE samplepgpoint
-- (
--   samplepgpoint_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplepgpoint_pkey PRIMARY KEY (samplepgpoint_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplepgpoint', 'geom', 4326, 'POINT', 2);
INSERT INTO samplepgpoint (samplepgpoint_id, id, name, geom) VALUES (1, 1000, 'Point 0', NULL);
INSERT INTO samplepgpoint (samplepgpoint_id, id, name, geom) VALUES (2, 1001, 'Point 1', st_geometryfromtext('POINT(10 10)',4326));
INSERT INTO samplepgpoint (samplepgpoint_id, id, name, geom) VALUES (3, 1002, 'Point 2', st_geometryfromtext('POINT(75 75)',4326));
--
-- SELECT DropGeometryColumn('samplepglinestring', 'geom');
-- DROP TABLE samplepglinestring;
-- CREATE TABLE samplepglinestring
-- (
--   samplepglinestring_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplepglinestring_pkey PRIMARY KEY (samplepglinestring_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplepglinestring', 'geom', 4326, 'LINESTRING', 2);
INSERT INTO samplepglinestring (samplepglinestring_id, id, name, geom) VALUES (1, 2000, 'LineString 0', NULL);
INSERT INTO samplepglinestring (samplepglinestring_id, id, name, geom) VALUES (2, 2001, 'LineString 1', st_geometryfromtext('LINESTRING(0 50,100 50)',4326));
INSERT INTO samplepglinestring (samplepglinestring_id, id, name, geom) VALUES (3, 2002, 'LineString 2', st_geometryfromtext('LINESTRING(50 0,50 100)',4326));
INSERT INTO samplepglinestring (samplepglinestring_id, id, name, geom) VALUES (4, 2003, 'LineString 3', st_geometryfromtext('LINESTRING(100 25,120 25,110 10,110 45)',4326));
--
-- SELECT DropGeometryColumn('samplepgpolygon', 'geom');
-- DROP TABLE samplepgpolygon;
-- CREATE TABLE samplepgpolygon
-- (
--   samplepgpolygon_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplepgpolygon_pkey PRIMARY KEY (samplepgpolygon_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplepgpolygon', 'geom', 4326, 'POLYGON', 2);
INSERT INTO samplepgpolygon (samplepgpolygon_id, id, name, geom) VALUES (1, 3000, 'Polygon 0', NULL);
INSERT INTO samplepgpolygon (samplepgpolygon_id, id, name, geom) VALUES (2, 3001, 'Polygon 1', st_geometryfromtext('POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))',4326));
INSERT INTO samplepgpolygon (samplepgpolygon_id, id, name, geom) VALUES (3, 3002, 'Polygon 2', st_geometryfromtext('POLYGON((75 75,100 75,100 100,75 75))',4326));
--
-- SELECT DropGeometryColumn('samplepggeometrycollection', 'geom');
-- DROP TABLE samplepggeometrycollection;
-- CREATE TABLE samplepggeometrycollection
-- (
--   samplepggeometrycollection_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT samplepggeometrycollection_pkey PRIMARY KEY (samplepggeometrycollection_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('samplepggeometrycollection', 'geom', 4326, 'GEOMETRYCOLLECTION', 2);
INSERT INTO samplepggeometrycollection (samplepggeometrycollection_id, id, name, geom) VALUES (1, 7000, 'Collection 0', NULL);
INSERT INTO samplepggeometrycollection (samplepggeometrycollection_id, id, name, geom) VALUES (2, 7001, 'Collection 1', st_geometryfromtext('GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))',4326));
INSERT INTO samplepggeometrycollection (samplepggeometrycollection_id, id, name, geom) VALUES (3, 7002, 'Collection 2', st_geometryfromtext('GEOMETRYCOLLECTION(POINT(75 75),LINESTRING(50 0,50 100),POLYGON((75 75,100 75,100 100,75 75)))',4326));
INSERT INTO samplepggeometrycollection (samplepggeometrycollection_id, id, name, geom) VALUES (4, 7003, 'Collection 3', st_geometryfromtext('GEOMETRYCOLLECTION(LINESTRING(100 25,120 25,110 10,110 45))',4326));
