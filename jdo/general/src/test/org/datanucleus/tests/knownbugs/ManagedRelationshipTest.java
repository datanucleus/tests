/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.knownbugs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.one_many.bidir_2.House;
import org.jpox.samples.one_many.bidir_2.Window;

/**
 * Tests for managed relationships.
 */
public class ManagedRelationshipTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ManagedRelationshipTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    House.class,
                    Window.class,
                });
            initialised = true;
        }
    }

    <T> Set<T> createSet(T... elements) 
    {
        Set<T> result = new HashSet<T>();
        for (T t : elements)
        {
            result.add(t);
        }
        return result;
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element is being moved
     * from one collection owner to another, by setting a collection containing that element
     * on a new owner.
     * See NUCCORE-291
     * @see {@link #testOneToManyFKBidirSetCollectionMoveElement()}
     */
    public void testOneToManyJoinBidirSetCollectionMoveElement()
    {
        PersistenceManager pm = null;
        Transaction tx = null;
        try
        {
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();

            // Create object graph:
            // house1 <-> {window1, window2}
            // house2 <-> {window3, window4}
            // house5 <-> {window5}
            tx.begin();

            House house1 = new House(1, "house1");
            Window window1 = new Window("window1");
            Window window2 = new Window("window2");
            house1.setWindows(createSet(window1, window2));

            pm.makePersistent(house1);
            
            House house2 = new House(2, "house2");
            Window window3 = new Window("window3");
            Window window4 = new Window("window4");
            house2.setWindows(createSet(window3, window4));
            
            pm.makePersistent(house2);
            
            House house3 = new House(3, "house3");
            Window window5 =  new Window("window5");
            house3.setWindows(createSet(window5));
            
            pm.makePersistent(house3);

            pm.flush();

            // validate objectgraph
            assertEquals(createSet(window1, window2), house1.getWindows());
            assertEquals(house1, window1.getHouse());
            assertEquals(house1, window2.getHouse());
            
            assertEquals(createSet(window3, window4), house2.getWindows());
            assertEquals(house2, window3.getHouse());
            assertEquals(house2, window4.getHouse());
            
            assertEquals(createSet(window5), house3.getWindows());
            assertEquals(house3, window5.getHouse());

            tx.commit();

            
            // perform update and validate
            tx.begin();
            house1.setWindows(createSet(window2, window3, window5));
            pm.flush();
            // should result in:
            // house1 <-> {window2, window3, window5}
            // house2 <-> {window4}
            // house3 <-> {}
            // i.e. window3 and window5 moved from their previous owners to house1

            assertEquals(createSet(window2, window3, window5), house1.getWindows());
            assertEquals(house1, window2.getHouse());
            assertEquals(house1, window3.getHouse());
            assertEquals(house1, window5.getHouse());

            assertEquals(createSet(window4), house2.getWindows());
            assertEquals(house2, window4.getHouse());
            
            assertEquals(Collections.EMPTY_SET, house3.getWindows());

            tx.commit();
        }
        finally
        {
            try 
            {
                if (tx!=null && tx.isActive())
                {
                    tx.rollback();
                }
                if (pm!=null)
                {
                    pm.close();
                }
            }
            finally
            {
                clean(House.class);
                clean(Window.class);
            }
        }
    }
}