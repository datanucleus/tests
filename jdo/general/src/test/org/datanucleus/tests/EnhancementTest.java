/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributions :
    ...
***********************************************************************/
package org.datanucleus.tests;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.enhancement.EnhancerFieldNames;

/**
 * Test the enhancement contract features.
 */
public class EnhancementTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public EnhancementTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    EnhancerFieldNames.class,
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of field names starting "dn" but not enhancer fields.
     */
    public void testFieldsStartingDN()
    throws Exception
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();

                EnhancerFieldNames e1 = new EnhancerFieldNames(1, "First", "MyDNId", "some dns name");
                pm.makePersistent(e1);
                
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

                EnhancerFieldNames e = pm.getObjectById(EnhancerFieldNames.class, 1);
                assertEquals("First", e.getName());
                assertEquals("MyDNId", e.getDnId());
                assertEquals("some dns name", e.getDnsName());

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
            clean(EnhancerFieldNames.class);
        }
    }
}