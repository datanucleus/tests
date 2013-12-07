/**********************************************************************
Copyright (c) 2010 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.mixed;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class MixedTest extends JDOPersistenceTestCase
{
    public MixedTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(User.class);
    }

    protected void tearDown() throws Exception
    {
        clean(User.class);
        super.tearDown();
    }

    public void testFetchCountries()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // countries are there, added with example.ldif
            Country us = pm.getObjectById(Country.class, "US");
            assertNotNull(us);
            assertEquals("US", us.getName());
            assertNotNull(us.getUsers());
            assertEquals(0, us.getUsers().size());

            Country fr = pm.getObjectById(Country.class, "FR");
            assertNotNull(fr);
            assertEquals("FR", fr.getName());
            assertNotNull(fr.getUsers());
            assertEquals(0, fr.getUsers().size());

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

    public void testOperations()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            Country us = pm.getObjectById(Country.class, "US");
            Country fr = pm.getObjectById(Country.class, "FR");
            User daffy = new User("Daffy", "Duck", "Daffy Duck", us);
            pm.makePersistent(daffy);
            User speedy = new User("Speedy", "Gonzales", "Speedy Gonzales", fr);
            pm.makePersistent(speedy);
            tx.commit();
            pm.close();

            // getObjectById
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();

            daffy = pm.getObjectById(User.class, "Daffy Duck");
            assertNotNull(daffy);
            assertEquals("Daffy Duck", daffy.getFullName());
            assertEquals("Daffy", daffy.getFirstName());
            assertEquals("Duck", daffy.getLastName());
            assertNotNull(daffy.getCountry());
            assertEquals(us, daffy.getCountry());

            speedy = pm.getObjectById(User.class, "Speedy Gonzales");
            assertNotNull(speedy);
            assertEquals("Speedy Gonzales", speedy.getFullName());
            assertEquals("Speedy", speedy.getFirstName());
            assertEquals("Gonzales", speedy.getLastName());
            assertNotNull(speedy.getCountry());
            assertEquals(fr, speedy.getCountry());

            tx.commit();
            pm.close();

            // TODO: must fix query and extent
            // // query
            // Collection c = (Collection) pm.newQuery(User.class).execute();
            // assertEquals(2, c.size());
            //
            // // extent
            // Extent<User> extent = pm.getExtent(User.class);
            // assertTrue(extent.iterator().hasNext());
            // assertTrue(extent.iterator().hasNext());
            // assertFalse(extent.iterator().hasNext());

            // delete
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffy = pm.getObjectById(User.class, "Daffy Duck");
            speedy = pm.getObjectById(User.class, "Speedy Gonzales");
            pm.deletePersistent(daffy);
            pm.deletePersistent(speedy);
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
