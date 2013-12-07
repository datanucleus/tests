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
package org.datanucleus.tests.datastore;

import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.abstractclasses.self.ComplexAssembly;
import org.jpox.samples.abstractclasses.self.Module;

/**
 * Series of tests for the persistence of Abstract classes.
 */
public class AbstractClassesTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public AbstractClassesTest(String name)
    {
        super(name);

        if (!initialised)
        {
            // Add all classes required for abstract FCO tests
            addClassesToSchema(new Class[]
                {
                    Module.class,
                    ComplexAssembly.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of a complex self-referencing assembly, with collections of abstract objects
     */
    public void testAbstractWithCollection()
    {
        Object id = null;
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                tx.begin();
                Module module = new Module();
                pm.makePersistent(module);
                tx.commit();
                id = pm.getObjectId(module);
                
                tx.begin();
                Extent extent = pm.getExtent(Module.class, true);
                Iterator mods = extent.iterator();
                Module mod = (Module) mods.next();
                mod.getRoot().traverse(1);
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
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Module mod = (Module)pm.getObjectById(id);
                mod.clearRoot(); // Clears all relations
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
            clean(Module.class);
            clean(ComplexAssembly.class);
        }
    }
}