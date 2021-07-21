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
2005 Eddy Young - embedded-only JDOQL test
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.embedded.Device;
import org.datanucleus.samples.embedded.Film;
import org.datanucleus.samples.embedded.FilmLibrary;
import org.datanucleus.samples.embedded.Job;
import org.datanucleus.samples.embedded.Network;
import org.datanucleus.samples.embedded.Processor;
import org.datanucleus.store.StoreManager;

/**
 * Tests for persistence of embedded collection/map fields.
 */
public class EmbeddedContainerTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public EmbeddedContainerTest(String name)
    {
        super(name);
        if (!initialised)
        {
            if (storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_COLLECTION))
            {
                addClassesToSchema(new Class[] {Network.class, Device.class, Processor.class, Job.class});
            }
            if (storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
            {
                addClassesToSchema(new Class[] {FilmLibrary.class, Film.class});
            }
            initialised = true;
        }
    }

    /**
     * Test for an embedded PC object.
     * @throws Exception
     */
    public void testEmbeddedCollection() 
    throws Exception
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_COLLECTION))
        {
            return;
        }

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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_COLLECTION))
        {
            return;
        }

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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
     * Test for an embedded PC object in a List.
     * @throws Exception
     */
    public void testEmbeddedList() 
    throws Exception
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_COLLECTION))
        {
            return;
        }

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded list -----------------
            Object processorId = null;
            try
            {
                tx.begin();
                
                Processor proc = new Processor("Intel Pentium 2 - 300MHz");
                Job job1 = new Job("MS Outlook", 1);
                Job job2 = new Job("Yahoo Messenger", 2);
                Job job3 = new Job("Mozilla Firefox", 5);
                proc.addJob(job1);
                proc.addJob(job2);
                proc.addJob(job3);
                pm.makePersistent(proc);
                
                // Access the object containing the embedded object before commit
                // This tries to go to the DB if our object isn't marked as embedded.
                assertEquals("Number of devices is not correct", 3, proc.getNumberOfJobs());
                
                tx.commit();
                processorId = pm.getObjectId(proc);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
            // Retrieve the Processor and the jobs
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Processor proc = (Processor)pm.getObjectById(processorId);

                // Check retrieval of jobs
                List jobs = proc.getJobs();
                assertTrue("No jobs retrieved from calling getJobs()!", jobs != null);
                assertEquals("Number of jobs retrieved from calling getJobs() is incorrect", 3, jobs.size());
                
                Job first = (Job)jobs.get(0);
                Job second = (Job)jobs.get(1);
                Job third = (Job)jobs.get(2);
                assertTrue("First job should be MS Outlook but isnt", first.getName().equals("MS Outlook"));
                assertTrue("Second job should be Yahoo Messenger but isnt", second.getName().equals("Yahoo Messenger"));
                assertTrue("Third job should be Mozilla FIrefox but isnt", third.getName().equals("Mozilla Firefox"));
                assertTrue("First job should have priority 1 but hasnt", first.getPriority() == 1);
                assertTrue("Second job should have priority 2 but hasnt", second.getPriority() == 2);
                assertTrue("Third job should have priority 5 but hasnt", third.getPriority() == 5);
                
                // Remove the "MS Outlook" job since it crashed :-)
                proc.removeJob(first);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
            // Check that the MS Outlook job is removed from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Processor proc = (Processor)pm.getObjectById(processorId);
                
                // Check "contains"
                assertTrue("Processor says that it contains the MS Outlook job but it shouldnt", !proc.containsJob(new Job("MS Outlook", 1)));
                
                // Check retrieval of jobs
                List jobs = proc.getJobs();
                assertTrue("No jobs retrieved from calling getJobs()!", jobs != null);
                assertEquals("Number of jobs retrieved from calling getJobs() is incorrect", 2, jobs.size());
                
                Job first = (Job)jobs.get(0);
                Job second = (Job)jobs.get(1);
                assertTrue("First job should have been Yahoo Messenger but wasnt", first.getName().equals("Yahoo Messenger"));
                assertTrue("Second job should have been Mozilla FIrefox but wasnt", second.getName().equals("Mozilla Firefox"));
                assertTrue("First job should have priority 2 but hasnt", first.getPriority() == 2);
                assertTrue("Second job should have priority 5 but hasnt", second.getPriority() == 5);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            // Clean out created data
            clean(Processor.class);
        }
    }

    /**
     * Test for Map with embedded value object.
     * @throws Exception
     */
    public void testEmbeddedMap()
    throws Exception
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_ORM_EMBEDDED_MAP))
        {
            return;
        }

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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                film.setName("Star Wars Episode IV : A New Hope");
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                
                pm.getFetchPlan().addGroup("film_all");
                FilmLibrary library = (FilmLibrary)pm.getObjectById(libraryId);
                
                // Check retrieval of films
                Collection films = library.getFilms();
                assertTrue("No films retrieved from calling getFilms()!", films != null);
                assertEquals("Number of films retrieved from calling getFilms() is incorrect", 2, films.size());
                
                Film film3 = library.getFilm("Star Wars");
                assertTrue("Star Wars was not returned by getFilm!", film3 != null);
                assertEquals("Star Wars has wrong name", "Star Wars Episode IV : A New Hope", film3.getName());
                assertEquals("Star Wars has wrong director", "George Lucas", film3.getDirector());
                
                detachedLibrary = (FilmLibrary)pm.detachCopy(library);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
}