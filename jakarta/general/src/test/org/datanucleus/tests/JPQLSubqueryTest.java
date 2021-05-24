/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import org.datanucleus.samples.annotations.models.company.Account;
import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Organisation;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.models.company.WebSite;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

/**
 * Tests for subqueries with JPQL.
 */
public class JPQLSubqueryTest extends JakartaPersistenceTestCase
{
    private static boolean initialised = false;

    public JPQLSubqueryTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class
                }
            );
        }
    }

    /**
     * Simple query using a subquery with greater than.
     */
    public void testSubqueryGreaterThan()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE P.age > (SELECT avg(Q.age) FROM " + Person.class.getName() + " Q)").getResultList();
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
     * Simple query using a subquery as the argument to IN (...).
     */
    public void testIn()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE P.firstName IN " +
                    "(SELECT Q.firstName FROM " + Person.class.getName() + " Q WHERE Q.lastName = 'Rubble')").getResultList();
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
     * Simple query using a subquery as the argument to IN (...).
     */
    public void testSubqueryWithLike()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);
                em.flush();

                Query q = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE P.firstName IN " +
                    "(SELECT Q.firstName FROM " + Person.class.getName() + " Q WHERE Q.lastName = :lastname AND Q.firstName like :firstname)");
                q.setParameter("lastname", "Rubble");
                q.setParameter("firstname", "B%");
                List result = q.getResultList();
                assertEquals(1, result.size());
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during query", e);
                fail("Exception thrown during query : "+ e.getMessage());
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
     * Simple query using a subquery as the argument to IN (...).
     */
    public void testInNested()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Query query = em.createQuery(
                    "SELECT W FROM " + WebSite.class.getName() + " W " +
                    "WHERE W.id IN (SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE WW.id IN " +
                    "(SELECT Z.id FROM " + WebSite.class.getName()+" Z WHERE Z.id=1000))");
//                query.setParameter("name", "Rubble");
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
        finally
        {
            clean(WebSite.class);
        }
    }

    public void testSubqueryWithParam()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Query query = em.createQuery("SELECT Object(W) FROM " + WebSite.class.getName() + " W " +
                        "WHERE W.id IN (SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE WW.url = :url)");
                query.setParameter("url", "nourl");
                List result = query.getResultList();

                query  = em.createQuery("SELECT Object(W) FROM " + WebSite.class.getName() + " W " +
                    "WHERE W.id IN (SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE WW.url = :url AND WW.id= :oid)");
                query.setParameter("url", "nourl");
                query.setParameter("oid", new Integer(3));
                result = query.getResultList();
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
            clean(WebSite.class);
        }
    }

    /**
     * Simple query using a subquery as the argument to NOT IN (...).
     */
    public void testNotIn()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);

                List<Person> result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE P.firstName NOT IN " +
                    "(SELECT Q.firstName FROM " + Person.class.getName() + " Q WHERE Q.lastName = 'Rubble')").getResultList();
                assertEquals(1, result.size());
                Person p = result.iterator().next();
                assertEquals("Wrong person returned with NOT IN", "Fred", p.getFirstName());
                assertEquals("Wrong person returned with NOT IN", "Flintstone", p.getLastName());
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
     * Simple query using a subquery with EXISTS.
     */
    public void testExists()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                p1.setBestFriend(p2);
                em.persist(p1);
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE EXISTS " +
                    "(SELECT Q.personNum FROM " + Person.class.getName() + " Q WHERE Q.bestFriend = P)").getResultList();
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
     * Simple query using a subquery with NOT EXISTS.
     */
    public void testNotExists()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                p1.setBestFriend(p2);
                em.persist(p1);
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT Object(P) FROM " + Person.class.getName() + " P " +
                    "WHERE NOT EXISTS " +
                    "(SELECT Q.personNum FROM " + Person.class.getName() + " Q WHERE Q.bestFriend = P)").getResultList();
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
     * Simple query using a subquery in the HAVING clause.
     */
    public void testHavingSubquery()
    {
        if (!(storeMgr instanceof RDBMSStoreManager))
        {
            return;
        }
        DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
        if (!dba.supportsOption(DatastoreAdapter.SUBQUERY_IN_HAVING))
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

                Account a1 = new Account();
                a1.setUsername("Flintstone");
                a1.setId(1);
                em.persist(a1);
                Organisation o1 = new Organisation("Flintstone");
                o1.setDescription("Freds organisation");
                em.persist(o1);

                // TODO Come up with a better sample query. Why does the JPA TCK/Spec have none?
                List<Person> result = em.createQuery(
                    "SELECT o FROM " + Organisation.class.getName() + " o " +
                    "GROUP BY o.name HAVING EXISTS (SELECT a FROM " + Account.class.getName() + " a WHERE a.username = o.name)").getResultList();
                assertNotNull(result);
                assertEquals(1, result.size());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception in query", e);
                fail("Exception executing query with HAVING subquery : " + e.getMessage());
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
     * ANY(subquery).
     */
    public void testSubqueryAll()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);

                List result = em.createQuery(
                    "SELECT p FROM " + Person.class.getName() + " p " +
                    "WHERE p.age > ALL(SELECT avg(q.age) FROM " + Person.class.getName() + " q)").getResultList();
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
     * Simple query using a subquery in SELECT clause
     */
    public void testSubqueryInResult()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p1.setAge(35);
                Person p2 = new Person(101, "Barney", "Rubble", "barney.rubble@jpox.com");
                p2.setAge(45);
                em.persist(p1);
                em.persist(p2);
                em.flush();

                List<Object[]> result = em.createQuery(
                    "SELECT p, (SELECT avg(Q.age) FROM " + Person.class.getName() + " Q) FROM " + Person.class.getName() + " p WHERE p.age > 3 ORDER BY p.age").getResultList();
                assertEquals(2, result.size());
                Object[] row0 = result.get(0);
                Object[] row1 = result.get(1);
                assertTrue(row0[0] instanceof Person);
                assertEquals("Fred", ((Person)row0[0]).getFirstName());
                assertEquals(40.0, row0[1]);
                assertTrue(row1[0] instanceof Person);
                assertEquals("Barney", ((Person)row1[0]).getFirstName());
                assertEquals(40.0, row1[1]);
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
}