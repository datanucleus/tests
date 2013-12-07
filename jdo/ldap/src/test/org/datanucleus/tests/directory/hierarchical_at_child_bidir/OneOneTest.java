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
package org.datanucleus.tests.directory.hierarchical_at_child_bidir;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests hierarchical mapping of 1-1 bidirectional relationship, using the following test data:
 * 
 * <pre>
 * o=JDO Inc.
 *   ou=Engineering
 *     cn=Bugs Bunny
 *       uid=bbunny
 *       city=B-City
 *     cn=Ana Hicks
 *       uid=ahicks
 *       city=A-City
 *   ou=Sales
 *     cn=Lami Puxa
 *       uid=lpuxa
 *       city=L-City
 *</pre>
 */
public class OneOneTest extends JDOPersistenceTestCase
{
    TestHelper helper = new TestHelper();

    public OneOneTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(AccountWithPassword.class);
        clean(Account.class);
        clean(Address.class);
        clean(Person.class);
        clean(Department.class);
        clean(Company.class);
        helper.setUp(pmf);
    }

    protected void tearDown() throws Exception
    {
        clean(AccountWithPassword.class);
        clean(Account.class);
        clean(Address.class);
        clean(Person.class);
        clean(Department.class);
        clean(Company.class);
        super.tearDown();
    }

    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs Bunny", bugsBunny.getFullName());
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(bugsBunny.getAddress());

            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            assertEquals("bbunny", bbunny.getUid());
            assertNotNull(bbunny.getPerson());
            assertEquals(bbunny, bugsBunny.getAccount());
            assertEquals(bugsBunny, bbunny.getPerson());

            Address bbAddress = pm.getObjectById(Address.class, "B-City");
            assertEquals("B-City", bbAddress.getCity());
            assertNotNull(bbAddress.getPerson());
            assertEquals(bbAddress, bugsBunny.getAddress());
            assertEquals(bugsBunny, bbAddress.getPerson());

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

    private Department getDepartment(PersistenceManager pm)
    {
        Department department = pm.getObjectById(Department.class, "Engineering");
        return department;
    }

    /**
     * Persist an object with an hierarchical mapped relation.
     */
    public void testPersist()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Address ddAddress = new Address("D-City", "D-Street", null);
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, ddAddress, getDepartment(pm));
            ddAddress.setPerson(daffyDuck);
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            pm.makePersistent(ddAddress);
            helper.ids.add(pm.getObjectId(ddAddress));
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertTrue(daffyDuck.getAccount() instanceof AccountWithPassword);
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertNotNull(daffyDuck.getAddress());
            assertEquals("D-Street", daffyDuck.getAddress().getStreet());
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
     * Only the child object is persisted, the reference to the parent object is null, must fail.
     */
    public void testPersistWithNoParentFails()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account lpuxa = new AccountWithPassword("lpuxa", "secret", null);
            pm.makePersistent(lpuxa);
            helper.ids.add(pm.getObjectId(lpuxa));
            tx.commit();
            fail("Child object couldn't be persistet without an parent!");
        }
        catch (JDOUserException e)
        {
            // expected
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
     * Only the parent object is persisted, the reference to the child object is null.
     */
    public void testPersistWithNoChild()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null, getDepartment(pm));
            pm.makePersistent(daffyDuck);
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
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
     * Persist an object with an hierarchical mapped relation. When persisting the parent object only the reachable
     * child object must also be persisted
     */
    public void testPersistParentOnly()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
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
     * Persist an object with an hierarchical mapped relation. When persisting the child object the parent object must
     * be persisted *before*.
     */
    public void testPersistChildOnly()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(dduck);
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
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

    public void testUpdateField()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs", bugsBunny.getFirstName());
            bugsBunny.setFirstName("BBB");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bugsBunny.getFirstName());
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
     * Update the parent reference of the child object.
     */
    public void testUpdateParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            assertNotNull(dduck.getPerson());
            assertEquals("Daffy Duck", dduck.getPerson().getFullName());

            // set a new person
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            dduck.setPerson(bugsBunny);
            tx.commit();
            helper.ids.remove(pm.getObjectId(bugsBunny));
            helper.ids.add(pm.getObjectId(bugsBunny));
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            assertNotNull(dduck.getPerson());
            assertEquals("Bugs Bunny", dduck.getPerson().getFullName());
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
     * Update the parent reference of the child object.
     */
    public void testUpdateParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
    
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            pm.close();
    
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(-1);
            tx = pm.currentTransaction();
            tx.begin();
            Person detachedBugs = pm.detachCopy(pm.getObjectById(Person.class, "Bugs Bunny"));
            Account detachedDduck = pm.detachCopy(pm.getObjectById(AccountWithPassword.class, "dduck"));
            tx.commit();
            pm.close();
    
            // set a new person
            detachedDduck.setPerson(detachedBugs);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDduck);
            tx.commit();
            pm.close();
    
            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(AccountWithPassword.class, "dduck");
            assertNotNull(dduck.getPerson());
            assertEquals("Bugs Bunny", dduck.getPerson().getFullName());
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
     * Update the child reference of the parent object.
     */
    public void testUpdateChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());

            // set a new account
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            daffyDuck.setAccount(bbunny);
            bbunny.setPerson(daffyDuck);
            tx.commit();
            helper.ids.remove(pm.getObjectId(daffyDuck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertEquals("bbunny", daffyDuck.getAccount().getUid());
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNull(bugsBunny.getAccount());
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
     * Update the child reference of the parent object.
     */
    public void testUpdateChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // create account
            tx.begin();
            Account dduck = new Account("dduck", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            tx.commit();
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            pm.close();
    
            // detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(-1);
            tx = pm.currentTransaction();
            tx.begin();
            Account detachedBbunny = pm.detachCopy(pm.getObjectById(AccountWithPassword.class, "bbunny"));
            Account detachedDduck = pm.detachCopy(pm.getObjectById(Account.class, "dduck"));
            Person detachedDaffyDuck = pm.detachCopy(pm.getObjectById(Person.class, "Daffy Duck"));
            tx.commit();
            pm.close();

            // set a new account
            detachedDaffyDuck.setAccount(detachedBbunny);
            detachedBbunny.setPerson(detachedDaffyDuck);
            detachedDduck.setPerson(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            pm.deletePersistent(detachedDduck);
            tx.commit();
            pm.close();
    
            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            assertEquals("bbunny", daffyDuck.getAccount().getUid());
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNull(bugsBunny.getAccount());
            try
            {
                pm.getObjectById(Account.class, "dduck");
                fail("Object 'dduck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
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
     * Swap the parent reference of the child objects.
     */
    public void testSwapParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            Account ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(bbunny, bugsBunny.getAccount());
            assertEquals(ahicks, anaHicks.getAccount());
            assertEquals(bugsBunny, bbunny.getPerson());
            assertEquals(anaHicks, ahicks.getPerson());

            // swap references
            bbunny.setPerson(anaHicks);
            anaHicks.setAccount(bbunny);
            ahicks.setPerson(bugsBunny);
            bugsBunny.setAccount(ahicks);
            tx.commit();
            helper.ids.remove(pm.getObjectId(bugsBunny));
            helper.ids.add(pm.getObjectId(bugsBunny));
            helper.ids.remove(pm.getObjectId(anaHicks));
            helper.ids.add(pm.getObjectId(anaHicks));
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(ahicks, bugsBunny.getAccount());
            assertEquals(bbunny, anaHicks.getAccount());
            assertEquals(anaHicks, bbunny.getPerson());
            assertEquals(bugsBunny, ahicks.getPerson());
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
     * Swap the parent reference of the child objects.
     */
    public void testSwapParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
    
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            Account ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(bbunny, bugsBunny.getAccount());
            assertEquals(ahicks, anaHicks.getAccount());
            assertEquals(bugsBunny, bbunny.getPerson());
            assertEquals(anaHicks, ahicks.getPerson());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            Person detachedAnaHicks = pm.detachCopy(anaHicks);
            Account detachedBbunny = pm.detachCopy(bbunny);
            Account detachedAhicks = pm.detachCopy(ahicks);
            tx.commit();
            pm.close();
    
            // swap references
            detachedBbunny.setPerson(detachedAnaHicks);
            detachedAnaHicks.setAccount(detachedBbunny);
            detachedAhicks.setPerson(detachedBugsBunny);
            detachedBugsBunny.setAccount(detachedAhicks);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBbunny);
            pm.makePersistent(detachedAhicks);
            tx.commit();
            pm.close();
    
            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(ahicks, bugsBunny.getAccount());
            assertEquals(bbunny, anaHicks.getAccount());
            assertEquals(anaHicks, bbunny.getPerson());
            assertEquals(bugsBunny, ahicks.getPerson());
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
     * Swap the child reference of the parent object.
     */
    public void testSwapChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            Account ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(bbunny, bugsBunny.getAccount());
            assertEquals(ahicks, anaHicks.getAccount());
            assertEquals(bugsBunny, bbunny.getPerson());
            assertEquals(anaHicks, ahicks.getPerson());

            // swap references
            bugsBunny.setAccount(ahicks);
            anaHicks.setAccount(bbunny);
            ahicks.setPerson(bugsBunny);
            bbunny.setPerson(anaHicks);
            tx.commit();
            helper.ids.remove(pm.getObjectId(bugsBunny));
            helper.ids.add(pm.getObjectId(bugsBunny));
            helper.ids.remove(pm.getObjectId(anaHicks));
            helper.ids.add(pm.getObjectId(anaHicks));
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(ahicks, bugsBunny.getAccount());
            assertEquals(bbunny, anaHicks.getAccount());
            assertEquals(anaHicks, bbunny.getPerson());
            assertEquals(bugsBunny, ahicks.getPerson());
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
     * Swap the child reference of the parent object.
     */
    public void testSwapChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
    
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            Account ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(bbunny, bugsBunny.getAccount());
            assertEquals(ahicks, anaHicks.getAccount());
            assertEquals(bugsBunny, bbunny.getPerson());
            assertEquals(anaHicks, ahicks.getPerson());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            Person detachedAnaHicks = pm.detachCopy(anaHicks);
            Account detachedBbunny = pm.detachCopy(bbunny);
            Account detachedAhicks = pm.detachCopy(ahicks);
            tx.commit();
            pm.close();
    
            // swap references
            detachedBbunny.setPerson(detachedAnaHicks);
            detachedAnaHicks.setAccount(detachedBbunny);
            detachedAhicks.setPerson(detachedBugsBunny);
            detachedBugsBunny.setAccount(detachedAhicks);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedAnaHicks);
            pm.makePersistent(detachedBugsBunny);
            tx.commit();
            pm.close();
    
            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            anaHicks = pm.getObjectById(Person.class, "Ana Hicks");
            bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            ahicks = pm.getObjectById(Account.class, "ahicks");
            assertNotNull(bugsBunny.getAccount());
            assertNotNull(anaHicks.getAccount());
            assertNotNull(bbunny.getPerson());
            assertNotNull(ahicks.getPerson());
            assertEquals(ahicks, bugsBunny.getAccount());
            assertEquals(bbunny, anaHicks.getAccount());
            assertEquals(anaHicks, bbunny.getPerson());
            assertEquals(bugsBunny, ahicks.getPerson());
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

    public void testNullOutParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Address ddAddress = new Address("D-City", "D-Street", null);
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, ddAddress, getDepartment(pm));
            ddAddress.setPerson(daffyDuck);
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            pm.makePersistent(ddAddress);
            helper.ids.add(pm.getObjectId(ddAddress));
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());

            // set to null
            daffyDuck.getAccount().setPerson(null);
            daffyDuck.getAddress().setPerson(null);
            tx.commit();
            pm.close();

            // now check deleted reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
            assertNull(daffyDuck.getAddress());
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

    public void testNullOutParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
    
            Address ddAddress = new Address("D-City", "D-Street", null);
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, ddAddress, getDepartment(pm));
            ddAddress.setPerson(daffyDuck);
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            pm.makePersistent(ddAddress);
            helper.ids.add(pm.getObjectId(ddAddress));
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();
    
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();
    
            // set to null
            detachedDaffyDuck.getAccount().setPerson(null);
            detachedDaffyDuck.getAddress().setPerson(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck.getAccount());
            pm.makePersistent(detachedDaffyDuck.getAddress());
            tx.commit();
            pm.close();
    
            // now check deleted reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
            assertNull(daffyDuck.getAddress());
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

    public void testNullOutChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Address ddAddress = new Address("D-City", "D-Street", null);
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, ddAddress, getDepartment(pm));
            ddAddress.setPerson(daffyDuck);
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            pm.makePersistent(ddAddress);
            helper.ids.add(pm.getObjectId(ddAddress));
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());

            // set to null
            daffyDuck.setAccount(null);
            daffyDuck.setAddress(null);
            tx.commit();
            pm.close();

            // now check deleted reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
            assertNull(daffyDuck.getAddress());
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

    public void testNullOutChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
    
            Address ddAddress = new Address("D-City", "D-Street", null);
            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, ddAddress, getDepartment(pm));
            ddAddress.setPerson(daffyDuck);
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            pm.makePersistent(ddAddress);
            helper.ids.add(pm.getObjectId(ddAddress));
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();
    
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();
    
            // set to null
            detachedDaffyDuck.setAccount(null);
            detachedDaffyDuck.setAddress(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();
    
            // now check deleted reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
            assertNull(daffyDuck.getAddress());
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

    public void testDeleteReferenceByDeletingObject()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Account dduck = new AccountWithPassword("dduck", "secret", null);
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", dduck, null, getDepartment(pm));
            dduck.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(dduck);
            helper.ids.add(pm.getObjectId(dduck));
            helper.ids.add(pm.getObjectId(daffyDuck));
            tx.commit();
            pm.close();

            // delete referenced object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAccount());
            pm.deletePersistent(daffyDuck.getAccount());
            tx.commit();
            pm.close();

            // now check deleted reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
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

    public void testDeleteAndAddReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account bbunny = pm.getObjectById(AccountWithPassword.class, "bbunny");
            assertEquals("bbunny", bbunny.getUid());
            assertNotNull(bbunny.getPerson());

            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs Bunny", bugsBunny.getFullName());
            assertNotNull(bugsBunny.getAccount());
            assertEquals(bbunny, bugsBunny.getAccount());

            // delete account
            pm.deletePersistent(bbunny);
            tx.commit();
            pm.close();

            // check account is deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs Bunny", bugsBunny.getFullName());
            assertNull(bugsBunny.getAccount());
            tx.commit();
            pm.close();

            // add account again
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            bbunny = new AccountWithPassword("bbunny", "secret", bugsBunny);
            bugsBunny.setAccount(bbunny);
            pm.makePersistent(bbunny);
            tx.commit();
            pm.close();

            // check account exists
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("Bugs Bunny", bugsBunny.getFullName());
            assertNotNull(bugsBunny.getAccount());
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

}
