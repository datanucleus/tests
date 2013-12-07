/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.datanucleus.samples.versioned.VersionEmptyDFG;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.versioned.Trade1;
import org.jpox.samples.versioned.Trade1Holder;
import org.jpox.samples.versioned.Trade3;
import org.jpox.samples.versioned.Trade4;
import org.jpox.samples.versioned.Trade4Sub;
import org.jpox.samples.versioned.Trade5Base;
import org.jpox.samples.versioned.Trade5Sub;
import org.jpox.samples.versioned.Trade5SubSub;
import org.jpox.samples.versioned.Trade6;
import org.jpox.samples.versioned.Trade6Holder;
import org.jpox.samples.versioned.Trade7Base;
import org.jpox.samples.versioned.Trade7Sub;

/**
 * Test optimistic transactions.
 */
public class OptimisticTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    int status;

    public OptimisticTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{Trade1.class, Trade3.class, Trade4.class, Trade4Sub.class});
            initialised = true;
        }
    }

    /**
     * Test of conflicting transactions, using "version-number" strategy.
     */
    public void testBasicVersionNumberStrategy()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            tx1.begin();
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            pm1.makePersistent(t1);
            pm1.flush();
            assertNotNull("Object just persisted using makePersistent doesnt have a version!", JDOHelper.getVersion(t1));

            tx1.commit();
            Object id = pm1.getObjectId(t1);

            // retrieve the object in PM1/txn1
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            Trade1 t1a = (Trade1) pm1.getObjectById(id, true);

            // retrieve the object in PM2/txn2 (without the change from txn1 presumably)
            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            Trade1 t1b = (Trade1) pm2.getObjectById(id, true);

            // update the object in PM1/txn1
            t1a.setPerson("Mr Y");

            // commit txn1 with the change
            tx1.commit();

            // Update in txn2
            t1b.setPerson("Mr Z");

            boolean success = false;
            try
            {
                // commit txn2 with the change - should throw exception since it has been updated in txn1 first since the read of the object
                tx2.commit();
            }
            catch (JDOOptimisticVerificationException ove)
            {
                success = true;
            }
            assertTrue("JDOOptimisticVerificationException expected", success);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test of using "none" strategy.
     * This should do no actual optimistic checking, although it will update the version column
     */
    public void testBasicNoneStrategy()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            tx1.begin();
            Trade3 t1 = new Trade3("Mr X", 100.0, new Date());
            pm1.makePersistent(t1);
            tx1.commit();
            Object id = pm1.getObjectId(t1);

            // Check that the version is 1 (JPOX-specific behaviour since JDO2 doesnt define it)
            tx1.begin();
            t1 = (Trade3)pm1.getObjectById(id);
            assertEquals("Version of unversioned object is incorrect after persist", 
                new Long(1), JDOHelper.getVersion(t1));
            tx1.commit();

            //check conflict between transactions
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            t1 = (Trade3) pm1.getObjectById(id, true);
            t1.setPerson("Mr Y");

            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            t1 = (Trade3) pm2.getObjectById(id, true);
            t1.setPerson("Mr Z");

            //commit tx1
            tx1.commit();

            // Check that the version is 2 (JPOX-specific behaviour since JDO2 doesnt define it)
            tx1.begin();
            t1 = (Trade3)pm1.getObjectById(id);
            assertEquals("Version of unversioned object is incorrect after update",
                new Long(2), JDOHelper.getVersion(t1));
            tx1.commit();

            boolean success = false;
            try
            {
                tx2.commit();
            }
            catch (JDOOptimisticVerificationException ove)
            {
                success = true;
            }
            assertFalse("JDOOptimisticVerificationException not expected", success);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade3.class);
        }
    }

    /**
     * Test of conflicting transactions, using "version-number" strategy where the class has a version field.
     */
    public void testBasicVersionNumberStrategyVersionField()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            tx1.begin();
            Trade4 tradeA = new Trade4("Mr X", 100.0, new Date());
            pm1.makePersistent(tradeA);
            pm1.flush();
            assertEquals("Object just persisted using makePersistent has incorrect version!", 1, tradeA.getVersion());

            tx1.commit();
            Object id = pm1.getObjectId(tradeA);

            // retrieve the object in PM1/txn1
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            Trade4 tradeB = (Trade4) pm1.getObjectById(id, true);

            // retrieve the object in PM2/txn2 (without the change from txn1 presumably)
            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            Trade4 tradeC = (Trade4) pm2.getObjectById(id, true);

            // update the object in PM1/txn1
            tradeB.setPerson("Mr Y");

            // commit txn1 with the change
            tx1.commit();

            // Update in txn2
            tradeC.setPerson("Mr Z");

            boolean success = false;
            try
            {
                // commit txn2 with the change - should throw exception since it has been updated in txn1 first since the read of the object
                tx2.commit();
            }
            catch (JDOOptimisticVerificationException ove)
            {
                success = true;
            }
            assertTrue("JDOOptimisticVerificationException expected", success);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade4.class);
        }
    }

    /**
     * Test of conflicting transactions, using "version-number" strategy where the class has a version field.
     */
    public void testBasicVersionNumberStrategyVersionFieldForSubclass()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            tx1.begin();

            Trade4Sub tradeA = new Trade4Sub("Mr S", 200.0, new Date());
            tradeA.setSubName("SubName-1");
            pm1.makePersistent(tradeA);
            pm1.flush();
            assertEquals("Trade4Sub just persisted using makePersistent has incorrect version!", 1, tradeA.getVersion());

            tx1.commit();
            pmf.getDataStoreCache().evictAll();
            Object id = pm1.getObjectId(tradeA);

            // retrieve the object in PM1/txn1
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            Trade4Sub tradeB = (Trade4Sub) pm1.getObjectById(id, true);

            // retrieve the object in PM2/txn2 (without the change from txn1 presumably)
            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            Trade4Sub tradeC = (Trade4Sub) pm2.getObjectById(id, true);

            // update the object in PM1/txn1
            tradeB.setSubName("SubName-2");

            // commit txn1 with the change
            tx1.commit();
            assertEquals("Version of Trade4Sub is incorrect", 2l, JDOHelper.getVersion(tradeB));

            // Update in txn2
            tradeC.setSubName("SubName-3");

            boolean success = false;
            try
            {
                // commit txn2 with the change - should throw exception since it has been updated in txn1 first since the read of the object
                tx2.commit();
            }
            catch (JDOOptimisticVerificationException ove)
            {
                success = true;
            }
            assertTrue("JDOOptimisticVerificationException expected", success);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade4Sub.class);
        }
    }

    /**
     * tests the creation and update of version/timestamp optimistic columns
     * outside an optimistic transaction
     */
    public void testCreationUpdateVersionColumns()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            //check insert
            tx.begin();
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            pm.makePersistent(t1);
            tx.commit();
            Object id = pm.getObjectId(t1);

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(1), JDOHelper.getVersion(t1));
            tx.commit();

            //check update
            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr Y");
            tx.commit();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(2), JDOHelper.getVersion(t1));
            tx.commit();

            //check update with rollback
            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr Z");
            //this flushes the changes to the database; the column version is incremented
            pm.flush();
            assertEquals(new Long(3), JDOHelper.getVersion(t1));
            //changes are rolled back; the column version keeps the old value
            tx.rollback();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(2), JDOHelper.getVersion(t1));
            tx.commit();

            //---------------------
            //check update + update
            //---------------------
            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr A");
            tx.commit();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(3), JDOHelper.getVersion(t1));
            tx.commit();

            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr B");
            tx.commit();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(4), JDOHelper.getVersion(t1));
            tx.commit();

            //---------------------
            //check update + update with instance in cache
            //---------------------

            tx.begin();
            t1.setPerson("Mr C");
            tx.commit();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(5), JDOHelper.getVersion(t1));
            tx.commit();

            tx.begin();
            t1.setPerson("Mr D");
            tx.commit();

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(6), JDOHelper.getVersion(t1));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test delete after update
     */
    public void testDeleteAfterUpdate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        try
        {
            // Insert
            tx.begin();
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            pm.makePersistent(t1);
            tx.commit();
            Object id = pm.getObjectId(t1);

            tx.begin();
            t1 = (Trade1)pm.getObjectById(id);
            assertEquals(new Long(1), JDOHelper.getVersion(t1));
            tx.commit();

            // Delete - what the test is for
            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr Y");
            pm.flush();
            pm.deletePersistent(t1);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test two succeeding updates in the same transaction, with the
     * first updating the version in the datastore before the second is made and committed
     */
    public void testMultipleUpdates()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            Trade1 t1 = new Trade1("Mr Smith", 1400.0, new Date());
            pm.makePersistent(t1);
            tx.commit();
            Object id = pm.getObjectId(t1);

            //check conflict
            tx.setOptimistic(true);
            tx.begin();
            t1 = (Trade1) pm.getObjectById(id, true);
            t1.setPerson("Mr Jones");
            pm.flush();
            t1.setPerson("Mr Green");
            pm.flush();
            t1.setPerson("Mr Arbuthnot");
            tx.commit();
            
            // not seeing a JDOOptimisticVerificationException is the success here 
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test of conflicting transactions, using "version-number" strategy, when the
     * object is retrieved via a query.
     */
    public void testConflictTransactionsVersionNumberFromQuery()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            // Persist the object
            tx1.begin();
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            pm1.makePersistent(t1);
            tx1.commit();

            // retrieve the object
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            Query q1 = pm1.newQuery(Trade1.class, "person == \"Mr X\"");
            List results = (List)q1.execute();
            t1 = (Trade1)results.get(0);
            assertNotNull("Optimistic version is null", JDOHelper.getVersion(t1));
            t1.setPerson("Mr Y");

            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            Query q2 = pm2.newQuery(Trade1.class, "person == \"Mr X\"");
            results = (List)q2.execute();
            t1 = (Trade1)results.get(0);
            assertNotNull("Optimistic version is null", JDOHelper.getVersion(t1));
            t1.setPerson("Mr Z");

            // commit tx1
            tx1.commit();

            boolean success = false;
            try
            {
                tx2.commit();
            }
            catch (JDOOptimisticVerificationException ove)
            {
                success = true;
            }
            assertTrue("JDOOptimisticVerificationException expected", success);
            assertFalse("transaction should be rolledback", tx2.isActive());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of conflictTransactions via query: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test of an optimistic query.
     * The query connection will be closed just after the query, so this tests
     * the access to the elements even though there is no active connection.
     */
    public void testOptimisticQuery()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();
        try
        {
            // Create some data
            tx1.begin();

            Trade1 t1 = new Trade1("Tony Blair", 200.0, new Date());
            pm1.makePersistent(t1);

            Trade1 t2 = new Trade1("Osama Bin Laden", 50000.0, new Date());
            pm1.makePersistent(t2);

            Trade1 t3 = new Trade1("Jacques Chirac", 40000.0, new Date());
            pm1.makePersistent(t3);
            
            tx1.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("optimisticQueryTest failed while creating its reference data : " + e.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
        }

        // Perform an optimistic query on this data
        PersistenceManager pm2 = pmf.getPersistenceManager();
        pm2.setIgnoreCache(true);
        Transaction tx2 = pm2.currentTransaction();
        tx2.setOptimistic(true);
        try
        {
            tx2.begin();

            Query q = pm2.newQuery(pm2.getExtent(Trade1.class, false));
            List results = (List)q.execute();
            Iterator resultsIter = results.iterator();
            while (resultsIter.hasNext())
            {
                resultsIter.next();
            }

            tx2.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("optimisticQueryTest failed while executing the query : " + e.getMessage());
        }
        finally
        {
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            clean(Trade1.class);
        }
    }

    /**
     * Test pc-new objects transiting to pc-dirty in optimistic tx.
     */
    public void testPCnewToPCdirty()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        try
        {
            tx1.setOptimistic(true);

            //---
            // test 1
            //---
            tx1.begin();
            Trade1 t1 = new Trade1("Mr X pc-new", 100.0, new Date());
            pm1.makePersistent(t1);
            pm1.flush();
            pm1.getObjectId(t1);
            t1.setPerson("Mr X pc-dirty");
            tx1.commit();

            //---
            // test 2
            //---
            tx1.begin();
            Trade1 t2 = new Trade1("Mr Y pc-new", 200.0, new Date());
            pm1.makePersistent(t2);
            tx1.commit();
            Object id2 = pm1.getObjectId(t2);

            tx1.begin();
            Trade1 t2b = (Trade1) pm1.getObjectById(id2);
            t2b.setPerson("Mr Y pc-dirty");
            tx1.commit();

            tx1.begin();
            t2b.setPerson("Mr Y pc-dirty-1");
            tx1.commit();

            //---
            // test 3
            //---
            tx1.begin();
            Trade1 t3 = new Trade1("Mr Z pc-new", 300.0, new Date());
            pm1.makePersistent(t3);
            pm1.flush();
            pm1.getObjectId(t3);
            tx1.commit();

            tx1.begin();
            t3.setPerson("Mr Z pc-dirty");
            tx1.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * tests the use of versions and attach/detach.
     */
    public void testDetachAttach()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            // create some data
            tx.begin();
            Trade1 t1 = new Trade1("Mr X", 250.0, new Date());
            pm.makePersistent(t1);
            tx.commit();

            // retrieve the trade
            tx.setOptimistic(true);
            tx.begin();
            Query query = pm.newQuery(Trade1.class);
            t1 = (Trade1)((Collection)query.execute()).iterator().next();
            Long version = (Long)JDOHelper.getVersion(t1);
            Trade1 detachedTrade1 = (Trade1)pm.detachCopy(t1);
            Long detachedVersion = (Long)JDOHelper.getVersion(detachedTrade1);
            assertEquals("Version of object and its detached form are not the same!", version, detachedVersion);
            tx.commit();

            // modify detached trade
            detachedTrade1.setPerson("Mr Y");

            // attach the trade
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction(); 
            tx.setOptimistic(true);
            tx.begin();
            pm.makePersistent(detachedTrade1);
            tx.commit();

            // retrieve the object and check the version
            tx.setOptimistic(true);
            tx.begin();
            query = pm.newQuery(Trade1.class);
            t1 = (Trade1)((Collection)query.execute()).iterator().next();
            Long latestVersion = (Long)JDOHelper.getVersion(t1);
            assertEquals("Version of attached object has incorrect version", new Long(detachedVersion.longValue()+1), latestVersion);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test the use of refresh() on an object that has been updated in another PM.
     * It should update the value in the object so it is up to date.
     */
    public void testRefreshOfOptimisticObjects()
    {
        try
        {
            Object id1 = null;

            // Create some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Trade1 t1 = new Trade1("Fred Smith", 100.00, new Date());
                pm.makePersistent(t1);

                tx.commit();
                id1 = pm.getObjectId(t1);
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
            PersistenceManager pm2 = pmf.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            try
            {
                tx1.setOptimistic(true);
                tx2.setOptimistic(true);
                tx1.begin();
                tx2.begin();

                // Retrieve the data (in both PMs)
                Trade1 t1pm1 = (Trade1) pm1.getObjectById(id1);
                Trade1 t1pm2 = (Trade1) pm2.getObjectById(id1);

                // Update it in pm1
                t1pm1.setPerson("Fred Smithy");
                tx1.commit();

                // refresh t1pm2 so it's up to date
                pm2.refresh(t1pm2);
                assertEquals("Name of person in other pm has just been refreshed and name is incorrect", 
                    t1pm2.getPerson(), "Fred Smithy");

                tx2.commit();
            }
            catch (JDOOptimisticVerificationException e)
            {
                fail("Exception thrown while refreshing optimistic object : " + e.getMessage());
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                pm1.close();
                pm2.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Tests the persistence of data using a 1-N join table relation with both classes using optimistic txns.
     */
    public void testOptimisticJoinTableRelation()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);

        try
        {
            tx.begin();
            Trade1Holder block = new Trade1Holder("Bearings");
            pm.makePersistent(block);
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            block.addTrade(t1);
            Trade1 t2 = new Trade1("Mr Y", 500.0, new Date());
            block.addTrade(t2);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1Holder.class);
            clean(Trade1.class);
        }
    }

    /**
     * Test of conflicting transactions with use of SCO collection clear() method.
     * Could also use remove() method on collection for same result.
     * Tests that SCO methods are caught by the optimistic process.
     */
    public void testSCOContainerClear()
    {
        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();
        pm1.setIgnoreCache(true);
        tx1.setOptimistic(true);

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();
        pm2.setIgnoreCache(true);
        tx2.setOptimistic(true);

        try
        {
            Object id = null;

            // Persist the object
            tx1.begin();
            Trade1Holder block = new Trade1Holder("First block");
            Trade1 t1 = new Trade1("Tony Blair", 200.00, new Date());
            block.addTrade(t1);
            pm1.makePersistent(block);
            tx1.commit();
            id = pm1.getObjectId(block);

            // TXN1 : retrieve the object and update a field
            tx1.begin();
            Trade1Holder block1 = (Trade1Holder)pm1.getObjectById(id);
            block1.setName("First block modified");

            // TXN2 : retrieve the object
            tx2.begin();
            Trade1Holder block2 = (Trade1Holder)pm2.getObjectById(id);

            // TXN1 : commit the change
            tx1.commit();

            // TXN2 : clear the collection of trades
            // This should NOT throw exceptions (particularly not OptimisticVerificationExceptions)
            block2.clearTrades();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of SCO collection clear when should have left it until commit: " + ex.getMessage());
        }
        finally
        {
            if (tx1.isActive())
            {
                tx1.rollback();
            }
            pm1.close();
            if (tx2.isActive())
            {
                tx2.rollback();
            }
            pm2.close();

            // Clean out our data
            clean(Trade1Holder.class);
            clean(Trade1.class);
        }
    }

    public void testOptimisticRollingBack()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.setOptimistic(true);
        boolean success = false;
        status = -1;
        try
        {
            tx.begin();
            tx.setSynchronization(new Synchronization()
            {
                public void beforeCompletion()
                {
                    throw new UnsupportedOperationException("before");
                }

                public void afterCompletion(int arg0)
                {
                    status = arg0;
                    throw new UnsupportedOperationException("after");
                }
            });
            tx.commit();
        }
        catch (UnsupportedOperationException ex)
        {
            success = true;
        }
        finally
        {
            try
            {
                assertTrue(success);
                assertFalse(tx.isActive());
                assertEquals(Status.STATUS_ROLLEDBACK, status);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
        }
        success = false;
        try
        {
            tx.begin();
            tx.setSynchronization(new Synchronization()
            {
                public void beforeCompletion()
                {
                    throw new UnsupportedOperationException("before");
                }

                public void afterCompletion(int arg0)
                {
                    status = arg0;
                    throw new UnsupportedOperationException("after");
                }
            });
            tx.rollback();
        }
        catch (UnsupportedOperationException ex)
        {
            success = true;
        }
        finally
        {
            try
            {
                assertTrue(success);
                assertFalse(tx.isActive());
                assertEquals(Status.STATUS_ROLLEDBACK, status);
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

    /**
     * Test whether we can find an object after persist but before flush, using its id.
     * This seems to be totally JPOX-specific and not in the JDO spec.
     * If this behaviour is in the JDO spec mention where this is defined.
     */
    public void testRetrieveAfterPersistBeforeFlush()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            try
            {
                tx.begin();
                Trade1 t = new Trade1("Mr X", 100.0, new Date());
                pm.makePersistent(t);
                pm.flush();

                Object id = pm.getObjectId(t);
                Object obj = pm.getObjectById(id);
                assertNotNull("getObjectById returned null object even though just persisted", obj);

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of retrieve after persist before flush: " + ex.getMessage());
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
            clean(Trade1.class);
        }
    }

    /**
     * Test the handling of inheritance with versioned records.
     * This doesnt actually test optimistic checking, just the fact that optimistic versions are catered
     * for being specified at different levels of the inheritance.
     */
    public void testInheritance()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            Object id = null;
            try
            {
                tx.begin();
                Trade5Base t = new Trade5SubSub(1, "Mr Banker", 123.45, new java.util.Date());
                pm.makePersistent(t);

                tx.commit();
                id = pm.getObjectId(t);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance and versioning: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the name
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);

            try
            {
                tx.begin();
                Trade5SubSub t = (Trade5SubSub)pm.getObjectById(id);
                assertNotNull("Object retrieved via pm.getObjectById has null version!", JDOHelper.getVersion(t));

                // Update a field
                t.setName("My trade");

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance and versioning: " + ex.getMessage());
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
            clean(Trade5SubSub.class);
            clean(Trade5Sub.class);
            clean(Trade5Base.class);
        }
    }

    /**
     * Test the handling of inheritance with versioned records using a query instead of getObjectById.
     * This doesnt actually test optimistic checking, just the fact that optimistic versions are catered
     * for being specified at different levels of the inheritance.
     */
    public void testInheritanceUsingQuery()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            try
            {
                tx.begin();
                Trade5SubSub t = new Trade5SubSub(1, "Mr Banker", 123.45, new java.util.Date());
                t.setName("Urgent Trade #20");
                pm.makePersistent(t);

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance, queries and versioning: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the name
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);

            try
            {
                tx.begin();
                Query q = pm.newQuery(Trade5SubSub.class, "name == 'Urgent Trade #20'");
                q.setUnique(true);
                Trade5SubSub t = (Trade5SubSub)q.execute();
                assertNotNull("Object retrieved via query has null version!", JDOHelper.getVersion(t));

                // Update a field
                t.setName("My trade");

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance, queries and versioning: " + ex.getMessage());
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
            clean(Trade5SubSub.class);
            clean(Trade5Sub.class);
            clean(Trade5Base.class);
        }
    }

    /**
     * Test the handling of inheritance with versions storing in a field.
     */
    public void testInheritanceWithVersionField()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            Object id = null;
            try
            {
                tx.begin();
                Trade7Sub t = new Trade7Sub(1, "Mr Banker", 123.45, new java.util.Date());
                t.setSubValue("First Value");
                pm.makePersistent(t);

                tx.commit();
                id = pm.getObjectId(t);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance and versioning: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the subValue
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);

            try
            {
                tx.begin();
                Trade7Sub t = (Trade7Sub)pm.getObjectById(id);
                assertNotNull("Object retrieved via pm.getObjectById has null version!", JDOHelper.getVersion(t));

                // Update a field
                t.setSubValue("Second Value");

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance and versioning: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the subValue
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();
                Trade7Sub t = (Trade7Sub)pm.getObjectById(id);
                assertNotNull("Object retrieved via pm.getObjectById has null version!", JDOHelper.getVersion(t));
                assertEquals("Second Value", t.getSubValue());

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of inheritance and versioning: " + ex.getMessage());
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
            clean(Trade7Sub.class);
            clean(Trade7Base.class);
        }
    }

    /**
     * Test of detachAllOnCommit and that the version number is stored when detaching.
     */
    public void testDetachAllOnCommitVersionNumber()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            Trade1 trade = null;
            try
            {
                tx.begin();
                trade = new Trade1("Mr X", 100.0, new Date());
                pm.makePersistent(trade);
                pm.flush();

                // Make sure version is set after flushing
                assertNotNull("Object just persisted using makePersistent doesnt have a version!", 
                    JDOHelper.getVersion(trade));

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Exception thrown while persisting/detaching object", e);
                fail("Exception thrown while persisting/detaching " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the object
            assertEquals("Detached object has incorrect version", new Long(1), JDOHelper.getVersion(trade));
            trade.setPerson("Mr Y");

            // Attach the object
            pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();
                trade = (Trade1)pm.makePersistent(trade);
                pm.flush();

                assertEquals("Object version has not been updated correctly on attach", 
                    new Long(2), JDOHelper.getVersion(trade));
                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the object
            assertEquals("Detached object has incorrect version", new Long(2), JDOHelper.getVersion(trade));  
            trade.setPerson("Mr Z");

            // Attach the object
            pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();
                trade = (Trade1)pm.makePersistent(trade);
                pm.flush();

                assertEquals("Object version has not been updated correctly on attach", 
                    new Long(3), JDOHelper.getVersion(trade));
                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
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
            clean(Trade1.class);
        }
    }

    /**
     * Test of detachCopy and that the version number is stored when detaching.
     */
    public void testDetachCopyVersionNumber()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);

            Trade1 trade = null;
            try
            {
                tx.begin();
                trade = new Trade1("Mr X", 100.0, new Date());
                pm.makePersistent(trade);
                pm.flush();

                // Make sure version is set after flushing
                assertNotNull("Object just persisted using makePersistent doesnt have a version!", 
                    JDOHelper.getVersion(trade));

                // Detach a copy
                trade = (Trade1)pm.detachCopy(trade);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Exception thrown while persisting/detaching object", e);
                fail("Exception thrown while persisting/detaching " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the object
            assertEquals("Detached object has incorrect version", new Long(1), JDOHelper.getVersion(trade));
            trade.setPerson("Mr Y");

            // Attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();
                trade = (Trade1)pm.makePersistent(trade);
                pm.flush();

                // Make sure version is set after flushing
                assertEquals("Object version has not been updated correctly on attach", 
                    new Long(2), JDOHelper.getVersion(trade));

                // Detach a copy
                trade = (Trade1)pm.detachCopy(trade);
                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the object
            assertEquals("Detached object has incorrect version", new Long(2), JDOHelper.getVersion(trade));
            trade.setPerson("Mr Z");

            // Attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();
                trade = (Trade1)pm.makePersistent(trade);
                pm.flush();

                // Make sure version is set after flushing
                assertEquals("Object version has not been updated correctly on attach", 
                    new Long(3), JDOHelper.getVersion(trade));

                tx.commit();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                fail("Exception thrown during test of conflictTransactions: " + ex.getMessage());
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
            clean(Trade1.class);
        }
    }

    /**
     * Test of use of autoincrement ids with optimistic transactions.
     * Autoincrement ids are generated in the datastore and so will call flush() from the outset.
     */
    public void testOptimisticWithIdentityFieldObjects()
    throws Exception
    {
        if (!storeMgr.supportsValueStrategy("identity"))
        {
            // Lets just say it passed
            return;
        }

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(true);
            try
            {
                tx.begin();

                Trade6Holder t6a = new Trade6Holder("NASDAQ 1");
                Trade6Holder t6b = new Trade6Holder("FTSE 2");
                Trade6Holder t6c = new Trade6Holder("CAC 5");
                pm.makePersistent(t6a);
                pm.makePersistent(t6b);
                pm.makePersistent(t6c);

                HashSet c = new HashSet();
                c.add(new Trade6("Donald Duck", 123.45, new Date()));
                c.add(new Trade6("Mickey Mouse", 234.5, new Date()));
                t6a.setTrades(c);

                c = new HashSet();
                c.add(new Trade6("Yogi Bear", 2300.0, new Date()));
                c.add(new Trade6("Minnie Mouse", 1.0, new Date()));
                t6b.setTrades(c);

                c = new HashSet();
                c.add(new Trade6("Barney Rubble", 1245.0, new Date()));
                c.add(new Trade6("Fred Flintstone", 2.0, new Date()));
                t6c.setTrades(c);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Exception thrown during test of autoincrement with flushed objects");
                LOG.error(e);
                fail("Exception thrown during test of autoincrement with flushed objects : " + e.getMessage());
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
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(Trade6.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Trade6 tr6 = (Trade6)iter.next();
                    if (tr6.getHolder() != null)
                    {
                        Trade6Holder holder = tr6.getHolder();
                        tr6.setHolder(null);
                        holder.setTrades(null);
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
            }
            clean(Trade6Holder.class);
            clean(Trade6.class);
        }
    }

    public void testVersionResetOnRollback()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
            pm.makePersistent(t1);
            pm.flush();
            assertNotNull("Object just persisted using makePersistent doesnt have a version!", JDOHelper.getVersion(t1));

            tx.commit();
            Object id = pm.getObjectId(t1);
            assertEquals("Version is incorrect", new Long(1), JDOHelper.getVersion(t1));
            pmf.getDataStoreCache().evictAll();

            // retrieve the object in PM1/txn1
            pm.setIgnoreCache(true);
            tx.setOptimistic(true);
            tx.begin();
            Trade1 t1a = (Trade1) pm.getObjectById(id, true);

            // Update the object and flush to the datastore
            t1a.setPerson("Mr Y");
            pm.flush();
            assertEquals("Version is incorrect", new Long(2), JDOHelper.getVersion(t1a));

            // rollback the transaction
            tx.rollback();

            assertEquals("Version is incorrect", new Long(1), JDOHelper.getVersion(t1a));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown during test of rollback effect on version: " + ex.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Trade1.class);
        }
    }

    /**
     * Test of putting object into L2 cache via a query, and retrieving making sure the version is still set.
     */
    public void testVersionFromLevel2Cache()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Trade1 t1 = new Trade1("Mr X", 100.0, new Date());
                pm.makePersistent(t1);
                pm.flush();
                assertNotNull("Object just persisted using makePersistent doesnt have a version!", JDOHelper.getVersion(t1));

                tx.commit();
                id = pm.getObjectId(t1);
                assertEquals("Version is incorrect", new Long(1), JDOHelper.getVersion(t1));
            }
            catch (Exception e)
            {
                LOG.error("Exception in persist", e);
                fail("Exception persisting objects");
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
            pmf.getDataStoreCache().evictAll();
            pmf.getDataStoreCache().pinAll(true, Trade1.class);
            try
            {
                // retrieve using query (puts object in L2 cache)
                tx.begin();
                Query q = pm.newQuery(Trade1.class);
                Iterator<Trade1> iter = ((List)q.execute()).iterator();
                while (iter.hasNext())
                {
                    Trade1 t1 = iter.next();
                    assertEquals("Version is incorrect", new Long(1), JDOHelper.getVersion(t1));
                }
                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during query", ex);
                fail("Exception thrown during query of object: " + ex.getMessage());
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
                // retrieve using id (gets object from L2 cache)
                tx.begin();
                Trade1 t1 = (Trade1)pm.getObjectById(id);
                assertEquals("Version is incorrect", new Long(1), JDOHelper.getVersion(t1));
                tx.commit();
            }
            catch (Exception ex)
            {
                LOG.error("Exception during retrieval by id", ex);
                fail("Exception thrown during retrieval by id: " + ex.getMessage());
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
            clean(Trade1.class);
        }
    }

    /**
     * Test of versioning when the class has no fields in the DFG (datastore id, and surrogate version).
     */
    public void testVersionWithEmptyDFG()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                VersionEmptyDFG ve1 = new VersionEmptyDFG();
                ve1.setData(String.valueOf(System.currentTimeMillis()).getBytes());
                pm.makePersistent(ve1);
                id = pm.getObjectId(ve1);
                tx.commit();
                pm.close();

                pmf.getDataStoreCache().evictAll(); // Make sure not in L2 cache
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();

                // Retrieve the object and check the version
                VersionEmptyDFG ve2 = (VersionEmptyDFG)pm.getObjectById(id, true);
                assertEquals(new Long(1), JDOHelper.getVersion(ve2));

                // Update the object
                ve2.setData(String.valueOf(System.currentTimeMillis()).getBytes());
                tx.commit();
                pm.close();

                pmf.getDataStoreCache().evictAll(); // Make sure not in L2 cache
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();

                // Retrieve the object and check the version was incremented on updating
                VersionEmptyDFG ve3 = (VersionEmptyDFG)pm.getObjectById(id, true);
                assertEquals(new Long(2), JDOHelper.getVersion(ve3));

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
            // Clean out our data
            clean(VersionEmptyDFG.class);
        }
    }
}