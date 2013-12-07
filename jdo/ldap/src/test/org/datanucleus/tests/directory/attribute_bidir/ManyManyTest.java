/**********************************************************************
Copyright (c) 2008 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.attribute_bidir;

import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class ManyManyTest extends JDOPersistenceTestCase
{
    public ManyManyTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(SecurityGroup.class);
        clean(Project.class);
        clean(Person.class);
    }

    protected void tearDown() throws Exception
    {
        clean(SecurityGroup.class);
        clean(Project.class);
        clean(Person.class);
        super.tearDown();
    }

    public void testPersistWithoutRef()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            pm.makePersistent(adminGroup);
            Project ldapProject = new Project("LDAP");
            pm.makePersistent(ldapProject);
            ProjectWithBudget appEngineProject = new ProjectWithBudget("AppEngine", 100);
            pm.makePersistent(appEngineProject);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNull(daffyDuck.getAddress());
            assertNull(daffyDuck.getComputer());
            assertNull(daffyDuck.getDepartment());
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());
            assertNotNull(daffyDuck.getGroups());
            assertTrue(daffyDuck.getGroups().isEmpty());
            assertNotNull(daffyDuck.getProjects());
            assertTrue(daffyDuck.getProjects().isEmpty());

            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertTrue(adminGroup.getMembers().isEmpty());

            ldapProject = pm.getObjectById(Project.class, "LDAP");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertTrue(ldapProject.getMembers().isEmpty());
            
            appEngineProject = pm.getObjectById(ProjectWithBudget.class, "AppEngine");
            assertEquals("AppEngine", appEngineProject.getName());
            assertEquals(100, appEngineProject.getBudget());
            assertNotNull(appEngineProject.getMembers());
            assertTrue(appEngineProject.getMembers().isEmpty());

            Extent<Project> extent = pm.getExtent(Project.class);
            Iterator<Project> iterator = extent.iterator();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertTrue(iterator.hasNext());
            iterator.next();
            assertFalse(iterator.hasNext());
            
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

    public void testPersistPersonWithRef()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist using persistence-by-reachability
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            SecurityGroup developerGroup = new SecurityGroup("Developers");
            Project ldapProject = new Project("LDAP");
            Project rdbmsProject = new Project("RDBMS");
            Project xmlProject = new Project("XML");
            ProjectWithBudget appEngineProject = new ProjectWithBudget("AppEngine", 100);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            Person bugsBunny = new Person("Bugs", "Bunny", "Bugs Bunny", null, null, null);
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);

            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            daffyDuck.getGroups().add(developerGroup);
            developerGroup.getMembers().add(daffyDuck);

            daffyDuck.getProjects().add(ldapProject);
            ldapProject.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(rdbmsProject);
            rdbmsProject.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(xmlProject);
            xmlProject.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(appEngineProject);
            appEngineProject.getMembers().add(daffyDuck);

            bugsBunny.getGroups().add(adminGroup);
            adminGroup.getMembers().add(bugsBunny);

            bugsBunny.getProjects().add(ldapProject);
            ldapProject.getMembers().add(bugsBunny);

            speedyGonzales.getGroups().add(developerGroup);
            developerGroup.getMembers().add(speedyGonzales);

            speedyGonzales.getProjects().add(rdbmsProject);
            rdbmsProject.getMembers().add(speedyGonzales);

            pm.makePersistent(daffyDuck);
            pm.makePersistent(bugsBunny);
            pm.makePersistent(speedyGonzales);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getGroups());
            assertEquals(2, daffyDuck.getGroups().size());
            assertNotNull(daffyDuck.getProjects());
            assertEquals(4, daffyDuck.getProjects().size());

            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs Bunny", bugsBunny.getFullName());
            assertEquals("Bugs", bugsBunny.getFirstName());
            assertEquals("Bunny", bugsBunny.getLastName());
            assertNotNull(bugsBunny.getGroups());
            assertEquals(1, bugsBunny.getGroups().size());
            assertNotNull(bugsBunny.getProjects());
            assertEquals(1, bugsBunny.getProjects().size());

            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("Speedy Gonzales", speedyGonzales.getFullName());
            assertEquals("Speedy", speedyGonzales.getFirstName());
            assertEquals("Gonzales", speedyGonzales.getLastName());
            assertNotNull(speedyGonzales.getGroups());
            assertEquals(1, speedyGonzales.getGroups().size());
            assertNotNull(speedyGonzales.getProjects());
            assertEquals(1, speedyGonzales.getProjects().size());

            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertEquals(2, adminGroup.getMembers().size());

            developerGroup = pm.getObjectById(SecurityGroup.class, "Developers");
            assertEquals("Developers", developerGroup.getName());
            assertNotNull(developerGroup.getMembers());
            assertEquals(2, developerGroup.getMembers().size());

            ldapProject = pm.getObjectById(Project.class, "LDAP");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertEquals(2, ldapProject.getMembers().size());

            rdbmsProject = pm.getObjectById(Project.class, "RDBMS");
            assertEquals("RDBMS", rdbmsProject.getName());
            assertNotNull(rdbmsProject.getMembers());
            assertEquals(2, rdbmsProject.getMembers().size());

            xmlProject = pm.getObjectById(Project.class, "XML");
            assertEquals("XML", xmlProject.getName());
            assertNotNull(xmlProject.getMembers());
            assertEquals(1, xmlProject.getMembers().size());

            appEngineProject = pm.getObjectById(ProjectWithBudget.class, "AppEngine");
            assertEquals("AppEngine", appEngineProject.getName());
            assertNotNull(appEngineProject.getMembers());
            assertEquals(1, appEngineProject.getMembers().size());
            
            assertTrue(adminGroup.getMembers().contains(daffyDuck));
            assertTrue(adminGroup.getMembers().contains(bugsBunny));
            assertFalse(adminGroup.getMembers().contains(speedyGonzales));

            assertTrue(developerGroup.getMembers().contains(daffyDuck));
            assertFalse(developerGroup.getMembers().contains(bugsBunny));
            assertTrue(developerGroup.getMembers().contains(speedyGonzales));

            assertTrue(ldapProject.getMembers().contains(daffyDuck));
            assertTrue(ldapProject.getMembers().contains(bugsBunny));
            assertFalse(ldapProject.getMembers().contains(speedyGonzales));

            assertTrue(rdbmsProject.getMembers().contains(daffyDuck));
            assertFalse(rdbmsProject.getMembers().contains(bugsBunny));
            assertTrue(rdbmsProject.getMembers().contains(speedyGonzales));

            assertTrue(xmlProject.getMembers().contains(daffyDuck));
            assertFalse(xmlProject.getMembers().contains(bugsBunny));
            assertFalse(xmlProject.getMembers().contains(speedyGonzales));

            assertTrue(appEngineProject.getMembers().contains(daffyDuck));
            assertFalse(appEngineProject.getMembers().contains(bugsBunny));
            assertFalse(appEngineProject.getMembers().contains(speedyGonzales));
            
            assertTrue(daffyDuck.getGroups().contains(adminGroup));
            assertTrue(daffyDuck.getGroups().contains(developerGroup));

            assertTrue(daffyDuck.getProjects().contains(ldapProject));
            assertTrue(daffyDuck.getProjects().contains(rdbmsProject));
            assertTrue(daffyDuck.getProjects().contains(xmlProject));
            assertTrue(daffyDuck.getProjects().contains(appEngineProject));

            assertTrue(bugsBunny.getGroups().contains(adminGroup));

            assertTrue(bugsBunny.getProjects().contains(ldapProject));

            assertTrue(speedyGonzales.getGroups().contains(developerGroup));

            assertTrue(speedyGonzales.getProjects().contains(rdbmsProject));

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

    /**
     * SecurityGroup-(N)---------------------(M)-Person
     * <ul>
     * <li>The SecurityGroup class has a Collection<Person> members
     * <li>The Person class has a Collection<SecurityGroup> groups, this relation is mapped-by
     * <li>In LDAP the relation is stored at the SecurityGroup side (attribute member, multi-valued)
     * </ul>
     */
    public void testOwnerAtReferencingSide()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            Project ldapProject = new Project("LDAP");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(ldapProject);
            ldapProject.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertEquals(1, adminGroup.getMembers().size());
            assertTrue(adminGroup.getMembers().contains(daffyDuck));
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getGroups());
            assertEquals(1, daffyDuck.getGroups().size());
            assertTrue(daffyDuck.getGroups().contains(adminGroup));

            // remove daffy duck
            adminGroup.getMembers().remove(daffyDuck);
            daffyDuck.getGroups().remove(adminGroup);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from group
            // ensure that daffy duck wasn't deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertEquals(0, adminGroup.getMembers().size());
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getGroups());
            assertEquals(0, daffyDuck.getGroups().size());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            speedyGonzales.getGroups().add(adminGroup);
            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            adminGroup.getMembers().add(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new group members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertEquals(2, adminGroup.getMembers().size());
            assertTrue(adminGroup.getMembers().contains(daffyDuck));
            assertTrue(adminGroup.getMembers().contains(speedyGonzales));

            // create a developers group, move daffy to it, add speedy
            adminGroup.getMembers().remove(daffyDuck);
            SecurityGroup developerGroup = new SecurityGroup("Developers");
            developerGroup.getMembers().add(daffyDuck);
            developerGroup.getMembers().add(speedyGonzales);
            pm.makePersistent(developerGroup);
            tx.commit();
            pm.close();

            // test the members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            developerGroup = pm.getObjectById(SecurityGroup.class, "Developers");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("Administrators", adminGroup.getName());
            assertNotNull(adminGroup.getMembers());
            assertEquals(1, adminGroup.getMembers().size());
            assertTrue(adminGroup.getMembers().contains(speedyGonzales));
            assertEquals("Developers", developerGroup.getName());
            assertNotNull(developerGroup.getMembers());
            assertEquals(2, developerGroup.getMembers().size());
            assertTrue(developerGroup.getMembers().contains(daffyDuck));
            assertTrue(developerGroup.getMembers().contains(speedyGonzales));

            // delete group and person
            pm.deletePersistent(adminGroup);
            pm.deletePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test deleted objects and removed relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
                fail("Object 'Administrators' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            developerGroup = pm.getObjectById(SecurityGroup.class, "Developers");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("Developers", developerGroup.getName());
            assertNotNull(developerGroup.getMembers());
            assertEquals(1, developerGroup.getMembers().size());
            assertTrue(developerGroup.getMembers().contains(speedyGonzales));
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

    /**
     * SecurityGroup-(N)---------------------(M)-Person
     * <ul>
     * <li>The SecurityGroup class has a Collection<Person> members
     * <li>The Person class has a Collection<SecurityGroup> groups, this relation is mapped-by
     * <li>In LDAP the relation is stored at the SecurityGroup side (attribute member, multi-valued)
     * </ul>
     */
    public void testOwnerAtReferencingSideDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            Project ldapProject = new Project("LDAP");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(ldapProject);
            ldapProject.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            SecurityGroup detachedAdminGroup = pm.detachCopy(adminGroup);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // check references
            assertEquals("Administrators", detachedAdminGroup.getName());
            assertNotNull(detachedAdminGroup.getMembers());
            assertEquals(1, detachedAdminGroup.getMembers().size());
            assertTrue(detachedAdminGroup.getMembers().contains(detachedDaffyDuck));
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertNotNull(detachedDaffyDuck.getGroups());
            assertEquals(1, detachedDaffyDuck.getGroups().size());
            assertTrue(detachedDaffyDuck.getGroups().contains(detachedAdminGroup));

            // remove daffy duck
            detachedAdminGroup.getMembers().remove(detachedDaffyDuck);
            detachedDaffyDuck.getGroups().remove(detachedAdminGroup);
            JDOHelper.makeDirty(detachedAdminGroup, "members");

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAdminGroup);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            detachedAdminGroup = pm.detachCopy(adminGroup);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from group
            // ensure that daffy duck wasn't deleted
            assertEquals("Administrators", detachedAdminGroup.getName());
            assertNotNull(detachedAdminGroup.getMembers());
            assertEquals(0, detachedAdminGroup.getMembers().size());
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertNotNull(detachedDaffyDuck.getGroups());
            assertEquals(0, detachedDaffyDuck.getGroups().size());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            detachedAdminGroup.getMembers().add(detachedDaffyDuck);
            detachedAdminGroup.getMembers().add(speedyGonzales);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAdminGroup);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedAdminGroup = pm.detachCopy(adminGroup);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Person detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new group members
            assertEquals("Administrators", detachedAdminGroup.getName());
            assertNotNull(detachedAdminGroup.getMembers());
            assertEquals(2, detachedAdminGroup.getMembers().size());
            assertTrue(detachedAdminGroup.getMembers().contains(detachedDaffyDuck));
            assertTrue(detachedAdminGroup.getMembers().contains(detachedSpeedyGonzales));
            assertTrue(detachedDaffyDuck.getGroups().contains(detachedAdminGroup));
            assertTrue(detachedSpeedyGonzales.getGroups().contains(detachedAdminGroup));

            // create a developers group, move daffy to it, add speedy
            detachedAdminGroup.getMembers().remove(detachedDaffyDuck);
            detachedDaffyDuck.getGroups().remove(detachedAdminGroup);
            SecurityGroup developerGroup = new SecurityGroup("Developers");
            developerGroup.getMembers().add(detachedDaffyDuck);
            developerGroup.getMembers().add(detachedSpeedyGonzales);
            detachedDaffyDuck.getGroups().add(developerGroup);
            detachedSpeedyGonzales.getGroups().add(developerGroup);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAdminGroup);
            pm.makePersistent(developerGroup);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            adminGroup = pm.getObjectById(SecurityGroup.class, "Administrators");
            developerGroup = pm.getObjectById(SecurityGroup.class, "Developers");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedAdminGroup = pm.detachCopy(adminGroup);
            SecurityGroup detachedDeveloperGroup = pm.detachCopy(developerGroup);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the members
            assertEquals("Administrators", detachedAdminGroup.getName());
            assertNotNull(detachedAdminGroup.getMembers());
            assertEquals(1, detachedAdminGroup.getMembers().size());
            assertTrue(detachedAdminGroup.getMembers().contains(speedyGonzales));
            assertEquals("Developers", detachedDeveloperGroup.getName());
            assertNotNull(detachedDeveloperGroup.getMembers());
            assertEquals(2, detachedDeveloperGroup.getMembers().size());
            assertTrue(detachedDeveloperGroup.getMembers().contains(daffyDuck));
            assertTrue(detachedDeveloperGroup.getMembers().contains(speedyGonzales));
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }
    }

    /**
     * Project-(N)---------------------------(M)-Person
     * <ul>
     * <li>The Project class has a Collection<Person> members
     * <li>The Person class has a Collection<Project> groups, this relation is mapped-by
     * <li>In LDAP the relation is stored at the *Person* side (attribute ou, multi-valued)
     * </ul>
     */
    public void testOwnerAtReferencedSide()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            Project ldapProject = new Project("LDAP");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(ldapProject);
            ldapProject.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertEquals(1, ldapProject.getMembers().size());
            assertTrue(ldapProject.getMembers().contains(daffyDuck));
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getProjects());
            assertEquals(1, daffyDuck.getProjects().size());
            assertTrue(daffyDuck.getProjects().contains(ldapProject));

            // remove daffy duck
            daffyDuck.getProjects().remove(ldapProject);
            ldapProject.getMembers().remove(daffyDuck);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from accounts
            // ensure that daffy duck wasn't deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getProjects());
            assertEquals(0, daffyDuck.getProjects().size());
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertEquals(0, ldapProject.getMembers().size());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            ldapProject.getMembers().add(daffyDuck);
            ldapProject.getMembers().add(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new project members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertEquals(2, ldapProject.getMembers().size());
            assertTrue(ldapProject.getMembers().contains(daffyDuck));
            assertTrue(ldapProject.getMembers().contains(speedyGonzales));

            // create a xml project, move daffy to it, add speedy
            ldapProject.getMembers().remove(daffyDuck);
            Project xmlProject = new Project("XML");
            xmlProject.getMembers().add(daffyDuck);
            xmlProject.getMembers().add(speedyGonzales);
            pm.makePersistent(xmlProject);
            tx.commit();
            pm.close();

            // test the members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            xmlProject = pm.getObjectById(Project.class, "XML");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("LDAP", ldapProject.getName());
            assertNotNull(ldapProject.getMembers());
            assertEquals(1, ldapProject.getMembers().size());
            assertTrue(ldapProject.getMembers().contains(speedyGonzales));
            assertEquals("XML", xmlProject.getName());
            assertNotNull(xmlProject.getMembers());
            assertEquals(2, xmlProject.getMembers().size());
            assertTrue(xmlProject.getMembers().contains(daffyDuck));
            assertTrue(xmlProject.getMembers().contains(speedyGonzales));

            // delete project and person
            pm.deletePersistent(ldapProject);
            pm.deletePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test deleted objects and removed relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                ldapProject = pm.getObjectById(Project.class, "LDAP");
                fail("Object 'LDAP' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            xmlProject = pm.getObjectById(Project.class, "XML");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("XML", xmlProject.getName());
            assertNotNull(xmlProject.getMembers());
            assertEquals(1, xmlProject.getMembers().size());
            assertTrue(xmlProject.getMembers().contains(speedyGonzales));
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

    /**
     * Project-(N)---------------------------(M)-Person
     * <ul>
     * <li>The Project class has a Collection<Person> members
     * <li>The Person class has a Collection<Project> groups, this relation is mapped-by
     * <li>In LDAP the relation is stored at the *Person* side (attribute ou, multi-valued)
     * </ul>
     */
    public void testOwnerAtReferencedSideDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            SecurityGroup adminGroup = new SecurityGroup("Administrators");
            Project ldapProject = new Project("LDAP");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            daffyDuck.getGroups().add(adminGroup);
            adminGroup.getMembers().add(daffyDuck);
            daffyDuck.getProjects().add(ldapProject);
            ldapProject.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Project detachedLdapProject = pm.detachCopy(ldapProject);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // check references
            assertEquals("LDAP", detachedLdapProject.getName());
            assertNotNull(detachedLdapProject.getMembers());
            assertEquals(1, detachedLdapProject.getMembers().size());
            assertTrue(detachedLdapProject.getMembers().contains(detachedDaffyDuck));
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertNotNull(detachedDaffyDuck.getProjects());
            assertEquals(1, detachedDaffyDuck.getProjects().size());
            assertTrue(detachedDaffyDuck.getProjects().contains(detachedLdapProject));

            // remove daffy duck
            detachedLdapProject.getMembers().remove(detachedDaffyDuck);
            detachedDaffyDuck.getProjects().remove(detachedLdapProject);
            JDOHelper.makeDirty(detachedLdapProject, "members");

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedLdapProject);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            detachedLdapProject = pm.detachCopy(ldapProject);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from accounts
            // ensure that daffy duck wasn't deleted
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertNotNull(detachedDaffyDuck.getProjects());
            assertEquals(0, detachedDaffyDuck.getProjects().size());
            assertEquals("LDAP", detachedLdapProject.getName());
            assertNotNull(detachedLdapProject.getMembers());
            assertEquals(0, detachedLdapProject.getMembers().size());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            detachedLdapProject.getMembers().add(detachedDaffyDuck);
            detachedLdapProject.getMembers().add(speedyGonzales);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedLdapProject);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedLdapProject = pm.detachCopy(ldapProject);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Person detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new project members
            assertNotNull(detachedLdapProject.getMembers());
            assertEquals(2, detachedLdapProject.getMembers().size());
            assertTrue(detachedLdapProject.getMembers().contains(detachedDaffyDuck));
            assertTrue(detachedLdapProject.getMembers().contains(detachedSpeedyGonzales));
            assertEquals(1, detachedDaffyDuck.getProjects().size());
            assertTrue(detachedDaffyDuck.getProjects().contains(detachedLdapProject));
            assertEquals(1, detachedSpeedyGonzales.getProjects().size());
            assertTrue(detachedSpeedyGonzales.getProjects().contains(detachedLdapProject));

            // create a xml project, move daffy to it, add speedy
            detachedLdapProject.getMembers().remove(detachedDaffyDuck);
            detachedDaffyDuck.getProjects().remove(detachedLdapProject);
            Project xmlProject = new Project("XML");
            xmlProject.getMembers().add(detachedDaffyDuck);
            xmlProject.getMembers().add(detachedSpeedyGonzales);
            detachedDaffyDuck.getProjects().add(xmlProject);
            detachedSpeedyGonzales.getProjects().add(xmlProject);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedLdapProject);
            pm.makePersistent(xmlProject);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            ldapProject = pm.getObjectById(Project.class, "LDAP");
            xmlProject = pm.getObjectById(Project.class, "XML");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedLdapProject = pm.detachCopy(ldapProject);
            Project detachedXmlProject = pm.detachCopy(xmlProject);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the members
            assertEquals("LDAP", detachedLdapProject.getName());
            assertNotNull(detachedLdapProject.getMembers());
            assertEquals(1, detachedLdapProject.getMembers().size());
            assertTrue(detachedLdapProject.getMembers().contains(detachedSpeedyGonzales));
            assertEquals("XML", detachedXmlProject.getName());
            assertNotNull(detachedXmlProject.getMembers());
            assertEquals(2, detachedXmlProject.getMembers().size());
            assertTrue(detachedXmlProject.getMembers().contains(detachedDaffyDuck));
            assertTrue(detachedXmlProject.getMembers().contains(detachedSpeedyGonzales));
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }
    }

}
