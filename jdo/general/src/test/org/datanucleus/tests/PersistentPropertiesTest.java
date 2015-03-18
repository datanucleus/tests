/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.annotations.persistentproperties.BasePropertyType;
import org.datanucleus.samples.annotations.persistentproperties.RelatedPropertyType;
import org.datanucleus.samples.annotations.persistentproperties.SubPropertyType;
import org.datanucleus.samples.annotations.persistentproperties.SubRelatedPropertyType;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Placeholder for additional tests when using persistent properties.
 */
public class PersistentPropertiesTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PersistentPropertiesTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[] { 
                        BasePropertyType.class, 
                        SubPropertyType.class, 
                        RelatedPropertyType.class,
                        });
            initialised = true;
        }
    }

    /**
     * Test use of inherited persistent properties.
     */
    public void testInheritance() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist some objects
                tx.begin();

                SubPropertyType sub = new SubPropertyType(1, "First Subtype");
                sub.setDescription("First Description");
                RelatedPropertyType rel1 = new SubRelatedPropertyType(101, "First Related");
                sub.setRelated(rel1);
                pm.makePersistent(sub);

                BasePropertyType base = new BasePropertyType(2, "First Basetype");
                RelatedPropertyType rel2 = new RelatedPropertyType(102, "Second Related");
                base.setRelated(rel2);
                pm.makePersistent(base);

                tx.commit();
                pmf.getDataStoreCache().evictAll(); // Isolate caching

                // Query for the objects
                tx.begin();

                SubPropertyType theSub = pm.getObjectById(SubPropertyType.class, 1);
                assertNotNull(theSub);
                assertEquals(1, theSub.getId());
                assertEquals("First Subtype", theSub.getName());
                assertEquals("First Description", theSub.getDescription());
                assertNotNull(theSub.getRelated());
                assertEquals(101, theSub.getRelated().getId());

                BasePropertyType theBase = pm.getObjectById(BasePropertyType.class, 2);
                assertNotNull(theBase);
                assertEquals(2, theBase.getId());
                assertEquals("First Basetype", theBase.getName());
                assertNotNull(theBase.getRelated());
                assertEquals(102, theBase.getRelated().getId());

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
            clean(SubPropertyType.class);
            clean(RelatedPropertyType.class);
        }
    }
}