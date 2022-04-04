/**********************************************************************
Copyright (c) 2003 Mike Martin and others. All rights reserved.
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
2006 Andy Jefferson - converted into helper class
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.junit.Assert;

/**
 * Utility class to test the persistence of objects.
 * Provides simple tests for all main lifecycle operations.
 */
public class StorageTester
{
    public static final int TEST_OBJECT_COUNT = 10;

    protected Object[] ids = new Object[TEST_OBJECT_COUNT];

    protected TestObject[] objs = new TestObject[TEST_OBJECT_COUNT];

    PersistenceManagerFactory pmf = null;

    /**
     * Constructor
     * @param pmf Persistence Manager Factory
     */
    public StorageTester(PersistenceManagerFactory pmf)
    {
        this.pmf = pmf;
    }

    /**
     * Method to run the storage test for the provided class
     * @param c The class (assumed to extend TestObject)
     * @throws Exception Exception thrown during persistence testing
     */
    public void runStorageTestForClass(Class c) 
    throws Exception
    {
        try
        {
            insertObjects(c);
            validateObjects(c);
            updateObjects(c);
            validateObjects(c);
            iterateUsingExtent(c);
            validateTransactionalRefresh(c);
            removeObjects();
            validateNewObjectRollback(c);
        }
        finally
        {
            ids = new Object[TEST_OBJECT_COUNT];
            objs = new TestObject[TEST_OBJECT_COUNT];
        }
    }

    /**
     * Accessor for the ids of the objects
     * @return ids of the objects
     */
    public Object[] getObjectIds()
    {
        return ids;
    }

    /**
     * Accessor for the objects
     * @return the objects
     */
    public TestObject[] getObjects()
    {
        return objs;
    }

    /**
     * Asserts that the persistent fields of two test objects are equal using
     * the <i>compareTo()</i> method. The <i>equals()</i> method cannot
     * be used for this purpose because, for most persistence-capable objects
     * (including all our test widgets), it only compares JDO identity.
     * @param expected An object having the expected field values.
     * @param actual The object to compare fields against.
     * @see TestObject#compareTo
     */
    public static void assertFieldsEqual(TestObject expected, TestObject actual)
    {
        Assert.assertTrue("Incorrect field values in object, was " + actual + ", should be " + expected, actual.compareTo(expected));
    }

    public static void assertResultsEqual(Set expected, Collection results)
    {
        if (!expected.isEmpty() || !results.isEmpty())
        {
            Assert.assertTrue("Query has no expected results (test is broken)", !expected.isEmpty());
            Assert.assertTrue("Query returned no rows", !results.isEmpty());

            HashSet actual = new HashSet(results);

            Assert.assertEquals("Query returned duplicate rows", results.size(), actual.size());
            Assert.assertEquals("Query did not return expected results", expected, actual);
        }
    }

    /**
     * Method to insert a series of test objects of the specified type
     * @param c The class whose objects we create
     * @throws Exception
     */
    public void insertObjects(Class c)
    throws Exception
    {
        // Insert TEST_OBJECT_COUNT random objects
        TestHelper.LOG.info("Inserting " + TEST_OBJECT_COUNT + " " + c.getName() + " objects");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                tx.setRetainValues(true);
                tx.begin();

                TestObject obj = (TestObject) c.getDeclaredConstructor().newInstance();
                obj.fillRandom();

                objs[i] = (TestObject) obj.clone();

                assertFieldsEqual(obj, objs[i]);

                pm.makePersistent(obj);

                ids[i] = JDOHelper.getObjectId(obj);

                tx.commit();
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.insertObjects exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    protected void validateObjects(Class c)
    throws Exception
    {
        // Read them back and verify that they contain what they should
        TestHelper.LOG.info("Validating " + TEST_OBJECT_COUNT + " " + c.getName() + " objects:");
        TestHelper.LOG.info("  Normal read");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            TestObject[] loaded = new TestObject[TEST_OBJECT_COUNT];

            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                tx.begin();

                TestObject obj = (TestObject) pm.getObjectById(ids[i], true);

                assertFieldsEqual(objs[i], obj);

                loaded[i] = obj;

                tx.commit();
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.validateObjects exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }

        /*
         * Read some of them back and verify them using non-transactional reads.
         * Only some are done because non-transactional reads are much slower
         * unless connection pooling is used (eventually we should use pooling
         * when testing).
         */
        TestHelper.LOG.info("  Non-transactional read");
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();

        try
        {
            tx.setNontransactionalRead(true);

            for (int i = 0; i < TEST_OBJECT_COUNT; i += 10)
            {
                TestObject obj = (TestObject) pm.getObjectById(ids[i], false);

                assertFieldsEqual(objs[i], obj);
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.validateObjects exception thrown", e);
            throw e;
        }
        finally
        {
            pm.close();
        }

        // Read some of them back, verify them, then verify values get retained after commit when retainValues mode is on
        TestHelper.LOG.info("  Retain values mode");
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();

        try
        {
            tx.setRetainValues(true);
            tx.begin();

            TestObject[] loaded = new TestObject[TEST_OBJECT_COUNT];

            for (int i = 0; i < TEST_OBJECT_COUNT; i += 10)
            {
                TestObject obj = (TestObject) pm.getObjectById(ids[i], true);

                assertFieldsEqual(objs[i], obj);

                loaded[i] = obj;
            }

            tx.commit();

            for (int i = 0; i < TEST_OBJECT_COUNT; i += 10)
                assertFieldsEqual(objs[i], loaded[i]);
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.validateObjects exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    protected void updateObjects(Class c)
    throws Exception
    {
        // Update them all with new values
        TestHelper.LOG.info("Updating " + TEST_OBJECT_COUNT + " " + c.getName() + " objects");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            // Test basic update functionality by filling each object with new random data
            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                tx.setRetainValues(true);
                tx.begin();

                TestObject obj = (TestObject) pm.getObjectById(ids[i], false);
                obj.fillUpdateRandom();

                objs[i] = (TestObject) obj.clone();

                assertFieldsEqual(obj, objs[i]);

                tx.commit();
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.updateObjects exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    protected void iterateUsingExtent(Class c)
    throws Exception
    {
        // Iterate over them using an Extent and verify that they're all returned.
        TestHelper.LOG.info("Iterating over " + TEST_OBJECT_COUNT + " " + c.getName() + " objects with an Extent");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            Extent extent = pm.getExtent(c, true);
            Iterator ei = extent.iterator();

            try
            {
                HashSet<TestObject> returned = new HashSet<TestObject>();

                while (ei.hasNext())
                {
                    TestObject obj = (TestObject) ei.next();

                    Assert.assertTrue("Object returned twice from Extent iterator: " + obj, returned.add(obj));
                }

                Assert.assertEquals(TEST_OBJECT_COUNT, returned.size());

                for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
                {
                    TestObject obj = (TestObject) pm.getObjectById(ids[i], true);

                    Assert.assertTrue("Object never returned from Extent iterator: " + obj, returned.remove(obj));
                }
            }
            finally
            {
                extent.close(ei);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.iterateUsingExtent exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    protected void validateTransactionalRefresh(Class c)
    throws Exception
    {
        // Validate that persistent non-transactional objects transition to persistent, 
        // and refresh themselves, when accessed from within a transaction
        TestHelper.LOG.info("Validating transactional refresh on " + TEST_OBJECT_COUNT + " " + c.getName() + " objects");
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        tx1.setRetainValues(true);

        try
        {
            PersistenceManager pm2 = pmf.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();

            Random rnd = new Random(0);
            TestObject[] pobjs = new TestObject[TEST_OBJECT_COUNT];

            try
            {
                // Load all of the objects using pm1
                tx1.begin();

                for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
                {
                    // Half will be Hollow and half PersistentClean
                    boolean validate = rnd.nextBoolean();

                    pobjs[i] = (TestObject) pm1.getObjectById(ids[i], validate);

                    // Half of the PersistentClean will be fully loaded
                    if (validate && rnd.nextBoolean())
                        assertFieldsEqual(objs[i], pobjs[i]);
                }

                tx1.commit();

                for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
                {
                    Assert.assertTrue("Object is not persistent: " + ids[i], JDOHelper.isPersistent(pobjs[i]));
                    Assert.assertTrue("Object is transactional: " + ids[i], !JDOHelper.isTransactional(pobjs[i]));
                }

                // Modify them all using pm2
                tx2.begin();

                for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
                {
                    TestObject obj = (TestObject) pm2.getObjectById(ids[i], false);
                    obj.fillUpdateRandom();

                    objs[i] = (TestObject) obj.clone();

                    assertFieldsEqual(obj, objs[i]);
                }

                tx2.commit();
                if (!tx1.getOptimistic())
                {
                    // Access them all inside a transaction using pm1
                    tx1.begin();

                    for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
                    {
                        Assert.assertTrue("Object is not persistent: " + ids[i], JDOHelper.isPersistent(pobjs[i]));
                        Assert.assertTrue("Object is transactional: " + ids[i], !JDOHelper.isTransactional(pobjs[i]));

                        assertFieldsEqual(objs[i], pobjs[i]);

                        Assert.assertTrue("Object is not persistent: " + ids[i], JDOHelper.isPersistent(pobjs[i]));
                        Assert.assertTrue("Object is not transactional: " + ids[i], JDOHelper.isTransactional(pobjs[i]));
                    }

                    tx1.commit();
                }
            }
            finally
            {
                if (tx2.isActive())
                    tx2.rollback();

                pm2.close();
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.validateTransactionalRefresh exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx1.isActive())
                tx1.rollback();

            pm1.close();
        }
    }

    /**
     * Method to remove the test objects of the specified type
     * @throws Exception
     */
    public void removeObjects()
    throws Exception
    {
        // Remove all of the objects.
        TestHelper.LOG.info("Removing " + TEST_OBJECT_COUNT + " objects");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                tx.begin();

                TestObject obj = (TestObject) pm.getObjectById(ids[i], false);

                pm.deletePersistent(obj);

                tx.commit();
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.removeObjects exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    protected void validateNewObjectRollback(Class c)
    throws Exception
    {
        // Create TEST_OBJECT_COUNT random objects, update them, rollback the transaction, 
        // and verify they return to being transient objects having their former values. 
        // Requires RestoreValues == true in order to get the restoration on rollback
        TestHelper.LOG.info("Testing rollback of updates on " + TEST_OBJECT_COUNT + " new " + c.getName() + " objects");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setRestoreValues(true);

        try
        {
            TestObject[] pobjs = new TestObject[TEST_OBJECT_COUNT];

            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                objs[i] = (TestObject) c.getDeclaredConstructor().newInstance();
                objs[i].fillRandom();

                pobjs[i] = (TestObject) objs[i].clone();
            }

            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                tx.begin();

                pm.makePersistent(pobjs[i]);

                pobjs[i].fillRandom();

                tx.rollback();
            }

            for (int i = 0; i < TEST_OBJECT_COUNT; ++i)
            {
                Assert.assertNull(JDOHelper.getPersistenceManager(pobjs[i]));
                assertFieldsEqual(objs[i], pobjs[i]);
            }
        }
        catch (Exception e)
        {
            TestHelper.LOG.error("StorageTester.validateNewObjectRollback exception thrown", e);
            throw e;
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Compares two sets of TestObjects. Returns true if and only if the two
     * sets contain the same number of objects and each element of the first set
     * has a corresponding element in the second set whose fields compare equal
     * according to the compareTo() method.
     * @return <i>true</i> if the sets compare equal, <i>false</i> otherwise.
     */
    public static boolean compareSet(Set s1, Set s2)
    {
        if (s1 == null)
        {
            return s2 == null;
        }
        else if (s2 == null)
        {
            return false;
        }

        if (s1.size() != s2.size())
        {
            return false;
        }

        s2 = new HashSet(s2);

        Iterator i = s1.iterator();

        while (i.hasNext())
        {
            TestObject obj = (TestObject) i.next();

            boolean found = false;
            Iterator j = s2.iterator();

            while (j.hasNext())
            {
                if (obj.compareTo(j.next()))
                {
                    j.remove();
                    found = true;
                    break;
                }
            }

            if (!found)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two lists of TestObjects. Returns true if and only if the two
     * lists contain the same number of objects and each element of the first
     * list has a corresponding element in the second list whose fields compare
     * equal according to the compareTo() method.
     * @return <i>true</i> if the lists compare equal, <i>false</i> otherwise.
     */
    public static boolean compareList(List<TestObject> l1, List l2)
    {
        if (l1 == null)
        {
            return l2 == null;
        }
        else if (l2 == null)
        {
            return false;
        }

        if (l1.size() != l2.size())
        {
            return false;
        }

        for (int i = 0; i < l1.size(); i++)
        {
            if (!(l1.get(i)).compareTo(l2.get(i)))
            {
                return false;
            }

        }

        return true;
    }
}