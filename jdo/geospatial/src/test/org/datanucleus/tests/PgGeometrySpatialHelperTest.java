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

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.jdo.spatial.SpatialHelper;
import org.datanucleus.tests.JDOPersistenceTestCase;

public class PgGeometrySpatialHelperTest extends JDOPersistenceTestCase
{
    protected SpatialHelper helper;

    public PgGeometrySpatialHelperTest(String name)
    {
        super(name);
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("postgresql") || vendorID.equalsIgnoreCase("mysql"));
    }

    protected void setUp() throws Exception
    {
        helper = new SpatialHelper((JDOPersistenceManagerFactory)pmf);
        super.setUp();
    }

    public void testIsGeometryColumnBackedField() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        assertTrue(helper.isGeometryColumnBackedField(org.datanucleus.samples.pggeometry.SampleGeometry.class, "geom"));
        assertFalse(helper.isGeometryColumnBackedField(org.datanucleus.samples.pggeometry.SampleGeometry.class, "name"));
    }

    public void testGetDimensionFromJdoMetadata() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Integer dim2 = new Integer(2);
        Integer dim3 = new Integer(3);
        assertEquals(dim2, helper.getDimensionFromJdoMetadata(org.datanucleus.samples.pggeometry.SampleGeometry.class, "geom"));
        assertEquals(dim3, helper.getDimensionFromJdoMetadata(org.datanucleus.samples.pggeometry.SampleGeometryCollection3D.class, "geom"));
    }

    public void testGetSridFromJdoMetadata() throws SQLException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Integer srid = new Integer(4326);
        assertEquals(srid, helper.getSridFromJdoMetadata(org.datanucleus.samples.pggeometry.SampleGeometry.class, "geom"));
    }
}