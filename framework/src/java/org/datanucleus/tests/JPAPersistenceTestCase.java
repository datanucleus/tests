/******************************************************************
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
*****************************************************************/
package org.datanucleus.tests;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.exceptions.ClassNotResolvedException;

/**
 * Abstract base class for all JPA unit tests needing access to an entity manager factory.
 */
public abstract class JPAPersistenceTestCase extends PersistenceTestCase
{
    /** The EntityManagerFactory to use for all tests. */
    protected static EntityManagerFactory emf;

    /** Local EntityManager. */
    protected EntityManager em = null;

    public JPAPersistenceTestCase(String name)
    {
        super(name);
        init();
    }

    /**
     * Initialisation method, called on construction.
     */
    protected synchronized void init()
    {
        if (emf == null && initOnCreate)
        {
            // Get a new EMF
            emf = getEMF(null);
        }
    }

    /**
     * Method to obtain the EMF to use allowing specification of custom user EMF properties.
     * Creates a new EMF on each call.
     * NOTE : THIS SETS THE "emf" AND "storeMgr" FIELDS.
     * @param unitName Name of the persistence unit to use (if any)
     * @param userProps The custom EMF props to use when creating the EMF
     * @return The EMF (also stored in the local "emf" variable)
     */
    protected synchronized EntityManagerFactory getEMF(String unitName, Properties userProps)
    {
        emf = TestHelper.getEMF(1, unitName, userProps);
        storeMgr = emf.unwrap(NucleusContext.class).getStoreManager();

        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        try
        {
            clr.classForName("org.datanucleus.store.rdbms.RDBMSStoreManager");
            if (storeMgr instanceof org.datanucleus.store.rdbms.RDBMSStoreManager)
            {
                // RDBMS datastores have a vendor id
                vendorID = ((org.datanucleus.store.rdbms.RDBMSStoreManager)storeMgr).getDatastoreAdapter().getVendorID();
            }
        }
        catch (ClassNotResolvedException cnre)
        {
        }

        return emf;
    }

    /**
     * Method to obtain the EMF to use.
     * Creates a new EMF on each call.
     * @param unitName Name of the persistence unit to use (if any)
     * @return The EMF (also stored in the local "emf" variable)
     */
    protected synchronized EntityManagerFactory getEMF(String unitName)
    {
        return getEMF(unitName, null);
    }

    /**
     * Method to obtain a new PM using the EMF.
     * @return The PersistenceManager (also stored in the local "pm" variable)
     */
    protected EntityManager getEM()
    {
        em = emf.createEntityManager();
        return em;
    }

    /**
     * Convenience method to help cleaning the database in teardown
     * @param cls The class whose instances to remove
     */
    protected void clean(Class cls)
    {
        clean(emf, cls);
    }

    /**
     * Convenience method to help cleaning the database in teardown
     * @param emf EntityManagerFactory from which to remove the instances
     * @param cls The class whose instances to remove
     */
    protected void clean(EntityManagerFactory emf, Class cls)
    {
        TestHelper.clean(emf, cls);
    }
}