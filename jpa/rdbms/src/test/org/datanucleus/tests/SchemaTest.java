/******************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved. 
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
*****************************************************************/
package org.datanucleus.tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.jpox.samples.annotations.inheritance.InheritA;
import org.jpox.samples.annotations.inheritance.InheritA1;
import org.jpox.samples.annotations.inheritance.InheritA2;
import org.jpox.samples.annotations.inheritance.InheritA2a;
import org.jpox.samples.annotations.inheritance.InheritB;
import org.jpox.samples.annotations.inheritance.InheritB1;
import org.jpox.samples.annotations.inheritance.InheritB2;
import org.jpox.samples.annotations.inheritance.InheritC;
import org.jpox.samples.annotations.inheritance.InheritC1;
import org.jpox.samples.annotations.inheritance.InheritC2;

/**
 * Tests for schema creation.
 * These tests only apply to a datastore that has a notion of a schema. Consequently it should not
 * be run for other datastores.
 */
public class SchemaTest extends JPAPersistenceTestCase
{
    public SchemaTest(String name)
    {
        super(name);
    }

    /**
     * Test for JPA inheritance strategy "joined" on a hierarchy of classes.
     * We have 4 classes. A base class, 2 direct subclasses, and a subclass of a subclass.
     * This should create 4 tables, with each table only having the fields for that class.
     */
    public void testInheritanceStrategyJoined()
    throws Exception
    {
        addClassesToSchema(new Class[] {InheritA.class, InheritA1.class, InheritA2.class, InheritA2a.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_A", columnNames);

            columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME1");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_A1", columnNames);

            columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME2");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_A2", columnNames);

            columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME2A");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_A2A", columnNames);

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
            em.close();
        }
    }

    /**
     * Test for JPA inheritance strategy "table-per-class" on a hierarchy of classes.
     */
    public void testInheritanceStrategyTablePerClass()
    throws Exception
    {
        addClassesToSchema(new Class[] {InheritB.class, InheritB1.class, InheritB2.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_B", columnNames);

            columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("NAME1");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_B1", columnNames);

            columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("NAME2");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_B2", columnNames);

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
            em.close();
        }
    }

    /**
     * Test for JPA inheritance strategy "single-table" on a hierarchy of classes.
     */
    public void testInheritanceStrategySingleTable()
    throws Exception
    {
        addClassesToSchema(new Class[] {InheritC.class, InheritC1.class, InheritC2.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("NAME1");
            columnNames.add("NAME2");

            // Check base table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_INHERIT_C", columnNames);

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
            em.close();
        }
    }
}