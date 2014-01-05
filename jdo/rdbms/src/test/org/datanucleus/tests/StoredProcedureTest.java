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
package org.datanucleus.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.api.jdo.JDOQuery;
import org.datanucleus.metadata.StoredProcQueryParameterMode;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.query.StoredProcedureQuery;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;

/**
 * Series of tests for stored procedures.
 * Any datastores that don't support stored procs will omit these tests.
 * TODO Generalise the stored proc creation/drop to work with all RDBMS
 */
public class StoredProcedureTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public StoredProcedureTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class
                });
        }
    }

    public void testExecuteWithoutParamsReturningResultSet()
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.STORED_PROCEDURES))
            {
                LOG.warn("Database doesnt support stored procedures so ignoring the test");
                return;
            }
        }

        String procName = "DN_PROC_NOPARAMS_RS";

        RDBMSStoreManager rdbmsMgr = (RDBMSStoreManager)storeMgr;
        ManagedConnection mc = rdbmsMgr.getConnection(-1);
        try
        {
            Connection conn = (Connection)mc.getConnection();
            Statement stmt = conn.createStatement();

            // Drop it first
            String dropStmt = "DROP PROCEDURE IF EXISTS " + procName;
            stmt.execute(dropStmt);

            // Create it
            String createStmt = "CREATE PROCEDURE " + procName + "() BEGIN " +
                "SELECT COUNT(*) FROM PERSON; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            LOG.error("Exception in drop+create of stored proc", sqle);
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            PersistenceManager pm = getPM();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(p);
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

            pm = getPM();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = pm.newQuery("SELECT COUNT(this) FROM " + Person.class.getName());
                Long count = (Long)q.execute();

                // Execute stored proc and compare
                q = pm.newQuery("STOREDPROC", procName);
                StoredProcedureQuery spq = (StoredProcedureQuery) ((JDOQuery)q).getInternalQuery();
                Boolean hasRS = (Boolean)spq.execute();
                assertTrue(hasRS);

                List results = (List)spq.getNextResults();
                assertNotNull("ResultSet was null!", results);
                assertEquals("Number of results was wrong", 1, results.size());
                assertEquals("Result set result was wrong", count, results.get(0));
                assertFalse("More results present but should be the end", spq.hasMoreResults());

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
        finally
        {
            // Cleanup data
            clean(Person.class);
        }
    }

    public void testExecuteOutputParam()
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.STORED_PROCEDURES))
            {
                LOG.warn("Database doesnt support stored procedures so ignoring the test");
                return;
            }
        }

        String procName = "DN_PROC_OUTPUTPARAM";

        RDBMSStoreManager rdbmsMgr = (RDBMSStoreManager)storeMgr;
        ManagedConnection mc = rdbmsMgr.getConnection(-1);
        try
        {
            Connection conn = (Connection)mc.getConnection();
            Statement stmt = conn.createStatement();

            // Drop it first
            String dropStmt = "DROP PROCEDURE IF EXISTS " + procName;
            stmt.execute(dropStmt);

            // Create it
            String createStmt = "CREATE PROCEDURE " + procName + "(OUT PARAM1 INT) BEGIN " +
                "SELECT COUNT(*) INTO PARAM1 FROM PERSON; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            LOG.error("Exception in drop+create of stored proc", sqle);
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            PersistenceManager em = getPM();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                em.makePersistent(p);
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
                em.close();
            }

            em = getPM();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = pm.newQuery("SELECT COUNT(this) FROM " + Person.class.getName());
                Long count = (Long)q.execute();

                // Execute stored proc and compare
                q = pm.newQuery("STOREDPROC", procName);
                StoredProcedureQuery spq = (StoredProcedureQuery) ((JDOQuery)q).getInternalQuery();
                spq.registerParameter("PARAM1", Integer.class, StoredProcQueryParameterMode.OUT);
                Boolean hasRS = (Boolean)spq.execute();
                assertFalse("Flag for result set returned true but should have been false", hasRS);

                Object paramVal = spq.getOutputParameterValue("PARAM1");
                assertEquals("Output parameter is incorrect", new Integer(count.intValue()), paramVal);

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
                em.close();
            }
        }
        finally
        {
            // Cleanup data
            clean(Person.class);
        }
    }

    public void testExecuteInputParamAndOutputParam()
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.STORED_PROCEDURES))
            {
                LOG.warn("Database doesnt support stored procedures so ignoring the test");
                return;
            }
        }

        String procName = "DN_PROC_INPUTPARAM_OUTPUTPARAM";

        RDBMSStoreManager rdbmsMgr = (RDBMSStoreManager)storeMgr;
        ManagedConnection mc = rdbmsMgr.getConnection(-1);
        try
        {
            Connection conn = (Connection)mc.getConnection();
            Statement stmt = conn.createStatement();

            // Drop it first
            String dropStmt = "DROP PROCEDURE IF EXISTS " + procName;
            stmt.execute(dropStmt);

            // Create it
            String createStmt = "CREATE PROCEDURE " + procName + "(IN PARAM1 VARCHAR(255), OUT PARAM2 INT) BEGIN " +
                "SELECT COUNT(*) INTO PARAM2 FROM PERSON WHERE FIRSTNAME = PARAM1; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            LOG.error("Exception in drop+create of stored proc", sqle);
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            PersistenceManager em = getPM();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                Person p2 = new Person(102, "Fred", "Gravel", "fred.gravel@warnerbros.com");
                em.makePersistent(p1);
                em.makePersistent(p2);
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
                em.close();
            }

            em = getPM();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = pm.newQuery("SELECT COUNT(this) FROM " + Person.class.getName() + " WHERE firstName == 'Fred'");
                Long count = (Long)q.execute();

                // Execute stored proc and compare
                q = pm.newQuery("STOREDPROC", procName);
                StoredProcedureQuery spq = (StoredProcedureQuery) ((JDOQuery)q).getInternalQuery();
                spq.registerParameter("PARAM1", String.class, StoredProcQueryParameterMode.IN);
                spq.registerParameter("PARAM2", Integer.class, StoredProcQueryParameterMode.OUT);

                Map params = new HashMap();
                params.put("PARAM1", "Fred");
                Boolean hasRS = (Boolean)spq.executeWithMap(params);
                assertFalse("Flag for result set returned true but should have been false", hasRS);

                Object paramVal = spq.getOutputParameterValue("PARAM2");
                assertEquals("Output parameter is incorrect", new Integer(count.intValue()), paramVal);

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
                em.close();
            }
        }
        finally
        {
            // Cleanup data
            clean(Person.class);
        }
    }
}