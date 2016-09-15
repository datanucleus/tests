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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.generics.GenericsBase;
import org.datanucleus.samples.generics.GenericsBaseSubRelated;
import org.datanucleus.samples.generics.GenericsBaseSubSub;
import org.datanucleus.samples.generics.GenericsContainer;
import org.datanucleus.samples.generics.GenericsElement;
import org.datanucleus.samples.generics.GenericsValue;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Test use of Java generics.
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
     * Test for using Java generics and not specifying <collection>, <map> in MetaData
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

    /**
     * Test for using Java generics in the inheritance tree.
     */
    public void testInheritanceGenerics()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                GenericsBaseSubSub sub = new GenericsBaseSubSub();
                sub.setId(1l);
                sub.setName("First Sub");
                sub.setLongValue(101l);
                GenericsBaseSubRelated rel = new GenericsBaseSubRelated();
                rel.setId(150);
                rel.setBaseSub(sub);
                sub.setRelated(rel);
                pm.makePersistent(rel);

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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(GenericsBase.class);
                List<GenericsBase> results = q.executeList();
                assertEquals(1, results.size());
                GenericsBase base = results.get(0);
                assertNotNull(base);
                assertTrue(base instanceof GenericsBaseSubSub);
                GenericsBaseSubSub sub = (GenericsBaseSubSub)base;
                assertEquals("First Sub", sub.getName());
                assertEquals(101l, sub.getLongValue());
                assertEquals(new Long(1), sub.getId());
                GenericsBaseSubRelated rel = sub.getRelated();
                assertNotNull(rel);
                assertEquals(150, rel.getId());
                assertEquals(rel.getBaseSub(), sub);

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
            clean(GenericsBaseSubRelated.class);
            clean(GenericsBaseSubSub.class);
        }        
    }
}