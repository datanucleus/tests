/**********************************************************************
Copyright (c) 2002 Mike Martin (TJDO) and others. All rights reserved.
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
***********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.exceptions.NucleusDataStoreException;
import org.datanucleus.samples.rdbms.views.CircularReferenceView1;
import org.datanucleus.samples.rdbms.views.CircularReferenceView2;
import org.datanucleus.samples.rdbms.views.CircularReferenceView3;
import org.datanucleus.samples.rdbms.views.DependentView;
import org.datanucleus.samples.rdbms.views.FNameView;
import org.datanucleus.samples.rdbms.views.FNameView2;
import org.datanucleus.samples.rdbms.views.MinMaxWidgetValues;
import org.datanucleus.samples.rdbms.views.NameObject;
import org.datanucleus.samples.rdbms.views.ReliedOnView;
import org.datanucleus.samples.rdbms.views.SetWidgetCounts;
import org.datanucleus.samples.widget.DateWidget;
import org.datanucleus.samples.widget.DecimalWidget;
import org.datanucleus.samples.widget.ElementWidget;
import org.datanucleus.samples.widget.FloatWidget;
import org.datanucleus.samples.widget.HashSetWidget;
import org.datanucleus.samples.widget.SetWidget;
import org.datanucleus.samples.widget.StringWidget;
import org.datanucleus.samples.widget.Widget;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.junit.BeforeClass;

/**
 * Tests the functionality of view objects.
 * TODO Rewrite this using our standard classes and with view code that works on many RDBMS.
 */
public class ViewTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    StorageTester tester = null;
    
    @BeforeClass
    public static void runTests() 
    {
        skipWhen(!supportsViews(),"Database [" + rdbmsVendorID + "] does not support views, view tests not run");
    } 

    public ViewTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ReliedOnView.class,
                    DateWidget.class,
                    ElementWidget.class,
                    DecimalWidget.class,
                    DateWidget.class,
                    FloatWidget.class,
                    SetWidget.class,
                    HashSetWidget.class,
                    StringWidget.class,
                    Widget.class,
                    SetWidgetCounts.class
                }
            );

            // Can't create this view on SQL Server because it doesn't allow you
            // to GROUP BY a bit column.
            if (!"sqlserver".equals(rdbmsVendorID))
            {
                addClassesToSchema(new Class[] { MinMaxWidgetValues.class });
            }
            initialised = true;
        }
        tester = new StorageTester(pmf);
    }

    protected static boolean supportsViews() 
    {
        return ((RDBMSStoreManager)storeMgr).getDatastoreAdapter().supportsOption(DatastoreAdapter.VIEWS);
    }

    /**
     * Use of a simple view for an object, using "nondurable" identity.
     */
    public void testNameView()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(new NameObject(1, "FIRST"));
                pm.makePersistent(new NameObject(2, "SECOND"));
                pm.makePersistent(new NameObject(3, "THIRD"));
                pm.makePersistent(new NameObject(4, "FOURTH"));
                tx.commit();

                tx.begin();
                Query q = pm.newQuery("SELECT FROM " + NameObject.class.getName() + " ORDER BY id");
                List<NameObject> persons = (List<NameObject>) q.execute();
                assertEquals(4, persons.size());
                assertEquals("FIRST", persons.get(0).getName());

                Query q1 = pm.newQuery("SELECT FROM " + FNameView.class.getName() + " ORDER BY id");
                List<FNameView> fNames = ((List<FNameView>)q1.execute());
                assertEquals(2, fNames.size());
                assertEquals("FIRST", fNames.get(0).getName());
                assertEquals("FOURTH", fNames.get(1).getName());

                tx.commit();
            }
            catch (Throwable thr)
            {
                LOG.error(">> Exception thrown persist/view data with FNameView", thr);
                fail("Failed to persist data : " + thr.getMessage());
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
            clean(NameObject.class);
        }
    }

    /**
     * Use of a simple view for an object, using application identity for the view objects.
     */
    public void testNameViewWithId()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(new NameObject(1, "FIRST"));
                pm.makePersistent(new NameObject(2, "SECOND"));
                pm.makePersistent(new NameObject(3, "THIRD"));
                pm.makePersistent(new NameObject(4, "FOURTH"));
                tx.commit();

                tx.begin();
                Query q = pm.newQuery("SELECT FROM " + NameObject.class.getName() + " ORDER BY id");
                List<NameObject> persons = (List<NameObject>) q.execute();
                assertEquals(4, persons.size());
                assertEquals("FIRST", persons.get(0).getName());

                Query q1 = pm.newQuery("SELECT FROM " + FNameView2.class.getName() + " ORDER BY id");
                List<FNameView2> fNames = ((List<FNameView2>)q1.execute());
                assertEquals(2, fNames.size());
                assertEquals("FIRST", fNames.get(0).getName());
                assertEquals("FOURTH", fNames.get(1).getName());
                tx.commit();
            }
            catch (Throwable thr)
            {
                LOG.error(">> Exception thrown persist/view data with FNameView2", thr);
                fail("Failed to persist data : " + thr.getMessage());
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
            clean(NameObject.class);
        }
    }

    public void testViewOfWidgets() 
    throws Exception
    {
        if ("sqlserver".equals(rdbmsVendorID))
        {
            // Can't run this test on SQL Server because it doesn't allow you to GROUP BY a bit column.
            return;
        }

        try
        {
            LOG.info("Testing view derived from of " + StorageTester.TEST_OBJECT_COUNT + " " + Widget.class.getName() + " objects");
            tester.insertObjects(Widget.class);

            MinMaxWidgetValues trueValues  = new MinMaxWidgetValues(true);
            MinMaxWidgetValues falseValues = new MinMaxWidgetValues(false);

            TestObject[] objs = tester.getObjects();
            for (int i = 0; i < objs.length; ++i)
            {
                Widget w = (Widget)objs[i];
                MinMaxWidgetValues tfv = w.getBooleanField() ? trueValues : falseValues;

                if (tfv.getMinByteValue() > w.getByteField())
                {
                    tfv.setMinByteValue(w.getByteField());
                }

                if (tfv.getMinShortValue() > w.getShortField())
                {
                    tfv.setMinShortValue(w.getShortField());
                }

                if (tfv.getMaxIntValue() < w.getIntField())
                {
                    tfv.setMaxIntValue(w.getIntField());
                }

                if (tfv.getMaxLongValue() < w.getLongField())
                {
                    tfv.setMaxLongValue(w.getLongField());
                }
            }

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                Extent ext = pm.getExtent(MinMaxWidgetValues.class, false);
                Iterator exti = ext.iterator();
                int count = 0;

                while (exti.hasNext())
                {
                    MinMaxWidgetValues wv = (MinMaxWidgetValues)exti.next();
                    MinMaxWidgetValues tfv;

                    if (wv.getBooleanValue())
                    {
                        tfv = trueValues;
                        trueValues = null;
                    }
                    else
                    {
                        tfv = falseValues;
                        falseValues = null;
                    }

                    StorageTester.assertFieldsEqual(tfv, wv);
                    ++count;
                }

                assertEquals("Iteration over view extent returned wrong number of rows", 2, count);

                tx.commit();

                /*
                 * Negative test #1.  Ensure that an attempt to write a field
                 * throws the proper exception.
                 */
                try
                {
                    tx.begin();

                    MinMaxWidgetValues wv = (MinMaxWidgetValues)ext.iterator().next();
                    wv.fillRandom();

                    tx.commit();

                    fail("Writing to a persistent view object succeeded");
                }
                catch (Exception e)
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }

                /*
                 * Negative test #2.  Ensure that an attempt to make a view object
                 * persistent throws the proper exception.
                 */
                try
                {
                    tx.begin();

                    MinMaxWidgetValues wv = new MinMaxWidgetValues(true);
                    pm.makePersistent(wv);

                    tx.commit();

                    fail("Making a view object persistent succeeded");
                }
                catch (Exception e)
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }

                /*
                 * Negative test #3.  Ensure that an attempt to delete a view object
                 * throws the proper exception.
                 */
                try
                {
                    tx.begin();

                    MinMaxWidgetValues wv = (MinMaxWidgetValues)ext.iterator().next();
                    pm.deletePersistent(wv);

                    tx.commit();

                    fail("Deleting a persistent view object succeeded");
                }
                catch (Exception e)
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }
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
            tester.removeObjects();
            clean(Widget.class);
            clean(MinMaxWidgetValues.class);
        }
    }

    public void testViewOfSetWidgets() 
    throws Exception
    {
        /*
         * We can't run this test on Cloudscape because the view used by
         * SetWidgetCounts doesn't execute properly; some counts that should
         * be 0 come up 1.  This is presumably due to a bug in Cloudscape
         * (last tried on both 3.6 and 4.0).
         */
        if ("cloudscape".equals(rdbmsVendorID))
        {
            return;
        }

        try
        {
            LOG.info("Testing view derived from of " + StorageTester.TEST_OBJECT_COUNT + " " + SetWidget.class.getName() + " objects");
            tester.insertObjects(SetWidget.class);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                Extent ext = pm.getExtent(SetWidgetCounts.class, true);
                Iterator exti = ext.iterator();
                int count = 0;

                while (exti.hasNext())
                {
                    SetWidgetCounts actual = (SetWidgetCounts)exti.next();
                    SetWidgetCounts expected = new SetWidgetCounts(actual.getSetWidget());

                    StorageTester.assertFieldsEqual(expected, actual);
                    ++count;
                }

                tx.commit();

                assertEquals("Iteration over view extent returned wrong number of rows", StorageTester.TEST_OBJECT_COUNT, count);

                tx.begin();

                Query query = pm.newQuery(pm.getExtent(SetWidgetCounts.class, true));
                query.setFilter("normalSetSize != 0");
                query.setOrdering("sw.numElementWidgets descending");
                Collection results = (Collection)query.execute();

                TestObject[] objs = tester.getObjects();
                try
                {
                    HashSet expected = new HashSet();

                    for (int i = 0; i < objs.length; ++i)
                    {
                        SetWidget sw = (SetWidget)objs[i];
                        if (sw.getNormalSet().size() != 0)
                        {
                            expected.add(new SetWidgetCounts(sw));
                        }
                    }

                    assertTrue("Query has no expected results (test is broken)", !expected.isEmpty());
                    assertTrue("Query returned no rows", !results.isEmpty());

                    HashSet actual = new HashSet(results);

                    assertEquals("Query returned duplicate rows", results.size(), actual.size());
                    assertTrue("Query did not return expected results: expected " + expected + ", but was " + actual, StorageTester.compareSet(expected, actual));
                }
                finally
                {
                    query.closeAll();
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
        catch (JDOException e)
        {
            LOG.error(">> Exception during test", e);
            fail("Exception occurred during test : " + e.getMessage());
        }
        finally
        {
            tester.removeObjects();
            clean(Widget.class);
        }
    }

    /**
     * Test creating views with the dependent view created first.
     */
    public void testCreatingDependentFirst()
    {
        addClassesToSchema(new Class[]{DependentView.class, ReliedOnView.class});
    }

    /**
     * Test creating views with the relied on view created first.
     */
    public void testCreatingReliedOnFirst()
    {
        addClassesToSchema(new Class[]{ReliedOnView.class, DependentView.class});
    }

    /**
     * Test that creating views with circular dependencies fails.
     * Note that some RDBMS have syntax in VIEW creation that can force creation even if a dependent table/view isn't yet there.
     */
    public void testCircularViewDependencies()
    {
        /*
         * Try creating with one view.  Others should be pulled in through reference.
         */
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView1.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView2.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView3.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        /*
         * Try creating with two views.  Other should be pulled in through reference.
         */
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView1.class, CircularReferenceView2.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView2.class, CircularReferenceView1.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView1.class, CircularReferenceView3.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView3.class, CircularReferenceView1.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView2.class, CircularReferenceView3.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView3.class, CircularReferenceView2.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        /*
         * Try creating with three views.
         */
        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView1.class, CircularReferenceView2.class, CircularReferenceView3.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView1.class, CircularReferenceView3.class, CircularReferenceView2.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView2.class, CircularReferenceView1.class, CircularReferenceView3.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView2.class, CircularReferenceView3.class, CircularReferenceView1.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView3.class, CircularReferenceView1.class, CircularReferenceView2.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }

        try
        {
            addClassesToSchema(new Class[]{CircularReferenceView3.class, CircularReferenceView2.class, CircularReferenceView1.class});
            fail("Creating a view with a circular dependency should have thrown exception.");
        }
        catch (NucleusDataStoreException e)
        { /* Ignore */
        }
    }
}