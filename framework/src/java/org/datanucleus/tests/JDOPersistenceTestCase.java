/******************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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
2003 Erik Bengtson - cleanup
2004 Andy Jefferson - rationalisation of pmf creation methods
    ...
*****************************************************************/
package org.datanucleus.tests;

import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.Configuration;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.exceptions.ClassNotResolvedException;

/**
 * Abstract base class for all JDO unit tests needing access to a persistence manager factory.
 */
public abstract class JDOPersistenceTestCase extends PersistenceTestCase
{
    /** The PersistenceManagerFactory to use for all tests. */
    protected static PersistenceManagerFactory pmf;

    /** Local PersistenceManager. */
    protected PersistenceManager pm = null;

    public JDOPersistenceTestCase(String name)
    {
        this(name, null);
        init(null);
    }

    public JDOPersistenceTestCase(String name, Properties userProps)
    {
        super(name);
        init(userProps);
    }

    protected synchronized void init(Properties userProps)
    {
        if (pmf == null && initOnCreate)
        {
            // Get a new PMF
            getPMF(userProps);
        }
    }

    /**
     * Method to obtain the PMF to use allowing specification of custom user PMF properties.
     * Creates a new PMF on each call.
     * @param userProps The custom PMF props to use when creating the PMF
     * @return The PMF (also stored in the local "pmf" variable)
     */
    protected synchronized PersistenceManagerFactory getPMF(Properties userProps)
    {
        if (pmf != null)
        {
            if (!pmf.isClosed())
            {
                // Close the current PMF first
                pmf.close();
            }
        }
        pmf = TestHelper.getPMF(1, userProps);
        TestHelper.freezePMF(pmf);

        // Set up the StoreManager
        storeMgr = ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getStoreManager();
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

        return pmf;
    }

    /**
     * Method to obtain the PMF to use.
     * Creates a new PMF on each call.
     * @return The PMF (also stored in the local "pmf" variable)
     */
    protected synchronized PersistenceManagerFactory getPMF()
    {
        return getPMF(null);
    }

    protected synchronized void closePMF()
    {
        pmf.close();
        storeMgr = null;
        vendorID = null;
        pmf = null;
    }

    /**
     * Method to obtain a new PM using the PMF.
     * @return The PersistenceManager (also stored in the local "pm" variable)
     */
    protected PersistenceManager getPM()
    {
        pm = pmf.getPersistenceManager();
        return pm;
    }

    /**
     * Convenience method to help cleaning the database in teardown
     * @param cls The class whose instances to remove
     */
    protected void clean(Class cls)
    {
        clean(pmf, cls);
    }

    /**
     * Convenience method to help cleaning the database in teardown
     * @param pmf PersistenceManagerFactory from which to remove the instances
     * @param cls The class whose instances to remove
     */
    protected void clean(PersistenceManagerFactory pmf, Class cls)
    {
        TestHelper.clean(pmf,cls);
    }

    protected Configuration getConfigurationForPMF(PersistenceManagerFactory pmf)
    {
        return ((JDOPersistenceManagerFactory)pmf).getNucleusContext().getConfiguration();
    }
}