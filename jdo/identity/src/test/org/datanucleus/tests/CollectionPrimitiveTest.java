/**********************************************************************
Copyright (c) 2003 Erik Bengtson and others. All rights reserved.
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
2005 Andy Jefferson - added test for Non-PC collection without join
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.Iterator;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.one_many.collection.SetHolder;

/**
 * Tests persistence of Collections of NonPC objects.
 * @version $Revision: 1.1 $
 */
public class CollectionPrimitiveTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the test case.
     */
    public CollectionPrimitiveTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    SetHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of collections of Strings and Dates persisted into JoinTable.
     **/
    public void testCollectionNonPCUsingJoinTable()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Object id = null;
            try
            {
                tx.begin();

                SetHolder coll = new SetHolder();
                coll.getJoinSetNonPC1().add(new String("First string"));
                coll.getJoinSetNonPC1().add(new String("Second string"));
                coll.getJoinSetNonPC1().add(new String("Third string"));
                coll.getJoinSetNonPC2().add(new java.util.Date(0));
                coll.getJoinSetNonPC2().add(new java.util.Date(120000000));
                coll.getJoinSetNonPC2().add(new java.util.Date());

                pm.makePersistent(coll);
                id = pm.getObjectId(coll);

                tx.commit();

                // Retrieve the container object
                tx.begin();

                coll = (SetHolder) pm.getObjectById(id, false);
                assertNotNull("SetHolder", coll);

                Collection dates = coll.getJoinSetNonPC2();
                assertNotNull("Date Collection", dates);
                assertEquals("Expected number of dates", 3, dates.size());

                Iterator iter = dates.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a Date." + o, o instanceof java.util.Date);
                }

                Collection strings = coll.getJoinSetNonPC1();
                assertNotNull("String collection", strings);
                assertEquals("strings.size()", 3, strings.size());

                iter = strings.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a String." + o, o instanceof String);
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
            // Clean out the data we have created
            clean(SetHolder.class);
        }
    }

    /**
     * Test of collections of Strings and Dates persisted without join table, hence serialised.
     **/
    public void testCollectionNonPCWithoutJoinTable()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Object id = null;
            try
            {
                tx.begin();

                SetHolder coll = new SetHolder();
                coll.getSetNonPC1().add(new String("First string"));
                coll.getSetNonPC1().add(new String("Second string"));
                coll.getSetNonPC1().add(new String("Third string"));
                coll.getSetNonPC2().add(new java.util.Date(0));
                coll.getSetNonPC2().add(new java.util.Date(120000000));
                coll.getSetNonPC2().add(new java.util.Date());

                pm.makePersistent(coll);
                id = pm.getObjectId(coll);

                tx.commit();

                // Retrieve the container object
                tx.begin();

                coll = (SetHolder) pm.getObjectById(id, false);
                assertNotNull("CollectionNonPC", coll);

                Collection dates = coll.getSetNonPC2();
                assertNotNull("Date Collection", dates);
                assertEquals("Expected number of dates", 3, dates.size());

                Iterator iter = dates.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a Date." + o, o instanceof java.util.Date);
                }

                Collection strings = coll.getSetNonPC1();
                assertNotNull("String collection", strings);
                assertEquals("strings.size()", 3, strings.size());

                iter = strings.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a String." + o, o instanceof String);
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
            // Clean out the data we have created
            clean(SetHolder.class);
        }
    }

    /**
     * Test of collections of Strings and Dates persisted serialised.
     **/
    public void testCollectionNonPCSerialised()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Object id = null;
            try
            {
                tx.begin();

                SetHolder coll = new SetHolder();
                coll.getSetNonPCSerial1().add(new String("First string"));
                coll.getSetNonPCSerial1().add(new String("Second string"));
                coll.getSetNonPCSerial1().add(new String("Third string"));
                coll.getSetNonPCSerial2().add(new java.util.Date(0));
                coll.getSetNonPCSerial2().add(new java.util.Date(120000000));
                coll.getSetNonPCSerial2().add(new java.util.Date());

                pm.makePersistent(coll);
                id = pm.getObjectId(coll);

                tx.commit();

                // Retrieve the container object
                tx.begin();

                coll = (SetHolder) pm.getObjectById(id, false);
                assertNotNull("SetHolder", coll);

                Collection dates = coll.getSetNonPCSerial2();
                assertNotNull("Date Collection", dates);
                assertEquals("Expected number of dates", 3, dates.size());

                Iterator iter = dates.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a Date." + o, o instanceof java.util.Date);
                }

                Collection strings = coll.getSetNonPCSerial1();
                assertNotNull("String collection", strings);
                assertEquals("strings.size()", 3, strings.size());

                iter = strings.iterator();
                while (iter.hasNext())
                {
                    Object o = iter.next();
                    assertTrue("object [" + o + "] should be a String." + o, o instanceof String);
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
            // Clean out the data we have created
            clean(SetHolder.class);
        }
    }
}