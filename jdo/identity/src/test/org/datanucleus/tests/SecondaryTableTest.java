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
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.secondarytable.Printer;

/**
 * Simple tests for secondary table handling.
 * @version $Revision: 1.1 $
 */
public class SecondaryTableTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public SecondaryTableTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Printer.class   // Performs schema generation test
                }
            );
            initialised = true;
        }
    }

    protected void tearDown() 
    throws Exception
    {
        super.tearDown();

        clean(Printer.class);
    }

    /**
     * Test for simple secondary table.
     * Performs the main lifecycle operations on an object with a secondary table :-
     * 1. makePersistent
     * 2. update within a transaction
     * 3. detach
     * 4. update the detached object and attach it
     * 5. delete
     * @throws Exception
     */
    public void testSecondaryTable() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        String make = "Hewlett-Packard";
        String model = "LaserJet 1200";
        String tonerModel = "Laser Cartridge";
        int tonerLifetime = 10000;

        // =================================================================
        // Check the persistence of an object with secondary tables
        Object printerId = null;
        try
        {
            tx.begin();

            Printer printer = new Printer(make, model, tonerModel, tonerLifetime);

            pm.makePersistent(printer);

            tx.commit();
            printerId = pm.getObjectId(printer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while creating objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // =================================================================
        // Check the retrieval of the object
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Printer printer = (Printer)pm.getObjectById(printerId);

            assertEquals("Printer make is incorrect after retrieval", make, printer.getMake());
            assertEquals("Printer model is incorrect after retrieval", model, printer.getModel());
            assertEquals("Printer tonerModel is incorrect after retrieval", tonerModel, printer.getTonerModel());
            assertTrue("Printer tonerLifetime is incorrect after retrieval", tonerLifetime == printer.getTonerLifetime());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while retrieving objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // =================================================================
        // Check the update of the object
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        String newMake = "Epson";
        String newModel = "Stylus Photo R1800";
        String newTonerModel = "Colour Cartridge";
        int newTonerLifetime = 5000;
        try
        {
            tx.begin();

            Printer printer = (Printer)pm.getObjectById(printerId);

            printer.setMake(newMake);
            printer.setModel(newModel);
            printer.setTonerModel(newTonerModel);
            printer.setTonerLifetime(newTonerLifetime);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while updating objects with secondary tables : " + e.getMessage());
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

            Printer printer = (Printer)pm.getObjectById(printerId);

            assertTrue("Printer make is incorrect after update", newMake.equals(printer.getMake()));
            assertTrue("Printer model is incorrect after update", newModel.equals(printer.getModel()));
            assertTrue("Printer tonerModel is incorrect after update", newTonerModel.equals(printer.getTonerModel()));
            assertTrue("Printer tonerLifetime is incorrect after update", newTonerLifetime == printer.getTonerLifetime());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while retrieving updated objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // =================================================================
        // Check the detach of the object
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        Printer detachedPrinter = null;
        try
        {
            tx.begin();

            Printer printer = (Printer)pm.getObjectById(printerId);
            detachedPrinter = (Printer)pm.detachCopy(printer);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while detaching objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // =================================================================
        // Check the attach of the (updated) object
        detachedPrinter.setMake(make);
        detachedPrinter.setModel(model);
        detachedPrinter.setTonerModel(tonerModel);
        detachedPrinter.setTonerLifetime(tonerLifetime);
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.makePersistent(detachedPrinter);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while attaching objects with secondary tables : " + e.getMessage());
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

            Printer printer = (Printer)pm.getObjectById(printerId);

            assertTrue("Printer make is incorrect after attach", make.equals(printer.getMake()));
            assertTrue("Printer model is incorrect after attach", model.equals(printer.getModel()));
            assertTrue("Printer tonerModel is incorrect after attach", tonerModel.equals(printer.getTonerModel()));
            assertTrue("Printer tonerLifetime is incorrect after attach", tonerLifetime == printer.getTonerLifetime());

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while retrieving attached objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // =================================================================
        // Check the deletion of the object
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Printer printer = (Printer)pm.getObjectById(printerId);
            pm.deletePersistent(printer);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while deleting objects with secondary tables : " + e.getMessage());
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
     * Test for querying an object that stores some field(s) in a secondary table.
     * @throws Exception
     */
    public void testQuerySecondaryTable() 
    throws Exception
    {
        // Persist a record to use in JDOQL queries
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object printerId = null;
        try
        {
            tx.begin();

            Printer printer = new Printer("Hewlett-Packard", "LaserJet 1200", "Laser Cartridge", 10000);

            pm.makePersistent(printer);

            tx.commit();
            printerId = pm.getObjectId(printer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while creating objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Perform a query referring to a field stored in the primary table
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(Printer.class, "make == \"Hewlett-Packard\"");
            List results = (List)q.execute();
            assertTrue("Number of objects returned from JDOQL when using field in primary table is wrong : is " +
                results.size() + " but should be 1", results.size() == 1);
            assertTrue("Object returned from JDOQL when using field in primary table is incorrect",
                pm.getObjectId(results.get(0)).equals(printerId));

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while querying objects with secondary tables : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Perform a query referring to a field stored in the secondary table
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(Printer.class, "tonerLifetime == 10000");
            List results = (List)q.execute();
            assertTrue("Number of objects returned from JDOQL when using field in secondary table is wrong : is " +
                results.size() + " but should be 1", results.size() == 1);
            assertTrue("Object returned from JDOQL when using field in secondary table is incorrect",
                pm.getObjectId(results.get(0)).equals(printerId));

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while querying objects with secondary tables : " + e.getMessage());
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