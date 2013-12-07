/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jpa.JPAEntityManagerFactory;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.schema.naming.ColumnType;
import org.datanucleus.store.schema.naming.JPANamingFactory;
import org.datanucleus.store.schema.naming.NamingCase;
import org.datanucleus.store.schema.naming.NamingFactory;
import org.datanucleus.store.schema.naming.SchemaComponent;
import org.jpox.samples.annotations.array.ByteArray;
import org.jpox.samples.annotations.one_many.unidir_2.UserGroup;
import org.jpox.samples.jpa.datastoreid.MyDatastoreId;
import org.jpox.samples.versioned.Trade4;

/**
 * Test for the "jpa" naming factory.
 */
public class JPANamingFactoryTest extends JPAPersistenceTestCase
{
    public JPANamingFactoryTest(String name)
    {
        super(name);
    }

    public void testTableName()
    {
        JPAEntityManagerFactory jpaEMF = (JPAEntityManagerFactory) emf;
        NucleusContext nucCtx = jpaEMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(ByteArray.class, clr);

        NamingFactory factory = new JPANamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.TABLE, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Table name is incorrect", "bytearray", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.LOWER_CASE_QUOTED);
        assertEquals("Table name is incorrect", "\"bytearray\"", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.UPPER_CASE);
        assertEquals("Table name is incorrect", "BYTEARRAY", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.UPPER_CASE_QUOTED);
        assertEquals("Table name is incorrect", "\"BYTEARRAY\"", factory.getTableName(cmd1));
    }

    public void testJoinTableName()
    {
        JPAEntityManagerFactory jpaEMF = (JPAEntityManagerFactory) emf;
        NucleusContext nucCtx = jpaEMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(UserGroup.class, clr);

        NamingFactory factory = new JPANamingFactory(nucCtx);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        factory.setMaximumLength(SchemaComponent.TABLE, 128);
        String name = factory.getTableName(cmd1.getMetaDataForMember("members"));
        assertEquals("Join table name is incorrect", "usergroup_groupmember", name);
    }

    public void testVersionColumnName()
    {
        JPAEntityManagerFactory jpaEMF = (JPAEntityManagerFactory) emf;
        NucleusContext nucCtx = jpaEMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(Trade4.class, clr);
        NamingFactory factory = new JPANamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Column name for version is incorrect", "version", factory.getColumnName(cmd1, ColumnType.VERSION_COLUMN));
    }

    public void testDatastoreIdColumnName()
    {
        JPAEntityManagerFactory jpaEMF = (JPAEntityManagerFactory) emf;
        NucleusContext nucCtx = jpaEMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(MyDatastoreId.class, clr);

        NamingFactory factory = new JPANamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Column name for datastore-id is incorrect", "mydatastoreid_id", factory.getColumnName(cmd1, ColumnType.DATASTOREID_COLUMN));
    }
}