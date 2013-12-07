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
public class ManyOneTest extends JDOPersistenceTestCase
{
    TestHelper helper = new TestHelper();

    public ManyOneTest(String name)
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

            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());

            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());

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

    public void testPersistParentDoesNotPersistChildren()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            OrgUnit ou = new OrgUnit("ZZ");
            new Person("Daffy", "Duck", "Daffy Duck", ou);
            new Person("Speedy", "Gonzales", "Speedy Gonzales", ou);
            pm.makePersistent(ou);
            helper.ids.add(pm.getObjectId(ou));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ou = pm.getObjectById(OrgUnit.class, "ZZ");
            try
            {
                pm.getObjectById(Person.class, "Daffy Duck");
                fail("Person Daffy Duck mustn't be persisted.");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Person.class, "Speedy Gonzales");
                fail("Person Speedy Gonzales mustn't be persisted.");
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

    public void testPersistChildrenOnlyAlsoPersistsParent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            OrgUnit ou = new OrgUnit("ZZ");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", ou);
            Person speedyGonzales = new Person("Speedy", "Gonzales", "Speedy Gonzales", ou);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(speedyGonzales);
            helper.ids.add(pm.getObjectId(ou));
            helper.ids.add(pm.getObjectId(daffyDuck));
            helper.ids.add(pm.getObjectId(speedyGonzales));
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ou = pm.getObjectById(OrgUnit.class, "ZZ");
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            speedyGonzales = pm.getObjectById(Person.class, "Speedy Gonzales");
            assertNotNull(daffyDuck.getOrgUnit());
            assertEquals(ou, daffyDuck.getOrgUnit());
            assertNotNull(speedyGonzales.getOrgUnit());
            assertEquals(ou, speedyGonzales.getOrgUnit());
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
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            bugs.setFirstName("BBB");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bugs.getFirstName());
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
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person detachedBugs = pm.detachCopy(bugs);
            tx.commit();
            pm.close();

            detachedBugs.setFirstName("BBB");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugs);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bugs.getFirstName());
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());

            // update parent reference
            bugs.setOrgUnit(br);
            lami.setOrgUnit(null);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            br = pm.getObjectById(OrgUnit.class, "BR");
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(br, bugs.getOrgUnit());
            try
            {
                lami = pm.getObjectById(Person.class, "Lami Puxa");
                fail("Person Lami Puxa must be deleted.");
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());
            Person detachedBugs = pm.detachCopy(bugs);
            Person detachedLami = pm.detachCopy(lami);
            OrgUnit detachedBr = pm.detachCopy(br);
            tx.commit();
            pm.close();

            // update parent reference
            detachedBugs.setOrgUnit(detachedBr);
            detachedBugs.setFirstName("BBB");
            detachedLami.setOrgUnit(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugs);
            pm.makePersistent(detachedLami);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            br = pm.getObjectById(OrgUnit.class, "BR");
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(br, bugs.getOrgUnit());
            assertEquals("BBB", bugs.getFirstName());
            try
            {
                lami = pm.getObjectById(Person.class, "Lami Puxa");
                fail("Person Lami Puxa must be deleted.");
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());

            // swap parent reference
            bugs.setOrgUnit(br);
            ana.setOrgUnit(br);
            lami.setOrgUnit(ar);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ar = pm.getObjectById(OrgUnit.class, "AR");
            br = pm.getObjectById(OrgUnit.class, "BR");
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            ana = pm.getObjectById(Person.class, "Ana Hicks");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(br, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(br, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(ar, lami.getOrgUnit());
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());
            Person detachedBugs = pm.detachCopy(bugs);
            Person detachedAna = pm.detachCopy(ana);
            Person detachedLami = pm.detachCopy(lami);
            OrgUnit detachedAr = pm.detachCopy(ar);
            OrgUnit detachedBr = pm.detachCopy(br);
            tx.commit();
            pm.close();

            // swap parent reference
            detachedBugs.setOrgUnit(detachedBr);
            detachedAna.setOrgUnit(detachedBr);
            detachedLami.setOrgUnit(detachedAr);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugs);
            pm.makePersistent(detachedAna);
            pm.makePersistent(detachedLami);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ar = pm.getObjectById(OrgUnit.class, "AR");
            br = pm.getObjectById(OrgUnit.class, "BR");
            bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            ana = pm.getObjectById(Person.class, "Ana Hicks");
            lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(br, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(br, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(ar, lami.getOrgUnit());
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());

            // null out parent reference
            bugs.setOrgUnit(null);
            bugs.setFirstName("BBB");
            lami.setOrgUnit(null);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ar = pm.getObjectById(OrgUnit.class, "AR");
            br = pm.getObjectById(OrgUnit.class, "BR");
            // Ana must still exists
            ana = pm.getObjectById(Person.class, "Ana Hicks");
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Person Bugs Bunny must be deleted.");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Person.class, "Lami Puxa");
                fail("Person Lami Puxa must be deleted.");
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
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            assertNotNull(bugs.getOrgUnit());
            assertEquals(ar, bugs.getOrgUnit());
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            assertNotNull(lami.getOrgUnit());
            assertEquals(br, lami.getOrgUnit());
            Person detachedBugs = pm.detachCopy(bugs);
            Person detachedAna = pm.detachCopy(ana);
            Person detachedLami = pm.detachCopy(lami);
            tx.commit();
            pm.close();

            // null out parent reference
            detachedBugs.setOrgUnit(null);
            detachedLami.setOrgUnit(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugs);
            pm.makePersistent(detachedAna);
            pm.makePersistent(detachedLami);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            ar = pm.getObjectById(OrgUnit.class, "AR");
            br = pm.getObjectById(OrgUnit.class, "BR");
            // Ana must still exists
            ana = pm.getObjectById(Person.class, "Ana Hicks");
            assertNotNull(ana.getOrgUnit());
            assertEquals(ar, ana.getOrgUnit());
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Person Bugs Bunny must be deleted.");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Person.class, "Lami Puxa");
                fail("Person Lami Puxa must be deleted.");
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

    public void testRemoveAllChildrenDoesNotDeleteParent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person bugs = pm.getObjectById(Person.class, "Bugs Bunny");
            Person ana = pm.getObjectById(Person.class, "Ana Hicks");
            Person lami = pm.getObjectById(Person.class, "Lami Puxa");
            pm.deletePersistent(bugs);
            pm.deletePersistent(ana);
            pm.deletePersistent(lami);
            tx.commit();
            pm.close();

            // now check that parents still exist
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            assertEquals("AR", ar.getName());
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");
            assertEquals("BR", br.getName());
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Person Bugs Bunny must be deleted.");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Person.class, "Ana Hicks");
                fail("Person Ana Hicks must be deleted.");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Person.class, "Lami Puxa");
                fail("Person Lami Puxa must be deleted.");
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
