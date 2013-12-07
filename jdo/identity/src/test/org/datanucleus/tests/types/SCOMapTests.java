/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
import org.jpox.samples.types.container.MapHolder;

/**
 * Series of Reflection based utilities for testing SCO map collections.
 * The idea is to write the methods for typical operations using generic code
 * so that we can use the same method just passing in a different map
 * class or a different type of map (Normal/Inverse). 
 *
 * <P>
 * Makes the following assumptions in the methods.
 * <UL>
 * <LI>Putting an item in the Map is done by method "putItem(key,obj)"</LI> 
 * <LI>Putting items to the Map is done by method "putItems(Map)"</LI> 
 * <LI>No of items in the Map is retrieved by method "getNoOfItems()"</LI> 
 * <LI>Removing an item from the Map class uses a "removeItem" method.</LI> 
 * </UL>
 * </P> 
 **/
public class SCOMapTests
{
    /** Log4J logger. */
    static final NucleusLogger LOG = TestHelper.LOG;

    /**
     * Utility for checking the addition of a Map of elements. Calls the
     * putAll method on a map container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkPutItems(PersistenceManagerFactory pmf,
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

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                m.put(new String("Key" + (i+1)),item);
            }

            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
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
     * Utility for checking the addition of a Map of elements. Calls the
     * putAll method on a map container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkPutNullValues(PersistenceManagerFactory pmf,
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

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                m.put(new String("Key" + (i+1)), null);
            }

            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size = SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);
                Map items = container.getItems();
                Iterator iter = items.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Entry) iter.next();
                    Assert.assertNull(entry.getValue());
                    Assert.assertNotNull(entry.getKey());
                }
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
     * Utility for checking the removal of an element with a key in the Map.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g MapNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkRemove(PersistenceManagerFactory pmf,
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

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Add a few items
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                SCOHolderUtilities.addItemToMap(container,new String("Key" + (i+1)),item);
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

        // Find the container and remove an item
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + NO_OF_ITEMS,container_size == NO_OF_ITEMS);

                // Remove an item
                SCOHolderUtilities.removeItemFromMap(container,new String("Key2"));
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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
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
     * Utility for checking the clearing out of a Map. Calls the
     * clear method on a Map container.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkClearMap(PersistenceManagerFactory pmf,
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

            // Create a container and a few items
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Add a few items to the container
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                SCOHolderUtilities.addItemToMap(container,new String("Key" + (i+1)),item);
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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
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
     * Utility for checking the use of inherited values within the map.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g MapNormal
     * @param item_class_parent The parent element class
     * @param item_class_child The child element class 
     **/
    public static void checkValueInheritance(PersistenceManagerFactory pmf,
                                             Class container_class,
                                             Class item_class_parent,
                                             Class item_class_child)
    throws Exception
    {
        int NO_OF_ITEMS=4;
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
                MapHolder container = (MapHolder) container_class.newInstance();

                Object item=null;

                item = SCOHolderUtilities.createItemParent(item_class_parent,"Toaster",12.99,0);
                SCOHolderUtilities.addItemToMap(container,"Key1",item);
                item = SCOHolderUtilities.createItemChild(item_class_child,"Kettle",15.00,3,"KETT1");
                SCOHolderUtilities.addItemToMap(container,"Key2",item);
                item = SCOHolderUtilities.createItemChild(item_class_child,"Hifi",99.99,0,"HIFI");
                SCOHolderUtilities.addItemToMap(container,"Key3",item);
                item = SCOHolderUtilities.createItemParent(item_class_parent,"Curtains",5.99,1);
                SCOHolderUtilities.addItemToMap(container,"Key4",item);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been 4",container_size == 4);

                // Interrogate the items
                Object item=null;
                int no_parent=0;
                int no_child=0;

                for (int i=0;i<NO_OF_ITEMS;i++)
                {
                    item = SCOHolderUtilities.getItemFromMap(container, "Key" + (i+1));
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
     * Utility for checking the use of queries with JDOQL on a Map.
     * Uses containsKey(),containsValue(),containsEntry() methods.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     * @param db_vendor_id Id of datastore e.g mysql (TODO : remove this)
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
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                m.put(new String("Key" + (i+1)),item);
            }

            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

        // Query the Map
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Get all Maps that contain the key "Key1" - should return 1
            Extent e1=pm.getExtent(container_class,true);
            Query  q1=pm.newQuery(e1,"items.containsKey(\"Key1\")");
            java.util.Collection c1=(java.util.Collection)q1.execute();
            Assert.assertTrue("containsKey : No of containers with a key \"Key1\" is incorrect (" + c1.size() + ") : should have been 1",c1.size() == 1);

            // Get all Maps that contain the key "Key7" - should return 0
            Extent e2=pm.getExtent(container_class,true);
            Query  q2=pm.newQuery(e2,"items.containsKey(\"Key7\")");
            java.util.Collection c2=(java.util.Collection)q2.execute();
            Assert.assertTrue("containsKey : No of containers with a key \"Key7\" is incorrect (" + c2.size() + ") : should have been 0",c2.size() == 0);

            // Get all Maps that are empty
            // TODO : remove the MySQL omittal when it supports subqueries 
            if (!db_vendor_id.equals("mysql"))
            {
                Extent e3=pm.getExtent(container_class,true);
                Query  q3=pm.newQuery(e3,"items.isEmpty()");
                java.util.Collection c3=(java.util.Collection)q3.execute();
                Assert.assertTrue("No of containers that are empty is incorrect (" + c3.size() + ") : should have been 0",c3.size() == 0);
            }

            // Get all Maps containing a particular value 
            Extent e4=pm.getExtent(container_class,true);
            Query  q4=pm.newQuery(e4,"items.containsValue(the_value) && the_value.name==\"Item 1\"");
            q4.declareImports("import " + item_class_parent.getName());
            q4.declareVariables(item_class_parent.getName() + " the_value");
            java.util.Collection c4=(java.util.Collection)q4.execute();
            Assert.assertTrue("containsValue : No of containers with the specified value is incorrect (" + c4.size() + ") : should have been 1",c4.size() == 1);

            // Get all Maps containing a particular value 
            Extent e5=pm.getExtent(container_class,true);
            Query  q5=pm.newQuery(e5,"items.containsEntry(\"Key1\",the_value) && the_value.name==\"Item 0\"");
            q5.declareImports("import " + item_class_parent.getName());
            q5.declareVariables(item_class_parent.getName() + " the_value");
            java.util.Collection c5=(java.util.Collection)q5.execute();
            Assert.assertTrue("containsEntry : No of containers with the specified value is incorrect (" + c5.size() + ") : should have been 1",c5.size() == 1);
 
            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
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
     * Utility for checking the use of queries with JDOQL on a Map with
     * primitive key/value objects. This is separate from the checkQuery
     * because with primitive (String) value a different table is created.
     * Uses containsKey(),containsValue(),containsEntry() methods.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param db_vendor_id Id of datastore e.g mysql (TODO : remove this)
     **/
    public static void checkQueryPrimitive(PersistenceManagerFactory pmf,
                                           Class container_class,
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
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                m.put(new String("Key" + (i+1)),new String("Value " + (i+1)));
            }

            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

        // Query the Map
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Get all Maps that contain the key "Key1" - should return 1
            Extent e1=pm.getExtent(container_class,true);
            Query  q1=pm.newQuery(e1,"items.containsKey(\"Key1\")");
            java.util.Collection c1=(java.util.Collection)q1.execute();
            Assert.assertTrue("containsKey : No of containers with a key \"Key1\" is incorrect (" + c1.size() + ") : should have been 1",c1.size() == 1);

            // Get all Maps that contain the key "Key7" - should return 0
            Extent e2=pm.getExtent(container_class,true);
            Query  q2=pm.newQuery(e2,"items.containsKey(\"Key7\")");
            java.util.Collection c2=(java.util.Collection)q2.execute();
            Assert.assertTrue("containsKey : No of containers with a key \"Key7\" is incorrect (" + c2.size() + ") : should have been 0",c2.size() == 0);

            // Get all Maps that are empty
            // TODO : remove the MySQL omittal when it supports subqueries 
            if (!db_vendor_id.equals("mysql"))
            {
                Extent e3=pm.getExtent(container_class,true);
                Query  q3=pm.newQuery(e3,"items.isEmpty()");
                java.util.Collection c3=(java.util.Collection)q3.execute();
                Assert.assertTrue("No of containers that are empty is incorrect (" + c3.size() + ") : should have been 0",c3.size() == 0);
            }

            // Get all Maps containing a particular value 
            Extent e4=pm.getExtent(container_class,true);
            Query  q4=pm.newQuery(e4,"items.containsValue(the_value) && the_value==\"Value 1\"");
            q4.declareImports("import java.lang.String");
            q4.declareVariables("java.lang.String the_value");
            java.util.Collection c4=(java.util.Collection)q4.execute();
            Assert.assertTrue("containsValue : No of containers with the specified value is incorrect (" + c4.size() + ") : should have been 1",c4.size() == 1);

            // Get all Maps containing a particular entry 
            Extent e5=pm.getExtent(container_class,true);
            Query  q5=pm.newQuery(e5,"items.containsEntry(\"Key1\",the_value) && the_value==\"Value 1\"");
            q5.declareImports("import java.lang.String");
            q5.declareVariables("java.lang.String the_value");
            java.util.Collection c5=(java.util.Collection)q5.execute();
            Assert.assertTrue("containsEntry : No of containers with the specified value is incorrect (" + c5.size() + ") : should have been 1",c5.size() == 1);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
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
     * Utility for checking the use of queries with JDOQL on a Map with
     * primitive key/value objects. This is separate from the checkQuery
     * because with primitive (String) value a different table is created.
     * Uses containsKey(),containsValue(),containsEntry() methods.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param key_class The key class
     * @param value_class The value class
     **/
    public static void checkQueryNonPrimitiveKey(PersistenceManagerFactory pmf,
                                                 Class container_class,
                                                 Class key_class,
                                                 Class value_class)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object key_id = null;
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            Object saved_key = null;
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(value_class,"Item " + i,0.00 + (10.00*i),i);
                Object k;
                if (key_class.getName().equals(String.class.getName()))
                {
                    k = new String("Key" + i);
                }
                else
                {
                    k = SCOHolderUtilities.createItemParent(key_class,"Key " + i,0.00 + (11.13*i),i);
                }
                m.put(k,item);

                // Save the first key in the map for future use
                if (i == 0)
                {
                    saved_key = k;
                }
            }
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(value_class,"Item " + i,0.00 + (10.00*i),i);
                m.put(item,item);
            }
            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

            pm.makePersistent(container);
            JDOHelper.getObjectId(container);
            key_id = pm.getObjectId(saved_key);

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

        // Query the Map
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            
            Object saved_key = pm.getObjectById(key_id, false);

            // Get all Maps that contain the key "Key1" - should return 1
            Extent e1=pm.getExtent(container_class,true);
            Query  q1=pm.newQuery(e1,"items.containsKey(theKey)");
            q1.declareParameters(key_class.getName() + " theKey");
            java.util.Collection c1=(java.util.Collection)q1.execute(saved_key);
            Assert.assertTrue("containsKey : No of containers with a key with id \"" + key_id + "\" is incorrect (" + c1.size() + ") : should have been 1",c1.size() == 1);

            tx.commit();
        }
        catch (JDOUserException e)
        {
            LOG.error(e);
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
     * Utility for checking the use of key/value same instances
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkSameKeyValueInstances(PersistenceManagerFactory pmf,
                                     Class container_class,
                                     Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;
        java.util.Collection keys = null;
        java.util.Collection values = null;
        java.util.Map entries = null;
        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object detachedItem = null;
        try
        {
            tx.setRetainValues(true);
            tx.begin();

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            entries=new java.util.HashMap();
            values = new java.util.HashSet();
            keys = new java.util.HashSet();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                m.put(item,item);
                entries.put(item,item);
                values.add(item);
                keys.add(item);
            }
            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);
            // Create an item
            Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + NO_OF_ITEMS+1,0.00 + (10.00*(NO_OF_ITEMS+1)),NO_OF_ITEMS+1);
            m.put(item,item);
            entries.put(item,item);
            values.add(item);
            keys.add(item);
            SCOHolderUtilities.addItemToMap(container,item,item);
            // Create an item
            item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + NO_OF_ITEMS+2,0.00 + (10.00*(NO_OF_ITEMS+2)),NO_OF_ITEMS+2);
            m.put(item,item);
            entries.put(item,item);
            values.add(item);
            keys.add(item);
            pm.makePersistent(item);
            pm.flush();
            detachedItem = pm.detachCopy(item);
            SCOHolderUtilities.addItemToMap(container,detachedItem,item);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the entry set from the container
                java.util.Set entry_set=SCOHolderUtilities.getEntrySetFromMap(container);
                Assert.assertTrue("EntrySet has incorrect number of items (" + entry_set.size() + ") : should have been " + (NO_OF_ITEMS+2),entry_set.size() == (NO_OF_ITEMS+2));
                Assert.assertTrue("Entries are not the same, result = "+entry_set+" expected = "+entries.entrySet(),SCOHolderUtilities.compareSet(entry_set,entries.entrySet()));
                Assert.assertTrue("Entries does not contain expected value",container.getValues().contains(detachedItem));
                Assert.assertTrue("Entries does not contain expected key",container.containsKey(detachedItem));
                Assert.assertTrue("Entries does not contain expected value",container.containsValue(detachedItem));
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
     * Utility for checking the use of entrySet. Calls the
     * entrySet method on a map container and checks the contents.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     * @param key_class The key class
     **/
    public static void checkEntrySet(PersistenceManagerFactory pmf,
                                     Class container_class,
                                     Class item_class_parent,
    								 Class key_class)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;
        java.util.Collection keys = null;
        java.util.Collection values = null;
        java.util.Map entries = null;
        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.setRetainValues(true);
            tx.begin();

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            entries=new java.util.HashMap();
            values = new java.util.HashSet();
            keys = new java.util.HashSet();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                Object k;
                if( key_class.getName().equals(String.class.getName()) )
                {
                    k = new String("Key" + i);
                }
                else
                {
                    k = SCOHolderUtilities.createItemParent(key_class,"Key " + i,0.00 + (11.13*i),i);
                }
                m.put(k,item);
                entries.put(k,item);
                values.add(item);
                keys.add(k);
            }
            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the entry set from the container
                java.util.Set entry_set=SCOHolderUtilities.getEntrySetFromMap(container);
                Assert.assertTrue("EntrySet has incorrect number of items (" + entry_set.size() + ") : should have been " + NO_OF_ITEMS,entry_set.size() == NO_OF_ITEMS);
                Assert.assertTrue("Entries are not the same, result = "+entry_set+" expected = "+entries.entrySet(),SCOHolderUtilities.compareSet(entry_set,entries.entrySet()));
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
     * Utility for checking the use of keySet. Calls the
     * keySet method on a map container and checks the contents.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     * @param key_class The key class
     **/
    public static void checkKeySet(PersistenceManagerFactory pmf,
                                     Class container_class,
                                     Class item_class_parent,
                                     Class key_class)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;
        java.util.Collection items = null;

        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.setRetainValues(true);
            tx.begin();

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                Object k;
                if( key_class.getName().equals(String.class.getName()) )
                {
                    k = new String("Key" + i);
                }
                else
                {
                    k = SCOHolderUtilities.createItemParent(key_class,"Key " + i,0.00 + (11.13*i),i);
                }
                m.put(k,item);
            }
            items = new java.util.HashSet();
            items.addAll(m.keySet());
            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the entry set from the container
                java.util.Set key_set=SCOHolderUtilities.getKeySetFromMap(container);
                Assert.assertTrue("KeySet has incorrect number of items (" + key_set.size() + ") : should have been " + NO_OF_ITEMS,key_set.size() == NO_OF_ITEMS);
                Assert.assertTrue("Collections are not the same, result = "+key_set+" expected = "+items,SCOHolderUtilities.compareSet(key_set,items));
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
     * Utility for checking the use of values. Calls the
     * keySet method on a map container and checks the contents.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g HashMapNormal
     * @param item_class_parent The parent element class
     * @param key_class The key class
     **/
    public static void checkValues(PersistenceManagerFactory pmf,
                                     Class container_class,
                                     Class item_class_parent,
                                     Class key_class)
    throws Exception
    {
        int NO_OF_ITEMS=5;
        Object container_id=null;
        java.util.Collection items = null;
        // Create the container
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.setRetainValues(true);
            tx.begin();

            // Create a container
            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            // Create a few items and add them to a Map
            java.util.Map m=new java.util.HashMap();
            items = new java.util.HashSet();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                Object k;
                if( key_class.getName().equals(String.class.getName()) )
                {
                    k = new String("Key" + i);
                }
                else
                {
                    k = SCOHolderUtilities.createItemParent(key_class,"Key " + i,0.00 + (11.13*i),i);
                }
                m.put(k,item);
            }

            items.addAll(m.values());
            
            // Add the items to the container
            SCOHolderUtilities.addItemsToMap(container,m);

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

            MapHolder container=(MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the entry set from the container
                java.util.Collection value_set=SCOHolderUtilities.getValuesFromMap(container);
                Assert.assertTrue("Values has incorrect number of items (" + value_set.size() + ") : should have been " + NO_OF_ITEMS,value_set.size() == NO_OF_ITEMS);
                Assert.assertTrue("Collections are not the same, result = "+value_set+" expected = "+items,SCOHolderUtilities.compareSet(value_set,items));
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
     * Utility for checking the use of detach, then attach.
     * @param pmf The PersistenceManager factory
     * @param container_class The container class e.g ArrayListNormal
     * @param item_class_parent The parent element class
     **/
    public static void checkAttachDetach(PersistenceManagerFactory pmf,
                                         Class container_class,
                                         Class item_class_parent)
    throws Exception
    {
        int NO_OF_ITEMS = 5;
        Object container_id = null;
        MapHolder detachedContainer = null;

        // Create a container and some elements
        PersistenceManager pm=pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            MapHolder container = null;
            try
            {
                container = (MapHolder) container_class.newInstance();
            }
            catch (Exception e1)
            {
                LOG.error(e1);
                Assert.fail("Failed to find Container class " + container_class.getName());
            }

            java.util.Map m=new java.util.HashMap();
            for (int i=0;i<NO_OF_ITEMS;i++)
            {
                // Create an item
                Object item=SCOHolderUtilities.createItemParent(item_class_parent,"Item " + i,0.00 + (10.00*i),i);
                m.put(new String("Key" + (i+1)),item);
            }
            SCOHolderUtilities.addItemsToMap(container,m);

            pm.makePersistent(container);
            container_id = JDOHelper.getObjectId(container);
            detachedContainer = (MapHolder) pm.detachCopy(container);

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
        SCOHolderUtilities.addItemToMap(detachedContainer, "Key10", item);

        // Attach the updated container
        pm = pmf.getPersistenceManager();
        pm.getFetchPlan().addGroup("items");
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            MapHolder attachedContainer = (MapHolder) pm.makePersistent(detachedContainer);

            // Add another item to the container
            Object newItem = SCOHolderUtilities.createItemParent(item_class_parent, "Item 20", 200, 20);
            SCOHolderUtilities.addItemToMap(attachedContainer, "Key20", newItem);

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

            MapHolder container = (MapHolder) pm.getObjectById(container_id,false);
            if (container != null)
            {
                // Get the no of items in the container
                int container_size=SCOHolderUtilities.getContainerSize(container);
                Assert.assertTrue(container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
                    (NO_OF_ITEMS+2), container_size == (NO_OF_ITEMS+2));
            }

            detachedContainer = (MapHolder) pm.detachCopy(container);

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
        int container_size = SCOHolderUtilities.getContainerSize(detachedContainer);
        Assert.assertTrue("Detached container " + container_class.getName() + " has incorrect number of items (" + container_size + ") : should have been " + 
            (NO_OF_ITEMS+2), container_size == (NO_OF_ITEMS+2));
        SCOHolderUtilities.removeItemFromMap(detachedContainer, "Key1");
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

            MapHolder container = (MapHolder) pm.getObjectById(container_id,false);
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
}