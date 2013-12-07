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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.calendar.CalendarHolder;

/**
 * Tests for SCO mutable type java.util.Calendar.
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
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as 2 columns.
     * @throws Exception
     */
    public void testBasicPersistenceAsTwoColumns()
    throws Exception
    {
        try
        {
            CalendarHolder cal = new CalendarHolder();
            cal.setCal1Time(1000);
            cal.setCal1TimeZone(TimeZone.getTimeZone("CET"));
            
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
                assertEquals(1000, cal2.getCal1TimeInMillisecs());
                assertEquals("CET", cal2.getCal1TimeZone().getID());
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
                assertEquals(1000, cal2.getCal1TimeInMillisecs());
                assertEquals("CET", cal2.getCal1TimeZone().getID());
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
                assertTrue("CalendarContainer class had a null calendar but should have had a value", cal2.getCal1() != null);
                
                cal2.setCal1Time(123456);
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
                assertEquals(123456, cal2.getCal1TimeInMillisecs());
                assertEquals("CET", cal2.getCal1TimeZone().getID());
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
            
            // Check the mutability of timezone
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                assertTrue("CalendarContainer class had a null Calendar but should have had a value", cal2.getCal1() != null);
                
                cal2.setCal1TimeZone(TimeZone.getTimeZone("GMT"));
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the TimeZone of the Calendar : " + e.getMessage());
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
                assertEquals(123456, cal2.getCal1TimeInMillisecs());
                assertEquals("GMT", cal2.getCal1TimeZone().getID());
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

    /**
     * Test of the attach/detach process for an object that contains a SCO Calendar stored as 2 columns.
     * @throws Exception
     */
    public void testDetachAttachAsTwoColumns()
    throws Exception
    {
        try
        {
            CalendarHolder detachedCal = null;
            Object calId = null;

            // Persist an object containing a Point
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("calendar");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CalendarHolder cal = new CalendarHolder();
                cal.setCal1TimeZone(TimeZone.getTimeZone("GMT"));
                cal.setCal1Time(10000);
                pm.makePersistent(cal);

                detachedCal = (CalendarHolder) pm.detachCopy(cal);

                tx.commit();
                calId = pm.getObjectId(cal);
            }
            catch (Exception e)
            {
                fail("Error whilst persisting and detaching object containing SCO Calendar : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            assertTrue("Value of the java.util.Calendar that was detached is incorrect : time should have been 10000 but was " +
                detachedCal.getCal1TimeInMillisecs(), detachedCal.getCal1TimeInMillisecs() == 10000);
            assertTrue("Value of the java.util.Calendar that was detached is incorrect : timezone should have been GMT but was " +
                detachedCal.getCal1TimeZone().getID(), detachedCal.getCal1TimeZone().getID().equals("GMT"));

            // Perform an update to the contents of the Calendar
            long detachedTime = 123456;
            String attachedTimeZone = "CET";
            detachedCal.setCal1Time(detachedTime);

            // Attach the Calendar
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("calendar");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CalendarHolder attachedCal = (CalendarHolder) pm.makePersistent(detachedCal);

                // Update the contents of the Point now attached (test that it
                // uses SCO wrappers)
                attachedCal.setCal1TimeZone(TimeZone.getTimeZone(attachedTimeZone));

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO Calendar : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("calendar");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CalendarHolder cal = (CalendarHolder) pm.getObjectById(calId);

                assertTrue("Value of the java.util.Calendar that was retrieved is incorrect : time should have been " + 
                    detachedTime + " but was " + cal.getCal1TimeInMillisecs(), cal.getCal1TimeInMillisecs() == detachedTime);
                assertTrue(
                    "Value of the java.util.Calendar that was retrieved is incorrect : timezone should have been " +
                    attachedTimeZone + " but was " + cal.getCal1TimeZone().getID(), cal.getCal1TimeZone().getID().equals(attachedTimeZone));

                detachedCal = (CalendarHolder) pm.detachCopy(cal);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO Calendar : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Replace the calendar
            GregorianCalendar newCal = new GregorianCalendar(TimeZone.getTimeZone("PST"));
            newCal.setTimeInMillis(444444);
            detachedCal.setCal1(newCal);

            // Attach the calendar
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("calendar");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detachedCal);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO Calendar : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("point");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CalendarHolder cal = (CalendarHolder) pm.getObjectById(calId);

                assertTrue(
                    "Value of the java.util.Calendar that was retrieved is incorrect : time should have been 444444 but was " +
                    cal.getCal1TimeInMillisecs(), cal.getCal1TimeInMillisecs() == 444444);
                assertTrue("Value of the java.util.Calendar that was retrieved is incorrect : timezone should have been PST but was " +
                    cal.getCal1TimeZone().getID(), cal.getCal1TimeZone().getID().equals("PST"));

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO Calendar : " + e.getMessage());
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

    /**
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as 1 column with null values.
     * @throws Exception
     */
    public void testNullAsOneColumn()
    throws Exception
    {
        try
        {
            CalendarHolder cal = new CalendarHolder();
            cal.setCal2Time(1000);
            cal.setCal2TimeZone(TimeZone.getTimeZone("CET"));

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

            // Check null calendar
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                assertTrue("CalendarContainer class had a null Calendar but should have had a value", 
                    cal2.getCal2() != null);
                cal2.setCal2(null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error setting null Calendar : " + e.getMessage());
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
                assertTrue("CalendarContainer class had a not null Calendar but should have no value", 
                    cal2.getCal2() == null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error retrieving null Calendar : " + e.getMessage());
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

    /**
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as 1 column with set values.
     * @throws Exception
     */
    public void testPersistAndQueryAsOneColumn()
    throws Exception
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1000);
        cal.setTimeZone(TimeZone.getTimeZone("CET"));
        try
        {
            Object id = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CalendarHolder holder = new CalendarHolder();
                holder.setCal2Time(1000);
                holder.setCal2TimeZone(TimeZone.getTimeZone("CET"));
                pm.makePersistent(holder);
                id = JDOHelper.getObjectId(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting Calendar holder", e);
                fail("Error persisting Calendar : " + e.getMessage());
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

            // Query it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery("SELECT FROM " + CalendarHolder.class.getName() +
                    " WHERE cal2 == :cal");
                List<CalendarHolder> results = (List<CalendarHolder>)q.execute(cal);
                assertEquals("Number of holders returned by query was incorrect", 1, results.size());
                CalendarHolder holder = results.iterator().next();
                assertEquals("Returned holder is incorrect", id, JDOHelper.getObjectId(holder));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception querying Calendar holder", e);
                fail("Error querying Calendar : " + e.getMessage());
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

    /**
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as 2 column with null values.
     * @throws Exception
     */
    public void testNullAsTwoColumn()
    throws Exception
    {
        try
        {
            CalendarHolder cal = new CalendarHolder();
            cal.setCal1Time(1000);
            cal.setCal1TimeZone(TimeZone.getTimeZone("CET"));

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
                assertEquals(1000, cal2.getCal1TimeInMillisecs());
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

            // Check null calendar
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CalendarHolder cal2 = (CalendarHolder) pm.getObjectById(id, true);
                assertTrue("CalendarContainer class had a null Calendar but should have had a value", 
                    cal2.getCal1() != null);
                cal2.setCal1(null);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error setting null Calendar : " + e.getMessage());
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
                assertTrue("CalendarContainer class had a not null Calendar but should have no value", 
                    cal2.getCal1() == null);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception retrieving null Calendar", e);
                fail("Error retrieving null Calendar : " + e.getMessage());
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

    /**
     * Test for the persistence and retrieval of a Calendar mutable SCO type stored as two columns.
     * See JIRA "NUCRDBMS-16"
     */
    public void testQueryCalendarAsTwoColumns()
    throws Exception
    {
        try
        {
            CalendarHolder cal = new CalendarHolder();
            cal.setCal1Time(1000);
            cal.setCal1TimeZone(TimeZone.getTimeZone("CET"));
            
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
                assertEquals(1000, cal2.getCal1TimeInMillisecs());
                assertEquals("CET", cal2.getCal1TimeZone().getID());
                Query q = pm.newQuery(CalendarHolder.class);
                q.setOrdering("cal1 ascending");
                q.execute();
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown in test", e);
                fail("Unexpected exception thrown while querying Calendar : " + e.getMessage());
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