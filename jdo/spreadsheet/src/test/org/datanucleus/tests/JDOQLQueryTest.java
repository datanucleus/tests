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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;

public class JDOQLQueryTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];
    public JDOQLQueryTest(String name)
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
            id[0] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(5);
            p.setGlobalNum("5");
            p.setFirstName("Ana");
            p.setLastName("Hick");
            pm.makePersistent(p);
            id[1] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(3);
            p.setGlobalNum("3");
            p.setFirstName("Lami");
            p.setLastName("Puxa");
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
            q.setResult("count(this), min(personNum), max(personNum), avg(personNum)");
            Object[] aggs = (Object[])q.execute();
            assertEquals("Count value was wrong", 3, ((Long)aggs[0]).longValue());
            assertEquals("Min value was wrong", 3, ((Long)aggs[1]).longValue());
            assertEquals("Max value was wrong", 5, ((Long)aggs[2]).longValue());
            assertEquals("Avg value was wrong", 4.0, ((Double)aggs[3]).doubleValue());
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
     * Tests for JDOQL String methods (startsWith, endsWith) specification.
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
     * Test of parameter and filter.
     */
    public void testFilterParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LOG.info(">>>> FilterParameterExplicit");
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

    /**
     * Test of illegal parameter specifications.
     */
    public void testParameterInvalid()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("firstName == param1");
            q.declareParameters("java.lang.String param1, java.lang.String param1");
            q.execute("Ana");
            fail("Should have thrown exception due to invalid specification of parameters but didnt!");
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            // Expected
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
     * Test of implicit parameter.
     */
    public void testImplicitParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            q.setFilter("personNum == :param1");
            Map params = new HashMap();
            params.put("param1", 4);
            Collection c = (Collection)q.executeWithMap(params);
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

    /**
     * Test a subquery using the JDOQL Query API.
     **/
    public void testAPISubquery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist 2 Employees
            Employee emp1 = new Employee(101, "F1", "S1", "f1.s1@company.com", 100f, "10001");
            Employee emp2 = new Employee(102, "F2", "S2", "f2.s2@company.com", 200f, "10002");
            pm.makePersistent(emp1);
            pm.makePersistent(emp2);
            pm.flush();

            // Find the Employees earning more than the average salary
            LOG.info(">> Querying for Employees with salary above the average, and the average is defined by subquery");
            Query q = pm.newQuery(Employee.class, "salary > averageSalary");
            q.declareVariables("double averageSalary");
            Query averageSalaryQuery = pm.newQuery("SELECT avg(salary) FROM " + Employee.class.getName());
            q.addSubquery(averageSalaryQuery, "double averageSalary", null);
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            tx.commit();
        }
        catch (JDOUserException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Employee.class);
        }
    }

    /**
     * Test a subquery using the JDOQL Query as single-string.
     **/
    public void testSingleStringSubquery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist 2 Employees
            Employee emp1 = new Employee(101, "F1", "S1", "f1.s1@company.com", 100f, "10001");
            Employee emp2 = new Employee(102, "F2", "S2", "f2.s2@company.com", 200f, "10002");
            pm.makePersistent(emp1);
            pm.makePersistent(emp2);
            pm.flush();

            // Find the Employees earning more than the average salary
            Query q = pm.newQuery("JDOQL", "SELECT FROM " + Employee.class.getName() + 
                " WHERE salary > (SELECT avg(salary) FROM " + Employee.class.getName() + ")");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.compile();
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            tx.commit();
        }
        catch (JDOUserException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Employee.class);
        }
    }

    /**
     * Test use of a cast operator.
     **/
    public void testCast()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist 2 Employees
            Employee emp1 = new Employee(101, "F1", "S1", "f1.s1@company.com", 100f, "10001");
            Employee emp2 = new Employee(102, "F2", "S2", "f2.s2@company.com", 200f, "10002");
            pm.makePersistent(emp1);
            pm.makePersistent(emp2);
            pm.flush();

            // Find the Employee with the specified serial number
            LOG.info(">> Querying for cast Employee serial number");
            Query q = pm.newQuery(Person.class, "((Employee)this).serialNo == \"10001\"");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with serial number was incorrect", 1, results.size());

            tx.commit();
        }
        catch (JDOUserException e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Employee.class);
        }
    }
}