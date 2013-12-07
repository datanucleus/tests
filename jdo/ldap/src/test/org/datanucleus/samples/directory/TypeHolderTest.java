/**********************************************************************
Copyright (c) 2008 Stefan Seelmann and others. All rights reserved.
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
 * Tests the mapping of of strings, primitives, wrappers and other basic types
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
 */
public class TypeHolderTest extends JDOPersistenceTestCase
{
    Object id;

    public TypeHolderTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(TypeHolder.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            TypeHolder dto = new TypeHolder();
            dto.setPrimaryKey(88);
            dto.setTheString("ABCxyz");
            dto.setAnotherString("secret");
            dto.setTheFloat(1.2F);
            dto.setTheFloatObj(new Float(2.3F));
            dto.setTheDouble(1234567.890D);
            dto.setTheDoubleObj(new Double(2345678.901D));
            dto.setTheBoolean(true);
            dto.setTheBooleanObj(Boolean.FALSE);
            dto.setTheChar('Z');
            dto.setTheCharObj(new Character('Y'));
            dto.setTheByte((byte) 0x41);
            dto.setTheByteObj(new Byte((byte) 0x42));
            dto.setTheShort((short) 1);
            dto.setTheShortObj(new Short((short) 11));
            dto.setTheInt((int) 2);
            dto.setTheIntObj((int) 22);
            dto.setTheLong((long) 3);
            dto.setTheLongObj((long) 33);
            dto.setTheBigInteger(new BigInteger("1234567890"));
            dto.setTheBigDecimal(new BigDecimal("12345.67890"));
            dto.setTheCurrency(Currency.getInstance(Locale.US));
            dto.setTheLocale(Locale.GERMANY);
            dto.setTheTimeZone(TimeZone.getTimeZone("GMT"));
            dto.setTheUUID(new UUID(5, 7));
            dto.setTheDate(new Date(123456789000L)); // strip millis
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(23456780000L); // strip millis
            dto.setTheCalendar(calendar);
            dto.setTheEnum(Gender.female);
            pm.makePersistent(dto);
            id = pm.getObjectId(dto);
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
        clean(TypeHolder.class);
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
            TypeHolder dto = pm.getObjectById(TypeHolder.class, 88);
            assertEquals("ABCxyz", dto.getTheString());
            assertEquals("secret", dto.getAnotherString());
            assertEquals(1.2F, dto.getTheFloat(), 0.1F);
            assertEquals(new Float(2.3F), dto.getTheFloatObj(), 0.1F);
            assertEquals(1234567.890D, dto.getTheDouble(), 0.1D);
            assertEquals(new Double(2345678.901D), dto.getTheDoubleObj(), 0.1D);
            assertEquals(true, dto.isTheBoolean());
            assertEquals(Boolean.FALSE, dto.getTheBooleanObj());
            assertEquals('Z', dto.getTheChar());
            assertEquals(new Character('Y'), dto.getTheCharObj());
            assertEquals((byte) 0x41, dto.getTheByte());
            assertEquals(new Byte((byte) 0x42), dto.getTheByteObj());
            assertEquals((short) 1, dto.getTheShort());
            assertEquals(new Short((short) 11), dto.getTheShortObj());
            assertEquals((int) 2, dto.getTheInt());
            assertEquals(new Integer((int) 22), dto.getTheIntObj());
            assertEquals((long) 3, dto.getTheLong());
            assertEquals(new Long((long) 33), dto.getTheLongObj());
            assertEquals(new BigInteger("1234567890"), dto.getTheBigInteger());
            assertEquals(new BigDecimal("12345.67890"), dto.getTheBigDecimal());
            assertEquals(Currency.getInstance(Locale.US), dto.getTheCurrency());
            assertEquals(Locale.GERMANY, dto.getTheLocale());
            assertEquals(TimeZone.getTimeZone("GMT"), dto.getTheTimeZone());
            assertEquals(new UUID(5, 7), dto.getTheUUID());
            assertEquals(new Date(123456789000L).getTime(), dto.getTheDate().getTime());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(23456780000L);
            assertEquals(calendar, dto.getTheCalendar());
            assertEquals(Gender.female, dto.getTheEnum());

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
     * Tests the update of string values.
     * <ul>
     * <li>set to long value
     * <li>set to multi-byte unicode
     * <li>set to null
     * </ul>
     */
    public void testUpdateString()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value ABCxyz
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals("ABCxyz", dto1.getTheString());
            assertEquals("secret", dto1.getAnotherString());

            // set to long value
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 1000; i++)
            {
                sb.append('W');
            }
            dto1.setTheString(sb.toString());
            dto1.setAnotherString(sb.toString() + "B");
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(sb.toString(), dto2.getTheString());
            assertEquals(sb.toString() + "B", dto2.getAnotherString());

            // set to multi-byte unicode value
            dto2.setTheString("\u00E4\u00F6\u00FC\u00DF\u90E8\u9577");
            dto2.setAnotherString("\u00E4\u00F6\u00FC\u00DF\u90E8\u9577" + "B");
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals("\u00E4\u00F6\u00FC\u00DF\u90E8\u9577", dto3.getTheString());
            assertEquals("\u00E4\u00F6\u00FC\u00DF\u90E8\u9577" + "B", dto3.getAnotherString());

            // set to null
            dto3.setTheString(null);
            dto3.setAnotherString(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheString());
            assertNull(dto4.getAnotherString());
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
     * Tests the update of double values and Double objects.
     * <ul>
     * <li>set to max value
     * <li>set to 0.0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateDouble()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 1234567.890
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(1234567.890, dto1.getTheDouble(), 0.01);
            assertEquals(new Double(2345678.901D), dto1.getTheDoubleObj(), 0.1D);

            // set to max value
            dto1.setTheDouble(Double.MAX_VALUE);
            dto1.setTheDoubleObj(new Double(Double.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Double.MAX_VALUE, dto2.getTheDouble(), 0.01);
            assertEquals(new Double(Double.MAX_VALUE), dto2.getTheDoubleObj(), 0.01);

            // set to 0
            dto2.setTheDouble(0.0D);
            dto2.setTheDoubleObj(new Double(0.0D));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0.0F, dto3.getTheDouble(), 0.01);
            assertEquals(new Double(0.0F), dto3.getTheDoubleObj(), 0.01);

            // set to null
            dto3.setTheDoubleObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheDoubleObj());

            // set to min value
            dto4.setTheDouble(Double.MIN_VALUE);
            dto4.setTheDoubleObj(new Double(Double.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Double.MIN_VALUE, dto5.getTheDouble(), 0.01);
            assertEquals(new Double(Double.MIN_VALUE), dto5.getTheDoubleObj(), 0.01);
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
     * Tests the update of float values.
     * <ul>
     * <li>set to max value
     * <li>set to 0.0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateFloat()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 1.2
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(1.2F, dto1.getTheFloat(), 0.01F);
            assertEquals(new Float(2.3F), dto1.getTheFloatObj(), 0.01F);

            // set to max value
            dto1.setTheFloat(Float.MAX_VALUE);
            dto1.setTheFloatObj(new Float(Float.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Float.MAX_VALUE, dto2.getTheFloat(), 0.01F);
            assertEquals(new Float(Float.MAX_VALUE), dto2.getTheFloatObj(), 0.01F);

            // set to 0
            dto2.setTheFloat(0.0F);
            dto2.setTheFloatObj(new Float(0.0F));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0.0F, dto3.getTheFloat(), 0.01F);
            assertEquals(new Float(0.0F), dto3.getTheFloatObj(), 0.01F);

            // set to null
            dto3.setTheFloatObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheFloatObj());

            // set to min value
            dto4.setTheFloat(Float.MIN_VALUE);
            dto4.setTheFloatObj(new Float(Float.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Float.MIN_VALUE, dto5.getTheFloat(), 0.01F);
            assertEquals(new Float(Float.MIN_VALUE), dto5.getTheFloatObj(), 0.01F);
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
     * Tests the update of char values.
     * <ul>
     * <li>set to blank
     * <li>set to null
     * <li>set to a multi-byte unicode character
     * </ul>
     */
    public void testUpdateChar()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value Z
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals('Z', dto1.getTheChar());
            assertEquals(new Character('Y'), dto1.getTheCharObj());

            // set to blank
            dto1.setTheChar(' ');
            dto1.setTheCharObj(new Character(' '));
            tx.commit();

            // assert new value blank
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(' ', dto2.getTheChar());
            assertEquals(new Character(' '), dto2.getTheCharObj());

            // set to null
            dto2.setTheCharObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheCharObj());

            // set to an multi-byte unicode character
            dto3.setTheChar('\u9577');
            dto3.setTheCharObj(new Character('\u9577'));
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals('\u9577', dto4.getTheChar());
            assertEquals(new Character('\u9577'), dto4.getTheCharObj());
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
     * Tests the update of boolean values.
     * <ul>
     * <li>set to false
     * <li>set to null
     * <li>set to true
     * </ul>
     */
    public void testUpdateBoolean()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert the initial value true
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);

            // set to false
            dto1.setTheBoolean(false);
            dto1.setTheBooleanObj(Boolean.FALSE);
            tx.commit();

            // assert new value false
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(false, dto2.isTheBoolean());
            assertEquals(Boolean.FALSE, dto2.getTheBooleanObj());

            // set to null
            dto2.setTheBooleanObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheBooleanObj());

            // set to true
            dto3.setTheBoolean(true);
            dto3.setTheBooleanObj(Boolean.TRUE);
            tx.commit();

            // assert new value true
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(true, dto4.isTheBoolean());
            assertEquals(Boolean.TRUE, dto4.getTheBooleanObj());

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
     * Tests the update of byte values.
     * <ul>
     * <li>set to max value
     * <li>set to 0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateByte()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 0x41
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0x41, dto1.getTheByte());
            assertEquals(new Byte((byte) 0x42), dto1.getTheByteObj());

            // set to max value
            dto1.setTheByte(Byte.MAX_VALUE);
            dto1.setTheByteObj(new Byte(Byte.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Byte.MAX_VALUE, dto2.getTheByte());
            assertEquals(new Byte(Byte.MAX_VALUE), dto2.getTheByteObj());

            // set to 0
            dto2.setTheByte((byte) 0x00);
            dto2.setTheByteObj(new Byte((byte) 0x00));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0, dto3.getTheByte());
            assertEquals(new Byte((byte) 0x00), dto3.getTheByteObj());

            // set to null
            dto3.setTheByteObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheByteObj());

            // set to min value
            dto4.setTheByte(Byte.MIN_VALUE);
            dto4.setTheByteObj(new Byte(Byte.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Byte.MIN_VALUE, dto5.getTheByte());
            assertEquals(new Byte(Byte.MIN_VALUE), dto5.getTheByteObj());
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
     * Tests the update of short values.
     * <ul>
     * <li>set to max value
     * <li>set to 0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateShort()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 1
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals((short) 1, dto1.getTheShort());
            assertEquals(new Short((short) 11), dto1.getTheShortObj());

            // set to max value
            dto1.setTheShort(Short.MAX_VALUE);
            dto1.setTheShortObj(new Short(Short.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Short.MAX_VALUE, dto2.getTheShort());
            assertEquals(new Short(Short.MAX_VALUE), dto2.getTheShortObj());

            // set to 0
            dto2.setTheShort((short) 0);
            dto2.setTheShortObj(new Short((short) 0));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals((short) 0, dto3.getTheShort());
            assertEquals(new Short((short) 0), dto3.getTheShortObj());

            // set to null
            dto3.setTheShortObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheShortObj());

            // set to min value
            dto4.setTheShort(Short.MIN_VALUE);
            dto4.setTheShortObj(new Short(Short.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Short.MIN_VALUE, dto5.getTheShort());
            assertEquals(new Short(Short.MIN_VALUE), dto5.getTheShortObj());
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
     * Tests the update of int values.
     * <ul>
     * <li>set to max value
     * <li>set to 0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateInt()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 2
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(2, dto1.getTheInt());
            assertEquals(new Integer(22), dto1.getTheIntObj());

            // set to max value
            dto1.setTheInt(Integer.MAX_VALUE);
            dto1.setTheIntObj(new Integer(Integer.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Integer.MAX_VALUE, dto2.getTheInt());
            assertEquals(new Integer(Integer.MAX_VALUE), dto2.getTheIntObj());

            // set to 0
            dto2.setTheInt(0);
            dto2.setTheIntObj(new Integer(0));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0, dto3.getTheInt());
            assertEquals(new Integer(0), dto3.getTheIntObj());

            // set to null
            dto3.setTheIntObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheIntObj());

            // set to min value
            dto4.setTheInt(Integer.MIN_VALUE);
            dto4.setTheIntObj(new Integer(Integer.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Integer.MIN_VALUE, dto5.getTheInt());
            assertEquals(new Integer(Integer.MIN_VALUE), dto5.getTheIntObj());
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
     * Tests the update of long values.
     * <ul>
     * <li>set to max value
     * <li>set to 0
     * <li>set to null
     * <li>set to min value
     * </ul>
     */
    public void testUpdateLong()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value 3
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(3L, dto1.getTheLong());
            assertEquals(new Long(33L), dto1.getTheLongObj());

            // set to max value
            dto1.setTheLong(Long.MAX_VALUE);
            dto1.setTheLongObj(new Long(Long.MAX_VALUE));
            tx.commit();

            // assert new value max
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Long.MAX_VALUE, dto2.getTheLong());
            assertEquals(new Long(Long.MAX_VALUE), dto2.getTheLongObj());

            // set to 0
            dto2.setTheLong(0L);
            dto2.setTheLongObj(new Long(0L));
            tx.commit();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(0L, dto3.getTheLong());
            assertEquals(new Long(0L), dto3.getTheLongObj());

            // set to null
            dto3.setTheLongObj(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheLongObj());

            // set to min value
            dto4.setTheLong(Long.MIN_VALUE);
            dto4.setTheLongObj(new Long(Long.MIN_VALUE));
            tx.commit();

            // assert new value min
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Long.MIN_VALUE, dto5.getTheLong());
            assertEquals(new Long(Long.MIN_VALUE), dto5.getTheLongObj());
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
     * Tests the update of BigInteger.
     * <ul>
     * <li>set to positive value
     * <li>set to 0
     * <li>set to null
     * <li>set to negative value
     * </ul>
     */
    public void testUpdateBigInteger()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigInteger("1234567890"), dto1.getTheBigInteger());

            // set to positive value
            String value = "1111222233334444555566667777888899990000";
            dto1.setTheBigInteger(new BigInteger(value));
            tx.commit();
            pm.close();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigInteger(value), dto2.getTheBigInteger());

            // set to 0
            dto2.setTheBigInteger(new BigInteger("0"));
            tx.commit();
            pm.close();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigInteger("0"), dto3.getTheBigInteger());

            // set to null
            dto3.setTheBigInteger(null);
            tx.commit();
            pm.close();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheBigInteger());

            // set to negative value
            value = "-999988887777666655554444333322221111";
            dto4.setTheBigInteger(new BigInteger(value));
            tx.commit();
            pm.close();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigInteger(value), dto5.getTheBigInteger());
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
     * Tests the update of BigInteger.
     * <ul>
     * <li>set some values
     * <li>set to 0
     * <li>set to null
     * <li>set some values
     * </ul>
     */
    public void testUpdateBigDecimal()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigDecimal("12345.67890"), dto1.getTheBigDecimal());

            // set to positive value
            String value = "159E+85";
            dto1.setTheBigDecimal(new BigDecimal(value));
            tx.commit();
            pm.close();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigDecimal(value), dto2.getTheBigDecimal());

            // set to 0
            dto2.setTheBigDecimal(new BigDecimal("0"));
            tx.commit();
            pm.close();

            // assert new value 0
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigDecimal("0"), dto3.getTheBigDecimal());

            // set to null
            dto3.setTheBigDecimal(null);
            tx.commit();
            pm.close();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto4.getTheBigDecimal());

            // set to negative value
            value = "-11223344556677889900E-135";
            dto4.setTheBigDecimal(new BigDecimal(value));
            tx.commit();
            pm.close();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto5 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new BigDecimal(value), dto5.getTheBigDecimal());
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
     * Tests the update of Currency.
     * <ul>
     * <li>set to JAPAN
     * <li>set to null
     * <li>set to FRANCE
     * </ul>
     */
    public void testUpdateCurrency()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Currency.getInstance(Locale.US), dto1.getTheCurrency());

            // set to new value
            dto1.setTheCurrency(Currency.getInstance(Locale.JAPAN));
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Currency.getInstance(Locale.JAPAN), dto2.getTheCurrency());

            // set to null
            dto2.setTheCurrency(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheCurrency());

            // set to new value
            dto3.setTheCurrency(Currency.getInstance(Locale.FRANCE));
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Currency.getInstance(Locale.FRANCE), dto4.getTheCurrency());
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
     * Tests the update of Locale.
     * <ul>
     * <li>set to JAPAN
     * <li>set to null
     * <li>set to FRENCH
     * </ul>
     */
    public void testUpdateLocale()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Locale.GERMANY, dto1.getTheLocale());

            // set to new value
            dto1.setTheLocale(Locale.JAPAN);
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Locale.JAPAN, dto2.getTheLocale());

            // set to null
            dto2.setTheLocale(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheLocale());

            // set to new value
            dto3.setTheLocale(Locale.FRENCH);
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Locale.FRENCH, dto4.getTheLocale());
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
     * Tests the update of TimeZone.
     * <ul>
     * <li>set to PMT
     * <li>set to null
     * <li>set to GMT-08:00
     * </ul>
     */
    public void testUpdateTimeZone()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(TimeZone.getTimeZone("GMT"), dto1.getTheTimeZone());

            // set to new value
            dto1.setTheTimeZone(TimeZone.getTimeZone("PST"));
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(TimeZone.getTimeZone("PST"), dto2.getTheTimeZone());

            // set to null
            dto2.setTheTimeZone(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheTimeZone());

            // set to new value
            dto3.setTheTimeZone(TimeZone.getTimeZone("GMT-08:00"));
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(TimeZone.getTimeZone("GMT-08:00"), dto4.getTheTimeZone());
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
     * Tests the update of UUID.
     * <ul>
     * <li>set to random
     * <li>set to null
     * <li>set to random
     * </ul>
     */
    public void testUpdateUUID()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new UUID(5, 7), dto1.getTheUUID());

            // set to new value
            UUID value = UUID.randomUUID();
            dto1.setTheUUID(value);
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(value, dto2.getTheUUID());

            // set to null
            dto2.setTheUUID(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheUUID());

            // set to new value
            value = UUID.randomUUID();
            dto3.setTheUUID(value);
            tx.commit();

            // assert new value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(value, dto4.getTheUUID());
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
     * Tests the update of Date.
     * <ul>
     * <li>set to current value
     * <li>set to null
     * <li>set to epoch 1970-01-01
     * </ul>
     */
    public void testUpdateDate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            assertEquals(new Date(123456789000L), dto1.getTheDate());

            // set to current value
            Date current = new Date();
            current.setTime(current.getTime() / 1000 * 1000); // strip millis
            dto1.setTheDate(current);
            tx.commit();

            // assert current value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            // TODO: date skew, fix the test!
            // assertEquals(current, dto2.getTheDate());

            // set to null
            dto2.setTheDate(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheDate());

            // set to epoch value
            Date epoch = new Date(0L);
            dto3.setTheDate(epoch);
            tx.commit();

            // assert epoch value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(epoch, dto4.getTheDate());
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
     * Tests the update of Calendar.
     * <ul>
     * <li>set to current
     * <li>set to null
     * <li>set to 0
     * </ul>
     */
    public void testUpdateCalendar()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert initial value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTimeInMillis(23456780000L);
            assertEquals(calendar, dto1.getTheCalendar());

            // set to current value
            Calendar current = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            current.set(Calendar.MILLISECOND, 0); // strip millis
            dto1.setTheCalendar(current);
            tx.commit();

            // assert current value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(current, dto2.getTheCalendar());

            // set to null
            dto2.setTheCalendar(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheCalendar());

            // set to epoch value
            Calendar epoch = Calendar.getInstance(TimeZone.getTimeZone("PMT"));
            epoch.setTimeInMillis(0L);
            dto3.setTheCalendar(epoch);
            tx.commit();

            // assert epoch value
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(epoch, dto4.getTheCalendar());
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
     * Tests the update of Enum values.
     * <ul>
     * <li>set to male
     * <li>set to null
     * <li>set to female
     * </ul>
     */
    public void testUpdateEnum()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // assert the initial value true
            tx.begin();
            TypeHolder dto1 = (TypeHolder) pm.getObjectById(id);

            // set to male
            dto1.setTheEnum(Gender.male);
            tx.commit();

            // assert new value male
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto2 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Gender.male, dto2.getTheEnum());

            // set to null
            dto2.setTheEnum(null);
            tx.commit();

            // assert new value null
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto3 = (TypeHolder) pm.getObjectById(id);
            assertNull(dto3.getTheEnum());

            // set to female
            dto3.setTheEnum(Gender.female);
            tx.commit();

            // assert new value true
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            TypeHolder dto4 = (TypeHolder) pm.getObjectById(id);
            assertEquals(Gender.female, dto4.getTheEnum());

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
