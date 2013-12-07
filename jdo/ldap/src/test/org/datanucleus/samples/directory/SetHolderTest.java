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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests the mapping of sets of strings, primitives, wrappers and other basic types
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
public class SetHolderTest extends JDOPersistenceTestCase
{
    Object id;

    public SetHolderTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(SetHolder.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SetHolder sh = new SetHolder();
            sh.setPrimaryKey(Long.MIN_VALUE);
            sh.getTheStrings().add("AAA");
            sh.getTheStrings().add("BBB");
            sh.getTheStrings().add("CCC");
            sh.getAnotherStrings().add("secret1");
            sh.getAnotherStrings().add("secret2");
            sh.getTheFloats().add(1.2F);
            sh.getTheFloats().add(-2345678.479F);
            sh.getTheDoubles().add(1234567.890);
            sh.getTheDoubles().add(-0.001);
            sh.getTheBooleans().add(true);
            sh.getTheBooleans().add(false);
            sh.getTheCharacters().add('Z');
            sh.getTheCharacters().add('\n');
            sh.getTheCharacters().add('\u1567');
            sh.getTheBytes().add((byte) 0x41);
            sh.getTheBytes().add((byte) 0x00);
            sh.getTheBytes().add((byte) 0xFF);
            sh.getTheBytes().add((byte) 0x7F);
            sh.getTheBytes().add((byte) 0x80);
            sh.getTheShorts().add((short) 1);
            sh.getTheShorts().add((short) -88);
            sh.getTheIntegers().add(-12345);
            sh.getTheIntegers().add(-3);
            sh.getTheIntegers().add(0);
            sh.getTheIntegers().add(1);
            sh.getTheIntegers().add(987654);
            sh.getTheLongs().add(-3L);
            sh.getTheLongs().add(1234567890L);
            sh.getTheBigIntegers().add(new BigInteger("1234567890"));
            sh.getTheBigIntegers().add(new BigInteger("2345678901"));
            sh.getTheBigDecimals().add(new BigDecimal("12345.67890"));
            sh.getTheBigDecimals().add(new BigDecimal("23456.78901"));
            sh.getTheCurrencies().add(Currency.getInstance(Locale.US));
            sh.getTheCurrencies().add(Currency.getInstance(Locale.UK));
            sh.getTheLocales().add(Locale.GERMANY);
            sh.getTheLocales().add(Locale.ITALY);
            sh.getTheTimeZones().add(TimeZone.getTimeZone("GMT"));
            sh.getTheTimeZones().add(TimeZone.getTimeZone("PST"));
            sh.getTheUUIDs().add(new UUID(5, 7));
            sh.getTheUUIDs().add(new UUID(0, 0));
            sh.getTheDates().add(new Date(123456789000L));
            sh.getTheDates().add(new Date(0));
            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            sh.getTheCalendars().add(calendar1);
            sh.getTheCalendars().add(calendar2);
            sh.getTheEnums().add(Gender.female);
            sh.getTheEnums().add(Gender.male);

            pm.makePersistent(sh);
            id = pm.getObjectId(sh);
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
        clean(SetHolder.class);
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
            SetHolder sh = pm.getObjectById(SetHolder.class, Long.MIN_VALUE);

            assertNotNull(sh.getTheStrings());
            assertEquals(3, sh.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(sh.getTheStrings()));

            assertNotNull(sh.getAnotherStrings());
            assertEquals(2, sh.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(sh.getAnotherStrings()));

            assertNotNull(sh.getTheFloats());
            assertEquals(2, sh.getTheFloats().size());
            assertTrue(Arrays.asList(1.2F, -2345678.479F).containsAll(sh.getTheFloats()));

            assertNotNull(sh.getTheDoubles());
            assertEquals(2, sh.getTheDoubles().size());
            assertTrue(Arrays.asList(1234567.890, -0.001).containsAll(sh.getTheDoubles()));

            assertNotNull(sh.getTheBooleans());
            assertEquals(2, sh.getTheBooleans().size());
            assertTrue(Arrays.asList(true, false).containsAll(sh.getTheBooleans()));

            assertNotNull(sh.getTheCharacters());
            assertEquals(3, sh.getTheCharacters().size());
            assertTrue(Arrays.asList('Z', '\n', '\u1567').containsAll(sh.getTheCharacters()));

            assertNotNull(sh.getTheBytes());
            assertEquals(5, sh.getTheBytes().size());
            assertTrue(Arrays.asList((byte) 0x41, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80).containsAll(sh.getTheBytes()));

            assertNotNull(sh.getTheShorts());
            assertEquals(2, sh.getTheShorts().size());
            assertTrue(Arrays.asList((short) 1, (short) -88).containsAll(sh.getTheShorts()));

            assertNotNull(sh.getTheIntegers());
            assertEquals(5, sh.getTheIntegers().size());
            assertTrue(Arrays.asList(-12345, -3, 0, 1, 987654).containsAll(sh.getTheIntegers()));

            assertNotNull(sh.getTheLongs());
            assertEquals(2, sh.getTheLongs().size());
            assertTrue(Arrays.asList(-3L, 1234567890L).containsAll(sh.getTheLongs()));

            assertNotNull(sh.getTheBigIntegers());
            assertEquals(2, sh.getTheBigIntegers().size());
            assertTrue(Arrays.asList(new BigInteger("1234567890"), new BigInteger("2345678901")).containsAll(sh.getTheBigIntegers()));

            assertNotNull(sh.getTheBigDecimals());
            assertEquals(2, sh.getTheBigDecimals().size());
            assertTrue(Arrays.asList(new BigDecimal("12345.67890"), new BigDecimal("23456.78901")).containsAll(sh.getTheBigDecimals()));

            assertNotNull(sh.getTheCurrencies());
            assertEquals(2, sh.getTheCurrencies().size());
            assertTrue(Arrays.asList(Currency.getInstance(Locale.US), Currency.getInstance(Locale.UK)).containsAll(sh.getTheCurrencies()));

            assertNotNull(sh.getTheLocales());
            assertEquals(2, sh.getTheLocales().size());
            assertTrue(Arrays.asList(Locale.GERMANY, Locale.ITALY).containsAll(sh.getTheLocales()));

            assertNotNull(sh.getTheTimeZones());
            assertEquals(2, sh.getTheTimeZones().size());
            assertTrue(Arrays.asList(TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("PST")).containsAll(sh.getTheTimeZones()));

            assertNotNull(sh.getTheUUIDs());
            assertEquals(2, sh.getTheUUIDs().size());
            assertTrue(Arrays.asList(new UUID(5, 7), new UUID(0, 0)).containsAll(sh.getTheUUIDs()));

            assertNotNull(sh.getTheDates());
            assertEquals(2, sh.getTheDates().size());
            assertTrue(Arrays.asList(new Date(123456789000L), new Date(0)).containsAll(sh.getTheDates()));

            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            assertNotNull(sh.getTheCalendars());
            assertEquals(2, sh.getTheCalendars().size());
            assertTrue(Arrays.asList(calendar1, calendar2).containsAll(sh.getTheCalendars()));

            assertNotNull(sh.getTheEnums());
            assertEquals(2, sh.getTheEnums().size());
            assertTrue(Arrays.asList(Gender.female, Gender.male).containsAll(sh.getTheEnums()));

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
            SetHolder sh1 = (SetHolder) pm.getObjectById(id);
            sh1.getTheStrings().clear();
            sh1.getTheStrings().add("ABCxyz");
            sh1.getAnotherStrings().clear();
            sh1.getAnotherStrings().add("secret44");
            sh1.getTheFloats().clear();
            sh1.getTheFloats().add(-1.2F);
            sh1.getTheDoubles().clear();
            sh1.getTheDoubles().add(999.99);
            sh1.getTheBooleans().clear();
            sh1.getTheBooleans().add(true);
            sh1.getTheCharacters().clear();
            sh1.getTheCharacters().add('u');
            sh1.getTheBytes().clear();
            sh1.getTheBytes().add((byte) 0x45);
            sh1.getTheShorts().clear();
            sh1.getTheShorts().add((short) 7);
            sh1.getTheIntegers().clear();
            sh1.getTheIntegers().add(-3399);
            sh1.getTheLongs().clear();
            sh1.getTheLongs().add(-1L);
            sh1.getTheBigIntegers().clear();
            sh1.getTheBigIntegers().add(BigInteger.ONE);
            sh1.getTheBigDecimals().clear();
            sh1.getTheBigDecimals().add(BigDecimal.TEN);
            sh1.getTheCurrencies().clear();
            sh1.getTheCurrencies().add(Currency.getInstance(Locale.KOREA));
            sh1.getTheLocales().clear();
            sh1.getTheLocales().add(Locale.KOREA);
            sh1.getTheTimeZones().clear();
            sh1.getTheTimeZones().add(TimeZone.getTimeZone("GMT-11:00"));
            UUID randomUUID = UUID.randomUUID();
            sh1.getTheUUIDs().clear();
            sh1.getTheUUIDs().add(randomUUID);
            sh1.getTheEnums().clear();
            sh1.getTheEnums().add(Gender.female);
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh2 = (SetHolder) pm.getObjectById(id);

            assertNotNull(sh2.getTheStrings());
            assertEquals(1, sh2.getTheStrings().size());
            assertEquals("ABCxyz", sh2.getTheStrings().iterator().next());

            assertNotNull(sh2.getAnotherStrings());
            assertEquals(1, sh2.getAnotherStrings().size());
            assertEquals("secret44", sh2.getAnotherStrings().iterator().next());

            assertNotNull(sh2.getTheFloats());
            assertEquals(1, sh2.getTheFloats().size());
            assertEquals(-1.2F, sh2.getTheFloats().iterator().next(), 0.1);

            assertNotNull(sh2.getTheDoubles());
            assertEquals(1, sh2.getTheDoubles().size());
            assertEquals(999.99, sh2.getTheDoubles().iterator().next(), 0.1);

            assertNotNull(sh2.getTheBooleans());
            assertEquals(1, sh2.getTheBooleans().size());
            assertEquals(Boolean.TRUE, sh2.getTheBooleans().iterator().next());

            assertNotNull(sh2.getTheCharacters());
            assertEquals(1, sh2.getTheCharacters().size());
            assertEquals(new Character('u'), sh2.getTheCharacters().iterator().next());

            assertNotNull(sh2.getTheBytes());
            assertEquals(1, sh2.getTheBytes().size());
            assertEquals(new Byte((byte) 0x45), sh2.getTheBytes().iterator().next());

            assertNotNull(sh2.getTheShorts());
            assertEquals(1, sh2.getTheShorts().size());
            assertEquals(new Short((short) 7), sh2.getTheShorts().iterator().next());

            assertNotNull(sh2.getTheIntegers());
            assertEquals(1, sh2.getTheIntegers().size());
            assertEquals(new Integer(-3399), sh2.getTheIntegers().iterator().next());

            assertNotNull(sh2.getTheLongs());
            assertEquals(1, sh2.getTheLongs().size());
            assertEquals(new Long(-1L), sh2.getTheLongs().iterator().next());

            assertNotNull(sh2.getTheBigIntegers());
            assertEquals(1, sh2.getTheBigIntegers().size());
            assertEquals(BigInteger.ONE, sh2.getTheBigIntegers().iterator().next());

            assertNotNull(sh2.getTheBigDecimals());
            assertEquals(1, sh2.getTheBigDecimals().size());
            assertEquals(BigDecimal.TEN, sh2.getTheBigDecimals().iterator().next());

            assertNotNull(sh2.getTheCurrencies());
            assertEquals(1, sh2.getTheCurrencies().size());
            assertEquals(Currency.getInstance(Locale.KOREA), sh2.getTheCurrencies().iterator().next());

            assertNotNull(sh2.getTheLocales());
            assertEquals(1, sh2.getTheLocales().size());
            assertEquals(Locale.KOREA, sh2.getTheLocales().iterator().next());

            assertNotNull(sh2.getTheTimeZones());
            assertEquals(1, sh2.getTheTimeZones().size());
            assertEquals(TimeZone.getTimeZone("GMT-11:00"), sh2.getTheTimeZones().iterator().next());

            assertNotNull(sh2.getTheUUIDs());
            assertEquals(1, sh2.getTheUUIDs().size());
            assertEquals(randomUUID, sh2.getTheUUIDs().iterator().next());

            assertNotNull(sh2.getTheEnums());
            assertEquals(1, sh2.getTheEnums().size());
            assertEquals(Gender.female, sh2.getTheEnums().iterator().next());
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
    public void testUpdateSingleValueDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SetHolder sh1 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh1 = pm.detachCopy(sh1);
            tx.commit();
            pm.close();

            // update values
            detachedSh1.getTheStrings().clear();
            detachedSh1.getTheStrings().add("ABCxyz");
            detachedSh1.getAnotherStrings().clear();
            detachedSh1.getAnotherStrings().add("secret44");
            detachedSh1.getTheFloats().clear();
            detachedSh1.getTheFloats().add(-1.2F);
            detachedSh1.getTheDoubles().clear();
            detachedSh1.getTheDoubles().add(999.99);
            detachedSh1.getTheBooleans().clear();
            detachedSh1.getTheBooleans().add(true);
            detachedSh1.getTheCharacters().clear();
            detachedSh1.getTheCharacters().add('u');
            detachedSh1.getTheBytes().clear();
            detachedSh1.getTheBytes().add((byte) 0x45);
            detachedSh1.getTheShorts().clear();
            detachedSh1.getTheShorts().add((short) 7);
            detachedSh1.getTheIntegers().clear();
            detachedSh1.getTheIntegers().add(-3399);
            detachedSh1.getTheLongs().clear();
            detachedSh1.getTheLongs().add(-1L);
            detachedSh1.getTheBigIntegers().clear();
            detachedSh1.getTheBigIntegers().add(BigInteger.ONE);
            detachedSh1.getTheBigDecimals().clear();
            detachedSh1.getTheBigDecimals().add(BigDecimal.TEN);
            detachedSh1.getTheCurrencies().clear();
            detachedSh1.getTheCurrencies().add(Currency.getInstance(Locale.KOREA));
            detachedSh1.getTheLocales().clear();
            detachedSh1.getTheLocales().add(Locale.KOREA);
            detachedSh1.getTheTimeZones().clear();
            detachedSh1.getTheTimeZones().add(TimeZone.getTimeZone("GMT-11:00"));
            UUID randomUUID = UUID.randomUUID();
            detachedSh1.getTheUUIDs().clear();
            detachedSh1.getTheUUIDs().add(randomUUID);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedSh1);
            tx.commit();
            pm.close();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh2 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh2 = pm.detachCopy(sh2);
            tx.commit();
            pm.close();

            assertNotNull(detachedSh2.getTheStrings());
            assertEquals(1, detachedSh2.getTheStrings().size());
            assertEquals("ABCxyz", detachedSh2.getTheStrings().iterator().next());

            assertNotNull(detachedSh2.getAnotherStrings());
            assertEquals(1, detachedSh2.getAnotherStrings().size());
            assertEquals("secret44", detachedSh2.getAnotherStrings().iterator().next());

            assertNotNull(detachedSh2.getTheFloats());
            assertEquals(1, detachedSh2.getTheFloats().size());
            assertEquals(-1.2F, detachedSh2.getTheFloats().iterator().next(), 0.1);

            assertNotNull(detachedSh2.getTheDoubles());
            assertEquals(1, detachedSh2.getTheDoubles().size());
            assertEquals(999.99, detachedSh2.getTheDoubles().iterator().next(), 0.1);

            assertNotNull(detachedSh2.getTheBooleans());
            assertEquals(1, detachedSh2.getTheBooleans().size());
            assertEquals(Boolean.TRUE, detachedSh2.getTheBooleans().iterator().next());

            assertNotNull(detachedSh2.getTheCharacters());
            assertEquals(1, detachedSh2.getTheCharacters().size());
            assertEquals(new Character('u'), detachedSh2.getTheCharacters().iterator().next());

            assertNotNull(detachedSh2.getTheBytes());
            assertEquals(1, detachedSh2.getTheBytes().size());
            assertEquals(new Byte((byte) 0x45), detachedSh2.getTheBytes().iterator().next());

            assertNotNull(detachedSh2.getTheShorts());
            assertEquals(1, detachedSh2.getTheShorts().size());
            assertEquals(new Short((short) 7), detachedSh2.getTheShorts().iterator().next());

            assertNotNull(detachedSh2.getTheIntegers());
            assertEquals(1, detachedSh2.getTheIntegers().size());
            assertEquals(new Integer(-3399), detachedSh2.getTheIntegers().iterator().next());

            assertNotNull(detachedSh2.getTheLongs());
            assertEquals(1, detachedSh2.getTheLongs().size());
            assertEquals(new Long(-1L), detachedSh2.getTheLongs().iterator().next());

            assertNotNull(detachedSh2.getTheBigIntegers());
            assertEquals(1, detachedSh2.getTheBigIntegers().size());
            assertEquals(BigInteger.ONE, detachedSh2.getTheBigIntegers().iterator().next());

            assertNotNull(detachedSh2.getTheBigDecimals());
            assertEquals(1, detachedSh2.getTheBigDecimals().size());
            assertEquals(BigDecimal.TEN, detachedSh2.getTheBigDecimals().iterator().next());

            assertNotNull(detachedSh2.getTheCurrencies());
            assertEquals(1, detachedSh2.getTheCurrencies().size());
            assertEquals(Currency.getInstance(Locale.KOREA), detachedSh2.getTheCurrencies().iterator().next());

            assertNotNull(detachedSh2.getTheLocales());
            assertEquals(1, detachedSh2.getTheLocales().size());
            assertEquals(Locale.KOREA, detachedSh2.getTheLocales().iterator().next());

            assertNotNull(detachedSh2.getTheTimeZones());
            assertEquals(1, detachedSh2.getTheTimeZones().size());
            assertEquals(TimeZone.getTimeZone("GMT-11:00"), detachedSh2.getTheTimeZones().iterator().next());

            assertNotNull(detachedSh2.getTheUUIDs());
            assertEquals(1, detachedSh2.getTheUUIDs().size());
            assertEquals(randomUUID, detachedSh2.getTheUUIDs().iterator().next());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
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
            SetHolder sh1 = (SetHolder) pm.getObjectById(id);
            sh1.getTheStrings().clear();
            sh1.getAnotherStrings().clear();
            sh1.getTheFloats().clear();
            sh1.getTheDoubles().clear();
            sh1.getTheBooleans().clear();
            sh1.getTheCharacters().clear();
            sh1.getTheBytes().clear();
            sh1.getTheShorts().clear();
            sh1.getTheIntegers().clear();
            sh1.getTheLongs().clear();
            sh1.getTheBigIntegers().clear();
            sh1.getTheBigDecimals().clear();
            sh1.getTheCurrencies().clear();
            sh1.getTheLocales().clear();
            sh1.getTheTimeZones().clear();
            sh1.getTheUUIDs().clear();
            sh1.getTheEnums().clear();
            tx.commit();

            // assert null values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh2 = (SetHolder) pm.getObjectById(id);
            assertTrue(sh2.getTheStrings().isEmpty());
            assertTrue(sh2.getAnotherStrings().isEmpty());
            assertTrue(sh2.getTheFloats().isEmpty());
            assertTrue(sh2.getTheDoubles().isEmpty());
            assertTrue(sh2.getTheBooleans().isEmpty());
            assertTrue(sh2.getTheCharacters().isEmpty());
            assertTrue(sh2.getTheBytes().isEmpty());
            assertTrue(sh2.getTheShorts().isEmpty());
            assertTrue(sh2.getTheShorts().isEmpty());
            assertTrue(sh2.getTheIntegers().isEmpty());
            assertTrue(sh2.getTheLongs().isEmpty());
            assertTrue(sh2.getTheBigIntegers().isEmpty());
            assertTrue(sh2.getTheBigDecimals().isEmpty());
            assertTrue(sh2.getTheCurrencies().isEmpty());
            assertTrue(sh2.getTheLocales().isEmpty());
            assertTrue(sh2.getTheTimeZones().isEmpty());
            assertTrue(sh2.getTheUUIDs().isEmpty());
            assertTrue(sh2.getTheEnums().isEmpty());
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
     * Tests the update of string set.
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
            SetHolder sh1 = (SetHolder) pm.getObjectById(id);
            assertNotNull(sh1.getTheStrings());
            assertEquals(3, sh1.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(sh1.getTheStrings()));
            assertNotNull(sh1.getAnotherStrings());
            assertEquals(2, sh1.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(sh1.getAnotherStrings()));

            // add one value
            sh1.getTheStrings().add("EEE");
            sh1.getAnotherStrings().add("secret3");
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh2 = (SetHolder) pm.getObjectById(id);
            assertEquals(4, sh2.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC", "EEE").containsAll(sh2.getTheStrings()));
            assertEquals(3, sh2.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2", "secret3").containsAll(sh2.getAnotherStrings()));

            // remove one value
            sh2.getTheStrings().remove("BBB");
            sh2.getAnotherStrings().remove("secret2");
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh3 = (SetHolder) pm.getObjectById(id);
            assertEquals(3, sh3.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "CCC", "EEE").containsAll(sh3.getTheStrings()));
            assertEquals(2, sh3.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret3").containsAll(sh3.getAnotherStrings()));

            // set empty
            sh3.getTheStrings().clear();
            sh3.getAnotherStrings().clear();
            tx.commit();

            // assert empty
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh4 = (SetHolder) pm.getObjectById(id);
            assertTrue(sh4.getTheStrings().isEmpty());
            assertTrue(sh4.getAnotherStrings().isEmpty());

            // set to another value
            String v1 = "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577";
            StringBuffer v2 = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                v2.append('W');
            }
            sh4.getTheStrings().add(v1);
            sh4.getTheStrings().add(v2.toString());
            sh4.getAnotherStrings().add(v1 + "B");
            sh4.getAnotherStrings().add(v2.toString() + "B");
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh5 = (SetHolder) pm.getObjectById(id);
            assertEquals(2, sh5.getTheStrings().size());
            assertTrue(Arrays.asList(v1, v2.toString()).containsAll(sh5.getTheStrings()));
            assertEquals(2, sh5.getAnotherStrings().size());
            assertTrue(Arrays.asList(v1 + "B", v2.toString() + "B").containsAll(sh5.getAnotherStrings()));
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
     * Tests the detached update of string set.
     * <ul>
     * <li>add one value
     * <li>remove one value
     * <li>set to empty
     * <li>set to another value with multi-byte characters and long value
     * </ul>
     */
    public void testUpdateStringsDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SetHolder sh1 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh1 = pm.detachCopy(sh1);
            tx.commit();
            pm.close();

            // assert initial values
            assertNotNull(detachedSh1.getTheStrings());
            assertEquals(3, detachedSh1.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(detachedSh1.getTheStrings()));
            assertNotNull(detachedSh1.getAnotherStrings());
            assertEquals(2, detachedSh1.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(detachedSh1.getAnotherStrings()));

            // add one value
            detachedSh1.getTheStrings().add("EEE");
            detachedSh1.getAnotherStrings().add("secret3");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedSh1);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh2 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh2 = pm.detachCopy(sh2);
            tx.commit();
            pm.close();

            // assert new values
            assertEquals(4, detachedSh2.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "BBB", "CCC", "EEE").containsAll(detachedSh2.getTheStrings()));
            assertEquals(3, detachedSh2.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret2", "secret3").containsAll(detachedSh2.getAnotherStrings()));

            // remove one value
            detachedSh2.getTheStrings().remove("BBB");
            detachedSh2.getAnotherStrings().remove("secret2");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedSh2);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh3 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh3 = pm.detachCopy(sh3);
            tx.commit();
            pm.close();

            // assert new value
            assertEquals(3, detachedSh3.getTheStrings().size());
            assertTrue(Arrays.asList("AAA", "CCC", "EEE").containsAll(detachedSh3.getTheStrings()));
            assertEquals(2, detachedSh3.getAnotherStrings().size());
            assertTrue(Arrays.asList("secret1", "secret3").containsAll(detachedSh3.getAnotherStrings()));

            // set empty
            detachedSh3.getTheStrings().clear();
            detachedSh3.getAnotherStrings().clear();
            JDOHelper.makeDirty(detachedSh3, "theStrings");
            JDOHelper.makeDirty(detachedSh3, "anotherStrings");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedSh3);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh4 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh4 = pm.detachCopy(sh4);
            tx.commit();
            pm.close();

            // assert empty
            assertTrue(detachedSh4.getTheStrings().isEmpty());
            assertTrue(detachedSh4.getAnotherStrings().isEmpty());

            // set to another value
            String v1 = "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577";
            StringBuffer v2 = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                v2.append('W');
            }
            detachedSh4.getTheStrings().add(v1);
            detachedSh4.getTheStrings().add(v2.toString());
            detachedSh4.getAnotherStrings().add(v1 + "B");
            detachedSh4.getAnotherStrings().add(v2.toString() + "B");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedSh4);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            SetHolder sh5 = (SetHolder) pm.getObjectById(id);
            SetHolder detachedSh5 = pm.detachCopy(sh5);
            tx.commit();
            pm.close();

            // assert new value
            assertEquals(2, detachedSh5.getTheStrings().size());
            assertTrue(Arrays.asList(v1, v2.toString()).containsAll(detachedSh5.getTheStrings()));
            assertEquals(2, detachedSh5.getAnotherStrings().size());
            assertTrue(Arrays.asList(v1 + "B", v2.toString() + "B").containsAll(detachedSh5.getAnotherStrings()));
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }
    }

}
