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
**********************************************************************/
package org.datanucleus.tests.types;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthOfYear;

import org.datanucleus.samples.types.javaxtime.JavaxtimeSample1;
import org.datanucleus.samples.types.javaxtime.JavaxtimeSample2;
import org.datanucleus.samples.types.javaxtime.JavaxtimeSample3;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for persistence of javax.time types.
 */
public class JavaxTimeTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public JavaxTimeTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    JavaxtimeSample1.class,
                    JavaxtimeSample2.class,
                    JavaxtimeSample3.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for DateTime persistence and retrieval.
     */
    public void testLocalDateTime()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            LocalDateTime dateTime1 = LocalDateTime.of(2008, 3, 14, 15, 9, 26, 0);
            LocalDateTime dateTime2 = LocalDateTime.of(2009, 5, 13, 7, 9, 26, 0);
            Object id = null;
            try
            {
                tx.begin();
                JavaxtimeSample1 s = new JavaxtimeSample1(1, dateTime1, dateTime2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting DateTime sample", e);
                fail("Error persisting DateTime sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaxtimeSample1 s = (JavaxtimeSample1)pm.getObjectById(id);

                LocalDateTime dt1 = s.getDateTime1();
                assertNotNull("Retrieved DateTime was null!", dt1);
                assertEquals("Timestamp : Year was wrong", 2008, dt1.getYear());
                assertEquals("Timestamp : Month was wrong", MonthOfYear.MARCH, dt1.getMonthOfYear());
                assertEquals("Timestamp : Day was wrong", 14, dt1.getDayOfMonth());
                assertEquals("Timestamp : Hour was wrong", 15, dt1.getHourOfDay());
                assertEquals("Timestamp : Minute was wrong", 9, dt1.getMinuteOfHour());
                assertEquals("Timestamp : Second was wrong", 26, dt1.getSecondOfMinute());

                LocalDateTime dt2 = s.getDateTime2();
                assertNotNull("Retrieved DateTime was null!", dt2);
                assertEquals("String : Year was wrong", 2009, dt2.getYear());
                assertEquals("String : Month was wrong", MonthOfYear.MAY, dt2.getMonthOfYear());
                assertEquals("String : Day was wrong", 13, dt2.getDayOfMonth());
                assertEquals("String : Hour was wrong", 7, dt2.getHourOfDay());
                assertEquals("String : Minute was wrong", 9, dt2.getMinuteOfHour());
                assertEquals("String : Second was wrong", 26, dt2.getSecondOfMinute());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving DateTime data", e);
                fail("Error retrieving DateTime data : " + e.getMessage());
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
            clean(JavaxtimeSample1.class);
        }
    }

    /**
     * Test for LocalDate persistence and retrieval.
     */
    public void testLocalDate()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            LocalDate localDate1 = LocalDate.of(2008, 3, 14);
            LocalDate localDate2 = LocalDate.of(2009, 6, 16);
            Object id = null;
            try
            {
                tx.begin();
                JavaxtimeSample2 s = new JavaxtimeSample2(1, localDate1, localDate2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting LocalDate sample", e);
                fail("Error persisting LocalDate sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaxtimeSample2 s = (JavaxtimeSample2)pm.getObjectById(id);

                LocalDate ld1 = s.getLocalDate1();
                assertNotNull("Retrieved LocalDate was null!", ld1);
                assertEquals("Year was wrong", 2008, ld1.getYear());
                assertEquals("Month was wrong", MonthOfYear.MARCH, ld1.getMonthOfYear());
                assertEquals("Day was wrong", 14, ld1.getDayOfMonth());

                LocalDate ld2 = s.getLocalDate2();
                assertNotNull("Retrieved LocalDate was null!", ld2);
                assertEquals("Year was wrong", 2009, ld2.getYear());
                assertEquals("Month was wrong", MonthOfYear.JUNE, ld2.getMonthOfYear());
                assertEquals("Day was wrong", 16, ld2.getDayOfMonth());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving LocalDate data", e);
                fail("Error retrieving LocalDate data : " + e.getMessage());
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
            clean(JavaxtimeSample2.class);
        }
    }

    /**
     * Test for LocalTime persistence and retrieval.
     */
    public void testLocalTime()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            LocalTime localTime1 = LocalTime.of(15, 9, 26);
            LocalTime localTime2 = LocalTime.of(7, 9, 26);
            Object id = null;
            try
            {
                tx.begin();
                JavaxtimeSample3 s = new JavaxtimeSample3(1, localTime1, localTime2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting LocalTime sample", e);
                fail("Error persisting LocalTime sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaxtimeSample3 s = (JavaxtimeSample3)pm.getObjectById(id);

                LocalTime lt1 = s.getLocalTime1();
                assertNotNull("Retrieved LocalTime was null!", lt1);
                assertEquals("Hour was wrong", 15, lt1.getHourOfDay());
                assertEquals("Minute was wrong", 9, lt1.getMinuteOfHour());
                assertEquals("Second was wrong", 26, lt1.getSecondOfMinute());

                LocalTime lt2 = s.getLocalTime2();
                assertNotNull("Retrieved LocalTime was null!", lt2);
                assertEquals("Hour was wrong", 7, lt2.getHourOfDay());
                assertEquals("Minute was wrong", 9, lt2.getMinuteOfHour());
                assertEquals("Second was wrong", 26, lt2.getSecondOfMinute());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving LocalTime data", e);
                fail("Error retrieving LocalTime data : " + e.getMessage());
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
            clean(JavaxtimeSample3.class);
        }
    }

    /**
     * Test for LocalTime query.
     */
    public void testLocalTimeQuery()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            LocalTime localTime1 = LocalTime.of(15, 9, 26);
            LocalTime localTime2 = LocalTime.of(7, 9, 26);
            try
            {
                tx.begin();
                JavaxtimeSample3 s1 = new JavaxtimeSample3(1, localTime1, localTime1);
                pm.makePersistent(s1);
                JavaxtimeSample3 s2 = new JavaxtimeSample3(2, localTime2, localTime2);
                pm.makePersistent(s2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting LocalTime sample", e);
                fail("Error persisting LocalTime sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the data by query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + JavaxtimeSample3.class.getName() +
                    " WHERE localTime1 < :timeParam");
                LocalTime timeParam = LocalTime.of(11, 9, 26);
                List<JavaxtimeSample3> results = (List<JavaxtimeSample3>) q.execute(timeParam);
                assertEquals("Number of results is wrong", 1, results.size());
                JavaxtimeSample3 s = results.get(0);
                LocalTime lt1 = s.getLocalTime1();
                assertNotNull("Retrieved LocalTime was null!", lt1);
                assertEquals("Hour was wrong", 7, lt1.getHourOfDay());
                assertEquals("Minute was wrong", 9, lt1.getMinuteOfHour());
                assertEquals("Second was wrong", 26, lt1.getSecondOfMinute());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving LocalTime data", e);
                fail("Error retrieving LocalTime data : " + e.getMessage());
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
            clean(JavaxtimeSample3.class);
        }
    }
}