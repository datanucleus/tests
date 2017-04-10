/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.lifecyclelistener.BasicListener;
import org.datanucleus.samples.lifecyclelistener.LifecycleListenerSpecification;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.NucleusLogger;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Developer;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Person;

/**
 * Tests for JDO2.1 PM "proxy" functionality.
 * Provides a PM that is a proxy to the real PM.
 */
public class PersistenceManagerProxyTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the TestCase. 
     */
    public PersistenceManagerProxyTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[] {
                Person.class, Manager.class, Developer.class });
        }
    }

    /**
     * Test for use of PM proxy using resource-local transactions.
     */
    public void testProxyResourceLocal() throws Exception
    {
        try
        {
            // Create a PM proxy
            PersistenceManager pm = pmf.getPersistenceManagerProxy();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager m1 = new Manager(101, "Daffy", "Duck", "daffy.duck@warnerbros.com",
                    105.45f, "123407");
                pm.makePersistent(m1);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }

            // Try to access something on the proxy to check if it is closed
            try
            {
                tx = pm.currentTransaction();
                // The proxy should have created a new delegate
            }
            catch (Exception e)
            {
                fail("Access to the PM methods after close should have worked, but failed " + e.getMessage());
            }

            try
            {
                tx.begin();
                Manager m2 = new Manager(102, "Donald", "Duck", "donald.duck@warnerbros.com",
                    105.46f, "123408");
                pm.makePersistent(m2);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }

            // PM closed so just re-get the txn
            tx = pm.currentTransaction();
            try
            {
                // Check that our objects are persisted
                tx.begin();
                Query<Manager> q = pm.newQuery(Manager.class);
                Collection<Manager> results = q.executeList();
                assertEquals("Number of persisted objects is incorrect", 2, results.size());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(Manager.class);
        }
    }

    /**
     * Test for use of PM proxy using multiple threads.
     * In this test we create a proxy in sub threads only, so don't re-use the underlying PM of the main thread.
     */
    public void testProxyInMultiThreads() throws Exception
    {
        try
        {
            // Persist some objects in main thread (without proxy)
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager m1 = new Manager(101, "Daffy", "Duck", "daffy.duck@warnerbros.com", 105.45f, "123407");
                pm.makePersistent(m1);
                Manager m2 = new Manager(102, "Donald", "Duck", "donald.duck@warnerbros.com", 105.46f, "123408");
                pm.makePersistent(m2);
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

            // Start multiple threads to persist the object
            int THREAD_SIZE = 5;
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                final int threadNo = i;
                threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        PersistenceManager pmthread = pmf.getPersistenceManagerProxy();
                        try
                        {
                            pmthread.currentTransaction().begin();
                            Manager m2 = new Manager(110 + threadNo, "Donald", "Duck", "donald.duck@warnerbros.com", 105.46f, "123408");
                            pmthread.makePersistent(m2);
                            pmthread.currentTransaction().commit();
                        }
                        catch (Exception e)
                        {
                            NucleusLogger.GENERAL.error("Exception while persisting object in thread " + threadNo, e);
                            fail("Exception thrown while accessing object in thread " + threadNo + " : " + e.getMessage());
                        }
                        finally
                        {
                            if (pmthread.currentTransaction().isActive())
                            {
                                pmthread.currentTransaction().rollback();
                            }
                            pmthread.close();
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

            // Get a new PM and check
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // Check that our objects are persisted
                tx.begin();
                Query<Manager> q = pm.newQuery(Manager.class);
                Collection<Manager> results = q.executeList();
                assertEquals("Number of persisted objects is incorrect", 7, results.size());
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
            clean(Manager.class);
        }
    }

    /**
     * Test for use of PM proxy using multiple threads.
     * In this test we create a proxy in main thread and sub threads, so re-use the underlying PM of the main thread.
     * This means that we are using a single underlying PM and so totally dependent on the Multithreaded capability of the PM. TODO enable this when we fully support it
     */
    public void xtestProxyInMultiThread() throws Exception
    {
        try
        {
            // Create a PM proxy
            PersistenceManager pm = pmf.getPersistenceManagerProxy();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager m1 = new Manager(101, "Daffy", "Duck", "daffy.duck@warnerbros.com", 105.45f, "123407");
                pm.makePersistent(m1);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }

            // Try to access something on the proxy to check if it is closed
            try
            {
                tx = pm.currentTransaction();
                // The proxy should have created a new delegate
            }
            catch (Exception e)
            {
                fail("Access to the PM methods after close should have worked, but failed " + e.getMessage());
            }

            try
            {
                tx.begin();
                Manager m2 = new Manager(102, "Donald", "Duck", "donald.duck@warnerbros.com", 105.46f, "123408");
                pm.makePersistent(m2);
                pm.flush();

                // Start multiple threads to persist the object
                int THREAD_SIZE = 5;
                Thread[] threads = new Thread[THREAD_SIZE];
                for (int i = 0; i < THREAD_SIZE; i++)
                {
                    final int threadNo = i;
                    threads[i] = new Thread(new Runnable()
                    {
                        public void run()
                        {
                            PersistenceManager pmthread = pmf.getPersistenceManagerProxy();
                            try
                            {
                                Manager m2 = new Manager(110 + threadNo, "Donald", "Duck", "donald.duck@warnerbros.com", 105.46f, "123408");
                                pmthread.makePersistent(m2);
                            }
                            catch (Exception e)
                            {
                                NucleusLogger.GENERAL.error("Exception while persisting object in thread " + threadNo, e);
                                fail("Exception thrown while accessing object in thread " + threadNo + " : " + e.getMessage());
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

                // Commit the transaction once all have completed
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }

            // PM closed so just re-get the txn
            tx = pm.currentTransaction();
            try
            {
                // Check that our objects are persisted
                tx.begin();
                Query<Manager> q = pm.newQuery(Manager.class);
                Collection<Manager> results = q.executeList();
                assertEquals("Number of persisted objects is incorrect", 7, results.size());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                // Close the proxy, which will close the underlying PM but not the proxy
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(Manager.class);
        }
    }

    /**
     * Test of lifecycle listener registered for all classes
     */
    public void testLifecycleListenerRegisteredInPMFforAllClasses()
    {
        BasicListener listener = new BasicListener(true);

        PersistenceManagerFactory pmf = getConfigurablePMF(1, null);
        pmf.addInstanceLifecycleListener(listener, null);
        freezePMF(pmf);

        try
        {
            PersistenceManager pm = pmf.getPersistenceManagerProxy();
            Transaction tx = pm.currentTransaction();
            int i = 0;
            try
            {
                tx.begin();

                // Persist an object and check the events
                Person person = new Person(12345, "Fred", "Smith", "Fred.Smith@jpox.org");
                pm.makePersistent(person);

                // Persist related objects and check the events
                // Manager has a 1-N (FK) with Department
                Manager manager = new Manager(12346, "George", "Bush", "george.bush@thewhitehouse.com", 2000000, "ABC-DEF");
                Department dept1 = new Department("Invasions");
                Department dept2 = new Department("Propaganda");
                Department dept3 = new Department("Lies");
                manager.addDepartment(dept1);
                manager.addDepartment(dept2);
                manager.addDepartment(dept3);
                dept1.setManager(manager);
                dept2.setManager(manager);
                dept3.setManager(manager);

                pm.makePersistent(manager);
                pm.flush();
                Integer[] events = listener.getRegisteredEventsAsArray();

                assertEquals("Wrong number of lifecycle events", 15, events.length);
                if (tx.getOptimistic())
                {
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Person
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3

                    int numPreStore = 0;
                    int numPostStore = 0;
                    int numEventsToProcess = i+10;
                    for (int j=i;j<numEventsToProcess;j++)
                    {
                        if (events[j].intValue() == LifecycleListenerSpecification.EVENT_PRE_STORE)
                        {
                            numPreStore++;
                        }
                        else if (events[j].intValue() == LifecycleListenerSpecification.EVENT_POST_STORE)
                        {
                            numPostStore++;
                        }
                        i++;
                    }
                    assertEquals("Number of PreStore events is wrong", 5, numPreStore); // 1 for each object
                    assertEquals("Number of PostStore events is wrong", 5, numPostStore); // 1 for each object
                }
                else
                {
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());

                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 1
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 1
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 3
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 3
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
                }

                tx.rollback();
                listener.getRegisteredEvents().clear();

                PersistenceManager pm2 = pmf.getPersistenceManager();
                Transaction tx2 = pm.currentTransaction();
                try
                {

                    tx2.begin();

                    // Persist an object and check the events
                    pm.makePersistent(person);
                    pm.flush();

                    events = listener.getRegisteredEventsAsArray();
                    i=0;
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
                    assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
                    assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());

                    tx2.rollback();
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
            catch (Exception e)
            {
                LOG.error("Exception while running lifecycle listener simple object test", e);
                fail("Exception thrown while running lifecycle listener simple object test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
                listener.getRegisteredEvents().clear();
            }
        }
        finally
        {
            CompanyHelper.clearCompanyData(pmf);
            pmf.close();
        }
    }
}