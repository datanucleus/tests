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
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.embedded.collection.EmbeddedCollElement;
import org.datanucleus.samples.annotations.embedded.collection.EmbeddedCollectionOwner;
import org.datanucleus.samples.annotations.embedded.map.EmbeddedMapKey;
import org.datanucleus.samples.annotations.embedded.map.EmbeddedMapValue;
import org.datanucleus.samples.annotations.embedded.pc.EmbeddedPC;
import org.datanucleus.samples.annotations.embedded.pc.EmbeddedPCOwner;
import org.datanucleus.samples.annotations.embedded.map.EmbeddedMapOwner;
import org.datanucleus.samples.annotations.inheritance.InheritA;
import org.datanucleus.samples.annotations.inheritance.InheritA1;
import org.datanucleus.samples.annotations.inheritance.InheritA2;
import org.datanucleus.samples.annotations.inheritance.InheritA2a;
import org.datanucleus.samples.annotations.inheritance.InheritB;
import org.datanucleus.samples.annotations.inheritance.InheritB1;
import org.datanucleus.samples.annotations.inheritance.InheritB2;
import org.datanucleus.samples.annotations.inheritance.InheritC;
import org.datanucleus.samples.annotations.inheritance.InheritC1;
import org.datanucleus.samples.annotations.inheritance.InheritC2;
import org.datanucleus.samples.annotations.one_many.collection.CollectionHolder1;
import org.datanucleus.samples.annotations.one_many.collection.CollectionHolder1Element;
import org.datanucleus.samples.annotations.one_many.map.MapHolder1;
import org.datanucleus.samples.annotations.one_many.map.MapHolder1Key;
import org.datanucleus.samples.annotations.one_many.map.MapHolder1Value;
import org.datanucleus.samples.annotations.types.basic.DateHolder;
import org.datanucleus.samples.xml.one_many.map.MapHolder1Xml;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

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

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
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

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
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

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("NAME");
            columnNames.add("NAME1");
            columnNames.add("NAME2");
            columnNames.add("DTYPE");

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

    /**
     * Test for allows null setting.
     */
    public void testAllowsNull()
    throws Exception
    {
        addClassesToSchema(new Class[] {DateHolder.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("ID");
            columnNames.add("DATEFIELD");
            columnNames.add("DATEFIELD2");

            // Check base table column names
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_ANN_DATEHOLDER", columnNames);

            String insensitiveTableName = RDBMSTestHelper.getIdentifierInCaseOfAdapter(storeMgr, "JPA_ANN_DATEHOLDER", false);
            ResultSet rs = dmd.getColumns(null, null, insensitiveTableName, null);
            while (rs.next())
            {
                String colName = rs.getString(4);
                int nullValue = rs.getInt(11);
                if (colName.equalsIgnoreCase("DATEFIELD"))
                {
                    if (nullValue != 1)
                    {
                        fail("Column " + colName + " should have allowed nulls but doesnt");
                    }
                }
                else if (colName.equalsIgnoreCase("DATEFIELD2"))
                {
                    if (nullValue != 1)
                    {
                        fail("Column " + colName + " should have allowed nulls but doesnt");
                    }
                }
                else if (colName.equalsIgnoreCase("ID"))
                {
                    if (nullValue != 0)
                    {
                        fail("Column " + colName + " shouldnt have allowed nulls but does");
                    }
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Specification of table and column names gave error when checking schema. Exception was thrown : " + e.getMessage());
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
     * Test for JPA Map<NonPC, NonPC> using annotations.
     */
    public void testMapOfSimpleSimpleViaAnnotations()
    throws Exception
    {
        addClassesToSchema(new Class[] {MapHolder1.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("JPA_AN_MAPHOLDER1_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_MAPHOLDER1", columnNames);

            HashSet<String> columnNames2 = new HashSet<String>();
            columnNames2.add("MAPHOLDER1_ID");
            columnNames2.add("PROP_NAME");
            columnNames2.add("PROP_VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_MAPHOLDER1_PROPS", columnNames2);

            HashSet<String> columnNames3 = new HashSet<String>();
            columnNames3.add("MAPHOLDER1_JPA_AN_MAPHOLDER1_ID");
            columnNames3.add("PROPERTIES2_KEY");
            columnNames3.add("PROPERTIES2_VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER1_PROPERTIES2", columnNames3);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA Map<NonPC, NonPC> using xml.
     */
    public void testMapOfSimpleSimpleViaXml()
    throws Exception
    {
        addClassesToSchema(new Class[] {MapHolder1Xml.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("JPA_XML_MAPHOLDER1_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_XML_MAPHOLDER1", columnNames);

            HashSet<String> columnNames2 = new HashSet<String>();
            columnNames2.add("MAPHOLDER1_ID");
            columnNames2.add("PROP_NAME");
            columnNames2.add("PROP_VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_XML_MAPHOLDER1_PROPS", columnNames2);

            HashSet<String> columnNames3 = new HashSet<String>();
            columnNames3.add("MAPHOLDER1XML_JPA_XML_MAPHOLDER1_ID");
            columnNames3.add("PROPERTIES2_KEY");
            columnNames3.add("PROPERTIES2_VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "MAPHOLDER1XML_PROPERTIES2", columnNames3);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA embedded map keys/values.
     */
    public void testEmbeddedMap()
    throws Exception
    {
        addClassesToSchema(new Class[] {EmbeddedMapOwner.class, EmbeddedMapKey.class, EmbeddedMapValue.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Map with embedded value taking default value column names
            Set<String> columnNames = new HashSet<String>();
            columnNames.add("JPA_MAP_EMB_OWNER_ID"); // FK to owner
            columnNames.add("MAPEMBEDDEDVALUE_KEY"); // Key
            columnNames.add("NAME"); // Value "name"
            columnNames.add("VALUE"); // Value "value"
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_MAP_EMB_VALUE", columnNames);

            // Map with embedded value overriding the value column names
            Set<String> columnNames2 = new HashSet<String>();
            columnNames2.add("JPA_MAP_EMB_OWNER_ID"); // FK to owner
            columnNames2.add("MAP_KEY"); // Key "name"
            columnNames2.add("MAP_VALUE_NAME"); // Value "name"
            columnNames2.add("MAP_VALUE_VALUE"); // Value "value"
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_MAP_EMB_VALUE_OVERRIDE", columnNames2);

            // Map with embedded key taking default key column names
            Set<String> columnNames3 = new HashSet<String>();
            columnNames3.add("JPA_MAP_EMB_OWNER_ID"); // FK to owner
            columnNames3.add("NAME"); // Key "name"
            columnNames3.add("VALUE"); // Key "value"
            columnNames3.add("MAPEMBEDDEDKEY_VALUE"); // Value
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_MAP_EMB_KEY", columnNames3);

            // Map with embedded key overriding the key column names
            Set<String> columnNames4 = new HashSet<String>();
            columnNames4.add("JPA_MAP_EMB_OWNER_ID"); // FK to owner
            columnNames4.add("MAP_KEY_NAME"); // Key "name"
            columnNames4.add("MAP_KEY_VALUE"); // Key "value"
            columnNames4.add("MAPEMBEDDEDKEYOVERRIDE_VALUE"); // Value
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_MAP_EMB_KEY_OVERRIDE", columnNames4);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA embedded collection elements.
     */
    public void testEmbeddedCollection()
    throws Exception
    {
        addClassesToSchema(new Class[] {EmbeddedCollectionOwner.class, EmbeddedCollElement.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Map with embedded value taking default value column names
            Set<String> columnNames = new HashSet<String>();
            columnNames.add("JPA_COLL_EMB_OWNER_ID"); // FK to owner
            columnNames.add("NAME"); // Element "name"
            columnNames.add("VALUE"); // Element "value"
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_COLL_EMB", columnNames);

            // Map with embedded value overriding the value column names
            Set<String> columnNames2 = new HashSet<String>();
            columnNames2.add("JPA_COLL_EMB_OWNER_ID"); // FK to owner
            columnNames2.add("COLL_ELEM_NAME"); // Element "name"
            columnNames2.add("COLL_ELEM_VALUE"); // Element "value"
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_COLL_EMB_OVERRIDE", columnNames2);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA embedded PC.
     */
    public void testEmbeddedPC()
    throws Exception
    {
        addClassesToSchema(new Class[] {EmbeddedPCOwner.class, EmbeddedPC.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Map with embedded value taking default value column names
            Set<String> columnNames = new HashSet<String>();
            columnNames.add("ID"); // Id
            columnNames.add("EMB_NAME"); // PC "name"
            columnNames.add("EMB_VALUE"); // PC "value"
            columnNames.add("PC_EMB_NAME"); // PC "name" (overridden)
            columnNames.add("PC_EMB_VALUE"); // PC "value" (overridden)
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_PC_EMBEDDED_OWNER", columnNames);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA map using join table with entity keys/values.
     */
    public void testMapJoinTableEntityEntity()
    throws Exception
    {
        addClassesToSchema(new Class[] {MapHolder1.class, MapHolder1Key.class, MapHolder1Value.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Map with user-specified join table namings
            Set<String> columnNames = new HashSet<String>();
            columnNames.add("MAP4_OWNER_ID"); // FK to owner
            columnNames.add("MAP4_KEY"); // Key
            columnNames.add("MAP4_VALUE"); // Value
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_MAPHOLDER1_MAP4", columnNames);

            // Map with default join table namings
            columnNames = new HashSet<String>();
            columnNames.add("MAPHOLDER1_JPA_AN_MAPHOLDER1_ID"); // FK to owner
            columnNames.add("MAP3_KEY"); // Key
            columnNames.add("MAP3_ID"); // Value
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_MAPHOLDER1_MAPHOLDER1VALUE", columnNames);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA Collection<NonPC> using annotations.
     */
    public void testCollectionOfSimpleViaAnnotations()
    throws Exception
    {
        addClassesToSchema(new Class[] {MapHolder1.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("JPA_AN_COLLHOLDER1_ID");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_COLLHOLDER1", columnNames);

            HashSet<String> columnNames2 = new HashSet<String>();
            columnNames2.add("COLLHOLDER1_ID");
            columnNames2.add("PROP_VALUE");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_COLLHOLDER1_STRINGS", columnNames2);

            HashSet<String> columnNames3 = new HashSet<String>();
            columnNames3.add("COLLECTIONHOLDER1_JPA_AN_COLLHOLDER1_ID");
            columnNames3.add("COLLBASIC2_ELEMENT");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "COLLECTIONHOLDER1_COLLBASIC2", columnNames3);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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
     * Test for JPA collection using join table with entity elements.
     */
    public void testCollectionJoinTableEntity()
    throws Exception
    {
        addClassesToSchema(new Class[] {CollectionHolder1.class, CollectionHolder1Element.class});

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
        Connection conn = null; ManagedConnection mconn = null;
        try
        {
            tx.begin();

            mconn = databaseMgr.getConnectionManager().getConnection(0); conn = (Connection) mconn.getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // User-specified join table namings
            Set<String> columnNames = new HashSet<String>();
            columnNames.add("COLLECTIONHOLDER1_JPA_AN_COLLHOLDER1_ID"); // FK to owner
            columnNames.add("COLL3_ID"); // Element FK
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_COLLHOLDER1_COLLECTIONHOLDER1ELEMENT", columnNames);

            // Default join table namings
            columnNames = new HashSet<String>();
            columnNames.add("COLL4_OWNER_ID"); // FK to owner
            columnNames.add("COLL4_ELEMENT"); // Element FK
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "JPA_AN_COLLHOLDER1_COLL4", columnNames);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown : " + e.getMessage());
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