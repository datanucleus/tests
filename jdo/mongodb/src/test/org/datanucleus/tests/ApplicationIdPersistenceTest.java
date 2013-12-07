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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

import org.datanucleus.tests.applicationid.ClassWithUniqueField;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.PhoneNumber;
import org.jpox.samples.models.company.Project;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.types.enums.Colour;
import org.jpox.samples.types.enums.Palette;

import com.mongodb.DB;

/**
 * Application identity persistence tests for MongoDB datastores.
 */
public class ApplicationIdPersistenceTest extends JDOPersistenceTestCase
{
    Object id;

    public ApplicationIdPersistenceTest(String name)
    {
        super(name);
    }

    public void testInsert() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person();
                p.setPersonNum(1);
                p.setGlobalNum("1");
                p.setFirstName("Bugs");
                p.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("My");
                p2.setLastName("Friend");

                pm.makePersistent(p);
                pm.makePersistent(p2);

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
        }
        finally
        {
            clean(Person.class);
        }
    }

    public void testInsertSerialised() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Qualification q = new Qualification("MSc");
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.YEAR, 2010);
                cal.set(Calendar.MONTH, 5);
                cal.set(Calendar.DAY_OF_MONTH, 10);
                q.setDate(cal.getTime());

                pm.makePersistent(q);

                tx.commit();
                id = pm.getObjectId(q);
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

            // Retrieve and check it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Qualification q = (Qualification) pm.getObjectById(id);
                Date d = q.getDate();
                assertNotNull("Date is null!", d);
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(d);
                assertEquals("Year is wrong", 2010, cal.get(Calendar.YEAR));
                assertEquals("Month is wrong", 5, cal.get(Calendar.MONTH));
                assertEquals("Day is wrong", 10, cal.get(Calendar.DAY_OF_MONTH));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during retrieve", e);
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
            clean(Qualification.class);
        }
    }

    public void testAccessJdoConnection() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person();
                p.setPersonNum(1);
                p.setGlobalNum("1");
                p.setFirstName("Bugs");
                p.setLastName("Bunny");

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("My");
                p2.setLastName("Friend");

                pm.makePersistent(p);
                pm.makePersistent(p2);

                JDOConnection conn = pm.getDataStoreConnection();
                assertTrue("Native connection should be instanceof com.mongodb.DB!",
                    conn.getNativeConnection() instanceof DB);
                conn.close();

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist with connection access", e);
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

    /**
     * Test of persistence of more than 1 app id objects with the same "id".
     */
    public void testPersistDuplicates()
    {
        try
        {
            // Persist an object with id "101"
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                p1.setGlobalNum("101");
                pm.makePersistent(p1);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 persist", e);
                fail("Exception thrown persisting data " + e.getMessage());
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

                Person p2 = new Person(101, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                p2.setGlobalNum("101");
                pm.makePersistent(p2);

                tx.commit();
                fail("Was allowed to persist two application-identity objects with the same identity");
            }
            catch (Exception e)
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
        finally
        {
            // Do clean up
            clean(Person.class);
        }
    }

    public void testOneToOne() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object p1Id = null;
            Object p2Id = null;
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
                p2.setBestFriend(p1);

                pm.makePersistent(p2);

                tx.commit();
                p1Id = pm.getObjectId(p1);
                p2Id = pm.getObjectId(p2);
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 persist", e);
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
            pmf.getDataStoreCache().evictAll(); // No L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q = pm.newQuery(Person.class);
                List<Person> results = (List<Person>)q.execute();
                assertEquals(2, results.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 retrieve and check", e);
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

                Person p1 = null;
                try
                {
                    p1 = (Person)pm.getObjectById(p1Id);
                    assertEquals("Bugs", p1.getFirstName());
                    assertEquals("Bunny", p1.getLastName());
                    assertEquals(1, p1.getPersonNum());
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    fail("Person p1 not found");
                }

                assertNull("p1 best friend should be null", p1.getBestFriend());

                Person p2 = null;
                try
                {
                    p2 = (Person)pm.getObjectById(p2Id);
                    assertEquals("Daffy", p2.getFirstName());
                    assertEquals("Duck", p2.getLastName());
                    assertEquals(2, p2.getPersonNum());
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    fail("Person p2 not found");
                }

                assertNotNull("p2 best friend should not be null", p2.getBestFriend());
                assertEquals("p2 best friend should be same as p1 object", p1, p2.getBestFriend());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 retrieve and check", e);
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

    public void testOneToMany() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object dept1Id = null;
            try
            {
                tx.begin();
                Department dept1 = new Department("Finance");
                Project prj1 = new Project("Cost Cutting", 150000);
                Project prj2 = new Project("Restructuring", 100000);
                dept1.addProject(prj1);
                dept1.addProject(prj2);
                pm.makePersistent(dept1);

                tx.commit();
                dept1Id = pm.getObjectId(dept1);
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-N persist", e);
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
            pmf.getDataStoreCache().evictAll(); // No L2 cache interference

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check number of objects present
                Query q1 = pm.newQuery(Department.class);
                List<Department> results1 = (List<Department>)q1.execute();
                assertEquals(1, results1.size());
                Query q2 = pm.newQuery(Project.class);
                List<Project> results2 = (List<Project>)q2.execute();
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Department dept1 = null;
                try
                {
                    dept1 = (Department)pm.getObjectById(dept1Id);
                    assertEquals("Finance", dept1.getName());
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    fail("Department dept1 not found");
                }

                assertNotNull("Department projects should not be null", dept1.getProjects());
                Set<Project> projects = dept1.getProjects();
                assertEquals("Number of projects is incorrect", 2, projects.size());
                Iterator<Project> iter = projects.iterator();
                Project prj1 = iter.next();
                Project prj2 = iter.next();
                if (prj1.getName().equals("Cost Cutting"))
                {
                    assertEquals("Budget of Cost Cutting incorrect", 150000, prj1.getBudget());
                    assertEquals("Second project unexpected", "Restructuring", prj2.getName());
                    assertEquals("Second project budget incorrect", 100000, prj2.getBudget());
                }
                else if (prj1.getName().equals("Restructuring"))
                {
                    assertEquals("Budget of Restructring incorrect", 100000, prj1.getBudget());
                    assertEquals("Second project unexpected", "Cost Cutting", prj2.getName());
                    assertEquals("Second project budget incorrect", 150000, prj2.getBudget());
                }
                else
                {
                    fail("First project unexpected");
                }

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
            clean(Department.class);
            clean(Project.class);
        }
    }
    public void testOneToManyMapStrings() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object p1Id = null;
            Object p2Id = null;
            try
            {
                tx.begin();
                Person p1 = new Person();
                p1.setPersonNum(1);
                p1.setGlobalNum("1");
                p1.setFirstName("Bugs");
                p1.setLastName("Bunny");
                Map<String, PhoneNumber> phones = p1.getPhoneNumbers();
                PhoneNumber number1 = new PhoneNumber("Home Number", "98706 123454");
                number1.setId(101);
                PhoneNumber number2 = new PhoneNumber("Mobile", "98605 436578");
                number2.setId(102);
                phones.put("John", number1);
                phones.put("Sally", number2);

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("My");
                p2.setLastName("Friend");

                pm.makePersistent(p1);
                pm.makePersistent(p2);

                tx.commit();
                p1Id = pm.getObjectId(p1);
                p2Id = pm.getObjectId(p2);
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 persist", e);
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

                Person p1 = (Person)pm.getObjectById(p1Id);
                assertNotNull("P1 not found", p1);
                Map phones1 = p1.getPhoneNumbers();
                assertNotNull("P2 phone numbers null!", phones1);
                assertEquals("Number of phone numbers is wrong", 2, phones1.size());
                assertTrue("John number not found", phones1.containsKey("John"));
                PhoneNumber number1 = (PhoneNumber) phones1.get("John");
                assertEquals("John number incorrect", "98706 123454", number1.getNumber());
                assertTrue("Sally number not found", phones1.containsKey("Sally"));
                PhoneNumber number2 = (PhoneNumber) phones1.get("Sally");
                assertEquals("Sally number incorrect", "98605 436578", number2.getNumber());

                Person p2 = (Person)pm.getObjectById(p2Id);
                assertNotNull("P2 not found", p2);
                assertNotNull("P2 phone numbers null!", p2.getPhoneNumbers());
                assertEquals("P2 phone numbers incorrect size!", 0, p2.getPhoneNumbers().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during 1-1 persist", e);
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
            clean(PhoneNumber.class);
        }
    }

    /**
     * Test of persistence/retrieve of objects with surrogate version.
     */
    public void testSurrogateVersion()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();

                Organisation org1 = new Organisation("First");
                org1.setDescription("Original Description");
                pm.makePersistent(org1);

                tx.commit();
                id = JDOHelper.getObjectId(org1);
                assertEquals("Incorrect version after persist", new Long(1), JDOHelper.getVersion(org1));
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting data", e);
                fail("Exception thrown persisting data " + e.getMessage());
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

                Organisation org1 = (Organisation)pm.getObjectById(id);
                assertEquals("Incorrect version after getObjectById", new Long(1), JDOHelper.getVersion(org1));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception retrieving data", e);
                fail("Exception thrown persisting data " + e.getMessage());
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

                Query q = pm.newQuery(Organisation.class);
                List<Organisation> results = (List<Organisation>)q.execute();
                assertNotNull("No results from query!", results);
                assertEquals("Incorrect number of Organisation objects!", 1, results.size());
                Organisation org1 = results.iterator().next();
                assertEquals("Incorrect version after query", new Long(1), JDOHelper.getVersion(org1));

                // Provoke an update
                org1.setDescription("New Description");

                tx.commit();
                assertEquals("Incorrect version after update", new Long(2), JDOHelper.getVersion(org1));
            }
            catch (Exception e)
            {
                LOG.error("Exception retrieving data", e);
                fail("Exception thrown persisting data " + e.getMessage());
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

                Organisation org1 = (Organisation)pm.getObjectById(id);
                assertEquals("Incorrect version after getObjectById", new Long(2), JDOHelper.getVersion(org1));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception retrieving data", e);
                fail("Exception thrown persisting data " + e.getMessage());
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
            // Do clean up
            clean(Organisation.class);
        }
    }

    /**
     * Test optimistic checking of surrogate version.
     */
    public void testOptimisticVersionChecks() throws Exception
    {
        try
        {
            Object id = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Organisation o = new Organisation("DataNucleus");
                o.setDescription("The company behind this software");

                pm.makePersistent(o);

                tx.commit();
                id = pm.getObjectId(o);
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

            PersistenceManager pm1 = pmf.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            tx1.begin();
            Organisation o1 = (Organisation)pm1.getObjectById(id);

            PersistenceManager pm2 = pmf.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            tx2.begin();
            Organisation o2 = (Organisation)pm2.getObjectById(id);

            // Update o1 in tx1 and commit it
            try
            {
                o1.setDescription("Global dataservices company");
                tx1.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during retrieve/update in tx1", e);
                fail("Exception thrown when running test " + e.getMessage());
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Update o2 in tx2 and (try to) commit it
            try
            {
                o2.setDescription("Global dataservices company number 2");
                tx2.commit();
                fail("Should have thrown JDOOptimisticVerificationException!");
            }
            catch (Exception e)
            {
                if (e instanceof JDOOptimisticVerificationException)
                {
                    // Expected
                }
                else
                {
                    LOG.error("Incorrect exception during update in tx2", e);
                    fail("Incorrect exception thrown when running test " + e.getMessage());
                }
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            clean(Organisation.class);
        }
    }

    /**
     * Test persistence of an enum as a String and as ordinal.
     */
    public void testEnum()
    {
        Palette p;
        Object id = null;

        try
        {
            // ---------------------
            // RED
            // ---------------------
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = new Palette();
                p.setAmount(100);
                p.setColour(Colour.RED);
                p.setColourOrdinal(Colour.RED);
                pm.makePersistent(p);
                id = JDOHelper.getObjectId(p);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(100, p.getAmount());
                assertEquals(Colour.RED, p.getColour());
                assertEquals(Colour.RED, p.getColourOrdinal());
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

            // ---------------------
            // null
            // ---------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = new Palette();
                p.setAmount(101);
                p.setColour(null);
                p.setColourOrdinal(null);
                pm.makePersistent(p);
                id = JDOHelper.getObjectId(p);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(101, p.getAmount());
                assertNull(p.getColour());
                assertNull(p.getColourOrdinal());
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
            // ---------------------
            // GREEN
            // ---------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = new Palette();
                p.setAmount(102);
                p.setColour(Colour.GREEN);
                p.setColourOrdinal(Colour.GREEN);
                pm.makePersistent(p);
                id = JDOHelper.getObjectId(p);
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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(102, p.getAmount());
                assertEquals(Colour.GREEN, p.getColour());
                assertEquals(Colour.GREEN, p.getColourOrdinal());
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
        finally
        {
            clean(Palette.class);
        }
    }

    /**
     * Test persistence of Date field (using StringConverter).
     */
    public void testDate()
    {
        Object id = null;
        Object id2 = null;

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p = new Person();
                p.setPersonNum(1);
                p.setGlobalNum("1");
                p.setFirstName("Bugs");
                p.setLastName("Bunny");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, 2011);
                cal.set(Calendar.MONTH, 4);
                cal.set(Calendar.DAY_OF_MONTH, 15);
                p.setBirthDate(cal.getTime());
                pm.makePersistent(p);

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("My");
                p2.setLastName("Friend");
                pm.makePersistent(p2);

                tx.commit();
                id = pm.getObjectId(p);
                id2 = pm.getObjectId(p2);
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
                Person p = (Person) pm.getObjectById(id, true);
                assertNotNull("Date is null!", p.getBirthDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(p.getBirthDate());
                assertEquals("Year is wrong", 2011, cal.get(Calendar.YEAR));
                assertEquals("Month is wrong", 4, cal.get(Calendar.MONTH));
                assertEquals("Day is wrong", 15, cal.get(Calendar.DAY_OF_MONTH));

                Person p2 = (Person) pm.getObjectById(id2, true);
                assertNull("Date is not null!", p2.getBirthDate());
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
        finally
        {
            clean(Person.class);
        }
    }
    /**
     * Test persistence of inheritance, and persistence of float.
     */
    public void testInheritanceAndFloat()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e = new Employee();
                e.setPersonNum(1);
                e.setGlobalNum("1");
                e.setFirstName("Bugs");
                e.setLastName("Bunny");
                e.setSalary(123);
                pm.makePersistent(e);

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
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(Employee.class);
                List<Employee> employees = (List<Employee>) q.execute();
                assertNotNull(employees);
                assertEquals("Number of employees is wrong", 1, employees.size());
                Employee e = employees.get(0);
                assertEquals("Employee name is wrong", "Bugs", e.getFirstName());
                assertEquals("Employee name is wrong", "Bunny", e.getLastName());
                assertEquals("Employee salary is wrong", 123, e.getSalary(), 0.1);

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
        finally
        {
            clean(Employee.class);
        }
    }

    public void testUniqueConstraint() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ClassWithUniqueField u1 = new ClassWithUniqueField(1, "First Name", "Value");
                pm.makePersistent(u1);

                ClassWithUniqueField u2 = new ClassWithUniqueField(2, "Second Name", "Value");
                pm.makePersistent(u2);

                tx.commit();
                fail("Allowed to insert dupd objects when unique constraint exists");
            }
            catch (JDOException e)
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
        finally
        {
            clean(ClassWithUniqueField.class);
        }
    }
}
