/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.nondurable.LogEntry;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.NucleusLogger;

/**
 * Tests for the use of "nondurable" identity.
 */
public class NondurableIdTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public NondurableIdTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    LogEntry.class
                });
            initialised = true;
        }
    }

    /**
     * Method to test the persistence of nondurable objects.
     */
    public void testPersist()
    {
        if (!storeMgr.getSupportedOptions().contains("NonDurableIdentity"))
        {
            return;
        }

        try
        {
            // Persist some "nondurable" objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                List entries = new ArrayList();
                LogEntry entry = new LogEntry(LogEntry.WARNING, "Datastore adapter not found. Falling back to default");
                entries.add(entry);
                entries.add(new LogEntry(LogEntry.ERROR, "No datastore specified"));
                entries.add(new LogEntry(LogEntry.INFO, "Object X1 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object X2 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object Y1 persisted"));
                entries.add(new LogEntry(LogEntry.ERROR, "Error persisting object Y2"));
                pm.makePersistentAll(entries);

                tx.commit();
                Object id = pm.getObjectId(entry);

                // Try to access LogEntry in HOLLOW state
                try
                {
                    entry.getLevel();
                    fail("Attempt to access field of HOLLOW nondurable instance succeeded!");
                }
                catch (JDOUserException e)
                {
                    // Expected JDO2 [5.4.4] Access of field of HOLLOW nondurable throws JDOUserException
                }

                // Try to access the (HOLLOW) object using its id
                try
                {
                    tx.begin();
                    pm.getObjectById(id);
                    fail("Attempt to access hollow nondurable instance succeeded!");
                    tx.commit();
                }
                catch (JDOUserException e)
                {
                    // Expected : JDO2 [5.4.4] Attempt to access nondurable object with id throws JDOUserException
                }
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve some objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Try an Extent
                Extent ex = pm.getExtent(LogEntry.class);
                Iterator iter = ex.iterator();
                int noEntries = 0;
                while (iter.hasNext())
                {
                    Object obj = iter.next();
                    Object id = JDOHelper.getObjectId(obj);
                    assertNotNull("Identity of nondurable object retrieved from Extent is null!", id);
                    noEntries++;
                }
                assertEquals("Number of LogEntry objects retrieved via Extent is incorrect", 6, noEntries);

                // Try a query for particular objects
                Query q = pm.newQuery("SELECT FROM " + LogEntry.class.getName() + " WHERE level == 1");
                List results = (List)q.execute();
                assertEquals("Number of LogEntry objects retrieved via Query (WARNING) is incorrect", 3, results.size());
                Iterator queryIter = results.iterator();
                while (queryIter.hasNext())
                {
                    Object obj = queryIter.next();
                    Object id = JDOHelper.getObjectId(obj);
                    assertNotNull("Identity of nondurable object retrieved from Query is null!", id);
                }

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
            // Clear out our data
            clean(LogEntry.class);
        }
    }

    /**
     * Method to test the update of nondurable objects.
     */
    public void testUpdate()
    {
        if (!storeMgr.getSupportedOptions().contains("NonDurableIdentity"))
        {
            return;
        }
        try
        {
            // Persist some "nondurable" objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                List entries = new ArrayList();
                LogEntry entry = new LogEntry(LogEntry.WARNING, "Datastore adapter not found. Falling back to default");
                entries.add(entry);
                entries.add(new LogEntry(LogEntry.ERROR, "No datastore specified"));
                entries.add(new LogEntry(LogEntry.INFO, "Object X1 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object X2 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object Y1 persisted"));
                entries.add(new LogEntry(LogEntry.ERROR, "Error persisting object Y2"));
                pm.makePersistentAll(entries);

                tx.commit();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception during persist", e);
                fail("Exception during persist : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve some objects and update one
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            String msgOfUpdated = null;
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + LogEntry.class.getName() + " WHERE level == 2");
                List results = (List)q.execute();
                assertEquals("Number of LogEntry objects retrieved via Query (ERROR) is incorrect", 2, results.size());

                // Update the first one
                LogEntry entry = (LogEntry)results.iterator().next();
                Object id = JDOHelper.getObjectId(entry);
                assertNotNull("Identity of nondurable object retrieved from Query is null!", id);
                msgOfUpdated = entry.getMessage();
                entry.setLevel(LogEntry.WARNING); // Downgrade to warning

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while updating field of nondurable object", e);
                fail("Exception thrown while updating field of nondurable object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check result
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + LogEntry.class.getName() + " WHERE message == :val");
                List results = (List)q.execute(msgOfUpdated);
                assertEquals("Number of LogEntry objects retrieved via Query is incorrect", 1, results.size());

                LogEntry entry = (LogEntry)results.iterator().next();
                assertEquals("Level of updated object is incorrect", LogEntry.WARNING, entry.getLevel());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while checking field of nondurable object", e);
                fail("Exception thrown while checking field of nondurable object : " + e.getMessage());
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
            // Clear out our data
            clean(LogEntry.class);
        }
    }

    /**
     * Method to test the delete of nondurable objects.
     */
    public void testDelete()
    {
        if (!storeMgr.getSupportedOptions().contains("NonDurableIdentity"))
        {
            return;
        }
        try
        {
            // Persist some "nondurable" objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                List entries = new ArrayList();
                LogEntry entry = new LogEntry(LogEntry.WARNING, "Datastore adapter not found. Falling back to default");
                entries.add(entry);
                entries.add(new LogEntry(LogEntry.ERROR, "No datastore specified"));
                entries.add(new LogEntry(LogEntry.INFO, "Object X1 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object X2 persisted"));
                entries.add(new LogEntry(LogEntry.WARNING, "Object Y1 persisted"));
                entries.add(new LogEntry(LogEntry.ERROR, "Error persisting object Y2"));
                pm.makePersistentAll(entries);

                tx.commit();
            }
            catch (Exception e)
            {
                NucleusLogger.GENERAL.error(">> Exception during persist", e);
                fail("Exception during persist : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve some objects and update one
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery("SELECT FROM " + LogEntry.class.getName() + " WHERE level == 2");
                List results = (List)q.execute();
                assertEquals("Number of LogEntry objects retrieved via Query (ERROR) is incorrect", 2, results.size());

                // Delete the first one
                LogEntry entry = (LogEntry)results.iterator().next();
                Object id = JDOHelper.getObjectId(entry);
                assertNotNull("Identity of nondurable object retrieved from Query is null!", id);
                pm.deletePersistent(entry);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while deleting nondurable object", e);
                fail("Exception thrown while deleting nondurable object : " + e.getMessage());
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
            // Clear out our data
            clean(LogEntry.class);
        }
    }
}