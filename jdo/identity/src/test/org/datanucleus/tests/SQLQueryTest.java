/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdentityType;

import org.datanucleus.PropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Developer;
import org.jpox.samples.models.company.DeveloperRC;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.PersonalDetails;
import org.jpox.samples.resultclass.TableSize;

/**
 * Tests the use of SQL queries as specified in JDO2.0 spec section 14.
 */
public class SQLQueryTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests. Normally, programmers
     * would never explicitly use this constructor.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public SQLQueryTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{Person.class, Manager.class, Employee.class, Developer.class});
            initialised = true;
        }
    }

    /**
     * Test of a null query.
     */
    public void testNullQuery()
    throws Exception
    {
        // Try a null query
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query query = pm.newQuery("javax.jdo.query.SQL", null);
            query.execute();
            fail("Should have thrown an exception on a null query, but allowed execution");
        }
        catch (JDOUserException ue)
        {
            // Do nothing, since this is expected
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
     * Test of an invalid query. All queries should start "SELECT".
     */
    public void testInvalidQuery()
    throws Exception
    {
        // Try an invalid query
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String sqlText = "INSERT INTO PERSON VALUES(1,2)";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            query.execute();
            fail("Should have thrown an exception on an invalid (non-SELECT) query, but allowed execution");
        }
        catch (JDOUserException ue)
        {
            // Do nothing, since this is expected
        }
        catch (JDODataStoreException dse)
        {
            // Do nothing, since this is expected
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
     * Test of the use of SELECT * in a query.
     */
    public void testSelectStarQuery()
    throws Exception
    {
        try
        {
            // Persist something to select
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager m1 = new Manager(1, "Barney", "Rubble", "barney.rubble@flintstones.com", 100, "123456");
                pm.makePersistent(m1);
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

            // Do an SQL query to find the Manager
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                String sqlText = "SELECT * FROM MANAGER";
                Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
                query.setClass(Manager.class);
                List results = (List)query.execute();
                assertTrue("\"SELECT *\" query returned null, yet should have returned some results", results != null);
                assertEquals("Number of Manager objects retrieved from \"SELECT *\" query was incorrect", 1, results.size());
                Manager mgr = (Manager)results.iterator().next();

                // These will cause further SQL statements to retrieve the fields in the Person/Employee part of the object
                assertEquals("\"SELECT *\" query returned Manager with incorrect first name", "Barney", mgr.getFirstName());
                assertEquals("\"SELECT *\" query returned Manager with incorrect last name", "Rubble", mgr.getLastName());
                assertEquals("\"SELECT *\" query returned Manager with incorrect email", "barney.rubble@flintstones.com", mgr.getEmailAddress());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown from \"SELECT *\" query : " + e.getMessage());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test of a query with a candidate class, without a result class.
     * @throws Exception Thrown if an error occurs
     */
    public void testWithCandidateClassWithoutResultClass()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            Person p1 = new Person(1, "First", "Person", "first.person@jpox.org");
            pm.makePersistent(p1);

            Person p2 = new Person(2, "Second", "Person", "second.person@jpox.org");
            pm.makePersistent(p2);
            tx.commit();

            // Query for a basic object, including the PK field(s)
            tx = pm.currentTransaction();
            tx.begin();
            Query query = pm.newNamedQuery(Person.class, "PeopleWithEmail");
            List results = (List) query.execute("second.person@jpox.org");
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been Person", obj.getClass().getName().equals(Person.class.getName()));
                Person p = (Person)obj;
                assertTrue("Query with candidate class has returned the wrong Person object.", p.getPersonNum() == 2);
                assertTrue("Query with candidate class has returned the wrong Person object.", p.getFirstName().equals("Second"));
            }
            tx.commit();

            // Create some inherited data to query
            tx = pm.currentTransaction();
            tx.begin();
            Developer p3 = new Developer(10, "James", "Java", "james@java.com", (float)12.00, "1234567", new Integer(1), "jdo");
            pm.makePersistent(p3);
            tx.commit();

            // Query for an inherited object, including the PK field(s)
            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery = pm.newNamedQuery(Developer.class, "DeveloperWithSkill");
            results = (List) inhQuery.execute("jdo");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been Developer", obj.getClass().getName().equals(Developer.class.getName()));
                Developer p = (Developer)obj;
                assertTrue("Query with candidate class has returned the wrong Developer object.", p.getSKILL().equals("jdo"));
            }
            tx.commit();

            // Create some inherited data to query
            tx = pm.currentTransaction();
            tx.begin();
            Developer p4 = new Developer(11, "Paul", "Perl", "paul@perl.com", (float)6.00, "1234568", new Integer(2), "perl");
            p4.setGlobalNum("GUID-p4");
            pm.makePersistent(p4);
            tx.commit();

            // Query for an inherited object, including the PK field(s)
            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery2 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoin");
            results = (List) inhQuery2.execute("perl");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been Developer", obj.getClass().getName().equals(Developer.class.getName()));
                Developer p = (Developer)obj;
                assertTrue("Query with candidate class has returned the wrong Developer object.", p.getSKILL().equals("perl"));
                assertTrue("Query with candidate class has returned the wrong Developer object.", p.getGlobalNum().equals("GUID-p4"));
            }
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Developer.class);
            clean(Person.class);
        }

    }

    /**
     * Test of SQL queries with a candidate class AND a result class.
     */
    public void testWithCandidateClassWithResultClass()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query, and query using a ResultClass that is constructor based
            tx = pm.currentTransaction();
            tx.begin();
            Person pers1 = new Person(234568, "Homer", "Simpson", "homer.simpson@fox.com");
            pers1.setAge(45);
            Person pers2 = new Person(234578, "Marge", "Simpson", "marge.simpson@fox.com");
            pers1.setAge(42);
            pm.makePersistent(pers1);
            pm.makePersistent(pers2);
            tx.commit();

            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                Query amountQuery = pm.newNamedQuery(Person.class, "PersonDetails");
                List results = (List)amountQuery.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertEquals("ResultClass of query is incorrect.", 
                        PersonalDetails.class.getName(), obj.getClass().getName());
                }
                amountQuery.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }
            tx.commit();

            // Create some inherited data to query and query using a ResultClass without constructor
            tx = pm.currentTransaction();
            tx.begin();
            Developer p3 = new Developer(13, "James", "Java", "james@java.com", (float)15.00, "1234569", new Integer(3), "jdo");
            pm.makePersistent(p3);
            tx.commit();

            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery = pm.newNamedQuery(Developer.class, "DeveloperWithSkillForResult");
            List results = (List) inhQuery.execute("jdo");
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been DeveloperRC", obj.getClass().getName().equals(DeveloperRC.class.getName()));
                DeveloperRC p = (DeveloperRC)obj;
                assertTrue("Query with candidate class has returned the wrong Developer object.", p.getSKILL().equals("jdo"));
            }
            tx.commit();

            // Create some inherited data to query and query using INNER JOIN and users class
            tx = pm.currentTransaction();
            tx.begin();
            Developer p4 = new Developer(100, "Mike", "Microsoft", "mike@microsoft.com", 10, "1234570", new Integer(3), ".net");
            p4.setGlobalNum("GUID-p4");
            pm.makePersistent(p4);
            tx.commit();

            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery2 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoinForResult");
            results = (List) inhQuery2.execute(".net");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been DeveloperRC", obj.getClass().getName().equals(DeveloperRC.class.getName()));
                DeveloperRC p = (DeveloperRC)obj;
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", p.getSKILL().equals(".net"));
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", p.getGlobalNum().equals("GUID-p4"));
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, p.personNum);
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 10, (int)p.salary);
            }
            tx.commit();

            // Create some inherited data to query and query using HashMap.
            tx = pm.currentTransaction();
            tx.begin();
            Developer p5 = new Developer();
            p5.setFirstName("John");
            p5.setSKILL("uml");
            p5.setGlobalNum("GUID-p5");
            p5.setSalary(10);
            p5.setPersonNum(100);
            p5.setSerialNo(""+new Random(System.currentTimeMillis()).nextInt());
            pm.makePersistent(p5);
            tx.commit();

            // ResultClass = java.util.HashMap
            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery3 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoinForResultHashMap");
            results = (List) inhQuery3.execute("uml");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been HashMap", obj.getClass().getName().equals(HashMap.class.getName()));
                HashMap p = (HashMap)obj;
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "SKILL").equals("uml"));
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "GLOBALNUM").equals("GUID-p5"));
                Number salary = (Number)getValueForKeyInMapCaseInsensitive(p, "salary");
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 10, salary.intValue());
                Object personNumObj = getValueForKeyInMapCaseInsensitive(p, "PERSONNUM");
                if (personNumObj instanceof Long)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((Long)personNumObj).intValue());
                }
                else if (personNumObj instanceof BigDecimal)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((BigDecimal)personNumObj).intValue());
                }
                else
                {
                    fail("Test doest support keys in map of type " + personNumObj.getClass());
                }
            }
            tx.commit();

            // ResultClass = java.util.Map
            tx = pm.currentTransaction();
            tx.begin();
            Query inhQuery4 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoinForResultMap");
            results = (List) inhQuery4.execute("uml");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been HashMap", obj.getClass().getName().equals(HashMap.class.getName()));
                HashMap p = (HashMap)obj;
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "SKILL").equals("uml"));
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "GLOBALNUM").equals("GUID-p5"));
                Number salary = (Number)getValueForKeyInMapCaseInsensitive(p, "salary");
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 10, salary.intValue());
                Object personNumObj = getValueForKeyInMapCaseInsensitive(p, "PERSONNUM");
                if (personNumObj instanceof Long)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((Long)personNumObj).intValue());
                }
                else if (personNumObj instanceof BigDecimal)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((BigDecimal)personNumObj).intValue());
                }
                else
                {
                    fail("Test doest support keys in map of type " + personNumObj.getClass());
                }
            }
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while querying with candidate class and result class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Developer.class);
            clean(Person.class);
        }
    }

    /**
     * Convenience method to allow for case-insensitive keys.
     * Some RDBMS do case-sensitive things. For example Derby will UPPERCASE the
     * keys of the map when retrieved. Postgresql will often lowercase them depending
     * on the identifier case setting. This method allows for both.
     * @param map The map
     * @param key The key
     * @return The value
     */
    private Object getValueForKeyInMapCaseInsensitive(Map map, String key)
    {
        Object value = map.get(key.toLowerCase());
        if (value == null)
        {
            value = map.get(key.toUpperCase()); // Derby uppercases the keys
        }
        return value;
    }

    /**
     * Basic test of SQL without a candidate class and without a result class.
     */
    public void testWithoutCandidateClassWithoutResultClass() 
    throws Exception
    {
        // Do a simple count(*) query
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert a new element for table Person
            tx.begin();
            Person p1 = new Person(1, "First", "Person", "first.person@jpox.org");
            pm.makePersistent(p1);
            Person p2 = new Person(2, "Second", "Person", "second.person@jpox.org");
            pm.makePersistent(p2);
            tx.commit();

            // Query the count
            tx.begin();
            String sqlText = "SELECT count(*) FROM PERSON";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            List results = (List) query.execute();
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.getClass().isArray())
                {
                    fail("SQL Query selecting count(*) has returned an Object[] yet should have been Object");
                }
                assertTrue("SQL Query selecting count(*) has returned an object of the wrong type : was " + obj.getClass().getName() + 
                    " but should have been Number or subclass", obj instanceof Number);
                Number value = (Number)obj;
                assertEquals("SQL Query selecting count(*) returned the wrong value : was " + value.longValue() + 
                    " but should have been 2", value.longValue(), 2);
            }
            tx.commit();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with no candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test of SQL queries when no candidate class is defined, and a result class is given.
     */
    public void testWithoutCandidateClassWithResultClass()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Add a couple of payments
            tx.begin();
            Person pers1 = new Person(234568, "Homer", "Simpson", "homer.simpson@fox.com");
            pers1.setAge(45);
            Person pers2 = new Person(234578, "Marge", "Simpson", "marge.simpson@fox.com");
            pers1.setAge(42);
            pm.makePersistent(pers1);
            pm.makePersistent(pers2);
            tx.commit();

            // Test using ResultClass that is constructor-based
            tx.begin();
            try
            {
                String sqlText = "SELECT FIRSTNAME, LASTNAME, AGE FROM PERSON";
                Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
                query.setResultClass(PersonalDetails.class);
                List results = (List)query.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    Object obj = resultsIter.next();
                    assertEquals("ResultClass of query is incorrect.",
                        PersonalDetails.class.getName(), obj.getClass().getName());
                }
                query.closeAll();
            }
            catch (JDOUserException e)
            {
                fail(e.getMessage());
            }
            tx.commit();

            tx.begin();
            Developer p3 = new Developer();
            p3.setFirstName("John");
            p3.setSKILL("jdo");
            p3.setGlobalNum("GUID-p3");
            p3.setSerialNo(""+new Random(System.currentTimeMillis()).nextInt());
            pm.makePersistent(p3);

            Query inhQuery = pm.newNamedQuery(Developer.class, "DeveloperWithSkillForResult");
            inhQuery.setClass(null);
            List results = (List) inhQuery.execute("jdo");
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been DeveloperRC", obj.getClass().getName().equals(DeveloperRC.class.getName()));
                DeveloperRC p = (DeveloperRC)obj;
                assertTrue("Query with candidate class has returned the wrong Developer object.", p.getSKILL().equals("jdo"));
            }
            tx.commit();

            tx.begin();
            Developer p4 = new Developer();
            p4.setFirstName("John");
            p4.setSKILL("ejb");
            p4.setGlobalNum("GUID-p4");
            p4.setSalary(10);
            p4.setPersonNum(100);
            p4.setSerialNo(""+new Random(System.currentTimeMillis()).nextInt());
            pm.makePersistent(p4);

            Query inhQuery2 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoinForResult");
            inhQuery2.setClass(null);
            results = (List) inhQuery2.execute("ejb");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been DeveloperRC", obj.getClass().getName().equals(DeveloperRC.class.getName()));
                DeveloperRC p = (DeveloperRC)obj;
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", p.getSKILL().equals("ejb"));
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", p.getGlobalNum().equals("GUID-p4"));
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, p.personNum);
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 10, (int)p.salary);
            }
            tx.commit();

            tx.begin();
            Developer p5 = new Developer();
            p5.setFirstName("John");
            p5.setSKILL("sap");
            p5.setGlobalNum("GUID-p5");
            p5.setSalary(10);
            p5.setPersonNum(100);
            p5.setSerialNo(""+new Random(System.currentTimeMillis()).nextInt());
            pm.makePersistent(p5);

            Query inhQuery3 = pm.newNamedQuery(Developer.class, "DeveloperWithSkillUsingJoinForResultHashMap");
            inhQuery3.setClass(null);
            results = (List) inhQuery3.execute("sap");
            iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                assertTrue("Query with candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been HashMap", obj.getClass().getName().equals(HashMap.class.getName()));
                HashMap p = (HashMap)obj;
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "SKILL").equals("sap"));
                assertTrue("Query with candidate class has returned the wrong DeveloperRC object.", 
                    getValueForKeyInMapCaseInsensitive(p, "GLOBALNUM").equals("GUID-p5"));
                Number salary = (Number)getValueForKeyInMapCaseInsensitive(p, "SALARY");
                assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 10, salary.intValue());
                Object personNumObj = getValueForKeyInMapCaseInsensitive(p, "PERSONNUM");
                if (personNumObj instanceof Long)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((Long)personNumObj).intValue());
                }
                else if (personNumObj instanceof BigDecimal)
                {
                    assertEquals("Query with candidate class has returned the wrong DeveloperRC object.", 100, 
                        ((BigDecimal)personNumObj).intValue());
                }
                else
                {
                    fail("Test doest support keys in map of type " + personNumObj.getClass());
                }
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Developer.class);
            clean(Person.class);
        }
    }

    /**
     * Basic test of SQL without a candidate class but with parameters.
     */
    public void testWithoutCandidatesClassWithParameters() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert a new element for table person
            tx.begin();
            Person p = new Person(1, "Nobody", "Nobody", "nobody@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();
            String sqlText = "SELECT count(*) FROM PERSON WHERE EMAIL_ADDRESS = ?";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            List results = (List) query.execute("nobody@jpox.org");
            Iterator iter = results.iterator();
            assertEquals(1, results.size());
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.getClass().isArray())
                {
                    fail("SQL Query selecting count(*) has returned an Object[] yet should have been Object");
                }
                assertTrue("SQL Query selecting count(*) has returned an object of the wrong type : was " + obj.getClass().getName() + " but should have been Number or subclass", obj instanceof Number);
                Number value = (Number)obj;
                assertEquals("SQL Query selecting count(*) returned the wrong value : was " + value.longValue() + " but should have been 1", value.longValue(), 1);
            }

            //test more than one parameter
            sqlText = "SELECT count(*) FROM PERSON WHERE EMAIL_ADDRESS = ? AND FIRSTNAME = ?";
            query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            results = (List) query.execute("nobody@jpox.org","Nobody");
            iter = results.iterator();
            assertEquals(1, results.size());
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.getClass().isArray())
                {
                    fail("SQL Query selecting count(*) has returned an Object[] yet should have been Object");
                }
                assertTrue("SQL Query selecting count(*) has returned an object of the wrong type : was " + obj.getClass().getName() + " but should have been Number or subclass", obj instanceof Number);
                Number value = (Number)obj;
                assertEquals("SQL Query selecting count(*) returned the wrong value : was " + value.longValue() + " but should have been 1", value.longValue(), 1);
            }

            //test more than one parameter
            sqlText = "SELECT count(*) FROM PERSON WHERE EMAIL_ADDRESS = ? AND FIRSTNAME = ?";
            query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            results = (List) query.execute("nobody@jpox.org","Noboda");
            assertEquals(1, results.size());

            //test more than one parameter
            sqlText = "SELECT * FROM PERSON WHERE EMAIL_ADDRESS = ? AND FIRSTNAME = ?";
            query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            results = (List) query.execute("nobody@jpox.org","Nobody");
            assertEquals(1, results.size());
            
            //test more than one parameter
            sqlText = "SELECT * FROM PERSON WHERE EMAIL_ADDRESS = ? AND FIRSTNAME = ?";
            query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            results = (List) query.execute("nobody@jpox.org","Noboda");
            assertEquals(0, results.size());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with parameters : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Basic test of SQL without a candidate class but with parameters specified in a Map.
     */
    public void testWithoutCandidatesWithParametersInMap() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert a new element for table person
            tx.begin();
            Person p = new Person(1, "Nobody", "Nobody", "nobody@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();
            String sqlText = "SELECT count(*) FROM PERSON WHERE EMAIL_ADDRESS = ?";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            Map params = new HashMap();
            params.put(new Integer("1"), "nobody@jpox.org");
            List results = (List) query.executeWithMap(params);
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.getClass().isArray())
                {
                    fail("SQL Query selecting count(*) has returned an Object[] yet should have been Object");
                }
                assertTrue("SQL Query selecting count(*) has returned an object of the wrong type : was " + obj.getClass().getName() + " but should have been Number or subclass", obj instanceof Number);
                Number value = (Number)obj;
                assertEquals("SQL Query selecting count(*) returned the wrong value : was " + value.longValue() + " but should have been 1", value.longValue(), 1);
            }
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with parameters : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test using Named Queries.
     */
    public void testNamedQueries()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert a new element for table person
            tx.begin();
            Person p = new Person(1, "Fred", "Jones", "fred.jones@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            // Named query with candidate class AND result class AND unique
            tx.begin();
            Query query = pm.newNamedQuery(Person.class, "NumberOfPeople");
            Object res = query.execute();
            assertTrue("Results returned from unique NamedQuery was not of result class type", res instanceof TableSize);
            TableSize tableSize = (TableSize) res;
            assertEquals("size expected from \"NumberOfPeople\" was 1", 1,
                    tableSize.getTheSize().intValue());
            tx.commit();

            // insert a new element for table person
            tx.begin();
            p = new Person(1, "Alan", "Smith", "alan.smith@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            // Named query with candidate class without result class
            tx.begin();
            query = pm.newNamedQuery(Person.class, "PeopleCalledSmith");
            List results = (List) query.execute();
            Iterator resultsIter = results.iterator();
            assertTrue("Incorrect number of results from NamedQuery with candidate class : was " + results.size() + " but should be 1",
                results.size() == 1);
            while (resultsIter.hasNext())
            {
                Person per = (Person)resultsIter.next();
                assertTrue("Incorrect Person returned by NamedQuery with candidate class : surname was " + per.getLastName() + " but should have been Smith",
                    per.getLastName().equals("Smith"));
            }
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test using Named Queries that aren't scoped by a candidate class.
     */
    public void testNamedQueriesDescoped()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some inherited data to query and query using a ResultClass without constructor
            tx = pm.currentTransaction();
            tx.begin();
            Developer p3 = new Developer(13, "James", "Java", "james@java.com", (float)15.00, "1234569", new Integer(3), "jdo");
            pm.makePersistent(p3);
            tx.commit();

            // Named query with no candidate class without result class
            tx.begin();
            Query query = pm.newNamedQuery(null, "DeveloperWithSkillForResultDescoped");
            List results = (List) query.execute("jdo");
            Iterator resultsIter = results.iterator();
            assertTrue("Incorrect number of results from NamedQuery with no candidate class : was " + results.size() + " but should be 1",
                results.size() == 1);
            while (resultsIter.hasNext())
            {
                Object obj = resultsIter.next();
                assertTrue("Query with no candidate class has returned the wrong type of object : was " + 
                    obj.getClass().getName() + " but should have been DeveloperRC", obj.getClass().getName().equals(DeveloperRC.class.getName()));
                DeveloperRC p = (DeveloperRC)obj;
                assertTrue("Query with no candidate class has returned the wrong Developer object.", p.getSKILL().equals("jdo"));
            }
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while executing Named Query with no class scope : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Developer.class);
        }
    }

    /**
     * Test of a query with a timeout.
     */
    public void testQueryWithTimeout()
    throws Exception
    {
        // Try a query
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // TODO Change this to a query that will take a LONG time and check for it
            String sqlText = "SELECT count(*) FROM PERSON";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            query.addExtension("org.jpox.query.timeout", "1");
            query.execute();

            tx.commit();
        }
        catch (JDODataStoreException dse)
        {
            fail("JDODataStoreException thrown when using query timeout : " + dse.getCause().getMessage());
        }
        catch (JDOUserException ue)
        {
            // Do nothing, since this is expected
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
     * Test of a query specified in a jdo query file.
     */
    public void testQueryFromJdoqueryFile()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some inherited data to query and query using a ResultClass without constructor
            tx = pm.currentTransaction();
            tx.begin();
            Developer p3 = new Developer(13, "James", "Java", "james@java.com", (float)15.00, "1234569", new Integer(3), "jdo");
            pm.makePersistent(p3);
            tx.commit();

            // Run the query (specified in a jdoquery file)
            tx.begin();
            Query query = pm.newNamedQuery(null, "DeveloperSkills");
            List results = (List) query.execute("jdo");
            Iterator resultsIter = results.iterator();
            while (resultsIter.hasNext())
            {
                Object skill = resultsIter.next();
                LOG.debug(">> Skill : " + skill);
            }
        }
        catch (JDOUserException ue)
        {
            ue.printStackTrace();
            LOG.error(ue);
            fail("Exception thrown while persisting object and performing query : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Developer.class);
        }
    }

    /**
     * Test of a query not starting with "SELECT"
     */
    public void testInvalidQueryAllowedByConfiguration()
    throws Exception
    {
        addClassesToSchema(new Class[]{Person.class, Manager.class, Employee.class, Developer.class});

        // Try a query
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_SQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String sqlText = "EXECUTE SELECT 1 FROM PERSON";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            query.execute();
            //expected
        }
        catch (JDODataStoreException dse)
        {
            //expected, if query is invalid by database
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
     * Test of a query including EOLs.
     */
    public void testQueryWithEndOfLineChars()
    throws Exception
    {
        // Try a query
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            String sqlText = "SELECT\n*\nFROM\nPERSON";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            query.execute();
            //expected
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
     * Test use of an SQL query with parameters calling it multiple times.
     * @throws Exception
     */
    public void testWithoutCandidatesClassWithParametersMultipleExecution()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert new elements for table person
            tx.begin();
            Person p = new Person(1, "a", "A", "a@jpox.org");
            pm.makePersistent(p);
            p = new Person(2, "b", "B", "b@jpox.org");
            pm.makePersistent(p);
            p = new Person(3, "c", "C", "c@jpox.org");
            pm.makePersistent(p);
            p = new Person(4, "d", "D", "d@jpox.org");
            pm.makePersistent(p);
            p = new Person(5, "e", "E", "e@jpox.org");
            pm.makePersistent(p);
            p = new Person(6, "f", "F", "f@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();

            //test more than one parameter; multiple execution
            int[][] parameters = new int[][] { { 1, 3 }, { 3, 5 }, { 5, 7 } };
            String sqlText = "SELECT count(*) FROM PERSON WHERE PERSONNUM >= ? AND PERSONNUM < ?";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);

            for (int i = 0; i < parameters.length; i++)
            {
                List list = (List) query.execute(new Integer(parameters[i][0]),
                    new Integer(parameters[i][1]));
                Object obj = list.iterator().next();
                if (obj instanceof Integer)
                {
                    assertEquals(2, ((Integer)obj).intValue());
                }
                else if (obj instanceof Long)
                {
                    assertEquals(2, ((Long)obj).intValue());
                }
                else if (obj instanceof BigInteger)
                {
                    assertEquals(2, ((BigInteger)obj).intValue());
                }
                else if (obj instanceof BigDecimal)
                {
                    assertEquals(2, ((BigDecimal)obj).intValue());
                }
                else
                {
                    fail("Test doesnt support count(*) being returned as " + obj.getClass().getName());
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with parameters : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test use of an SQL query using numbered parameters ("?1", "?2", etc).
     * @throws Exception
     */
    public void testNumberedParameters()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert new elements for table person
            tx.begin();
            Person p = new Person(1, "John", "Smith", "a@jpox.org");
            pm.makePersistent(p);
            p = new Person(2, "Paul", "Smith", "b@jpox.org");
            pm.makePersistent(p);
            p = new Person(3, "Paul", "Green", "c@jpox.org");
            pm.makePersistent(p);
            p = new Person(4, "John", "Brown", "d@jpox.org");
            pm.makePersistent(p);
            p = new Person(5, "Jason", "White", "e@jpox.org");
            pm.makePersistent(p);
            p = new Person(6, "John", "White", "f@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();

            // Use numbered params and reuse param 1
            String sqlText = "SELECT count(*) FROM PERSON WHERE (FIRSTNAME = ?1 AND LASTNAME = ?2) OR " + 
                "(FIRSTNAME = ?1 AND LASTNAME = ?3)";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);

            List list = (List) query.execute("John", "Smith", "Brown");
            Object obj = list.iterator().next();
            if (obj instanceof Integer)
            {
                assertEquals(2, ((Integer)obj).intValue());
            }
            else if (obj instanceof Long)
            {
                assertEquals(2, ((Long)obj).intValue());
            }
            else if (obj instanceof BigInteger)
            {
                assertEquals(2, ((BigInteger)obj).intValue());
            }
            else if (obj instanceof BigDecimal)
            {
                assertEquals(2, ((BigDecimal)obj).intValue());
            }
            else
            {
                fail("Test doesnt support count(*) being returned as " + obj.getClass().getName());
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with parameters : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test use of an SQL query using named parameters (":myname1", ":myothername2", etc).
     * @throws Exception
     */
    public void testNamedParameters()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert new elements for table person
            tx.begin();
            Person p = new Person(1, "John", "Smith", "a@jpox.org");
            pm.makePersistent(p);
            p = new Person(2, "Paul", "Smith", "b@jpox.org");
            pm.makePersistent(p);
            p = new Person(3, "Paul", "Green", "c@jpox.org");
            pm.makePersistent(p);
            p = new Person(4, "John", "Brown", "d@jpox.org");
            pm.makePersistent(p);
            p = new Person(5, "Jason", "White", "e@jpox.org");
            pm.makePersistent(p);
            p = new Person(6, "John", "White", "f@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();

            // Use numbered params and reuse param 1
            String sqlText = "SELECT count(*) FROM PERSON WHERE (FIRSTNAME = :firstName AND LASTNAME = :surname1) OR " + 
                "(FIRSTNAME = :firstName AND LASTNAME = :surname2)";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);

            Map params = new HashMap();
            params.put("firstName", "John");
            params.put("surname1", "Smith");
            params.put("surname2", "Brown");
            List list = (List) query.executeWithMap(params);
            Object obj = list.iterator().next();
            if (obj instanceof Integer)
            {
                assertEquals(2, ((Integer)obj).intValue());
            }
            else if (obj instanceof Long)
            {
                assertEquals(2, ((Long)obj).intValue());
            }
            else if (obj instanceof BigInteger)
            {
                assertEquals(2, ((BigInteger)obj).intValue());
            }
            else if (obj instanceof BigDecimal)
            {
                assertEquals(2, ((BigDecimal)obj).intValue());
            }
            else
            {
                fail("Test doesnt support count(*) being returned as " + obj.getClass().getName());
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while running SQL query with parameters : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Verify CORE-2976
     */
    public void testTimestampQueryOnOracle()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // run this test only on Oracle
            if (vendorID != null && vendorID.equals("oracle"))
            {
                tx.begin();
                Query timestampQuery = pm.newQuery(Query.SQL, "SELECT LOCALTIMESTAMP FROM DUAL");
                timestampQuery.setResultClass(java.sql.Timestamp.class);
                timestampQuery.setUnique(true);
                Timestamp result = (Timestamp) timestampQuery.execute();
                assertNotNull(result);
            }
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
     * Test use of an SQL statement to update data in the datastore (extension).
     * @throws Exception
     */
    public void testSQLUpdateStatement()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_SQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            // insert new elements for table person
            tx.begin();
            Person p = new Person(1, "a", "A", "a@jpox.org");
            pm.makePersistent(p);
            p = new Person(2, "b", "B", "b@jpox.org");
            pm.makePersistent(p);
            p = new Person(3, "c", "C", "c@jpox.org");
            pm.makePersistent(p);
            p = new Person(4, "d", "D", "d@jpox.org");
            pm.makePersistent(p);
            p = new Person(5, "e", "E", "e@jpox.org");
            pm.makePersistent(p);
            p = new Person(6, "f", "F", "f@jpox.org");
            pm.makePersistent(p);
            tx.commit();

            tx.begin();

            // Try a simple UPDATE statement
            String sqlText = "UPDATE PERSON SET FIRSTNAME=? WHERE FIRSTNAME=?";
            Query query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            Object results = query.execute("aa", "a");
            assertNotNull("Result is null from UPDATE statement!", results);
            assertTrue("Result is not an Long from UPDATE statement! (" + results.getClass().getName() + ")", 
                results instanceof Long);
            long numRecords = ((Long)results).longValue();
            assertEquals("Number of records updated by UPDATE statement was incorrect!", numRecords, 1);

            // Try a DELETE statement
            sqlText = "DELETE FROM PERSON WHERE FIRSTNAME=?";
            query = pm.newQuery("javax.jdo.query.SQL", sqlText);
            results = query.execute("aa");
            assertNotNull("Result is null from DELETE statement!", results);
            assertTrue("Result is not an Long from DELETE statement! (" + results.getClass().getName() + ")", 
                results instanceof Long);
            numRecords = ((Long)results).longValue();
            assertEquals("Number of records updated by DELETE statement was incorrect!", numRecords, 1);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while running SQL update statements : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }

    /**
     * Test of a query with a candidate class with comments in the SELECT.
     * @throws Exception Thrown if an error occurs
     */
    public void testWithCandidateClassWithComments()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            Person p1 = new Person(1, "First", "Person", "first.person@jpox.org");
            pm.makePersistent(p1);

            Person p2 = new Person(2, "Second", "Person", "second.person@jpox.org");
            pm.makePersistent(p2);
            tx.commit();

            // Query for a basic object, including the PK field(s) and a comment
            tx = pm.currentTransaction();
            tx.begin();

            String queryStr = null;
            if (pmf.getMetadata(Person.class.getName()).getIdentityType().equals(IdentityType.APPLICATION))
            {
                queryStr = "SELECT /*SomethingOrOther*/PERSONNUM, GLOBALNUM FROM PERSON WHERE FIRSTNAME = 'Second'";
            }
            else
            {
                queryStr = "SELECT /*SomethingOrOther*/PERSON_ID FROM PERSON WHERE FIRSTNAME = 'Second'";
            }

            Query query = pm.newQuery("javax.jdo.query.SQL", queryStr);
            
            query.setClass(Person.class);
            List<Person> results = (List<Person>) query.execute();
            assertNotNull(results);
            assertEquals("Number of results incorrect", 1, results.size());
            Person p = results.iterator().next();
            assertTrue("Query with candidate class has returned the wrong Person object.", p.getPersonNum() == 2);
            assertTrue("Query with candidate class has returned the wrong Person object.", p.getFirstName().equals("Second"));

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Person.class);
        }
    }
}