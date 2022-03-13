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
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.PersistenceManager;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.samples.rdbms.schema.SchemaClass1;
import org.datanucleus.samples.rdbms.schema.SchemaClass2;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.schema.ForeignKeyInfo;
import org.datanucleus.store.rdbms.schema.IndexInfo;
import org.datanucleus.store.rdbms.schema.PrimaryKeyInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableFKInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableIndexInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTableInfo;
import org.datanucleus.store.rdbms.schema.RDBMSTablePKInfo;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.util.StringUtils;

/**
 * Tests for the SchemaHandler for RDBMS datastores.
 * Note that we do case-insensitive comparison of column names here.
 */
public class SchemaHandlerTest extends JDOPersistenceTestCase
{
    public SchemaHandlerTest(String name)
    {
        super(name);
    }

    /**
     * Test of the retrieval of columns.
     */
    public void testColumnRetrieval() throws SQLException
    {
        addClassesToSchema(new Class[] {SchemaClass1.class, SchemaClass2.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        StoreSchemaHandler handler = databaseMgr.getSchemaHandler();
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        Connection con = (Connection) databaseMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
        if (rdbmsVendorID.equals("cloudspanner"))
        {
            // Spanner allows information schema calls only in read-only mode
            con.setReadOnly(true);
        }

        // Retrieve and check the table for SchemaClass1
        DatastoreClass table1 = databaseMgr.getDatastoreClass(SchemaClass1.class.getName(), clr);
        RDBMSTableInfo tableInfo1 = (RDBMSTableInfo)handler.getSchemaData(con, "columns", new Object[] {table1});
        assertNotNull("TableInfo from getColumns is NULL!", tableInfo1);
        assertEquals("Number of columns for table " + table1 + " is wrong", 4, tableInfo1.getNumberOfChildren());
        Iterator colsIter = tableInfo1.getChildren().iterator();
        Collection colNamesPresent = new HashSet();
        colNamesPresent.add("TABLE1_ID1");
        colNamesPresent.add("TABLE1_ID2");
        colNamesPresent.add("NAME");
        colNamesPresent.add("OTHER_ID");
        while (colsIter.hasNext())
        {
            RDBMSColumnInfo colInfo = (RDBMSColumnInfo)colsIter.next();
            String colInfoName = colInfo.getColumnName().toUpperCase();
            if (colInfoName.equals("TABLE1_ID1") || colInfoName.equals("TABLE1_ID2") || colInfoName.equals("NAME") || colInfoName.equals("OTHER_ID"))
            {
                colNamesPresent.remove(colInfoName);
            }
        }
        assertTrue("Some columns expected were not present in the datastore table : " + 
            StringUtils.collectionToString(colNamesPresent), colNamesPresent.size() == 0);

        // Retrieve and check the table for SchemaClass2
        DatastoreClass table2 = databaseMgr.getDatastoreClass(SchemaClass2.class.getName(), clr);
        RDBMSTableInfo tableInfo2 = (RDBMSTableInfo)handler.getSchemaData(con, "columns", new Object[] {table2});
        assertEquals("Number of columns for table " + table2 + " is wrong", 3, tableInfo2.getNumberOfChildren());
        colsIter = tableInfo2.getChildren().iterator();
        colNamesPresent.clear();
        colNamesPresent.add("TABLE2_ID");
        colNamesPresent.add("NAME");
        colNamesPresent.add("VALUE");
        while (colsIter.hasNext())
        {
            RDBMSColumnInfo colInfo = (RDBMSColumnInfo)colsIter.next();
            String colInfoName = colInfo.getColumnName().toUpperCase();
            if (colInfoName.equals("TABLE2_ID"))
            {
                colNamesPresent.remove(colInfoName);
            }
            if (colInfoName.equals("NAME"))
            {
                colNamesPresent.remove(colInfoName);
                assertEquals("Length of column " + colInfo.getColumnName() + " has incorrect length", 20, colInfo.getColumnSize());
            }
            if (colInfoName.equals("VALUE"))
            {
                colNamesPresent.remove(colInfoName);
            }
        }
        assertTrue("Some columns expected were not present in the datastore table : " + 
            StringUtils.collectionToString(colNamesPresent), colNamesPresent.size() == 0);

        // Now check retrieval of a column for a table
        RDBMSColumnInfo colInfo = (RDBMSColumnInfo)handler.getSchemaData(con, "column", new Object[] {table2, "VALUE"});
        if (colInfo == null)
        {
            colInfo = (RDBMSColumnInfo)handler.getSchemaData(con, "column", new Object[] {table2, "value"});
        }
        assertNotNull("Column VALUE for table " + table2 + " was not found", colInfo);
        assertEquals("Column name is wrong", "VALUE", colInfo.getColumnName().toUpperCase());
    }

    /**
     * Test of the retrieval of FKs.
     */
    public void testForeignKeyRetrieval() throws SQLException
    {
        addClassesToSchema(new Class[] {SchemaClass1.class, SchemaClass2.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;

        // Retrieve the table for SchemaClass1
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        DatastoreClass table1 = databaseMgr.getDatastoreClass(SchemaClass1.class.getName(), clr);

        // Check for the FK using the schema handler
        StoreSchemaHandler handler = databaseMgr.getSchemaHandler();
        Connection con = (Connection) databaseMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
        if (rdbmsVendorID.equals("cloudspanner"))
        {
            // Spanner allows information schema calls only in read-only mode
            con.setReadOnly(true);
        }
        RDBMSTableFKInfo fkInfo = (RDBMSTableFKInfo)handler.getSchemaData(con, "foreign-keys", new Object[] {table1});

        // Expecting single FK between SchemaClass1.other and SchemaClass2
        assertEquals("Number of FKs for table " + table1 + " is wrong", 1, fkInfo.getNumberOfChildren());

        // Check the FK details
        ForeignKeyInfo fk = (ForeignKeyInfo)fkInfo.getChild(0);
        assertEquals("FK Name is wrong", "TABLE1_FK1", ((String)fk.getProperty("fk_name")).toUpperCase());
        assertEquals("PK Table Name is wrong", "SCHEMA_TABLE_2", ((String)fk.getProperty("pk_table_name")).toUpperCase());
        assertEquals("FK Table Name is wrong", "SCHEMA_TABLE_1", ((String)fk.getProperty("fk_table_name")).toUpperCase());
        assertEquals("PK Column Name is wrong", "TABLE2_ID", ((String)fk.getProperty("pk_column_name")).toUpperCase());
        assertEquals("FK Column Name is wrong", "OTHER_ID", ((String)fk.getProperty("fk_column_name")).toUpperCase());
    }

    /**
     * Test of the retrieval of PKs.
     */
    public void testPrimaryKeyRetrieval() throws SQLException
    {
        addClassesToSchema(new Class[] {SchemaClass1.class, SchemaClass2.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;

        // Retrieve the table for SchemaClass1
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        DatastoreClass table1 = databaseMgr.getDatastoreClass(SchemaClass1.class.getName(), clr);
        DatastoreClass table2 = databaseMgr.getDatastoreClass(SchemaClass2.class.getName(), clr);

        // Check for the FK using the schema handler
        StoreSchemaHandler handler = databaseMgr.getSchemaHandler();
        Connection con = (Connection) databaseMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
        if (rdbmsVendorID.equals("cloudspanner"))
        {
            // Spanner allows information schema calls only in read-only mode
            con.setReadOnly(true);
        }
        RDBMSTablePKInfo pkInfo1 = (RDBMSTablePKInfo)handler.getSchemaData(con, "primary-keys", new Object[] {table1});
        RDBMSTablePKInfo pkInfo2 = (RDBMSTablePKInfo)handler.getSchemaData(con, "primary-keys", new Object[] {table2});

        // Expecting 2 PK columns for SchemaClass1
        // TODO Enable checks on the PK name (when JDBC drivers return it correctly)
        assertEquals("Number of PKs for table " + table1 + " is wrong", 2, pkInfo1.getNumberOfChildren());
        PrimaryKeyInfo pk = (PrimaryKeyInfo)pkInfo1.getChild(0);
        assertEquals("Column Name is wrong", "TABLE1_ID1", ((String)pk.getProperty("column_name")).toUpperCase());
//        assertEquals("PK Name is wrong", "TABLE1_PK", pk.getProperty("pk_name"));
        pk = (PrimaryKeyInfo)pkInfo1.getChild(1);
        assertEquals("Column Name is wrong", "TABLE1_ID2", ((String)pk.getProperty("column_name")).toUpperCase());
//        assertEquals("PK Name is wrong", "TABLE1_PK", pk.getProperty("pk_name"));

        // Expecting 1 PK column for SchemaClass
        assertEquals("Number of PKs for table " + table1 + " is wrong", 1, pkInfo2.getNumberOfChildren());
        pk = (PrimaryKeyInfo)pkInfo2.getChild(0);
        assertEquals("Column Name is wrong", "TABLE2_ID", ((String)pk.getProperty("column_name")).toUpperCase());
//        assertEquals("PK Name is wrong", "TABLE2_PK", pk.getProperty("pk_name"));
    }

    /**
     * Test of the retrieval of indices.
     */
    public void testIndexRetrieval() throws SQLException
    {
        addClassesToSchema(new Class[] {SchemaClass1.class, SchemaClass2.class});

        PersistenceManager pm = pmf.getPersistenceManager();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;

        // Retrieve the table for SchemaClass1
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        DatastoreClass table1 = databaseMgr.getDatastoreClass(SchemaClass1.class.getName(), clr);
        DatastoreClass table2 = databaseMgr.getDatastoreClass(SchemaClass2.class.getName(), clr);

        // Check for the indices using the schema handler
        StoreSchemaHandler handler = databaseMgr.getSchemaHandler();
        Connection con = (Connection) databaseMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
        if (rdbmsVendorID.equals("cloudspanner"))
        {
            // Spanner allows information schema calls only in read-only mode
            con.setReadOnly(true);
        }

        RDBMSTableIndexInfo indexInfo = (RDBMSTableIndexInfo)handler.getSchemaData(con, "indices", new Object[] {table1});
        int numIndices = 3;
        if (rdbmsVendorID.equals("hsql") || rdbmsVendorID.equals("cloudspanner"))
        {
            // HSQL will create an index for the FK without asking, and we can't replace it with our own so end up with two
            numIndices = 4;
        }
        assertEquals("Number of Indices for table " + table1 + " is wrong", numIndices, indexInfo.getNumberOfChildren());
        Iterator indexIter = indexInfo.getChildren().iterator();
        while (indexIter.hasNext())
        {
            IndexInfo index = (IndexInfo)indexIter.next();
            String columnName = ((String)index.getProperty("column_name")).toUpperCase();
            boolean unique = !((Boolean)index.getProperty("non_unique")).booleanValue();
            if (columnName.equals("OTHER_ID"))
            {
                assertFalse("Index for column " + columnName + " is unique!", unique);
            }
            else if (columnName.equals("TABLE1_ID1"))
            {
                assertTrue("Index for column " + columnName + " is not unique!", unique);
            }
            else if (columnName.equals("TABLE1_ID2"))
            {
                assertTrue("Index for column " + columnName + " is not unique!", unique);
            }
            else
            {
                fail("Unexpected index " + columnName + " for table " + table1);
            }
        }

        indexInfo = (RDBMSTableIndexInfo)handler.getSchemaData(con, "indices", new Object[] {table2});
        assertEquals("Number of Indices for table " + table2 + " is wrong", 2, indexInfo.getNumberOfChildren());
        indexIter = indexInfo.getChildren().iterator();
        while (indexIter.hasNext())
        {
            IndexInfo index = (IndexInfo)indexIter.next();
            String columnName = ((String)index.getProperty("column_name")).toUpperCase();
            String indexName = ((String)index.getProperty("index_name")).toUpperCase();
            boolean unique = !((Boolean)index.getProperty("non_unique")).booleanValue();
            if (columnName.equals("VALUE"))
            {
                assertFalse("Index for column " + columnName + " is unique!", unique);
                assertEquals("Index name for column " + columnName + " is wrong!", "VALUE_IDX", indexName);
            }
            else if (columnName.equals("TABLE2_ID"))
            {
                assertTrue("Index for column " + columnName + " is not unique!", unique);
            }
            else
            {
                fail("Unexpected index " + columnName + " for table " + table1);
            }
        }
    }
}