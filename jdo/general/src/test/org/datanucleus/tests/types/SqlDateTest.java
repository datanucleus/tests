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
    Michael Brown  - Adapted from URITest to test java.util.Date
**********************************************************************/
package org.datanucleus.tests.types;

import java.sql.Date;
import java.util.Collection;
import java.util.Properties;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.sqldate.SqlDateHolder;

/**
 * Tests for SCO mutable type java.sql.Date.
 *
 * Tests against the field 'Value' are using the default date mapping. Due to differences
 * between databases we can only really test that we get out the value as persisted.
 *
 * Tests against the field 'Value2' are using a VARCHAR mapping. This allows fine
 * control over semantics, and so the tests in this case are checking for correct
 * conversions across timezones, i.e. none, as pure dates should not be effected
 * by time zones.
 *
 * @author Michael Brown
 */
public class SqlDateTest extends AbstractTypeTestCase
{
    /**
     * Holds the default TimeZone before any tests run, so that it may be restored
     * after the test completes.
     */
    private TimeZone savedTz;


    public SqlDateTest()
    {
        super("SqlDateTest");
    }
    
    public SqlDateTest(String name)
    {
        super(name);
    }



    /* Save the current TimeZone and cleanup from any previous tests.
     * Cleanup is done here, and not at test completion, so that the
     * database can be examined when tests fail.
     */
    public void setUp() throws Exception
    {
        super.setUp();
        savedTz = TimeZone.getDefault();
    }

    /* Restore the default TimeZone, since some of these tests change
     * TimeZone.
     */
    public void tearDown() throws Exception
    {
        if (savedTz != null)
        {
            TimeZone.setDefault(savedTz);
        }
        clean(SqlDateHolder.class);
        super.tearDown();
    }

    protected PersistenceManagerFactory getPMFForTimezone(String tz)
    {
        Properties props = new Properties();
        props.setProperty("javax.jdo.option.ServerTimeZoneID", tz); // Although not used by the java.sql.Date as String persistence, set for completeness
        return getPMF(props);
    }

    private Date valueWithCounter(String value, long add)
    {
        add = Math.abs(add);
        String month = Long.toString(add / 28 % 12 + 1);
        String   day = Long.toString(add      % 28 + 1);
        return Date.valueOf(value + "-" + "00".substring(0, 2-month.length()) + month + "-" + "00".substring(0, 2-day.length()) + day);
    }

    private Date generateTestKey()
    {
        return valueWithCounter("1999", System.currentTimeMillis() + counter);
    }

    private Date generateTestValue()
    {
        return valueWithCounter("2003", System.currentTimeMillis() + counter);
    }

    private Date generateTestValue2()
    {
        return valueWithCounter("2009", 511);
    }


    /**
     * Test for querying of URI fields.
     * @throws Exception
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

            SqlDateHolder holder = new SqlDateHolder();
            holder.setKey(Date.valueOf("2000-01-01"));
            holder.setValue(generateTestValue2());
            holder.setValue2(generateTestValue2());
            pm.makePersistent(holder);
            pm.flush();

            Query q = pm.newQuery(getSimpleClass(), "value == p");
            q.declareImports("import java.util.Date");
            q.declareParameters("Date p");
            Collection c = (Collection) q.execute(holder.getValue());
            assertEquals(1, c.size());
            assertEquals(generateTestValue2(), ((SqlDateHolder)c.iterator().next()).getValue());

            q = pm.newQuery(getSimpleClass(), "value2 == p");
            q.declareImports("import java.util.Date");
            q.declareParameters("Date p");
            c = (Collection) q.execute(holder.getValue2());
            assertEquals(1, c.size());
            assertEquals(generateTestValue2(), ((SqlDateHolder)c.iterator().next()).getValue2());

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

    SqlDateHolder holder;
    static long counter = 1;

    protected Class getSimpleClass()
    {
        return SqlDateHolder.class;
    }

    protected Object getOneObject()
    {
        holder = new SqlDateHolder();
        holder.setValue(generateTestValue());
        counter++;

        SqlDateHolder u = new SqlDateHolder();
        u.setKey(generateTestKey());
        u.setValue(holder.getValue());
        return u;
    }

    protected void assertCorrectValues(Object obj)
    {
        SqlDateHolder u = (SqlDateHolder) obj;
        assertEquals(holder.getValue().toString(), u.getValue().toString());
    }

    protected void changeObject(Object obj)
    {
        SqlDateHolder u = (SqlDateHolder) obj;
        u.setValue(generateTestValue());
        counter++;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected void replaceObject(Object obj)
    {
        SqlDateHolder u = (SqlDateHolder) obj;
        u.setValue(generateTestValue());
        counter++;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected int getNumberOfMutabilityChecks()
    {
        return 0;
    }


    /***************************************
     * Second test block, for date with varchar mapping.
     */

    private Date generateValueInTZ(String value)
    {
        return Date.valueOf(value);
    }

    private long msvalue;

    public void testWrite()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        SqlDateHolder holder = new SqlDateHolder();
        holder.setKey(generateValueInTZ("2007-09-09"));
        holder.setValue2(generateValueInTZ("2001-03-01"));
        msvalue = holder.getValue2().getTime();

        getPMFForTimezone("GMT");
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            pm.makePersistent(holder);
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    public void testRead()
    {
        testWrite();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            SqlDateHolder holder = (SqlDateHolder)pm.getObjectById(SqlDateHolder.class, generateValueInTZ("2007-09-09"));
            assertEquals(generateValueInTZ("2001-03-01"), holder.getValue2());
            assertEquals(msvalue, holder.getValue2().getTime()); // MS value still same
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    public void testTimezone()
    {
        testWrite();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));

        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            SqlDateHolder holder = (SqlDateHolder)pm.getObjectById(SqlDateHolder.class, generateValueInTZ("2007-09-09"));
            assertNotNull(holder);
            assertEquals(generateValueInTZ("2001-03-01"), holder.getValue2());
            assertEquals(msvalue + 3*60*60*1000l, holder.getValue2().getTime()); // MS value different
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }
}