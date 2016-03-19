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
 2008 Andy Jefferson - test for app id dups
 2008 Andy Jefferson - tests for 1-1, 1-N
 2008 Eric Sultan - test for 1-N
 ...
 ***********************************************************************/
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.types.basic.BasicTypeHolder;
import org.jpox.samples.types.enums.Colour;
import org.jpox.samples.types.enums.Palette;

/**
 * Application identity persistence tests for HBase datastores.
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

                p.setBestFriend(p2);

                pm.makePersistent(p);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error when persisting 2 Person objects", e);
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

    public void testInsertThenUpdateDifferentPMs() throws Exception
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

                Person p3 = new Person();
                p3.setPersonNum(3);
                p3.setGlobalNum("3");
                p3.setFirstName("Daffy");
                p3.setLastName("Duck");

                pm.makePersistent(p);
                pm.makePersistent(p2);
                pm.makePersistent(p3);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception when persisting 3 Person objects", e);
                fail("Exception thrown when persisting in PM : " + e.getMessage());
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

            for (int i=0;i<3;i++)
            {
                PersistenceManager pm2 = pmf.getPersistenceManager();
                Transaction tx2 = pm2.currentTransaction();
                try
                {
                    tx2.begin();
                    Query q = pm2.newQuery("SELECT FROM " + Person.class.getName() + " WHERE this.lastName != \"Rabbit\"");
                    List<Person> people = (List<Person>) q.execute();
                    Person p = people.iterator().next();
                    p.setLastName("Rabbit");

                    tx2.commit();
                }
                catch (Exception e)
                {
                    LOG.error("Exception when querying and updating Person objects", e);
                    fail("Exception thrown when updating in PM2 : " + e.getMessage());
                    return;
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
        }
        finally
        {
            clean(Person.class);
        }
    }

    public void testInsertThenDelete() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id1 = null;
            Object id2 = null;
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

                pm.makePersistent(p1);
                pm.makePersistent(p2);
                pm.flush();
                id1 = pm.getObjectId(p1);
                id2 = pm.getObjectId(p2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception when persisting 2 Person objects", e);
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
                Person p1 = (Person)pm.getObjectById(id1);
                Person p2 = (Person)pm.getObjectById(id2);

                pm.deletePersistent(p1);
                pm.deletePersistent(p2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception when deleting 2 Person objects", e);
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

                Query q = pm.newQuery(Person.class);
                List<Person> results = (List)q.execute();
                assertNotNull("Results should not be null");
                assertEquals("Number of person objects is wrong", 0, results.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception when querying Person objects", e);
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
                LOG.error(">> Error when persisting first object", e);
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
     * Test persistence of an enum as a String.
     */
    public void testStringEnum()
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(100, p.getAmount());
                assertEquals(Colour.RED, p.getColour());
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(101, p.getAmount());
                assertNull(p.getColour());
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(102, p.getAmount());
                assertEquals(Colour.GREEN, p.getColour());
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
     * Test persistence of an enum as a numeric.
     */
    public void testEnumAsNumeric()
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(100, p.getAmount());
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(101, p.getAmount());
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                p = (Palette) pm.getObjectById(id, true);
                assertEquals(102, p.getAmount());
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

    public void testBasicTypes() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id1 = null;
            Object id2 = null;
            try
            {
                tx.begin();
                BasicTypeHolder basic1 = new BasicTypeHolder();
                basic1.setIntObjField(new Integer(1));
                BasicTypeHolder basic2 = new BasicTypeHolder();
                basic2.setIntObjField(null);
                pm.makePersistent(basic1);
                pm.makePersistent(basic2);
                tx.commit();
                id1 = pm.getObjectId(basic1);
                id2 = pm.getObjectId(basic2);
            }
            catch (Exception e)
            {
                LOG.error("Exception when persisting 2 BasicTypeHolder objects", e);
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
                BasicTypeHolder basic1 = (BasicTypeHolder)pm.getObjectById(id1);
                assertNotNull(basic1);
                assertEquals(new Integer(1), basic1.getIntObjField());
                BasicTypeHolder basic2 = (BasicTypeHolder)pm.getObjectById(id2);
                assertNotNull(basic2);
                assertNull(basic2.getIntObjField());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in retrieve", e);
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
            clean(BasicTypeHolder.class);
        }
    }
}