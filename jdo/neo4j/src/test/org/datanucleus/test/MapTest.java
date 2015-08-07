/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved. 
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
package org.datanucleus.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.one_many.map_fk.MapFKHolder;
import org.jpox.samples.one_many.map_fk.MapFKValue;
import org.jpox.samples.one_many.map_fk.MapFKValueBase;

/**
 * Tests for maps in Neo4j. Same as test.jdo.application RelationshipTest "test1toNBidirFKMapWithInheritedValue".
 */
public class MapTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public MapTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    MapFKHolder.class,
                    MapFKValue.class,
                    MapFKValueBase.class
                }
            );
            initialised = true;
        }
    }

    public void test1toNBidirFKMapWithInheritedValue()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;

            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                MapFKHolder holder = new MapFKHolder("First");
                MapFKValue value1 = new MapFKValue("Value 1", "First value", "1");
                value1.setHolder(holder);
                MapFKValue value2 = new MapFKValue("Value 2", "Second value", "2");
                value2.setHolder(holder);
                holder.getMap().put(value1.getKey(), value1);
                holder.getMap().put(value2.getKey(), value2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting data", e);
                fail("Exception thrown while creating 1-N (Map) bidir FK relationships with inherited value : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf.getDataStoreCache().evictAll();

            // Check the retrieval of the owner and values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                MapFKHolder holder = (MapFKHolder)pm.getObjectById(holderId);
                assertNotNull("Unable to retrieve container object for 1-N bidir FK relationship (Map)", holder);
                assertEquals("Number of map entries is not correct", 2, holder.getMap().size());

                // Force the retrieval of the entrySet
                Collection<Map.Entry<String, MapFKValue>> mapEntries = holder.getMap().entrySet();
                for (Map.Entry<String, MapFKValue> entry : mapEntries)
                {
                    String key = entry.getKey();
                    MapFKValue val = entry.getValue();
                    LOG.debug("Map key="+ key + " value=" + val);
                }

                Collection values = holder.getMap().values();
                Iterator iter = values.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                assertEquals("Number of values in 1-N bidir FK relationship (Map) is wrong", 2, values.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception running test", e);
                fail("Exception thrown while querying 1-N bidir FK relationships (Map) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean out our data
            clean(MapFKHolder.class);
            clean(MapFKValue.class);
        }
    }
}
