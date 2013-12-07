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
package org.datanucleus.tests;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.many_many.AccountCustomer;
import org.jpox.samples.many_many.GasSupplier;
import org.jpox.samples.many_many.OilSupplier;
import org.jpox.samples.many_many.OneOffCustomer;
import org.jpox.samples.many_many.PetroleumCustomer;
import org.jpox.samples.many_many.PetroleumSupplier;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Office;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Project;
import org.jpox.samples.models.fitness.Cloth;
import org.jpox.samples.models.fitness.FitnessHelper;
import org.jpox.samples.models.fitness.Gym;
import org.jpox.samples.models.fitness.GymEquipment;
import org.jpox.samples.models.fitness.Wardrobe;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.DairyFarm;
import org.jpox.samples.one_many.bidir.Farm;
import org.jpox.samples.one_many.map.MapHolder;

/**
 * Tests for JDOQL queries of collections and maps.
 */
public class JDOQLContainerTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLContainerTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    PetroleumCustomer.class,
                    PetroleumSupplier.class,
                    AccountCustomer.class,
                    OneOffCustomer.class,
                    GasSupplier.class,
                    OilSupplier.class,
                    Farm.class,
                    DairyFarm.class,
                    Animal.class
                });
            initialised = true;
        }
    }

    /**
     * Tests contains() of the value of a field in the candidate.
     */
    public void testContainsFieldOfCandidate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some data
            tx.begin();

            Farm farm1 = new Farm("Jones Farm");
            Animal animal1 = new Animal("Dog");
            Animal animal2 = new Animal("Cow 1");
            Animal animal3 = new Animal("Cow 2");
            farm1.getAnimals().add(animal2);
            farm1.getAnimals().add(animal3);
            farm1.getAnimals().add(animal1);
            animal1.setFarm(farm1);
            animal2.setFarm(farm1);
            animal3.setFarm(farm1);
            farm1.setPet(animal1);

            Farm farm2 = new Farm("Smith Farm");
            Animal animal4 = new Animal("Pig 1");
            Animal animal5 = new Animal("Pig 2");
            farm2.getAnimals().add(animal4);
            farm2.getAnimals().add(animal5);
            animal4.setFarm(farm2);
            animal5.setFarm(farm2);

            pm.makePersistent(farm1);
            pm.makePersistent(farm2);
            pm.flush();

            // Query the data
            Query q = pm.newQuery("SELECT FROM " + Farm.class.getName() + " WHERE animals.contains(pet)");
            List results = (List) q.execute();
            assertEquals(1, results.size());
            assertEquals("Jones Farm", ((Farm)results.get(0)).getName());

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test", e);
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

    /**
     * Tests !contains() of the value of a field in the candidate.
     */
    public void testNotContainsFieldOfCandidate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create some data
            tx.begin();

            Farm farm1 = new Farm("Jones Farm");
            Animal animal1 = new Animal("Dog");
            Animal animal2 = new Animal("Cow 1");
            Animal animal3 = new Animal("Cow 2");
            farm1.getAnimals().add(animal2);
            farm1.getAnimals().add(animal3);
            farm1.getAnimals().add(animal1);
            animal1.setFarm(farm1);
            animal2.setFarm(farm1);
            animal3.setFarm(farm1);
            farm1.setPet(animal1);

            Farm farm2 = new Farm("Smith Farm");
            Animal animal4 = new Animal("Pig 1");
            Animal animal5 = new Animal("Pig 2");
            farm2.getAnimals().add(animal4);
            farm2.getAnimals().add(animal5);
            animal4.setFarm(farm2);
            animal5.setFarm(farm2);
            Animal animal6 = new Animal("Cat");
            farm2.setPet(animal6);

            pm.makePersistent(farm1);
            pm.makePersistent(farm2);
            pm.flush();

            // Query the data
            Query q = pm.newQuery("SELECT FROM " + Farm.class.getName() + " WHERE !animals.contains(pet)");
            List results = (List) q.execute();
            assertEquals(1, results.size());
            assertEquals("Smith Farm", ((Farm)results.get(0)).getName());

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test", e);
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

    /**
     * Tests NOT contains in Map.values
     */
    public void testNotContainsValuesInMapFields()
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
                pm.flush();

                // Query the data
                Query q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes.containsValue(wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes.containsValue(wrd) && !this.wardrobes.containsValue(wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes.containsValue(wrd) && !this.wardrobes.containsValue(wrd2) && this.wardrobes.containsValue(wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests NOT contains in Map.values
     */
    public void testNotContainsValuesInMapFieldsInverse()
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
                gym.getWardrobesInverse().put(wardrobe2.getModel(),wardrobe2);
                gym.getWardrobesInverse().put(wardrobe3.getModel(),wardrobe3);
                gym2.getWardrobesInverse().put(wardrobe1.getModel(),wardrobe1);
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
                pm.flush();

                // Query the data
                Query q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse.containsValue(wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse.containsValue(wrd) && !this.wardrobesInverse.containsValue(wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse.containsValue(wrd) && !this.wardrobesInverse.containsValue(wrd2) && this.wardrobesInverse.containsValue(wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests NOT contains in Map.keys
     */
    public void testNotContainsKeysInMapFields()
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
                gym.getWardrobes2().put(wardrobe2,wardrobe2.getModel());
                gym.getWardrobes2().put(wardrobe3,wardrobe3.getModel());
                gym2.getWardrobes2().put(wardrobe1,wardrobe1.getModel());
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
                    "WHERE !this.wardrobes2.containsKey(wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes2.containsKey(wrd) && !this.wardrobes2.containsKey(wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes2.containsKey(wrd) && !this.wardrobes2.containsKey(wrd2) && this.wardrobes2.containsKey(wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests NOT contains in Map.keys
     */
    public void testNotContainsKeysInMapFieldsInverse()
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
                gym.getWardrobesInverse2().put(wardrobe2,wardrobe2.getModel());
                gym.getWardrobesInverse2().put(wardrobe3,wardrobe3.getModel());
                gym2.getWardrobesInverse2().put(wardrobe1,wardrobe1.getModel());
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
                    "WHERE !this.wardrobesInverse2.containsKey(wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse2.containsKey(wrd) && !this.wardrobesInverse2.containsKey(wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse2.containsKey(wrd) && !this.wardrobesInverse2.containsKey(wrd2) && this.wardrobesInverse2.containsKey(wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests NOT contains in Map.entry
     */
    public void testNotContainsEntryInMapFields()
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
                    "WHERE !this.wardrobes.containsEntry(wrd.model,wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes.containsEntry(wrd.model,wrd) && !this.wardrobes.containsEntry(wrd2.model,wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobes.containsEntry(wrd.model,wrd) && !this.wardrobes.containsEntry(wrd2.model,wrd2) && this.wardrobes.containsEntry(wrd1.model,wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests NOT contains in Map.entry
     */
    public void testNotContainsEntryInMapFieldsInverse()
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
                gym.getWardrobesInverse().put(wardrobe2.getModel(),wardrobe2);
                gym.getWardrobesInverse().put(wardrobe3.getModel(),wardrobe3);
                gym2.getWardrobesInverse().put(wardrobe1.getModel(),wardrobe1);
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
                    "WHERE !this.wardrobesInverse.containsEntry(wrd.model,wrd) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse.containsEntry(wrd.model,wrd) && !this.wardrobesInverse.containsEntry(wrd2.model,wrd2) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2");
                results = (List) q.execute(wardrobe3,wardrobe2);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());
                //two !contains and one contains
                q = pm.newQuery("SELECT FROM org.jpox.samples.models.fitness.Gym " +
                    "WHERE !this.wardrobesInverse.containsEntry(wrd.model,wrd) && !this.wardrobesInverse.containsEntry(wrd2.model,wrd2) && this.wardrobesInverse.containsEntry(wrd1.model,wrd1) " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd,org.jpox.samples.models.fitness.Wardrobe wrd2,org.jpox.samples.models.fitness.Wardrobe wrd1");
                results = (List) q.execute(wardrobe3,wardrobe2,wardrobe1);
                assertEquals(1, results.size());
                assertEquals("village",((Gym)results.get(0)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Tests get
     */
    public void testGetInMapFields()
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
                    "WHERE this.wardrobes.get(wrd.model) == wrd " +
                    "PARAMETERS org.jpox.samples.models.fitness.Wardrobe wrd");
                List results = (List) q.execute(wardrobe3);
                assertEquals(1, results.size());
                assertEquals("downtown",((Gym)results.get(0)).getLocation());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
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
     * Test of !contains in a JDOQL statement.
     */
    public void testNegateContains()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some data
                tx.begin();
                Office o1 = new Office(1, "Green", "Big spacious office");
                Office o2 = new Office(2, "Blue", "Pokey office at the back of the building");
                Office o3 = new Office(1, "Yellow", "Massive open plan office");
                Department d1 = new Department("Finance");
                Department d2 = new Department("Customer Support");
                Department d3 = new Department("Sales");
                Department d4 = new Department("IT");
                o1.addDepartment(d1);
                o1.addDepartment(d3);
                o2.addDepartment(d2);
                o3.addDepartment(d4);
                o3.addDepartment(d3);
                pm.makePersistent(o1);
                pm.makePersistent(o2);
                pm.makePersistent(o3);

                tx.commit();
                
                tx.begin();
                Query q = pm.newQuery(Office.class);
                q.setFilter("!(departments.contains(dept) && dept.name.equals(\"Sales\"))");
                q.declareVariables("Department dept");
                q.declareImports("import org.jpox.samples.models.company.Department");
                Collection c = (Collection) q.execute();
                assertEquals(1,c.size()); // Only Office 2 doesnt have "Sales"
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
            CompanyHelper.clearCompanyData(pmf);
	    }
    }

    /**
     * Tests contains() in List fields
     */
    public void testContainsInListFields()
    {
        Object id = null;
        Object idCloth = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Wardrobe wardrobe2 = new Wardrobe();
            wardrobe2.setModel("2 doors");
            Wardrobe wardrobe3 = new Wardrobe();
            wardrobe3.setModel("3 doors");
            pm.makePersistent(wardrobe2);
            pm.makePersistent(wardrobe3);
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

            id = JDOHelper.getObjectId(wardrobe3);
            idCloth = JDOHelper.getObjectId(blackShirt);
            tx.begin();
            Query q = pm.newQuery(Wardrobe.class,"this.clothes.contains(c)");
            q.declareParameters("org.jpox.samples.models.fitness.Cloth c");
            Collection c = (Collection) q.execute(skirt);
            assertEquals(1,c.size());
            assertEquals(id,JDOHelper.getObjectId(c.iterator().next()));
            assertEquals(2,wardrobe3.getClothes().size());
            tx.commit();

            tx.begin();
            Query q1 = pm.newQuery(Cloth.class,"wardrobe.clothes.contains(this) && wardrobe.model ==\"2 doors\"");
            q1.declareVariables("org.jpox.samples.models.fitness.Wardrobe wardrobe");
            Collection c1 = (Collection) q1.execute();
            assertEquals(1,c1.size());
            assertEquals(idCloth,JDOHelper.getObjectId(c1.iterator().next()));
            tx.commit();

            tx.begin();
            Query q2 = pm.newQuery(Cloth.class,"wardrobe.clothes.contains(this)");
            q2.declareParameters("org.jpox.samples.models.fitness.Wardrobe wardrobe");
            Collection c2 = (Collection) q2.execute(wardrobe2);
            assertEquals(1,c2.size());
            assertEquals(idCloth,JDOHelper.getObjectId(c2.iterator().next()));
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
     * Tests contains() in Set fields
     */
    public void testContainsInSetFields()
    {
        Object managerId = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Manager manager = new Manager(1, "John", "Doe", "john.doe@acme.com", 10000f, "1");
            Department dept1 = new Department("accounting");
            Department dept2 = new Department("entertainment");
            Employee emp1 = new Employee(2, "Harvey", "Hacker", "harvey.hacker@acme.com", 500f, "2");
            Employee emp2 = new Employee(3, "Geoffrey", "Gimp", "geoffrey.gimp@acme.com", 500f, "3");
            pm.makePersistentAll(new Object[]{manager, dept1, dept2, emp1, emp2});
            managerId = JDOHelper.getObjectId(manager);
            
            Set depts = manager.getDepartments();
            depts.add(dept1);
            depts.add(dept2);
            Set emps = manager.getSubordinates();
            emps.add(emp1);
            emps.add(emp2);
            pm.flush();

            Query q = pm.newQuery(Manager.class,"departments.contains(d) && subordinates.contains(e)");
            q.declareParameters(Department.class.getName() + " d, " + Employee.class.getName() + " e");
            q.compile();
            
            Collection c = (Collection) q.execute(dept1, emp1);
            assertEquals(1,c.size());
            assertEquals(managerId,JDOHelper.getObjectId(c.iterator().next()));
            tx.rollback();
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

    /**
     * Tests contains() in Map fields
     */
    public void testContainsInMapFields()
    {
        Object idGym = null;
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
            gym.getWardrobes().put(wardrobe2.getModel(),wardrobe2);
            gym.getWardrobes().put(wardrobe3.getModel(),wardrobe3);
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
            idGym = JDOHelper.getObjectId(gym);

            tx.begin();
            Query q = pm.newQuery(Gym.class,"this.wardrobes.containsValue(w)");
            q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
            Collection c = (Collection) q.execute(wardrobe3);
            assertEquals(1,c.size());
            assertEquals(idGym,JDOHelper.getObjectId(c.iterator().next()));
            assertEquals(2,gym.getWardrobes().size());
            tx.commit();

            tx.begin();
            Query q1 = pm.newQuery(Wardrobe.class,"gym.wardrobes.containsValue(this) && gym.location ==\"downtown\"");
            q1.declareVariables("org.jpox.samples.models.fitness.Gym gym");
            Collection c1 = (Collection) q1.execute();
            assertEquals(2,c1.size());
            assertTrue(c1.contains(wardrobe3));
            assertTrue(c1.contains(wardrobe2));
            tx.commit();

            tx.begin();
            Query q2 = pm.newQuery(Wardrobe.class,"gym.wardrobes.containsValue(this)");
            q2.declareParameters("org.jpox.samples.models.fitness.Gym gym");
            Collection c2 = (Collection) q2.execute(gym);
            assertEquals(2,c2.size());
            assertTrue(c2.contains(wardrobe3));
            assertTrue(c2.contains(wardrobe2));
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

    public void testContainsInParameterCollection()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);
            Department deptA = new Department("deptA");
            Department deptB = new Department("deptB");
            Department deptC = new Department("deptC");
            Department deptD = new Department("deptD");
            Department deptE = new Department("deptE");
            boss.addDepartment(deptA);
            boss.addDepartment(deptB);
            boss.addDepartment(deptC);
            boss2.addDepartment(deptD);
            boss2.addDepartment(deptE);
            deptA.setManager(boss);
            deptB.setManager(boss);
            deptC.setManager(boss);
            deptD.setManager(boss2);
            deptE.setManager(boss2);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("emps.contains(this.manager)");
                q.declareParameters("java.util.Collection emps");
                Collection c = (Collection) q.execute(emps);
                assertEquals(1,c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(), "Homer");

                q = pm.newQuery(Manager.class);
                q.setFilter("emps.contains(this.manager) && (this.lastName == 'WakesUp2' || this.lastName == 'WakesUp4')");
                q.declareParameters("java.util.Collection emps");
                c = (Collection) q.execute(emps);
                assertEquals(0,c.size());

                //should work... but
                //q = pm.newQuery(Manager.class);
                //q.setFilter("mgr0.departments.contains(dept) && (dept.manager != this)");
                //q.setFilter("(dept.manager != this) && mgr0.departments.contains(dept)");
                //q.declareVariables("org.jpox.samples.models.company.Department dept");
                //q.declareParameters("org.jpox.samples.models.company.Manager mgr0");
                //c = (Collection) q.execute(boss2);
                //assertEquals(1,c.size());                
                //tx.commit();
                
                //q = pm.newQuery(Department.class);
                //q.setFilter("emps.contains(mgr0) && (mgr0.lastName == 'WakesUp2' || mgr0.lastName == 'WakesUp4') && !emps.departments.contains(this)");
                //q.declareParameters("java.util.Collection emps");
                //q.declareVariables("org.jpox.samples.models.company.Manager mgr0");
                //c = (Collection) q.execute(emps);               
                //tx.commit();

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
            CompanyHelper.clearCompanyData(pmf);
	    }
    }

    public void testContainsInParameterArray()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);
            Department deptA = new Department("deptA");
            Department deptB = new Department("deptB");
            Department deptC = new Department("deptC");
            Department deptD = new Department("deptD");
            Department deptE = new Department("deptE");
            boss.addDepartment(deptA);
            boss.addDepartment(deptB);
            boss.addDepartment(deptC);
            boss2.addDepartment(deptD);
            boss2.addDepartment(deptE);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Department[] depts = (Department[])boss2.getDepartments().toArray(
                    new Department[boss2.getDepartments().size()]);

                // Get all Departments that are present in our array
                Query q = pm.newQuery(Department.class);
                q.setFilter("dept.contains(this)");
                q.declareParameters("org.jpox.samples.models.company.Department[] dept");
                q.setOrdering("this.name ascending");
                Collection c = (Collection) q.execute(depts);
                assertEquals(2, c.size());
                Iterator it = c.iterator();
                assertEquals(((Department)it.next()).getName(), "deptD");
                assertEquals(((Department)it.next()).getName(), "deptE");

                // Get all Managers whose Departments are present in our array
                q = pm.newQuery(Manager.class);
                q.setResult("distinct this");
                q.setFilter("dept0.manager == this && dept.contains(dept0)");
                q.declareParameters("org.jpox.samples.models.company.Department[] dept");
                q.declareVariables("org.jpox.samples.models.company.Department dept0");
                q.setOrdering("this.firstName ascending");
                c = (Collection) q.execute(depts);
                assertEquals(1, c.size()); // boss2
                it = c.iterator();

                // Get all Managers whose Departments are not present in our array
                q = pm.newQuery(Manager.class);
                q.setResult("distinct this");
                q.setFilter("dept0.manager != this && dept.contains(dept0)");
                q.declareParameters("org.jpox.samples.models.company.Department[] dept");
                q.declareVariables("org.jpox.samples.models.company.Department dept0");
                q.setOrdering("this.firstName ascending");
                c = (Collection) q.execute(depts);
                assertEquals(4, c.size()); // not boss2
                it = c.iterator();
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testContainsInParameterArray2()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);
            Department deptA = new Department("deptA");
            Department deptB = new Department("deptB");
            Department deptC = new Department("deptC");
            Department deptD = new Department("deptD");
            Department deptE = new Department("deptE");
            boss.addDepartment(deptA);
            boss.addDepartment(deptB);
            boss.addDepartment(deptC);
            boss2.addDepartment(deptD);
            boss2.addDepartment(deptE);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Department[] depts = (Department[])boss2.getDepartments().toArray(
                    new Department[boss2.getDepartments().size()]);

                // Get all Managers whose Departments are present in our array
                // This is the same as the test above yet with the clauses rearranged
                Query q = pm.newQuery(Manager.class);
                q.setResult("distinct this");
                q.setFilter("dept.contains(dept0) && dept0.manager == this");
                q.declareParameters("org.jpox.samples.models.company.Department[] dept");
                q.declareVariables("org.jpox.samples.models.company.Department dept0");
                q.setOrdering("this.firstName ascending");
                Collection c = (Collection) q.execute(depts);
                assertEquals(1, c.size()); // boss2
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testContainsResultVariable()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            Department deptA = new Department("deptA");
            Department deptB = new Department("deptB");
            Department deptC = new Department("deptC");
            Department deptD = new Department("deptD");
            Department deptE = new Department("deptE");
            boss.addDepartment(deptA);
            boss.addDepartment(deptB);
            boss.addDepartment(deptC);
            boss2.addDepartment(deptD);
            boss2.addDepartment(deptE);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("departments.contains(d)");
                q.setResult("d");
                q.declareVariables("Department d");
                Collection c = (Collection) q.execute();
                assertEquals(5,c.size());

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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    public void testContainsResultVariableNestedContains()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            Department deptA = new Department("deptA");
            Department deptB = new Department("deptB");
            Department deptC = new Department("deptC");
            Department deptD = new Department("deptD");
            Department deptE = new Department("deptE");
            Project projA = new Project("projA",1);
            Project projB = new Project("projB",2);
            Project projC = new Project("projC",3);
            Project projD = new Project("projD",4);
            Project projE = new Project("projE",5);
            deptA.addProject(projA);
            deptA.addProject(projB);
            deptB.addProject(projC);
            deptC.addProject(projA);
            deptC.addProject(projB);
            deptD.addProject(projD);
            deptE.addProject(projA);
            deptE.addProject(projE);
            boss.addDepartment(deptA);
            boss.addDepartment(deptB);
            boss.addDepartment(deptC);
            boss2.addDepartment(deptD);
            boss2.addDepartment(deptE);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("departments.contains(d) && d.projects.contains(p)");
                q.setResult("d, p");
                q.declareVariables("Department d; Project p");
                Collection c = (Collection) q.execute();
                assertEquals(8,c.size());

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
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    public void testContainsInParameterCollectionOfPCleanInstances()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q1 = pm.newQuery(Manager.class);
                Collection coll1 = (Collection)q1.execute();

                Query q2 = pm.newQuery(Manager.class);
                q2.setFilter("emps.contains(this.manager)");
                q2.declareParameters("java.util.Collection emps");
                Collection coll2 = (Collection) q2.execute(coll1);
                assertEquals(3, coll2.size());
                Iterator iter = coll2.iterator();
                boolean bartPresent = false;
                boolean homerPresent = false;
                boolean bossPresent = false;
                while (iter.hasNext())
                {
                    Manager m = (Manager)iter.next();
                    if (m.getFirstName().equals("Homer"))
                    {
                        homerPresent = true;
                    }
                    else if (m.getFirstName().equals("Bart"))
                    {
                        bartPresent = true;
                    }
                    else if (m.getFirstName().equals("Boss") && m.getLastName().equals("WakesUp"))
                    {
                        bossPresent = true;
                    }
                }
                assertTrue("Homer is not present!", homerPresent);
                assertTrue("Bart is not present!", bartPresent);
                assertTrue("Boss is not present!", bossPresent);
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    public void testContainsInImplicitParameterCollection()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter(":emps.contains(this.manager)");
                Collection c = (Collection) q.execute(emps);
                assertEquals(1,c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(), "Homer");
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }


    public void testContainsInImplicitParameterCollectionOfPCleanInstances()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            bart.setManager(boss2);
            boss.setManager(boss4);
            homer.setManager(bart);

            Collection emps = new HashSet();
            emps.add(bart);
            emps.add(boss);
            emps.add(homer);
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(boss);
                pm.makePersistent(boss2);
                pm.makePersistent(boss4);
                tx.commit();

                tx.begin();
                Query q1 = pm.newQuery(Manager.class);
                Collection coll1 = (Collection)q1.execute();

                Query q2 = pm.newQuery(Manager.class);
                q2.setFilter(":emps.contains(this.manager)");
                Collection coll2 = (Collection) q2.execute(coll1);
                assertEquals(3, coll2.size());
                Iterator iter = coll2.iterator();
                boolean bartPresent = false;
                boolean homerPresent = false;
                boolean bossPresent = false;
                while (iter.hasNext())
                {
                    Manager m = (Manager)iter.next();
                    if (m.getFirstName().equals("Homer"))
                    {
                        homerPresent = true;
                    }
                    else if (m.getFirstName().equals("Bart"))
                    {
                        bartPresent = true;
                    }
                    else if (m.getFirstName().equals("Boss") && m.getLastName().equals("WakesUp"))
                    {
                        bossPresent = true;
                    }
                }
                assertTrue("Homer is not present!", homerPresent);
                assertTrue("Bart is not present!", bartPresent);
                assertTrue("Boss is not present!", bossPresent);
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for Map.get() in the ORDER BY clause.
     */
    public void testMapGetInOrderBy()
    {
        try
        {
            // Create some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                MapHolder holder1 = new MapHolder("First Holder");
                holder1.getJoinMapNonNon().put("Order", "First");
                pm.makePersistent(holder1);
                MapHolder holder2 = new MapHolder("Second Holder");
                holder2.getJoinMapNonNon().put("Order", "Second");
                pm.makePersistent(holder2);
                MapHolder holder3 = new MapHolder("Third Holder");
                holder3.getJoinMapNonNon().put("Order", "Third");
                pm.makePersistent(holder3);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown during test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // ORDER BY Map.get()
                Query q = pm.newQuery("SELECT FROM " + MapHolder.class.getName() + " ORDER BY joinMapNonNon.get(\"order\")");
                List results = (List)q.execute();
                LOG.info("results=" + StringUtils.collectionToString(results));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown during test : " + e.getMessage());
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
            clean(MapHolder.class);
        }
    }

    /**
     * Tests the Map get() method.
     */
    public void testMapGetMethod()
    {
        try
        {
            Object idGym = null;
            Gym gym;
            Gym gym2;
            Wardrobe wardrobe2;
            Wardrobe wardrobe3;
            Wardrobe wardrobe4;
            Cloth whiteShirt;
            Cloth blackShirt;
            Cloth skirt;

            // Create some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                gym = new Gym();
                gym.setLocation("downtown");
                gym2 = new Gym();
                gym2.setLocation("village");
                wardrobe2 = new Wardrobe();
                wardrobe2.setModel("2 doors");
                wardrobe3 = new Wardrobe();
                wardrobe3.setModel("3 doors");
                wardrobe4 = new Wardrobe();
                wardrobe4.setModel("4 doors");
                gym.getWardrobes().put(wardrobe2.getModel(),wardrobe2);
                gym.getWardrobes().put(wardrobe3.getModel(),wardrobe3);
                gym.getWardrobes().put(wardrobe4.getModel(),wardrobe4);
                gym2.getWardrobes().put(wardrobe4.getModel(),wardrobe4);
                pm.makePersistent(gym);
                pm.makePersistent(gym2);
                whiteShirt = new Cloth();
                whiteShirt.setName("white shirt");
                blackShirt = new Cloth();
                blackShirt.setName("black shirt");
                skirt = new Cloth();
                skirt.setName("skirt");
                wardrobe3.getClothes().add(whiteShirt);
                wardrobe3.getClothes().add(skirt);
                wardrobe2.getClothes().add(blackShirt);
                tx.commit();
                idGym = JDOHelper.getObjectId(gym);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                //check map.get -> object expression == object literal
                Query q = pm.newQuery(Gym.class,"this.wardrobes.get(\"2 doors\") == w");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                Collection c = (Collection) q.execute(wardrobe2);
                assertEquals(1,c.size());
                assertEquals(idGym,JDOHelper.getObjectId(c.iterator().next()));
                q = pm.newQuery(Gym.class,"this.wardrobes.get(\"4 doors\") == w");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe4);
                assertEquals(2,c.size());
                q = pm.newQuery(Gym.class,"this.wardrobes.get(\"2 doors\") == w");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe3);
                assertEquals(0,c.size());
                q = pm.newQuery(Gym.class,"this.wardrobes.get(\"5 doors\") == w");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe4);
                assertEquals(0,c.size());

                //check object literal == map.get -> object expression
                q = pm.newQuery(Gym.class,"w == this.wardrobes.get(\"2 doors\")");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe2);
                assertEquals(1,c.size());
                assertEquals(idGym,JDOHelper.getObjectId(c.iterator().next()));
                q = pm.newQuery(Gym.class,"w == this.wardrobes.get(\"4 doors\")");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe4);
                assertEquals(2,c.size());
                q = pm.newQuery(Gym.class,"w == this.wardrobes.get(\"2 doors\")");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe3);
                assertEquals(0,c.size());
                q = pm.newQuery(Gym.class,"w == this.wardrobes.get(\"5 doors\")");
                q.declareParameters("org.jpox.samples.models.fitness.Wardrobe w");
                c = (Collection) q.execute(wardrobe4);
                assertEquals(0,c.size());
                
                //test map.get in map literals
                Map map1 = new HashMap();
                map1.put("2 doors",wardrobe2);
                
                q = pm.newQuery(Wardrobe.class,"this == map.get(\"2 doors\")");
                q.declareParameters("java.util.Map map");
                c = (Collection) q.execute(map1);
                assertEquals(1,c.size());

                Map map2 = new HashMap();
                
                q = pm.newQuery(Wardrobe.class,"this == map.get(\"2 doors\")");
                q.declareParameters("java.util.Map map");
                c = (Collection) q.execute(map2);
                assertEquals(0,c.size());

                Map map3 = new HashMap();
                map3.put("2 doors",wardrobe2);
                map3.put("3 doors",wardrobe3);
                
                q = pm.newQuery(Wardrobe.class,"this == map.get(\"4 doors\")");
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
            }
        }
        finally
        {
            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * Test for the Collection.size() method.
     */
    public void testCollectionSize()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee lisa = new Employee(3,"Lisa","Simpson","lisa@simpson.com",4,"serial 3");
            Manager homersBrother = new Manager(4,"Homer Jr","Simpson","homerjr@simpson.com",1,"serial 4");
            Employee lisasSister = new Employee(5,"Lisa Sr","Simpson","lisasr@simpson.com",4,"serial 5");
            bart.setManager(homer);
            lisa.setManager(homer);
            homer.addSubordinate(lisa);
            homer.addSubordinate(bart);
            lisasSister.setManager(homersBrother);
            homersBrother.addSubordinate(lisasSister);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(lisa);
                pm.makePersistent(homersBrother);
                pm.makePersistent(lisasSister);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("subordinates.size() == 2");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(), "Homer");
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown while executing query with collection.size()");
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the Collection.isEmpty() method.
     */
    public void testCollectionIsEmpty()
    {
        try
        {
            Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
            Employee bart = new Employee(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Employee lisa = new Employee(3,"Lisa","Simpson","lisa@simpson.com",4,"serial 3");
            Manager homersBrother = new Manager(4,"Homer Jr","Simpson","homerjr@simpson.com",1,"serial 4");
            bart.setManager(homer);
            lisa.setManager(homer);
            homer.addSubordinate(lisa);
            homer.addSubordinate(bart);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                pm.makePersistent(homer);
                pm.makePersistent(lisa);
                pm.makePersistent(homersBrother);
                tx.commit();

                // Try isEmpty() - should just return "homersBrother" since has no subordinates
                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("subordinates.isEmpty()");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(), "Homer Jr");
                tx.commit();

                // Try !isEmpty() - should just return "homer" since has subordinates
                tx.begin();
                q = pm.newQuery(Manager.class);
                q.setFilter("!subordinates.isEmpty()");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Manager)c.iterator().next()).getFirstName(), "Homer");
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown while executing query with collection.isEmpty()");
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the Map.size() method.
     */
    public void testMapSize()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setLocation("First floor");
            Wardrobe w1 = new Wardrobe();
            Wardrobe w2 = new Wardrobe();
            w1.setModel("2 door");
            w2.setModel("3 door");
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            Gym gym2 = new Gym();
            gym2.setLocation("Second floor");
            Wardrobe w3 = new Wardrobe();
            w3.setModel("1 door");
            gym2.getWardrobes().put(w3.getModel(), w3);

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
                q.setFilter("wardrobes.size() > 1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Gym)c.iterator().next()).getLocation(), "First floor");
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown while executing query with map.size()");
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
     * Test for the Map.isEmpty() method.
     */
    public void testMapIsEmpty()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setLocation("First floor");
            Wardrobe w1 = new Wardrobe();
            Wardrobe w2 = new Wardrobe();
            w1.setModel("2 door");
            w2.setModel("3 door");
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            Gym gym2 = new Gym();
            gym2.setLocation("Second floor");

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(gym1);
                pm.makePersistent(gym2);
                tx.commit();

                // Try isEmpty() - should just return "Second floor"
                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.isEmpty()");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Gym)c.iterator().next()).getLocation(), "Second floor");
                tx.commit();

                // Try !isEmpty() - should return "First floor"
                tx.begin();
                q = pm.newQuery(Gym.class);
                q.setFilter("!wardrobes.isEmpty()");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals(((Gym)c.iterator().next()).getLocation(), "First floor");
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during test", e);
                fail("Exception thrown while executing query with map.isEmpty()");
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
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariable()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        boss4.addSubordinate(bart);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && emp1.lastName == \"WakesUp\"");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp2\")");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(2, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(db) && e.departments.contains(db) && db.name =='DeptB'");
            q.declareVariables("Department db; Manager e");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
                      
            
            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(e) && (e.departments.contains(db) && db.name =='DeptB')");
            q.declareVariables("Department db; Manager e");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
            assertEquals("WakesUp4",((Manager)c.iterator().next()).getLastName());
                      
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceAndEqualsOnOneUnboundVariable()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        boss4.addSubordinate(bart);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Department.class);
            q.setFilter("m1.departments.contains(this) && m1.firstName == \"Bart\" && m1.lastName == \"Simpson\"");
            q.setResult("distinct this");
            q.declareVariables("Manager m1");
            q.declareImports("import org.jpox.samples.models.company.Manager");
            Collection c = (Collection) q.execute();
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

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    /**
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariable()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("wardrobes.containsValue(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipments.containsValue(e) && g.equipments.containsValue(e) && e.name =='Yoga'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipments.containsValue(e) && g.equipments.containsValue(e) && e.name =='Pilates'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Shopping",((Gym)c.iterator().next()).getName());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("partners.containsValue(g) && (g.equipments.containsValue(e) && e.name =='Pilates')");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());
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
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueAndEqualsOnceOnOneUnboundVariable()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("g.partners.containsValue(this) && g.name == 'Cinema'");
                q.declareVariables("Gym g");
                q.setResult("distinct this");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("Shopping",((Gym)c.iterator().next()).getName());
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
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariableInverse()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setName("Cinema");
            gym1.setLocation("First floor");
            Wardrobe w1 = new Wardrobe();
            Wardrobe w2 = new Wardrobe();
            w1.setModel("2 door");
            w2.setModel("3 door");
            w1.setGym(gym1);
            w2.setGym(gym1);
            gym1.getWardrobesInverse().put(w1.getModel(), w1);
            gym1.getWardrobesInverse().put(w2.getModel(), w2);
            GymEquipment ge1 = new GymEquipment();
            ge1.setName("Weight");
            GymEquipment ge2 = new GymEquipment();
            ge2.setName("Yoga");
            ge1.setGym(gym1);
            ge2.setGym(gym1);
            gym1.getEquipmentsInverse().put(ge1.getName(), ge1);
            gym1.getEquipmentsInverse().put(ge2.getName(), ge2);

            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            Wardrobe w3 = new Wardrobe();
            Wardrobe w4 = new Wardrobe();
            w3.setModel("4 door");
            w4.setModel("5 door");
            w3.setGym(gym2);
            w4.setGym(gym2);
            gym2.getWardrobesInverse().put(w3.getModel(), w3);
            gym2.getWardrobesInverse().put(w4.getModel(), w4);
            GymEquipment ge3 = new GymEquipment();
            ge3.setName("Pilates");
            GymEquipment ge4 = new GymEquipment();
            ge4.setName("Abdominal");
            ge3.setGym(gym2);
            ge4.setGym(gym2);
            gym2.getEquipmentsInverse().put(ge3.getName(), ge3);
            gym2.getEquipmentsInverse().put(ge4.getName(), ge4);

            gym2.setGym(gym1);
            gym1.getPartnersInverse().put(gym2.getName(), gym2);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(gym1);
                pm.makePersistent(gym2);
                pm.flush();
                assertEquals(2,gym1.getWardrobesInverse().size());
                assertEquals(2,gym1.getEquipmentsInverse().size());
                tx.commit();

                tx.begin();
                Collection c = (Collection)pm.newQuery(Gym.class).execute();
                assertEquals(2,c.size());
                Gym g = (Gym)c.iterator().next();
                assertEquals(2,g.getWardrobesInverse().size());
                assertEquals(2,g.getEquipmentsInverse().size());
                g = (Gym)c.iterator().next();
                assertEquals(2,g.getWardrobesInverse().size());
                assertEquals(2,g.getEquipmentsInverse().size());
                tx.commit();                

                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipmentsInverse.containsValue(e) && g.equipmentsInverse.containsValue(e) && e.name =='Yoga'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipmentsInverse.containsValue(e) && g.equipmentsInverse.containsValue(e) && e.name =='Pilates'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Shopping",((Gym)c.iterator().next()).getName());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("partnersInverse.containsValue(g) && (g.equipmentsInverse.containsValue(e) && e.name =='Pilates')");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());
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
     * Test for the Map.containsKey(Character) method.
     */
    public void testQueryContainsKeyOnCharacter()
    {
        try
        {
            Gym gym1 = new Gym();
            gym1.setName("Cinema");
            gym1.setLocation("First floor");
            gym1.getCodes().put(new Character('a'), new String("aaaaa"));
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(gym1);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("codes.containsKey('a')");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("codes.containsKey('b')");
                c = (Collection) q.execute();
                assertEquals(0, c.size());

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
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariableInverse()
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
                pm.flush();
                assertEquals(2,gym1.getWardrobesInverse2().size());
                assertEquals(2,gym1.getEquipmentsInverse2().size());
                tx.commit();

                tx.begin();
                Collection c = (Collection)pm.newQuery(Gym.class).execute();
                assertEquals(2,c.size());
                Gym g = (Gym)c.iterator().next();
                assertEquals(2,g.getWardrobesInverse2().size());
                assertEquals(2,g.getEquipmentsInverse2().size());
                g = (Gym)c.iterator().next();
                assertEquals(2,g.getWardrobesInverse2().size());
                assertEquals(2,g.getEquipmentsInverse2().size());
                tx.commit();                

                tx.begin();
                Query q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipmentsInverse2.containsKey(e) && g.equipmentsInverse2.containsKey(e) && e.name =='Yoga'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipmentsInverse2.containsKey(e) && g.equipmentsInverse2.containsKey(e) && e.name =='Pilates'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Shopping",((Gym)c.iterator().next()).getName());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("partnersInverse2.containsKey(g) && (g.equipmentsInverse2.containsKey(e) && e.name =='Pilates')");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());
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
     * Test for the Map.containsKey() method.
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariable()
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
            gym1.getWardrobes2().put(w1, w1.getModel());
            gym1.getWardrobes2().put(w2, w2.getModel());
            gym1.getWardrobes2().put(w3, w3.getModel());
            gym1.getWardrobes2().put(w4, w4.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes2().put(w1, w1.getModel());
            gym2.getWardrobes2().put(w2, w2.getModel());
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
            gym1.getEquipments2().put(ge1, ge1.getName());
            gym1.getEquipments2().put(ge2, ge2.getName());
            gym2.getEquipments2().put(ge3, ge3.getName());
            gym2.getEquipments2().put(ge4, ge4.getName());
            gym1.getPartners2().put(gym2, gym2.getName());
            
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
                q.setFilter("wardrobes2.containsKey(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipments2.containsKey(e) && g.equipments2.containsKey(e) && e.name =='Yoga'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());

                q = pm.newQuery(Gym.class);
                q.setFilter("equipments2.containsKey(e) && g.equipments2.containsKey(e) && e.name =='Pilates'");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size()); 
                assertEquals("Shopping",((Gym)c.iterator().next()).getName());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("partners2.containsKey(g) && (g.equipments2.containsKey(e) && e.name =='Pilates')");
                q.declareVariables("GymEquipment e; Gym g");
                c = (Collection) q.execute();
                assertEquals(1, c.size());
                assertEquals("Cinema",((Gym)c.iterator().next()).getName());
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
     * test query with "field.contains(x)" using a workaround
     * Totally stupid query ("param1.field.contains(param2)") wtf.
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableUsingParameters()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Department.class);
            q.setFilter("name == 'DeptB'");
            q.setUnique(true);
            Department db = (Department) q.execute();

            q = pm.newQuery(Manager.class);
            q.setFilter("firstName == 'Bart'");
            q.setUnique(true);
            Manager e = (Manager) q.execute();
            e.getDepartments(); // ensure that field is loaded, because the query will use it

            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(db) && e.departments.contains(db)");
            q.declareParameters("Department db, Employee e");
            Collection c = (Collection) q.execute(db, e);
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

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.containsValue(x)" using a workaround
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariableUsingParameters()
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
        gym1.getWardrobes().put(w1.getModel(), w1);
        gym1.getWardrobes().put(w2.getModel(), w2);
        gym1.getWardrobes().put(w3.getModel(), w3);
        gym1.getWardrobes().put(w4.getModel(), w4);
        Gym gym2 = new Gym();
        gym2.setName("Shopping");
        gym2.setLocation("Second floor");
        gym2.getWardrobes().put(w1.getModel(), w1);
        gym2.getWardrobes().put(w2.getModel(), w2);
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
        gym1.getEquipments().put(ge1.getName(), ge1);
        gym1.getEquipments().put(ge2.getName(), ge2);
        gym2.getEquipments().put(ge3.getName(), ge3);
        gym2.getEquipments().put(ge4.getName(), ge4);
        gym1.getPartners().put(gym2.getName(), gym2);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(gym1);
            pm.makePersistent(gym2);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(GymEquipment.class);
            q.setFilter("name == 'Pilates'");
            q.setUnique(true);
            GymEquipment ge = (GymEquipment) q.execute();            
            q = pm.newQuery(Gym.class);
            q.setFilter("name == 'Shopping'");
            q.setUnique(true);
            Gym g = (Gym) q.execute();
            g.getEquipments();//MAKE sure field is loaded, becase the query will use it 
            q = pm.newQuery(Gym.class);
            q.setFilter("equipments.containsValue(ge) && g.equipments.containsValue(ge)");
            q.declareParameters("GymEquipment ge, Gym g");
            Collection c = (Collection) q.execute(ge, g);
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

            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * test query with "field.containsValue(x)" using a workaround
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariableUsingParametersInverse()
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
            Query q = pm.newQuery(GymEquipment.class);
            q.setFilter("name == 'Pilates'");
            q.setUnique(true);
            GymEquipment ge = (GymEquipment) q.execute();            
            q = pm.newQuery(Gym.class);
            q.setFilter("name == 'Shopping'");
            q.setUnique(true);
            Gym g = (Gym) q.execute();
            g.getEquipmentsInverse();//MAKE sure field is loaded, becase the query will use it 
            q = pm.newQuery(Gym.class);
            q.setFilter("equipmentsInverse.containsValue(ge) && g.equipmentsInverse.containsValue(ge)");
            q.declareParameters("GymEquipment ge, Gym g");
            Collection c = (Collection) q.execute(ge, g);
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

            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }
    
    /**
     * test query with "field.containsKey(x)" using a workaround
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariableUsingParametersInverse()
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
            Query q = pm.newQuery(GymEquipment.class);
            q.setFilter("name == 'Pilates'");
            q.setUnique(true);
            GymEquipment ge = (GymEquipment) q.execute();            
            q = pm.newQuery(Gym.class);
            q.setFilter("name == 'Shopping'");
            q.setUnique(true);
            Gym g = (Gym) q.execute();
            g.getEquipmentsInverse2();//MAKE sure field is loaded, becase the query will use it 
            q = pm.newQuery(Gym.class);
            q.setFilter("equipmentsInverse2.containsKey(ge) && g.equipmentsInverse2.containsKey(ge)");
            q.declareParameters("GymEquipment ge, Gym g");
            Collection c = (Collection) q.execute(ge, g);
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

            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }
    
    /**
     * test query with "field.containsKey(x)" using a workaround
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariableUsingParameters()
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
        gym1.getWardrobes2().put(w1, w1.getModel());
        gym1.getWardrobes2().put(w2, w2.getModel());
        gym1.getWardrobes2().put(w3, w3.getModel());
        gym1.getWardrobes2().put(w4, w4.getModel());
        Gym gym2 = new Gym();
        gym2.setName("Shopping");
        gym2.setLocation("Second floor");
        gym2.getWardrobes2().put(w1, w1.getModel());
        gym2.getWardrobes2().put(w2, w2.getModel());
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
        gym1.getEquipments2().put(ge1, ge1.getName());
        gym1.getEquipments2().put(ge2, ge2.getName());
        gym2.getEquipments2().put(ge3, ge3.getName());
        gym2.getEquipments2().put(ge4, ge4.getName());
        gym1.getPartners2().put(gym2, gym2.getName());
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(gym1);
            pm.makePersistent(gym2);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(GymEquipment.class);
            q.setFilter("name == 'Pilates'");
            q.setUnique(true);
            GymEquipment ge = (GymEquipment) q.execute();            
            q = pm.newQuery(Gym.class);
            q.setFilter("name == 'Shopping'");
            q.setUnique(true);
            Gym g = (Gym) q.execute();
            g.getEquipments2();//MAKE sure field is loaded, becase the query will use it 
            q = pm.newQuery(Gym.class);
            q.setFilter("equipments2.containsKey(ge) && g.equipments2.containsKey(ge)");
            q.declareParameters("GymEquipment ge, Gym g");
            Collection c = (Collection) q.execute(ge, g);
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

            // Clean out our data
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableImplicitVariables()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        boss4.addSubordinate(bart);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && emp1.lastName == \"WakesUp\"");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp2\")");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")");
            c = (Collection) q.execute();
            assertEquals(2, c.size());
           
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(db) && e.departments.contains(db) && db.name =='DeptB'");
            q.declareVariables("Department db; Manager e");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(e) && (e.departments.contains(db) && db.name =='DeptB')");
            q.declareVariables("Department db; Manager e");
            c = (Collection) q.execute();
            assertEquals(1, c.size());
            assertEquals("WakesUp4",((Manager)c.iterator().next()).getLastName());

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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariableImplicitVariables()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("wardrobes.containsValue(w1) && w1.model == \"4 door\"");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                try
                {
                    q = pm.newQuery(Gym.class);
                    q.setFilter("equipments.containsValue(e) && g.equipments.containsValue(e) && e.name =='Yoga'");
                    c = (Collection) q.execute();
                    fail("expected JDOUserException");
                }
                catch(JDOUserException e)
                {
                    //expected
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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsValueOnceOnOneUnboundVariableImplicitVariablesInverse()
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
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobesInverse().put(w3.getModel(), w3);
            gym2.getWardrobesInverse().put(w4.getModel(), w4);
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
                q.setFilter("wardrobesInverse.containsValue(w1) && w1.model == \"4 door\"");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"4 door\")");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                try
                {
                    q = pm.newQuery(Gym.class);
                    q.setFilter("equipmentsInverse.containsValue(e) && g.equipmentsInverse.containsValue(e) && e.name =='Yoga'");
                    c = (Collection) q.execute();
                    fail("expected JDOUserException");
                }
                catch(JDOUserException e)
                {
                    //expected
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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * Test for the Map.containsValue() method.
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariableImplicitVariablesInverse()
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
                q.setFilter("wardrobesInverse2.containsKey(w1) && w1.model == \"4 door\"");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"4 door\")");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                try
                {
                    q = pm.newQuery(Gym.class);
                    q.setFilter("equipmentsInverse2.containsKey(e) && g.equipmentsInverse2.containsKey(e) && e.name =='Yoga'");
                    c = (Collection) q.execute();
                    fail("expected JDOUserException");
                }
                catch(JDOUserException e)
                {
                    //expected
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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }

    /**
     * Test for the Map.containsKey() method.
     */
    public void testQueryUsesContainsKeyOnceOnOneUnboundVariableImplicitVariables()
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
            gym1.getWardrobes2().put(w1, w1.getModel());
            gym1.getWardrobes2().put(w2, w2.getModel());
            gym1.getWardrobes2().put(w3, w3.getModel());
            gym1.getWardrobes2().put(w4, w4.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes2().put(w1, w1.getModel());
            gym2.getWardrobes2().put(w2, w2.getModel());
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
            gym1.getEquipments2().put(ge1, ge1.getName());
            gym1.getEquipments2().put(ge2, ge2.getName());
            gym2.getEquipments2().put(ge3, ge3.getName());
            gym2.getEquipments2().put(ge4, ge4.getName());
            gym1.getPartners2().put(gym2, gym2.getName());
            
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
                q.setFilter("wardrobes2.containsKey(w1) && w1.model == \"4 door\"");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                try
                {
                    q = pm.newQuery(Gym.class);
                    q.setFilter("equipments2.containsKey(e) && g.equipments2.containsKey(e) && e.name =='Yoga'");
                    c = (Collection) q.execute();
                    fail("expected JDOUserException");
                }
                catch(JDOUserException e)
                {
                    //expected
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
            FitnessHelper.cleanFitnessData(pmf);
        }
    }
    
    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableInverse()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        Department deptC = new Department("DeptC");
        Department deptD = new Department("DeptD");
        Department deptE = new Department("DeptE");
        bart.addDepartment(deptB);     
        bart.addDepartment(deptA);
        homer.addDepartment(deptC);
        boss.addDepartment(deptD);
        boss.addDepartment(deptE);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && dept1.name == \"DeptB\"");
            q.declareVariables("Department dept1");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && (dept1.name == \"DeptB\" || dept1.name == \"DeptC\")");
            q.declareVariables("Department dept1");
            c = (Collection) q.execute();
            assertEquals(2, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && (dept1.name == \"DeptB\" || dept1.name == \"DeptA\")");
            q.declareVariables("Department dept1");
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

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableInverseImplicitVariables()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        Department deptC = new Department("DeptC");
        Department deptD = new Department("DeptD");
        Department deptE = new Department("DeptE");
        bart.addDepartment(deptB);     
        bart.addDepartment(deptA);
        homer.addDepartment(deptC);
        boss.addDepartment(deptD);
        boss.addDepartment(deptE);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && dept1.name == \"DeptB\"");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && (dept1.name == \"DeptB\" || dept1.name == \"DeptC\")");
            c = (Collection) q.execute();
            assertEquals(2, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && (dept1.name == \"DeptB\" || dept1.name == \"DeptA\")");
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

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableInverseUsingParameter()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        Department deptC = new Department("DeptC");
        Department deptD = new Department("DeptD");
        Department deptE = new Department("DeptE");
        bart.addDepartment(deptB);     
        bart.addDepartment(deptA);
        homer.addDepartment(deptC);
        boss.addDepartment(deptD);
        boss.addDepartment(deptE);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            tx.commit();

            tx.begin();
            
            Query q = pm.newQuery(Department.class);
            q.setFilter("name == 'DeptA'");
            q.setUnique(true);
            Department da = (Department) q.execute();
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(deptA)");
            q.declareParameters("Department deptA");
            Collection c = (Collection) q.execute(da);
            assertEquals(1, c.size());            

            q = pm.newQuery(Department.class);
            q.setFilter("name == 'DeptC'");
            q.setUnique(true);
            Department dc = (Department) q.execute();
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(deptA) || departments.contains(deptC) ");
            q.declareParameters("Department deptA, Department deptC");
            c = (Collection) q.execute(da,dc);
            assertEquals(2, c.size());  
            
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(deptA) && deptA.manager == this");
            q.declareParameters("Department deptA");
            c = (Collection) q.execute(da);
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

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsOnceOnOneUnboundVariableMtoN()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            PetroleumCustomer customer1 = new PetroleumCustomer("C1");
            PetroleumCustomer customer2 = new PetroleumCustomer("C2");
            PetroleumCustomer customer3 = new PetroleumCustomer("C3");
            PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
            PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
            PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
            PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
            PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
            customer1.addSupplier(supplier1);
            customer1.addSupplier(supplier2);
            customer2.addSupplier(supplier1);
            customer2.addSupplier(supplier3);
            customer2.addSupplier(supplier5);
            customer3.addSupplier(supplier1);
            customer3.addSupplier(supplier2);
            customer3.addSupplier(supplier3);
            customer3.addSupplier(supplier4);
            pm.makePersistent(customer1);
            pm.makePersistent(customer2);
            pm.makePersistent(customer3);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && sup1.name == \"S1\"");
            q.declareVariables("PetroleumSupplier sup1");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());
            
            q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && (sup1.name == \"S3\" || sup1.name == \"S4\")");
            q.declareVariables("PetroleumSupplier sup1");
            c = (Collection) q.execute();
            assertEquals(2, c.size());

            q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && (sup1.name == \"S6\" || sup1.name == \"S5\")");
            q.declareVariables("PetroleumSupplier sup1");
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

            // Clean out our data
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }
    
    
    /**
     * test query with "field.contains(x) && field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsTwiceOnOneUnboundVariable()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();            
            Query q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp2\")");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(2, c.size());
           
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for the Map.containsValue(x) && Map.containsValue(x) method.
     */
    public void testQueryUsesContainsValueTwiceOnOneUnboundVariable()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("wardrobes.containsValue(w1) && wardrobes.containsValue(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && wardrobes.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && wardrobes.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
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
     * Test for the Map.containsValue(x) && Map.containsValue(x) method.
     */
    public void testQueryUsesContainsValueTwiceOnOneUnboundVariableInverse()
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
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobesInverse().put(w3.getModel(), w3);
            gym2.getWardrobesInverse().put(w4.getModel(), w4);
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
                q.setFilter("wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w1) && (w1.model == \"2 door\" || w1.model == \"4 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobesInverse.containsValue(w1) && wardrobesInverse.containsValue(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")) " +
                            "&& (wardrobesInverse.containsValue(w2) && (w2.model == \"4 door\" || w2.model == \"5 door\"))");
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
     * Test for the Map.containsKey(x) && Map.containsKey(x) method.
     */
    public void testQueryUsesContainsKeyTwiceOnOneUnboundVariableInverse()
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
                q.setFilter("wardrobesInverse2.containsKey(w1) && wardrobesInverse2.containsKey(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && wardrobesInverse2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"4 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobesInverse2.containsKey(w1) && wardrobesInverse2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
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
     * Test for the containsKey(x) && containsKey(x) method.
     */
    public void testQueryUsesContainsKeyTwiceOnOneUnboundVariable()
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
            gym1.getWardrobes2().put(w1, w1.getModel());
            gym1.getWardrobes2().put(w2, w2.getModel());
            gym1.getWardrobes2().put(w3, w3.getModel());
            gym1.getWardrobes2().put(w4, w4.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes2().put(w1, w1.getModel());
            gym2.getWardrobes2().put(w2, w2.getModel());
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
            gym1.getEquipments2().put(ge1, ge1.getName());
            gym1.getEquipments2().put(ge2, ge2.getName());
            gym2.getEquipments2().put(ge3, ge3.getName());
            gym2.getEquipments2().put(ge4, ge4.getName());
            gym1.getPartners2().put(gym2, gym2.getName());
            
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
                q.setFilter("wardrobes2.containsKey(w1) && wardrobes2.containsKey(w1) && w1.model == \"4 door\"");
                q.declareVariables("Wardrobe w1");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && wardrobes2.containsKey(w1) && (w1.model == \"2 door\" || w1.model == \"3 door\")");
                q.declareVariables("Wardrobe w1");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && wardrobes2.containsKey(w1) && (w1.model == \"4 door\" || w1.model == \"5 door\")");
                q.declareVariables("Wardrobe w1");
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
     * test query with "field.contains(x) && field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsTwiceOnOneUnboundVariableInverse()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        Department deptC = new Department("DeptC");
        Department deptD = new Department("DeptD");
        Department deptE = new Department("DeptE");
        bart.addDepartment(deptB);     
        bart.addDepartment(deptA);
        homer.addDepartment(deptC);
        boss.addDepartment(deptD);
        boss.addDepartment(deptE);
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            tx.commit();

            tx.begin();   
            
            Query q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && departments.contains(dept1) && (dept1.name == \"DeptA\" || dept1.name == \"DeptB\")");
            q.declareVariables("Department dept1");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && departments.contains(dept1) && (dept1.name == \"DeptA\" || dept1.name == \"DeptD\")");
            q.declareVariables("Department dept1");
            c = (Collection) q.execute();
            assertEquals(2, c.size());
           
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.contains(x) && field.contains(x)" using a workaround
     */
    public void testQueryUsesContainsTwiceOnOneUnboundVariableMtoN()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            PetroleumCustomer customer1 = new PetroleumCustomer("C1");
            PetroleumCustomer customer2 = new PetroleumCustomer("C2");
            PetroleumCustomer customer3 = new PetroleumCustomer("C3");
            PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
            PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
            PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
            PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
            PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
            customer1.addSupplier(supplier1);
            customer1.addSupplier(supplier2);
            customer2.addSupplier(supplier1);
            customer2.addSupplier(supplier3);
            customer2.addSupplier(supplier5);
            customer3.addSupplier(supplier1);
            customer3.addSupplier(supplier2);
            customer3.addSupplier(supplier3);
            customer3.addSupplier(supplier4);
            pm.makePersistent(customer1);
            pm.makePersistent(customer2);
            pm.makePersistent(customer3);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && suppliers.contains(sup1) && (sup1.name == \"S3\" || sup1.name == \"S4\")");
            q.declareVariables("PetroleumSupplier sup1");
            Collection c = (Collection) q.execute();
            assertEquals(2, c.size());

            q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && suppliers.contains(sup1) && (sup1.name == \"S6\" || sup1.name == \"S5\")");
            q.declareVariables("PetroleumSupplier sup1");
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

            // Clean out our data
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }

    /**
     * test query with "field.contains(x) && field.contains(y)" using "or" 
     * workaround. Use the workaround to bypass a deficiency on query generation
     */
    public void testQueryUsesContainsTwiceOnFieldUsingWorkaround()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);     
        
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
	        tx.begin();
	        pm.makePersistent(bart);
	        pm.makePersistent(homer);
	        pm.makePersistent(boss);
	        pm.makePersistent(boss2);
	        pm.makePersistent(boss4);
	        pm.makePersistent(deptA);
	        pm.makePersistent(deptB);
	        tx.commit();
	        tx.begin();
	        Query q = pm.newQuery(Manager.class);
	        q.setFilter("subordinates.contains(emp1) && subordinates.contains(emp2) && emp1.lastName == \"WakesUp\" || emp2.lastName == \"WakesUp2\"");
	        q.declareVariables("Employee emp1; Employee emp2");
	        q.declareImports("import org.jpox.samples.models.company.Employee");
	        Collection c = (Collection) q.execute();
	        assertEquals(1,c.size());
            
            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")");
            q.declareVariables("Employee emp1");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(2, c.size());            
	        
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
            CompanyHelper.clearCompanyData(pmf);
	    }
    }

    /**
     * test query with "field.containsValue(x) && field.containsValue(y)" using "or" 
     * workaround. Use the workaround to bypass a deficiency on query generation
     **/
    public void testQueryUsesContainsValueTwiceOnFieldUsingWorkaround()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("wardrobes.containsValue(w1) && wardrobes.containsValue(w2) && (w1.model == \"2 door\" || w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes.containsValue(w1) && wardrobes.containsValue(w2) && (w1.model == \"4 door\" || w2.model == \"5 door\")");
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
    public void testQueryUsesContainsKeyTwiceOnFieldUsingWorkaround()
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
            gym1.getWardrobes2().put(w1, w1.getModel());
            gym1.getWardrobes2().put(w2, w2.getModel());
            gym1.getWardrobes2().put(w3, w3.getModel());
            gym1.getWardrobes2().put(w4, w4.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes2().put(w1, w1.getModel());
            gym2.getWardrobes2().put(w2, w2.getModel());
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
            gym1.getEquipments2().put(ge1, ge1.getName());
            gym1.getEquipments2().put(ge2, ge2.getName());
            gym2.getEquipments2().put(ge3, ge3.getName());
            gym2.getEquipments2().put(ge4, ge4.getName());
            gym1.getPartners2().put(gym2, gym2.getName());
            
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
                q.setFilter("wardrobes2.containsKey(w1) && wardrobes2.containsKey(w2) && (w1.model == \"2 door\" || w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("wardrobes2.containsKey(w1) && wardrobes2.containsKey(w2) && (w1.model == \"4 door\" || w2.model == \"5 door\")");
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
     * test query with "field.contains(x) && field.contains(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsTwiceOnFieldWithNamespace()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        boss.addSubordinate(boss2);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("(subordinates.contains(emp1) && emp1.lastName == \"WakesUp\") && (subordinates.contains(emp2) && emp2.lastName == \"WakesUp2\")");
            q.declareVariables("Employee emp1; Employee emp2");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();
            
            tx.begin();
            q = pm.newQuery(Manager.class);
            q.setFilter("(subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")) && (subordinates.contains(emp2) && emp2.lastName == \"WakesUp2\")");
            q.declareVariables("Employee emp1; Employee emp2");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();            

            tx.begin();
            q = pm.newQuery(Manager.class);
            q.setFilter("(subordinates.contains(emp1) && (emp1.lastName == \"WakesUp\" || emp1.lastName == \"WakesUp4\")) || (subordinates.contains(emp2) && emp2.lastName == \"WakesUp2\")");
            q.declareVariables("Employee emp1; Employee emp2");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(3, c.size());

            tx.commit();            
        
        }
        finally
        {
            if (tx.isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.containsValue(x) && field.containsValue(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsValueTwiceOnFieldWithNamespace()
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
            gym1.getWardrobes().put(w1.getModel(), w1);
            gym1.getWardrobes().put(w2.getModel(), w2);
            gym1.getWardrobes().put(w3.getModel(), w3);
            gym1.getWardrobes().put(w4.getModel(), w4);
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes().put(w1.getModel(), w1);
            gym2.getWardrobes().put(w2.getModel(), w2);
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
            gym1.getEquipments().put(ge1.getName(), ge1);
            gym1.getEquipments().put(ge2.getName(), ge2);
            gym2.getEquipments().put(ge3.getName(), ge3);
            gym2.getEquipments().put(ge4.getName(), ge4);
            gym1.getPartners().put(gym2.getName(), gym2);
            
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
                q.setFilter("(wardrobes.containsValue(w1) && w1.model == \"4 door\") && (wardrobes.containsValue(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobes.containsValue(w1) && w1.model == \"2 door\") && (wardrobes.containsValue(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobes.containsValue(w1) && w1.model == \"2 door\") || (wardrobes.containsValue(w2) && w2.model == \"5 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

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
     * test query with "field.containsValue(x) && field.containsValue(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsValueTwiceOnFieldWithNamespaceInverse()
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
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobesInverse().put(w3.getModel(), w3);
            gym2.getWardrobesInverse().put(w4.getModel(), w4);
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
                q.setFilter("(wardrobesInverse.containsValue(w1) && w1.model == \"4 door\") && (wardrobesInverse.containsValue(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(0, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobesInverse.containsValue(w1) && w1.model == \"2 door\") && (wardrobesInverse.containsValue(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobesInverse.containsValue(w1) && w1.model == \"4 door\") || (wardrobesInverse.containsValue(w2) && w2.model == \"5 door\")");
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
     * test query with "field.containsKey(x) && field.containsKey(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsKeyTwiceOnFieldWithNamespaceInverse()
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
                q.setFilter("(wardrobesInverse2.containsKey(w1) && w1.model == \"4 door\") && (wardrobesInverse2.containsKey(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(0, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobesInverse2.containsKey(w1) && w1.model == \"2 door\") && (wardrobesInverse2.containsKey(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(1, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobesInverse2.containsKey(w1) && w1.model == \"4 door\") || (wardrobesInverse2.containsKey(w2) && w2.model == \"5 door\")");
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
     * test query with "field.containsKey(x) && field.containsKey(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsKeyTwiceOnFieldWithNamespace()
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
            gym1.getWardrobes2().put(w1, w1.getModel());
            gym1.getWardrobes2().put(w2, w2.getModel());
            gym1.getWardrobes2().put(w3, w3.getModel());
            gym1.getWardrobes2().put(w4, w4.getModel());
            Gym gym2 = new Gym();
            gym2.setName("Shopping");
            gym2.setLocation("Second floor");
            gym2.getWardrobes2().put(w1, w1.getModel());
            gym2.getWardrobes2().put(w2, w2.getModel());
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
            gym1.getEquipments2().put(ge1, ge1.getName());
            gym1.getEquipments2().put(ge2, ge2.getName());
            gym2.getEquipments2().put(ge3, ge3.getName());
            gym2.getEquipments2().put(ge4, ge4.getName());
            gym1.getPartners2().put(gym2, gym2.getName());
            
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
                q.setFilter("(wardrobes2.containsKey(w1) && w1.model == \"4 door\") && (wardrobes2.containsKey(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                Collection c = (Collection) q.execute();
                assertEquals(1, c.size());
                
                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobes2.containsKey(w1) && w1.model == \"2 door\") && (wardrobes2.containsKey(w2) && w2.model == \"3 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

                q = pm.newQuery(Gym.class);
                q.setFilter("(wardrobes2.containsKey(w1) && w1.model == \"2 door\") || (wardrobes2.containsKey(w2) && w2.model == \"5 door\")");
                q.declareVariables("Wardrobe w1; Wardrobe w2");
                c = (Collection) q.execute();
                assertEquals(2, c.size());

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
     * test query with "field.contains(x) && field.contains(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsTwiceOnFieldWithNamespaceInverse()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        Department deptC = new Department("DeptC");
        Department deptD = new Department("DeptD");
        Department deptE = new Department("DeptE");
        bart.addDepartment(deptB);     
        bart.addDepartment(deptA);
        homer.addDepartment(deptC);
        boss.addDepartment(deptD);
        boss.addDepartment(deptE);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("(departments.contains(dept1) && dept1.name == \"DeptA\") && (departments.contains(dept2) && dept2.name == \"DeptB\")");
            q.declareVariables("Department dept1; Department dept2");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();
            
            tx.begin();
            q = pm.newQuery(Manager.class);
            q.setFilter("(departments.contains(dept1) && (dept1.name == \"DeptA\" || dept1.name == \"DeptC\")) && (departments.contains(dept2) && dept2.name == \"DeptB\")");
            q.declareVariables("Department dept1; Department dept2");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();            

            tx.begin();
            q = pm.newQuery(Manager.class);
            q.setFilter("(departments.contains(dept1) && (dept1.name == \"DeptA\" || dept1.name == \"DeptC\")) || (departments.contains(dept2) && dept2.name == \"DeptD\")");
            q.declareVariables("Department dept1; Department dept2");
            c = (Collection) q.execute();
            assertEquals(3, c.size());

            tx.commit();    
            
            tx.begin();
            q = pm.newQuery(Manager.class);
            q.setFilter("departments.contains(dept1) && (dept1.name == \"DeptA\" && (departments.contains(dept2) && (dept2.name.matches(\"Dept.*\") && dept1 != dept2)))");
            q.declareVariables("Department dept1; Department dept2");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();             
        
        }
        finally
        {
            if (tx.isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test query with "field.contains(x) && field.contains(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsTwiceOnFieldWithNamespaceMtoN()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            PetroleumCustomer customer1 = new PetroleumCustomer("C1");
            PetroleumCustomer customer2 = new PetroleumCustomer("C2");
            PetroleumCustomer customer3 = new PetroleumCustomer("C3");
            PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
            PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
            PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
            PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
            PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
            customer1.addSupplier(supplier1);
            customer1.addSupplier(supplier2);
            customer2.addSupplier(supplier1);
            customer2.addSupplier(supplier3);
            customer2.addSupplier(supplier5);
            customer3.addSupplier(supplier1);
            customer3.addSupplier(supplier2);
            customer3.addSupplier(supplier3);
            customer3.addSupplier(supplier4);
            pm.makePersistent(customer1);
            pm.makePersistent(customer2);
            pm.makePersistent(customer3);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("(suppliers.contains(sup1) && sup1.name == \"S3\") && (suppliers.contains(sup2) && sup2.name == \"S4\")");
            q.declareVariables("PetroleumSupplier sup1; PetroleumSupplier sup2");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("(suppliers.contains(sup1) && sup1.name == \"S2\") && (suppliers.contains(sup2) && sup2.name == \"S5\")");
            q.declareVariables("PetroleumSupplier sup1; PetroleumSupplier sup2");
            c = (Collection) q.execute();
            assertEquals(0, c.size());

            q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("(suppliers.contains(sup1) && (sup1.name == \"S1\" || sup1.name == \"S3\")) && (suppliers.contains(sup2) && sup2.name == \"S5\")");
            q.declareVariables("PetroleumSupplier sup1; PetroleumSupplier sup2");
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

            // Clean out our data
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }

    /**
     * test query with "field.contains(x) && field.contains(y)"
     * 
     * namespace put related expressions inside parentheses
     */
    public void testQueryUsesContainsTwiceOnFieldWithNamespace2MtoN()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            PetroleumCustomer customer1 = new PetroleumCustomer("C1");
            PetroleumCustomer customer2 = new PetroleumCustomer("C2");
            PetroleumCustomer customer3 = new PetroleumCustomer("C3");
            PetroleumSupplier supplier1 = new PetroleumSupplier("S1");
            PetroleumSupplier supplier2 = new PetroleumSupplier("S2");
            PetroleumSupplier supplier3 = new PetroleumSupplier("S3");
            PetroleumSupplier supplier4 = new PetroleumSupplier("S4");
            PetroleumSupplier supplier5 = new PetroleumSupplier("S5");
            customer1.addSupplier(supplier1);
            customer1.addSupplier(supplier2);
            customer2.addSupplier(supplier1);
            customer2.addSupplier(supplier3);
            customer2.addSupplier(supplier5);
            customer3.addSupplier(supplier1);
            customer3.addSupplier(supplier2);
            customer3.addSupplier(supplier3);
            customer3.addSupplier(supplier4);
            pm.makePersistent(customer1);
            pm.makePersistent(customer2);
            pm.makePersistent(customer3);
            tx.commit();

            tx.begin();

            Query q = pm.newQuery(PetroleumCustomer.class);
            q.setFilter("suppliers.contains(sup1) && (sup1.name == \"S3\" && (suppliers.contains(sup2) && (sup2.name == \"S5\")))");
            q.declareVariables("PetroleumSupplier sup1; PetroleumSupplier sup2");
            Collection c = (Collection) q.execute();
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

            // Clean out our data
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }
    
    
    /**
     * test query with "field.contains(x) && field.contains(y)"
     */
    public void testQueryUsesContainsTwiceOnFieldNoNamespace()
    {
        Manager homer = new Manager(1,"Homer","Simpson","homer@simpson.com",1,"serial 1");
        Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
        Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
        Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
        Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
        bart.addSubordinate(boss);
        bart.addSubordinate(boss2);
        homer.addSubordinate(boss4);
        boss.addSubordinate(boss2);
        Department deptA = new Department("DeptA");
        Department deptB = new Department("DeptB");
        bart.addDepartment(deptB);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.makePersistent(bart);
            pm.makePersistent(homer);
            pm.makePersistent(boss);
            pm.makePersistent(boss2);
            pm.makePersistent(boss4);
            pm.makePersistent(deptA);
            pm.makePersistent(deptB);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && emp1.lastName == \"WakesUp\" && subordinates.contains(emp2) && emp2.lastName == \"WakesUp2\"");
            q.declareVariables("Employee emp1; Employee emp2");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());

            q = pm.newQuery(Manager.class);
            q.setFilter("subordinates.contains(emp1) && subordinates.contains(emp2) && emp1.lastName == \"WakesUp\" && emp2.lastName == \"WakesUp2\"");
            q.declareVariables("Employee emp1; Employee emp2");
            q.declareImports("import org.jpox.samples.models.company.Employee");
            c = (Collection) q.execute();
            assertEquals(1, c.size());

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                pm.currentTransaction().rollback();
            }
            pm.close();

            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    /**
     * Test isEmpty() "or"-ed with a contains clause, that is likely to fail
     * due to innner joins defeating the isEmpty() clause 
     */
    public void testIsEmptyOredWithContains()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Wardrobe wardrobe1 = new Wardrobe();
            Wardrobe wardrobe2 = new Wardrobe();
            wardrobe2.setModel("2 doors");
            Wardrobe wardrobe3 = new Wardrobe();
            wardrobe3.setModel("3 doors");
            pm.makePersistent(wardrobe1);
            pm.makePersistent(wardrobe2);
            pm.makePersistent(wardrobe3);
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

            Query q3 = pm.newQuery(Wardrobe.class,"clothes.isEmpty() || (clothes.contains(cloth) && cloth.name==\"white shirt\" )");
            q3.declareVariables("org.jpox.samples.models.fitness.Cloth cloth");
            q3.setOrdering("model ascending");
            Collection c3 = (Collection) q3.execute();
            assertEquals(2,c3.size());
            Object findId1 = JDOHelper.getObjectId(wardrobe1);
            Object findId2 = JDOHelper.getObjectId(wardrobe3);
            Iterator iter = c3.iterator();
            Object foundId1 = JDOHelper.getObjectId(iter.next());
            Object foundId2 = JDOHelper.getObjectId(iter.next());
            boolean ok = !foundId1.equals(foundId2) &&
                (foundId1.equals(findId1) || foundId1.equals(findId2)) &&
                (foundId2.equals(findId1) || foundId2.equals(findId2));
            assertTrue(ok);

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
     * Test of collection(A) contains C1 and C1.contains(B) contains D1
     */
    public void testContainsResultVariableNestedContains2()
    {
        try
        {
            Manager mgr1 = new Manager(101, "Fred", "Flintstone", "fred.flintstone@jpox.com", (float)250.0, "12345");
            Manager mgr2 = new Manager(102, "Barney", "Rubble", "barney.rubble@jpox.com", (float)220.0, "12356");
            Department dept1 = new Department("Gravel");
            Department dept2 = new Department("Coal");
            Department dept3 = new Department("Cement");
            Department dept4 = new Department("Sales");
            Department dept5 = new Department("Marketing");
            Project proj1 = new Project("proj1", 12000);
            Project proj2 = new Project("proj2", 24000);
            Project proj3 = new Project("proj3", 18000);
            mgr1.addDepartment(dept1);
            mgr1.addDepartment(dept2);
            mgr1.addDepartment(dept3);
            mgr2.addDepartment(dept4);
            mgr2.addDepartment(dept5);
            dept1.addProject(proj1);
            dept2.addProject(proj1);
            dept2.addProject(proj2);
            dept3.addProject(proj1);
            dept3.addProject(proj2);
            dept4.addProject(proj3);
            dept5.addProject(proj3);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(mgr1);
                pm.makePersistent(mgr2);
                tx.commit();

                tx.begin();
                Query q = pm.newQuery(Manager.class);
                q.setFilter("departments.contains(d) && d.projects.contains(p)");
                q.setResult("d, p");
                q.declareVariables("Department d; Project p");
                Collection c = (Collection) q.execute();
                assertEquals(7, c.size());

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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Tests contains() of a primitive in a Collection<Object wrapper>.
     */
    public void testContainsOfPrimitive()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person per1 = new Person(1, "Harvey", "Hacker", "harvey.hacker@acme.com");
            per1.setAge(35);
            Person per2 = new Person(2, "Geoffrey", "Gimp", "geoffrey.gimp@acme.com");
            per2.setAge(37);
            pm.makePersistentAll(new Object[]{per1, per2});
            pm.flush();

            Collection<Integer> ages = new HashSet();
            ages.add(new Integer(35));
            Query q = pm.newQuery(Person.class,":param.contains(age)");
            Collection c = (Collection) q.execute(ages);
            assertEquals(1,c.size());
            Person per = (Person)c.iterator().next();
            assertEquals("Wrong person", "Harvey", per.getFirstName());
            assertEquals("Wrong person", "Hacker", per.getLastName());
            tx.rollback();
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
}