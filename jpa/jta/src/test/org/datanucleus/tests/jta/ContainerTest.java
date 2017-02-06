/**********************************************************************
Copyright (c) 2017 Andy Jefferson and others. All rights reserved.
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;

import org.datanucleus.api.jpa.JPAEntityManagerFactory;
import org.datanucleus.api.jpa.PersistenceUnitInfoImpl;
import org.datanucleus.exceptions.ConnectionFactoryNotFoundException;
import org.datanucleus.exceptions.UnsupportedConnectionFactoryException;
import org.datanucleus.samples.annotations.Person;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for use of JTA with JPA, using the EMF constructor used by a JavaEE container to simulate container usage.
 */
public class ContainerTest extends JPAPersistenceTestCase
{
    static EntityManagerFactory jtaEMF = null;
    public ContainerTest(String name)
    {
        super(name);
        if (jtaEMF == null)
        {
            // Create JTA DataSource
            Object obj;
            try
            {
                obj = new InitialContext().lookup("java:comp/env/NucleusDS");
            }
            catch (NamingException e)
            {
                throw new ConnectionFactoryNotFoundException("java:comp/env/NucleusDS", e);
            }
            if (!(obj instanceof DataSource) && !(obj instanceof XADataSource))
            {
                throw new UnsupportedConnectionFactoryException(obj);
            }
            DataSource jtaDataSource = (DataSource) obj;

            // TODO Fix this
            URL rootURL = null;
            try
            {
                rootURL = new URL("file:/home/andy/work/datanucleus/tests/jpa/jta/target/classes");
            }
            catch (MalformedURLException mue)
            {
            }
            
            PersistenceUnitInfoImpl unitInfo = new PersistenceUnitInfoImpl("org.datanucleus.api.jpa.PersistenceProviderImpl", "TEST_JTA", 
                PersistenceUnitTransactionType.JTA, rootURL);
            unitInfo.setJtaDataSource(jtaDataSource);
            unitInfo.getProperties().setProperty("datanucleus.jtaLocator", "custom_jndi");
            unitInfo.getProperties().setProperty("datanucleus.jtaJndiLocation", "java:comp/TransactionManager");
            unitInfo.getProperties().setProperty("datanucleus.storeManagerType", "rdbms");
            unitInfo.getProperties().setProperty("javax.persistence.schema-generation.database.action", "drop-and-create");
            unitInfo.addManagedClassName("org.datanucleus.samples.annotations.Person");
            unitInfo.setExcludeUnlistedClasses(true);
            Map overridingProps = null;
            jtaEMF = new JPAEntityManagerFactory(unitInfo, overridingProps);
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

    private UserTransaction getUserTransaction() 
    throws NamingException
    {
        return (UserTransaction)new InitialContext().lookup("java:comp/UserTransaction");
    }
    
}