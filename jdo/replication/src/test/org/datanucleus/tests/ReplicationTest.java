/**********************************************************************
 Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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

import org.jpox.samples.one_many.unidir.Computer;
import org.jpox.samples.one_many.unidir.DesktopComputer;
import org.jpox.samples.one_many.unidir.LaptopComputer;
import org.jpox.samples.one_many.unidir.Office;
import org.datanucleus.api.jdo.JDOReplicationManager;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import java.io.IOException;
import java.util.Properties;

/**
 * Test the replication facility.
 * In these tests, PMF1 is the PMF for whatever datastore is being run (e.g RDBMS) and
 * PMF2 is for XML.
 */
public class ReplicationTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /** PMF for XML datastore. */
    private static PersistenceManagerFactory pmf2;

    public ReplicationTest(String name) throws IOException
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Office.class, 
                    LaptopComputer.class, 
                    DesktopComputer.class, 
                    Computer.class
                });

            Properties props = new Properties();
            props.load(Thread.currentThread().getContextClassLoader().getResource(
                "datanucleus-xml.1.properties").openStream());
            props.setProperty("javax.jdo.option.Mapping", "xml");
            pmf2 = JDOHelper.getPersistenceManagerFactory(props);
            initialised = true;
        }
    }

    protected void setUp() throws Exception
    {
    }

    /**
     * Test that creates an Office+Computer(x2) in PMF1, replicates this to PMF2.
     */
    public void testReplicateCleanToXML()
    {
        // Create RDBMS data
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object office1Id;
        try
        {
            tx.begin();
            Office office1 = new Office("Head Office");
            LaptopComputer laptop1 = new LaptopComputer("192.168.0.2", "Linux", 4, 0);
            office1.addComputer(laptop1);
            DesktopComputer desktop1 = new DesktopComputer("192.168.0.3", "Windows XP", 1);
            office1.addComputer(desktop1);
            pm.makePersistent(office1);
            office1Id = pm.getObjectId(office1);
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

        JDOReplicationManager replicator = new JDOReplicationManager(pmf, pmf2);
        replicator.replicate(office1Id);

        pm = pmf2.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Office office1 = (Office) pm.getObjectById(office1Id);
            assertNotNull(office1);
            assertEquals("Office name is incorrect", "Head Office", office1.getName());
            assertEquals("Number of computers is incorrect", 2, office1.getNumberOfComputers());
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

        TestHelper.clean(pmf, Office.class);
        TestHelper.clean(pmf, LaptopComputer.class);
        TestHelper.clean(pmf, DesktopComputer.class);
        TestHelper.clean(pmf2, Office.class);
        TestHelper.clean(pmf2, LaptopComputer.class);
        TestHelper.clean(pmf2, DesktopComputer.class);
    }

    /**
     * Test that creates an Office+Computer(x2) in PMF2, replicates this to PMF1.
     */
    public void testReplicateCleanFromXML() throws IOException
    {
        PersistenceManager pm = pmf2.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object office1Id;
        try
        {
            tx.begin();
            Office office1 = new Office("Head Office");
            LaptopComputer laptop1 = new LaptopComputer("192.168.0.2", "Linux", 4, 0);
            office1.addComputer(laptop1);
            DesktopComputer desktop1 = new DesktopComputer("192.168.0.3", "Windows XP", 1);
            office1.addComputer(desktop1);
            pm.makePersistent(office1);
            office1Id = pm.getObjectId(office1);
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

        JDOReplicationManager replicator = new JDOReplicationManager(pmf2, pmf);
        replicator.replicate(office1Id);

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Office office1 = (Office) pm.getObjectById(office1Id);
            assertNotNull(office1);
            assertEquals("Office name is incorrect", "Head Office", office1.getName());
            assertEquals("Number of computers is incorrect", 2, office1.getNumberOfComputers());
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

        TestHelper.clean(pmf, Office.class);
        TestHelper.clean(pmf, LaptopComputer.class);
        TestHelper.clean(pmf, DesktopComputer.class);
        TestHelper.clean(pmf2, Office.class);
        TestHelper.clean(pmf2, LaptopComputer.class);
        TestHelper.clean(pmf2, DesktopComputer.class);
    }

    /**
     * Test that creates an Office+Computer in PMF1, and Office+Computer(x2) in PMF2. 
     * Then replicates from PMF2 to PMF1 and checks that the other Computer is replicated.
     */
    public void testReplicatePartialFromXML() throws IOException
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object office1Id;
        try
        {
            tx.begin();
            Office office1 = new Office("Head Office");
            LaptopComputer laptop1 = new LaptopComputer("192.168.0.2", "Linux", 4, 0);
            office1.addComputer(laptop1);
            pm.makePersistent(office1);
            office1Id = pm.getObjectId(office1);
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

        pm = pmf2.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Office office1 = new Office("Head Office");
            LaptopComputer laptop1 = new LaptopComputer("192.168.0.2", "Linux", 4, 0);
            office1.addComputer(laptop1);
            DesktopComputer desktop1 = new DesktopComputer("192.168.0.3", "Windows XP", 1);
            office1.addComputer(desktop1);
            pm.makePersistent(office1);
            office1Id = pm.getObjectId(office1);
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

        JDOReplicationManager replicator = new JDOReplicationManager(pmf2, pmf);
        replicator.replicate(office1Id);

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Office office1 = (Office) pm.getObjectById(office1Id);
            assertNotNull(office1);
            assertEquals("Office name is incorrect", "Head Office", office1.getName());
            assertEquals("Number of computers is incorrect", 2, office1.getNumberOfComputers());
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

        TestHelper.clean(pmf, Office.class);
        TestHelper.clean(pmf, LaptopComputer.class);
        TestHelper.clean(pmf, DesktopComputer.class);
        TestHelper.clean(pmf2, Office.class);
        TestHelper.clean(pmf2, LaptopComputer.class);
        TestHelper.clean(pmf2, DesktopComputer.class);
    }

    /**
     * Test that creates an Office+Computer(x2) in PMF1, replicates using all registered classes this to PMF2.
     */
    public void testReplicateCleanAllToXML()
    {
        // Create RDBMS data
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object office1Id;
        try
        {
            tx.begin();
            Office office1 = new Office("Head Office");
            LaptopComputer laptop1 = new LaptopComputer("192.168.0.2", "Linux", 4, 0);
            office1.addComputer(laptop1);
            DesktopComputer desktop1 = new DesktopComputer("192.168.0.3", "Windows XP", 1);
            office1.addComputer(desktop1);
            pm.makePersistent(office1);
            office1Id = pm.getObjectId(office1);
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

        JDOReplicationManager replicator = new JDOReplicationManager(pmf, pmf2);
        replicator.replicateRegisteredClasses();

        pm = pmf2.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Office office1 = (Office) pm.getObjectById(office1Id);
            assertNotNull(office1);
            assertEquals("Office name is incorrect", "Head Office", office1.getName());
            assertEquals("Number of computers is incorrect", 2, office1.getNumberOfComputers());
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

        TestHelper.clean(pmf, Office.class);
        TestHelper.clean(pmf, LaptopComputer.class);
        TestHelper.clean(pmf, DesktopComputer.class);
        TestHelper.clean(pmf2, Office.class);
        TestHelper.clean(pmf2, LaptopComputer.class);
        TestHelper.clean(pmf2, DesktopComputer.class);
    }
}