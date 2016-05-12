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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.samples.types.javatime.JavaTimeSample1;
import org.datanucleus.samples.types.javatime.JavaTimeSample2;
import org.datanucleus.samples.types.javatime.JavaTimeSample3;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for persisting Java8 Time types under JPA.
 */
public class JavaTimeTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

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

            LocalDateTime dateTime1 = LocalDateTime.of(2008, 3, 14, 15, 9, 26, 0);
            LocalDateTime dateTime2 = LocalDateTime.of(2009, 5, 13, 7, 9, 26, 0);
            try
            {
                tx.begin();
                JavaTimeSample1 s = new JavaTimeSample1(1, dateTime1, dateTime2);
                JavaTimeSample1 s2 = new JavaTimeSample1(2, null, null);
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

                JavaTimeSample1 s = em.find(JavaTimeSample1.class, 1);

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

                JavaTimeSample1 s2 = em.find(JavaTimeSample1.class, 2);

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

            // Query the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Try query with a LocalDateTime parameter
                Query q = em.createQuery("SELECT s FROM " + JavaTimeSample1.class.getName() + " s WHERE s.dateTime1 IS NOT NULL AND s.dateTime1 < :param");
                q.setParameter("param", dateTime2);
                List<JavaTimeSample1> results = q.getResultList();
                assertEquals(1,  results.size());

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
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalDate localDate1 = LocalDate.of(2008, 3, 14);
            LocalDate localDate2 = LocalDate.of(2009, 6, 16);
            try
            {
                tx.begin();
                JavaTimeSample2 s = new JavaTimeSample2(1, localDate1, localDate2);
                JavaTimeSample2 s2 = new JavaTimeSample2(2, null, null);
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

                JavaTimeSample2 s = em.find(JavaTimeSample2.class, 1);

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

                JavaTimeSample2 s2 = em.find(JavaTimeSample2.class, 2);
                
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
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();

            LocalTime localTime1 = LocalTime.of(15, 9, 26);
            LocalTime localTime2 = LocalTime.of(7, 9, 26);
            try
            {
                tx.begin();
                JavaTimeSample3 s = new JavaTimeSample3(1, localTime1, localTime2);
                JavaTimeSample3 s2 = new JavaTimeSample3(2, null, null);
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

                JavaTimeSample3 s = em.find(JavaTimeSample3.class, 1);

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

                JavaTimeSample3 s2 = em.find(JavaTimeSample3.class, 2);

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
            clean(JavaTimeSample3.class);
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

            LocalTime localTime1 = LocalTime.of(15, 9, 26);
            LocalTime localTime2 = LocalTime.of(7, 9, 26);
            try
            {
                tx.begin();
                JavaTimeSample3 s1 = new JavaTimeSample3(1, localTime1, localTime1);
                em.persist(s1);
                JavaTimeSample3 s2 = new JavaTimeSample3(2, localTime2, localTime2);
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

                Query q = em.createQuery("SELECT s FROM " + JavaTimeSample3.class.getName() +
                    " s WHERE s.localTime1 < :timeParam");
                LocalTime timeParam = LocalTime.of(11, 9, 26);
                q.setParameter("timeParam", timeParam);
                List<JavaTimeSample3> results = q.getResultList();
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
                em.close();
            }
        }
        finally
        {
            clean(JavaTimeSample3.class);
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

            LocalDate localDate1 = LocalDate.of(2001, 3, 25);
            LocalDate localDate2 = LocalDate.of(2001, 9, 26);
            try
            {
                tx.begin();
                JavaTimeSample2 s1 = new JavaTimeSample2(1, localDate1, localDate1);
                em.persist(s1);
                JavaTimeSample2 s2 = new JavaTimeSample2(2, localDate2, localDate2);
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

                Query q = em.createQuery("SELECT s FROM " + JavaTimeSample2.class.getName() +
                    " s WHERE s.localDate1 < :dateParam");
                LocalDate dateParam = LocalDate.of(2001, 6, 25);
                q.setParameter("dateParam", dateParam);
                List<JavaTimeSample2> results = (List<JavaTimeSample2>) q.getResultList();
                assertEquals("Number of results is wrong", 1, results.size());
                JavaTimeSample2 s = results.get(0);
                LocalDate ld1 = s.getLocalDate1();
                assertNotNull("Retrieved LocalDate was null!", ld1);
                assertEquals("Year was wrong", 2001, ld1.getYear());
                assertEquals("Month was wrong", Month.MARCH, ld1.getMonth());
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
            clean(JavaTimeSample2.class);
        }
    }
}