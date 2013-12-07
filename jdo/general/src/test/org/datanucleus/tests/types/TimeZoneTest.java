/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import java.util.TimeZone;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.timezone.TimeZoneHolder;

/**
 * Tests for SCO mutable type java.util.TimeZone.
 */
public class TimeZoneTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public TimeZoneTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    TimeZoneHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence and retrieval.
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            TimeZoneHolder myZone = new TimeZoneHolder();
            myZone.setTimeZoneField(TimeZone.getTimeZone("GMT"));
            Object id = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myZone);
                pm.flush();

                id = JDOHelper.getObjectId(myZone);
                TimeZoneHolder myZone2 = (TimeZoneHolder) pm.getObjectById(id, true);
                pm.refresh(myZone2);
                assertNotNull(myZone2.getTimeZoneField());
                assertEquals("GMT", myZone2.getTimeZoneField().getID());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist", e);
                fail("Exception on persist");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                TimeZoneHolder myZone2 = (TimeZoneHolder) pm.getObjectById(id, true);
                assertNotNull("TimeZone is null on retrieval", myZone2.getTimeZoneField());
                assertEquals("GMT", myZone2.getTimeZoneField().getID());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieval", e);
                fail("Exception on retrieval");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                TimeZoneHolder myZone2 = (TimeZoneHolder) pm.getObjectById(id, true);
                assertNotNull("TimeZoneHolder class had a null currency but should have had a value", myZone2.getTimeZoneField());
                myZone2.setTimeZoneField(TimeZone.getTimeZone("CET"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on update", e);
                fail("Error updating the Zone : " + e.getMessage());
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
                TimeZoneHolder myZone2 = (TimeZoneHolder) pm.getObjectById(id, true);
                assertNotNull("TimeZone is null on retrieval", myZone2.getTimeZoneField());
                assertEquals("CET", myZone2.getTimeZoneField().getID());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieval", e);
                fail("Error retrieving the TimeZone : " + e.getMessage());
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
            clean(TimeZoneHolder.class);
        }
    }

    /**
     * Test of the attach/detach process.
     * @throws Exception
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            TimeZoneHolder detachedZone = null;
            Object id = null;
            
            // Persist an object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                TimeZoneHolder zone = new TimeZoneHolder();
                zone.setTimeZoneField(TimeZone.getTimeZone("GMT"));
                pm.makePersistent(zone);
                
                detachedZone = (TimeZoneHolder)pm.detachCopy(zone);
                
                tx.commit();
                id = pm.getObjectId(zone);
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+detach", e);
                fail("Error on persist+detach : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            assertNotNull("TimeZone is null, but should have been detached", detachedZone.getTimeZoneField());
            assertEquals("TimeZone is incorrect (detached)", "GMT", detachedZone.getTimeZoneField().getID());

            // Perform an update
            detachedZone.setTimeZoneField(TimeZone.getTimeZone("EST"));

            // Attach it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                TimeZoneHolder attachedZone = (TimeZoneHolder)pm.makePersistent(detachedZone);
                assertNotNull("TimeZone is null, but should have been attached", attachedZone.getTimeZoneField());
                assertEquals("TimeZone is incorrect (attached)", "EST", attachedZone.getTimeZoneField().getID());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on attach", e);
                fail("Error on attach : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                TimeZoneHolder zone = (TimeZoneHolder)pm.getObjectById(id);
                assertNotNull("TimeZone is null, but should have been detached", zone.getTimeZoneField());
                assertEquals("TimeZone is incorrect (detached)", "EST", zone.getTimeZoneField().getID());
                detachedZone = (TimeZoneHolder)pm.detachCopy(zone);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieve+detach(2)", e);
                fail("Error on retrieve+detach(2) : " + e.getMessage());
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
            clean(TimeZoneHolder.class);
        }
    }

    public void testQuery() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                TimeZoneHolder curr = new TimeZoneHolder();
                curr.setTimeZoneField(TimeZone.getTimeZone("CET"));
                pm.makePersistent(curr);
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
                Query q = pm.newQuery(TimeZoneHolder.class, "timeZoneField == :p");
                List<TimeZoneHolder> results = (List<TimeZoneHolder>) q.execute(TimeZone.getTimeZone("CET"));
                assertEquals(1, results.size());
                TimeZoneHolder curr = results.get(0);
                assertNotNull("TimeZone field is null", curr.getTimeZoneField());
                assertEquals("TimeZone is incorrect", "CET", curr.getTimeZoneField().getID());
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
            clean(TimeZoneHolder.class);
        }
    }
}