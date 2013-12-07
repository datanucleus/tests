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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Person;
import org.jpox.samples.annotations.one_one.unidir.Login;
import org.jpox.samples.annotations.one_one.unidir.LoginAccount;

/**
 * Tests for SQL queries via JPA.
 */
public class SQLQueryTest extends JPAPersistenceTestCase
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
        if (vendorID == null)
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

                // Persist an object
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setAge(34);
                em.persist(p);

                // Check the results
                List result = em.createNativeQuery(
                "SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=34").getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long (type of "PERSON_ID" column, Person.personNum field)
                    Object obj = iter.next();
                    assertEquals("SQL query has returned an object of an incorrect type", Long.class, obj.getClass());
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
        if (vendorID == null)
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
                List result = em.createNativeQuery(
                "SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=34").getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long (type of "PERSON_ID" column (Person.personNum field))
                    Object obj = iter.next();
                    assertEquals("SQL query has returned an object of an incorrect type", Long.class, obj.getClass());
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
        if (vendorID == null)
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

                // Persist an object
                Person p = new Person(101, "Fred", "Flintstone", "fred.flintstone@jpox.com");
                p.setAge(34);
                em.persist(p);

                // Check the results
                Query q = em.createNativeQuery("SELECT P.PERSON_ID FROM JPA_AN_PERSON P WHERE P.AGE_COL=?1");
                q.setParameter(1, 34);
                List result = q.getResultList();
                assertEquals(1, result.size());
                Iterator iter = result.iterator();
                while (iter.hasNext())
                {
                    // Should be a Long (type of "PERSON_ID" column, Person.personNum field)
                    Object obj = iter.next();
                    assertEquals("SQL query has returned an object of an incorrect type", Long.class, obj.getClass());
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
        if (vendorID == null)
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
        if (vendorID == null)
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
        if (vendorID == null)
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
}