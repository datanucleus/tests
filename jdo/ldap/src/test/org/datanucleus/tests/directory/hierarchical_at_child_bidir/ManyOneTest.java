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

import javax.jdo.FetchPlan;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests hierarchical mapping of N-1 bidirectional relationship, using the following test data:
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
        clean(Company.class);
        clean(Department.class);
        clean(Account.class);
        clean(Address.class);
        clean(Person.class);
        helper.setUp(pmf);
    }

    protected void tearDown() throws Exception
    {
        clean(Company.class);
        clean(Department.class);
        clean(Account.class);
        clean(Address.class);
        clean(Person.class);
        super.tearDown();
    }

    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Person bbunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertNotNull(bbunny.getDepartment());
            assertNotNull(bbunny.getDepartment().getCompany());

            Department engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(engineering, bbunny.getDepartment());
            assertNotNull(engineering.getPersons());
            assertEquals(2, engineering.getPersons().size());
            assertTrue(engineering.getPersons().contains(bbunny));

            Company jdo = pm.getObjectById(Company.class, "JDO Inc.");
            assertEquals(jdo, bbunny.getDepartment().getCompany());
            assertNotNull(jdo.getDepartments());
            assertEquals(2, jdo.getDepartments().size());
            assertTrue(jdo.getDepartments().contains(engineering));

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
            Person p1 = new Person("p1", "p1", "p1", null, null, null);
            pm.makePersistent(p1);
            tx.commit();
            helper.ids.add(pm.getObjectId(p1));
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
     * Only the parent object is persisted, the reference to the child object is empty.
     */
    public void testPersistWithNoChild()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Company c = new Company("company");
            pm.makePersistent(c);
            tx.commit();
            helper.ids.add(pm.getObjectId(c));
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            c = pm.getObjectById(Company.class, "company");
            assertNotNull(c.getDepartments());
            assertTrue(c.getDepartments().isEmpty());
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
     * Persist an object with an hierarchical mapped relation. When persisting the parent object the reachable child
     * objects must also be persisted.
     */
    public void testPersistParentOnly()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Company c = new Company("company");
            Department d1 = new Department("dep1", c);
            Department d2 = new Department("dep2", c);
            c.getDepartments().add(d1);
            c.getDepartments().add(d2);
            Person p1 = new Person("p1", "p1", "p1", null, null, d1);
            d1.getPersons().add(p1);
            Person p2 = new Person("p2", "p2", "p2", null, null, d1);
            d1.getPersons().add(p2);
            Person p3 = new Person("p3", "p3", "p3", null, null, d2);
            d2.getPersons().add(p3);
            pm.makePersistent(c);
            tx.commit();
            helper.ids.add(pm.getObjectId(p1));
            helper.ids.add(pm.getObjectId(p2));
            helper.ids.add(pm.getObjectId(p3));
            helper.ids.add(pm.getObjectId(d1));
            helper.ids.add(pm.getObjectId(d2));
            helper.ids.add(pm.getObjectId(c));
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p3 = pm.getObjectById(Person.class, "p3");
            assertNotNull(p3.getDepartment());
            assertNotNull(p3.getDepartment().getCompany());
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
     * Persist an object with an hierarchical mapped relation. When persisting the child object the parent objects must
     * be persisted *before*.
     */
    public void testPersistChildOnly()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Company c = new Company("company");
            Department d1 = new Department("dep1", c);
            Department d2 = new Department("dep2", c);
            c.getDepartments().add(d1);
            c.getDepartments().add(d2);
            Person p1 = new Person("p1", "p1", "p1", null, null, d1);
            d1.getPersons().add(p1);
            Person p2 = new Person("p2", "p2", "p2", null, null, d1);
            d1.getPersons().add(p2);
            Person p3 = new Person("p3", "p3", "p3", null, null, d2);
            d2.getPersons().add(p3);
            pm.makePersistent(p1);
            pm.makePersistent(p2);
            pm.makePersistent(p3);
            tx.commit();
            helper.ids.add(pm.getObjectId(p1));
            helper.ids.add(pm.getObjectId(p2));
            helper.ids.add(pm.getObjectId(p3));
            helper.ids.add(pm.getObjectId(d1));
            helper.ids.add(pm.getObjectId(d2));
            helper.ids.add(pm.getObjectId(c));
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p3 = pm.getObjectById(Person.class, "p3");
            assertNotNull(p3.getDepartment());
            assertNotNull(p3.getDepartment().getCompany());
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
            Person bbunny = pm.getObjectById(Person.class, "Bugs Bunny");
            bbunny.setFirstName("BBB");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            bbunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals("BBB", bbunny.getFirstName());
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

    public void testAddChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup("persons_of_department");
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(2, engineering.getPersons().size());

            // add new child references
            Person dduck = new Person("Daffy", "Duck", "Daffy Duck", null, null, null);
            engineering.getPersons().add(dduck);
            dduck.setDepartment(engineering);
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // now check the new child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("persons_of_department");
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(Person.class, "Daffy Duck");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(3, engineering.getPersons().size());
            assertTrue(engineering.getPersons().contains(dduck));
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

    public void testAddChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup("persons_of_department");
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(2, engineering.getPersons().size());
            Department detachedEngineering = pm.detachCopy(engineering);
            tx.commit();
            pm.close();

            // add new child references
            Person dduck = new Person("Daffy", "Duck", "Daffy Duck", null, null, detachedEngineering);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(dduck);
            tx.commit();
            pm.close();

            // now check the new child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("persons_of_department");
            tx = pm.currentTransaction();
            tx.begin();
            dduck = pm.getObjectById(Person.class, "Daffy Duck");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(3, engineering.getPersons().size());
            assertTrue(engineering.getPersons().contains(dduck));
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
            pm.getFetchPlan().addGroup("persons_of_department");
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(engineering, bugsBunny.getDepartment());

            // set new parent reference
            bugsBunny.setDepartment(sales);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("persons_of_department");
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            sales = pm.getObjectById(Department.class, "Sales");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(sales, bugsBunny.getDepartment());
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertFalse(engineering.getPersons().contains(bugsBunny));
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
            pm.getFetchPlan().addGroup("persons_of_department");
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(engineering, bugsBunny.getDepartment());
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            Department detachedSales = pm.detachCopy(sales);
            tx.commit();
            pm.close();

            // set new parent reference
            detachedBugsBunny.setDepartment(detachedSales);
            detachedBugsBunny.setFirstName("BBB");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugsBunny);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("persons_of_department");
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            sales = pm.getObjectById(Department.class, "Sales");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(sales, bugsBunny.getDepartment());
            assertEquals("BBB", bugsBunny.getFirstName());
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertFalse(engineering.getPersons().contains(bugsBunny));
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

    public void testUpdateChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup("persons_of_department");
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(engineering, bugsBunny.getDepartment());

            // set new child references
            sales.getPersons().add(bugsBunny);
            engineering.getPersons().remove(bugsBunny);
            bugsBunny.setDepartment(sales);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("persons_of_department");
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            sales = pm.getObjectById(Department.class, "Sales");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(sales, bugsBunny.getDepartment());
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertFalse(engineering.getPersons().contains(bugsBunny));
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

    public void testUpdateChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(engineering, bugsBunny.getDepartment());
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            Department detachedSales = pm.detachCopy(sales);
            Department detachedEngineering = pm.detachCopy(engineering);
            tx.commit();
            pm.close();

            // set new child references
            detachedSales.getPersons().add(detachedBugsBunny);
            detachedEngineering.getPersons().remove(detachedBugsBunny);
            detachedBugsBunny.setDepartment(detachedSales);
            detachedBugsBunny.setFirstName("BBB");
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugsBunny);
            tx.commit();
            pm.close();

            // now check the updated reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            sales = pm.getObjectById(Department.class, "Sales");
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(sales, bugsBunny.getDepartment());
            assertEquals("BBB", bugsBunny.getFirstName());
            assertEquals(2, sales.getPersons().size());
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertEquals(1, engineering.getPersons().size());
            assertFalse(engineering.getPersons().contains(bugsBunny));
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
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertTrue(engineering.getPersons().contains(bugsBunny));
            assertEquals(engineering, bugsBunny.getDepartment());
            assertTrue(sales.getPersons().contains(lamiPuxa));
            assertEquals(sales, lamiPuxa.getDepartment());

            // swap references
            bugsBunny.setDepartment(sales);
            lamiPuxa.setDepartment(engineering);
            tx.commit();
            pm.close();

            // now check the swapped references
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            sales = pm.getObjectById(Department.class, "Sales");
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertTrue(engineering.getPersons().contains(lamiPuxa));
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertEquals(engineering, lamiPuxa.getDepartment());
            assertEquals(sales, bugsBunny.getDepartment());
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

    public void testSwapChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertEquals(engineering, bugsBunny.getDepartment());
            assertEquals(sales, lamiPuxa.getDepartment());

            // swap references
            bugsBunny.setDepartment(sales);
            lamiPuxa.setDepartment(engineering);
            sales.getPersons().add(bugsBunny);
            engineering.getPersons().add(lamiPuxa);
            engineering.getPersons().remove(bugsBunny);
            sales.getPersons().remove(lamiPuxa);
            tx.commit();
            pm.close();

            // now check the swapped references
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            sales = pm.getObjectById(Department.class, "Sales");
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertTrue(engineering.getPersons().contains(lamiPuxa));
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertEquals(engineering, lamiPuxa.getDepartment());
            assertEquals(sales, bugsBunny.getDepartment());
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

    public void testSwapReferencesDetached() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Company jdo = pm.getObjectById(Company.class, "JDO Inc.");
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Department sales = pm.getObjectById(Department.class, "Sales");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            Person lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertTrue(engineering.getPersons().contains(bugsBunny));
            assertEquals(engineering, bugsBunny.getDepartment());
            assertTrue(sales.getPersons().contains(lamiPuxa));
            assertEquals(sales, lamiPuxa.getDepartment());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Company detachedJdo = pm.detachCopy(jdo);
            tx.commit();
            pm.close();
    
            // extract objects from parent object
            Department detachedSales = null;
            Department detachedEngineering = null;
            for (Department department : detachedJdo.getDepartments())
            {
                if ("Sales".equals(department.getName()))
                {
                    detachedSales = department;
                }
                if ("Engineering".equals(department.getName()))
                {
                    detachedEngineering = department;
                }
            }
            Person detachedBugsBunny = null;
            Person detachedLamiPuxa = null;
            for (Person person : detachedSales.getPersons())
            {
                if ("Lami Puxa".equals(person.getFullName()))
                {
                    detachedLamiPuxa = person;
                }
            }
            for (Person person : detachedEngineering.getPersons())
            {
                if ("Bugs Bunny".equals(person.getFullName()))
                {
                    detachedBugsBunny = person;
                }
            }
    
            // swap references
            detachedBugsBunny.setDepartment(detachedSales);
            detachedSales.getPersons().add(detachedBugsBunny);
            detachedEngineering.getPersons().remove(detachedBugsBunny);
            detachedLamiPuxa.setDepartment(detachedEngineering);
            detachedSales.getPersons().remove(detachedLamiPuxa);
            detachedEngineering.getPersons().add(detachedLamiPuxa);
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedJdo);
            tx.commit();
            pm.close();

            // now check the swapped references
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            sales = pm.getObjectById(Department.class, "Sales");
            bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            lamiPuxa = pm.getObjectById(Person.class, "Lami Puxa");
            assertEquals(2, engineering.getPersons().size());
            assertEquals(1, sales.getPersons().size());
            assertTrue(engineering.getPersons().contains(lamiPuxa));
            assertTrue(sales.getPersons().contains(bugsBunny));
            assertEquals(engineering, lamiPuxa.getDepartment());
            assertEquals(sales, bugsBunny.getDepartment());
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
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Person bbunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(2, engineering.getPersons().size());

            // set parent reference to null
            bbunny.setDepartment(null);
            tx.commit();
            pm.close();

            // now check the removed child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(1, engineering.getPersons().size());
            // check object doesn't exist
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Object 'Bugs Bunny' should not exist any more!");
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

    public void testNullOutParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(2, engineering.getPersons().size());
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            tx.commit();
            pm.close();

            // set parent reference to null
            detachedBugsBunny.setDepartment(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedBugsBunny);
            tx.commit();
            pm.close();

            // now check the removed child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(1, engineering.getPersons().size());
            // check object doesn't exist
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Object 'Bugs Bunny' should not exist any more!");
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

    public void testRemoveChildReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(2, engineering.getPersons().size());

            // remove a child reference
            engineering.getPersons().remove(bugsBunny);
            bugsBunny.setDepartment(null);
            tx.commit();
            pm.close();

            // now check the removed child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(1, engineering.getPersons().size());
            // check object doesn't exist
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Object 'Bugs Bunny' should not exist any more!");
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

    public void testRemoveChildReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Person bugsBunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(2, engineering.getPersons().size());
            pm.getFetchPlan().setMaxFetchDepth(-1);
            Person detachedBugsBunny = pm.detachCopy(bugsBunny);
            Department detachedEngineering = pm.detachCopy(engineering);
            tx.commit();
            pm.close();

            // remove a child reference
            detachedEngineering.getPersons().remove(detachedBugsBunny);
            // detachedBugsBunny.setDepartment(null);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedEngineering);
            tx.commit();
            pm.close();

            // now check the removed child reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(1, engineering.getPersons().size());
            // check object doesn't exist
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Object 'Bugs Bunny' should not exist any more!");
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

    public void testDeleteReferenceByDeletingObject()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx.begin();
            Department engineering = pm.getObjectById(Department.class, "Engineering");
            Person bbunny = pm.getObjectById(Person.class, "Bugs Bunny");
            assertEquals(2, engineering.getPersons().size());
            assertTrue(engineering.getPersons().contains(bbunny));
            assertEquals(engineering, bbunny.getDepartment());

            // delete object
            pm.deletePersistent(bbunny);
            tx.commit();
            pm.close();

            // now check the removed reference
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            tx.begin();
            engineering = pm.getObjectById(Department.class, "Engineering");
            assertEquals(1, engineering.getPersons().size());
            try
            {
                pm.getObjectById(Person.class, "Bugs Bunny");
                fail("Object 'Bugs Bunny' should not exist any more!");
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
