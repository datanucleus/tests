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
 

Contributions :
2004 Barry Haddow - extended to give rigorous test suite
2004 Andy Jefferson - added test of collection of interfaces
    ...
***********************************************************************/
package org.datanucleus.tests.newfeatures;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.interfaces.Diet;
import org.datanucleus.samples.interfaces.Food;
import org.datanucleus.samples.interfaces.Steak;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Interface tests that are feature requests to current functionality and so likely fail.
 */
public class InterfacesTest extends JDOPersistenceTestCase
{
    public InterfacesTest(String name)
    {
        super(name);
    }

    /**
     * Test for use of mapping-strategy="identity" for 1-1 relation, with a query taking in the string form of the id.
     */ 
    public void testMappingStrategyIdentity1To1Query()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {Diet.class});

            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            Object steakId = null;
            try
            {
                // Create some objects
                tx.begin();
                Diet diet = new Diet(1);
                Food fave = new Steak();
                diet.setFavouriteFood(fave);
                pm.makePersistent(diet);
                tx.commit();
                steakId = JDOHelper.getObjectId(fave);
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during create of interface objects.");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Try a query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                // Pass in a parameter using a String form of the id
                Query q = pm.newQuery("SELECT FROM " + Diet.class.getName() + " WHERE favouriteFood == :param");
                q.execute(Steak.class.getName() + ":" + steakId.toString());
                // TODO Compare the results
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception thrown during query", ue);
                fail("Exception thrown during query of objects");
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
            clean(Diet.class);
            clean(Steak.class);
        }
    }
}