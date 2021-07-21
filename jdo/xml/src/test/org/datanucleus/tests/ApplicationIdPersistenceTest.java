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
 2008 Andy Jefferson - test for app id dups
 2008 Andy Jefferson - tests for 1-1, 1-N
 2008 Eric Sultan - test for 1-N
 ...
 ***********************************************************************/
package org.datanucleus.tests;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.identity.StringIdentity;

import org.datanucleus.samples.map.MapHolderXML;
import org.datanucleus.samples.models.company.Account;
import org.datanucleus.samples.models.company.Person;
import org.datanucleus.samples.models.company.Project;
import org.datanucleus.samples.one_many.bidir.Animal;
import org.datanucleus.samples.one_many.bidir.Cattle;
import org.datanucleus.samples.one_many.bidir.DairyFarm;
import org.datanucleus.samples.one_many.bidir.Farm;
import org.datanucleus.samples.one_many.bidir.Poultry;
import org.datanucleus.samples.one_many.unidir.Computer;
import org.datanucleus.samples.one_many.unidir.DesktopComputer;
import org.datanucleus.samples.one_many.unidir.LaptopComputer;
import org.datanucleus.samples.one_many.unidir.Office;
import org.datanucleus.samples.one_one.bidir.Boiler;
import org.datanucleus.samples.one_one.bidir.Timer;
import org.datanucleus.samples.one_one.unidir.Login;
import org.datanucleus.samples.one_one.unidir.LoginAccount;

/**
 * Application identity persistence tests for XML datastores.
 */
public class ApplicationIdPersistenceTest extends JDOPersistenceTestCase
{
    Object id;

    public ApplicationIdPersistenceTest(String name)
    {
        super(name);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        // Delete the file so each test starts from nothing
        File file = new File("test.xml");
        if (file.exists())
        {
            file.delete();
        }
    }

    public void testFindNoXpathDefinedOnNewFile() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getObjectById(new StringIdentity(Project.class, "xpath"));
                fail("Expected ObjectNotFoundException");
                tx.commit();
            }
            catch (JDOObjectNotFoundException ex)
            {
                //expected
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown when running test " + e.getMessage());
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
        }
    }

    public void testFindOnXpathDefinedOnNewFile() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getObjectById(new StringIdentity(Person.class, "1"));
                fail("Expected ObjectNotFoundException");
                tx.commit();
            }
            catch (JDOObjectNotFoundException ex)
            {
                //expected
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown when running test " + e.getMessage());
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
        }
    }

    public void testInsert() throws Exception
    {
        try
        {
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

                Person p2 = new Person();
                p2.setPersonNum(2);
                p2.setGlobalNum("2");
                p2.setFirstName("My");
                p2.setLastName("Friend");

                p.setBestFriend(p2);

                pm.makePersistent(p);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown when running test " + e.getMessage());
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
            clean(Person.class);
        }
    }

    public void testInsertMultipleClassesToSameXPath() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(4);
            p.setGlobalNum("4");
            p.setFirstName("Bugs4");
            p.setLastName("Bunny4");

            Account a = new Account();
            a.setId(1);
            a.setUsername("testusername");
            pm.makePersistent(p);
            pm.makePersistent(a);
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown when running test " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            clean(Person.class);
            clean(Account.class);
        }
    }

    /**
     * Test of persistence of more than 1 app id objects with the same "id".
     */
    public void testPersistDuplicates()
    {
        try
        {
            // Persist an object with id "101"
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Person p1 = new Person(101, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                p1.setGlobalNum("101");
                pm.makePersistent(p1);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
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

                Person p2 = new Person(101, "Bugs", "Bunny", "bugs.bunny@warnerbros.com");
                p2.setGlobalNum("101");
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
        finally
        {
            // Do clean up
            clean(Person.class);
        }
    }

    /**
     * Test of persist of 1-1 UNIDIR relation (PC field).
     */
    public void testPersistOneToOneUni()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object acctId = null;
            Object loginId = null;
            try
            {
                tx.begin();

                LoginAccount acct = new LoginAccount("Mickey", "Mouse", "mickeym", "minnie");
                acct.setId(1);
                Login login = acct.getLogin();
                login.setId(1);
                pm.makePersistent(acct);

                tx.commit();
                acctId = pm.getObjectId(acct);
                loginId = pm.getObjectId(login);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct = (LoginAccount) pm.getObjectById(acctId);
                Login login = (Login) pm.getObjectById(loginId);
                assertEquals("Acct first name retrieved is incorrect", "Mickey", acct.getFirstName());
                assertEquals("Acct last name retrieved is incorrect", "Mouse", acct.getLastName());
                assertEquals("Login username retrieved is incorrect", "mickeym", login.getUserName());
                assertEquals("Login password retrieved is incorrect", "minnie", login.getPassword());
                assertEquals("Login of LoginAccount retrieved is incorrect", login.getId(), acct.getLogin().getId());
                assertEquals("Login of LoginAccount retrieved is incorrect", login.getUserName(), acct.getLogin().getUserName());
                assertEquals("Login of LoginAccount retrieved is incorrect", login.getPassword(), acct.getLogin().getPassword());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(LoginAccount.class);
            clean(Login.class);
        }
    }

    /**
     * Test of persist of 1-1 relation (PC field).
     */
    public void testPersistOneToOneBidir()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object boilerId = null;
            Object timerId = null;
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Maxwarm");
                boiler.setId(1);
                Timer timer = new Timer("Seiko", true, boiler);
                boiler.setId(1);
                boiler.setTimer(timer);
                pm.makePersistent(boiler);

                tx.commit();
                boilerId = pm.getObjectId(boiler);
                timerId = pm.getObjectId(timer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Boiler boiler = (Boiler) pm.getObjectById(boilerId);
                Timer timer = (Timer) pm.getObjectById(timerId);
                assertEquals("Make of Boiler retrieved is incorrect", "Baxi", boiler.getMake());
                assertEquals("Model of Boiler retrieved is incorrect", "Maxwarm", boiler.getModel());
                assertEquals("Model of Timer retrieved is incorrect", "Seiko", timer.getMake());
                assertEquals("Digital flag of Timer retrieved is incorrect", true, timer.isDigital());
                assertNotNull("Timer should not be null", boiler.getTimer());
                assertEquals("Timer make is incorrect", timer.getMake(), boiler.getTimer().getMake());
                assertEquals("Timer id is incorrect", timer.getId(), boiler.getTimer().getId());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(Boiler.class);
            clean(Timer.class);
        }
    }

    /**
     * Test of persist of 1-N UNIDIR relation (PC field).
     */
    public void testPersistOneToManyUni()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object officeId = null;
            try
            {
                tx.begin();

                Office office = new Office("theOffice");

                LaptopComputer macBook = new LaptopComputer("192.168.1.1", "MACOSX", 4, 0);
                macBook.setId(1);
                office.addComputer(macBook);

                DesktopComputer dell = new DesktopComputer("192.168.1.2", "Windows", 2);
                dell.setId(2);
                office.addComputer(dell);

                pm.makePersistent(office);

                tx.commit();
                officeId = pm.getObjectId(office);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Office theOffice = (Office) pm.getObjectById(officeId);
                assertEquals("Office name retrieved is incorrect", "theOffice", theOffice.getName());
                assertEquals("Office computers size retrieved is incorrect", 2, theOffice.getNumberOfComputers());
                Iterator iterator = theOffice.getComputers().iterator();
                while (iterator.hasNext())
                {
                    Computer computer = (Computer) iterator.next();
                    if (computer instanceof LaptopComputer)
                    {
                        LaptopComputer laptop = (LaptopComputer) computer;
                        assertEquals("Laptop id retrieved is incorrect", 1, laptop.getId());
                        assertEquals("Laptop ip retrieved is incorrect", "192.168.1.1", laptop.getIpAddress());
                        assertEquals("Laptop OperatingSystem retrieved is incorrect", "MACOSX", laptop.getOperatingSystem());
                        assertEquals("Laptop BatteryLife retrieved is incorrect", 4, laptop.getBatteryLife());
                        assertEquals("Laptop NumberOfPcmcia retrieved is incorrect", 0, laptop.getNumberOfPcmcia());
                    }
                    else if (computer instanceof DesktopComputer)
                    {
                        DesktopComputer desktop = (DesktopComputer) computer;
                        assertEquals("Desktop id retrieved is incorrect", 2, desktop.getId());
                        assertEquals("Desktop ip retrieved is incorrect", "192.168.1.2", desktop.getIpAddress());
                        assertEquals("Desktop Operating System retrieved is incorrect", "Windows", desktop.getOperatingSystem());
                        assertEquals("Desktop NumberOfProcessors retrieved is incorrect", 2, desktop.getNumberOfProcessors());
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(Office.class);
            clean(Computer.class);
        }
    }

    /**
     * Test of persist of 1-N BIDIR relation (PC field).
     */
    public void testPersistOneToManyBidi()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object dairyFarmId = null;
            try
            {
                tx.begin();

                DairyFarm dairyFarm = new DairyFarm("theFarm");
                Cattle cattle = new Cattle("theCattle");
                cattle.setBreed("theBreedofCattle");
                cattle.setFarm(dairyFarm);
                dairyFarm.addAnimal(cattle);

                Poultry poultry = new Poultry("thePoultry");
                poultry.setLaysEggs(true);
                poultry.setFarm(dairyFarm);
                dairyFarm.addAnimal(poultry);

                pm.makePersistent(dairyFarm);

                tx.commit();
                dairyFarmId = pm.getObjectId(dairyFarm);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DairyFarm theDairyFarm = (DairyFarm) pm.getObjectById(dairyFarmId);
                assertEquals("DairyFarm name retrieved is incorrect", "theFarm", theDairyFarm.getName());
                assertEquals("DairyFarm animals size retrieved is incorrect", 2, theDairyFarm.getAnimals().size());
                Iterator iterator = theDairyFarm.getAnimals().iterator();
                while (iterator.hasNext())
                {
                    Animal animal = (Animal) iterator.next();
                    if (animal instanceof Cattle)
                    {
                        Cattle cattle = (Cattle) animal;
                        assertEquals("Cattle name retrieved is incorrect", "theCattle", cattle.getName());
                        assertEquals("Cattle breed retrieved is incorrect", "theBreedofCattle", cattle.getBreed());
                        assertEquals("Cattle farm retrieved is incorrect", theDairyFarm, cattle.getFarm());
                    }
                    else if (animal instanceof Poultry)
                    {
                        Poultry poultry = (Poultry) animal;
                        assertEquals("Poultry name retrieved is incorrect", "thePoultry", poultry.getName());
                        assertEquals("Poultry layseggs retrieved is incorrect", true, poultry.getLaysEggs());
                        assertEquals("Poultry farm retrieved is incorrect", theDairyFarm, poultry.getFarm());
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data using ALL fetch plan and infinite reach. Tests loading bidirs
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL).setMaxFetchDepth(-1);
            try
            {
                tx.begin();
                DairyFarm theDairyFarm = (DairyFarm) pm.getObjectById(dairyFarmId);
                assertEquals("DairyFarm name retrieved is incorrect", "theFarm", theDairyFarm.getName());
                assertEquals("DairyFarm animals size retrieved is incorrect", 2, theDairyFarm.getAnimals().size());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.begin();
            Extent ex = pm.getExtent(Farm.class);
            Iterator iter = ex.iterator();
            while (iter.hasNext())
            {
                Farm f = (Farm)iter.next();
                f.getAnimals().clear();
            }
            tx.commit();

            clean(Farm.class);
            clean(Animal.class);
        }
    }

    public void testMapOfNonPersistent()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object map1Id = null;
            try
            {
                tx.begin();

                MapHolderXML map1 = new MapHolderXML();
                map1.setId("First");
                map1.getMap().put("Key 1", "Value 1");
                map1.getMap().put("Key 2", "Value 2");
                map1.getMap().put("Key 3", "Value 3");
                pm.makePersistent(map1);

                tx.commit();
                map1Id = pm.getObjectId(map1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown persisting data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                MapHolderXML map1 = (MapHolderXML)pm.getObjectById(map1Id);
                Map<String, String> theMap = map1.getMap();
                assertEquals("Number of entries in map is wrong", 3, theMap.size());
                assertTrue("Key 1 is not present!", theMap.containsKey("Key 1"));
                assertTrue("Key 2 is not present!", theMap.containsKey("Key 2"));
                assertTrue("Key 3 is not present!", theMap.containsKey("Key 3"));

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown retrieving data " + e.getMessage());
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
            // Clean out data
            clean(MapHolderXML.class);
        }
    }
}