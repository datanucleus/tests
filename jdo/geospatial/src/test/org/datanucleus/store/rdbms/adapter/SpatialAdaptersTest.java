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

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.tests.JDOPersistenceTestCase;

public class SpatialAdaptersTest extends JDOPersistenceTestCase
{

    public SpatialAdaptersTest(String name) {
		super(name);
	}

	public void testDatabaseProductNames() throws SQLException
    {
        PluginManager pluginMgr = ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getPluginManager();
        ClassLoaderResolver clr = ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getClassLoaderResolver(null);

        DatastoreAdapterFactory factory = DatastoreAdapterFactory.getInstance();
        
        assertEquals("org.datanucleus.store.rdbms.adapter.MySQLSpatialAdapter", factory.getAdapterClass(pluginMgr, null, "MySQL", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.OracleSpatialAdapter", factory.getAdapterClass(pluginMgr, null, "Oracle", clr).getName());
        assertEquals("org.datanucleus.store.rdbms.adapter.PostGISAdapter", factory.getAdapterClass(pluginMgr, null, "PostgreSQL", clr).getName());
    }

}
