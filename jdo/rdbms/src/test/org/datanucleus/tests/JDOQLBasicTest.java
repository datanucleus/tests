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
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.models.company.Office;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

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
     * Tests the SQL_rollup() expression.
     */
    public void testSqlRollup()
    {
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        if (srm.getDatastoreAdapter().getSQLMethodClass(null, "SQL_rollup", null) == null)
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

            // TODO This throws an exception about "JDOQL query has result clause PrimaryExpression{roomName} but this is invalid (see JDO spec 14.6.10). When specified with grouping should be aggregate, or grouping expression"
            tx.begin();
            Query q = pm.newQuery(Office.class);
            q.setGrouping("SQL_rollup({roomName})");
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
            q.setGrouping("SQL_rollup({date})");
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

    /**
     * Tests the extension for location of NULLS within an ORDER BY clause.
     */
    public void testOrderNullsFirstLast()
    {
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        if (!srm.getDatastoreAdapter().supportsOption(DatastoreAdapter.ORDERBY_NULLS_DIRECTIVES))
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

            for (int i=0;i<3;i++)
            {
                Office off = new Office(i, "Colour" + i, null);
                pm.makePersistent(off);
            }
            Office o1 = new Office(3, "Green", "Big spacious office");
            Office o2 = new Office(4, "Blue", "Pokey office at the back of the building");
            Office o3 = new Office(5, "Yellow", "Massive open plan office");
            pm.makePersistent(o1);
            pm.makePersistent(o2);
            pm.makePersistent(o3);
            tx.commit();

            tx.begin();

            Query q = pm.newQuery("SELECT FROM " + Office.class.getName() + " ORDER BY description NULLS FIRST");
            List<Office> offices = (List<Office>) q.execute();
            assertEquals(6, offices.size());
            Iterator<Office> it = offices.iterator();
            assertNull("First result (NULLS FIRST) should have null description", it.next().getDescription());
            assertNull("Second result (NULLS FIRST) should have null description", it.next().getDescription());
            assertNull("Third result (NULLS FIRST) should have null description", it.next().getDescription());
            assertNotNull("Fourth result (NULLS FIRST) should have not null description", it.next().getDescription());
            assertNotNull("Fifth result (NULLS FIRST) should have not null description", it.next().getDescription());
            assertNotNull("Sixth result (NULLS FIRST) should have not null description", it.next().getDescription());

            q = pm.newQuery("SELECT FROM " + Office.class.getName() + " ORDER BY description NULLS LAST");
            offices = (List<Office>) q.execute();
            assertEquals(6, offices.size());
            it = offices.iterator();
            assertNotNull("First result (NULLS FIRST) should have not null description", it.next().getDescription());
            assertNotNull("Second result (NULLS FIRST) should have not null description", it.next().getDescription());
            assertNotNull("Third result (NULLS FIRST) should have not null description", it.next().getDescription());
            assertNull("Fourth result (NULLS FIRST) should have null description", it.next().getDescription());
            assertNull("Fifth result (NULLS FIRST) should have null description", it.next().getDescription());
            assertNull("Sixth result (NULLS FIRST) should have null description", it.next().getDescription());

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