/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.queue.Queue1;
import org.jpox.samples.types.queue.Queue1Item;

/**
 * Tests for mapping java.util.Queue fields.
 * TODO Change this to use the same structure as all SCO collections.
 * @version $Revision: 1.1 $
 */
public class QueueTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public QueueTest(String name)
    {
        super(name);
    }

    /**
     * Test for a join table Queue
     */
    public void testQueue1()
    {
        // Persist a Queue object
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();

            Queue1 queue = new Queue1("My Queue");
            Queue1Item item1 = new Queue1Item("First");
            Queue1Item item2 = new Queue1Item("Second");
            Queue1Item item3 = new Queue1Item("Third");
            queue.offer(item1);
            queue.offer(item2);
            queue.offer(item3);

            pm.makePersistent(queue);
            id = JDOHelper.getObjectId(queue);
            tx.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown whilst persisting Queue objects! : " + ex.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the Queue object and check it
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Queue1 queue = (Queue1)pm.getObjectById(id);
            assertEquals("Size of the Queue is incorrect.", 3, queue.getQueueSize());
            
            tx.commit();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            fail("Exception thrown whilst persisting Queue objects! : " + ex.getMessage());
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
}