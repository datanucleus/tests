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
package org.datanucleus.tests;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.jpox.samples.many_many.AccountCustomer;
import org.jpox.samples.many_many.GasSupplier;
import org.jpox.samples.many_many.OilSupplier;
import org.jpox.samples.many_many.OneOffCustomer;
import org.jpox.samples.many_many.PetroleumCustomer;
import org.jpox.samples.many_many.PetroleumSupplier;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.Farm;
import org.jpox.samples.one_many.bidir_2.House;
import org.jpox.samples.one_many.bidir_2.Window;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;

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
                    PetroleumCustomer.class,
                    PetroleumSupplier.class,
                    AccountCustomer.class,
                    OneOffCustomer.class,
                    GasSupplier.class,
                    OilSupplier.class,
                });
            initialised = true;
        }
    }

    /**
     * Test for management of relations with a 1-1 bidir where the objects are persisted
     * and only the owner side is set.
     */
    public void testOneToOneBidirPersist()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Vaillant", "SuperDeluxe");
                Timer timer = new Timer("Quartz", true, boiler);
                assertNull("Boiler has non-null Timer but should be null before persist", boiler.getTimer());
                assertNotNull("Timer has null Boiler yet should be non-null before persist", timer.getBoiler());

                pm.makePersistent(timer);
                pm.flush();

                // Check that the relation sides are both set
                assertNotNull("Boiler has null Timer yet should be non-null after persist/flush", boiler.getTimer());
                assertNotNull("Timer has null Boiler yet should be non-null after persist/flush", timer.getBoiler());

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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }

    /**
     * Same as {@link #testOneToOneBidirPersist()}, except that we verify here that
     * this works also when there are two bidirectional associations between the same
     * classes.
     */
    public void testOneToOneBidirPersist2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Vaillant", "SuperDeluxe");
                Timer timer = new Timer("Quartz", true, null);
                timer.setBoiler2(boiler);
                assertNull("Boiler has non-null Timer but should be null before persist", boiler.getTimer2());
                assertNotNull("Timer has null Boiler yet should be non-null before persist", timer.getBoiler2());

                pm.makePersistent(timer);
                pm.flush();

                // Check that the relation sides are both set
                assertNotNull("Boiler has null Timer yet should be non-null after persist/flush", boiler.getTimer2());
                assertNotNull("Timer has null Boiler yet should be non-null after persist/flush", timer.getBoiler2());

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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }
    
    /**
     * Test for management of relations with a 1-1 bidir where the objects are persisted
     * and the relation is inconsistent.
     */
    public void testOneToOneBidirPersistInconsistent()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // timer1 -> boiler, but boiler -> timer2 !!
                Boiler boiler = new Boiler("Vaillant", "SuperDeluxe");
                Timer timer1 = new Timer("Quartz", true, boiler);
                Timer timer2 = new Timer("Quartz", true, null);
                boiler.setTimer(timer2);

                pm.makePersistent(timer1);
                pm.flush();
                fail("Should have thrown exception when persisting inconsistent 1-1 bidir relation");

                tx.commit();
            }
            catch (Exception e)
            {
                // Success
                return;
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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }

    /**
     * Test for management of relations with a 1-1 bidir where the objects are updated at the owner side.
     */
    public void testOneToOneBidirUpdateOwner()
    {
        try
        {
            Object boilerId = null;
            Object timerId = null;

            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Vaillant", "SuperDeluxe");
                Timer timer = new Timer("Quartz", true, boiler);
                boiler.setTimer(timer);
                pm.makePersistent(timer);

                tx.commit();
                boilerId = pm.getObjectId(boiler);
                timerId = pm.getObjectId(timer);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update the owner side
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the objects and check the relation is correct
                Boiler boiler = (Boiler)pm.getObjectById(boilerId);
                Timer timer = (Timer)pm.getObjectById(timerId);
                assertEquals("Retrieved boiler.timer is incorrect", boiler.getTimer(), timer);
                assertEquals("Retrieved timer.boiler is incorrect", timer.getBoiler(), boiler);

                // Update the timer.boiler side and check after flush
                Boiler boiler2 = new Boiler("Baxi", "Compact");
                timer.setBoiler(boiler2);
                pm.flush();
                assertEquals("Updated timer.boiler is incorrect", boiler2, timer.getBoiler());
                assertEquals("Updated boiler.timer is incorrect", timer, boiler2.getTimer());
                assertNull("Updated boiler.timer(old) is incorrect", boiler.getTimer());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown retrieving/updating 1-1 bidir changing owner side", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }

    /**
     * Test for management of relations with a 1-1 bidir where the objects are updated at the non-owner side.
     */
    public void testOneToOneBidirUpdateNonOwner()
    {
        try
        {
            PersistenceManager pm = null;
            Transaction tx = null;
            Object boilerId = null;
            Object timerId = null;

            // Persist the objects so we have them
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Vaillant", "SuperDeluxe");
                Timer timer = new Timer("Quartz", true, boiler);
                boiler.setTimer(timer);
                pm.makePersistent(timer);

                tx.commit();
                boilerId = pm.getObjectId(boiler);
                timerId = pm.getObjectId(timer);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update the non-owner side
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the objects and check the relation is correct
                Boiler boiler = (Boiler)pm.getObjectById(boilerId);
                Timer timer = (Timer)pm.getObjectById(timerId);
                assertEquals("Retrieved boiler.timer is incorrect", boiler.getTimer(), timer);
                assertEquals("Retrieved timer.boiler is incorrect", timer.getBoiler(), boiler);

                // Update the boiler.timer side and check after flush
                Timer timer2 = new Timer("Casio", false, null);
                boiler.setTimer(timer2);
                pm.flush();
                assertEquals("Updated timer.boiler is incorrect", boiler, timer2.getBoiler());
                assertEquals("Updated boiler.timer is incorrect", timer2, boiler.getTimer());
                assertNull("Updated timer.boiler(old) is incorrect", timer.getBoiler());

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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where the objects are persisted
     * and only the collection side is set.
     */
    public void testOneToManyFKBidirPersistCollection()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm = new Farm("Giles Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                farm.addAnimal(animal1);
                farm.addAnimal(animal2);
                assertNull("Animal1 has non-null Farm but should be null before persist", animal1.getFarm());
                assertNull("Animal2 has non-null Farm but should be null before persist", animal2.getFarm());
                assertNotNull("Farm has null Animals yet should be non-null before persist", farm.getAnimals());

                pm.makePersistent(farm);
                pm.flush();

                // Check that the relation sides are both set
                assertNotNull("Animal1 has null Farm but should be non-null after persist/flush", animal1.getFarm());
                assertNotNull("Animal2 has null Farm but should be non-null after persist/flush", animal2.getFarm());
                assertEquals("Farm has incorrect Animals after persist/flush", 2, farm.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N FK bidir with only collection side set", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where the objects are persisted
     * and the owner side of elements is inconsistent with the container.
     */
    public void testOneToManyFKBidirPersistInconsistent()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // farm1 -> animal1, animal2 BUT animal1 -> farm2, animal2 -> farm2
                Farm farm1 = new Farm("Giles Farm");
                Farm farm2 = new Farm("Warburton Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                farm1.addAnimal(animal1);
                farm1.addAnimal(animal2);
                animal1.setFarm(farm2);
                animal2.setFarm(farm2);

                pm.makePersistent(farm1);
                pm.flush();

                fail("Should have thrown exception when persisting inconsistent 1-N FK bidir relation");

                tx.commit();
            }
            catch (Exception e)
            {
                // Success
                return;
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where the objects are persisted
     * and only the FK side is set.
     */
    public void testOneToManyFKBidirPersistElement()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm = new Farm("Giles Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                animal1.setFarm(farm);
                animal2.setFarm(farm);
                assertNotNull("Animal1 has null Farm but should be not-null before persist", animal1.getFarm());
                assertNotNull("Animal2 has null Farm but should be not-null before persist", animal2.getFarm());
                assertEquals("Farm has Animals yet should be empty before persist", 0, farm.getAnimals().size());

                pm.makePersistent(animal1);
                pm.makePersistent(animal2);
                pm.flush();

                // TODO This will work if the collection is using lazy loading since it will not be loaded
                // before here, and will load from the datastore when called.

                // Check that the relation sides are both set
                assertSame("Animal1 has incorrect Farm after persist/flush", farm, animal1.getFarm());
                assertSame("Animal2 has incorrect Farm after persist/flush", farm, animal2.getFarm());
                assertEquals("Farm has incorrect Animals after persist/flush", 2, farm.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N FK bidir with only FK side set", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where the objects are persisted
     * and only the FK side is set for one element but both set for other.
     */
    public void testOneToManyFKBidirPersistElement2()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm = new Farm("Giles Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                animal1.setFarm(farm);
                animal2.setFarm(farm);
                farm.addAnimal(animal2);
                assertNotNull("Animal1 has null Farm but should be not-null before persist", animal1.getFarm());
                assertNotNull("Animal2 has null Farm but should be not-null before persist", animal2.getFarm());
                assertEquals("Farm has Animals yet should be empty before persist", 1, farm.getAnimals().size());

                pm.makePersistent(animal1);
                pm.makePersistent(animal2);
                pm.flush();

                // Check that the relation sides are both set for all objects
                assertSame("Animal1 has incorrect Farm after persist/flush", farm, animal1.getFarm());
                assertSame("Animal2 has incorrect Farm after persist/flush", farm, animal2.getFarm());
                assertEquals("Farm has incorrect Animals after persist/flush", 2, farm.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N FK bidir with FK side set for one element", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where an element is updated to be in a different
     * owner collection and we check that the collections are correctly updated.
     */
    public void testOneToManyFKBidirUpdateElement()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object farm1Id = null;
            Object farm2Id = null;
            Object animal1Id = null;
            Object animal2Id = null;
            try
            {
                tx.begin();

                Farm farm1 = new Farm("Giles Farm");
                Farm farm2 = new Farm("Sunnybrook Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                farm1.addAnimal(animal1);
                farm1.addAnimal(animal2);
                animal1.setFarm(farm1);
                animal2.setFarm(farm1);

                pm.makePersistent(farm1);
                pm.makePersistent(farm2);

                tx.commit();
                farm1Id = pm.getObjectId(farm1);
                farm2Id = pm.getObjectId(farm2);
                animal1Id = pm.getObjectId(animal1);
                animal2Id = pm.getObjectId(animal2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N FK bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm1 = (Farm)pm.getObjectById(farm1Id);
                Farm farm2 = (Farm)pm.getObjectById(farm2Id);
                Animal animal1 = (Animal)pm.getObjectById(animal1Id);
                Animal animal2 = (Animal)pm.getObjectById(animal2Id);
                assertEquals("Animal1 'farm' is incorrect at retrieve", farm1, animal1.getFarm());
                assertEquals("Animal2 'farm' is incorrect at retrieve", farm1, animal2.getFarm());
                assertEquals("Farm1 has incorrect number of animals at retrieve", 2, farm1.getAnimals().size());
                assertEquals("Farm2 has incorrect number of animals at retrieve", 0, farm2.getAnimals().size());

                // Make sure the animals are loaded (cached)
                farm1.getAnimals().iterator();
                farm2.getAnimals().iterator();

                // Move Animal1 from Farm1 to Farm2
                animal1.setFarm(farm2);
                pm.flush();

                assertEquals("Farm1 has incorrect number of animals after animal update", 1, farm1.getAnimals().size());
                assertEquals("Farm2 has incorrect number of animals after animal update", 1, farm2.getAnimals().size());
                assertFalse("House1 contains window1 after update but shouldnt", farm1.getAnimals().contains(animal1));
                assertTrue("House2 doesnt contain window1 after update but should", farm2.getAnimals().contains(animal1));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N FK bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and check
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm1 = (Farm)pm.getObjectById(farm1Id);
                Farm farm2 = (Farm)pm.getObjectById(farm2Id);
                Animal animal1 = (Animal)pm.getObjectById(animal1Id);
                Animal animal2 = (Animal)pm.getObjectById(animal2Id);
                assertEquals("Animal1 'farm' is incorrect at retrieve", farm2, animal1.getFarm());
                assertEquals("Animal2 'farm' is incorrect at retrieve", farm1, animal2.getFarm());
                assertEquals("Farm1 has incorrect number of animals at retrieve", 1, farm1.getAnimals().size());
                assertEquals("Farm2 has incorrect number of animals at retrieve", 1, farm2.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N FK bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where an element is deleted and we check that
     * the collection is correctly updated.
     */
    public void testOneToManyFKBidirDeleteElement()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object farmId = null;
            Object animal1Id = null;
            Object animal2Id = null;
            try
            {
                tx.begin();

                Farm farm = new Farm("Giles Farm");
                Animal animal1 = new Animal("Cow");
                Animal animal2 = new Animal("Dog");
                farm.addAnimal(animal1);
                farm.addAnimal(animal2);
                animal1.setFarm(farm);
                animal2.setFarm(farm);

                pm.makePersistent(farm);

                tx.commit();
                farmId = pm.getObjectId(farm);
                animal1Id = pm.getObjectId(animal1);
                animal2Id = pm.getObjectId(animal2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N FK bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and delete an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm = (Farm)pm.getObjectById(farmId);
                Animal animal1 = (Animal)pm.getObjectById(animal1Id);
                Animal animal2 = (Animal)pm.getObjectById(animal2Id);
                assertEquals("Animal1 'farm' is incorrect at retrieve", farm, animal1.getFarm());
                assertEquals("Animal2 'farm' is incorrect at retrieve", farm, animal2.getFarm());
                assertEquals("Farm has incorrect number of animals at retrieve", 2, farm.getAnimals().size());

                // Delete Animal1
                pm.deletePersistent(animal1);
                pm.flush();

                assertEquals("Farm has incorrect number of animals after animal delete", 1, farm.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown deleting element from 1-N FK bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where the objects are persisted
     * and only the collection side is set.
     */
    public void testOneToManyJoinBidirPersistCollection()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house = new House(15, "King Street");
                Window window1 = new Window(150, 200,null);
                Window window2 = new Window(350, 400,null);
                house.addWindow(window1);
                house.addWindow(window2);
                assertNull("Window1 has non-null House but should be null before persist", window1.getHouse());
                assertNull("Window2 has non-null House but should be null before persist", window2.getHouse());
                assertNotNull("House has null Windows yet should be non-null before persist", house.getWindows());

                pm.makePersistent(house);
                pm.flush();

                // Check that the relation sides are both set
                assertNotNull("Window1 has null House but should be not-null after persist/flush", window1.getHouse());
                assertNotNull("Window2 has null House but should be not-null after persist/flush", window2.getHouse());
                assertNotNull("House has null Windows yet should be non-null after persist/flush", house.getWindows());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir with only collection side set", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N JoinTable bidir where the objects are persisted
     * and only the FK side is set.
     */
    public void testOneToManyJoinBidirPersistElement()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house = new House(14, "Smith Street");
                Window window1 = new Window(100, 200, house);
                Window window2 = new Window(600, 200, house);
                assertNotNull("Window1 has null House but should be not-null before persist", window1.getHouse());
                assertNotNull("Window2 has null House but should be not-null before persist", window2.getHouse());
                assertEquals("House has Windows yet should be empty before persist", 0, house.getWindows().size());

                pm.makePersistent(window1);
                pm.makePersistent(window2);
                pm.flush();

                // Check that the relation sides are both set
                assertSame("Window1 has incorrect House after persist/flush", house, window1.getHouse());
                assertSame("Window2 has incorrect House after persist/flush", house, window2.getHouse());
                assertEquals("House has incorrect Windows after persist/flush", 2, house.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir with only FK side set", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element is updated to be in a different
     * owner collection, and check that they are updated accordingly.
     */
    public void testOneToManyJoinBidirUpdateElement()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object house1Id = null;
            Object house2Id = null;
            Object window1Id = null;
            Object window2Id = null;
            try
            {
                tx.begin();

                House house1 = new House(15, "King Street");
                House house2 = new House(35, "Queen Street");
                Window window1 = new Window(150, 200, null);
                Window window2 = new Window(350, 400, null);
                house1.addWindow(window1);
                house1.addWindow(window2);
                window1.setHouse(house1);
                window2.setHouse(house1);

                pm.makePersistent(house1);
                pm.makePersistent(house2);

                tx.commit();
                house1Id = pm.getObjectId(house1);
                house2Id = pm.getObjectId(house2);
                window1Id = pm.getObjectId(window1);
                window2Id = pm.getObjectId(window2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                House house2 = (House)pm.getObjectById(house2Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house1, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 2, house1.getWindows().size());
                assertEquals("House2 has incorrect number of windows at retrieve", 0, house2.getWindows().size());

                // Update Window1 to be in House2
                window1.setHouse(house2);
                pm.flush();

                assertEquals("House1 has incorrect number of windows after window update", 1, house1.getWindows().size());
                assertEquals("House2 has incorrect number of windows after window update", 1, house2.getWindows().size());
                assertFalse("House1 contains window1 after update but shouldnt", house1.getWindows().contains(window1));
                assertTrue("House2 doesnt contain window1 after update but should", house2.getWindows().contains(window1));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and check
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                House house2 = (House)pm.getObjectById(house2Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house2, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 1, house1.getWindows().size());
                assertEquals("House2 has incorrect number of windows at retrieve", 1, house2.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element is deleted, so we make
     * sure that it is deleted from the collection too.
     */
    public void testOneToManyJoinBidirDeleteElement()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object houseId = null;
            Object window1Id = null;
            Object window2Id = null;
            try
            {
                tx.begin();

                House house = new House(15, "King Street");
                Window window1 = new Window(150, 200,null);
                Window window2 = new Window(350, 400,null);
                house.addWindow(window1);
                house.addWindow(window2);
                window1.setHouse(house);
                window2.setHouse(house);

                pm.makePersistent(house);

                tx.commit();
                houseId = pm.getObjectId(house);
                window1Id = pm.getObjectId(window1);
                window2Id = pm.getObjectId(window2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and delete an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house = (House)pm.getObjectById(houseId);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house, window2.getHouse());
                assertEquals("House has incorrect number of windows at retrieve", 2, house.getWindows().size());

                // Delete Window1
                pm.deletePersistent(window1);
                pm.flush();

                assertEquals("House has incorrect number of windows after window delete", 1, house.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown deleting element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element's owner is being removed
     * (set to null), and check that both element and owner are updated accordingly.
     */
    public void testOneToManyJoinBidirUnsetOwner()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object house1Id = null;
            Object window1Id = null;
            Object window2Id = null;
            try
            {
                tx.begin();

                House house1 = new House(15, "King Street");
                Window window1 = new Window(150, 200, null);
                Window window2 = new Window(350, 400, null);
                house1.addWindow(window1);
                house1.addWindow(window2);

                pm.makePersistent(house1);

                tx.commit();
                house1Id = pm.getObjectId(house1);
                window1Id = pm.getObjectId(window1);
                window2Id = pm.getObjectId(window2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house1, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 2, house1.getWindows().size());

                // Update Window1 to have no house
                window1.setHouse(null);
                pm.flush();

                assertEquals("House1 has incorrect number of windows after window update", 1, house1.getWindows().size());
                assertFalse("House1 contains window1 after update but shouldnt", house1.getWindows().contains(window1));
                assertEquals("Window1 'house' is incorrect after update", null, window1.getHouse());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and check
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", null, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 1, house1.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element is being removed
     * from its owner, and check that both element and owner are updated accordingly.
     */
    public void testOneToManyJoinBidirRemoveElement()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object house1Id = null;
            Object window1Id = null;
            Object window2Id = null;
            try
            {
                tx.begin();

                House house1 = new House(15, "King Street");
                Window window1 = new Window(150, 200, null);
                Window window2 = new Window(350, 400, null);
                house1.addWindow(window1);
                house1.addWindow(window2);

                pm.makePersistent(house1);

                tx.commit();
                house1Id = pm.getObjectId(house1);
                window1Id = pm.getObjectId(window1);
                window2Id = pm.getObjectId(window2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house1, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 2, house1.getWindows().size());

                // Update Window1 no to belong to House1 anymore
                house1.removeWindow(window1);
                pm.flush();

                assertEquals("House1 has incorrect number of windows after window update", 1, house1.getWindows().size());
                assertFalse("House1 contains window1 after update but shouldnt", house1.getWindows().contains(window1));
                assertEquals("Window1 'house' is incorrect after update", null, window1.getHouse());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and check
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", null, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 1, house1.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a 1-N jointable bidir where an element is being removed
     * from its owner by resetting its element collection,
     * and check that both element and owner are updated accordingly.
     */
    public void testOneToManyJoinBidirSetCollection()
    {
        try
        {
            // Persist the objects so we have them
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            Object house1Id = null;
            Object window1Id = null;
            Object window2Id = null;
            try
            {
                tx.begin();

                House house1 = new House(15, "King Street");
                Window window1 = new Window(150, 200, null);
                Window window2 = new Window(350, 400, null);
                house1.addWindow(window1);
                house1.addWindow(window2);

                pm.makePersistent(house1);

                tx.commit();
                house1Id = pm.getObjectId(house1);
                window1Id = pm.getObjectId(window1);
                window2Id = pm.getObjectId(window2);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and update an element
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", house1, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 2, house1.getWindows().size());

                // Update House1 to have just window2
                house1.setWindows(Collections.singleton(window2));
                pm.flush();

                assertEquals("House1 has incorrect number of windows after window update", 1, house1.getWindows().size());
                assertFalse("House1 contains window1 after update but shouldnt", house1.getWindows().contains(window1));
                assertTrue("House1 should still contain window2 after update", house1.getWindows().contains(window2));
                assertEquals("Window1 'house' is incorrect after update", null, window1.getHouse());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the objects and check
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                House house1 = (House)pm.getObjectById(house1Id);
                Window window1 = (Window)pm.getObjectById(window1Id);
                Window window2 = (Window)pm.getObjectById(window2Id);
                assertEquals("Window1 'house' is incorrect at retrieve", null, window1.getHouse());
                assertEquals("Window2 'house' is incorrect at retrieve", house1, window2.getHouse());
                assertEquals("House1 has incorrect number of windows at retrieve", 1, house1.getWindows().size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown updating element from 1-N JoinTable bidir", e);
                fail("Error in test : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
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
     * Test for management of relations with a 1-N jointable bidir where the objects are persisted
     * and the owner side of elements is inconsistent with the container.
     */
    public void testOneToManyJoinBidirPersistInconsistent()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // house1 -> window1, window2 BUT window1 -> house2, window2 -> house2
                House house1 = new House(15, "King Street");
                House house2 = new House(23, "Queen Street");
                Window window1 = new Window(150, 200,null);
                Window window2 = new Window(350, 400,null);
                house1.addWindow(window1);
                house1.addWindow(window2);
                window1.setHouse(house2);
                window2.setHouse(house2);

                pm.makePersistent(house1);
                pm.flush();

                fail("Should have thrown exception when persisting inconsistent 1-N JoinTable bidir relation");

                tx.commit();
            }
            catch (Exception e)
            {
                // Success
                return;
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
            clean(House.class);
            clean(Window.class);
        }
    }

    /**
     * Test for management of relations with a M-N bidir where the objects are persisted
     * and only the owner side is set.
     */
    public void testManyToManyBidirPersist()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                OilSupplier supplier1 = new OilSupplier("Petroco");
                OneOffCustomer customer1 = new OneOffCustomer("George Bush");
                AccountCustomer customer2 = new AccountCustomer("Tony Blair");
                supplier1.addCustomer(customer1);
                supplier1.addCustomer(customer2);
                assertEquals("Customer1 has suppliers but shouldnt before persist", 0, customer1.getSuppliers().size());
                assertEquals("Customer2 has suppliers but shouldnt before persist", 0, customer2.getSuppliers().size());
                assertEquals("Supplier1 has incorrect customers before persist", 2, supplier1.getCustomers().size());
                
                pm.makePersistent(supplier1);
                pm.flush();
                
                // Check that the relation sides are both set
                assertEquals("Customer1 has incorrect suppliers after persist/flush", 1, customer1.getSuppliers().size());
                assertEquals("Customer2 has incorrect suppliers after persist/flush", 1, customer2.getSuppliers().size());
                assertEquals("Supplier1 has incorrect customers after persist/flush", 2, supplier1.getCustomers().size());
                
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
            clean(PetroleumSupplier.class);
            clean(PetroleumCustomer.class);
        }
    }

    /**
     * Test for management of relations with a 1-N FK bidir where an element is being moved
     * from one collection owner to another, by setting a collection containing that element
     * on a new owner 
     * @see {@link #testOneToManyJoinBidirSetCollectionMoveElement()}
     */
    public void testOneToManyFKBidirSetCollectionMoveElement()
    {
        PersistenceManager pm = null;
        Transaction tx = null;
        try
        {
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();

            // Create object graph:
            // farm1 <-> {animal1, animal2}
            // farm2 <-> {animal3, animal4}
            // farm5 <-> {animal5}
            tx.begin();

            Farm farm1 = new Farm("farm1");
            Animal animal1 = new Animal("animal1");
            Animal animal2 = new Animal("animal2");
            farm1.setAnimals(createSet(animal1, animal2));

            pm.makePersistent(farm1);
            
            Farm farm2 = new Farm("farm2");
            Animal animal3 = new Animal("animal3");
            Animal animal4 = new Animal("animal4");
            farm2.setAnimals(createSet(animal3, animal4));
            
            pm.makePersistent(farm2);
            
            Farm farm3 = new Farm("farm3");
            Animal animal5 =  new Animal("animal5");
            farm3.setAnimals(createSet(animal5));
            
            pm.makePersistent(farm3);

            pm.flush();

            // validate objectgraph
            assertEquals(createSet(animal1, animal2), farm1.getAnimals());
            assertEquals(farm1, animal1.getFarm());
            assertEquals(farm1, animal2.getFarm());
            
            assertEquals(createSet(animal3, animal4), farm2.getAnimals());
            assertEquals(farm2, animal3.getFarm());
            assertEquals(farm2, animal4.getFarm());
            
            assertEquals(createSet(animal5), farm3.getAnimals());
            assertEquals(farm3, animal5.getFarm());

            tx.commit();

            
            // perform update and validate
            tx.begin();
            farm1.setAnimals(createSet(animal2, animal3, animal5));
            pm.flush();
            // should result in:
            // farm1 <-> {animal2, animal3, animal5}
            // farm2 <-> {animal4}
            // farm3 <-> {}
            // i.e. animal3 and animal5 moved from their previous owners to farm1

            assertEquals(createSet(animal2, animal3, animal5), farm1.getAnimals());
            assertEquals(farm1, animal2.getFarm());
            assertEquals(farm1, animal3.getFarm());
            assertEquals(farm1, animal5.getFarm());

            assertEquals(createSet(animal4), farm2.getAnimals());
            assertEquals(farm2, animal4.getFarm());
            
            assertEquals(Collections.EMPTY_SET, farm3.getAnimals());

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
                clean(Farm.class);
                clean(Animal.class);
            }
        }
    }
}