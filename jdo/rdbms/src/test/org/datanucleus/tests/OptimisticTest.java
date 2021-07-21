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

import java.util.Date;

import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.versioned.Trade2;
import org.datanucleus.store.StoreManager;

/**
 * Test optimistic transactions specific to RDBMS.
 */
public class OptimisticTest extends JDOPersistenceTestCase
{
    public OptimisticTest(String name)
    {
        super(name);
    }

    /**
     * Test of conflicting transactions, using "date-time" strategy.
     */
    public void testBasicDateTimeStrategy()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_DATASTORE_TIME_STORES_MILLISECS))
        {
            LOG.warn("Database doesnt support storing of millisecs in DATETIME columns so ignoring the test");
            return;
        }

        addClassesToSchema(new Class[]{Trade2.class});

        PersistenceManager pm1 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();

        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx2 = pm2.currentTransaction();

        try
        {
            tx1.begin();
            Trade2 t1 = new Trade2("Mr X", 100.0, new Date());
            pm1.makePersistent(t1);
            tx1.commit();
            Object id = pm1.getObjectId(t1);

            //check conflict
            pm1.setIgnoreCache(true);
            tx1.setOptimistic(true);
            tx1.begin();
            t1 = (Trade2) pm1.getObjectById(id, true);
            t1.setPerson("Mr Y");

            pm2.setIgnoreCache(true);
            tx2.setOptimistic(true);
            tx2.begin();
            t1 = (Trade2) pm2.getObjectById(id, true);
            t1.setPerson("Mr Z");

            //commit tx1
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
            clean(Trade2.class);
        }
    }
}