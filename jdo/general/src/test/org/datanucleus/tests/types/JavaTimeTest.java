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

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;

import org.datanucleus.samples.types.javatime.JavaTimeSample1;
import org.datanucleus.samples.types.javatime.JavaTimeSample2;
import org.datanucleus.samples.types.javatime.JavaTimeSample3;
import org.datanucleus.samples.types.javatime.JavaTimeSample4;
import org.datanucleus.samples.types.javatime.JavaTimeSample5;
import org.datanucleus.samples.types.javatime.JavaTimeSample6;
import org.datanucleus.samples.types.javatime.JavaTimeSample7;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for persistence of java.time types.
 */
public class JavaTimeTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public JavaTimeTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    JavaTimeSample1.class,
                    JavaTimeSample2.class,
                    JavaTimeSample3.class,
                    JavaTimeSample4.class,
                    JavaTimeSample5.class,
                    JavaTimeSample6.class,
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
                JavaTimeSample1 s = new JavaTimeSample1(1, dateTime1, dateTime2);
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

                JavaTimeSample1 s = (JavaTimeSample1)pm.getObjectById(id);

                LocalDateTime dt1 = s.getDateTime1();
                assertNotNull("Retrieved DateTime was null!", dt1);
                assertEquals("Timestamp : Year was wrong", 2008, dt1.getYear());
                assertEquals("Timestamp : Month was wrong", Month.MARCH, dt1.getMonth());
                assertEquals("Timestamp : Day was wrong", 14, dt1.getDayOfMonth());
                assertEquals("Timestamp : Hour was wrong", 15, dt1.getHour());
                assertEquals("Timestamp : Minute was wrong", 9, dt1.getMinute());
                assertEquals("Timestamp : Second was wrong", 26, dt1.getSecond());

                LocalDateTime dt2 = s.getDateTime2();
                assertNotNull("Retrieved DateTime was null!", dt2);
                assertEquals("String : Year was wrong", 2009, dt2.getYear());
                assertEquals("String : Month was wrong", Month.MAY, dt2.getMonth());
                assertEquals("String : Day was wrong", 13, dt2.getDayOfMonth());
                assertEquals("String : Hour was wrong", 7, dt2.getHour());
                assertEquals("String : Minute was wrong", 9, dt2.getMinute());
                assertEquals("String : Second was wrong", 26, dt2.getSecond());

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

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getYear() == 2008");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getMonthValue() == 3");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getDayOfMonth() == 14");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getHour() == 15");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getMinute() == 9");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample1> q = pm.newQuery(JavaTimeSample1.class, "dateTime1.getSecond() == 26");
                    List<JavaTimeSample1> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample1 s = results.iterator().next();
                    LocalDateTime dt = s.getDateTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error querying LocalDateTime", e);
                fail("Error querying LocalDateTime : " + e.getMessage());
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
            clean(JavaTimeSample1.class);
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
                JavaTimeSample2 s = new JavaTimeSample2(1, localDate1, localDate2);
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

                JavaTimeSample2 s = (JavaTimeSample2)pm.getObjectById(id);

                LocalDate ld1 = s.getLocalDate1();
                assertNotNull("Retrieved LocalDate was null!", ld1);
                assertEquals("Year was wrong", 2008, ld1.getYear());
                assertEquals("Month was wrong", Month.MARCH, ld1.getMonth());
                assertEquals("Day was wrong", 14, ld1.getDayOfMonth());

                LocalDate ld2 = s.getLocalDate2();
                assertNotNull("Retrieved LocalDate was null!", ld2);
                assertEquals("Year was wrong", 2009, ld2.getYear());
                assertEquals("Month was wrong", Month.JUNE, ld2.getMonth());
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

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                {
                    Query<JavaTimeSample2> q = pm.newQuery(JavaTimeSample2.class, "localDate1.getYear() == 2008");
                    List<JavaTimeSample2> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample2 s = results.iterator().next();
                    LocalDate dt = s.getLocalDate1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                }
                {
                    Query<JavaTimeSample2> q = pm.newQuery(JavaTimeSample2.class, "localDate1.getMonthValue() == 3");
                    List<JavaTimeSample2> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample2 s = results.iterator().next();
                    LocalDate dt = s.getLocalDate1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                }
                {
                    Query<JavaTimeSample2> q = pm.newQuery(JavaTimeSample2.class, "localDate1.getDayOfMonth() == 14");
                    List<JavaTimeSample2> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample2 s = results.iterator().next();
                    LocalDate dt = s.getLocalDate1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Year was wrong", 2008, dt.getYear());
                    assertEquals("Timestamp : Month was wrong", Month.MARCH, dt.getMonth());
                    assertEquals("Timestamp : Day was wrong", 14, dt.getDayOfMonth());
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error querying LocalDate", e);
                fail("Error querying LocalDate : " + e.getMessage());
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
            clean(JavaTimeSample2.class);
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
                JavaTimeSample3 s = new JavaTimeSample3(1, localTime1, localTime2);
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

                JavaTimeSample3 s = (JavaTimeSample3)pm.getObjectById(id);

                LocalTime lt1 = s.getLocalTime1();
                assertNotNull("Retrieved LocalTime was null!", lt1);
                assertEquals("Hour was wrong", 15, lt1.getHour());
                assertEquals("Minute was wrong", 9, lt1.getMinute());
                assertEquals("Second was wrong", 26, lt1.getSecond());

                LocalTime lt2 = s.getLocalTime2();
                assertNotNull("Retrieved LocalTime was null!", lt2);
                assertEquals("Hour was wrong", 7, lt2.getHour());
                assertEquals("Minute was wrong", 9, lt2.getMinute());
                assertEquals("Second was wrong", 26, lt2.getSecond());

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

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                {
                    Query<JavaTimeSample3> q = pm.newQuery(JavaTimeSample3.class, "localTime1.getHour() == 15");
                    List<JavaTimeSample3> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample3 s = results.iterator().next();
                    LocalTime dt = s.getLocalTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample3> q = pm.newQuery(JavaTimeSample3.class, "localTime1.getMinute() == 9");
                    List<JavaTimeSample3> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample3 s = results.iterator().next();
                    LocalTime dt = s.getLocalTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }
                {
                    Query<JavaTimeSample3> q = pm.newQuery(JavaTimeSample3.class, "localTime1.getSecond() == 26");
                    List<JavaTimeSample3> results = q.executeList();
                    assertNotNull(results);
                    assertEquals(1, results.size());
                    JavaTimeSample3 s = results.iterator().next();
                    LocalTime dt = s.getLocalTime1();
                    assertNotNull(dt);
                    assertEquals("Timestamp : Hour was wrong", 15, dt.getHour());
                    assertEquals("Timestamp : Minute was wrong", 9, dt.getMinute());
                    assertEquals("Timestamp : Second was wrong", 26, dt.getSecond());
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error querying LocalTime", e);
                fail("Error querying LocalTime : " + e.getMessage());
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
            clean(JavaTimeSample3.class);
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
                JavaTimeSample3 s1 = new JavaTimeSample3(1, localTime1, localTime1);
                pm.makePersistent(s1);
                JavaTimeSample3 s2 = new JavaTimeSample3(2, localTime2, localTime2);
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

                Query<JavaTimeSample3> q = pm.newQuery(JavaTimeSample3.class, "localTime1 < :timeParam");
                LocalTime timeParam = LocalTime.of(11, 9, 26);
                q.setParameters(timeParam);
                List<JavaTimeSample3> results = q.executeList();
                assertEquals("Number of results is wrong", 1, results.size());
                JavaTimeSample3 s = results.get(0);
                LocalTime lt1 = s.getLocalTime1();
                assertNotNull("Retrieved LocalTime was null!", lt1);
                assertEquals("Hour was wrong", 7, lt1.getHour());
                assertEquals("Minute was wrong", 9, lt1.getMinute());
                assertEquals("Second was wrong", 26, lt1.getSecond());

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
            clean(JavaTimeSample3.class);
        }
    }

    /**
     * Test the use of the Date.getDay(), Date.getMonth(), Date.getYear(), Time.getHour(), Time.getMinute(), Time.getSecond() methods.
     */
    public void testDateTimeMethods()
    {
        try
        {
            LocalDateTime dateTime1 = LocalDateTime.of(2008, 3, 17, 15, 9, 0, 0);
            LocalDateTime dateTime2 = LocalDateTime.of(2009, 5, 13, 7, 9, 26, 0);
            JavaTimeSample1 s1a = new JavaTimeSample1(1, dateTime1, dateTime2);

            LocalDateTime dateTime3 = LocalDateTime.of(2011, 10, 14, 1, 0, 15, 0);
            LocalDateTime dateTime4 = LocalDateTime.of(2012, 11, 1, 7, 9, 0, 0);
            JavaTimeSample1 s1b = new JavaTimeSample1(2, dateTime3, dateTime4);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id1 = null;
            Object id2 = null;
            try
            {
                tx.begin();
                pm.makePersistent(s1a);
                pm.makePersistent(s1b);
                tx.commit();
                id1 = JDOHelper.getObjectId(s1a);
                id2 = JDOHelper.getObjectId(s1b);

                tx.begin();
                try
                {
                    Query q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getDayOfMonth() == 17");
                    List results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getDayOfMonth()", 1, results.size());
                    JavaTimeSample1 first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getDayOfMonth() is incorrect", id1, JDOHelper.getObjectId(first));
                    q.closeAll();

                    q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getMonthValue() == 10");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getMonthValue", 1, results.size());
                    first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getMonth() is incorrect", id2, JDOHelper.getObjectId(first));
                    q.closeAll();

                    q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getYear() == 2008");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getYear", 1, results.size());
                    first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getYear() is incorrect", id1, JDOHelper.getObjectId(first));
                    q.closeAll();

                    // TODO sample can be persisted with timezone info resulting in H2 returning incorrect value for the test
                    /*q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getHour() == 1");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getHour", 1, results.size());
                    first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getHour() is incorrect", id2, JDOHelper.getObjectId(first));
                    q.closeAll();*/

                    q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getMinute() == 9");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getMinute", 1, results.size());
                    first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getMinute() is incorrect", id1, JDOHelper.getObjectId(first));
                    q.closeAll();

                    q = pm.newQuery(JavaTimeSample1.class);
                    q.setFilter("dateTime1.getSecond() == 0");
                    results = (List)q.execute();
                    assertEquals("Received incorrect number of results for LocalDateTime.getSecond", 1, results.size());
                    first = (JavaTimeSample1)results.iterator().next();
                    assertEquals("Retrieved object for getSecond() is incorrect", id1, JDOHelper.getObjectId(first));
                    q.closeAll();
                }
                catch (JDOUserException e)
                {
                    LOG.error("Exception during test", e);
                    fail(e.getMessage());
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
            // Clean out our data
            clean(JavaTimeSample1.class);
        }
    }

    /**
     * Test for Instant persistence and retrieval.
     */
    public void testInstant()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Instant inst1 = Instant.ofEpochMilli(1000000);
            Instant inst2 = Instant.ofEpochMilli(2000000);
            Object id = null;
            try
            {
                tx.begin();
                JavaTimeSample4 s = new JavaTimeSample4(1, inst1, inst2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Instant sample", e);
                fail("Error persisting Instant sample");
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

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaTimeSample4 s = (JavaTimeSample4)pm.getObjectById(id);

                Instant ins1 = s.getInstant1();
                assertNotNull("Retrieved Instant was null!", ins1);
                assertEquals(1000000, ins1.toEpochMilli());

                Instant ins2 = s.getInstant2();
                assertNotNull("Retrieved Instant was null!", ins2);
                assertEquals(2000000, ins2.toEpochMilli());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Instant data", e);
                fail("Error retrieving Instant data : " + e.getMessage());
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
            clean(JavaTimeSample4.class);
        }
    }

    /**
     * Test for MonthDay persistence and retrieval.
     */
    public void testMonthDay()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            MonthDay md1 = MonthDay.of(5, 20);
            MonthDay md2 = MonthDay.of(11, 1);
            Object id = null;
            try
            {
                tx.begin();
                JavaTimeSample5 s = new JavaTimeSample5(1, md1, md2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting MonthDay sample", e);
                fail("Error persisting MonthDay sample");
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

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaTimeSample5 s = (JavaTimeSample5)pm.getObjectById(id);

                MonthDay md1r = s.getMonthDay1();
                assertNotNull("Retrieved MonthDay was null!", md1r);
                assertEquals(5, md1r.getMonthValue());
                assertEquals(20, md1r.getDayOfMonth());

                MonthDay md2r = s.getMonthDay2();
                assertNotNull("Retrieved MonthDay was null!", md2r);
                assertEquals(11, md2r.getMonthValue());
                assertEquals(1, md2r.getDayOfMonth());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving MonthDay data", e);
                fail("Error retrieving MonthDay data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Query the MonthDay that was stored as a DATE
                Query<JavaTimeSample5> q1 = pm.newQuery(JavaTimeSample5.class, "monthDay1.getMonthValue() == 5");
                List<JavaTimeSample5> results1 = q1.executeList();
                assertNotNull(results1);
                assertEquals(1, results1.size());
                JavaTimeSample5 s = results1.iterator().next();
                assertEquals("MonthDay.dayOfMonth is wrong for queried monthValue", 20, s.getMonthDay1().getDayOfMonth());

                // Query the MonthDay that was stored as a DATE
                Query<JavaTimeSample5> q2 = pm.newQuery(JavaTimeSample5.class, "monthDay1.getDayOfMonth() == 20");
                List<JavaTimeSample5> results2 = q2.executeList();
                assertNotNull(results2);
                assertEquals(1, results2.size());
                s = results2.iterator().next();
                assertEquals("MonthDay.monthValue is wrong for queried dayOfMonth", 5, s.getMonthDay1().getMonthValue());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving MonthDay data", e);
                fail("Error retrieving MonthDay data : " + e.getMessage());
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
            clean(JavaTimeSample5.class);
        }
    }

    /**
     * Test for YearMonth persistence and retrieval.
     */
    public void testYearMonth()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            YearMonth ym1 = YearMonth.of(2001, 5);
            YearMonth ym2 = YearMonth.of(2006, 11);
            Object id = null;
            try
            {
                tx.begin();
                JavaTimeSample6 s = new JavaTimeSample6(1, ym1, ym2);
                pm.makePersistent(s);
                tx.commit();
                id = pm.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Error persisting YearMonth sample", e);
                fail("Error persisting YearMonth sample");
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

            // Retrieve the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                JavaTimeSample6 s = (JavaTimeSample6)pm.getObjectById(id);

                YearMonth ym1r = s.getYearMonth1();
                assertNotNull("Retrieved YearMonth was null!", ym1r);
                assertEquals(5, ym1r.getMonthValue());
                assertEquals(2001, ym1r.getYear());

                YearMonth ym2r = s.getYearMonth2();
                assertNotNull("Retrieved YearMonth was null!", ym2r);
                assertEquals(11, ym2r.getMonthValue());
                assertEquals(2006, ym2r.getYear());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving YearMonth data", e);
                fail("Error retrieving YearMonth data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Query the YearMonth that was stored as a DATE
                Query<JavaTimeSample6> q1 = pm.newQuery(JavaTimeSample6.class, "yearMonth1.getMonthValue() == 5");
                List<JavaTimeSample6> results1 = q1.executeList();
                assertNotNull(results1);
                assertEquals(1, results1.size());
                JavaTimeSample6 s = results1.iterator().next();
                assertEquals("YearMonth.dayOfMonth is wrong for queried monthValue", 2001, s.getYearMonth1().getYear());

                // Query the YearMonth that was stored as a DATE
                Query<JavaTimeSample6> q2 = pm.newQuery(JavaTimeSample6.class, "yearMonth1.getYear() == 2001");
                List<JavaTimeSample6> results2 = q2.executeList();
                assertNotNull(results2);
                assertEquals(1, results2.size());
                s = results2.iterator().next();
                assertEquals("YearMonth.monthValue is wrong for queried dayOfMonth", 5, s.getYearMonth1().getMonthValue());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving YearMonth data", e);
                fail("Error retrieving YearMonth data : " + e.getMessage());
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
            clean(JavaTimeSample6.class);
        }
    }

    /**
     * Test for Year query.
     */
    public void testYearQuery()
    {
        try
        {
            // Create some data we can use for access
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();
                JavaTimeSample7 s1 = new JavaTimeSample7(1, Year.of(1999));
                pm.makePersistent(s1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Year sample", e);
                fail("Error persisting Year sample");
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

                Query<JavaTimeSample7> q = pm.newQuery(JavaTimeSample7.class, "this.year1 == :year");
                q.setParameters(Year.of(1999));
                List<JavaTimeSample7> results = q.executeList();
                assertEquals("Number of results is wrong", 1, results.size());
                JavaTimeSample7 s = results.get(0);
                assertNotNull("Retrieved Year was null!", s.getYear1());
                assertEquals("Year was wrong", 1999, s.getYear1().getValue());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Year data", e);
                fail("Error retrieving Year data : " + e.getMessage());
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
            clean(JavaTimeSample7.class);
        }
    }
}