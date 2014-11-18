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
package org.datanucleus.tests.directory.dn_bidir;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class OneManyTest extends JDOPersistenceTestCase
{
    public OneManyTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Account.class);
        clean(Person.class);
        clean(Department.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Account.class);
        clean(Person.class);
        clean(Department.class);
        super.tearDown();
    }

    public void testPersistWithoutRef()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Department randd = new Department("R&D");
            pm.makePersistent(randd);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            pm.makePersistent(daffyDuck);
            AccountWithPassword dduck = new AccountWithPassword("dduck", "secret1", null);
            pm.makePersistent(dduck);
            Account dduck2 = new Account("dduck2", null);
            pm.makePersistent(dduck2);
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

            randd = pm.getObjectById(Department.class, "R&D");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertTrue(randd.getMembers().isEmpty());

            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            assertEquals("dduck", dduck.getUid());
            assertEquals("secret1", dduck.getPassword());
            assertNull(dduck.getOwner());
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            assertEquals("dduck2", dduck2.getUid());
            assertNull(dduck2.getOwner());
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
            Department randd = new Department("R&D");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", randd, null, null);
            randd.getMembers().add(daffyDuck);
            AccountWithPassword dduck = new AccountWithPassword("dduck", "secret1", daffyDuck);
            daffyDuck.getAccounts().add(dduck);
            Account dduck2 = new Account("dduck2", daffyDuck);
            daffyDuck.getAccounts().add(dduck2);
            pm.makePersistent(randd);
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
            assertNotNull(daffyDuck.getDepartment());
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(2, daffyDuck.getAccounts().size());
            assertNotNull(daffyDuck.getGroups());
            assertTrue(daffyDuck.getGroups().isEmpty());
            assertNotNull(daffyDuck.getProjects());
            assertTrue(daffyDuck.getProjects().isEmpty());

            randd = pm.getObjectById(Department.class, "R&D");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertEquals(1, randd.getMembers().size());
            assertEquals(daffyDuck, randd.getMembers().iterator().next());
            assertEquals(randd, daffyDuck.getDepartment());

            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            assertEquals("dduck", dduck.getUid());
            assertEquals("secret1", dduck.getPassword());
            assertNotNull(dduck.getOwner());
            assertEquals(daffyDuck, dduck.getOwner());
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            assertEquals("dduck2", dduck2.getUid());
            assertNotNull(dduck2.getOwner());
            assertEquals(daffyDuck, dduck2.getOwner());
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
     * Department-(1)------------------------(N)-Person
     * <ul>
     * <li>The Department class has a Collection<Person> members
     * <li>The Account class has a reference to its Person, this relation is mapped-by
     * <li>In LDAP the relation is stored at the Department side (attribute members, multi-valued)
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
            Department randd = new Department("R&D");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", randd, null, null);
            randd.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(randd);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertEquals(1, randd.getMembers().size());
            assertTrue(randd.getMembers().contains(daffyDuck));

            // remove daffy duck
            randd.getMembers().remove(daffyDuck);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from departement
            // ensure that daffy duck wasn't deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertEquals(0, randd.getMembers().size());
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNull(daffyDuck.getDepartment());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            randd.getMembers().add(daffyDuck);
            randd.getMembers().add(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new department members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertEquals(2, randd.getMembers().size());
            assertTrue(randd.getMembers().contains(daffyDuck));
            assertTrue(randd.getMembers().contains(speedyGonzales));
            assertEquals(randd, daffyDuck.getDepartment());
            assertEquals(randd, speedyGonzales.getDepartment());

            // create a sales department and move daffy to it
            randd.getMembers().remove(daffyDuck);
            Department sales = new Department("Sales");
            sales.getMembers().add(daffyDuck);
            daffyDuck.setDepartment(sales);
            pm.makePersistent(sales);
            tx.commit();
            pm.close();

            // test the members
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            sales = pm.getObjectById(Department.class, "Sales");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNotNull(randd.getMembers());
            assertEquals(1, randd.getMembers().size());
            assertTrue(randd.getMembers().contains(speedyGonzales));
            assertEquals(randd, speedyGonzales.getDepartment());
            assertNotNull(sales.getMembers());
            assertEquals(1, sales.getMembers().size());
            assertTrue(sales.getMembers().contains(daffyDuck));
            assertEquals(sales, daffyDuck.getDepartment());

            // delete department and person
            pm.deletePersistent(randd);
            pm.deletePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test deleted objects and removed relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                randd = pm.getObjectById(Department.class, "R&D");
                fail("Object 'R&D' should not exist any more!");
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
            sales = pm.getObjectById(Department.class, "Sales");
            assertNotNull(sales.getMembers());
            assertEquals(0, sales.getMembers().size());
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNull(speedyGonzales.getDepartment());
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
     * Department-(1)------------------------(N)-Person
     * <ul>
     * <li>The Department class has a Collection<Person> members
     * <li>The Account class has a reference to its Person, this relation is mapped-by
     * <li>In LDAP the relation is stored at the Department side (attribute members, multi-valued)
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
            Department randd = new Department("R&D");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", randd, null, null);
            randd.getMembers().add(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(randd);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Department detachedRandd = pm.detachCopy(randd);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // check references
            assertEquals("R&D", detachedRandd.getName());
            assertNotNull(detachedRandd.getMembers());
            assertEquals(1, detachedRandd.getMembers().size());
            assertTrue(detachedRandd.getMembers().contains(detachedDaffyDuck));
            assertEquals(detachedRandd, detachedDaffyDuck.getDepartment());

            // remove daffy duck
            detachedRandd.getMembers().remove(detachedDaffyDuck);
            JDOHelper.makeDirty(detachedRandd, "members");

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedRandd);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            detachedRandd = pm.detachCopy(randd);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test that daffy duck was removed from departement
            // ensure that daffy duck wasn't deleted
            assertEquals("R&D", detachedRandd.getName());
            assertNotNull(detachedRandd.getMembers());
            assertEquals(0, detachedRandd.getMembers().size());
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertNull(detachedDaffyDuck.getDepartment());

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            detachedRandd.getMembers().add(detachedDaffyDuck);
            detachedRandd.getMembers().add(speedyGonzales);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedRandd);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedRandd = pm.detachCopy(randd);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Person detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the new department members
            assertEquals("R&D", detachedRandd.getName());
            assertNotNull(detachedRandd.getMembers());
            assertEquals(2, detachedRandd.getMembers().size());
            assertTrue(detachedRandd.getMembers().contains(detachedDaffyDuck));
            assertTrue(detachedRandd.getMembers().contains(detachedSpeedyGonzales));
            assertEquals(detachedRandd, detachedDaffyDuck.getDepartment());
            assertEquals(detachedRandd, detachedSpeedyGonzales.getDepartment());

            // create a sales department and move daffy to it
            detachedRandd.getMembers().remove(detachedDaffyDuck);
            Department sales = new Department("Sales");
            sales.getMembers().add(detachedDaffyDuck);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(sales);
            pm.makePersistent(detachedRandd);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            randd = pm.getObjectById(Department.class, "R&D");
            sales = pm.getObjectById(Department.class, "Sales");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedRandd = pm.detachCopy(randd);
            Department detachedSales = pm.detachCopy(sales);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test the members
            assertNotNull(detachedRandd.getMembers());
            assertEquals(1, detachedRandd.getMembers().size());
            assertTrue(detachedRandd.getMembers().contains(detachedSpeedyGonzales));
            assertEquals(detachedRandd, detachedSpeedyGonzales.getDepartment());
            assertNotNull(detachedSales.getMembers());
            assertEquals(1, detachedSales.getMembers().size());
            assertTrue(detachedSales.getMembers().contains(detachedDaffyDuck));
            assertEquals(detachedSales, detachedDaffyDuck.getDepartment());

            // delete department and person
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.deletePersistent(detachedRandd);
            pm.deletePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // test deleted objects and removed relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                randd = pm.getObjectById(Department.class, "R&D");
                fail("Object 'R&D' should not exist any more!");
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
            sales = pm.getObjectById(Department.class, "Sales");
            assertNotNull(sales.getMembers());
            assertEquals(0, sales.getMembers().size());
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNull(speedyGonzales.getDepartment());
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
     * Person-(1)----------------------------(N)-Account
     * <ul>
     * <li>The Person class has a Collection<Account> accounts, this relation is mapped-by
     * <li>The Account class has a reference to its Person
     * <li>In LDAP the relation is stored at the *Account* side (attribute seeAlso, single-valued)
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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            AccountWithPassword dduck = new AccountWithPassword("dduck", "secret1", daffyDuck);
            daffyDuck.getAccounts().add(dduck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            tx.begin();
            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("dduck", dduck.getUid());
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(1, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck));
            assertEquals(daffyDuck, dduck.getOwner());

            // remove dduck from account list
            daffyDuck.getAccounts().remove(dduck);
            tx.commit();
            pm.close();

            // test that dduck was removed from accounts
            // ensure that dduck was deleted as it is dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(0, daffyDuck.getAccounts().size());
            try
            {
                dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }

            // add two accounts
            AccountWithPassword dduck2 = new AccountWithPassword("dduck2", "secret2", daffyDuck);
            AccountWithPassword sgonzales2 = new AccountWithPassword("sgonzales2", "secret22", daffyDuck);
            daffyDuck.getAccounts().add(dduck2);
            daffyDuck.getAccounts().add(sgonzales2);
            tx.commit();
            pm.close();

            // test the two accounts are in the account list
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            dduck2 = pm.getObjectById(AccountWithPassword.class, "dduck2");
            sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(2, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck2));
            assertTrue(daffyDuck.getAccounts().contains(sgonzales2));
            assertEquals(daffyDuck, dduck2.getOwner());
            assertEquals(daffyDuck, sgonzales2.getOwner());

            // create a new person and move one account to the new person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            daffyDuck.getAccounts().remove(sgonzales2);
            speedyGonzales.getAccounts().add(sgonzales2);
            sgonzales2.setOwner(speedyGonzales);
            pm.makePersistent(speedyGonzales);
            tx.commit();
            pm.close();

            // test new person was persisted and check account relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            dduck2 = pm.getObjectById(AccountWithPassword.class, "dduck2");
            sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(1, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck2));
            assertEquals(daffyDuck, dduck2.getOwner());
            assertNotNull(speedyGonzales.getAccounts());
            assertEquals(1, speedyGonzales.getAccounts().size());
            assertTrue(speedyGonzales.getAccounts().contains(sgonzales2));
            assertEquals(speedyGonzales, sgonzales2.getOwner());

            // delete one person and account
            // test removed objects and relationships
            pm.deletePersistent(daffyDuck);
            pm.deletePersistent(sgonzales2);
            tx.commit();
            pm.close();

            // test that Daffy Duck and sgonzales2 were removed
            // ensure that dduck was deleted as it is dependent
            // ensure that Speedy Gonzales exists with no account
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNotNull(speedyGonzales.getAccounts());
            assertEquals(0, speedyGonzales.getAccounts().size());
            try
            {
                daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
                fail("Object 'sgonzales2' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
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
     * Person-(1)----------------------------(N)-Account
     * <ul>
     * <li>The Person class has a Collection<Account> accounts, this relation is mapped-by
     * <li>The Account class has a reference to its Person
     * <li>In LDAP the relation is stored at the *Account* side (attribute seeAlso, single-valued)
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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            AccountWithPassword dduck = new AccountWithPassword("dduck", "secret1", daffyDuck);
            daffyDuck.getAccounts().add(dduck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(-1);
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            AccountWithPassword detachedDduck = pm.detachCopy(dduck);
            Person detachedDaffyDuck = detachedDduck.getOwner();
            tx.commit();
            pm.close();

            // check references
            assertEquals("dduck", detachedDduck.getUid());
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(1, detachedDaffyDuck.getAccounts().size());
            assertEquals(detachedDduck, detachedDaffyDuck.getAccounts().iterator().next());
            assertEquals(detachedDaffyDuck, detachedDduck.getOwner());

            // remove dduck from account list
            detachedDaffyDuck.getAccounts().clear();
            JDOHelper.makeDirty(detachedDaffyDuck, "accounts");

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDduck);
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // fetch and detach
            // ensure that dduck was deleted as it is dependent
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test that dduck was removed from accounts
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(0, detachedDaffyDuck.getAccounts().size());

            // add two accounts
            AccountWithPassword dduck2 = new AccountWithPassword("dduck2", "secret2", null);
            AccountWithPassword sgonzales2 = new AccountWithPassword("sgonzales2", "secret22", null);
            detachedDaffyDuck.getAccounts().add(dduck2);
            detachedDaffyDuck.getAccounts().add(sgonzales2);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            dduck2 = pm.getObjectById(AccountWithPassword.class, "dduck2");
            sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Account detachedDduck2 = pm.detachCopy(dduck2);
            Account detachedSgonzales2 = pm.detachCopy(sgonzales2);
            tx.commit();
            pm.close();

            // test the two accounts are in the account list
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(2, detachedDaffyDuck.getAccounts().size());
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedDduck2));
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedSgonzales2));
            assertEquals(detachedDaffyDuck, detachedDduck2.getOwner());
            assertEquals(detachedDaffyDuck, detachedSgonzales2.getOwner());

            // create a new person and move one account to the new person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null, null);
            speedyGonzales.getAccounts().add(detachedSgonzales2);
            detachedSgonzales2.setOwner(speedyGonzales);
            detachedDaffyDuck.getAccounts().remove(detachedSgonzales2);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(speedyGonzales);
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            dduck2 = pm.getObjectById(AccountWithPassword.class, "dduck2");
            sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Person detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            detachedDduck2 = pm.detachCopy(dduck2);
            detachedSgonzales2 = pm.detachCopy(sgonzales2);
            tx.commit();
            pm.close();

            // test new person was persisted and check account relationships
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(1, detachedDaffyDuck.getAccounts().size());
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedDduck2));
            assertEquals(detachedDaffyDuck, detachedDduck2.getOwner());
            assertNotNull(detachedSpeedyGonzales.getAccounts());
            assertEquals(1, detachedSpeedyGonzales.getAccounts().size());
            assertTrue(detachedSpeedyGonzales.getAccounts().contains(detachedSgonzales2));
            assertEquals(detachedSpeedyGonzales, detachedSgonzales2.getOwner());

            // delete one person and account
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.deletePersistent(detachedDaffyDuck);
            pm.deletePersistent(detachedSgonzales2);
            tx.commit();
            pm.close();

            // test that Daffy Duck and sgonzales2 were removed
            // ensure that dduck was deleted as it is dependent
            // ensure that Speedy Gonzales exists with no account
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNotNull(speedyGonzales.getAccounts());
            assertEquals(0, speedyGonzales.getAccounts().size());
            try
            {
                daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                sgonzales2 = pm.getObjectById(AccountWithPassword.class, "sgonzales2");
                fail("Object 'sgonzales2' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
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
