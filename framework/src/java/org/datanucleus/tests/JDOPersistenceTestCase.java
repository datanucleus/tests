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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.Configuration;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.enhancement.Persistable;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.StringUtils;

/**
 * Abstract base class for all JDO unit tests needing access to a persistence manager factory.
 */
public abstract class JDOPersistenceTestCase extends PersistenceTestCase
{
    private static final int MAX_CLEANUP_ATTEMPTS = 10;

    /** The PersistenceManagerFactory to use for all tests. */
    protected static PersistenceManagerFactory pmf;

    /** Local PersistenceManager. */
    protected PersistenceManager pm = null;

    private Set<PersistenceManager> managedPms = new HashSet<>();

    private Set<Object> idsToCleanup = new LinkedHashSet<>();

    private Map<String, Consumer<PersistenceManager>> cleanersByPrefix = new HashMap<>();

    /**
     * Allow tests with no Constructor
     */
    public JDOPersistenceTestCase()
    {
        init(null);
    }

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

    protected static synchronized void init(Properties userProps)
    {
        if (pmf == null && initOnCreate)
        {
            // Get a new PMF
            getPMF(userProps);
        }
    }

    /**
     * Method to obtain the PMF to use allowing specification of custom user PMF properties. Creates a new PMF
     * on each call.
     * @param userProps The custom PMF props to use when creating the PMF
     * @return The PMF (also stored in the local "pmf" variable)
     */
    protected static synchronized PersistenceManagerFactory getPMF(Properties userProps)
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
        storeMgr = ((JDOPersistenceManagerFactory) pmf).getNucleusContext().getStoreManager();
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        try
        {
            clr.classForName("org.datanucleus.store.rdbms.RDBMSStoreManager");
            if (storeMgr instanceof org.datanucleus.store.rdbms.RDBMSStoreManager)
            {
                // RDBMS datastores have a vendor id
                vendorID = ((org.datanucleus.store.rdbms.RDBMSStoreManager) storeMgr).getDatastoreAdapter().getVendorID();
            }
        }
        catch (ClassNotResolvedException cnre)
        {
        }

        return pmf;
    }

    /**
     * Method to obtain the PMF to use. Creates a new PMF on each call.
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

    protected PersistenceManager newPM()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.addInstanceLifecycleListener(new AutoCleanupListener(), Object.class);
        managedPms.add(pm);
        return pm;
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
    protected void clean(Class<?> cls)
    {
        clean(pmf, cls);
    }

    /**
     * Convenience method to help cleaning the database in teardown
     * @param pmf PersistenceManagerFactory from which to remove the instances
     * @param cls The class whose instances to remove
     */
    protected void clean(PersistenceManagerFactory pmf, Class<?> cls)
    {
        TestHelper.clean(pmf, cls);
    }

    protected Configuration getConfigurationForPMF(PersistenceManagerFactory pmf)
    {
        return ((JDOPersistenceManagerFactory) pmf).getNucleusContext().getConfiguration();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        closeManagedPms();
        // Enable this if you don't bother cleaning each test manually. Can result in warnings in the log if enabled!
//        cleanup(idsToCleanup);
    }

    private void closeManagedPms()
    {
        for (PersistenceManager pm : managedPms)
        {
            if (!pm.isClosed())
            {
                Transaction tx = pm.currentTransaction();
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
    }

    protected final void cleanWith(String prefix, Consumer<PersistenceManager> cleaner)
    {
        cleanersByPrefix.put(prefix,cleaner);
    }

    protected void cleanup(Set<Object> toCleanup) throws Exception
    {
        // Keep track of what is being deleted so we don't need to
        // delete what has already been deleted by the cascade.
        Set<Object> deleted = new HashSet<>();
        DeleteLifecycleListener listener = new DeleteLifecycleListener()
        {
            @Override
            public void preDelete(InstanceLifecycleEvent event)
            {
                // Nothing to do
            }

            @Override
            public void postDelete(InstanceLifecycleEvent event)
            {
                Object id = ((Persistable) event.getSource()).dnGetObjectId();
                deleted.add(id);
            }
        };

        try (PersistenceManager pm = pmf.getPersistenceManager())
        {
            pm.addInstanceLifecycleListener(listener, Object.class);
            Set<Object> toDelete = toCleanup;
            Set<Object> retry = new HashSet<>();

            int attempts = 0;
            do
            {
                // Try to delete 
                for (Object id : toDelete)
                {
                    try
                    {
                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Cleaning up: " + id);
                        }
                        if (deleted.contains(id))
                        {
                            if (LOG.isDebugEnabled())
                            {
                                LOG.debug("Already deleted: " + id);
                            }
                        }
                        else
                        {
                            Object object = pm.getObjectById(id, false);
                            boolean cleanerRun = false;

                            for (Entry<String, Consumer<PersistenceManager>> entry : cleanersByPrefix.entrySet())
                            {
                                if (object.getClass().getName().startsWith(entry.getKey()))
                                {
                                    entry.getValue().accept(pm);
                                    cleanerRun = true;
                                    break;
                                }
                            }

                            if (!cleanerRun)
                            {
                                LOG.debug(">> calling deletePersistent on " + StringUtils.toJVMIDString(object) + " state=" + JDOHelper.getObjectState(object));
                                pm.deletePersistent(object);
                            }
                            if (LOG.isDebugEnabled())
                            {
                                LOG.debug("Cleaned " + id + " successfully");
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        LOG.warn("Exception in delete process", e);
                        if (LOG.isDebugEnabled())
                        {
                            LOG.debug("Failed, retrying later: " + id);
                        }
                        retry.add(id);
                    }
                }

                attempts++;
                if (!retry.isEmpty() && attempts > MAX_CLEANUP_ATTEMPTS)
                {
                    // Give up
                    retry.clear();
//                    throw new Exception("Fail to cleanup the following object(s) after " + attempts + " attempts: " + retry);
                }

                // Try again
                toDelete.clear();
                toDelete.addAll(retry);
                retry.clear();
            }
            while (!toDelete.isEmpty());
        }
    }
    
    /*
     * Keep track of what is stored and deleted by the tests so we know what to delete later.
     * It keeps track of ids instead of extent/class only in order to be easier to know when
     * everything has been clean up when using custom cleaners. 
     */
    private class AutoCleanupListener implements StoreLifecycleListener, DeleteLifecycleListener
    {
        @Override
        public void preStore(InstanceLifecycleEvent event)
        {
            // Nothing to do
        }

        @Override
        public void postStore(InstanceLifecycleEvent event)
        {
            Object source = event.getSource();
            Object id = ((Persistable) source).dnGetObjectId();
            idsToCleanup.add(id);
        }

        @Override
        public void preDelete(InstanceLifecycleEvent event)
        {
            // Nothing to do
        }

        @Override
        public void postDelete(InstanceLifecycleEvent event)
        {
            Object id = ((Persistable) event.getSource()).dnGetObjectId();
            idsToCleanup.remove(id);
        }
    }
}