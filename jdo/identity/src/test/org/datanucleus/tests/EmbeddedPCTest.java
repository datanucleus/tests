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

import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.embedded.Bath;
import org.jpox.samples.embedded.Battery;
import org.jpox.samples.embedded.Chip;
import org.jpox.samples.embedded.Computer;
import org.jpox.samples.embedded.ComputerCard;
import org.jpox.samples.embedded.DigitalCamera;
import org.jpox.samples.embedded.FittedBathroom;
import org.jpox.samples.embedded.FittedKitchen;
import org.jpox.samples.embedded.Manufacturer;
import org.jpox.samples.embedded.Memory;
import org.jpox.samples.embedded.MultifunctionOven;
import org.jpox.samples.embedded.MusicPlayer;
import org.jpox.samples.embedded.Oven;
import org.jpox.samples.embedded.ShowerBath;

/**
 * Tests for persistence of embedded PC fields.
 */
public class EmbeddedPCTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public EmbeddedPCTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Computer.class,
                    ComputerCard.class,
                    Manufacturer.class,
                    DigitalCamera.class,
                    MusicPlayer.class,
                    FittedKitchen.class, Oven.class, MultifunctionOven.class,
                    FittedBathroom.class, Bath.class, ShowerBath.class,
                }
            );
            initialised = true;
        }
    }

    /**
     * Test for an embedded PC object.
     * @throws Exception
     */
    public void testEmbeddedPCObject() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded objects -----------------
            Object comp_id = null;
            try
            {
                tx.begin();
                
                Computer comp = new Computer("Linux",
                    new ComputerCard("ATI", ComputerCard.AGP_CARD), 
                    new ComputerCard("Creative Labs", ComputerCard.PCI_CARD));
                
                pm.makePersistent(comp);
                
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                comp.getGraphicsCard().toString();
                
                tx.commit();
                comp_id = pm.getObjectId(comp);
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
            
            // -------------- Check the retrieval of objects with embedded subobjects -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object with both embedded subobjects
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect operating system field",
                    comp.getOperatingSystem().equals("Linux"));
                assertTrue("Retrieved object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Embedded graphics card doesn't have its owner field set",
                    comp.getGraphicsCard().getComputer() != null);
                assertTrue("Embedded graphics card has its owner field set incorrectly",
                    comp.getGraphicsCard().getComputer() == comp);
                assertTrue("Retrieved object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Creative Labs"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                assertTrue("Embedded sound card doesn't have its owner field set",
                    comp.getSoundCard().getComputer() != null);
                assertTrue("Embedded sound card has its owner field set incorrectly",
                    comp.getSoundCard().getComputer() == comp);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                
                // Retrieve the object and change its sound card
                Computer comp = (Computer)pm.getObjectById(comp_id);
                comp.setSoundCard(new ComputerCard("Turtle Beach", ComputerCard.PCI_CARD));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Updated object with embedded object(s) has incorrect operating system field", 
                    comp.getOperatingSystem().equals("Linux"));
                assertTrue("Updated object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Updated object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Turtle Beach"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                
                // Retrieve the object and update its sound card details
                Computer comp = (Computer)pm.getObjectById(comp_id);
                ComputerCard sound_card = comp.getSoundCard();
                sound_card.setType(ComputerCard.ISA_CARD);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Updated object with embedded object(s) has incorrect operating system field", 
                    comp.getOperatingSystem().equals("Linux"));
                assertTrue("Updated object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Updated object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Turtle Beach"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.ISA_CARD);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
        }
        finally
        {
            // Clean out created data
            clean(ComputerCard.class);
            clean(Computer.class);
        }
    }

    /**
     * Test for an embedded PC relation to non-embedded
     * @throws Exception
     */
    public void testEmbeddedPCObjectWithLinkToNonEmbedded() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded objects -----------------
            Object comp_id = null;
            try
            {
                tx.begin();

                Manufacturer manu1 = new Manufacturer(1, "ATI Industries");
                Manufacturer manu2 = new Manufacturer(2, "Creative Labs");
                ComputerCard graphicsCard = new ComputerCard("ATI", ComputerCard.AGP_CARD);
                graphicsCard.setManufacturer(manu1);
                ComputerCard soundCard = new ComputerCard("Creative Labs", ComputerCard.PCI_CARD);
                soundCard.setManufacturer(manu2);
                Computer comp = new Computer("Linux", graphicsCard, soundCard);
                pm.makePersistent(comp);

                tx.commit();
                comp_id = pm.getObjectId(comp);
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
            
            // -------------- Check the retrieval of objects with embedded subobjects -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the object with both embedded subobjects
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect operating system field",
                    comp.getOperatingSystem().equals("Linux"));
                assertTrue("Retrieved object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Embedded graphics card doesn't have its owner field set",
                    comp.getGraphicsCard().getComputer() != null);
                assertTrue("Embedded graphics card has its owner field set incorrectly",
                    comp.getGraphicsCard().getComputer() == comp);
                Manufacturer manu1 = comp.getGraphicsCard().getManufacturer();
                assertNotNull("Graphics card manufacturer should not be null", manu1);
                assertEquals("Graphics card manufacturer is wrong", "ATI Industries", manu1.getName());

                assertTrue("Retrieved object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Creative Labs"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                assertTrue("Embedded sound card doesn't have its owner field set",
                    comp.getSoundCard().getComputer() != null);
                assertTrue("Embedded sound card has its owner field set incorrectly",
                    comp.getSoundCard().getComputer() == comp);
                Manufacturer manu2 = comp.getSoundCard().getManufacturer();
                assertNotNull("Graphics card manufacturer should not be null", manu2);
                assertEquals("Graphics card manufacturer is wrong", "Creative Labs", manu2.getName());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
        }
        finally
        {
            // Clean out created data
            clean(ComputerCard.class);
            clean(Computer.class);
            clean(Manufacturer.class);
        }
    }

    /**
     * Test for an embedded PC object, and use of null-value.
     * @throws Exception
     */
    public void testEmbeddedPCObjectNullValue() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded objects -----------------
            Object comp_id = null;
            try
            {
                tx.begin();
                
                Computer comp = new Computer("MacOS",
                    new ComputerCard("NVidia", ComputerCard.AGP_CARD),
                    null);
                
                pm.makePersistent(comp);
                
                tx.commit();
                comp_id = pm.getObjectId(comp);
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
            
            // -------------- Check the retrieval of objects with embedded subobjects -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object with both embedded subobjects
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object", comp != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect operating system field", 
                    comp.getOperatingSystem().equals("MacOS"));
                assertTrue("Retrieved object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("NVidia"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Retrieved object with embedded object(s) has sound card, but shouldn't have",
                    comp.getSoundCard() == null);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                
                // Retrieve the object with 1 embedded subobject, and one unset, and set its sound card
                Computer comp = (Computer)pm.getObjectById(comp_id);
                comp.setSoundCard(new ComputerCard("Turtle Beach", ComputerCard.PCI_CARD));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Updated object with embedded object(s) has incorrect operating system field", 
                    comp.getOperatingSystem().equals("MacOS"));
                assertTrue("Updated object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("NVidia"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Updated object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Turtle Beach"));
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
        }
        finally
        {
            // Clean out created data
            clean(ComputerCard.class);
            clean(Computer.class);
        }
    }

    /**
     * Test the ability to persist (in its own right) an embedded object.
     * @throws Exception
     */
    public void testEmbeddedObjectPersist() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Persist an object with an embedded PC -----------------
            Object comp_id = null;
            try
            {
                tx.begin();
                
                Computer comp = new Computer("Windows",
                    new ComputerCard("ATI", ComputerCard.AGP_CARD), 
                    new ComputerCard("Intel", ComputerCard.PCI_CARD));
                
                pm.makePersistent(comp);
                
                tx.commit();
                comp_id = pm.getObjectId(comp);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating an object with embedded field(s) : " + e.getMessage());
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
            Object card_id = null;
            try
            {
                tx.begin();
                
                // Retrieve the Computer
                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect operating system field", 
                    comp.getOperatingSystem().equals("Windows"));
                assertTrue("Retrieved object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Retrieved object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Intel"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);

                // Persist a copy of the embedded graphics card (can't persist the actual object since it is marked as embedded)
                ComputerCard graphics = new ComputerCard(comp.getGraphicsCard().getMakerName(), comp.getGraphicsCard().getType());
                pm.makePersistent(graphics);

                tx.commit();
                card_id = pm.getObjectId(graphics);
                assertTrue("Persisted ComputerCard has no identity !", card_id != null);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the ComputerCard
                ComputerCard card = (ComputerCard)pm.getObjectById(card_id);
                assertTrue("Unable to retrieve object that was previously persisted embedded", card != null);
                assertTrue("Retrieved object has incorrect type",
                    card.getType() == ComputerCard.AGP_CARD);
                assertTrue("Retrieved object has unset manufacturer",
                    card.getMakerName() != null);
                assertTrue("Retrieved object has incorrect manufacturer",
                    card.getMakerName().equals("ATI"));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while checking the persistence of previously embedded objects : " + e.getMessage());
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
            clean(ComputerCard.class);
            clean(Computer.class);
        }
    }

    /**
     * Test for detaching/attaching an embedded PC object.
     * @throws Exception
     */
    public void testEmbeddedPCObjectDetachAttach() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist some objects
            Object comp_id = null;
            try
            {
                tx.begin();
                Computer comp = new Computer("Linux", new ComputerCard("ATI", ComputerCard.AGP_CARD), 
                    new ComputerCard("Creative", ComputerCard.PCI_CARD));
                pm.makePersistent(comp);

                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                comp.getGraphicsCard().toString();
                comp.getSoundCard().toString();

                tx.commit();
                comp_id = pm.getObjectId(comp);
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
            
            // Retrieve the object(s) and detach them
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            Computer detachedComputer = null;
            try
            {
                tx.begin();

                Computer comp = (Computer)pm.getObjectById(comp_id);
                assertTrue("Unable to retrieve object with embedded object(s)", comp != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect operating system field",
                    comp.getOperatingSystem().equals("Linux"));
                assertTrue("Retrieved object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Embedded graphics card doesn't have its owner field set",
                    comp.getGraphicsCard().getComputer() != null);
                assertTrue("Embedded graphics card has its owner field set incorrectly",
                    comp.getGraphicsCard().getComputer() == comp);
                assertTrue("Retrieved object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Creative"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                assertTrue("Embedded sound card doesn't have its owner field set",
                    comp.getSoundCard().getComputer() != null);
                assertTrue("Embedded sound card has its owner field set incorrectly",
                    comp.getSoundCard().getComputer() == comp);

                detachedComputer = (Computer)pm.detachCopy(comp);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            assertNotNull("Detached Computer is null!", detachedComputer);
            assertTrue("Updated object with embedded object(s) has incorrect operating system field", 
                detachedComputer.getOperatingSystem().equals("Linux"));
            assertTrue("Updated object with embedded object(s) has no graphics card",
                detachedComputer.getGraphicsCard() != null);
            assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                detachedComputer.getGraphicsCard().getMakerName().equals("ATI"));
            assertTrue("Updated object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                detachedComputer.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
            assertTrue("Updated object with embedded object(s) has no sound card",
                detachedComputer.getSoundCard() != null);
            assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                detachedComputer.getSoundCard().getMakerName().equals("Creative"));
            assertTrue("Updated object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                detachedComputer.getSoundCard().getType() == ComputerCard.PCI_CARD);

            // Update some objects
            detachedComputer.setOperatingSystem("Windows XP");
            detachedComputer.getSoundCard().setMakerName("Intel");

            // Attach the objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            try
            {
                tx.begin();

                Computer comp = (Computer)pm.makePersistent(detachedComputer);
                assertTrue("Attached object with embedded object(s) is null!!", comp != null);
                assertTrue("Attached object with embedded object(s) has incorrect operating system field",
                    comp.getOperatingSystem().equals("Windows XP"));
                assertTrue("Attached object with embedded object(s) has no graphics card",
                    comp.getGraphicsCard() != null);
                assertTrue("Attached object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong", 
                    comp.getGraphicsCard().getMakerName().equals("ATI"));
                assertTrue("Attached object with embedded object(s) has incorrect embedded object : graphics card type is wrong", 
                    comp.getGraphicsCard().getType() == ComputerCard.AGP_CARD);
                assertTrue("Embedded graphics card doesn't have its owner field set",
                    comp.getGraphicsCard().getComputer() != null);
                assertTrue("Embedded graphics card has its owner field set incorrectly",
                    comp.getGraphicsCard().getComputer() == comp);
                assertTrue("Retrieved object with embedded object(s) has no sound card",
                    comp.getSoundCard() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong", 
                    comp.getSoundCard().getMakerName().equals("Intel"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", 
                    comp.getSoundCard().getType() == ComputerCard.PCI_CARD);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            pm.getFetchPlan().setMaxFetchDepth(2);
            try
            {
                tx.begin();
                Computer comp = (Computer) pm.getObjectById(comp_id);
                assertNotNull("Attached object with embedded object(s) is null!!", comp);
                assertEquals("Attached object with embedded object(s) has incorrect operating system field",
                    "Windows XP", comp.getOperatingSystem());
                assertNotNull("Attached object with embedded object(s) has no graphics card", comp.getGraphicsCard());
                assertEquals("Attached object with embedded object(s) has incorrect embedded object : graphics card manufacturer is wrong",
                    "ATI", comp.getGraphicsCard().getMakerName());
                assertEquals("Attached object with embedded object(s) has incorrect embedded object : graphics card type is wrong",
                    ComputerCard.AGP_CARD, comp.getGraphicsCard().getType());
                assertNotNull("Embedded graphics card doesn't have its owner field set", comp.getGraphicsCard().getComputer());
                assertEquals("Embedded graphics card has its owner field set incorrectly", comp, comp.getGraphicsCard().getComputer());
                assertNotNull("Retrieved object with embedded object(s) has no sound card", comp.getSoundCard());
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card manufacturer is wrong",
                    comp.getSoundCard().getMakerName().equals("Intel"));
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : sound card type is wrong", comp.getSoundCard().getType() == ComputerCard.PCI_CARD);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            clean(ComputerCard.class);
            clean(Computer.class);
        }
    }

    /**
     * Test the embedding of a class tagged as "embeddedOnly".
     * @throws Exception
     */
    public void testEmbeddedOnly() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded object -----------------
            Object player_id = null;
            try
            {
                tx.begin();
                
                MusicPlayer player = new MusicPlayer("Apple", "IPOD", new Battery("Duracell", 100));
                pm.makePersistent(player);
                
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                player.getBattery().toString();
                
                tx.commit();
                player_id = pm.getObjectId(player);
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
            
            // -------------- Check the retrieval of objects with embedded subobject -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object with both embedded subobjects
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                assertTrue("Unable to retrieve object with embedded object(s)", player != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field",
                    player.getMake().equals("Apple"));
                assertTrue("Retrieved object with embedded object(s) has no battery",
                    player.getBattery() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : battery make is wrong", 
                    player.getBattery().getMake().equals("Duracell"));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                
                // Retrieve the object and change its battery
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                player.setBattery(new Battery("Hahnel", 400));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                assertTrue("Unable to retrieve object with embedded object(s)", player != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    player.getModel().equals("IPOD"));
                assertTrue("Updated object with embedded object(s) has no battery",
                    player.getBattery() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : battery make is wrong", 
                    player.getBattery().getMake().equals("Hahnel"));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                Battery battery = player.getBattery();
                battery.setLifetime(600);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                assertTrue("Unable to retrieve object with embedded object(s)", player != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    player.getModel().equals("IPOD"));
                assertTrue("Updated object with embedded object(s) has no battery",
                    player.getBattery() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : battery lifetime is wrong", 
                    player.getBattery().getLifetime() == 600);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
            // Detach an embedded object
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("fetch-battery");
            tx = pm.currentTransaction();
            MusicPlayer detachedPlayer = null;
            try
            {
                tx.begin();
                
                // Retrieve the object that has just been updated
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                detachedPlayer = (MusicPlayer)pm.detachCopy(player);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while detaching embedded objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check that the MusicPlayer is detached. The Battery will not be "detached" since it has no identity.
            assertTrue("Music player was not detached but should have been", JDOHelper.isDetached(detachedPlayer));
            
            // Change the detached object
            detachedPlayer.setModel("Mini IPOD");
            detachedPlayer.getBattery().setLifetime(700);
            
            // Attach an embedded object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(detachedPlayer);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while attaching embedded objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check the attached player
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object that has just been updated
                MusicPlayer player = (MusicPlayer)pm.getObjectById(player_id);
                assertTrue("Unable to retrieve object with embedded object(s)", player != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    player.getModel().equals("Mini IPOD"));
                assertTrue("Updated object with embedded object(s) has no battery",
                    player.getBattery() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : battery lifetime is wrong - should be 700 but is "+
                    player.getBattery().getLifetime(), player.getBattery().getLifetime() == 700);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            
            // TODO Delete an embedded object
        }
        finally
        {
            // Clean out created data
            clean(MusicPlayer.class);
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
                
                pm.makePersistent(camera);
                
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                camera.getMemory().toString();
                
                tx.commit();
                cameraId = pm.getObjectId(camera);
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
            
            // -------------- Check the retrieval of objects with embedded subobject -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the object with both embedded subobjects
                DigitalCamera camera = (DigitalCamera)pm.getObjectById(cameraId);
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field",
                    camera.getMake().equals("Canon"));
                assertTrue("Retrieved object with embedded object(s) has no memory",
                    camera.getMemory() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : memory type is wrong", 
                    camera.getMemory().getType() == Memory.COMPACT_FLASH);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    camera.getModel().equals("Powerzoom A40"));
                assertTrue("Updated object with embedded object(s) has no memory",
                    camera.getMemory() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : memory size is wrong", 
                    camera.getMemory().getSize() == 256);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    camera.getModel().equals("Powerzoom A40"));
                assertTrue("Updated object with embedded object(s) has no memory",
                    camera.getMemory() != null);
                assertTrue("Updated object with embedded object(s) has incorrect embedded object : memory voltage is wrong", 
                    camera.getMemory().getVoltage() == 5.0);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
                LOG.error("Exception in test", e);
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
                assertTrue("Unable to retrieve object with embedded object(s)", camera != null);
                assertTrue("Updated object with embedded object(s) has incorrect model field", 
                    camera.getModel().equals("Powerzoom A40"));
                assertTrue("Updated object with embedded object(s) has no memory",
                    camera.getMemory() != null);
                assertTrue("Updated object with embedded object(s) has no chip",
                    camera.getMemory().getChip() != null);
                assertTrue("Updated object with embedded object(s) has incorrect nested embedded object : chip thickness is wrong", 
                    camera.getMemory().getChip().getThickness() == 6);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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

                Query query = pm.newQuery("SELECT FROM org.jpox.samples.embedded.DigitalCamera WHERE memory.chip.thickness == 6");
                List results = (List) query.execute();
                assertEquals("Number of cameras retrieved by query of nested embedded is incorrect", results.size(), 1);
                DigitalCamera camera = (DigitalCamera) results.iterator().next();
                
                assertTrue("Unable to retrieve object with nested embedded object(s)", camera != null);
                assertTrue("Retrieved object with nested embedded object(s) has incorrect make field", camera.getMake().equals("Canon"));
                assertTrue("Retrieved object with nested embedded object(s) has no memory", camera.getMemory() != null);
                assertFalse("Camera memory should not be marked as dirty but is", JDOHelper.isDirty(camera.getMemory()));
                assertTrue("Retrieved object with nested embedded object(s) has no memory chip", camera.getMemory().getChip() != null);
                assertTrue("Retrieved object with nested embedded object(s) has incorrect memory chip thickness", 
                    camera.getMemory().getChip().getThickness() == 6);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
     * Test the retrieval of an "embedded-only" object with query
     * @throws Exception
     */
    public void testEmbeddedOnlyWithQuery() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded object -----------------
            try
            {
                tx.begin();
                
                MusicPlayer player = new MusicPlayer("Apple", "IPOD", new Battery("Duracell", 100));
                pm.makePersistent(player);
                
                // Access the object containing the embedded object before commit
                // This used to try to go to the datastore at this point
                player.getBattery().toString();
                
                tx.commit();
                pm.getObjectId(player);
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
            
            // -------------- Check the retrieval of objects with embedded subobject with query -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Query query = pm.newQuery("SELECT FROM org.jpox.samples.embedded.MusicPlayer " +
                "WHERE battery.make == param_make PARAMETERS java.lang.String param_make");
                List results = (List) query.execute("Duracell");
                MusicPlayer player = (MusicPlayer) results.iterator().next();
                
                assertTrue("Unable to retrieve object with embedded object(s)", player != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect make field", player.getMake().equals("Apple"));
                assertTrue("Retrieved object with embedded object(s) has no battery", player.getBattery() != null);
                assertTrue("Retrieved object with embedded object(s) has incorrect embedded object : battery make is wrong", 
                    player.getBattery().getMake().equals("Duracell"));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
        }
        finally
        {
            // Clean out created data
            clean(MusicPlayer.class);
        }
    }

    /**
     * Test for an embedded PC object with inheritance.
     * @throws Exception
     */
    public void testEmbeddedPCObjectWithInheritance() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded objects -----------------
            Object kit1_id = null;
            Object kit2_id = null;
            try
            {
                tx.begin();

                FittedKitchen kit1 = new FittedKitchen();
                kit1.setId(1);
                kit1.setMake("IKEA");
                Oven oven = new Oven();
                oven.setMake("Baumatic");
                oven.setModel("El Basico");
                kit1.setOven(oven);
                pm.makePersistent(kit1);

                FittedKitchen kit2 = new FittedKitchen();
                kit2.setId(2);
                kit2.setMake("Klassic Kitchens");
                MultifunctionOven oven2 = new MultifunctionOven();
                oven2.setMake("Baumatic");
                oven2.setModel("El Classico");
                oven2.setCapabilities("Microwave");
                kit2.setOven(oven2);
                pm.makePersistent(kit2);

                tx.commit();
                kit1_id = pm.getObjectId(kit1);
                kit2_id = pm.getObjectId(kit2);
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
            
            // -------------- Check the retrieval of objects with embedded subobjects -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the objects with embedded subobject
                FittedKitchen kit1 = (FittedKitchen)pm.getObjectById(kit1_id);
                assertTrue("Unable to retrieve object with embedded object(s)", kit1 != null);
                assertEquals("Incorrect make", "IKEA", kit1.getMake());
                assertTrue("Retrieved Kitchen1 has no oven", kit1.getOven() != null);
                Oven oven = kit1.getOven();
                assertEquals("Kitchen1.oven is incorrect type", Oven.class.getName(), oven.getClass().getName());
                assertEquals("Kitchen1.oven.make is incorrect", "Baumatic", oven.getMake());
                assertEquals("Kitchen1.oven.model is incorrect", "El Basico", oven.getModel());
                FittedKitchen kit2 = (FittedKitchen)pm.getObjectById(kit2_id);
                assertTrue("Unable to retrieve object with embedded object(s)", kit2 != null);
                assertEquals("Incorrect make", "Klassic Kitchens", kit2.getMake());
                assertTrue("Retrieved Kitchen1 has no oven", kit2.getOven() != null);
                oven = kit2.getOven();
                assertEquals("Kitchen1.oven is incorrect type", MultifunctionOven.class.getName(), oven.getClass().getName());
                assertEquals("Kitchen1.oven.make is incorrect", "Baumatic", oven.getMake());
                assertEquals("Kitchen1.oven.model is incorrect", "El Classico", oven.getModel());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
        }
        finally
        {
            // Clean out created data
            clean(FittedKitchen.class);
        }
    }

    /**
     * Test for an embedded PC object with inheritance using value-map discrim.
     * @throws Exception
     */
    public void testEmbeddedPCObjectWithInheritanceValueMap() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // ------------------ Check the persistence of an object with embedded objects -----------------
            Object bath1_id = null;
            Object bath2_id = null;
            try
            {
                tx.begin();

                FittedBathroom bathrm1 = new FittedBathroom();
                bathrm1.setId(1);
                bathrm1.setMake("Willbond");
                Bath bath = new Bath();
                bath.setMake("Bathstore");
                bath.setWidth(700);
                bath.setLength(1600);
                bathrm1.setBath(bath);
                pm.makePersistent(bathrm1);

                FittedBathroom bathrm2 = new FittedBathroom();
                bathrm2.setId(2);
                bathrm2.setMake("Best Bathrooms");
                ShowerBath bath2 = new ShowerBath();
                bath2.setMake("Bathrooms.com");
                bath2.setWidth(700);
                bath2.setLength(1700);
                bath2.setCapabilities("Luxury curved screen");
                bathrm2.setBath(bath2);
                pm.makePersistent(bathrm2);

                tx.commit();
                bath1_id = pm.getObjectId(bathrm1);
                bath2_id = pm.getObjectId(bathrm2);
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
            
            // -------------- Check the retrieval of objects with embedded subobjects -----------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the objects with embedded subobject
                FittedBathroom bathrm1 = (FittedBathroom)pm.getObjectById(bath1_id);
                assertTrue("Unable to retrieve object with embedded object(s)", bathrm1 != null);
                assertEquals("Incorrect make", "Willbond", bathrm1.getMake());
                assertTrue("Retrieved Kitchen1 has no oven", bathrm1.getBath() != null);
                Bath bath1 = bathrm1.getBath();
                assertEquals("Bathroom1.bath is incorrect type", Bath.class.getName(), bath1.getClass().getName());
                assertEquals("Bathroom1.bath.make is incorrect", "Bathstore", bath1.getMake());
                assertEquals("Bathroom1.bath.width is incorrect", 700, bath1.getWidth());
                assertEquals("Bathroom1.bath.length is incorrect", 1600, bath1.getLength());

                FittedBathroom bathrm2 = (FittedBathroom)pm.getObjectById(bath2_id);
                assertTrue("Unable to retrieve object with embedded object(s)", bathrm2 != null);
                assertEquals("Incorrect make", "Best Bathrooms", bathrm2.getMake());
                assertTrue("Retrieved Kitchen1 has no oven", bathrm2.getBath() != null);
                Bath bath2 = bathrm2.getBath();
                assertEquals("Bathroom1.bath is incorrect type", ShowerBath.class.getName(), bath2.getClass().getName());
                assertEquals("Bathroom1.bath.make is incorrect", "Bathrooms.com", bath2.getMake());
                assertEquals("Bathroom1.bath.width is incorrect", 700, bath2.getWidth());
                assertEquals("Bathroom1.bath.length is incorrect", 1700, bath2.getLength());
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
        }
        finally
        {
            // Clean out created data
            clean(FittedBathroom.class);
        }
    }
}