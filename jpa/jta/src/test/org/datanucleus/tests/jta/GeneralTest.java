/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.jta;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.transaction.UserTransaction;

import org.datanucleus.samples.annotations.Person;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Series of general tests for using JTA with JPA.
 * Refer to http://en.wikibooks.org/wiki/Java_Persistence/Transactions#Example_JTA_transaction
 * for some examples of what the spec requires.
 */
public class GeneralTest extends JPAPersistenceTestCase
{
    static EntityManagerFactory jtaEMF = null;
    public GeneralTest(String name)
    {
        super(name);
        if (jtaEMF == null)
        {
            jtaEMF = getEMF("TEST_JTA");
        }
    }

    /**
     * Initialisation method, called on construction.
     */
    protected synchronized void init()
    {
        // Overridden so we don't try the default persistence-unit
    }

    protected void tearDown() throws Exception
    {
        try 
        {
            UserTransaction ut = getUserTransaction();
            ut.rollback();
        }
        catch (Throwable t)
        {}

        EntityManager em = jtaEMF.createEntityManager();
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);
        boolean active = true;
        try
        {
            ut.begin();
            Query q = em.createQuery("DELETE FROM Person p");
            q.executeUpdate();
            ut.commit();
            active = false;
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }

        super.tearDown();
    }

    /**
     * Test for JTA started/committed using UserTransaction.
     */
    public void testBasicJTA() throws Exception
    {
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);

        boolean active = true;
        EntityManager em = jtaEMF.createEntityManager();
        try
        {
            ut.begin();
            Person p = new Person(1, "Joe User");
            em.persist(p);
            ut.commit();
            active = false;
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in persist", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }

        // Check results in datastore
        em = jtaEMF.createEntityManager();
        active = true;
        try
        {
            ut.begin();
            Query q = em.createQuery("SELECT p FROM Person p");
            List<Person> people = q.getResultList();
            assertNotNull(people);
            assertEquals(1, people.size());
            Person p = people.get(0);
            assertEquals("Joe User", p.getName());
            assertEquals(1, p.getId());
            ut.commit();
            active = false;
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in check of results", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }
    }

    /**
     * Test for JTA where we have the EntityManager and join a JTA transaction.
     */
    public void testJoinJTA() throws Exception
    {
        // Create a Person object to use later
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);
        boolean active = true;
        EntityManager em = jtaEMF.createEntityManager();
        try
        {
            ut.begin();
            Person p = new Person(1, "Joe User");
            em.persist(p);
            ut.commit();
            active = false;
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in persist", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }

        ut = null;
        em = jtaEMF.createEntityManager();
        active = false;
        try
        {
            Person p = em.find(Person.class, 1);
            p.setName("Fred User");

            ut = getUserTransaction();
            active = true;
            ut.setTransactionTimeout(300);
            ut.begin();

            em.joinTransaction();
            // This should commit the update above
            ut.commit();
            active = false;
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in persist", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }
    }

    /**
     * Test for JTA where we have the EntityManager and join a JTA transaction where no UserTransaction.
     */
    public void testJoinJTAButNotPresent() throws Exception
    {
        // Create a Person object to use later
        UserTransaction ut = getUserTransaction();
        ut.setTransactionTimeout(300);
        boolean active = true;
        EntityManager em = jtaEMF.createEntityManager();
        try
        {
            ut.begin();
            Person p = new Person(1, "Joe User");
            em.persist(p);
            ut.commit();
            active = false;
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in persist", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            if (active)
            {
                ut.rollback();
            }
            em.close();
        }

        ut = null;
        em = jtaEMF.createEntityManager();
        active = false;
        try
        {
            Person p = em.find(Person.class, 1);
            p.setName("Fred User");

            try
            {
                em.joinTransaction();
                fail("Failed to throw TransactionRequiredException from joinTransaction with no UserTransaction");
            }
            catch (TransactionRequiredException tre)
            {
                LOG.info(">> Expected exception caught from joinTransaction");
            }
        }
        catch (Exception e)
        {
            LOG.error(">> Exception in test", e);
            fail("Exception in check of results : " + e.getMessage());
        }
        finally
        {
            em.close();
        }
    }

    /**
     * Test for RESOURCE_LOCAL transaction with datasource using JNDI.
     */
    public void testResourceLocalJNDI() throws Exception
    {
        emf = getEMF("TEST_RESOURCELOCAL");

        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                Person p = new Person(2, "Jimmy User");
                em.persist(p);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in persist", e);
                fail("Exception in check of results : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check results in datastore
            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                Query q = em.createQuery("SELECT p FROM Person p WHERE name.startsWith('Jimmy')");
                List<Person> people = q.getResultList();
                assertNotNull(people);
                assertEquals(1, people.size());
                Person p = people.get(0);
                assertEquals("Jimmy User", p.getName());
                assertEquals(2, p.getId());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in check of results", e);
                fail("Exception in check of results : " + e.getMessage());
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
            clean(emf, Person.class);
        }
    }

    private UserTransaction getUserTransaction() 
    throws NamingException
    {
        return (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
    }
    
}