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
package org.datanucleus.tests.knownbugs;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.jpox.samples.reachability.ReachableHolder;
import org.jpox.samples.reachability.ReachableItem;

/**
 * Series of tests for "persistence-by-reachability" that are known bugs and expected to fail.
 */
public class ReachabilityTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ReachabilityTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[] {
                    Boiler.class,
                    Timer.class,
                    Organisation.class,
                    Qualification.class,
                    ReachableHolder.class,
                    ReachableItem.class,
                });
        }
    }

    /**
     * Test for temporary reachability using a 1-1 unidirectional relation between 2 classes looking
     * at the object states. See JDO2 spec 12.6.7
     * See JIRA "NUCCORE-26"
     */
    public void testOneToOneUniTemporaryStates()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object qualId = null;
            Object orgId = null;
            try
            {
                tx.setOptimistic(true);
                tx.begin();

                // Create the objects of the 1-1 uni relation
                Qualification qual = new Qualification("ISO 2001 certificate number 123045");
                Organisation org = new Organisation("JPOX Corporation");
                qual.setOrganisation(org);

                // Check that both are transient
                assertTrue("Object state of new Qualification is incorrect", 
                    !JDOHelper.isPersistent(qual) && !JDOHelper.isNew(qual) && !JDOHelper.isDirty(qual));
                assertTrue("Object state of new Organisation is incorrect", 
                    !JDOHelper.isPersistent(org) && !JDOHelper.isNew(org) && !JDOHelper.isDirty(org));

                // Persist the Qualification (so the Organisation should be persisted too)
                pm.makePersistent(qual);

                // Check that both are persistent-new (JDO2 spec 12.6.7)
                assertTrue("Object state of newly persisted Qualification is incorrect",
                    JDOHelper.isPersistent(qual) && JDOHelper.isNew(qual) && JDOHelper.isDirty(qual));
                assertTrue("Object state of newly persisted (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org) && JDOHelper.isNew(org) && JDOHelper.isDirty(org));

                Organisation org2 = new Organisation("JPOX Consulting");
                qual.setOrganisation(org2);

                // Commit
                tx.commit();

                // Check that both are clean/hollow
                assertTrue("Object state of committed Qualification is incorrect",
                    JDOHelper.isPersistent(qual) && !JDOHelper.isNew(qual) && !JDOHelper.isDirty(qual));
                assertFalse("Object state of committed (by temp reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org) && !JDOHelper.isNew(org) && !JDOHelper.isDirty(org));
                assertTrue("Object state of committed (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org2) && !JDOHelper.isNew(org2) && !JDOHelper.isDirty(org2));
                assertEquals("Field value of former persistent-new-deleted (now transient) object has been changed", "JPOX Corporation", org.getName());
                qualId = pm.getObjectId(qual);
                orgId = pm.getObjectId(org2);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that the objects exist in the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Organisation org = (Organisation)queryByName(pm, Organisation.class, "JPOX Corporation");
                assertTrue("Organisation is in the datastore!", org == null);
                Organisation org2 = (Organisation)pm.getObjectById(orgId);
                assertTrue("Organisation 2 is not in the datastore!", org2 != null);
                Qualification qual = (Qualification)pm.getObjectById(qualId);
                assertTrue("Qualification is not in the datastore!", qual != null);

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
            // Clean out our data
            clean(Qualification.class);
            clean(Organisation.class);
        }
    }

    /**
     * Generic query for instances of the candidate class and its subclasses which have the specified name.
     * @param pm PersistenceManager to use
     * @param candidateClass the candidate class
     * @param name the name to find
     * @return the first instance found or <code>null</code> if none exists
     */
    private static Object queryByName(PersistenceManager pm, Class candidateClass, String name)
    {
        Query query = null;
        try
        {
            query = pm.newQuery(candidateClass, "name == param");
            query.declareImports("import java.lang.String;");
            query.declareParameters("java.lang.String param");
            Collection result = (Collection)query.execute(name);
            Iterator iter = result.iterator();
            if (iter.hasNext())
            {
                return iter.next();
            }
            else
            {
                return null;
            }
        }
        finally
        {
            if (query != null)
            {
                query.closeAll();
            }
        }
    }
}