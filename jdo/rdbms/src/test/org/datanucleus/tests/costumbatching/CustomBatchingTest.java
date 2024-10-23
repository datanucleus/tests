package org.datanucleus.tests.costumbatching;

import org.datanucleus.ExecutionContext;
import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.enhancement.Persistable;
import org.datanucleus.management.ManagerStatistics;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.samples.models.transportation.FemaleDriver;
import org.datanucleus.samples.models.transportation.MaleDriver;
import org.datanucleus.samples.models.transportation.RobotDriver;
import org.datanucleus.state.DNStateManager;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ManagedConnectionWithListenerAccess;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOOptimisticVerificationException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomBatchingTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public CustomBatchingTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                    {
                    });
            initialised = true;
        }
    }

    /**
     * Test that are actually batch inserting, updating and deleting when requested.
     */
    public void testBatchingWithInsertUpdateDelete()
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_FLUSH_PROCESS_CLASS, CustomBatchingFlushProcess.class.getName());
        userProps.put("javax.jdo.option.Optimistic", "true");

        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        PersistenceManager pm = pmfForTest.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // ----------Test batch in INSERT
            // create drivers and addresses
            final String insertConstant="INSERT_VAL";
            Driver driver1 = new RobotDriver(++idseq);
            driver1.setName("R2D2"+insertConstant);

            Address address1a = new Address(++idseq);
            address1a.setAddressLine("123 Robo Street"+insertConstant);
            driver1.setSimpleAddress(address1a);
            Address address1b = new Address(++idseq);
            address1b.setAddressLine("123 Robo Street Away"+insertConstant);

            Driver driver2 = new FemaleDriver(++idseq);
            driver2.setName("Eva"+insertConstant);

            Address address2a = new Address(++idseq);
            address2a.setAddressLine("123 Skyline"+insertConstant);
            driver2.setSimpleAddress(address2a);
            Address address2b = new Address(++idseq);
            address2b.setAddressLine("123 Skyline Away"+insertConstant);

            Driver driver3 = new MaleDriver(++idseq);
            driver3.setName("Adam"+insertConstant);

            driver3.setSimpleAddress(address2a);

            final ManagerStatistics statistics = ((JDOPersistenceManager) pm).getExecutionContext().getStatistics();

            // on purpose make persistent in random order
            pm.makePersistent(address1a);
            pm.makePersistent(driver1);
            pm.makePersistent(address1b);
            pm.makePersistent(address2b);
            pm.makePersistent(driver3);
            pm.makePersistent(driver2);
            pm.makePersistent(address2a);

            String driver1String = getDriverString(driver1);
            String driver2String = getDriverString(driver2);
            String driver3String = getDriverString(driver3);
            String address1aString = getAddressString(address1a);
            String address1bString = getAddressString(address1b);
            String address2aString = getAddressString(address2a);
            String address2bString = getAddressString(address2b);

            int countBeforeInsert = statistics.getNumberOfDatastoreWrites();

            tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking

            // We only expect ONE batch insert into driver table + ONE batch insert into address table
            final int countAfterInsert = statistics.getNumberOfDatastoreWrites();
            assertEquals("Batching not properly kicked in for insert ", countBeforeInsert+2, countAfterInsert);

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertDriversByQuery(pm, Set.of(driver1String, driver2String, driver3String));
            assertAddressesByQuery(pm, Set.of(address1aString, address1bString, address2aString, address2bString));

            //---------- Test batch in Update
            final String updateConstant="UPDATE_VAL";
            address1a.setAddressLine(address1a.getAddressLine().replaceAll(insertConstant, updateConstant));
            driver1.setName(driver1.getName().replaceAll(insertConstant, updateConstant));
            address2b.setAddressLine(address2b.getAddressLine().replaceAll(insertConstant, updateConstant));
            driver3.setName(driver3.getName().replaceAll(insertConstant, updateConstant));
            address1b.setAddressLine(address1b.getAddressLine().replaceAll(insertConstant, updateConstant));
            driver2.setName(driver2.getName().replaceAll(insertConstant, updateConstant));
            address2a.setAddressLine(address2a.getAddressLine().replaceAll(insertConstant, updateConstant));

            tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking

            // We only expect ONE batch update in driver table + ONE batch update in address table
            final int countAfterUpdate = statistics.getNumberOfDatastoreWrites();
            assertEquals("Batching not properly kicked in for update ", countAfterInsert+2, countAfterUpdate);


            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all updated
            driver1String = driver1String.replaceAll(insertConstant, updateConstant);
            driver2String = driver2String.replaceAll(insertConstant, updateConstant);
            driver3String = driver3String.replaceAll(insertConstant, updateConstant);
            address1aString = address1aString.replaceAll(insertConstant, updateConstant);
            address1bString = address1bString.replaceAll(insertConstant, updateConstant);
            address2aString = address2aString.replaceAll(insertConstant, updateConstant);
            address2bString = address2bString.replaceAll(insertConstant, updateConstant);

            assertDriversByQuery(pm, Set.of(driver1String, driver2String, driver3String));
            assertAddressesByQuery(pm, Set.of(address1aString, address1bString, address2aString, address2bString));

            //---------- Test batch in DELETE
            pm.deletePersistent(driver2);
            pm.deletePersistent(address2b);
            pm.deletePersistent(address1b);
            pm.deletePersistent(driver3);
            pm.deletePersistent(address2a);
            pm.deletePersistent(driver1);
            pm.deletePersistent(address1a);
            // Below is a bit of a hack to avoid batching being ruined by intermediate queries fetching unloaded
            // relation fields during deletion - so we pre-load them here just to see batch delete kick-in fully.
            ((DNStateManager)((Persistable)driver1).dnGetStateManager()).loadUnloadedRelationFields();
            ((DNStateManager)((Persistable)driver2).dnGetStateManager()).loadUnloadedRelationFields();
            ((DNStateManager)((Persistable)driver3).dnGetStateManager()).loadUnloadedRelationFields();
            tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking

            // We only expect ONE batch delete in driver table + ONE batch delete in address table
            final int countAfterDelete = statistics.getNumberOfDatastoreWrites();
            assertEquals("Batching not properly kicked in for delete ", countAfterUpdate+2, countAfterDelete);

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all deleted
            assertDriversByQuery(pm, Set.of());
            assertAddressesByQuery(pm, Set.of());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(pmfForTest, Driver.class);
            clean(pmfForTest, Address.class);
        }
    }


    /**
     * Test SQLException still thrown when batch inserting
     */
    public void testSQLExceptionWithBatchInsert()
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_FLUSH_PROCESS_CLASS, CustomBatchingFlushProcess.class.getName());
        userProps.put("javax.jdo.option.Optimistic", "true");

        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        PersistenceManager pm = pmfForTest.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // ----------Test batch in INSERT
            // create drivers and addresses
            final String insertConstant="INSERT_VAL";
            Driver driver1 = new FemaleDriver(++idseq);
            driver1.setName("R2D2"+insertConstant);
            Driver driver2 = new FemaleDriver(++idseq);
            driver2.setName("Eva"+insertConstant);
            Driver driver3 = new FemaleDriver(idseq); // On purpose not incrementing id here - to get Unique Constraint error on batch insert
            driver3.setName("Adam"+insertConstant);

            final ManagerStatistics statistics = ((JDOPersistenceManager) pm).getExecutionContext().getStatistics();

            pm.makePersistent(driver1);
            pm.makePersistent(driver2);
            pm.makePersistent(driver3);

            int countBeforeInsert = statistics.getNumberOfDatastoreWrites();

            try
            {
                tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking
                fail("Commit should have errored with Unique Constraint error trying to insert two rows with same ID");
            } catch (JDODataStoreException e) {
                LOG.info("Got expected exception", e);
                // expected
            }

            // We only expect ONE batch insert into driver table
            final int countAfterInsert = statistics.getNumberOfDatastoreWrites();
            assertEquals("Batching not properly kicked in for insert ", countBeforeInsert+1, countAfterInsert);

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test nothing was actually inserted due exception
            assertDriversByQuery(pm, Set.of());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(pmfForTest, Driver.class);
            clean(pmfForTest, Address.class);
        }
    }

    /**
     * Test optimistic lock exception with batch updates and batch deletes
     */
    public void testOptimisticLockExceptionWithNoBatchUpdateAndDelete()
    {
        checkOptimisticLockExceptionWithBatchUpdateAndDelete(false);
    }

    /**
     * Test optimistic lock exception with batch updates and batch deletes
     */
    public void testOptimisticLockExceptionWithBatchUpdateAndDelete()
    {
        checkOptimisticLockExceptionWithBatchUpdateAndDelete(true);
    }
    private void checkOptimisticLockExceptionWithBatchUpdateAndDelete(boolean batchingFlusher)
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        if (batchingFlusher)
            userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_FLUSH_PROCESS_CLASS, CustomBatchingFlushProcess.class.getName());
        userProps.put("javax.jdo.option.Optimistic", "true");

        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        PersistenceManager pm = pmfForTest.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // ----------Test batch in INSERT
            // create addresses that use optimistic locking
            Address address1 = new Address(++idseq);
            address1.setAddressLine("1 Skydrive");
            Address address2 = new Address(++idseq);
            address2.setAddressLine("2 Skydrive");
            Address address3 = new Address(++idseq);
            address3.setAddressLine("3 Skydrive");

            final ManagerStatistics statistics = ((JDOPersistenceManager) pm).getExecutionContext().getStatistics();

            pm.makePersistentAll(address1, address2, address3);

            String address1String = getAddressString(address1);
            String address2String = getAddressString(address2);
            String address3String = getAddressString(address3);

            int countBeforeInsert = statistics.getNumberOfDatastoreWrites();

            tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking

            // We only expect ONE batch insert into address table
            final int countAfterInsert = statistics.getNumberOfDatastoreWrites();
            if (batchingFlusher)
                assertEquals("Batching not properly kicked in for insert ", countBeforeInsert+1, countAfterInsert);

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test inserted objects
            assertAddressesByQuery(pm, Set.of(address1String, address2String, address3String));

            address1.setAddressLine("new address 1");
            address2.setAddressLine("new address 2");
            address3.setAddressLine("new address 3");

            runStmt("update address set checkid=checkid+20 where id="+idseq, true); // trigger that we will get an optimistic exception

            try
            {
                tx.commit(); // all writes deferred to commit as we are using optimistic-lock-checking
                fail("Expected optimistic lock exception during batch update");
            } catch (JDOOptimisticVerificationException e) {
                LOG.info("Got expected exception", e);
                // expected
            }

            // We only expect ONE batch update in address table
            final int countAfterUpdate = statistics.getNumberOfDatastoreWrites();
            if (batchingFlusher)
                assertEquals("Batching not properly kicked in for update ", countAfterInsert+1, countAfterUpdate);

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test NO updates was made
            assertAddressesByQuery(pm, Set.of(address1String, address2String, address3String));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query using candidate class : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(pmfForTest, Driver.class);
            clean(pmfForTest, Address.class);
        }
    }

    private void assertDriversByQuery(PersistenceManager pm, Set<String> expected)
    {
        clearCaches(pm);
        Set<String> foundDrivers = pm.newQuery(Driver.class)
                .executeList()
                .stream()
                .map(this::getDriverString)
                .collect(Collectors.toSet());
        assertEquals("Found drivers incorrect:",
                expected,
                foundDrivers);
    }

    private void assertAddressesByQuery(PersistenceManager pm, Set<String> expected)
    {
        clearCaches(pm);
        Set<String> foundAddreses = pm.newQuery(Address.class)
                .executeList()
                .stream()
                .map(this::getAddressString)
                .collect(Collectors.toSet());
        assertEquals("Found addresses incorrect:",
                expected,
                foundAddreses);
    }

    private void clearCaches(PersistenceManager pm)
    {
        pm.evictAll();
        pm.getPersistenceManagerFactory().getDataStoreCache().evictAll();
    }

    private String getDriverString(Driver driver)
    {
        return driver.getClass().getSimpleName() + "{" +
                "; name=" + driver.getName() +
                "; homeAddress=" + getAddressString(driver.getHomeAddress()) +
                "; awayAddress=" + getAddressString(driver.getAwayAddress()) +
                "}";
    }

    private String getAddressString(Address address)
    {
        return address == null ? "<null>" :
                address.getClass().getSimpleName() + "{" +
                        "; addressLine=" + address.getAddressLine() +
                        "; type=" + address.getType() +
                        "}";
    }

    private void runStmt(String stmt, boolean failOnError)
    {
        Connection con = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            LOG.debug(stmt);
            pm.currentTransaction().begin();
            con = (Connection) storeMgr.getConnectionManager().getConnection(((JDOPersistenceManager)pm).getExecutionContext()).getConnection();
            con.prepareStatement(stmt).execute();
            pm.currentTransaction().commit();
        }
        catch (SQLException e)
        {
            LOG.error("Error running statement: "+stmt+"\n"+e);
            if (failOnError)
            {
                fail("Error running statement: "+stmt+"\n"+e);
            }
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }


    /**
     * Test running many Batchable SQL updates - previous this provoked just as many listeners added to connection
     * which lead slow commit when processing all the listeners (and extra memory usage).
     * @throws Exception Thrown if an error occurs
     */
    public void testManyBatchableUpdateStatements()
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_FLUSH_PROCESS_CLASS, CustomBatchingFlushProcess.class.getName());
        userProps.put("javax.jdo.option.Optimistic", "true");

        userProps.setProperty(PropertyNames.PROPERTY_ENABLE_STATISTICS, "true");
        PersistenceManagerFactory pmfForTest = getPMF(1, userProps);

        PersistenceManager pm = pmfForTest.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some basic data to query
            tx.begin();
            long idseq = 0;

            // ----------Test batch in INSERT
            // create objects
            final ArrayList<Address> addresses = new ArrayList<>();
            final int COUNT = 100;
            for (int i = 0; i < COUNT; i++)
            {
                final Address address = new Address(++idseq);
                address.setAddressLine("new address "+i);
                addresses.add(address);
                pm.makePersistent(address);
            }

            tx.commit();

            // ready for next operations
            tx = pm.currentTransaction();
            tx.begin();

            final ExecutionContext ec = ((JDOPersistenceManager) pm).getExecutionContext();
            for (int i = 0; i < COUNT; i++)
            {
                final Address address = addresses.get(i);
                address.setAddressLine(address.getAddressLine()+"new");
                final DNStateManager addressSM = ec.findStateManager(address);
                final int addressLineNO = addressSM.getClassMetaData().getAbsolutePositionOfMember("addressLine");
                // Simulate an update that would normally take place during commit.
                // We do it like this in order to assert number of listeners do not grow
                // (it would be hard to test the listener count inside a commit call).
                ec.getStoreManager().getPersistenceHandler().updateObject(addressSM, new int[]{addressLineNO});
                // trigger some other SQL to clear last batched statement in SQLController
                try (Query<?> query = pm.newQuery("javax.jdo.query.SQL", "SELECT COUNT(*) FROM PERSON"))
                {
                    ((List<?>) query.execute()).forEach(o->{});
                }
            }
            final ManagedConnection managedConnection = ec.getStoreManager().getConnectionManager().getConnection(ec);
            final int managedConnectionListenerSize = ((ManagedConnectionWithListenerAccess) managedConnection).getListeners().size();
            assertTrue("Too many listeners registered on connection: "+managedConnectionListenerSize, managedConnectionListenerSize < 10);
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing SQL query: " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            clean(Address.class);
        }
    }
}
