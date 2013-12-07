/**********************************************************************
Copyright (c) 2004 Ralf Ulrich and others.
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
2004 Andy Jefferson - set useUpdateLock
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.concurrency.Account;
import org.datanucleus.samples.concurrency.Transfer;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for concurrent operations operating correctly and in the correct order.
 */
public class ConcurrencyTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ConcurrencyTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Account.class, Transfer.class
                });
        }
    }

    /**
     * Method to clean up the data after the test.
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        clean(Account.class);
        clean(Transfer.class);
    }

    public void testBasicConcurrency()
    {
        // Persist Accounts and Transfers
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_SERIALIZE_READ, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(new Account("alice", 1000));
            pm.makePersistent(new Account("berta", 0));
            pm.makePersistent(new Account("charly", 0));
            pm.makePersistent(new Transfer("alice", "berta", 100));
            pm.makePersistent(new Transfer("alice", "charly", 200));
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

        // Process the Transfers, one per Thread, and one PM per Thread
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_SERIALIZE_READ, "true");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LinkedList threads = new LinkedList();
            Extent ext = pm.getExtent(Transfer.class, true);
            Iterator iter = ext.iterator();
            while (iter.hasNext())
            {
                Transfer t = (Transfer) iter.next();
                threads.add(startConcurrentTransfer(JDOHelper.getObjectId(t)));
            }
            tx.commit();

            while (!threads.isEmpty())
            {
                Thread td = (Thread)threads.removeFirst();
                try
                {
                    td.join();
                }
                catch (InterruptedException e)
                {
                }
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

        // Check the results
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_SERIALIZE_READ, "true");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent ext = pm.getExtent(Transfer.class, true);
            Iterator iter = ext.iterator();
            while (iter.hasNext())
            {
                Transfer transfer = (Transfer) iter.next();
                assertTrue(transfer.isBooked());
            }

            ext = pm.getExtent(Account.class, true);
            iter = ext.iterator();
            while (iter.hasNext())
            {
                Account acct = (Account) iter.next();
                String name = acct.getName();
                if ("alice".equals(name))
                {
                    assertEquals(700, acct.getSaldo());
                }
                else if ("berta".equals(name))
                {
                    assertEquals(100, acct.getSaldo());
                }
                else if ("charly".equals(name))
                {
                    assertEquals(200, acct.getSaldo());
                }
                else
                {
                    assertFalse("unexpected account name: " + name, true);
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
    }

    private Thread startConcurrentTransfer(final Object transferId)
    {
        // Perform the transfer in its own PM
        final PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_SERIALIZE_READ, "true");

        final Object lock = new Object();
        Thread td = new Thread()
        {
            public void run()
            {
                LOG.info(">> Starting thread " + transferId + " in pm=" + pm);
                synchronized (lock)
                {
                    lock.notifyAll();
                }

                pm.currentTransaction().begin();
                Transfer t = (Transfer) pm.getObjectById(transferId, true);

                performTransfer(t);
                LOG.info(">> Completed thread " + transferId + " in pm=" + pm);
            }
        };

        synchronized (lock)
        {
            td.start();
            try
            {
                lock.wait();
            }
            catch (InterruptedException e)
            {
            }
        }
        return td;
    }

    public void performTransfer(Transfer t)
    {
        PersistenceManager pm = JDOHelper.getPersistenceManager(t);
        pm.setProperty(PropertyNames.PROPERTY_SERIALIZE_READ, "true");
        Transaction tx = pm.currentTransaction();
        int amount = t.getAmount();
        try
        {
            // Retrieve from and to Accounts using this PM - should lock the objects in the database
            Account from = getAccountByName(pm, t.getFromAccount());
            Account to = getAccountByName(pm, t.getToAccount());

            int fromSaldo = from.getSaldo() - amount;
            from.setSaldo(fromSaldo);
            pm.flush();
            try
            {
                // make sure the other transaction comes here concurrently
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }

            // Update "to" account
            int toSaldo = to.getSaldo() + amount;
            to.setSaldo(toSaldo);

            // Update Transfer as "booked"
            t.setBooked(true);

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

    private Account getAccountByName(PersistenceManager pm, String acct)
    {
        Query query = pm.newQuery("SELECT FROM " + Account.class.getName() + " WHERE name == :acct");
        Collection result = (Collection) query.execute(acct);
        Iterator iter = result.iterator();
        Account account = (Account) (iter.hasNext() ? iter.next() : null);
        query.closeAll();
        return account;
    }
}