/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.samples.types.jodatime.JodaSample1;
import org.datanucleus.samples.types.jodatime.JodaSample2;
import org.datanucleus.samples.types.jodatime.JodaSample3;
import org.datanucleus.samples.types.jodatime.JodaSample4;
import org.datanucleus.samples.types.jodatime.JodaSample5;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Tests for persisting JodaTime types under JPA.
 */
public class JodaTimeTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public JodaTimeTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    JodaSample1.class,
                    JodaSample2.class,
                    JodaSample3.class,
                    JodaSample4.class,
                    JodaSample5.class
                });
            initialised = true;
        }
    }

    /**
     * Test for DateTime persistence/retrieval.
     */
    public void testDateTime()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();

            DateTime dateTime1 = new DateTime(2008, 3, 14, 15, 9, 26, 0);
            DateTime dateTime2 = new DateTime(2009, 5, 13, 7, 9, 26, 0);
            try
            {
                tx.begin();
                JodaSample1 s = new JodaSample1(1, dateTime1, dateTime2);
                JodaSample1 s2 = new JodaSample1(2, null, null);
                em.persist(s);
                em.persist(s2);
                tx.commit();
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
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                JodaSample1 s = em.find(JodaSample1.class, 1);

                DateTime dt1 = s.getDateTime1();
                assertNotNull("Retrieved DateTime was null!", dt1);
                assertEquals("Timestamp : Year was wrong", 2008, dt1.getYear());
                assertEquals("Timestamp : Month was wrong", 3, dt1.getMonthOfYear());
                assertEquals("Timestamp : Day was wrong", 14, dt1.getDayOfMonth());
                assertEquals("Timestamp : Hour was wrong", 15, dt1.getHourOfDay());
                assertEquals("Timestamp : Minute was wrong", 9, dt1.getMinuteOfHour());
                assertEquals("Timestamp : Second was wrong", 26, dt1.getSecondOfMinute());

                DateTime dt2 = s.getDateTime2();
                assertNotNull("Retrieved DateTime was null!", dt2);
                assertEquals("String : Year was wrong", 2009, dt2.getYear());
                assertEquals("String : Month was wrong", 5, dt2.getMonthOfYear());
                assertEquals("String : Day was wrong", 13, dt2.getDayOfMonth());
                assertEquals("String : Hour was wrong", 7, dt2.getHourOfDay());
                assertEquals("String : Minute was wrong", 9, dt2.getMinuteOfHour());
                assertEquals("String : Second was wrong", 26, dt2.getSecondOfMinute());

                JodaSample1 s2 = em.find(JodaSample1.class, 2);

                assertNull(s2.getDateTime1());
                assertNull(s2.getDateTime2());

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
                em.close();
            }
        }
        finally
        {
            clean(JodaSample1.class);
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
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalDate localDate1 = new LocalDate(2008, 3, 14);
            LocalDate localDate2 = new LocalDate(2009, 6, 16);
            try
            {
                tx.begin();
                JodaSample2 s = new JodaSample2(1, localDate1, localDate2);
                JodaSample2 s2 = new JodaSample2(2, null, null);
                em.persist(s);
                em.persist(s2);
                tx.commit();
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
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                JodaSample2 s = em.find(JodaSample2.class, 1);

                LocalDate ld1 = s.getLocalDate1();
                assertNotNull("Retrieved LocalDate was null!", ld1);
                assertEquals("Year was wrong", 2008, ld1.getYear());
                assertEquals("Month was wrong", 3, ld1.getMonthOfYear());
                assertEquals("Day was wrong", 14, ld1.getDayOfMonth());

                LocalDate ld2 = s.getLocalDate2();
                assertNotNull("Retrieved LocalDate was null!", ld2);
                assertEquals("Year was wrong", 2009, ld2.getYear());
                assertEquals("Month was wrong", 6, ld2.getMonthOfYear());
                assertEquals("Day was wrong", 16, ld2.getDayOfMonth());

                JodaSample2 s2 = em.find(JodaSample2.class, 2);
                
                assertNull(s2.getLocalDate1());
                assertNull(s2.getLocalDate2());
                
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
                em.close();
            }
        }
        finally
        {
            clean(JodaSample2.class);
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
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalTime localTime1 = new LocalTime(15, 9, 26);
            LocalTime localTime2 = new LocalTime(7, 9, 26);
            try
            {
                tx.begin();
                JodaSample3 s = new JodaSample3(1, localTime1, localTime2);
                JodaSample3 s2 = new JodaSample3(2, null, null);
                em.persist(s);
                em.persist(s2);
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
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                JodaSample3 s = em.find(JodaSample3.class, 1);

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

                JodaSample3 s2 = em.find(JodaSample3.class, 2);

                assertNull(s2.getLocalTime1());
                assertNull(s2.getLocalTime2());

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
                em.close();
            }
        }
        finally
        {
            clean(JodaSample3.class);
        }
    }

    /**
     * Test for Duration persistence and retrieval.
     */
    public void testDuration()
    {
        try
        {
            // Create some data we can use for access
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            Duration duration1 = new Duration(20011986);
            Duration duration2 = new Duration(1234567890);
            try
            {
                tx.begin();
                JodaSample4 s = new JodaSample4(1, duration1, duration2);
                JodaSample4 s2 = new JodaSample4(2, null, null);
                em.persist(s);
                em.persist(s2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Duration sample", e);
                fail("Error persisting Duration sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                JodaSample4 s = em.find(JodaSample4.class, 1);

                Duration ld1 = s.getDuration1();
                assertNotNull("Retrieved Duration was null!", ld1);
                assertEquals("Duration was wrong", 20011986, ld1.getMillis());

                Duration ld2 = s.getDuration2();
                assertNotNull("Retrieved Duration was null!", ld2);
                assertEquals("Duration was wrong", 1234567890, ld2.getMillis());

                JodaSample4 s2 = em.find(JodaSample4.class, 2);

                assertNull(s2.getDuration1());
                assertNull(s2.getDuration2());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Duration data", e);
                fail("Error retrieving Duration data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(JodaSample4.class);
        }
    }

    /**
     * Test for Interval persistence and retrieval.
     */
    public void testInterval()
    {
        try
        {
            // Create some data we can use for access
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            Interval interval1 = new Interval(20011986, 20021986);
            Interval interval2 = new Interval(1234567890, 1234567891);
            try
            {
                tx.begin();
                JodaSample5 s = new JodaSample5(1, interval1, interval2);
                em.persist(s);
                JodaSample5 s2 = new JodaSample5(2, null, null);
                em.persist(s2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Interval sample", e);
                fail("Error persisting Interval sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                JodaSample5 s = em.find(JodaSample5.class, 1);

                Interval int1 = s.getInterval1();
                assertNotNull("Retrieved Interval was null!", int1);
                assertEquals("Interval start was wrong", 20011986, int1.getStartMillis());
                assertEquals("Interval end was wrong", 20021986, int1.getEndMillis());

                Interval int2 = s.getInterval2();
                assertNotNull("Retrieved Interval was null!", int2);
                assertEquals("Interval start was wrong", 1234567890, int2.getStartMillis());
                assertEquals("Interval end was wrong", 1234567891, int2.getEndMillis());

                JodaSample5 s2 = em.find(JodaSample5.class, 2);

                assertNull(s2.getInterval1());
                assertNull(s2.getInterval2());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Interval data", e);
                fail("Error retrieving Interval data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(JodaSample5.class);
        }
    }
    /**
     * Test for LocalTime persistence and retrieval.
     */
    public void testQueryLocalTime()
    {
        try
        {
            // Create some data we can use for access
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalTime localTime1 = new LocalTime(15, 9, 26);
            LocalTime localTime2 = new LocalTime(7, 9, 26);
            try
            {
                tx.begin();
                JodaSample3 s1 = new JodaSample3(1, localTime1, localTime1);
                em.persist(s1);
                JodaSample3 s2 = new JodaSample3(2, localTime2, localTime2);
                em.persist(s2);
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
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("SELECT s FROM " + JodaSample3.class.getName() +
                    " s WHERE s.localTime1 < :timeParam");
                LocalTime timeParam = new LocalTime(11, 9, 26);
                q.setParameter("timeParam", timeParam);
                List<JodaSample3> results = q.getResultList();
                assertEquals("Number of results is wrong", 1, results.size());
                JodaSample3 s = results.get(0);
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
                em.close();
            }
        }
        finally
        {
            clean(JodaSample3.class);
        }
    }

    /**
     * Test for LocalDate persistence and retrieval.
     */
    public void testQueryLocalDate()
    {
        try
        {
            // Create some data we can use for access
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalDate localDate1 = new LocalDate(2001, 3, 25);
            LocalDate localDate2 = new LocalDate(2001, 9, 26);
            try
            {
                tx.begin();
                JodaSample2 s1 = new JodaSample2(1, localDate1, localDate1);
                em.persist(s1);
                JodaSample2 s2 = new JodaSample2(2, localDate2, localDate2);
                em.persist(s2);
                tx.commit();
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
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("SELECT s FROM " + JodaSample2.class.getName() +
                    " s WHERE s.localDate1 < :dateParam");
                LocalDate dateParam = new LocalDate(2001, 6, 25);
                q.setParameter("dateParam", dateParam);
                List<JodaSample2> results = (List<JodaSample2>) q.getResultList();
                assertEquals("Number of results is wrong", 1, results.size());
                JodaSample2 s = results.get(0);
                LocalDate ld1 = s.getLocalDate1();
                assertNotNull("Retrieved LocalDate was null!", ld1);
                assertEquals("Year was wrong", 2001, ld1.getYear());
                assertEquals("Month was wrong", 3, ld1.getMonthOfYear());
                assertEquals("Day was wrong", 25, ld1.getDayOfMonth());

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
                em.close();
            }
        }
        finally
        {
            clean(JodaSample2.class);
        }
    }

    /**
     * Test for Interval persistence and retrieval.
     */
    public void testIntervalQuery()
    {
        try
        {
            // Create some data we can use for access
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            // Take 18-6-2012 0:00 as base time.
            DateTime baseTime = new DateTime(1340048501).withHourOfDay(0);

            // Test interval from 2:00 to 4:00
            Interval interval1 = new Interval(baseTime.plusHours(2), baseTime.plusHours(4));
            // Test interval from 3:00 to 5:00
            Interval interval2 = new Interval(baseTime.plusHours(3), baseTime.plusHours(5));
            // Test interval from 4:00 to 6:00
            Interval interval3 = new Interval(baseTime.plusHours(4), baseTime.plusHours(6));
            // Test interval from 5:00 to 7:00
            Interval interval4 = new Interval(baseTime.plusHours(5), baseTime.plusHours(7));
            try
            {
                tx.begin();
                JodaSample5 s1 = new JodaSample5(1, interval1, interval2);
                em.persist(s1);
                JodaSample5 s2 = new JodaSample5(2, interval3, interval4);
                em.persist(s2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Interval sample", e);
                fail("Error persisting Interval sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("SELECT s FROM " + JodaSample5.class.getName() + 
                    " s WHERE s.interval1.getStart() < :timeParam");
                DateTime timeParam = baseTime.plusHours(2).plusMinutes(30);
                q.setParameter("timeParam", timeParam);
                List<JodaSample5> results = q.getResultList();
                assertEquals("Number of results is wrong", 1, results.size());
                JodaSample5 s = results.get(0);
                Interval sit1 = s.getInterval1();
                assertNotNull("Retrieved Interval was null!", sit1);
                assertEquals("Returned wrong object", 1, s.getId());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Interval data", e);
                fail("Error retrieving Interval data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Retrieve some more data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("SELECT s FROM " + JodaSample5.class.getName() + 
                    " s WHERE s.interval1.getEnd() > :timeParam");
                DateTime timeParam = baseTime.plusHours(5);
                q.setParameter("timeParam", timeParam);
                List<JodaSample5> results = (List<JodaSample5>) q.getResultList();
                assertEquals("Number of results is wrong", 1, results.size());
                JodaSample5 s = results.get(0);
                Interval sit1 = s.getInterval1();
                assertNotNull("Retrieved Interval was null!", sit1);
                assertEquals("Returned wrong object", 2, s.getId());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Interval data", e);
                fail("Error retrieving Interval data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(JodaSample5.class);
        }
    }
}