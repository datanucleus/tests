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

import java.util.Random;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.interfaces.Circle3;
import org.jpox.samples.interfaces.Circle3b;
import org.jpox.samples.interfaces.Diet;
import org.jpox.samples.interfaces.Food;
import org.jpox.samples.interfaces.Rectangle3b;
import org.jpox.samples.interfaces.ShapeHolder3b;
import org.jpox.samples.interfaces.Square3b;
import org.jpox.samples.interfaces.Steak;

/**
 * Interface tests that are feature requests to current functionality and so likely fail.
 */
public class InterfacesTest extends JDOPersistenceTestCase
{
    private Random r = new Random();

    public InterfacesTest(String name)
    {
        super(name);
    }

    /**
     * Test for the creation of an list for interface objects using FK.
     * See JIRA "NUCRDBMS-19"
     **/
    public void testListFK()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {ShapeHolder3b.class, Rectangle3b.class, Circle3b.class, Square3b.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id;
            try
            {
                // Create container and some shapes
                tx.begin();
                ShapeHolder3b container = new ShapeHolder3b(r.nextInt());
                Circle3 circle = new Circle3(r.nextInt(), 1.75);
                container.getShapeList().add(circle);
                Rectangle3b rectangle = new Rectangle3b(r.nextInt(), 1.0, 2.0);
                container.getShapeList().add(rectangle);
                assertEquals(2,container.getShapeList().size());
                pm.makePersistent(container);
                tx.commit();
                id = pm.getObjectId(container);
                pm.close();

                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                ShapeHolder3b actual = (ShapeHolder3b) pm.getObjectById(id);
                assertEquals(2,actual.getShapeList().size());
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
            
            // TODO Extend this to then query the elements in the collection
        }
        finally
        {
            clean(Circle3b.class);
            clean(Rectangle3b.class);
            clean(ShapeHolder3b.class);
        }
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