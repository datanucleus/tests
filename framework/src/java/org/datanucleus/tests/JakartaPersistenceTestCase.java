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

import java.util.List;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.metadata.MetaDataUtils;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.util.NucleusLogger;

/**
 * Abstract base class for all JPA unit tests needing access to an entity manager factory.
 */
public abstract class JakartaPersistenceTestCase extends PersistenceTestCase
{
    /** The EntityManagerFactory to use for all tests. */
    protected static EntityManagerFactory emf;

    /** Local EntityManager. */
    protected EntityManager em = null;

    public JakartaPersistenceTestCase(String name)
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
            try
            {
                // Get a new EMF
                emf = getEMF(null);
            }
            catch (Throwable thr)
            {
                NucleusLogger.GENERAL.error("Exception creating EMF", thr);
                throw thr;
            }
        }
    }

    /**
     * Method to return a EMF for the specified datastore number, adding on the user-provided properties
     * @param number Number of the datastore (equates to a property file in the CLASSPATH)
     * @param unitName Name of the persistence-unit to use (if any - defaults to TEST)
     * @param userProps The user properties (null if not required)
     * @return The EMF
     */
    public static EntityManagerFactory getEMF(int number, String unitName, Properties userProps)
    {
        if (unitName == null)
        {
            unitName = "TEST";
        }
        return jakarta.persistence.Persistence.createEntityManagerFactory(unitName, TestHelper.getFactoryProperties(number, userProps));
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
        emf = getEMF(1, unitName, userProps);
        storeMgr = emf.unwrap(PersistenceNucleusContext.class).getStoreManager();

        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        try
        {
            clr.classForName("org.datanucleus.store.rdbms.RDBMSStoreManager");
            if (storeMgr instanceof org.datanucleus.store.rdbms.RDBMSStoreManager)
            {
                // RDBMS datastores have a vendor id
                rdbmsVendorID = ((org.datanucleus.store.rdbms.RDBMSStoreManager)storeMgr).getDatastoreAdapter().getVendorID();
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
        cleanClassForEMF(emf, cls);
    }

    /**
     * Convenience method to remove all objects of the passed class in JPA.
     * @param emf EntityManagerFactory
     * @param cls The class
     */
    public static void cleanClassForEMF(EntityManagerFactory emf, Class cls)
    {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            List result = em.createQuery("SELECT T FROM " + cls.getName() + " T").getResultList();
            LOG.debug("Cleanup : Number of objects of type " + cls.getName() + " to delete is " + result.size());
            for (int i = 0; i < result.size(); i++)
            {
                em.remove(result.get(i));
            }
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
    }

    /**
     * Convenience method to extract the metadata for a persistence-unit.
     * @param nucleusCtx NucleusContext
     * @param persistenceUnitName Name of the persistence-unit
     * @return MetaData for the persistence-unit (or null if not found)
     */
    public PersistenceUnitMetaData getMetaDataForPersistenceUnit(NucleusContext nucleusCtx, String persistenceUnitName)
    {
        String filename = nucleusCtx.getConfiguration().getStringProperty(PropertyNames.PROPERTY_PERSISTENCE_XML_FILENAME);
        boolean validateXML = nucleusCtx.getConfiguration().getBooleanProperty(PropertyNames.PROPERTY_METADATA_XML_VALIDATE);
        boolean supportXMLNamespaces = nucleusCtx.getConfiguration().getBooleanProperty(PropertyNames.PROPERTY_METADATA_XML_NAMESPACE_AWARE);
        ClassLoaderResolver clr = nucleusCtx.getClassLoaderResolver(null);
        return MetaDataUtils.getMetaDataForPersistenceUnit(nucleusCtx.getPluginManager(), filename, persistenceUnitName, validateXML, supportXMLNamespaces, clr);
    }
}