/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.datanucleus.samples.jtsgeometry.SampleGeometry;
import org.datanucleus.samples.jtsgeometry.SamplePoint;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Some simple tests for spatial using JPA.
 */
public class JtsGeometryMappingTest extends JPAPersistenceTestCase
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
        }
        super.tearDown();
    }

    boolean runTestsForDatastore()
    {
        return (vendorID.equalsIgnoreCase("mysql") || vendorID.equalsIgnoreCase("postgresql"));
    }

    public void testNoUserDataMapping() throws ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setSRID(-1);
        point.setUserData(new Object());
        SampleGeometry sampleGeometry = null;
        SampleGeometry sampleGeometry_read = null;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            sampleGeometry = new SampleGeometry(10001, "NoUserData", point);
            em.persist(sampleGeometry);
            tx.commit();
        }
        catch (PersistenceException pe)
        {
            LOG.error("Exception on persist", pe);
            fail("Persist failed : " + pe.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }

        em = emf.createEntityManager();
        emf.getCache().evictAll(); // Make sure not cached
        tx = em.getTransaction();
        try
        {
            tx.begin();
            sampleGeometry_read = (SampleGeometry) em.find(SampleGeometry.class, new Long(10001));
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
            em.close();
        }
    }

    public void testUserDataMappingWithObject() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setSRID(-1);
        Object userData = new Object();
        point.setUserData(userData);
        SamplePoint samplePoint;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(11001, "UserDataWithObject", point);
            em.persist(samplePoint);
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
            em.close();
        }
    }

    public void testUserDataMappingWithString() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setSRID(-1);
        Object userData = "UserDataString";
        point.setUserData(userData);
        SamplePoint samplePoint;
        SamplePoint samplePoint_read;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(12001, "UserDataWithString", point);
            em.persist(samplePoint);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try
        {
            tx.begin();
            samplePoint_read = (SamplePoint) em.find(SamplePoint.class, new Long(12001));
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
            em.close();
        }
    }
    public void testGeometryMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setSRID(-1);
        SampleGeometry sampleGeometry;
        SampleGeometry sampleGeometry_read;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            sampleGeometry = new SampleGeometry(1001, "Geometry (Point)", point);
            em.persist(sampleGeometry);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try
        {
            tx.begin();
            sampleGeometry_read = (SampleGeometry) em.find(SampleGeometry.class, new Long(1001));
            assertEquals(sampleGeometry, sampleGeometry_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }
    
    public void testPointMapping() throws SQLException, ParseException
    {
        if (!runTestsForDatastore())
        {
            return;
        }

        Point point = (Point) wktReader.read("POINT(10 10)");
        point.setSRID(-1);
        SamplePoint samplePoint;
        SamplePoint samplePoint_read;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            samplePoint = new SamplePoint(1001, "Point 1", point);
            em.persist(samplePoint);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try
        {
            tx.begin();
            samplePoint_read = (SamplePoint) em.find(SamplePoint.class, new Long(1001));
            assertEquals(samplePoint, samplePoint_read);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
    }
}