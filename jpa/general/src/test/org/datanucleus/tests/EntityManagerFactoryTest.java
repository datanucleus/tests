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
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.datanucleus.api.jpa.JPAEntityManagerFactory;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.jpox.samples.annotations.models.company.Person;

/**
 * Tests for EntityManagerFactory (and PersistenceProvider).
 */
public class EntityManagerFactoryTest extends JPAPersistenceTestCase
{
    public EntityManagerFactoryTest(String name)
    {
        super(name);
    }

    /**
     * Test for the creation of EntityManagerFactory with unspecified persistence provider
     */
    public void testCreateEntityManagerFactoryWithNoProvider()
    {
        // We need to get the datastore props direct like this since the persistence.xml doesnt have them
        Properties datastoreProps = TestHelper.getPropertiesForDatastore(1);
        assertNotNull(javax.persistence.Persistence.createEntityManagerFactory("TEST", datastoreProps));
    }

    /**
     * Test for the creation of EntityManagerFactory using single arg method via Persistence.
     */
    public void testCreateEntityManagerFactoryWithoutOverridingProps()
    {
        try
        {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("TEST2");
            if (emf == null)
            {
                fail("Failed to find EMF with name");
            }
        }
        catch (Exception e)
        {
            LOG.info("Exception thrown creating EMF", e);
            fail("Exception thrown while creating EMF using Persistence.createEntityManagerFactory(String) : " + e.getMessage());
        }
    }

    /**
     * Test for the creation of EntityManagerFactory with specified invalid persistence provider
     */
    public void testCreateEntityManagerFactoryWithInvalidProvider()
    {
        try
        {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("Invalid Provider");
            if (emf != null)
            {
                fail("Managed to create an EntityManagerFactory yet the provider should have resulted in none valid");
            }
        }
        catch (PersistenceException pe)
        {
            // Expected since we dont have the required provider
        }
    }

    public void testSerialize()
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
            }
            catch (Exception e)
            {
                LOG.error(">> Exception on persist before serialisation", e);
                fail("Exception on persist : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Serialize the current EMF
            byte[] bytes = null;
            try
            {
                bytes = TestHelper.serializeObject(emf);
            }  
            catch (RuntimeException re)
            {
                LOG.error("Error serializing EMF", re);
                fail("Error in serialization : " + re.getMessage());
            }

            // Deserialise the EMF
            EntityManagerFactory emf = null;
            try
            {
                emf = (JPAEntityManagerFactory)TestHelper.deserializeObject(bytes);
            }
            catch (RuntimeException re)
            {
                LOG.error("Error deserializing EMF", re);
                fail("Error in deserialization : " + re.getMessage());
            }

            JPAEntityManagerFactory jpaEMF = (JPAEntityManagerFactory)emf;
            assertNotNull(jpaEMF);
            assertNotNull(jpaEMF.getNucleusContext());
            assertNotNull(jpaEMF.getMetamodel());

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                TypedQuery<Person> q = em.createQuery("SELECT FROM " + Person.class.getName() + " p", Person.class);
                List<Person> results = q.getResultList();
                assertEquals(1, results.size());
                Person p = results.get(0);
                assertEquals("Fred", p.getFirstName());
                assertEquals("Flintstone", p.getLastName());
                assertEquals("fred.flintstone@jpox.com", p.getEmailAddress());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception on retrieve after deserialisation", e);
                fail("Exception on retrieve after deserialisation : " + e.getMessage());
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