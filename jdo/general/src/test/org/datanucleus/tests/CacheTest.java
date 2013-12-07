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


Contributors:
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.datastore.DataStoreCache;

import org.datanucleus.api.jdo.JDODataStoreCache;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.api.jdo.NucleusJDOHelper;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.models.voting.Vote;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;
import org.jpox.samples.versioned.Trade1;

/**
 * Tests for the L1, L2 caches.
 */
public class CacheTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public CacheTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Employee.class,
                    Manager.class,
                    Person.class,
                    Organisation.class,
                    Qualification.class
                });
            initialised = true;
        }
    }

    /**
     * Basic test for L1 Cache using "WeakRefCache" and L2 cache.
     */
    public void testL1WeakRefL2()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.cache.level1.type", "weak");
        userProps.setProperty("datanucleus.cache.level2.type", "weak");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        runL2CacheTestForPMF(cachePMF);
    }

    /**
     * Basic test for L1 Cache using "SoftRefCache" and L2 cache.
     */
    public void testL1SoftRefL2()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.cache.level1.type", "soft");
        userProps.setProperty("datanucleus.cache.level2.type", "weak");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        runL2CacheTestForPMF(cachePMF);
    }

    /**
     * Method to perform a basic test of L2 Caching for the specified PMF.
     * @param cachePMF The PMF to use
     */
    private void runL2CacheTestForPMF(PersistenceManagerFactory cachePMF)
    {
        try
        {
            // Create a PM and add an object
            Object id = null; 
            PersistenceManager pm = cachePMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Employee empl = new Employee(101, "Clint", "Eastwood", "clint.eastwood@hollywood.com", 1000000, "1234567");
                pm.makePersistent(empl);
                id = pm.getObjectId(empl);
                
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

            // Create PM's and run the test
            PersistenceManager pm1 = cachePMF.getPersistenceManager();
            performSerialNumberChange(pm1, id, "1234569");
            
            PersistenceManager pm2 = cachePMF.getPersistenceManager();
            performSerialNumberChange(pm2, id, "1234579"); // will fail if the L2 doesnt allow use by other PMs
            pm1.close();
            pm2.close();
        }
        finally
        {
            clearEmployeeData();
        }
    }

    /**
     * Convenience method to update an object using a PM
     * @param pm The Persistence Manager
     * @param id Id of the object to update
     * @param newNumber New serial number to apply to the object
     */
    private void performSerialNumberChange(PersistenceManager pm, Object id, String newNumber)
    {
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            Employee empl = (Employee)pm.getObjectById(id, true);
            empl.setSerialNo(newNumber);
            tx.commit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Attempted to update a field on a cached object and the access failed : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
    }

    /**
     * Test for storage of an object in the L2 cache, and whether it has its fields populated.
     */
    public void testL2CachedObject()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);
            
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object woodyId = null;
            Object woodlessId = null;
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Employee.class); // All Employees/Managers get pinned
                tx.begin();
                final Employee woody = new Employee(1,"Woody",null,"woody@woodpecker.com",13,"serial 1",new Integer(10));
                final Employee woodless = new Employee(2,"Woodless","Woodpecker","woodless@woodpecker.com",14,"serial 2",new Integer(11));
                Manager bart = new Manager(3,"Bart","Simpson","bart@simpson.com",3,"serial 3");
                woody.setManager(bart);
                pm.makePersistent(woody);

                woody.setLastName("Woodpecker");
                pm.makePersistent(woodless);

                woodyId = pm.getObjectId(woody);
                woodlessId = pm.getObjectId(woodless);

                // Woody, Woodless, and Bart will all be pinned since we have all Employee objects being pinned

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run multithread test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            assertTrue("Incorrect number of pinned objects : should have been 3 but is " + l2Cache.getNumberOfPinnedObjects(),
                l2Cache.getNumberOfPinnedObjects() == 3);
            assertTrue("Level 2 Cache returned that it is empty yet should have pinned object(s)!",
                !l2Cache.isEmpty());

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Employee woody = (Employee)pm.getObjectById(woodyId, false);
                assertTrue("Object retrieved from L2 cache was null", woody != null);
                assertTrue("Field of object retrieved from L2 cache was null!", woody.getLastName() != null);
                
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error encountered accessing object from L2 cache : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            l2Cache.evict(woodlessId);
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfPinnedObjects() + " pinned objects, yet should have 2",
                l2Cache.getNumberOfPinnedObjects() == 2);

            // Clear the cache and check if the objects are released
            l2Cache.evictAll();
            assertTrue("Level 2 Cache returned that it is not empty yet we just cleared it!",
                l2Cache.isEmpty());
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfPinnedObjects() + " pinned objects, yet we just cleared it!",
                l2Cache.getNumberOfPinnedObjects() == 0);
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfUnpinnedObjects() + " unpinned objects, yet we just cleared it!",
                l2Cache.getNumberOfUnpinnedObjects() == 0);
        }
        finally
        {
            clearEmployeeData();
        }
    }

    /**
     * Test to check the retrieval of an object from the L2 cache and the observance of its loaded fields.
     */
    public void testL2LoadedFields()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.cache.level1.type", "soft");
        userProps.setProperty("datanucleus.cache.level2.type", "weak");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        try
        {
            // Create a PM and add an object
            Object id = null; 
            PersistenceManager pm = cachePMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(102, "George", "Bush", "george.bush@whitehouse.gov");
                pm.makePersistent(p1);
                id = pm.getObjectId(p1);

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

            // Clear the L2 cache so we dont have this object
            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            l2Cache.evictAll();
            l2Cache.pinAll(Person.class, false); // Pin all Person objects

            // Retrieve the object with just some fields
            PersistenceManager pm1 = cachePMF.getPersistenceManager();
            tx = pm1.currentTransaction();
            pm1.getFetchPlan().setGroup("groupA");
            try
            {
                // Load the Person object - will only have firstName, lastName loaded.
                // Will be added to L2 cache, so pin it
                tx.begin();
                pm1.getObjectById(id);

                // George Bush will now be pinned since all Person objects are pinned

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving object to store in L2 cache with only few fields : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            // Object should now be in L2 cache

            // Retrieve the object with all fields and check an unretrieved field
            PersistenceManager pm2 = cachePMF.getPersistenceManager();
            tx = pm2.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = (Person)pm2.getObjectById(id);
                assertTrue("Additional field of L2 cached object hasn't been retrieved correctly (wasnt in original FetchPlan)", p1.getEmailAddress() != null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving object from L2 cache : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm1.close();
            pm2.close();
        }
        finally
        {
            // Clean out created data
            clean(Person.class);
        }
    }

    /**
     * Demonstrate refreshing SCO and PC field values for a PC object returned from the L2 cache.
     */
    public void testSCOAndPCReuse()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);

            // Create some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object qualId = null;
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Qualification.class);
                l2Cache.pinAll(true, Organisation.class);
                l2Cache.pinAll(true, Person.class);
                tx.begin();

                Person tweety = new Person(1, "Tweety", "Pie", "tweety.pie@warnerbros.com");
                pm.makePersistent(tweety);

                Organisation org = new Organisation("The Training Company");
                pm.makePersistent(org);

                Qualification qual = new Qualification("Certified JPOX Developer");
                qual.setPerson(tweety);
                qual.setOrganisation(org);
                qual.setDate(new GregorianCalendar(2005, 8, 02).getTime());
                pm.makePersistent(qual);
                qualId = pm.getObjectId(qual);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run SCO/PC field reuse test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            /*
             * OK, now we have a Person, an Organisation and a Qualification object in 2L cache.
             * Previously getting the Qualification object from 2L cache cleared SCO
             * and PC fields, so accessing the room/guest or any of the date
             * fields resulted in SQL query, even though they are in the cache.
             * Let's see if that changed. The following should cause no SQL
             * execution at all.
             */
            PersistenceManager pm2 = pmf.getPersistenceManager();
            tx = pm2.currentTransaction();
            try
            {
                tx.begin();
                Qualification qual = (Qualification) pm2.getObjectById(qualId);

                // these are PC fields, their primary keys are kept even in cache
                assertEquals("Person's first name is not what expected", qual.getPerson().getFirstName(), "Tweety");
                assertEquals("Person's last name is not what expected", qual.getPerson().getLastName(), "Pie");

                // this is a SCO field, value is kept even in cache
                assertEquals("From date is not what expected", qual.getDate(), new GregorianCalendar(2005, 8, 02).getTime());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error using objects");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm2.close();
            }

            /*
             * Now we unpin all objects and clean out the cache. Then we make
             * sure, that in spite of the changes, it is still reloading fields,
             * when they are not available
             */
            PersistenceManager pm3 = pmf.getPersistenceManager();
            tx = pm3.currentTransaction();
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.unpinAll(true, Qualification.class);
                l2Cache.unpinAll(true, Person.class);
                l2Cache.unpinAll(true, Organisation.class);
                l2Cache.evictAll();

                tx.begin();
                Qualification qual = (Qualification) pm3.getObjectById(qualId);

                assertEquals("Person's first name is not what expected", qual.getPerson().getFirstName(), "Tweety");
                assertEquals("Person's last name is not what expected", qual.getPerson().getLastName(), "Pie");

                assertEquals("From date is not what expected", qual.getDate(), new GregorianCalendar(2005, 8, 02).getTime());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error using objects");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm3.close();
            }
        }
        finally
        {
            clean(Qualification.class);
            clean(Organisation.class);
            clean(Person.class);
        }
    }

    /**
    * Test DataStoreCache.evictAll(Class, boolean)
    */
    public void testEvictAll()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "soft");
            pmf = TestHelper.getPMF(1, userProps);
            
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Employee.class); // All Employees/Managers get pinned

                tx.begin();
                final Employee woody = new Employee(1,"Woody",null,"woody@woodpecker.com",13,"serial 1",new Integer(10));
                final Employee woodless = new Employee(2,"Woodless","Woodpecker","woodless@woodpecker.com",14,"serial 2",new Integer(11));
                Manager bart = new Manager(3,"Bart","Simpson","bart@simpson.com",3,"serial 3");
                woody.setManager(bart);
                pm.makePersistent(woody);
                woody.setLastName("Woodpecker");
                pm.makePersistent(woodless);

                // Woody, Woodless, and Bart will all be pinned since we have all Employee objects being pinned

                // create a few unpinned objects so DefaultLevel2Cache.evictAll() will have something to iterate
                // and remove a few times
                Qualification quali = new Qualification("patience");
                pm.makePersistent(quali);
                quali = new Qualification("endurance");
                pm.makePersistent(quali);
                quali = new Qualification("humour");
                pm.makePersistent(quali);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run multithread test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();

            // cannot assert reliably existence of unpinned objects as they can get GC'ed any time
            // just check that the following executes without errors and that there are no unpinned objects
            // afterwards
            l2Cache.evictAll(Qualification.class, true);
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfUnpinnedObjects() + " unpinned objects, yet we just cleared it!",
                l2Cache.getNumberOfUnpinnedObjects() == 0);
            
            // check whether it was only the Qualification objects that got evicted
            assertTrue("Incorrect number of pinned objects : should have been 3 but is " + l2Cache.getNumberOfPinnedObjects(),
                l2Cache.getNumberOfPinnedObjects() == 3);
            assertTrue("Level 2 Cache returned that it is empty yet should have pinned object(s)!",
                !l2Cache.isEmpty());

            // evict all Employee + subclass objects and check if the objects are released
            l2Cache.evictAll(Employee.class, true);
            assertTrue("Level 2 Cache returned that it is not empty yet we just cleared it!",
                l2Cache.isEmpty());
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfPinnedObjects() + " pinned objects, yet we just cleared it!",
                l2Cache.getNumberOfPinnedObjects() == 0);
            assertTrue("Level 2 Cache returned that it has " + l2Cache.getNumberOfUnpinnedObjects() + " unpinned objects, yet we just cleared it!",
                l2Cache.getNumberOfUnpinnedObjects() == 0);
        }
        finally
        {
            clearEmployeeData();
        }
    }

    /**
    * Test use of optimistic txns and L2 cache. Tests that we store and retrieve the version correctly
    * when using L2 cached objects
    */
    public void testOptimisticTransactionWithL2Cache()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);

            DataStoreCache l2Cache = pmf.getDataStoreCache();
            l2Cache.pinAll(true, Trade1.class); // All Trade1 get pinned

            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            Object version = null;
            try
            {
                tx.begin();

                Trade1 trade = new Trade1("Woody Woodpecker", 500.0, new java.util.Date());
                pm.makePersistent(trade);

                // trade will be pinned since we have all Trade1 objects being pinned

                tx.commit();
                id = JDOHelper.getObjectId(trade);
                version = JDOHelper.getVersion(trade);
                assertNotNull("Version of persisted object is null!!", JDOHelper.getVersion(trade));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run optimistic L2 test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Start new PM so we know "woody" isnt present in the L1 cache and has to get from L2
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Trade1 trade = (Trade1)pm.getObjectById(id);
                assertNotNull("Version of retrieved L2 cached object is null!!", JDOHelper.getVersion(trade));
                assertEquals("Versions of original/retrieved objects are different so wasn't L2 cached correctly",
                    version, JDOHelper.getVersion(trade));
                trade.setPerson("Donald Duck");

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error updating object that was retrieved from the L2 cache : " + e.getMessage());
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
            clean(Trade1.class);
        }
    }

    /**
     * Basic test for "detachAllOnCommit" WITHOUT L2 cache usage.
     */
    public void testDetachAllOnCommitWithoutL2()
    {
        Properties userProps = new Properties();
        userProps.setProperty("javax.jdo.option.DetachAllOnCommit", "true");
        userProps.setProperty("datanucleus.cache.level1.type", "weak");
        userProps.setProperty("datanucleus.cache.level2.type", "none");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        runL2CacheDetachmentTestForPMF(cachePMF, 5);
    }

    /**
     * Basic test for "detachAllOnCommit" WITH L2 cache usage.
     */
    public void testDetachAllOnCommitWithL2()
    {
        Properties userProps = new Properties();
        userProps.setProperty("javax.jdo.option.DetachAllOnCommit", "true");
        userProps.setProperty("datanucleus.cache.level1.type", "weak");
        userProps.setProperty("datanucleus.cache.level2.type", "weak");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        runL2CacheDetachmentTestForPMF(cachePMF, 5);
    }

    /**
     * Test for whether a class that is marked as not cacheable is L2 cached.
     */
    public void testClassNotCacheable()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);

            // Create some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object qualId = null;
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Qualification.class);
                tx.begin();

                Qualification qual = new Qualification("Certified JPOX Developer");
                pm.makePersistent(qual);
                qualId = pm.getObjectId(qual);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting data for test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            JDODataStoreCache jdoCache = (JDODataStoreCache)pmf.getDataStoreCache();
            Level2Cache l2Cache = jdoCache.getLevel2Cache();
            assertNull("QUalification object should not have been L2 cached but was!", l2Cache.get(qualId));

            // Try to retrieve this object - need a way of detecting if it tried the L2 cache
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                LOG.info(">> getObjectById qualId=" + qualId);
                pm.getObjectById(qualId);
                LOG.info(">> getObjectById qualId done");
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting data for test");
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

    /**
     * Test to check the access of the same object in 2 PMs, with the first PM changing it then committing
     * and by the time the second PM commits (with no change) the object has been evicted.
     */
    public void testL2MultiplePMSameObjectEvictionChange()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.cache.level1.type", "soft");
        userProps.setProperty("datanucleus.cache.level2.type", "weak");
        PersistenceManagerFactory cachePMF = TestHelper.getPMF(1, userProps);

        try
        {
            // Create a PM and add an object
            Object id = null; 
            PersistenceManager pm = cachePMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(102, "George", "Bush", "george.bush@whitehouse.gov");
                pm.makePersistent(p1);
                id = pm.getObjectId(p1);

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

            // Clear the L2 cache so we don't have this object
            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            l2Cache.evictAll();
            l2Cache.pinAll(Person.class, false); // Pin all Person objects

            // Start 2 PMs, and their txns
            PersistenceManager pm1 = cachePMF.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            tx1.begin();
            PersistenceManager pm2 = cachePMF.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            tx2.begin();

            // Retrieve the object in both PMs
            Person per1 = (Person)pm1.getObjectById(id);
            /*Person per2 = (Person)*/pm2.getObjectById(id);

            // Change the object in PM1, and commit it
            per1.setEmailAddress("obama@whitehouse.gov");
            tx1.commit();
            cachePMF.getDataStoreCache().evict(id);

            // Commit PM2 txn
            tx2.commit();
            pm1.close();
            pm2.close();

            // Retrieve the object and check it
            PersistenceManager pm3 = cachePMF.getPersistenceManager();
            tx = pm3.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = (Person)pm3.getObjectById(id);
                assertEquals("Email address found was not modified version!", "obama@whitehouse.gov", p1.getEmailAddress());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving object from L2 cache : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm3.close();
            }
        }
        finally
        {
            // Clean out created data
            clean(Person.class);
        }
    }

    /**
     * Test for storage of an object in the L2 cache from a query or from getObjectById.
     */
    public void testL2CacheAfterReadDatastoreIdentity()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);
            
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object woodyId = null;
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Employee.class); // All Employees/Managers get pinned
                tx.begin();
                final Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com",
                    13, "serial 1", new Integer(10));
                pm.makePersistent(woody);
                woodyId = pm.getObjectId(woody);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run multithread test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            assertEquals("Incorrect number of pinned objects", 1, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects", 0, l2Cache.getNumberOfUnpinnedObjects());
            l2Cache.evictAll();
            assertEquals("Incorrect number of pinned objects after evict", 0, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects after evict", 0, l2Cache.getNumberOfUnpinnedObjects());

            // Do the query - should load the object into the L2 cache
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(Employee.class);
                List results = (List)q.execute();
                assertEquals("Number of objects returned by query was incorrect", 1, results.size());
                Iterator iter = results.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }

                // Check the cache
                assertEquals("L2 cache size is incorrect after query", 1, l2Cache.getSize());
                assertTrue("Level 2 Cache (after query) doesnt have the object but should!", l2Cache.containsOid(woodyId));

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error encountered accessing object from L2 cache : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check/reset L2 cache
            assertEquals("Incorrect number of pinned objects", 1, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects", 0, l2Cache.getNumberOfUnpinnedObjects());
            l2Cache.evictAll();
            assertEquals("Incorrect number of pinned objects after evict", 0, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects after evict", 0, l2Cache.getNumberOfUnpinnedObjects());

            // Do getObjectById - should load the object into the L2 cache
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = (Employee)pm.getObjectById(woodyId);
                assertNotNull("Retrieved object is null!", woody);

                // Check the cache
                assertEquals("L2 cache size is incorrect (after getObjectById)", 1, l2Cache.getSize());
                assertTrue("Level 2 Cache (after getObjectById) doesnt have the object but should!", l2Cache.containsOid(woodyId));

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error encountered accessing object from L2 cache : " + e.getMessage());
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
            clearEmployeeData();
        }
    }

    /**
     * Test for storage of an object in the L2 cache from a query or from getObjectById.
     */
    public void testL2CacheAfterReadApplicationIdentity()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);

            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Vote.class);
                tx.begin();

                Vote vote1 = new Vote(1, "Vote 1");
                pm.makePersistent(vote1);
                Vote vote2 = new Vote(2, "Vote 2");
                pm.makePersistent(vote2);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run optimistic L2 test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            assertEquals("Incorrect number of pinned objects", 2, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects", 0, l2Cache.getNumberOfUnpinnedObjects());
            l2Cache.evictAll();
            assertEquals("Incorrect number of pinned objects after evict", 0, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects after evict", 0, l2Cache.getNumberOfUnpinnedObjects());

            // Try getObjectById
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Vote vote1 = pm.getObjectById(Vote.class, 1);
                Vote vote2 = pm.getObjectById(Vote.class, 2);
                assertNotNull(vote1);
                assertNotNull(vote2);
                assertEquals("Incorrect number of pinned objects after getObjectById", 2, l2Cache.getNumberOfPinnedObjects());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error updating object that was retrieved from the L2 cache : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            assertEquals("Incorrect number of pinned objects", 2, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects", 0, l2Cache.getNumberOfUnpinnedObjects());
            l2Cache.evictAll();
            assertEquals("Incorrect number of pinned objects after evict", 0, l2Cache.getNumberOfPinnedObjects());
            assertEquals("Incorrect number of unpinned objects after evict", 0, l2Cache.getNumberOfUnpinnedObjects());

            // Try Query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query query = pm.newQuery(Vote.class);
                Collection votes = (Collection) query.execute();
                Iterator iter = votes.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                assertEquals("Incorrect number of pinned objects after Query", 2, l2Cache.getNumberOfPinnedObjects());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error retrieving object from the L2 cache : " + e.getMessage());
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
            clean(Vote.class);
        }
    }

    // ---------------------- Multithreaded tests -----------------------------

    /**
     * Test for the retrieval of an object from multiple threads where an L2
     * cache is in use. All threads should find the object in the (L2) cache and
     * return it.
     */
    public void testMultithreadObjectRead()
    {
        try
        {
            Properties userProps = new Properties();
            userProps.setProperty("datanucleus.cache.level1.type", "weak");
            userProps.setProperty("datanucleus.cache.level2.type", "weak");
            pmf = TestHelper.getPMF(1, userProps);
            
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object woodyId = null;
            try
            {
                DataStoreCache l2Cache = pmf.getDataStoreCache();
                l2Cache.pinAll(true, Employee.class);
                tx.begin();
                final Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13,
                    "serial 1", new Integer(10));
                Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
                woody.setManager(bart);
                pm.makePersistent(woody);
                pm.makePersistent(bart);
                woodyId = pm.getObjectId(woody);
                // Woody/Bart will now be pinned since they we have all Employee/Manager objects being pinned

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Error persisting basic data necessary to run multithread test");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            Level2Cache l2Cache = ((JDODataStoreCache)pmf.getDataStoreCache()).getLevel2Cache();
            assertTrue("Incorrect number of pinned objects : should have been 2 but is " + 
                l2Cache.getNumberOfPinnedObjects(),
                l2Cache.getNumberOfPinnedObjects() == 2);

            // Start multiple threads to retrieve the object
            // All should find it in the L2 cache
            int THREAD_SIZE = 5;
            final Object objectId = woodyId;
            Thread[] threads = new Thread[THREAD_SIZE];
            try
            {
                for (int i = 0; i < THREAD_SIZE; i++)
                {
                    final int threadNo = i;
                    threads[i] = new Thread(new Runnable()
                        {
                        public void run()
                        {
                            boolean success = true;
                            PersistenceManager pmthread = pmf.getPersistenceManager();
                            Transaction txthread = pmthread.currentTransaction();
                            try
                            {
                                txthread.begin();
                                Employee woody = (Employee)pmthread.getObjectById(objectId);
                                if (woody == null)
                                {
                                    LOG.error("Object retrieved from L2 cache is null, but should have a value !");
                                    success = false;
                                }
                                if (success)
                                {
                                    if (woody.getLastName() == null)
                                    {
                                        LOG.error("Field of object retrieved from L2 cache is null, but should have its value !");
                                        success = false;
                                    }
                                    if (success)
                                    {
                                        if (woody.getManager().getLastName() == null)
                                        {
                                            LOG.error("Field of related object retrieved from L2 cache is null, but should have a value !");
                                            success = false;
                                        }
                                    }
                                }
                                txthread.commit();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                fail("Exception thrown while accessing object in thread " + threadNo + " : " + e.getMessage());
                            }
                            finally
                            {
                                if (txthread.isActive())
                                {
                                    txthread.rollback();
                                }
                            }
                            if (!success)
                            {
                                fail("Thread had an error in retrieving the L2 cache objects. Inspect the log for the errors");
                            }
                        }
                        });
                }

                // Start the threads
                for (int i = 0; i < THREAD_SIZE; i++)
                {
                    threads[i].start();
                }

                // Wait for the end of the threads
                for (int i = 0; i < THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                    }
                    catch (InterruptedException e)
                    {
                        fail(e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                fail("Error encountered while accessing the objects via the L2 Cache : " + e.getMessage());
            }
            finally
            {
            }
        }
        finally
        {
            clearEmployeeData();
        }
    }

    protected void clearEmployeeData()
    {
        Extent ext = null;
        java.util.Iterator it = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
          // disassociate all Employees and Departments from their Managers
          tx.begin();
          ext = pm.getExtent(Manager.class, false);
          it = ext.iterator();
          while (it.hasNext())
          {
            Manager mgr = (Manager) it.next();
            mgr.clearSubordinates();
            mgr.clearDepartments();
          }
          tx.commit();

          // delete all Employee objects
          tx.begin();
          ext = pm.getExtent(Employee.class, false);
          it = ext.iterator();
          while (it.hasNext())
          {
            Employee emp = (Employee) it.next();
            pm.deletePersistent(emp);
          }
          tx.commit();

          // delete all Qualification objects
          tx.begin();
          ext = pm.getExtent(Qualification.class, false);
          it = ext.iterator();

          while (it.hasNext())
          {
            Qualification q = (Qualification) it.next();
            pm.deletePersistent(q);
          }
          tx.commit();

          // delete all Department objects
          tx.begin();
          ext = pm.getExtent(Department.class, false);
          it = ext.iterator();
          while (it.hasNext())
          {
            Department d = (Department) it.next();
            pm.deletePersistent(d);
          }
          tx.commit();

          // delete all Manager objects
          tx.begin();
          ext = pm.getExtent(Manager.class, false);
          it = ext.iterator();
          while (it.hasNext())
          {
            Manager mgr = (Manager) it.next();
            pm.deletePersistent(mgr);
          }
          tx.commit();

          // delete all Person objects
          tx.begin();
          ext = pm.getExtent(Person.class, true);
          it = ext.iterator();
          while (it.hasNext())
          {
            Person person = (Person) it.next();
            pm.deletePersistent(person);
          }
          tx.commit();
        }
        finally
        {
          if (tx.isActive())
              tx.rollback();

          pm.close();
        }
    }

    /**
     * Method to perform a basic test of L2 Caching for the specified PMF.
     * @param cachePMF The PMF to use
     */
    private void runL2CacheDetachmentTestForPMF(final PersistenceManagerFactory cachePMF, final int loops)
    {
        try
        {
            // Create a PM and add an object
            PersistenceManager pm = cachePMF.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                DataStoreCache l2Cache = cachePMF.getDataStoreCache();
                l2Cache.pinAll(true, Employee.class);
                tx.begin();

                Employee empl = new Employee(101, "Clint", "Eastwood", "clint.eastwood@hollywood.com", 
                    1000000, "1234567");
                pm.makePersistent(empl);

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

            // Test object state of instance multiple times; using (auto) detachment.
            List employees;
            for (int i = 0; i < loops; i++)
            {
                Collection data = null;

                // Retrieve and detach the object (detachAllAtCommit)
                pm = cachePMF.getPersistenceManager();
                pm.setDetachAllOnCommit(true);
                tx = pm.currentTransaction();
                try
                {
                    tx.begin();

                    Query query = pm.newQuery(Employee.class);
                    data = (Collection) query.execute();
                    employees = new ArrayList(data);

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

                // Check that it is detached
                Iterator iter = employees.iterator();
                while (iter.hasNext())
                {
                    Employee employee = (Employee)iter.next();
                    assertEquals(ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(employee));
                }
            }
        }
        finally
        {
            clean(Employee.class);
        }
    }

    public void testRelationshipCachingWhenNontransactional()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Create sample data
            Object acctId = null; 
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount("Barney", "Rubble", "brubble", "bambam");
                pm.makePersistent(acct);
                acctId = JDOHelper.getObjectId(acct);

                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Make sure the object isn't in the cache following persistence
            pmf.getDataStoreCache().evictAll();
            pmf.getDataStoreCache().pinAll(true, LoginAccount.class);
            pmf.getDataStoreCache().pinAll(true, Login.class);

            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            try
            {
                LOG.info(">> CacheTest fetching LoginAccount(1)");
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                assertTrue("LoginAccount \"firstName\" is incorrect",acct.getFirstName().equals("Barney"));
                assertTrue("LoginAccount \"lastName\" is incorrect",acct.getLastName().equals("Rubble"));

                LOG.info(">> CacheTest fetching Login(1)");
                // Access the login field to make sure it is read. Should get L2 cached now
                Login login = acct.getLogin();
                assertEquals("Login \"login\" is incorrect", login.getUserName(), "brubble");
                assertEquals("Login \"password\" is incorrect", login.getPassword(), "bambam");
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating 1-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                pm.close();
            }

            // login account is in the cache now after the last getObjectById
            LOG.info(">> CacheTest fetching LoginAccount(non-tx)");
            CachedPC cpc = ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getLevel2Cache().get(acctId);
            assertTrue("LoginAccount is not cached", cpc != null);
            assertTrue("LoginAccount \"login\" is not cached", cpc.getFieldValue(2) != null);

            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            try
            {
                LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
                String[] loadedFields = NucleusJDOHelper.getLoadedFields(acct, pm);
                LOG.info(">> loadedFields=" + StringUtils.objectArrayToString(loadedFields));
                LOG.info(">> Accessing field login");
                Login login = acct.getLogin();
                LOG.info(">> login=" + login);
            }
            finally
            {
                pm.close();
            }
        }
        finally
        {
            clean(LoginAccount.class);
        }
    }
}