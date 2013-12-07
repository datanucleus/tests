/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.util.Calendar;
import java.util.TimeZone;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.calendar.CalendarHolder;

/**
 * Tests for SCO mutable type java.util.Calendar.
 * Tests specific to RDBMS
 */
public class CalendarTest  extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public CalendarTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    org.jpox.samples.types.calendar.CalendarHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as 1 column.
     * @throws Exception
     */
    public void testBasicPersistenceAsOneColumn()
    throws Exception
    {
        try
        {
            CalendarHolder cal = new CalendarHolder();
            cal.setCal2Time(1000);
            cal.setCal2TimeZone(TimeZone.getTimeZone("CET"));
            int calMilli = cal.getCal2().get(Calendar.MILLISECOND);
            int calSec = cal.getCal2().get(Calendar.SECOND);
            int calMin = cal.getCal2().get(Calendar.MINUTE);
            int calHour = cal.getCal2().get(Calendar.HOUR_OF_DAY);
            int calDay = cal.getCal2().get(Calendar.DAY_OF_MONTH);
            int calMonth = cal.getCal2().get(Calendar.MONTH);
            int calYear = cal.getCal2().get(Calendar.YEAR);

            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(cal);
                id = JDOHelper.getObjectId(cal);

                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                pm.refresh(cal2);
                assertEquals(1000, cal2.getCal2TimeInMillisecs());
                assertEquals(calMilli, cal2.getCal2().get(Calendar.MILLISECOND));
                assertEquals(calSec, cal2.getCal2().get(Calendar.SECOND));
                assertEquals(calMin, cal2.getCal2().get(Calendar.MINUTE));
                assertEquals(calHour, cal2.getCal2().get(Calendar.HOUR_OF_DAY));
                assertEquals(calDay, cal2.getCal2().get(Calendar.DAY_OF_MONTH));
                assertEquals(calMonth, cal2.getCal2().get(Calendar.MONTH));
                assertEquals(calYear, cal2.getCal2().get(Calendar.YEAR));
                // Dont compare timezone since we only have 1 column
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
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                assertEquals(1000, cal2.getCal2TimeInMillisecs());
                assertEquals(calMilli, cal2.getCal2().get(Calendar.MILLISECOND));
                assertEquals(calSec, cal2.getCal2().get(Calendar.SECOND));
                assertEquals(calMin, cal2.getCal2().get(Calendar.MINUTE));
                assertEquals(calHour, cal2.getCal2().get(Calendar.HOUR_OF_DAY));
                assertEquals(calDay, cal2.getCal2().get(Calendar.DAY_OF_MONTH));
                assertEquals(calMonth, cal2.getCal2().get(Calendar.MONTH));
                assertEquals(calYear, cal2.getCal2().get(Calendar.YEAR));
                // Dont compare timezone since we only have 1 column
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
            
            // Check the mutability of time
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                assertTrue("CalendarContainer class had a null calendar but should have had a value",
                    cal2.getCal2() != null);
                cal2.setCal2Time(123456);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the time of the Calendar : " + e.getMessage());
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
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                if (storeMgr.getClass().getName().equals("org.datanucleus.store.rdbms.RDBMSStoreManager"))
                {
                    DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
                    if (dba.supportsOption(org.datanucleus.store.rdbms.adapter.DatastoreAdapter.DATETIME_STORES_MILLISECS))
                    {
                        assertEquals(123456, cal2.getCal2TimeInMillisecs());
                    }
                    else
                    {
                        // Millisecs not stored so just check hours/mins/secs
                        assertEquals(123000, cal2.getCal2TimeInMillisecs());
                    }
                }
                else
                {
                    assertEquals(123456, cal2.getCal2TimeInMillisecs());
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
        }
        finally
        {
            clean(CalendarHolder.class);
        }
    }


}