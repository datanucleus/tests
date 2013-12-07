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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.models.company.WebSite;

/**
 * Tests for subqueries with JPQL.
 */
public class JPQLSubqueryTest extends JPAPersistenceTestCase
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
                    "WHERE P.age > " +
                    "(SELECT avg(Q.age) FROM " + Person.class.getName() + " Q)").getResultList();
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
    public void testSubqueryIn()
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
    public void testSubqueryInNested()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();


                Query query = em.createQuery(
                    "SELECT Object(W) FROM " + WebSite.class.getName() + " W " +
                    "WHERE W.id IN " +
                    "(" +
                    "SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE " +
                    " WW.id IN " +
                    "(SELECT Z.id FROM " + WebSite.class.getName()+" Z WHERE Z.id=1000)"+
                    ")");
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


                Query query = em.createQuery(
                        "SELECT Object(W) FROM " + WebSite.class.getName() + " W " +
                        "WHERE W.id IN " +
                        "(" +
                        "SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE " +
                        " WW.url = :url" +
                        ")");
                query.setParameter("url", "nourl");
                List result = query.getResultList();
                
                query  = em.createQuery(
                    "SELECT Object(W) FROM " + WebSite.class.getName() + " W " +
                    "WHERE W.id IN " +
                    "(" +
                    "SELECT WW.id FROM " + WebSite.class.getName() + " WW WHERE " +
                    " WW.url = :url AND WW.id= :oid" +
                    ")");
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
    public void testSubqueryNotIn()
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
}