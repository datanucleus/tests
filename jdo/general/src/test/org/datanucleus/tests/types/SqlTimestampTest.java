/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.sqltimestamp.SqlTimestampHolder;

/**
 * Tests for SCO mutable type java.sql.Timestamp.
 *
 * Tests against the field 'Value' are using the default date mapping. Due to differences
 * between databases we can only really test that we get out the value as persisted.
 *
 * Tests against the field 'Value2' are using a VARCHAR mapping. This allows fine
 * control over semantics, and so the tests in this case are checking for correct
 * conversions across timezones, i.e. none, as pure times should not be effected
 * by time zones.
 */
public class SqlTimestampTest extends JDOPersistenceTestCase
{
    public void tearDown() throws Exception
    {
        clean(SqlTimestampHolder.class);
        super.tearDown();
    }

    static long keyCounter = 100000;
    static long counter = 200000;

    protected Object getOneObject()
    {
        Timestamp ts = generateTestValue2();
        counter++;

        SqlTimestampHolder u = new SqlTimestampHolder();
        u.setKey(generateTestKey());
        u.setValue(ts);
        return u;
    }

    private Timestamp generateTestKey()
    {
        String baseString = "2014-03-05 01:10:25.599";
        Timestamp time = Timestamp.valueOf(baseString + keyCounter);
        keyCounter++;
        return time;
    }

    private Timestamp generateTestValue2()
    {
        String baseString = "2012-10-01 01:10:25.299";
        Timestamp time = Timestamp.valueOf(baseString + counter);
        counter++;
        return time;
    }

    private Timestamp generateTestValue3()
    {
        String baseString = "2011-07-01 01:25:25.299";
        Timestamp time = Timestamp.valueOf(baseString + counter);
        counter++;
        return time;
    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Test of the basic persistence and retrieval of the type holder being tested.
     */
    public void testBasicPersistence()
    throws Exception
    {
        if (vendorID != null)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.DATETIME_STORES_MILLISECS))
            {
                return;
            }
        }

        try
        {
            Timestamp keyTs = Timestamp.valueOf("2014-03-05 01:10:25.599000001");
            Timestamp ts1 = Timestamp.valueOf("2012-10-01 01:10:25.299500000");
            Timestamp ts2 = Timestamp.valueOf("2011-07-01 01:25:25.299000000");
            Timestamp ts3 = Timestamp.valueOf("2001-10-01 01:10:25.299515000");
            Timestamp ts4 = Timestamp.valueOf("2011-07-01 01:25:25.277000000");

            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SqlTimestampHolder holder = new SqlTimestampHolder();
                holder.setKey(keyTs);
                holder.setValue(ts1);
                holder.setValue2(ts2);
                pm.makePersistent(holder);
                pm.flush();
                id = JDOHelper.getObjectId(holder);

                holder = (SqlTimestampHolder) pm.getObjectById(id,true);
                pm.refresh(holder);
                assertEquals(ts1.toString(), holder.getValue().toString());
                assertEquals(ts2.toString(), holder.getValue2().toString());

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
            pmf.getDataStoreCache().evictAll();

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                SqlTimestampHolder holder = (SqlTimestampHolder) pm.getObjectById(id, true);
                assertEquals(ts1.toString(), holder.getValue().toString());
                assertEquals(ts2.toString(), holder.getValue2().toString());
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

            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                SqlTimestampHolder holder = (SqlTimestampHolder) pm.getObjectById(id,true);
                assertEquals(ts1.toString(), holder.getValue().toString());
                assertEquals(ts2.toString(), holder.getValue2().toString());

                holder.setValue(ts3);
                holder.setValue2(ts4);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Error updating the object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                SqlTimestampHolder holder = (SqlTimestampHolder) pm.getObjectById(id,true);
                assertEquals(ts3.toString(), holder.getValue().toString());
                assertEquals(ts4.toString(), holder.getValue2().toString());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Error updating the object : " + e.getMessage());
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
            clean(SqlTimestampHolder.class);
        }
    }

    /**
     * Test of the attach/detach process for a holder of the type being tested.
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            Timestamp keyTs = Timestamp.valueOf("2013-03-05 01:10:25.599000001");
            Timestamp ts1 = Timestamp.valueOf("2012-10-01 01:10:25.299500000");
            Timestamp ts2 = Timestamp.valueOf("2011-07-01 01:25:25.299000000");
            Timestamp ts3 = Timestamp.valueOf("2001-10-01 01:10:25.299515000");
            Timestamp ts4 = Timestamp.valueOf("2011-07-01 01:25:25.277000000");

            SqlTimestampHolder holder = new SqlTimestampHolder();
            holder.setKey(keyTs);
            holder.setValue(ts1);
            holder.setValue2(ts2);

            SqlTimestampHolder detached = null;
            Object id = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("group");
            try
            {
                tx.begin();
                pm.makePersistent(holder);
                detached = pm.detachCopy(holder);

                tx.commit();
                id = pm.getObjectId(holder);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }        

            assertEquals(ts1.toString(), detached.getValue().toString());
            assertEquals(ts2.toString(), detached.getValue2().toString());

            detached.setValue(ts3);
            detached.setValue2(ts4);

            // Attach it
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detached);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Error whilst attaching object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();

            }

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                SqlTimestampHolder holder2 = (SqlTimestampHolder) pm.getObjectById(id,true);
                assertEquals(ts3.toString(), holder2.getValue().toString());
                assertEquals(ts4.toString(), holder2.getValue2().toString());
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

            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("group");
            try
            {
                tx.begin();
                SqlTimestampHolder holder3 = (SqlTimestampHolder) pm.getObjectById(id,true);
                detached = pm.detachCopy(holder3);

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
        finally
        {
            clean(SqlTimestampHolder.class);
        }
    }

    /**
     * Test for querying of time fields.
     */
    public void testQuery()
    throws Exception
    {
        if (vendorID != null)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.DATETIME_STORES_MILLISECS))
            {
                return;
            }
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.makePersistent(getOneObject());
            pm.makePersistent(getOneObject());
            pm.makePersistent(getOneObject());

            SqlTimestampHolder holder = new SqlTimestampHolder();
            holder.setKey(generateTestKey());
            holder.setValue(generateTestValue3());
            holder.setValue2(generateTestValue3());
            pm.makePersistent(holder);
            pm.flush();

            // Query when stored in "native"
            Query q = pm.newQuery(SqlTimestampHolder.class, "value == p");
            q.declareParameters("java.sql.Timestamp p");
            List<SqlTimestampHolder> c = (List<SqlTimestampHolder>) q.execute(holder.getValue());
            assertEquals(1, c.size());
            SqlTimestampHolder queryHolder = c.iterator().next();
            assertEquals(holder.getKey(), queryHolder.getKey());
            assertEquals(holder.getValue(), queryHolder.getValue());

            // Query when stored as String
            q = pm.newQuery(SqlTimestampHolder.class, "value2 == p");
            q.declareParameters("java.sql.Timestamp p");
            c = (List<SqlTimestampHolder>) q.execute(holder.getValue2());
            assertEquals(1, c.size());
            queryHolder = c.iterator().next();
            assertEquals(holder.getValue2(), queryHolder.getValue2());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
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
            SqlTimestampHolder holder = (SqlTimestampHolder)pm.getObjectById(SqlTimestampHolder.class, Timestamp.valueOf("2009-03-05 01:10:25.599000000"));
            assertEquals(Timestamp.valueOf("2001-01-02 01:10:00.000000000"), holder.getValue2());
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

    private long msvalue;

    public void performWrite()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+0"));

        SqlTimestampHolder holder = new SqlTimestampHolder();
        holder.setKey(Timestamp.valueOf("2009-03-05 01:10:25.599000000"));
        holder.setValue2(Timestamp.valueOf("2001-01-02 01:10:00.000000000"));
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