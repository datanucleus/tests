/**********************************************************************
Copyright (c) 2007 Erik Bengtson and others. All rights reserved.
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
2007 Andy Jefferson - rewritten for test.framework/samples
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.types.interfaces.Circle;
import org.datanucleus.samples.types.interfaces.Rectangle;
import org.datanucleus.samples.types.interfaces.Shape;
import org.datanucleus.samples.types.interfaces.ShapeHolder;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.models.company.Account;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.models.company.Project;

/**
 * Testcases for EntityManagerImpl.
 */
public class EntityManagerTest extends JPAPersistenceTestCase
{
    public EntityManagerTest(String name)
    {
        super(name);
    }

    /**
     * Test of calling tx.begin() twice in a row.
     */
    public void testTransactionBeginTwice()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                try
                {
                    tx.begin();
                    fail("Expected IllegalStateException on tx.begin() twice but got nothing");
                }
                catch (IllegalStateException ise)
                {
                    // Expected
                }
                catch (Exception e)
                {
                    fail("Expected IllegalStateException but got " + e);
                }
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
        }
    }

    /**
     * Test of em.close
     */
    public void testOpenClose()
    {
        try
        {
            EntityManager em = getEM();
            em.close();
            assertFalse("EM should be closed but isn't", em.isOpen());
        }
        finally
        {
        }
    }

    /**
     * Test of EntityManager.persist()
     */
    public void testPersist()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setGlobalNum("First");
                em.persist(p);
                tx.commit();

                try
                {
                    tx.begin();
                    Person p2 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                    p2.setGlobalNum("First");
                    em.persist(p2);
                    tx.commit();
                    fail("Allowed to persist same object twice!");
                }
                catch (EntityExistsException eee)
                {
                    // Expected since the object had been persisted earlier
                }
                catch (PersistenceException eee)
                {
                    // Expected since the object had been persisted earlier
                }
                finally
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }

                try
                {
                    tx.begin();
                    Person p2 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                    p2.setGlobalNum("First");
                    em.persist(p2);
                    tx.commit();
                    fail("Allowed to persist object with same id twice");
                }
                catch (EntityExistsException eee)
                {
                    // Either this or PersistenceException expected
                }
                catch (PersistenceException pe)
                {
                    // Either this or EntityExistsException expected
                }
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.persist() with a Collection
     */
    public void testPersistAll()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                p1.setGlobalNum("First");
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                p2.setGlobalNum("Second");
                Collection people = new HashSet();
                people.add(p1);
                people.add(p2);
                em.persist(people);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            if (emf.getCache() != null)
            {
                emf.getCache().evictAll();
            }

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                Query q = em.createQuery("SELECT p FROM " + Person.class.getName() + " p");
                List<Person> people = q.getResultList();
                assertEquals("Number of Person objects is incorrect", 2, people.size());
                boolean barneyPresent = false;
                boolean fredPresent = false;
                for (Person p : people)
                {
                    if (p.getFirstName().equals("Fred") && p.getLastName().equals("Flintstone"))
                    {
                        fredPresent = true;
                    }
                    else if (p.getFirstName().equals("Barney") && p.getLastName().equals("Rubble"))
                    {
                        barneyPresent = true;
                    }
                }
                assertTrue("Fred not persisted", fredPresent);
                assertTrue("Barney not persisted", barneyPresent);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.merge()
     */
    public void testMerge()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                // Persist an object
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                tx.commit();

                // Merge the detached object
                tx.begin();
                em.merge(p1);
                tx.commit();

                // Merge a new object
                tx.begin();
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.merge(p2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception using merge " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.refresh()
     */
    public void testRefresh()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                // Persist an object
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);

                // Refresh the object (though not yet in the datastore). Should just return
                em.refresh(p1);

                tx.commit();

                try
                {
                    // Refresh a new object
                    tx.begin();
                    Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                    em.refresh(p2);
                    tx.commit();
                    fail("Refresh on non-managed object succeeded!");
                }
                catch (IllegalArgumentException e)
                {
                    // Expected since object is not managed
                }
                finally
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception using refresh " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.find()
     */
    public void testFind()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                Person.PK pk = null;
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                pk = p1.getPK();
                em.persist(p1);
                tx.commit();

                tx.begin();
                Person person = em.find(Person.class, pk);
                assertEquals(p1.getFirstName(), person.getFirstName());
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.remove().
     */
    public void testRemove()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                Person.PK pk = null;
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);

                tx.commit();
                pk = p1.getPK();

                em.getTransaction().begin();

                Person person = em.find(Person.class, pk);
                assertEquals(p1.getFirstName(), person.getFirstName());
                em.remove(person);

                //before flush should be found
                person = em.find(Person.class, pk);
                assertNotNull("before flush should be found", person);
                assertEquals(p1.getPersonNum(), person.getPersonNum());
                assertEquals(p1.getGlobalNum(), person.getGlobalNum());

                //after flush should not be found
                em.flush();
                person = em.find(Person.class, pk);
                assertNull("after flush should not be found", person);

                em.getTransaction().commit();
            }
            finally
            {
                if (em.getTransaction().isActive())
                {
                    em.getTransaction().rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of marking of current txn for rollback on exception.
     */
    public void testMarkForRollbackOnError()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                try
                {
                    em.find(Account.class, new Long(123));
                }
                catch (EntityNotFoundException enfe)
                {
                    if (!tx.getRollbackOnly())
                    {
                        fail("Transaction wasn't marked for rollback after exception on object find()");
                    }
                }

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                em.close();
            }
        }
        finally
        {
            clean(Account.class);
        }
    }

    /**
     * Test of EntityManager.persist() and detach().
     */
    public void testPersistThenDetach()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setGlobalNum("First");
                em.persist(p);
                em.detach(p);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of EntityManager.merge() of a transient object that represents one in the datastore
     */
    public void testMergeOfTransientAsAttach()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            em.setProperty(PropertyNames.PROPERTY_ALLOW_ATTACH_OF_TRANSIENT, "true");
            EntityTransaction tx = em.getTransaction();
            try
            {
                // Persist an object
                tx.begin();
                Project p1 = new Project("DataNucleus Xenon", 125000);
                em.persist(p1);
                tx.commit();

                // Merge the detached object
                Project p2 = new Project("DataNucleus Xenon", 150000);
                tx.begin();
                Project p = em.merge(p2);
                assertEquals("Project budget not merged in returned object", 150000, p.getBudget());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown on merge of transient", e);
                fail("Exception using merge of transient " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
            emf.getCache().evictAll();

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                Project p = (Project)em.find(Project.class, "DataNucleus Xenon");
                assertEquals("Budget is incorrect. Merge didn't succeed", 150000, p.getBudget());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown on retrieval", e);
                fail("Exception using retrieval " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();
        }
        finally
        {
            clean(Project.class);
        }
    }

    /**
     * Test of detaching and access of an undetached field.
     */
    public void testDetachAccessUndetachedField()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            Person p = null;
            Person.PK pk = null;
            try
            {
                tx.begin();
                p = new Person(101, "Billy", "Nomates", "billy.nomates@nowhere.com");
                p.getPhoneNumbers().put("Joey", new org.jpox.samples.annotations.models.company.PhoneNumber("Joey", "+44 123456789"));
                em.persist(p);
                tx.commit();
                pk = p.getPK();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();

            // Retrieve it
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                p = em.find(Person.class, pk);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            em.close();

            em = getEM();

            try
            {
                p.getPhoneNumbers();
            }
            catch (Throwable thr)
            {
                assertTrue(thr instanceof IllegalAccessException);
            }
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test of persistence/retrieval of object with an interface field.
     */
    public void testPersistEntityWithInterfaceField()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                ShapeHolder holder = new ShapeHolder(100);
                holder.setShape1(new Rectangle(200, 45, 55));
                Circle circ = new Circle(300, 25.8);
                holder.getShapeSet1().add(circ);
                em.persist(holder);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Retrieve it
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                ShapeHolder holder = em.find(ShapeHolder.class, 100);
                assertNotNull(holder);
                Shape shape = holder.getShape1();
                assertNotNull(shape);
                assertTrue(shape instanceof Rectangle);
                Rectangle rect = (Rectangle)shape;
                assertEquals(45.0, rect.getWidth(), 0.1);
                assertEquals(55.0, rect.getLength(), 0.1);
                Set<Shape> shapes = holder.getShapeSet1();
                assertNotNull(shapes);
                assertEquals(1, shapes.size());
                Shape setShape = shapes.iterator().next();
                assertTrue(setShape instanceof Circle);
                Circle circ = (Circle)setShape;
                assertEquals(25.8, circ.getRadius(), 0.1);

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(ShapeHolder.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }
}