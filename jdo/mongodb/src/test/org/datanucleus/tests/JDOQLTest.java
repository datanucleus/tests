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
package org.datanucleus.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;

/**
 * Query tests for MongoDB datastores.
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q1 = pm.newQuery(pm.getExtent(Person.class, true));
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

                Query q2 = pm.newQuery(pm.getExtent(Person.class, false));
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

    /**
     * Query with a simple filter with one clause ("field == value")
     * @throws Exception
     */
    public void testFilterSingle() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName == 'Daffy'");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(1, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p = iter.next();
                assertEquals("Daffy", p.getFirstName());
                assertEquals("Duck", p.getLastName());
                assertEquals(2, p.getPersonNum());

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

    /**
     * Query with a simple filter with one clause ("field < value")
     * @throws Exception
     */
    public void testFilterLessThanNumeric() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE personNum < 2");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(1, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p = iter.next();
                assertEquals("Bugs", p.getFirstName());
                assertEquals("Bunny", p.getLastName());
                assertEquals(1, p.getPersonNum());

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

    /**
     * Query with a simple filter with one clause ("field == value && field2 < value2")
     * @throws Exception
     */
    public void testFilterUsingAnd() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName == 'Daffy' && personNum < 3");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(1, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p = iter.next();
                assertEquals("Daffy", p.getFirstName());
                assertEquals("Duck", p.getLastName());
                assertEquals(2, p.getPersonNum());

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

    /**
     * Query with a simple filter with one clause ("field == value || field2 == value2")
     * @throws Exception
     */
    public void testFilterUsingOr() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName == 'Daffy' || personNum < 2");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(2, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p1 = iter.next();
                Person p2 = iter.next();
                boolean daffy = false;
                boolean bugs = false;
                if (p1.getPersonNum() == 2)
                {
                    assertEquals("Daffy", p1.getFirstName());
                    assertEquals("Duck", p1.getLastName());
                    assertEquals(2, p1.getPersonNum());
                    daffy = true;
                }
                else
                {
                    assertEquals("Bugs", p1.getFirstName());
                    assertEquals("Bunny", p1.getLastName());
                    assertEquals(1, p1.getPersonNum());
                    bugs = true;
                }
                if (p2.getPersonNum() == 2)
                {
                    assertEquals("Daffy", p2.getFirstName());
                    assertEquals("Duck", p2.getLastName());
                    assertEquals(2, p2.getPersonNum());
                    daffy = true;
                }
                else
                {
                    assertEquals("Bugs", p2.getFirstName());
                    assertEquals("Bunny", p2.getLastName());
                    assertEquals(1, p2.getPersonNum());
                    bugs = true;
                }
                assertTrue("Bugs not present in results!", bugs);
                assertTrue("Daffy not present in results!", daffy);

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

    /**
     * Query with a simple filter with two ORed clauses 
     * "(field >= value1 && field < value2)" and "(field >= value3 && field < value4)".
     * @throws Exception
     */
    public void testFilterUsingOrNestedAnd() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE (personNum >= 1 && personNum < 2) || (personNum >= 100 && personNum < 200)");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(2, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p1 = iter.next();
                Person p2 = iter.next();
                boolean barney = false;
                boolean bugs = false;
                if (p1.getPersonNum() == 1)
                {
                    assertEquals("Bugs", p1.getFirstName());
                    assertEquals("Bunny", p1.getLastName());
                    assertEquals(1, p1.getPersonNum());
                    bugs = true;
                }
                else
                {
                    assertEquals("Barney", p1.getFirstName());
                    assertEquals("Rubble", p1.getLastName());
                    assertEquals(103, p1.getPersonNum());
                    barney = true;
                }
                if (p2.getPersonNum() == 1)
                {
                    assertEquals("Bugs", p2.getFirstName());
                    assertEquals("Bunny", p2.getLastName());
                    assertEquals(1, p2.getPersonNum());
                    bugs = true;
                }
                else
                {
                    assertEquals("Barney", p2.getFirstName());
                    assertEquals("Rubble", p2.getLastName());
                    assertEquals(103, p2.getPersonNum());
                    barney = true;
                }
                assertTrue("Bugs not present in results!", bugs);
                assertTrue("Barney not present in results!", barney);

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

    /**
     * Query with a simple filter with two ANDed clauses 
     * "(field >= value1 && field < value2)" and "(field >= value3 && field < value4)".
     * @throws Exception
     */
    public void testFilterUsingAndNestedAnd() throws Exception
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
                p1.setAge(15);

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("Daffy");
                p2.setLastName("Duck");
                p2.setAge(16);

                Employee e3 = new Employee();
                e3.setFirstName("Barney");
                e3.setLastName("Rubble");
                e3.setPersonNum(103);
                e3.setGlobalNum("103");
                e3.setSalary(124.50f);
                e3.setAge(18);

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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE (personNum >= 1 && personNum <=2) && (age >= 14 && age < 18)");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(2, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p1 = iter.next();
                Person p2 = iter.next();
                boolean daffy = false;
                boolean bugs = false;
                if (p1.getPersonNum() == 1)
                {
                    assertEquals("Bugs", p1.getFirstName());
                    assertEquals("Bunny", p1.getLastName());
                    assertEquals(1, p1.getPersonNum());
                    bugs = true;
                }
                else
                {
                    assertEquals("Daffy", p1.getFirstName());
                    assertEquals("Duck", p1.getLastName());
                    assertEquals(2, p1.getPersonNum());
                    daffy = true;
                }
                if (p2.getPersonNum() == 1)
                {
                    assertEquals("Bugs", p2.getFirstName());
                    assertEquals("Bunny", p2.getLastName());
                    assertEquals(1, p2.getPersonNum());
                    bugs = true;
                }
                else
                {
                    assertEquals("Daffy", p2.getFirstName());
                    assertEquals("Duck", p2.getLastName());
                    assertEquals(2, p2.getPersonNum());
                    daffy = true;
                }
                assertTrue("Bugs not present in results!", bugs);
                assertTrue("Daffy not present in results!", daffy);

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

    /**
     * Query with a simple filter with a method call (not possible totally in-datastore)
     * @throws Exception
     */
    public void testFilterWithMethod() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName.startsWith('D')");
                List<Person> results1 = (List<Person>)q1.execute();
                assertEquals(1, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p = iter.next();
                assertEquals("Daffy", p.getFirstName());
                assertEquals("Duck", p.getLastName());
                assertEquals(2, p.getPersonNum());

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

    /**
     * Query with a simple filter with one clause ("field == :param")
     * @throws Exception
     */
    public void testFilterWithParameter() throws Exception
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // One param, positional
                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName == :name");
                List<Person> results1 = (List<Person>)q1.execute("Daffy");
                assertEquals(1, results1.size());
                Iterator<Person> iter1 = results1.iterator();
                Person p1 = iter1.next();
                assertEquals("Daffy", p1.getFirstName());
                assertEquals("Duck", p1.getLastName());
                assertEquals(2, p1.getPersonNum());

                // Two params, named
                Query q2 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE firstName == :name && lastName == :surname");
                Map params = new HashMap();
                params.put("name", "Daffy");
                params.put("surname", "Duck");
                List<Person> results2 = (List<Person>)q2.executeWithMap(params);
                assertEquals(1, results2.size());
                Iterator<Person> iter2 = results2.iterator();
                Person p2 = iter2.next();
                assertEquals("Daffy", p2.getFirstName());
                assertEquals("Duck", p2.getLastName());
                assertEquals(2, p2.getPersonNum());

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

    /**
     * Query with a simple filter with one clause ("field == :param")
     * @throws Exception
     */
    public void testFilterWithNullParameter() throws Exception
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
                p2.setLastName(null);

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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() +
                    " WHERE lastName == :name");
                List<Person> results1 = (List<Person>)q1.execute(null);
                assertEquals(1, results1.size());
                Iterator<Person> iter = results1.iterator();
                Person p = iter.next();
                assertEquals("Daffy", p.getFirstName());
                assertNull(p.getLastName());
                assertEquals(2, p.getPersonNum());

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

    /**
     * Query with a range and ordering clause.
     * @throws Exception
     */
    public void testRangeWithOrdering() throws Exception
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

                Person p3 = new Person();
                p3.setPersonNum(3);
                p3.setGlobalNum("3");
                p3.setFirstName("Fred");
                p3.setLastName("Flintstone");

                Person p4 = new Person();
                p4.setPersonNum(4);
                p4.setGlobalNum("4");
                p4.setFirstName("Barney");
                p4.setLastName("Rubble");

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                pm.makePersistent(p4);

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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SELECT FROM " + Person.class.getName() + " ORDER BY firstName RANGE 1,3");
                List<Person> results1 = (List<Person>)q1.execute(null);
                assertEquals(2, results1.size());
                Person p1 = results1.get(0);
                Person p2 = results1.get(1);
                assertEquals("Bugs", p1.getFirstName());
                assertEquals("1", p1.getGlobalNum());
                assertEquals("Daffy", p2.getFirstName());
                assertEquals("2", p2.getGlobalNum());

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
}