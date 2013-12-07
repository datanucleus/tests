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
package org.datanucleus.tests;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;

public class JDOQLTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];

    public JDOQLTest(String name) throws Exception
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
            pm.makePersistent(p);
            Person bugs = p;
            //id[0] = pm.getObjectId(p);

            p = new Person();
            p.setPersonNum(5);
            p.setGlobalNum("5");
            p.setFirstName("Ana");
            p.setLastName("Hick");
            pm.makePersistent(p);
            //id[1] = pm.getObjectId(p);

            p = new Person();
            p.setPersonNum(3);
            p.setGlobalNum("3");
            p.setFirstName("Lami");
            p.setLastName("Puxa");
            p.setBestFriend(bugs);
            pm.makePersistent(p);
            //id[2] = pm.getObjectId(p);

            Employee e3 = new Employee();
            e3.setFirstName("Barney");
            e3.setLastName("Rubble");
            e3.setPersonNum(103);
            e3.setGlobalNum("103");
            e3.setSalary(124.50f);
            pm.makePersistent(e3);

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
        clean(Employee.class);
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
            Iterator it = pm.getExtent(Person.class, false).iterator();
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
            Collection c = (Collection) pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES").execute();
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
     * Runs basic query with Extent
     */
    public void testBasicQueryWithExtent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(pm.getExtent(Person.class, false)).execute();
            assertEquals(3, c.size());
            c = (Collection) pm.newQuery(pm.getExtent(Person.class, true)).execute();
            assertEquals(4, c.size());
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
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " ORDER BY firstName DESC, lastName");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());
            Iterator it = c.iterator();
            assertEquals("Lami", ((Person)it.next()).getFirstName());
            assertEquals("Bugs", ((Person)it.next()).getFirstName());
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
    public void testResult()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("SELECT count(this) FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES");
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
     * result test
     */
    public void testFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES WHERE firstName == 'Ana'");
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
     * Test query with parameters (NUCCORE-205)
     */
    public void testFilterWithParameters()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            // declare query an run with the first parameter
            pm.currentTransaction().begin();
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " WHERE firstName == s1 PARAMETERS java.lang.String s1");
            Collection c = (Collection) q.execute("Ana");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());
            pm.currentTransaction().commit();

            // declare same query an run with another parameter
            pm.currentTransaction().begin();
            q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " WHERE firstName == s1 PARAMETERS java.lang.String s1");
            c = (Collection) q.execute("xyz");
            assertEquals(0, c.size());
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
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("SELECT count(this), firstName FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " GROUP BY firstName ORDER BY firstName");
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
     * Tests for JDOQL String methods (startsWith, endsWith) specification.
     */
    public void testStringMethods()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " WHERE firstName.startsWith('B')");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.startsWith() has wrong size", 1, c.size());

            q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " WHERE lastName.endsWith('ck')");
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
            fail("Exception thrown querying data to DB4O " + e.getMessage());
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
     * Query returning an object with relation fields, testing the contents of the relation fields.
     */
    public void testRetrieve()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES" +
                " WHERE firstName == 'Lami'");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            Iterator it = c.iterator();
            Person lami = (Person)it.next();
            assertEquals("FirstName is incorrect", "Lami", lami.getFirstName());
            assertNotNull("Value of field storing relation is null but shouldnt be", lami.getBestFriend());
            assertEquals("FirstName of related object is incorrect", "Bugs", lami.getBestFriend().getFirstName());

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

    public void testCandidateWithSubclasses() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q1 = pm.newQuery(pm.getExtent(Person.class, true));
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(4, results1.size());
                Iterator<Person> iter = results1.iterator();
                int numEmployees = 0;
                int numPeople = 0;
                while (iter.hasNext())
                {
                    Person p = iter.next();
                    if (p instanceof Employee)
                    {
                        numEmployees++;
                    }
                    else
                    {
                        numPeople++;
                    }
                }
                assertEquals("Number of Employees wrong", 1, numEmployees);
                assertEquals("Number of Person wrong", 3, numPeople);

                Query q2 = pm.newQuery(pm.getExtent(Person.class, false));
                List<Person> results2 = (List<Person>)q2.execute();
                assertEquals(3, results2.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-N retrieve and check", e);
                fail("Exception thrown when running test " + e.getMessage());
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
        }
    }
}