package org.datanucleus.tests.enlistedobjectcache;

import org.datanucleus.ExecutionContext;
import org.datanucleus.ExecutionContextImpl;
import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.enhancement.Persistable;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.junit.Assert;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.util.Collection;
import java.util.Properties;

public class EnlistedObjectCacheTest extends JDOPersistenceTestCase
{
    /**
     * Test having other than default enlisted SM cache in ExecutionContext.
     */
    public void testHavingOwnEnlistedObjectCache()
    {
        Properties userProps = new Properties();
        userProps.put("javax.jdo.option.Optimistic", "true");
        userProps.setProperty(PropertyNames.PROPERTY_EXECUTION_CONTEXT_ENLISTED_CACHE_FACTORY_CLASS, EnlistedObjectCacheTestFactory.class.getName());

        final int testNumber = 1;
        PersistenceManagerFactory pmfForTest = getPMF(testNumber, userProps);

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                final ExecutionContext ec = ((JDOPersistenceManager) pm).getExecutionContext();
                int countBefore = EnlistedObjectCacheTestSMCache.getValuesCount;
                final Collection<DNStateManager> enlistedSMCacheValues = ((ExecutionContextImpl) ec).getEnlistedSMCacheValues();
                Assert.assertTrue("Enlisted object cache should be empty", enlistedSMCacheValues.isEmpty());
                Assert.assertEquals("Enlisted cache getValues count should be increased", countBefore + 1, EnlistedObjectCacheTestSMCache.getValuesCount);
                tx.begin();

                int no = 0;
                final Address pc = new Address(no++);
                pc.setAddressLine("1313 Webfoot Walk");
                pm.makePersistent(pc);

                final DNStateManager stateManager = ec.findStateManager(pc);
                // try contains using same enlisted SM cache object as retrieved above - to prove it is backed
                // by live cache - and not a defensive copy - for performance reasons.
                Assert.assertTrue("StateManager expected to be found in enlisted object cache", enlistedSMCacheValues.contains(stateManager));
                // try iterate
                boolean found[] = {false};
                for (DNStateManager sm : enlistedSMCacheValues)
                {
                    if (sm.equals(stateManager))
                    {
                        found[0] = true;
                        break;
                    }
                }
                assertEquals("StateManager expected to be found in enlisted object cache using iterator", true, found[0]);
                // try forEach
                found[0] = false;
                enlistedSMCacheValues.forEach(sm ->
                {
                    if (sm.equals(stateManager))
                    {
                        found[0] = true;
                    }
                });
                assertEquals("StateManager expected to be found in enlisted object cache using forEach", true, found[0]);

                tx.commit();
                tx.begin();

                pm.deletePersistent(pc);
                tx.commit();

            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing lock-manager test : " + e.getMessage());
        }
        finally
        {
            // Clean out our data
            clean(pmfForTest, Address.class);
        }
    }
}
