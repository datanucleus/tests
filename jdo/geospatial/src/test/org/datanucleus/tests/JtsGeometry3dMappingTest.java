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

import org.datanucleus.samples.jtsgeometry.SampleGeometryCollection3D;
import org.datanucleus.samples.jtsgeometry.SampleGeometryCollectionM;
import org.datanucleus.tests.JDOPersistenceTestCase;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;

/**
 * Series of JTS 3D geometry tests.
 * Run for Mysql/Postgresql only currently.
 *
 * @version $Revision: 1.2 $
 */
public class JtsGeometry3dMappingTest extends JDOPersistenceTestCase
{
    private static final GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(), -1);

    public JtsGeometry3dMappingTest(String name)
    {
        super(name);
    }

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SampleGeometryCollection3D.class);
            clean(SampleGeometryCollectionM.class);
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("mysql") || vendorID.equalsIgnoreCase("postgresql"));
    }

    public void testGeometryCollection3DMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        SampleGeometryCollection3D sampleGeometryCollection;
        SampleGeometryCollection3D sampleGeometryCollection_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            Point point = geomFactory.createPoint(new Coordinate(10.0, 10.0, 100.0));
            LineString linestring = geomFactory.createLineString(new Coordinate[]{new Coordinate(0.0, 50.0, 100.0),
                    new Coordinate(100.0, 50.0, 100.0)});
            LinearRing extRing = geomFactory.createLinearRing(new Coordinate[]{new Coordinate(25.0, 25.0, 100),
                    new Coordinate(75.0, 25.0, 100.0), new Coordinate(75.0, 75.0, 100.0), new Coordinate(25.0, 75.0, 100.0),
                    new Coordinate(25.0, 25.0, 100)});
            Polygon polygon = geomFactory.createPolygon(extRing, null);
            GeometryCollection collection = geomFactory.createGeometryCollection(new Geometry[]{point, linestring, polygon});
            sampleGeometryCollection = new SampleGeometryCollection3D(7101, "Collection of 3-dimensional geometries", collection);
            pm.makePersistent(sampleGeometryCollection);
            id = JDOHelper.getObjectId(sampleGeometryCollection);
            sampleGeometryCollection = (SampleGeometryCollection3D) pm.detachCopy(sampleGeometryCollection);
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
            sampleGeometryCollection_read = (SampleGeometryCollection3D) pm.getObjectById(id, true);
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