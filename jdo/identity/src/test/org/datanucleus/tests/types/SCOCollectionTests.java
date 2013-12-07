/**********************************************************************
Copyright (c) 12-Apr-2004 Andy Jefferson and others.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



Contributions
    ...
***********************************************************************/
package org.datanucleus.tests.types;
  
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import junit.framework.Assert;

import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.NucleusLogger;
import org.jpox.samples.types.container.CollectionHolder;
import org.jpox.samples.types.container.ListHolder;

/**
 * Series of Reflection based utilities for testing SCO collections.
 * The idea is to write the methods for typical operations using generic code
 * so that we can use the same method just passing in a different collection
 * class or a different type of collection (Normal/Inverse). 
 *
 * <P>
 * Makes the following assumptions in the methods.
 * <UL>
 * <LI>You can insert a parent item and a child item. The constructor for the
 * parent will take 3 params ... String, double, int. The constructor for the
 * child will take 4 params ... String, double, int, String.</LI>
 * <LI>To add an item to a collection class, use an "addItem" method.</LI> 
 * <LI>To add a collection of items to a collection class, use an "addItems"
 * method.</LI>
 * <LI>To remove an item from a collection class, use an "removeItem" 
 * method.</LI>
 * <LI>To remove a collection of items from a collection class, use a 
 * "removeItems" method.</LI>
 * <LI>To remove an item from a collection class at a position, use a
 * "removeItem" method.</LI>
 * <LI>To get the number of items in the collection, use a "getNoOfItems"
 * method.</LI>
 * </UL>
 * </P>
 **/
public class SCOCollectionTests
{
    /** Log4J logger. */
    static NucleusLogger LOG = TestHelper.LOG;

    /**
     * Utility for checking the addition of a collection of elements.
     * Calls the addAll method on a collection container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkPersistCollectionByContainer(PersistenceManagerFactory pmf,
            Class container_class,
            Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = null;
            container = createContainer(container_class, container);
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Collection c=new HashSet();

                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                c.add(item);

                // Add the items to the container
                SCOHolderUtilities.addItemsToCollection(container,c);
            }
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the addition of an element to a collection by
     * specification at the element end.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkAddElement(PersistenceManagerFactory pmf,
                                       Class container_class,
                                       Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            Object container = null;
            // Create a container and a few items
            try
            {
                container = container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }
            pm.makePersistent(container);

            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item and set its container
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);

                // Add the items to the container
                SCOHolderUtilities.setContainerForItem(item_class_parent,item,container_class,container);

                pm.makePersistent(item);
            }
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the retrieval of an item from a position in a
     * Collection. This is specific to List based collections. 
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkGetAt(PersistenceManagerFactory pmf,
                                  Class container_class,
                                  Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create some data
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and get an item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            ListHolder container=(ListHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Get middle item
                Object obj=SCOHolderUtilities.getItemFromList(container,2);
                Assert.assertTrue("Requested item could not be retrieved",obj != null);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the contains 
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkContains(PersistenceManagerFactory pmf,
                                  Class container_class,
                                  Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create some data
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object detachedItem=null;
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + NO_OF_ITEMS+1,0.00 + (10.00*(NO_OF_ITEMS+1)),NO_OF_ITEMS+1);
            pm.makePersistent(item);
            pm.flush();
            detachedItem = pm.detachCopy(item);
            SCOHolderUtilities.addItemToCollection(container,detachedItem);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and get an item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (NO_OF_ITEMS+1),container_size == NO_OF_ITEMS+1);

                Assert.assertTrue("Requested item could not be retrieved",container.contains(detachedItem));
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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
    
    /**
     * Utility for checking the removal of an element from a Collection.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkRemoveItem(PersistenceManagerFactory pmf,
                                       Class container_class,
                                       Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;
        Object element_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = null;
            Object element = null;
            
            container = createContainer(container_class, container);
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                
                if (i == 2)
                {
                    element = item;
                }
                
                // Add the item to the container
                SCOHolderUtilities.addItemToCollection(container,item);
            }
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);
            element_id = JDOHelper.getObjectId(element);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and remove the item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            
            Object element = pm.getObjectById(element_id,false);

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Remove the required element
                SCOHolderUtilities.removeItemFromCollection(container,element);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the new number of items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (NO_OF_ITEMS-1),container_size == (NO_OF_ITEMS-1));
            }

            tx.commit();
        }
        catch (JDOUserException e4)
        {
            LOG.error(e4);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e4.getMessage(),false);
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

    /**
     * Utility for checking the removal of a collection of elements. Calls the
     * removeAll method on a collection container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkRemoveCollection(PersistenceManagerFactory pmf,
                                             Class container_class,
                                             Class collection_class,
                                             Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and remove the odd numbered items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        int no_to_remove=0;
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Select the items to remove
                Iterator item_iter=SCOHolderUtilities.getCollectionItemsIterator(container);

                int i=0;
                Collection c=new HashSet();
                while (item_iter.hasNext())
                {
                    Object item=item_iter.next();
                    if ((i/2)*2 == i)
                    {
                        c.add(item);
                        no_to_remove++;
                    }
                    i++;
                }

                // Remove the items from the container
                SCOHolderUtilities.removeItemsFromCollection(container,c);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (NO_OF_ITEMS-no_to_remove),container_size == (NO_OF_ITEMS-no_to_remove));
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the retention of a collection of elements. Calls the
     * retainAll method on a collection container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkRetainCollection(PersistenceManagerFactory pmf,
                                             Class container_class,
                                             Class collection_class,
                                             Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and retain the odd numbered items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        int no_to_retain=0;
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Select the items to remove
                Iterator item_iter=SCOHolderUtilities.getCollectionItemsIterator(container);

                int i=0;
                Collection c=new HashSet();
                while (item_iter.hasNext())
                {
                    Object item=item_iter.next();
                    if ((i/2)*2 == i)
                    {
                        c.add(item);
                        no_to_retain++;
                    }
                    i++;
                }

                // Remove the items from the container
                SCOHolderUtilities.retainItemsInCollection(container,c);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (no_to_retain),container_size == (no_to_retain));
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the addition of an element to a position in a
     * Collection. This is specific to List based collections. 
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkAddAt(PersistenceManagerFactory pmf,
                                  Class container_class,
                                  Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and add an item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            ListHolder container=(ListHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item 7", 225.00,7);
                
                // Add item in middle of list
                SCOHolderUtilities.addItemToCollection(container, item, 2);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the new number of items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (NO_OF_ITEMS+1),container_size == (NO_OF_ITEMS+1));
            }

            tx.commit();
        }
        catch (JDOUserException e4)
        {
            LOG.error(e4);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e4.getMessage(),false);
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
    /**
     * Utility for checking the removal of an element from a position in a
     * Collection. This is specific to List based collections. 
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkRemoveAt(PersistenceManagerFactory pmf,
                                     Class container_class,
                                     Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and remove an item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            ListHolder container=(ListHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Remove middle item
                SCOHolderUtilities.removeItemFromCollection(container,2);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the new number of items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + (NO_OF_ITEMS-1),container_size == (NO_OF_ITEMS-1));
            }

            tx.commit();
        }
        catch (JDOUserException e4)
        {
            LOG.error(e4);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e4.getMessage(),false);
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

	private static CollectionHolder createAndFillContainer(
			Class container_class, Class item_class_parent, int NO_OF_ITEMS) {
		CollectionHolder container = null;
		// Create a container and a few items
		container = createContainer(container_class, container);
		for (int i=0;i<NO_OF_ITEMS;i++)
		{
		    // Create an item
		    Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
		    
		    // Add the item to the container
		    SCOHolderUtilities.addItemToCollection(container,item);
		}
		return container;
	}

	private static CollectionHolder createContainer(Class container_class,
			CollectionHolder container) {
		try
		{
		    container = (CollectionHolder) container_class.newInstance();
		}
		catch (Exception e1)
		{
		    LOG.error(e1);
		    Assert.fail("Failed to find Container class " + container_class.getName());
		}
		return container;
	}

	public static void checkEquals(PersistenceManagerFactory pmf, Class container_class, Class item_class_parent)
	{
        int NO_OF_ITEMS=5;
        Object container1_id=null;
        Object container2_id=null;

        // Create two equal containers
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container1 = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            CollectionHolder container2 = null;
    		container2 = createContainer(container_class, container2);
    		container2.addItems(container1.getItems());
    		
    		// check before persisting
    		Assert.assertEquals(container1.getItems(), container2.getItems());
            pm.makePersistent(container1);
            pm.makePersistent(container2);
            // check after persisting inside tx
    		Assert.assertEquals(container1.getItems(), container2.getItems());
            container1_id = JDOHelper.getObjectId(container1);
            container2_id = JDOHelper.getObjectId(container2);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }
        
        // Retrieve the two containers, check equality again, change order in one of them
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            ListHolder container1=(ListHolder) pm.getObjectById(container1_id,false);
            Assert.assertNotNull(container1);
            ListHolder container2=(ListHolder) pm.getObjectById(container2_id,false);
            Assert.assertNotNull(container2);
            // must still be equal after retrieval
            Assert.assertEquals(container1.getItems(), container2.getItems());
            
            // exchange first and last item in first container
            Object firstItem = container1.getItem(0);
            Object lastItem = container1.getItem(NO_OF_ITEMS-1);
            ((List)container1.getItems()).set(0, lastItem);
            ((List)container1.getItems()).set(NO_OF_ITEMS-1, firstItem);
            Assert.assertFalse(container1.getItems().equals(container2.getItems()));

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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
	
    /**
     * Utility for checking the clearing out of a collection. Calls the
     * clear method on a collection container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkClearCollection(PersistenceManagerFactory pmf,
                                            Class container_class,
                                            Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = createAndFillContainer(
					container_class, item_class_parent, NO_OF_ITEMS);
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Clear out the container
                SCOHolderUtilities.clearItemsFromContainer(container);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                boolean is_empty=SCOHolderUtilities.isContainerEmpty(container);
                Assert.assertTrue(container_class.getName() + " is not empty, yet should be since clear() was called.",is_empty);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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

    /** 
     * Utility for checking the use of inherited elements within a collection.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param collection_class The collection return e.g java.util.ArrayList
     * @param item_class_parent The parent element class
     * @param item_class_child The child element class 
     **/
    public static void checkElementInheritance(PersistenceManagerFactory pmf,
                                               Class container_class,
                                               Class collection_class,
                                               Class item_class_parent,
                                               Class item_class_child)
    throws Exception
    {
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            try
            {
                // Create a container and a few items
                CollectionHolder container = (CollectionHolder) container_class.newInstance();

                Object item=null;

                item = SCOHolderUtilities.createItemParent(item_class_parent,"Toaster",12.99,0);
                SCOHolderUtilities.addItemToCollection(container,item);
                item = SCOHolderUtilities.createItemChild(item_class_child,"Kettle",15.00,3,"KETT1");
                SCOHolderUtilities.addItemToCollection(container,item);
                item = SCOHolderUtilities.createItemChild(item_class_child,"Hifi",99.99,0,"HIFI");
                SCOHolderUtilities.addItemToCollection(container,item);
                item = SCOHolderUtilities.createItemParent(item_class_parent,"Curtains",5.99,1);
                SCOHolderUtilities.addItemToCollection(container,item);

                // Persist the container (and all of the items)
                pm.makePersistent(container);
                container_id = JDOHelper.getObjectId(container);
            }
            catch (Exception e1)
            {
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            tx.commit();
        }
        catch (JDOUserException e)
        {
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Query the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been 4",container_size == 4);

                // Interrogate the items
                Iterator item_iter=SCOHolderUtilities.getCollectionItemsIterator(container);

                int no_parent=0;
                int no_child=0;
                while (item_iter.hasNext())
                {
                    Object item=item_iter.next();

                    if (item_class_child.isAssignableFrom(item.getClass()))
                    {
                        no_child++;
                    }
                    else if (item_class_parent.isAssignableFrom(item.getClass()))
                    {
                        no_parent++;
                    }
                }
                Assert.assertTrue("No of " + item_class_parent.getName() + " is incorrect (" + no_parent + ") : should have been 2",no_parent == 2);
                Assert.assertTrue("No of " + item_class_child.getName() + " is incorrect (" + no_child + ") : should have been 2",no_child == 2);
            }

            tx.commit();
        }
        catch (JDOUserException e)
        {
            Assert.assertTrue("Exception thrown while querying " + container_class.getName() + e.getMessage(),false);
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

    /**
     * Utility for checking the use of queries with JDOQL on a container.
     * Uses contains(), isEmpty() operations.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashSetNormal
     * @param item_class_parent The parent element class
     * @param db_vendor_id Name of RDBMS. (TODO Remove this)
     **/
    public static void checkQuery(PersistenceManagerFactory pmf,
                                  Class container_class,
                                  Class item_class_parent,
                                  String db_vendor_id)
    throws Exception
    {
        int NO_OF_ITEMS=5;

        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            // Create a container
            CollectionHolder container = null;
            container = createContainer(container_class, container);

            // Create a few items and add them to a Collection
            java.util.Collection c=new java.util.HashSet();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                c.add(item);
            }

            // Add the items to the container
            SCOHolderUtilities.addItemsToCollection(container,c);

            pm.makePersistent(container);
            JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Query the containers
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Get all containers that are empty
            // TODO : Remove this restriction when MySQL supports subqueries 
            if (!db_vendor_id.equals("mysql")) 
            {
                Extent e1=pm.getExtent(container_class,true);
                Query  q1=pm.newQuery(e1,"items.isEmpty()");
                java.util.Collection c1=(java.util.Collection)q1.execute();
                Assert.assertTrue("No of containers that are empty is incorrect (" + c1.size() + ") : should have been 0",c1.size() == 0);
            }

            // Get all containers containing a particular value 
            Extent e2=pm.getExtent(container_class,true);
            Query  q2=pm.newQuery(e2,"items.contains(element) && element.name==\"Item 1\"");
            q2.declareImports("import " + item_class_parent.getName());
            q2.declareVariables(item_class_parent.getName() + " element");
            java.util.Collection c2=(java.util.Collection)q2.execute();
            Assert.assertTrue("No of containers with the specified value is incorrect (" + c2.size() + ") : should have been 1",c2.size() == 1);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            e.printStackTrace();
            Assert.assertTrue("Exception thrown while querying container objects " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Query the containers for the container containing an element.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Get all elements
            Extent e=pm.getExtent(item_class_parent,true);
            Iterator e_iter=e.iterator();
            while (e_iter.hasNext())
            {
                Object obj=e_iter.next();
                Query  q=pm.newQuery(pm.getExtent(container_class,true),"items.contains(element)");
                q.declareImports("import " + item_class_parent.getName());
                q.declareParameters(item_class_parent.getName() + " element");
                java.util.Collection c=(java.util.Collection)q.execute(obj);
                Assert.assertTrue("No of containers with the specified element is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);
            }

            tx.commit();
        }
        catch (JDOUserException e)
        {
            e.printStackTrace();
            Assert.assertTrue("Exception thrown while querying container objects " + e.getMessage(),false);
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

    /**
     * Utility for checking the use of detach, then attach.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkAttachDetach(PersistenceManagerFactory pmf,
                                         Class container_class,
                                         Class item_class_parent,
                                         Class collection_class)
    throws Exception
    {
        int NO_OF_ITEMS = 5;
        Object container_id = null;
        CollectionHolder detachedContainer = null;

        // Create a container and some elements
        PersistenceManager pm=pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = null;
            container = createContainer(container_class, container);
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Collection c = new HashSet();
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                c.add(item);
                SCOHolderUtilities.addItemsToCollection(container,c);
            }
            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);
            detachedContainer = (CollectionHolder) pm.detachCopy(container);

            int container_size=SCOHolderUtilities.getContainerSize(container);
            Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
                (NO_OF_ITEMS), container_size == (NO_OF_ITEMS));
            container_size=SCOHolderUtilities.getContainerSize(detachedContainer);
            Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
                (NO_OF_ITEMS), container_size == (NO_OF_ITEMS));
            tx.commit();

        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating and detaching " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Update the container
        Object item = SCOHolderUtilities.createItemParent(item_class_parent, "Item 10", 100, 10);
        SCOHolderUtilities.addItemToCollection(detachedContainer, item);
        int container_size=SCOHolderUtilities.getContainerSize(detachedContainer);
        Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
            (NO_OF_ITEMS+1), container_size == (NO_OF_ITEMS+1));

        // Attach the updated container
        pm = pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder attachedContainer = (CollectionHolder) pm.makePersistent(detachedContainer);

            // Add another item to the container
            Object newItem = SCOHolderUtilities.createItemParent(item_class_parent, "Item 20", 200, 20);
            SCOHolderUtilities.addItemToCollection(attachedContainer, newItem);

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while retrieving " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container = (CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
                    (NO_OF_ITEMS+2), container_size == (NO_OF_ITEMS+2));
            }

            detachedContainer = (CollectionHolder) pm.detachCopy(container);

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while retrieving " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Remove an item from the container
        container_size = SCOHolderUtilities.getContainerSize(detachedContainer);
        Assert.assertTrue("Detached container " + container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
            (NO_OF_ITEMS+2), container_size == (NO_OF_ITEMS+2));
        Iterator containerIter = SCOHolderUtilities.getCollectionItemsIterator(detachedContainer);
        item = containerIter.next();
        SCOHolderUtilities.removeItemFromCollection(detachedContainer, item);
        container_size = SCOHolderUtilities.getContainerSize(detachedContainer);
        Assert.assertTrue("Detached container " + container_class.getName() + " (after delete) has incorrect number of items (" + container_size + ") : should have been " + 
            (NO_OF_ITEMS+1), container_size == (NO_OF_ITEMS+1));

        // Attach the updated container
        pm = pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.makePersistent(detachedContainer);

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while retrieving " + container_class.getName() + " " + e2.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container = (CollectionHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                container_size = SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue("Attached container " + container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
                    (NO_OF_ITEMS+1), container_size == (NO_OF_ITEMS+1));
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while retrieving " + container_class.getName() + " " + e2.getMessage(),false);
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

    /**
     * Utility for checking the persistence of a 1-N bidir relation by persisting the element.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g Set1
     * @param item_class_parent The parent element class
     **/
    public static void checkPersistCollectionByElement(PersistenceManagerFactory pmf,
            Class container_class,
            Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=2;
        Object container_id=null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            CollectionHolder container = null;
            container = createContainer(container_class, container);
            Object itemToPersist = null;
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Collection c = new HashSet();

                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                SCOHolderUtilities.setContainerForItem(item_class_parent, item, container_class, container);
                if (i == 0)
                {
                    itemToPersist = item; // Persist from the first of the elements.
                }

                // Add the items to the container
                c.add(item);
                SCOHolderUtilities.addItemsToCollection(container, c);
            }
            pm.makePersistent(itemToPersist);
            Assert.assertEquals("Number of elements in container just after persist is wrong", 
                NO_OF_ITEMS, container.getNoOfItems());
            container_id = JDOHelper.getObjectId(container);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
            Assert.assertTrue("Exception thrown while creating " + container_class.getName() + " " + e.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Find the container and check the items
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            CollectionHolder container=(CollectionHolder) pm.getObjectById(container_id, false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);
            }

            tx.commit();
        }
        catch (JDOUserException e2)
        {
            LOG.error(e2);
            Assert.assertTrue("Exception thrown while manipulating " + container_class.getName() + " " + e2.getMessage(),false);
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