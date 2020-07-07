/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.jpox.samples.models.company.Person;

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
                    Person.class,
                });
            initialised = true;
        }
    }

    /**
     * Test to check the refreshing of an object and whether the refreshed values are put in the L2 cache.
     */
    public void testRefreshWhenUpdatedExternally()
    {
        try
        {
            // Create a PM and add an object
            Object id = null; 
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(102, "George", "Bush", "george.bush@whitehouse.gov");
                pm.makePersistent(p1);
                id = pm.getObjectId(p1);

                tx.commit();
                // Person should be pinned
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup("all");
            try
            {
                tx.begin();

                Person p = (Person)pm.getObjectById(id);

                // Update a field in the datastore via different mechanism so it doesn't affect the L2 cache
                updateFieldInDatastore();

                LOG.info(">> Refreshing fields of Person");
                pm.refresh(p);

                assertEquals("George W", p.getFirstName());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception during retrieve", e);
                fail("Exception thrown while retrieving object to store in L2 cache with only few fields : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Refresh the object, so should pull in change
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                LOG.info(">> pm.getObjectById (from L2 cache?)");
                // Retrieve the object, should be taken from L2 cache
                Person p1 = (Person)pm.getObjectById(id);
                assertEquals("George W", p1.getFirstName());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving object. L2 cache not updated? : " + e.getMessage());
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
            // Clean out created data
            clean(Person.class);
        }
    }

    protected void updateFieldInDatastore()
    {
        // Update datastore by JDBC
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0);
            conn = (Connection) mconn.getConnection();

            // TODO Make sure this works on all datastores (e.g case of identifiers)
            String stmt = "UPDATE PERSON SET FIRSTNAME = 'George W' WHERE PERSONNUM = 102";
            PreparedStatement ps = conn.prepareStatement(stmt);
            int num = ps.executeUpdate();
            assertEquals("Person wasn't updated for some reason", 1, num);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception updating database", e);
            fail("Failure updating value in datastore directly. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
}