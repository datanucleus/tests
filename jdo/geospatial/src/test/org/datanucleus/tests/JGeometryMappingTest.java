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

import oracle.spatial.geometry.JGeometry;

import org.datanucleus.samples.jgeometry.SampleGeometry;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Series of mapping tests for JGeometry.
 * Run for Oracle only currently.
 *
 * @version $Revision: 1.2 $
 */
public class JGeometryMappingTest extends JDOPersistenceTestCase
{
    public JGeometryMappingTest(String name)
    {
        super(name);
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
        return (vendorID.equalsIgnoreCase("oracle"));
    }

    public void testPointMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry point = new JGeometry(10.0, 10.0, 4326);
        SampleGeometry samplePoint;
        SampleGeometry samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SampleGeometry(1001, "Point 1", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SampleGeometry) pm.detachCopy(samplePoint);
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
            samplePoint_read = (SampleGeometry) pm.getObjectById(id, true);
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
    
    public void testLineStringMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry lineString = JGeometry.createLinearLineString(new double[] {0,50,100,50}, 2, 4326);
        SampleGeometry sampleLineString;
        SampleGeometry sampleLineString_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleLineString = new SampleGeometry(2001, "LineString 1", lineString);
            pm.makePersistent(sampleLineString);
            id = JDOHelper.getObjectId(sampleLineString);
            sampleLineString = (SampleGeometry) pm.detachCopy(sampleLineString);
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
            sampleLineString_read = (SampleGeometry) pm.getObjectById(id, true);
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

    public void testPolygonMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry polygon = JGeometry.createLinearPolygon(new Object[] {new double[] {25,25,75,25,75,75,25,75,25,25}, new double[] {45,45,55,45,55,55,45,55,45,45}}, 2, 4326);
        SampleGeometry samplePolygon;
        SampleGeometry samplePolygon_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePolygon = new SampleGeometry(3001, "Polygon 1", polygon);
            pm.makePersistent(samplePolygon);
            id = JDOHelper.getObjectId(samplePolygon);
            samplePolygon = (SampleGeometry) pm.detachCopy(samplePolygon);
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
            samplePolygon_read = (SampleGeometry) pm.getObjectById(id, true);
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

    public void testMultiPointMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry multipoint = JGeometry.createMultiPoint(new Object[] {new double[] {10,10}, new double[] {75,75}}, 2, 4326);
        SampleGeometry sampleMultiPoint;
        SampleGeometry sampleMultiPoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiPoint = new SampleGeometry(4001, "MultiPoint", multipoint);
            pm.makePersistent(sampleMultiPoint);
            id = JDOHelper.getObjectId(sampleMultiPoint);
            sampleMultiPoint = (SampleGeometry) pm.detachCopy(sampleMultiPoint);
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
            sampleMultiPoint_read = (SampleGeometry) pm.getObjectById(id, true);
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

    public void testMultiLineStringMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry multiLineString = JGeometry.createLinearMultiLineString(new Object[] {new double[] {0,50,100,50}, new double[] {50,0,50,100}, new double[] {100,25,120,25,110,10,110,45}}, 2, 4326);
        SampleGeometry sampleMultiLineString;
        SampleGeometry sampleMultiLineString_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiLineString = new SampleGeometry(5001, "MultiLineString", multiLineString);
            pm.makePersistent(sampleMultiLineString);
            id = JDOHelper.getObjectId(sampleMultiLineString);
            sampleMultiLineString = (SampleGeometry) pm.detachCopy(sampleMultiLineString);
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
            sampleMultiLineString_read = (SampleGeometry) pm.getObjectById(id, true);
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

    public void testMultiPolygonMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        int gtype = JGeometry.GTYPE_MULTIPOLYGON;
        int srid = 4326;
        int[] elemInfo = {1,1003,1,11,2003,1,21,1003,1};
        double[] ordinates = {25,25,75,25,75,75,25,75,25,25,45,45,45,55,55,55,55,45,45,45,75,75,100,75,100,100,75,75};
        JGeometry multiPolygon = new JGeometry(gtype, srid, elemInfo, ordinates);
        SampleGeometry sampleMultiPolygon;
        SampleGeometry sampleMultiPolygon_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleMultiPolygon = new SampleGeometry(6001, "MultiPolygon", multiPolygon);
            pm.makePersistent(sampleMultiPolygon);
            id = JDOHelper.getObjectId(sampleMultiPolygon);
            sampleMultiPolygon = (SampleGeometry) pm.detachCopy(sampleMultiPolygon);
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
            sampleMultiPolygon_read = (SampleGeometry) pm.getObjectById(id, true);
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

    public void testGeometryCollectionMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        int gtype = JGeometry.GTYPE_COLLECTION;
        int srid = 4326;
//        Polygons with holes don't seem to work in GeometryCollections!
//        int[] elemInfo = {1,1,1,3,2,1,7,1003,1,17,2003,1};
//        double[] ordinates = {10,10,0,50,100,50,25,25,75,25,75,75,25,75,25,25,45,45,45,55,55,55,55,45,45,45};
        int[] elemInfo = {1,1,1,3,2,1,7,1003,1};
        double[] ordinates = {10,10,0,50,100,50,25,25,75,25,75,75,25,75,25,25};
        JGeometry geometryCollection = new JGeometry(gtype, srid, elemInfo, ordinates);
        SampleGeometry sampleGeometryCollection;
        SampleGeometry sampleGeometryCollection_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometryCollection = new SampleGeometry(7001, "Collection 1", geometryCollection);
            pm.makePersistent(sampleGeometryCollection);
            id = JDOHelper.getObjectId(sampleGeometryCollection);
            sampleGeometryCollection = (SampleGeometry) pm.detachCopy(sampleGeometryCollection);
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
            sampleGeometryCollection_read = (SampleGeometry) pm.getObjectById(id, true);
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