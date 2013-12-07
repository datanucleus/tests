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

Contributors:
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Developer;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.jpox.samples.types.basic.BasicTypeHolder;

/**
 * Tests for JDOQL subquery operations.
 */
public class JDOQLSubqueryTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLSubqueryTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Employee.class,
                    Person.class,
                    Manager.class,
                    Developer.class,
                });
            initialised = true;
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
            Query q = pm.newQuery(Employee.class, "salary > averageSalary");
            q.declareVariables("double averageSalary");
            Query averageSalaryQuery = pm.newQuery("SELECT avg(salary) FROM " + Employee.class.getName());
            q.addSubquery(averageSalaryQuery, "double averageSalary", null);
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    /**
     * Test a simple subquery using single-string form.
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
            Query q = pm.newQuery("SELECT FROM " + Employee.class.getName() +
                " WHERE salary > (SELECT avg(salary) FROM " + Employee.class.getName() + " e)");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    /**
     * Test a simple subquery using single-string form and a filter in the subquery.
     **/
    public void testSingleStringSubqueryWithFilter()
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

            // Find the Employees earning more than the average salary of people with surname "S2"
            Query q = pm.newQuery("SELECT FROM " + Employee.class.getName() +
                " WHERE salary > (SELECT avg(e.salary) FROM " + Employee.class.getName() + " e WHERE e.lastName == 'S1')");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    /**
     * Test a simple subquery using single-string form and a parameter in the subquery.
     **/
    public void testSingleStringSubqueryWithParameter()
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.ACCESS_PARENTQUERY_IN_SUBQUERY_JOINED))
            {
                // Access of outer query cols not supported by this datastore so dont test it
                LOG.warn("Database doesnt support use of parameters with subqueries so omitting the test");
                return;
            }
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist Employees
            Employee emp1 = new Employee(101, "Fred", "Smith", "fred.smith@company.com", 100f, "10001");
            Employee emp2 = new Employee(102, "John", "Smith", "john.smith@company.com", 80f, "10002");
            Employee emp3 = new Employee(103, "Jim", "Smith", "jim.smith@company.com", 80f, "10003");
            Employee emp4 = new Employee(104, "Geoff", "Jones", "f2.s2@company.com", 200f, "10004");
            pm.makePersistent(emp1);
            pm.makePersistent(emp2);
            pm.makePersistent(emp3);
            pm.makePersistent(emp4);
            pm.flush();

            // Find the Employees earning more than the average salary of people with the same surname
            Query q = pm.newQuery("SELECT FROM " + Employee.class.getName() +
                " WHERE salary > " +
                " (SELECT avg(e.salary) FROM " + Employee.class.getName() + " e " +
                " WHERE e.lastName == this.lastName)");
            // NOTE : HSQL <= 1.8 doesnt seem to support and conditions back to the outer query
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    /**
     * Test a simple subquery using API form and a parameter in the subquery.
     **/
    public void testAPISubqueryWithParameter()
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.ACCESS_PARENTQUERY_IN_SUBQUERY_JOINED))
            {
                // Access of outer query cols not supported by this datastore so dont test it
                LOG.warn("Database doesnt support use of parameters with subqueries so omitting the test");
                return;
            }
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist Employees
            Employee emp1 = new Employee(101, "Fred", "Smith", "fred.smith@company.com", 100f, "10001");
            Employee emp2 = new Employee(102, "John", "Smith", "john.smith@company.com", 80f, "10002");
            Employee emp3 = new Employee(103, "Jim", "Smith", "jim.smith@company.com", 80f, "10003");
            Employee emp4 = new Employee(104, "Geoff", "Jones", "f2.s2@company.com", 200f, "10004");
            pm.makePersistent(emp1);
            pm.makePersistent(emp2);
            pm.makePersistent(emp3);
            pm.makePersistent(emp4);
            pm.flush();

            // Find the Employees earning more than the average salary of people with the same surname
            Query subquery = pm.newQuery(Employee.class);
            subquery.setResult("avg(this.salary)");
            subquery.setFilter("this.lastName == :lastNameParam");

            Query q = pm.newQuery(Employee.class, "salary > averageSalaryForFamily");
            q.addSubquery(subquery, "double averageSalaryForFamily", null, "this.lastName");

            // NOTE : HSQL <= 1.8 doesnt seem to support and conditions back to the outer query
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Employees with more than average salary was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    /**
     * Test a subquery using contains().
     **/
    public void testSubqueryContains()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Persist some objects
            Boiler boiler1 = new Boiler("Baxi", "Heatfest");
            Boiler boiler2 = new Boiler("Vaillant", "Superwarm");
            Timer timer1 = new Timer("Seiko", true, null);
            timer1.setBoiler(boiler1);
            boiler1.setTimer(timer1);
            pm.makePersistent(boiler1);
            pm.makePersistent(boiler2);
            pm.flush();

            // Find all Boilers that aren't referenced by Timers
            Query q = pm.newQuery("SELECT FROM " + Boiler.class.getName() +
                " WHERE !" + 
                "(SELECT DISTINCT t.boiler FROM " + Timer.class.getName() + " t WHERE t.boiler != null)" +
                ".contains(this)");
            List results = (List)q.execute();
            assertNotNull("No results from query!", results);
            assertEquals("Number of Boilers that are unreferenced was wrong", 1, results.size());

            // Don't commit the data
            tx.rollback();
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
        }
    }

    public void testSubqueryInResult()
    {
        try
        {
            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                BasicTypeHolder holder1 = new BasicTypeHolder();
                holder1.setLongField(1);
                BasicTypeHolder holder2 = new BasicTypeHolder();
                holder2.setLongField(1);                
                BasicTypeHolder holder3 = new BasicTypeHolder();
                holder3.setLongField(1);

                BasicTypeHolder holder4 = new BasicTypeHolder();
                holder4.setLongField(2);
                BasicTypeHolder holder5 = new BasicTypeHolder();
                holder5.setLongField(2);

                pm.makePersistent(holder1);
                pm.makePersistent(holder2);
                pm.makePersistent(holder3);
                pm.makePersistent(holder4);
                pm.makePersistent(holder5);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting objects", e);
                fail("Exception thrown persisting objects : " + e.getMessage());
                return;
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            //Query using a subquery that returns its value to the result and ordering clauses
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query subquery = pm.newQuery(BasicTypeHolder.class);
                subquery.setResult("count(this)");
                subquery.setFilter("this.longField == :tid");
                Map<String, Object> innerParams = new HashMap<String, Object>();
                innerParams.put("tid", "this.longField");

                Query q = pm.newQuery(BasicTypeHolder.class);
                q.setResult("this.longField, bcnt");
                q.addSubquery(subquery, "long bcnt", null, innerParams);
                q.setOrdering("bcnt desc");
                List<Object[]> results = (List<Object[]>)q.execute();
                assertEquals(5, results.size());

                //check first record
                Object[] o = results.get(0);                
                Long num = (Long)o[0];
                Long cnt = (Long)o[1];
                assertEquals("Expected 1 as the first record because its count should be highest", new Long(1), num);
                assertEquals("Expected 3 as the count for 1", new Long(3), cnt);

                //check last record
                o = results.get(results.size()-1);
                num = (Long)o[0];
                cnt = (Long)o[1];
                assertEquals("Expected 2 as the last record because its count should be lowest", new Long(2), num);
                assertEquals("Expected 2 as the count for 2", new Long(2), cnt);
                 
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown querying subquery in result", e);
                fail("Exception thrown querying subquery in result : " + e.getMessage());
                return;
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
            // Clean out our data
            clean(BasicTypeHolder.class);
        }
    }
}