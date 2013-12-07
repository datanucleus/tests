/**********************************************************************
 Copyright (c) 2006 Thomas Marti, Stefan Schmid and others. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.samples.pggeometry.SampleGeometryCollection;
import org.datanucleus.samples.pggeometry.SampleLineString;
import org.datanucleus.samples.pggeometry.SamplePoint;
import org.datanucleus.samples.pggeometry.SamplePolygon;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.StringUtils;
import org.postgis.Geometry;
import org.postgis.GeometryCollection;
import org.postgis.LineString;
import org.postgis.MultiLineString;
import org.postgis.MultiPoint;
import org.postgis.MultiPolygon;
import org.postgis.Point;
import org.postgis.Polygon;

/**
 * Series of spatial tests for PostGis functions.
 * Run on MySQL and Postgresql.
 *
 * @version $Revision: 1.3 $
 */
public class PgGeometrySpatialTest extends JDOPersistenceTestCase
{
    public PgGeometrySpatialTest(String name)
    {
        super(name);
    }

    static public Test suite()
    {
        // Extract the datastore being run
        String datastoreVendor = null;
        JDOPersistenceManagerFactory pmf = (JDOPersistenceManagerFactory)TestHelper.getPMF(1, null);
        StoreManager storeMgr = pmf.getNucleusContext().getStoreManager();
        if (!(storeMgr instanceof RDBMSStoreManager))
        {
            return null;
        }
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        if (srm.getDatastoreAdapter() != null)
        {
            // RDBMS datastores have a vendor id
            datastoreVendor = srm.getDatastoreAdapter().getVendorID();
        }

        TestSuite suite = new TestSuite();
        if (datastoreVendor.equalsIgnoreCase("mysql"))
        {
            // MySQL
            suite.addTest(new PgGeometrySpatialTest("testGeomFromText"));
            suite.addTest(new PgGeometrySpatialTest("testPointFromText"));
            suite.addTest(new PgGeometrySpatialTest("testLineFromText"));
            suite.addTest(new PgGeometrySpatialTest("testPolyFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMPointFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMLineFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMPolyFromText"));
            suite.addTest(new PgGeometrySpatialTest("testGeomCollFromText"));
            suite.addTest(new PgGeometrySpatialTest("testGeomFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testPointFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testLineFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testPolyFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMPointFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMLineFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMPolyFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testGeomCollFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testDimension")); 
            suite.addTest(new PgGeometrySpatialTest("testGeometryType"));
            suite.addTest(new PgGeometrySpatialTest("testSrid"));
            suite.addTest(new PgGeometrySpatialTest("testEnvelope"));
            suite.addTest(new PgGeometrySpatialTest("testAsText"));
            suite.addTest(new PgGeometrySpatialTest("testAsBinary"));
//            suite.addTest(new PgGeometrySpatialTest("testIsEmpty"));
//            suite.addTest(new PgGeometrySpatialTest("testIsSimple"));
//            suite.addTest(new PgGeometrySpatialTest("testBoundary"));
            suite.addTest(new PgGeometrySpatialTest("testEquals"));
            suite.addTest(new PgGeometrySpatialTest("testDisjoint"));
            suite.addTest(new PgGeometrySpatialTest("testIntersects"));
            suite.addTest(new PgGeometrySpatialTest("testTouches"));
//            suite.addTest(new PgGeometrySpatialTest("testCrosses"));
            suite.addTest(new PgGeometrySpatialTest("testWithin"));
            suite.addTest(new PgGeometrySpatialTest("testContains"));
            suite.addTest(new PgGeometrySpatialTest("testOverlaps"));
//            suite.addTest(new PgGeometrySpatialTest("testRelate"));
//            suite.addTest(new PgGeometrySpatialTest("testDistance"));
//            suite.addTest(new PgGeometrySpatialTest("testBuffer"));
//            suite.addTest(new PgGeometrySpatialTest("testConvexHull"));
//            suite.addTest(new PgGeometrySpatialTest("testIntersection"));
//            suite.addTest(new PgGeometrySpatialTest("testUnion"));
//            suite.addTest(new PgGeometrySpatialTest("testSymDifference"));
//            suite.addTest(new PgGeometrySpatialTest("testDifference"));
            suite.addTest(new PgGeometrySpatialTest("testX"));
            suite.addTest(new PgGeometrySpatialTest("testY"));
            suite.addTest(new PgGeometrySpatialTest("testStartPoint"));
            suite.addTest(new PgGeometrySpatialTest("testEndPoint"));
//            suite.addTest(new PgGeometrySpatialTest("testIsRing"));
            suite.addTest(new PgGeometrySpatialTest("testIsClosed"));
            suite.addTest(new PgGeometrySpatialTest("testLength"));
            suite.addTest(new PgGeometrySpatialTest("testNumPoints"));
            suite.addTest(new PgGeometrySpatialTest("testPointN"));
            suite.addTest(new PgGeometrySpatialTest("testArea"));
//            suite.addTest(new PgGeometrySpatialTest("testCentroid"));
//            suite.addTest(new PgGeometrySpatialTest("testPointOnSurfaceMethod"));
            suite.addTest(new PgGeometrySpatialTest("testExteriorRingMethod"));
            suite.addTest(new PgGeometrySpatialTest("testNumInteriorRingMethod"));
            suite.addTest(new PgGeometrySpatialTest("testInteriorRingNMethod"));
            suite.addTest(new PgGeometrySpatialTest("testNumGeometries"));
            suite.addTest(new PgGeometrySpatialTest("testGeometryN"));
//            suite.addTest(new PgGeometrySpatialTest("testBboxTest"));
        }
        else if (datastoreVendor.equalsIgnoreCase("postgresql"))
        {
            // Postgresql
            suite.addTest(new PgGeometrySpatialTest("testGeomFromText"));
            suite.addTest(new PgGeometrySpatialTest("testPointFromText"));
            suite.addTest(new PgGeometrySpatialTest("testLineFromText"));
            suite.addTest(new PgGeometrySpatialTest("testPolyFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMPointFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMLineFromText"));
            suite.addTest(new PgGeometrySpatialTest("testMPolyFromText"));
            suite.addTest(new PgGeometrySpatialTest("testGeomCollFromText"));
            suite.addTest(new PgGeometrySpatialTest("testGeomFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testPointFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testLineFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testPolyFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMPointFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMLineFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testMPolyFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testGeomCollFromWKB"));
            suite.addTest(new PgGeometrySpatialTest("testDimension")); 
            suite.addTest(new PgGeometrySpatialTest("testGeometryType"));
            suite.addTest(new PgGeometrySpatialTest("testSrid"));
            suite.addTest(new PgGeometrySpatialTest("testEnvelope"));
            suite.addTest(new PgGeometrySpatialTest("testAsText"));
            suite.addTest(new PgGeometrySpatialTest("testAsBinary"));
            suite.addTest(new PgGeometrySpatialTest("testIsEmpty"));
            suite.addTest(new PgGeometrySpatialTest("testIsSimple"));
            suite.addTest(new PgGeometrySpatialTest("testBoundary"));
            suite.addTest(new PgGeometrySpatialTest("testEquals"));
            suite.addTest(new PgGeometrySpatialTest("testDisjoint"));
            suite.addTest(new PgGeometrySpatialTest("testIntersects"));
            suite.addTest(new PgGeometrySpatialTest("testTouches"));
            suite.addTest(new PgGeometrySpatialTest("testCrosses"));
            suite.addTest(new PgGeometrySpatialTest("testWithin"));
            suite.addTest(new PgGeometrySpatialTest("testContains"));
            suite.addTest(new PgGeometrySpatialTest("testOverlaps"));
            suite.addTest(new PgGeometrySpatialTest("testRelate"));
            suite.addTest(new PgGeometrySpatialTest("testDistance"));
            suite.addTest(new PgGeometrySpatialTest("testBuffer"));
            suite.addTest(new PgGeometrySpatialTest("testConvexHull"));
            suite.addTest(new PgGeometrySpatialTest("testIntersection"));
            suite.addTest(new PgGeometrySpatialTest("testUnion"));
            suite.addTest(new PgGeometrySpatialTest("testSymDifference"));
            suite.addTest(new PgGeometrySpatialTest("testDifference"));
            suite.addTest(new PgGeometrySpatialTest("testX"));
            suite.addTest(new PgGeometrySpatialTest("testY"));
            suite.addTest(new PgGeometrySpatialTest("testStartPoint"));
            suite.addTest(new PgGeometrySpatialTest("testEndPoint"));
            suite.addTest(new PgGeometrySpatialTest("testIsRing"));
            suite.addTest(new PgGeometrySpatialTest("testIsClosed"));
            suite.addTest(new PgGeometrySpatialTest("testLength"));
            suite.addTest(new PgGeometrySpatialTest("testNumPoints"));
            suite.addTest(new PgGeometrySpatialTest("testPointN"));
            suite.addTest(new PgGeometrySpatialTest("testArea"));
            suite.addTest(new PgGeometrySpatialTest("testCentroid"));
            suite.addTest(new PgGeometrySpatialTest("testPointOnSurfaceMethod"));
            suite.addTest(new PgGeometrySpatialTest("testExteriorRingMethod"));
            suite.addTest(new PgGeometrySpatialTest("testNumInteriorRingMethod"));
            suite.addTest(new PgGeometrySpatialTest("testInteriorRingNMethod"));
            suite.addTest(new PgGeometrySpatialTest("testNumGeometries"));
            suite.addTest(new PgGeometrySpatialTest("testGeometryN"));
            suite.addTest(new PgGeometrySpatialTest("testBboxTest"));
        }
        return suite;
    }

    public void testGeomFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.equals(geom, Spatial.geomFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(75 75)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.equals(geom, Spatial.pointFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testLineFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "LINESTRING(50 0, 50 100)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.equals(geom, Spatial.lineFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkt", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPolyFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POLYGON((75 75,100 75,100 100,75 75))";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.equals(geom, Spatial.polyFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkt", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPointFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTIPOINT(10 10,75 75)";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mPointFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMLineFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTILINESTRING((0 50,100 50),(50 0,50 100),(100 25,120 25,110 10,110 45))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleLineString.class,
                "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mLineFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given wkt", list.contains(getSampleLineString(3)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPolyFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTIPOLYGON(((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)),((75 75,100 75,100 100,75 75)))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 2);
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mPolyFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkt", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomCollFromText() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.geomCollFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point geom = new Point("SRID=4326;POINT(10 10)");
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.equals(geom, Spatial.geomFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point geom = new Point("SRID=4326;POINT(75 75)");
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.equals(geom, Spatial.pointFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testLineFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString geom = new LineString("SRID=4326;LINESTRING(50 0, 50 100)");
            Query query = pm.newQuery(SampleLineString.class,
                "geom != null && Spatial.equals(geom, Spatial.lineFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPolyFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon geom = new Polygon("SRID=4326;POLYGON((75 75,100 75,100 100,75 75))");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.equals(geom, Spatial.polyFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkb", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPointFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiPoint geom = new MultiPoint("SRID=4326;MULTIPOINT(10 10,75 75)");
            Short n = new Short((short) 1);
            Query query = pm
                    .newQuery(SamplePoint.class,
                        "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mPointFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMLineFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiLineString geom = new MultiLineString(
                    "SRID=4326;MULTILINESTRING((0 50,100 50),(50 0,50 100),(100 25,120 25,110 10,110 45))");
            Short n = new Short((short) 3);
            Query query = pm
                    .newQuery(SampleLineString.class,
                        "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mLineFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(3)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPolyFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiPolygon geom = new MultiPolygon(
                    "SRID=4326;MULTIPOLYGON(((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)),((75 75,100 75,100 100,75 75)))");
            Short n = new Short((short) 2);
            Query query = pm
                    .newQuery(SamplePolygon.class,
                        "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mPolyFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkb", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomCollFromWKB() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            GeometryCollection geom = new GeometryCollection(
                    "SRID=4326;GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))");
            Short n = new Short((short) 1);
            Query query = pm
                    .newQuery(SamplePoint.class,
                        "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.geomCollFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDimension() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer dim = new Integer(1);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.dimension(geom) == :dim");
            List list = (List) query.execute(dim);
            assertEquals("Wrong number of geometries with dimension " + dim + " returned", 3, list.size());
            assertTrue("LineString 1 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.dimension(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer dim_read = (Integer) query.execute(new Long(getSampleLineString(2).getId()));
            assertEquals("Dimension of LineString 2 should be equal to a given dimension", dim, dim_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeometryType() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String type = "POINT";
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.geometryType(geom) == :type");
            List list = (List) query.execute(type);
            assertEquals("Wrong number of geometries with type " + type + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with type " + type, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with type " + type, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.geometryType(geom)");
            query.setResultClass(String.class);
            query.setUnique(true);
            String type_read = (String) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Geometry type of Point 1 should be equal to a given geometry type", type, type_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testSrid() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer srid = new Integer(4326); // WGS84
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.srid(geom) == :srid");
            List list = (List) query.execute(srid);
            assertEquals("Wrong number of geometries with srid " + srid + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.srid(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer srid_read = (Integer) query.execute(new Long(getSamplePoint(2).getId()));
            assertEquals("SRID of Point 2 should be equal to a given SRID", srid, srid_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEnvelope() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon envelope = new Polygon("SRID=4326;POLYGON((100 10,120 10,120 45,100 45,100 10))");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.equals(Spatial.envelope(geom), :envelope)");
            List list = (List) query.execute(envelope);
            assertEquals("Wrong number of geometries with a given envelope returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given envelope", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.envelope(geom)");
            query.setUnique(true);
            Geometry envelope_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned envelope should be equal to a given envelope (geometry type)", envelope.getType(), envelope_read
                    .getType());
            assertEquals("Returned envelope should be equal to a given envelope (srid)", envelope.getSrid(), envelope_read.getSrid());
            assertEquals("Returned envelope should be equal to a given envelope (no. points)", envelope.numPoints(), envelope_read
                    .numPoints());
            assertEquals("Returned envelope should be equal to a given envelope (minx, miny)", envelope.getPoint(0), envelope_read
                    .getPoint(0));
            assertEquals("Returned envelope should be equal to a given envelope (maxx, maxy)", envelope.getPoint(2), envelope_read
                    .getPoint(2));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsText() throws SQLException
    {
        SamplePoint point1 = getSamplePoint(1);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.asText(geom) == :wkt");
            List list = (List) query.execute(wkt);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(point1));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResultClass(String.class);
            query.setResult("Spatial.asText(geom)");
            list = (List) query.execute(new Long(point1.getId()));
            assertEquals("Wrong number of geometries with a given id returned", 1, list.size());
            String wkt_read = (String) list.get(0);
            assertEquals("WKT of Point 1 should be equal to a given point's wkt", wkt, wkt_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsBinary() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.asText(Spatial.pointFromWKB(Spatial.asBinary(geom), Spatial.srid(geom))) == :wkt");
            List list = (List) query.execute(wkt);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsEmpty() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = new LineString("SRID=4326;LINESTRING(25 25, 25 75)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.isEmpty(Spatial.intersection(geom, :linestring))");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries that do not intersect with a given linestring returned", 2, list.size());
            assertTrue("LineString 2 should be in the list of geometries that do not intersect with a given linestring", list
                    .contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries that do not intersect with a given linestring", list
                    .contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "geom != null");
            query
                    .setResult("Spatial.isEmpty(Spatial.intersection(geom, Spatial.geomFromText('LINESTRING(25 25, 25 75)', 4326))) AS isEmpty");
            query.setResultClass(Boolean.class);
            list = (List) query.execute();
            assertEquals("Wrong number of geometries returned", 3, list.size());
            assertEquals("The intersection of LineString 1 with a given linestring should not be empty", false, ((Boolean) list.get(0))
                    .booleanValue());
            assertEquals("The intersection of LineString 2 with a given linestring should be empty", true, ((Boolean) list.get(1))
                    .booleanValue());
            assertEquals("The intersection of LineString 3 with a given linestring should be empty", true, ((Boolean) list.get(2))
                    .booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsSimple() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.isSimple(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of simple geometries returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of simple geometries", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of simple geometries", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.isSimple(geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean isSimple = (Boolean) query.execute(new Long(getSampleLineString(2).getId()));
            assertEquals("LineString 2 is simple", true, isSimple.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBoundary() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString boundary = new LineString("SRID=4326;LINESTRING(75 75,100 75,100 100,75 75)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.equals(Spatial.boundary(geom), :boundary)");
            List list = (List) query.execute(boundary);
            assertEquals("Wrong number of geometries with a given boundary returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given boundary", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.boundary(geom)");
            query.setUnique(true);
            Geometry boundary_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Boundary of Polygon 2 should be equal to a given boundary", boundary, boundary_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEquals() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.equals(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are equal to a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which are equal to a given polygon", list
                    .contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query
                    .setResult("Spatial.equals(geom, Spatial.geomFromText('POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))', 4326))");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should be equal to the given polygon", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDisjoint() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.disjoint(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are disjoint from a given polygon returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which are disjoint from a given polygon", list
                    .contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.disjoint(geom, Spatial.geomFromText('POLYGON((10 10,40 10,40 40,10 40,10 10))', 4326))");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Polygon 2 should be disjoint from the given polygon", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIntersects() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.intersects(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which intersect with a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which intersect with a given polygon", list
                    .contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.intersects(geom, Spatial.geomFromText('POLYGON((10 10,40 10,40 40,10 40,10 10))', 4326))");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should intersect the given polygon", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testTouches() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(75 75)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.touches(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which are touched by a given point returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which are touched by a given point", list
                    .contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries which are touched by a given point", list
                    .contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.touches(Spatial.geomFromText('POINT(75 75)', 4326), geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should be touched by the given point", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testCrosses() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = new LineString("SRID=4326;LINESTRING(25 25,25 75)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.crosses(:lineString, geom)");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries which are crossed by a given linestring returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which are crossed by a given linestring", list
                    .contains(getSampleLineString(1)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.crosses(Spatial.geomFromText('LINESTRING(25 25,25 75)', 4326), geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSampleLineString(1).getId()));
            assertEquals("LineString 1 should be crossed by the given linestring", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testWithin() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.within(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries where a given point is within returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries where a given point is within", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.within(Spatial.geomFromText('POINT(30 30)', 4326), geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Given point should be in Polygon 1", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testContains() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.contains(geom, :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which contain a given point returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which contain a given point", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.contains(geom, Spatial.geomFromText('POINT(30 30)', 4326))");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should contain the given point", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testOverlaps() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((10 10,50 10,50 50,10 50,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.overlaps(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which overlap a given polygon returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which overlap a given polygon", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.overlaps(geom, Spatial.geomFromText('POLYGON((10 10,50 10,50 50,10 50,10 10))', 4326))");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should overlap the given polygon", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testRelate() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = new LineString("SRID=4326;LINESTRING(25 25,25 75)");
            String pattern = "T*T******"; // crosses
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.relate(geom, :lineString, :pattern)");
            List list = (List) query.execute(lineString, pattern);
            assertEquals("Wrong number of geometries which are related to a given linestring returned (crosses)", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which are related to a given linestring (crosses)", list
                    .contains(getSampleLineString(1)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.relate(geom, Spatial.geomFromText('LINESTRING(25 25,25 75)', 4326), 'T*T******')");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSampleLineString(1).getId()));
            assertEquals("LineString 1 should be crossed by the given linestring (relate mask T*T******)", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDistance() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(0 10)");
            Double distance = new Double(10.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.distance(geom, :point) == :distance");
            List list = (List) query.execute(point, distance);
            assertEquals("Wrong number of geometries with a distance of " + distance + " to a given point returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a distance of " + distance + " to a given point", list
                    .contains(getSamplePoint(1)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.distance(geom, Spatial.geomFromText('POINT(0 10)', 4326))");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double distance_read = (Double) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Point 1 should be in the given distance from the given point", distance, distance_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBuffer() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(0 0)");
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.within(geom, Spatial.buffer(:point, 20))");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which are within the buffer of a given point returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries which are within the buffer of a given point", list
                    .contains(getSamplePoint(1)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.buffer(geom, 20)");
            query.setUnique(true);
            Geometry buffer = (Geometry) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Returned buffer should be a polygon", "POLYGON", buffer.getTypeString());
            assertEquals("Returned buffer should have the given srid", 4326, buffer.getSrid());
            assertTrue("First point of returned buffer should have to be in a given distance from Point 1", buffer.getFirstPoint()
                    .distance(point) >= 20.0);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testConvexHull() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon convexHull = new Polygon("SRID=4326;POLYGON((110 10,120 25,110 45,100 25,110 10))");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.equals(Spatial.convexHull(geom), :convexHull)");
            List list = (List) query.execute(convexHull);
            assertEquals("Wrong number of geometries with a given convex hull returned", 1, list.size());
            assertTrue("LineSting 3 should be in the list of geometries with a given convex hull", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.convexHull(geom)");
            query.setUnique(true);
            Geometry convexHull_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned convex hull should be a polygon", "POLYGON", convexHull.getTypeString());
            assertEquals("Returned convex hull should have the given srid", 4326, convexHull.getSrid());
            assertEquals("First point of returned convex hull should be equal to given point", convexHull.getFirstPoint(), convexHull_read
                    .getFirstPoint());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIntersection() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = new LineString("SRID=4326;LINESTRING(25 25, 25 75)");
            Point intersection = new Point("SRID=4326;POINT(25 50)");
            Query query = pm.newQuery(SampleLineString.class,
                "geom != null && Spatial.asText(Spatial.intersection(geom, :lineString)) == Spatial.asText(:intersection)");
            List list = (List) query.execute(lineString, intersection);
            assertEquals("Wrong number of geometries which intersect a given linestring at a given point returned", 1, list.size());
            assertTrue("LineSting 1 should be in the list of geometries which intersect a given linestring at a given point", list
                    .contains(getSampleLineString(1)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.intersection(geom, Spatial.geomFromText('LINESTRING(25 25, 25 75)', 4326))");
            query.setUnique(true);
            Geometry intersection_read = (Geometry) query.execute(new Long(getSampleLineString(1).getId()));
            assertEquals("Returned intersection should be equal to the given point", intersection, intersection_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testUnion() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(50 50)");
            MultiPoint union = new MultiPoint("SRID=4326;MULTIPOINT(10 10,50 50)");
            Query query = pm.newQuery(SamplePoint.class,
                "geom != null && Spatial.asText(Spatial.union(geom, :point)) == Spatial.asText(:union)");
            List list = (List) query.execute(point, union);
            assertEquals("Wrong number of geometries whose union with a given point is equal to a given multipoint returned", 1, list
                    .size());
            assertTrue("Point 1 should be in the list of geometries whose union with a given point is equal to a given multipoint", list
                    .contains(getSamplePoint(1)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.union(geom, Spatial.geomFromText('POINT(50 50)', 4326))");
            query.setUnique(true);
            Geometry union_read = (Geometry) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Returned union should be equal to the given multipoint", union, union_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testSymDifference() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((75 75, 125 75, 125 100, 75 100, 75 75))");
            MultiPolygon symDifference = new MultiPolygon(
                    "SRID=4326;MULTIPOLYGON(((100 75,100 100,125 100,125 75,100 75)),((100 100,75 75,75 100,100 100)))");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.equals(Spatial.symDifference(geom, :polygon), :symDifference)");
            List list = (List) query.execute(polygon, symDifference);
            assertEquals("Wrong number of geometries whose symDifference to a given polygon is equal to a given multipolygon returned", 1,
                list.size());
            assertTrue(
                "Polygon 2 should be in the list of geometries whose symDifference to a given polygon is equal to a given multipolygon",
                list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.symDifference(geom, Spatial.geomFromText('POLYGON((75 75, 125 75, 125 100, 75 100, 75 75))', 4326))");
            query.setUnique(true);
            Geometry symDifference_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Returned symDifference should be equal to the given multipolygon", symDifference, symDifference_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDifference() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = new Polygon("SRID=4326;POLYGON((75 75,100 75,100 80,80 80,75 75))");
            Polygon difference = new Polygon("SRID=4326;POLYGON((80 80,100 100,100 80,80 80))");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.equals(Spatial.difference(geom, :polygon), :difference)");
            List list = (List) query.execute(polygon, difference);
            assertEquals("Wrong number of geometries whose difference from a given polygon is equal to a given polygon returned", 1, list
                    .size());
            assertTrue("Polygon 2 should be in the list of geometries whose difference from a given polygon is equal to a given polygon",
                list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.difference(geom, Spatial.geomFromText('POLYGON((75 75,100 75,100 80,80 80,75 75))', 4326))");
            query.setUnique(true);
            Geometry difference_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Returned difference should be equal to the given polygon", difference, difference_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testX() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double x = new Double(75.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.x(geom) == :x");
            List list = (List) query.execute(x);
            assertEquals("Wrong number of geometries with a given x = " + x + " returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given x = " + x, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.x(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double x_read = (Double) query.execute(new Long(getSamplePoint(2).getId()));
            assertEquals("Returned x should be equal to the given value", x, x_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testY() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double y = new Double(10.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.y(geom) == :y");
            List list = (List) query.execute(y);
            assertEquals("Wrong number of geometries with a given y = " + y + " returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given y = " + y, list.contains(getSamplePoint(1)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("Spatial.y(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double y_read = (Double) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Returned y should be equal to the given value", y, y_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testStartPoint() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(50 0)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.equals(Spatial.startPoint(geom), :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given start point returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given start point", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.startPoint(geom)");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(2).getId()));
            assertEquals("Returned start point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEndPoint() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(110 45)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.equals(Spatial.endPoint(geom), :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given end point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given end point", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.endPoint(geom)");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned end point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsRing() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.isRing(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of rings returned", 0, list.size());

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.isRing(geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean isRing = (Boolean) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("LineString 3 should not be a ring", false, isRing.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsClosed() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.isClosed(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of closed geometries returned", 0, list.size());

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.isClosed(geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean isClosed = (Boolean) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("LineString 3 should not be closed", false, isClosed.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testLength() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double length = new Double(100.0);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.length(geom) == :length");
            List list = (List) query.execute(length);
            assertEquals("Wrong number of geometries with length " + length + " returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of geometries with length " + length, list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with length " + length, list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.length(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double length_read = (Double) query.execute(new Long(getSampleLineString(1).getId()));
            assertEquals("Returned length should be equal to the given value", length, length_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumPoints() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(2);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && Spatial.numPoints(geom) == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of geometries with " + num + " points returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.numPoints(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer num_read = (Integer) query.execute(new Long(getSampleLineString(1).getId()));
            assertEquals("Returned number of points should be equal to the given value", num, num_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointN() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(110 10)");
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleLineString.class,
                "geom != null && Spatial.asText(Spatial.pointN(geom, :n)) == Spatial.asText(:point)");
            List list = (List) query.execute(n, point);
            assertEquals("Wrong number of geometries whose point no. " + n + " equals a given point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries whose point no. " + n + " equals a given point", list
                    .contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("Spatial.pointN(geom, 3)");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned third point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testArea() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double area = new Double(2400.0);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.area(geom) == :area");
            List list = (List) query.execute(area);
            assertEquals("Wrong number of geometries with an area of " + area + " returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with an area of " + area, list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.area(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double area_read = (Double) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Returned length should be equal to the given value", area, area_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testCentroid() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point centroid = new Point("SRID=4326;POINT(50 50)");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.asText(Spatial.centroid(geom)) == Spatial.asText(:centroid)");
            List list = (List) query.execute(centroid);
            assertEquals("Wrong number of geometries with a given centroid returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given centroid", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.centroid(geom)");
            query.setUnique(true);
            Geometry centroid_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Given point shoul be centroid of Polygon 1", centroid, centroid_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointOnSurfaceMethod() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.pointOnSurface(geom) != null");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with a point on the surface returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a point on the surface", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with a point on the surface", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.pointOnSurface(geom)");
            query.setUnique(true);
            Geometry pointOnSurface = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertNotNull("Polygon 1 should have a point on the surface", pointOnSurface);
            assertEquals("Polygon 1 should have a point on the surface", "POINT", pointOnSurface.getTypeString());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testExteriorRingMethod() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString exteriorRing = new LineString("SRID=4326;LINESTRING(25 25,75 25,75 75,25 75,25 25)");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.asText(Spatial.exteriorRing(geom)) == Spatial.asText(:exteriorRing)");
            List list = (List) query.execute(exteriorRing);
            assertEquals("Wrong number of geometries with a given exterior ring returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given exterior ring", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.exteriorRing(geom)");
            query.setUnique(true);
            Geometry exteriorRing_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Exterior ring of Polygon 1 should be equal to the given linestring", exteriorRing, exteriorRing_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumInteriorRingMethod() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(1);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.numInteriorRing(geom) == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of geometries with " + num + " interior rings returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with " + num + " interior rings", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.numInteriorRing(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer num_read = (Integer) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should have one interior ring", num, num_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testInteriorRingNMethod() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Short n = new Short((short) 1);
            LineString interiorRing = new LineString("SRID=4326;LINESTRING(45 45,55 45,55 55,45 55,45 45)");
            Query query = pm.newQuery(SamplePolygon.class,
                "geom != null && Spatial.asText(Spatial.interiorRingN(geom, :n)) == Spatial.asText(:interiorRing)");
            List list = (List) query.execute(n, interiorRing);
            assertEquals("Wrong number of geometries whose " + n + "th interior ring is equal to a given linestring returned", 1, list
                    .size());
            assertTrue("Polygon 1 should be in the list of geometries whose " + n + "th interior ring is equal to a given linestring", list
                    .contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.interiorRingN(geom, 1)");
            query.setUnique(true);
            Geometry interiorRing_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("First interior ring of Polygon 1 should be equal to the given linestring", interiorRing, interiorRing_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumGeometries() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(3);
            Query query = pm.newQuery(SampleGeometryCollection.class, "geom != null && Spatial.numGeometries(geom) == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of collections with " + num + " geometries returned", 2, list.size());
            assertTrue("Collection 1 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(1)));
            assertTrue("Collection 2 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(2)));

            query = pm.newQuery(SampleGeometryCollection.class, "id == :id");
            query.setResult("Spatial.numGeometries(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer num_read = (Integer) query.execute(new Long(getSampleGeometryCollection(1).getId()));
            assertEquals("Collection 1 should have three geometries", num, num_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeometryN() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Short n = new Short((short) 2);
            LineString lineString = new LineString("SRID=4326;LINESTRING(50 0,50 100)");
            Query query = pm.newQuery(SampleGeometryCollection.class,
                "geom != null && Spatial.asText(Spatial.geometryN(geom, :n)) == Spatial.asText(:lineString)");
            List list = (List) query.execute(n, lineString);
            assertEquals("Wrong number of collections whose " + n + "th geometry is equal to a given linestring returned", 1, list.size());
            assertTrue("Collection 2 should be in the list of collections whose " + n + "th geometry is equal to a given linestring", list
                    .contains(getSampleGeometryCollection(2)));

            query = pm.newQuery(SampleGeometryCollection.class, "id == :id");
            query.setResult("Spatial.geometryN(geom, 2)");
            query.setUnique(true);
            Geometry lineString_read = (Geometry) query.execute(new Long(getSampleGeometryCollection(2).getId()));
            assertEquals("Second geometry of Collection 2 should be equal to the given linestring", lineString, lineString_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBboxTest() throws SQLException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = new Point("SRID=4326;POINT(90 90)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && Spatial.bboxTest(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which pass the bbox test with a given point returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which pass the bbox test with a given point", list
                    .contains(getSamplePolygon(2)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("Spatial.bboxTest(Spatial.geomFromText('POINT(90 90)', 4326), geom)");
            query.setResultClass(Boolean.class);
            query.setUnique(true);
            Boolean equals = (Boolean) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Polygon 2 should pass bbox test with the given point", true, equals.booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loadData();
    }

    protected void tearDown() throws Exception
    {
        clean(SamplePoint.class);
        clean(SampleLineString.class);
        clean(SamplePolygon.class);
        clean(SampleGeometryCollection.class);
        super.tearDown();
    }

    private SamplePoint getSamplePoint(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SamplePoint(1000, "Point 0", null);
            case 1 :
                return new SamplePoint(1001, "Point 1", new Point("SRID=4326;POINT(10 10)"));
            case 2 :
                return new SamplePoint(1002, "Point 2", new Point("SRID=4326;POINT(75 75)"));
        }
        return null;
    }

    private SampleLineString getSampleLineString(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SampleLineString(2000, "LineString 0", null);
            case 1 :
                return new SampleLineString(2001, "LineString 1", new LineString("SRID=4326;LINESTRING(0 50,100 50)"));
            case 2 :
                return new SampleLineString(2002, "LineString 2", new LineString("SRID=4326;LINESTRING(50 0,50 100)"));
            case 3 :
                return new SampleLineString(2003, "LineString 3", new LineString("SRID=4326;LINESTRING(100 25,120 25,110 10,110 45)"));
        }
        return null;
    }

    private SamplePolygon getSamplePolygon(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SamplePolygon(3000, "Polygon 0", null);
            case 1 :
                return new SamplePolygon(3001, "Polygon 1", new Polygon(
                        "SRID=4326;POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))"));
            case 2 :
                return new SamplePolygon(3002, "Polygon 2", new Polygon("SRID=4326;POLYGON((75 75,100 75,100 100,75 75))"));
        }
        return null;
    }

    private SampleGeometryCollection getSampleGeometryCollection(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SampleGeometryCollection(7100, "Collection 0", null);
            case 1 :
                return new SampleGeometryCollection(
                        7001,
                        "Collection 1",
                        new GeometryCollection(
                                "SRID=4326;GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))"));
            case 2 :
                return new SampleGeometryCollection(7002, "Collection 2", new GeometryCollection(
                        "SRID=4326;GEOMETRYCOLLECTION(POINT(75 75),LINESTRING(50 0,50 100),POLYGON((75 75,100 75,100 100,75 75)))"));
            case 3 :
                return new SampleGeometryCollection(7003, "Collection 3", new GeometryCollection(
                        "SRID=4326;GEOMETRYCOLLECTION(LINESTRING(100 25,120 25,110 10,110 45))"));
        }
        return null;
    }

    private void loadData() throws SQLException, IOException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.newQuery(SamplePoint.class).deletePersistentAll();
            pm.newQuery(SampleLineString.class).deletePersistentAll();
            pm.newQuery(SamplePolygon.class).deletePersistentAll();
            pm.newQuery(SampleGeometryCollection.class).deletePersistentAll();
        }
        finally
        {
            tx.commit();
        }
        Connection sqlConn = null;
        try
        {
            tx.begin();
            sqlConn = (Connection) pm.getDataStoreConnection();
            String connUrl = pmf.getConnectionURL().toLowerCase();
            String fileName = "sample_pg_postgis.sql";
            if (connUrl.contains("mysql"))
            {
                fileName = "sample_pg_mysql.sql";
            }
            File file = new File(PgGeometrySpatialTest.class.getResource("/org/datanucleus/samples/data/" + fileName).getFile());
            StringBuffer sb = new StringBuffer();
            InputStream is = new FileInputStream(file);
            int c;
            while ((c = is.read()) != -1)
            {
                sb.append((char) c);
            }
            int start = sb.indexOf("--");
            int end = -1;
            while ((start > 0 && sb.charAt(start - 1) == '\n') || start == 0)
            {
                end = Math.min(sb.indexOf("\n",start) + 1, sb.length() - 1);
                sb.delete(start, end);
                start = sb.indexOf("--", start);
            }
            String ss[] = StringUtils.split(sb.toString(), ";");
            for (int i = 0; i < ss.length; i++)
            {
                sqlConn.createStatement().execute(ss[i]);
            }
            is.close();
        }
        finally
        {
            sqlConn.close();
            tx.commit();
        }
    }

}