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
package org.datanucleus.tests.knownbugs;

import org.datanucleus.tests.*;
import java.sql.SQLException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.pggeometry.SampleBox;
import org.datanucleus.samples.pggeometry.SampleGeometryCollection3D;
import org.datanucleus.samples.pggeometry.SampleGeometryCollectionM;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.postgis.GeometryCollection;
import org.postgis.PGbox2d;
import org.postgis.PGbox3d;
import org.postgis.Point;

/**
 * Series of mapping tests for Postgis using Postgresql extensions.
 *
 * @version $Revision: 1.2 $
 */
public class PgGeometryPostGISExtMappingTest extends JDOPersistenceTestCase
{
    public PgGeometryPostGISExtMappingTest(String name)
    {
        super(name);
    }

    protected void tearDown() throws Exception
    {
        if (runTestsForDatastore())
        {
            clean(SampleBox.class);
            clean(SampleGeometryCollection3D.class);
            clean(SampleGeometryCollectionM.class);
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("postgresql"));
    }

    // TODO this test should be running successfully after using 
    // 2.1.3 or alter postgis jdbc jars from OSSRH rep
    // when they are available.
    public void testGeometryCollectionMMapping() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        SampleGeometryCollectionM sampleGeometryCollection;
        SampleGeometryCollectionM sampleGeometryCollection_read;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            sampleGeometryCollection = new SampleGeometryCollectionM(
                    7201,
                    "Collection of geometries with measure",
                    new GeometryCollection(
                            "SRID=-1;GEOMETRYCOLLECTIONM(POINTM(10 10 100),LINESTRINGM(0 50 100, 100 50 100),POLYGONM((25 25 100,75 25 100,75 75 100,25 75 100,25 25 100),(45 45 100,55 45 100,55 55 100,45 55 100,45 45 100)))"));
            pm.makePersistent(sampleGeometryCollection);
            id = JDOHelper.getObjectId(sampleGeometryCollection);
            sampleGeometryCollection = (SampleGeometryCollectionM) pm.detachCopy(sampleGeometryCollection);
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
            sampleGeometryCollection_read = (SampleGeometryCollectionM) pm.getObjectById(id, true);
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