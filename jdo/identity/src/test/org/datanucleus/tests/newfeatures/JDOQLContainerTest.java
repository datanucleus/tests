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
2003 Mike Martin - original tests in JDOQLQueryTest
2004 Erik Bengtson - added many many tests
    ...
***********************************************************************/
package org.datanucleus.tests.newfeatures;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

import org.jpox.samples.models.fitness.Cloth;
import org.jpox.samples.models.fitness.FitnessHelper;
import org.jpox.samples.models.fitness.Gym;
import org.jpox.samples.models.fitness.GymEquipment;
import org.jpox.samples.models.fitness.Wardrobe;

/**
 * Tests for JDOQL queries of collections and maps.
 */
public class JDOQLContainerTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public JDOQLContainerTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                });
            initialised = true;
        }
    }

    /**
     * Tests get method used in ordering
     */
    public void testGetInOrderingInMapFields()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create some data
                tx.begin();

                Gym gym = new Gym();
                gym.setLocation("downtown");
                Gym gym2 = new Gym();
                gym2.setLocation("village");
                Wardrobe wardrobe1 = new Wardrobe();
                wardrobe1.setModel("1 door");
                Wardrobe wardrobe2 = new Wardrobe();
                wardrobe2.setModel("2 doors");
                Wardrobe wardrobe3 = new Wardrobe();
                wardrobe3.setModel("3 doors");
                gym.getWardrobes().put(wardrobe2.getModel(),wardrobe2);
                gym.getWardrobes().put(wardrobe3.getModel(),wardrobe3);
                gym2.getWardrobes().put(wardrobe1.getModel(),wardrobe1);
                pm.makePersistent(gym);
                pm.makePersistent(gym2);
                Cloth whiteShirt = new Cloth();
                whiteShirt.setName("white shirt");
                Cloth blackShirt = new Cloth();
                blackShirt.setName("black shirt");
                Cloth skirt = new Cloth();
                skirt.setName("skirt");
                wardrobe3.getClothes().add(whiteShirt);
                wardrobe3.getClothes().add(skirt);
                wardrobe2.getClothes().add(blackShirt);
                tx.commit();

                // Query the data
                tx.begin();
                Query q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                q.setOrdering("this.wardrobes.get(wrd.model).model ascending");
                List results = (List) q.execute(wardrobe3);
                assertEquals(2, results.size());
                assertEquals("downtown",((Gym)results.get(0)).getLocation());
                assertEquals("village",((Gym)results.get(1)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during query", e);
                fail("Exception thrown while performing test : " + e.getMessage());
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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * Tests the map get method with a map as literal (parameter) and
     * the key value is obtained from an expression (from the database)
     */
    public void testMapGetAsLiteralWithKeyAsExpression()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Gym gym = new Gym();
            gym.setLocation("downtown");
            Gym gym2 = new Gym();
            gym2.setLocation("village");
            Wardrobe wardrobe2 = new Wardrobe();
            wardrobe2.setModel("2 doors");
            Wardrobe wardrobe3 = new Wardrobe();
            wardrobe3.setModel("3 doors");
            Wardrobe wardrobe4 = new Wardrobe();
            wardrobe4.setModel("4 doors");
            gym.getWardrobes().put(wardrobe2.getModel(),wardrobe2);
            gym.getWardrobes().put(wardrobe3.getModel(),wardrobe3);
            gym.getWardrobes().put(wardrobe4.getModel(),wardrobe4);
            gym2.getWardrobes().put(wardrobe4.getModel(),wardrobe4);
            pm.makePersistent(gym);
            pm.makePersistent(gym2);
            Cloth whiteShirt = new Cloth();
            whiteShirt.setName("white shirt");
            Cloth blackShirt = new Cloth();
            blackShirt.setName("black shirt");
            Cloth skirt = new Cloth();
            skirt.setName("skirt");
            wardrobe3.getClothes().add(whiteShirt);
            wardrobe3.getClothes().add(skirt);
            wardrobe2.getClothes().add(blackShirt);
            tx.commit();
            tx.begin();
            
            //test map.get in map literals
            Map map1 = new HashMap();
            map1.put("2 doors",wardrobe2);
            
            Query q = pm.newQuery(Wardrobe.class,"this == map.get(this.model)");
            q.declareParameters("java.util.Map map");
            Collection c = (Collection) q.execute(map1);
            assertEquals(1,c.size());

            Map map2 = new HashMap();
            
            q = pm.newQuery(Wardrobe.class,"this == map.get(this.model)");
            q.declareParameters("java.util.Map map");
            c = (Collection) q.execute(map2);
            assertEquals(0,c.size());

            Map map3 = new HashMap();
            map3.put("2 doors",wardrobe2);
            map3.put("3 doors",wardrobe3);
            
            q = pm.newQuery(Wardrobe.class,"this == map.get(this.model)");
            q.declareParameters("java.util.Map map");
            c = (Collection) q.execute(map3);
            assertEquals(2,c.size());

            Map map4 = new HashMap();
            map4.put("5 doors",wardrobe2);
            
            q = pm.newQuery(Wardrobe.class,"this == map.get(this.model)");
            q.declareParameters("java.util.Map map");
            c = (Collection) q.execute(map3);
            assertEquals(0,c.size());
            
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * test query with "field.containsValue(x) && field.containsValue(y)" using "or" 
     * workaround. Use the workaround to bypass a deficiency on query generation
     **/
    public void testQueryUsesContainsValueTwiceOnFieldUsingWorkaroundInverse()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setName("Cinema");
            gym1.setLocation("First floor");
            Wardrobe w1 = new Wardrobe();
            Wardrobe w2 = new Wardrobe();
            Wardrobe w3 = new Wardrobe();
            Wardrobe w4 = new Wardrobe();
            w1.setModel("2 door");
            w2.setModel("3 door");
            w3.setModel("4 door");
            w4.setModel("5 door");
            gym1.getWardrobesInverse().put(w1.getModel(), w1);
            gym1.getWardrobesInverse().put(w2.getModel(), w2);
            gym1.getWardrobesInverse().put(w3.getModel(), w3);
            gym1.getWardrobesInverse().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobesInverse().put(w1.getModel(), w1);
            gym2.getWardrobesInverse().put(w2.getModel(), w2);
            Cloth c1 = new Cloth();
            c1.setName("green shirt");
            Cloth c2 = new Cloth();
            c2.setName("red shirt");
            Cloth c3 = new Cloth();
            c3.setName("blue shirt");
            GymEquipment ge1 = new GymEquipment();
            ge1.setName("Weight");
            GymEquipment ge2 = new GymEquipment();
            ge2.setName("Yoga");
            GymEquipment ge3 = new GymEquipment();
            ge3.setName("Pilates");
            GymEquipment ge4 = new GymEquipment();
            ge4.setName("Abdominal");
            gym1.getEquipmentsInverse().put(ge1.getName(), ge1);
            gym1.getEquipmentsInverse().put(ge2.getName(), ge2);
            gym2.getEquipmentsInverse().put(ge3.getName(), ge3);
            gym2.getEquipmentsInverse().put(ge4.getName(), ge4);
            gym1.getPartnersInverse().put(gym2.getName(), gym2);
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(gym1);
                pm.makePersistent(gym2);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w2) && (w1.model == \"2 door\" || w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w2) && (w1.model == \"4 door\" || w2.model == \"5 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }
    
    /**
     * test query with "field.containsKey(x) && field.containsKey(y)" using "or" 
     * workaround. Use the workaround to bypass a deficiency on query generation
     **/
    public void testQueryUsesContainsKeyTwiceOnFieldUsingWorkaroundInverse()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setName("Cinema");
            gym1.setLocation("First floor");
            Wardrobe w1 = new Wardrobe();
            Wardrobe w2 = new Wardrobe();
            Wardrobe w3 = new Wardrobe();
            Wardrobe w4 = new Wardrobe();
            w1.setModel("2 door");
            w2.setModel("3 door");
            w3.setModel("4 door");
            w4.setModel("5 door");
            gym1.getWardrobesInverse2().put(w1, w1.getModel());
            gym1.getWardrobesInverse2().put(w2, w2.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobesInverse2().put(w3, w3.getModel());
            gym2.getWardrobesInverse2().put(w4, w4.getModel());
            Cloth c1 = new Cloth();
            c1.setName("green shirt");
            Cloth c2 = new Cloth();
            c2.setName("red shirt");
            Cloth c3 = new Cloth();
            c3.setName("blue shirt");
            GymEquipment ge1 = new GymEquipment();
            ge1.setName("Weight");
            GymEquipment ge2 = new GymEquipment();
            ge2.setName("Yoga");
            GymEquipment ge3 = new GymEquipment();
            ge3.setName("Pilates");
            GymEquipment ge4 = new GymEquipment();
            ge4.setName("Abdominal");
            gym1.getEquipmentsInverse2().put(ge1, ge1.getName());
            gym1.getEquipmentsInverse2().put(ge2, ge2.getName());
            gym2.getEquipmentsInverse2().put(ge3, ge3.getName());
            gym2.getEquipmentsInverse2().put(ge4, ge4.getName());
            gym1.getPartnersInverse2().put(gym2, gym2.getName());
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(gym1);
                pm.makePersistent(gym2);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && wardrobesInverse2.containsKey(w2) && (w1.model == \"2 door\" || w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && wardrobesInverse2.containsKey(w2) && (w1.model == \"4 door\" || w2.model == \"5 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }
}