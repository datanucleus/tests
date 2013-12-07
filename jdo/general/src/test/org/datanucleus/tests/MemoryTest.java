/**********************************************************************
Copyright (c) 2004 Ralf Ulrich and others. All rights reserved.
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

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.fitness.Cloth;
import org.jpox.samples.models.fitness.Wardrobe;

/**
 * Tests for memory utilisation aspects of DataNucleus.
 */
public class MemoryTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public MemoryTest(String name)
    {
        super(name);
    }

    /**
     * Test for memory utilisation when persisting large numbers of new objects.
     */
    public void testMemoryManagementNewPC()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                for (int i=0; i<10000; i++)
                {
                    Wardrobe wardrobe = new Wardrobe();
                    wardrobe.setModel("3 doors");
                    pm.makePersistent(wardrobe);
                    if (i % 10000 == 0)
                    {
                        pm.flush();
                    }
                }
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
        }
        finally
        {
            // Clean out our data
            clean(Cloth.class);
            clean(Wardrobe.class);
        }
    }
}