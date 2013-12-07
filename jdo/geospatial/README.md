test-jdo-geospatial
===================

Tests for persistence of geospatial types using the JDO API.

To run these tests
`mvn clean test -Pmysql`      (for MySQL)
`mvn clean test -Ppostgresql` (for PostgreSQL)



To generate a PostGIS datastore you need PostgreSQL and PostGIS installed and
1. `createdb nucleus`
2. `psql -d nucleus -f /usr/share/postgresql/contrib/postgis-1.5/postgis.sql`
3. `psql -d nucleus -f /usr/share/postgresql/contrib/postgis-1.5/spatial_ref_sys.sql`

Note that these may need to be done as user "postgres"
