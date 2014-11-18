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
package org.datanucleus.tests.directory.dn_unidir;

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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            pm.makePersistent(daffyDuck);
            Account dduck = new Account("dduck", "secret1");
            pm.makePersistent(dduck);
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
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());
            randd = pm.getObjectById(Department.class, "R&D");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertTrue(randd.getMembers().isEmpty());
            dduck = pm.getObjectById(Account.class, "dduck");
            assertEquals("dduck", dduck.getUid());
            assertEquals("secret1", dduck.getPassword());
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
            tx.begin();
            Department randd = new Department("R&D");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account dduck = new Account("dduck", "secret1");
            randd.getMembers().add(daffyDuck);
            daffyDuck.getAccounts().add(dduck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(randd);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            dduck = pm.getObjectById(Account.class, "dduck");
            randd = pm.getObjectById(Department.class, "R&D");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNull(daffyDuck.getAddress());
            assertNull(daffyDuck.getComputer());
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(1, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck));
            randd = pm.getObjectById(Department.class, "R&D");
            assertEquals("R&D", randd.getName());
            assertNotNull(randd.getMembers());
            assertEquals(1, randd.getMembers().size());
            assertTrue(randd.getMembers().contains(daffyDuck));
            dduck = pm.getObjectById(Account.class, "dduck");
            assertEquals("dduck", dduck.getUid());
            assertEquals("secret1", dduck.getPassword());
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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Department randd = new Department("R&D");
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

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null);
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

            // create a sales department and move daffy to it
            randd.getMembers().remove(daffyDuck);
            Department sales = new Department("Sales");
            sales.getMembers().add(daffyDuck);
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
            assertNotNull(sales.getMembers());
            assertEquals(1, sales.getMembers().size());
            assertTrue(sales.getMembers().contains(daffyDuck));

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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Department randd = new Department("R&D");
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

            // check relationships
            assertEquals("R&D", detachedRandd.getName());
            assertNotNull(detachedRandd.getMembers());
            assertEquals(1, detachedRandd.getMembers().size());
            assertTrue(detachedRandd.getMembers().contains(detachedDaffyDuck));

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

            // add duffy duck and another person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null);
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

            // create a sales department and move daffy to it
            Department sales = new Department("Sales");
            detachedRandd.getMembers().remove(detachedDaffyDuck);
            sales.getMembers().add(detachedDaffyDuck);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedRandd);
            pm.makePersistent(sales);
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
            assertNotNull(detachedSales.getMembers());
            assertEquals(1, detachedSales.getMembers().size());
            assertTrue(detachedSales.getMembers().contains(detachedDaffyDuck));
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
     * <li>The Person class has a Collection<Account> accounts
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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account dduck = new Account("dduck", "secret1");
            daffyDuck.getAccounts().add(dduck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(Account.class, "dduck");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("dduck", dduck.getUid());
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(1, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck));

            // remove dduck from account list
            daffyDuck.getAccounts().remove(dduck);
            tx.commit();
            pm.close();

            // test that dduck was removed from accounts
            // ensure that dduck was deleted as it is dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(0, daffyDuck.getAccounts().size());
            try
            {
                dduck = pm.getObjectById(Account.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }

            // add two accounts
            Account dduck2 = new Account("dduck2", "secret2");
            Account sgonzales2 = new Account("sgonzales2", "secret22");
            daffyDuck.getAccounts().add(dduck2);
            daffyDuck.getAccounts().add(sgonzales2);
            tx.commit();
            pm.close();

            // test the two accounts are in the account list
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            sgonzales2 = pm.getObjectById(Account.class, "sgonzales2");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(2, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck2));
            assertTrue(daffyDuck.getAccounts().contains(sgonzales2));

            // create a new person and move one account to the new person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null);
            speedyGonzales.getAccounts().add(sgonzales2);
            daffyDuck.getAccounts().remove(sgonzales2); // TODO Triggers cascade-delete since relation doesn't know of assignment!
            pm.makePersistent(speedyGonzales);
            tx.commit();
            pm.close();

            // test new person was persisted and check account relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            sgonzales2 = pm.getObjectById(Account.class, "sgonzales2");
            assertNotNull(daffyDuck.getAccounts());
            assertEquals(1, daffyDuck.getAccounts().size());
            assertTrue(daffyDuck.getAccounts().contains(dduck2));
            assertNotNull(speedyGonzales.getAccounts());
            assertEquals(1, speedyGonzales.getAccounts().size());
            assertTrue(speedyGonzales.getAccounts().contains(sgonzales2));

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
                dduck = pm.getObjectById(Account.class, "sgonzales2");
                fail("Object 'sgonzales2' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                dduck = pm.getObjectById(Account.class, "dduck");
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
     * <li>The Person class has a Collection<Account> accounts
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
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account dduck = new Account("dduck", "secret1");
            daffyDuck.getAccounts().add(dduck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(Account.class, "dduck");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Account detachedDduck = pm.detachCopy(dduck);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // check relationships
            assertEquals("dduck", detachedDduck.getUid());
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(1, detachedDaffyDuck.getAccounts().size());
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedDduck));

            // remove dduck from account list
            detachedDaffyDuck.getAccounts().remove(detachedDduck);
            JDOHelper.makeDirty(detachedDaffyDuck, "accounts");

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
            try
            {
                dduck = pm.getObjectById(Account.class, "dduck");
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
            // ensure that dduck was deleted as it is dependent
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(0, detachedDaffyDuck.getAccounts().size());

            // add two accounts
            Account dduck2 = new Account("dduck2", "secret2");
            Account sgonzales2 = new Account("sgonzales2", "secret22");
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
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            sgonzales2 = pm.getObjectById(Account.class, "sgonzales2");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Account detachedDduck2 = pm.detachCopy(dduck2);
            Account detachedSgonzales2 = pm.detachCopy(sgonzales2);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test the two accounts are in the account list
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(2, detachedDaffyDuck.getAccounts().size());
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedDduck2));
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedSgonzales2));

            // create a new person and move one account to the new person
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", null, null);
            speedyGonzales.getAccounts().add(detachedSgonzales2);
            detachedDaffyDuck.getAccounts().remove(detachedSgonzales2); // TODO Remove will trigger cascade-delete

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.setOptimistic(true);
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
            dduck2 = pm.getObjectById(Account.class, "dduck2");
            sgonzales2 = pm.getObjectById(Account.class, "sgonzales2");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            detachedDduck2 = pm.detachCopy(dduck2);
            detachedSgonzales2 = pm.detachCopy(sgonzales2);
            detachedDaffyDuck = pm.detachCopy(daffyDuck);
            Person detachedSpeedyGonzales = pm.detachCopy(speedyGonzales);
            tx.commit();
            pm.close();

            // test new person was persisted and check account relationships
            assertNotNull(detachedDaffyDuck.getAccounts());
            assertEquals(1, detachedDaffyDuck.getAccounts().size());
            assertTrue(detachedDaffyDuck.getAccounts().contains(detachedDduck2));
            assertNotNull(detachedSpeedyGonzales.getAccounts());
            assertEquals(1, detachedSpeedyGonzales.getAccounts().size());
            assertTrue(detachedSpeedyGonzales.getAccounts().contains(detachedSgonzales2));
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
