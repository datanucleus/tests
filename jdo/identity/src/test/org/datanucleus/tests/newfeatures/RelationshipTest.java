/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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
***********************************************************************/
package org.datanucleus.tests.newfeatures;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.one_many.unidir.DesktopComputer;
import org.jpox.samples.one_many.unidir.LaptopComputer;
import org.jpox.samples.one_many.unidir.Office;
import org.jpox.samples.one_one.unidir_2.Magazine;
import org.jpox.samples.one_one.unidir_2.MediaWork;
import org.jpox.samples.one_one.unidir_2.Newspaper;
import org.jpox.samples.one_one.unidir_2.Reader;

/**
 * Relationships tests that are feature requests to current functionality and so likely fail.
 **/
public class RelationshipTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public RelationshipTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                {
                    DesktopComputer.class,
                    LaptopComputer.class,
                    Office.class,
                    Magazine.class,
                    MediaWork.class,
                    Newspaper.class,
                    Reader.class,
                });
            initialised = true;
        }
    }

    /**
     * Test case for 1-N unidirectional join table relationship with the element using "subclass-table" inheritance.
     * See JIRA "NUCRDBMS-17"
     **/
    public void test1toNUnidirJoinSubclassTable()
    throws Exception
    {
        try
        {
            Object officeId = null;
            Object[] computerIds = null;
            Object[] deletedComputerIds = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create some data
                Office office = new Office("JPOX Corporate Office");
                LaptopComputer laptop1 = new LaptopComputer("192.168.0.10", "Linux", 3, 2);
                DesktopComputer desktop1 = new DesktopComputer("192.168.0.11", "Linux", 2);
                DesktopComputer desktop2 = new DesktopComputer("192.168.0.12", "Windows", 1);
                office.addComputer(laptop1);
                office.addComputer(desktop1);
                office.addComputer(desktop2);
                pm.makePersistent(office);

                officeId = JDOHelper.getObjectId(office);
                computerIds = new Object[3];
                computerIds[0] = JDOHelper.getObjectId(laptop1);
                computerIds[1] = JDOHelper.getObjectId(desktop1);
                computerIds[2] = JDOHelper.getObjectId(desktop2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while creating 1-N unidirectional Join Table \"subclass-table\" relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Test the retrieval of objects by id
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Check the Office
                Office office = (Office)pm.getObjectById(officeId);
                assertTrue("Office was not retrieved via getObjectById", office != null);
                assertEquals("Office obtained by getObjectById was incorrect : has wrong name", office.getName(), "JPOX Corporate Office");
                assertTrue("Office obtained by getObjectById was incorrect : has null collection of computers", office.getComputers() != null);
                assertEquals("Office obtained by getObjectById was incorrect : has incorrect number of computers", 
                    office.getNumberOfComputers(), 3);
                Collection computers = office.getComputers();
                Iterator iter = computers.iterator();
                while (iter.hasNext())
                {
                    Object obj = iter.next();
                    if (JDOHelper.getObjectId(obj).equals(computerIds[0]))
                    {
                        assertTrue("Laptop1 is not a LaptopComputer type!", obj instanceof LaptopComputer);
                        LaptopComputer laptop = (LaptopComputer)obj;
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong IP address", 
                            laptop.getIpAddress(), "192.168.0.10");
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong operating system", 
                            laptop.getOperatingSystem(), "Linux");
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong number of PCMCIA", 
                            laptop.getNumberOfPcmcia(), 2);
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has incorrect battery life", 
                            laptop.getBatteryLife(), 3);
                    }
                    else if (JDOHelper.getObjectId(obj).equals(computerIds[1]))
                    {
                        assertTrue("Desktop1 is not a DesktopComputer type!", obj instanceof DesktopComputer);
                        DesktopComputer desktop = (DesktopComputer)obj;
                        assertEquals("Desktop1 obtained by getObjectById was incorrect : has wrong IP address", 
                            desktop.getIpAddress(), "192.168.0.11");
                        assertEquals("Desktop1 obtained by getObjectById was incorrect : has wrong operating system", 
                            desktop.getOperatingSystem(), "Linux");
                        assertEquals("Desktop1 obtained by getObjectById was incorrect : has wrong number of processors", 
                            desktop.getNumberOfProcessors(), 2);
                    }
                    else if (JDOHelper.getObjectId(obj).equals(computerIds[2]))
                    {
                        assertTrue("Desktop2 is not a DesktopComputer type!", obj instanceof DesktopComputer);
                        DesktopComputer desktop = (DesktopComputer)obj;
                        assertEquals("Desktop2 obtained by getObjectById was incorrect : has wrong IP address", 
                            desktop.getIpAddress(), "192.168.0.12");
                        assertEquals("Desktop2 obtained by getObjectById was incorrect : has wrong operating system", 
                            desktop.getOperatingSystem(), "Windows");
                        assertEquals("Desktop2 obtained by getObjectById was incorrect : has wrong number of processors", 
                            desktop.getNumberOfProcessors(), 1);
                    }
                    else
                    {
                        fail("Computer retrieved from Office has unknown id! : " + JDOHelper.getObjectId(obj));
                    }
                }

                // Check the Computers retrieved via getObjectById
                LaptopComputer laptop1 = (LaptopComputer)pm.getObjectById(computerIds[0]);
                assertTrue("Laptop1 was not retrieved via getObjectById", laptop1 != null);
                assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong operating system", 
                    laptop1.getOperatingSystem(), "Linux");
                assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong number of PCMCIA", 
                    laptop1.getNumberOfPcmcia(), 2);

                DesktopComputer desktop1 = (DesktopComputer)pm.getObjectById(computerIds[1]);
                assertTrue("Desktop1 was not retrieved via getObjectById", desktop1 != null);
                assertEquals("Desktop1 obtained by getObjectById was incorrect : has wrong operating system", 
                    desktop1.getOperatingSystem(), "Linux");
                assertEquals("Desktop1 obtained by getObjectById was incorrect : has wrong number of processors", 
                    desktop1.getNumberOfProcessors(), 2);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown creating 1-N bidir JoinTable \"subclass-table\" relationship", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table \"subclass-table\" relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Test add/remove of computers
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the Office
                Office office = (Office)pm.getObjectById(officeId);
                
                // Remove the 2 desktops and replace with a new one
                DesktopComputer desktop1 = (DesktopComputer)pm.getObjectById(computerIds[1]);
                DesktopComputer desktop2 = (DesktopComputer)pm.getObjectById(computerIds[2]);
                office.removeComputer(desktop1);
                office.removeComputer(desktop2);

                DesktopComputer desktop3 = new DesktopComputer("192.168.0.13", "Solaris", 4);
                office.addComputer(desktop3);
                
                tx.commit();
                
                deletedComputerIds = new Object[2];
                deletedComputerIds[0] = computerIds[1];
                deletedComputerIds[1] = computerIds[2];

                Object laptopId = computerIds[0];
                computerIds = new Object[2];
                computerIds[0] = laptopId;
                computerIds[1] = JDOHelper.getObjectId(desktop3);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while updating 1-N bidirectional Join Table \"subclass-table\" relationship data : " + e.getMessage());
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
                
                // Check the Office
                Office office = (Office)pm.getObjectById(officeId);
                assertTrue("Office was not retrieved via getObjectById", office != null);
                assertEquals("Office obtained by getObjectById was incorrect : has wrong name", office.getName(), "JPOX Corporate Office");
                assertTrue("Office obtained by getObjectById was incorrect : has null collection of computers", office.getComputers() != null);
                assertEquals("Office obtained by getObjectById was incorrect : has incorrect number of computers", 
                    office.getNumberOfComputers(), 2);

                Collection computers = office.getComputers();
                Iterator iter = computers.iterator();
                while (iter.hasNext())
                {
                    Object obj = iter.next();
                    if (JDOHelper.getObjectId(obj).equals(computerIds[0]))
                    {
                        assertTrue("Laptop1 is not a LaptopComputer type!", obj instanceof LaptopComputer);
                        LaptopComputer laptop = (LaptopComputer)obj;
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong IP address", 
                            laptop.getIpAddress(), "192.168.0.10");
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong operating system", 
                            laptop.getOperatingSystem(), "Linux");
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong number of PCMCIA", 
                            laptop.getNumberOfPcmcia(), 2);
                        assertEquals("Laptop1 obtained by getObjectById was incorrect : has incorrect battery life", 
                            laptop.getBatteryLife(), 3);
                    }
                    else if (JDOHelper.getObjectId(obj).equals(computerIds[1]))
                    {
                        assertTrue("Desktop3 is not a DesktopComputer type!", obj instanceof DesktopComputer);
                        DesktopComputer desktop = (DesktopComputer)obj;
                        assertEquals("Desktop3 obtained by getObjectById was incorrect : has wrong IP address", 
                            desktop.getIpAddress(), "192.168.0.13");
                        assertEquals("Desktop3 obtained by getObjectById was incorrect : has wrong operating system", 
                            desktop.getOperatingSystem(), "Solaris");
                        assertEquals("Desktop3 obtained by getObjectById was incorrect : has wrong number of processors", 
                            desktop.getNumberOfProcessors(), 4);
                    }
                    else
                    {
                        fail("Computer retrieved from Office has unknown id! : " + JDOHelper.getObjectId(obj));
                    }
                }

                // Check the Computers retrieved via getObjectById
                LaptopComputer laptop1 = (LaptopComputer)pm.getObjectById(computerIds[0]);
                assertTrue("Laptop1 was not retrieved via getObjectById", laptop1 != null);
                assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong operating system", 
                    laptop1.getOperatingSystem(), "Linux");
                assertEquals("Laptop1 obtained by getObjectById was incorrect : has wrong number of PCMCIA", 
                    laptop1.getNumberOfPcmcia(), 2);

                DesktopComputer desktop3 = (DesktopComputer)pm.getObjectById(computerIds[1]);
                assertTrue("Desktop3 was not retrieved via getObjectById", desktop3 != null);
                assertEquals("Desktop3 obtained by getObjectById was incorrect : has wrong operating system", 
                    desktop3.getOperatingSystem(), "Solaris");
                assertEquals("Desktop3 obtained by getObjectById was incorrect : has wrong number of processors", 
                    desktop3.getNumberOfProcessors(), 4);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // TODO Perform some queries
        }
        finally
        {
            // Clean out our data
            clean(LaptopComputer.class);
            clean(DesktopComputer.class);
            clean(Office.class);
        }
    }

    /**
     * Test case for 1-1 uni relationship to a class using "subclass-table" inheritance strategy.
     * See JIRA "NUCRDBMS-18"
     **/
    public void test1to1UnidirInheritanceSubclassTable()
    throws Exception
    {
        try
        {
            // Create the necessary schema
            try
            {
                addClassesToSchema(new Class[]
                   {
                    Newspaper.class,
                    Magazine.class,
                    MediaWork.class,
                    Reader.class
                   });
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail ("Exception thrown while adding classes for 1-1 relation using subclass-table : " + e.getMessage());
            }

            // Check the persistence of data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object fredId = null;
            Object pamId = null;
            try
            {
                tx.begin();
                Magazine hello = new Magazine("Hello", MediaWork.FREQ_WEEKLY, "Trash Publishers");
                Newspaper mail = new Newspaper("Daily Mail", MediaWork.FREQ_DAILY, "Piers Morgan", "Tabloid");
                Reader fred = new Reader("Fred Smith", mail);
                Reader pam = new Reader("Pam Green", hello);
                pm.makePersistent(fred);
                pm.makePersistent(pam);
                tx.commit();
                fredId = pm.getObjectId(fred);
                pamId = pm.getObjectId(pam);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the retrieval of the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Reader fred = (Reader) pm.getObjectById(fredId, true);
                assertTrue("Fred has the wrong name!", fred.getName().equals("Fred Smith"));
                assertTrue("Fred has the wrong type of material", fred.getMaterial() instanceof Newspaper);
                assertTrue("Fred has the wrong material", fred.getMaterial().getName().equals("Daily Mail"));

                Reader pam = (Reader) pm.getObjectById(pamId, true);
                assertTrue("Pam has the wrong name", pam.getName().equals("Pam Green"));
                assertTrue("Pam has the wrong type of material", pam.getMaterial() instanceof Magazine);
                assertTrue("Pam has the wrong material", pam.getMaterial().getName().equals("Hello"));

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

            // Check a query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery(Reader.class, "material.name == \"Hello\"");
                List results1 = (List)q1.execute();
                assertEquals("Number of readers who read \"Hello\" magazine was incorrect", results1.size(), 1);

                Query q2 = pm.newQuery(Reader.class, "material.name == \"Daily Mail\"");
                List results2 = (List)q2.execute();
                assertEquals("Number of readers who read \"Daily Mail\" newspaper was incorrect", results2.size(), 1);

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
            // Clean out all data
            clean(Reader.class);
            clean(Newspaper.class);
            clean(Magazine.class);
        }
    }
}