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

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.validation.ConstraintViolationException;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.multitenancy.TenantedObject;

/**
 * Tests for multitenancy with JDO.
 */
public class MultitenancyTest extends JDOPersistenceTestCase
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
        PersistenceManagerFactory pmfTenant1 = getPMF(1, userProps);

        Properties userProps2 = new Properties();
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID2");
        PersistenceManagerFactory pmfTenant2 = getPMF(1, userProps2);

        try
        {
            // Persist object for first tenant
            PersistenceManager pm1 = pmfTenant1.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            Object id1 = null;
            try
            {
                tx1.begin();
                TenantedObject o1 = new TenantedObject();
                o1.setId(1);
                o1.setName("First");
                pm1.makePersistent(o1);
                tx1.commit();
                id1 = pm1.getObjectId(o1);
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
                pm1.close();
            }

            // Persist object for second tenant
            PersistenceManager pm2 = pmfTenant2.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            Object id2 = null;
            try
            {
                tx2.begin();
                TenantedObject o2 = new TenantedObject();
                o2.setId(2);
                o2.setName("Second");
                pm2.makePersistent(o2);
                tx2.commit();
                id2 = pm2.getObjectId(o2);
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
                pm2.close();
            }

            // Retrieve all objects for first tenant
            pm1 = pmfTenant1.getPersistenceManager();
            tx1 = pm1.currentTransaction();
            try
            {
                tx1.begin();

                // Check basic retrieve of expect object
                TenantedObject obj1 = (TenantedObject) pm1.getObjectById(id1, true);
                assertNotNull(obj1);
                
                try
                {
                    pm1.getObjectById(id2, true);
                    fail("Returned object 2 from tenant 1 but shouldn't have");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                tx1.commit();
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Retrieve all objects for second tenant
            pm2 = pmfTenant2.getPersistenceManager();
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                // Check basic retrieve of expect object
                TenantedObject obj2 = (TenantedObject) pm2.getObjectById(id2, true);
                assertNotNull(obj2);
                
                try
                {
                    pm2.getObjectById(id1, true);
                    fail("Returned object 1 from tenant 2 but shouldn't have");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                tx2.commit();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            // Clear data
            clean(pmfTenant1, TenantedObject.class);
            clean(pmfTenant2, TenantedObject.class);

            // Close PMFs
            pmfTenant1.close();
            pmfTenant2.close();
        }
    }

    /**
     * Test tenantId specification via MultitenancyProvider
     */
    // TODO

    /**
     * Test tenantReadIds specification via persistence property
     */
    public void testTenantIdWithReadIds()
    {
        Properties userProps = new Properties();
        userProps.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID");
        userProps.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_READ_IDS, "MyID,MyID2");
        PersistenceManagerFactory pmfTenant1 = getPMF(1, userProps);

        Properties userProps2 = new Properties();
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_ID, "MyID2");
        userProps2.setProperty(PropertyNames.PROPERTY_MAPPING_TENANT_READ_IDS, "MyID,MyID2");
        PersistenceManagerFactory pmfTenant2 = getPMF(1, userProps2);

        try
        {
            // Persist object for first tenant
            PersistenceManager pm1 = pmfTenant1.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            Object id1 = null;
            try
            {
                tx1.begin();
                TenantedObject o1 = new TenantedObject();
                o1.setId(1);
                o1.setName("First");
                pm1.makePersistent(o1);
                tx1.commit();
                id1 = pm1.getObjectId(o1);
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
                pm1.close();
            }

            // Persist object for second tenant
            PersistenceManager pm2 = pmfTenant2.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            Object id2 = null;
            try
            {
                tx2.begin();
                TenantedObject o2 = new TenantedObject();
                o2.setId(2);
                o2.setName("Second");
                pm2.makePersistent(o2);
                tx2.commit();
                id2 = pm2.getObjectId(o2);
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
                pm2.close();
            }

            // Retrieve all objects for first tenant
            pm1 = pmfTenant1.getPersistenceManager();
            tx1 = pm1.currentTransaction();
            try
            {
                tx1.begin();

                // Check basic retrieve of expect objects
                TenantedObject obj1 = (TenantedObject) pm1.getObjectById(id1, true);
                assertNotNull(obj1);

                TenantedObject obj2 = (TenantedObject) pm1.getObjectById(id2, true);
                assertNotNull(obj2);

                tx1.commit();
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Retrieve all objects for second tenant
            pm2 = pmfTenant2.getPersistenceManager();
            tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                // Check basic retrieve of expect objects
                TenantedObject obj1 = (TenantedObject) pm2.getObjectById(id1, true);
                assertNotNull(obj1);

                TenantedObject obj2 = (TenantedObject) pm2.getObjectById(id2, true);
                assertNotNull(obj2);

                tx2.commit();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            // Clear data
            clean(pmfTenant1, TenantedObject.class);
            clean(pmfTenant2, TenantedObject.class);

            // Close PMFs
            pmfTenant1.close();
            pmfTenant2.close();
        }
    }

    /**
     * Test tenantReadIds specification via MultitenancyProvider
     */
    // TODO
}