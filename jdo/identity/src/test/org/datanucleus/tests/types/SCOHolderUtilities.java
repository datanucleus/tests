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
  
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

import junit.framework.Assert;

import org.datanucleus.tests.TestHelper;
import org.jpox.samples.types.container.CollectionHolder;
import org.jpox.samples.types.container.ContainerHolder;
import org.jpox.samples.types.container.ListHolder;
import org.jpox.samples.types.container.MapHolder;

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
public class SCOHolderUtilities
{
    /**
     * Utility to create an Item (parent) object.
     **/
    static Object createItemParent(Class item_class, String name, double value, int status)
    {
        if( item_class == String.class )
        {
            return new Double(value).toString();
        }
        Object item=null;
        try
        {
            Class[] ctr_args_classes=new Class[] {String.class,double.class,int.class};
            Object[] ctr_args=new Object[] {name,new Double(value),new Integer(status)};
            Constructor ctr=item_class.getConstructor(ctr_args_classes);
            item = ctr.newInstance(ctr_args);
        }
        catch (Exception e)
        {
            TestHelper.LOG.error(e);
            Assert.fail("Failed to create Item of class " + item_class.getName() + " for adding to container");
        }
        return item;
    }

    /**
     * Utility to create an Item (child) object.
     **/
    static Object createItemChild(Class item_class, String name, double value, int status, String code)
    {
        if( item_class == String.class )
        {
            return new Double(value).toString();
        }
        Object item=null;
        try
        {
            Class[] ctr_args_classes=new Class[] {String.class,double.class,int.class,String.class};
            Object[] ctr_args=new Object[] {name,new Double(value),new Integer(status),code};
            Constructor ctr=item_class.getConstructor(ctr_args_classes);
            item = ctr.newInstance(ctr_args);
        }
        catch (Exception e)
        {
            TestHelper.LOG.error(e);
            Assert.fail("Failed to create Item of class " + item_class.getName() + " for adding to container");
        }
        return item;
    }

    /**
     * Utility to set the collection for an item.
     **/
    static void setContainerForItem(Class element_class, Object element,
                                    Class container_class, Object container)
    {
        try
        {
            Class[] param_classes=new Class[] {container_class};
            Object[] params=new Object[] {container};
            Method add_method=element_class.getMethod("setContainer",param_classes); 
            add_method.invoke(element,params);
        }
        catch (Exception e)
        {
            TestHelper.LOG.error(e);
            Assert.fail("Failed to set the container for an item of class " + element_class.getName());
        }
    }

    /**
     * Utility to add an Item to a Collection.
     **/
    static void addItemToCollection(CollectionHolder container, Object item)
    {
        container.addItem(item);
    }

    /**
     * Utility to add an Item to a Collection at a position.
     **/
    static void addItemToCollection(ListHolder container, Object item, int position)
    {
        container.addItem(item,position);
    }

    /**
     * Utility to add a collection of Items to a Collection.
     **/
    static void addItemsToCollection(CollectionHolder container, Collection items)
    {
        container.addItems(items);
    }

    /**
     * Utility to add an Item to a Map.
     **/
    static void addItemToMap(MapHolder container, Object key, Object item)
    {
        container.putItem(key,item);
    }

    /**
     * Utility to add a map of Items to a Container.
     **/
    static void addItemsToMap(MapHolder container, java.util.Map items)
    {
        container.putItems(items);
    }

    /**
     * Utility to remove an Item from a Collection.
     **/
    static void removeItemFromCollection(CollectionHolder container, Object item)
    {
        container.removeItem(item);
    }

    /**
     * Utility to remove an Item from a Collection at a position.
     **/
    static void removeItemFromCollection(ListHolder container, int position)
    {
        container.removeItem(position);
    }

    /**
     * Utility to remove a collection of Items from a Collection.
     **/
    static void removeItemsFromCollection(CollectionHolder container, Collection items)
    {
        container.removeItems(items);
    }

    /**
     * Utility to retain a collection of Items in a Collection.
     **/
    static void retainItemsInCollection(CollectionHolder container, Collection items)
    {
        container.retainItems(items);
    }

    /**
     * Utility to retrieve an Item from a List at a position.
     **/
    static Object getItemFromList(ListHolder container, int position)
    {
        return container.getItem(position);
    }

    /**
     * Utility to remove an Item from a Map.
     **/
    static void removeItemFromMap(MapHolder container,  Object key)
    {
        container.removeItem(key);
    }

    /**
     * Utility to clear out the Items from a Container.
     * Applies to Map or Collection. 
     **/
    static void clearItemsFromContainer(ContainerHolder container)
    {
        container.clear();
    }

    /**
     * Utility to check if a container is empty.
     * Applies to Map or Collection. 
     **/
    static boolean isContainerEmpty(ContainerHolder container)
    {
        return container.isEmpty();
    }

    /**
     * Utility to return the size of a container.
     * Applies to Map or Collection. 
     **/
    static int getContainerSize(ContainerHolder container)
    {
        return container.getNoOfItems();
    }

    /**
     * Utility to return the items of a Collection via an Iterator.
     **/
    static Iterator getCollectionItemsIterator(CollectionHolder container)
    {
        return container.getItems().iterator();
    }

    /**
     * Utility to return an item from a Map
     **/
    static Object getItemFromMap(MapHolder container, Object key)
    {
        return container.getItem(key);
    }

    /**
     * Utility to retrieve the entrySet for a Map.
     **/
    static Set getEntrySetFromMap(MapHolder container)
    {
        return container.getEntrySet();
    }
    
    /**
     * Utility to retrieve the keySet for a Map.
     **/
    static Set getKeySetFromMap(MapHolder container)
    {
        return container.getKeySet();
    }
    
    /**
     * Utility to retrieve the values for a Map.
     **/
    static Collection getValuesFromMap(MapHolder container)
    {
        return container.getValues();
    }
    
    /**
     * Compares two sets of Objects.  Returns true if and only if the two
     * sets contain the same number of objects and each element of the first
     * set has a corresponding element in the second set whose fields compare
     * equal according to the compareTo() method.
     *
     * @return  <tt>true</tt> if the sets compare equal,
     *          <tt>false</tt> otherwise.
     */

    public static boolean compareSet(Collection s1, Collection s2)
    {
        if (s1 == null)
        {
            return s2 == null;
        }
        else if (s2 == null)
        {
            return false;
        }

        if (s1.size() != s2.size())
        {
            return false;
        }

        HashSet set2 = new HashSet(s2);

        Iterator i = s1.iterator();

        while (i.hasNext())
        {
            Object obj = i.next();

            boolean found = false;
            Iterator j = set2.iterator();

            while (j.hasNext())
            {
                if (obj.equals(j.next()))
                {
                    j.remove();
                    found = true;
                    break;
                }
            }

            if (!found)
                return false;
        }

        return true;
    }    
}