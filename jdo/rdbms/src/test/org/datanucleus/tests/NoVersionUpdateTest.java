/**********************************************************************
 Copyright (c) 2018 Andy Jefferson and others. All rights reserved.
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

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.management.ManagerStatistics;
import org.datanucleus.samples.models.transportation.Address;
import org.datanucleus.samples.models.transportation.Driver;
import org.datanucleus.store.rdbms.RDBMSPropertyNames;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class NoVersionUpdateTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public NoVersionUpdateTest(String name)
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
     * Test that version is not updated if updating only version-update=false members.
     */
    public void testNoVersionUpdate()
    {
        Properties userProps = new Properties();
        userProps.setProperty(RDBMSPropertyNames.PROPERTY_RDBMS_ALLOW_COLUMN_REUSE, "true");
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

            // create data
            final String constant01 = "_Constant01";
            final String constant02 = "_Constant02";
            final String constant03 = "_Constant03";
            final String constant04 = "_Constant04";
            Address address = new Address(++idseq);
            final String addressLine = "123 Robo Street";
            address.setAddressLine(addressLine+constant01);
            final String extra1 = "Extra1";
            address.setExtra1(extra1+constant01);
            final String extra2 = "Extra2";
            address.setExtra2(extra2+constant01);

            final ManagerStatistics statistics = ((JDOPersistenceManager) pm).getExecutionContext().getStatistics();

            pm.makePersistent(address);

            String addressString = getAddressString(address);

            // commit and get ready for next operations
            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            //======= Assert normal update with version update
            int dbWritesBefore = statistics.getNumberOfDatastoreWrites();
            long versionBefore = ((Number) JDOHelper.getVersion(address)).longValue();

            address.setAddressLine(addressLine+constant02);
            addressString = addressString.replace(addressLine+constant01, addressLine+constant02);

            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertAddressesByQuery(pm, Set.of(addressString));

            // We only expect ONE update and version update
            int dbWritesAfter = statistics.getNumberOfDatastoreWrites();
            long versionAfter = ((Number) JDOHelper.getVersion(address)).longValue();
            assertEquals("Normal version update expected", versionBefore+1, versionAfter);
            assertEquals("DB writes expected", dbWritesBefore+1, dbWritesAfter);
            dbWritesBefore = dbWritesAfter;
            versionBefore = versionAfter;


            //======= Assert update with no-version-update
            address.setExtra1(extra1+constant02);
            addressString = addressString.replace(extra1+constant01, extra1+constant02);

            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertAddressesByQuery(pm, Set.of(addressString));

            // We only expect ONE update and version update
            dbWritesAfter = statistics.getNumberOfDatastoreWrites();
            versionAfter = ((Number) JDOHelper.getVersion(address)).longValue();
            assertEquals("No version update expected", versionBefore, versionAfter);
            assertEquals("DB writes still expected", dbWritesBefore+1, dbWritesAfter);
            dbWritesBefore = dbWritesAfter;
            versionBefore = versionAfter;

            //======= Assert no-update with no-version-update (update read-only field)
            address.setExtra2(extra2+constant02); // no change in DB expected as update is disabled for field
//            addressString = addressString.replace(extra1+constant01, extra1+constant02);

            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertAddressesByQuery(pm, Set.of(addressString));

            // We expect NO update whatsoever - only updated read-only field marked as no-version-update
            dbWritesAfter = statistics.getNumberOfDatastoreWrites();
            versionAfter = ((Number) JDOHelper.getVersion(address)).longValue();
            assertEquals("No version update expected", versionBefore, versionAfter);
            assertEquals("No DB writes expected", dbWritesBefore, dbWritesAfter);
            dbWritesBefore = dbWritesAfter;
            versionBefore = versionAfter;

            //======= Assert update with no-version-update - multiple fields
            address.setExtra1(extra1+constant03); // no change in DB expected as update is disabled for field
            address.setExtra2(extra2+constant03); // no change in DB expected as update is disabled for field
            addressString = addressString.replace(extra1+constant02, extra1+constant03);

            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertAddressesByQuery(pm, Set.of(addressString));

            // We only expect ONE update but no version update
            dbWritesAfter = statistics.getNumberOfDatastoreWrites();
            versionAfter = ((Number) JDOHelper.getVersion(address)).longValue();
            assertEquals("No version update expected", versionBefore, versionAfter);
            assertEquals("DB writes expected", dbWritesBefore+1, dbWritesAfter);
            dbWritesBefore = dbWritesAfter;
            versionBefore = versionAfter;

            //======= Assert normal update - multiple fields
            address.setAddressLine(addressLine+constant03);
            address.setExtra1(extra1+constant04);
            address.setExtra2(extra2+constant04); // no change in DB expected as update is disabled for field
            addressString = addressString.replace(addressLine+constant02, addressLine+constant03);
            addressString = addressString.replace(extra1+constant03, extra1+constant04);

            tx.commit();
            tx = pm.currentTransaction();
            tx.begin();

            // Query to test all inserted
            assertAddressesByQuery(pm, Set.of(addressString));

            // We only expect ONE update and version update
            dbWritesAfter = statistics.getNumberOfDatastoreWrites();
            versionAfter = ((Number) JDOHelper.getVersion(address)).longValue();
            assertEquals("Version update expected", versionBefore+1, versionAfter);
            assertEquals("DB writes expected", dbWritesBefore+1, dbWritesAfter);
            dbWritesBefore = dbWritesAfter;
            versionBefore = versionAfter;
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

            clean(pmfForTest, Address.class);
        }
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

    private String getAddressString(Address address)
    {
        return address == null ? "<null>" :
                address.getClass().getSimpleName() + "{" +
                        "; addressLine=" + address.getAddressLine() +
                        "; type=" + address.getType() +
                        "; extra1=" + address.getExtra1() +
                        "; extra2=" + address.getExtra2() +
                        "}";
    }
}
