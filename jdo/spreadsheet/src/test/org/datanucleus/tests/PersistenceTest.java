/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
2008 Andy Jefferson - added app-id dup test
 ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Project;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.Farm;

/**
 * Series of basic persistence tests for spreadsheets (Excel, OOXML, ODF).
 */
public class PersistenceTest extends JDOPersistenceTestCase
{
    Object id1;
    Object id2;

    public PersistenceTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(1);
            p.setGlobalNum("1");
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            pm.makePersistent(p);

            Employee e = new Employee();
            e.setPersonNum(2);
            e.setGlobalNum("2");
            e.setFirstName("Daffy");
            e.setLastName("Duck");
            e.setSerialNo("A12345");
            pm.makePersistent(e);
            pm.flush();

            id1 = pm.getObjectId(p);
            id2 = pm.getObjectId(e);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in setUp", e);
            fail("Exception in setup");
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

    protected void tearDown() throws Exception
    {
        super.tearDown();
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.deletePersistent(pm.getObjectById(id1));
            pm.deletePersistent(pm.getObjectById(id2));
            tx.commit();
        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
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
     * Test that adds a new attribute to spreadsheet
     */
    public void testUpdateFieldNoLongerNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id1);
            p.setEmailAddress("ppp");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id1);
            assertEquals("ppp", p1.getEmailAddress());
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

    /**
     * Test setting a field to null, removes the attribute from spreadsheet
     */
    public void testUpdateFieldSetNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id1);
            p.setEmailAddress("ppp");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id1);
            assertEquals("ppp", p1.getEmailAddress());
            p1.setEmailAddress(null);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p1 = (Person) pm.getObjectById(id1);
            assertNull(p1.getEmailAddress());
            p.setEmailAddress(null);
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
    
    /**
     * Fetch objects and assert basic values
     */
    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id1);
            assertEquals("Bugs", p1.getFirstName());
            assertEquals("Bunny", p1.getLastName());
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

    public void testExtent()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ex = pm.getExtent(Person.class, false);
            Iterator it = ex.iterator();
            boolean personFound = false;
            boolean employeeFound = false;
            int number = 0;
            while (it.hasNext())
            {
                Object obj = it.next();
                Object id = JDOHelper.getObjectId(obj);
                if (id.equals(id1))
                {
                    personFound = true;
                }
                else if (id.equals(id2))
                {
                    employeeFound = true;
                }
                number++;
            }
            assertEquals("Number of objects in Extent was wrong", 1, number);
            assertTrue("Should have found Person but didnt", personFound);
            assertFalse("Shouldnt have found Employee but did", employeeFound);
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

    public void testExtentSubclasses()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ex = pm.getExtent(Person.class, true);
            Iterator it = ex.iterator();
            boolean personFound = false;
            boolean employeeFound = false;
            int number = 0;
            while (it.hasNext())
            {
                Object obj = it.next();
                Object id = JDOHelper.getObjectId(obj);
                if (id.equals(id1))
                {
                    personFound = true;
                }
                else if (id.equals(id2))
                {
                    employeeFound = true;
                }
                number++;
            }
            assertEquals("Number of objects in Extent was wrong", 2, number);
            assertTrue("Should have found Person but didnt", personFound);
            assertTrue("Should have found Employee but didnt", employeeFound);
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

    /**
     * Test of persistence of more than 1 app id objects with the same "id".
     */
    public void testPersistDuplicates()
    {
        // Persist an object with same PK as the setUp object
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Person p2 = new Person(2, "Mickey", "Mouse", "micket.mouse@warnerbros.com");
            p2.setGlobalNum("1");
            pm.makePersistent(p2);

            tx.commit();
            fail("Was allowed to persist two application-identity objects with the same identity");
        }
        catch (Exception e)
        {
            // Expected
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
     * Test the persistence of class with a Date field.
     */
    public void testPersistDate()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Qualification q = new Qualification("Cycling Proficiency");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 15);
                cal.set(Calendar.MONTH, 5);
                cal.set(Calendar.YEAR, 2006);
                cal.set(Calendar.HOUR_OF_DAY, 7);
                cal.set(Calendar.MINUTE, 30);
                cal.set(Calendar.SECOND, 0);
                q.setDate(cal.getTime());
                pm.makePersistent(q);
                tx.commit();
                id = JDOHelper.getObjectId(q);
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
                Qualification q = (Qualification) pm.getObjectById(id);
                assertEquals("Cycling Proficiency", q.getName());
                Date d = q.getDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                assertEquals("Year is wrong", 2006, cal.get(Calendar.YEAR));
                assertEquals("Month is wrong", 5, cal.get(Calendar.MONTH));
                assertEquals("Day of month is wrong", 15, cal.get(Calendar.DAY_OF_MONTH));
                assertEquals("Hour is wrong", 7, cal.get(Calendar.HOUR_OF_DAY));
                assertEquals("Minute is wrong", 30, cal.get(Calendar.MINUTE));
                assertEquals("Second is wrong", 0, cal.get(Calendar.SECOND));
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
            clean(Qualification.class);
        }
    }

    /**
     * Test that persists using datastore identity.
     */
    public void testDatastoreIdPersist()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Project p = new Project("DataNucleus", 1000000);
                pm.makePersistent(p);
                tx.commit();
                id = pm.getObjectId(p);
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
                Project p = (Project)pm.getObjectById(id);
                assertEquals("DataNucleus", p.getName());
                assertEquals(1000000, p.getBudget());
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
            clean(Project.class);
        }
    }

    /**
     * Test the persistence/retrieval of class with 1-1 relation.
     */
    public void testPersistOneToOne()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object qId = null;
            Object oId = null;
            try
            {
                tx.begin();
                Qualification q = new Qualification("Cycling Proficiency");
                Organisation o = new Organisation("BSA");
                q.setOrganisation(o);
                pm.makePersistent(q);
                tx.commit();
                qId = JDOHelper.getObjectId(q);
                oId = JDOHelper.getObjectId(o);
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
                Qualification q = (Qualification) pm.getObjectById(qId);
                assertEquals("Cycling Proficiency", q.getName());
                Organisation o = q.getOrganisation();
                assertNotNull("Organisation is null!", o);
                assertEquals("Organisation id is different", oId, JDOHelper.getObjectId(o));
                assertEquals("Organisation name is different", "BSA", o.getName());
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
            clean(Qualification.class);
            clean(Organisation.class);
        }
    }

    /**
     * Test the persistence/retrieval of class with 1-N relation.
     * In addition the collection field is marked as dependent-element, so delete of an element
     * should delete the related object.
     */
    public void testPersistOneToManyBidir()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object farm1Id = null;
            Object farm2Id = null;
            Object animal1Id = null;
            Object animal2Id = null;
            Object animal3Id = null;
            try
            {
                tx.begin();

                Farm farm1 = new Farm("Giles Farm");
                Animal cow = new Animal("Big Brown Cow");
                Animal pig = new Animal("Dirty Pig");
                farm1.addAnimal(cow);
                cow.setFarm(farm1);
                farm1.addAnimal(pig);
                pig.setFarm(farm1);

                Farm farm2 = new Farm("Brook Farm");
                Animal dog = new Animal("Yapping Dog");
                farm2.addAnimal(dog);
                dog.setFarm(farm2);

                pm.makePersistent(farm1);
                pm.makePersistent(farm2);

                tx.commit();

                farm1Id = JDOHelper.getObjectId(farm1);
                farm2Id = JDOHelper.getObjectId(farm2);
                animal1Id = JDOHelper.getObjectId(cow);
                animal2Id = JDOHelper.getObjectId(pig);
                animal3Id = JDOHelper.getObjectId(dog);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Clear L2 cache so we force load from spreadsheet
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm1 = (Farm)pm.getObjectById(farm1Id);
                assertEquals("Name of farm1 is incorrect", "Giles Farm", farm1.getName());
                Set<Animal> farm1Animals = farm1.getAnimals();
                assertEquals("Number of animals in farm1 is incorrect", 2, farm1Animals.size());

                Farm farm2 = (Farm)pm.getObjectById(farm2Id);
                assertEquals("Name of farm2 is incorrect", "Brook Farm", farm2.getName());
                Set<Animal> farm2Animals = farm2.getAnimals();
                assertEquals("Number of animals in farm2 is incorrect", 1, farm2Animals.size());

                Animal cow = (Animal)pm.getObjectById(animal1Id);
                assertEquals("Farm of cow is incorrect", farm1, cow.getFarm());
                Animal pig = (Animal)pm.getObjectById(animal2Id);
                assertEquals("Farm of pig is incorrect", farm1, pig.getFarm());
                Animal dog = (Animal)pm.getObjectById(animal3Id);
                assertEquals("Farm of dog is incorrect", farm2, dog.getFarm());

                // Remove the cow, should get deleted
                farm1.removeAnimal(cow);

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

            // Clear L2 cache so we force load from spreadsheet
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Farm farm1 = (Farm)pm.getObjectById(farm1Id);
                assertEquals("Name of farm1 is incorrect", "Giles Farm", farm1.getName());
                Set<Animal> farm1Animals = farm1.getAnimals();
                assertEquals("Number of animals in farm1 is incorrect", 1, farm1Animals.size());

                Farm farm2 = (Farm)pm.getObjectById(farm2Id);
                assertEquals("Name of farm2 is incorrect", "Brook Farm", farm2.getName());
                Set<Animal> farm2Animals = farm2.getAnimals();
                assertEquals("Number of animals in farm2 is incorrect", 1, farm2Animals.size());

                try
                {
                    pm.getObjectById(animal1Id);
                    fail("Cow should have been deleted, but still exists in datastore");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }
                Animal pig = (Animal)pm.getObjectById(animal2Id);
                assertEquals("Farm of pig is incorrect", farm1, pig.getFarm());
                Animal dog = (Animal)pm.getObjectById(animal3Id);
                assertEquals("Farm of dog is incorrect", farm2, dog.getFarm());

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
            clean(Farm.class);
        }
    }
}