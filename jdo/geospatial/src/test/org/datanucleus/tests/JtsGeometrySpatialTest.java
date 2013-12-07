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
import org.datanucleus.samples.jtsgeometry.SampleGeometryCollection;
import org.datanucleus.samples.jtsgeometry.SampleLineString;
import org.datanucleus.samples.jtsgeometry.SamplePoint;
import org.datanucleus.samples.jtsgeometry.SamplePolygon;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.StringUtils;
import org.postgis.MultiLineString;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Series of tests for JTS spatial functions.
 * Apply to MySQL and Postgresql.
 */
public class JtsGeometrySpatialTest extends JDOPersistenceTestCase
{
    private static final WKTReader wktReader = new WKTReader(new GeometryFactory(new PrecisionModel(), 4326));

    public JtsGeometrySpatialTest(String name)
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
            suite.addTest(new JtsGeometrySpatialTest("testGeomFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testPointFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testLineFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testPolyFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMPointFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMLineFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMPolyFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomCollFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testPointFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testLineFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testPolyFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMPointFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMLineFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMPolyFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomCollFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testDimension")); 
            suite.addTest(new JtsGeometrySpatialTest("testGeometryType"));
            suite.addTest(new JtsGeometrySpatialTest("testSrid"));
            suite.addTest(new JtsGeometrySpatialTest("testEnvelope"));
            suite.addTest(new JtsGeometrySpatialTest("testAsText"));
            suite.addTest(new JtsGeometrySpatialTest("testAsBinary"));
//            suite.addTest(new JtsGeometrySpatialTest("testIsEmpty"));
//            suite.addTest(new JtsGeometrySpatialTest("testIsSimple"));
//            suite.addTest(new JtsGeometrySpatialTest("testBoundary"));
            suite.addTest(new JtsGeometrySpatialTest("testEquals"));
            suite.addTest(new JtsGeometrySpatialTest("testDisjoint"));
            suite.addTest(new JtsGeometrySpatialTest("testIntersects"));
            suite.addTest(new JtsGeometrySpatialTest("testTouches"));
//            suite.addTest(new JtsGeometrySpatialTest("testCrosses"));
            suite.addTest(new JtsGeometrySpatialTest("testWithin"));
            suite.addTest(new JtsGeometrySpatialTest("testContains"));
            suite.addTest(new JtsGeometrySpatialTest("testOverlaps"));
//            suite.addTest(new JtsGeometrySpatialTest("testRelate"));
//            suite.addTest(new JtsGeometrySpatialTest("testDistance"));
//            suite.addTest(new JtsGeometrySpatialTest("testBuffer"));
//            suite.addTest(new JtsGeometrySpatialTest("testConvexHull"));
//            suite.addTest(new JtsGeometrySpatialTest("testIntersection"));
//            suite.addTest(new JtsGeometrySpatialTest("testUnion"));
//            suite.addTest(new JtsGeometrySpatialTest("testSymDifference"));
//            suite.addTest(new JtsGeometrySpatialTest("testDifference"));
            suite.addTest(new JtsGeometrySpatialTest("testX"));
            suite.addTest(new JtsGeometrySpatialTest("testY"));
            suite.addTest(new JtsGeometrySpatialTest("testStartPoint"));
            suite.addTest(new JtsGeometrySpatialTest("testEndPoint"));
//            suite.addTest(new JtsGeometrySpatialTest("testIsRing"));
            suite.addTest(new JtsGeometrySpatialTest("testIsClosed"));
            suite.addTest(new JtsGeometrySpatialTest("testLength"));
            suite.addTest(new JtsGeometrySpatialTest("testNumPoints"));
            suite.addTest(new JtsGeometrySpatialTest("testPointN"));
            suite.addTest(new JtsGeometrySpatialTest("testArea"));
//            suite.addTest(new JtsGeometrySpatialTest("testCentroid"));
//            suite.addTest(new JtsGeometrySpatialTest("testPointOnSurfaceMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testExteriorRingMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testNumInteriorRingMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testInteriorRingNMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testNumGeometries"));
            suite.addTest(new JtsGeometrySpatialTest("testGeometryN"));
        }
        else if (datastoreVendor.equalsIgnoreCase("postgresql"))
        {
            // Postgresql
            suite.addTest(new JtsGeometrySpatialTest("testGeomFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testPointFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testLineFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testPolyFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMPointFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMLineFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testMPolyFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomCollFromText"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testPointFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testLineFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testPolyFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMPointFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMLineFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testMPolyFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testGeomCollFromWKB"));
            suite.addTest(new JtsGeometrySpatialTest("testDimension")); 
            suite.addTest(new JtsGeometrySpatialTest("testGeometryType"));
            suite.addTest(new JtsGeometrySpatialTest("testSrid"));
            suite.addTest(new JtsGeometrySpatialTest("testEnvelope"));
            suite.addTest(new JtsGeometrySpatialTest("testAsText"));
            suite.addTest(new JtsGeometrySpatialTest("testAsBinary"));        
            suite.addTest(new JtsGeometrySpatialTest("testIsEmpty"));
            suite.addTest(new JtsGeometrySpatialTest("testIsSimple"));
            suite.addTest(new JtsGeometrySpatialTest("testBoundary"));
            suite.addTest(new JtsGeometrySpatialTest("testEquals"));
            suite.addTest(new JtsGeometrySpatialTest("testDisjoint"));
            suite.addTest(new JtsGeometrySpatialTest("testIntersects"));
            suite.addTest(new JtsGeometrySpatialTest("testTouches"));
            suite.addTest(new JtsGeometrySpatialTest("testCrosses"));
            suite.addTest(new JtsGeometrySpatialTest("testWithin"));
            suite.addTest(new JtsGeometrySpatialTest("testContains"));
            suite.addTest(new JtsGeometrySpatialTest("testOverlaps"));
            suite.addTest(new JtsGeometrySpatialTest("testRelate"));
            suite.addTest(new JtsGeometrySpatialTest("testDistance"));
            suite.addTest(new JtsGeometrySpatialTest("testBuffer"));
            suite.addTest(new JtsGeometrySpatialTest("testConvexHull"));
            suite.addTest(new JtsGeometrySpatialTest("testIntersection"));
            suite.addTest(new JtsGeometrySpatialTest("testUnion"));
            suite.addTest(new JtsGeometrySpatialTest("testSymDifference"));
            suite.addTest(new JtsGeometrySpatialTest("testDifference"));
            suite.addTest(new JtsGeometrySpatialTest("testX"));
            suite.addTest(new JtsGeometrySpatialTest("testY"));
            suite.addTest(new JtsGeometrySpatialTest("testStartPoint"));
            suite.addTest(new JtsGeometrySpatialTest("testEndPoint"));
            suite.addTest(new JtsGeometrySpatialTest("testIsRing"));
            suite.addTest(new JtsGeometrySpatialTest("testIsClosed"));
            suite.addTest(new JtsGeometrySpatialTest("testLength"));
            suite.addTest(new JtsGeometrySpatialTest("testNumPoints"));
            suite.addTest(new JtsGeometrySpatialTest("testPointN"));
            suite.addTest(new JtsGeometrySpatialTest("testArea"));
            suite.addTest(new JtsGeometrySpatialTest("testCentroid"));
            suite.addTest(new JtsGeometrySpatialTest("testPointOnSurfaceMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testExteriorRingMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testNumInteriorRingMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testInteriorRingNMethod"));
            suite.addTest(new JtsGeometrySpatialTest("testNumGeometries"));
            suite.addTest(new JtsGeometrySpatialTest("testGeometryN"));
            suite.addTest(new JtsGeometrySpatialTest("testBboxTest"));
        }
        return suite;
    }

    public void testGeomFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geomFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }
 
    public void testPointFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(75 75)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.pointFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(2)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testLineFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "LINESTRING(50 0, 50 100)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.equals(Spatial.lineFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkt", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPolyFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POLYGON((75 75,100 75,100 100,75 75))";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.equals(Spatial.polyFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkt", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPointFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTIPOINT(10 10,75 75)";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mPointFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testMLineFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTILINESTRING((0 50,100 50),(50 0,50 100),(100 25,120 25,110 10,110 45))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mLineFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given wkt", list.contains(getSampleLineString(3)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testMPolyFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTIPOLYGON(((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)),((75 75,100 75,100 100,75 75)))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 2);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mPolyFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkt", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomCollFromText() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.geomCollFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point geom = (Point) wktReader.read("POINT(10 10)");
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geomFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testPointFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point geom = (Point) wktReader.read("POINT(75 75)");
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.pointFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(2)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testLineFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString geom = (LineString) wktReader.read("LINESTRING(50 0, 50 100)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.equals(Spatial.lineFromWKB(Spatial.asBinary(:geom), :geom.getSRID()))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPolyFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon geom = (Polygon) wktReader.read("POLYGON((75 75,100 75,100 100,75 75))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.equals(Spatial.polyFromWKB(Spatial.asBinary(:geom), :geom.getSRID()))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkb", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPointFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiPoint geom = (MultiPoint) wktReader.read("MULTIPOINT(10 10,75 75)");
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mPointFromWKB(Spatial.asBinary(:geom), :geom.getSRID()), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testMLineFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiLineString geom = new MultiLineString("SRID=4326;MULTILINESTRING((0 50,100 50),(50 0,50 100),(100 25,120 25,110 10,110 45))");
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mLineFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(3)));
        }
        finally
        {
            tx.commit();
        }
    }
    
    public void testMPolyFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            MultiPolygon geom = (MultiPolygon) wktReader.read("MULTIPOLYGON(((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)),((75 75,100 75,100 100,75 75)))");
            Short n = new Short((short) 2);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.mPolyFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given wkb", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomCollFromWKB() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            GeometryCollection geom = (GeometryCollection) wktReader.read("GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))");
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.equals(Spatial.geometryN(Spatial.geomCollFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkb", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDimension() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer dim = new Integer(1);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getDimension() == :dim");
            List list = (List) query.execute(dim);
            assertEquals("Wrong number of geometries with dimension " + dim + " returned", 3, list.size());
            assertTrue("LineString 1 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries with dimension " + dim, list.contains(getSampleLineString(3)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getDimension()");
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

    public void testGeometryType() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String type = "POINT";
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.getGeometryType() == :type");
            List list = (List) query.execute(type);
            assertEquals("Wrong number of geometries with type " + type + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with type " + type, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with type " + type, list.contains(getSamplePoint(2)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("geom.getGeometryType()");
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

    public void testSrid() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer srid = new Integer(4326); // WGS84
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.getSRID() == :srid");
            List list = (List) query.execute(srid);
            assertEquals("Wrong number of geometries with srid " + srid + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(2)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("geom.getSRID()");
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

    public void testEnvelope() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon envelope = (Polygon) wktReader.read("POLYGON((100 10,120 10,120 45,100 45,100 10))");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getEnvelope().equals(:envelope)");
            List list = (List) query.execute(envelope);
            assertEquals("Wrong number of geometries with a given envelope returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given envelope", list.contains(getSampleLineString(3)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getEnvelope()");
            list = (List) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Wrong number of geometries with a given id returned", 1, list.size());
            Geometry envelope_read = (Geometry) list.get(0);
            envelope.normalize();
            envelope_read.normalize();        
            assertTrue("Returned envelope should be equal to a given envelope", envelope_read.equalsExact(envelope));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsText() throws SQLException, ParseException
    {
        SamplePoint point1 = getSamplePoint(1);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.toText() == :wkt");
            List list = (List) query.execute(wkt);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(point1));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResultClass(String.class);
            query.setResult("geom.toText()");
            list = (List) query.execute(new Long(point1.getId()));
            assertEquals("Wrong number of geometries with a given id returned", 1, list.size());
            String wkt_read = (String)list.get(0);
            assertEquals("WKT of Point 1 should be equal to a given point's wkt", wkt, wkt_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsBinary() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(10 10)";
            Query query = pm.newQuery(SamplePoint.class, "geom != null && Spatial.asText(Spatial.pointFromWKB(Spatial.asBinary(geom), Spatial.srid(geom))) == :wkt");
            List list = (List) query.execute(wkt);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsEmpty() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = (LineString) wktReader.read("LINESTRING(25 25, 25 75)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.intersection(:linestring).isEmpty()");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries that do not intersect with a given linestring returned", 2, list.size());
            assertTrue("LineString 2 should be in the list of geometries that do not intersect with a given linestring", list.contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries that do not intersect with a given linestring", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleLineString.class, "geom != null");
            query.setResult("geom.intersection(Spatial.geomFromText('LINESTRING(25 25, 25 75)', 4326)).isEmpty()");
            query.setResultClass(Boolean.class);
            list = (List) query.execute();
            assertEquals("Wrong number of geometries returned", 3, list.size());
            assertEquals("The intersection of LineString 1 with a given linestring should not be empty", false, ((Boolean) list.get(0)).booleanValue());
            assertEquals("The intersection of LineString 2 with a given linestring should be empty", true, ((Boolean) list.get(1)).booleanValue());
            assertEquals("The intersection of LineString 3 with a given linestring should be empty", true, ((Boolean) list.get(2)).booleanValue());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsSimple() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.isSimple()");
            List list = (List) query.execute();
            assertEquals("Wrong number of simple geometries returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of simple geometries", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of simple geometries", list.contains(getSampleLineString(2)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.isSimple()");
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

    public void testBoundary() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString boundary = (LineString) wktReader.read("LINESTRING(75 75,100 75,100 100,75 75)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getBoundary().equals(:boundary)");
            List list = (List) query.execute(boundary);
            assertEquals("Wrong number of geometries with a given boundary returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given boundary", list.contains(getSamplePolygon(2)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getBoundary()");
            query.setUnique(true);
            Geometry boundary_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            boundary.normalize();
            boundary_read.normalize();
            assertTrue("Boundary of Polygon 2 should be equal to a given boundary", boundary_read.equalsExact(boundary)); 
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEquals() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.equals(:polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are equal to a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which are equal to a given polygon", list
                    .contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.equals(Spatial.geomFromText('POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))', 4326))");
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

    public void testDisjoint() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.disjoint(:polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are disjoint from a given polygon returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which are disjoint from a given polygon", list
                    .contains(getSamplePolygon(2)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.disjoint(Spatial.geomFromText('POLYGON((10 10,40 10,40 40,10 40,10 10))', 4326))");
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

    public void testIntersects() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.intersects(:polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which intersect with a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which intersect with a given polygon", list
                    .contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.intersects(Spatial.geomFromText('POLYGON((10 10,40 10,40 40,10 40,10 10))', 4326))");
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

    public void testTouches() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(75 75)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && :point.touches(geom)");
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

    public void testCrosses() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = (LineString) wktReader.read("LINESTRING(25 25,25 75)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && :lineString.crosses(geom)");
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

    public void testWithin() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && :point.within(geom)");
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

    public void testContains() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.contains(:point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which contain a given point returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which contain a given point", list.contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");          
            query.setResult("geom.contains(Spatial.geomFromText('POINT(30 30)', 4326))");
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

    public void testOverlaps() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,50 10,50 50,10 50,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.overlaps(:polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which overlap a given polygon returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which overlap a given polygon", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.overlaps(Spatial.geomFromText('POLYGON((10 10,50 10,50 50,10 50,10 10))', 4326))");
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

    public void testRelate() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = (LineString) wktReader.read("LINESTRING(25 25,25 75)");
            String pattern = "T*T******"; // crosses
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.relate(:lineString, :pattern)");
            List list = (List) query.execute(lineString, pattern);
            assertEquals("Wrong number of geometries which are related to a given linestring returned (crosses)", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which are related to a given linestring (crosses)", list
                    .contains(getSampleLineString(1)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.relate(Spatial.geomFromText('LINESTRING(25 25,25 75)', 4326), 'T*T******')");
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

    public void testDistance() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(0 10)");
            Double distance = new Double(10.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.distance(:point) == :distance");
            List list = (List) query.execute(point, distance);
            assertEquals("Wrong number of geometries with a distance of " + distance + " to a given point returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a distance of " + distance + " to a given point", list
                    .contains(getSamplePoint(1)));

            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("geom.distance(Spatial.geomFromText('POINT(0 10)', 4326))");
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

    public void testBuffer() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(0 0)");
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.within(:point.buffer(20))");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which are within the buffer of a given point returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries which are within the buffer of a given point", list
                    .contains(getSamplePoint(1)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("geom.buffer(20)");
            query.setUnique(true);
            Geometry buffer = (Geometry) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Returned buffer should be a polygon", "POLYGON", buffer.getGeometryType().toUpperCase());
            assertEquals("Returned buffer should have the given srid", 4326, buffer.getSRID());
            assertTrue("Given point should be within the returned buffer", point.within(buffer));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testConvexHull() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon convexHull = (Polygon) wktReader.read("POLYGON((110 10,120 25,110 45,100 25,110 10))");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.convexHull().equals(:convexHull)");
            List list = (List) query.execute(convexHull);
            assertEquals("Wrong number of geometries with a given convex hull returned", 1, list.size());
            assertTrue("LineSting 3 should be in the list of geometries with a given convex hull", list.contains(getSampleLineString(3)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.convexHull()");
            query.setUnique(true);
            Geometry convexHull_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            convexHull.normalize();
            convexHull_read.normalize();
            assertTrue("Returned convex hull should be equal to the given polygon", convexHull_read.equalsExact(convexHull));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIntersection() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString lineString = (LineString) wktReader.read("LINESTRING(25 25, 25 75)");
            Point intersection = (Point) wktReader.read("POINT(25 50)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.intersection(:lineString).toText() == Spatial.asText(:intersection)");
            List list = (List) query.execute(lineString, intersection);
            assertEquals("Wrong number of geometries which intersect a given linestring at a given point returned", 1, list.size());
            assertTrue("LineSting 1 should be in the list of geometries which intersect a given linestring at a given point", list
                    .contains(getSampleLineString(1)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.intersection(Spatial.geomFromText('LINESTRING(25 25, 25 75)', 4326))");
            query.setUnique(true);
            Geometry intersection_read = (Geometry) query.execute(new Long(getSampleLineString(1).getId()));
            assertTrue("Returned intersection should be equal to the given point", intersection_read.equalsExact(intersection));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testUnion() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(50 50)");
            MultiPoint union = (MultiPoint) wktReader.read("MULTIPOINT(10 10,50 50)");
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.union(:point).toText() == :union.toText()");
            List list = (List) query.execute(point, union);
            assertEquals("Wrong number of geometries whose union with a given point is equal to a given multipoint returned", 1, list
                    .size());
            assertTrue("Point 1 should be in the list of geometries whose union with a given point is equal to a given multipoint", list
                    .contains(getSamplePoint(1)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");
            query.setResult("geom.union(Spatial.geomFromText('POINT(50 50)', 4326))");
            query.setUnique(true);
            Geometry union_read = (Geometry) query.execute(new Long(getSamplePoint(1).getId()));
            union.normalize();
            union_read.normalize();
            assertTrue("Returned union should be equal to the given multipoint", union_read.equalsExact(union));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testSymDifference() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((75 75, 125 75, 125 100, 75 100, 75 75))");
            MultiPolygon symDifference = (MultiPolygon) wktReader.read(
                    "MULTIPOLYGON(((100 75,100 100,125 100,125 75,100 75)),((100 100,75 75,75 100,100 100)))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.symDifference(:polygon).equals(:symDifference)");
            List list = (List) query.execute(polygon, symDifference);
            assertEquals("Wrong number of geometries whose symDifference to a given polygon is equal to a given multipolygon returned", 1,
                list.size());
            assertTrue(
                "Polygon 2 should be in the list of geometries whose symDifference to a given polygon is equal to a given multipolygon",
                list.contains(getSamplePolygon(2)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.symDifference(Spatial.geomFromText('POLYGON((75 75, 125 75, 125 100, 75 100, 75 75))', 4326))");
            query.setUnique(true);
            Geometry symDifference_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            symDifference.normalize();
            symDifference_read.normalize();
            assertTrue("Returned symDifference should be equal to the given multipolygon", symDifference_read.equalsExact(symDifference));
         }
        finally
        {
            tx.commit();
        }
    }

    public void testDifference() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((75 75,100 75,100 80,80 80,75 75))");
            Polygon difference = (Polygon) wktReader.read("POLYGON((80 80,100 100,100 80,80 80))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.difference(:polygon).equals(:difference)");
            List list = (List) query.execute(polygon, difference);
            assertEquals("Wrong number of geometries whose difference from a given polygon is equal to a given polygon returned", 1, list
                    .size());
            assertTrue("Polygon 2 should be in the list of geometries whose difference from a given polygon is equal to a given polygon",
                list.contains(getSamplePolygon(2)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.difference(Spatial.geomFromText('POLYGON((75 75,100 75,100 80,80 80,75 75))', 4326))");
            query.setUnique(true);
            Geometry difference_read = (Geometry) query.execute(new Long(getSamplePolygon(2).getId()));
            difference.normalize();
            difference_read.normalize();
            assertTrue("Returned difference should be equal to the given polygon", difference_read.equalsExact(difference));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testX() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double x = new Double(75.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.getX() == :x");
            List list = (List) query.execute(x);
            assertEquals("Wrong number of geometries with a given x = " + x + " returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given x = " + x, list.contains(getSamplePoint(2)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");      
            query.setResult("geom.getX()");
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

    public void testY() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double y = new Double(10.0);
            Query query = pm.newQuery(SamplePoint.class, "geom != null && geom.getY() == :y");
            List list = (List) query.execute(y);
            assertEquals("Wrong number of geometries with a given y = " + y + " returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given y = " + y, list.contains(getSamplePoint(1)));
            
            query = pm.newQuery(SamplePoint.class, "id == :id");      
            query.setResult("geom.getY()");
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

    public void testStartPoint() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(50 0)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getStartPoint().toText() == Spatial.asText(:point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given start point returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given start point", list.contains(getSampleLineString(2)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getStartPoint()");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(2).getId()));
            point.normalize();
            point_read.normalize();
            assertTrue("Returned start point should be equal to the given point", point_read.equalsExact(point));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEndPoint() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(110 45)");
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getEndPoint().toText() == Spatial.asText(:point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given end point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given end point", list.contains(getSampleLineString(3)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getEndPoint()");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            point.normalize();
            point_read.normalize();
            assertTrue("Returned end point should be equal to the given point", point_read.equalsExact(point));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsRing() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.isRing()");
            List list = (List) query.execute();
            assertEquals("Wrong number of rings returned", 0, list.size());
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.isRing()");
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

    public void testIsClosed() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.isClosed()");
            List list = (List) query.execute();
            assertEquals("Wrong number of closed geometries returned", 0, list.size());
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.isClosed()");
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

    public void testLength() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double length = new Double(100.0);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getLength() == :length");
            List list = (List) query.execute(length);
            assertEquals("Wrong number of geometries with length " + length + " returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of geometries with length " + length, list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with length " + length, list.contains(getSampleLineString(2)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getLength()");
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

    public void testNumPoints() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(2);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getNumPoints() == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of geometries with " + num + " points returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(2)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getNumPoints()");
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

    public void testPointN() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(110 10)");
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleLineString.class, "geom != null && geom.getPointN(:n).toText() == Spatial.asText(:point)");
            List list = (List) query.execute(n, point);
            assertEquals("Wrong number of geometries whose point no. " + n + " equals a given point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries whose point no. " + n + " equals a given point", list
                    .contains(getSampleLineString(3)));
            
            query = pm.newQuery(SampleLineString.class, "id == :id");
            query.setResult("geom.getPointN(3)");
            query.setUnique(true);
            Geometry point_read = (Geometry) query.execute(new Long(getSampleLineString(3).getId()));
            point.normalize();
            point_read.normalize();
            assertTrue("Returned third point should be equal to the given point", point_read.equalsExact(point));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testArea() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double area = new Double(2400.0);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getArea() == :area");
            List list = (List) query.execute(area);
            assertEquals("Wrong number of geometries with an area of " + area + " returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with an area of " + area, list.contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getArea()");
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

    public void testCentroid() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point centroid = (Point) wktReader.read("POINT(50 50)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getCentroid().toText() == Spatial.asText(:centroid)");
            List list = (List) query.execute(centroid);
            assertEquals("Wrong number of geometries with a given centroid returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given centroid", list.contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getCentroid()");
            query.setUnique(true);
            Geometry centroid_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("Given point shoul be centroid of Polygon 1", centroid_read.equalsExact(centroid));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointOnSurfaceMethod() throws SQLException, ParseException
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
            assertEquals("Polygon 1 should have a point on the surface", "POINT", pointOnSurface.getGeometryType().toUpperCase());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testExteriorRingMethod() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LineString exteriorRing = (LineString) wktReader.read("LINESTRING(25 25,75 25,75 75,25 75,25 25)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getExteriorRing().toText() == Spatial.asText(:exteriorRing)");
            List list = (List) query.execute(exteriorRing);
            assertEquals("Wrong number of geometries with a given exterior ring returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a given exterior ring", list.contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getExteriorRing()");
            query.setUnique(true);
            Geometry exteriorRing_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            exteriorRing.normalize();
            exteriorRing_read.normalize();
            assertTrue("Exterior ring of Polygon 1 should be equal to the given linestring", exteriorRing_read.equalsExact(exteriorRing));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumInteriorRingMethod() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(1);
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getNumInteriorRing() == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of geometries with " + num + " interior rings returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with " + num + " interior rings", list.contains(getSamplePolygon(1)));
            
            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getNumInteriorRing()");
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

    public void testInteriorRingNMethod() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Short n = new Short((short) 1);
            LineString interiorRing = (LineString) wktReader.read("LINESTRING(45 45,55 45,55 55,45 55,45 45)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && geom.getInteriorRingN(:n).toText() == Spatial.asText(:interiorRing)");
            List list = (List) query.execute(n, interiorRing);
            assertEquals("Wrong number of geometries whose " + n + "th interior ring is equal to a given linestring returned", 1, list
                    .size());
            assertTrue("Polygon 1 should be in the list of geometries whose " + n + "th interior ring is equal to a given linestring", list
                    .contains(getSamplePolygon(1)));

            query = pm.newQuery(SamplePolygon.class, "id == :id");
            query.setResult("geom.getInteriorRingN(1)");
            query.setUnique(true);
            Geometry interiorRing_read = (Geometry) query.execute(new Long(getSamplePolygon(1).getId()));
            interiorRing.normalize();
            interiorRing_read.normalize();
            assertTrue("First interior ring of Polygon 1 should be equal to the given linestring", interiorRing_read.equalsExact(interiorRing));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumGeometries() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer num = new Integer(3);
            Query query = pm.newQuery(SampleGeometryCollection.class, "geom != null && geom.getNumGeometries() == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of collections with " + num + " geometries returned", 2, list.size());
            assertTrue("Collection 1 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(1)));
            assertTrue("Collection 2 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(2)));
            
            query = pm.newQuery(SampleGeometryCollection.class, "id == :id");
            query.setResult("geom.getNumGeometries()");
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

    public void testGeometryN() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Short n = new Short((short) 2);
            LineString lineString = (LineString) wktReader.read("LINESTRING(50 0,50 100)");
            Query query = pm.newQuery(SampleGeometryCollection.class, "geom != null && geom.getGeometryN(:n).toText() == Spatial.asText(:lineString)");
            List list = (List) query.execute(n, lineString);
            assertEquals("Wrong number of collections whose " + n + "th geometry is equal to a given linestring returned", 1, list.size());
            assertTrue("Collection 2 should be in the list of collections whose " + n + "th geometry is equal to a given linestring", list
                    .contains(getSampleGeometryCollection(2)));
            
            query = pm.newQuery(SampleGeometryCollection.class, "id == :id");
            query.setResult("geom.getGeometryN(2)");
            query.setUnique(true);
            Geometry lineString_read = (Geometry) query.execute(new Long(getSampleGeometryCollection(2).getId()));
            lineString.normalize();
            lineString_read.normalize();
            assertTrue("Second geometry of Collection 2 should be equal to the given linestring", lineString_read.equalsExact(lineString));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBboxTest() throws SQLException, ParseException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(90 90)");
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

    private SamplePoint getSamplePoint(int num) throws SQLException, ParseException
    {
        switch (num)
        {
            case 0 :
                return new SamplePoint(1000, "Point 0", null);
            case 1 :
                return new SamplePoint(1001, "Point 1", (Point) wktReader.read("POINT(10 10)"));
            case 2 :
                return new SamplePoint(1002, "Point 2", (Point) wktReader.read("POINT(75 75)"));
        }
        return null;
    }

    private SampleLineString getSampleLineString(int num) throws SQLException, ParseException
    {
        switch (num)
        {
            case 0 :
                return new SampleLineString(2000, "LineString 0", null);
            case 1 :
                return new SampleLineString(2001, "LineString 1", (LineString) wktReader.read("LINESTRING(0 50,100 50)"));
            case 2 :
                return new SampleLineString(2002, "LineString 2", (LineString) wktReader.read("LINESTRING(50 0,50 100)"));
            case 3 :
                return new SampleLineString(2003, "LineString 3", (LineString) wktReader.read("LINESTRING(100 25,120 25,110 10,110 45)"));
        }
        return null;
    }

    private SamplePolygon getSamplePolygon(int num) throws SQLException, ParseException
    {
        switch (num)
        {
            case 0 :
                return new SamplePolygon(3000, "Polygon 0", null);
            case 1 :
                return new SamplePolygon(3001, "Polygon 1", (Polygon) wktReader.read(
                        "POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))"));
            case 2 :
                return new SamplePolygon(3002, "Polygon 2", (Polygon) wktReader.read("POLYGON((75 75,100 75,100 100,75 75))"));
        }
        return null;
    }

    private SampleGeometryCollection getSampleGeometryCollection(int num) throws SQLException, ParseException
    {
        switch (num)
        {
            case 0 :
                return new SampleGeometryCollection(7100, "Collection 0", null);
            case 1 :
                return new SampleGeometryCollection(
                        7001,
                        "Collection 1",
                        (GeometryCollection) wktReader.read(
                                "GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))"));
            case 2 :
                return new SampleGeometryCollection(7002, "Collection 2", (GeometryCollection) wktReader.read(
                        "GEOMETRYCOLLECTION(POINT(75 75),LINESTRING(50 0,50 100),POLYGON((75 75,100 75,100 100,75 75)))"));
            case 3 :
                return new SampleGeometryCollection(7003, "Collection 3", (GeometryCollection) wktReader.read(
                        "GEOMETRYCOLLECTION(LINESTRING(100 25,120 25,110 10,110 45))"));
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
            String fileName = "sample_jts_postgis.sql";
            if (connUrl.contains("mysql"))
            {
                fileName = "sample_jts_mysql.sql";
            }
            File file = new File(JtsGeometrySpatialTest.class.getResource("/org/datanucleus/samples/data/" + fileName).getFile());
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