/**********************************************************************
Copyright (c) 2014 "pica" and others. All rights reserved.
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

import javax.jdo.*;
import java.util.concurrent.Semaphore;
import org.datanucleus.PropertyNames;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Office;
import org.junit.Assert;
import org.junit.Test;

public class SerializableIsolationLevelTest extends JDOPersistenceTestCase
{
    private static final String DESC = "desc";

    private static final String DPT = "dpt 1";

    private static boolean initialised = false;

    private Object oid;

    private static final String ROOM = "room";

    public SerializableIsolationLevelTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{Office.class, Department.class,});
            initialised = true;
        }
    }

    @Test
    public void testSerializable() throws InterruptedException
    {
        final Modifier modifier;
        final Checker checker;
        _init();
        Semaphore[] sems = {new Semaphore(0), new Semaphore(0)};
        modifier = new Modifier(pmf, oid, sems);
        checker = new Checker(pmf, oid, sems);
        Thread ts[] = {new Thread(modifier), new Thread(checker)};
        ts[0].start();
        ts[1].start();
        for (int i = 0; i < ts.length; i++)
        {
            ts[i].join(500);
        }
        if (checker.err != null)
        {
            throw checker.err;
        }
        if (modifier.err != null)
        {
            throw modifier.err;
        }
    }

    // create sample data
    private void _init()
    {
        Transaction tx = null;
        try
        {
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            tx = pm.currentTransaction();
            tx.begin();
            Office o = new Office(1L, ROOM, DESC);
            o.addDepartment(new Department(DPT));
            o = pm.makePersistent(o);
            oid = pm.getObjectId(o);
            tx.commit();
        }
        finally
        {
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
    private static class Modifier implements Runnable
    {
        private final PersistenceManagerFactory pmf;

        private final Object oid;

        private final Semaphore[] sems;

        protected volatile AssertionError err;

        public Modifier(PersistenceManagerFactory pmf, Object oid, Semaphore[] sems)
        {
            this.pmf = pmf;
            this.oid = oid;
            this.sems = sems;
        }

        @Override
        public void run()
        {
            final PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            final Transaction tx = pm.currentTransaction();
            tx.setIsolationLevel(Constants.TX_SERIALIZABLE);
            try
            {
                LOG.info("waiting for checker thread " + sems[0].availablePermits());
                // wait until other thread/tx has started
                sems[0].acquire();
                tx.begin();
                final Office o = (Office) pm.getObjectById(oid);
                o.setDescription(o.getDescription() + o.getRoomName());
                o.addDepartment(new Department("dept 2"));
                tx.commit();
                // let the other thread proceed
                sems[1].release();
                LOG.info("commited modification");
            }
            catch (AssertionError e)
            {
                err = e;
            }
            catch (InterruptedException e)
            {
                err = new AssertionError(e);
                Assert.assertFalse(e.getMessage(), true);
            }
            catch (JDODataStoreException e)
            {
                LOG.info("expected datastore timeout", e);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
                sems[1].release();
            }
        }
    }
    private static class Checker implements Runnable
    {
        private final PersistenceManagerFactory pmf;

        private final Object oid;

        private final Semaphore[] sems;

        protected volatile AssertionError err;

        public Checker(final PersistenceManagerFactory pmf, final Object oid, Semaphore[] sems)
        {
            this.pmf = pmf;
            this.oid = oid;
            this.sems = sems;
        }

        @Override
        public void run()
        {
            Office o;
            final PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            final Transaction tx = pm.currentTransaction();
            tx.setIsolationLevel(Constants.TX_SERIALIZABLE);
            try
            {
                tx.begin();
                o = (Office) pm.getObjectById(oid);
                // allow modification to proceed now that we've read from the datastore
                sems[0].release();
                LOG.info("within check tx waiting for mod " + sems[1].availablePermits());
                // wait until other thread has committed its transaction
                sems[1].acquire();
                o = (Office) pm.getObjectById(oid);
                Assert.assertEquals("MUST NOT see department added in other SERIALIZABLE transaction.", 1, o.getDepartments().size());
                tx.commit();
            }
            catch (InterruptedException e)
            {
                err = new AssertionError(e);
                Assert.assertFalse(e.getMessage(), true);
            }
            catch (AssertionError e)
            {
                err = e;
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
                sems[0].release();
            }
        }
    }
}