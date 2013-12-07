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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;

/**
 * Tests for in-memory evaluation of JDOQL queries.
 */
public class JDOQLInMemoryTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];
    public JDOQLInMemoryTest(String name)
    {
        super(name);
    }

    public String getQueryLanguage()
    {
        return "JDOQL";
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
     * Runs basic query
     */
    public void testBasicQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Collection c = (Collection)q.execute();
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.setResult("COUNT(this), min(personNum), max(personNum), avg(personNum)");
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
    public void testResultWithParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT :param, firstName FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            List<Object[]> results = (List<Object[]>)q.executeWithArray("FirstParam");
            assertEquals(3, results.size());
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object[] result = (Object[])iter.next();
                assertEquals("Number of results columns is incorrect", 2, result.length);
                assertEquals("FirstParam", result[0]);
            }
            q.closeAll();
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
     * result class with result test
     */
    public void testResultClassWithResult()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT COUNT(this) AS TOTAL INTO java.util.HashMap FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Object result = q.execute();
            assertTrue("Result is not a HashMap", result instanceof HashMap);
            Map resultMap = (Map)result;
            assertTrue("TOTAL is not contained in results map", resultMap.containsKey("TOTAL"));
            Object totalValue = resultMap.get("TOTAL");
            assertTrue("TOTAL value is not a Long, is " + totalValue.getClass().getName(), totalValue instanceof Long);
            assertEquals("Count value was wrong", 3, ((Long)totalValue).longValue());
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

        // Test for COUNT() where there are no results - should return 0
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT COUNT(firstName) INTO " + Long.class.getName() +
                " FROM " + Person.class.getName() + " WHERE firstName == 'Rubbish'");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Long result = (Long) q.execute();
            assertNotNull("Result of COUNT() for no data was null!", result);
            assertEquals("Result of COUNT() for no data was incorrect", 0, result.intValue());
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
     * filter test
     */
    public void testFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
     * Test for JDOQL String startsWith method.
     */
    public void testStringStartsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName()  + " WHERE firstName.startsWith('B')");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.startsWith() has wrong size", 1, c.size());

            // same test with parameter expression
            q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.startsWith(p1)");
            c = (Collection) q.execute("B");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());

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
     * Test for JDOQL String endsWith method.
     */
    public void testStringEndsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName() + " WHERE lastName.endsWith('ck')");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.endsWith() has wrong size", 1, c.size());

            // same test with parameter expression
            q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.endsWith(p1)");
            c = (Collection) q.execute("a");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Ana", ((Person) it.next()).getFirstName());

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
     * Test for JDOQL String indexOf method.
     */
    public void testStringIndexOf()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(),
                "SELECT FROM " + Person.class.getName() + " WHERE lastName.indexOf('ck') > 0");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.endsWith() has wrong size", 1, c.size());

            // same test with parameter expression
            q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.indexOf(p1) > 0");
            c = (Collection) q.execute("ug");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());

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
     * Test for JDOQL chained String methods, using toUpperCase().startsWith().
     */
    public void testStringToUpperCaseStartsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName()  + " WHERE firstName.toUpperCase().startsWith('BU')");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Object res = q.execute();
            assertNotNull("Result set from JDOQL query is null!", res);
            assertTrue("Result set from JDOQL query is of incorrect type!", res instanceof List);
            Collection c = (Collection) res;
            assertEquals("Collection from String.startsWith() has wrong size", 1, c.size());

            // same test with parameter expression
            q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareParameters("java.lang.String p1");
            q.setFilter("firstName.toUpperCase().startsWith(p1)");
            c = (Collection) q.execute("BU");
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Bugs", ((Person) it.next()).getFirstName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception during toUpperCase().startsWith() evaluation", e);
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
     * Test for JDOQL chained String methods, using toUpperCase().startsWith() using parameters.
     */
    public void testParamToUpperCaseStartsWith()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName()  + " WHERE :param1.toUpperCase().startsWith(:param2.toUpperCase())");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            Map params = new HashMap();
            params.put("param1", "First");
            params.put("param2", "f");
            List results = (List) q.executeWithMap(params);
            assertEquals(3, results.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception during param.toUpperCase().startsWith() evaluation", e);
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
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
     * Test of implicit parameter.
     */
    public void testImplicitParameterWithPositionalInput()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Person.class.getName());
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.setFilter("personNum == :param1");
            Collection c = (Collection)q.execute(new Integer(4));
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
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Employee.class.getName() + " WHERE salary > averageSalary");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareVariables("double averageSalary");
            Query averageSalaryQuery = pm.newQuery(getQueryLanguage(), "SELECT avg(salary) FROM " + Employee.class.getName());
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
            Query q = pm.newQuery(getQueryLanguage(), "SELECT FROM " + Employee.class.getName() + 
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
     * Test use of instanceof operator.
     **/
    public void testInstanceOf()
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

            // Find all Employee objects
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName() + " WHERE this instanceof Employee");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees was incorrect", 2, results.size());
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Returned object is not an Employee!", obj instanceof Employee);
            }

            tx.commit();
        }
        catch (JDOException e)
        {
            LOG.error("Exception in execution of query", e);
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
            Query q = pm.newQuery(getQueryLanguage(), 
                "SELECT FROM " + Person.class.getName() + 
                " WHERE this instanceof Employee && ((Employee)this).serialNo == \"10001\"");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with serial number was incorrect", 1, results.size());
            Object obj = results.get(0);
            assertTrue("Object is not Employee", obj instanceof Employee);
            Employee emp = (Employee)obj;
            assertEquals("Employee first name is incorrect", "F1", emp.getFirstName());
            assertEquals("Employee last name is incorrect", "S1", emp.getLastName());
            assertEquals("Employee serial number is incorrect", "10001", emp.getSerialNo());

            tx.commit();
        }
        catch (JDOException e)
        {
            LOG.error("Exception in execution of query", e);
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
     * Test of parameter.method().
     */
    public void testParameterMethodInvoke()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(getQueryLanguage(),
                "SELECT FROM " + Person.class.getName() + " WHERE firstName.toUpperCase() == param1.toUpperCase()");
            q.addExtension("datanucleus.query.evaluateInMemory", "true");
            q.declareParameters("java.lang.String param1");
            Collection results = (Collection)q.execute("aNa");
            assertEquals("Number of results is incorrect", 1, results.size());
            Person p = (Person)results.iterator().next();
            assertEquals("First name of result is incorrect", "Ana", p.getFirstName());
            
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in execution of query", e);
            fail(e.getMessage());
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