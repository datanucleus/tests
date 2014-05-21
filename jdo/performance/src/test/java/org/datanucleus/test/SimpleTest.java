/**********************************************************************
Copyright (c) 2014 Kaarel Kann and others. All rights reserved.
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
2014 Andy Jefferson - adapted for use in generalised performance test suite
     ...
***********************************************************************/
package org.datanucleus.test;

import org.junit.*;
import javax.jdo.*;

import java.util.Random;
import java.util.concurrent.Semaphore;

import mydomain.model.*;
import org.datanucleus.util.NucleusLogger;

public class SimpleTest
{
    private Random r = new Random();

    /**
     * Test for the retrieval of objects using pm.getObjectById, in a separate ExecutionContext multithreaded.
     * This uses 2000 objects with 1 threads.
     * @throws Exception If an error occurs in the multithreading process or creating a PMF.
     */
    @Test
    public void testGetObjectByIdInExecutionContext1()
    throws Exception
    {
        performGetObjectByIdMultithreadTest(1, 2000, 200000);
    }

    /**
     * Test for the retrieval of objects using pm.getObjectById, in a separate ExecutionContext multithreaded.
     * This uses 60000 objects with 10 threads.
     * @throws Exception If an error occurs in the multithreading process or creating a PMF.
     */
    @Test
    public void testGetObjectByIdInExecutionContext2()
    throws Exception
    {
        performGetObjectByIdMultithreadTest(10, 2000, 60000);
    }

    public void performGetObjectByIdMultithreadTest(final int numThreads, final int numObjects, final int numTriesPerThread)
    throws Exception
    {
        NucleusLogger.GENERAL.info(">> test START");
        // TODO Obtain PMF using standard test mechanism
        final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("MyTest");

        // Create data
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            for (int i = 0; i < numObjects; i++)
            {
                pm.makePersistent(new User((long)i));            
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

        int threadCount = numThreads;
        final int count = numTriesPerThread;
        final Semaphore semaphore = new Semaphore(threadCount);
        semaphore.acquire(threadCount);

        // Run test
        long start = System.currentTimeMillis();
        NucleusLogger.GENERAL.debug(">> Starting threads");

        for (int i = 0; i < threadCount; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    for (int j = 0; j < count; j++) 
                    {
                        PersistenceManager pm = pmf.getPersistenceManager();
                        Transaction tx = pm.currentTransaction();
                        try
                        {
                            tx.begin();
                            for (long ctr = 0; ctr < 5; ctr++) 
                            {
                                User ro = null;
                                long id = r.nextInt(numObjects);
                                try 
                                {
                                    ro = pm.getObjectById(User.class, id);
                                } 
                                catch (Exception e) 
                                {
                                    ro = new User(id);
                                }
                                ro.getBalance();
                            }
                            tx.commit();
                        }
                        catch (Exception e)
                        {
                            NucleusLogger.GENERAL.error(">> Exception in test", e);
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
                    semaphore.release();
                }
            }).start();
        }

        semaphore.acquire(threadCount);
        System.out.println("getObjectById(objs=" + numObjects + ", threads=" + numThreads + ", triesPerThread=" + numTriesPerThread + ") time(ms)=" + (System.currentTimeMillis() - start));
        NucleusLogger.GENERAL.info(">> test END");

        pmf.close();
    }
}
