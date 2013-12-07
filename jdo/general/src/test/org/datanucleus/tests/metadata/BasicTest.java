/**********************************************************************
Copyright (c) 2004 Ralf Ullrich and others. All rights reserved. 
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
2004 Andy Jefferson - merged ClassMetaDataTest
    ...
**********************************************************************/
package org.datanucleus.tests.metadata;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import junit.framework.Assert;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.metadata.ForeignKeyAction;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.NullValue;
import org.datanucleus.metadata.xml.MetaDataParser;
import org.datanucleus.samples.haunted.Phantom;
import org.datanucleus.samples.haunted.Vampire;
import org.datanucleus.samples.metadata.animal.Animal;
import org.datanucleus.samples.metadata.animal.Cat;
import org.datanucleus.samples.metadata.animal.Dog;
import org.datanucleus.samples.metadata.datastoreidentity.D0;
import org.datanucleus.samples.metadata.datastoreidentity.D1;
import org.datanucleus.samples.metadata.datastoreidentity.D2;
import org.datanucleus.samples.metadata.datastoreidentity.D3;
import org.datanucleus.samples.metadata.datastoreidentity.D4;
import org.datanucleus.samples.metadata.datastoreidentity.D5;
import org.datanucleus.samples.metadata.inh2.Base;
import org.datanucleus.samples.metadata.inh2.Container;
import org.datanucleus.samples.metadata.inh2.SubBase;
import org.datanucleus.samples.metadata.user.User1;
import org.datanucleus.samples.metadata.user.User2;
import org.datanucleus.samples.metadata.user.User3;
import org.datanucleus.samples.metadata.user.UserGroup1;
import org.datanucleus.samples.metadata.user.UserGroup2;
import org.datanucleus.samples.metadata.user.UserGroup3;
import org.datanucleus.samples.metadata.user.UserId1;
import org.datanucleus.samples.metadata.user.UserId2;
import org.datanucleus.samples.metadata.user.UserId3;
import org.datanucleus.samples.store.Book;
import org.datanucleus.samples.store.Inventory;
import org.datanucleus.samples.widget.DateWidget;
import org.datanucleus.samples.widget.FloatWidget;
import org.datanucleus.samples.widget.LevelAboveWidget;
import org.datanucleus.samples.widget.PackageClassWidget;
import org.datanucleus.samples.widget.Widget;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.i18n.ISO8859_2;
import org.jpox.samples.i18n.UTF8;
import org.jpox.samples.models.company.Manager;

/**
 * Tests for metadata
 */
public class BasicTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public BasicTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Container.class,
                    SubBase.class,
                    Base.class,
                    UserGroup1.class,
                    UserGroup2.class,
                    UserGroup3.class,
                    User1.class,
                    User2.class,
                    User3.class
                }
            );
            initialised = true;
        }
    }

    public void testInh2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Base base1 = new Base(1,"b1");
                Base base2 = new Base(2,"b2");
                Base base3 = new Base(3,"b3");
                SubBase group1 = new SubBase(4, "b4", "SB1");
                SubBase group2 = new SubBase(5, "b5", "SB2");
                Container c = new Container(new Base[]{base1, base2, base3, group1, group2});
                pm.makePersistent(c);
                tx.commit();
                tx.begin();
                Collection col = (Collection) pm.newQuery(SubBase.class).execute();
                assertTrue(col.size()==2);
                assertTrue(col.contains(group1));
                assertTrue(col.contains(group2));
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(Container.class);
            clean(SubBase.class);
            clean(Base.class);
        }
    }

    public void testUseOfObjectIdClass()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                User1 u1 = new User1();
                u1.setId("u1");
                u1.setName("user1");
                UserGroup1 ug1 = new UserGroup1();
                ug1.setId("ug1");
                ug1.setName("userg1");
                ug1.setUserGroupName("usergn1");
                pm.makePersistent(ug1);
                pm.makePersistent(u1);
                
                User2 u2 = new User2();
                u2.setId("u2");
                u2.setName("user2");
                UserGroup2 ug2 = new UserGroup2();
                ug2.setId("ug2");
                ug2.setName("userg2");
                ug2.setUserGroupName("usergn2");
                pm.makePersistent(ug2);
                pm.makePersistent(u2);
                
                User3 u3 = new User3();
                u3.setId("u3");
                u3.setName("user3");
                UserGroup3 ug3 = new UserGroup3();
                ug3.setId("ug3");
                ug3.setName("userg3");
                ug3.setUserGroupName("usergn3");
                pm.makePersistent(ug3);
                pm.makePersistent(u3);
                
                tx.commit();
                tx.begin();
                
                u1 = (User1) pm.getObjectById(new UserId1("u1"),true);
                assertTrue(u1.getName().equals("user1"));
                ug1 = (UserGroup1) pm.getObjectById(new UserId1("ug1"),true);
                assertTrue(ug1.getName().equals("userg1"));
                
                u2 = (User2) pm.getObjectById(new UserId2("u2"),true);
                assertTrue(u2.getName().equals("user2"));
                ug2 = (UserGroup2) pm.getObjectById(new UserId2("ug2"),true);
                assertTrue(ug2.getName().equals("userg2"));
                
                u3 = (User3) pm.getObjectById(new UserId3("u3"),true);
                assertTrue(u3.getName().equals("user3"));
                ug3 = (UserGroup3) pm.getObjectById(new UserId3("ug3"),true);
                assertTrue(ug3.getName().equals("userg3"));
                
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(UserGroup1.class);
            clean(UserGroup2.class);
            clean(UserGroup3.class);
            clean(User1.class);
            clean(User2.class);
            clean(User3.class);
        }
    }

    /**
     * Test for the MetaDataManager.
     */
    public void testMetaDataManager()
    {
        MetaDataManager mgr = new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        org.datanucleus.metadata.AbstractClassMetaData cmd=mgr.getMetaDataForClass(Inventory.class, new ClassLoaderResolverImpl());
        if (cmd == null)
        {
            fail("Failed to load MetaData for org.jpox.samples.store.Inventory class");
        }

        org.datanucleus.metadata.AbstractClassMetaData cmd2=mgr.getMetaDataForClass(Book.class, new ClassLoaderResolverImpl());
        if (cmd2 == null)
        {
            fail("Failed to load MetaData for org.jpox.samples.store.Book class");
        }
    }
    
    /**
     * Test for the MetaDataManager.
     */
    public void testMetaDataMultithreaded()
    {
        final MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);

        ThreadGroup group = new ThreadGroup("tgroup");
        Thread threads[] = new Thread[500];
        
        final List errors = new Vector();
        final Object lock = new Object();        
        for(int i=0; i<500; i++)
        {
            threads[i] = new Thread(group,new Runnable()
            {
                public void run()
                {
                    try
                    {
                        synchronized(lock)
                        {
                            lock.wait();
                        }
                    }
                    catch(InterruptedException e)
                    {                        
                    }
                    try
                    {
                        //this populate and initilize the class
                        mgr.getMetaDataForClass(Manager.class, new ClassLoaderResolverImpl()).isPopulated();
                    }
                    catch(Throwable e)
                    {
                        e.printStackTrace();
                        errors.add(e.getMessage());
                    }
                }
            }); 
        }
        
        for(int i=0; i<500; i++)
        {
            threads[i].start();
        }
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e1)
        {
        }
        synchronized(lock)
        {
            lock.notifyAll();
        }
        for(int i=0; i<500; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        assertEquals("Errors "+errors,0,errors.size());
        
        org.datanucleus.metadata.AbstractClassMetaData cmd2=mgr.getMetaDataForClass(Book.class, new ClassLoaderResolverImpl());
        if (cmd2 == null)
        {
            fail("Failed to load MetaData for org.jpox.samples.store.Book class");
        }
    }

    /**
     * Tests the loading of XML metadata specified in package,jdo.
     */
    public void testLoadingMetaDataForPackage()
    {
        MetaDataManager mmgr = new NucleusContext("JDO", null).getMetaDataManager();
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        // Validate metadata for DateWidget
        AbstractClassMetaData cmd = mmgr.getMetaDataForClass(DateWidget.class, clr);
        assertNotNull("ClassMetaData is null!", cmd);
        assertEquals("Class name", DateWidget.class.getName(), cmd.getFullClassName());
        assertEquals("Package name", "org.datanucleus.samples.widget", cmd.getPackageName());
        assertEquals("Superclass", Widget.class.getName(), cmd.getPersistenceCapableSuperclass());
        assertEquals("Identity type", IdentityType.DATASTORE, cmd.getIdentityType());
        assertNull("Identity class", cmd.getObjectidClass());
        assertNotNull("Inheritance", cmd.getInheritanceMetaData());
        assertEquals("Inheritance strategy", cmd.getInheritanceMetaData().getStrategy().toString(), "new-table");

        String[] sortedFieldNames = new String[]
            {
                "dateField",
                "dateJdbcTimestampField",
                "sqlDateField",
                "sqlTimestampField"
            };
        Assert.assertEquals("Field count", sortedFieldNames.length, cmd.getNoOfManagedMembers());
        for (int i = 0; i < sortedFieldNames.length; ++i)
        {
            AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtRelativePosition(i);
            String s = sortedFieldNames[i];

            assertEquals(s, fmd.getName());
            assertEquals(s + " persistence modifier", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
            assertEquals(s + " primary key", false, fmd.isPrimaryKey());
            assertEquals(s + " null value handling", NullValue.NONE, fmd.getNullValue());
            assertEquals(s + " default fetch group", true, fmd.isDefaultFetchGroup());
            assertNull(s + " collection metadata", fmd.getContainer());
            assertNull(s + " map metadata", fmd.getContainer());
        }

        // Validate metadata for FloatWidget
        cmd = mmgr.getMetaDataForClass(FloatWidget.class, clr);
        assertNotNull("Metadata", cmd);
        assertEquals("Class name", FloatWidget.class.getName(), cmd.getFullClassName());
        assertEquals("Package name", "org.datanucleus.samples.widget", cmd.getPackageName());
        assertEquals("Superclass", Widget.class.getName(), cmd.getPersistenceCapableSuperclass());
        assertEquals("Identity type", IdentityType.DATASTORE, cmd.getIdentityType());
        assertNull("Identity class", cmd.getObjectidClass());
        sortedFieldNames = new String[]
            {
                "doubleField",
                "doubleObjField",
                "floatField",
                "floatObjField"
            };
        NullValue[] nullValueHandlings = new NullValue[]
            {
                NullValue.EXCEPTION,
                NullValue.NONE,
                NullValue.EXCEPTION,
                NullValue.NONE
            };
        assertEquals("Field count", sortedFieldNames.length, cmd.getNoOfManagedMembers());
        for (int i = 0; i < sortedFieldNames.length; ++i)
        {
            AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtRelativePosition(i);
            String s = sortedFieldNames[i];

            Assert.assertEquals(s, fmd.getName());
            Assert.assertEquals(s + " persistence modifier", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
            Assert.assertEquals(s + " primary key", false, fmd.isPrimaryKey());
            Assert.assertEquals(s + " null value handling", nullValueHandlings[i], fmd.getNullValue());
            Assert.assertEquals(s + " default fetch group", true, fmd.isDefaultFetchGroup());
            Assert.assertNull(s + " collection metadata", fmd.getContainer());
            Assert.assertNull(s + " map metadata", fmd.getContainer());
        }
    }

    /**
     * Test to check the loading of MetaData files from valid locations.
     */
    public void testLoadingMetaDataFileLocations()
    {
        MetaDataManager mmgr = new NucleusContext("JDO", null).getMetaDataManager();
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        // Check for a class defined in its own MetaData file
        AbstractClassMetaData cmd = mmgr.getMetaDataForClass(PackageClassWidget.class, clr);
        assertNotNull("MetaData is null!", cmd);
        assertNotNull("Metadata", cmd);
        assertEquals(PackageClassWidget.class.getName(), cmd.getFullClassName());
        assertEquals("org.datanucleus.samples.widget", cmd.getPackageName());
        assertNull("Superclass", cmd.getPersistenceCapableSuperclass());
        assertEquals("Identity type", IdentityType.DATASTORE, cmd.getIdentityType());
        assertNull("Identity class", cmd.getObjectidClass());
        String[] sortedFieldNames = new String[]
            {
                "normalString"
            };
        assertEquals("Field count", sortedFieldNames.length, cmd.getNoOfManagedMembers());
        for (int i=0; i<sortedFieldNames.length; ++i)
        {
            AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtRelativePosition(i);
            String s = sortedFieldNames[i];

            assertEquals(s, fmd.getName());
            assertEquals(s + " persistence modifier", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
            assertEquals(s + " primary key", false, fmd.isPrimaryKey());
            assertEquals(s + " null value handling", NullValue.NONE, fmd.getNullValue());
            assertEquals(s + " default fetch group", true, fmd.isDefaultFetchGroup());
            assertNull(s + " collection metadata", fmd.getContainer());
            assertNull(s + " map metadata", fmd.getContainer());
        }

        // Check for a class defined in package.jdo at a higher level
        cmd = mmgr.getMetaDataForClass(LevelAboveWidget.class, clr);
        assertNotNull("MetaData is null!", cmd);
        assertNotNull("Metadata", cmd);
        assertEquals(LevelAboveWidget.class.getName(), cmd.getFullClassName());
        assertEquals("org.datanucleus.samples.widget", cmd.getPackageName());
        assertNull("Superclass", cmd.getPersistenceCapableSuperclass());
        assertEquals("Identity type", IdentityType.DATASTORE, cmd.getIdentityType());
        assertNull("Identity class", cmd.getObjectidClass());
        sortedFieldNames = new String[]
            {
                "normalString"
            };
        assertEquals("Field count", sortedFieldNames.length, cmd.getNoOfManagedMembers());
        for (int i=0; i<sortedFieldNames.length; ++i)
        {
            AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtRelativePosition(i);
            String s = sortedFieldNames[i];

            assertEquals(s, fmd.getName());
            assertEquals(s + " persistence modifier", FieldPersistenceModifier.PERSISTENT, fmd.getPersistenceModifier());
            assertEquals(s + " primary key", false, fmd.isPrimaryKey());
            assertEquals(s + " null value handling", NullValue.NONE, fmd.getNullValue());
            assertEquals(s + " default fetch group", true, fmd.isDefaultFetchGroup());
            assertNull(s + " collection metadata", fmd.getContainer());
            assertNull(s + " map metadata", fmd.getContainer());
        }
    }

    /**
     * Test for the MetaDataManager.
     */
    public void testMetaDataManagerI18N()
    {
        String filename = "/org/jpox/samples/i18n/UTF8.jdo";
        MetaDataManager mmgr1 = new JDOMetaDataManager(new NucleusContext("JDO", null));
        MetaDataParser parser1 = new MetaDataParser(mmgr1, mmgr1.getNucleusContext().getPluginManager(), true);
        mmgr1.setValidate(false);
        FileMetaData fmd = (FileMetaData)parser1.parseMetaDataStream(MetaDataParser.class.getResourceAsStream(filename), filename, "jdo");
        fmd.getPackage("org.jpox.samples.i18n").getClass("UTF8");
        fmd.setMetaDataManager(mmgr1);
        org.datanucleus.metadata.AbstractClassMetaData cmd1=fmd.getPackage("org.jpox.samples.i18n").getClass("UTF8");
        cmd1.populate(new ClassLoaderResolverImpl(), null, mmgr1);
        cmd1.initialise(new ClassLoaderResolverImpl(), mmgr1);

        MetaDataManager mmgr2 = new JDOMetaDataManager(new NucleusContext("JDO", null));
        mmgr2.setValidate(false);
        org.datanucleus.metadata.AbstractClassMetaData cmd = mmgr2.getMetaDataForClass(UTF8.class, new ClassLoaderResolverImpl());
        if (!cmd.toString().equals(cmd1.toString()))
        {
            fail("i18n UTF-8 issues in parser");
        }

        filename = "/org/jpox/samples/i18n/ISO8859_2.jdo";
        MetaDataManager mmgr3 = new JDOMetaDataManager(new NucleusContext("JDO", null));
        MetaDataParser parser3 = new MetaDataParser(mmgr3, mmgr3.getNucleusContext().getPluginManager(), true);
        mmgr3.setValidate(false);
        fmd = (FileMetaData)parser3.parseMetaDataStream(MetaDataParser.class.getResourceAsStream(filename), filename, "jdo");
        fmd.getPackage("org.jpox.samples.i18n").getClass("ISO8859_2");
        fmd.setMetaDataManager(mmgr3);

        MetaDataManager mmgr4 = new JDOMetaDataManager(new NucleusContext("JDO", null));
        mmgr4.setValidate(false);
        
        cmd1=fmd.getPackage("org.jpox.samples.i18n").getClass("ISO8859_2");
        cmd1.populate(new ClassLoaderResolverImpl(), null, mmgr4);
        cmd1.initialise(null, mmgr4);
        cmd = mmgr4.getMetaDataForClass(ISO8859_2.class, new ClassLoaderResolverImpl());
        if (!cmd.toString().equals(cmd1.toString()))
        {
            fail("i18n ISO8859_2 issues in parser");
        }
    }

    /**
     * Tests datastore identity declared in many forms
     */
    public void testDatastoreIdentityMetadata()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdD0 = mgr.getMetaDataForClass(D0.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD1 = mgr.getMetaDataForClass(D1.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD2 = mgr.getMetaDataForClass(D2.class, new ClassLoaderResolverImpl());

        assertEquals("identity",cmdD0.getIdentityMetaData().getValueStrategy().toString());
        assertEquals(cmdD0.getIdentityMetaData().getValueStrategy(),cmdD1.getIdentityMetaData().getValueStrategy());
        assertEquals(cmdD0.getIdentityMetaData().getValueStrategy(),cmdD2.getIdentityMetaData().getValueStrategy());

        assertEquals("D_ID",cmdD0.getIdentityMetaData().getColumnMetaData().getName());
        assertEquals(cmdD0.getIdentityMetaData().getColumnMetaData().getName(),cmdD1.getIdentityMetaData().getColumnMetaData().getName());
        assertEquals(cmdD0.getIdentityMetaData().getColumnMetaData().getName(),cmdD2.getIdentityMetaData().getColumnMetaData().getName());
    }
    
    /**
     * Tests the column field declared in many forms
     */
    public void testFieldColumnMetadata()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdD0 = mgr.getMetaDataForClass(D0.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD1 = mgr.getMetaDataForClass(D1.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD2 = mgr.getMetaDataForClass(D2.class, new ClassLoaderResolverImpl());

        assertEquals("NNN",cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getColumnMetaData()[0].getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getColumnMetaData()[0].getName(),cmdD1.getMetaDataForManagedMemberAtAbsolutePosition(cmdD1.getAbsolutePositionOfMember("name")).getColumnMetaData()[0].getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getColumnMetaData()[0].getName(),cmdD2.getMetaDataForManagedMemberAtAbsolutePosition(cmdD2.getAbsolutePositionOfMember("name")).getColumnMetaData()[0].getName());
    }

    /**
     * Tests the foreign key declared in many forms
     */
    public void testFieldForeignKeyMetadata()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdD0 = mgr.getMetaDataForClass(D0.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD1 = mgr.getMetaDataForClass(D1.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD2 = mgr.getMetaDataForClass(D2.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD3 = mgr.getMetaDataForClass(D3.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD4 = mgr.getMetaDataForClass(D4.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD5 = mgr.getMetaDataForClass(D5.class, new ClassLoaderResolverImpl());

        //Foreign Key name
        assertEquals("FK1",cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName(),cmdD1.getMetaDataForManagedMemberAtAbsolutePosition(cmdD1.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName(),cmdD2.getMetaDataForManagedMemberAtAbsolutePosition(cmdD2.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName(),cmdD3.getMetaDataForManagedMemberAtAbsolutePosition(cmdD3.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName(),cmdD4.getMetaDataForManagedMemberAtAbsolutePosition(cmdD4.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());
        assertEquals(cmdD0.getMetaDataForManagedMemberAtAbsolutePosition(cmdD0.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName(),cmdD5.getMetaDataForManagedMemberAtAbsolutePosition(cmdD5.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getName());

        // Foreign Key delete action
        assertEquals(ForeignKeyAction.CASCADE.toString(), cmdD4.getMetaDataForManagedMemberAtAbsolutePosition(cmdD4.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getDeleteAction().toString());
        // D5 should be "restrict" since the DTD imposes a default of "restrict" when specifying the <foreign-key/> element so the
        // "delete-action" attribute is ignored.
        assertEquals(ForeignKeyAction.RESTRICT.toString(), cmdD5.getMetaDataForManagedMemberAtAbsolutePosition(cmdD5.getAbsolutePositionOfMember("name")).getForeignKeyMetaData().getDeleteAction().toString());
    }
    
    /**
     * Tests the primary key
     */
    public void testPrimaryKeyMetadata()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdD0 = mgr.getMetaDataForClass(D0.class, new ClassLoaderResolverImpl());

        //Primary Key name
        assertEquals("THED0_PK",cmdD0.getPrimaryKeyMetaData().getName());
    }

    /**
     * Tests a field declared in the metadata of a subclass, like Subclass.field
     */
    public void testFieldDeclaredInSubClassMetadata()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdD0 = mgr.getMetaDataForClass(Animal.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD1 = mgr.getMetaDataForClass(Dog.class, new ClassLoaderResolverImpl());
        AbstractClassMetaData cmdD2 = mgr.getMetaDataForClass(Cat.class, new ClassLoaderResolverImpl());
        assertNotNull(cmdD0.getMetaDataForMember("name"));
        assertNotNull(cmdD1.getMetaDataForMember("name"));
        assertEquals("dog",cmdD1.getMetaDataForMember("name").getColumnMetaData()[0].getName());
        assertNotNull(cmdD2.getMetaDataForMember("name"));
        assertEquals("cat",cmdD2.getMetaDataForMember("name").getColumnMetaData()[0].getName());
    }        

    /**
     * Test for defaulting of "dependent" attribute on a field and on a collection.
     */
    public void testDependent()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmdInh2 = mgr.getMetaDataForClass(Container.class, new ClassLoaderResolverImpl());
        AbstractMemberMetaData fmd = cmdInh2.getMetaDataForMember("members");
        // mmd doesn't have dependent attribute, so should default to false
        assertFalse(fmd.isDependent());
        assertFalse(fmd.getCollection().isDependentElement());
    }    

    /**
     * Test that MetaData classes can be serialised.
     * Why we would ever want to serialise a MetaData definition is not known to me.
     * @throws Throwable
     */
    /*public void testSerialization() throws Throwable
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext(new PersistenceConfiguration(){}));
        mgr.setValidate(false);
        AbstractClassMetaData cmdInh2 = mgr.getMetaDataForClass(Container.class, new ClassLoaderResolverImpl());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(cmdInh2);
        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
        ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        assertTrue(obj.toString().equals(cmdInh2.toString()));
    }*/
    
    public void testPersistentProperties()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmd = mgr.getMetaDataForClass(Vampire.class, new ClassLoaderResolverImpl());
        assertEquals(FieldPersistenceModifier.NONE,cmd.getMetaDataForMember("_age").getPersistenceModifier());
        assertEquals(FieldPersistenceModifier.NONE,cmd.getMetaDataForMember("_name").getPersistenceModifier());
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd.getMetaDataForMember("name").getPersistenceModifier());
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd.getMetaDataForMember("age").getPersistenceModifier());
        cmd = mgr.getMetaDataForClass(Phantom.class, new ClassLoaderResolverImpl());
        assertEquals(2,cmd.getNoOfManagedMembers());
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd.getMetaDataForMember("name").getPersistenceModifier());
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd.getMetaDataForMember("age").getPersistenceModifier());
   }
    
    public void testPersistentPropertiesFieldPropertyClash()
    {
        MetaDataManager mgr=new JDOMetaDataManager(new NucleusContext("JDO", null));
        mgr.setValidate(false);
        AbstractClassMetaData cmd = mgr.getMetaDataForClass(Phantom.class, new ClassLoaderResolverImpl());
        assertNull(cmd.getMetaDataForMember("_name"));
        assertEquals(FieldPersistenceModifier.PERSISTENT,cmd.getMetaDataForMember("name").getPersistenceModifier());
   }    
}