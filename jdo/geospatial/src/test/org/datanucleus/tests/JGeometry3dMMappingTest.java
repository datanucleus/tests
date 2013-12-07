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
import org.datanucleus.samples.jgeometry.SampleGeometry3D;
import org.datanucleus.samples.jgeometry.SampleGeometryM;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Series of 3D geometry tests for JGeometry.
 * Run for Oracle only currently.
 *
 * @version $Revision: 1.2 $
 */
public class JGeometry3dMMappingTest extends JDOPersistenceTestCase
{
    public JGeometry3dMMappingTest(String name)
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

    public void testPointMMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        int gtype = 3301; // point with measure in third dimension
        int srid = 4326;
        int[] elemInfo = {1,1,1};
        double[] ordinates = {10.0, 10.0, 100.0};
        JGeometry point = new JGeometry(gtype, srid, elemInfo, ordinates);
        SampleGeometry3D samplePoint;
        SampleGeometry3D samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SampleGeometry3D(1101, "Point with measure", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SampleGeometry3D) pm.detachCopy(samplePoint);
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
            samplePoint_read = (SampleGeometry3D) pm.getObjectById(id, true);
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

    public void testPoint3DMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        JGeometry point = new JGeometry(10.0, 10.0, 100.0, 4326);
        SampleGeometryM samplePoint;
        SampleGeometryM samplePoint_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            samplePoint = new SampleGeometryM(1201, "Point 3D", point);
            pm.makePersistent(samplePoint);
            id = JDOHelper.getObjectId(samplePoint);
            samplePoint = (SampleGeometryM) pm.detachCopy(samplePoint);
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
            samplePoint_read = (SampleGeometryM) pm.getObjectById(id, true);
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
}