/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved. 
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.valuegeneration.TableGeneratorItem;

/**
 * Test the use of all forms of Id generators.
 * If a datastore doesn't support a particular id generator, the test is not run.
 */
public class ValueGeneratorTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ValueGeneratorTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    TableGeneratorItem.class,
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of "increment" generator for a loop of persists.
     */
    public void testIncrementStrategy()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Create a few objects.
                for (int i=0;i<100;i++)
                {
                    TableGeneratorItem item = new TableGeneratorItem("Item " + i);
                    pm.makePersistent(item);
                    idSet.add(new Integer(item.getIdentifier()));
                    idClass = JDOHelper.getObjectId(item).getClass();
                }

                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"increment\" generator test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Collection idSetCopy = new HashSet(idSet);

                // Retrieve the items
                Query q = pm.newQuery(TableGeneratorItem.class);
                Collection c = (Collection)q.execute();

                // Check on the number of items
                assertEquals("Number of TableGeneratorItem's retrieved is incorrect", 100, c.size());
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (TableGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(new Integer(((TableGeneratorItem)o).getIdentifier()));
                    }
                }

                tx.commit();

                if (TableGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 100, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any created data
            clean(TableGeneratorItem.class);
        }
    }
}