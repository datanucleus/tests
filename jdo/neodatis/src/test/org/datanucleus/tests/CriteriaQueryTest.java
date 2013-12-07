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
import org.neodatis.odb.core.query.criteria.Where;
import org.neodatis.odb.impl.core.query.criteria.CriteriaQuery;

/**
 * Tests for criteria queries with NeoDatis.
 **/
public class CriteriaQueryTest extends JDOPersistenceTestCase
{
    public CriteriaQueryTest(String name)
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
     * Test using a where clause on a subclass.
     */
    public void testSubclassWhereClause()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            // Find all employees older than 31
            Query q = pm.newQuery("Criteria", new CriteriaQuery(ASub1.class, Where.ge("revision", 32)));
            Object res = q.execute();
            assertNotNull("Results from Criteria Query with no filter returned null!", res);
            assertTrue("Result set from Criteria Query is of incorrect type", res instanceof List);
            List results = (List)res;
            assertEquals("Number of results from Criteria Query is incorrect", 1, results.size());
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
     * Test using a where clause on a superclass.
     */
    public void testSuperclassWhereClause()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            // Find all people older than 31
            CriteriaQuery query = new CriteriaQuery(ABase.class, Where.ge("revision", 32));
            query.setPolymorphic(true);
            Query q = pm.newQuery("Criteria", query);
            Object res = q.execute();
            assertNotNull("Results from Criteria Query with no filter returned null!", res);
            assertTrue("Result set from Criteria Query is of incorrect type", res instanceof List);
            List results = (List)res;
            assertEquals("Number of results from Criteria Query is incorrect", 2, results.size());
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