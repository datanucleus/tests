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
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.datanucleus.api.jpa.JPAEntityManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Person;

/**
 * Series of tests for stored procedures.
 * Any datastores that don't support stored procs will omit these tests.
 * TODO Note that the stored procedures in here use a particular syntax and there is no common syntax for 
 * all datastores StoredProcedures. Could update this class with different procedures depending on vendorId.
 */
public class StoredProcedureTest extends JPAPersistenceTestCase
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
        if (vendorID == null)
        {
            return;
        }
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
                "SELECT COUNT(*) FROM JPA_AN_PERSON; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            JPAEntityManager em = (JPAEntityManager)getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                em.persist(p);
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

            em = (JPAEntityManager)getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = em.createQuery("SELECT COUNT(p) FROM " + Person.class.getName() + " p");
                Long count = (Long)q.getSingleResult();

                // Execute stored proc and compare
                StoredProcedureQuery spq = em.createStoredProcedureQuery(procName);
                boolean val = spq.execute();
                assertTrue("Return from execute should have been true", val);

                List results = spq.getResultList();
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
                em.close();
            }
        }
        finally
        {
            // Cleanup data
            clean(Person.class);
        }
    }

    public void testGetResultListWithoutParamsReturningResultSet()
    {
        if (vendorID == null)
        {
            return;
        }
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
                "SELECT COUNT(*) FROM JPA_AN_PERSON; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            JPAEntityManager em = (JPAEntityManager)getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                em.persist(p);
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

            em = (JPAEntityManager)getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = em.createQuery("SELECT COUNT(p) FROM " + Person.class.getName() + " p");
                Long count = (Long)q.getSingleResult();

                // Execute stored proc and compare
                StoredProcedureQuery spq = em.createStoredProcedureQuery(procName);
                List results = spq.getResultList();
                assertNotNull("ResultSet was null!", results);
                assertEquals("Number of results was wrong", 1, results.size());
                assertEquals("Result set result was wrong", count, results.get(0));

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

    public void testExecuteOutputParam()
    {
        if (vendorID == null)
        {
            return;
        }
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
                "SELECT COUNT(*) INTO PARAM1 FROM JPA_AN_PERSON; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            JPAEntityManager em = (JPAEntityManager)getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                em.persist(p);
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

            em = (JPAEntityManager)getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = em.createQuery("SELECT COUNT(p) FROM " + Person.class.getName() + " p");
                Long count = (Long)q.getSingleResult();

                // Execute stored proc and compare
                StoredProcedureQuery spq = em.createStoredProcedureQuery(procName);
                spq.registerStoredProcedureParameter("PARAM1", Integer.class, ParameterMode.OUT);
                boolean val = spq.execute();
                assertFalse("Flag for result set returned true but should have been false", val);

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
        if (vendorID == null)
        {
            return;
        }
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
                "SELECT COUNT(*) INTO PARAM2 FROM JPA_AN_PERSON WHERE FIRSTNAME = PARAM1; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            JPAEntityManager em = (JPAEntityManager)getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                Person p2 = new Person(102, "Fred", "Gravel", "fred.gravel@warnerbros.com");
                em.persist(p1);
                em.persist(p2);
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

            em = (JPAEntityManager)getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = em.createQuery("SELECT COUNT(p) FROM " + Person.class.getName() + " p WHERE p.firstName = 'Fred'");
                Long count = (Long)q.getSingleResult();

                // Execute stored proc and compare
                StoredProcedureQuery spq = em.createStoredProcedureQuery(procName);
                spq.registerStoredProcedureParameter("PARAM1", String.class, ParameterMode.IN);
                spq.registerStoredProcedureParameter("PARAM2", Integer.class, ParameterMode.OUT);
                spq.setParameter("PARAM1", "Fred");
                boolean val = spq.execute();
                assertFalse("Flag for result set returned true but should have been false", val);

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

    public void testNamedProcWithParamReturningResultSet()
    {
        if (vendorID == null)
        {
            return;
        }
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.STORED_PROCEDURES))
            {
                LOG.warn("Database doesnt support stored procedures so ignoring the test");
                return;
            }
        }

        String procName = "DN_PROC_NAMED_RS";

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
            String createStmt = "CREATE PROCEDURE " + procName + "(IN PARAM1 VARCHAR(255)) BEGIN " +
                "SELECT COUNT(*) FROM JPA_AN_PERSON WHERE FIRSTNAME = PARAM1; END";
            stmt.execute(createStmt);
        }
        catch (SQLException sqle)
        {
            fail("Exception in drop-create of stored procedure : " + sqle.getMessage());
        }
        finally
        {
            mc.close();
        }

        try
        {
            JPAEntityManager em = (JPAEntityManager)getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                em.persist(p);
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

            em = (JPAEntityManager)getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Get value to compare against
                Query q = em.createQuery("SELECT COUNT(p) FROM " + Person.class.getName() + " p WHERE p.firstName='Fred'");
                Long count = (Long)q.getSingleResult();

                // Execute stored proc and compare
                StoredProcedureQuery spq = em.createNamedStoredProcedureQuery("myNamedSP");
                spq.setParameter("PARAM1", "Fred");
                boolean val = spq.execute();
                assertTrue("Return from execute should have been true", val);

                List results = spq.getResultList();
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