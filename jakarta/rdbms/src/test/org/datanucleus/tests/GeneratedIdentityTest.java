/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashSet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.models.company.Account;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

/**
 * Test case for use of Jakarta identity generators.
 */
public class GeneratedIdentityTest extends JakartaPersistenceTestCase
{
    public GeneratedIdentityTest(String name)
    {
        super(name);
    }

    /**
     * Test of table generator.
     */
    public void testTableGenerator()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            // This will create the table required by our TableGenerator specification
            Account acc1 = new Account();
            acc1.setEnabled(true);
            acc1.setUsername("bill");
            em.persist(acc1);
            em.flush();

            // Check the structure of the table
            RDBMSStoreManager databaseMgr = (RDBMSStoreManager)storeMgr;
            Connection conn = (Connection)databaseMgr.getConnectionManager().getConnection(0).getConnection();
            DatabaseMetaData dmd = conn.getMetaData();

            // Check PAYMENTS table column names
            HashSet<String> columnNames = new HashSet<String>();
            columnNames.add("SEQUENCE_NAME");
            columnNames.add("NEXT_VAL");
            RDBMSTestHelper.checkColumnsForTable(storeMgr, dmd, "SEQUENCE_TABLE", columnNames);

            tx.rollback();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown while checking TableGenerator table structure " + e.getMessage());
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
}