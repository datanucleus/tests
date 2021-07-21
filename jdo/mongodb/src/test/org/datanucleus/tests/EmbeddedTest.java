/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.embedded.Chip;
import org.datanucleus.samples.embedded.Device;
import org.datanucleus.samples.embedded.DigitalCamera;
import org.datanucleus.samples.embedded.Film;
import org.datanucleus.samples.embedded.FilmLibrary;
import org.datanucleus.samples.embedded.Memory;
import org.datanucleus.samples.embedded.Network;
import org.datanucleus.tests.embedded.LibraryUser;
import org.datanucleus.tests.embedded.Video;
import org.datanucleus.tests.embedded.ViewedVideo;

/**
 * Simple tests for embedded fields.
 */
public class EmbeddedTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public EmbeddedTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    DigitalCamera.class,
                    Network.class,
                    Device.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of nested embedded PC objects.
     * @throws Exception
     */
    public void testNestedEmbeddedPCObjects() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // ------------------ Check the persistence of an object with embedded object -----------------
            Object cameraId = null;
            try
            {
                tx.begin();
                
                Memory memory = new Memory(Memory.COMPACT_FLASH, 64, 3.3);
                memory.setChip(new Chip(12));
                DigitalCamera camera = new DigitalCamera("Canon", "Powerzoom A40", memory);
                camera.setId(245);
                
                pm.makePersistent(camera);
                
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                camera.getMemory().toString();
                
                tx.commit();
                cameraId = pm.getObjectId(camera);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
            
            // -------------- Check the retrieval of objects with embedded subobject -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object with both embedded subobjects
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                assertNotNull("Unable to retrieve object with embedded object(s)", camera);
                assertEquals("Retrieved object with embedded object(s) has incorrect make field",
                    "Canon", camera.getMake());
                assertNotNull("Retrieved object with embedded object(s) has no memory", camera.getMemory());
                assertEquals("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", 
                    Memory.COMPACT_FLASH, camera.getMemory().getType());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while fetching objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // ------------------- Check update of an embedded object ------------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object and change its memory
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                Memory memory = new Memory(Memory.COMPACT_FLASH, 256, 3.3);
                memory.setChip(new Chip(15));
                camera.setMemory(memory);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf.getDataStoreCache().evictAll();
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object that has just been updated
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                assertNotNull("Unable to retrieve object with embedded object(s)", camera);
                assertEquals("Updated object with embedded object(s) has incorrect model field", 
                    "Powerzoom A40", camera.getModel());
                assertNotNull("Updated object with embedded object(s) has no memory", camera.getMemory());
                assertEquals("Updated object with embedded object(s) has incorrect embedded object : memory size is wrong", 
                    256, camera.getMemory().getSize());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating objects with embedded field(s) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // ------------- Check for updates in the embedded object ------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the object and update its battery details
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                Memory memory = camera.getMemory();
                memory.setVoltage(5.0);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating embedded objects : " + e.getMessage());
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
                
                // Retrieve the object that has just been updated
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                assertNotNull("Unable to retrieve object with embedded object(s)", camera);
                assertEquals("Updated object with embedded object(s) has incorrect model field", 
                    "Powerzoom A40", camera.getModel());
                assertNotNull("Updated object with embedded object(s) has no memory", camera.getMemory());
                assertEquals("Updated object with embedded object(s) has incorrect embedded object : memory voltage is wrong", 
                    5.0, camera.getMemory().getVoltage());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating embedded objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // ------------- Check for updates in the nested embedded object ------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object and update its battery details
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                Chip chip = camera.getMemory().getChip();
                chip.setThickness(6);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating nested embedded objects : " + e.getMessage());
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

                // Retrieve the object that has just been updated
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                assertNotNull("Unable to retrieve object with embedded object(s)", camera);
                assertEquals("Updated object with embedded object(s) has incorrect model field", 
                    "Powerzoom A40", camera.getModel());
                assertNotNull("Updated object with embedded object(s) has no memory", camera.getMemory());
                assertNotNull("Updated object with embedded object(s) has no chip", camera.getMemory().getChip());
                assertEquals("Updated object with embedded object(s) has incorrect nested embedded object : chip thickness is wrong", 
                    6, camera.getMemory().getChip().getThickness());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating embedded objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // -------------- Check the retrieval of objects with nested embedded subobject with query -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LOG.info(">> query of nested object value");
                Query query = pm.newQuery("SELECT FROM org.datanucleus.samples.embedded.DigitalCamera WHERE memory.chip.thickness == 6");
                List results = (List) query.execute();
                assertEquals("Number of cameras retrieved by query of nested embedded is incorrect", results.size(), 1);
                DigitalCamera camera = (DigitalCamera) results.iterator().next();
                
                assertNotNull("Unable to retrieve object with nested embedded object(s)", camera != null);
                assertEquals("Retrieved object with nested embedded object(s) has incorrect make field", "Canon", camera.getMake());
                assertNotNull("Retrieved object with nested embedded object(s) has no memory", camera.getMemory());
                assertNotNull("Retrieved object with nested embedded object(s) has no memory chip", camera.getMemory().getChip());
                assertEquals("Retrieved object with nested embedded object(s) has incorrect memory chip thickness", 
                    6, camera.getMemory().getChip().getThickness());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while querying objects with nested embedded field(s) : " + e.getMessage());
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

    /**
     * Test for detaching/attaching an nested embedded PC object.
     * @throws Exception
     */
    public void testNestedEmbeddedPCObjectDetachAttach()
    throws Exception
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
                camera.setId(245);
                pm.makePersistent(camera);
    
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                // camera.getMemory().toString();
    
                tx.commit();
                cameraId = pm.getObjectId(camera);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();

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
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", camera.getMemory().getType() == Memory.COMPACT_FLASH);
                assertTrue("Retrieved object with embedded object(s) has no chip", camera.getMemory().getChip() != null);
                assertEquals("Retrieved chip has wrong thickness", 12, camera.getMemory().getChip().getThickness());
    
                detachedCamera = (DigitalCamera) pm.detachCopy(camera);
    
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
    
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
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
    
            // Retrieve the object(s) and re-check updated objects
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
                LOG.error(">> Exception in test", e);
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

    /**
     * Test for an embedded PC object.
     * @throws Exception
     */
    public void testEmbeddedCollection() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded collection -----------------
            Object networkId = null;
            try
            {
                tx.begin();
                
                Network network = new Network("Local Area Net");
                Device device1 = new Device("Printer", "192.168.0.2", "HP LaserJet", network);
                Device device2 = new Device("Audio Server", "192.168.0.3", "SLiMP3", network);
                Device device3 = new Device("Mail Server", "192.168.0.4", "Office IMAP", network);
                network.addDevice(device1);
                network.addDevice(device2);
                network.addDevice(device3);
                pm.makePersistent(network);
                
                // Access the object containing the embedded object before commit
                // This tries to go to the DB if our object isn't marked as embedded.
                assertEquals("Number of devices is not correct", 3, network.getNumberOfDevices());
                
                tx.commit();
                networkId = pm.getObjectId(network);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
            
            // Retrieve the Network and the devices
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Network net = (Network)pm.getObjectById(networkId);

                // Check "contains"
                assertTrue("Network says that it doesnt contain the Audio Server but it should", net.containsDevice(new Device("Audio Server", "192.168.0.3", "SLiMP3", null)));
                assertFalse("Network says that it contains the Printer on an incorrect IP address but doesnt", net.containsDevice(new Device("Printer", "192.168.0.3", "HP LaserJet", null)));
                
                // Check retrieval of devices
                Device[] devices = net.getDevices();
                assertTrue("No devices retrieved from calling getDevices()!", devices != null);
                assertEquals("Number of devices retrieved from calling getDevices() is incorrect", 3, devices.length);
                
                Device printerDevice = null;
                Device audioDevice = null;
                Device mailDevice = null;
                for (int i=0;i<devices.length;i++)
                {
                    Device dev = devices[i];
                    if (dev.getName().equals("Audio Server"))
                    {
                        audioDevice = dev;
                    }
                    else if (dev.getName().equals("Printer"))
                    {
                        printerDevice = dev;
                    }
                    else if (dev.getName().equals("Mail Server"))
                    {
                        mailDevice = dev;
                    }
                }
                assertTrue("Audio device was not returned by getDevices!", audioDevice != null);
                assertTrue("Printer device was not returned by getDevices!", printerDevice != null);
                assertTrue("Mail device was not returned by getDevices!", mailDevice != null);
                assertEquals("Audio Server has incorrect IP address", "192.168.0.3", audioDevice.getIPAddress());
                assertEquals("Printer has incorrect IP address", "192.168.0.2", printerDevice.getIPAddress());
                assertEquals("Mail Server has incorrect IP address", "192.168.0.4", mailDevice.getIPAddress());
                assertTrue("Audio device has no owner network assigned", audioDevice.getNetwork() != null);
                assertEquals("Audio device has incorrect network assigned", "Local Area Net", audioDevice.getNetwork().getName());
                
                // Remove the "Mail Server"
                net.removeDevice(mailDevice);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the mail server is removed from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Network net = (Network)pm.getObjectById(networkId);
                
                // Check "contains"
                assertTrue("Network says that it contains the Mail Server but it shouldnt", !net.containsDevice(new Device("Mail Server", "192.168.0.4", "Office IMAP", null)));
                
                // Check retrieval of devices
                Device[] devices = net.getDevices();
                assertTrue("No devices retrieved from calling getDevices()!", devices != null);
                assertEquals("Number of devices retrieved from calling getDevices() is incorrect", 2, devices.length);
                
                Device printerDevice = null;
                Device audioDevice = null;
                for (int i=0;i<devices.length;i++)
                {
                    Device dev = devices[i];
                    if (dev.getName().equals("Audio Server"))
                    {
                        audioDevice = dev;
                    }
                    else if (dev.getName().equals("Printer"))
                    {
                        printerDevice = dev;
                    }
                }
                assertTrue("Audio device was not returned by getDevices!", audioDevice != null);
                assertTrue("Printer device was not returned by getDevices!", printerDevice != null);
                assertEquals("Audio Server has incorrect IP address", "192.168.0.3", audioDevice.getIPAddress());
                assertEquals("Printer has incorrect IP address", "192.168.0.2", printerDevice.getIPAddress());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Add a device to the retrieved Network
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Network net = (Network)pm.getObjectById(networkId);
                
                // Add a "DB server"
                Device dbServer = new Device("DB Server", "192.168.0.10", "MySQL 4.0", net);
                net.addDevice(dbServer);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while adding object to embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the DB server is added to the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Network net = (Network)pm.getObjectById(networkId);
                
                // Check retrieval of devices
                Device[] devices = net.getDevices();
                assertTrue("No devices retrieved from calling getDevices()!", devices != null);
                assertEquals("Number of devices retrieved from calling getDevices() is incorrect", 3, devices.length);
                
                Device dbDevice = null;
                for (int i=0;i<devices.length;i++)
                {
                    Device dev = devices[i];
                    if (dev.getName().equals("DB Server"))
                    {
                        dbDevice = dev;
                    }
                }
                assertTrue("DB Server was not returned by getDevices!", dbDevice != null);
                assertEquals("DB Server has incorrect IP address", "192.168.0.10", dbDevice.getIPAddress());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Update an embedded element and check that the update gets to the DB
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Network net = (Network)pm.getObjectById(networkId);
                Device[] devices = net.getDevices();
                Device audioDevice = null;
                for (int i=0;i<devices.length;i++)
                {
                    Device dev = devices[i];
                    if (dev.getName().equals("Audio Server"))
                    {
                        audioDevice = dev;
                        break;
                    }
                }
                
                // Change the IP address of our printer
                audioDevice.setIPAddress("192.168.1.20");
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating embedded objects stored in container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the audio server has been updated in the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Network net = (Network)pm.getObjectById(networkId);
                Device[] devices = net.getDevices();
                assertTrue("No devices retrieved from calling getDevices()!", devices != null);
                assertEquals("Number of devices retrieved from calling getDevices() is incorrect", 3, devices.length);
                
                Device printerDevice = null;
                Device audioDevice = null;
                Device dbDevice = null;
                for (int i=0;i<devices.length;i++)
                {
                    Device dev = devices[i];
                    if (dev.getName().equals("Audio Server"))
                    {
                        audioDevice = dev;
                    }
                    else if (dev.getName().equals("Printer"))
                    {
                        printerDevice = dev;
                    }
                    else if (dev.getName().equals("DB Server"))
                    {
                        dbDevice = dev;
                    }
                }
                assertTrue("Audio device was not returned by getDevices!", audioDevice != null);
                assertTrue("Printer device was not returned by getDevices!", printerDevice != null);
                assertTrue("DB device was not returned by getDevices!", dbDevice != null);
                assertEquals("Audio Server has incorrect IP address", "192.168.1.20", audioDevice.getIPAddress());
                assertEquals("Printer has incorrect IP address", "192.168.0.2", printerDevice.getIPAddress());
                assertEquals("DB Server has incorrect IP address", "192.168.0.10", dbDevice.getIPAddress());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving updated objects with embedded container : " + e.getMessage());
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
            clean(Network.class);
        }
    }

    /**
     * Test for query of an embedded PC collection object.
     * @throws Exception
     */
    public void testEmbeddedCollectionQuery() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded collection -----------------
            
            try
            {
                tx.begin();
                
                Network network = new Network("Local Area Net");
                Device device1 = new Device("Printer", "192.168.0.2", "HP LaserJet", network);
                Device device2 = new Device("Audio Server", "192.168.0.3", "SLiMP3", network);
                Device device3 = new Device("Mail Server", "192.168.0.4", "Office IMAP", network);
                network.addDevice(device1);
                network.addDevice(device2);
                network.addDevice(device3);
                pm.makePersistent(network);
                
                // Access the object containing the embedded object before commit
                // This tries to go to the DB if our object isn't marked as embedded.
                assertEquals("Number of devices is not correct", 3, network.getNumberOfDevices());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
            
            // Basic query of all Networks
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Query q = pm.newQuery(Network.class);
                List results = (List)q.execute();
                
                assertTrue("No networks retrieved from query of all Networks!", results != null);
                assertEquals("Number of networks retrieved from Query is incorrect", 1, results.size());
                
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    Network net = (Network)resultsIter.next();
                    
                    Device[] devices = net.getDevices();
                    assertTrue("No devices retrieved from retrieved network)!", devices != null);
                    assertEquals("Number of devices retrieved from retrieved network is incorrect", 3, devices.length);
                    
                    Device printerDevice = null;
                    Device audioDevice = null;
                    Device mailDevice = null;
                    for (int i=0;i<devices.length;i++)
                    {
                        Device dev = devices[i];
                        if (dev.getName().equals("Audio Server"))
                        {
                            audioDevice = dev;
                        }
                        else if (dev.getName().equals("Printer"))
                        {
                            printerDevice = dev;
                        }
                        else if (dev.getName().equals("Mail Server"))
                        {
                            mailDevice = dev;
                        }
                    }
                    assertTrue("Audio device was not returned by getDevices!", audioDevice != null);
                    assertTrue("Printer device was not returned by getDevices!", printerDevice != null);
                    assertTrue("Mail device was not returned by getDevices!", mailDevice != null);
                    assertEquals("Audio Server has incorrect IP address", "192.168.0.3", audioDevice.getIPAddress());
                    assertEquals("Printer has incorrect IP address", "192.168.0.2", printerDevice.getIPAddress());
                    assertEquals("Mail Server has incorrect IP address", "192.168.0.4", mailDevice.getIPAddress());
                    assertTrue("Audio device has no owner network assigned", audioDevice.getNetwork() != null);
                    assertEquals("Audio device has incorrect network assigned", "Local Area Net", audioDevice.getNetwork().getName());
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while querying objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Query of Collection.contains() with embedded elements
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Query q = pm.newQuery(Network.class, "devices.contains(elem) && elem.name == \"Audio Server\"");
                q.declareVariables("org.datanucleus.samples.embedded.Device elem");
                List results = (List)q.execute();
                
                assertTrue("No networks retrieved from query of Networks containing audio server!", results != null);
                assertEquals("Number of networks retrieved from Query is incorrect", 1, results.size());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while querying objects with embedded container : " + e.getMessage());
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
            clean(Network.class);
        }
    }

    /**
     * Test for Map with embedded value object.
     * @throws Exception
     */
    public void testEmbeddedMap()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with an embedded map -----------------
            Object libraryId = null;
            try
            {
                tx.begin();
                
                Film film1 = new Film("ET", "Steven Spielberg", "Extra-terrestrial nonsense");
                Film film2 = new Film("Los diarios del motociclista", "Walter Salles", "Journey of Che Guevara");
                FilmLibrary library = new FilmLibrary("Mr Blockbuster");
                library.addFilm("ET", film1);
                library.addFilm("Motorcycle Diaries", film2);
                
                pm.makePersistent(library);
                
                // Access the object containing the embedded object before commit
                // This tries to go to the DB if our object isn't marked as embedded.
                assertEquals("Number of films is not correct", 2, library.getNumberOfFilms());
                
                tx.commit();
                libraryId = pm.getObjectId(library);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
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
            pmf.getDataStoreCache().evictAll();
            
            // Retrieve the Library and the films
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check "contains"
                assertTrue("Library says that it doesnt contain the ET but it should", library.containsFilm("ET"));
                assertFalse("Library says that it contains Independence Day but doesnt", library.containsFilm("Independence Day"));
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 2, films.size());
                
                Film film1 = library.getFilm("ET");
                assertTrue("ET was not returned by getFilm!", film1 != null);
                assertEquals("ET has wrong name", "ET", film1.getName());
                assertEquals("ET has wrong director", "Steven Spielberg", film1.getDirector());
                Film film2 = library.getFilm("Motorcycle Diaries");
                assertTrue("Motorcycle Diaries was not returned by getFilm!", film2 != null);
                assertEquals("Motorcycle Diaries has wrong name", "Los diarios del motociclista", film2.getName());
                assertEquals("Motorcycle Diaries has wrong director", "Walter Salles", film2.getDirector());

                // Remove "ET" since nobody wants to see it :-)
                library.removeFilm("ET");
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the film was removed
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 1, films.size());
                
                Film film1 = library.getFilm("ET");
                assertTrue("ET was returned by getFilm!", film1 == null);
                Film film2 = library.getFilm("Motorcycle Diaries");
                assertTrue("Motorcycle Diaries was not returned by getFilm!", film2 != null);
                assertEquals("Motorcycle Diaries has wrong name", "Los diarios del motociclista", film2.getName());
                assertEquals("Motorcycle Diaries has wrong director", "Walter Salles", film2.getDirector());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Add a new film to the library
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                Film film3 = new Film("Star Wars Episode IV", "George Lucas", "War between the Jedi and the Sith");
                library.addFilm("Star Wars", film3);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while adding objects to embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the film was added
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 2, films.size());
                
                Film film2 = library.getFilm("Motorcycle Diaries");
                assertTrue("Motorcycle Diaries was not returned by getFilm!", film2 != null);
                assertEquals("Motorcycle Diaries has wrong name", "Los diarios del motociclista", film2.getName());
                assertEquals("Motorcycle Diaries has wrong director", "Walter Salles", film2.getDirector());
                
                Film film3 = library.getFilm("Star Wars");
                assertTrue("Star Wars was not returned by getFilm!", film3 != null);
                assertEquals("Star Wars has wrong name", "Star Wars Episode IV", film3.getName());
                assertEquals("Star Wars has wrong director", "George Lucas", film3.getDirector());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Update a film in the library
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                Film film = library.getFilm("Star Wars");
                LOG.info(">> Updating name of film StarWars state=" + JDOHelper.getObjectState(film));
                film.setName("Star Wars Episode IV : A New Hope");
                LOG.info(">> Updated name of film StarWars state=" + JDOHelper.getObjectState(film));
                
                tx.commit();
                LOG.info(">> Updated name of film StarWars ?");
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while updating objects in embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the film was updated
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            FilmLibrary detachedLibrary = null;
            try
            {
                tx.begin();
                LOG.info(">> Retrieving films");
                pm.getFetchPlan().addGroup("film_all");
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 2, films.size());

                LOG.info(">> Correctly retrieved 2 films");
                Film film3 = library.getFilm("Star Wars");
                assertTrue("Star Wars was not returned by getFilm!", film3 != null);
                assertEquals("Star Wars has wrong name", "Star Wars Episode IV : A New Hope", film3.getName());
                assertEquals("Star Wars has wrong director", "George Lucas", film3.getDirector());
                
                detachedLibrary = (FilmLibrary)pm.detachCopy(library);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Update the detached object, adding a new film
            detachedLibrary.addFilm("Kamchatka", new Film("Kamchatka", "Marcelo Py�eiro", "Darkest part of Argentinian history told through the eyeys of a child"));
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.makePersistent(detachedLibrary);
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms() on attached object!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() on attached object is incorrect", 3, films.size());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while attaching objects with embedded container : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the film was attached
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 3, films.size());
                
                Film film3 = library.getFilm("Kamchatka");
                assertTrue("Kamchatka was not returned by getFilm!", film3 != null);
                assertEquals("Kamchatka has wrong name", "Kamchatka", film3.getName());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception in test", e);
                fail("Exception thrown while retrieving objects with embedded container : " + e.getMessage());
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
            clean(FilmLibrary.class);
        }
    }

    public void testEmbeddedCollectionWithReferenceToUnembedded()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Video vid1 = new Video();
                vid1.setIdVideo(456L);
                vid1.setVideoTitle("Inception");

                Video vid2 = new Video();
                vid2.setIdVideo(789L);
                vid2.setVideoTitle("The Matrix");

                Video vid3 = new Video();
                vid3.setIdVideo(123L);
                vid3.setVideoTitle("Saving Private Ryan");

                LibraryUser user1 = new LibraryUser();
                user1.setName("Andy");
                user1.getViewedVideos().add(new ViewedVideo(vid1));
                user1.getViewedVideos().add(new ViewedVideo(vid2));

                LibraryUser user2 = new LibraryUser();
                user2.setName("Chris");
                user2.getViewedVideos().add(new ViewedVideo(vid1));
                user2.getViewedVideos().add(new ViewedVideo(vid2));
                user2.getViewedVideos().add(new ViewedVideo(vid3));

                //Persist all elements :
                pm.makePersistentAll(user1, user2);

                tx.commit();
            }
            catch (Throwable thr)
            {
                LOG.error(">> Exception in persist", thr);
                fail("Exception in persist of data : " + thr.getMessage());
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

                Query q = pm.newQuery("SELECT FROM " + LibraryUser.class.getName() + " ORDER BY name");
                List<LibraryUser> users = (List<LibraryUser>)q.execute();
                assertEquals("Number of users is wrong", 2, users.size());

                LibraryUser user1 = users.get(0);
                assertNotNull(user1);
                assertEquals("Andy", user1.getName());
                Set<ViewedVideo> vids1 = user1.getViewedVideos();
                assertNotNull(vids1);
                assertEquals(2, vids1.size());
                boolean vid1Present = false;
                boolean vid2Present = false;
                boolean vid3Present = false;
                for (ViewedVideo vid : vids1)
                {
                    if (vid.getVideo().getIdVideo() == 456)
                    {
                        vid1Present = true;
                    }
                    else if (vid.getVideo().getIdVideo() == 789)
                    {
                        vid2Present = true;
                    }
                    else if (vid.getVideo().getIdVideo() == 123)
                    {
                        vid3Present = true;
                    }
                }
                assertTrue(vid1Present);
                assertTrue(vid2Present);
                assertFalse(vid3Present);

                LibraryUser user2 = users.get(1);
                assertNotNull(user2);
                assertEquals("Chris", user2.getName());
                Set<ViewedVideo> vids2 = user2.getViewedVideos();
                assertNotNull(vids2);
                assertEquals(3, vids2.size());
                vid1Present = false;
                vid2Present = false;
                vid3Present = false;
                for (ViewedVideo vid : vids2)
                {
                    if (vid.getVideo().getIdVideo() == 456)
                    {
                        vid1Present = true;
                    }
                    else if (vid.getVideo().getIdVideo() == 789)
                    {
                        vid2Present = true;
                    }
                    else if (vid.getVideo().getIdVideo() == 123)
                    {
                        vid3Present = true;
                    }
                }
                assertTrue(vid1Present);
                assertTrue(vid2Present);
                assertTrue(vid3Present);

                tx.commit();
            }
            catch (Throwable thr)
            {
                LOG.error(">> Exception in query", thr);
                fail("Exception in query of data : " + thr.getMessage());
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

            PersistenceManager pm = pmf.getPersistenceManager();
            Collection<Object> toDelete;

            Query qVideo = pm.newQuery(Video.class);
            Query qUser = pm.newQuery(LibraryUser.class);

            toDelete = new ArrayList<Object>((Collection<Object>) qVideo.execute());
            toDelete.addAll( new ArrayList<Object>((Collection<Object>)qUser.execute()));
            pm.deletePersistentAll(toDelete);

            pm.close();
        }
    }
}