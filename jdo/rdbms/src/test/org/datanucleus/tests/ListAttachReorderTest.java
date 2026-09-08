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
import java.util.Properties;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.samples.rdbms.scostore.ListElement;
import org.datanucleus.samples.rdbms.scostore.ListHolder;

/**
 * Tests for reattaching detached Lists with modified element order.
 * Regression test for https://github.com/datanucleus/datanucleus-core/issues/526
 */
public class ListAttachReorderTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ListAttachReorderTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] { ListHolder.class, ListElement.class });
            initialised = true;
        }
    }

    /**
     * Test that reattaching a detached list with an element inserted at the start
     * works correctly when datanucleus.cache.collections=false.
     * Without the fix, this throws IndexOutOfBoundsException in
     * SCOUtils.updateListWithListElements() because the backed List's set() method
     * calls delegate.set() without loading the delegate first.
     */
    public void testReattachReorderedListWithCacheDisabled()
    throws Exception
    {
        Properties userProps = new Properties();
        userProps.put("datanucleus.cache.collections", "false");
        PersistenceManagerFactory myPMF = getPMF(1, userProps);

        ListHolder detached = null;
        Object holderId = null;

        // Step 1: Persist a list with 3 elements
        PersistenceManager pm = myPMF.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = new ListHolder("holder1");
            holder.getItems().add(new ListElement("A"));
            holder.getItems().add(new ListElement("B"));
            holder.getItems().add(new ListElement("C"));
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

        // Step 2: Detach with the items fetch group
        pm = myPMF.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        pm.getFetchPlan().setMaxFetchDepth(-1);
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            holder.getItems().size(); // Force load
            detached = pm.detachCopy(holder);
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

        // Step 3: Insert a new element at the beginning (reorder)
        detached.getItems().add(0, new ListElement("D"));
        // Detached list is now [D, A, B, C]

        // Step 4: Reattach — this is where #526 throws IndexOutOfBoundsException
        pm = myPMF.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(detached);
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

        // Step 5: Verify the list order is correct
        pm = myPMF.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            List<ListElement> items = holder.getItems();

            assertEquals("List should have 4 elements", 4, items.size());
            assertEquals("D", items.get(0).getValue());
            assertEquals("A", items.get(1).getValue());
            assertEquals("B", items.get(2).getValue());
            assertEquals("C", items.get(3).getValue());

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
        cleanClassForPMF(myPMF, ListHolder.class);
        cleanClassForPMF(myPMF, ListElement.class);
        myPMF.close();
    }

    /**
     * Test that reattaching a detached list with elements fully reordered
     * works correctly when datanucleus.cache.collections=false.
     */
    public void testReattachFullyReorderedListWithCacheDisabled()
    throws Exception
    {
        Properties userProps = new Properties();
        userProps.put("datanucleus.cache.collections", "false");
        PersistenceManagerFactory myPMF = getPMF(1, userProps);

        ListHolder detached = null;
        Object holderId = null;

        // Step 1: Persist a list with 3 elements [A, B, C]
        PersistenceManager pm = myPMF.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = new ListHolder("holder2");
            holder.getItems().add(new ListElement("A"));
            holder.getItems().add(new ListElement("B"));
            holder.getItems().add(new ListElement("C"));
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

        // Step 2: Detach with items
        pm = myPMF.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        pm.getFetchPlan().setMaxFetchDepth(-1);
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            holder.getItems().size(); // Force load
            detached = pm.detachCopy(holder);
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

        // Step 3: Reverse the list order [A, B, C] -> [C, B, A]
        ListElement a = detached.getItems().remove(0);
        ListElement c = detached.getItems().remove(1);
        detached.getItems().add(0, c);
        detached.getItems().add(a);

        // Step 4: Reattach
        pm = myPMF.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(detached);
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

        // Step 5: Verify reversed order
        pm = myPMF.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            ListHolder holder = (ListHolder) pm.getObjectById(holderId);
            List<ListElement> items = holder.getItems();

            assertEquals("List should have 3 elements", 3, items.size());
            assertEquals("C", items.get(0).getValue());
            assertEquals("B", items.get(1).getValue());
            assertEquals("A", items.get(2).getValue());

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
        cleanClassForPMF(myPMF, ListHolder.class);
        cleanClassForPMF(myPMF, ListElement.class);
        myPMF.close();
    }
}
