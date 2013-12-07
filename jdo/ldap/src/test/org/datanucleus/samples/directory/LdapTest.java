/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
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
package org.datanucleus.samples.directory;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class LdapTest extends JDOPersistenceTestCase
{
    public LdapTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Singleton.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Singleton.class);
        super.tearDown();
    }

    public void testSingleton()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Singleton s = new Singleton();
            s.setName("Singleton");
            s = pm.makePersistent(s);
            Object id = pm.getObjectId(s);
            tx.commit();
            pm.close();

            // fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Singleton s2 = (Singleton) pm.getObjectById(id);
            assertEquals("Singleton", s2.getName());
            assertNull(s2.getDescription());
            tx.commit();
            pm.close();

            // update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Singleton s3 = (Singleton) pm.getObjectById(id);
            s3.setDescription("This is a description");
            tx.commit();
            pm.close();

            // fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Singleton s4 = (Singleton) pm.getObjectById(id);
            assertEquals("Singleton", s4.getName());
            assertEquals("This is a description", s4.getDescription());
            tx.commit();
            pm.close();

            // extent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Iterator<Singleton> it = pm.getExtent(Singleton.class).iterator();
            assertTrue(it.hasNext());
            it.next();
            assertFalse(it.hasNext());
            tx.commit();
            pm.close();

            // delete
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Singleton s5 = (Singleton) pm.getObjectById(id);
            pm.deletePersistent(s5);
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

    public void testFetchSingleton()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Singleton s = new Singleton();
            s.setName("Singleton");
            s = pm.makePersistent(s);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Singleton s2 = pm.getObjectById(Singleton.class, "[OID]" + Singleton.class.getName());
            assertEquals("Singleton", s2.getName());
            assertNull(s2.getDescription());
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

    public void testQuerySingleton()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Singleton s = new Singleton();
            s.setName("Singleton");
            s = pm.makePersistent(s);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Collection<Singleton> c = (Collection<Singleton>) pm.newQuery(Singleton.class).execute();
            assertEquals(1, c.size());
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