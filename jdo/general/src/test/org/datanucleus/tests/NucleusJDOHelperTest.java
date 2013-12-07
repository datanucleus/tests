/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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

import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Employee;

/**
 * Series of tests for NucleusJDOHelper.
 */
public class NucleusJDOHelperTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public NucleusJDOHelperTest(String name)
    {
        super(name);

        if (!initialised)
        {
            // Add all classes required for abstract FCO tests
            addClassesToSchema(new Class[]
                {
                    Employee.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Check the return of object state values.
     **/ 
    public void testObjectState()
    throws Exception
    {
        try
        {
            Employee emp = new Employee(1, "Donald" , "Duck", "donald.duck@warnerbros.com", 123, "ABCD");
            assertEquals("Expected state is incorrect", ObjectState.TRANSIENT, JDOHelper.getObjectState(emp));

            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            
            try
            {
                tx.begin();

                // Persist object and it moves to "persistent-new"
                Employee emp1 = (Employee)pm.makePersistent(emp);
                assertEquals("Expected state is incorrect", ObjectState.PERSISTENT_NEW, JDOHelper.getObjectState(emp1));

                tx.commit();

                // leave transaction and it moves to "hollow / persistent-nontransactional"
                assertEquals("Expected state is incorrect", ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL, JDOHelper.getObjectState(emp1));

                tx.begin();

                // Update a field and it moves to "persistent-dirty"
                emp1.setAge(28);
                assertEquals("Expected state is incorrect", ObjectState.PERSISTENT_DIRTY, JDOHelper.getObjectState(emp1));

                tx.commit();

                // leave transaction and it moves to "hollow / persistent-nontransactional"
                assertEquals("Expected state is incorrect", ObjectState.HOLLOW_PERSISTENT_NONTRANSACTIONAL, JDOHelper.getObjectState(emp1));

                tx.begin();

                pm.makeTransient(emp1);
                assertEquals("Expected state is incorrect", ObjectState.TRANSIENT, JDOHelper.getObjectState(emp1));

                tx.commit();

                tx.begin();
                // Persist it again
                emp1 = (Employee)pm.makePersistent(emp1);
                assertEquals("Expected state is incorrect", ObjectState.PERSISTENT_NEW, JDOHelper.getObjectState(emp1));

                // Detach it
                Employee detachedEmp = (Employee)pm.detachCopy(emp1);
                tx.commit();
                assertEquals("Expected state is incorrect", ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(detachedEmp));
                
                // Modify it so it moves to "detached-dirty"
                detachedEmp.setFirstName("Billy");
                assertEquals("Expected state is incorrect", ObjectState.DETACHED_DIRTY, JDOHelper.getObjectState(detachedEmp));
            }
            catch (Exception e)
            {
                
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
            clean(Employee.class);
        }
    }
}