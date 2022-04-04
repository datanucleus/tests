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
 * Tests the mapping of array of strings, primitives, wrappers and other basic types
 * <ul>
 * <li>String</li>
 * <li>double and Double</li>
 * <li>float and Float</li>
 * <li>char and Character</li>
 * <li>boolean and Boolean</li>
 * <li>byte and Byte</li>
 * <li>short and Short</li>
 * <li>int and Integer</li>
 * <li>long and Long</li>
 * <li>BigInteger and BigDecimal</li>
 * <li>Currency</li>
 * <li>Locale</li>
 * <li>TimeZone</li>
 * <li>UUID</li>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>Enum</li>
 * </ul>
 */
public class ArrayHolderTest extends JDOPersistenceTestCase
{
    Object id;

    public ArrayHolderTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(ArrayHolder.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ArrayHolder ao = new ArrayHolder();
            ao.setPrimaryKey(new UUID(1234L, 5678L));
            ao.setTheStringArray("AAA", "BBB", "CCC");
            ao.setAnotherStringArray("secret1", "secret2");
            ao.setTheFloatArray(1.2F, -2345678.479F);
            ao.setTheFloatObjArray(Float.valueOf(2.3F), Float.valueOf((float) -23.78));
            ao.setTheDoubleArray(1234567.890, -0.001);
            ao.setTheDoubleObjArray(Double.valueOf(2345678.901), Double.valueOf(-0.99999));
            ao.setTheBooleanArray(true, false);
            ao.setTheBooleanObjArray(Boolean.FALSE, Boolean.TRUE);
            ao.setTheStreamedBooleanObjArray(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
            ao.setTheCharArray('Z', '\n', '\u1567');
            ao.setTheCharObjArray(Character.valueOf('Y'), Character.valueOf('\u1382'));
            ao.setTheByteArray((byte) 0x41, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80);
            ao.setTheStreamedByteArray((byte) 0x42, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80);
            ao.setTheByteObjArray(Byte.valueOf((byte) 0x42), Byte.valueOf((byte) 0x43));
            ao.setTheShortArray((short) 1, (short) -88);
            ao.setTheShortObjArray(Short.valueOf((short) 11), Short.valueOf((short) -987));
            ao.setTheIntArray(-12345, -3, 0, 1, 987654);
            ao.setTheIntObjArray(Integer.valueOf(22), Integer.valueOf(-43242));
            ao.setTheLongArray(-3L, 1234567890L);
            ao.setTheLongObjArray(Long.valueOf(33L), Long.valueOf(-132143214321L));
            ao.setTheBigIntegerArray(new BigInteger("1234567890"), new BigInteger("2345678901"));
            ao.setTheBigDecimalArray(new BigDecimal("12345.67890"), new BigDecimal("23456.78901"));
            ao.setTheCurrencyArray(Currency.getInstance(Locale.US), Currency.getInstance(Locale.UK));
            ao.setTheLocaleArray(Locale.GERMANY, Locale.ITALY);
            ao.setTheTimeZoneArray(TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("PST"));
            ao.setTheUUIDArray(new UUID(5, 7), new UUID(0, 0));
            ao.setTheDateArray(new Date(123456789000L), new Date(0));
            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            ao.setTheCalendarArray(calendar1, calendar2);
            ao.setTheEnumArray(Gender.male, Gender.female);

            pm.makePersistent(ao);
            id = pm.getObjectId(ao);
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
        clean(ArrayHolder.class);
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
            ArrayHolder ao = pm.getObjectById(ArrayHolder.class, new UUID(1234L, 5678L));

            assertNotNull(ao.getTheStringArray());
            assertEquals(3, ao.getTheStringArray().length);
            assertTrue(Arrays.asList("AAA", "BBB", "CCC").containsAll(Arrays.asList(ao.getTheStringArray())));

            assertNotNull(ao.getAnotherStringArray());
            assertEquals(2, ao.getAnotherStringArray().length);
            assertTrue(Arrays.asList("secret1", "secret2").containsAll(Arrays.asList(ao.getAnotherStringArray())));

            assertNotNull(ao.getTheFloatArray());
            assertEquals(2, ao.getTheFloatArray().length);
            assertTrue(Arrays.equals(new float[]{1.2F, -2345678.479F}, ao.getTheFloatArray()));

            assertNotNull(ao.getTheFloatObjArray());
            assertEquals(2, ao.getTheFloatObjArray().length);
            assertTrue(Arrays.equals(new Float[]{Float.valueOf(2.3F), Float.valueOf((float) -23.78)}, ao.getTheFloatObjArray()));

            assertNotNull(ao.getTheDoubleArray());
            assertEquals(2, ao.getTheDoubleArray().length);
            assertTrue(Arrays.equals(new double[]{1234567.890, -0.001}, ao.getTheDoubleArray()));

            assertNotNull(ao.getTheDoubleObjArray());
            assertEquals(2, ao.getTheDoubleObjArray().length);
            assertTrue(Arrays.equals(new Double[]{Double.valueOf(2345678.901), Double.valueOf(-0.99999)}, ao.getTheDoubleObjArray()));

            assertNotNull(ao.getTheBooleanArray());
            assertEquals(2, ao.getTheBooleanArray().length);
            assertTrue(Arrays.equals(new boolean[]{true, false}, ao.getTheBooleanArray()));

            assertNotNull(ao.getTheBooleanObjArray());
            assertEquals(2, ao.getTheBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.FALSE, Boolean.TRUE}, ao.getTheBooleanObjArray()));

            assertNotNull(ao.getTheStreamedBooleanObjArray());
            assertEquals(4, ao.getTheStreamedBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE}, ao
                    .getTheStreamedBooleanObjArray()));

            assertNotNull(ao.getTheCharArray());
            assertEquals(3, ao.getTheCharArray().length);
            assertTrue(Arrays.equals(new char[]{'Z', '\n', '\u1567'}, ao.getTheCharArray()));

            assertNotNull(ao.getTheCharObjArray());
            assertEquals(2, ao.getTheCharObjArray().length);
            assertTrue(Arrays.equals(new Character[]{Character.valueOf('Y'), Character.valueOf('\u1382')}, ao.getTheCharObjArray()));

            assertNotNull(ao.getTheByteArray());
            assertEquals(5, ao.getTheByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x41, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80}, ao.getTheByteArray()));

            assertNotNull(ao.getTheStreamedByteArray());
            assertEquals(5, ao.getTheStreamedByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x42, (byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80}, ao
                    .getTheStreamedByteArray()));

            assertNotNull(ao.getTheByteObjArray());
            assertEquals(2, ao.getTheByteObjArray().length);
            assertTrue(Arrays.equals(new Byte[]{Byte.valueOf((byte) 0x42), Byte.valueOf((byte) 0x43)}, ao.getTheByteObjArray()));

            assertNotNull(ao.getTheShortArray());
            assertEquals(2, ao.getTheShortArray().length);
            assertTrue(Arrays.equals(new short[]{(short) 1, (short) -88}, ao.getTheShortArray()));

            assertNotNull(ao.getTheShortObjArray());
            assertEquals(2, ao.getTheShortObjArray().length);
            assertTrue(Arrays.equals(new Short[]{Short.valueOf((short) 11), Short.valueOf((short) -987)}, ao.getTheShortObjArray()));

            assertNotNull(ao.getTheIntArray());
            assertEquals(5, ao.getTheIntArray().length);
            assertTrue(Arrays.equals(new int[]{-12345, -3, 0, 1, 987654}, ao.getTheIntArray()));

            assertNotNull(ao.getTheIntObjArray());
            assertEquals(2, ao.getTheIntObjArray().length);
            assertTrue(Arrays.equals(new Integer[]{Integer.valueOf(22), Integer.valueOf(-43242)}, ao.getTheIntObjArray()));

            assertNotNull(ao.getTheLongArray());
            assertEquals(2, ao.getTheLongArray().length);
            assertTrue(Arrays.equals(new long[]{-3L, 1234567890L}, ao.getTheLongArray()));

            assertNotNull(ao.getTheLongObjArray());
            assertEquals(2, ao.getTheLongObjArray().length);
            assertTrue(Arrays.equals(new Long[]{Long.valueOf(33L), Long.valueOf(-132143214321L)}, ao.getTheLongObjArray()));

            assertNotNull(ao.getTheBigIntegerArray());
            assertEquals(2, ao.getTheBigIntegerArray().length);
            assertTrue(Arrays.equals(new BigInteger[]{new BigInteger("1234567890"), new BigInteger("2345678901")}, ao
                    .getTheBigIntegerArray()));

            assertNotNull(ao.getTheBigDecimalArray());
            assertEquals(2, ao.getTheBigDecimalArray().length);
            assertTrue(Arrays.equals(new BigDecimal[]{new BigDecimal("12345.67890"), new BigDecimal("23456.78901")}, ao
                    .getTheBigDecimalArray()));

            assertNotNull(ao.getTheCurrencyArray());
            assertEquals(2, ao.getTheCurrencyArray().length);
            assertTrue(Arrays.equals(new Currency[]{Currency.getInstance(Locale.US), Currency.getInstance(Locale.UK)}, ao
                    .getTheCurrencyArray()));

            assertNotNull(ao.getTheLocaleArray());
            assertEquals(2, ao.getTheLocaleArray().length);
            assertTrue(Arrays.equals(new Locale[]{Locale.GERMANY, Locale.ITALY}, ao.getTheLocaleArray()));

            assertNotNull(ao.getTheTimeZoneArray());
            assertEquals(2, ao.getTheTimeZoneArray().length);
            assertTrue(Arrays.equals(new TimeZone[]{TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("PST")}, ao.getTheTimeZoneArray()));

            assertNotNull(ao.getTheUUIDArray());
            assertEquals(2, ao.getTheUUIDArray().length);
            assertTrue(Arrays.equals(new UUID[]{new UUID(5, 7), new UUID(0, 0)}, ao.getTheUUIDArray()));

            assertNotNull(ao.getTheDateArray());
            assertEquals(2, ao.getTheDateArray().length);
            assertTrue(Arrays.equals(new Date[]{new Date(123456789000L), new Date(0)}, ao.getTheDateArray()));

            Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar1.setTimeInMillis(23456780000L); // strip millis
            Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar2.setTimeInMillis(1000L); // strip millis
            assertNotNull(ao.getTheCalendarArray());
            assertEquals(2, ao.getTheCalendarArray().length);
            assertTrue(Arrays.equals(new Calendar[]{calendar1, calendar2}, ao.getTheCalendarArray()));

            assertNotNull(ao.getTheEnumArray());
            assertEquals(2, ao.getTheEnumArray().length);
            assertTrue(Arrays.equals(new Gender[]{Gender.male, Gender.female}, ao.getTheEnumArray()));

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
     * Update all values with multiple values
     */
    public void testUpdateMultiValues()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // update values
            tx.begin();
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            ao1.setTheStringArray(" A ", "BB", "CCC", "DDDD", "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577");
            ao1.setAnotherStringArray("secret33\n", "secret44\t", "secret55\r");
            ao1.setTheFloatArray(Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE);
            ao1.setTheFloatObjArray(new Float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE});
            ao1.setTheDoubleArray(Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE);
            ao1.setTheDoubleObjArray(new Double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE});
            ao1.setTheBooleanArray(false, true);
            ao1.setTheBooleanObjArray(true, false);
            ao1.setTheStreamedBooleanObjArray(true, true, false, false);
            ao1.setTheCharArray('\u0000', 'A', '\u9999');
            ao1.setTheCharObjArray(' ', 'Z');
            ao1.setTheByteArray((byte) 0x00, (byte) 0x7F);
            ao1.setTheStreamedByteArray((byte) 0x00, (byte) 0x01, (byte) 0x7F);
            ao1.setTheByteObjArray((byte) 0x46, (byte) 0x80, (byte) 0xFF);
            ao1.setTheShortArray(Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE);
            ao1.setTheShortObjArray(Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE);
            ao1.setTheIntArray(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
            ao1.setTheIntObjArray(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
            ao1.setTheLongArray(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
            ao1.setTheLongObjArray(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
            ao1.setTheBigIntegerArray(BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO);
            ao1.setTheBigDecimalArray(BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);
            ao1.setTheCurrencyArray(Currency.getInstance(Locale.JAPAN), Currency.getInstance(Locale.CHINA));
            ao1.setTheLocaleArray(Locale.FRANCE, Locale.CANADA);
            ao1.setTheTimeZoneArray(TimeZone.getTimeZone("CTT"), TimeZone.getTimeZone("AST"));
            ao1.setTheUUIDArray(new UUID(Long.MAX_VALUE, Long.MAX_VALUE), new UUID(0, 0), new UUID(Long.MIN_VALUE, Long.MIN_VALUE));
            ao1.setTheEnumArray(Gender.female, Gender.male);
            tx.commit();

            // assert new values
            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);

            assertNotNull(ao2.getTheStringArray());
            assertEquals(5, ao2.getTheStringArray().length);
            assertTrue(Arrays.asList(" A ", "BB", "CCC", "DDDD", "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577").containsAll(
                Arrays.asList(ao2.getTheStringArray())));

            assertNotNull(ao2.getAnotherStringArray());
            assertEquals(3, ao2.getAnotherStringArray().length);
            assertTrue(Arrays.asList("secret33\n", "secret44\t", "secret55\r").containsAll(Arrays.asList(ao2.getAnotherStringArray())));

            assertNotNull(ao2.getTheFloatArray());
            assertEquals(5, ao2.getTheFloatArray().length);
            assertTrue(Arrays.equals(new float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE}, ao2
                    .getTheFloatArray()));

            assertNotNull(ao2.getTheFloatObjArray());
            assertEquals(5, ao2.getTheFloatObjArray().length);
            assertTrue(Arrays.equals(new Float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE}, ao2
                    .getTheFloatObjArray()));

            assertNotNull(ao2.getTheDoubleArray());
            assertEquals(5, ao2.getTheDoubleArray().length);
            assertTrue(Arrays.equals(new double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE}, ao2
                    .getTheDoubleArray()));

            assertNotNull(ao2.getTheDoubleObjArray());
            assertEquals(5, ao2.getTheDoubleObjArray().length);
            assertTrue(Arrays.equals(new Double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE}, ao2
                    .getTheDoubleObjArray()));

            assertNotNull(ao2.getTheBooleanArray());
            assertEquals(2, ao2.getTheBooleanArray().length);
            assertTrue(Arrays.equals(new boolean[]{false, true}, ao2.getTheBooleanArray()));

            assertNotNull(ao2.getTheBooleanObjArray());
            assertEquals(2, ao2.getTheBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.TRUE, Boolean.FALSE}, ao2.getTheBooleanObjArray()));

            assertNotNull(ao2.getTheStreamedBooleanObjArray());
            assertEquals(4, ao2.getTheStreamedBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}, ao2
                    .getTheStreamedBooleanObjArray()));

            assertNotNull(ao2.getTheCharArray());
            assertEquals(3, ao2.getTheCharArray().length);
            assertTrue(Arrays.equals(new char[]{'\u0000', 'A', '\u9999'}, ao2.getTheCharArray()));

            assertNotNull(ao2.getTheCharObjArray());
            assertEquals(2, ao2.getTheCharObjArray().length);
            assertTrue(Arrays.equals(new Character[]{Character.valueOf(' '), Character.valueOf('Z')}, ao2.getTheCharObjArray()));

            assertNotNull(ao2.getTheByteArray());
            assertEquals(2, ao2.getTheByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x00, (byte) 0x7F}, ao2.getTheByteArray()));

            assertNotNull(ao2.getTheStreamedByteArray());
            assertEquals(3, ao2.getTheStreamedByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x7F}, ao2.getTheStreamedByteArray()));

            assertNotNull(ao2.getTheByteObjArray());
            assertEquals(3, ao2.getTheByteObjArray().length);
            assertTrue(Arrays.equals(new Byte[]{Byte.valueOf((byte) 0x46), Byte.valueOf((byte) 0x80), Byte.valueOf((byte) 0xFF)}, ao2.getTheByteObjArray()));

            assertNotNull(ao2.getTheShortArray());
            assertEquals(5, ao2.getTheShortArray().length);
            assertTrue(Arrays.equals(new short[]{Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE}, ao2
                    .getTheShortArray()));

            assertNotNull(ao2.getTheShortObjArray());
            assertEquals(5, ao2.getTheShortObjArray().length);
            assertTrue(Arrays.equals(new Short[]{Short.valueOf(Short.MIN_VALUE), Short.valueOf((short) -1), Short.valueOf((short) 0),
                    Short.valueOf((short) 1), Short.valueOf(Short.MAX_VALUE)}, ao2.getTheShortObjArray()));

            assertNotNull(ao2.getTheIntArray());
            assertEquals(5, ao2.getTheIntArray().length);
            assertTrue(Arrays.equals(new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}, ao2.getTheIntArray()));

            assertNotNull(ao2.getTheIntObjArray());
            assertEquals(5, ao2.getTheIntObjArray().length);
            assertTrue(Arrays.equals(new Integer[]{Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(-1), Integer.valueOf(0), Integer.valueOf(1),
                    Integer.valueOf(Integer.MAX_VALUE)}, ao2.getTheIntObjArray()));

            assertNotNull(ao2.getTheLongArray());
            assertEquals(5, ao2.getTheLongArray().length);
            assertTrue(Arrays.equals(new long[]{Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE}, ao2.getTheLongArray()));

            assertNotNull(ao2.getTheLongObjArray());
            assertEquals(5, ao2.getTheLongObjArray().length);
            assertTrue(Arrays.equals(new Long[]{Long.valueOf(Long.MIN_VALUE), Long.valueOf(-1L), Long.valueOf(0L), Long.valueOf(1L),
                    Long.valueOf(Long.MAX_VALUE)}, ao2.getTheLongObjArray()));

            assertNotNull(ao2.getTheBigIntegerArray());
            assertEquals(3, ao2.getTheBigIntegerArray().length);
            assertTrue(Arrays.equals(new BigInteger[]{BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO}, ao2.getTheBigIntegerArray()));

            assertNotNull(ao2.getTheBigDecimalArray());
            assertEquals(3, ao2.getTheBigDecimalArray().length);
            assertTrue(Arrays.equals(new BigDecimal[]{BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO}, ao2.getTheBigDecimalArray()));

            assertNotNull(ao2.getTheCurrencyArray());
            assertEquals(2, ao2.getTheCurrencyArray().length);
            assertTrue(Arrays.equals(new Currency[]{Currency.getInstance(Locale.JAPAN), Currency.getInstance(Locale.CHINA)}, ao2
                    .getTheCurrencyArray()));

            assertNotNull(ao2.getTheLocaleArray());
            assertEquals(2, ao2.getTheLocaleArray().length);
            assertTrue(Arrays.equals(new Locale[]{Locale.FRANCE, Locale.CANADA}, ao2.getTheLocaleArray()));

            assertNotNull(ao2.getTheTimeZoneArray());
            assertEquals(2, ao2.getTheTimeZoneArray().length);
            assertTrue(Arrays.equals(new TimeZone[]{TimeZone.getTimeZone("CTT"), TimeZone.getTimeZone("AST")}, ao2.getTheTimeZoneArray()));

            assertNotNull(ao2.getTheUUIDArray());
            assertEquals(3, ao2.getTheUUIDArray().length);
            assertTrue(Arrays.equals(new UUID[]{new UUID(Long.MAX_VALUE, Long.MAX_VALUE), new UUID(0, 0),
                    new UUID(Long.MIN_VALUE, Long.MIN_VALUE)}, ao2.getTheUUIDArray()));

            assertNotNull(ao2.getTheEnumArray());
            assertEquals(2, ao2.getTheEnumArray().length);
            assertTrue(Arrays.equals(new Gender[]{Gender.female, Gender.male}, ao2.getTheEnumArray()));
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
     * Detached update all values with multiple values
     */
    public void testUpdateMultiValuesDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo1 = pm.detachCopy(ao1);
            tx.commit();
            pm.close();

            // update values
            detachedAo1.setTheStringArray(" A ", "BB", "CCC", "DDDD", "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577");
            detachedAo1.setAnotherStringArray("secret33\n", "secret44\t", "secret55\r");
            detachedAo1.setTheFloatArray(Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE);
            detachedAo1.setTheFloatObjArray(new Float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE});
            detachedAo1.setTheDoubleArray(Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE);
            detachedAo1.setTheDoubleObjArray(new Double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE,
                    Double.MAX_VALUE});
            detachedAo1.setTheBooleanArray(false, true);
            detachedAo1.setTheBooleanObjArray(true, false);
            detachedAo1.setTheStreamedBooleanObjArray(true, true, false, false);
            detachedAo1.setTheCharArray('\u0000', 'A', '\u9999');
            detachedAo1.setTheCharObjArray(' ', 'Z');
            detachedAo1.setTheByteArray((byte) 0x00, (byte) 0x7F);
            detachedAo1.setTheStreamedByteArray((byte) 0x00, (byte) 0x01, (byte) 0x7F);
            detachedAo1.setTheByteObjArray((byte) 0x46, (byte) 0x80, (byte) 0xFF);
            detachedAo1.setTheShortArray(Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE);
            detachedAo1.setTheShortObjArray(Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE);
            detachedAo1.setTheIntArray(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
            detachedAo1.setTheIntObjArray(Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE);
            detachedAo1.setTheLongArray(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
            detachedAo1.setTheLongObjArray(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE);
            detachedAo1.setTheBigIntegerArray(BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO);
            detachedAo1.setTheBigDecimalArray(BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO);
            detachedAo1.setTheCurrencyArray(Currency.getInstance(Locale.JAPAN), Currency.getInstance(Locale.CHINA));
            detachedAo1.setTheLocaleArray(Locale.FRANCE, Locale.CANADA);
            detachedAo1.setTheTimeZoneArray(TimeZone.getTimeZone("CTT"), TimeZone.getTimeZone("AST"));
            detachedAo1.setTheUUIDArray(new UUID(Long.MAX_VALUE, Long.MAX_VALUE), new UUID(0, 0), new UUID(Long.MIN_VALUE, Long.MIN_VALUE));
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAo1);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo2 = pm.detachCopy(ao2);
            tx.commit();
            pm.close();

            // assert new values
            assertNotNull(detachedAo2.getTheStringArray());
            assertEquals(5, detachedAo2.getTheStringArray().length);
            assertTrue(Arrays.asList(" A ", "BB", "CCC", "DDDD", "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577").containsAll(
                Arrays.asList(detachedAo2.getTheStringArray())));

            assertNotNull(detachedAo2.getAnotherStringArray());
            assertEquals(3, detachedAo2.getAnotherStringArray().length);
            assertTrue(Arrays.asList("secret33\n", "secret44\t", "secret55\r").containsAll(
                Arrays.asList(detachedAo2.getAnotherStringArray())));

            assertNotNull(detachedAo2.getTheFloatArray());
            assertEquals(5, detachedAo2.getTheFloatArray().length);
            assertTrue(Arrays.equals(new float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE},
                detachedAo2.getTheFloatArray()));

            assertNotNull(detachedAo2.getTheFloatObjArray());
            assertEquals(5, detachedAo2.getTheFloatObjArray().length);
            assertTrue(Arrays.equals(new Float[]{Float.MIN_VALUE, 1 / Float.MIN_VALUE, 0F, 1 / Float.MAX_VALUE, Float.MAX_VALUE},
                detachedAo2.getTheFloatObjArray()));

            assertNotNull(detachedAo2.getTheDoubleArray());
            assertEquals(5, detachedAo2.getTheDoubleArray().length);
            assertTrue(Arrays.equals(new double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE},
                detachedAo2.getTheDoubleArray()));

            assertNotNull(detachedAo2.getTheDoubleObjArray());
            assertEquals(5, detachedAo2.getTheDoubleObjArray().length);
            assertTrue(Arrays.equals(new Double[]{Double.MIN_VALUE, 1 / Double.MIN_VALUE, 0D, 1 / Double.MAX_VALUE, Double.MAX_VALUE},
                detachedAo2.getTheDoubleObjArray()));

            assertNotNull(detachedAo2.getTheBooleanArray());
            assertEquals(2, detachedAo2.getTheBooleanArray().length);
            assertTrue(Arrays.equals(new boolean[]{false, true}, detachedAo2.getTheBooleanArray()));

            assertNotNull(detachedAo2.getTheBooleanObjArray());
            assertEquals(2, detachedAo2.getTheBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.TRUE, Boolean.FALSE}, detachedAo2.getTheBooleanObjArray()));

            assertNotNull(detachedAo2.getTheStreamedBooleanObjArray());
            assertEquals(4, detachedAo2.getTheStreamedBooleanObjArray().length);
            assertTrue(Arrays.equals(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE}, detachedAo2
                    .getTheStreamedBooleanObjArray()));

            assertNotNull(detachedAo2.getTheCharArray());
            assertEquals(3, detachedAo2.getTheCharArray().length);
            assertTrue(Arrays.equals(new char[]{'\u0000', 'A', '\u9999'}, detachedAo2.getTheCharArray()));

            assertNotNull(detachedAo2.getTheCharObjArray());
            assertEquals(2, detachedAo2.getTheCharObjArray().length);
            assertTrue(Arrays.equals(new Character[]{Character.valueOf(' '), Character.valueOf('Z')}, detachedAo2.getTheCharObjArray()));

            assertNotNull(detachedAo2.getTheByteArray());
            assertEquals(2, detachedAo2.getTheByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x00, (byte) 0x7F}, detachedAo2.getTheByteArray()));

            assertNotNull(detachedAo2.getTheStreamedByteArray());
            assertEquals(3, detachedAo2.getTheStreamedByteArray().length);
            assertTrue(Arrays.equals(new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x7F}, detachedAo2.getTheStreamedByteArray()));

            assertNotNull(detachedAo2.getTheByteObjArray());
            assertEquals(3, detachedAo2.getTheByteObjArray().length);
            assertTrue(Arrays.equals(new Byte[]{Byte.valueOf((byte) 0x46), Byte.valueOf((byte) 0x80), Byte.valueOf((byte) 0xFF)}, detachedAo2
                    .getTheByteObjArray()));

            assertNotNull(detachedAo2.getTheShortArray());
            assertEquals(5, detachedAo2.getTheShortArray().length);
            assertTrue(Arrays.equals(new short[]{Short.MIN_VALUE, (short) -1, (short) 0, (short) 1, Short.MAX_VALUE}, detachedAo2
                    .getTheShortArray()));

            assertNotNull(detachedAo2.getTheShortObjArray());
            assertEquals(5, detachedAo2.getTheShortObjArray().length);
            assertTrue(Arrays.equals(new Short[]{Short.valueOf(Short.MIN_VALUE), Short.valueOf((short) -1), Short.valueOf((short) 0),
                    Short.valueOf((short) 1), Short.valueOf(Short.MAX_VALUE)}, detachedAo2.getTheShortObjArray()));

            assertNotNull(detachedAo2.getTheIntArray());
            assertEquals(5, detachedAo2.getTheIntArray().length);
            assertTrue(Arrays.equals(new int[]{Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE}, detachedAo2.getTheIntArray()));

            assertNotNull(detachedAo2.getTheIntObjArray());
            assertEquals(5, detachedAo2.getTheIntObjArray().length);
            assertTrue(Arrays.equals(new Integer[]{Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(-1), Integer.valueOf(0), Integer.valueOf(1),
                    Integer.valueOf(Integer.MAX_VALUE)}, detachedAo2.getTheIntObjArray()));

            assertNotNull(detachedAo2.getTheLongArray());
            assertEquals(5, detachedAo2.getTheLongArray().length);
            assertTrue(Arrays.equals(new long[]{Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE}, detachedAo2.getTheLongArray()));

            assertNotNull(detachedAo2.getTheLongObjArray());
            assertEquals(5, detachedAo2.getTheLongObjArray().length);
            assertTrue(Arrays.equals(new Long[]{Long.valueOf(Long.MIN_VALUE), Long.valueOf(-1L), Long.valueOf(0L), Long.valueOf(1L),
                    Long.valueOf(Long.MAX_VALUE)}, detachedAo2.getTheLongObjArray()));

            assertNotNull(detachedAo2.getTheBigIntegerArray());
            assertEquals(3, detachedAo2.getTheBigIntegerArray().length);
            assertTrue(Arrays
                    .equals(new BigInteger[]{BigInteger.ONE, BigInteger.TEN, BigInteger.ZERO}, detachedAo2.getTheBigIntegerArray()));

            assertNotNull(detachedAo2.getTheBigDecimalArray());
            assertEquals(3, detachedAo2.getTheBigDecimalArray().length);
            assertTrue(Arrays
                    .equals(new BigDecimal[]{BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO}, detachedAo2.getTheBigDecimalArray()));

            assertNotNull(detachedAo2.getTheCurrencyArray());
            assertEquals(2, detachedAo2.getTheCurrencyArray().length);
            assertTrue(Arrays.equals(new Currency[]{Currency.getInstance(Locale.JAPAN), Currency.getInstance(Locale.CHINA)}, detachedAo2
                    .getTheCurrencyArray()));

            assertNotNull(detachedAo2.getTheLocaleArray());
            assertEquals(2, detachedAo2.getTheLocaleArray().length);
            assertTrue(Arrays.equals(new Locale[]{Locale.FRANCE, Locale.CANADA}, detachedAo2.getTheLocaleArray()));

            assertNotNull(detachedAo2.getTheTimeZoneArray());
            assertEquals(2, detachedAo2.getTheTimeZoneArray().length);
            assertTrue(Arrays.equals(new TimeZone[]{TimeZone.getTimeZone("CTT"), TimeZone.getTimeZone("AST")}, detachedAo2
                    .getTheTimeZoneArray()));

            assertNotNull(detachedAo2.getTheUUIDArray());
            assertEquals(3, detachedAo2.getTheUUIDArray().length);
            assertTrue(Arrays.equals(new UUID[]{new UUID(Long.MAX_VALUE, Long.MAX_VALUE), new UUID(0, 0),
                    new UUID(Long.MIN_VALUE, Long.MIN_VALUE)}, detachedAo2.getTheUUIDArray()));
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
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            ao1.setTheStringArray("ABCxyz");
            ao1.setAnotherStringArray("secret44");
            ao1.setTheFloatArray(-1.2F);
            ao1.setTheFloatObjArray(2.3F);
            ao1.setTheDoubleArray(999.99);
            ao1.setTheDoubleObjArray(-111.11);
            ao1.setTheBooleanArray(true);
            ao1.setTheBooleanObjArray(false);
            ao1.setTheStreamedBooleanObjArray(false);
            ao1.setTheCharArray('u');
            ao1.setTheCharObjArray('\u52F5');
            ao1.setTheByteArray((byte) 0x45);
            ao1.setTheStreamedByteArray((byte) 0x46);
            ao1.setTheByteObjArray((byte) 0x46);
            ao1.setTheShortArray((short) 7);
            ao1.setTheShortObjArray((short) -33);
            ao1.setTheIntArray(-3399);
            ao1.setTheIntObjArray(4763);
            ao1.setTheLongArray(-1L);
            ao1.setTheLongObjArray(1L);
            ao1.setTheBigIntegerArray(BigInteger.ONE);
            ao1.setTheBigDecimalArray(BigDecimal.TEN);
            ao1.setTheCurrencyArray(Currency.getInstance(Locale.KOREA));
            ao1.setTheLocaleArray(Locale.KOREA);
            ao1.setTheTimeZoneArray(TimeZone.getTimeZone("GMT-11:00"));
            UUID randomUUID = UUID.randomUUID();
            ao1.setTheUUIDArray(randomUUID);
            ao1.setTheEnumArray(Gender.female);
            tx.commit();

            // assert new values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);

            assertNotNull(ao2.getTheStringArray());
            assertEquals(1, ao2.getTheStringArray().length);
            assertEquals("ABCxyz", ao2.getTheStringArray()[0]);

            assertNotNull(ao2.getAnotherStringArray());
            assertEquals(1, ao2.getAnotherStringArray().length);
            assertEquals("secret44", ao2.getAnotherStringArray()[0]);

            assertNotNull(ao2.getTheFloatArray());
            assertEquals(1, ao2.getTheFloatArray().length);
            assertEquals(-1.2F, ao2.getTheFloatArray()[0], 0.1);

            assertNotNull(ao2.getTheFloatObjArray());
            assertEquals(1, ao2.getTheFloatObjArray().length);
            assertEquals(2.3F, ao2.getTheFloatObjArray()[0], 0.1);

            assertNotNull(ao2.getTheDoubleArray());
            assertEquals(1, ao2.getTheDoubleArray().length);
            assertEquals(999.99, ao2.getTheDoubleArray()[0], 0.1);

            assertNotNull(ao2.getTheDoubleObjArray());
            assertEquals(1, ao2.getTheDoubleObjArray().length);
            assertEquals(-111.11, ao2.getTheDoubleObjArray()[0], 0.1);

            assertNotNull(ao2.getTheBooleanArray());
            assertEquals(1, ao2.getTheBooleanArray().length);
            assertEquals(true, ao2.getTheBooleanArray()[0]);

            assertNotNull(ao2.getTheBooleanObjArray());
            assertEquals(1, ao2.getTheBooleanObjArray().length);
            assertEquals(Boolean.FALSE, ao2.getTheBooleanObjArray()[0]);

            assertNotNull(ao2.getTheStreamedBooleanObjArray());
            assertEquals(1, ao2.getTheStreamedBooleanObjArray().length);
            assertEquals(Boolean.FALSE, ao2.getTheStreamedBooleanObjArray()[0]);

            assertNotNull(ao2.getTheCharArray());
            assertEquals(1, ao2.getTheCharArray().length);
            assertEquals('u', ao2.getTheCharArray()[0]);

            assertNotNull(ao2.getTheCharObjArray());
            assertEquals(1, ao2.getTheCharObjArray().length);
            assertEquals(Character.valueOf('\u52F5'), ao2.getTheCharObjArray()[0]);

            assertNotNull(ao2.getTheByteArray());
            assertEquals(1, ao2.getTheByteArray().length);
            assertEquals((byte) 0x45, ao2.getTheByteArray()[0]);

            assertNotNull(ao2.getTheStreamedByteArray());
            assertEquals(1, ao2.getTheStreamedByteArray().length);
            assertEquals((byte) 0x46, ao2.getTheStreamedByteArray()[0]);

            assertNotNull(ao2.getTheByteObjArray());
            assertEquals(1, ao2.getTheByteObjArray().length);
            assertEquals(Byte.valueOf((byte) 0x46), ao2.getTheByteObjArray()[0]);

            assertNotNull(ao2.getTheShortArray());
            assertEquals(1, ao2.getTheShortArray().length);
            assertEquals((short) 7, ao2.getTheShortArray()[0]);

            assertNotNull(ao2.getTheShortObjArray());
            assertEquals(1, ao2.getTheShortObjArray().length);
            assertEquals(Short.valueOf((short) -33), ao2.getTheShortObjArray()[0]);

            assertNotNull(ao2.getTheIntArray());
            assertEquals(1, ao2.getTheIntArray().length);
            assertEquals(-3399, ao2.getTheIntArray()[0]);

            assertNotNull(ao2.getTheIntObjArray());
            assertEquals(1, ao2.getTheIntObjArray().length);
            assertEquals(Integer.valueOf(4763), ao2.getTheIntObjArray()[0]);

            assertNotNull(ao2.getTheLongArray());
            assertEquals(1, ao2.getTheLongArray().length);
            assertEquals(-1L, ao2.getTheLongArray()[0]);

            assertNotNull(ao2.getTheLongObjArray());
            assertEquals(1, ao2.getTheLongObjArray().length);
            assertEquals(Long.valueOf(1L), ao2.getTheLongObjArray()[0]);

            assertNotNull(ao2.getTheBigIntegerArray());
            assertEquals(1, ao2.getTheBigIntegerArray().length);
            assertEquals(BigInteger.ONE, ao2.getTheBigIntegerArray()[0]);

            assertNotNull(ao2.getTheBigDecimalArray());
            assertEquals(1, ao2.getTheBigDecimalArray().length);
            assertEquals(BigDecimal.TEN, ao2.getTheBigDecimalArray()[0]);

            assertNotNull(ao2.getTheCurrencyArray());
            assertEquals(1, ao2.getTheCurrencyArray().length);
            assertEquals(Currency.getInstance(Locale.KOREA), ao2.getTheCurrencyArray()[0]);

            assertNotNull(ao2.getTheLocaleArray());
            assertEquals(1, ao2.getTheLocaleArray().length);
            assertEquals(Locale.KOREA, ao2.getTheLocaleArray()[0]);

            assertNotNull(ao2.getTheTimeZoneArray());
            assertEquals(1, ao2.getTheTimeZoneArray().length);
            assertEquals(TimeZone.getTimeZone("GMT-11:00"), ao2.getTheTimeZoneArray()[0]);

            assertNotNull(ao2.getTheUUIDArray());
            assertEquals(1, ao2.getTheUUIDArray().length);
            assertEquals(randomUUID, ao2.getTheUUIDArray()[0]);

            assertNotNull(ao2.getTheEnumArray());
            assertEquals(1, ao2.getTheEnumArray().length);
            assertEquals(Gender.female, ao2.getTheEnumArray()[0]);
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
     * Update all values to null
     */
    public void testUpdateToNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // update values
            tx.begin();
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            ao1.setTheStringArray((String[]) null);
            ao1.setAnotherStringArray((String[]) null);
            ao1.setTheFloatArray(null);
            ao1.setTheFloatObjArray((Float[]) null);
            ao1.setTheDoubleArray(null);
            ao1.setTheDoubleObjArray((Double[]) null);
            ao1.setTheBooleanArray(null);
            ao1.setTheStreamedBooleanObjArray((Boolean[]) null);
            ao1.setTheCharArray(null);
            ao1.setTheCharObjArray((Character[]) null);
            ao1.setTheByteArray(null);
            ao1.setTheStreamedByteArray(null);
            ao1.setTheByteObjArray((Byte[]) null);
            ao1.setTheShortArray(null);
            ao1.setTheShortObjArray((Short[]) null);
            ao1.setTheIntArray(null);
            ao1.setTheIntObjArray((Integer[]) null);
            ao1.setTheLongArray(null);
            ao1.setTheLongObjArray((Long[]) null);
            ao1.setTheBigIntegerArray((BigInteger[]) null);
            ao1.setTheBigDecimalArray((BigDecimal[]) null);
            ao1.setTheCurrencyArray((Currency[]) null);
            ao1.setTheLocaleArray((Locale[]) null);
            ao1.setTheTimeZoneArray((TimeZone[]) null);
            ao1.setTheUUIDArray((UUID[]) null);
            ao1.setTheEnumArray((Gender[]) null);
            tx.commit();

            // assert null values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);
            assertNull(ao2.getTheStringArray());
            assertNull(ao2.getAnotherStringArray());
            assertNull(ao2.getTheFloatArray());
            assertNull(ao2.getTheFloatObjArray());
            assertNull(ao2.getTheDoubleArray());
            assertNull(ao2.getTheDoubleObjArray());
            assertNull(ao2.getTheBooleanArray());
            assertNull(ao2.getTheStreamedBooleanObjArray());
            assertNull(ao2.getTheCharArray());
            assertNull(ao2.getTheCharObjArray());
            assertNull(ao2.getTheByteArray());
            assertNull(ao2.getTheStreamedByteArray());
            assertNull(ao2.getTheByteObjArray());
            assertNull(ao2.getTheShortArray());
            assertNull(ao2.getTheShortObjArray());
            assertNull(ao2.getTheShortArray());
            assertNull(ao2.getTheShortObjArray());
            assertNull(ao2.getTheIntArray());
            assertNull(ao2.getTheIntObjArray());
            assertNull(ao2.getTheLongArray());
            assertNull(ao2.getTheLongObjArray());
            assertNull(ao2.getTheBigIntegerArray());
            assertNull(ao2.getTheBigDecimalArray());
            assertNull(ao2.getTheCurrencyArray());
            assertNull(ao2.getTheLocaleArray());
            assertNull(ao2.getTheTimeZoneArray());
            assertNull(ao2.getTheUUIDArray());
            assertNull(ao2.getTheEnumArray());
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
     * Tests the update of string array.
     * <ul>
     * <li>set to one value
     * <li>set to null
     * <li>set to another value with multi-byte characters and long value
     * <li>set to empty array (=null)
     * </ul>
     */
    public void testUpdateStringArray()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // assert initial value
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            assertNotNull(ao1.getTheStringArray());
            assertEquals(3, ao1.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{"AAA", "BBB", "CCC"}).containsAll(Arrays.asList(ao1.getTheStringArray())));
            assertEquals(2, ao1.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{"secret1", "secret2"}).containsAll(Arrays.asList(ao1.getAnotherStringArray())));

            // set to one value
            ao1.setTheStringArray(new String[]{"EEE"});
            ao1.setAnotherStringArray(new String[]{"secret3"});
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);
            assertEquals(1, ao2.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{"EEE"}).containsAll(Arrays.asList(ao2.getTheStringArray())));
            assertEquals(1, ao2.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{"secret3"}).containsAll(Arrays.asList(ao2.getAnotherStringArray())));

            // set to null
            ao2.setTheStringArray((String[]) null);
            ao2.setAnotherStringArray((String[]) null);
            tx.commit();

            // assert null value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao3 = (ArrayHolder) pm.getObjectById(id);
            assertNull(ao3.getTheStringArray());
            assertNull(ao3.getAnotherStringArray());

            // set to another value
            String v1 = "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577";
            StringBuffer v2 = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                v2.append('W');
            }
            ao3.setTheStringArray(new String[]{v1, v2.toString()});
            ao3.setAnotherStringArray(new String[]{v1 + "B", v2.toString() + "B"});
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao4 = (ArrayHolder) pm.getObjectById(id);
            assertEquals(2, ao4.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{v1, v2.toString()}).containsAll(Arrays.asList(ao4.getTheStringArray())));
            assertEquals(2, ao4.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{v1 + "B", v2.toString() + "B"}).containsAll(Arrays.asList(ao4.getAnotherStringArray())));

            // set to empty array
            ao4.setTheStringArray(new String[]{});
            ao4.setAnotherStringArray(new String[]{});
            tx.commit();

            // assert empty array, is handled as null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao5 = (ArrayHolder) pm.getObjectById(id);
            assertNull(ao5.getTheStringArray());
            assertNull(ao5.getAnotherStringArray());
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
     * Tests the detached update of string array.
     * <ul>
     * <li>set to one value
     * <li>set to null
     * <li>set to another value with multi-byte characters and long value
     * <li>set to empty array (=null)
     * </ul>
     */
    public void testUpdateStringArrayDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ArrayHolder ao1 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo1 = pm.detachCopy(ao1);
            tx.commit();
            pm.close();

            // assert initial value
            assertNotNull(detachedAo1.getTheStringArray());
            assertEquals(3, detachedAo1.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{"AAA", "BBB", "CCC"}).containsAll(Arrays.asList(detachedAo1.getTheStringArray())));
            assertEquals(2, detachedAo1.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{"secret1", "secret2"}).containsAll(Arrays.asList(detachedAo1.getAnotherStringArray())));

            // set to one value
            detachedAo1.setTheStringArray(new String[]{"EEE"});
            detachedAo1.setAnotherStringArray(new String[]{"secret3"});
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAo1);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao2 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo2 = pm.detachCopy(ao2);
            tx.commit();
            pm.close();

            // assert new value
            assertEquals(1, detachedAo2.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{"EEE"}).containsAll(Arrays.asList(detachedAo2.getTheStringArray())));
            assertEquals(1, detachedAo2.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{"secret3"}).containsAll(Arrays.asList(detachedAo2.getAnotherStringArray())));

            // set to null
            detachedAo2.setTheStringArray((String[]) null);
            detachedAo2.setAnotherStringArray((String[]) null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAo2);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao3 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo3 = pm.detachCopy(ao3);
            tx.commit();
            pm.close();

            // assert null value
            assertNull(detachedAo3.getTheStringArray());
            assertNull(detachedAo3.getAnotherStringArray());

            // set to another value
            String v1 = "\u00E4\u00F6\u00FC\u00DF\u90E8\u9577";
            StringBuffer v2 = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                v2.append('W');
            }
            detachedAo3.setTheStringArray(new String[]{v1, v2.toString()});
            detachedAo3.setAnotherStringArray(new String[]{v1 + "B", v2.toString() + "B"});
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAo3);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao4 = (ArrayHolder) pm.getObjectById(id);
            ArrayHolder detachedAo4 = pm.detachCopy(ao4);
            tx.commit();
            pm.close();

            // assert new value
            assertEquals(2, detachedAo4.getTheStringArray().length);
            assertTrue(Arrays.asList(new String[]{v1, v2.toString()}).containsAll(Arrays.asList(detachedAo4.getTheStringArray())));
            assertEquals(2, detachedAo4.getAnotherStringArray().length);
            assertTrue(Arrays.asList(new String[]{v1 + "B", v2.toString() + "B"}).containsAll(
                Arrays.asList(detachedAo4.getAnotherStringArray())));

            // set to empty array
            detachedAo4.setTheStringArray(new String[]{});
            detachedAo4.setAnotherStringArray(new String[]{});
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAo4);
            tx.commit();
            pm.close();

            // assert empty array, is handled as null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ao5 = (ArrayHolder) pm.getObjectById(id);
            assertNull(ao5.getTheStringArray());
            assertNull(ao5.getAnotherStringArray());
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

    public void testOrderingIndex()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ArrayHolder ah = new ArrayHolder();
            ah.setPrimaryKey(new UUID(9876L, 5432L));
            ah.setOrderedIntArray(new int[]{8, 6, 4, 2, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, 2, 4, 6, 8});
            pm.makePersistent(ah);
            Object oId = pm.getObjectId(ah);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ah2 = (ArrayHolder) pm.getObjectById(oId);
            // check exact order
            assertEquals(8, ah2.getOrderedIntArray()[0]);
            assertEquals(6, ah2.getOrderedIntArray()[1]);
            assertEquals(4, ah2.getOrderedIntArray()[2]);
            assertEquals(2, ah2.getOrderedIntArray()[3]);
            assertEquals(Integer.MAX_VALUE, ah2.getOrderedIntArray()[4]);
            assertEquals(0, ah2.getOrderedIntArray()[5]);
            assertEquals(Integer.MIN_VALUE, ah2.getOrderedIntArray()[6]);
            assertEquals(2, ah2.getOrderedIntArray()[7]); // duplicate value!
            assertEquals(4, ah2.getOrderedIntArray()[8]); // duplicate value!
            assertEquals(6, ah2.getOrderedIntArray()[9]); // duplicate value!
            assertEquals(8, ah2.getOrderedIntArray()[10]); // duplicate value!

            // modify order
            ah2.setOrderedIntArray(new int[]{8, 6, 4, 2, Integer.MAX_VALUE, 0, Integer.MIN_VALUE, 8, 6, 4, 2});
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ArrayHolder ah3 = (ArrayHolder) pm.getObjectById(oId);
            // check new order
            assertEquals(8, ah3.getOrderedIntArray()[0]);
            assertEquals(6, ah3.getOrderedIntArray()[1]);
            assertEquals(4, ah3.getOrderedIntArray()[2]);
            assertEquals(2, ah3.getOrderedIntArray()[3]);
            assertEquals(Integer.MAX_VALUE, ah3.getOrderedIntArray()[4]);
            assertEquals(0, ah3.getOrderedIntArray()[5]);
            assertEquals(Integer.MIN_VALUE, ah3.getOrderedIntArray()[6]);
            assertEquals(8, ah3.getOrderedIntArray()[7]); // moved
            assertEquals(6, ah3.getOrderedIntArray()[8]); // moved
            assertEquals(4, ah3.getOrderedIntArray()[9]); // moved
            assertEquals(2, ah3.getOrderedIntArray()[10]); // moved
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
}
