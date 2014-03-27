/**********************************************************************
Copyright (c) 2009 Michael Brown and others. All rights reserved.
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

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Properties;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.date.DateHolderC;
import org.jpox.samples.types.date.DateHolderA;
import org.jpox.samples.types.date.DateHolderB;

/**
 * Tests for SCO mutable type java.util.Date.
 *
 * Tests against the field 'Value' are using the default date mapping. Due to differences
 * between databases we can only really test that we get out the value as persisted. But
 * even that will fail on a DB such as MySQL, due to its very constrained datetime type.
 *
 * Tests against the field 'Value2' are using a VARCHAR mapping. This allows fine
 * control over semantics, and so the tests in this case are checking for correct
 * conversions across timezones.
 */
public class DateTest extends AbstractTypeTestCase
{
    /**
     * Holds the default TimeZone before any tests run, so that it may be restored
     * after the test completes.
     */
    private TimeZone savedTz;

    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public DateTest()
    {
        super("DateTest");
    }

    public DateTest(String name)
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
        clean(DateHolderC.class);
        savedTz = TimeZone.getDefault();
    }

    public void tearDown() throws Exception
    {
        // Restore the default TimeZone, since some of these tests change TimeZone.
        if (savedTz != null)
        {
            TimeZone.setDefault(savedTz);
        }
        super.tearDown();
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
            DateHolderC holder = new DateHolderC();
            holder.setKey(generateTestValue1());
            holder.setValue(generateTestValue3());
            holder.setValue2(generateTestValue3());
            pm.makePersistent(holder);
            pm.flush();

            Query q = pm.newQuery(getSimpleClass(), "value == p");
            q.declareParameters("java.util.Date p");
            Collection c = (Collection) q.execute(holder.getValue());
            assertEquals(1, c.size());
            assertEquals(generateTestValue3(), ((DateHolderC)c.iterator().next()).getValue());

            q = pm.newQuery(getSimpleClass(), "value2 == p");
            q.declareParameters("java.util.Date p");
            c = (Collection) q.execute(holder.getValue2());
            assertEquals(1, c.size());
            assertEquals(generateTestValue3(), ((DateHolderC)c.iterator().next()).getValue2());

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

    DateHolderC holder;
    static long counter = 1;

    protected Class getSimpleClass()
    {
        return DateHolderC.class;
    }

    protected Object getOneObject()
    {
        holder = new DateHolderC();
        holder.setValue(generateTestValue2());
        counter+=100;

        DateHolderC u = new DateHolderC();
        u.setKey(generateTestValue1());
        counter+=100;        
        u.setValue(holder.getValue());
        return u;
    }

    protected void assertCorrectValues(Object obj)
    {
        DateHolderC u = (DateHolderC) obj;
        assertEquals(holder.getValue().toString(), u.getValue().toString());
    }

    protected void changeObject(Object obj)
    {
        DateHolderC u = (DateHolderC) obj;
        u.setValue(generateTestValue2());
        counter+=100;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected void replaceObject(Object obj)
    {
        DateHolderC u = (DateHolderC) obj;
        u.setValue(generateTestValue2());
        counter+=100;

        //update local holder
        holder.setValue(u.getValue());
    }

    protected int getNumberOfMutabilityChecks()
    {
        return 0;
    }

    private Date generateValueInTZ(String value)
    {
        try
        {
            /* Create a new Format object each time, because we want it to be
             * absolutely sure it will parse in the current TimeZone.
             */
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value);
        }
        catch (ParseException pe)
        {
            throw new RuntimeException(pe);
        }
    }

    private long msvalue;

    public void testWriteAsString()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        DateHolderC holder = new DateHolderC();
        holder.setKey(generateValueInTZ("2007-09-09 12:27:00.021"));
        holder.setValue2(generateValueInTZ("2001-03-01 20:00:00.001"));
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


    public void testReadAsString()
    {
        testWriteAsString();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            DateHolderC holder = (DateHolderC)pm.getObjectById(DateHolderC.class, generateValueInTZ("2007-09-09 12:27:00.021"));
            assertEquals(generateValueInTZ("2001-03-01 20:00:00.001"), holder.getValue2());
            assertEquals(msvalue, holder.getValue2().getTime()); // MS value still same
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

    /* A value persisted in GMT+0 and retreived in GMT-3 should have a different
     * formatted representation, but an identical internal millisecond value.
     */
    public void testTimezoneAsString()
    {
        testWriteAsString();

        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            DateHolderC holder = (DateHolderC)pm.getObjectById(DateHolderC.class, generateValueInTZ("2007-09-09 09:27:00.021"));
            assertNotNull(holder);
            assertEquals(generateValueInTZ("2001-03-01 17:00:00.001"), holder.getValue2());
            assertEquals(msvalue, holder.getValue2().getTime()); // MS value still same
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
     * Test that discriminator where the key field is a date with standard mapping works.
     * This is prompted by http://www.jpox.org/servlet/jira/browse/NUCRDBMS-250.
     */
    public void testDiscriminatorA()
    {
        /* Change timezone to ensure we are not at +0, so that we can test
         * that the stringified dates used to search for discriminators use
         * the same timezone settings as the datastore.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));

        getPMFForTimezone("GMT");
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DateHolderA a = new DateHolderA();
                a.setKey(generateValueInTZ("2001-03-01 20:00:00.001"));
                pm.makePersistent(a);
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getObjectById(DateHolderA.class, generateValueInTZ("2001-03-01 20:00:00.001"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in object retrieval", e);
                fail("Exception thrown retrieving object " + e.getMessage());
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
            clean(DateHolderA.class);
        }
    }

    /**
     * Test that discriminator where the key field is a date with varchar mapping works.
     * This is prompted by http://www.jpox.org/servlet/jira/browse/NUCRDBMS-250.
     */
    public void testDiscriminatorB()
    {
        /* Change timezone to ensure we are not at +0, so that we can test
         * that the stringified dates used to search for discriminators use
         * the same timezone settings as the datastore.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-3"));

        getPMFForTimezone("GMT");
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DateHolderB b = new DateHolderB();
                b.setKey(generateValueInTZ("2001-03-01 20:00:00.001"));
                pm.makePersistent(b);
                tx.commit();
                
                tx.begin();
                pm.getObjectById(DateHolderB.class, generateValueInTZ("2001-03-01 20:00:00.001"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in object retrieval", e);
                fail("Exception thrown retrieving object " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getObjectById(DateHolderB.class, generateValueInTZ("2001-03-01 20:00:00.001"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in object retrieval", e);
                fail("Exception thrown retrieving object " + e.getMessage());
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
            clean(DateHolderB.class);
        }
    }

    // -------------------------------- Convenience Methods --------------------------------------

    private Date valueWithCounter(String value, long add)
    {
        try
        {
            String counterString = Long.toString(Math.abs(add) % 1000);
            return dateFormat.parse(value + "." + "000".substring(0, 3-counterString.length()) + counterString);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Date generateTestValue1()
    {
        return valueWithCounter("1999-09-09 09:09:09", System.currentTimeMillis() + counter);
    }

    private Date generateTestValue2()
    {
        return valueWithCounter("2003-03-07 11:23:45", System.currentTimeMillis() + counter);
    }

    private Date generateTestValue3()
    {
        return valueWithCounter("2009-01-01 01:01:01", 511);
    }

    protected PersistenceManagerFactory getPMFForTimezone(String tz)
    {
        Properties props = new Properties();
        props.setProperty("javax.jdo.option.ServerTimeZoneID", tz); // Although not used by the java.sql.Date as String persistence, set for completeness
        return getPMF(props);
    }
}