/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
2004 Andy Jefferson - added table creation in constructor
2005 Andy Jefferson - added bidirectional dependent test
2005 Andy Jefferson - added interface dependent test
    ...
**********************************************************************/
package org.datanucleus.tests.application;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.dependentfield.DependentHolder2;
import org.jpox.samples.dependentfield.SimpleDependentElement;

/**
 * Tests for the metadata dependent
 * <ul>
 * <li>dependent</li>
 * <li>dependent-element</li>
 * <li>dependent-value</li>
 * <li>dependent-key</li>
 * </ul> 
 */
public class DependentFieldTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test
     */
    public DependentFieldTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                    {
                        DependentHolder2.class,
                        SimpleDependentElement.class
                    });
            initialised = true;
        }
    }

    /**
     * Test for deletion of an object that has a PK field as another object and dependent.
     */
    public void testDependentPkFieldsDeletion()
    {
        Object holderId = null;
        Object elementId = null;
    	try
        {
            // Populate the objects
            SimpleDependentElement element = new SimpleDependentElement(0);
            DependentHolder2 holder = new DependentHolder2(element);

            PersistenceManager pm = pmf.getPersistenceManager();
            try
            {
                // Persist the objects
                pm.currentTransaction().begin();
                pm.makePersistent(holder);
                pm.currentTransaction().commit();

                // Access the ids of the objects
                holderId = pm.getObjectId(holder);
                elementId = pm.getObjectId(element);

                pm.currentTransaction().begin();
                LOG.info(">> Retrieving Holder2");
                DependentHolder2 holder2 = (DependentHolder2) pm.getObjectById(holderId, true);

                // make sure persistence is fine
                assertNotNull(holder2);
                assertNotNull(holder2.getElement());

                // Delete the container object
                pm.deletePersistent(holder2);
                pm.currentTransaction().commit();

                // Check to make sure that the holder was deleted correctly
                pm.currentTransaction().begin();
                boolean success = false;
                try
                {
                    success = false;
                    holder2 = (DependentHolder2)pm.getObjectById(holderId, true);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                finally
                {
                    if (!success)
                    {
                        fail("holder should have been deleted");
                    }
                }

                // Check to make sure that the dependent element was deleted correctly
                try
                {
                    success = false;
                    pm.getObjectById(elementId, true);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                finally
                {
                    if (!success)
                    {
                        fail("dependent field should have been deleted");
                    }
                }

                pm.currentTransaction().commit();
            }
            finally
            {
                if (pm.currentTransaction().isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                try
                {
                    DependentHolder2 holder = (DependentHolder2)pm.getObjectById(holderId);
                    pm.deletePersistent(holder);
                }
                catch (JDOObjectNotFoundException onfe)
                {
                }
                tx.commit();
                tx.begin();
                try
                {
                    SimpleDependentElement element = (SimpleDependentElement)pm.getObjectById(elementId);
                    pm.deletePersistent(element);
                }
                catch (JDOObjectNotFoundException onfe)
                {
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
    }
}