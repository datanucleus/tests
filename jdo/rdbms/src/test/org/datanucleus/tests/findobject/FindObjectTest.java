package org.datanucleus.tests.findobject;

import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.enhancement.Persistable;
import org.datanucleus.management.ManagerStatistics;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.state.LifeCycleState;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.junit.Assume;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Properties;

public class FindObjectTest extends JDOPersistenceTestCase
{
    /**
     * Test find object from standard persistence handler in store manager
     */
    public void testFindObjectFromStandardPersistenceHandler()
    {
        Properties userProps = new Properties();
        userProps.put("javax.jdo.option.Optimistic", "true");
        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");

        final int testNumber = 1;
        PersistenceManagerFactory pmfForTest = getPMF(testNumber, userProps);

        if (!(((JDOPersistenceManagerFactory)pmfForTest).getNucleusContext().getStoreManager() instanceof RDBMSStoreManager))
        {
            return;
        }

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int no = 0;
                final Address pc = new Address(no++);
                pc.setAddressLine("1313 Webfoot Walk");
                pm.makePersistent(pc);
                final Object oid = ((Persistable)pc).dnGetObjectId();
                tx.commit();
                tx.begin();

                final ExecutionContext ec = ((JDOPersistenceManager) pm).getExecutionContext();
                final ManagerStatistics statistics = ec.getStatistics();

                int readsBefore = getTotalReads(statistics);

                // find from L1 cache - no validate
                final Persistable o1 = ec.findObject(oid, false, false, pc.getClass().getName());
                int readsNow = getTotalReads(statistics);
                assertTrue("Should be same PC instance returned from findObject", pc == o1);
                assertEquals("No reads expected reading from L1 cache - and no validate", readsBefore, readsNow);

                // find from L1 cache - with validate
                final Persistable o2 = ec.findObject(oid, true, false, pc.getClass().getName());
                assertTrue("Should be same PC instance returned from findObject", pc == o2);
                assertNotHollow(o2);

                readsNow = getTotalReads(statistics);
                assertEquals("Validate read expected reading from L1 cache - due to validate DB check", readsBefore+1, readsNow);
                readsBefore = readsNow;

                // test where nothing in L1 and L2 cache
                clearCaches(pm);
                final Persistable o3 = ec.findObject(oid, true, false, pc.getClass().getName());
                readsNow = getTotalReads(statistics);
                assertNotHollow(o3);

                assertTrue("Validate read expected reading from L1 cache - due to load from DB", readsBefore < readsNow);
                readsBefore = getTotalReads(statistics);

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

    /**
     * Test find object from persistence handler in custom persistence handler supporting findObject
     */
    public void testFindObjectFromCustomPersistenceHandler()
    {
        Properties userProps = new Properties();
        userProps.put("javax.jdo.option.Optimistic", "true");
        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");

        final int testNumber = 1;
        final Properties factoryProperties = TestHelper.getFactoryProperties(testNumber, userProps);
        String dnConnectionurl = FindObjectTestStoreManager.DN_CONNECTIONURL;
        Object url = factoryProperties.get(dnConnectionurl);
        if (url == null)
        {
            dnConnectionurl = dnConnectionurl.toLowerCase();
            factoryProperties.get(dnConnectionurl);
        }
        Assume.assumeTrue(url instanceof String && ((String)url).startsWith("jdbc"));
        userProps.setProperty(dnConnectionurl, FindObjectTestStoreManager.STOREMANAGER_PREFIX+url);
        PersistenceManagerFactory pmfForTest = getPMF(testNumber, userProps);

        if (!(((JDOPersistenceManagerFactory)pmfForTest).getNucleusContext().getStoreManager() instanceof RDBMSStoreManager))
        {
            return;
        }

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int no = 0;
                final Address pc = new Address(no++);
                pc.setAddressLine("1313 Webfoot Walk");
                pm.makePersistent(pc);
                final Object oid = ((Persistable)pc).dnGetObjectId();
                tx.commit();
                tx.begin();

                final ExecutionContext ec = ((JDOPersistenceManager) pm).getExecutionContext();
                final ManagerStatistics statistics = ec.getStatistics();

                int readsBefore = getTotalReads(statistics);

                // find from L1 cache - no validate
                final Persistable o1 = ec.findObject(oid, false, false, pc.getClass().getName());
                int readsNow = getTotalReads(statistics);
                assertTrue("Should be same PC instance returned from findObject", pc == o1);
                assertEquals("No reads expected reading from L1 cache - and no validate", readsBefore, readsNow);

                // find from L1 cache - with validate
                final Persistable o2 = ec.findObject(oid, true, false, pc.getClass().getName());
                assertTrue("Should be same PC instance returned from findObject", pc == o2);
                assertNotHollow(o1);

                readsNow = getTotalReads(statistics);
                assertEquals("Validate read expected reading from L1 cache - due to validate DB check", readsBefore+1, readsNow);
                readsBefore = readsNow;

                // test where nothing in L1 and L2 cache - but PersistenceHandler is able to provide object
                ((FindObjectTestPersistenceHandler)ec.getStoreManager().getPersistenceHandler()).setNextFindObject((Address) o2);
                clearCaches(pm);
                final Persistable o3 = ec.findObject(oid, true, false, pc.getClass().getName());
                readsNow = getTotalReads(statistics);
                assertNotHollow(o3);

                assertEquals("Validate read NOT expected as read from persistence handler - no need to validate in DB again", readsBefore, readsNow);
                readsBefore = getTotalReads(statistics);

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

    /**
     * Test find object used for instance in JDOQuery - ignoring cache
     */
    public void testFindObjectUsedFromJDOQuery()
    {
        Properties userProps = new Properties();
        userProps.put("javax.jdo.option.Optimistic", "true");
        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");

        final int testNumber = 1;
        PersistenceManagerFactory pmfForTest = getPMF(testNumber, userProps);

        if (!(((JDOPersistenceManagerFactory)pmfForTest).getNucleusContext().getStoreManager() instanceof RDBMSStoreManager))
        {
            return;
        }

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int no = 0;
                final Address pc = new Address(no++);
                pc.setAddressLine("1313 Webfoot Walk");
                pm.makePersistent(pc);
                pm.flush();

                // test JDO query finds same instance - so that we dont end up with two PC instances for same instance
                {
                    Object res = pm.newQuery(Address.class)
                            .filter("id==:theid")
                            .executeWithArray(no - 1);
                    Address foundAddress = ((List<Address>) res).get(0);
                    assertEquals("Expects to find same instance", pc, foundAddress);
                }

                // test JDO query finds same instance - so that we dont end up with two PC instances for same instance
                // Even if we ignore cache this must not happen
                {
                    final Query<Address> query = pm.newQuery(Address.class)
                            .filter("id==:theid");
                    query.setIgnoreCache(true);
                    Object res = query.executeWithArray(no - 1);
                    Address foundAddress = ((List<Address>) res).get(0);
                    assertEquals("Expects to find same instance", pc, foundAddress);
                }

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

    /**
     * Test find object used for instance in JDOQuery - ignoring cache
     */
    public void testFindObjectUsedFromJDOQueryCallingPersistenceHandler()
    {
        Properties userProps = new Properties();
        userProps.put("javax.jdo.option.Optimistic", "true");
        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");

        final int testNumber = 1;
        final Properties factoryProperties = TestHelper.getFactoryProperties(testNumber, userProps);
        String dnConnectionurl = FindObjectTestStoreManager.DN_CONNECTIONURL;
        Object url = factoryProperties.get(dnConnectionurl);
        if (url == null)
        {
            dnConnectionurl = dnConnectionurl.toLowerCase();
            factoryProperties.get(dnConnectionurl);
        }
        Assume.assumeTrue(url instanceof String && ((String)url).startsWith("jdbc"));
        userProps.setProperty(dnConnectionurl, FindObjectTestStoreManager.STOREMANAGER_PREFIX+url);
        PersistenceManagerFactory pmfForTest = getPMF(testNumber, userProps);

        if (!(((JDOPersistenceManagerFactory)pmfForTest).getNucleusContext().getStoreManager() instanceof RDBMSStoreManager))
        {
            return;
        }

        try
        {
            PersistenceManager pm = pmfForTest.getPersistenceManager();
            final Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int no = 0;
                final Address pc = new Address(no++);
                pc.setAddressLine("1313 Webfoot Walk");
                pm.makePersistent(pc);
                tx.commit();
                tx.begin();

                final String addressString = getAddressString(pc);

                clearCaches(pm); // clear both L1 and L2 cache
                final ExecutionContext ec = ((JDOPersistenceManager) pm).getExecutionContext();
                final FindObjectTestPersistenceHandler persistenceHandler = (FindObjectTestPersistenceHandler)ec.getStoreManager().getPersistenceHandler();

                int callCountBefore = persistenceHandler.getCallCount();
                // Test JDO query finds equivalent instance (not same as L1 cache is cleared)
                // AND that is do not call PersistenceHandler when it has all values from FieldValues
                {
                    Object res = pm.newQuery(Address.class)
                            .filter("id==:theid")
                            .executeWithArray(no - 1);
                    Address foundAddress = ((List<Address>) res).get(0);
                    assertEquals("Expects to find same content", addressString, getAddressString(foundAddress));
                }

                // Test JDO query finds equivalent instance (not same as L1 cache is cleared)
                // AND that is do not call PersistenceHandler when it has all values from FieldValues
                // Even if we ignore cache this must not happen
                clearCaches(pm); // clear both L1 and L2 cache
                {
                    final Query<Address> query = pm.newQuery(Address.class)
                            .filter("id==:theid");
                    query.setIgnoreCache(true);
                    Object res = query.executeWithArray(no - 1);
                    Address foundAddress = ((List<Address>) res).get(0);
                    assertEquals("Expects to find same content", addressString, getAddressString(foundAddress));
                }
                int callCountAfter = persistenceHandler.getCallCount();
                assertEquals("Persistence handler not expected to be called when FieldValues are used for finding object", callCountBefore, callCountAfter);

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

    private void assertNotHollow(Persistable o3)
    {
        assertFalse("Object should not be hollow - after findObject with validate which triggers a read which in turn transitions to NonTransactional)", isHollow(o3));
    }

    private boolean isHollow(Persistable o1)
    {
        final LifeCycleState lifecycleState = ((DNStateManager) o1.dnGetStateManager()).getLifecycleState();
        return LifeCycleState.HOLLOW == lifecycleState.stateType();
    }

    private static int getTotalReads(ManagerStatistics statistics)
    {
        return statistics.getNumberOfDatastoreReads();
    }

    private String getAddressString(Address address)
    {
        return address == null ? "<null>" :
                address.getClass().getSimpleName() + "{" +
                        "; addressLine=" + address.getAddressLine() +
                        "; type=" + address.getType() +
                        "}";
    }

    private void clearCaches(PersistenceManager pm)
    {
        pm.evictAll();
        pm.getPersistenceManagerFactory().getDataStoreCache().evictAll();
    }
}
