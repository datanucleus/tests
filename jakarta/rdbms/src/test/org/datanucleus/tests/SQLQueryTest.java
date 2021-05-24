/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.util.Iterator;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import org.datanucleus.samples.annotations.models.company.Employee;
import org.datanucleus.samples.annotations.models.company.Person;
import org.datanucleus.samples.annotations.one_one.unidir.Login;
import org.datanucleus.samples.annotations.one_one.unidir.LoginAccount;
import org.datanucleus.samples.annotations.one_one.unidir.LoginAccountComplete;

/**
 * Tests for SQL queries via Jakarta Persistence.
 */
public class SQLQueryTest extends JakartaPersistenceTestCase
{
    private static boolean initialised = false;

    public SQLQueryTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Person.class, Employee.class, Login.class, LoginAccount.class
                });
        }
    }

    /**
     * Test of an SQL query persisting the object and querying in the same txn.
     */
    public void testBasic1()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // Persist an object
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setAge(34);
                em.persist(p);
                em.flush();

                // Check the results
                List result = em.createNativeQuery("SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=34").getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long or equivalent (type of "PERSON_ID" column (Person.personNum field)); Oracle returns BigDecimal
                    Object obj = iter.next();
                    assertTrue("SQL query has returned an object of an incorrect type", Number.class.isAssignableFrom(obj.getClass()));
                }

                tx.rollback(); // Dont persist the data
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
     * Test of an SQL query persisting the object and querying in a different txn.
     */
    public void testBasic2()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // Persist an object
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setAge(34);
                em.persist(p);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Check the results
                List result = em.createNativeQuery("SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=34").getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long or equivalent (type of "PERSON_ID" column (Person.personNum field)); Oracle returns BigDecimal
                    Object obj = iter.next();
                    assertTrue("SQL query has returned an object of an incorrect type", Number.class.isAssignableFrom(obj.getClass()));
                }

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
     * Test of an SQL query persisting the object and querying in the same txn, using (numbered) parameters.
     */
    public void testBasicWithNumberedParameters()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                // Persist an object
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setAge(34);
                em.persist(p);
                em.flush();

                // Check the results
                Query q = em.createNativeQuery("SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=?1");
                q.setParameter(1, 34);
                try
                {
                    q.getParameter(2);
                    fail("Should have thrown exception but didnt when calling q.getParameter(int)");
                }
                catch (IllegalStateException ise)
                {
                    // Expected
                }
                catch (Throwable thr)
                {
                    fail("Incorrect throwable caught when calling q.getParameter(int) : " + thr.getMessage());
                }
                List result = q.getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long or equivalent (type of "PERSON_ID" column (Person.personNum field)); Oracle returns BigDecimal
                    Object obj = iter.next();
                    assertTrue("SQL query has returned an object of an incorrect type", Number.class.isAssignableFrom(obj.getClass()));
                }

                tx.rollback(); // Dont persist the data
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
     * Test of an SQL query using a result set mapping giving two entities (Login + LoginAccount).
     */
    public void testSQLResult()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("flintstone","pwd");
                acct.setLogin(login);
                em.persist(login);
                em.persist(acct);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Check the results
                List result = em.createNativeQuery(
                    "SELECT P.ID, P.FIRSTNAME, P.LASTNAME, P.LOGIN_ID, L.ID, L.USERNAME, L.PASSWORD " +
                    "FROM JPA_AN_LOGINACCOUNT P, JPA_AN_LOGIN L", "AN_LOGIN_PLUS_ACCOUNT").getResultList();
                assertEquals(1, result.size());

                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a String (type of "ID" column)
                    Object[] obj = (Object[])iter.next();
                    assertEquals("Fred", ((LoginAccount)obj[0]).getFirstName());
                    assertEquals("flintstone", ((Login)obj[1]).getUserName());
                    assertEquals("Fred", ((LoginAccount)obj[0]).getFirstName());
                    assertTrue(((LoginAccount)obj[0]).getLogin() == ((Login)obj[1]));
                }

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
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of an SQL query using a result set mapping giving two entities (Login + LoginAccount) and
     * using aliasing of columns.
     */
    public void testSQLResultAliased()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("flintstone","pwd");
                acct.setLogin(login);
                em.persist(login);
                em.persist(acct);

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
            emf.getCache().evictAll();

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result = em.createNativeQuery(
                    "SELECT P.ID AS THISID, P.FIRSTNAME AS FN, P.LASTNAME, P.LOGIN_ID, " +
                    "L.ID AS IDLOGIN, L.USERNAME AS UN, L.PASSWORD FROM " +
                    "JPA_AN_LOGINACCOUNT P, JPA_AN_LOGIN L", "AN_LOGIN_PLUS_ACCOUNT_ALIAS").getResultList();
                assertEquals(1, result.size());

                // Check the results
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a String (type of "ID" column)
                    Object[] obj = (Object[])iter.next();
                    assertEquals("Fred", ((LoginAccount)obj[0]).getFirstName());
                    assertEquals("Flintstone", ((LoginAccount)obj[0]).getLastName());
                    assertEquals("flintstone", ((Login)obj[1]).getUserName());
                    assertTrue(((LoginAccount)obj[0]).getLogin() == ((Login)obj[1]));
                }

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
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of an SQL query using a result set mapping giving two entities (Login + LoginAccount) and
     * using aliasing of columns.
     */
    public void testSQLResultAliased2()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("flintstone", "pwd");
                acct.setLogin(login);
                em.persist(login);
                em.persist(acct);

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
            emf.getCache().evictAll();

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                List result = em.createNativeQuery(
                    "SELECT P.ID AS THISID, P.FIRSTNAME AS FN, P.LASTNAME AS LN, P.LOGIN_ID AS LID, " +
                    "L.ID, L.USERNAME, L.PASSWORD FROM " +
                    "JPA_AN_LOGINACCOUNT P, JPA_AN_LOGIN L","AN_LOGIN_PLUS_ACCOUNT_ALIAS2").getResultList();
                assertEquals(1, result.size());

                // Check the results
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a String (type of "ID" column)
                    Object[] obj = (Object[])iter.next();
                    assertEquals("Fred", ((LoginAccount)obj[0]).getFirstName());
                    assertEquals("Flintstone", ((LoginAccount)obj[0]).getLastName());
                    assertEquals("flintstone", ((Login)obj[1]).getUserName());
                    assertTrue(((LoginAccount)obj[0]).getLogin() == ((Login)obj[1]));
                }

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
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of an SQL query using a result set mapping giving two entities (Login + LoginAccount) and
     * using aliasing of columns and constructor result.
     */
    public void testSQLResultConstructor()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount(1, "Fred", "Flintstone");
                Login login = new Login("flintstone", "pwd");
                acct.setLogin(login);
                em.persist(login);
                em.persist(acct);

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
            emf.getCache().evictAll();

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                List result = em.createNativeQuery(
                    "SELECT P.FIRSTNAME AS FN, P.LASTNAME AS LN, L.USERNAME AS USER, L.PASSWORD AS PWD FROM " +
                    "JPA_AN_LOGINACCOUNT P, JPA_AN_LOGIN L","AN_LOGIN_PLUS_ACCOUNT_CONSTRUCTOR").getResultList();
                assertEquals(1, result.size());

                // Check the results
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a LoginAccountComplete
                    LoginAccountComplete acctCmp = (LoginAccountComplete)iter.next();
                    assertEquals("Fred", acctCmp.getFirstName());
                    assertEquals("Flintstone", acctCmp.getLastName());
                    assertEquals("flintstone", acctCmp.getUserName());
                    assertEquals("pwd", acctCmp.getPassword());
                }

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
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of an SQL query using a result class as an Entity.
     */
    public void testSQLResultEntity()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Login login = new Login("flintstone","pwd");
                em.persist(login);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Execute a query
                List<Login> result = em.createNativeQuery("SELECT * FROM JPA_AN_LOGIN", Login.class).getResultList();
                assertEquals(1, result.size());

                Login l = result.get(0);
                assertNotNull(l);

                // Check that the result entity is managed by this EntityManager
                assertTrue(em.contains(l));

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
        }
    }
}