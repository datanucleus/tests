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

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.one_one.bidir.Boiler;
import org.datanucleus.samples.one_one.bidir.Timer;

/**
 * Datastore identity persistence tests for MongoDB datastores.
 */
public class DatastoreIdPersistenceTest extends JDOPersistenceTestCase
{
    Object id;

    public DatastoreIdPersistenceTest(String name)
    {
        super(name);
    }

    public void testInsertOneToOneBidirWithIdentity() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Boiler boiler = new Boiler("Vaillant", "Superwarm");
                Timer timer = new Timer("Casio", true, boiler);
                boiler.setTimer(timer);
                pm.makePersistent(timer);

                tx.commit();
            }
            catch (Throwable thr)
            {
                LOG.error("Exception during persist", thr);
                fail("Exception thrown when running test " + thr.getMessage());
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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }
}