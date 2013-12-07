/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.cls.ClassHolder;

/**
 * Tests for Class field types.
 */
public class ClassTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public ClassTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ClassHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence of Class type.
     */
    public void testBasicPersistence() throws Exception
    {
        try
        {
            ClassHolder holder = new ClassHolder();
            holder.setCls(JDOHelper.class);
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(holder);
                id = JDOHelper.getObjectId(holder);
                ClassHolder holder2 = (ClassHolder) pm.getObjectById(id, true);
                pm.refresh(holder2);
                assertEquals(JDOHelper.class, holder2.getCls());
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
            pmf.getDataStoreCache().evictAll();
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ClassHolder holder2 = (ClassHolder) pm.getObjectById(id, true);
                assertEquals(JDOHelper.class, holder2.getCls());
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
            clean(ClassHolder.class);
        }
    }
}