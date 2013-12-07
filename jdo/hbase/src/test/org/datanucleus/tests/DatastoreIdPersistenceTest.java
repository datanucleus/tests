/**********************************************************************
 Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.jpox.samples.models.voting.Meeting;

/**
 * Datastore identity persistence tests for HBase.
 */
public class DatastoreIdPersistenceTest extends JDOPersistenceTestCase
{
    Object id;

    public DatastoreIdPersistenceTest(String name)
    {
        super(name);
    }

    public void testInsert() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Meeting m1 = new Meeting(1, "First Meeting");
                Meeting m2 = new Meeting(2, "Annual General Meeting");
                pm.makePersistent(m1);
                pm.makePersistent(m2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown when running test " + e.getMessage());
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
        finally
        {
            clean(Meeting.class);
        }
    }
}