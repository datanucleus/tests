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
    Michael Brown  - Adapted from DateTest to test java.util.Time
**********************************************************************/
package org.datanucleus.tests.types;

import java.sql.Time;
import java.util.Collection;
import java.util.Properties;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.sqltime.SqlTimeHolder;

/**
 * Tests for SCO mutable type java.sql.Time.
 *
 * Tests against the field 'Value' are using the default date mapping. Due to differences
 * between databases we can only really test that we get out the value as persisted.
 *
 * Tests against the field 'Value2' are using a VARCHAR mapping. This allows fine
 * control over semantics, and so the tests in this case are checking for correct
 * conversions across timezones, i.e. none, as pure times should not be effected
 * by time zones.
 */
public class SqlTimeTest extends AbstractTypeTestCase
{
    public SqlTimeTest()
    {
        super("SqlTimeTest");
    }
    
    public SqlTimeTest(String name)
    {
        super(name);
    }

    public void tearDown() throws Exception
    {
        clean(SqlTimeHolder.class);
        super.tearDown();
    }

    protected Class getSimpleClass()
    {
        return SqlTimeHolder.class;
    }

    SqlTimeHolder holder;

    static long keyCounter = 0;
    static long counter = 1;

    protected Object getOneObject()
    {
        holder = new SqlTimeHolder();
        holder.setValue(generateTestValue2());
        counter++;

        SqlTimeHolder u = new SqlTimeHolder();
        u.setKey(generateTestKey());
        u.setValue(holder.getValue());
        return u;
    }

    private Time generateTestKey()
    {
        Time time = Time.valueOf("09:09:" + (keyCounter > 9 ? keyCounter : ("0" + keyCounter)));
        keyCounter++;
        return time;
    }

    private Time generateTestValue2()
    {
        return valueWithCounter("11:23:45", System.currentTimeMillis() + counter);
    }

    private Time generateTestValue3()
    {
        return valueWithCounter("01:01:01", 511);
    }

    private Time valueWithCounter(String value, long add)
    {
        Time result = Time.valueOf(value);
        result.setTime(result.getTime() + (Math.abs(add) % 1000 * 1000));
        return result;
    }

    protected void assertCorrectValues(Object obj)
    {
        SqlTimeHolder u = (SqlTimeHolder) obj;
        assertEquals(holder.getValue().toString(), u.getValue().toString());
    }

    protected void changeObject(Object obj)
    {
        SqlTimeHolder u = (SqlTimeHolder) obj;
        u.setValue(generateTestValue2());
        counter++;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected void replaceObject(Object obj)
    {
        SqlTimeHolder u = (SqlTimeHolder) obj;
        u.setValue(generateTestValue2());
        counter++;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected int getNumberOfMutabilityChecks()
    {
        return 0;
    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Test for querying of time fields.
     */
    public void testQuery()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.makePersistent(getOneObject());
            pm.makePersistent(getOneObject());
            pm.makePersistent(getOneObject());

            SqlTimeHolder holder = new SqlTimeHolder();
            holder.setKey(generateTestKey());
            holder.setValue(generateTestValue3());
            holder.setValue2(generateTestValue3());
            pm.makePersistent(holder);
            pm.flush();

            // Query when stored in "native"
            Query q = pm.newQuery(getSimpleClass(), "value == p");
            q.declareParameters("java.sql.Time p");
            Collection c = (Collection) q.execute(holder.getValue());
            assertEquals(1, c.size());
            assertEquals(generateTestValue3(), ((SqlTimeHolder)c.iterator().next()).getValue());

            // Query when stored as String
            q = pm.newQuery(getSimpleClass(), "value2 == p");
            q.declareParameters("java.sql.Time p");
            c = (Collection) q.execute(holder.getValue2());
            assertEquals(1, c.size());
            assertEquals(generateTestValue3(), ((SqlTimeHolder)c.iterator().next()).getValue2());

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
     * Test persistence of Time field as String.
     */
    public void testWritePersistingAsString()
    {
        performWrite();
    }

    /**
     * Test retrieval of Time field as String.
     */
    public void testReadPersistingAsString()
    {
        performWrite();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SqlTimeHolder holder = (SqlTimeHolder)pm.getObjectById(SqlTimeHolder.class, generateValue("12:27:00"));
            assertEquals(generateValue("20:00:00"), holder.getValue2());
            assertEquals(msvalue, holder.getValue2().getTime()); // MS value same
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
     * Test timezone handling of Time field as String.
     */
    public void testTimezonePersistingAsString()
    {
        performWrite();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SqlTimeHolder holder = (SqlTimeHolder)pm.getObjectById(SqlTimeHolder.class, generateValue("12:27:00"));
            assertNotNull(holder);
            assertEquals(generateValue("20:00:00"), holder.getValue2());
            assertEquals(msvalue + 3*60*60*1000l, holder.getValue2().getTime()); // MS value different
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

    private long msvalue;

    private Time generateValue(String value)
    {
        return Time.valueOf(value);
    }

    public void performWrite()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        SqlTimeHolder holder = new SqlTimeHolder();
        holder.setKey(generateValue("12:27:00"));
        holder.setValue2(generateValue("20:00:00"));
        msvalue = holder.getValue2().getTime();

        getPMFForTimezone("GMT");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(holder);
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

    protected PersistenceManagerFactory getPMFForTimezone(String tz)
    {
        Properties props = new Properties();
        props.setProperty("javax.jdo.option.ServerTimeZoneID", tz); // Although not used by the java.sql.Date as String persistence, set for completeness
        return getPMF(props);
    }
}