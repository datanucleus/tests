/******************************************************************
Copyright (c) 2003 Erik Bengtson and others. All rights reserved. 
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
2007 Andy Jefferson - rewritten all tests to match ORM situations
    ...
*****************************************************************/
package org.datanucleus.tests.application;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.JDOFatalInternalException;
import javax.jdo.JDOFatalUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.RDBMSTestHelper;
import org.datanucleus.samples.rdbms.application.EmbeddedA;
import org.datanucleus.samples.secondarytable.Printer;
import org.datanucleus.samples.array.ArrayElement;
import org.datanucleus.samples.array.IntArray;
import org.datanucleus.samples.array.PersistableArray;
import org.datanucleus.samples.embedded.Computer;
import org.datanucleus.samples.embedded.FilmLibrary;
import org.datanucleus.samples.embedded.FittedKitchen;
import org.datanucleus.samples.embedded.MultifunctionOven;
import org.datanucleus.samples.embedded.Network;
import org.datanucleus.samples.embedded.Oven;
import org.datanucleus.samples.embedded.Processor;
import org.datanucleus.samples.inheritance.LBase;
import org.datanucleus.samples.inheritance.LSub;
import org.datanucleus.samples.inheritance.MBase;
import org.datanucleus.samples.inheritance.MRelated;
import org.datanucleus.samples.inheritance.MSub1;
import org.datanucleus.samples.inheritance.MSub2;
import org.datanucleus.samples.inheritance.NBase;
import org.datanucleus.samples.inheritance.NSub;
import org.datanucleus.samples.interfaces.Diet;
import org.datanucleus.samples.interfaces.ShapeHolder;
import org.datanucleus.samples.many_many.PetroleumCustomer;
import org.datanucleus.samples.many_many.PetroleumSupplier;
import org.datanucleus.samples.many_one.unidir.CarRental;
import org.datanucleus.samples.many_one.unidir.HireCar;
import org.datanucleus.samples.models.company.Developer;
import org.datanucleus.samples.models.company.Employee;
import org.datanucleus.samples.models.company.Manager;
import org.datanucleus.samples.models.company.Person;
import org.datanucleus.samples.objects.ObjectHolder;
import org.datanucleus.samples.one_many.collection.ListHolder;
import org.datanucleus.samples.one_many.collection.SetHolder;
import org.datanucleus.samples.one_many.map.MapHolder;
import org.datanucleus.samples.one_one.bidir.Boiler;
import org.datanucleus.samples.one_one.bidir.Timer;
import org.datanucleus.samples.one_one.unidir.Login;
import org.datanucleus.samples.one_one.unidir.LoginAccount;
import org.datanucleus.samples.rdbms.application.CollElement;
import org.datanucleus.samples.rdbms.application.CollElementOwner1;
import org.datanucleus.samples.rdbms.application.CollElementOwner2;
import org.datanucleus.samples.rdbms.application.ConflictAssociation;
import org.datanucleus.samples.rdbms.application.ConflictRole;
import org.datanucleus.samples.rdbms.application.ConflictUser;

/**
 * Tests for schema creation, schema validation and existing schemas.
 * These tests only apply to a datastore that has a notion of a schema. Consequently it should not
 * be run for other datastores.
 */
public class SchemaTest extends JDOPersistenceTestCase
{
    public SchemaTest(String name)
    {
        super(name);
    }

    /**
     * Test of the specification of table and column names.
     * Table is created in the constructor, and performs an SQL select to check the use of the
     * table and column names.
     */
    public void testTableColumnNames()
    {
        addClassesToSchema(new Class[] {Person.class, Employee.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null;
        ManagedConnection mconn = null; 
        try
        {
            tx.begin();

            HashSet columnNames = new HashSet();
            columnNames.add("PERSONNUM");
            columnNames.add("GLOBALNUM");
            columnNames.add("FIRSTNAME");
            columnNames.add("LASTNAME");
            columnNames.add("EMAIL_ADDRESS");
            columnNames.add("AGE");
            columnNames.add("BESTFRIEND_GLOBALNUM_OID");
            columnNames.add("BESTFRIEND_PERSONNUM_OID");
            columnNames.add("BIRTHDATE");

            mconn = databaseMgr.getConnectionManager().getConnection(0);
            conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PERSON", columnNames);
 
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names must have been ignored when creating the schema for Person. Exception was thrown : " + e.getMessage());
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
     * Test of the specification of columns.
     * Table is created in the constructor, and checks the DatabaseMetaData for the details.
     */
    public void testColumnSpecifications()
    {
        // Postgresql doesnt support DECIMAL
        if (rdbmsVendorID.equals("postgresql"))
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            LOG.info("JDBC Types CHAR = " + Types.CHAR + ", VARCHAR=" + Types.VARCHAR + 
                ", INTEGER=" + Types.INTEGER + ", DOUBLE=" + Types.DOUBLE + ", BINARY=" + Types.BINARY + ", LONGVARBINARY=" + Types.LONGVARBINARY +
                ", NUMERIC=" + Types.NUMERIC + ", VARBINARY=" + Types.VARBINARY);

            String tableName = RDBMSTestHelper.getIdentifierInCaseOfAdapter(storeMgr, "EMPLOYEE", false);

            RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
            DatabaseMetaData dmd = ((Connection)databaseMgr.getConnectionManager().getConnection(0).getConnection()).getMetaData();
            ResultSet rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next())
            {
                String colName = rs.getString(4);
                int colType = rs.getInt(5);
                int colLength = rs.getInt(7);
                int colScale = rs.getInt(9);

                if (colName.equalsIgnoreCase("SERIAL_NO")) // CHAR column
                {
                    // MySQL returns CHAR as being VARCHAR here even though it is CHAR in the DB!
                 // assertTrue("Type of field (SERIAL_NO) was wrong : should have been " + Types.CHAR + " but was " + colType, colType == Types.CHAR);
                    assertEquals("Length of field (SERIAL_NO) was wrong", 12, colLength);
                }
                else if (colName.equalsIgnoreCase("SALARY"))
                {
                    assertEquals("Type of field (SALARY) was wrong", Types.DECIMAL, colType);
                    assertEquals("Precision of field (SALARY) was wrong", 10, colLength);
                    assertEquals("Scale of field (SALARY) was wrong", 2, colScale);
                }
                else if (colName.equalsIgnoreCase("YRS_IN_COMPANY")) // int field
                {
                    assertEquals("Type of field (YRS_IN_COMPANY) was wrong", Types.INTEGER, colType);
                    // TODO Check "default" setting
                }

                if (colName.equalsIgnoreCase("FIRSTNAME")) // Restricted String field
                {
                    assertEquals("Type of field (FIRSTNAME) was wrong", Types.VARCHAR, colType);
                    assertEquals("Length of field (FIRSTNAME) was wrong", 32, colLength);
                }
                else if (colName.equalsIgnoreCase("GLOBALNUM")) // Unlimited String field
                {
                    if(rdbmsVendorID.equals("cloudspanner")){
                        //default string column maps to nvarchar in spanner
                        assertEquals("Type of field (GLOBALNUM) was wrong", Types.NVARCHAR, colType);
                    } else {
                        assertEquals("Type of field (GLOBALNUM) was wrong", Types.VARCHAR, colType);
                        assertEquals("Length of field (GLOBALNUM) was wrong", 255, colLength);
                    }
                }
            }

            tableName = RDBMSTestHelper.getIdentifierInCaseOfAdapter(storeMgr, "PERSON", false);
            rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next())
            {
                String colName = rs.getString(4);
                int colType = rs.getInt(5);
                int colLength = rs.getInt(7);

                if (colName.equalsIgnoreCase("FIRSTNAME")) // Restricted String field
                {
                    assertEquals("Type of field (FIRSTNAME) was wrong", Types.VARCHAR, colType);
                    assertEquals("Length of field (FIRSTNAME) was wrong", 32, colLength);
                }
                else if (colName.equalsIgnoreCase("GLOBALNUM")) // Unlimited String field
                {
                    if(rdbmsVendorID.equals("cloudspanner")){
                        //default string column maps to nvarchar in spanner
                        assertEquals("Type of field (GLOBALNUM) was wrong", Types.NVARCHAR, colType);
                    } else {
                        assertEquals("Type of field (GLOBALNUM) was wrong", Types.VARCHAR, colType);
                        assertEquals("Length of field (GLOBALNUM) was wrong", 255, colLength);
                    }
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Failed interpretation of column specification for Employee. Exception was thrown : " + e.getMessage());
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
     * Test of the "unique" constraint creation. This uses the constraint on
     * a field (column) and tries to insert non-unique values.
     */
    public void testUnique()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = new Employee(123, "Barney", "Rubble", "barney.rubble@warnerbros.com", (float)123.45, "1245C");
                pm.makePersistent(e);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Error persisting an Employee object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            // Try to insert another Employee with the same serial number
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = new Employee(245, "Fred", "Flintstone", "fred.flintstone@warnerbros.com", (float)178.90, "1245C");
                pm.makePersistent(e);
                tx.commit();
                fail("Was able to persist a second object with the same value as an existing record in a unique constraint!");
            }
            catch (Exception e)
            {
                LOG.info(e);
                // Expected to come here with a duplicate key exception
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
            clean(Employee.class);
        }
    }

    /**
     * Test of the column width specification.
     * Test the PMF property "datanucleus.rdbms.stringLengthExceededAction".
     */
    public void testColumnWidth()
    {
        try
        {
            // 1). Persist an object with a "serialNo" too long for the column, and expect an Exception
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = new Employee(245, "Fred", "Flintstone", "fred.flintstone@warnerbros.com", (float)178.90, "123456789012345");
                pm.makePersistent(e);
                tx.commit();
                fail("Persisted an object with a field value that was too long for the column storing it!");
            }
            catch (JDOFatalUserException e)
            {
                // Expected this to be thrown
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // 2). Persist an object with a "serialNo" too long for the column, and use PMF option to truncate
            Properties userProps = new Properties();
            userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_STRING_LENGTH_EXCEEDED_ACTION, "TRUNCATE");
            PersistenceManagerFactory pmf2 = getPMF(1, userProps);
            pm = pmf2.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = new Employee(245, "Fred", "Flintstone", "fred.flintstone@warnerbros.com", (float)178.90, 
                    "123456789012345");
                pm.makePersistent(e);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown when persisting object with too-long String field but truncate selected");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf2.close();
        }
        finally
        {
            clean(Employee.class);
        }
    }

    /**
     * Test of the Read-Only datastore facility.
     * Should prevent all attempts to write to the datastore.
     */
    public void testReadOnlyDatastore()
    {
        try
        {
            // Create the necessary table and create a few objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getExtent(Developer.class); // Make sure our read-write PMF has schema for this class
                pm.getExtent(Manager.class); // Make sure our read-write PMF has schema for this class
                Employee e = new Employee(123, "Barney", "Rubble", "barney.rubble@warnerbros.com", 
                    (float)123.45, "1245C");
                pm.makePersistent(e);
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

            // Create a PMF for our read-only schema
            Properties userProps = new Properties();
            userProps.setProperty(PropertyNames.PROPERTY_DATASTORE_READONLY, "true");
            PersistenceManagerFactory pmf2 = getPMF(1, userProps);
            assertTrue("The PMF should have had the ReadOnlyDatastore property, yet hasn't", getConfigurationForPMF(pmf2).getBooleanProperty(PropertyNames.PROPERTY_DATASTORE_READONLY));

            PersistenceManager pm2 = pmf2.getPersistenceManager();

            // a). Try makePersistent
            Transaction tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();
                Employee e = new Employee(123, "Barney", "Rubble", "barney.rubble@warnerbros.com", 
                    (float)123.45, "1245C");
                pm2.makePersistent(e);
                tx2.commit();
                assertTrue("Should have thrown an exception when trying makePersistent on ReadOnly datastore", false);
            }
            catch (Exception e)
            {
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // b). Try deletePersistent
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();
                Extent ex = pm2.getExtent(Employee.class, true);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Employee e = (Employee)iter.next();
                    pm2.deletePersistent(e);
                }
                tx2.commit();
                assertTrue("Should have thrown an exception when trying deletePersistent on ReadOnly datastore", false);
            }
            catch (Exception e)
            {
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // c). Try update
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                Extent ex = pm2.getExtent(Employee.class, true);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Employee e = (Employee)iter.next();
                    e.setAge(23);
                }

                tx2.commit();
                assertTrue("Should have thrown an exception when modifying an object on ReadOnly datastore", false);
            }
            catch (Exception e)
            {
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // d). Try query
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                Query q = pm2.newQuery(Employee.class);
                Collection results=(Collection)q.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    resultsIter.next();
                }

                tx2.commit();
            }
            catch (Exception e)
            {
                assertTrue("Should have been able to access objects on a ReadOnly datastore", false);
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            pm2.close();
            pmf2.close();
        }
        finally
        {
            clean(Employee.class);
        }
    }

    /**
     * Test of the fixed datastore facility.
     * Should prevent all attempts to change tables in the datastore, yet allow insert/delete of rows.
     */
    public void testFixedDatastore()
    {
        try
        {
            // Create the necessary table and create a few objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = new Employee(123, "Barney", "Rubble", "barney.rubble@warnerbros.com", 
                    (float)123.45, "1245C");
                pm.makePersistent(e);
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

            // Create a PMF for our read-only schema
            Properties userProps = new Properties();
            userProps.setProperty(PropertyNames.PROPERTY_SCHEMA_AUTOCREATE_TABLES, "false");
            PersistenceManagerFactory pmf2 = getPMF(1, userProps);
            assertFalse("The PMF should have had the AutoCreate property as false, yet hasn't",
                getConfigurationForPMF(pmf2).getBooleanProperty(PropertyNames.PROPERTY_SCHEMA_AUTOCREATE_TABLES));

            PersistenceManager pm2=pmf2.getPersistenceManager();

            // a). Try makePersistent
            Transaction tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();
                Employee e = new Employee(123, "Barney", "Rubble", "barney.rubble@warnerbros.com", (float)123.45, "1245D");
                pm2.makePersistent(e);
                tx2.commit();
            }
            catch (Exception e)
            {
                assertTrue("Should not have thrown an exception when trying makePersistent on fixed datastore", false);
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // b). Try deletePersistent
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                Extent ex = pm2.getExtent(Employee.class, true);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Employee e = (Employee)iter.next();
                    pm2.deletePersistent(e);
                }
                tx2.commit();
            }
            catch (Exception e)
            {
                assertTrue("Should not have thrown an exception when trying deletePersistent on fixed datastore", false);
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // c). Try update
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                Extent ex = pm2.getExtent(Employee.class, true);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Employee e = (Employee)iter.next();
                    e.setAge(21);
                }

                tx2.commit();
            }
            catch (Exception e)
            {
                assertTrue("Should not have thrown an exception when modifying an object on fixed datastore", false);
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // d). Try query
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                Query q = pm2.newQuery(Employee.class);
                Collection results = (Collection)q.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    resultsIter.next();
                }

                tx2.commit();
            }
            catch (Exception e)
            {
                assertTrue("Should have been able to access objects on a fixed datastore", false);
                LOG.error(e);
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            pm2.close();
            pmf2.close();
        }
        finally
        {
            clean(Employee.class);
        }
    }

    /**
     * Test conflicts in naming between columns.
     */
    public void testDuplicateColumnNamesInSimpleAssociations()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Test conflicts in column naming in ConflictUser class.
            // 2 columns have been given the same name and should error
            boolean success = false;
            try 
            {
                tx.begin();
                ConflictRole conflictRole = new ConflictRole();
                conflictRole.setRole("rolename");
                ConflictUser conflictUser = new ConflictUser();
                conflictUser.setUser("fred");
                conflictUser.setRole(conflictRole);
                conflictUser.setRoleid("blabla");
                pm.makePersistent(conflictUser);
                tx.commit();
            }
            catch (JDOFatalInternalException jdoex)
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                success = true;
            }

            if (!success)
            {
                fail("completed an unexpected persistence of an object.");
            }

            // Test conflicts in column naming in ConflictAssociation class.
            // A column has been given the same name as the one DN will assign to a different column
            success = false;
            try
            {
                tx.begin();
                ConflictRole conflictRole = new ConflictRole();
                conflictRole.setRole("rolename");
                ConflictAssociation conflictAssociation = new ConflictAssociation();
                conflictAssociation.setAssociation("assoc1");
                conflictAssociation.setRole(conflictRole);
                conflictAssociation.setRoleid("blabla");
                pm.makePersistent(conflictAssociation);
                tx.commit();
                fail("completed an unexpected persistence of an object");
            }
            catch (JDOFatalInternalException jdoex)
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                success = true;
            }
        }
        catch(Exception e)
        {
            LOG.error("Exception thrown", e);
            fail(e.toString());             
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
     * Test the schema generation for a 1-1 bidirectional relationship with single FK.
     * Checks that the tables are correctly generated and only 1 FK is present.
     */
    public void test1To1Bidir()
    throws Exception
    {
        addClassesToSchema(new Class[] {Timer.class, Boiler.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet boilerColumnNames = new HashSet();
            boilerColumnNames.add("BOILER_ID");
            boilerColumnNames.add("MODEL");
            boilerColumnNames.add("TIMER2_TIMER_ID_OID");

            HashSet timerColumnNames = new HashSet();
            timerColumnNames.add("TIMER_ID");
            timerColumnNames.add("DIGITAL");
            timerColumnNames.add("BOILER_ID");
            timerColumnNames.add("BOILER2_BOILER_ID_OID");

            mconn = databaseMgr.getConnectionManager().getConnection(0);
            conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check BOILER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "HEATING_BOILER", boilerColumnNames);

            // Check TIMER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "HEATING_TIMER", timerColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for 1-1 bidirectional is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for a table using a secondary table.
     */
    public void testSecondaryTableSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {Printer.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet printerColumnNames = new HashSet();
            printerColumnNames.add("PRINTER_ID");
            printerColumnNames.add("MAKE");
            printerColumnNames.add("MODEL");

            HashSet tonerColumnNames = new HashSet();
            tonerColumnNames.add("PRINTER_REFID");
            tonerColumnNames.add("MODEL");
            tonerColumnNames.add("LIFETIME");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check PRINTER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PRINTER", printerColumnNames);

            // Check PRINTER_TONER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PRINTER_TONER", tonerColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for secondary tables is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test of the specification of table and column names for Interface types.
     * Provides a test for the specification of multiple columns for a reference field
     * which has multiple implementations.
     */
    public void testInterfaces()
    throws Exception
    {
        addClassesToSchema(new Class[] {ShapeHolder.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            HashSet columnNames = new HashSet();

            // Check table column names
            columnNames.add("SHAPEHOLDER_ID");
            columnNames.add("SHAPE1_SQUARE_SQUARE_ID_EID");
            columnNames.add("SHAPE1_RECTANGLE_RECTANGLE_ID_EID");
            columnNames.add("SHAPE1_CIRCLE_CIRCLE_ID_EID");
            columnNames.add("SHAPE1_TRIANGLE_TRIANGLE_ID_EID");
            columnNames.add("SHAPE1_TRIANGLE_ID_EID");
            columnNames.add("SHAPE2_SQUARE_SQUARE_ID_EID");
            columnNames.add("SHAPE2_RECTANGLE_RECTANGLE_ID_EID");
            columnNames.add("SHAPE2_CIRCLE_CIRCLE_ID_EID");
            columnNames.add("SHAPE2_TRIANGLE_TRIANGLE_ID_EID");
            columnNames.add("SHAPE2_TRIANGLE_ID_EID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SHAPEHOLDER", columnNames);
            columnNames.clear();

            // Check Set join table
            columnNames.add("SHAPEHOLDER_ID_OID");
            columnNames.add("SHAPESET1_CIRCLE_CIRCLE_ID_EID");
            columnNames.add("SHAPESET1_RECTANGLE_RECTANGLE_ID_EID");
            columnNames.add("SHAPESET1_SQUARE_SQUARE_ID_EID");
            columnNames.add("SHAPESET1_TRIANGLE_TRIANGLE_ID_EID");
            columnNames.add("SHAPESET1_TRIANGLE_ID_EID");
//            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SHAPEHOLDER_SHAPESET1", columnNames);
            columnNames.clear();

            // Check List join table
            columnNames.add("SHAPEHOLDER_ID_OID");
            columnNames.add("SHAPELIST1_CIRCLE_CIRCLE_ID_EID");
            columnNames.add("SHAPELIST1_RECTANGLE_RECTANGLE_ID_EID");
            columnNames.add("SHAPELIST1_SQUARE_SQUARE_ID_EID");
            columnNames.add("SHAPELIST1_TRIANGLE_TRIANGLE_ID_EID");
            columnNames.add("SHAPELIST1_TRIANGLE_ID_EID");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SHAPEHOLDER_SHAPELIST1", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names was incorrect when using Interface type fields. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test of the specification of table and column names for Interface types when using the
     * mapping-strategy of "identity.
     */
    public void testInterfacesMappingStrategyIdentity()
    throws Exception
    {
        addClassesToSchema(new Class[] {Diet.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            HashSet columnNames = new HashSet();

            // Check table column names
            columnNames.add("DIET_ID");
            columnNames.add("FAVOURITE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "DIET", columnNames);
            columnNames.clear();

            // Check Set join table
            columnNames.add("DIET_ID_OID");
            columnNames.add("FOOD");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "DIET_FOODS", columnNames);
            columnNames.clear();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names was incorrect when using Interface type fields. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test of the specification of table and column names for Object 1-1, 1-N relations.
     */
    public void testObjectFields()
    throws Exception
    {
        addClassesToSchema(new Class[] {ObjectHolder.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check primary table
            HashSet columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("EMBEDDEDOBJECT");
            columnNames.add("SERIALISEDOBJECT");
            columnNames.add("NONSERIALISED_IMPL1_ID");
            columnNames.add("NONSERIALISED_IMPL2_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "OBJECT_HOLDER", columnNames);

            // Check join table
            columnNames.clear();
            columnNames.add("HOLDER_ID");
            columnNames.add("OBJECT_IMPL_1_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "OBJECT_SET1_OBJECTS", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names was incorrect when using Object collection fields. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for a 1-1 unidirectional relationship.
     * Checks that the tables are correctly generated.
     */
    public void test1To1UnidirectionalSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {Login.class, LoginAccount.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet accountColumnNames = new HashSet();
            accountColumnNames.add("ID");
            accountColumnNames.add("FIRSTNAME");
            accountColumnNames.add("LASTNAME");
            accountColumnNames.add("LOGIN_ID_OID");

            HashSet loginColumnNames = new HashSet();
            loginColumnNames.add("ID");
            loginColumnNames.add("USERNAME");
            loginColumnNames.add("PASSWORD");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check LOGIN table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LOGIN", loginColumnNames);

            // Check LOGINACCOUNT table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LOGINACCOUNT", accountColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for 1-1 unidirectional is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test for overriding of columns where the base class uses "new-table" and the sub class uses "new-table".
     * The expected result is for the subclass table to have extra columns for the overridden fields.
     */
    public void testOverridingColumnsBaseNewTableSubNewTable()
    throws Exception
    {
        addClassesToSchema(new Class[] {LBase.class, LSub.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("LEVEL");

            // Check base table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "L_BASE", columnNames);

            columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("OVERRIDE_NAME");
            columnNames.add("VALUE");

            // Check sub table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "L_SUB", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names must have been ignored when creating the schema for " + 
                "inheritance case where the fields were overridden. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test for overriding of columns where the base class uses "subclass-table" and the sub class uses "new-table".
     * The expected result is for the subclass table to the columns for the base class but using the overridden names.
     */
    public void testOverridingColumnsBaseSubclassTableSubNewTable()
    throws Exception
    {
        addClassesToSchema(new Class[] {MBase.class, MSub1.class, MSub2.class, MRelated.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("VALUE");
            columnNames.add("BASE_2A_NAME");
            columnNames.add("BASE_2A_RELATED_ID");

            // Check base table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "M_SUB1_OVERRIDE", columnNames);

            columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("VALUE");
            columnNames.add("BASE_2B_NAME");
            columnNames.add("BASE_2B_RELATED_ID");

            // Check sub table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "M_SUB2_OVERRIDE", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names must have been ignored when creating the schema for " + 
                "inheritance case where the fields were overridden. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test for overriding of columns where the base class uses "new-table" and the sub class uses "superclass-table".
     * The expected result is for the table to havecolumns using the overridden names.
     */
    public void testOverridingColumnsBaseNewTableSubSuperclassTable()
    throws Exception
    {
        addClassesToSchema(new Class[] {NBase.class, NSub.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("ID");
            columnNames.add("TYPE");
            columnNames.add("LEVEL");
            columnNames.add("VALUE");
            columnNames.add("OVERRIDE_NAME");

            // Check base table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "N_BASE_OVERRIDE", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names must have been ignored when creating the schema for " + 
                "inheritance case where the fields were overridden. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for embedded PC objects.
     * Checks that the tables are correctly generated and the correct columns are present.
     */
    public void testEmbeddedPCSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {Computer.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet embeddedColumnNames = new HashSet();
            embeddedColumnNames.add("COMPUTER_ID");
            embeddedColumnNames.add("OS_NAME");
            embeddedColumnNames.add("GRAPHICS_MAKER");
            embeddedColumnNames.add("GRAPHICS_TYPE");
            embeddedColumnNames.add("GRAPHICS_MANUFACTURER_ID");
            embeddedColumnNames.add("SOUND_MAKER");
            embeddedColumnNames.add("SOUND_TYPE");
            embeddedColumnNames.add("SOUND_MANUFACTURER_ID");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check container columns
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "COMPUTER", embeddedColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC object is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for an object with collection of embedded PC objects.
     */
    public void testEmbeddedPCCollectionSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {Network.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet networkColumnNames = new HashSet();
            networkColumnNames.add("NETWORK_ID");
            networkColumnNames.add("NAME");

            HashSet joinColumnNames = new HashSet();
            joinColumnNames.add("NETWORK_ID");
            joinColumnNames.add("IDX");
            joinColumnNames.add("DESCRIPTION");
            joinColumnNames.add("DEVICE_IP_ADDR");
            joinColumnNames.add("DEVICE_NAME");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check NETWORK table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "NETWORK", networkColumnNames);

            // Check NETWORK_DEVICES join table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "NETWORK_DEVICES", joinColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC collection is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for an object with list of embedded PC objects.
     */
    public void testEmbeddedPCListSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {Processor.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet processorColumnNames = new HashSet();
            processorColumnNames.add("PROCESSOR_ID");
            processorColumnNames.add("TYPE");

            HashSet joinColumnNames = new HashSet();
            joinColumnNames.add("PROCESSOR_ID");
            joinColumnNames.add("JOB_ORDER");
            joinColumnNames.add("JOB_NAME");
            joinColumnNames.add("JOB_PRIORITY");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check NETWORK table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PROCESSOR", processorColumnNames);

            // Check NETWORK_DEVICES join table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PROCESSOR_JOBS", joinColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC list is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for an object with map of embedded PC value objects.
     */
    public void testEmbeddedPCMapSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {FilmLibrary.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet libraryColumnNames = new HashSet();
            libraryColumnNames.add("FILM_LIBRARY_ID");
            libraryColumnNames.add("OWNER");

            HashSet joinColumnNames = new HashSet();
            joinColumnNames.add("FILM_LIBRARY_ID");
            joinColumnNames.add("FILM_ALIAS");
            joinColumnNames.add("DESCRIPTION");
            joinColumnNames.add("FILM_DIRECTOR");
            joinColumnNames.add("FILM_NAME");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check FILM_LIBRARY table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "FILM_LIBRARY", libraryColumnNames);

            // Check FILM_LIBRARY_FILMS join table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "FILM_LIBRARY_FILMS", joinColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC map is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for embedded PC objects which have inheritance
     * Checks that the tables are correctly generated and the correct columns are present.
     */
    public void testEmbeddedPCInheritedSchema()
    throws Exception
    {
        addClassesToSchema(new Class[] {FittedKitchen.class, Oven.class, MultifunctionOven.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet embeddedColumnNames = new HashSet();
            embeddedColumnNames.add("KITCHEN_ID");
            embeddedColumnNames.add("MAKE");
            embeddedColumnNames.add("OVEN_DISCRIMINATOR");
            embeddedColumnNames.add("OVEN_MAKE");
            embeddedColumnNames.add("OVEN_MODEL");
            embeddedColumnNames.add("OVEN_CAPABILITIES");
            embeddedColumnNames.add("MULTIOVEN_MAKE");
            embeddedColumnNames.add("MULTIOVEN_MODEL");
            embeddedColumnNames.add("MULTIOVEN_CAPS");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check container columns
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "KITCHEN", embeddedColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC object with inheritance is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for embedded PC objects when just using "embedded=true".
     * Checks that the tables are correctly generated and the correct columns are present.
     */
    public void testEmbeddedPCSchemaWithEmbeddedFlag()
    throws Exception
    {
        addClassesToSchema(new Class[] {EmbeddedA.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet embeddedColumnNames = new HashSet();
            embeddedColumnNames.add("EMBEDDEDA_ID");
            embeddedColumnNames.add("EMBEDDEDB_ID");
            embeddedColumnNames.add("NAME");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check container columns
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "EMBEDDEDA", embeddedColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for embedded PC object is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for a M-N relationship.
     * Checks that the tables are correctly generated with a single join table.
     */
    public void testMtoN()
    throws Exception
    {
        addClassesToSchema(new Class[] {PetroleumCustomer.class, PetroleumSupplier.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet customerColumnNames = new HashSet();
            customerColumnNames.add("ID");
            customerColumnNames.add("NAME");

            HashSet supplierColumnNames = new HashSet();
            supplierColumnNames.add("ID");
            supplierColumnNames.add("NAME");

            HashSet joinColumnNames = new HashSet();
            joinColumnNames.add("CUSTOMER_ID");
            joinColumnNames.add("SUPPLIER_ID");

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check CUSTOMER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PETROLEUM_CUSTOMER", customerColumnNames);

            // Check SUPPLIER table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PETROLEUM_SUPPLIER", supplierColumnNames);

            // Check join table names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PETROLEUM_CUSTOMER_SUPPLIER", joinColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for M-N is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for an array of primitives, stored serialised and stored in
     * a join table.
     */
    public void testArrayNonPC()
    throws Exception
    {
        addClassesToSchema(new Class[] {IntArray.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            HashSet tableColumnNames = new HashSet();
            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Main table with serialised int[]
            tableColumnNames.add("INTARRAY_ID");
            tableColumnNames.add("ARRAY1");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "INTARRAY", tableColumnNames);
            tableColumnNames.clear();

            // Join table for int[]
            tableColumnNames.add("ARRAY_ID_OID");
            tableColumnNames.add("INT_VALUE");
            tableColumnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "ARRAY_INTARRAY", tableColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for serialised array is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for an array of PC objects, stored serialised and stored in
     * a join table.
     */
    public void testArrayPC()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {PersistableArray.class, ArrayElement.class});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();
            HashSet tableColumnNames = new HashSet();

            // Check column names for main table
            tableColumnNames.add("PERSISTABLEARRAY_ID");
            tableColumnNames.add("ARRAY1");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PERSISTABLEARRAY", tableColumnNames);
            tableColumnNames.clear();

            // Check column names for join table
            tableColumnNames.add("ARRAY_ID_OID");
            tableColumnNames.add("ELEMENT_ID_EID");
            tableColumnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "ARRAY_PERSISTABLEARRAY", tableColumnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Specification of table and column names for PC array is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for Sets with all possible types of declarations.
     */
    public void testSet()
    throws Exception
    {
        addClassesToSchema(new Class[] {SetHolder.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("SETHOLDER_ID");
            columnNames.add("NAME");
            columnNames.add("SETNONPC_SERIAL_1"); // Set<String> serialised
            columnNames.add("SETNONPC_SERIAL_2"); // Set<Date> serialised
            columnNames.add("SETNONPC_1"); // Set<String> with no join table, so serialised
            columnNames.add("SETNONPC_2"); // Set<Date> with no join table, so serialised
            columnNames.add("SETPC_EMBEDDED"); // Set<PC> embedded with no join table, so serialised
            columnNames.add("SETHOLDER_FK2_ID_OID"); // FK for self-referring Set
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER", columnNames);
            columnNames.clear();

            // Set<PC> using join table
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_PC", columnNames);
            columnNames.clear();

            // Set<String> using join table
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("STRING_ELEMENT");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_NONPC1", columnNames);
            columnNames.clear();

            // Set<Date> using join table
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("DATE_ELEMENT");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_NONPC2", columnNames);
            columnNames.clear();

            // Set<String> using join table with no schema info
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("ELEMENT");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_NONPC3", columnNames);
            columnNames.clear();

            // Set<PC> using join table with elements serialised
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_SERIAL");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_PCSERIAL", columnNames);
            columnNames.clear();

            // Set<PC> using shared join table
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_ID");
            columnNames.add("COLLECTION_TYPE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SETHOLDER_SHARED", columnNames);
            columnNames.clear();

            // Set<PC> using FK
            columnNames.add("PCFKSETELEMENT_ID");
            columnNames.add("NAME");
            columnNames.add("SETHOLDER_ID_OID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PCFKSETELEMENT", columnNames);
            columnNames.clear();

            // Set<PC> using FK shared
            columnNames.add("PCFKSETELEMENTSHARE_ID");
            columnNames.add("NAME");
            columnNames.add("SETHOLDER_ID_OID");
            columnNames.add("COLLECTION_TYPE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PCFKSETELEMENTSHARE", columnNames);
            columnNames.clear();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for various types of Set fields is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for Lists with all possible types of declarations.
     */
    public void testList()
    throws Exception
    {
        addClassesToSchema(new Class[] {ListHolder.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("LISTHOLDER_ID");
            columnNames.add("LISTNONPC_SERIAL_1"); // List<String> serialised
            columnNames.add("LISTNONPC_SERIAL_2"); // List<Date> serialised
            columnNames.add("LISTNONPC_1"); // List<String> with no join table, so serialised
            columnNames.add("LISTNONPC_2"); // List<Date> with no join table, so serialised
            columnNames.add("LISTPC_EMBEDDED"); // List<PC> embedded with no join table, so serialised
            columnNames.add("LISTHOLDER_FK2_ID_OID"); // FK for self-referring List
            columnNames.add("LISTHOLDER_FK2_IDX"); // IDX for self-referring List
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER", columnNames);
            columnNames.clear();

            //Liset<PC> using join table
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_ID");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER_PC", columnNames);
            columnNames.clear();

            // List<String> using join table
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("STRING_ELEMENT");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER_NONPC1", columnNames);
            columnNames.clear();

            // List<Date> using join table
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("DATE_ELEMENT");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER_NONPC2", columnNames);
            columnNames.clear();

            // List<PC> using join table with elements serialised
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_SERIAL");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER_PCSERIAL", columnNames);
            columnNames.clear();

            // List<PC> using shared join table
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("PCJOINELEMENT_ID");
            columnNames.add("IDX");
            columnNames.add("COLLECTION_TYPE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "LISTHOLDER_SHARED", columnNames);
            columnNames.clear();

            // List<PC> using FK
            columnNames.add("PCFKLISTELEMENT_ID");
            columnNames.add("NAME");
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("LISTHOLDER2_ID_OID");
            columnNames.add("IDX");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PCFKLISTELEMENT", columnNames);
            columnNames.clear();

            // List<PC> using FK shared
            columnNames.add("PCFKLISTELEMENTSHARE_ID");
            columnNames.add("NAME");
            columnNames.add("LISTHOLDER_ID_OID");
            columnNames.add("IDX");
            columnNames.add("COLLECTION_TYPE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "PCFKLISTELEMENTSHARE", columnNames);
            columnNames.clear();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for various types of List fields is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test the schema generation for Sets with all possible types of declarations.
     */
    public void testMap()
    throws Exception
    {
        addClassesToSchema(new Class[] {MapHolder.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet columnNames = new HashSet();
            columnNames.add("MAPHOLDER_ID");
            columnNames.add("NAME");
            columnNames.add("MAPNONNON"); // Map<String,String> without join table, so serialised
            columnNames.add("MAPSERIAL"); // Map<String,String> serialised
            columnNames.add("MAPHOLDER_FK2_ID_OID"); // FK for self-referring Map
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER", columnNames);
            columnNames.clear();

            // Map<Non,Non> using join table
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY");
            columnNames.add("VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_NON_NON", columnNames);
            columnNames.clear();

            // Map<Non,Non> using join table and no schema info
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY");
            columnNames.add("VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_NON_NON2", columnNames);
            columnNames.clear();

            // Map<Non,PC> using join table
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY");
            columnNames.add("VALUE_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_NON_PC", columnNames);
            columnNames.clear();

            // Map<Non,Non> using join table
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY");
            columnNames.add("VALUE_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_NON_PC", columnNames);
            columnNames.clear();

            // Map<PC,Non> using join table
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY_ID");
            columnNames.add("VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_PC_NON", columnNames);
            columnNames.clear();

            // Map<PC,PC> using join table
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY_ID");
            columnNames.add("VALUE_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_PC_PC", columnNames);
            columnNames.clear();

            // Map<Non,PC> using join table with serialised value
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("KEY");
            columnNames.add("VALUE_SERIAL");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER_NON_PCSERIAL", columnNames);
            columnNames.clear();

            // Map<Non,PC> using FK with key stored in value
            columnNames.add("MAPFKVALUEITEM_ID");
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("NAME");
            columnNames.add("DESC");
            columnNames.add("KEY");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPFKVALUEITEM", columnNames);
            columnNames.clear();

            // Map<PC,Non> using FK with value stored in key
            columnNames.add("MAPFKKEYITEM_ID");
            columnNames.add("MAPHOLDER_ID_OID");
            columnNames.add("NAME");
            columnNames.add("DESC");
            columnNames.add("VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPFKKEYITEM", columnNames);
            columnNames.clear();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names for various types of Map fields is incorrect. Exception was thrown : " + e.getMessage());
        }
        finally
        {
            if (conn != null)
            {
                mconn.close();
            }
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test of the specification and persistence of a class with defaulted fields.
     */
    public void testDefaultedFields()
    {
        addClassesToSchema(new Class[] {Employee.class});

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Employee e = new Employee(245, "Fred", "Flintstone", "fred.flintstone@warnerbros.com", (float)178.90, 
                    "1245C");
                pm.makePersistent(e);
                tx.commit();
                id = pm.getObjectId(e);
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Persistence of object with defaulted fields failed when should have just used defaults : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf.getDataStoreCache().evictAll(false, Employee.class); // Avoid L2 cache interference if enabled

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e = (Employee)pm.getObjectById(id);
                assertEquals("Defaulted SalaryCurrency is incorrect", "GBP", e.getSalaryCurrency());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Checking of object with defaulted fields failed : " + e.getMessage());
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
            clean(Employee.class);
        }
    }

    /**
     * Test of the allows-null facility in MetaData.
     * Tests the capability for basic field type only. 
     */
    public void testNullsAllowed()
    {
        try
        {
            // Create the necessary table and create a few objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            Object oid=null;
            try
            {
                tx.begin();
                Person p = new Person(101, null, "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(p);
                tx.commit();
                fail("Error persisting an object that had a allows-null field - should have failed, but persisted ok!");

                oid = pm.getObjectId(p);
            }
            catch (Exception e)
            {
                // Do nothing since we expect an exception here
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Persist an object correctly this time
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@warnerbros.com");
                pm.makePersistent(p);
                tx.commit();
                oid = pm.getObjectId(p);
            }
            catch (Exception e)
            {
                fail("Error persisting an object that had a allows-null field!");
                LOG.error("Error persisting an object that should have persisted fine but failed with " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object and check it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = (Person) pm.getObjectById(oid, false);
                assertTrue("Field that should have been null is " + p.getFirstName(),
                    (p.getFirstName() != null && p.getFirstName().equals("Fred")));
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error retrieving an object that had a allows-null field");
                LOG.error("Exception thrown while retrieving object in allows-null test " + e.getMessage());
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
            clean(Person.class);
        }
    }

    /**
     * Samples creates 3 tables, 2 owners for FK collections, and 1 element table which has both FKs to the owners.
     */
    public void testTableWithMultipleCollectionOwners()
    {
        addClassesToSchema(new Class[] {CollElementOwner1.class, CollElementOwner2.class, CollElement.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null;
        ManagedConnection mconn = null; 
        try
        {
            tx.begin();

            HashSet columnNames = new HashSet();
            columnNames.add("COLL_ELEMENT_ID");
            columnNames.add("COLL_ELEMENT_NAME");
            columnNames.add("OWNER1_ID");
            columnNames.add("OWNER2_ID");

            mconn = databaseMgr.getConnectionManager().getConnection(0);
            conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "COLL_ELEMENT", columnNames);
 
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names has error in creation of schema for CollElement. Exception was thrown : " + e.getMessage());
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
     * Samples creates 3 tables, with the N-1 relation using a join table.
     */
    public void testManyToOneUnidirJoin()
    {
        addClassesToSchema(new Class[] {CarRental.class, HireCar.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null;
        ManagedConnection mconn = null; 
        try
        {
            tx.begin();

            Set<String> columnNames = new HashSet<>();

            mconn = databaseMgr.getConnectionManager().getConnection(0);
            conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check table column names

            columnNames.clear();
            columnNames.add("REGISTRATIONID");
            columnNames.add("MAKE");
            columnNames.add("MODEL");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JDO_HIRECAR", columnNames);

            columnNames.clear();
            columnNames.add("CUSTOMERID");
            columnNames.add("STARTDATE");
            columnNames.add("ENDDATE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JDO_CARRENTAL", columnNames);

            columnNames.clear();
            columnNames.add("CUSTOMERID_OID");
            columnNames.add("REGISTRATIONID_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JDO_CARRENTAL_HIRE_JOIN", columnNames);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names has error in creation of schema for Many-One UNIDIR via join. Exception was thrown : " + e.getMessage());
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

    // ------------------------------------ Utilities --------------------------------------
    
    /**
     * Utility to execute a JDBC statement.
     * @param stmt
     */
    protected void runStmt(String stmt)
    {
        Connection con = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            LOG.debug(stmt);
            pm.currentTransaction().begin();
            con = (Connection) ((RDBMSStoreManager)storeMgr).getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
            con.prepareStatement(stmt).execute();
            pm.currentTransaction().commit();
        }
        catch (SQLException e)
        {
            LOG.error("Error running statement: "+stmt+"\n"+e);
            fail("Error running statement: "+stmt+"\n"+e);
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