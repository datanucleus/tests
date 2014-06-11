/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.inheritance.ABase;
import org.jpox.samples.inheritance.ASub1;

import org.neodatis.odb.OdbConfiguration;
import org.neodatis.odb.core.query.nq.NativeQuery;

/**
 * Tests for native queries with NeoDatis.
 **/
public class NativeQueryTest extends JDOPersistenceTestCase
{
    public NativeQueryTest(String name)
    {
        super(name);
        OdbConfiguration.setLogServerStartupAndShutdown(false);
    }

    /**
     * Create a couple of Employee objects to query.
     * @throws Exception
     */
    protected void setUp() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ASub1 sub1 = new ASub1();
            sub1.setName("First");
            sub1.setRevision(31);
            ASub1 sub2 = new ASub1();
            sub2.setName("Second");
            sub2.setRevision(32);
            ABase base3 = new ABase();
            base3.setName("Third");
            base3.setRevision(33);
            pm.makePersistent(sub1);
            pm.makePersistent(sub2);
            pm.makePersistent(base3);
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
     * Clean out the objects.
     * @throws Exception
     */
    protected void tearDown() throws Exception
    {
        clean(ASub1.class);
        clean(ABase.class);
    }

    /**
     * Test using a predicate on a subclass condition.
     */
    public void testSubclassPredicate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            // Find all with revision more than 31
            NativeQuery nq = new NativeQuery()
            {
                private static final long serialVersionUID = -8713726438022753379L;
                public boolean match(Object e)
                {
                    return ((ASub1)e).getRevision() >= 32;
                }
                public Class getObjectType()
                {
                    return ASub1.class;
                }
            };
            Query q = pm.newQuery("Native", nq);
            Object res = q.execute();
            assertNotNull("Results from Native Query with no filter returned null!", res);
            assertTrue("Result set from Native Query is of incorrect type", res instanceof List);
            List results = (List)res;
            assertEquals("Number of results from Native Query is incorrect", 1, results.size());
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown querying data " + e.getMessage());
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
     * Test using a predicate on a superclass condition.
     */
    public void testSuperclassPredicate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            // Find all with revision more than 31
            NativeQuery nq = new NativeQuery()
            {
                private static final long serialVersionUID = -1369609081363122187L;
                public boolean match(Object e)
                {
                    return ((ABase)e).getRevision() >= 32;
                }
                public Class getObjectType()
                {
                    return ABase.class;
                }
            }; 
            nq.setPolymorphic(true); // Allow ASub1 objects
            Query q = pm.newQuery("Native", nq);
            Object res = q.execute();
            assertNotNull("Results from Native Query with no filter returned null!", res);
            assertTrue("Result set from Native Query is of incorrect type", res instanceof List);
            List results = (List)res;
            assertEquals("Number of results from Native Query is incorrect", 2, results.size());
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown querying data " + e.getMessage());
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