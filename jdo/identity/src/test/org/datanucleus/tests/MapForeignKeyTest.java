/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved. 
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.one_many.map.MapFKKeyItem;
import org.jpox.samples.one_many.map.MapFKValueItem;
import org.jpox.samples.one_many.map.MapHolder;

/**
 * Series of tests for Maps using ForeignKey relations.
 *
 * @version $Revision: 1.1 $
 */
public class MapForeignKeyTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public MapForeignKeyTest(String name)
    {
        super(name);
    }

    /**
     * Test for persistence of a Map with the key stored in the value object.
     */
    public void testMapWithKeyAsFieldInValue()
    {
        try
        {
            Object containerId = null;
            MapHolder container = new MapHolder();
            MapFKValueItem item1 = new MapFKValueItem("First", "First element", "Item1");
            MapFKValueItem item3 = new MapFKValueItem("Third", "Third element", "Item3");
            MapFKValueItem item2 = new MapFKValueItem("Second", "Second element", "Item2");
            container.getFkMapKey().put(item1.getKey(), item1);
            container.getFkMapKey().put(item3.getKey(), item3);
            container.getFkMapKey().put(item2.getKey(), item2);

            // Persist the objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(container);
                tx.commit();
                containerId = JDOHelper.getObjectId(container);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object and inspect it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                container = (MapHolder)pm.getObjectById(containerId);
                assertTrue("Container map was not found!", container != null);
                Map map = container.getFkMapKey();
                assertEquals("Number of items in the container map is incorrect", map.size(), 3);

                // Check Entry set
                Set entries = map.entrySet();
                Iterator entryIter = entries.iterator();
                while (entryIter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)entryIter.next();
                    MapFKValueItem item = (MapFKValueItem)entry.getValue();
                    if (entry.getKey().equals("Item1"))
                    {
                        assertEquals("item has incorrect name for key Item1", item.getName(), "First");
                    }
                    else if (entry.getKey().equals("Item2"))
                    {
                        assertEquals("item has incorrect name for key Item2", item.getName(), "Second");
                    }
                    else if (entry.getKey().equals("Item3"))
                    {
                        assertEquals("item has incorrect name for key Item3", item.getName(), "Third");
                    }
                    else
                    {
                        fail("Unknown Map entry found with key " + entry.getKey());
                    }
                }

                // Check Key set
                Set keys = map.keySet();
                assertEquals("Number of keys in Map.keySet() is incorrect", keys.size(), 3);
                Iterator keyIter = keys.iterator();
                boolean item1Present = false;
                boolean item2Present = false;
                boolean item3Present = false;
                while (keyIter.hasNext())
                {
                    Object obj = keyIter.next();
                    assertEquals("Type of value objects returned from Map.keySet().iterator() is incorrect", 
                        obj.getClass().getName(), String.class.getName());
                    String key = (String)obj;
                    if (key.equals("Item1"))
                    {
                        item1Present = true;
                    }
                    else if (key.equals("Item2"))
                    {
                        item2Present = true;
                    }
                    else if (key.equals("Item3"))
                    {
                        item3Present = true;
                    }
                }
                assertTrue("Item1 was not present in the keySet", item1Present);
                assertTrue("Item2 was not present in the keySet", item2Present);
                assertTrue("Item3 was not present in the keySet", item3Present);

                // Check Value set
                Collection values = map.values();
                assertEquals("Number of values in Map.values() is incorrect", values.size(), 3);
                Iterator valueIter = values.iterator();
                item1Present = false;
                item2Present = false;
                item3Present = false;
                while (valueIter.hasNext())
                {
                    Object obj = valueIter.next();
                    assertEquals("Type of value objects returned from Map.values().iterator() is incorrect", 
                        obj.getClass().getName(), MapFKValueItem.class.getName());
                    MapFKValueItem value = (MapFKValueItem)obj;
                    if (value.getName().equals("First"))
                    {
                        item1Present = true;
                    }
                    else if (value.getName().equals("Second"))
                    {
                        item2Present = true;
                    }
                    else if (value.getName().equals("Third"))
                    {
                        item3Present = true;
                    }
                }
                assertTrue("Item1 was not present in the values()", item1Present);
                assertTrue("Item2 was not present in the values()", item2Present);
                assertTrue("Item3 was not present in the values()", item3Present);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
            // Clean out our data
            clean(MapHolder.class);
        }
    }

    /**
     * Test for persistence of a Map with the value stored in the key object.
     */
    public void testMapWithValueAsFieldInKey()
    {
        try
        {
            Object containerId = null;
            MapHolder container = new MapHolder();
            MapFKKeyItem item1 = new MapFKKeyItem("First", "First element", "Item1");
            MapFKKeyItem item3 = new MapFKKeyItem("Third", "Third element", "Item3");
            MapFKKeyItem item2 = new MapFKKeyItem("Second", "Second element", "Item2");
            container.getFkMapValue().put(item1, item1.getValue());
            container.getFkMapValue().put(item3, item3.getValue());
            container.getFkMapValue().put(item2, item2.getValue());

            // Persist the objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(container);
                tx.commit();
                containerId = JDOHelper.getObjectId(container);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object and inspect it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                container = (MapHolder)pm.getObjectById(containerId);
                assertTrue("Container map was not found!", container != null);
                Map map = container.getFkMapValue();
                assertEquals("Number of items in the container map is incorrect", map.size(), 3);

                // Check Entry set
                Set entries = map.entrySet();
                Iterator entryIter = entries.iterator();
                while (entryIter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)entryIter.next();
                    MapFKKeyItem item = (MapFKKeyItem)entry.getKey();
                    if (entry.getValue().equals("Item1"))
                    {
                        assertEquals("item has incorrect name for value Item1", item.getName(), "First");
                    }
                    else if (entry.getValue().equals("Item2"))
                    {
                        assertEquals("item has incorrect name for value Item2", item.getName(), "Second");
                    }
                    else if (entry.getValue().equals("Item3"))
                    {
                        assertEquals("item has incorrect name for value Item3", item.getName(), "Third");
                    }
                    else
                    {
                        fail("Unknown Map entry found with value " + entry.getValue());
                    }
                }

                // Check Key set
                Set keys = map.keySet();
                assertEquals("Number of keys in Map.keySet() is incorrect", keys.size(), 3);
                Iterator keyIter = keys.iterator();
                boolean item1Present = false;
                boolean item2Present = false;
                boolean item3Present = false;
                while (keyIter.hasNext())
                {
                    Object obj = keyIter.next();
                    assertEquals("Type of key objects returned from Map.keySet().iterator() is incorrect", 
                        obj.getClass().getName(), MapFKKeyItem.class.getName());
                    MapFKKeyItem key = (MapFKKeyItem)obj;
                    if (key.getName().equals("First"))
                    {
                        item1Present = true;
                    }
                    else if (key.getName().equals("Second"))
                    {
                        item2Present = true;
                    }
                    else if (key.getName().equals("Third"))
                    {
                        item3Present = true;
                    }
                }
                assertTrue("Item1 was not present in the keySet", item1Present);
                assertTrue("Item2 was not present in the keySet", item2Present);
                assertTrue("Item3 was not present in the keySet", item3Present);

                // Check Value set
                Collection values = map.values();
                assertEquals("Number of values in Map.values() is incorrect", values.size(), 3);
                Iterator valueIter = values.iterator();
                item1Present = false;
                item2Present = false;
                item3Present = false;
                while (valueIter.hasNext())
                {
                    Object obj = valueIter.next();
                    assertEquals("Type of value objects returned from Map.values().iterator() is incorrect", 
                        obj.getClass().getName(), String.class.getName());
                    String value = (String)obj;
                    if (value.equals("Item1"))
                    {
                        item1Present = true;
                    }
                    else if (value.equals("Item2"))
                    {
                        item2Present = true;
                    }
                    else if (value.equals("Item3"))
                    {
                        item3Present = true;
                    }
                }
                assertTrue("Item1 was not present in the values()", item1Present);
                assertTrue("Item2 was not present in the values()", item2Present);
                assertTrue("Item3 was not present in the values()", item3Present);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
            // Clean out our data
            clean(MapHolder.class);
        }
    }
}