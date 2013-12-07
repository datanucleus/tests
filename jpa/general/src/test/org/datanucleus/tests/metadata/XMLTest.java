/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jpa.metadata.JPAMetaDataManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.ElementMetaData;
import org.datanucleus.metadata.EventListenerMetaData;
import org.datanucleus.metadata.JoinMetaData;
import org.datanucleus.metadata.KeyMetaData;
import org.datanucleus.metadata.MapMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.metadata.QueryLanguage;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.QueryResultMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.metadata.TableGeneratorMetaData;
import org.datanucleus.metadata.QueryResultMetaData.PersistentTypeMapping;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.abstractclasses.AbstractSimpleBase;
import org.jpox.samples.abstractclasses.ConcreteSimpleSub1;
import org.jpox.samples.abstractclasses.ConcreteSimpleSub2;
import org.jpox.samples.many_many.PetroleumCustomer;
import org.jpox.samples.many_many.PetroleumSupplier;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.MyListener;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.WebSite;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;

/**
 * Tests for the use of JPA XML MetaData.
 */
public class XMLTest extends JPAPersistenceTestCase
{
    public XMLTest(String name)
    {
        super(name);
    }

    /**
     * Test of 1-1 uni relation
     */
    public void testOneToOneUni()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(LoginAccount.class.getName(), clr);
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("login");
        assertNotNull("LoginAccount.login is null!", fmd1);
        assertEquals("LoginAccount.login mapped-by is incorrect", fmd1.getMappedBy(), null);
        assertEquals("LoginAccount.login relationType is incorrect", 
            fmd1.getRelationType(clr), RelationType.ONE_TO_ONE_UNI);

        assertNotNull("LoginAccount.login has no column info", fmd1.getColumnMetaData());
        assertEquals("LoginAccount.login has incorrect number of columns", fmd1.getColumnMetaData().length, 1);
        assertEquals("LoginAccount.login column name is wrong", fmd1.getColumnMetaData()[0].getName(), "LOGIN_ID");
    }

    /**
     * Test of 1-1 bi relation
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
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("timer");
        assertNotNull("Boiler.timer is null!", fmd1);
        assertEquals("Boiler.timer mapped-by is incorrect", "boiler", fmd1.getMappedBy());
        assertEquals("Boiler.timer relationType is incorrect", 
            RelationType.ONE_TO_ONE_BI, fmd1.getRelationType(clr));

        // owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Timer.class.getName(), clr);
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
        assertEquals("Department.projects jointable name is incorrect", "JPA_MD_DEPT_PROJECTS", fmd1.getTable());

        JoinMetaData joinmd = fmd1.getJoinMetaData();
        assertNotNull("Department.projects has no join table!", joinmd);
        assertNotNull("Department.projects has incorrect join columns", joinmd.getColumnMetaData());
        assertEquals("Department.projects has incorrect number of join columns",
            1, joinmd.getColumnMetaData().length);
        assertEquals("Department.projects has incorrect join column name",
            joinmd.getColumnMetaData()[0].getName(), "DEPT_ID");

        ElementMetaData elemmd = fmd1.getElementMetaData();
        assertNotNull("Department.projects has no element column info but should", elemmd);
        ColumnMetaData[] colmds = elemmd.getColumnMetaData();
        assertNotNull("Department.projects has incorrect element columns", colmds);
        assertEquals("Department.projects has incorrect number of element columns", 1, colmds.length);
        assertEquals("Department.projects has incorrect element column name", "PROJECT_ID", colmds[0].getName());
    }

    /**
     * Test of JPA 1-N unidir FK relation.
     * This is really a 1-N uni join since JPA doesnt support 1-N uni FK.
     */
    /*public void testOneToManyUniFK()
    {
        NucleusContext pmfcontext = new NucleusContext(new PersistenceConfiguration(){});
        pmfcontext.setApi("JPA");
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        MetaDataManager metaDataMgr = new JPAMetaDataManager(pmfcontext);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.initialise(pumd, clr);

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
     * Test of JPA 1-N bidir JoinTable relation
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
        assertEquals("Manager has incorrect table name", cmd1.getTable(), "JPA_MD_MANAGER");
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("subordinates");
        assertNotNull("Manager.subordinates is null!", fmd1);
        assertEquals("Manager.subordinates mapped-by is incorrect", fmd1.getMappedBy(), "manager");
        assertEquals("Manager.subordinates relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.ONE_TO_MANY_BI);
        assertEquals("Manager.subordinates jointable name is incorrect", fmd1.getTable(), "JPA_MD_MGR_EMPLOYEES");

        // non-owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Employee.class.getName(), clr);
        assertEquals("Employee has incorrect table name", cmd2.getTable(), "JPA_MD_EMPLOYEE");
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
        assertEquals("Customer has incorrect table name", cmd1.getTable(), "JPA_MD_PETROL_CUSTOMER");
        AbstractMemberMetaData fmd1 = cmd1.getMetaDataForMember("suppliers");
        assertNotNull("Customer.suppliers is null!", fmd1);
        assertEquals("Customer.suppliers mapped-by is incorrect", fmd1.getMappedBy(), "customers");
        assertEquals("Customer.suppliers relationType is incorrect",
            fmd1.getRelationType(clr), RelationType.MANY_TO_MANY_BI);
        assertEquals("Customer.suppliers jointable name is incorrect", fmd1.getTable(), "JPA_MD_PETROL_CUST_SUPP");

        // non-owner side
        ClassMetaData cmd2 = (ClassMetaData)metaDataMgr.getMetaDataForClass(PetroleumSupplier.class.getName(), clr);
        assertEquals("Supplier has incorrect table name", cmd2.getTable(), "JPA_MD_PETROL_SUPPLIER");
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
     * Test of JPA 1-N unidir Map relation.
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
     * Test of JPA <table-generator>
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
     * Test of JPA <sequence-generator>
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
     * Test of Event Listeners
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
     * Test of JPA <named-query>, <named-native-query>
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
            "SELECT * FROM JPALOGIN WHERE FIRSTNAME = 'John'", sqlQuery.getQuery());
    }

    /**
     * Test of JPA <sql-result-set-mapping>
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
        assertEquals("LoginAccount has incorrect number of query result mappings", 2, queryResultMappings.length);

        // Example 1 : Returning 2 entities
        QueryResultMetaData qrmd = null;
        for (int i=0;i<queryResultMappings.length;i++)
        {
            QueryResultMetaData md = queryResultMappings[i];
            if (md.getName().equals("MD_LOGIN_PLUS_ACCOUNT"))
            {
                qrmd = md;
                break;
            }
        }
        if (qrmd == null)
        {
            fail("SQL ResultSet mapping MD_LOGIN_PLUS_ACCOUNT is not present!");
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
            if (md.getName().equals("MD_ACCOUNT_NAMES"))
            {
                qrmd = md;
                break;
            }
        }
        if (qrmd == null)
        {
            fail("SQL ResultSet mapping MD_ACCOUNT_NAMES is not present!");
        }
        scalarCols = qrmd.getScalarColumns();
        assertNotNull("LoginAccount sql mapping has incorrect scalar cols", scalarCols);
        assertEquals("LoginAccount sql mapping has incorrect column name", "FIRSTNAME", scalarCols[0]);
        assertEquals("LoginAccount sql mapping has incorrect column name", "LASTNAME", scalarCols[1]);
        sqlMappingEntities = qrmd.getPersistentTypeMappings();
        assertNull("LoginAccount sql mapping has incorrect entities", sqlMappingEntities);
    }

    /**
     * Test of JPA <map-key>.
     */
    public void testMapKey()
    {
        NucleusContext nucleusCtx = new NucleusContext("JPA", null);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        MetaDataManager metaDataMgr = new JPAMetaDataManager(nucleusCtx);
        PersistenceUnitMetaData pumd = metaDataMgr.getMetaDataForPersistenceUnit("JPATest");
        metaDataMgr.loadPersistenceUnit(pumd, null);

        // Retrieve the metadata from the MetaDataManager (populates and initialises everything)
        ClassMetaData cmd1 = (ClassMetaData)metaDataMgr.getMetaDataForClass(Person.class.getName(), clr);
        assertEquals("name", cmd1.getMetaDataForMember("phoneNumbers").getKeyMetaData().getMappedBy());
    }    
}