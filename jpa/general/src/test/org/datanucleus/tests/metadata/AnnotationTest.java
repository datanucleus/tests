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
    ...
**********************************************************************/
package org.datanucleus.tests.metadata;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jpa.metadata.JPAMetaDataManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.EventListenerMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.OrderMetaData;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.metadata.QueryLanguage;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.QueryResultMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.metadata.TableGeneratorMetaData;
import org.datanucleus.metadata.OrderMetaData.FieldOrder;
import org.datanucleus.metadata.QueryResultMetaData.PersistentTypeMapping;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.abstractclasses.AbstractSimpleBase;
import org.jpox.samples.annotations.abstractclasses.ConcreteSimpleSub1;
import org.jpox.samples.annotations.abstractclasses.ConcreteSimpleSub2;
import org.jpox.samples.annotations.array.ByteArray;
import org.jpox.samples.annotations.idclass.IdClassAccessors;
import org.jpox.samples.annotations.models.company.MyListener;
import org.jpox.samples.annotations.models.company.WebSite;
import org.jpox.samples.annotations.many_many.PetroleumCustomer;
import org.jpox.samples.annotations.many_many.PetroleumSupplier;
import org.jpox.samples.annotations.models.company.Account;
import org.jpox.samples.annotations.models.company.DepartmentPK;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Department;
import org.jpox.samples.annotations.models.company.Manager;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.models.company.Project;
import org.jpox.samples.annotations.one_many.unidir_2.UserGroup;
import org.jpox.samples.annotations.one_one.bidir.Boiler;
import org.jpox.samples.annotations.one_one.bidir.Timer;
import org.jpox.samples.annotations.one_one.unidir.Login;
import org.jpox.samples.annotations.one_one.unidir.LoginAccount;
import org.jpox.samples.annotations.secondarytable.Printer;
import org.jpox.samples.annotations.types.basic.TypeHolder;
import org.jpox.samples.annotations.types.enums.EnumHolder;

/**
 * Tests for the use of JPA annotations and the generation of internal JPOX metadata.
 */
public class AnnotationTest extends JPAPersistenceTestCase
{
    public AnnotationTest(String name)
    {
        super(name);
    }

    /**
     * Test of basic JPA annotations reading capability
     */
    public void testBasic()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        // Checks for Department
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        String prefix = cmd1.getFullClassName() + " : ";
        assertEquals(prefix + "detachable is wrong", cmd1.isDetachable(), true);
        assertEquals(prefix + "identity-type is wrong", cmd1.getIdentityType(), IdentityType.APPLICATION);
        assertEquals(prefix + "embedded-only is wrong", cmd1.isEmbeddedOnly(), false);
        assertEquals(prefix + "requires-extent is wrong", cmd1.isRequiresExtent(), true);
        assertEquals(prefix + "catalog is wrong", cmd1.getCatalog(), null);
        assertEquals(prefix + "schema is wrong", cmd1.getSchema(), null);
        assertEquals(prefix + "table is wrong", cmd1.getTable(), "JPA_AN_DEPARTMENT");
        assertEquals(prefix + "has incorrect number of persistent fields", cmd1.getNoOfManagedMembers(), 4);

        InheritanceMetaData inhmd1 = cmd1.getInheritanceMetaData();
        assertEquals("Inheritance strategy is incorrect", InheritanceStrategy.NEW_TABLE, inhmd1.getStrategy());

        // "projects"
        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("projects");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "should be persistent", fmd.getPersistenceModifier(), FieldPersistenceModifier.PERSISTENT);
        assertFalse(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertFalse(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertTrue(prefix + "has no container specified!", fmd.getCollection() != null);
        assertEquals(prefix + "should have collection of Project elements but hasnt",
            fmd.getCollection().getElementType(), Project.class.getName());
        assertEquals(prefix + "shouldnt have collection of serialised elements but has",
            fmd.getCollection().isSerializedElement(), false);
        assertEquals(prefix + "shouldnt have collection of dependent elements but has",
            fmd.getCollection().isDependentElement(), false);

        // Checks for Project
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Project.class.getName(), clr);
        prefix = cmd2.getFullClassName() + " : ";
        assertEquals(prefix + "detachable is wrong", true, cmd2.isDetachable());
        assertEquals(prefix + "identity-type is wrong", cmd2.getIdentityType(), IdentityType.APPLICATION);
        assertEquals(prefix + "objectid-class is wrong", "javax.jdo.identity.StringIdentity", cmd2.getObjectidClass());
        assertEquals(prefix + "embedded-only is wrong", cmd2.isEmbeddedOnly(), false);
        assertEquals(prefix + "requires-extent is wrong", cmd2.isRequiresExtent(), true);
        assertEquals(prefix + "catalog is wrong", cmd2.getCatalog(), null);
        assertEquals(prefix + "schema is wrong", cmd2.getSchema(), null);
        assertEquals(prefix + "table is wrong", "JPA_AN_PROJECT", cmd2.getTable());
        assertEquals(prefix + "has incorrect number of persistent fields", cmd2.getNoOfManagedMembers(), 2);

        InheritanceMetaData inhmd2 = cmd2.getInheritanceMetaData();
        assertEquals("Inheritance strategy is incorrect", InheritanceStrategy.NEW_TABLE, inhmd2.getStrategy());

        // "name"
        fmd = cmd2.getMetaDataForMember("name");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertTrue(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", fmd.getPersistenceModifier(), FieldPersistenceModifier.PERSISTENT);

        // "budget"
        fmd = cmd2.getMetaDataForMember("budget");
        assertNotNull(prefix + "doesnt have required field", fmd);
        assertEquals(prefix + "has incorrect persistent field", fmd.getName(), "budget");
        assertFalse(prefix + "pk is wrong", fmd.isPrimaryKey());
        assertTrue(prefix + "dfg is wrong", fmd.isDefaultFetchGroup());
        assertEquals(prefix + "should be persistent", fmd.getPersistenceModifier(), FieldPersistenceModifier.PERSISTENT);
    }

    /**
     * Test of JPA 1-1 unidir relation
     */
    public void testOneToOneUni()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(LoginAccount.class.getName(), clr);
        assertEquals("LoginAccount has incorrect table name", cmd1.getTable(), "JPA_AN_LOGINACCOUNT");
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("login");
        assertNotNull("LoginAccount.login is null!", fmd1);
        assertEquals("LoginAccount.login mapped-by is incorrect", fmd1.getMappedBy(), null);
        assertEquals("LoginAccount.login relationType is incorrect", 
            fmd1.getRelationType(clr), RelationType.ONE_TO_ONE_UNI);

        assertNotNull("LoginAccount.login has no column info", fmd1.getColumnMetaData());
        assertEquals("LoginAccount.login has incorrect number of columns", fmd1.getColumnMetaData().length, 1);
        assertEquals("LoginAccount.login column name is wrong", fmd1.getColumnMetaData()[0].getName(), "LOGIN_ID");

        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Login.class.getName(), clr);
        assertEquals("LoginAccount has incorrect table name", cmd2.getTable(), "JPA_AN_LOGIN");
    }

    /**
     * Test of JPA 1-1 bidir relation
     */
    public void testOneToOneBi()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // non-owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Boiler.class.getName(), clr);
        assertEquals("Boiler has incorrect table name", "JPA_AN_BOILER", cmd1.getTable());
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("timer");
        assertNotNull("Boiler.timer is null!", fmd1);
        assertEquals("Boiler.timer mapped-by is incorrect", "boiler", fmd1.getMappedBy());
        assertEquals("Boiler.timer relationType is incorrect", 
            RelationType.ONE_TO_ONE_BI, fmd1.getRelationType(clr));

        // owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Timer.class.getName(), clr);
        assertEquals("Timer has incorrect table name", "JPA_AN_TIMER", cmd2.getTable());
        AbstractMemberMetaData fmd2 = cmd2.getMetaDataForMember("boiler");
        assertNotNull("Timer.boiler is null!", fmd2);
        assertEquals("Timer.boiler mapped-by is incorrect", null, fmd2.getMappedBy());
        assertEquals("Timer.boiler relationType is incorrect", RelationType.ONE_TO_ONE_BI, fmd2.getRelationType(clr));

        assertNotNull("Timer.boiler has no column info", fmd2.getColumnMetaData());
        assertEquals("Timer.boiler has incorrect number of columns", 1, fmd2.getColumnMetaData().length);
        assertEquals("Timer.boiler column name is wrong", "BOILER_ID", fmd2.getColumnMetaData()[0].getName());
    }

    /**
     * Test of JPA 1-N bidir FK relation
     */
    public void testOneToManyBiFK()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("departments");
        assertNotNull("Manager.departments is null!", fmd1);
        assertEquals("Manager.departments mapped-by is incorrect", fmd1.getMappedBy(), "manager");
        assertEquals("Manager.departments relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.ONE_TO_MANY_BI);

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNull("Manager.departments has join column info but shouldnt (specified on N side)", elemmd);

        // non-owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        AbstractMemberMetaData fmd2 = cmd2.getMetaDataForMember("manager");
        assertNotNull("Department.manager is null!", fmd2);
        assertEquals("Department.manager mapped-by is incorrect", fmd2.getMappedBy(), null);
        assertEquals("Department.manager relationType is incorrect",
            fmd2.getRelationType(clr), RelationType.MANY_TO_ONE_BI);

        ColumnMetaData[] colmds = fmd2.getColumnMetaData();
        assertNotNull("Department.manager has no join column info", colmds);
        assertEquals("Department.manager has incorrect number of joincolumns", colmds.length, 1);
        assertEquals("Department.manager joincolumn name is wrong", "MGR_ID", colmds[0].getName());
    }

    /**
     * Test of JPA 1-N unidir JoinTable relation
     */
    public void testOneToManyUniJoin()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("projects");
        assertNotNull("Department.projects is null!", fmd1);
        assertEquals("Department.projects mapped-by is incorrect", null, fmd1.getMappedBy());
        assertEquals("Department.projects relationType is incorrect", RelationType.ONE_TO_MANY_UNI,
            fmd1.getRelationType(clr));
        assertEquals("Department.projects jointable name is incorrect", "JPA_AN_DEPT_PROJECTS", fmd1.getTable());

        JoinMetaData joinmd = fmd1.getJoinMetaData();
        assertNotNull("Department.projects has no join table!", joinmd);
        assertNotNull("Department.projects has incorrect join columns", joinmd.getColumnMetaData());
        assertEquals("Department.projects has incorrect number of join columns",
            2, joinmd.getColumnMetaData().length);
        assertEquals("Department.projects has incorrect join column name",
            joinmd.getColumnMetaData()[0].getName(), "DEPT_ID");
        assertEquals("Department.projects has incorrect join column name",
            joinmd.getColumnMetaData()[1].getName(), "DEPT_ID_STRING");

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNotNull("Department.projects has no element column info but should", elemmd);
        ColumnMetaData[] colmds = elemmd.getColumnMetaData();
        assertNotNull("Department.projects has incorrect element columns", colmds);
        assertEquals("Department.projects has incorrect number of element columns", 1, colmds.length);
        assertEquals("Department.projects has incorrect element column name", "PROJECT_ID", colmds[0].getName());
    }

    /**
     * Test of JPA 1-N unidir FK relation.
     * Really is 1-N uni join since JPA doesnt support 1-N uni FK
     */
    /*public void testOneToManyUniFK()
    {
        NucleusContext nucleusCtx = new NucleusContext(new PersistenceConfiguration(){});
        nucleusCtx.setApi("JPA");
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Site.class.getName(), clr);
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("offices");
        assertNotNull("Site.offices is null!", fmd1);
        assertEquals("Site.offices mapped-by is incorrect", fmd1.getMappedBy(), null);
        assertEquals("Site.offices relationType is incorrect",
            fmd1.getRelationType(clr), Relation.ONE_TO_MANY_UNI);
        assertEquals("Site.offices jointable name is incorrect", fmd1.getTable(), null);
        assertNotNull("Site.offices should have join but doesnt", fmd1.getJoinMetaData());

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNotNull("Site.offices has no element column info but should", elemmd);
        ColumnMetaData[] colmds = elemmd.getColumnMetaData();
        assertNotNull("Site.offices has incorrect element columns", colmds);
        assertEquals("Site.offices has incorrect number of element columns", colmds.length, 1);
        assertEquals("Site.offices has incorrect element column name", colmds[0].getName(), "SITE_ID");
    }*/

    /**
     * Test of JPA 1-N bidir join relation
     */
    public void testOneToManyBiJoin()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Manager.class.getName(), clr);
        assertEquals("Manager has incorrect table name", cmd1.getTable(), "JPA_AN_MANAGER");
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("subordinates");
        assertNotNull("Manager.subordinates is null!", fmd1);
        assertEquals("Manager.subordinates mapped-by is incorrect", fmd1.getMappedBy(), "manager");
        assertEquals("Manager.subordinates relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.ONE_TO_MANY_BI);
        assertEquals("Manager.subordinates jointable name is incorrect", fmd1.getTable(), "JPA_AN_MGR_EMPLOYEES");

        // non-owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Employee.class.getName(), clr);
        assertEquals("Employee has incorrect table name", cmd2.getTable(), "JPA_AN_EMPLOYEE");
        AbstractMemberMetaData fmd2 = cmd2.getMetaDataForMember("manager");
        assertNotNull("Employee.manager is null!", fmd2);
        assertEquals("Employee.manager mapped-by is incorrect", fmd2.getMappedBy(), null);
        assertEquals("Employee.manager relationType is incorrect",
            fmd2.getRelationType(clr), RelationType.MANY_TO_ONE_BI);
        assertEquals("Employee.manager jointable name is incorrect", fmd2.getTable(), null);

        // join-table
        JoinMetaData joinmd = fmd1.getJoinMetaData();
        assertNotNull("Manager.subordinates has no join table!", joinmd);
        assertNotNull("Manager.subordinates has incorrect join columns", joinmd.getColumnMetaData());
        assertEquals("Manager.subordinates has incorrect number of join columns", 1, joinmd.getColumnMetaData().length);
        assertEquals("Manager.subordinates has incorrect owner join column name",
            "MGR_ID", joinmd.getColumnMetaData()[0].getName());

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNotNull("Manager.subordinates has no element column info but should", elemmd);
        assertNotNull("Manager.subordinates has incorrect element columns", elemmd.getColumnMetaData());
        assertEquals("Manager.subordinates has incorrect number of element columns", 1, elemmd.getColumnMetaData().length);
        assertEquals("Manager.subordinates has incorrect element join column name",
            "EMP_ID", elemmd.getColumnMetaData()[0].getName());
    }

    /**
     * Test of JPA M-N relation
     */
    public void testManyToMany()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(PetroleumCustomer.class.getName(), clr);
        assertEquals("Customer has incorrect table name", cmd1.getTable(), "JPA_AN_PETROL_CUSTOMER");
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("suppliers");
        assertNotNull("Customer.suppliers is null!", fmd1);
        assertEquals("Customer.suppliers mapped-by is incorrect", fmd1.getMappedBy(), "customers");
        assertEquals("Customer.suppliers relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.MANY_TO_MANY_BI);
        assertEquals("Customer.suppliers jointable name is incorrect", fmd1.getTable(), "JPA_AN_PETROL_CUST_SUPP");

        // non-owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(PetroleumSupplier.class.getName(), clr);
        assertEquals("Supplier has incorrect table name", cmd2.getTable(), "JPA_AN_PETROL_SUPPLIER");
        AbstractMemberMetaData fmd2 = cmd2.getMetaDataForMember("customers");
        assertNotNull("Supplier.customers is null!", fmd2);
        assertEquals("Supplier.customers mapped-by is incorrect", fmd2.getMappedBy(), null);
        assertEquals("Supplier.customers relationType is incorrect",
            fmd2.getRelationType(clr), RelationType.MANY_TO_MANY_BI);
        assertEquals("Supplier.customers jointable name is incorrect", fmd2.getTable(), null);

        // join table info
        JoinMetaData joinmd = fmd1.getJoinMetaData();
        assertNotNull("Customer.suppliers has no join table!", joinmd);
        assertNotNull("Customer.suppliers has incorrect join columns", joinmd.getColumnMetaData());
        assertEquals("Customer.suppliers has incorrect number of join columns", joinmd.getColumnMetaData().length, 1);
        assertEquals("Customer.suppliers has incorrect owner join column name", 
            joinmd.getColumnMetaData()[0].getName(), "CUSTOMER_ID");

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNotNull("Customer.suppliers has no element column info but should", elemmd);
        assertNotNull("Customer.suppliers has incorrect element columns", elemmd.getColumnMetaData());
        assertEquals("Customer.suppliers has incorrect number of element columns", elemmd.getColumnMetaData().length, 1);
        assertEquals("Customer.suppliers has incorrect element join column name", 
            elemmd.getColumnMetaData()[0].getName(), "SUPPLIER_ID");
    }

    /**
     * Test of JPA 1-N unidir Map relation
     */
    public void testOneToManyUniMapFK()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // owner side
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("phoneNumbers");
        assertNotNull("Department.phoneNumbers is null!", fmd1);
        assertEquals("Department.phoneNumbers mapped-by is incorrect", fmd1.getMappedBy(), null);
        assertEquals("Department.phoneNumbers relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.ONE_TO_MANY_UNI);
        assertEquals("Department.phoneNumbers jointable name is incorrect", fmd1.getTable(), null);

        MapMetaData mmd = fmd1.getMap();
        assertNotNull("Department.phoneNumbers has no Map metadata!", mmd);
        KeyMetaData keymd = fmd1.getKeyMetaData();
        assertNotNull("Department.phoneNumbers has no Key metadata!", keymd);
        assertEquals("Department.phoneNumbers has incorrect key mapped-by", keymd.getMappedBy(), "name");
    }

    /**
     * Test of basic JPA @GeneratedValue.
     */
    public void testGeneratedValue()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Account.class.getName(), clr);

        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("id");
        assertNotNull("Account has no id field!", fmd1);
        assertEquals("Account has incorrect value strategy", fmd1.getValueStrategy(), IdentityStrategy.INCREMENT);
    }

    /**
     * Test of basic JPA @TableGenerator
     */
    public void testTableGenerator()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Employee.class.getName(), clr);

        PackageMetaData pmd = cmd1.getPackageMetaData();
        assertEquals("Number of TableGenerators registered for Employee class is wrong", pmd.getNoOfTableGenerators(), 1);
        TableGeneratorMetaData tgmd = pmd.getTableGenerators()[0];
        assertEquals("TableGenerator has incorrect name", tgmd.getName(), "EmployeeGenerator");
        assertEquals("TableGenerator has incorrect table", tgmd.getTableName(), "ID_TABLE");
        assertEquals("TableGenerator has incorrect pk column name", tgmd.getPKColumnName(), "TYPE");
        assertEquals("TableGenerator has incorrect value column name", tgmd.getValueColumnName(), "LATEST_VALUE");
        assertEquals("TableGenerator has incorrect pk column value", tgmd.getPKColumnValue(), "EMPLOYEE");
        assertEquals("TableGenerator has incorrect initial value", tgmd.getInitialValue(), 0);
        assertEquals("TableGenerator has incorrect allocation size", tgmd.getAllocationSize(), 50);
    }

    /**
     * Test of basic JPA @SequenceGenerator
     */
    public void testSequenceGenerator()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);

        PackageMetaData pmd = cmd1.getPackageMetaData();
        assertEquals("Number of Sequences registered for Department class is wrong", pmd.getNoOfSequences(), 1);
        SequenceMetaData seqmd = pmd.getSequences()[0];
        assertEquals("SequenceGenerator has incorrect name", seqmd.getName(), "DepartmentGenerator");
        assertEquals("SequenceGenerator has incorrect sequence name", seqmd.getDatastoreSequence(), "DEPT_SEQ");
        assertEquals("SequenceGenerator has incorrect initial value", seqmd.getInitialValue(), 1);
        assertEquals("SequenceGenerator has incorrect allocation size", seqmd.getAllocationSize(), 50);
    }

    /**
     * Test of basic JPA @EmbeddedId.
     */
    public void testEmbeddedId()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Department.class.getName(), clr);
        assertEquals(1, cmd1.getNoOfPrimaryKeyMembers());
    }

    /**
     * Test of JPA @Embeddable.
     */
    public void testEmbeddable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(DepartmentPK.class.getName(), clr);
        assertNotNull(cmd1);
    }

    /**
     * Test of JPA Byte[] is embedded by default
     */
    public void testByteArrayEmbeddedByDefault()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(ByteArray.class.getName(), clr);
        assertTrue(cmd1.getMetaDataForMember("array1").isEmbedded());
    }

    /**
     * Test of JPA column length
     */
    public void testColumnLength()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Printer.class.getName(), clr);

        AbstractMemberMetaData fmd = cmd1.getMetaDataForMember("make");
        assertEquals(fmd.getColumnMetaData().length, 1);
        assertEquals(fmd.getColumnMetaData()[0].getName(), "MAKE");
        assertEquals(40, fmd.getColumnMetaData()[0].getLength().intValue());
    }

    /**
     * Test of EventListeners
     */
    public void testEventListeners()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(WebSite.class.getName(), clr);

        // Example callbacks
        EventListenerMetaData elmd = cmd1.getListenerForClass(cmd1.getFullClassName());
        assertNotNull("Site didnt have its own class registered as an EventListener!", elmd);
        assertEquals("Site EventListener has incorrect method for prePersist callback",
            elmd.getClassName() + ".prePersist", elmd.getMethodNameForCallbackClass(PrePersist.class.getName()));
        assertEquals("Site EventListener has incorrect method for postPersist callback",
            elmd.getClassName() + ".postPersist", elmd.getMethodNameForCallbackClass(PostPersist.class.getName()));
        assertEquals("Site EventListener has incorrect method for postPersist callback",
            elmd.getClassName() + ".load", elmd.getMethodNameForCallbackClass(PostLoad.class.getName()));
        assertNull(elmd.getMethodNameForCallbackClass(PreRemove.class.getName()));

        // Example listener
        elmd = cmd1.getListenerForClass(MyListener.class.getName());
        assertNotNull("Site didnt have MyListener registered as an EventListener!", elmd);
        assertEquals("Site EventListener has incorrect method for prePersist callback",
            elmd.getClassName() + ".register", elmd.getMethodNameForCallbackClass(PostPersist.class.getName()));
        assertEquals("Site EventListener has incorrect method for postPersist callback",
            elmd.getClassName() + ".deregister", elmd.getMethodNameForCallbackClass(PreRemove.class.getName()));
        assertNull(elmd.getMethodNameForCallbackClass(PrePersist.class.getName()));
    }

    /**
     * Test of MappedSuperclass
     */
    public void testMappedSuperclass()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // AbstractSimpleBase
        ClassMetaData cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(AbstractSimpleBase.class.getName(), clr);
        assertNotNull("No MetaData found for AbstractSimpleBase yet is MappedSuperclass", cmd);
        assertNotNull("No Inheritance info found for AbstractSimpleBase", cmd.getInheritanceMetaData());
        assertEquals("Inheritance for AbstractSimpleBase is incorrect", "subclass-table", 
            cmd.getInheritanceMetaData().getStrategy().toString());
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("id");
        assertNotNull("No field info found for AbstractSimpleBase.id", fmd);
        assertNotNull("No column info found for AbstractSimpleBase.id", fmd.getColumnMetaData());
        assertEquals("Column name for AbstractSimpleBase.id is wrong", "ID", fmd.getColumnMetaData()[0].getName());
        fmd = cmd.getMetaDataForMember("baseField");
        assertNotNull("No field info found for AbstractSimpleBase.baseField", fmd);
        assertNotNull("No column info found for AbstractSimpleBase.baseField", fmd.getColumnMetaData());
        assertEquals("Column name for Product.baseField is wrong", "BASE_FIELD", fmd.getColumnMetaData()[0].getName());

        // ConcreteSimpleSub1
        cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(ConcreteSimpleSub1.class.getName(), clr);
        assertNotNull("No MetaData found for ConcreteSimpleSub1 yet is Entity", cmd);
        assertNotNull("No Inheritance info found for ConcreteSimpleSub1", cmd.getInheritanceMetaData());
        assertEquals("Inheritance for ConcreteSimpleSub1 is incorrect", "new-table",
            cmd.getInheritanceMetaData().getStrategy().toString());
        fmd = cmd.getOverriddenMember("baseField");
        assertNotNull("No overridden field info found for ConcreteSimpleSub1.baseField", fmd);
        assertNotNull("No column info found for ConcreteSimpleSub1.baseField", fmd.getColumnMetaData());
        assertEquals("Column name for ConcreteSimpleSub1.baseField is wrong", 
            "BASE_FIELD_OR", fmd.getColumnMetaData()[0].getName());
        fmd = cmd.getMetaDataForMember("sub1Field");
        assertNotNull("No field info found for ConcreteSimpleSub1.sub1Field", fmd);
        assertNotNull("No column info found for ConcreteSimpleSub1.sub1Field", fmd.getColumnMetaData());
        assertEquals("Column name for ConcreteSimpleSub1.sub1Field is wrong", 
            "SUB1_FIELD", fmd.getColumnMetaData()[0].getName());

        // ConcreteSimpleSub2
        cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(ConcreteSimpleSub2.class.getName(), clr);
        assertNotNull("No MetaData found for ConcreteSimpleSub2 yet is Entity", cmd);
        assertNotNull("No Inheritance info found for ConcreteSimpleSub2", cmd.getInheritanceMetaData());
        assertEquals("Inheritance for ConcreteSimpleSub2 is incorrect", "new-table",
            cmd.getInheritanceMetaData().getStrategy().toString());
        fmd = cmd.getOverriddenMember("baseField");
        assertNull("Overridden field info found for ConcreteSimpleSub2.baseField!", fmd);
        fmd = cmd.getMetaDataForMember("sub2Field");
        assertNotNull("No overridden field info found for ConcreteSimpleSub2.sub2Field", fmd);
        assertNotNull("No column info found for ConcreteSimpleSub2.sub2Field", fmd.getColumnMetaData());
        assertEquals("Column name for ConcreteSimpleSub2.sub2Field is wrong", 
            "SUB2_FIELD", fmd.getColumnMetaData()[0].getName());
    }

    /**
     * Test of JPA @NamedQuery, @NamedNativeQuery.
     */
    public void testNamedQuery()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(LoginAccount.class.getName(), clr);
        QueryMetaData[] qmds = cmd.getQueries();
        assertNotNull("LoginAccount has no queries!", qmds);
        assertEquals("LoginAccount has incorrect number of queries", 2, qmds.length);

        QueryMetaData jpqlQuery = null;
        QueryMetaData sqlQuery = null;
        if (qmds[0].getLanguage().equals(QueryLanguage.JPQL.toString()))
        {
            jpqlQuery = qmds[0];
        }
        else if (qmds[1].getLanguage().equals(QueryLanguage.JPQL.toString()))
        {
            jpqlQuery = qmds[1];
        }
        if (qmds[0].getLanguage().equals(QueryLanguage.SQL.toString()))
        {
            sqlQuery = qmds[0];
        }
        else if (qmds[1].getLanguage().equals(QueryLanguage.SQL.toString()))
        {
            sqlQuery = qmds[1];
        }
        if (jpqlQuery == null)
        {
            fail("No JPQL Query was registered for LoginAccount");
        }
        if (sqlQuery == null)
        {
            fail("No SQL Query was registered for LoginAccount");
        }
        assertEquals("LoginAccount JPQL has incorrect query name", "LoginForJohnSmith", jpqlQuery.getName());
        assertEquals("LoginAccount JPQL has incorrect query", 
            "SELECT a FROM LoginAccount a WHERE a.firstName='John' AND a.lastName='Smith'", jpqlQuery.getQuery());
        assertEquals("LoginAccount SQL has incorrect query name", "LoginForJohn", sqlQuery.getName());
        assertEquals("LoginAccount SQL has incorrect query",
            "SELECT * FROM JPA_AN_LOGIN WHERE FIRSTNAME = 'John'", sqlQuery.getQuery());
    }

    /**
     * Test of JPA @SqlResultSetMapping
     */
    public void testSqlResultSetMapping()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(LoginAccount.class.getName(), clr);
        QueryResultMetaData[] queryResultMappings = cmd.getQueryResultMetaData();
        assertNotNull("LoginAccount has no QueryResultMetaData!", queryResultMappings);
        assertEquals("LoginAccount has incorrect number of query result mappings", 4, queryResultMappings.length);

        // Example 1 : Returning 2 entities
        QueryResultMetaData qrmd = null;
        for (int i=0;i<queryResultMappings.length;i++)
        {
            QueryResultMetaData md = queryResultMappings[i];
            if (md.getName().equals("AN_LOGIN_PLUS_ACCOUNT"))
            {
                qrmd = md;
                break;
            }
        }
        if (qrmd == null)
        {
            fail("SQL ResultSet mapping AN_LOGIN_PLUS_ACCOUNT is not present!");
        }
        String[] scalarCols = qrmd.getScalarColumns();
        assertNull("LoginAccount sql mapping has incorrect scalar cols", scalarCols);
        PersistentTypeMapping[] sqlMappingEntities = qrmd.getPersistentTypeMappings();
        assertNotNull("LoginAccount sql mapping has incorrect entities", sqlMappingEntities);
        assertEquals("LoginAccount sql mapping has incorrect number of entities", 2, sqlMappingEntities.length);

        // LoginAccount
        assertEquals("LoginAccount sql mapping entity 0 has incorrect class",
            LoginAccount.class.getName(), sqlMappingEntities[0].getClassName());
        assertNull("LoginAccount sql mapping entity 0 has incorrect discriminator",
            sqlMappingEntities[0].getDiscriminatorColumn());

        // Login
        assertEquals("LoginAccount sql mapping entity 1 has incorrect class",
            Login.class.getName(), sqlMappingEntities[1].getClassName());
        assertNull("LoginAccount sql mapping entity 1 has incorrect discriminator",
            sqlMappingEntities[1].getDiscriminatorColumn());

        // Example 2 : Returning 2 scalars
        qrmd = null;
        for (int i=0;i<queryResultMappings.length;i++)
        {
            QueryResultMetaData md = queryResultMappings[i];
            if (md.getName().equals("AN_ACCOUNT_NAMES"))
            {
                qrmd = md;
                break;
            }
        }
        if (qrmd == null)
        {
            fail("SQL ResultSet mapping AN_ACCOUNT_NAMES is not present!");
        }
        scalarCols = qrmd.getScalarColumns();
        assertNotNull("LoginAccount sql mapping has incorrect scalar cols", scalarCols);
        assertEquals("LoginAccount sql mapping has incorrect column name", "FIRSTNAME", scalarCols[0]);
        assertEquals("LoginAccount sql mapping has incorrect column name", "LASTNAME", scalarCols[1]);
        sqlMappingEntities = qrmd.getPersistentTypeMappings();
        assertNull("LoginAccount sql mapping has incorrect entities", sqlMappingEntities);
    }

    /**
     * Test for use of annotations for secondary tables, in particular @SecondaryTable.
     * Uses Printer class, storing some fields in table "PRINTER" and some in "PRINTER_TONER".
     */
    public void testSecondaryTable()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        ClassMetaData cmd = (ClassMetaData)metaDataMgr.getMetaDataForClass(Printer.class.getName(), clr);

        assertEquals("detachable is wrong", cmd.isDetachable(), true);
        assertEquals("identity-type is wrong", cmd.getIdentityType(), IdentityType.APPLICATION);
        assertEquals("embedded-only is wrong", cmd.isEmbeddedOnly(), false);
        assertEquals("requires-extent is wrong", cmd.isRequiresExtent(), true);
        assertNull("catalog is wrong", cmd.getCatalog());
        assertNull("schema is wrong", cmd.getSchema());
        assertEquals("table is wrong", cmd.getTable(), "JPA_AN_PRINTER");
        assertEquals("has incorrect number of persistent fields", cmd.getNoOfManagedMembers(), 5);

        // Check JoinMetaData at class-level
        JoinMetaData[] joinmds = cmd.getJoinMetaData();
        assertNotNull("JoinMetaData at class-level is null!", joinmds);
        assertEquals("Number of JoinMetaData at class-level is wrong!", joinmds.length, 1);
        assertEquals("Table of JoinMetaData at class-level is wrong", "JPA_AN_PRINTER_TONER", joinmds[0].getTable());
        ColumnMetaData[] joinColmds = joinmds[0].getColumnMetaData();
        assertEquals("Number of columns with MetaData in secondary table is incorrect", 1, joinColmds.length);
        assertEquals("Column of JoinMetaData at class-level is wrong", joinColmds[0].getName(), "PRINTER_ID");

        // "model" (stored in primary-table)
        AbstractMemberMetaData fmd = cmd.getMetaDataForMember("model");
        assertNotNull("Doesnt have required field", fmd);
        assertNull("Field 'model' has non-null table!", fmd.getTable());

        // "tonerModel" (stored in secondary-table)
        fmd = cmd.getMetaDataForMember("tonerModel");
        assertNotNull("Doesnt have required field", fmd);
        assertEquals("Field 'tonerModel' has non-null table!", fmd.getTable(), "JPA_AN_PRINTER_TONER");
    }

    /**
     * Test of JPA enumerated JDBC type.
     */
    public void testEnumeratedJDBCType()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(EnumHolder.class.getName(), clr);
        AbstractMemberMetaData mmd1 = cmd1.getMetaDataForMember("colour1");
        assertEquals("INTEGER", mmd1.getColumnMetaData()[0].getJdbcType());
        assertEquals(FieldPersistenceModifier.PERSISTENT, mmd1.getPersistenceModifier());
        AbstractMemberMetaData mmd2 = cmd1.getMetaDataForMember("colour2");
        assertEquals("VARCHAR", mmd2.getColumnMetaData()[0].getJdbcType());
        assertEquals(FieldPersistenceModifier.PERSISTENT, mmd2.getPersistenceModifier());
    }

    /**
     * Test of string length default to JPA default 255.
     */
    public void testStringLength()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Account.class.getName(), clr);
        AbstractMemberMetaData mmd1 = cmd1.getMetaDataForMember("username");
        assertEquals(255, mmd1.getColumnMetaData()[0].getLength().intValue());
    }

    /**
     * Test of char length default to 1 with JPA.
     */
    public void testCharDefaultTo1Length()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(TypeHolder.class.getName(), clr);
        assertEquals(1, cmd1.getMetaDataForMember("char1").getColumnMetaData()[0].getLength().intValue());
    }

    /**
     * Test of @OrderBy.
     */
    public void testOrderBy()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(UserGroup.class.getName(), clr);

        OrderMetaData omd = cmd1.getMetaDataForMember("members").getOrderMetaData();
        assertNotNull("UserGroup.members has no OrderMetaData!", omd);
        FieldOrder[] orderTerms = omd.getFieldOrders();
        assertFalse("UserGroup.members is not marked as using an ordered list", omd.isIndexedList());
        assertNotNull("UserGroup.members has null field ordering info", orderTerms);
        assertEquals("UserGroup.members has incorrect number of field ordering terms", orderTerms.length, 1);
        assertEquals("UserGroup.members has incorrect field ordering field-name", orderTerms[0].getFieldName(), "name");
        assertTrue("UserGroup.members has incorrect field ordering direction", orderTerms[0].isForward());
    }

    /**
     * Test of JPA @IdClass with pk using acessors.
     */
    public void testIdClassAccessors()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(IdClassAccessors.class.getName(), clr);
        assertEquals(1, cmd1.getNoOfPrimaryKeyMembers());
        assertTrue(cmd1.getAbsolutePositionOfMember("free")>=0);
        assertEquals("FFFF",cmd1.getMetaDataForManagedMemberAtAbsolutePosition(cmd1.getRelativePositionOfMember("free")).getColumnMetaData()[0].getName());
    }

    /**
     * Test of persistent properties using annotations.
     */
    /*public void testPersistentProperties()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(JPAGetter.class.getName(), clr);
        assertEquals(1, cmd1.getNoOfPrimaryKeyMembers());
    }*/

    /**
     * Test of column name for property instead of field
     */
    /*public void testPropertyColumName()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Employee.class.getName(), clr);

        // it is valid according JPA to have property accessor instead of field accessors. property accessors are persistent while field not.
        assertNotNull("Employee.lastName has no field information", cmd1.getMetaDataForMember("lastName"));
        assertNotNull("Employee.lastName has no column information", cmd1.getMetaDataForMember("lastName").getColumnMetaData());
        assertEquals("Employee.lastName has incorrect number of columns", 
            1, cmd1.getMetaDataForMember("lastName").getColumnMetaData().length);
        assertEquals("Employee.last has incorrect column spec", 
            "LASTNAME", cmd1.getMetaDataForMember("lastName").getColumnMetaData()[0].getName());

        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);

        // it is valid according JPA to have property accessor instead of field accessors. property accessors are persistent while field not.
        assertNotNull(cmd2.getMetaDataForMember("age"));
        assertNotNull("AGE_COL",cmd2.getMetaDataForMember("age").getColumnMetaData()[0].getName());
        assertNotNull(cmd2.getMetaDataForMember("maidenName"));
        assertEquals(FieldPersistenceModifier.NONE,cmd2.getMetaDataForMember("_maidenName").getPersistenceModifier());
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd2.getMetaDataForMember("maidenName").getPersistenceModifier());
    }*/
    
    /**
     * Test of JPA @MapKeyColumn.
     */
    public void testMapKeyColumn()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertEquals("phoneNumbers_key1",cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getColumnMetaData()[0].getName());
    }
}