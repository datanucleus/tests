/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
import javax.jdo.Query;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];

    public JDOQLBasicTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Person.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p = new Person();
            p.setPersonNum(4);
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setEmailAddress("bugs.bunny@example.com");
            p.setAge(34);
            pm.makePersistent(p);
            id[0] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(5);
            p.setFirstName("Ana");
            p.setLastName("Hick");
            p.setEmailAddress("ana.hick@example.com");
            p.setAge(35);
            pm.makePersistent(p);
            id[1] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(3);
            p.setFirstName("Lami");
            p.setLastName("Puxa");
            p.setEmailAddress("lami.puxa@example.com");
            p.setAge(36);
            pm.makePersistent(p);
            id[2] = pm.getObjectId(p);
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    protected void tearDown() throws Exception
    {
        clean(Person.class);
        super.tearDown();
    }

    /**
     * Runs basic query extent
     */
    public void testExtent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Iterator it = pm.getExtent(Person.class).iterator();
            assertTrue(it.hasNext());
            it.next();
            assertTrue(it.hasNext());
            it.next();
            assertTrue(it.hasNext());
            it.next();
            assertFalse(it.hasNext());
            pm.currentTransaction().commit();

        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
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
        try
        {
            pm.currentTransaction().begin();
            Collection c = (Collection) pm.newQuery(Person.class).execute();
            assertEquals(3, c.size());
            pm.currentTransaction().commit();

        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
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
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setOrdering("firstName, lastName");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Bugs", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
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
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setResult("count(this)");
            Long count = (Long) q.execute();
            assertEquals("Count value was wrong", 3, count.longValue());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an simple filter
     */
    public void testFilterWithParameters()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            // declare query and run with the first parameter
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == s1");
            q.declareParameters("java.lang.String s1");
            Collection c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
    
            // declare same query an run with another parameter
            pm.currentTransaction().begin();
            Query q2 = pm.newQuery(Person.class);
            q2 = pm.newQuery(Person.class);
            q2.setFilter("firstName == s1");
            q2.declareParameters("java.lang.String s1");
            Collection c2 = (Collection) q2.execute("xyz");
            assertEquals(0, c2.size());
            pm.currentTransaction().commit();
    
            // same test with JPQL query
            // declare query and run with the first parameter
            pm.currentTransaction().begin();
            Query q3 = pm.newQuery("javax.jdo.query.JPQL",
                "SELECT p FROM org.datanucleus.samples.directory.Person p WHERE p.firstName = :name");
            Collection c3 = (Collection) q3.execute("Ana");
            assertEquals(1, c3.size());
            it = c3.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // declare same query an run with another parameter
            pm.currentTransaction().begin();
            Query q4 = pm.newQuery("javax.jdo.query.JPQL",
                "SELECT p FROM org.datanucleus.samples.directory.Person p WHERE p.firstName = :name");
            Collection c4 = (Collection) q4.execute("xyz");
            assertEquals(0, c4.size());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an simple filter
     */
    public void testEqualsFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'Ana'");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'ana'");
            c = (Collection) q.execute();
            assertEquals(0, c.size());
            pm.currentTransaction().commit();

            // same test with parameter expression
            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.declareParameters("java.lang.String name");
            q.setFilter("firstName == name");
            c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with single-string query
            pm.currentTransaction().begin();
            q = pm
                    .newQuery("SELECT FROM org.datanucleus.samples.directory.Person WHERE firstName == name PARAMETERS java.lang.String name");
            c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with JPQL query
            pm.currentTransaction().begin();
            q = pm.newQuery("javax.jdo.query.JPQL", "SELECT p FROM org.datanucleus.samples.directory.Person p WHERE p.firstName = :name");
            c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test and AND filter
     */
    public void testAndFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'Ana' && lastName == 'Hick'");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            pm.currentTransaction().begin();
            Query q2 = pm.newQuery(Person.class);
            q2.setFilter("firstName == 'Ana' && lastName == 'Hick' && age == 35");
            Collection c2 = (Collection) q2.execute();
            assertEquals(1, c2.size());
            Iterator it2 = c2.iterator();
            assertEquals("Ana", ((Person) it2.next()).getFirstName());
            pm.currentTransaction().commit();

            //same test with parameter expression
            pm.currentTransaction().begin();
            Query q3 = pm.newQuery(Person.class);
            q3.declareParameters("java.lang.String p1, java.lang.String p2, int p3");
            q3.setFilter("firstName == p1 && lastName == p2 && age == p3");
            Collection c3 = (Collection) q3.execute("Ana", "Hick", Integer.valueOf(35) );
            assertEquals(1, c3.size());
            Iterator it3 = c3.iterator();
            assertEquals("Ana", ((Person) it3.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with JPQL query
            pm.currentTransaction().begin();
            Query q4 = pm.newQuery("javax.jdo.query.JPQL",
                "SELECT p FROM org.datanucleus.samples.directory.Person p WHERE p.firstName = :p1 AND p.lastName = :p2 and p.age = :p3");
            Collection c4 = (Collection) q4.execute("Ana", "Hick", Integer.valueOf(35));
            assertEquals(1, c4.size());
            Iterator it4 = c4.iterator();
            assertEquals("Ana", ((Person) it4.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an OR filter
     */
    public void testOrFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'Ana' || firstName == 'Lami'");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an NOT filter
     */
    public void testNotEqualsFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName != 'Bugs'");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with parameter expression
            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName != p1");
            c = (Collection) q.execute("Bugs");
            assertEquals(2, c.size());
            it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an Less filter
     */
    public void testLessFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("age < 35");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an Less Or Equal filter
     */
    public void testLessOrEqualFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("age <= 35");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an Greater filter
     */
    public void testGreaterFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("age > 35");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an Lesser Or Equal filter
     */
    public void testGreaterOrEqualFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("age >= 35");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an startsWith
     */
    public void testStartsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName.startsWith('B')");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with parameter expression
            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.startsWith(p1)");
            c = (Collection) q.execute("B");
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an endsWith
     */
    public void testEndsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName.endsWith('a')");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // same test with parameter expression
            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.endsWith(p1)");
            c = (Collection) q.execute("a");
            assertEquals(1, c.size());
            it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * Test for null values, both
     */
    public void testNullFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            // remove email of 'Bugs Bunny'
            pm.currentTransaction().begin();
            Person p = (Person) pm.getObjectById(id[0]);
            p.setEmailAddress(null);
            pm.currentTransaction().commit();

            // check not null
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("emailAddress != null");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            assertEquals("Lami", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // check null
            pm.currentTransaction().begin();
            q = pm.newQuery(Person.class);
            q.setFilter("emailAddress == null");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it2 = c.iterator();
            assertEquals("Bugs", ((Person) it2.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the in-memory filter extension. LDAP filters don't support arithmetic operations so this must be evaluated
     * in-memory.
     */
    public void testInMemoryFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("1 + 1 == 2");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    /**
     * test an OR filter
     */
    public void testOrInjection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == 'Ana' || firstName == 'Lami)(objectClass=*'");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
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
        try
        {
            pm.currentTransaction().begin();
            Query q = pm.newQuery(Person.class);
            q.setResult("count(this), firstName");
            q.setGrouping("firstName");
            q.setOrdering("firstName");
            Collection c = (Collection) q.execute();
            Iterator it = c.iterator();
            Object[] obj = (Object[]) it.next();
            assertEquals(1, ((Long) obj[0]).longValue());
            assertEquals("Ana", obj[1].toString());
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

}
