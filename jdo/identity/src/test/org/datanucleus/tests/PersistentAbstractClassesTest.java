/**********************************************************************
 Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.spi.PersistenceCapable;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.persistentabstracts.ACity;
import org.jpox.samples.persistentabstracts.ALocation;
import org.jpox.samples.persistentabstracts.Country;

/**
 * Series of tests for persistent abstract classes.
 * @version $Revision: 1.1 $
 */
public class PersistentAbstractClassesTest extends JDOPersistenceTestCase
{
    public PersistentAbstractClassesTest(String name)
    {
        super(name);
    }

    /**
     * test making persistent an instance returned from newInstance()
     */
    public void testMakePersistent() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create a new ALocation representing the city of Paris
            ALocation paris = (ALocation) pm.newInstance(ALocation.class);
            paris.setPosition(1001);
            assertEquals(1001, paris.getPosition());

            Object id = null;
            try
            {
                // Persist the object
                tx.begin();
                pm.makePersistent(paris);
                tx.commit();
                id = pm.getObjectId(paris);
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
                // Retrieve the object
                tx.begin();
                paris = (ALocation)pm.getObjectById(id);
                assertEquals(1001, paris.getPosition());
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
        finally
        {
            // Clean out our data
            clean(ALocation.class);
        }
    }

    /**
     * test query
     */
    public void testQuery() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create new ALocations representing the cities of Paris, Rome
            ALocation paris = (ALocation) pm.newInstance(ALocation.class);
            paris.setPosition(1001);
            assertEquals(1001, paris.getPosition());

            ALocation rome = (ALocation) pm.newInstance(ALocation.class);
            rome.setPosition(2003);
            assertEquals(2003, rome.getPosition());

            try
            {
                tx.begin();
                pm.makePersistent(paris);
                pm.makePersistent(rome);
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

                // Find all locations
                Query q = pm.newQuery(ALocation.class);
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                // Find Paris by position
                q = pm.newQuery(ALocation.class, "position == 1001");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                ALocation p = ((ALocation) c.iterator().next());
                assertEquals(1001, p.getPosition());
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
        finally
        {
            clean(ALocation.class);
        }
    }

    /**
     * test query instances on a fresh PMF (no call to newInstance on the fresh PMF)
     */
    /*public void testFreshPMF() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create new ILocations representing the cities of Paris, Rome
            ALocation paris = (ALocation) pm.newInstance(ALocation.class);
            paris.setPosition(1001);
            assertEquals(1001, paris.getPosition());

            ALocation rome = (ALocation) pm.newInstance(ALocation.class);
            rome.setPosition(2003);
            assertEquals(2003, rome.getPosition());

            try
            {
                tx.begin();
                pm.makePersistent(paris);
                pm.makePersistent(rome);
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

            pmf.close();
            getPMF();

            // this test will have to create a new class at runtime because the
            // ClassLoader is released with the pmf
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(ALocation.class);
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(ALocation.class, "position == 1001");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                ALocation p = ((ALocation) c.iterator().next());
                assertEquals(1001, p.getPosition());
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
        finally
        {
            clean(ALocation.class);
        }
    }*/
    
    /**
     * test objects with references to persistent interfaces
     */
    public void testObjectWithReferenceToPersistentInterface() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create object with reference to a persistent interface object
            Country france = new Country("France");
            ACity paris = (ACity) pm.newInstance(ACity.class);
            france.setCapital(paris);
            paris.setPosition(1001);
            paris.setPopulation(2345000);
            assertEquals(1001, paris.getPosition());

            Object id = null;
            try
            {
                tx.begin();
                pm.makePersistent(france);
                tx.commit();
                id = pm.getObjectId(france);
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
                france = (Country) pm.getObjectById(id);
                assertEquals(1001, france.getCapital().getPosition());
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
        finally
        {
            clean(Country.class);
            clean(ACity.class);
        }
    }

    /**
     * test of newInstance()
     * Test basic creation of interfaces
     */
    public void testNewInstance() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        ACity paris = (ACity) pm.newInstance(ACity.class);
        paris.setPosition(1001);
        paris.setName("Paris");
        assertEquals("Paris", paris.getName());
        paris.setCountry(new Country("France"));
    }

    /**
     * test of newInstance()
     * Test basic creation of interfaces with persistent super interfaces
     */
    public void testNewInstance3() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        ALocation lochNess = (ALocation) pm.newInstance(ALocation.class);
        lochNess.setName("Loch Ness");
        assertEquals("Loch Ness", lochNess.getName());
        lochNess.setPosition(100);
        assertEquals(100, lochNess.getPosition());
        assertTrue(PersistenceCapable.class.isAssignableFrom(lochNess.getClass()));
    }
}