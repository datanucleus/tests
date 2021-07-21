/**********************************************************************
Copyright (c) 2014 Andy Jefferson and others. All rights reserved.
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

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.embedded.Chip;
import org.datanucleus.samples.embedded.DigitalCamera;
import org.datanucleus.samples.embedded.Memory;

/**
 * Simple tests for embedded fields.
 */
public class EmbeddedTest extends JSONTestCase
{
    private static boolean initialised = false;

    public EmbeddedTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    DigitalCamera.class,
                    Memory.class,
                    Chip.class
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
}