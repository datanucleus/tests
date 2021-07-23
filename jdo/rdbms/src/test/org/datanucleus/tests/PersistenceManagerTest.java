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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;

/**
 * Series of tests for RDBMS specific capabilities of a JDO PersistenceManager.
 */
public class PersistenceManagerTest extends JDOPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the TestCase. 
     */
    public PersistenceManagerTest(String name)
    {
        super(name);
    }

    /**
     * Test for (RDBMS) access to the JDO connection using its JDO interface accessor.
     */
    public void testJDOConnection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        JDOConnection jdoConn = null;
        try
        {
            // test normal transactional usage
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            assertTrue("JDOConnection should be castable to java.sql.Connection but isnt", jdoConn instanceof Connection);

            Connection sqlConn = (Connection) jdoConn;
            try
            {
                sqlConn.close();
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("not expected JDOUserException");
            }
            catch (SQLException e)
            {
                fail("not expected SQLException");
            }
            assertFalse("tx should not be active", tx.isActive());

            // test commit with datastore txn and no close
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            try
            {
                tx.commit();
                fail("expected JDOUserException");
            }
            catch (JDOUserException e)
            {
            }
            assertTrue("tx should be active", tx.isActive());

            try
            {
                tx.rollback();
                fail("expected JDOUserException");
            }
            catch(JDOUserException e)
            {
            }
            assertTrue("tx should be active", tx.isActive());

            sqlConn = (Connection) jdoConn;
            try
            {
                sqlConn.close();
            }
            catch (SQLException e)
            {
                LOG.error(">> Exception thrown in test", e);
            }
            tx.commit();

            // test commit with optimistic txn and no close
            tx.setOptimistic(true);
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            try
            {
                tx.commit();
                fail("expected JDOUserException");
            }
            catch (JDOUserException e)
            {
                //expected
                jdoConn.close();
            }
            assertTrue("tx should be active", pm.currentTransaction().isActive());
            tx.rollback();

            // test non-transactional usage
            for (int i=0;i<2;i++)
            {
                jdoConn = pm.getDataStoreConnection();
                try
                {
                    Connection c = (Connection) jdoConn.getNativeConnection();
                    try (PreparedStatement stmt = c.prepareStatement("select 1")) 
                    {
                        try (ResultSet rs = stmt.executeQuery()) 
                        {
                            if (rs.next()) 
                            {
                                LOG.debug("Query OK");
                            }
                        }
                    }
                }
                catch (SQLException sqle)
                {
                    LOG.error(">> Exception thrown in test", sqle);
                }
                finally
                {
                    jdoConn.close();
                }
            }
        }
        finally
        {
            pm.close();
        }
    }
}
