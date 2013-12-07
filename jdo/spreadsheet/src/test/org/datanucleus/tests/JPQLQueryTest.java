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

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Person;

public class JPQLQueryTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];
    public JPQLQueryTest(String name)
    {
        super(name);
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
            p.setEmailAddress(null);
            pm.makePersistent(p);
            id[0] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(5);
            p.setGlobalNum("5");
            p.setFirstName("Ana");
            p.setLastName("Hick");
            p.setEmailAddress("ana.hick@nowhere.com");
            pm.makePersistent(p);
            id[1] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(3);
            p.setGlobalNum("3");
            p.setFirstName("Lami");
            p.setLastName("Puxa");
            p.setEmailAddress("lami.puxi@nowhere.com");
            pm.makePersistent(p);
            id[2] = pm.getObjectId(p);
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
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.deletePersistent(pm.getObjectById(id[0]));
            pm.deletePersistent(pm.getObjectById(id[1]));
            pm.deletePersistent(pm.getObjectById(id[2]));
            tx.commit();
        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
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
            Collection c = (Collection) pm.newQuery("JPQL", 
                "SELECT p FROM " + Person.class.getName() + " p").execute();
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
     * result test
     */
    public void testFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE firstName = 'Ana'");
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
     * result test
     */
    public void testFilterBetween()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE personNum BETWEEN 4 AND 5");
            Collection c = (Collection) q.execute();
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

    /**
     * result test
     */
    public void testFilterLike()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE firstName LIKE 'A.*a'");
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
     * ordering
     */
    public void testOrdering()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL", 
                "SELECT p FROM " + Person.class.getName() + " p ORDER BY firstName, lastName");
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
     * Test using "IS NULL" and "IS NOT NULL" keywords.
     */
    public void testFilterIsNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE emailAddress IS NULL");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person)it.next()).getFirstName());

            q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE emailAddress IS NOT NULL " + 
                "ORDER BY firstName");
            c = (Collection) q.execute();
            assertEquals(2, c.size());
            it = c.iterator();
            Person p1 = (Person)it.next();
            Person p2 = (Person)it.next();
            assertEquals("First person when using IS NOT NULL is incorrect", "Ana", p1.getFirstName());
            assertEquals("Second person when using IS NOT NULL is incorrect", "Lami", p2.getFirstName());
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
     * Test using "IS EMPTY" keywords.
     */
    public void testFilterIsEmpty()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE phoneNumbers IS EMPTY");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());

            q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE phoneNumbers IS NOT EMPTY");
            c = (Collection) q.execute();
            assertEquals(0, c.size());
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
     * Test using "IN (literal, literal)" and "NOT IN (literal, literal)".
     */
    public void testFilterInLiteral()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE firstName IN ('Ana', 'Alan')");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person)it.next()).getFirstName());

            q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE firstName NOT IN ('Ana', 'Bugs')");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Lami", ((Person)it.next()).getFirstName());
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
     * Test using UPPER(str) function.
     */
    public void testFilterUPPER()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE UPPER(firstName) = 'ANA'");
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
     * Test use of implicit parameter (under JDO).
     */
    public void testImplicitParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p WHERE firstName = :param1");
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

    /**
     * Test use of from clause.
     */
    public void testFrom()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LOG.info(">> 2 candidate classes");
            Query q = pm.newQuery("JPQL",
                "SELECT p FROM " + Person.class.getName() + " p, IN(p.phoneNumbers) n");
            q.execute();
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