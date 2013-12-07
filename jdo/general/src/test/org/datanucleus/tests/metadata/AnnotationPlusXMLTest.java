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
2007 Andr� F�genschuh - addition of many tests for XML supplements
    ...
**********************************************************************/
package org.datanucleus.tests.metadata;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.samples.ann_xml.models.company.Account;
import org.datanucleus.samples.ann_xml.models.company.Department;
import org.datanucleus.samples.ann_xml.models.company.Person;
import org.datanucleus.samples.ann_xml.models.company.Project;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for the use of JDO2 annotations+XML and that they generate the correct
 * internal metadata.<p/>
 * Test strategy is: <em>strict</em> separation of metadata by parse sequence:
 * <ol>
 *   <li>Annotations - for core persistence attributes.</li>
 *   <li>.jdo - for dbms related attributes.</li>
 *   <li>.orm - for vendor specific attributes.</li>
 * </ol>
 *
 * @see org.datanucleus.tests.metadata.AnnotationPlusXMLOverrideTest
 */
public class AnnotationPlusXMLTest extends JDOPersistenceTestCase
{
    private NucleusContext nucleusCtx;
    private MetaDataManager metaDataMgr;
    private ClassLoaderResolver clr;

    public AnnotationPlusXMLTest(String name)
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
     * Test of class based JDO annotations reading capability.
     */
    public void testPersistenceCapable()
    {
        // Department
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        assertTrue(prefix + "detachable is wrong", cmd.isDetachable());
        assertEquals(prefix + "identity-type is wrong",
                     IdentityType.APPLICATION, cmd.getIdentityType());
        assertFalse(prefix + "embedded-only is wrong", cmd.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd.getCatalog());
        assertNull(prefix + "schema is wrong", cmd.getSchema());
        assertEquals(prefix + "table is wrong", "DEPARTMENT", cmd.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields",
                     3, cmd.getNoOfManagedMembers());

        // Project
//        ...

        // Person
        cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        prefix = cmd.getFullClassName() + " : ";

        assertFalse(prefix + "detachable is wrong", cmd.isDetachable());
        assertEquals(prefix + "identity-type is wrong",
                     IdentityType.APPLICATION, cmd.getIdentityType());
        assertEquals(prefix + "objectid-class is wrong",
                     Person.Id.class.getName(), cmd.getObjectidClass());
        assertFalse(prefix + "embedded-only is wrong", cmd.isEmbeddedOnly());
        assertTrue(prefix + "requires-extent is wrong", cmd.isRequiresExtent());
        assertNull(prefix + "catalog is wrong", cmd.getCatalog());
        assertNull(prefix + "schema is wrong", cmd.getSchema());
        assertEquals(prefix + "table is wrong", "PERSON", cmd.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields",
                     7, cmd.getNoOfManagedMembers());
        assertEquals(prefix + "has incorrect number of fields",
                     10, cmd.getNoOfMembers());  // two non-persistent

        // Employee
//        ...
    }

    public void testInheritanceStrategy()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        InheritanceMetaData inhmd = cmd.getInheritanceMetaData();
        assertNotNull(prefix + " has no inheritance information", inhmd);
        assertEquals(prefix + " inheritance strategy is incorrect",
                     InheritanceStrategy.NEW_TABLE, inhmd.getStrategy());

        // Department
        cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        prefix = cmd.getFullClassName() + " : ";

        inhmd = cmd.getInheritanceMetaData();
        assertEquals(prefix + "inheritance strategy is incorrect",
                     InheritanceStrategy.NEW_TABLE, inhmd.getStrategy());

        // TODO: test a SUBCLASS_TABLE inheritance strategy annotation.
    }

    /**
     * Test of @PrimaryKey on a field (simple or complex).
     */
    public void testPrimaryKey()
    {
        // Department (simple PK)
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        assertEquals(prefix + "number of PK members is wrong",
                     1, cmd.getNoOfPrimaryKeyMembers());

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("name");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());

        // Person (complex PK)
        cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        prefix = cmd.getFullClassName() + " : ";

        assertEquals(prefix + "number of PK members is wrong",
                     2, cmd.getNoOfPrimaryKeyMembers());

        fmd = cmd.getMetaDataForMember("personNum");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());

        ColumnMetaData[] colmds = fmd.getColumnMetaData();
        assertEquals("Name of first col of pk is incorrect",
                     "PERSON_ID", colmds[0].getName());

        fmd = cmd.getMetaDataForMember("globalNum");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());

        colmds = fmd.getColumnMetaData();
        assertEquals("Name of second col of pk is incorrect",
                     "PERSON_GLOB_ID", colmds[0].getName());
    }

    /**
     * Test of @Persistent(primaryKey="true", valueStrategy=IDENTITY) on a field.
     */
    public void testValueStrategy()
    {
        // Account
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Account.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        assertEquals(prefix + "number of PK members is wrong",
                     1, cmd.getNoOfPrimaryKeyMembers());

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("id");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());

        assertEquals(prefix + "pk value strategy incorrect",
                     IdentityStrategy.IDENTITY, fmd.getValueStrategy());
    }

    /**
     * Test of @Persistent on a field.
     */
    public void testPersistent()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "bestFriend" - explicitly set
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("bestFriend");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());

        // "lastName" - not annotated, but default
        fmd = cmd.getMetaDataForMember("lastName");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
    }

    /**
     * Test of @NotPersistent on a field.
     */
    public void testNotPersistent()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("mood");
        assertEquals(prefix + "field should be non-persistent",
                     FieldPersistenceModifier.NONE, fmd.getPersistenceModifier());

        fmd = cmd.getMetaDataForMember("firstName");
        assertEquals(prefix + "field should be non-persistent",
                     FieldPersistenceModifier.NONE, fmd.getPersistenceModifier());
    }

    /**
     * Test of DFG on a field (implicit or explicit).
     */
    public void testDefaultFetchGroup()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "bestFriend" - not set, not default
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("bestFriend");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertFalse(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());

        // "dateOfBirth" - not set, but default
        fmd = cmd.getMetaDataForMember("dateOfBirth");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());

        // "lastName" - not annotated, but default
        fmd = cmd.getMetaDataForMember("lastName");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());

        // Department
        cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        prefix = cmd.getFullClassName() + " : ";

        // "manager" - explicitly set
        fmd = cmd.getMetaDataForMember("manager");
        assertEquals(prefix + "field should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
    }

    /**
     * Check explicit column information in "package.jdo" and/or "package.orm".
     */
    public void testColumn()
    {
        // Person
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        // "lastName"
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("lastName");
        ColumnMetaData[] colmds = fmd.getColumnMetaData();
        assertEquals(prefix + "column identifier incorrect",
                     "LASTNAME", colmds[0].getName());
        assertEquals(prefix + "column length incorrect",
                     new Integer(64), colmds[0].getLength());
        assertEquals(prefix + "column JDBC type incorrect",
                     "VARCHAR", colmds[0].getJdbcType());

        // "firstName"
        fmd = cmd.getMetaDataForMember("firstName");
        colmds = fmd.getColumnMetaData();
        assertEquals(prefix + "column identifier incorrect",
                     "FIRSTNAME", colmds[0].getName());
        assertEquals(prefix + "column length incorrect",
                     new Integer(32), colmds[0].getLength());
        assertEquals(prefix + "column JDBC type incorrect",
                     "VARCHAR", colmds[0].getJdbcType());

        // "emailAddress"
        fmd = cmd.getMetaDataForMember("emailAddress");
        colmds = fmd.getColumnMetaData();
        assertEquals(prefix + "column identifier incorrect",
                     "EMAILADDRESS", colmds[0].getName());
        assertEquals(prefix + "column length incorrect",
                     new Integer(128), colmds[0].getLength());
        assertEquals(prefix + "column JDBC type incorrect",
                     "VARCHAR", colmds[0].getJdbcType());

        // "dateOfBirth"
        fmd = cmd.getMetaDataForMember("dateOfBirth");
        colmds = fmd.getColumnMetaData();
        assertEquals("column identifier incorrect",
                     "DATEOFBIRTH", colmds[0].getName());
        assertEquals("column JDBC type incorrect",
                     "TIMESTAMP", colmds[0].getJdbcType());

        // package.orm (additional)
        ExtensionMetaData[] extmds = fmd.getExtensions();
        assertNotNull(prefix + "extension info is null!", extmds);
        assertEquals(prefix + "incorrect number of extensions", 1, extmds.length);
        assertEquals(prefix + "extension vendor incorrect",
                     "jpox", extmds[0].getVendorName());
        assertEquals(prefix + "extension key incorrect",
                     "insert-function", extmds[0].getKey());
        assertEquals(prefix + "extension value incorrect",
                     "CURRENT_TIMESTAMP", extmds[0].getValue());
    }

    public void testCollectionSet()
    {
        // Department
        ClassMetaData cmd = (ClassMetaData) metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd.getFullClassName() + " : ";

        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("projects");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "should be persistent",
                     FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
        assertFalse(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertNotNull(prefix + "has no container specified!", fmd.getCollection());
        assertEquals(prefix + "should have collection of Project elements but hasnt",
                     Project.class.getName(), fmd.getCollection().getElementType());
        assertFalse(prefix + "shouldnt have collection of serialised elements but has",
                    fmd.getCollection().isSerializedElement());
        assertFalse(prefix + "shouldnt have collection of dependent elements but has",
                    fmd.getCollection().isDependentElement());
    }

    // TODO
//    public void testCollectionMap()
//    {
//      // Person
//    }
}