/**********************************************************************
Copyright (c) 2026 Contributors. All rights reserved.
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

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.rdbms.scostore.ListElement;
import org.datanucleus.samples.rdbms.scostore.ListHolder;

/**
 * Tests for List bulk shift operations using join tables.
 * Verifies that positional insertion (add at index) works correctly when
 * existing elements need their indices shifted upward.
 * Regression test for https://github.com/datanucleus/datanucleus-rdbms/issues/464
 */
public class BulkShiftListTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public BulkShiftListTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] { ListHolder.class, ListElement.class });
            initialised = true;
        }
    }

    /**
     * Test inserting an element at the start of a join-table List.
     * This requires all existing indices to shift up by 1, which on MySQL
     * can cause duplicate key violations if the bulk UPDATE processes rows
     * in ascending index order (issue #464).
     */
    public void testAddAtStart()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object holderId = null;
        try
        {
            // Create a list with 5 elements
            tx.begin();
            ListHolder holder = new ListHolder("holder1");
            for (int i = 0; i < 5; i++)
            {
                holder.getItems().add(new ListElement("element-" + i));
            }
            pm.makePersistent(holder);
            holderId = pm.getObjectId(holder);
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

        // Insert at position 0 - forces all indices to shift up
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            holder.getItems().add(0, new ListElement("inserted-at-0"));
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

        // Verify the list order is correct
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            List<ListElement> items = holder.getItems();

            assertEquals("List should have 6 elements", 6, items.size());
            assertEquals("First element should be the inserted one", "inserted-at-0", items.get(0).getValue());
            assertEquals("Second element should be original first", "element-0", items.get(1).getValue());
            assertEquals("Last element should be original last", "element-4", items.get(5).getValue());

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

        // Clean up
        clean(ListHolder.class);
        clean(ListElement.class);
    }

    /**
     * Test inserting an element in the middle of a join-table List.
     */
    public void testAddAtMiddle()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object holderId = null;
        try
        {
            tx.begin();
            ListHolder holder = new ListHolder("holder2");
            for (int i = 0; i < 5; i++)
            {
                holder.getItems().add(new ListElement("element-" + i));
            }
            pm.makePersistent(holder);
            holderId = pm.getObjectId(holder);
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

        // Insert at position 2 - forces indices 2,3,4 to shift up
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            holder.getItems().add(2, new ListElement("inserted-at-2"));
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

        // Verify the list order is correct
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            List<ListElement> items = holder.getItems();

            assertEquals("List should have 6 elements", 6, items.size());
            assertEquals("Element at 0 should be unchanged", "element-0", items.get(0).getValue());
            assertEquals("Element at 1 should be unchanged", "element-1", items.get(1).getValue());
            assertEquals("Element at 2 should be the inserted one", "inserted-at-2", items.get(2).getValue());
            assertEquals("Element at 3 should be original element-2", "element-2", items.get(3).getValue());
            assertEquals("Element at 5 should be original element-4", "element-4", items.get(5).getValue());

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

        // Clean up
        clean(ListHolder.class);
        clean(ListElement.class);
    }
}
