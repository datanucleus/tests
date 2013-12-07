/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.samples.directory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests the mapping of lets of strings, primitives, wrappers and other basic types
 * <ul>
 * <li>String</li>
 * <li>Double</li>
 * <li>Float</li>
 * <li>Character</li>
 * <li>Boolean</li>
 * <li>Byte</li>
 * <li>Short</li>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>BigInteger and BigDecimal</li>
 * <li>Currency</li>
 * <li>Locale</li>
 * <li>TimeZone</li>
 * <li>UUID</li>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>Enum</li>
 */
public class ListHolderTest extends JDOPersistenceTestCase
{
    Object id;

    public ListHolderTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(ListHolder.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder lh = new ListHolder();
            lh.setPrimaryKey('x');
            lh.getTheStrings().add("AAA");
            lh.getTheStrings().add("BBB");
            lh.getTheStrings().add("CCC");
            lh.getAnotherStrings().add("secret1");
            lh.getAnotherStrings().add("secret2");
            lh.getTheFloats().add(1.2F);
            lh.getTheFloats().add(-2345678.479F);
            lh.getTheDoubles().add(1234567.890);
            lh.getTheDoubles().add(-0.001);
            lh.getTheBooleans().add(true);
            lh.getTheBooleans().add(false);
            lh.getTheCharacters().add('Z');
            lh.getTheCharacters().add('\n');
            lh.getTheCharacters().add('\u1567');
            lh.getTheBytes().add((byte) 0x41);
            lh.getTheBytes().add((byte) 0x00);
            lh.getTheBytes().add((byte) 0xFF);
            lh.getTheBytes().add((byte) 0x7F);
            lh.getTheBytes().add((byte) 0x80);
            lh.getTheShorts().add((short) 1);
            lh.getTheShorts().add((short) -88);
            lh.getTheIntegers().add(-12345);
            lh.getTheIntegers().add(-3);
            lh.getTheIntegers().add(0);
            lh.getTheIntegers().add(1);
            lh.getTheIntegers().add(987654);
            lh.getTheLongs().add(-3L);
            lh.getTheLongs().add(1234567890L);
            lh.getTheBigIntegers().add(new BigInteger("1234567890"));
            lh.getTheBigIntegers().add(new BigInteger("2345678901"));
            lh.getTheBigDecimals().add(new BigDecimal("12345.67890"));
            lh.getTheBigDecimals().add(new BigDecimal("23456.78901"));
            lh.getTheCurrencies().add(Currency.getInstance(Locale.US));
            lh.getTheCurrencies().add(Currency.getInstance(Locale.UK));
            lh.getTheLocales().add(Locale.GERMANY);
            lh.getTheLocales().add(Locale.ITALY);
            lh.getTheTimeZones().add(TimeZone.getTimeZone("GMT"));
            lh.getTheTimeZones().add(TimeZone.getTimeZone("PST"));
            lh.getTheUUIDs().add(new UUID(5, 7));
            lh.getTheUUIDs().add(new UUID(0, 0));
            lh.getTheDates().add(new Date(123456789000L));
            lh.getTheDates().add(new Date(0));
            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            lh.getTheCalendars().add(calendar1);
            lh.getTheCalendars().add(calendar2);
            lh.getTheEnums().add(Gender.male);
            lh.getTheEnums().add(Gender.female);

            pm.makePersistent(lh);
            id = pm.getObjectId(lh);
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

    protected void tearDown() throws Exception
    {
        clean(ListHolder.class);
        super.tearDown();
    }

    /**
     * Fetch object and assert all values.
     */
    public void testRead()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder lh = pm.getObjectById(ListHolder.class, 'x');

            assertNotNull(lh.getTheStrings());
            assertEquals(3, lh.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(lh.getTheStrings()));

            assertNotNull(lh.getAnotherStrings());
            assertEquals(2, lh.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(lh.getAnotherStrings()));

            assertNotNull(lh.getTheFloats());
            assertEquals(2, lh.getTheFloats().size());
            assertTrue(Arrays.asList(1.2F, -2345678.479F).containsAll(lh.getTheFloats()));

            assertNotNull(lh.getTheDoubles());
            assertEquals(2, lh.getTheDoubles().size());
            assertTrue(Arrays.asList(1234567.890, -0.001).containsAll(lh.getTheDoubles()));

            assertNotNull(lh.getTheBooleans());
            assertEquals(2, lh.getTheBooleans().size());
            assertTrue(Arrays.asList(true, false).containsAll(lh.getTheBooleans()));

            assertNotNull(lh.getTheCharacters());
            assertEquals(3, lh.getTheCharacters().size());
            assertTrue(Arrays.asList('Z', '\n', '\u1567').containsAll(lh.getTheCharacters()));

            assertNotNull(lh.getTheBytes());
            assertEquals(5, lh.getTheBytes().size());
            assertTrue(Arrays.asList((byte) 0x41, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80).containsAll(lh.getTheBytes()));

            assertNotNull(lh.getTheShorts());
            assertEquals(2, lh.getTheShorts().size());
            assertTrue(Arrays.asList((short) 1, (short) -88).containsAll(lh.getTheShorts()));

            assertNotNull(lh.getTheIntegers());
            assertEquals(5, lh.getTheIntegers().size());
            assertTrue(Arrays.asList(-12345, -3, 0, 1, 987654).containsAll(lh.getTheIntegers()));

            assertNotNull(lh.getTheLongs());
            assertEquals(2, lh.getTheLongs().size());
            assertTrue(Arrays.asList(-3L, 1234567890L).containsAll(lh.getTheLongs()));

            assertNotNull(lh.getTheBigIntegers());
            assertEquals(2, lh.getTheBigIntegers().size());
            assertTrue(Arrays.asList(new BigInteger("1234567890"), new BigInteger("2345678901")).containsAll(lh.getTheBigIntegers()));

            assertNotNull(lh.getTheBigDecimals());
            assertEquals(2, lh.getTheBigDecimals().size());
            assertTrue(Arrays.asList(new BigDecimal("12345.67890"), new BigDecimal("23456.78901")).containsAll(lh.getTheBigDecimals()));

            assertNotNull(lh.getTheCurrencies());
            assertEquals(2, lh.getTheCurrencies().size());
            assertTrue(Arrays.asList(Currency.getInstance(Locale.US), Currency.getInstance(Locale.UK)).containsAll(lh.getTheCurrencies()));

            assertNotNull(lh.getTheLocales());
            assertEquals(2, lh.getTheLocales().size());
            assertTrue(Arrays.asList(Locale.GERMANY, Locale.ITALY).containsAll(lh.getTheLocales()));

            assertNotNull(lh.getTheTimeZones());
            assertEquals(2, lh.getTheTimeZones().size());
            assertTrue(Arrays.asList(TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("PST")).containsAll(lh.getTheTimeZones()));

            assertNotNull(lh.getTheUUIDs());
            assertEquals(2, lh.getTheUUIDs().size());
            assertTrue(Arrays.asList(new UUID(5, 7), new UUID(0, 0)).containsAll(lh.getTheUUIDs()));

            assertNotNull(lh);
            assertEquals(2, lh.getTheDates().size());
            assertTrue(Arrays.asList(new Date(123456789000L), new Date(0)).containsAll(lh.getTheDates()));

            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            assertNotNull(lh.getTheCalendars());
            assertEquals(2, lh.getTheCalendars().size());
            assertTrue(Arrays.asList(calendar1, calendar2).containsAll(lh.getTheCalendars()));

            assertNotNull(lh.getTheEnums());
            assertEquals(2, lh.getTheEnums().size());
            assertTrue(Arrays.asList(Gender.male, Gender.female).containsAll(lh.getTheEnums()));

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

    /**
     * Update all values with single values
     */
    public void testUpdateSingleValue()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // update values
            tx.begin();
            ListHolder lh1 = (ListHolder) pm.getObjectById(id);
            lh1.getTheStrings().clear();
            lh1.getTheStrings().add("ABCxyz");
            lh1.getAnotherStrings().clear();
            lh1.getAnotherStrings().add("secret44");
            lh1.getTheFloats().clear();
            lh1.getTheFloats().add(-1.2F);
            lh1.getTheDoubles().clear();
            lh1.getTheDoubles().add(999.99);
            lh1.getTheBooleans().clear();
            lh1.getTheBooleans().add(true);
            lh1.getTheCharacters().clear();
            lh1.getTheCharacters().add('u');
            lh1.getTheBytes().clear();
            lh1.getTheBytes().add((byte) 0x45);
            lh1.getTheShorts().clear();
            lh1.getTheShorts().add((short) 7);
            lh1.getTheIntegers().clear();
            lh1.getTheIntegers().add(-3399);
            lh1.getTheLongs().clear();
            lh1.getTheLongs().add(-1L);
            lh1.getTheBigIntegers().clear();
            lh1.getTheBigIntegers().add(BigInteger.ONE);
            lh1.getTheBigDecimals().clear();
            lh1.getTheBigDecimals().add(BigDecimal.TEN);
            lh1.getTheCurrencies().clear();
            lh1.getTheCurrencies().add(Currency.getInstance(Locale.KOREA));
            lh1.getTheLocales().clear();
            lh1.getTheLocales().add(Locale.KOREA);
            lh1.getTheTimeZones().clear();
            lh1.getTheTimeZones().add(TimeZone.getTimeZone("GMT-11:00"));
            UUID randomUUID = UUID.randomUUID();
            lh1.getTheUUIDs().clear();
            lh1.getTheUUIDs().add(randomUUID);
            lh1.getTheEnums().clear();
            lh1.getTheEnums().add(Gender.female);
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh2 = (ListHolder) pm.getObjectById(id);

            assertNotNull(lh2.getTheStrings());
            assertEquals(1, lh2.getTheStrings().size());
            assertEquals("ABCxyz", lh2.getTheStrings().iterator().next());

            assertNotNull(lh2.getAnotherStrings());
            assertEquals(1, lh2.getAnotherStrings().size());
            assertEquals("secret44", lh2.getAnotherStrings().iterator().next());

            assertNotNull(lh2.getTheFloats());
            assertEquals(1, lh2.getTheFloats().size());
            assertEquals(-1.2F, lh2.getTheFloats().iterator().next(), 0.1);

            assertNotNull(lh2.getTheDoubles());
            assertEquals(1, lh2.getTheDoubles().size());
            assertEquals(999.99, lh2.getTheDoubles().iterator().next(), 0.1);

            assertNotNull(lh2.getTheBooleans());
            assertEquals(1, lh2.getTheBooleans().size());
            assertEquals(Boolean.TRUE, lh2.getTheBooleans().iterator().next());

            assertNotNull(lh2.getTheCharacters());
            assertEquals(1, lh2.getTheCharacters().size());
            assertEquals(new Character('u'), lh2.getTheCharacters().iterator().next());

            assertNotNull(lh2.getTheBytes());
            assertEquals(1, lh2.getTheBytes().size());
            assertEquals(new Byte((byte) 0x45), lh2.getTheBytes().iterator().next());

            assertNotNull(lh2.getTheShorts());
            assertEquals(1, lh2.getTheShorts().size());
            assertEquals(new Short((short) 7), lh2.getTheShorts().iterator().next());

            assertNotNull(lh2.getTheIntegers());
            assertEquals(1, lh2.getTheIntegers().size());
            assertEquals(new Integer(-3399), lh2.getTheIntegers().iterator().next());

            assertNotNull(lh2.getTheLongs());
            assertEquals(1, lh2.getTheLongs().size());
            assertEquals(new Long(-1L), lh2.getTheLongs().iterator().next());

            assertNotNull(lh2.getTheBigIntegers());
            assertEquals(1, lh2.getTheBigIntegers().size());
            assertEquals(BigInteger.ONE, lh2.getTheBigIntegers().iterator().next());

            assertNotNull(lh2.getTheBigDecimals());
            assertEquals(1, lh2.getTheBigDecimals().size());
            assertEquals(BigDecimal.TEN, lh2.getTheBigDecimals().iterator().next());

            assertNotNull(lh2.getTheCurrencies());
            assertEquals(1, lh2.getTheCurrencies().size());
            assertEquals(Currency.getInstance(Locale.KOREA), lh2.getTheCurrencies().iterator().next());

            assertNotNull(lh2.getTheLocales());
            assertEquals(1, lh2.getTheLocales().size());
            assertEquals(Locale.KOREA, lh2.getTheLocales().iterator().next());

            assertNotNull(lh2.getTheTimeZones());
            assertEquals(1, lh2.getTheTimeZones().size());
            assertEquals(TimeZone.getTimeZone("GMT-11:00"), lh2.getTheTimeZones().iterator().next());

            assertNotNull(lh2.getTheUUIDs());
            assertEquals(1, lh2.getTheUUIDs().size());
            assertEquals(randomUUID, lh2.getTheUUIDs().iterator().next());

            assertNotNull(lh2.getTheEnums());
            assertEquals(1, lh2.getTheEnums().size());
            assertEquals(Gender.female, lh2.getTheEnums().iterator().next());
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

    /**
     * Update all values to empty
     */
    public void testUpdateToEmpty()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // update values
            tx.begin();
            ListHolder lh1 = (ListHolder) pm.getObjectById(id);
            lh1.getTheStrings().clear();
            lh1.getAnotherStrings().clear();
            lh1.getTheFloats().clear();
            lh1.getTheDoubles().clear();
            lh1.getTheBooleans().clear();
            lh1.getTheCharacters().clear();
            lh1.getTheBytes().clear();
            lh1.getTheShorts().clear();
            lh1.getTheIntegers().clear();
            lh1.getTheLongs().clear();
            lh1.getTheBigIntegers().clear();
            lh1.getTheBigDecimals().clear();
            lh1.getTheCurrencies().clear();
            lh1.getTheLocales().clear();
            lh1.getTheTimeZones().clear();
            lh1.getTheUUIDs().clear();
            lh1.getTheEnums().clear();
            tx.commit();

            // assert null values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh2 = (ListHolder) pm.getObjectById(id);
            assertTrue(lh2.getTheStrings().isEmpty());
            assertTrue(lh2.getAnotherStrings().isEmpty());
            assertTrue(lh2.getTheFloats().isEmpty());
            assertTrue(lh2.getTheDoubles().isEmpty());
            assertTrue(lh2.getTheBooleans().isEmpty());
            assertTrue(lh2.getTheCharacters().isEmpty());
            assertTrue(lh2.getTheBytes().isEmpty());
            assertTrue(lh2.getTheShorts().isEmpty());
            assertTrue(lh2.getTheShorts().isEmpty());
            assertTrue(lh2.getTheIntegers().isEmpty());
            assertTrue(lh2.getTheLongs().isEmpty());
            assertTrue(lh2.getTheBigIntegers().isEmpty());
            assertTrue(lh2.getTheBigDecimals().isEmpty());
            assertTrue(lh2.getTheCurrencies().isEmpty());
            assertTrue(lh2.getTheLocales().isEmpty());
            assertTrue(lh2.getTheTimeZones().isEmpty());
            assertTrue(lh2.getTheUUIDs().isEmpty());
            assertTrue(lh2.getTheEnums().isEmpty());
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

    /**
     * Tests the update of string list.
     * <ul>
     * <li>add one value
     * <li>remove one value
     * <li>set to empty
     * <li>set to another value with multi-byte characters and long value
     * </ul>
     */
    public void testUpdateStrings()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // assert initial values
            ListHolder lh1 = (ListHolder) pm.getObjectById(id);
            assertNotNull(lh1.getTheStrings());
            assertEquals(3, lh1.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(lh1.getTheStrings()));
            assertNotNull(lh1.getAnotherStrings());
            assertEquals(2, lh1.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(lh1.getAnotherStrings()));

            // add one value
            lh1.getTheStrings().add("EEE");
            lh1.getAnotherStrings().add("secret3");
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh2 = (ListHolder) pm.getObjectById(id);
            assertEquals(4, lh2.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC", "EEE").containsAll(lh2.getTheStrings()));
            assertEquals(3, lh2.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2", "secret3").containsAll(lh2.getAnotherStrings()));

            // assert other values are unchanged
            assertValuesUnchanged(lh2);

            // remove one value
            lh2.getTheStrings().remove("BBB");
            lh2.getAnotherStrings().remove("secret2");
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh3 = (ListHolder) pm.getObjectById(id);
            assertEquals(3, lh3.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "CCC", "EEE").containsAll(lh3.getTheStrings()));
            assertEquals(2, lh3.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret3").containsAll(lh3.getAnotherStrings()));

            // assert other values are unchanged
            assertValuesUnchanged(lh3);

            // set empty
            lh3.getTheStrings().clear();
            lh3.getAnotherStrings().clear();
            tx.commit();

            // assert empty
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh4 = (ListHolder) pm.getObjectById(id);
            assertTrue(lh4.getTheStrings().isEmpty());
            assertTrue(lh4.getAnotherStrings().isEmpty());

            // assert other values are unchanged
            assertValuesUnchanged(lh4);

            // set to another value
            String v1 = "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577";
            StringBuffer v2 = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                v2.append('W');
            }
            lh4.getTheStrings().add(v1);
            lh4.getTheStrings().add(v2.toString());
            lh4.getAnotherStrings().add(v1 + "B");
            lh4.getAnotherStrings().add(v2.toString() + "B");
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh5 = (ListHolder) pm.getObjectById(id);
            assertEquals(2, lh5.getTheStrings().size());
            assertTrue(Arrays.asList(v1, v2.toString()).containsAll(lh5.getTheStrings()));
            assertEquals(2, lh5.getAnotherStrings().size());
            assertTrue(Arrays.asList(v1 + "B", v2.toString() + "B").containsAll(lh5.getAnotherStrings()));

            // assert other values are unchanged
            assertValuesUnchanged(lh5);
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

    public void testOrderingIndex()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder lh = new ListHolder();
            lh.setPrimaryKey('o');
            lh.getOrderedStrings().add("ZZZ");
            lh.getOrderedStrings().add("AAA");
            lh.getOrderedStrings().add("999");
            lh.getOrderedStrings().add("000");
            lh.getOrderedStrings().add("{0}");
            lh.getOrderedStrings().add("{Z}");
            lh.getOrderedStrings().add("AAA"); // duplicate value!
            lh.getOrderedStrings().add("BBB");
            lh.getOrderedStrings().add("CCC");
            lh.getOrderedStrings().add(""); // empty value!
            lh.getOrderedStrings().add("EEE");
            lh.getOrderedStrings().add("FFF");
            lh.getOrderedStrings().add("111");
            lh.getOrderedLongs().add(0L);
            lh.getOrderedLongs().add(Long.MAX_VALUE);
            lh.getOrderedLongs().add(Long.MIN_VALUE);
            lh.getOrderedLongs().add(0L); // duplicate value!
            pm.makePersistent(lh);
            Object oId = pm.getObjectId(lh);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh2 = (ListHolder) pm.getObjectById(oId);
            // check exact order
            assertEquals("ZZZ", lh2.getOrderedStrings().get(0));
            assertEquals("AAA", lh2.getOrderedStrings().get(1));
            assertEquals("999", lh2.getOrderedStrings().get(2));
            assertEquals("000", lh2.getOrderedStrings().get(3));
            assertEquals("{0}", lh2.getOrderedStrings().get(4));
            assertEquals("{Z}", lh2.getOrderedStrings().get(5));
            assertEquals("AAA", lh2.getOrderedStrings().get(6)); // duplicate value!
            assertEquals("BBB", lh2.getOrderedStrings().get(7));
            assertEquals("CCC", lh2.getOrderedStrings().get(8));
            assertEquals("", lh2.getOrderedStrings().get(9)); // empty value!
            assertEquals("EEE", lh2.getOrderedStrings().get(10));
            assertEquals("FFF", lh2.getOrderedStrings().get(11));
            assertEquals("111", lh2.getOrderedStrings().get(12));
            assertEquals(0L, lh2.getOrderedLongs().get(0).longValue());
            assertEquals(Long.MAX_VALUE, lh2.getOrderedLongs().get(1).longValue());
            assertEquals(Long.MIN_VALUE, lh2.getOrderedLongs().get(2).longValue());
            assertEquals(0L, lh2.getOrderedLongs().get(3).longValue()); // duplicate value!
            
            // modify order
            String removedString = lh2.getOrderedStrings().remove(0);
            lh2.getOrderedStrings().add(4, removedString);
            Long removedLong = lh2.getOrderedLongs().remove(0);
            lh2.getOrderedLongs().add(3, removedLong);
            tx.commit();
            pm.close();
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ListHolder lh3 = (ListHolder) pm.getObjectById(oId);
            // check new order
            assertEquals("AAA", lh3.getOrderedStrings().get(0));
            assertEquals("999", lh3.getOrderedStrings().get(1));
            assertEquals("000", lh3.getOrderedStrings().get(2));
            assertEquals("{0}", lh3.getOrderedStrings().get(3));
            assertEquals("ZZZ", lh3.getOrderedStrings().get(4)); // moved
            assertEquals("{Z}", lh3.getOrderedStrings().get(5));
            assertEquals("AAA", lh3.getOrderedStrings().get(6));
            assertEquals("BBB", lh3.getOrderedStrings().get(7));
            assertEquals("CCC", lh3.getOrderedStrings().get(8));
            assertEquals("", lh3.getOrderedStrings().get(9));
            assertEquals("EEE", lh3.getOrderedStrings().get(10));
            assertEquals("FFF", lh3.getOrderedStrings().get(11));
            assertEquals("111", lh3.getOrderedStrings().get(12));
            assertEquals(Long.MAX_VALUE, lh3.getOrderedLongs().get(0).longValue());
            assertEquals(Long.MIN_VALUE, lh3.getOrderedLongs().get(1).longValue());
            assertEquals(0L, lh3.getOrderedLongs().get(2).longValue());
            assertEquals(0L, lh3.getOrderedLongs().get(3).longValue()); // moved
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
    
    private void assertValuesUnchanged(ListHolder lh)
    {
        assertNotNull(lh.getTheFloats());
        assertEquals(2, lh.getTheFloats().size());
        assertTrue(Arrays.asList(1.2F, -2345678.479F).containsAll(lh.getTheFloats()));

        assertNotNull(lh.getTheDoubles());
        assertEquals(2, lh.getTheDoubles().size());
        assertTrue(Arrays.asList(1234567.890, -0.001).containsAll(lh.getTheDoubles()));

        assertNotNull(lh.getTheBooleans());
        assertEquals(2, lh.getTheBooleans().size());
        assertTrue(Arrays.asList(true, false).containsAll(lh.getTheBooleans()));

        assertNotNull(lh.getTheCharacters());
        assertEquals(3, lh.getTheCharacters().size());
        assertTrue(Arrays.asList('Z', '\n', '\u1567').containsAll(lh.getTheCharacters()));

        assertNotNull(lh.getTheBytes());
        assertEquals(5, lh.getTheBytes().size());
        assertTrue(Arrays.asList((byte) 0x41, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80).containsAll(lh.getTheBytes()));

        assertNotNull(lh.getTheShorts());
        assertEquals(2, lh.getTheShorts().size());
        assertTrue(Arrays.asList((short) 1, (short) -88).containsAll(lh.getTheShorts()));

        assertNotNull(lh.getTheIntegers());
        assertEquals(5, lh.getTheIntegers().size());
        assertTrue(Arrays.asList(-12345, -3, 0, 1, 987654).containsAll(lh.getTheIntegers()));

        assertNotNull(lh.getTheLongs());
        assertEquals(2, lh.getTheLongs().size());
        assertTrue(Arrays.asList(-3L, 1234567890L).containsAll(lh.getTheLongs()));

        assertNotNull(lh.getTheBigIntegers());
        assertEquals(2, lh.getTheBigIntegers().size());
        assertTrue(Arrays.asList(new BigInteger("1234567890"), new BigInteger("2345678901")).containsAll(lh.getTheBigIntegers()));

        assertNotNull(lh.getTheBigDecimals());
        assertEquals(2, lh.getTheBigDecimals().size());
        assertTrue(Arrays.asList(new BigDecimal("12345.67890"), new BigDecimal("23456.78901")).containsAll(lh.getTheBigDecimals()));

        assertNotNull(lh.getTheCurrencies());
        assertEquals(2, lh.getTheCurrencies().size());
        assertTrue(Arrays.asList(Currency.getInstance(Locale.US), Currency.getInstance(Locale.UK)).containsAll(lh.getTheCurrencies()));

        assertNotNull(lh.getTheLocales());
        assertEquals(2, lh.getTheLocales().size());
        assertTrue(Arrays.asList(Locale.GERMANY, Locale.ITALY).containsAll(lh.getTheLocales()));

        assertNotNull(lh.getTheTimeZones());
        assertEquals(2, lh.getTheTimeZones().size());
        assertTrue(Arrays.asList(TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("PST")).containsAll(lh.getTheTimeZones()));

        assertNotNull(lh.getTheUUIDs());
        assertEquals(2, lh.getTheUUIDs().size());
        assertTrue(Arrays.asList(new UUID(5, 7), new UUID(0, 0)).containsAll(lh.getTheUUIDs()));

        assertNotNull(lh);
        assertEquals(2, lh.getTheDates().size());
        assertTrue(Arrays.asList(new Date(123456789000L), new Date(0)).containsAll(lh.getTheDates()));

        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar1.setTimeInMillis(23456780000L); // strip millis
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar2.setTimeInMillis(1000L); // strip millis
        assertNotNull(lh.getTheCalendars());
        assertEquals(2, lh.getTheCalendars().size());
        assertTrue(Arrays.asList(calendar1, calendar2).containsAll(lh.getTheCalendars()));

        assertNotNull(lh.getTheEnums());
        assertEquals(2, lh.getTheEnums().size());
        assertTrue(Arrays.asList(Gender.male, Gender.female).containsAll(lh.getTheEnums()));

    }

}
