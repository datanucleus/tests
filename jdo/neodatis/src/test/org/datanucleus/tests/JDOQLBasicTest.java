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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Person;
import org.neodatis.odb.OdbConfiguration;

public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];

    public JDOQLBasicTest(String name) throws Exception
    {
        super(name);
        OdbConfiguration.setLogServerStartupAndShutdown(false);
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(4);
            p.setGlobalNum("4");
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setAge(34);
            pm.makePersistent(p);
            //id[0] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(5);
            p.setGlobalNum("5");
            p.setFirstName("Ana");
            p.setLastName("Hick");
            p.setAge(27);
            pm.makePersistent(p);
            //id[1] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(3);
            p.setGlobalNum("3");
            p.setFirstName("Lami");
            p.setLastName("Puxa");
            p.setAge(23);
            pm.makePersistent(p);
            //id[2] = pm.getObjectId(p);
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
        clean(Person.class);
    }

    /**
     * Runs basic query extent
     */
    public void testExtent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Iterator it = pm.getExtent(Person.class).iterator();
            assertTrue(it.hasNext());
            it.next();
            assertTrue(it.hasNext());
            it.next();
            assertTrue(it.hasNext());
            it.next();
            assertFalse(it.hasNext());
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
     * Runs basic query
     */
    public void testBasicQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Person.class).execute();
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
    }
    
    /**
     * ordering
     */
    public void testOrdering()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setOrdering("firstName, lastName");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person)it.next()).getFirstName());
            assertEquals("Bugs", ((Person)it.next()).getFirstName());
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
     * result test
     */
    public void testResult()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setResult("count(this)");
            Long count = (Long) q.execute();
            assertEquals("Count value was wrong", 3, count.longValue());
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
     * Test of equality operator in filter.
     */
    public void testFilterEqualityOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'Ana'");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person)it.next()).getFirstName());
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
     * Test of greater than operator in filter.
     */
    public void testFilterGreaterThanOperator()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("age > 32");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person)it.next()).getFirstName());
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
     * result grouping
     */
    public void testGrouping()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setResult("count(this), firstName");
            q.setGrouping("firstName");
            q.setOrdering("firstName");
            Collection c = (Collection) q.execute();
            Iterator it = c.iterator();
            Object[] obj = (Object[]) it.next();
            assertEquals(1, ((Long)obj[0]).longValue());
            assertEquals("Ana", obj[1].toString());
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
     * Tests for JDOQL String methods specification.
     */
    public void testStringMethods()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class, "firstName.startsWith('B')");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.startsWith() has wrong size", 1, c.size());

            q = pm.newQuery(Person.class, "lastName.endsWith('ck')");
            res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            c = (Collection) res;
            assertEquals("Collection from String.endsWith() has wrong size", 1, c.size());

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
     * Test of parameter and filter.
     */
    public void testFilterParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == param1");
            q.declareParameters("java.lang.String param1");
            Collection c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person)it.next()).getFirstName());
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