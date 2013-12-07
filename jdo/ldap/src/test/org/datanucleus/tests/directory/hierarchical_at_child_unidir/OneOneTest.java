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
package org.datanucleus.tests.directory.hierarchical_at_child_unidir;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests hierarchical mapping of N-1 unidirectional relationship, using the following test data:
 * 
 * <pre>
 * ou=AR
 *   cn=Bugs Bunny
 *     uid=bbunny
 *   cn=Ana Hicks
 *     uid=ahicks
 * ou=BR
 *   cn=Lami Puxa
 *     uid=lpuxa
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
        clean(Account.class);
        clean(Person.class);
        clean(OrgUnit.class);
        helper.setUp(pmf);
    }

    protected void tearDown() throws Exception
    {
        clean(Account.class);
        clean(Person.class);
        clean(OrgUnit.class);
        super.tearDown();
    }

    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());

            Account lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(lpuxa.getPerson());
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(lami, lpuxa.getPerson());

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

    private OrgUnit getOrgUnit(PersistenceManager pm)
    {
        OrgUnit ou = pm.getObjectById(OrgUnit.class, "AR");
        return ou;
    }

    public void testPersistParentDoesNotPersistChild()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", getOrgUnit(pm));
            Account dduck = new Account("dduck", "secret", daffyDuck);
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
            assertNotNull(daffyDuck.getOrgUnit());
            try
            {
                pm.getObjectById(Account.class, "dduck");
                fail("Account dduck mustn't be persisted.");
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

    public void testPersistChildOnlyAlsoPersistsParent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", getOrgUnit(pm));
            Account dduck = new Account("dduck", "secret", daffyDuck);
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
            dduck = pm.getObjectById(Account.class, "dduck");
            assertNotNull(dduck.getPerson());
            assertEquals(daffyDuck, dduck.getPerson());
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
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            bbunny.setPassword("too_simple");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            bugs.setFirstName("BBB");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bugs.getFirstName());
            bbunny = pm.getObjectById(Account.class, "bbunny");
            assertEquals("too_simple", bbunny.getPassword());
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

    public void testUpdateFieldDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person detachedBugs = pm.detachCopy(bugs);
            Account detachedBbunny = pm.detachCopy(bbunny);
            tx.commit();
            pm.close();

            detachedBbunny.setPassword("too_simple");
            detachedBugs.setFirstName("BBB");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBbunny);
             pm.makePersistent(detachedBugs);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bugs.getFirstName());
            bbunny = pm.getObjectById(Account.class, "bbunny");
            assertEquals("too_simple", bbunny.getPassword());
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

    public void testUpdateParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            Account lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(lami, lpuxa.getPerson());

            // update parent reference
            bbunny.setPerson(lami);
            lpuxa.setPerson(null);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            bbunny = pm.getObjectById(Account.class, "bbunny");
            assertNotNull(bbunny.getPerson());
            assertEquals(lami, bbunny.getPerson());
            try
            {
                lpuxa = pm.getObjectById(Account.class, "lpuxa");
                fail("Account lpuxa must be deleted.");
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

    public void testUpdateParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            Account lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(lami, lpuxa.getPerson());
            // Person detachedBugs = pm.detachCopy(bugs);
            Person detachedLami = pm.detachCopy(lami);
            Account detachedBbunny = pm.detachCopy(bbunny);
            Account detachedLpuxa = pm.detachCopy(lpuxa);
            tx.commit();
            pm.close();

            // update parent reference
            detachedBbunny.setPerson(detachedLami);
            detachedBbunny.setPassword("abc");
            detachedLpuxa.setPerson(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBbunny);
            pm.makePersistent(detachedLpuxa);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            bbunny = pm.getObjectById(Account.class, "bbunny");
            assertNotNull(bbunny.getPerson());
            assertEquals(lami, bbunny.getPerson());
            assertEquals("abc", bbunny.getPassword());
            try
            {
                lpuxa = pm.getObjectById(Account.class, "lpuxa");
                fail("Account lpuxa must be deleted.");
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

    public void testSwapParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            Account lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(lami, lpuxa.getPerson());

            // swap references
            bbunny.setPerson(lami);
            lpuxa.setPerson(bugs);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            bbunny = pm.getObjectById(Account.class, "bbunny");
            lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(lami, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(bugs, lpuxa.getPerson());
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

    public void testSwapParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            Account lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(lami, lpuxa.getPerson());
            Person detachedBugs = pm.detachCopy(bugs);
            Person detachedLami = pm.detachCopy(lami);
            Account detachedBbunny = pm.detachCopy(bbunny);
            Account detachedLpuxa = pm.detachCopy(lpuxa);
            tx.commit();
            pm.close();

            // swap references
            detachedBbunny.setPerson(detachedLami);
            detachedLpuxa.setPerson(detachedBugs);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBbunny);
            pm.makePersistent(detachedLpuxa);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            bbunny = pm.getObjectById(Account.class, "bbunny");
            lpuxa = pm.getObjectById(Account.class, "lpuxa");
            assertNotNull(bbunny.getPerson());
            assertEquals(lami, bbunny.getPerson());
            assertNotNull(lpuxa.getPerson());
            assertEquals(bugs, lpuxa.getPerson());
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

    public void testDeleteChildByNullOutParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());

            // null out parent reference
            bbunny.setPerson(null);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            // the parent must still exist
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNotNull(bugs.getLastName());
            assertEquals("Bunny", bugs.getLastName());
            // the child must be deleted
            try
            {
                bbunny = pm.getObjectById(Account.class, "bbunny");
                fail("Account bbunny must be deleted.");
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

    public void testDeleteChildByNullOutParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Account bbunny = pm.getObjectById(Account.class, "bbunny");
            assertNotNull(bbunny.getPerson());
            assertEquals(bugs, bbunny.getPerson());
            Account detachedBbunny = pm.detachCopy(bbunny);
            tx.commit();
            pm.close();

            // null out parent reference
            detachedBbunny.setPerson(null);
            detachedBbunny.setPassword("abc");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBbunny);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            // the parent must still exist
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNotNull(bugs.getLastName());
            assertEquals("Bunny", bugs.getLastName());
            // the child must be deleted
            try
            {
                bbunny = pm.getObjectById(Account.class, "bbunny");
                fail("Account bbunny must be deleted.");
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

}
