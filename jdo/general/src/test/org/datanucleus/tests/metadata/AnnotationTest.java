/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
2007 Andr� F�genschuh - some updates to MetaDataManager extraction
    ...
**********************************************************************/
package org.datanucleus.tests.metadata;

import java.util.Iterator;
import java.util.Set;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.CollectionMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import org.datanucleus.metadata.FetchGroupMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.IndexMetaData;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.samples.annotations.array.ByteArray;
import org.datanucleus.samples.annotations.models.company.Department;
import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Manager;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.models.company.PhoneNumber;
import org.datanucleus.samples.annotations.models.company.Project;
import org.datanucleus.samples.annotations.persistenceaware.AccessPublicFields;
import org.datanucleus.samples.annotations.persistentproperties.MyPropertyBean;
import org.datanucleus.samples.annotations.secondarytable.Printer;
import org.datanucleus.samples.annotations.versioned.Trade1;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for the use of JDO2 annotations and that they generate the correct internal metadata.
 */
public class AnnotationTest extends JDOPersistenceTestCase
{
    private NucleusContext nucleusCtx;
    private MetaDataManager metaDataMgr;
    private ClassLoaderResolver clr;

    public AnnotationTest(String name)
    {
        super(name);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        nucleusCtx = new NucleusContext("JDO", null);
        metaDataMgr = new JDOMetaDataManager(nucleusCtx);
        clr = new ClassLoaderResolverImpl();
    }

    @Override
    protected void tearDown() throws Exception
    {
        clr = null;
        metaDataMgr = null;
        nucleusCtx = null;

        super.tearDown();
    }

    /**
     * Test of basic JDO annotations reading capability
     */
    public void testBasic()
    {
        // Checks for Department
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        assertTrue(prefix + "detachable is wrong", cmd1.isDetachable());
        assertEquals(prefix + "identity-type is wrong", IdentityType.APPLICATION, cmd1.getIdentityType());
        assertFalse(prefix + "embedded-only is wrong", cmd1.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd1.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd1.getCatalog());
        assertNull(prefix + "schema is wrong", cmd1.getSchema());
        assertNull(prefix + "table is wrong", cmd1.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields", 3, cmd1.getNoOfManagedMembers());

        InheritanceMetaData inhmd1 = cmd1.getInheritanceMetaData();
        assertEquals("Inheritance strategy is incorrect", InheritanceStrategy.NEW_TABLE, inhmd1.getStrategy());

        // "projects"
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("projects");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "should be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertFalse(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertFalse(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertNotNull(prefix + "has no container specified!", fmd.getCollection());
        assertEquals(prefix + "should have collection of Project elements but hasnt", Project.class.getName(), fmd.getCollection()
                .getElementType());
        assertFalse(prefix + "shouldnt have collection of serialised elements but has", fmd.getCollection().isSerializedElement());
        assertFalse(prefix + "shouldnt have collection of dependent elements but has", fmd.getCollection().isDependentElement());

        // Checks for Project
        ClassMetaData cmd2 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Project.class.getName(), clr);
        prefix = cmd2.getFullClassName() + " : ";

        assertFalse(prefix + "detachable is wrong", cmd2.isDetachable());
        assertEquals(prefix + "identity-type is wrong", IdentityType.APPLICATION, cmd2.getIdentityType());
        assertEquals(prefix + "objectid-class is wrong", "javax.jdo.identity.StringIdentity", cmd2.getObjectidClass());
        assertFalse(prefix + "embedded-only is wrong", cmd2.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd2.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd2.getCatalog());
        assertNull(prefix + "schema is wrong", cmd2.getSchema());
        assertEquals(prefix + "table is wrong", "JDO_AN_PROJECT", cmd2.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields", 2, cmd2.getNoOfManagedMembers());

        InheritanceMetaData inhmd2 = cmd2.getInheritanceMetaData();
        assertEquals("Inheritance strategy is incorrect", InheritanceStrategy.NEW_TABLE, inhmd2.getStrategy());

        // "floor"
        fmd = cmd2.getMetaDataForMember("name");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());

        // "name"
        fmd = cmd2.getMetaDataForMember("budget");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "has incorrect persistent field", "budget", fmd.getName());
        assertFalse(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
    }

    /**
     * Test for use of @PersistenceAware
     */
    public void testPersistenceAware()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(AccessPublicFields.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";
        assertNotNull(prefix + "has no MetaData!", cmd1);
        assertTrue(prefix + "is not PersistenceAware yet should be",
            cmd1.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_AWARE);
    }

    /**
     * Test for use of annotations with inherited classes, testing @Inheritance and @Discriminator
     */
    public void testInheritance()
    {
        // Checks for Person
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";
        assertFalse(prefix + "detachable is wrong", cmd1.isDetachable());
        assertEquals(prefix + "identity-type is wrong", IdentityType.APPLICATION, cmd1.getIdentityType());
        assertEquals(prefix + "objectid-class is wrong", Person.Id.class.getName(), cmd1.getObjectidClass());
        assertFalse(prefix + "embedded-only is wrong", cmd1.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd1.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd1.getCatalog());
        assertNull(prefix + "schema is wrong", cmd1.getSchema());
        assertNull(prefix + "table is wrong", cmd1.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields", 8, cmd1.getNoOfManagedMembers());

        InheritanceMetaData inhmd1 = cmd1.getInheritanceMetaData();
        assertNotNull(prefix + " has no inheritance information", inhmd1);
        assertEquals(prefix + " inheritance strategy is wrong", InheritanceStrategy.NEW_TABLE, inhmd1.getStrategy());

        // "firstName"
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("firstName");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertTrue(prefix + ".firstName DFG is true but should be false", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());

        // "lastName"
        fmd = cmd1.getMetaDataForMember("lastName");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "should not be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());

        // Checks for Employee
        ClassMetaData cmd2 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        prefix = cmd2.getFullClassName() + " : ";
        assertFalse(prefix + "detachable is wrong", cmd2.isDetachable());
        assertEquals(prefix + "identity-type is wrong", IdentityType.APPLICATION, cmd2.getIdentityType());
        assertFalse(prefix + "embedded-only is wrong", cmd2.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd2.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd2.getCatalog());
        assertNull(prefix + "schema is wrong", cmd2.getSchema());
        assertNull(prefix + "table is wrong", cmd2.getTable());

        InheritanceMetaData inhmd2 = cmd2.getInheritanceMetaData();
        assertNotNull(prefix + " has no inheritance information", inhmd2);
        assertEquals(prefix + " inheritance strategy is wrong", InheritanceStrategy.NEW_TABLE, inhmd2.getStrategy());

        // "subordinates"
        fmd = cmd2.getMetaDataForMember("subordinates");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertFalse(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertFalse(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
    }

    /**
     * Test for use of annotations to define PK columns, overriding those of the superclass
     */
    public void testPrimaryKeyColumns()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        PrimaryKeyMetaData pkmd = cmd1.getPrimaryKeyMetaData();
        assertNotNull(prefix + "has null primary-key metadata!", pkmd);
        assertNotNull(prefix + "has no primary-key columns!", pkmd.getColumnMetaData());
        assertEquals(prefix + "has incorrect no of PK columns", 2, pkmd.getColumnMetaData().length);
        ColumnMetaData[] colmds = pkmd.getColumnMetaData();
        assertEquals("Name of first col of pk is incorrect", "MGR_ID", colmds[0].getName());
        assertEquals("Target of first col of pk is incorrect", "PERSON_ID", colmds[0].getTarget());
        assertEquals("Name of second col of pk is incorrect", "MGR_GLOBAL_ID", colmds[1].getName());
        assertEquals("Target of second col of pk is incorrect", "PERSON_GLOB_ID", colmds[1].getTarget());
    }

    /**
     * Test for use of annotations with fetch groups
     */
    public void testFetchGroups()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        Set<FetchGroupMetaData> fgmds = cmd1.getFetchGroupMetaData();
        assertNotNull(prefix + "fetch group info is incorrect", fgmds);
        FetchGroupMetaData fgmd = fgmds.iterator().next();
        assertEquals(prefix + "fetch group name incorrect", "names", fgmd.getName());
        assertFalse(prefix + "fetch group fields incorrect", fgmd.getPostLoad());
        Set<FetchGroupMemberMetaData> fgmmds = fgmd.getMembers();
        assertNotNull(fgmmds);
        assertEquals(prefix + "number of fetch group fields is wrong", 2, fgmmds.size());
        Iterator<FetchGroupMemberMetaData> fgmmdIter = fgmmds.iterator();
        FetchGroupMemberMetaData fgmmd0 = fgmmdIter.next();
        FetchGroupMemberMetaData fgmmd1 = fgmmdIter.next();
        assertTrue(prefix + "fields in fetch group are incorrect",
            (fgmmd0.getName().equals("firstName") && fgmmd1.getName().equals("lastName")) ||
            (fgmmd1.getName().equals("firstName") && fgmmd0.getName().equals("lastName")));
    }

    /**
     * Test for use of annotations with queries
     */
    public void testQueries()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        QueryMetaData[] qmds = cmd1.getQueries();
        assertNotNull(prefix + "query info is incorrect", qmds);
        assertEquals(prefix + "query name incorrect", "PeopleCalledSmith", qmds[0].getName());
        assertEquals(prefix + "query language incorrect", "JDOQL", qmds[0].getLanguage());
        assertFalse(prefix + "query unmodifiable incorrect", qmds[0].isUnmodifiable());
        assertFalse(prefix + "query unique incorrect", qmds[0].isUnique());
        assertEquals(prefix + "query string incorrect",
            "SELECT FROM org.jpox.samples.annotations.models.company.Person WHERE lastName == \"Smith\"",
            qmds[0].getQuery());
    }

    /**
     * Test for use of @Extension and @Extensions
     */
    public void testExtensions()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        // Test class level @Extension
        ExtensionMetaData[] extmds1 = cmd1.getExtensions();
        assertNotNull(prefix + "extension info is null!", extmds1);
        assertTrue(prefix + "extension info has incorrect number of extensions", extmds1.length == 1);
        assertEquals(prefix + "extension vendor incorrect", "datanucleus", extmds1[0].getVendorName());
        assertEquals(prefix + "extension vendor incorrect", "someExtensionProp", extmds1[0].getKey());
        assertEquals(prefix + "extension vendor incorrect", "My Value", extmds1[0].getValue());

        // Test field level @Extensions
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("departments");
        assertNotNull(prefix + "doesnt have required field", fmd);

        ExtensionMetaData[] extmds2 = fmd.getExtensions();
        assertNotNull(prefix + "extension info is null!", extmds2);
        assertEquals(prefix + "extension info has incorrect number of extensions", 2, extmds2.length);
        if (extmds2[0].getKey().equals("prop1"))
        {
            assertEquals(prefix + "extension vendor incorrect", "datanucleus", extmds2[0].getVendorName());
            assertEquals(prefix + "extension vendor incorrect", "prop1", extmds2[0].getKey());
            assertEquals(prefix + "extension vendor incorrect", "val1", extmds2[0].getValue());
            assertEquals(prefix + "extension vendor incorrect", "datanucleus", extmds2[1].getVendorName());
            assertEquals(prefix + "extension vendor incorrect", "prop2", extmds2[1].getKey());
            assertEquals(prefix + "extension vendor incorrect", "val2", extmds2[1].getValue());
        }
        else
        {
            assertEquals(prefix + "extension vendor incorrect", "datanucleus", extmds2[0].getVendorName());
            assertEquals(prefix + "extension vendor incorrect", "prop2", extmds2[0].getKey());
            assertEquals(prefix + "extension vendor incorrect", "val2", extmds2[0].getValue());
            assertEquals(prefix + "extension vendor incorrect", "datanucleus", extmds2[1].getVendorName());
            assertEquals(prefix + "extension vendor incorrect", "prop1", extmds2[1].getKey());
            assertEquals(prefix + "extension vendor incorrect", "val1", extmds2[1].getValue());
        }
    }

    /**
     * Test for use of collection with join table
     */
    public void testCollectionJoinTable()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("subordinates");
        assertNotNull(prefix + "doesnt have required field", fmd);
        CollectionMetaData colmd = fmd.getCollection();
        assertNotNull(prefix + "CollectionMetaData is null!", colmd);
        assertEquals(prefix + "CollectionMetaData elementType is incorrect", Employee.class.getName(), colmd.getElementType());
        JoinMetaData joinmd = fmd.getJoinMetaData();
        assertNotNull(prefix + "field \"subordinates\" has no join information!", joinmd);
        assertEquals(prefix + "field \"subordinates\" is stored in wrong table", "MANAGER_EMPLOYEES", fmd.getTable());
        assertEquals(prefix + "field \"subordinates\" join table has incorrect column", "MANAGER_ID", joinmd.getColumnName());
    }

    /**
     * Test for use of map with using @Key, @Value.
     */
    public void testMapKeyInValue()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("phoneNumbers");
        assertNotNull(prefix + "doesnt have required field", fmd);
        MapMetaData mapmd = fmd.getMap();
        assertNotNull(prefix + "MapMetaData is null!", mapmd);
        assertEquals(prefix + "MapMetaData keyType is incorrect", String.class.getName(), mapmd.getKeyType());
        assertEquals(prefix + "MapMetaData valueType is incorrect", PhoneNumber.class.getName(), mapmd.getValueType());
        JoinMetaData joinmd = fmd.getJoinMetaData();
        assertNull(prefix + "field \"phoneNumbers\" has join information!", joinmd);
        KeyMetaData keymd = fmd.getKeyMetaData();
        assertEquals(prefix + "field \"phoneNumbers\" has incorrect key mapped-by", "name", keymd.getMappedBy());
    }

    /**
     * Test for use of version specification in annotations
     */
    public void testVersion()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Trade1.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";

        VersionMetaData vermd = cmd1.getVersionMetaData();
        assertNotNull(prefix + "has no VersionMetaData", vermd);
        assertEquals(prefix + "has incorrect version strategy", "version-number", vermd.getVersionStrategy().toString());
        assertNotNull(prefix + "has incorrect version column info", vermd.getColumnMetaData());
        assertEquals(prefix + "has incorrect version column name", "TRADE_VERSION", vermd.getColumnMetaData().getName());
    }

    /**
     * Test of specifying annotations on getters.
     */
    public void testPersistentProperties()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(MyPropertyBean.class.getName(), clr);
        assertEquals(1, cmd1.getNoOfPrimaryKeyMembers());
    }

    /**
     * Test field of byte[] is embedded by default.
     */
    public void testByteArrayEmbeddedByDefault()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(ByteArray.class.getName(), clr);
        assertTrue(cmd1.getMetaDataForMember("array1").isEmbedded());
    }

    /**
     * Test of @Column, @Columns
     */
    public void testColumnLength()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Printer.class.getName(), clr);
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("make");
        assertEquals(1, fmd.getColumnMetaData().length);
        assertEquals("MAKE", fmd.getColumnMetaData()[0].getName());
        assertEquals("VARCHAR", fmd.getColumnMetaData()[0].getJdbcType());
        assertEquals(40, fmd.getColumnMetaData()[0].getLength().intValue());
    }

    /**
     * Test of basic JDO strategy value
     */
    public void testStrategyValue()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertEquals(IdentityStrategy.INCREMENT, cmd1.getMetaDataForMember("personNum").getValueStrategy());
    }

    /**
     * Test of @Unique at class level
     */
    public void testUniqueOnClass()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertNotNull("JDOPerson metadata should have had a Unique constraint but didnt", cmd1.getUniqueMetaData());
        assertEquals("JDOPerson metadata has incorrect Unique constraints", 1, cmd1.getUniqueMetaData().length);
        UniqueMetaData unimd = cmd1.getUniqueMetaData()[0];
        assertEquals("PERSON_NAME_EMAIL_UNIQUENESS", unimd.getName());
        assertEquals(3, unimd.getNumberOfMembers());
    }

    /**
     * Test of Index at field level
     */
    public void testIndexOnField()
    {
        ClassMetaData cmd1 = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("age");
        assertNotNull("JDOPerson.age metadata should have had an Index constraint but didnt", fmd.getIndexMetaData());
        IndexMetaData idxmd = fmd.getIndexMetaData();
        assertEquals("PERSON_AGE_IDX", idxmd.getName());
    }

    /**
     * Test for use of annotations for secondary tables, in particular @Join, @SecondaryTable.
     * Uses Printer class, storing some fields in table "PRINTER" and some in "PRINTER_TONER".
     */
    public void testSecondaryTable()
    {
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Printer.class.getName(), clr);

        assertTrue("detachable is wrong", cmd.isDetachable());
        assertEquals("identity-type is wrong", IdentityType.DATASTORE, cmd.getIdentityType());
        assertFalse("embedded-only is wrong", cmd.isEmbeddedOnly());
        assertTrue("requires-extent is wrong", cmd.isRequiresExtent());
        assertNull("catalog is wrong", cmd.getCatalog());
        assertNull("schema is wrong", cmd.getSchema());
        assertEquals("table is wrong", "JDO_AN_PRINTER", cmd.getTable());
        assertEquals("has incorrect number of persistent fields", 4, cmd.getNoOfManagedMembers());

        // Check JoinMetaData at class-level
        JoinMetaData[] joinmds = cmd.getJoinMetaData();
        assertNotNull("JoinMetaData at class-level is null!", joinmds);
        assertEquals("Number of JoinMetaData at class-level is wrong!", 1, joinmds.length);
        assertEquals("Table of JoinMetaData at class-level is wrong", "JDO_AN_PRINTER_TONER", joinmds[0].getTable());
        ColumnMetaData[] joinColmds = joinmds[0].getColumnMetaData();
        assertEquals("Number of columns with MetaData in secondary table is incorrect", 1, joinColmds.length);
        assertEquals("Column of JoinMetaData at class-level is wrong", "PRINTER_REFID", joinColmds[0].getName());

        // "model" (stored in primary-table)
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("model");
        assertNotNull("Doesnt have required field", fmd);
        assertNull("Field 'model' has non-null table!", fmd.getTable());

        // "tonerModel" (stored in secondary-table)
        fmd = cmd.getMetaDataForMember("tonerModel");
        assertNotNull("Doesnt have required field", fmd);
        assertEquals("Field 'tonerModel' has non-null table!", "JDO_AN_PRINTER_TONER", fmd.getTable());
    }

    /**
     * Test of @NotPersistent on a field.
     */
    public void testNonPersistent()
    {
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("mood");

        assertEquals(prefix + "field should be non-persistent", FieldPersistenceModifier.NONE, fmd.getPersistenceModifier());
    }
}