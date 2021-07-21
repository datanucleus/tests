/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.datanucleus.samples.types.calendar.CalendarHolder;
import org.datanucleus.tests.JakartaPersistenceTestCase;

/**
 * Tests for persisting Calendar types under JPA.
 */
public class CalendarTest extends JakartaPersistenceTestCase
{
    private static boolean initialised = false;

    public CalendarTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    CalendarHolder.class,
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

            try
            {
                tx.begin();
                CalendarHolder holder1 = new CalendarHolder();
                holder1.setId(1);
                Calendar cal1 = new GregorianCalendar();
                cal1.set(Calendar.YEAR, 1995);
                cal1.set(Calendar.MONTH, 3);
                cal1.set(Calendar.DAY_OF_MONTH, 6);
                cal1.set(Calendar.HOUR_OF_DAY, 5);
                cal1.set(Calendar.MINUTE, 25);
                cal1.set(Calendar.SECOND, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                holder1.setCal1(cal1);

                Calendar cal2 = new GregorianCalendar();
                cal2.set(Calendar.YEAR, 2000);
                cal2.set(Calendar.MONTH, 2);
                cal2.set(Calendar.DAY_OF_MONTH, 10);
                cal2.set(Calendar.HOUR_OF_DAY, 5);
                cal2.set(Calendar.MINUTE, 0);
                cal2.set(Calendar.SECOND, 0);
                cal2.set(Calendar.MILLISECOND, 0);
                holder1.setCal2(cal2);
                em.persist(holder1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting Calendar sample", e);
                fail("Error persisting Calendar sample");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            emf.getCache().evictAll();

            // Retrieve the data
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                CalendarHolder h1 = em.find(CalendarHolder.class, 1);

                Calendar cal1 = h1.getCal1();
                assertNotNull("Retrieved Calendar1 was null!", cal1);
                assertEquals("Timestamp : Year was wrong", 1995, cal1.get(Calendar.YEAR));
                assertEquals("Timestamp : Month was wrong", 3, cal1.get(Calendar.MONTH));
                assertEquals("Timestamp : Day was wrong", 6, cal1.get(Calendar.DAY_OF_MONTH));
                assertEquals("Timestamp : Hour was wrong", 5, cal1.get(Calendar.HOUR_OF_DAY));
                assertEquals("Timestamp : Minute was wrong", 25, cal1.get(Calendar.MINUTE));
                assertEquals("Timestamp : Second was wrong", 0, cal1.get(Calendar.SECOND));

                Calendar cal2 = h1.getCal2();
                assertNotNull("Retrieved Calendar2 was null!", cal2);
                assertEquals("Timestamp : Year was wrong", 2000, cal2.get(Calendar.YEAR));
                assertEquals("Timestamp : Month was wrong", 2, cal2.get(Calendar.MONTH));
                assertEquals("Timestamp : Day was wrong", 10, cal2.get(Calendar.DAY_OF_MONTH));
                int hour = cal2.get(Calendar.HOUR_OF_DAY);
                assertTrue("Timestamp : Hour was wrong", hour == 0 || hour == 5); // In-memory with some datastores has the real hour (not stored as 0)
                assertEquals("Timestamp : Minute was wrong", 0, cal2.get(Calendar.MINUTE));
                assertEquals("Timestamp : Second was wrong", 0, cal2.get(Calendar.SECOND));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error retrieving Calendar data", e);
                fail("Error retrieving Calendar data : " + e.getMessage());
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
            clean(CalendarHolder.class);
        }
    }
}