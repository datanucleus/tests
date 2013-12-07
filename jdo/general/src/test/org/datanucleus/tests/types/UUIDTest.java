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
package org.datanucleus.tests.types;

import java.util.UUID;
import java.util.Collection;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.uuid.UUIDHolder;

/**
 * Test for mapping a UUID field to the datastore
 */
public class UUIDTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public UUIDTest(String name)
    {
        super(name);
    }

    /**
     * Tests for persistence and retreival of a random UUID.
     */
    public void testRandomUuid()
    {
        UUIDHolder u;
        Object id = null;
        final UUID testUuid = UUID.randomUUID();

        // ---------------------
        // Random UUID
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            u = new UUIDHolder();
            u.setUuid(testUuid);
            pm.makePersistent(u);
            tx.commit();
            id = JDOHelper.getObjectId(u);
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
            u = (UUIDHolder) pm.getObjectById(id, true);
            assertEquals(testUuid, u.getUuid());
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
        

    /**
     * Tests for persistence and retreival of a null UUID reference.
     */
    public void testNullUuid()
    {
        UUIDHolder u;
        Object id = null;
        
        // ---------------------
        // null
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            u = new UUIDHolder();
            u.setUuid(null);
            pm.makePersistent(u);
            tx.commit();
            id = JDOHelper.getObjectId(u);
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
            u = (UUIDHolder) pm.getObjectById(id, true);
            assertNull(u.getUuid());
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

    /**
     * Test the persistence of a collection of UUID container classes, followed
     * by searching, checking for the correct result counts.
     */
    public void testQueryUuid()
    {
        UUIDHolder u[];
        Object id[];
        final UUID testUuid1 = UUID.randomUUID();
        final UUID testUuid2 = UUID.randomUUID();
        
        // ---------------------
        // Random UUID
        // ---------------------
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        u = new UUIDHolder[5];
        id = new Object[5];
        try
        {
            tx.begin();
            u[0] = new UUIDHolder();
            u[0].setUuid(testUuid1);
            u[1] = new UUIDHolder();
            u[1].setUuid(testUuid2);
            u[2] = new UUIDHolder();
            u[2].setUuid(testUuid1);
            u[3] = new UUIDHolder();
            u[3].setUuid(testUuid2);
            u[4] = new UUIDHolder();
            u[4].setUuid(testUuid1);
            pm.makePersistentAll(u);
            tx.commit();
            id[0] = JDOHelper.getObjectId(u[0]);
            id[1] = JDOHelper.getObjectId(u[1]);
            id[2] = JDOHelper.getObjectId(u[2]);
            id[3] = JDOHelper.getObjectId(u[3]);
            id[4] = JDOHelper.getObjectId(u[4]);
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
            Collection c = (Collection) pm.newQuery(UUIDHolder.class, "uuid == '" + testUuid1 + "'").execute();
            assertEquals(3, c.size());
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
            Collection c = (Collection) pm.newQuery(UUIDHolder.class, "uuid == '" + testUuid2 + "'").execute();
            assertEquals(2, c.size());
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

    protected void tearDown() throws Exception
    {
        super.tearDown();
        clean(UUIDHolder.class);
    }
}