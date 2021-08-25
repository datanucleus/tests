/**********************************************************************
Copyright (c) 2021 Andy Jefferson and others. All rights reserved.
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

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.validation.ConstraintViolationException;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.multitenancy.TenantedObject;

/**
 * Tests for multitenancy with JPA.
 */
public class MultitenancyTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public MultitenancyTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    TenantedObject.class
                });
            initialised = true;
        }
    }

    /**
     * Test basic tenantId specification via persistence property
     */
    public void testTenantId()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID");
        EntityManagerFactory emfTenant1 = getEMF(1, "TEST", userProps);

        Properties userProps2 = new Properties();
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID2");
        EntityManagerFactory emfTenant2 = getEMF(1, "TEST", userProps2);

        try
        {
            // Persist object for first tenant
            EntityManager em1 = emfTenant1.createEntityManager();
            EntityTransaction tx1 = em1.getTransaction();
            try
            {
                tx1.begin();
                TenantedObject o1 = new TenantedObject();
                o1.setId(1);
                o1.setName("First");
                em1.persist(o1);
                tx1.commit();
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                em1.close();
            }
            emfTenant1.getCache().evictAll();

            // Persist object for second tenant
            EntityManager em2 = emfTenant2.createEntityManager();
            EntityTransaction tx2 = em2.getTransaction();
            try
            {
                tx2.begin();
                TenantedObject o2 = new TenantedObject();
                o2.setId(2);
                o2.setName("Second");
                em2.persist(o2);
                tx2.commit();
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                em2.close();
            }
            emfTenant2.getCache().evictAll();

            // Retrieve all objects for first tenant
            em1 = emfTenant1.createEntityManager();
            tx1 = em1.getTransaction();
            try
            {
                tx1.begin();

                // Check retrieve of expected object
                TenantedObject obj1 = (TenantedObject) em1.find(TenantedObject.class, 1);
                assertNotNull(obj1);

                // Check retrieve of unexpected object
                TenantedObject obj2 = em1.find(TenantedObject.class, 2);
                assertNull(obj2);

                tx1.commit();
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                em1.close();
            }

            // Retrieve all objects for second tenant
            em2 = emfTenant2.createEntityManager();
            tx2 = em2.getTransaction();
            try
            {
                tx2.begin();

                // Check retrieve of expected object
                TenantedObject obj2 = (TenantedObject) em2.find(TenantedObject.class, 2);
                assertNotNull(obj2);

                // Check retrieve of unexpected object
                TenantedObject obj1 = (TenantedObject) em2.find(TenantedObject.class, 1);
                assertNull(obj1);

                tx2.commit();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                em2.close();
            }
        }
        finally
        {
            // Clear data
            clean(emfTenant1, TenantedObject.class);
            clean(emfTenant2, TenantedObject.class);

            // Close EMFs
            emfTenant1.close();
            emfTenant2.close();
        }
    }

    /**
     * Test tenantId specification via MultitenancyProvider
     */
    // TODO

    /**
     * Test tenantReadIds specification via persistence property for those datastores that support it.
     */
    public void testTenantIdWithReadIds()
    {
        if (rdbmsVendorID == null)
        {
            return; // Only applicable to RDBMS since no other datastore supports tenantReadIds
        }

        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID");
        userProps.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_READ_IDS, "MyID,MyID2");
        EntityManagerFactory emfTenant1 = getEMF(1, "TEST", userProps);

        Properties userProps2 = new Properties();
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID2");
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_READ_IDS, "MyID,MyID2");
        EntityManagerFactory emfTenant2 = getEMF(1, "TEST", userProps2);

        try
        {
            // Persist object for first tenant
            EntityManager em1 = emfTenant1.createEntityManager();
            EntityTransaction tx1 = em1.getTransaction();
            try
            {
                tx1.begin();
                TenantedObject o1 = new TenantedObject();
                o1.setId(1);
                o1.setName("First");
                em1.persist(o1);
                tx1.commit();
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                em1.close();
            }

            // Persist object for second tenant
            EntityManager em2 = emfTenant2.createEntityManager();
            EntityTransaction tx2 = em2.getTransaction();
            try
            {
                tx2.begin();
                TenantedObject o2 = new TenantedObject();
                o2.setId(2);
                o2.setName("Second");
                em2.persist(o2);
                tx2.commit();
            }
            catch (ConstraintViolationException cve)
            {
                // expected
                LOG.info("Exception correctly thrown : " + cve.getMessage());
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                em2.close();
            }

            // Retrieve all objects for first tenant
            em1 = emfTenant1.createEntityManager();
            tx1 = em1.getTransaction();
            try
            {
                tx1.begin();

                // Check basic retrieve of expected objects
                TenantedObject obj1 = (TenantedObject) em1.find(TenantedObject.class, 1);
                assertNotNull(obj1);

                TenantedObject obj2 = (TenantedObject) em1.find(TenantedObject.class, 2);
                assertNotNull(obj2);

                tx1.commit();
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                em1.close();
            }

            // Retrieve all objects for second tenant
            em2 = emfTenant2.createEntityManager();
            tx2 = em2.getTransaction();
            try
            {
                tx2.begin();

                // Check basic retrieve of expect objects
                TenantedObject obj1 = (TenantedObject) em2.find(TenantedObject.class, 1);
                assertNotNull(obj1);

                TenantedObject obj2 = (TenantedObject) em2.find(TenantedObject.class, 2);
                assertNotNull(obj2);

                tx2.commit();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                em2.close();
            }
        }
        finally
        {
            // Clear data
            clean(emfTenant1, TenantedObject.class);
            clean(emfTenant2, TenantedObject.class);

            // Close EMFs
            emfTenant1.close();
            emfTenant2.close();
        }
    }

    /**
     * Test tenantReadIds specification via MultitenancyProvider
     */
    // TODO
}