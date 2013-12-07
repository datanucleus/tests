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
package org.datanucleus.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.valuegeneration.TableGeneratorItem;

/**
 * Tests for value generators.
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
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of "increment"/"table" generator
     */
    public void testTableGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                TableGeneratorItem item=null;
                item = new TableGeneratorItem("First item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Second item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Third item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Fourth item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"table\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q = pm.newQuery(pm.getExtent(TableGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertEquals("Number of TableGeneratorItem's retrieved is incorrect", 4, c.size());
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
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
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