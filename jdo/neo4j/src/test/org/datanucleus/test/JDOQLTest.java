/**********************************************************************
 Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.models.company.Employee;
import org.datanucleus.samples.models.company.Person;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Query tests for Neo4j datastores.
 */
public class JDOQLTest extends JDOPersistenceTestCase
{
    Object id;

    public JDOQLTest(String name)
    {
        super(name);
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
                Person p1 = new Person();
                p1.setPersonNum(1);
                p1.setGlobalNum("1");
                p1.setFirstName("Bugs");
                p1.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("Daffy");
                p2.setLastName("Duck");

                Employee e3 = new Employee();
                e3.setFirstName("Barney");
                e3.setLastName("Rubble");
                e3.setPersonNum(103);
                e3.setGlobalNum("103");
                e3.setSalary(124.50f);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(e3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist", e);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q1 = pm.newQuery(Person.class);
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(3, results1.size());
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
                assertEquals("Number of Person wrong", 2, numPeople);

                Query q2 = pm.newQuery("SELECT FROM " + Person.class.getName() + " EXCLUDE SUBCLASSES");
                List<Person> results2 = (List<Person>)q2.execute();
                assertEquals(2, results2.size());

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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testRange() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person();
                p1.setPersonNum(1);
                p1.setGlobalNum("1");
                p1.setFirstName("Bugs");
                p1.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("Daffy");
                p2.setLastName("Duck");

                Employee e3 = new Employee();
                e3.setFirstName("Barney");
                e3.setLastName("Rubble");
                e3.setPersonNum(103);
                e3.setGlobalNum("103");
                e3.setSalary(124.50f);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(e3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist", e);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q2 = pm.newQuery("SELECT FROM " + Person.class.getName() + " ORDER BY this.personNum RANGE 1,3");
                List<Person> results2 = (List<Person>)q2.execute();
                assertEquals(2, results2.size());
                Iterator<Person> iter = results2.iterator();
                boolean daffyPresent = false;
                boolean barneyPresent = false;
                while (iter.hasNext())
                {
                    Person p = iter.next();
                    if (p.getFirstName().equals("Barney"))
                    {
                        barneyPresent = true;
                    }
                    else if (p.getFirstName().equals("Daffy"))
                    {
                        daffyPresent = true;
                    }
                }
                assertTrue(daffyPresent);
                assertTrue(barneyPresent);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during query", e);
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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testResult() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person();
                p1.setPersonNum(1);
                p1.setGlobalNum("1");
                p1.setFirstName("Bugs");
                p1.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("Daffy");
                p2.setLastName("Duck");

                Employee e3 = new Employee();
                e3.setFirstName("Barney");
                e3.setLastName("Rubble");
                e3.setPersonNum(103);
                e3.setGlobalNum("103");
                e3.setSalary(124.50f);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(e3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist", e);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q2 = pm.newQuery("SELECT this.firstName, this.lastName FROM " + Person.class.getName());
                List<Object[]> results2 = (List<Object[]>)q2.execute();
                assertEquals(3, results2.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during query", e);
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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testResultAggregate() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person();
                p1.setPersonNum(1);
                p1.setGlobalNum("1");
                p1.setFirstName("Bugs");
                p1.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("Daffy");
                p2.setLastName("Duck");

                Employee e3 = new Employee();
                e3.setFirstName("Barney");
                e3.setLastName("Rubble");
                e3.setPersonNum(103);
                e3.setGlobalNum("103");
                e3.setSalary(124.50f);

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(e3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist", e);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q2 = pm.newQuery("SELECT MAX(this.personNum), MIN(this.personNum) FROM " + Person.class.getName());
                Object[] results2 = (Object[])q2.execute();
                assertEquals(2, results2.length);
                assertEquals(103l, results2[0]);
                assertEquals(1l, results2[1]);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during query", e);
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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testEmptyOnEmptyResult() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery("SELECT FROM " + Person.class.getName());
                List<Person> results = q.executeList();
                assertTrue(results.isEmpty());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during query", e);
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
            clean(Person.class);
        }
    }

    public void testFilterEquals() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setFirstName("Barney");
                e.setLastName("Rubble");
                e.setPersonNum(103);
                e.setGlobalNum("103");
                e.setSalary(124.50f);

                pm.makePersistent(e);
                pm.flush();

                // Query using equality operator
                Query q1 = pm.newQuery("SELECT FROM " + Employee.class.getName() + " WHERE this.firstName == 'Barney'");
                List<Employee> results1 = (List<Employee>)q1.execute();
                assertEquals(1, results1.size());
                Employee e1 = results1.iterator().next();
                assertEquals("Wrong Employee", "Rubble", e1.getLastName());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during JDOQL equality operator test", e);
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

    public void testFilterNotEquals() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setFirstName("Barney");
                e.setLastName("Rubble");
                e.setPersonNum(103);
                e.setGlobalNum("103");
                e.setSalary(124.50f);

                pm.makePersistent(e);
                pm.flush();

                // Query using not equality operator
                Query q1 = pm.newQuery("SELECT FROM " + Employee.class.getName() + " WHERE this.firstName != 'Fred'");
                List<Employee> results1 = (List<Employee>)q1.execute();
                assertEquals(1, results1.size());
                Employee e1 = results1.iterator().next();
                assertEquals("Wrong Employee", "Rubble", e1.getLastName());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during JDOQL equality operator test", e);
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

    public void testFilterNotNull() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setFirstName("Barney");
                e.setLastName("Rubble");
                e.setPersonNum(103);
                e.setGlobalNum("103");
                e.setSalary(124.50f);

                pm.makePersistent(e);
                pm.flush();

                // Query using not equality operator
                Query q1 = pm.newQuery("SELECT FROM " + Employee.class.getName() + " WHERE this.firstName != null");
                List<Employee> results1 = (List<Employee>)q1.execute();
                assertEquals(1, results1.size());
                Employee e1 = results1.iterator().next();
                assertEquals("Wrong Employee", "Rubble", e1.getLastName());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during JDOQL inequality operator test", e);
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

    public void testStringToUpperCase() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setFirstName("Barney");
                e.setLastName("Rubble");
                e.setPersonNum(103);
                e.setGlobalNum("103");
                e.setSalary(124.50f);

                pm.makePersistent(e);
                pm.flush();

                Query q1 = pm.newQuery("SELECT FROM " + Employee.class.getName() + " WHERE this.firstName.toUpperCase() == 'BARNEY'");
                List<Employee> results1 = (List<Employee>)q1.execute();
                assertEquals(1, results1.size());
                Employee e1 = results1.iterator().next();
                assertEquals("Wrong Employee", "Rubble", e1.getLastName());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during JDOQL String toUpperCase test", e);
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

    public void testMathSin() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setFirstName("Barney");
                e.setLastName("Rubble");
                e.setPersonNum(103);
                e.setGlobalNum("103");
                e.setSalary(124.50f);

                pm.makePersistent(e);
                pm.flush();

                Query q1 = pm.newQuery("SELECT FROM " + Employee.class.getName() + " WHERE Math.sin(:param) == this.salary");
                Map<String, Object> params = new HashMap<>();
                params.put("param", 0.0);
                q1.setNamedParameters(params);
                List<Employee> results1 = (List<Employee>)q1.executeList();
                assertEquals(0, results1.size());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during JDOQL equality operator test", e);
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