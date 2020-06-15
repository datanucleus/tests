/**********************************************************************
Copyright (c) 2006 Thomas Marti and others. All rights reserved.
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
package org.datanucleus.store.rdbms.adapter;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.PersistenceNucleusContextImpl;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

/**
 * Tests for "org.datanucleus.store.rdbms.adapter.RDBMSAdapterFactory".
 */
public class RDBMSAdapterFactoryTest extends TestCase
{
    DatastoreAdapterFactory factory;
    ClassLoaderResolver clr;
    PluginManager pluginMgr;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        NucleusContext ctxt = new PersistenceNucleusContextImpl("JDO", null);
        pluginMgr = ctxt.getPluginManager();
        clr = ctxt.getClassLoaderResolver(null);

        // Load RDBMS resources since this is normally done on the init of RDBMSStoreManager and we aren't doing that
        Localiser.registerBundle("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());

        factory = DatastoreAdapterFactory.getInstance();
    }
    /**
     * Test the lookup of adapter from the product name.
     * @throws SQLException
     */
    public void testDatabaseProductNames() throws SQLException
    {
        assertEquals("org.datanucleus.store.rdbms.adapter.DerbyAdapter", factory.getAdapterClass(pluginMgr, null, "Apache Derby", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.DerbyAdapter", factory.getAdapterClass(pluginMgr, null, "Derby", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.DerbyAdapter", factory.getAdapterClass(pluginMgr, null, "Cloudscape", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.DB2Adapter", factory.getAdapterClass(pluginMgr, null, "DB2", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.DB2Adapter", factory.getAdapterClass(pluginMgr, null, "DB2/NT", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.DB2AS400Adapter", factory.getAdapterClass(pluginMgr, null, "DB2 AS/400", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.DB2AS400Adapter", factory.getAdapterClass(pluginMgr, null, "DB2 UDB for AS/400", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.FirebirdAdapter", factory.getAdapterClass(pluginMgr, null, "Firebird", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.FirebirdAdapter", factory.getAdapterClass(pluginMgr, null, "Interbase", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.SQLServerAdapter", factory.getAdapterClass(pluginMgr, null, "Microsoft SQL Server", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.H2Adapter", factory.getAdapterClass(pluginMgr, null, "H2", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.HSQLAdapter", factory.getAdapterClass(pluginMgr, null, "HSQL Database Engine", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.InformixAdapter", factory.getAdapterClass(pluginMgr, null, "Informix", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.MySQLAdapter", factory.getAdapterClass(pluginMgr, null, "MySQL", clr).getName());

        assertEquals("org.datanucleus.store.rdbms.adapter.SybaseAdapter", factory.getAdapterClass(pluginMgr, null, "Adaptive Server Anywhere", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.SybaseAdapter", factory.getAdapterClass(pluginMgr, null, "Adaptive Server Enterprise", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.SybaseAdapter", factory.getAdapterClass(pluginMgr, null, "Adaptive Server IQ", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.SybaseAdapter", factory.getAdapterClass(pluginMgr, null, "Sybase", clr).getName());
        
        assertEquals("org.datanucleus.store.rdbms.adapter.OracleAdapter", factory.getAdapterClass(pluginMgr, null, "Oracle", clr).getName());
        
        assertEquals("org.datanucleus.store.rdbms.adapter.PointbaseAdapter", factory.getAdapterClass(pluginMgr, null, "Pointbase", clr).getName());
        
        assertEquals("org.datanucleus.store.rdbms.adapter.PostgreSQLAdapter", factory.getAdapterClass(pluginMgr, null, "PostgreSQL", clr).getName());
        
        assertEquals("org.datanucleus.store.rdbms.adapter.SAPDBAdapter", factory.getAdapterClass(pluginMgr, null, "SAP DB", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.SAPDBAdapter", factory.getAdapterClass(pluginMgr, null, "SAPDB", clr).getName());
    }
    
    /**
     * datastores are identified by product name
     * test unknown product
     */
    public void testGetNewDatastoreAdapter1()
    {
        DatabaseMetaData md = new DatabaseMetaData();
        md.setProductName("unknown");
        md.setProductVersion("1");
        DatastoreAdapter adapter = factory.getNewDatastoreAdapter(clr, md, null, pluginMgr);
        assertNull(adapter);
    }
    /**
     * datastores are identified by product name
     * test unknown product with given adapterClassName  
     */
    public void testGetNewDatastoreAdapter2()
    {
        DatabaseMetaData md = new DatabaseMetaData();
        md.setProductName("unknown");
        md.setProductVersion("1");
        DatastoreAdapter adapter = factory.getNewDatastoreAdapter(clr, md, DerbyAdapter.class.getName(), pluginMgr);
        assertNotNull(adapter);
        assertEquals(DerbyAdapter.class.getName(), adapter.getClass().getName());
    }

    /**
     * datastores are identified by product name
     * test Derby adapter with null adapterClassName  
     */
    public void testGetNewDatastoreAdapter3()
    {
        DatabaseMetaData md = new DatabaseMetaData();
    
        //test Derby adapter  
        md.setProductName("Derby");
        md.setProductVersion("10");
        DatastoreAdapter adapter = factory.getNewDatastoreAdapter(clr, md, null, pluginMgr);
        assertNotNull(adapter);
        assertEquals(DerbyAdapter.class.getName(), adapter.getClass().getName());
    }

    /**
     * datastores are identified by product name
     * test jdbc driver returns null for product name  
     */
    public void testGetNewDatastoreAdapter4()
    {
        DatabaseMetaData md = new DatabaseMetaData();
    
        md.setProductName(null);
        DatastoreAdapter adapter = factory.getNewDatastoreAdapter(clr, md, null, pluginMgr);
        assertNull(adapter);
    }
    
}