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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;

import org.datanucleus.api.jpa.JPAQuery;
import org.datanucleus.samples.annotations.abstractclasses.AbstractSimpleBase;
import org.datanucleus.samples.annotations.abstractclasses.ConcreteSimpleSub1;
import org.datanucleus.samples.annotations.embedded.Job;
import org.datanucleus.samples.annotations.embedded.Processor;
import org.datanucleus.samples.annotations.inherited.SuperUser;
import org.datanucleus.samples.annotations.inherited.User;
import org.datanucleus.samples.annotations.models.company.Account;
import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Manager;
import org.datanucleus.samples.annotations.models.company.Organisation;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.models.company.Person1;
import org.datanucleus.samples.annotations.models.company.Person2;
import org.datanucleus.samples.annotations.models.company.Project;
import org.datanucleus.samples.annotations.models.company.Qualification;
import org.datanucleus.samples.annotations.one_many.bidir.Animal;
import org.datanucleus.samples.annotations.one_many.bidir.Farm;
import org.datanucleus.samples.annotations.one_many.bidir_2.House;
import org.datanucleus.samples.annotations.one_many.bidir_2.Window;
import org.datanucleus.samples.annotations.one_many.collection.ListHolder;
import org.datanucleus.samples.annotations.one_many.collection.PCFKListElement;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinEmbeddedValue;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinKey;
import org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue;
import org.datanucleus.samples.annotations.one_many.unidir_2.ExpertGroupMember;
import org.datanucleus.samples.annotations.one_many.unidir_2.GroupMember;
import org.datanucleus.samples.annotations.one_many.unidir_2.MemberDetails;
import org.datanucleus.samples.annotations.one_many.unidir_2.ModeratedUserGroup;
import org.datanucleus.samples.annotations.one_many.unidir_2.UserGroup;
import org.datanucleus.samples.annotations.one_one.bidir.Boiler;
import org.datanucleus.samples.annotations.one_one.bidir.Timer;
import org.datanucleus.samples.annotations.one_one.unidir.Login;
import org.datanucleus.samples.annotations.one_one.unidir.LoginAccount;
import org.datanucleus.samples.annotations.types.basic.DateHolder;
import org.datanucleus.samples.one_many.map_fk.MapFKHolder;
import org.datanucleus.samples.one_many.map_fk.MapFKValue;
import org.datanucleus.samples.one_many.map_fk.MapFKValueBase;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.types.wrappers.GregorianCalendar;
import org.datanucleus.util.StringUtils;

/**
 * Tests for JPQL "SELECT" queries.
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
                    Person.class, Employee.class, Manager.class, Qualification.class, Organisation.class, 
                    Animal.class, Farm.class,
                    House.class, Window.class, Boiler.class, Timer.class, Login.class, LoginAccount.class,
                    UserGroup.class, GroupMember.class, ModeratedUserGroup.class, ExpertGroupMember.class,
                    MapFKHolder.class, MapFKValue.class, MapFKValueBase.class,
                    MapJoinHolder.class, MapJoinValue.class,
                    Processor.class, Job.class,
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p);

                List result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                em.persist(p2);
                em.flush();

                List result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T").setMaxResults(1).getResultList();
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

    /*public void testInheritanceQuery()
    {
        EntityManager em = getEM();
        try
        {
            em.getTransaction().begin();
            List result = em.createQuery("SELECT C FROM org.datanucleus.samples.jpa.company.BrandMarketing C").getResultList();
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
            result = em.createQuery("SELECT C FROM org.datanucleus.samples.jpa.company.BrandMarketing C").getResultList();
            assertEquals(2, result.size());
            assertTrue(result.get(0) instanceof BrandMarketing);
            assertTrue(result.get(1) instanceof BrandMarketing);
            result = em.createQuery("SELECT C FROM org.datanucleus.samples.jpa.company.Marketing C").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT T FROM Person_Ann T").getResultList();
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
        EntityManagerFactory emf = getEMF(1, "JPATest", null); // Swap to "JPATest" EMF
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            List result = em.createQuery("SELECT T FROM Person_Ann T").getResultList();
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p);
                em.flush();

                List result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T WHERE firstName like '%Fred%'").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T WHERE T.firstName like '%Fred%'").getResultList();
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
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                em.persist(p2);

                List result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T ORDER BY T.firstName DESC").getResultList();
                assertEquals(2, result.size());
                assertEquals("Fred", ((Person) result.get(0)).getFirstName());
                assertEquals("Barney", ((Person) result.get(1)).getFirstName());
                result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T ORDER BY T.firstName ASC").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT T FROM " + Person.class.getName() + " T where T.firstName IS NULL").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery("SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName <> ?1 AND T.firstName = ?2");
                try
                {
                    q.getParameter(3);
                    fail("Exception not thrown when should have been on q.getParameter(int) call");
                }
                catch (IllegalArgumentException iae)
                {
                    // Expected
                }
                catch (Throwable thr)
                {
                    fail("Exception thrown but was wrong type on call to q.getParameter(int) : " + thr.getMessage());
                }
                Set params = q.getParameters();
                assertNotNull(params);
                assertEquals(2, params.size());
                q.setParameter(1, "Fred1");
                q.setParameter(2, "Fred");

                Parameter param1 = q.getParameter(1);
                assertEquals(Integer.valueOf(1), param1.getPosition());
                assertNull(param1.getName());
                Parameter param2 = q.getParameter(2);
                assertEquals(Integer.valueOf(2), param2.getPosition());
                assertNull(param2.getName());

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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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
     * Test use of getParameters when the parameter is in a subquery
     */
    public void testGetParametersWithSubquery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q1 = em.createQuery("SELECT p FROM " + Person.class.getName() + " p where firstName = :nameParam1");
                Set<Parameter<?>> params1 = q1.getParameters();
                assertEquals(1, params1.size());
                assertEquals("nameParam1", params1.iterator().next().getName());

                Query q2 = em.createQuery("SELECT p FROM " + Person.class.getName() + " p where p.personNum IN (SELECT p2.personNum FROM Person p2 WHERE p2.firstName = :nameParam2)");
                Set<Parameter<?>> params2 = q2.getParameters();
                assertEquals(1, params2.size());
                assertEquals("nameParam2", params2.iterator().next().getName());

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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery("SELECT Object(T) FROM " + Person.class.getName() + " T where T.firstName = :theName");
                try
                {
                    q.setParameter("theName", Integer.valueOf(1));
                }
                catch (IllegalArgumentException iae)
                {
                    // Exception expected since parameter type is wrong
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datatnucleus.org");
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

                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                em.persist(p2);
                p1.setBestFriend(p2);
                em.flush();

                result = em.createQuery(
                    "SELECT p FROM " + Person.class.getName() + " p JOIN FETCH p.bestFriend").getResultList();
                assertEquals(1, result.size());

                // TODO Add test for 1-N where we have multiple elements, should get as many rows as elements for each root

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

                List result = em.createQuery("SELECT Object(F) FROM " + Farm.class.getName() + " F INNER JOIN F.animals A").getResultList();
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

                List result = em.createQuery("SELECT G FROM " + UserGroup.class.getName() + " G INNER JOIN G.members M").getResultList();
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

                List result = em.createQuery("SELECT H FROM " + House.class.getName() + " H INNER JOIN H.windows W").getResultList();
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

                List result = em.createQuery("SELECT W FROM " + Window.class.getName() + " W INNER JOIN W.house H").getResultList();
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

                List result = em.createQuery("SELECT G FROM " + UserGroup.class.getName() + " G INNER JOIN G.members.details D").getResultList();
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

                List result = em.createQuery("SELECT A FROM " + LoginAccount.class.getName() + " A LEFT OUTER JOIN A.login L WHERE L.userName = 'fred'").getResultList();
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

                List result = em.createQuery("SELECT A FROM " + LoginAccount.class.getName() + " A LEFT OUTER JOIN A.login L ON L.userName = 'fred'").getResultList();
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
                List result = em.createQuery("SELECT b.model FROM " + Boiler.class.getName() + " b WHERE b.timer.make = 'Seiko'").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 NOT BETWEEN 1 AND 3").getResultList();
                assertEquals(0, result.size());
                result = em.createQuery("SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 NOT BETWEEN 3 AND 4").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 BETWEEN 1 AND 3").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery("SELECT Object(P) FROM " + Person.class.getName() + " P WHERE 2 BETWEEN 3 AND 4").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE ABS(2) = 2").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE SUBSTRING('erik',2,2) = 'ri'").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE SUBSTRING('erik',2) = 'rik'").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE LOCATE('r','erik') = 2").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE LOCATE('i','eriki',5) = 5").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT P.firstName FROM " + Person.class.getName() + " P GROUP BY P.firstName HAVING P.firstName = 'Fred'").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);

                List result = em.createQuery("SELECT P.firstName FROM " + Person.class.getName() + " P WHERE false = True").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                List result = em.createQuery("SELECT P.firstName FROM " + Person.class.getName() + " P WHERE NOT (1 = 0)").getResultList();
                assertEquals(1, result.size());
                result = em.createQuery("SELECT P.firstName FROM " + Person.class.getName() + " P WHERE NOT (1 = 0 AND 1 = 2)").getResultList();
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
                List result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P").getResultList();
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
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                em.persist(p2);
                em.flush();

                List result = em.createQuery("SELECT DISTINCT Object(P) FROM " + Person.class.getName() + " p").getResultList();
                assertEquals(2, result.size());
                tx.rollback();
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
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                p1.setAge(50);
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                p2.setAge(40);
                em.persist(p2);
                Person p3 = new Person(103, "Pebbles", "Flintstone", "pebbles.flintstone@datanucleus.org");
                p3.setAge(38);
                em.persist(p3);
                em.flush();

                List result = em.createQuery("SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p WHERE p.firstName IN ('Fred', 'Pebbles')").getResultList();
                assertEquals(2, result.size());

                result = em.createQuery("SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p WHERE p.firstName NOT IN ('Fred', 'Pebbles')").getResultList();
                assertEquals(1, result.size());

                result = em.createQuery("SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p WHERE p.age IN (38)").getResultList();
                assertEquals(1, result.size());

                // Numbered parameter (non-Collection) as the IN
                Query q = em.createQuery("SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p WHERE p.age IN (?1)");
                q.setParameter(1, 38);
                result = q.getResultList();
                assertEquals(1, result.size());

                // Numbered parameter as the item IN
                q = em.createQuery("SELECT DISTINCT Object(p) FROM " + Person.class.getName() + " p WHERE ?1 IN (p.age)");
                q.setParameter(1, 38);
                result = q.getResultList();
                assertEquals(1, result.size());

                tx.rollback();
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
                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                p1.setAge(30);
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@datanucleus.org");
                p2.setAge(35);
                em.persist(p2);
                Person p3 = new Person(103, "Pebbles", "Flintstone", "pebbles.flintstone@datanucleus.org");
                p3.setAge(3);
                em.persist(p3);
                p1.setBestFriend(p2);
                p3.setBestFriend(p2);
                p2.setBestFriend(p3);
                em.flush();

                Query q1 = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.firstName IN (:param1, :param2)");
                q1.setParameter("param1", "Fred");
                q1.setParameter("param2", "Pebbles");
                Parameter param1 = q1.getParameter("param1");
                assertNotNull(param1);
                assertEquals("param1", param1.getName());
                assertNull(param1.getPosition());
                Parameter param2 = q1.getParameter("param2");
                assertNotNull(param2);
                assertEquals("param2", param2.getName());
                assertNull(param2.getPosition());
                List result = q1.getResultList();
                assertEquals(2, result.size());

                Query q2 = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.firstName NOT IN (:param1, :param2)");
                q2.setParameter("param1", "Fred");
                q2.setParameter("param2", "Pebbles");
                result = q2.getResultList();
                assertEquals(1, result.size());

                // Parameter with single value
                Query q3a = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.firstName IN (:param1)");
                q3a.setParameter("param1", "Pebbles");
                result = q3a.getResultList();
                assertEquals(1, result.size());

                // Parameter with collection of values TODO If we set this param name to "param1" it will try to reuse compilation from previous query above and fail
                Query q3 = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.firstName IN (:collParam)");
                Collection<String> options = new HashSet<String>();
                options.add("Fred");
                options.add("Pebbles");
                q3.setParameter("collParam", options);
                result = q3.getResultList();
                assertEquals(2, result.size());

                Query q3b = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.age NOT IN (:param1)");
                List<Integer> options3b = new ArrayList<Integer>();
                options3b.add(30);
                options3b.add(35);
                q3b.setParameter("param1", options3b);
                result = q3b.getResultList();
                assertEquals(1, result.size());

                // Now try IN using entities
                Query q4 = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.bestFriend IN (:param1)");
                Collection<Person> friends = new HashSet<Person>();
                friends.add(p2);
                friends.add(p1);
                q4.setParameter("param1", friends);
                result = q4.getResultList();
                assertEquals(2, result.size());

                Query q5 = em.createQuery("SELECT DISTINCT p FROM " + Person.class.getName() + " p WHERE p.personNum IN (:param1)");
                List<Long> inList = new ArrayList<Long>();
                inList.add(Long.valueOf(101));
                inList.add(Long.valueOf(102));
                q5.setParameter("param1", inList);
                result = q5.getResultList();
                assertEquals(2, result.size());

                tx.rollback();
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
            removePersonBestFriendRelation();
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
                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org", 30000f, "1234A");
                Employee e2 = new Employee(102, "Barney", "Rubble", "barney.rubble@datanucleus.org", 27000f, "1234B");
                Employee e3 = new Employee(103, "George", "Cement", "george.cement@datanucleus.org", 20000f, "1235C");
                Manager mgr1 = new Manager(100, "Chief", "Rock", "chief.rock@warnerbros.com", 40000.0f, "12345A");
                mgr1.setBestFriend(e1);
                Manager mgr2 = new Manager(106, "Boss", "Blaster", "boss.blaster@warnerbros.com", 40005.0f, "12345B");
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

                // Retrieve an Employee to use in MEMBER of queries
                Employee emp = (Employee)em.createQuery("SELECT DISTINCT e FROM " + Employee.class.getName() + " e WHERE e.firstName = 'Fred'").getSingleResult();

                // Do a MEMBER OF query using an input parameter
                List result = em.createQuery("SELECT DISTINCT m FROM " + Manager.class.getName() + " m " +
                        "WHERE :param MEMBER OF m.subordinates").setParameter("param", emp).getResultList();
                assertEquals(1, result.size()); // Manager 1
                Manager mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", "Rock", mgr.getLastName());

                // Do a NOT MEMBER OF query using an input parameter
                result = em.createQuery("SELECT DISTINCT m FROM " + Manager.class.getName() + " m WHERE :param NOT MEMBER OF m.subordinates").setParameter("param", emp).getResultList();
                assertEquals(1, result.size()); // Manager 2
                mgr = (Manager)result.get(0);
                assertEquals("Manager returned from NOT MEMBER OF query has incorrect firstName", "Boss", mgr.getFirstName());
                assertEquals("Manager returned from NOT MEMBER OF query has incorrect lastName", "Blaster", mgr.getLastName());

                // Do a MEMBER OF query using a field
                result = em.createQuery("SELECT DISTINCT m FROM " + Manager.class.getName() + " m WHERE m.bestFriend MEMBER OF m.subordinates").getResultList();
                assertEquals(1, result.size()); // Manager 1
                mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", "Rock", mgr.getLastName());

                tx.rollback();
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
            removePersonBestFriendRelation();
            removeManagerEmployeeRelation();
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

            Query query = em.createQuery("SELECT M FROM " + Manager.class.getName() + " AS M , IN (M.departments) D, IN (D.projects) P WHERE P.name = 'DN'");
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
                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org", 30000f, "1234A");
                Employee e2 = new Employee(102, "Barney", "Rubble", "barney.rubble@datanucleus.org", 27000f, "1234B");
                Employee e3 = new Employee(103, "George", "Cement", "george.cement@datanucleus.org", 20000f, "1235C");
                Manager mgr1 = new Manager(100, "Chief", "Rock", "chief.rock@warnerbros.com", 40000.0f, "12345A");
                mgr1.setBestFriend(e1);
                Manager mgr2 = new Manager(106, "Boss", "Blaster", "boss.blaster@warnerbros.com", 40005.0f, "12345B");
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

                List result = em.createQuery("SELECT DISTINCT m FROM " + Manager.class.getName() + " m," + Employee.class.getName() + " e " +
                    "WHERE e MEMBER OF m.subordinates AND e.firstName = 'Barney'").getResultList();
                assertEquals(1, result.size()); // Manager 1
                Manager mgr = (Manager)result.get(0);
                assertEquals("Manager returned from MEMBER OF query has incorrect firstName", "Chief", mgr.getFirstName());
                assertEquals("Manager returned from MEMBER OF query has incorrect lastName", "Rock", mgr.getLastName());
                tx.rollback();
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
            removePersonBestFriendRelation();
            removeManagerEmployeeRelation();
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

                List result = em.createQuery(
                    "SELECT DISTINCT f FROM " + Farm.class.getName() + " f," + Animal.class.getName() + " a WHERE a MEMBER OF f.animals AND a.name = 'Dog'").getResultList();
                assertEquals(1, result.size()); // "High Farm"
                Farm farm = (Farm)result.get(0);
                assertEquals("Farm returned from MEMBER OF query has incorrect name", "High Farm", farm.getName());
                tx.rollback();
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
     * Test for use of CURRENT_DATE.
     */
    public void testCurrentDate()
    {
        try
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
                cal2.set(2030, 11, 01);
                d2.setDateField(cal2.getTime());
                em.persist(d2);
                em.flush();

                List result = em.createQuery("SELECT D FROM " + DateHolder.class.getName() + " D WHERE D.dateField < CURRENT_DATE").getResultList();
                assertEquals(1, result.size());
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(DateHolder.class);
        }
    }

    /**
     * Test for use of JDBC escape syntax literal.
     */
    @SuppressWarnings("deprecation")
    public void testJdbcEscapeSyntaxLiteral()
    {
        try
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
                d1.setDateField3(cal.getTime());
                em.persist(d1);
                DateHolder d2 = new DateHolder();
                Calendar cal2 = Calendar.getInstance();
                cal2.set(2022, 11, 01);
                d2.setDateField(cal2.getTime());
                d2.setDateField3(cal2.getTime());
                em.persist(d2);
                em.flush();

                List<DateHolder> result = em.createQuery("SELECT D FROM " + DateHolder.class.getName() + " D WHERE D.dateField < {d '2008-01-01'}").getResultList();
                assertEquals(1, result.size());
                DateHolder holder = result.get(0);
                Date holderDate = holder.getDateField();
                assertNotNull(holderDate);
                assertEquals(106, holderDate.getYear()); // 2006
                assertEquals(11, holderDate.getMonth()); // 11

                List<DateHolder> result2 = em.createQuery("SELECT D FROM " + DateHolder.class.getName() + " D WHERE D.dateField3 < {ts '2008-01-01 10:04:00.0'}").getResultList();
                assertEquals(1, result2.size());
                DateHolder holder2 = result2.get(0);
                Date holderDate2 = holder2.getDateField();
                assertNotNull(holderDate2);
                assertEquals(106, holderDate2.getYear()); // 2006
                assertEquals(11, holderDate2.getMonth()); // 11

                // TODO Add test for "t" syntax

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(DateHolder.class);
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p);
                em.flush();

                List result = em.createQuery("SELECT P FROM " + Person.class.getName() + " P WHERE LENGTH(P.firstName) > 3").getResultList();
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

                Person p = new Person(101, "Fred   ", "   Flintstone", "   fred.flintstone@datanucleus.org   ");
                em.persist(p);
                em.flush();

                // Leading
                List result = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TRIM(LEADING FROM p.lastName) = 'Flintstone'").getResultList();
                assertEquals(1, result.size());

                // Trailing
                result = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TRIM(TRAILING FROM p.firstName) = 'Fred'").getResultList();
                assertEquals(1, result.size());

                // Both
                result = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TRIM(p.emailAddress) = 'fred.flintstone@datanucleus.org'").getResultList();
                assertEquals(1, result.size());

                // Both (using keyword)
                result = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TRIM(BOTH FROM p.emailAddress) = 'fred.flintstone@datanucleus.org'").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery("SELECT T FROM " + Person.class.getName() + " T where T.firstName = :param");
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                TypedQuery<Person1> q = em.createQuery("SELECT T.firstName,T.lastName FROM " + Person.class.getName() + " T", Person1.class);
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                TypedQuery<Person2> q = em.createQuery("SELECT T.firstName,T.lastName FROM " + Person.class.getName() + " T", Person2.class);
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

    public void testResultClassViaTuple()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                TypedQuery<Tuple> q = em.createQuery("SELECT T.firstName,T.lastName FROM " + Person.class.getName() + " T", Tuple.class);
                List<Tuple> results = q.getResultList();
                assertNotNull("Returned object was null!", results);
                assertEquals(1, results.size());
                Tuple result = results.get(0);
                List<TupleElement<?>> resultElems = result.getElements();
                assertEquals(2, resultElems.size());
                assertEquals("firstName", resultElems.get(0).getAlias());
                assertEquals("lastName", resultElems.get(1).getAlias());
                Object val0 = result.get(0);
                assertEquals("Fred", val0);
                Object val1 = result.get(1);
                assertEquals("Flintstone", val1);
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

                List result = em.createQuery("SELECT F FROM " + Farm.class.getName() + " F WHERE SIZE(animals) > 1").getResultList();
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

    /**
     * Test for result aggregates.
     */
    public void testResultAggregate()
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

                Object result = em.createQuery("SELECT COUNT(F) FROM " + Farm.class.getName() + " F").getSingleResult();
                assertTrue(result instanceof Long);
                assertEquals(2, ((Long)result).longValue());
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p);
                Employee e = new Employee(102, "Barney", "Rubble", "barney.rubble@datanucleus.org", 10000.0f, "12345");
                em.persist(e);
                em.flush();

                List<Person> result = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) <> Employee_Ann").getResultList();
                assertEquals(1, result.size());
                Person p1 = result.get(0);
                assertTrue(p1 instanceof Person && !(p1 instanceof Employee));

                List result2 = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) IN (Employee_Ann, Person_Ann)").getResultList();
                assertEquals(2, result2.size());

                List<Person> result3 = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) IN (Employee_Ann)").getResultList();
                assertEquals(1, result3.size());
                Person p3 = result3.get(0);
                assertTrue(p3 instanceof Employee);

                List<Person> result4 = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) NOT IN (Employee_Ann)").getResultList();
                assertEquals(1, result4.size());
                Person p4 = result4.get(0);
                assertTrue(p4 instanceof Person && !(p4 instanceof Employee));

                Collection<String> collParam = new ArrayList<>();
                collParam.add("Employee_Ann");
                collParam.add("Person_Ann");
                TypedQuery<Person> q5 = (TypedQuery<Person>) em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) IN :collParam");
                q5.setParameter("collParam", collParam);
                List<Person> result5 = q5.getResultList();
                assertEquals(2, result5.size());

                Collection<Class> collParam2 = new ArrayList<>();
                collParam2.add(Employee.class);
                collParam2.add(Person.class);
                TypedQuery<Person> q6 = (TypedQuery<Person>) em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE TYPE(p) IN :collParam");
                q6.setParameter("collParam", collParam2);
                List<Person> result6 = q6.getResultList();
                assertEquals(2, result6.size());

                // Test for TYPE using a discriminator sample

                User u = new User(1, "Basic User");
                em.persist(u);
                SuperUser su = new SuperUser(2, "Root", "Admin");
                em.persist(su);
                em.flush();

                List<Object[]> result7 = em.createQuery("SELECT u.name, TYPE(u) FROM " + User.class.getName() + " u ORDER BY u.id").getResultList();
                assertEquals(2, result7.size());
                Object[] result7_0 = result7.get(0);
                Object[] result7_1 = result7.get(1);
                assertEquals(2, result7_0.length);
                assertEquals("Basic User", result7_0[0]);
                assertEquals("User", result7_0[1]);
                assertEquals("Root", result7_1[0]);
                assertEquals("SuperUser", result7_1[1]);

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception in TYPE handling : " + e.getMessage());
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
                Employee e = new Employee(106, "Barney", "Rubble", "barney.rubble@datanucleus.org", 10000.0f, "12345");
                e.setAge(35);
                em.persist(e);
                em.flush();

                List result = em.createQuery("SELECT p.personNum, CASE WHEN p.age < :param1 THEN 'Youth' WHEN p.age >= :param1 AND p.age < :param2 THEN 'Adult' ELSE 'Old' END" + 
                        " FROM " + Person.class.getName() + " p").setParameter("param1", 20).setParameter("param2", 50).getResultList();
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
                    if (((Number)values[0]).intValue() == 106 && values[1].equals("Adult"))
                    {
                        barney = true;
                    }
                }
                assertTrue("Pebbles wasn't correct in the Case results", pebbles);
                assertTrue("Barney wasn't correct in the Case results", barney);
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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

    public void testCASESimple()
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
                Employee e = new Employee(106, "Barney", "Rubble", "barney.rubble@datanucleus.org", 10000.0f, "12345");
                e.setAge(35);
                em.persist(e);
                em.flush();

                List result = em.createQuery("SELECT p.personNum, CASE p.age WHEN 5 THEN '5-yr old' ELSE 'Other' END FROM " + Person.class.getName() + " p").getResultList();
                assertEquals("Number of results is incorrect", 2, result.size());
                Iterator resultsIter = result.iterator();
                boolean pebbles = false;
                boolean barney = false;
                while (resultsIter.hasNext())
                {
                    Object[] values = (Object[])resultsIter.next();
                    int idValue = ((Number)values[0]).intValue();
                    String caseValue = (String)values[1];
                    if (idValue == 105 && caseValue.equals("5-yr old"))
                    {
                        pebbles = true;
                    }
                    if (idValue == 106 && (caseValue.equals("Other") || caseValue.equals("Other   "))) // HSQL 2.x pads the string to the same length (i.e CHAR not VARCHAR)
                    {
                        barney = true;
                    }
                }
                assertTrue("Pebbles wasn't correct in the Case results", pebbles);
                assertTrue("Barney wasn't correct in the Case results", barney);
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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

                List result = em.createQuery("SELECT e.name FROM ListHolder l JOIN l.joinListPC e WHERE INDEX(e) = 1").getResultList();
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

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery("SELECT T FROM " + Person.class.getName() + " T where T.firstName LIKE ?1");
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

                Query q = em.createQuery("SELECT T FROM " + AbstractSimpleBase.class.getName() + " T");
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
        if (rdbmsVendorID == null)
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

                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p);
                em.flush();

                Query q = em.createQuery("SELECT p FROM " + Person.class.getName() + " p WHERE FUNCTION('UPPER', p.firstName) = 'FRED'");
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
     * Test for TREAT in WHERE clause.
     */
    public void testWhereTREAT()
    {
        try
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
        finally
        {
            removeManagerEmployeeRelation();
            clean(Qualification.class);
            clean(Manager.class);
        }
    }

    /**
     * Test for TREAT in FROM clause.
     */
    public void testFromTREAT()
    {
        try
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
                    "SELECT q FROM " + Qualification.class.getName() + " q JOIN TREAT(q.person AS " + Employee.class.getName() + ") e WHERE e.serialNo = \"serial 3\"").getResultList();
                assertEquals(1, results.size());
                assertEquals("q1", ((Qualification) results.iterator().next()).getName());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in TREAT FROM test : " + e.getMessage());
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
            removeManagerEmployeeRelation();
            clean(Qualification.class);
            clean(Manager.class);
        }
    }

    /**
     * Test for named queries.
     */
    public void testNamedQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@gmail.com");
                em.persist(p1);

                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@gmail.com");
                em.persist(p2);

                em.flush();

                Query q1 = em.createNamedQuery("PeopleOfName");
                q1.setParameter("name", "Fred");
                List<Person> results1 = q1.getResultList();
                assertEquals(1, results1.size());
                assertEquals("Flintstone", ((Person)results1.iterator().next()).getLastName());

                q1.setParameter("name", "Barney");
                results1 = q1.getResultList();
                assertEquals(1, results1.size());
                assertEquals("Rubble", ((Person)results1.iterator().next()).getLastName());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in Named Query test : " + e.getMessage());
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
     * Test result using constructor syntax ("new class(args)").
     */
    public void testResultWithConstructor()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org");
                em.persist(p1);
                em.flush();

                Query q = em.createQuery("SELECT NEW " + Person1.class.getName() + "(p.firstName,p.lastName) FROM " + Person.class.getName() + " p");
                List results = q.getResultList();
                assertEquals(1, results.size());
                Object result = results.get(0);
                assertNotNull(result);
                assertTrue("Result is of incorrect type", result instanceof Person1);
                Person1 p = (Person1)result;
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
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
     * Test for multiple date params.
     */
    public void testMultipleDateParameters()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                DateHolder d1 = new DateHolder();
                Calendar cal1 = GregorianCalendar.getInstance();
                cal1.set(Calendar.YEAR, 2012);
                cal1.set(Calendar.MONTH, 3);
                cal1.set(Calendar.DAY_OF_MONTH, 0);
                cal1.set(Calendar.HOUR_OF_DAY, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal1.set(Calendar.SECOND, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                d1.setDateField(cal1.getTime());
                Calendar cal2 = GregorianCalendar.getInstance();
                cal2.set(Calendar.YEAR, 2001);
                cal2.set(Calendar.MONTH, 2);
                cal2.set(Calendar.DAY_OF_MONTH, 0);
                cal2.set(Calendar.HOUR_OF_DAY, 0);
                cal2.set(Calendar.MINUTE, 0);
                cal2.set(Calendar.SECOND, 0);
                cal2.set(Calendar.MILLISECOND, 0);
                d1.setDateField2(cal2.getTime());
                em.persist(d1);

                em.flush();

                Query q = em.createQuery("SELECT d FROM " + DateHolder.class.getName() + " d WHERE d.dateField = :date1 AND d.dateField2 = :date2");
                q.setParameter("date1", cal1.getTime());
                q.setParameter("date2", cal2.getTime());
                List<DateHolder> results = q.getResultList();
                assertEquals(1, results.size());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in multiple Date parameter test : " + e.getMessage());
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
            clean(DateHolder.class);
        }
    }

    /**
     * Test for date methods.
     */
    public void testDateMethods()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                DateHolder d1 = new DateHolder();
                Calendar cal1 = GregorianCalendar.getInstance();
                cal1.set(Calendar.YEAR, 2012);
                cal1.set(Calendar.MONTH, 3);
                cal1.set(Calendar.DAY_OF_MONTH, 3);
                cal1.set(Calendar.HOUR_OF_DAY, 0);
                cal1.set(Calendar.MINUTE, 0);
                cal1.set(Calendar.SECOND, 0);
                cal1.set(Calendar.MILLISECOND, 0);
                d1.setDateField(cal1.getTime());
                Calendar cal2 = GregorianCalendar.getInstance();
                cal2.set(Calendar.YEAR, 2001);
                cal2.set(Calendar.MONTH, 2);
                cal2.set(Calendar.DAY_OF_MONTH, 1);
                cal2.set(Calendar.HOUR_OF_DAY, 0);
                cal2.set(Calendar.MINUTE, 0);
                cal2.set(Calendar.SECOND, 0);
                cal2.set(Calendar.MILLISECOND, 0);
                d1.setDateField2(cal2.getTime());
                em.persist(d1);

                em.flush();

                Query q = em.createQuery("SELECT YEAR(d.dateField), MONTH(d.dateField), DAY(d.dateField) FROM " + DateHolder.class.getName() + " d");
                List<Object[]> results = q.getResultList();
                assertEquals(1, results.size());
                Object[] row = results.get(0);
                assertEquals(3, row.length);
                assertEquals(2012, row[0]);
                assertEquals(4, row[1]);
                assertEquals(3, row[2]);

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in date methods test : " + e.getMessage());
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
            clean(DateHolder.class);
        }
    }

    /**
     * Test for multiple joins finishing at the candidate again
     */
    public void testMultipleJoinsAndFieldAccess()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                String query = "Select d From Department d JOIN d.projects p JOIN d.manager m";
                em.createQuery(query).getResultList();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test with multiple joins", e);
                fail("Exception in test using multiple joins : " + e.getMessage());
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
     * Test for KEY and VALUE use with Map field.
     */
    public void testMapFKWithKEYandVALUE()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MapFKHolder holder = new MapFKHolder(1);
                holder.setName("First Holder");
                for (int i=0;i<3;i++)
                {
                    MapFKValue val1 = new MapFKValue("Key" + i, "Map value " + i, "Some description " + i);
                    val1.setHolder(holder);
                    val1.setId(100+i);
                    holder.getMap().put(val1.getKey(), val1);
                }
                em.persist(holder);

                em.flush();

/**
This will generate:

QueryCompilation:
  [result:PrimaryExpression{h.name},PrimaryExpression{m#VALUE.name}]
  [from:ClassExpression(alias=h join=
    JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map} alias=m})]
  [filter:DyadicExpression{PrimaryExpression{m#KEY}  =  Literal{Key1}}]
  [symbols: 
    m#VALUE type=org.datanucleus.samples.one_many.map_fk.MapFKValue, 
    h type=org.datanucleus.samples.one_many.map_fk.MapFKHolder, 
    m#KEY type=java.lang.String, 
    m type=org.datanucleus.samples.one_many.map_fk.MapFKValue]

SELECT H."NAME",M."NAME" 
FROM JPA_MAP_FK_HOLDER H 
LEFT OUTER JOIN JPA_MAP_FK_VALUE M ON H.ID = M.HOLDER_ID 
WHERE M."KEY" = 'Key1'
 */
                List<Object[]> results = em.createQuery("SELECT h.name, VALUE(m).name FROM " + MapFKHolder.class.getName() + " h LEFT OUTER JOIN h.map m WHERE KEY(m) = 'Key1'").getResultList();
                assertNotNull(results);
                assertEquals(1, results.size());
                Object[] resultRow = results.get(0);
                assertEquals(2, resultRow.length);
                assertEquals("First Holder", resultRow[0]);
                assertEquals("Map value 1", resultRow[1]);

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(MapFKValue.class);
            clean(MapFKHolder.class);
        }
    }

    /**
     * Test for KEY and VALUE use with Map field.
     */
    public void testMapJoinWithKEYandVALUE()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MapJoinHolder holder = new MapJoinHolder(1);
                holder.setName("First Holder");
                for (int i=0;i<3;i++)
                {
                    MapJoinValue val = new MapJoinValue(i, "Map value " + i, "Some description " + i);
                    holder.getMap().put("Key" + i, val);
                }
                em.persist(holder);
                em.flush();

/**
This will generate:

QueryCompilation:
  [result:PrimaryExpression{m#VALUE}]
  [from:ClassExpression(alias=h join=JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map} alias=m on=DyadicExpression{PrimaryExpression{m#KEY}  =  ParameterExpression{key}}})]
  [symbols: 
    m#VALUE type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue,
    h type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    m#KEY type=java.lang.String, 
    m type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue,
    key type=java.lang.String]

SELECT M.DESCRIPTION,M.ID,M."NAME" 
FROM JPA_AN_MAPJOINHOLDER H 
LEFT OUTER JOIN JPA_AN_MAPJOINHOLDER_MAP M_MAP ON H.ID = M_MAP.MAPJOINHOLDER_ID
LEFT OUTER JOIN JPA_AN_MAPJOINVALUE M ON M_MAP.MAP_ID = M.ID AND M_MAP.MAP_KEY = ?
 */
                TypedQuery<MapJoinValue> q = em.createQuery("SELECT VALUE(m) AS x FROM MapJoinHolder h LEFT JOIN h.map m ON KEY(m) = :key", MapJoinValue.class);
                q.setParameter("key", "Key2");
                List<MapJoinValue> results = q.getResultList();

                assertNotNull(results);
                assertEquals(3, results.size()); // TODO This should be 1, but we need to apply the ON clause on the join to the join table, not to the value table [rdbms-177]
/*                MapJoinValue resultVal = results.get(0);
                assertEquals("Map value 2", resultVal.getName());
                assertEquals("Some description 2", resultVal.getDescription());*/

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(MapJoinHolder.class);
            clean(MapJoinValue.class);
        }
    }

    /**
     * Test for KEY and VALUE use with Map<Integer, String> field.
     */
    public void testMapJoinNonPCWithKEYandVALUE()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MapJoinHolder holder = new MapJoinHolder(2);
                holder.setName("First Holder");
                for (int i=0;i<3;i++)
                {
                    holder.getMap2().put(i, "Val" + i);
                }
                em.persist(holder);
                em.flush();

/**
This will generate

QueryCompilation:
  [result:PrimaryExpression{m2#VALUE}]
  [from:ClassExpression(alias=h join=JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map2} alias=m2 on=DyadicExpression{PrimaryExpression{m2#KEY}  =  ParameterExpression{key}}})]
  [symbols: 
    m2#KEY type=java.lang.Integer, 
    m2 type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    h type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    m2#VALUE type=java.lang.String, key type=java.lang.Integer]


SELECT M2.MAP2_VALUE 
FROM JPA_AN_MAPJOINHOLDER H 
LEFT OUTER JOIN JPA_AN_MAPJOINHOLDER_MAP2 M2 ON H.ID = M2.MAPJOINHOLDER_ID AND M2.MAP2_KEY = <1>
 */
                Query q = em.createQuery("SELECT VALUE(m2) FROM MapJoinHolder h LEFT JOIN h.map2 m2 ON KEY(m2) = :key");
                q.setParameter("key", Integer.valueOf(1));
                List results = q.getResultList();
                assertNotNull(results);
                assertEquals(1, results.size());

                String resultVal = (String)results.get(0);
                assertEquals("Val1", resultVal);

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(MapJoinHolder.class);
        }
    }

    /**
     * Test for KEY and VALUE use with Map<String, {embedded}> field.
     */
    public void testMapJoinEmbeddableWithKEYandVALUE()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MapJoinHolder holder = new MapJoinHolder(3);
                holder.setName("First Holder");
                for (int i=0;i<3;i++)
                {
                    MapJoinEmbeddedValue val = new MapJoinEmbeddedValue("Name" + i, "Description" + i);
                    holder.getMap3().put("Key" + i, val);
                }
                em.persist(holder);
                em.flush();

/**
This will generate:

QueryCompilation:
  [result:PrimaryExpression{m3#VALUE}]
  [from:ClassExpression(alias=h 
    join=JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map3} alias=m3 
    on=DyadicExpression{PrimaryExpression{m3#KEY}  =  ParameterExpression{key}}})]
  [symbols: 
    m3 type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinEmbeddedValue, 
    m3#VALUE type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinEmbeddedValue, 
    h type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    m3#KEY type=java.lang.String, 
    key type=unknown]


SELECT M3.DESCRIPTION,M3."NAME" 
FROM JPA_AN_MAPJOINHOLDER H 
LEFT OUTER JOIN JPA_AN_MAPJOINHOLDER_MAP3 M3 ON H.ID = M3.MAPJOINHOLDER_ID AND M3.MAP3_KEY = ?
 */
                TypedQuery<MapJoinEmbeddedValue> q = em.createQuery("SELECT VALUE(m3) FROM MapJoinHolder h LEFT JOIN h.map3 m3 ON KEY(m3) = :key", MapJoinEmbeddedValue.class);
                q.setParameter("key", "Key1");
                List<MapJoinEmbeddedValue> results = q.getResultList();
                assertNotNull(results);
                assertEquals(1, results.size());

                MapJoinEmbeddedValue resultVal = results.get(0);
                assertEquals("Name1", resultVal.getName());
                assertEquals("Description1", resultVal.getDescription());

                // Try access to a field of the embedded value
                Query q2 = em.createQuery("SELECT VALUE(m3).name FROM MapJoinHolder h LEFT JOIN h.map3 m3 ON KEY(m3) = :key");
                q2.setParameter("key", "Key1");
                List results2 = q2.getResultList();
                assertNotNull(results2);
                assertEquals(1, results2.size());
                assertEquals("Name1", (String)results2.get(0));

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(MapJoinHolder.class);
        }
    }

    /**
     * Test for KEY and VALUE use with Map field in the FROM clause.
     */
    public void testMapJoinWithKEYandVALUEInFROM()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                MapJoinHolder holder = new MapJoinHolder(1);
                holder.setName("First Holder");
                for (int i=0;i<3;i++)
                {
                    MapJoinKey key = new MapJoinKey(i, "Map key " + i, "Key description " + i);
                    MapJoinValue val = new MapJoinValue(i, "Map value " + i, "Value description " + i);
                    holder.getMap4().put(key, val);
                }
                em.persist(holder);
                em.flush();

/**
This will generate
QueryCompilation:
  [result:PrimaryExpression{k.description},PrimaryExpression{v.description}]
  [from:ClassExpression(alias=h join=
    JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map4#VALUE} alias=v join=
    JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{v#KEY} alias=k}})]
  [symbols: 
    v type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue,
    v#VALUE type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue, 
    h type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    k type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinKey, 
    v#KEY type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinKey]

SELECT K.DESCRIPTION,V.DESCRIPTION 
FROM JPA_AN_MAPJOINHOLDER H 
LEFT OUTER JOIN JPA_AN_MAPJOINHOLDER_MAP4 V_MAP ON H.ID = V_MAP.MAPJOINHOLDER_ID
LEFT OUTER JOIN JPA_AN_MAPJOINVALUE V ON V_MAP.MAP4_ID = V.ID 
LEFT OUTER JOIN JPA_AN_MAPJOINKEY K ON V_MAP.MAP4_KEY = K.ID
 */
                Query q = em.createQuery("SELECT k.description, v.description FROM MapJoinHolder h LEFT JOIN VALUE(h.map4) v LEFT JOIN KEY(v) k");
                List<Object[]> results = q.getResultList();

                assertNotNull(results);
                assertEquals(3, results.size());

/**
This will generate:

QueryCompilation:
  [result:PrimaryExpression{k.description},PrimaryExpression{v.description}]
  [from:ClassExpression(alias=h join=
    JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{h.map4} alias=v join=
    JoinExpression{JOIN_LEFT_OUTER PrimaryExpression{v#KEY} alias=k}})]
  [symbols: 
    v type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue, 
    v#VALUE type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinValue, 
    h type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinHolder,
    k type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinKey,
    v#KEY type=org.datanucleus.samples.annotations.one_many.map_join.MapJoinKey]

SELECT K.DESCRIPTION,V.DESCRIPTION 
FROM JPA_AN_MAPJOINHOLDER H
LEFT OUTER JOIN JPA_AN_MAPJOINHOLDER_MAP4 V_MAP ON H.ID = V_MAP.MAPJOINHOLDER_ID
LEFT OUTER JOIN JPA_AN_MAPJOINVALUE V ON V_MAP.MAP4_ID = V.ID 
LEFT OUTER JOIN JPA_AN_MAPJOINKEY K ON V_MAP.MAP4_KEY = K.ID
 */
                Query q2 = em.createQuery("SELECT k.description, v.description FROM MapJoinHolder h LEFT JOIN h.map4 v LEFT JOIN KEY(v) k");
                List<Object[]> results2 = q2.getResultList();

                assertNotNull(results2);
                assertEquals(3, results2.size());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(MapJoinHolder.class);
            clean(MapJoinValue.class);
            clean(MapJoinKey.class);
        }
    }

    /**
     * Test of the use of a result alias, and using it in ORDER BY.
     */
    public void testOrderByResultAlias()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Employee e1 = new Employee(101, "Fred", "Flintstone", "fred.flintstone@datanucleus.org", 30000f, "1234A");
                Employee e2 = new Employee(102, "George", "Cement", "george.cement@datanucleus.org", 20000f, "1235C");
                em.persist(e1);
                em.persist(e2);
                em.flush();

                List<Object[]> result = em.createQuery("SELECT e.firstName, (e.salary/12) AS MONTHLY FROM " + Employee.class.getName() + " e ORDER BY MONTHLY ASC").getResultList();
                assertEquals(2, result.size());
                Object[] result1 = result.get(0);
                Object[] result2 = result.get(1);
                assertEquals("George", result1[0]);
                assertEquals(1666.6666, ((Number)result1[1]).doubleValue(), 0.1);
                assertEquals("Fred", result2[0]);
                assertEquals(2500.00, ((Number)result2[1]).doubleValue(), 0.1);

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
        }
    }

    /**
     * Test for saving a query as a named query via the EMF.
     */
    public void testAddAsNamedQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@gmail.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@gmail.com");
                em.persist(p2);
                em.flush();

                Query q1 = em.createQuery("SELECT p FROM Person_Ann p WHERE p.lastName = :param");
                q1.setParameter("param", "Flintstone");
                List<Person> results1 = q1.getResultList();
                assertEquals(1, results1.size());
                assertEquals("Flintstone", ((Person)results1.iterator().next()).getLastName());

                // Save this query for later use
                emf.addNamedQuery("PeopleWithLastName", q1);

                Query q2 = em.createNamedQuery("PeopleWithLastName");
                assertEquals("SELECT p FROM Person_Ann p WHERE p.lastName = :param", q2.toString());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in Named Query test : " + e.getMessage());
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
     * Test for saving a query as a named query via the Query (DN extension).
     */
    public void testSaveAsNamedQuery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@gmail.com");
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@gmail.com");
                em.persist(p2);
                em.flush();

                Query q1 = em.createQuery("SELECT p FROM Person_Ann p WHERE p.lastName = :param");
                q1.setParameter("param", "Flintstone");
                List<Person> results1 = q1.getResultList();
                assertEquals(1, results1.size());
                assertEquals("Flintstone", ((Person)results1.iterator().next()).getLastName());

                // Save this query for later use
                ((JPAQuery)q1).saveAsNamedQuery("PeopleWithLastNameFromQuery");

                Query q2 = em.createNamedQuery("PeopleWithLastNameFromQuery");
                assertEquals("SELECT p FROM Person_Ann p WHERE p.lastName = :param", q2.toString());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in Named Query test : " + e.getMessage());
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
     * Test for DN extension "excludeSubclasses".
     */
    public void testHintExcludeSubclasses()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@gmail.com");
                em.persist(p1);
                Employee e2 = new Employee(102, "Barney", "Rubble", "barney.rubble@gmail.com", 10000f, "12300");
                em.persist(e2);
                em.flush();

                // No hint
                Query q0 = em.createQuery("SELECT p FROM Person_Ann p");
                org.datanucleus.store.query.Query intQuery0 = q0.unwrap(org.datanucleus.store.query.Query.class);
                assertTrue(intQuery0.isSubclasses());
                List<Person> results0 = q0.getResultList();
                assertEquals(2, results0.size());

                // hint = true
                Query q1 = em.createQuery("SELECT p FROM Person_Ann p");
                q1.setHint(org.datanucleus.store.query.Query.EXTENSION_EXCLUDE_SUBCLASSES, "true");
                org.datanucleus.store.query.Query intQuery1 = q1.unwrap(org.datanucleus.store.query.Query.class);
                assertFalse(intQuery1.isSubclasses());
                List<Person> results1 = q1.getResultList();
                assertEquals(1, results1.size());
                Person p = results1.get(0);
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
                assertEquals(101, p.getPersonNum());

                // hint = false
                Query q2 = em.createQuery("SELECT p FROM Person_Ann p");
                q2.setHint(org.datanucleus.store.query.Query.EXTENSION_EXCLUDE_SUBCLASSES, "false");
                org.datanucleus.store.query.Query intQuery2 = q2.unwrap(org.datanucleus.store.query.Query.class);
                assertTrue(intQuery2.isSubclasses());
                List<Person> results2 = q2.getResultList();
                assertEquals(2, results2.size());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in Named Query test : " + e.getMessage());
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

    /**
     * Test for query of embedded collection elements.
     */
    public void testQueryOfEmbeddedElement()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Processor proc1 = new Processor(1, "quad-core");
                Job j1 = new Job("dn1", 1);
                Job j2 = new Job("dn2", 2);
                proc1.addJob(j1);
                proc1.addJob(j2);
                Processor proc2 = new Processor(2, "dual-core");
                Job j3 = new Job("dn3", 1);
                proc2.addJob(j3);
                em.persist(proc1);
                em.persist(proc2);
                em.flush();

                Query q = em.createQuery("SELECT p FROM Processor p JOIN p.jobs j WHERE j.name='dn1'");
                List<Processor> results = q.getResultList();
                assertEquals(1, results.size());
                Processor pr1 = results.get(0);
                assertEquals(2, pr1.getNumberOfJobs());
                assertEquals("quad-core", pr1.getType());

                tx.rollback();
            }
            catch (PersistenceException e)
            {
                LOG.error("Exception in test", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(Processor.class);
        }
    }

    /**
     * Test use of JPQL JOIN to another root using ON (DataNucleus Extension).
     */
    public void testJoinRootOn()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Project prj1 = new Project("DataNucleus", 1000000);
                em.persist(prj1);
                Project prj2 = new Project("JPOX", 50000);
                em.persist(prj2);

                Account acct1 = new Account();
                acct1.setUsername("DataNucleus");
                acct1.setId(1);
                em.persist(acct1);
                em.flush();

/**
This will generate the following

QueryCompilation:
  [result:PrimaryExpression{p.name},PrimaryExpression{p.budget}]
  [from:ClassExpression(alias=p join=JoinExpression{JOIN_INNER PrimaryExpression{Account} alias=a on=DyadicExpression{PrimaryExpression{p.name}  =  PrimaryExpression{a.username}}})]
  [symbols: p type=org.datanucleus.samples.annotations.models.company.Project, a type=org.datanucleus.samples.annotations.models.company.Account]

SELECT P."NAME",P.BUDGET FROM JPA_AN_PROJECT P INNER JOIN JPA_AN_ACCOUNT A ON P."NAME" = A.USERNAME
 */
                Query q = em.createQuery("SELECT p.name, p.budget FROM " + Project.class.getName() + " p JOIN Account a ON p.name = a.username");
                List<Object[]> results = q.getResultList();
                assertNotNull(results);
                assertEquals(1, results.size());
                for (Object[] row : results)
                {
                    assertEquals(2, row.length);
                    assertEquals("DataNucleus", row[0]);
                    assertEquals(Long.valueOf(1000000), row[1]);
                }
                // TODO Add some asserts, or choose a good example for this

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
            clean(Account.class);
            clean(Project.class);
        }
    }

    /**
     * Test use of JOIN ON where the ON clause contains a subquery.
     */
    public void testJoinOnWithSubquery()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@dn.org");
                p1.setAge(31);
                em.persist(p1);
                Person p2 = new Person(102, "Barney", "Rubble", "barney.rubble@dn.org");
                p2.setAge(35);
                em.persist(p2);
                Qualification q1 = new Qualification("BSc in Maths");
                q1.setPerson(p1);
                q1.setDate(new Date());
                em.flush();

                // Just check that this is queryable
                Query q = em.createQuery("SELECT q.name FROM " + Qualification.class.getName() + " q JOIN q.person p ON p.age < (SELECT AVG(p2.age) FROM " + Person.class.getName() + " p2)");
                q.getResultList();
                // TODO Add some asserts, or choose a good example for this

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
            clean(Qualification.class);
            clean(Person.class);
        }
    }

    private void removeManagerEmployeeRelation()
    {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            Query q = em.createQuery("SELECT m FROM " + Manager.class.getName() + " m");
            List<Manager> managers = q.getResultList();
            for (Manager m : managers)
            {
                Set<Employee> emps = m.getSubordinates();
                for (Employee e : emps)
                {
                    e.setManager(null);
                }
                m.clearSubordinates();
            }
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception performing removal of Manager-Employee relation", e);
            fail("Error in cleanup : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        emf.getCache().evictAll();
    }

    private void removePersonBestFriendRelation()
    {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            if (storeMgr.getSupportedOptions().contains(StoreManager.OPTION_QUERY_JPQL_BULK_UPDATE))
            {
                Query q = em.createQuery("UPDATE " + Person.class.getName() + " p SET p.bestFriend = NULL");
                q.executeUpdate();
            }
            else
            {
                Query q = em.createQuery("SELECT p FROM " + Person.class.getName() + " p");// WHERE bestFriend IS NOT NULL");
                List<Person> persons = q.getResultList();
                for (Person p : persons)
                {
                    String state = storeMgr.getApiAdapter().getObjectState(p).toLowerCase();
                    if (!state.endsWith("deleted"))
                    {
                        LOG.info(">> setting BESTFRIEND on " + StringUtils.toJVMIDString(p) + " state=" + state);
                        p.setBestFriend(null);
                    }
                }
            }
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception performing null of bestFriend field", e);
            fail("Error in cleanup : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        emf.getCache().evictAll();
    }
}