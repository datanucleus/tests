/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
2007 Andy Jefferson - rewritten to new test.framework/samples
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.abstractclasses.AbstractSimpleBase;
import org.jpox.samples.annotations.abstractclasses.ConcreteSimpleSub1;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Manager;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.models.company.Person1;
import org.jpox.samples.annotations.models.company.Person2;
import org.jpox.samples.annotations.models.company.Qualification;
import org.jpox.samples.annotations.one_many.bidir.Animal;
import org.jpox.samples.annotations.one_many.bidir.Farm;
import org.jpox.samples.annotations.one_many.bidir_2.House;
import org.jpox.samples.annotations.one_many.bidir_2.Window;
import org.jpox.samples.annotations.one_many.collection.ListHolder;
import org.jpox.samples.annotations.one_many.collection.PCFKListElement;
import org.jpox.samples.annotations.one_many.unidir_2.ExpertGroupMember;
import org.jpox.samples.annotations.one_many.unidir_2.GroupMember;
import org.jpox.samples.annotations.one_many.unidir_2.MemberDetails;
import org.jpox.samples.annotations.one_many.unidir_2.ModeratedUserGroup;
import org.jpox.samples.annotations.one_many.unidir_2.UserGroup;
import org.jpox.samples.annotations.one_one.bidir.Boiler;
import org.jpox.samples.annotations.one_one.bidir.Timer;
import org.jpox.samples.annotations.one_one.unidir.Login;
import org.jpox.samples.annotations.one_one.unidir.LoginAccount;
import org.jpox.samples.annotations.types.basic.DateHolder;

/**
 * Tests for JPQL.
 */
public class JPQLQueryTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLQueryTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class, Manager.class, Animal.class, Farm.class,
                    House.class, Window.class, Boiler.class, Timer.class, Login.class, LoginAccount.class,
                    UserGroup.class, GroupMember.class, ModeratedUserGroup.class, ExpertGroupMember.class
                });
        }
    }

    public void testBasicIncompleteQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);

                em.createQuery("SELECT T FROM " + Person.class.getName()).getResultList();
                fail("should have thrown an exception");
            }
            catch (PersistenceException e)
            {
                // expected, but would like to eventually have IllegalArgumentException
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
    
    public void testBasicQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);

                List result = em.createQuery("SELECT Object(T) FROM " + Person.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
    
    public void testMaxResultsQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.persist(p2);

                em.flush();
                tx.commit();

                tx.begin();
                List result = em.createQuery(
                    "SELECT T FROM " + Person.class.getName() + " T").setMaxResults(1).getResultList();
                assertEquals(1, result.size());
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

    /*public void testInheritanceQuery()
    {
        EntityManager em = getEM();
        try
        {
            em.getTransaction().begin();
            List result = em.createQuery("SELECT Object(C) FROM org.jpox.samples.jpa.company.BrandMarketing C").getResultList();
            assertEquals(0, result.size());
            BrandMarketing c1 = new BrandMarketing();
            c1.setId("1");
            c1.setName("name1");
            BrandMarketing c2 = new BrandMarketing();
            c2.setId("2");
            c2.setName("name2");
            ProductMarketing c3 = new ProductMarketing();
            c3.setId("3");
            c3.setName("name3");            
            Marketing c4 = new Marketing();
            c4.setId("4");
            c4.setName("name4");            
            em.persist(c1);
            em.persist(c2);
            em.persist(c3);
            em.persist(c4);
            em.flush();
            result = em.createQuery("SELECT Object(C) FROM org.jpox.samples.jpa.company.BrandMarketing C").getResultList();
            assertEquals(2, result.size());
            assertTrue(result.get(0) instanceof BrandMarketing);
            assertTrue(result.get(1) instanceof BrandMarketing);
            result = em.createQuery("SELECT Object(C) FROM org.jpox.samples.jpa.company.Marketing C").getResultList();
            assertEquals(4, result.size());
            assertTrue(result.get(0) instanceof BrandMarketing);
            assertTrue(result.get(1) instanceof BrandMarketing);
            assertTrue(result.get(2) instanceof ProductMarketing);
            assertTrue(result.get(3) instanceof Marketing);
            em.getTransaction().rollback();
        }
        finally
        {
            if (em.getTransaction().isActive())
            {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }*/

    public void testQueryUsingEntityName()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT Object(T) FROM Person_Ann T").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
    
    public void testQueryUsingEntityNameNotYetLoaded()
    {
        EntityManagerFactory emf = TestHelper.getEMF(1, "JPATest", null); // Swap to "JPATest" EMF
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            List result = em.createQuery("SELECT Object(T) FROM Person_Ann T").getResultList();
            assertEquals(0, result.size());
            tx.rollback();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
            emf.close();
        }
    }

    public void testLikeQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T WHERE firstName like '%Fred%'")
                    .getResultList();
                assertEquals(1, result.size());
                result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T WHERE T.firstName like '%Fred%'")
                    .getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testOrderBy()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T ORDER BY T.firstName DESC")
                    .getResultList();
                assertEquals(2, result.size());
                assertEquals("Fred", ((Person) result.get(0)).getFirstName());
                assertEquals("Barney", ((Person) result.get(1)).getFirstName());
                result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T ORDER BY T.firstName ASC").getResultList();
                assertEquals(2, result.size());
                assertEquals("Barney", ((Person) result.get(0)).getFirstName());
                assertEquals("Fred", ((Person) result.get(1)).getFirstName());
                tx.rollback();
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

    public void testIsNull()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName IS NULL")
                    .getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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

    public void testIsNotNull()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName IS NOT NULL").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testIsSomething()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName IS something").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
                fail("Expected exception");
            }
            catch (RuntimeException ex)
            {
                // expected
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

    public void testIsNotSomething()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName IS NOT something").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
                fail("Expected exception");
            }
            catch (RuntimeException ex)
            {
                // expected
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
    
    public void testNotEquals()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName <> 'Fred1' ").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
   
    public void testNoResultExceptionThrown()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName <> 'Fred' ").getSingleResult();
                fail("expected NoResultException");
            }
            catch(NoResultException ex)
            {
                //expected
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
    
    public void testNonUniqueResultExceptionThrown()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.persist(p2);

                em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName <> 'Wilma' ").getSingleResult();
                fail("expected NonUniqueResultException");
            }
            catch(NonUniqueResultException ex)
            {
                //expected
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
     * Test the specification of positional parameters.
     */
    public void testPositionalParameter()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName <> ?1 AND T.firstName = ?2");
                q.setParameter(1, "Fred1");
                q.setParameter(2, "Fred");
                List result = q.getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
     * Test the specification of named parameters with a relation.
     */
    public void testNamedParameterRelation()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where :name > T.globalNum");
                try
                {
                    // This should provoke a compile which will have no value for "name" so may get
                    // NullLiteral > T.globalNum which will be invalid
                    q.setParameter("badName", "Fred1");
                }
                catch (IllegalArgumentException iae)
                {
                    // Expected
                    return;
                }
                catch (Exception e)
                {
                    fail("Unexpected exception thrown when setting parameter " + e.getMessage());
                }
                fail("Allowed to specify parameter with invalid name");
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
     * Test of trying to set a named parameter where the named parameter doesnt exist in the query.
     * Expects an IllegalArgumentException to be thrown
     */
    public void testUnknownParameter()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName = :theName");
                try
                {
                    q.setParameter("otherName", "John");
                }
                catch (IllegalArgumentException iae)
                {
                    // Exception expected since parameter name is wrong
                    return;
                }
                fail("Should have thrown IllegalArgumentException on setting wrong parameter name but didnt");
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
     * Test of trying to set a named parameter where the type used is incorrect.
     * Expects an IllegalArgumentException to be thrown
     */
    public void testParameterWithIncorrectType()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName = :theName");
                try
                {
                    q.setParameter("theName", new Integer(1));
                }
                catch (IllegalArgumentException iae)
                {
                    // Exception expected since parameter name is wrong
                    return;
                }
                fail("Should have thrown IllegalArgumentException on setting parameter with wrong type but didnt");
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
    
    public void testAsQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " AS T").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
     * Test for Inner Join syntax.
     */
    public void testInnerJoinSyntax()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P JOIN P.bestFriend AS B").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P INNER JOIN P.bestFriend AS B").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P INNER JOIN P.bestFriend").getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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

    public void testFetchJoinSyntax()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P JOIN FETCH P.bestFriend AS B").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P INNER JOIN FETCH P.bestFriend AS B").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P INNER JOIN FETCH P.bestFriend").getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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
     * Test for Inner Join 1-1 relation from the owner side.
     */
    public void testInnerJoinOneToOneOwner()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Calentador");
                Timer timer = new Timer("Casio", true, boiler);
                em.persist(timer);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Timer.class.getName() + " T " +
                "INNER JOIN T.boiler B").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Boiler.class);
            clean(Timer.class);
        }
    }

    /**
     * Test for Inner Join 1-1 relation from the non-owner side.
     */
    public void testInnerJoinOneToOneNonOwner()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Calentador");
                Timer timer = new Timer("Casio", true, boiler);
                em.persist(timer);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(B) FROM " + Boiler.class.getName() + " B " +
                "INNER JOIN B.timer T").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Boiler.class);
            clean(Timer.class);
        }
    }

    /**
     * Test for Inner Join 1-N bidirectional FK relation from the owner side.
     */
    public void testInnerJoinOneToManyBiFKOwner()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Farm farm1 = new Farm("High Farm");
                Farm farm2 = new Farm("Low Farm");
                Animal a1 = new Animal("Dog");
                Animal a2 = new Animal("Sheep");
                Animal a3 = new Animal("Cow");
                farm1.getAnimals().add(a1);
                farm1.getAnimals().add(a2);
                farm2.getAnimals().add(a3);
                em.persist(farm1);
                em.persist(farm2);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(F) FROM " + Farm.class.getName() + " F " +
                "INNER JOIN F.animals A").getResultList();
                assertEquals(3, result.size());
                tx.rollback();
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for Inner Join 1-N unidirectional FK relation from the owner side.
     */
    @SuppressWarnings("unchecked")
    public void testInnerJoinOneToManyUniFKOwner()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                UserGroup grp = new UserGroup(101, "JPOX Users");
                GroupMember member1 = new GroupMember(201, "Joe User");
                grp.getMembers().add(member1);
                em.persist(grp);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(G) FROM " + UserGroup.class.getName() + " G " +
                "INNER JOIN G.members M").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(UserGroup.class);
            clean(GroupMember.class);
        }
    }

    /**
     * Test for Inner Join 1-N bidirectional JoinTable relation from the owner side.
     */
    public void testInnerJoinOneToManyBiJoinTableOwner()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                House house = new House(101, "Coronation Street");
                Window window = new Window(200, 400, house);
                house.getWindows().add(window);
                em.persist(house);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(H) FROM " + House.class.getName() + " H " +
                "INNER JOIN H.windows W").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for Inner Join N-1 bidirectional JoinTable relation from the element side.
     */
    public void testInnerJoinManyToOneBiJoinTableElement()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                House house = new House(101, "Coronation Street");
                Window window = new Window(200, 400, house);
                house.getWindows().add(window);
                em.persist(house);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(W) FROM " + Window.class.getName() + " W " +
                "INNER JOIN W.house H").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for join across multiple fields in single join.
     */
    public void testJoinMultipleFields()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                UserGroup grp = new UserGroup(101, "JPOX Users");
                GroupMember member1 = new GroupMember(201, "Joe User");
                MemberDetails memDets = new MemberDetails(301, "Joe", "User");
                member1.setDetails(memDets);
                grp.getMembers().add(member1);
                em.persist(grp);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(G) FROM " + UserGroup.class.getName() + " G " +
                    "INNER JOIN G.members.details D").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(UserGroup.class);
            clean(GroupMember.class);
            clean(MemberDetails.class);
        }
    }

    /**
     * Test for Left Outer Join.
     */
    public void testLeftOuterJoinQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("fred", "yabbadabbadoo");
                acct.setLogin(login);
                em.persist(acct);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(A) FROM " + LoginAccount.class.getName() + " A " +
                    "LEFT OUTER JOIN A.login L " +
                "WHERE L.userName = 'fred'").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Login.class);
            clean(LoginAccount.class);
        }
    }

    /**
     * Test for Left Outer Join with additional ON.
     */
    public void testLeftOuterJoinOnQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("fred", "yabbadabbadoo");
                acct.setLogin(login);
                em.persist(acct);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(A) FROM " + LoginAccount.class.getName() + " A " +
                    "LEFT OUTER JOIN A.login L ON L.userName = 'fred'").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Login.class);
            clean(LoginAccount.class);
        }
    }

    /**
     * Test for multiple levels of field access via identifiers.
     */
    public void testThreeLevelsOfFieldAccess()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Calentador");
                Timer timer = new Timer("Casio", true, boiler);
                boiler.setTimer(timer);
                em.persist(timer);
                em.flush();

                // Should create SQL like
                // SELECT THIS.MODEL FROM JPA_AN_BOILER THIS
                // INNER JOIN JPA_AN_TIMER OTHER1 ON OTHER1.BOILER_ID = THIS.ID
                // INNER JOIN JPA_AN_EQUIPMENT OTHER2 ON OTHER2.ID = OTHER1.ID 
                // WHERE OTHER2.MAKE = <'Seiko'>
                List result = em.createQuery(
                    "Select b.model FROM " + Boiler.class.getName() + " b " +
                "WHERE b.timer.make = 'Seiko'").getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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
            clean(Boiler.class);
            clean(Timer.class);
        }
    }

    /**
     * Test projection of N-1 field.
     */
    public void testProjectionOfManyToOneField()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Farm farm1 = new Farm("High Farm");
                Farm farm2 = new Farm("Low Farm");
                Animal a1 = new Animal("Dog");
                Animal a2 = new Animal("Sheep");
                Animal a3 = new Animal("Cow");
                farm1.getAnimals().add(a1);
                farm1.getAnimals().add(a2);
                farm2.getAnimals().add(a3);
                a1.setFarm(farm1);
                a2.setFarm(farm1);
                a3.setFarm(farm2);
                em.persist(farm1);
                em.persist(farm2);
                em.flush();

                List results = em.createQuery("SELECT a.farm FROM " + Animal.class.getName() + " a ", Farm.class).getResultList();
                assertEquals(3, results.size());
                Object result = results.get(0);
                assertNotNull(result);
                assertTrue("Result is of incorrect type", result instanceof Farm);
                tx.rollback();
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    public void testNotBetweenQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 NOT BETWEEN 1 AND 3").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 NOT BETWEEN 3 AND 4").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testBetweenQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 BETWEEN 1 AND 3").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 BETWEEN 3 AND 4").getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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

    public void testABS()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE ABS(2) = 2").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testSUBSTRING()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE SUBSTRING('erik',2,2) = 'ri'").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE SUBSTRING('erik',2) = 'rik'").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testLOCATE()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE LOCATE('r','erik') = 2").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE LOCATE('i','eriki',5) = 5").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testHaving()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P Group By P.firstName HAVING P.firstName = 'Fred'").getResultList();
                assertEquals(1, result.size());
                assertEquals("Fred", result.get(0).toString());
                tx.rollback();
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

    public void testConcat()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P WHERE P.firstName = concat(:a,:b)").setParameter("a", "Fr").setParameter("b","ed").getResultList();
                assertEquals(1, result.size());
                assertEquals("Fred", result.get(0).toString());
                result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P WHERE P.firstName = concat(:c,concat(:a,:b))").setParameter("a", "r").setParameter("b","ed").setParameter("c", "F").getResultList();
                assertEquals(1, result.size());
                assertEquals("Fred", result.get(0).toString());
                tx.rollback();
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

    public void testTrueFalse()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);

                List result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P WHERE false = True").getResultList();
                assertEquals(0, result.size());
                tx.rollback();
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
    
    public void testNot()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                List result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P WHERE NOT (1 = 0)").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery(
                    "SELECT P.firstName FROM " + Person.class.getName() + " P WHERE NOT (1 = 0 AND 1 = 2)").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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

    public void testNonTransactionalQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                List result = em.createQuery("SELECT Object(P) FROM " + Person.class.getName() + " P").getResultList();
                assertEquals(0, result.size());
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
     * Test of JPQL having case insensitive identifiers.
     */
    public void testCaseInsensitiveIdentifier()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.persist(p2);
                em.flush();
                tx.commit();

                tx.begin();
                List result = em.createQuery(
                    "SELECT DISTINCT Object(P) FROM " + Person.class.getName() + " p").getResultList();
                assertEquals(2, result.size());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query testing case-insensitive identifiers " + e.getMessage());
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
     * Test of JPQL "IN (literal)" syntax.
     */
    public void testInLiterals()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(50);
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(40);
                em.persist(p2);
                Person p3 = new Person(103, "Pebbles", "Flintstone", "pebbles.flintstone@jpox.com");
                p3.setAge(38);
                em.persist(p3);
                em.flush();
                tx.commit();

                tx.begin();
                List result = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.firstName IN ('Fred', 'Pebbles')").getResultList();
                assertEquals(2, result.size());
                tx.commit();

                tx.begin();
                result = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.firstName NOT IN ('Fred', 'Pebbles')").getResultList();
                assertEquals(1, result.size());
                tx.commit();

                tx.begin();
                result = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.age IN (38)").getResultList();
                assertEquals(1, result.size());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query with IN syntax for literals " + e.getMessage());
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
     * Test of JPQL "IN (parameter)" syntax.
     */
    public void testInParameters()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@jpox.com");
                em.persist(p2);
                Person p3 = new Person(103, "Pebbles", "Flintstone", "pebbles.flintstone@jpox.com");
                em.persist(p3);
                p1.setBestFriend(p2);
                p3.setBestFriend(p2);
                p2.setBestFriend(p3);
                em.flush();
                tx.commit();

                tx.begin();
                Query q1 = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.firstName IN (:param1, :param2)");
                q1.setParameter("param1", "Fred");
                q1.setParameter("param2", "Pebbles");
                List result = q1.getResultList();
                assertEquals(2, result.size());
                tx.commit();

                tx.begin();
                Query q2 = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.firstName NOT IN (:param1, :param2)");
                q2.setParameter("param1", "Fred");
                q2.setParameter("param2", "Pebbles");
                result = q2.getResultList();
                assertEquals(1, result.size());
                tx.commit();

                tx.begin();
                Query q3 = em.createQuery(
                    "SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p " + 
                    "WHERE p.firstName IN (:param1)");
                Collection<String> options = new HashSet<String>();
                options.add("Fred");
                options.add("Pebbles");
                q3.setParameter("param1", options);
                result = q3.getResultList();
                assertEquals(2, result.size());
                tx.commit();

                // Now try IN using entities
                tx.begin();
                Query q4 = em.createQuery(
                    "SELECT DISTINCT p FROM " + Person.class.getName() + " p " + 
                    "WHERE p.bestFriend IN (:param1)");
                Collection<Person> friends = new HashSet<Person>();
                friends.add(p2);
                friends.add(p1);
                q4.setParameter("param1", friends);
                result = q4.getResultList();
                assertEquals(2, result.size());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query with IN syntax for literals " + e.getMessage());
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
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Query q = em.createQuery("UPDATE " + Person.class.getName() + " p SET p.bestFriend = NULL");
                q.executeUpdate();
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
            clean(Person.class);
        }
    }

    /**
     * Test of JPQL "MEMBER [OF] (container-expr)" syntax.
     */
    public void testMemberOf()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@jpox.com", 30000f, "1234A");
                Employee e2 = new Employee(102, "Barney", "Rubble", "barney.rubble@jpox.com", 27000f, "1234B");
                Employee e3 = new Employee(103, "George", "Cement", "george.cement@jpox.com", 20000f, "1235C");
                Manager mgr1 = new Manager(100, "Chief", "Rock", "chief.rock@warnerbros.com", 
                    40000.0f, "12345A");
                mgr1.setBestFriend(e1);
                Manager mgr2 = new Manager(106, "Boss", "Blaster", "boss.blaster@warnerbros.com", 
                    40005.0f, "12345B");
                mgr2.setBestFriend(e2);
                mgr1.addSubordinate(e1);
                mgr1.addSubordinate(e2);
                e1.setManager(mgr1);
                e2.setManager(mgr1);
                mgr2.addSubordinate(e3);
                e3.setManager(mgr2);
                em.persist(mgr1);
                em.persist(mgr2);
                em.flush();
                tx.commit();

                tx.begin();
                // Retrieve an Employee to use in MEMBER of queries
                Employee emp = (Employee)em.createQuery("SELECT DISTINCT Object(e) FROM " + 
                    Employee.class.getName() + " e " +
                    "WHERE e.firstName = 'Fred'").getSingleResult();

                // Do a MEMBER OF query using an input parameter
                List result = em.createQuery(
                    "SELECT DISTINCT Object(m) FROM " + Manager.class.getName() + " m " +
                    "WHERE :param MEMBER OF m.subordinates").setParameter("param", emp).getResultList();
                assertEquals(1, result.size()); // Manager 1
                Manager mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", 
                    "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", 
                    "Rock", mgr.getLastName());

                // Do a NOT MEMBER OF query using an input parameter
                result = em.createQuery(
                    "SELECT DISTINCT Object(m) FROM " + Manager.class.getName() + " m " +
                    "WHERE :param NOT MEMBER OF m.subordinates").setParameter("param", emp).getResultList();
                assertEquals(1, result.size()); // Manager 2
                mgr = (Manager)result.get(0);
                assertEquals("Manager returned from NOT MEMBER OF query has incorrect firstName", 
                    "Boss", mgr.getFirstName());
                assertEquals("Manager returned from NOT MEMBER OF query has incorrect lastName", 
                    "Blaster", mgr.getLastName());

                // Do a MEMBER OF query using a field
                result = em.createQuery(
                    "SELECT DISTINCT Object(m) FROM " + Manager.class.getName() + " m " +
                    "WHERE m.bestFriend MEMBER OF m.subordinates").getResultList();
                assertEquals(1, result.size()); // Manager 1
                mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", 
                    "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", 
                    "Rock", mgr.getLastName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query with MEMBER syntax " + e.getMessage());
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
            clean(Manager.class);
            clean(Employee.class);
        }
    }

    public void testQueryNestedCollections()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Query query = em.createQuery(
                "SELECT Object(M) FROM " + Manager.class.getName() + " AS M " +
                ", IN (M.departments) D" +
                ", IN (D.projects) P" +
                "WHERE P.name = 'DN'"
            );
            List result = query.getResultList();

            assertEquals(0, result.size());
            tx.rollback();
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
  

    /**
     * Test of JPQL "MEMBER [OF] (container-expr)" syntax, using JoinTable.
     */
    public void testMemberOfViaUnboundVariableJoinTable()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@jpox.com", 30000f, "1234A");
                Employee e2 = new Employee(102, "Barney", "Rubble", "barney.rubble@jpox.com", 27000f, "1234B");
                Employee e3 = new Employee(103, "George", "Cement", "george.cement@jpox.com", 20000f, "1235C");
                Manager mgr1 = new Manager(100, "Chief", "Rock", "chief.rock@warnerbros.com", 
                    40000.0f, "12345A");
                mgr1.setBestFriend(e1);
                Manager mgr2 = new Manager(106, "Boss", "Blaster", "boss.blaster@warnerbros.com", 
                    40005.0f, "12345B");
                mgr2.setBestFriend(e2);
                mgr1.addSubordinate(e1);
                mgr1.addSubordinate(e2);
                e1.setManager(mgr1);
                e2.setManager(mgr1);
                mgr2.addSubordinate(e3);
                e3.setManager(mgr2);
                em.persist(mgr1);
                em.persist(mgr2);
                em.flush();
                tx.commit();

                tx.begin();
                List result = em.createQuery(
                    "SELECT DISTINCT Object(m) FROM " + Manager.class.getName() + " m," +
                    Employee.class.getName() + " e " +
                    "WHERE e MEMBER OF m.subordinates AND e.firstName = 'Barney'").getResultList();
                assertEquals(1, result.size()); // Manager 1
                Manager mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", 
                    "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", 
                    "Rock", mgr.getLastName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query with MEMBER syntax " + e.getMessage());
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
            clean(Manager.class);
            clean(Employee.class);
        }
    }

    /**
     * Test of JPQL "MEMBER [OF] (container-expr)" syntax, using FK.
     */
    public void testMemberOfViaUnboundVariableForeignKey()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Farm farm1 = new Farm("High Farm");
                Farm farm2 = new Farm("Low Farm");
                Animal a1 = new Animal("Dog");
                Animal a2 = new Animal("Sheep");
                Animal a3 = new Animal("Cow");
                farm1.getAnimals().add(a1);
                farm1.getAnimals().add(a2);
                farm2.getAnimals().add(a3);
                em.persist(farm1);
                em.persist(farm2);
                em.flush();
                tx.commit();

                tx.begin();
                List result = em.createQuery(
                    "SELECT DISTINCT Object(f) FROM " + Farm.class.getName() + " f," +
                    Animal.class.getName() + " a " +
                    "WHERE a MEMBER OF f.animals AND a.name = 'Dog'").getResultList();
                assertEquals(1, result.size()); // "High Farm"
                Farm farm = (Farm)result.get(0);
                assertEquals("Farm returned from MEMBER OF query has incorrect name", 
                    "High Farm", farm.getName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown generating query with MEMBER syntax " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test of simple UPDATE statement then calling getSingleResult().
     * This should throw an IllegalStateException
     */
    public void testUpdateSingleResult()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                Query q = em.createQuery("UPDATE " + Person.class.getName() + " p SET p.emailAddress = :param");
                q.setParameter("param", "fred@flintstones.com");
                try
                {
                    q.getSingleResult();
                }
                catch (IllegalStateException ise)
                {
                    // Expected
                    return;
                }
                fail("Called getSingleResult() on an UPDATE query!");
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
     * Test for use of CURRENT_DATE.
     */
    public void testCurrentDate()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            DateHolder d1 = new DateHolder();
            Calendar cal = Calendar.getInstance();
            cal.set(2006, 11, 01);
            d1.setDateField(cal.getTime());
            em.persist(d1);
            DateHolder d2 = new DateHolder();
            Calendar cal2 = Calendar.getInstance();
            cal2.set(2022, 11, 01);
            d2.setDateField(cal2.getTime());
            em.persist(d2);
            em.flush();

            List result = em.createQuery(
                "SELECT Object(D) FROM " + DateHolder.class.getName() + " D WHERE D.dateField < CURRENT_DATE").getResultList();
            assertEquals(1, result.size());
            tx.rollback();
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

    /**
     * Test for use of LENGTH.
     */
    public void testStringLength()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P WHERE LENGTH(P.firstName) > 3").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
     * Test for use of TRIM.
     */
    public void testStringTrim()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred   ", "   Flintstone", "   fred.flintstone@jpox.com   ");
                em.persist(p);
                em.flush();

                // Leading
                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                "WHERE TRIM(LEADING FROM lastName) = 'Flintstone'").getResultList();
                assertEquals(1, result.size());

                // Trailing
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                "WHERE TRIM(TRAILING FROM firstName) = 'Fred'").getResultList();
                assertEquals(1, result.size());

                // Both
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                "WHERE TRIM(emailAddress) = 'fred.flintstone@jpox.com'").getResultList();
                assertEquals(1, result.size());

                // Both (using keyword)
                result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                "WHERE TRIM(BOTH FROM emailAddress) = 'fred.flintstone@jpox.com'").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
    
    public void testSingleResultWithParams()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT T FROM " + Person.class.getName() + " T where T.firstName = :param");
                q.setParameter("param", "Fred");
                Person p = (Person)q.getSingleResult();
                assertNotNull("Returned object was null!", p);
                assertEquals("First name was wrong", "Fred", p.getFirstName());
                assertEquals("Last name was wrong", "Flintstone", p.getLastName());
            }
            catch(NoResultException ex)
            {
                //expected
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

    public void testResultClassViaConstructor()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                TypedQuery<Person1> q = em.createQuery(
                    "SELECT T.firstName,T.lastName FROM " + Person.class.getName() + " T", Person1.class);
                List<Person1> people = q.getResultList();
                assertNotNull("Returned object was null!", people);
                assertEquals(1, people.size());
                Person1 p = people.get(0);
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
            }
            catch(NoResultException ex)
            {
                //expected
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

    public void testResultClassViaConstructorAndSetters()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                TypedQuery<Person2> q = em.createQuery(
                    "SELECT T.firstName,T.lastName FROM " + Person.class.getName() + " T", Person2.class);
                List<Person2> people = q.getResultList();
                assertNotNull("Returned object was null!", people);
                assertEquals(1, people.size());
                Person2 p = people.get(0);
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
            }
            catch(NoResultException ex)
            {
                //expected
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
     * Test for SIZE function of container field.
     */
    public void testContainerSIZE()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Farm farm1 = new Farm("High Farm");
                Farm farm2 = new Farm("Low Farm");
                Animal a1 = new Animal("Dog");
                Animal a2 = new Animal("Sheep");
                Animal a3 = new Animal("Cow");
                farm1.getAnimals().add(a1);
                farm1.getAnimals().add(a2);
                farm2.getAnimals().add(a3);
                em.persist(farm1);
                em.persist(farm2);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(F) FROM " + Farm.class.getName() + " F WHERE SIZE(animals) > 1").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    public void testTYPE()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                Employee e = new Employee(102, "Barney", "Rubble", "barney.rubble@jpox.com", 10000.0f, "12345");
                em.persist(e);
                em.flush();

                List result = em.createQuery(
                    "SELECT Object(p) FROM " + Person.class.getName() + " p WHERE TYPE(p) <> Employee_Ann").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testCASE()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(105, "Pebbles", "Flintstone", "pebbles.flintstone@datanucleus.org");
                p.setAge(5);
                em.persist(p);
                Employee e = new Employee(102, "Barney", "Rubble", "barney.rubble@jpox.com", 10000.0f, "12345");
                e.setAge(35);
                em.persist(e);
                em.flush();

                List result = em.createQuery(
                    "SELECT p.personNum, CASE WHEN p.age < 20 THEN 'Youth' WHEN p.age >= 20 AND p.age < 50 THEN 'Adult' ELSE 'Old' END" + 
                    " FROM " + Person.class.getName() + " p").getResultList();
                Iterator resultsIter = result.iterator();
                boolean pebbles = false;
                boolean barney = false;
                while (resultsIter.hasNext())
                {
                    Object[] values = (Object[])resultsIter.next();
                    if (((Number)values[0]).intValue() == 105 && values[1].equals("Youth"))
                    {
                        pebbles = true;
                    }
                    if (((Number)values[0]).intValue() == 102 && values[1].equals("Adult"))
                    {
                        barney = true;
                    }
                }
                assertTrue("Pebbles wasn't correct in the Case results", pebbles);
                assertTrue("Barney wasn't correct in the Case results", barney);
                tx.rollback();
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
            clean(Employee.class);
            clean(Person.class);
        }
    }

    public void testINDEX()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ListHolder holder = new ListHolder(1);
                holder.getJoinListPC().add(new PCFKListElement(1, "First"));
                holder.getJoinListPC().add(new PCFKListElement(2, "Second"));
                holder.getJoinListPC().add(new PCFKListElement(3, "Third"));
                em.persist(holder);
                em.flush();

                List result = em.createQuery(
                    "SELECT e.name FROM ListHolder l JOIN l.joinListPC e " +
                    "WHERE INDEX(e) = 1").getResultList();
                assertEquals("Number of records is incorrect", 1, result.size());
                assertEquals("Name of element is incorrect", "Second", result.iterator().next());
                tx.rollback();
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
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }

    public void testBulkDelete()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Timer t = new Timer("Seiko", true, null);
                em.persist(t);
                em.flush();

                Query q = em.createQuery("DELETE FROM " + Timer.class.getName() + " t");
                int number = q.executeUpdate();
                assertEquals(1, number);
                tx.rollback();
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
            clean(Timer.class);
        }
    }

    /**
     * Test of simple DELETE statement. See JIRA "NUCRDBMS-7"
     */
    public void testDeleteWithJoinedInheritance()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                Query q = em.createQuery("DELETE FROM Person_Ann p WHERE p.firstName = 'Fred'");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);
                tx.commit();

                // TODO Check the datastore contents
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
     * Test of simple DELETE statement (where no other tables are involved).
     */
    public void testDeleteSimple()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                House h = new House(15, "The Street");
                em.persist(h);
                em.flush();

                Query q = em.createQuery("DELETE FROM House h");
                int val = q.executeUpdate();
                assertEquals("Number of records deleted by query was incorrect", 1, val);
                tx.commit();

                // TODO Check the datastore contents
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
            clean(House.class);
        }
    }

    /**
     * Test of bulk UPDATE statement with the field being modified in the supertable.
     */
    public void testBulkUpdateFieldInSupertable()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Timer t = new Timer("Seiko", true, null);
                em.persist(t);
                em.flush();

                Query q = em.createQuery("UPDATE " + Timer.class.getName() + " t SET make=\"Sony\" WHERE t.digital = TRUE");
                int number = q.executeUpdate();
                assertEquals(1, number);

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception during BULK UPDATE", e);
                fail("Exception thrown during BULK UPDATE : " + e.getMessage());
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
            clean(Timer.class);
        }
    }

    /**
     * Test of bulk UPDATE statement with the field being modified in the table of the class.
     */
    public void testBulkUpdateFieldInTable()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                Query q = em.createQuery("UPDATE Person_Ann p SET p.emailAddress = :param WHERE p.firstName = 'Fred'");
                q.setParameter("param", "fred@flintstones.com");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);

                tx.commit();

                // TODO Check the datastore contents
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
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
     * Test of bulk UPDATE statement setting a field to null
     */
    public void testBulkUpdateSettingToNull()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            Person.PK pk = null;
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();
                pk = p.getPK();
                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in persist", e);
                fail("Exception thrown on persist : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("UPDATE Person_Ann p SET p.emailAddress = NULL WHERE p.firstName = 'Fred'");
                int val = q.executeUpdate();
                assertEquals("Number of records updated by query was incorrect", 1, val);

                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = em.find(Person.class, pk);
                assertNull(p.getEmailAddress());

                tx.commit();
            }
            catch (Throwable e)
            {
                LOG.error("Exception thrown in bulk update", e);
                fail("Exception thrown on bulk update : " + e.getMessage());
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
     * Test the specification of positional parameters.
     */
    public void testPositionalParameterWithLike()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName LIKE ?1");
                q.setParameter(1, "Fr%");
                List result = q.getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
     * Test querying of mapped superclass.
     */
    public void testQueryOfMappedSuperclass()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ConcreteSimpleSub1 sub1 = new ConcreteSimpleSub1(101);
                sub1.setSub1Field("Sub1 Field");
                sub1.setBaseField("Sub1 Base Field");
                em.persist(sub1);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(T) FROM " + AbstractSimpleBase.class.getName() + " T");
                List result = q.getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
            clean(ConcreteSimpleSub1.class);
        }
    }

    /**
     * Test use of JPA2.1 "FUNCTION".
     */
    public void testFunction()
    {
        if (vendorID == null)
        {
            // Only applies to RDBMS
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                em.persist(p);
                em.flush();

                Query q = em.createQuery(
                    "SELECT p FROM " + Person.class.getName() + " p WHERE FUNCTION('UPPER', p.firstName) = 'FRED'");
                List result = q.getResultList();
                assertEquals(1, result.size());
                tx.rollback();
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
     * Test for TREAT in WHERE clause if we ever support it.
     */
    public void testWhereTREAT()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            bart.addSubordinate(boss);
            bart.addSubordinate(boss2);
            Qualification q1 = new Qualification("q1");
            q1.setPerson(boss);
            Qualification q2 = new Qualification("q2");
            q2.setPerson(boss2);
            em.persist(bart);
            em.persist(homer);
            em.persist(boss);
            em.persist(boss2);
            em.persist(q1);
            em.persist(q2);
            em.flush();

            List<Qualification> results = em.createQuery(
                "SELECT q FROM " + Qualification.class.getName() + " q WHERE (TREAT(person AS Employee_Ann)).serialNo = \"serial 3\"").getResultList();
            assertEquals(1, results.size());
            assertEquals("q1", ((Qualification) results.iterator().next()).getName());

            tx.rollback();
        }
        catch (PersistenceException e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in TREAT WHERE test : " + e.getMessage());
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
}