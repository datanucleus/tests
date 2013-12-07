/**********************************************************************
 Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.persistentinterfaces.ILocation;

/**
 * Series of tests for persistent interfaces.
 * Note that these are separate from the tests in PersistentInterfaces2Test due to those adding other implementations
 * of the persistent interfaces and so would affect these tests.
 */
public class PersistentInterfaces1Test extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PersistentInterfaces1Test(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]{});
            initialised = true;
        }
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

            // Create a new ILocation representing the city of Paris
            ILocation paris = (ILocation) pm.newInstance(ILocation.class);
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
                paris = (ILocation)pm.getObjectById(id);
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
            clean(ILocation.class);
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

            // Create new ILocations representing the cities of Paris, Rome
            ILocation paris = (ILocation) pm.newInstance(ILocation.class);
            paris.setPosition(1001);
            assertEquals(1001, paris.getPosition());

            ILocation rome = (ILocation) pm.newInstance(ILocation.class);
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
                Query q = pm.newQuery(ILocation.class);
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                // Find Paris by position
                q = pm.newQuery(ILocation.class, "position == 1001");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                ILocation p = ((ILocation) c.iterator().next());
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
            clean(ILocation.class);
        }
    }

    /**
     * test query instances on a fresh PMF (no call to newInstance on the fresh PMF)
     */
    public void testFreshPMF() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create new ILocations representing the cities of Paris, Rome
            ILocation paris = (ILocation) pm.newInstance(ILocation.class);
            paris.setPosition(1001);
            assertEquals(1001, paris.getPosition());

            ILocation rome = (ILocation) pm.newInstance(ILocation.class);
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

            // Release ClassLoader, so it creates new class at runtime
            // Note this will never work on embedded datastores
//            pmf.close();
//            getPMF();

            // this test will have to create a new class at runtime because the
            // ClassLoader is released with the pmf
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(ILocation.class);
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(ILocation.class, "position == 1001");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                ILocation p = ((ILocation) c.iterator().next());
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
            clean(ILocation.class);
        }
    }
}