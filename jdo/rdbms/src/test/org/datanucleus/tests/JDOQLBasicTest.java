/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Office;

/**
 * Tests for JDOQL basic operations specific for RDBMS.
 */
public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLBasicTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Office.class,
                });
            initialised = true;
        }        
    }

    /**
     * Tests the Analsys.rollup() expression
     */
    public void testAnalysisRollup()
    {
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        if (!srm.getDatastoreAdapter().supportsOption(DatastoreAdapter.ANALYSIS_METHODS))
        {
            // Not supported so it passed :-)
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist some simple objects (uses datastore id, or composite application id depending on suite)
            tx.begin();
            Office o1 = new Office(1, "Green", "Big spacious office");
            Calendar cal1 = Calendar.getInstance();
            cal1.set(2004, 1, 1);
            o1.setDate(cal1.getTime());
            Office o2 = new Office(2, "Blue", "Pokey office at the back of the building");
            Calendar cal2 = Calendar.getInstance();
            cal2.set(2005, 1, 1);
            o2.setDate(cal2.getTime());
            Office o3 = new Office(1, "Yellow", "Massive open plan office");
            Calendar cal3 = Calendar.getInstance();
            cal3.set(2005, 1, 1);
            o3.setDate(cal3.getTime());
            pm.newQuery(Office.class).deletePersistentAll();
            pm.makePersistent(o1);
            pm.makePersistent(o2);
            pm.makePersistent(o3);
            tx.commit();
            Object[] officeIds = new Object[3];
            officeIds[0] = pm.getObjectId(o1);
            officeIds[1] = pm.getObjectId(o2);
            officeIds[2] = pm.getObjectId(o3);

            tx.begin();
            Query q = pm.newQuery(Office.class);
            q.setGrouping("Analysis.rollup({roomName})");
            q.setOrdering("roomName ascending");
            q.setResult("roomName,count(floor)");
            
            Collection c = (Collection) q.execute();
            assertEquals(4, c.size());
            Iterator it = c.iterator();
            Object[] obj = ((Object[])it.next());
            assertEquals("Blue", obj[0]);
            assertEquals(1, ((Long)obj[1]).longValue());
            obj = ((Object[])it.next());
            assertEquals(1, ((Long)obj[1]).longValue());
            assertEquals("Green", obj[0]);
            obj = ((Object[])it.next());
            assertEquals(1, ((Long)obj[1]).longValue());
            assertEquals("Yellow", obj[0]);
            assertEquals(1, ((Long)obj[1]).longValue());
            obj = ((Object[])it.next());
            assertNull(obj[0]);
            assertEquals(3, ((Long)obj[1]).longValue());

            q = pm.newQuery(Office.class);
            q.setGrouping("Analysis.rollup({date})");
            q.setOrdering("date ascending");
            q.setResult("date,count(floor)");
            
            c = (Collection) q.execute();
            assertEquals(3, c.size());

            tx.commit();

        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            clean(Office.class);
        }
    }
}