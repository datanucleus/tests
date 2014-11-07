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

import javax.jdo.Constants;
import javax.jdo.JDODataStoreException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import org.datanucleus.PropertyNames;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Office;

public class ReadUncommittedIsolationLevelTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    private static final String ROOM = "room";

    public ReadUncommittedIsolationLevelTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]{Office.class,});
            initialised = true;
        }
    }

    public void testReadUncommited()
    {
        if (!pmf.supportedOptions().contains("javax.jdo.option.TransactionIsolationLevel.read-uncommitted"))
        {
            // Datastore doesn't support this isolation level
            return;
        }

        // Add some initial data
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
        Transaction tx = pm.currentTransaction();
        Object oid;
        try
        {
            tx.begin();
            // create sample data
            Office o = new Office(1L, ROOM, "desc");
            o = pm.makePersistent(o);
            oid = pm.getObjectId(o);
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

        String finalDescription = null;
        PersistenceManager pm1 = pmf.getPersistenceManager();
        PersistenceManager pm2 = pmf.getPersistenceManager();
        Transaction tx1 = pm1.currentTransaction();
        Transaction tx2 = pm2.currentTransaction();
        try
        {
            pm1.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            tx1.setIsolationLevel(Constants.TX_READ_UNCOMMITTED);
            tx1.begin();

            pm2.setProperty(PropertyNames.PROPERTY_CACHE_L2_TYPE, "none");
            tx2.setIsolationLevel(Constants.TX_READ_UNCOMMITTED);
            tx2.begin();

            Office o1 = (Office) pm1.getObjectById(oid);

            LOG.info("within tx1 after modifying:" + o1.asString());
            finalDescription = o1.getDescription() + o1.getRoomName();
            o1.setDescription(finalDescription);
            // send UPDATE to database
            pm1.flush();

            LOG.info("within tx1 after modifying:" + o1.asString());
            Office o2 = (Office) pm2.getObjectById(oid);
            LOG.info("within tx2: " + o2.asString());
            assertEquals("uncommited modification not seen", finalDescription, o2.getDescription());
        }
        catch (JDODataStoreException e)
        {
            assertFalse("Should be able to see description " + finalDescription + " but " + e.getMessage(), true);
        }
        finally
        {
            tx2.commit();
            pm2.close();
            tx1.commit();
            pm1.close();

            clean(Office.class);
            clean(Department.class);
        }
    }
}