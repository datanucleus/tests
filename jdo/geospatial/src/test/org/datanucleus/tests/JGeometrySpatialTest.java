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

import oracle.spatial.geometry.JGeometry;

import org.datanucleus.samples.jgeometry.SampleGeometry;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.StringUtils;

/**
 * Series of tests for JGeometry spatial functions.
 * Run for Oracle only currently.
 *
 * @version $Revision: 1.2 $
 */
public class JGeometrySpatialTest extends JDOPersistenceTestCase
{
    public JGeometrySpatialTest(String name)
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
                pm.newQuery(SampleGeometry.class).deletePersistentAll();
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
                String fileName = "sample_sdo_geometry.sql";
                File file = new File(JGeometrySpatialTest.class.getResource("/org/datanucleus/samples/data/" + fileName).getFile());
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
                    end = Math.min(sb.indexOf("\n", start) + 1, sb.length() - 1);
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

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SampleGeometry.class);
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        // Run for Oracle only
        return (vendorID.equalsIgnoreCase("oracle"));
    }

    public void testGeomFromText() throws SQLException
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
            String wkt = "POINT(10 10)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class, "geom != null && Spatial.equals(geom, Spatial.geomFromText(:wkt, :srid))");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POINT(75.0 75.0)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.equals(geom, Spatial.pointFromText(:wkt, :srid))");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "LINESTRING(50.0 0.0, 50.0 100.0)";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 2000 && id < 3000 && Spatial.equals(geom, Spatial.lineFromText(:wkt, :srid))");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "POLYGON((75.0 75.0,100.0 75.0,100.0 100.0,75.0 75.0))";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 3000 && id < 4000 && Spatial.equals(geom, Spatial.polyFromText(:wkt, :srid))");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "MULTIPOINT((10.0 10.0),(20.0 20.0))"; // Oracle syntax
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.overlaps(geom, Spatial.mPointFromText(:wkt, :srid))"); // Oracle's
                                                                                                            // geometryN
                                                                                                            // on
                                                                                                            // multipoint
                                                                                                            // returns a
                                                                                                            // multipoint
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries whitch overlap a multipoint constructed from given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries whitch overlap a multipoint constructed from given wkt", list
                    .contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMLineFromText() throws SQLException
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
            String wkt = "MULTILINESTRING((0.0 50.0,100.0 50.0),(50.0 0.0,50.0 100.0),(100.0 25.0,120.0 25.0,110.0 10.0,110.0 45.0))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 2);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 2000 && id < 3000 && Spatial.equals(geom, Spatial.geometryN(Spatial.mLineFromText(:wkt, :srid), :n))");
            List list = (List) query.execute(wkt, srid, n);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkt", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPolyFromText() throws SQLException
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
            String wkt = "MULTIPOLYGON(((25.0 25.0,75.0 25.0,75.0 75.0,25.0 75.0,25.0 25.0),(45.0 45.0,55.0 45.0,55.0 55.0,45.0 55.0,45.0 45.0)),((75.0 75.0,100.0 75.0,100.0 100.0,75.0 75.0)))";
            Short srid = new Short((short) 4326);
            Short n = new Short((short) 2);
            Query query = pm.newQuery(SampleGeometry.class,
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String wkt = "GEOMETRYCOLLECTION(POINT(10.0 10.0),LINESTRING(0.0 50.0, 100.0 50.0),POLYGON((25.0 25.0,75.0 25.0,75.0 75.0,25.0 75.0,25.0 25.0)))";
            Short srid = new Short((short) 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 7000 && id < 8000 && Spatial.equals(geom, Spatial.geomCollFromText(:wkt, :srid))");
            List list = (List) query.execute(wkt, srid);
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Collection 1 should be in the list of geometries with a given wkt", list.contains(getSampleGeometryCollection(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeomFromWKB() throws SQLException
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
            JGeometry geom = new JGeometry(10.0, 10.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry geom = new JGeometry(75.0, 75.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry geom = JGeometry.createLinearLineString(new double[]{0, 50, 100, 50}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "geom != null && Spatial.equals(geom, Spatial.lineFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPolyFromWKB() throws SQLException
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
            JGeometry geom = JGeometry.createLinearPolygon(new Object[]{new double[]{75, 75, 100, 75, 100, 100, 75, 75}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry geom = JGeometry.createMultiPoint(new Object[]{new double[]{10.0, 10.0}, new double[]{20.0, 20.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.overlaps(geom, Spatial.mPointFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries whitch overlap a multipoint constructed from given wkb returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries whitch overlap a multipoint constructed from given wkb", list
                    .contains(getSamplePoint(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMLineFromWKB() throws SQLException
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
            JGeometry geom = JGeometry.createLinearMultiLineString(new Object[]{new double[]{0.0, 50.0, 100.0, 50.0},
                    new double[]{50.0, 0.0, 50.0, 100.0}, new double[]{100.0, 25.0, 120.0, 25.0, 110.0, 10.0, 110.0, 45.0}}, 2, 4326);
            Short n = new Short((short) 2);
            Query query = pm
                    .newQuery(SampleGeometry.class,
                        "geom != null && Spatial.equals(geom, Spatial.geometryN(Spatial.mLineFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), :n))");
            List list = (List) query.execute(geom, n);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given wkb", list.contains(getSampleLineString(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testMPolyFromWKB() throws SQLException
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
            int gtype = JGeometry.GTYPE_MULTIPOLYGON;
            int srid = 4326;
            int[] elemInfo = {1, 1003, 1, 11, 1003, 1};
            double[] ordinates = {25.0, 25.0, 75.0, 25.0, 75.0, 75.0, 25.0, 75.0, 25.0, 25.0, 75.0, 75.0, 100.0, 75.0, 100.0, 100.0, 75.0,
                    75.0};
            JGeometry geom = new JGeometry(gtype, srid, elemInfo, ordinates);
            Short n = new Short((short) 2);
            Query query = pm
                    .newQuery(SampleGeometry.class,
                        "id > 3000 && id < 4000 && Spatial.equals(geom, Spatial.geometryN(Spatial.mPolyFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)), 2))");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            int gtype = JGeometry.GTYPE_COLLECTION;
            int srid = 4326;
            int[] elemInfo = {1, 1, 1, 3, 2, 1, 7, 1003, 1};
            double[] ordinates = {10.0, 10.0, 0.0, 50.0, 100.0, 50.0, 25.0, 25.0, 75.0, 25.0, 75.0, 75.0, 25.0, 75.0, 25.0, 25.0};
            JGeometry geom = new JGeometry(gtype, srid, elemInfo, ordinates);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 7000 && id < 8000 && Spatial.equals(geom, Spatial.geomCollFromWKB(Spatial.asBinary(:geom), Spatial.srid(:geom)))");
            List list = (List) query.execute(geom);
            assertEquals("Wrong number of geometries with a given wkb returned", 1, list.size());
            assertTrue("Collection 1 should be in the list of geometries with a given wkb", list.contains(getSampleGeometryCollection(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDimension() throws SQLException
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
            Integer dim = new Integer(2);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.dimension(geom) == :dim");
            List list = (List) query.execute(dim);
            assertEquals("Wrong number of geometries with dimension " + dim + " returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with dimension " + dim, list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with dimension " + dim, list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.dimension(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer dim_read = (Integer) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Dimension of Polygon 2 should be equal to a given dimension", dim, dim_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testGeometryType() throws SQLException
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
            String type = "POINT";
            Query query = pm.newQuery(SampleGeometry.class, "geom != null && Spatial.geometryType(geom) == :type");
            List list = (List) query.execute(type);
            assertEquals("Wrong number of geometries with type " + type + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with type " + type, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with type " + type, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Integer srid = new Integer(4326); // WGS84
            Query query = pm.newQuery(SampleGeometry.class, "id > 1000 && id < 2000 && Spatial.srid(geom) == :srid");
            List list = (List) query.execute(srid);
            assertEquals("Wrong number of geometries with srid " + srid + " returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries with srid " + srid, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            int gtype = JGeometry.GTYPE_POLYGON;
            int srid = 4326;
            int[] elemInfo = new int[]{1, 1003, 3};
            double[] ordinates = new double[]{100.0, 10.0, 120.0, 45.0};
            JGeometry envelope = new JGeometry(gtype, srid, elemInfo, ordinates);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.equals(Spatial.envelope(geom), :envelope)");
            List list = (List) query.execute(envelope);
            assertEquals("Wrong number of geometries with a given envelope returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given envelope", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.envelope(geom)");
            query.setUnique(true);
            JGeometry envelope_read = (JGeometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned envelope should be equal to a given envelope", envelope, envelope_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsText() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        SampleGeometry point1 = getSamplePoint(1);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.asText(geom).startsWith('POINT') && Spatial.asText(geom).endsWith('(10.0 10.0)')");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with a given wkt returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given wkt", list.contains(point1));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResultClass(String.class);
            query.setResult("Spatial.asText(geom)");
            list = (List) query.execute(new Long(point1.getId()));
            assertEquals("Wrong number of geometries with a given id returned", 1, list.size());
            String wkt_read = (String) list.get(0);
            assertTrue("WKT of Point 1 should start with 'POINT'", wkt_read.startsWith("POINT"));
            assertTrue("WKT of Point 1 should end with '(10.0 10.0)'", wkt_read.endsWith("(10.0 10.0)"));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testAsBinary() throws SQLException
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
            JGeometry point = JGeometry.createPoint(new double[]{10.0, 10.0}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.equals(Spatial.pointFromWKB(Spatial.asBinary(geom), Spatial.srid(geom)), :point)");
            List list = (List) query.execute(point);
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry lineString = JGeometry.createLinearLineString(new double[]{25.0, 25.0, 25.0, 75.0}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 2000 && id < 3000 && Spatial.isEmpty(Spatial.intersection(geom, :linestring))");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries that do not intersect with a given linestring returned", 2, list.size());
            assertTrue("LineString 2 should be in the list of geometries that do not intersect with a given linestring", list
                    .contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries that do not intersect with a given linestring", list
                    .contains(getSampleLineString(3)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsSimple() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.isSimple(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of simple geometries returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of simple geometries", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of simple geometries", list.contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBoundary() throws SQLException
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
            JGeometry boundary = JGeometry.createLinearLineString(new double[]{75.0, 75.0, 100.0, 75.0, 100.0, 100.0, 75.0, 75.0}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.equals(Spatial.boundary(geom), :boundary)");
            List list = (List) query.execute(boundary);
            assertEquals("Wrong number of geometries with a given boundary returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries with a given boundary", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.boundary(geom)");
            query.setUnique(true);
            JGeometry boundary_read = (JGeometry) query.execute(new Long(getSamplePolygon(2).getId()));
            assertEquals("Boundary of Polygon 2 should be equal to a given boundary", boundary, boundary_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEquals() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(
                new Object[]{new double[]{75.0, 75.0, 100.0, 75.0, 100.0, 100.0, 75.0, 75.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.equals(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which are equal to a given polygon returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which are equal to a given polygon", list
                    .contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDisjoint() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(new Object[]{new double[]{10.0, 10.0, 40.0, 10.0, 40.0, 40.0, 10.0, 40.0,
                    10.0, 10.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.disjoint(geom, :polygon)");
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

    public void testIntersects() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(new Object[]{new double[]{10.0, 10.0, 40.0, 10.0, 40.0, 40.0, 10.0, 40.0,
                    10.0, 10.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.intersects(geom, :polygon)");
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

    public void testTouches() throws SQLException
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
            JGeometry point = new JGeometry(75.0, 75.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.touches(:point, geom)");
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

    public void testCrosses() throws SQLException
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
            JGeometry lineString = JGeometry.createLinearLineString(new double[]{25.0, 25.0, 25.0, 75.0}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.crosses(geom, :lineString)");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries which are crossed by a given linestring returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which are crossed by a given linestring", list
                    .contains(getSampleLineString(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testWithin() throws SQLException
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
            JGeometry point = new JGeometry(30.0, 30.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.within(:point, geom)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries where a given point is within returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries where a given point is within", list.contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testContains() throws SQLException
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
            JGeometry point = new JGeometry(30.0, 30.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.contains(geom, :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which contain a given point returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which contain a given point", list.contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testOverlaps() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(new Object[]{new double[]{10.0, 10.0, 50.0, 10.0, 50.0, 50.0, 10.0, 50.0,
                    10.0, 10.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.overlaps(geom, :polygon)");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries which overlap a given polygon returned", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which overlap a given polygon", list.contains(getSamplePolygon(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testRelate() throws SQLException
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
            JGeometry lineString = JGeometry.createLinearLineString(new double[]{25.0, 25.0, 25.0, 75.0}, 2, 4326);
            String pattern = "T*T******"; // crosses
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.relate(geom, :lineString, :pattern)");
            List list = (List) query.execute(lineString, pattern);
            assertEquals("Wrong number of geometries which are related to a given linestring returned (crosses)", 1, list.size());
            assertTrue("LineString 1 should be in the list of geometries which are related to a given linestring (crosses)", list
                    .contains(getSampleLineString(1)));
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDistance() throws SQLException
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
            JGeometry point = new JGeometry(10.0, 10.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 1000 && id < 2000 && Spatial.distance(geom, :point) > 0.0");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a distance > 0.0 to a given point returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a distance > 0 to a given point", list.contains(getSamplePoint(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.distance(geom, Spatial.geomFromText('POINT(10.0 10.0)', 4326))");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double distance_read = (Double) query.execute(new Long(getSamplePoint(2).getId()));
            assertTrue("Point 2 should be in a distance > 0.0 to the given point", distance_read.doubleValue() > 0.0);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBuffer() throws SQLException
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
            JGeometry point = new JGeometry(10.0, 10.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 1000 && id < 2000 && Spatial.within(geom, Spatial.buffer(:point, 1000.0))");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which are within the buffer of a given point returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries which are within the buffer of a given point", list
                    .contains(getSamplePoint(1)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.buffer(geom, 1000.0)");
            query.setUnique(true);
            JGeometry buffer = (JGeometry) query.execute(new Long(getSamplePoint(1).getId()));
            assertEquals("Returned buffer should be a polygon", 3, buffer.getType());
            assertEquals("Returned buffer should have the given srid", 4326, buffer.getSRID());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testConvexHull() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && !Spatial.isEmpty(Spatial.convexHull(geom))");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries for which a convex hull can be calculated returned", 1, list.size());
            assertTrue("LineSting 3 should be in the list of geometries with a given convex hull", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.convexHull(geom)");
            query.setUnique(true);
            JGeometry convexHull_read = (JGeometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned convex hull should be of type polygon", JGeometry.GTYPE_POLYGON, convexHull_read.getType());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIntersection() throws SQLException
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
            JGeometry lineString = JGeometry.createLinearLineString(new double[]{25.0, 25.0, 25.0, 75.0}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.intersection(geom, :lineString) != null");
            List list = (List) query.execute(lineString);
            assertEquals("Wrong number of geometries which intersect a given linestring returned", 1, list.size());
            assertTrue("LineSting 1 should be in the list of geometries which intersect a given linestring", list
                    .contains(getSampleLineString(1)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.intersection(geom, Spatial.geomFromText('LINESTRING(25.0 25.0, 25.0 75.0)', 4326))");
            query.setUnique(true);
            JGeometry intersection_read = (JGeometry) query.execute(new Long(getSampleLineString(1).getId()));
            assertTrue("Returned intersection should be equal to the given point", intersection_read.getType() == JGeometry.GTYPE_POINT);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testUnion() throws SQLException
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
            JGeometry point = new JGeometry(50.0, 50.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 1000 && id < 2000 && Spatial.union(geom, :point) != null");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which can union with a given point returned", 2, list.size());
            assertTrue("Point 1 should be in the list of geometries which can union with a given point", list.contains(getSamplePoint(1)));
            assertTrue("Point 2 should be in the list of geometries which can union with a given point", list.contains(getSamplePoint(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.union(geom, Spatial.geomFromText('POINT(50.0 50.0)', 4326))");
            query.setUnique(true);
            JGeometry union_read = (JGeometry) query.execute(new Long(getSamplePoint(1).getId()));
            assertTrue("Returned union should be a multipoint", union_read.getType() == JGeometry.GTYPE_MULTIPOINT);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testSymDifference() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(new Object[]{new double[]{20.0, 20.0, 30.0, 20.0, 30.0, 30.0, 20.0, 30.0,
                    20.0, 20.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.symDifference(geom, :polygon) != null");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries whose symDifference to a given polygon is not null returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries whose symDifference to a given polygon is not null", list
                    .contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries whose symDifference to a given polygon is not null", list
                    .contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query
                    .setResult("Spatial.symDifference(geom, Spatial.geomFromText('POLYGON((20.0 20.0,30.0 20.0,30.0 30.0,20.0 30.0,20.0 20.0))', 4326))");
            query.setUnique(true);
            JGeometry symDifference_read = (JGeometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("Returned symDifference should be a multipolygon", symDifference_read.getType() == JGeometry.GTYPE_MULTIPOLYGON);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testDifference() throws SQLException
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
            JGeometry polygon = JGeometry.createLinearPolygon(new Object[]{new double[]{20.0, 20.0, 30.0, 20.0, 30.0, 30.0, 20.0, 30.0,
                    20.0, 20.0}}, 2, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.difference(geom, :polygon) != null");
            List list = (List) query.execute(polygon);
            assertEquals("Wrong number of geometries whose difference from a given polygon is not null returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries whose difference from a given polygon is not null", list
                    .contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries whose difference from a given polygon is not null", list
                    .contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query
                    .setResult("Spatial.difference(geom, Spatial.geomFromText('POLYGON((20.0 20.0,30.0 20.0,30.0 30.0,20.0 30.0,20.0 20.0))', 4326))");
            query.setUnique(true);
            JGeometry difference_read = (JGeometry) query.execute(new Long(getSamplePolygon(2).getId()));
            assertTrue("Returned difference should be a polygon", difference_read.getType() == JGeometry.GTYPE_POLYGON);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testX() throws SQLException
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
            Double x = new Double(75.0);
            Query query = pm.newQuery(SampleGeometry.class, "id > 1000 && id < 2000 && Spatial.x(geom) == :x");
            List list = (List) query.execute(x);
            assertEquals("Wrong number of geometries with a given x = " + x + " returned", 1, list.size());
            assertTrue("Point 2 should be in the list of geometries with a given x = " + x, list.contains(getSamplePoint(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Double y = new Double(10.0);
            Query query = pm.newQuery(SampleGeometry.class, "id > 1000 && id < 2000 && Spatial.y(geom) == :y");
            List list = (List) query.execute(y);
            assertEquals("Wrong number of geometries with a given y = " + y + " returned", 1, list.size());
            assertTrue("Point 1 should be in the list of geometries with a given y = " + y, list.contains(getSamplePoint(1)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry point = new JGeometry(50.0, 0.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.equals(Spatial.startPoint(geom), :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given start point returned", 1, list.size());
            assertTrue("LineString 2 should be in the list of geometries with a given start point", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.startPoint(geom)");
            query.setUnique(true);
            JGeometry point_read = (JGeometry) query.execute(new Long(getSampleLineString(2).getId()));
            assertEquals("Returned start point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testEndPoint() throws SQLException
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
            JGeometry point = new JGeometry(110.0, 45.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.equals(Spatial.endPoint(geom), :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries with a given end point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries with a given end point", list.contains(getSampleLineString(3)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.endPoint(geom)");
            query.setUnique(true);
            JGeometry point_read = (JGeometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned end point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsRing() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.isRing(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of rings returned", 0, list.size());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testIsClosed() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.isClosed(geom)");
            List list = (List) query.execute();
            assertEquals("Wrong number of closed geometries returned", 0, list.size());
        }
        finally
        {
            tx.commit();
        }
    }

    public void testLength() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.length(geom) > 0.0");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with length > 0 returned", 3, list.size());
            assertTrue("LineString 1 should be in the list of geometries with length > 0", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with length > 0", list.contains(getSampleLineString(2)));
            assertTrue("LineString 3 should be in the list of geometries with length > 0", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.length(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double length_read = (Double) query.execute(new Long(getSampleLineString(1).getId()));
            assertTrue("Returned length should be > 0", length_read.doubleValue() > 0.0);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumPoints() throws SQLException
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
            Integer num = new Integer(2);
            Query query = pm.newQuery(SampleGeometry.class, "id > 2000 && id < 3000 && Spatial.numPoints(geom) == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of geometries with " + num + " points returned", 2, list.size());
            assertTrue("LineString 1 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(1)));
            assertTrue("LineString 2 should be in the list of geometries with " + num + " points", list.contains(getSampleLineString(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            JGeometry point = new JGeometry(110.0, 10.0, 4326);
            Short n = new Short((short) 3);
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 2000 && id < 3000 && Spatial.numPoints(geom) >= :n && Spatial.equals(Spatial.pointN(geom, :n), :point)");
            List list = (List) query.execute(n, point);
            assertEquals("Wrong number of geometries whose point no. " + n + " equals a given point returned", 1, list.size());
            assertTrue("LineString 3 should be in the list of geometries whose point no. " + n + " equals a given point", list
                    .contains(getSampleLineString(3)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.pointN(geom, 3)");
            query.setUnique(true);
            JGeometry point_read = (JGeometry) query.execute(new Long(getSampleLineString(3).getId()));
            assertEquals("Returned third point should be equal to the given point", point, point_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testArea() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.area(geom) > 0.0");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with an area > 0 returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with an area > 0", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with an area > 0", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.area(geom)");
            query.setResultClass(Double.class);
            query.setUnique(true);
            Double area_read = (Double) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("Returned area should be > 0", area_read.doubleValue() > 0.0);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testCentroid() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.centroid(geom) != null");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries whith have a centroid returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a centroid", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with a centroid", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.centroid(geom)");
            query.setUnique(true);
            JGeometry centroid_read = (JGeometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("Centroid of Polygon 1 should be a point", centroid_read.getType() == JGeometry.GTYPE_POINT);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testPointOnSurfaceMethod() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.pointOnSurface(geom) != null");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with a point on the surface returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with a point on the surface", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with a point on the surface", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.pointOnSurface(geom)");
            query.setUnique(true);
            JGeometry pointOnSurface = (JGeometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertNotNull("Polygon 1 should have a point on the surface", pointOnSurface);
            assertTrue("Returned geometry should be a point", pointOnSurface.getType() == JGeometry.GTYPE_POINT);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testExteriorRingMethod() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.exteriorRing(geom) != null");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries whith have an exterior ring returned", 2, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with an exterior ring", list.contains(getSamplePolygon(1)));
            assertTrue("Polygon 2 should be in the list of geometries with an exterior ring", list.contains(getSamplePolygon(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.exteriorRing(geom)");
            query.setUnique(true);
            JGeometry exteriorRing_read = (JGeometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("Exterior ring of Polygon 1 should be a polygon", exteriorRing_read.getType() == JGeometry.GTYPE_POLYGON);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumInteriorRingMethod() throws SQLException
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
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.numInteriorRing(geom) > 0");
            List list = (List) query.execute();
            assertEquals("Wrong number of geometries with interior ring(s) returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries with interior ring(s)", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.numInteriorRing(geom)");
            query.setResultClass(Integer.class);
            query.setUnique(true);
            Integer num_read = (Integer) query.execute(new Long(getSamplePolygon(1).getId()));
            assertEquals("Polygon 1 should have one interior ring", new Integer(1), num_read);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testInteriorRingNMethod() throws SQLException
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
            Short n = new Short((short) 1);
            Query query = pm.newQuery(SampleGeometry.class, "id == 3001 && Spatial.interiorRingN(geom, 1) != null");
            List list = (List) query.execute(n);
            assertEquals("Wrong number of geometries which have interior ring(s) returned", 1, list.size());
            assertTrue("Polygon 1 should be in the list of geometries which have interior ring(s)", list.contains(getSamplePolygon(1)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.interiorRingN(geom, 1)");
            query.setUnique(true);
            JGeometry interiorRing_read = (JGeometry) query.execute(new Long(getSamplePolygon(1).getId()));
            assertTrue("First interior ring of Polygon 1 should be a polygon", interiorRing_read.getType() == JGeometry.GTYPE_POLYGON);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testNumGeometries() throws SQLException
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
            Integer num = new Integer(3);
            Query query = pm.newQuery(SampleGeometry.class, "id > 7000 && id < 8000 && Spatial.numGeometries(geom) == :num");
            List list = (List) query.execute(num);
            assertEquals("Wrong number of collections with " + num + " geometries returned", 2, list.size());
            assertTrue("Collection 1 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(1)));
            assertTrue("Collection 2 should be in the list of collections with " + num + " geometries", list
                    .contains(getSampleGeometryCollection(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
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
        if (!runTestsForDatastore())
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery(SampleGeometry.class,
                "id > 7000 && id < 8000 && Spatial.geometryType(Spatial.geometryN(geom, 1)).toUpperCase() == 'POINT'");
            List list = (List) query.execute();
            assertEquals("Wrong number of collections whose first geometry is equal to a given point returned", 2, list.size());
            assertTrue("Collection 1 should be in the list of collections whose first geometry is a point", list
                    .contains(getSampleGeometryCollection(2)));
            assertTrue("Collection 2 should be in the list of collections whose first geometry is a point", list
                    .contains(getSampleGeometryCollection(2)));

            query = pm.newQuery(SampleGeometry.class, "id == :id");
            query.setResult("Spatial.geometryN(geom, 1)");
            query.setUnique(true);
            JGeometry point_read = (JGeometry) query.execute(new Long(getSampleGeometryCollection(2).getId()));
            assertTrue("First geometry of Collection 2 should be a point", point_read.getType() == JGeometry.GTYPE_POINT);
        }
        finally
        {
            tx.commit();
        }
    }

    public void testBboxTest() throws SQLException
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
            JGeometry point = new JGeometry(90.0, 90.0, 4326);
            Query query = pm.newQuery(SampleGeometry.class, "id > 3000 && id < 4000 && Spatial.bboxTest(geom, :point)");
            List list = (List) query.execute(point);
            assertEquals("Wrong number of geometries which pass the bbox test with a given point returned", 1, list.size());
            assertTrue("Polygon 2 should be in the list of geometries which pass the bbox test with a given point", list
                    .contains(getSamplePolygon(2)));
        }
        finally
        {
            tx.commit();
        }
    }

    private SampleGeometry getSamplePoint(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SampleGeometry(1000, "Point 0", null);
            case 1 :
                return new SampleGeometry(1001, "Point 1", JGeometry.createPoint(new double[]{10.0, 10.0}, 2, 4326));
            case 2 :
                return new SampleGeometry(1002, "Point 2", JGeometry.createPoint(new double[]{75.0, 75.0}, 2, 4326));
        }
        return null;
    }

    private SampleGeometry getSampleLineString(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SampleGeometry(2000, "LineString 0", null);
            case 1 :
                return new SampleGeometry(2001, "LineString 1", JGeometry.createLinearLineString(new double[]{0.0, 50.0, 100.0, 50.0}, 2,
                    4326));
            case 2 :
                return new SampleGeometry(2002, "LineString 2", JGeometry.createLinearLineString(new double[]{50.0, 0.0, 50.0, 100.0}, 2,
                    4326));
            case 3 :
                return new SampleGeometry(2003, "LineString 3", JGeometry.createLinearLineString(new double[]{100.0, 25.0, 120.0, 25.0,
                        110.0, 10.0, 110.0, 45.0}, 2, 4326));
        }
        return null;
    }

    private SampleGeometry getSamplePolygon(int num) throws SQLException
    {
        switch (num)
        {
            case 0 :
                return new SampleGeometry(3000, "Polygon 0", null);
            case 1 :
                return new SampleGeometry(3001, "Polygon 1", JGeometry.createLinearPolygon(new Object[]{
                        new double[]{25.0, 25.0, 75.0, 25.0, 75.0, 75.0, 25.0, 75.0, 25.0, 25.0},
                        new double[]{45.0, 45.0, 45.0, 55.0, 55.0, 55.0, 55.0, 45.0, 45.0, 45.0}}, 2, 4326));
            case 2 :
                return new SampleGeometry(3002, "Polygon 2", JGeometry.createLinearPolygon(new Object[]{new double[]{75.0, 75.0, 100.0,
                        75.0, 100.0, 100.0, 75.0, 75.0}}, 2, 4326));
        }
        return null;
    }

    private SampleGeometry getSampleGeometryCollection(int num) throws SQLException
    {
        int gtype = JGeometry.GTYPE_COLLECTION;
        int srid = 4326;
        int[] elemInfo;
        double[] ordinates;

        switch (num)
        {
            case 0 :
                return new SampleGeometry(7100, "Collection 0", null);
            case 1 :
                elemInfo = new int[]{1, 1, 1, 3, 2, 1, 7, 1003, 1};
                ordinates = new double[]{10.0, 10.0, 0.0, 50.0, 100.0, 50.0, 25.0, 25.0, 75.0, 25.0, 75.0, 75.0, 25.0, 75.0, 25.0, 25.0};
                return new SampleGeometry(7001, "Collection 1", new JGeometry(gtype, srid, elemInfo, ordinates));
            case 2 :
                elemInfo = new int[]{1, 1, 1, 3, 2, 1, 7, 1003, 1};
                ordinates = new double[]{75.0, 75.0, 50.0, 0.0, 50.0, 100.0, 75.0, 75.0, 100.0, 75.0, 100.0, 100.0, 75.0, 75.0};
                return new SampleGeometry(7002, "Collection 2", new JGeometry(gtype, srid, elemInfo, ordinates));
            case 3 :
                elemInfo = new int[]{1, 2, 1};
                ordinates = new double[]{100.0, 25.0, 120.0, 25.0, 110.0, 10.0, 110.0, 45.0};
                return new SampleGeometry(7003, "Collection 3", new JGeometry(gtype, srid, elemInfo, ordinates));
        }
        return null;
    }
}