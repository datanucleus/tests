/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.softdelete.SDAddress;
import org.datanucleus.samples.softdelete.SDCar;
import org.datanucleus.samples.softdelete.SDPerson;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for Soft-Delete.
 */
public class SoftDeleteTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public SoftDeleteTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    SDPerson.class,
                    SDAddress.class,
                    SDCar.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for creation of Person+Address+Car and then delete Person.
     */
    public void testBasic()
    {
        if (vendorID == null)
        {
            // Ignore for non-RDBMS currently until we fix the cleanup
            return;
        }

        try
        {
            // Create some sample data including relations
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SDPerson p1 = new SDPerson(1, "George");
                SDCar c1 = new SDCar(1, "Volkswagen", "Golf");
                SDAddress a1 = new SDAddress(1, "Home");
                SDAddress a2 = new SDAddress(2, "Work");
                p1.getAddresses().add(a1);
                p1.getAddresses().add(a2);
                p1.setCar(c1);
                pm.makePersistent(p1);

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
            pmf.getDataStoreCache().evictAll();

            // Retrieve and (soft-)delete the SDPerson
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check that it is retrieved ok
                SDPerson p1 = pm.getObjectById(SDPerson.class, 1);

                // Check that a query finds it
                Query q = pm.newQuery("SELECT FROM org.datanucleus.samples.softdelete.SDPerson WHERE this.name.startsWith('Geo')");
                q.extension("datanucleus.query.compilation.cached", "false");
                List<SDPerson> results = q.executeList();
                assertNotNull(results);
                assertEquals(1, results.size());

                pm.deletePersistent(p1);

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

            // Check getObjectById and query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                try
                {
                    pm.getObjectById(SDPerson.class, 1);
                    fail("Managed to retrieve an object that was (soft-)deleted!");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                Query q = pm.newQuery("SELECT FROM org.datanucleus.samples.softdelete.SDPerson WHERE this.name.startsWith('Geo')");
                q.extension("datanucleus.query.compilation.cached", "false");
                List<SDPerson> results = q.executeList();
                assertNotNull(results);
                assertEquals(0, results.size());
LOG.info(">> Executing new query with include-soft-deletes");
                // Check query extension that includes soft-deleted objects
                Query q2 = pm.newQuery("SELECT FROM org.datanucleus.samples.softdelete.SDPerson WHERE this.name.startsWith('Geo') ");
                q2.extension("include-soft-deletes", "true");
                List<SDPerson> results2 = q2.executeList();
                assertNotNull(results2);
                assertEquals(1, results2.size());

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
            // TODO RDBMS specific! We need an API way of disabling soft-delete
            // Since these classes use soft-delete then we need to manually hard-delete them to clean them out
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_QUERY_SQL_ALLOWALL, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery("SQL", "DELETE FROM SDADDRESS");
                q1.execute();

                Query q2 = pm.newQuery("SQL", "DELETE FROM SDPERSON");
                q2.execute();

                Query q3 = pm.newQuery("SQL", "DELETE FROM SDCAR");
                q3.execute();

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
            clean(SDPerson.class);
            clean(SDCar.class);
            clean(SDAddress.class);
        }
    }
}