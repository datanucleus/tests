/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
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
package org.datanucleus.tests.directory.embedded;

import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.datastore.DataStoreCache;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.embedded.Chip;
import org.jpox.samples.embedded.DigitalCamera;
import org.jpox.samples.embedded.Memory;

public class EmbeddedMixedTest extends JDOPersistenceTestCase
{
    public EmbeddedMixedTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Person.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Person.class);
        super.tearDown();
    }

    public void testUpdateEmbeddedDetached1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account account = new Account("dduck", "secret12");
            daffyDuck.setAccount(account);
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            contactData.setPerson(daffyDuck);
            daffyDuck.setContactData(contactData);

            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);

            Computer apple = new Computer("PC-0001", "Apple");
            ComputerCard graphics = new ComputerCard("Graphics", "Matrox");
            ComputerCard sound = new ComputerCard("Sound", "Creative");
            ComputerCard network = new ComputerCard("Network", "Intel");
            apple.getCards().add(graphics);
            apple.getCards().add(sound);
            apple.getCards().add(network);
            OperatingSystem macosx = new OperatingSystem("MacOSX", "10");
            apple.setOperatingSystem(macosx);
            Computer sunfire = new Computer("PC-9999", "Sun Fire");
            OperatingSystem solaris = new OperatingSystem("Solaris", "8");
            sunfire.setOperatingSystem(solaris);
            daffyDuck.getComputers().add(apple);
            daffyDuck.getComputers().add(sunfire);

            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // update
            detachedDaffyDuck.getNotebook().setName("Lenovo");

            // attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());

            assertNotNull(daffyDuck.getNotebook());
            assertEquals("Lenovo", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(1, daffyDuck.getNotebook().getCards().size());
            assertEquals(2, daffyDuck.getComputers().size());

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

    public void testUpdateEmbeddedDetached2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account account = new Account("dduck", "secret12");
            daffyDuck.setAccount(account);
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            contactData.setPerson(daffyDuck);
            daffyDuck.setContactData(contactData);
    
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
    
            Computer apple = new Computer("PC-0001", "Apple");
            ComputerCard graphics = new ComputerCard("Graphics", "Matrox");
            ComputerCard sound = new ComputerCard("Sound", "Creative");
            ComputerCard network = new ComputerCard("Network", "Intel");
            apple.getCards().add(graphics);
            apple.getCards().add(sound);
            apple.getCards().add(network);
            OperatingSystem macosx = new OperatingSystem("MacOSX", "10");
            apple.setOperatingSystem(macosx);
            Computer sunfire = new Computer("PC-9999", "Sun Fire");
            OperatingSystem solaris = new OperatingSystem("Solaris", "8");
            sunfire.setOperatingSystem(solaris);
            daffyDuck.getComputers().add(apple);
            daffyDuck.getComputers().add(sunfire);
    
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();
    
            // update
            detachedDaffyDuck.getNotebook().getOperatingSystem().setVersion("XP");
    
            // attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
    
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("XP", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(1, daffyDuck.getNotebook().getCards().size());
            assertEquals(2, daffyDuck.getComputers().size());
    
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

    public void testUpdateEmbeddedDetached5()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account account = new Account("dduck", "secret12");
            daffyDuck.setAccount(account);
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            contactData.setPerson(daffyDuck);
            daffyDuck.setContactData(contactData);
    
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
    
            Computer apple = new Computer("PC-0001", "Apple");
            ComputerCard graphics = new ComputerCard("Graphics", "Matrox");
            ComputerCard sound = new ComputerCard("Sound", "Creative");
            ComputerCard network = new ComputerCard("Network", "Intel");
            apple.getCards().add(graphics);
            apple.getCards().add(sound);
            apple.getCards().add(network);
            OperatingSystem macosx = new OperatingSystem("MacOSX", "10");
            apple.setOperatingSystem(macosx);
            Computer sunfire = new Computer("PC-9999", "Sun Fire");
            OperatingSystem solaris = new OperatingSystem("Solaris", "8");
            sunfire.setOperatingSystem(solaris);
            daffyDuck.getComputers().add(apple);
            daffyDuck.getComputers().add(sunfire);
    
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();
    
            // update
            detachedDaffyDuck.getContactData().getAddress().setZip(23456);
    
            // attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
    
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(1, daffyDuck.getNotebook().getCards().size());
            assertEquals(2, daffyDuck.getComputers().size());
    
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

    public void testUpdateEmbeddedDetached9()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Account account = new Account("dduck", "secret12");
            daffyDuck.setAccount(account);
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            contactData.setPerson(daffyDuck);
            daffyDuck.setContactData(contactData);
    
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
    
            Computer apple = new Computer("PC-0001", "Apple");
            ComputerCard graphics = new ComputerCard("Graphics", "Matrox");
            ComputerCard sound = new ComputerCard("Sound", "Creative");
            ComputerCard network = new ComputerCard("Network", "Intel");
            apple.getCards().add(graphics);
            apple.getCards().add(sound);
            apple.getCards().add(network);
            OperatingSystem macosx = new OperatingSystem("MacOSX", "10");
            apple.setOperatingSystem(macosx);
            Computer sunfire = new Computer("PC-9999", "Sun Fire");
            OperatingSystem solaris = new OperatingSystem("Solaris", "8");
            sunfire.setOperatingSystem(solaris);
            daffyDuck.getComputers().add(apple);
            daffyDuck.getComputers().add(sunfire);
    
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();
    
            detachedDaffyDuck.getNotebook().setName("Lenovo");
            detachedDaffyDuck.getNotebook().setOperatingSystem(new OperatingSystem("Ubuntu", "9.04"));
            detachedDaffyDuck.getNotebook().getCards().iterator().next().setDescription("USB Stick");
            // JDOHelper.makeDirty(detachedDaffyDuck, "notebook");
            // JDOHelper.makeDirty(detachedDaffyDuck.getNotebook(), "operatingSystem");
    
            // attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("Lenovo", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Ubuntu", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("9.04", daffyDuck.getNotebook().getOperatingSystem().getVersion());
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
     * Test for detaching/attaching an nested embedded PC object.
     * @throws Exception
     */
    public void testNestedEmbeddedPCObjectDetachAttach() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist some objects
            Object cameraId = null;
            try
            {
                tx.begin();
                Memory memory = new Memory(Memory.COMPACT_FLASH, 64, 3.3);
                memory.setChip(new Chip(12));
                DigitalCamera camera = new DigitalCamera("Canon", "Powerzoom A40", memory);
                pm.makePersistent(camera);

                tx.commit();
                cameraId = pm.getObjectId(camera);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while creating objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object(s) and detach them
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            DigitalCamera detachedCamera = null;
            try
            {
                tx.begin();

                // Retrieve the object with both embedded subobjects
                DigitalCamera camera = (DigitalCamera) pm.getObjectById(cameraId);
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field", camera.getMake().equals("Canon"));
                assertTrue("Retrieved object with embedded object(s) has no memory", camera.getMemory() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", camera
                        .getMemory().getType() == Memory.COMPACT_FLASH);
                assertTrue("Retrieved object with embedded object(s) has no chip", camera.getMemory().getChip() != null);
                assertEquals("Retrieved chip has wrong thickness", 12, camera.getMemory().getChip().getThickness());

                detachedCamera = (DigitalCamera) pm.detachCopy(camera);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while retrieving/detaching objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the detached object(s) and check them
            assertNotNull("Detached Computer is null!", detachedCamera);
            assertTrue("Unable to retrieve object with embedded object(s)", detachedCamera != null);
            assertTrue("Retrieved object with embedded object(s) has incorrect make field", detachedCamera.getMake().equals("Canon"));
            assertTrue("Retrieved object with embedded object(s) has no memory", detachedCamera.getMemory() != null);
            assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", detachedCamera
                    .getMemory().getType() == Memory.COMPACT_FLASH);
            assertTrue("Retrieved object with embedded object(s) has no chip", detachedCamera.getMemory().getChip() != null);
            assertEquals("Retrieved chip has wrong thickness", 12, detachedCamera.getMemory().getChip().getThickness());

            // Update some objects
            detachedCamera.getMemory().getChip().setThickness(15);

            // Attach the objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            try
            {
                tx.begin();

                DigitalCamera camera = (DigitalCamera) pm.makePersistent(detachedCamera);
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field", camera.getMake().equals("Canon"));
                assertTrue("Retrieved object with embedded object(s) has no memory", camera.getMemory() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", camera
                        .getMemory().getType() == Memory.COMPACT_FLASH);
                assertTrue("Retrieved object with embedded object(s) has no chip", camera.getMemory().getChip() != null);
                assertEquals("Retrieved chip has wrong thickness", 15, camera.getMemory().getChip().getThickness());

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while attaching objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object(s) and re-check updated objects
            DataStoreCache cache = pmf.getDataStoreCache();
            cache.evictAll();
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            try
            {
                tx.begin();
                DigitalCamera camera = (DigitalCamera) pm.getObjectById(cameraId);
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field", camera.getMake().equals("Canon"));
                assertTrue("Retrieved object with embedded object(s) has no memory", camera.getMemory() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", camera
                        .getMemory().getType() == Memory.COMPACT_FLASH);
                assertTrue("Retrieved object with embedded object(s) has no chip", camera.getMemory().getChip() != null);
                assertEquals("Retrieved chip has wrong thickness", 15, camera.getMemory().getChip().getThickness());
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while retrieving/detaching objects with embedded field(s) : " + e.getMessage());
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
            // Clean out created data
            clean(DigitalCamera.class);
        }
    }

    public void testUpdateOfEmbedded1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            // assertTrue(JDOHelper.isDirty(daffyDuck));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getAccount()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData().getAddress()));

            // update values
            daffyDuck.getContactData().getPhoneNumbers().add("+49-000-000-000");
            tx.commit();
            pm.close();

            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals(3, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-000-000-000"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

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

    public void testUpdateOfEmbedded2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());
    

            // assertTrue(JDOHelper.isDirty(daffyDuck));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getAccount()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData().getAddress()));

            // update values
            address = daffyDuck.getContactData().getAddress();
            address.setZip(23456);
            address.setStreet("D-Street2");
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street2", daffyDuck.getContactData().getAddress().getStreet());
    
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

    public void testUpdateOfEmbedded3()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());
    
            // update values
            daffyDuck.setAccount(null);
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNull(daffyDuck.getAccount());
    
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

    
    public void testUpdateOfEmbedded4()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());
    

            // assertTrue(JDOHelper.isDirty(daffyDuck));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getAccount()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData()));
            // assertTrue(JDOHelper.isDirty(daffyDuck.getContactData().getAddress()));

            // update values
            daffyDuck.getContactData().setAddress(new Address(23456, "D-City2", "D-Street2"));
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City2", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street2", daffyDuck.getContactData().getAddress().getStreet());
    
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

    public void testUpdateOfHierarchicalEmbedded1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(0, daffyDuck.getComputers().size());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

            // update
            daffyDuck.getNotebook().getOperatingSystem().setVersion("XP");
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("XP", daffyDuck.getNotebook().getOperatingSystem().getVersion());

            // verify non-updated values
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals(0, daffyDuck.getComputers().size());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

    
    public void testUpdateOfHierarchicalEmbedded2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
    
            // update
            daffyDuck.getNotebook().setOperatingSystem(new OperatingSystem("Ubuntu", "9.04"));
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Ubuntu", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("9.04", daffyDuck.getNotebook().getOperatingSystem().getVersion());

            // verify non-updated values
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertEquals(0, daffyDuck.getComputers().size());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

    public void testUpdateOfHierarchicalEmbedded3()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals("Fritz", daffyDuck.getNotebook().getCards().iterator().next().getDescription());
    
            // update
            Set<ComputerCard> cards = daffyDuck.getNotebook().getCards();
            ComputerCard graphics = new ComputerCard("Graphics", "Matrox");
            cards.add(graphics);
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals(2, daffyDuck.getNotebook().getCards().size());
    
            // verify non-upated values
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(0, daffyDuck.getComputers().size());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

    public void testUpdateOfHierarchicalEmbedded4()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            Notebook notebook = new Notebook("M-0001", "IBM", daffyDuck);
            ComputerCard isdnCard = new ComputerCard("ISDN", "Fritz");
            notebook.getCards().add(isdnCard);
            OperatingSystem os = new OperatingSystem("Windows", "Vista");
            notebook.setOperatingSystem(os);
            daffyDuck.setNotebook(notebook);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertNotNull(daffyDuck.getNotebook());
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals("Fritz", daffyDuck.getNotebook().getCards().iterator().next().getDescription());

            // update
            Set<ComputerCard> cards = daffyDuck.getNotebook().getCards();
            ComputerCard card = cards.iterator().next();
            card.setDescription("Hans");
            tx.commit();
            pm.close();

            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals(1, daffyDuck.getNotebook().getCards().size());
            assertEquals("Hans", daffyDuck.getNotebook().getCards().iterator().next().getDescription());

            // verify non-upated values
            assertEquals("IBM", daffyDuck.getNotebook().getName());
            assertEquals("M-0001", daffyDuck.getNotebook().getSerialNumber());
            assertNotNull(daffyDuck.getNotebook().getOperatingSystem());
            assertEquals("Windows", daffyDuck.getNotebook().getOperatingSystem().getName());
            assertEquals("Vista", daffyDuck.getNotebook().getOperatingSystem().getVersion());
            assertEquals(0, daffyDuck.getComputers().size());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

    public void testUpdateOfHierarchicalEmbedded5()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            daffyDuck.getComputers().add(new Computer("PC-1234", "IBM"));
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals(1, daffyDuck.getComputers().size());
            assertEquals("IBM", daffyDuck.getComputers().iterator().next().getName());
            assertEquals(0, daffyDuck.getComputers().iterator().next().getCards().size());
    
            // update
            daffyDuck.getComputers().iterator().next().getCards().add(new ComputerCard("Sound", "Creative"));
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals(1, daffyDuck.getComputers().size());
            assertEquals("IBM", daffyDuck.getComputers().iterator().next().getName());
            assertEquals(1, daffyDuck.getComputers().iterator().next().getCards().size());
            assertEquals("Sound", daffyDuck.getComputers().iterator().next().getCards().iterator().next().getName());
            assertEquals("Creative", daffyDuck.getComputers().iterator().next().getCards().iterator().next().getDescription());

            // verify non-updated values
            assertNull(daffyDuck.getNotebook());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

    public void testUpdateOfHierarchicalEmbedded6()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            daffyDuck.getComputers().add(new Computer("PC-1234", "IBM"));
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();
    
            // verify
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals(1, daffyDuck.getComputers().size());
            assertEquals("IBM", daffyDuck.getComputers().iterator().next().getName());
    
            // update
            daffyDuck.getComputers().iterator().next().setName("Lenovo");
            tx.commit();
            pm.close();
    
            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals(1, daffyDuck.getComputers().size());
            assertEquals("Lenovo", daffyDuck.getComputers().iterator().next().getName());
    
            // verify non-updated values
            assertNull(daffyDuck.getNotebook());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());

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

}
