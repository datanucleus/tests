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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.jtsgeometry.SampleGeometryCollection;
import org.datanucleus.samples.jtsgeometry.SampleLineString;
import org.datanucleus.samples.jtsgeometry.SamplePoint;
import org.datanucleus.samples.jtsgeometry.SamplePolygon;
import org.datanucleus.util.StringUtils;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Series of tests for MySQL "MBR" spatial extensions to JTS.
 *
 * @version $Revision: 1.3 $
 */
public class JtsGeometryMySQLTest extends JDOPersistenceTestCase
{
    private static final WKTReader wktReader = new WKTReader(new GeometryFactory(new PrecisionModel(), 4326));

    public JtsGeometryMySQLTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        if (runTestsForDatastore())
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
                if (connUrl.contains("mysql") || connUrl.contains("mariadb"))
                {
                    fileName = "sample_jts_mysql.sql";
                }
                File file = new File(PgGeometryMySQLTest.class.getResource("/org/datanucleus/samples/data/" + fileName).toURI());
                String s = "";
                InputStream is = new FileInputStream(file);
                int c;
                while ((c = is.read()) != -1)
                {
                    s += (char) c;
                }
                String ss[] = StringUtils.split(s, ";");
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

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SamplePoint.class);
            clean(SampleLineString.class);
            clean(SamplePolygon.class);
            clean(SampleGeometryCollection.class);
        }
        super.tearDown();
   }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("mysql"));
    }

    public void testMbrEqual() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((25 25,75 25,75 75,25 75,25 25),(45 45,55 45,55 55,45 55,45 45))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrEqual(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are equal to a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which are equal to a given polygon", list
                    .contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrDisjoint() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrDisjoint(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are disjoint from a given polygon returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which are disjoint from a given polygon", list
                    .contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrIntersects() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,40 10,40 40,10 40,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrIntersects(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which intersect with a given polygon returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which intersect with a given polygon", list
                    .contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrTouches() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(75 75)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrTouches(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which are touched by a given point returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which are touched by a given point", list
                    .contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries which are touched by a given point", list
                    .contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrWithin() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrWithin(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries where a given point is within returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries where a given point is within", list.contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrContains() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Point point = (Point) wktReader.read("POINT(30 30)");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrContains(geom, :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which contain a given point returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which contain a given point", list.contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMbrOverlaps() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Polygon polygon = (Polygon) wktReader.read("POLYGON((10 10,50 10,50 50,10 50,10 10))");
            Query query = pm.newQuery(SamplePolygon.class, "geom != null && MySQL.mbrOverlaps(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which overlap a given linestring returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which overlap a given linestring", list
                    .contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
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
}