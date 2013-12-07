/**********************************************************************
Copyright (c) 2010 Tom Zurkan and others. All rights reserved.
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

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.jpox.samples.one_one.unidir.LoginAccount;

public class ManagedConnectionTest extends JDOPersistenceTestCase
{
    public ManagedConnectionTest(String name)
    {
        super(name);
    }

    /**
     * Test case for managed connections in nontx usage where not releasing.
     */
    public void testNontransactionalReleaseAfterUseFalse()
    {
        Properties userProps = new Properties();
        userProps.setProperty("datanucleus.connection.nontx.releaseAfterUse", "false");
        PersistenceManagerFactory thePMF = TestHelper.getPMF(1, userProps);
        PersistenceManager pm = thePMF.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        // Create sample data
        Object acctId = null; 
        try
        {
            tx.begin();
            LoginAccount acct = new LoginAccount("Barney", "Rubble", "brubble", "bambam");
            pm.makePersistent(acct);
            acctId = JDOHelper.getObjectId(acct);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception while creating data", e);
            fail("Exception thrown while creating data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        try
        {
            for (int i = 0; i < 60; i++)
            {
                pm = thePMF.getPersistenceManager();
                pm.getObjectById(acctId);
                pm.close();
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception while getting connections", e);
            fail("Exception thrown while getting connections : " + e.getMessage());
        }
        finally
        {
            if (!pm.isClosed())
            {
                pm.close();
            }
        }
    }
}