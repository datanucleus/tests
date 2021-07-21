/**********************************************************************
Copyright (c) 2018 Andy Jefferson and others. All rights reserved. 
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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.array.ArrayElement;
import org.datanucleus.samples.array.PersistableArray;
import org.datanucleus.samples.one_many.collection.SetHolder;

/**
 * Series of tests for RDBMS "bulk fetch" handling.
 */
public class BulkFetchTest extends JDOPersistenceTestCase
{
    public BulkFetchTest()
    {
    }
    
    /**
     * Test for bulk fetch of a field which is an array of persistables (using a join table).
     */
    public void testArrayJoin()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ArrayElement el1 = new ArrayElement("1", "Element1");
                ArrayElement el2 = new ArrayElement("2", "Element2");
                PersistableArray pa1 = new PersistableArray(null, new ArrayElement[] {el1, el2});
                PersistableArray pa2 = new PersistableArray(null, null);
                pm.makePersistent(pa1);
                pm.makePersistent(pa2);

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

                pm.getFetchPlan().addGroup("arr2");
                Query q = pm.newQuery("SELECT FROM " + PersistableArray.class.getName());
                List<PersistableArray> results = q.executeList();
                assertEquals("Number of results is wrong", 2, results.size());
                Iterator<PersistableArray> resIter = results.iterator();
                PersistableArray rpa1 = resIter.next();
                PersistableArray rpa2 = resIter.next();
                Object arr1 = rpa1.getArray2();
                Object arr2 = rpa2.getArray2();
                if (arr1 == null && arr2 != null)
                {
                    assertEquals(2, Array.getLength(arr2));
                }
                else if (arr1 != null && arr2 == null)
                {
                    assertEquals(2, Array.getLength(arr1));
                }
                else
                {
                    fail("Either both arrays are null, or both are non-null. Incorrect");
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
            // Clean out our data
            clean(PersistableArray.class);
            clean(ArrayElement.class);
        }
    }

    /**
     * Test for bulk fetch of a field which is a collection of non-persistables (using a join table).
     */
    public void testCollectionNonPCJoin()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SetHolder sh1 = new SetHolder("First holder");
                sh1.getJoinSetNonPC1().add("A");
                sh1.getJoinSetNonPC1().add("B");
                SetHolder sh2 = new SetHolder("Second holder");
                sh2.getJoinSetNonPC1().add("C");
                sh2.getJoinSetNonPC1().add("D");
                pm.makePersistent(sh1);
                pm.makePersistent(sh2);

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

                pm.getFetchPlan().addGroup("joinSetNonPC1");
                Query q = pm.newQuery("SELECT FROM " + SetHolder.class.getName());
                List<SetHolder> results = q.executeList();
                assertEquals("Number of results is wrong", 2, results.size());
                Iterator<SetHolder> resIter = results.iterator();
                SetHolder rsh1 = resIter.next();
                SetHolder rsh2 = resIter.next();
                Set<String> setstring1 = rsh1.getJoinSetNonPC1();
                Set<String> setstring2 = rsh2.getJoinSetNonPC1();
                if (rsh1.getName().equals("First holder"))
                {
                    assertEquals(2, setstring1.size());
                    assertTrue(setstring1.contains("A"));
                    assertTrue(setstring1.contains("B"));
                }
                else if (rsh1.getName().equals("Second holder"))
                {
                    assertEquals(2, setstring1.size());
                    assertTrue(setstring1.contains("C"));
                    assertTrue(setstring1.contains("D"));
                }
                if (rsh2.getName().equals("First holder"))
                {
                    assertEquals(2, setstring2.size());
                    assertTrue(setstring2.contains("A"));
                    assertTrue(setstring2.contains("B"));
                }
                else if (rsh2.getName().equals("Second holder"))
                {
                    assertEquals(2, setstring2.size());
                    assertTrue(setstring2.contains("C"));
                    assertTrue(setstring2.contains("D"));
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
            // Clean out our data
            clean(SetHolder.class);
        }
    }
}