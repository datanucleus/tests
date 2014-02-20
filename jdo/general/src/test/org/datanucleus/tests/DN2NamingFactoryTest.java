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

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.samples.annotations.embedded.EmbCls1;
import org.datanucleus.samples.annotations.embedded.EmbCls2;
import org.datanucleus.samples.annotations.embedded.EmbCls3;
import org.datanucleus.store.schema.naming.ColumnType;
import org.datanucleus.store.schema.naming.DN2NamingFactory;
import org.datanucleus.store.schema.naming.NamingCase;
import org.datanucleus.store.schema.naming.SchemaComponent;
import org.jpox.samples.array.BooleanArray;
import org.jpox.samples.embedded.Computer;
import org.jpox.samples.embedded.ComputerCard;
import org.jpox.samples.models.fitness.Gym;
import org.jpox.samples.one_many.unidir_2.UserGroup;
import org.jpox.samples.versioned.Trade1;

/**
 * Test for the "datanucleus2" naming factory.
 */
public class DN2NamingFactoryTest extends JDOPersistenceTestCase
{
    public DN2NamingFactoryTest(String name)
    {
        super(name);
    }

    public void testTableName()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(BooleanArray.class, clr);

        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.TABLE, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Table name is incorrect", "booleanarray", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.LOWER_CASE_QUOTED);
        assertEquals("Table name is incorrect", "\"booleanarray\"", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.UPPER_CASE);
        assertEquals("Table name is incorrect", "BOOLEANARRAY", factory.getTableName(cmd1));

        factory.setNamingCase(NamingCase.UPPER_CASE_QUOTED);
        assertEquals("Table name is incorrect", "\"BOOLEANARRAY\"", factory.getTableName(cmd1));
    }

    public void testJoinTableName()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(UserGroup.class, clr);

        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        factory.setMaximumLength(SchemaComponent.TABLE, 128);
        String name = factory.getTableName(cmd1.getMetaDataForMember("members"));
        assertEquals("Join table name is incorrect", "usergroup_members", name);
    }

    public void testVersionColumnName()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(Trade1.class, clr);

        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Column name for version is incorrect", "version", factory.getColumnName(cmd1, ColumnType.VERSION_COLUMN));
    }

    public void testDatastoreIdColumnName()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        AbstractClassMetaData cmd1 = mmgr.getMetaDataForClass(Gym.class, clr);

        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);
        assertEquals("Column name for datastore-id is incorrect", "gym_id", factory.getColumnName(cmd1, ColumnType.DATASTOREID_COLUMN));
    }

    public void testEmbeddedPCColumnNames()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);

        AbstractClassMetaData compCmd = mmgr.getMetaDataForClass(Computer.class, clr);
        AbstractMemberMetaData graphicsMmd = compCmd.getMetaDataForMember("graphicsCard");
        AbstractClassMetaData cardCmd = mmgr.getMetaDataForClass(ComputerCard.class, clr);
        AbstractMemberMetaData makerMmd = cardCmd.getMetaDataForMember("makerName");
        List<AbstractMemberMetaData> colMmds = new ArrayList<AbstractMemberMetaData>();
        colMmds.add(graphicsMmd);
        colMmds.add(makerMmd);
        String colName = factory.getColumnName(colMmds, 0);
        assertEquals("graphics_maker", colName); // Comes from EmbeddedMetaData override
    }

    /**
     * Test where the sample classes have no EmbeddedMetaData overriding the column names, so we just get generated names for embedded class and nested embedded class.
     */
    public void testNestedEmbeddedPCColumnNames()
    {
        JDOPersistenceManagerFactory jdoPMF = (JDOPersistenceManagerFactory) pmf;
        NucleusContext nucCtx = jdoPMF.getNucleusContext();
        ClassLoaderResolver clr = nucCtx.getClassLoaderResolver(null);
        MetaDataManager mmgr = nucCtx.getMetaDataManager();
        DN2NamingFactory factory = new DN2NamingFactory(nucCtx);
        factory.setMaximumLength(SchemaComponent.COLUMN, 128);
        factory.setNamingCase(NamingCase.LOWER_CASE);

        AbstractClassMetaData emb1Cmd = mmgr.getMetaDataForClass(EmbCls1.class, clr);
        AbstractMemberMetaData embCls2aMmd = emb1Cmd.getMetaDataForMember("embCls2a");
        AbstractMemberMetaData embCls2bMmd = emb1Cmd.getMetaDataForMember("embCls2b");

        AbstractClassMetaData emb2Cmd = mmgr.getMetaDataForClass(EmbCls2.class, clr);
        AbstractMemberMetaData embCls3Mmd = emb2Cmd.getMetaDataForMember("embCls3");
        AbstractMemberMetaData cls2NameMmd = emb2Cmd.getMetaDataForMember("cls2Name");

        AbstractClassMetaData emb3Cmd = mmgr.getMetaDataForClass(EmbCls3.class, clr);
        AbstractMemberMetaData cls3NameMmd = emb3Cmd.getMetaDataForMember("cls3Name");

        List<AbstractMemberMetaData> colMmds = new ArrayList<AbstractMemberMetaData>();
        colMmds.add(embCls2aMmd);
        colMmds.add(cls2NameMmd);
        String colName = factory.getColumnName(colMmds, 0);
        assertEquals("embcls2a_cls2name", colName);
        colMmds.clear();colMmds.add(embCls2bMmd);colMmds.add(cls2NameMmd);
        colName = factory.getColumnName(colMmds, 0);
        assertEquals("embcls2b_cls2name", colName);

        colMmds.clear();colMmds.add(embCls2aMmd);colMmds.add(embCls3Mmd);colMmds.add(cls3NameMmd);
        colName = factory.getColumnName(colMmds, 0);
        assertEquals("embcls2a_embcls3_cls3name", colName);
        colMmds.clear();colMmds.add(embCls2bMmd);colMmds.add(embCls3Mmd);colMmds.add(cls3NameMmd);
        colName = factory.getColumnName(colMmds, 0);
        assertEquals("embcls2b_embcls3_cls3name", colName);
    }
}