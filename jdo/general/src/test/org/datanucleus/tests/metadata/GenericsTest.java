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
package org.datanucleus.tests.metadata;

import java.util.HashMap;
import java.util.HashSet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.generics.GenericsContainer;
import org.datanucleus.samples.generics.GenericsElement;
import org.datanucleus.samples.generics.GenericsValue;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Test for metadata when using JDK1.5 generics.
 * @version $Revision: 1.1 $
 */
public class GenericsTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public GenericsTest(String name)
    {
        super(name);
    }

    /**
     * Test for using JDK 1.5 generics and not specifying <collection>, <map> in MetaData
     */
    public void testCollectionMapGenerics()
    {
        try
        {
            Object contId = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                GenericsContainer cont = new GenericsContainer("MyContainer");

                GenericsElement elem1 = new GenericsElement("FirstElement");
                cont.addElement(elem1);
                GenericsElement elem2 = new GenericsElement("SecondElement");
                cont.addElement(elem2);

                GenericsValue val1 = new GenericsValue("FirstValue");
                cont.addEntry("1", val1);

                pm.makePersistent(cont);

                tx.commit();
                contId = JDOHelper.getObjectId(cont);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check what was persisted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                GenericsContainer cont = (GenericsContainer) pm.getObjectById(contId);
                HashSet<GenericsElement> elements = cont.getElements();
                HashMap<String, GenericsValue> valueMap = cont.getValueMap();

                assertNotNull("Elements in container was null but should have had 2 elements", elements);
                assertEquals("Number of elements in container is incorrect", 2, elements.size());

                assertNotNull("ValueMap in container was null but should have had 1 entry", valueMap);
                assertEquals("Number of entries in value map in container is incorrect", 1, valueMap.size());

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
            clean(GenericsContainer.class);
            clean(GenericsElement.class);
            clean(GenericsValue.class);
        }
    }
}