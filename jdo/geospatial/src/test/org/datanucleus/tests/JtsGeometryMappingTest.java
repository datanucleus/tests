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

import java.sql.SQLException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.jtsgeometry.SampleGeometry;
import org.datanucleus.samples.jtsgeometry.SampleGeometryCollection;
import org.datanucleus.samples.jtsgeometry.SampleLineString;
import org.datanucleus.samples.jtsgeometry.SampleLinearRing;
import org.datanucleus.samples.jtsgeometry.SampleMultiLineString;
import org.datanucleus.samples.jtsgeometry.SampleMultiPoint;
import org.datanucleus.samples.jtsgeometry.SampleMultiPolygon;
import org.datanucleus.samples.jtsgeometry.SamplePoint;
import org.datanucleus.samples.jtsgeometry.SamplePolygon;
import org.datanucleus.tests.JDOPersistenceTestCase;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Series of geometry mapping tests using JTS.
 * Run for MySQL and Postgresql currently.
 */
public class JtsGeometryMappingTest extends JDOPersistenceTestCase
{
    private static final WKTReader wktReader = new WKTReader(new GeometryFactory(new PrecisionModel(), 4326));
    
    public JtsGeometryMappingTest(String name)
    {
        super(name);
    }

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SampleGeometry.class);
            clean(SamplePoint.class);
            clean(SampleLinearRing.class);
            clean(SampleLineString.class);
            clean(SamplePolygon.class);
            clean(SampleMultiPoint.class);
            clean(SampleMultiLineString.class);
            clean(SampleMultiPolygon.class);
            clean(SampleGeometryCollection.class);
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("mysql") || vendorID.equalsIgnoreCase("postgresql"));
    }

    public void testNoUserDataMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setUserData(new Object());
        SampleGeometry sampleGeometry;
        SampleGeometry sampleGeometry_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometry = new SampleGeometry(10001, "NoUserData", point);
            pm.makePersistent(sampleGeometry);
            id = JDOHelper.getObjectId(sampleGeometry);
            sampleGeometry = (SampleGeometry) pm.detachCopy(sampleGeometry);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        pm = pmf.getPersistenceManager();
        pmf.getDataStoreCache().evictAll(); // Make sure not cached
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleGeometry_read = (SampleGeometry) pm.getObjectById(id, true);
            assertEquals(sampleGeometry, sampleGeometry_read);
            assertNull(sampleGeometry_read.getGeom().getUserData());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
    public void testUserDataMappingWithObject() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        Object userData = new Object();
        point.setUserData(userData);
        SamplePoint samplePoint;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(11001, "UserDataWithObject", point);
            pm.makePersistent(samplePoint);
            tx.commit();
            fail("Persisted spatial field with non-serializable user-data but should have failed");
        }
        catch (Exception e)
        {
            // Expected : should fail since Object is not Serializable
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testUserDataMappingWithString() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        Object userData = "UserDataString";
        point.setUserData(userData);
        SamplePoint samplePoint;
        SamplePoint samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(12001, "UserDataWithString", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SamplePoint) pm.detachCopy(samplePoint);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            samplePoint_read = (SamplePoint) pm.getObjectById(id, true);
            assertEquals(samplePoint, samplePoint_read);
            assertNotNull(samplePoint_read.getGeom().getUserData());
            assertEquals(String.class, samplePoint_read.getGeom().getUserData().getClass());
            assertEquals(userData, samplePoint_read.getGeom().getUserData());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
    public void testGeometryMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        SampleGeometry sampleGeometry;
        SampleGeometry sampleGeometry_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometry = new SampleGeometry(1001, "Geometry (Point)", point);
            pm.makePersistent(sampleGeometry);
            id = JDOHelper.getObjectId(sampleGeometry);
            sampleGeometry = (SampleGeometry) pm.detachCopy(sampleGeometry);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleGeometry_read = (SampleGeometry) pm.getObjectById(id, true);
            assertEquals(sampleGeometry, sampleGeometry_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
    
    public void testPointMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        SamplePoint samplePoint;
        SamplePoint samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(1001, "Point 1", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SamplePoint) pm.detachCopy(samplePoint);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            samplePoint_read = (SamplePoint) pm.getObjectById(id, true);
            assertEquals(samplePoint, samplePoint_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testLinearRingMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        LinearRing linearRing = (LinearRing) wktReader.read("LINEARRING(0 0,10 0,10 10,0 10,0 0)");
        SampleLinearRing sampleLinearRing;
        SampleLinearRing sampleLinearRing_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleLinearRing = new SampleLinearRing(2101, "LinearRing", linearRing);
            pm.makePersistent(sampleLinearRing);
            id = JDOHelper.getObjectId(sampleLinearRing);
            sampleLinearRing = (SampleLinearRing) pm.detachCopy(sampleLinearRing);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleLinearRing_read = (SampleLinearRing) pm.getObjectById(id, true);
            assertEquals(sampleLinearRing, sampleLinearRing_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testLineStringMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        LineString lineString = (LineString) wktReader.read("LINESTRING(0 50,100 50)");
        SampleLineString sampleLineString;
        SampleLineString sampleLineString_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleLineString = new SampleLineString(2001, "LineString 1", lineString);
            pm.makePersistent(sampleLineString);
            id = JDOHelper.getObjectId(sampleLineString);
            sampleLineString = (SampleLineString) pm.detachCopy(sampleLineString);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleLineString_read = (SampleLineString) pm.getObjectById(id, true);
            assertEquals(sampleLineString, sampleLineString_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testPolygonMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Polygon polygon = (Polygon) wktReader.read("POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))");
        SamplePolygon samplePolygon;
        SamplePolygon samplePolygon_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePolygon = new SamplePolygon(3001, "Polygon 1", polygon);
            pm.makePersistent(samplePolygon);
            id = JDOHelper.getObjectId(samplePolygon);
            samplePolygon = (SamplePolygon) pm.detachCopy(samplePolygon);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            samplePolygon_read = (SamplePolygon) pm.getObjectById(id, true);
            assertEquals(samplePolygon, samplePolygon_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testMultiPointMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        MultiPoint multiPoint = (MultiPoint) wktReader.read("MULTIPOINT(10 10,75 75)");
        SampleMultiPoint sampleMultiPoint;
        SampleMultiPoint sampleMultiPoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiPoint = new SampleMultiPoint(4001, "MultiPoint", multiPoint);
            pm.makePersistent(sampleMultiPoint);
            id = JDOHelper.getObjectId(sampleMultiPoint);
            sampleMultiPoint = (SampleMultiPoint) pm.detachCopy(sampleMultiPoint);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleMultiPoint_read = (SampleMultiPoint) pm.getObjectById(id, true);
            assertEquals(sampleMultiPoint, sampleMultiPoint_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testMultiLineStringMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        MultiLineString multiLineString = (MultiLineString)wktReader.read("MULTILINESTRING((0 50,100 50),(50 0,50 100),(100 25,120 25,110 10,110 45))");
        SampleMultiLineString sampleMultiLineString;
        SampleMultiLineString sampleMultiLineString_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiLineString = new SampleMultiLineString(5001, "MultiLineString", multiLineString);
            pm.makePersistent(sampleMultiLineString);
            id = JDOHelper.getObjectId(sampleMultiLineString);
            sampleMultiLineString = (SampleMultiLineString) pm.detachCopy(sampleMultiLineString);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleMultiLineString_read = (SampleMultiLineString) pm.getObjectById(id, true);
            assertEquals(sampleMultiLineString, sampleMultiLineString_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testMultiPolygonMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        MultiPolygon multiPolygon = (MultiPolygon) wktReader.read("MULTIPOLYGON(((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)),((75 75,100 75,100 100,75 75)))");
        SampleMultiPolygon sampleMultiPolygon;
        SampleMultiPolygon sampleMultiPolygon_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiPolygon = new SampleMultiPolygon(6001, "MultiPolygon", multiPolygon);
            pm.makePersistent(sampleMultiPolygon);
            id = JDOHelper.getObjectId(sampleMultiPolygon);
            sampleMultiPolygon = (SampleMultiPolygon) pm.detachCopy(sampleMultiPolygon);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleMultiPolygon_read = (SampleMultiPolygon) pm.getObjectById(id, true);
            assertEquals(sampleMultiPolygon, sampleMultiPolygon_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    public void testGeometryCollectionMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        GeometryCollection geomCollection = (GeometryCollection)wktReader.read("GEOMETRYCOLLECTION(POINT(10 10),LINESTRING(0 50, 100 50),POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45)))");
        SampleGeometryCollection sampleGeometryCollection;
        SampleGeometryCollection sampleGeometryCollection_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometryCollection = new SampleGeometryCollection(7001, "Collection 1", geomCollection);
            pm.makePersistent(sampleGeometryCollection);
            id = JDOHelper.getObjectId(sampleGeometryCollection);
            sampleGeometryCollection = (SampleGeometryCollection) pm.detachCopy(sampleGeometryCollection);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            sampleGeometryCollection_read = (SampleGeometryCollection) pm.getObjectById(id, true);
            assertEquals(sampleGeometryCollection, sampleGeometryCollection_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
}