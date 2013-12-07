-- SELECT DropGeometryColumn('sampleaopoint', 'geom');
-- DROP TABLE sampleaopoint;
-- CREATE TABLE sampleaopoint
-- (
--   sampleaopoint_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT sampleaopoint_pkey PRIMARY KEY (sampleaopoint_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('sampleaopoint', 'geom', 4326, 'POINT', 2);
INSERT INTO sampleaopoint (sampleaopoint_id, id, name, geom) VALUES (1, 1000, 'Point 0', NULL);
INSERT INTO sampleaopoint (sampleaopoint_id, id, name, geom) VALUES (2, 1001, 'Point 1', GeomFromText('POINT(10 10)',4326));
INSERT INTO sampleaopoint (sampleaopoint_id, id, name, geom) VALUES (3, 1002, 'Point 2', GeomFromText('POINT(75 75)',4326));
--
-- SELECT DropGeometryColumn('sampleaopath', 'geom');
-- DROP TABLE sampleaopath;
-- CREATE TABLE sampleaopath
-- (
--   sampleaopath_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT sampleaopath_pkey PRIMARY KEY (sampleaopath_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('sampleaopath', 'geom', 4326, 'LINESTRING', 2);
INSERT INTO sampleaopath (sampleaopath_id, id, name, geom) VALUES (1, 2000, 'Path 0', NULL);
INSERT INTO sampleaopath (sampleaopath_id, id, name, geom) VALUES (2, 2001, 'Path 1', GeomFromText('LINESTRING(0 50,100 50)',4326));
INSERT INTO sampleaopath (sampleaopath_id, id, name, geom) VALUES (3, 2002, 'Path 2', GeomFromText('LINESTRING(50 0,50 100)',4326));
INSERT INTO sampleaopath (sampleaopath_id, id, name, geom) VALUES (4, 2003, 'Path 3', GeomFromText('LINESTRING(100 25,120 25,110 10,110 45)',4326));
--
-- SELECT DropGeometryColumn('sampleaopolygon', 'geom');
-- DROP TABLE sampleaopolygon;
-- CREATE TABLE sampleaopolygon
-- (
--   sampleaopolygon_id int8 NOT NULL,
--   id int8 NOT NULL,
--   name varchar(255),
--   CONSTRAINT sampleaopolygon_pkey PRIMARY KEY (sampleaopolygon_id)
-- ) 
-- WITHOUT OIDS;
-- SELECT AddGeometryColumn('sampleaopolygon', 'geom', 4326, 'POLYGON', 2);
INSERT INTO sampleaopolygon (sampleaopolygon_id, id, name, geom) VALUES (1, 3000, 'Polygon 0', NULL);
INSERT INTO sampleaopolygon (sampleaopolygon_id, id, name, geom) VALUES (2, 3001, 'Polygon 1', GeomFromText('POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))',4326));
INSERT INTO sampleaopolygon (sampleaopolygon_id, id, name, geom) VALUES (3, 3002, 'Polygon 2', GeomFromText('POLYGON((75 75,100 75,100 100,75 75))',4326));