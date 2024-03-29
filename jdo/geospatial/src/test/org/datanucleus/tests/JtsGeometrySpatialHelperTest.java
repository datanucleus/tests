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

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.types.geospatial.jdo.SpatialHelper;

public class JtsGeometrySpatialHelperTest extends JDOPersistenceTestCase
{
    protected SpatialHelper helper;

    public JtsGeometrySpatialHelperTest(String name)
    {
        super(name);
    }

    boolean runTestsForDatastore()
    {
        return (rdbmsVendorID.equalsIgnoreCase("mysql") || rdbmsVendorID.equalsIgnoreCase("postgresql"));
    }

    protected void setUp() throws Exception
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            helper = new SpatialHelper((RDBMSStoreManager) storeMgr);
        }
        super.setUp();
    }

    public void testIsGeometryColumnBackedField() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        assertTrue(helper.isGeometryColumnBackedField(org.datanucleus.samples.jtsgeometry.SampleGeometry.class, "geom"));
        assertFalse(helper.isGeometryColumnBackedField(org.datanucleus.samples.jtsgeometry.SampleGeometry.class, "name"));
    }

    public void testGetDimensionFromJdoMetadata() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Integer dim2 = Integer.valueOf(2);
        Integer dim3 = Integer.valueOf(3);
        assertEquals(dim2, helper.getDimensionFromJdoMetadata(org.datanucleus.samples.jtsgeometry.SampleGeometry.class, "geom"));
        assertEquals(dim3, helper.getDimensionFromJdoMetadata(org.datanucleus.samples.jtsgeometry.SampleGeometryCollection3D.class, "geom"));
    }

    public void testGetSridFromJdoMetadata() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Integer srid = Integer.valueOf(4326);
        assertEquals(srid, helper.getSridFromJdoMetadata(org.datanucleus.samples.jtsgeometry.SampleGeometry.class, "geom"));
    }
}