/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
2007 Andy Jefferson - rewritten to match new samples
    ...
**********************************************************************/
package org.datanucleus.tests.application;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.compoundidentity.CompoundAbstractBase;
import org.jpox.samples.compoundidentity.CompoundBiSource1;
import org.jpox.samples.compoundidentity.CompoundBiTarget1;
import org.jpox.samples.compoundidentity.CompoundConcreteSub;
import org.jpox.samples.compoundidentity.CompoundDoubleTarget;
import org.jpox.samples.compoundidentity.CompoundHolder;
import org.jpox.samples.compoundidentity.CompoundMapTarget;
import org.jpox.samples.compoundidentity.CompoundMapTarget2;
import org.jpox.samples.compoundidentity.CompoundRelated;
import org.jpox.samples.compoundidentity.CompoundSingleTarget;
import org.jpox.samples.compoundidentity.CompoundSource1;
import org.jpox.samples.compoundidentity.CompoundSourceL1;
import org.jpox.samples.compoundidentity.CompoundSourceL2;
import org.jpox.samples.compoundidentity.CompoundSourceL3;
import org.jpox.samples.compoundidentity.CompoundSourceL4;
import org.jpox.samples.compoundidentity.CompoundSourceL5;
import org.jpox.samples.compoundidentity.CompoundTarget1;
import org.jpox.samples.compoundidentity.CompoundX1;
import org.jpox.samples.compoundidentity.CompoundX2;
import org.jpox.samples.compoundidentity.CompoundX3;
import org.jpox.samples.compoundidentity.CompoundX4;

/**
 * Series of tests for Compound Identity (identifying) relations.
 */
public class CompoundIdentityTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public CompoundIdentityTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[] {
                    CompoundSingleTarget.class,
                    CompoundDoubleTarget.class,
                    CompoundRelated.class,
                    CompoundHolder.class,
                    CompoundBiSource1.class,
                    CompoundBiTarget1.class,
                    CompoundSourceL1.class,
                    CompoundSourceL2.class,
                    CompoundSourceL3.class,
                    CompoundSourceL4.class,
                    CompoundSourceL5.class,
                    CompoundSource1.class,
                    CompoundTarget1.class,
                    CompoundMapTarget.class,
                    CompoundMapTarget2.class,
                    CompoundAbstractBase.class,
                    CompoundConcreteSub.class
                    });
            initialised = true;
        }
    }

    /**
     * Basic test of 1-1 uni relation using compound identity relation.
     */
    public void testOneToOneUniSingle()
    {
        try
        {
            Object idHolder1 = null;
            Object idHolder2 = null;
            Object idHolder3 = null;
            Object idTargets[] = new Object[6];
            CompoundSingleTarget targets[] = new CompoundSingleTarget[6];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {      
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundHolder holder2 = new CompoundHolder("Second Holder");
                CompoundHolder holder3 = new CompoundHolder("Third Holder");
                targets[0] = new CompoundSingleTarget(holder3, 1.0);
                targets[1] = new CompoundSingleTarget(holder3, 2.0);
                targets[2] = new CompoundSingleTarget(holder2, 3.0);
                targets[3] = new CompoundSingleTarget(holder2, 4.0);
                targets[4] = new CompoundSingleTarget(holder1, 5.0);
                targets[5] = new CompoundSingleTarget(holder1, 6.0);
                pm.makePersistentAll(targets);
                tx.commit();
                idHolder1 = pm.getObjectId(holder1);
                idHolder2 = pm.getObjectId(holder2);
                idHolder3 = pm.getObjectId(holder3);
                for (int i=0; i<targets.length; i++)
                {
                    idTargets[i] = pm.getObjectId(targets[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                CompoundHolder holder1 = (CompoundHolder) pm.getObjectById(idHolder1, true);
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(idHolder2, true);
                CompoundHolder holder3 = (CompoundHolder) pm.getObjectById(idHolder3, true);
                assertEquals("Name of holder was incorrect", "First Holder", holder1.getName());
                assertEquals("Name of holder was incorrect", "Second Holder", holder2.getName());
                assertEquals("Name of holder was incorrect", "Third Holder", holder3.getName());

                for (int i = 0; i < targets.length; i++)
                {
                    CompoundSingleTarget target = (CompoundSingleTarget) pm.getObjectById(idTargets[i], false);
                    assertEquals(i + 1, target.getValue(), 0);
                    if (i == 0 || i == 1)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Third Holder", target.getHolder().getName());
                    }
                    if (i == 2 || i == 3)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Second Holder", target.getHolder().getName());
                    }
                    if (i == 4 || i == 5)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "First Holder", target.getHolder().getName());
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundSingleTarget.class);
            clean(CompoundHolder.class);
        }
    }

    /**
     * Basic test for persistence/retrieval using 1-1 uni relation with 2 source objects in target PK.
     */
    public void testOneToOneUniDouble()
    {
        try
        {
            Object idHolder1 = null;
            Object idHolder2 = null;
            Object idHolder3 = null;
            Object idTargets[] = new Object[6];
            CompoundDoubleTarget targets[] = new CompoundDoubleTarget[6];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundHolder holder2 = new CompoundHolder("Second Holder");
                CompoundHolder holder3 = new CompoundHolder("Third Holder");
                CompoundRelated related1 = new CompoundRelated("First Related");
                CompoundRelated related2 = new CompoundRelated("Second Related");
                CompoundRelated related3 = new CompoundRelated("Third Related");
                targets[0] = new CompoundDoubleTarget(holder3, related2, 1.0);
                targets[1] = new CompoundDoubleTarget(holder3, related1, 2.0);
                targets[2] = new CompoundDoubleTarget(holder2, related3, 3.0);
                targets[3] = new CompoundDoubleTarget(holder2, related1, 4.0);
                targets[4] = new CompoundDoubleTarget(holder1, related3, 5.0);
                targets[5] = new CompoundDoubleTarget(holder1, related2, 6.0);
                pm.makePersistentAll(targets);
                tx.commit();
                idHolder1 = pm.getObjectId(holder1);
                idHolder2 = pm.getObjectId(holder2);
                idHolder3 = pm.getObjectId(holder3);
                for (int i = 0; i < targets.length; i++)
                {
                    idTargets[i] = pm.getObjectId(targets[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                CompoundHolder holder1 = (CompoundHolder) pm.getObjectById(idHolder1, true);
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(idHolder2, true);
                CompoundHolder holder3 = (CompoundHolder) pm.getObjectById(idHolder3, true);

                assertEquals("Name of holder is incorrect", "First Holder", holder1.getName());
                assertEquals("Name of holder is incorrect", "Second Holder", holder2.getName());
                assertEquals("Name of holder is incorrect", "Third Holder", holder3.getName());

                for (int i=0; i<targets.length; i++)
                {
                    CompoundDoubleTarget target = (CompoundDoubleTarget) pm.getObjectById(idTargets[i], false);
                    assertEquals(i + 1, target.getValue(), 0);
                    if (i == 0)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Third Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Second Related", target.getRelated().getName());
                    }
                    else if (i == 1)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Third Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "First Related", target.getRelated().getName());
                    }
                    else if (i == 2)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Second Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Third Related", target.getRelated().getName());
                    }
                    else if (i == 3)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Second Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "First Related", target.getRelated().getName());
                    }
                    else if (i == 4)
                    {
                        assertEquals("Name of holder is incorrect",
                            "First Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Third Related", target.getRelated().getName());
                    }
                    else if (i == 5)
                    {
                        assertEquals("Name of holder is incorrect",
                            "First Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Second Related", target.getRelated().getName());
                    }
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
            clean(CompoundHolder.class);
        }
    }

    /**
     * Basic test for JDOQL using 1-1 uni relation with 2 source objects in target PK.
     */
    public void testOneToOneUniDoubleQuery()
    {
        try
        {
            Object idTargets[] = new Object[6];
            CompoundDoubleTarget targets[] = new CompoundDoubleTarget[6];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundHolder holder2 = new CompoundHolder("Second Holder");
                CompoundHolder holder3 = new CompoundHolder("Third Holder");
                CompoundRelated related1 = new CompoundRelated("First Related");
                CompoundRelated related2 = new CompoundRelated("Second Related");
                CompoundRelated related3 = new CompoundRelated("Third Related");
                targets[0] = new CompoundDoubleTarget(holder3, related2, 1.0);
                targets[1] = new CompoundDoubleTarget(holder3, related1, 2.0);
                targets[2] = new CompoundDoubleTarget(holder2, related3, 3.0);
                targets[3] = new CompoundDoubleTarget(holder2, related1, 4.0);
                targets[4] = new CompoundDoubleTarget(holder1, related3, 5.0);
                targets[5] = new CompoundDoubleTarget(holder1, related2, 6.0);
                pm.makePersistentAll(targets);
                tx.commit();
                for (int i = 0; i < targets.length; i++)
                {
                    idTargets[i] = pm.getObjectId(targets[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                Query q = pm.newQuery(CompoundDoubleTarget.class);
                q.setOrdering("value ascending");
                Iterator iterator = ((Collection)q.execute()).iterator();
                for (int i=0; iterator.hasNext(); i++)
                {
                    targets[i] = (CompoundDoubleTarget)iterator.next();
                }

                for (int i=0; i<targets.length; i++)
                {
                    CompoundDoubleTarget target = targets[i];
                    assertEquals(i + 1, target.getValue(), 0);
                    if (i == 0)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Third Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Second Related", target.getRelated().getName());
                    }
                    else if (i == 1)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Third Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "First Related", target.getRelated().getName());
                    }
                    else if (i == 2)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Second Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Third Related", target.getRelated().getName());
                    }
                    else if (i == 3)
                    {
                        assertEquals("Name of holder is incorrect",
                            "Second Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "First Related", target.getRelated().getName());
                    }
                    else if (i == 4)
                    {
                        assertEquals("Name of holder is incorrect",
                            "First Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Third Related", target.getRelated().getName());
                    }
                    else if (i == 5)
                    {
                        assertEquals("Name of holder is incorrect",
                            "First Holder", target.getHolder().getName());
                        assertEquals("Name of related is incorrect",
                            "Second Related", target.getRelated().getName());
                    }
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
            clean(CompoundHolder.class);
        }
    }

    /**
     * Test of compound identity 1-N relations using join table with single compound elements.
     */
    public void testOneToManyUniJoinTableSingle()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            CompoundSingleTarget elements[] = new CompoundSingleTarget[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                elements[0] = new CompoundSingleTarget(holder1, 1.0);
                elements[1] = new CompoundSingleTarget(holder1, 2.0);
                elements[2] = new CompoundSingleTarget(holder1, 3.0);
                elements[3] = new CompoundSingleTarget(holder1, 4.0);
                elements[4] = new CompoundSingleTarget(holder1, 5.0);
                elements[5] = new CompoundSingleTarget(holder1, 6.0);
                holder1.getList1().add(elements[0]);
                holder1.getList1().add(elements[1]);
                holder1.getList1().add(elements[2]);
                holder1.getList1().add(elements[3]);
                holder1.getList1().add(elements[4]);
                holder1.getList1().add(elements[5]);
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                tx.begin();
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder2.getList1().size());
                assertEquals(elements[0], holder2.getList1().get(0));
                assertEquals(elements[1], holder2.getList1().get(1));
                assertEquals(elements[2], holder2.getList1().get(2));
                assertEquals(elements[3], holder2.getList1().get(3));
                assertEquals(elements[4], holder2.getList1().get(4));
                assertEquals(elements[5], holder2.getList1().get(5));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundHolder.class);
            clean(CompoundSingleTarget.class);
        }
    }

    /**
     * Test of compound identity 1-N relations using join table with double compound elements.
     */
    public void testOneToManyUniJoinTableDouble()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            CompoundDoubleTarget elements[] = new CompoundDoubleTarget[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundRelated related1 = new CompoundRelated("First Related");
                elements[0] = new CompoundDoubleTarget(holder1, related1, 1.0);
                elements[1] = new CompoundDoubleTarget(holder1, related1, 2.0);
                elements[2] = new CompoundDoubleTarget(holder1, related1, 3.0);
                elements[3] = new CompoundDoubleTarget(holder1, related1, 4.0);
                elements[4] = new CompoundDoubleTarget(holder1, related1, 5.0);
                elements[5] = new CompoundDoubleTarget(holder1, related1, 6.0);
                holder1.getList2().add(elements[0]);
                holder1.getList2().add(elements[1]);
                holder1.getList2().add(elements[2]);
                holder1.getList2().add(elements[3]);
                holder1.getList2().add(elements[4]);
                holder1.getList2().add(elements[5]);
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                tx.begin();
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder2.getList2().size());
                assertEquals(elements[0], holder2.getList2().get(0));
                assertEquals(elements[1], holder2.getList2().get(1));
                assertEquals(elements[2], holder2.getList2().get(2));
                assertEquals(elements[3], holder2.getList2().get(3));
                assertEquals(elements[4], holder2.getList2().get(4));
                assertEquals(elements[5], holder2.getList2().get(5));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                Extent ex = pm.getExtent(CompoundHolder.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    CompoundHolder holder = (CompoundHolder)iter.next();
                    holder.getList1().clear();
                    holder.getList2().clear();
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
            clean(CompoundHolder.class);
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
        }
    }

    /**
     * Test for detach-attach of 1-N uni join table compound relations.
     */
    public void testOneToManyUniJoinTableDetachAttach()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            Object elements_single[] = new Object[6];
            Object elements_double[] = new Object[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundRelated related1 = new CompoundRelated("First Related");
                elements_single[0] = new CompoundSingleTarget(holder1, 1.0);
                elements_single[1] = new CompoundSingleTarget(holder1, 2.0);
                elements_single[2] = new CompoundSingleTarget(holder1, 3.0);
                elements_single[3] = new CompoundSingleTarget(holder1, 4.0);
                holder1.getList1().add(elements_single[0]);
                holder1.getList1().add(elements_single[1]);
                holder1.getList1().add(elements_single[2]);
                holder1.getList1().add(elements_single[3]);
                elements_double[0] = new CompoundDoubleTarget(holder1, related1, 1.0);
                elements_double[1] = new CompoundDoubleTarget(holder1, related1, 2.0);
                elements_double[2] = new CompoundDoubleTarget(holder1, related1, 3.0);
                elements_double[3] = new CompoundDoubleTarget(holder1, related1, 4.0);
                holder1.getList2().add(elements_double[0]);
                holder1.getList2().add(elements_double[1]);
                holder1.getList2().add(elements_double[2]);
                holder1.getList2().add(elements_double[3]);
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                // Detach the holder and elements
                tx.begin();
                pm.getFetchPlan().addGroup("detach");
                CompoundHolder holder2 = (CompoundHolder) pm.detachCopy(pm.getObjectById(id, true));
                tx.commit();

                // Add some more elements (single, double) to the holder
                elements_single[4] = new CompoundSingleTarget(holder1, 5.0);
                elements_single[5] = new CompoundSingleTarget(holder1, 6.0);
                holder2.getList1().add(elements_single[4]);
                holder2.getList1().add(elements_single[5]);
                elements_double[4] = new CompoundDoubleTarget(holder1, related1, 5.0);
                elements_double[5] = new CompoundDoubleTarget(holder1, related1, 6.0);
                holder2.getList2().add(elements_double[4]);
                holder2.getList2().add(elements_double[5]);

                // Attach the holder and elements
                tx.begin();
                pm.makePersistent(holder2);
                tx.commit();

                // Check the results
                tx.begin();
                CompoundHolder holder3 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder3.getList1().size());
                assertEquals(elements_single[0], holder3.getList1().get(0));
                assertEquals(elements_single[1], holder3.getList1().get(1));
                assertEquals(elements_single[2], holder3.getList1().get(2));
                assertEquals(elements_single[3], holder3.getList1().get(3));
                assertEquals(elements_single[4], holder3.getList1().get(4));
                assertEquals(elements_single[5], holder3.getList1().get(5));

                assertEquals(6, holder3.getList2().size());
                assertEquals(elements_double[0], holder3.getList2().get(0));
                assertEquals(elements_double[1], holder3.getList2().get(1));
                assertEquals(elements_double[2], holder3.getList2().get(2));
                assertEquals(elements_double[3], holder3.getList2().get(3));
                assertEquals(elements_double[4], holder3.getList2().get(4));
                assertEquals(elements_double[5], holder3.getList2().get(5));
                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                Extent ex = pm.getExtent(CompoundHolder.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    CompoundHolder holder = (CompoundHolder)iter.next();
                    holder.getList1().clear();
                    holder.getList2().clear();
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
            clean(CompoundHolder.class);
            clean(CompoundSingleTarget.class);
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
        }
    }

    /**
     * Test of compound identity 1-N relations using FK with double compound elements.
     */
    public void testOneToManyBiFKDouble()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            CompoundDoubleTarget elements[] = new CompoundDoubleTarget[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundRelated related1 = new CompoundRelated("First Related");
                elements[0] = new CompoundDoubleTarget(holder1, related1, 1.0);
                elements[1] = new CompoundDoubleTarget(holder1, related1, 2.0);
                elements[2] = new CompoundDoubleTarget(holder1, related1, 3.0);
                elements[3] = new CompoundDoubleTarget(holder1, related1, 4.0);
                elements[4] = new CompoundDoubleTarget(holder1, related1, 5.0);
                elements[5] = new CompoundDoubleTarget(holder1, related1, 6.0);
                holder1.getList3().add(elements[0]);
                holder1.getList3().add(elements[1]);
                holder1.getList3().add(elements[2]);
                holder1.getList3().add(elements[3]);
                holder1.getList3().add(elements[4]);
                holder1.getList3().add(elements[5]);
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                tx.begin();
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder2.getList3().size());
                assertEquals(elements[0], holder2.getList3().get(0));
                assertEquals(elements[1], holder2.getList3().get(1));
                assertEquals(elements[2], holder2.getList3().get(2));
                assertEquals(elements[3], holder2.getList3().get(3));
                assertEquals(elements[4], holder2.getList3().get(4));
                assertEquals(elements[5], holder2.getList3().get(5));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundHolder.class);
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
        }
    }

    /**
     * Test of detach-attach of 1-N bi FK relation with double compound identity.
     */
    public void testOneToManyBiFKDoubleDetachAttach()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            Object elements[] = new Object[6];
            try
            {
                tx.begin();
                CompoundHolder holder = new CompoundHolder("First Holder");
                CompoundRelated related = new CompoundRelated("First Related");
                elements[0] = new CompoundDoubleTarget(holder, related, 1.0);
                elements[1] = new CompoundDoubleTarget(holder, related, 2.0);
                elements[2] = new CompoundDoubleTarget(holder, related, 3.0);
                elements[3] = new CompoundDoubleTarget(holder, related, 4.0);
                elements[4] = new CompoundDoubleTarget(holder, related, 5.0);
                elements[5] = new CompoundDoubleTarget(holder, related, 6.0);
                holder.getList3().add(elements[0]);
                holder.getList3().add(elements[1]);
                holder.getList3().add(elements[2]);
                holder.getList3().add(elements[3]);
                pm.makePersistent(holder);
                tx.commit();
                id = pm.getObjectId(holder);

                // Detach the holder
                tx.begin();
                pm.getFetchPlan().addGroup("detach");
                CompoundHolder holder2 = (CompoundHolder) pm.detachCopy(pm.getObjectById(id,true));
                tx.commit();

                // Add more elements
                holder2.getList3().add(elements[4]);

                // Attach
                tx.begin();
                pm.makePersistent(holder2);
                tx.commit();

                // Check the results
                tx.begin();
                CompoundHolder holder3 = (CompoundHolder) pm.getObjectById(id,true);
                assertEquals(5, holder3.getList3().size());
                assertEquals(elements[0],holder3.getList3().get(0));
                assertEquals(elements[1],holder3.getList3().get(1));
                assertEquals(elements[2],holder3.getList3().get(2));
                assertEquals(elements[3],holder3.getList3().get(3));
                assertEquals(elements[4],holder3.getList3().get(4));
                tx.commit();

                // Go back and update the detached, adding the other element
                holder2.getList3().add(elements[5]);

                // Attach it
                tx.begin();
                pm.makePersistent(holder2);
                tx.commit();

                // Check the results
                tx.begin();
                holder3 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder3.getList3().size());
                assertEquals(elements[0], holder3.getList3().get(0));
                assertEquals(elements[1], holder3.getList3().get(1));
                assertEquals(elements[2], holder3.getList3().get(2));
                assertEquals(elements[3], holder3.getList3().get(3));
                assertEquals(elements[4], holder3.getList3().get(4));
                assertEquals(elements[5], holder3.getList3().get(5));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
            }        
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                try
                {
                    tx.begin();
                    CompoundHolder holder1 = (CompoundHolder) pm.getObjectById(id,true);
                    holder1.getList3().clear();
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
        }
        finally
        {
            clean(CompoundHolder.class);
            clean(CompoundDoubleTarget.class);
            clean(CompoundRelated.class);
        }
    }

    /**
     * Test of compound identity 1-N relations with a map using ForeignKey and the map key stored in the value.
     */
    public void testOneToManyMapBiKeyInValue()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            CompoundMapTarget values[] = new CompoundMapTarget[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                values[0] = new CompoundMapTarget("First", holder1, 1.0);
                values[1] = new CompoundMapTarget("Second", holder1, 2.0);
                values[2] = new CompoundMapTarget("Third", holder1, 3.0);
                values[3] = new CompoundMapTarget("Fourth", holder1, 4.0);
                values[4] = new CompoundMapTarget("Fifth", holder1, 5.0);
                values[5] = new CompoundMapTarget("Sixth", holder1, 6.0);
                for (int i=0;i<6;i++)
                {
                    holder1.getMap1().put(values[i].getName(), values[i]);
                }
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                tx.begin();
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder2.getMap1().size());
                assertEquals(values[0], holder2.getMap1().get("First"));
                assertEquals(values[1], holder2.getMap1().get("Second"));
                assertEquals(values[2], holder2.getMap1().get("Third"));
                assertEquals(values[3], holder2.getMap1().get("Fourth"));
                assertEquals(values[4], holder2.getMap1().get("Fifth"));
                assertEquals(values[5], holder2.getMap1().get("Sixth"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundHolder.class);
            clean(CompoundMapTarget.class);
        }
    }

    /**
     * Test of compound identity 1-N relations with a map using ForeignKey and the map value stored in the key.
     */
    public void testOneToManyMapBiValueInKey()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            CompoundMapTarget2 values[] = new CompoundMapTarget2[6];
            try
            {
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                values[0] = new CompoundMapTarget2("First", holder1, 1.0);
                values[1] = new CompoundMapTarget2("Second", holder1, 2.0);
                values[2] = new CompoundMapTarget2("Third", holder1, 3.0);
                values[3] = new CompoundMapTarget2("Fourth", holder1, 4.0);
                values[4] = new CompoundMapTarget2("Fifth", holder1, 5.0);
                values[5] = new CompoundMapTarget2("Sixth", holder1, 6.0);
                for (int i=0;i<6;i++)
                {
                    holder1.getMap2().put(values[i], values[i].getValue());
                }
                pm.makePersistent(holder1);
                tx.commit();
                id = pm.getObjectId(holder1);

                tx.begin();
                CompoundHolder holder2 = (CompoundHolder) pm.getObjectById(id, true);
                assertEquals(6, holder2.getMap2().size());
                assertEquals(new Double(1.0), holder2.getMap2().get(values[0]));
                assertEquals(new Double(2.0), holder2.getMap2().get(values[1]));
                assertEquals(new Double(3.0), holder2.getMap2().get(values[2]));
                assertEquals(new Double(4.0), holder2.getMap2().get(values[3]));
                assertEquals(new Double(5.0), holder2.getMap2().get(values[4]));
                assertEquals(new Double(6.0), holder2.getMap2().get(values[5]));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundHolder.class);
            clean(CompoundMapTarget2.class);
        }
    }

    /**
     * Test of inheritance with compound identity (1-1 uni).
     */
    public void testOneToOneUniInheritance()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            CompoundConcreteSub n[] = new CompoundConcreteSub[6];
            Object ids[] = new Object[6];
            try
            {
                tx.begin();
                CompoundRelated rel1 = new CompoundRelated("First Related");
                CompoundRelated rel2 = new CompoundRelated("Second Related");
                CompoundRelated rel3 = new CompoundRelated("Third Related");
                n[0] = new CompoundConcreteSub(rel3, "1", "1");
                n[1] = new CompoundConcreteSub(rel3, "2", "2");
                n[2] = new CompoundConcreteSub(rel2, "3", "3");
                n[3] = new CompoundConcreteSub(rel2, "4", "4");
                n[4] = new CompoundConcreteSub(rel1, "5", "5");
                n[5] = new CompoundConcreteSub(rel1, "6", "6");
                pm.makePersistentAll(n);
                tx.commit();
                for (int i=0;i<6;i++)
                {
                    ids[i] = JDOHelper.getObjectId(n[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
                for (int i=0;i<6;i++)
                {
                    CompoundConcreteSub sub = (CompoundConcreteSub)pm.getObjectById(ids[i]);
                    if (i == 0 || i == 1)
                    {
                        assertEquals("Name of related of target is incorrect", 
                            "Third Related", sub.getRelated().getName());
                    }
                    if (i == 2 || i == 3)
                    {
                        assertEquals("Name of related of target is incorrect", 
                            "Second Related", sub.getRelated().getName());
                    }
                    if (i == 4 || i == 5)
                    {
                        assertEquals("Name of related of target is incorrect", 
                            "First Related", sub.getRelated().getName());
                    }
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            // Clean up our data
            clean(CompoundConcreteSub.class);
            clean(CompoundRelated.class);
        }
    }

    /**
     * Test for 1-1 bidir but where one side has an FK not part of PK.
     */
    public void testOneToOneBiSingleFKPK()
    {
        try
        {
            // Persist via the source
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object sourceId = null;
            Object targetId = null;
            try
            {
                tx.begin();
                CompoundBiSource1 source = new CompoundBiSource1();
                CompoundBiTarget1 target = new CompoundBiTarget1();
                target.setName("First Target");
                source.setId("First Source");
                target.setSource(source);
                source.setTarget(target);
                pm.makePersistent(source);
                tx.commit();
                sourceId = pm.getObjectId(source);
                targetId = pm.getObjectId(target);
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CompoundBiSource1 source = (CompoundBiSource1)pm.getObjectById(sourceId);
                CompoundBiTarget1 target = (CompoundBiTarget1)pm.getObjectById(targetId);
                assertEquals("Target of CompoundBiSource1 is incorrect", source.getTarget(), target);
                assertEquals("Source of CompoundBiTarget1 is incorrect", target.getSource(), source);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Persist via the target
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CompoundBiSource1 source2 = new CompoundBiSource1();
                CompoundBiTarget1 target2 = new CompoundBiTarget1();
                target2.setName("Second Target");
                source2.setId("Second Source");
                source2.setTarget(target2);
                target2.setSource(source2);
                pm.makePersistent(target2);
                tx.commit();
                sourceId = pm.getObjectId(source2);
                targetId = pm.getObjectId(target2);
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
            }        
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                CompoundBiSource1 source2 = (CompoundBiSource1)pm.getObjectById(sourceId);
                CompoundBiTarget1 target2 = (CompoundBiTarget1)pm.getObjectById(targetId);
                assertEquals("Target of CompoundBiSource1 is incorrect", source2.getTarget(), target2);
                assertEquals("Source of CompoundBiTarget1 is incorrect", target2.getSource(), source2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundBiTarget1.class);
            clean(CompoundBiSource1.class);
        }
    }

    /**
     * Test of persistence of graph with 4 levels of 1-1 uni compound identity relations.
     */
    public void test4LevelsDepthPersistence()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object o[] = new Object[5];
            Object p[] = new Object[5];
            Object q[] = new Object[5];
            try
            {
                tx.begin();
                o[0] = new CompoundSourceL1("io");
                o[1] = new CompoundSourceL2((CompoundSourceL1)o[0],"iio");
                o[2] = new CompoundSourceL3((CompoundSourceL2)o[1]);
                o[3] = new CompoundSourceL4((CompoundSourceL3)o[2],"ivo");
                o[4] = new CompoundSourceL5((CompoundSourceL4)o[3],(CompoundSourceL1)o[0]);

                p[0] = new CompoundSourceL1("ip");
                p[1] = new CompoundSourceL2((CompoundSourceL1)p[0],"iip");
                p[2] = new CompoundSourceL3((CompoundSourceL2)p[1]);
                p[3] = new CompoundSourceL4((CompoundSourceL3)p[2],"ivp");
                p[4] = new CompoundSourceL5((CompoundSourceL4)p[3],(CompoundSourceL1)p[0]);

                q[0] = new CompoundSourceL1("iq");
                q[1] = new CompoundSourceL2((CompoundSourceL1)q[0],"iiq"); 
                q[2] = new CompoundSourceL3((CompoundSourceL2)q[1]);
                //refers to "o" on purpose
                q[3] = new CompoundSourceL4((CompoundSourceL3)o[2],"ivq");
                q[4] = new CompoundSourceL5((CompoundSourceL4)o[3],(CompoundSourceL1)q[0]);

                pm.makePersistentAll(o);
                pm.makePersistentAll(p);
                pm.makePersistentAll(q);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
                fail(e.getMessage());
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
            clean(CompoundSourceL5.class);
            clean(CompoundSourceL4.class);
            clean(CompoundSourceL3.class);
            clean(CompoundSourceL2.class);
            clean(CompoundSourceL1.class);
        }
    }

    /**
     * Test of the persistence via an FK update.
     */
    public void testOneToOneBiStoredAsFKUpdate()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            CompoundSource1 source;
            CompoundTarget1 target;

            source = new CompoundSource1();
            source.setAddress1Attr("8356 Green Street");
            source.setAddress2Attr("Unit 103");
            source.setBillingFlagAttr(new Boolean(false));
            source.setCityAttr("Honolulu");
            source.setStateAttr("HI");
            source.setZipAttr("99091");
            source.setIdAttr("103");

            target = new CompoundTarget1();
            target.setZipPlusFourAttr("1233");
            source.setTarget(target);

            // Persist some objects
            try
            {
                //STEP 1
                tx.begin(); 
                pm.makePersistent(source);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the results with an update
            try
            {
                CompoundSource1.Id sourceKey =  new CompoundSource1.Id("103");
                CompoundTarget1.Id targetKey =  new CompoundTarget1.Id("103");
                tx.begin();
                source = (CompoundSource1) pm.getObjectById(sourceKey);
                target = (CompoundTarget1) pm.getObjectById(targetKey);
                target.setZipPlusFourAttr("4322");
                source.setAddress2Attr("Apt 101");
                source.setTarget(target);
                //STEP 2 THIS EXPOSES THE BUG---
                pm.makePersistent(target);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean up our data
            clean(CompoundTarget1.class);
            clean(CompoundSource1.class);
        }
    }

    /**
     * Test of 1-1 bi persisted as FK change (delete old, persist new).
     */
    public void testOneToOneBiStoredAsFKChangeCompound()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            CompoundSource1 source;
            CompoundTarget1 target;

            source = new CompoundSource1();
            source.setAddress1Attr("8356 Green Street");
            source.setAddress2Attr("Unit 105");
            source.setBillingFlagAttr(new Boolean(false));
            source.setCityAttr("Honolulu");
            source.setStateAttr("HI");
            source.setZipAttr("99091");
            source.setIdAttr("105");

            target = new CompoundTarget1();
            target.setZipPlusFourAttr("1232");
            source.setTarget(target);

            // Persist some objects
            try
            {
                //STEP 1
                tx.begin(); 
                pm.makePersistent(source);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            Object sourceId = pm.getObjectId(source);

            try
            {
                target = new CompoundTarget1();
                target.setZipPlusFourAttr("1231");
                tx.begin();
                // Delete target, and replace with a new one
                pm.deletePersistent(source.getTarget());
                source = (CompoundSource1) pm.getObjectById(sourceId);
                source.setTarget(target);
                pm.makePersistent(target);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
        }
        finally
        {
            // Clean up our data
            clean(CompoundTarget1.class);
            clean(CompoundSource1.class);
        }
    }

    /**
     * Chain of 1-N bidir FK relations using compound identity, and detach/attach.
     */
    public void testOneToManyChain()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("all").setMaxFetchDepth(4);
            CompoundX1 x1 = new CompoundX1("First X1", 1);
            CompoundX1 detachedX1 = null;
            try
            {
                tx.begin();
                pm.makePersistent(x1);

                // Detach the X1
                detachedX1 = (CompoundX1) pm.detachCopy(x1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Error persisting CompoundX1", e);
                fail("Exception thrown persisting/detaching CompoundX1 : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("all").setMaxFetchDepth(4);
            CompoundX2 x2 = new CompoundX2("X2 A","X2 B", detachedX1);
            detachedX1.getSetX2().add(x2);
            CompoundX2 detachedX2 = null;
            try
            {
                tx.begin();
                pm.makePersistent(x2);

                // Detach the X2
                detachedX2 = (CompoundX2) pm.detachCopy(x2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Error persisting CompoundX2", e);
                fail("Exception thrown persisting/detaching CompoundX2 : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("all").setMaxFetchDepth(4);
            tx = pm.currentTransaction();
            CompoundX3 x3 = new CompoundX3("TASK 1", detachedX2);
            detachedX2.getSetX3().add(x3);
            try
            {
                tx.begin();
                pm.makePersistent(x3);

                // Detach the X3
                pm.detachCopy(x3); // TODO Continue to the next link in the chain detaching this
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error("Error persisting CompoundX3", e);
                fail("Exception thrown persisting/detaching CompoundX3 : " + e.getMessage());
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
            // Clean up our data
            clean(CompoundX4.class);
            clean(CompoundX3.class);
            clean(CompoundX2.class);
            clean(CompoundX1.class);
        }
    }

    /**
     * Test for JDOQL on a graph with 4 levels of compound identity relations.
     * See JIRA "NUCRDBMS-20"
     */
    public void test4LevelsDepthQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object o[] = new Object[5];
        Object p[] = new Object[5];
        Object q[] = new Object[5];
        try
        {      
            tx.begin();            
    
            o[0] = new CompoundSourceL1("io");
            o[1] = new CompoundSourceL2((CompoundSourceL1)o[0], "iio");
            o[2] = new CompoundSourceL3((CompoundSourceL2)o[1]);
            o[3] = new CompoundSourceL4((CompoundSourceL3)o[2], "ivo");
            o[4] = new CompoundSourceL5((CompoundSourceL4)o[3], (CompoundSourceL1)o[0]);

            p[0] = new CompoundSourceL1("ip");
            p[1] = new CompoundSourceL2((CompoundSourceL1)p[0], "iip");
            p[2] = new CompoundSourceL3((CompoundSourceL2)p[1]);
            p[3] = new CompoundSourceL4((CompoundSourceL3)p[2], "ivp");
            p[4] = new CompoundSourceL5((CompoundSourceL4)p[3], (CompoundSourceL1)p[0]);

            q[0] = new CompoundSourceL1("iq");
            q[1] = new CompoundSourceL2((CompoundSourceL1)q[0], "iiq"); 
            q[2] = new CompoundSourceL3((CompoundSourceL2)q[1]);
            //refers to "o" on purpose
            q[3] = new CompoundSourceL4((CompoundSourceL3)o[2], "ivq");
            q[4] = new CompoundSourceL5((CompoundSourceL4)o[3], (CompoundSourceL1)q[0]);

            pm.makePersistentAll(o);
            pm.makePersistentAll(p);
            pm.makePersistentAll(q);
            pm.flush();

            //test root
            Query query = pm.newQuery(CompoundSourceL1.class,"id == 'ip'");
            Collection c = (Collection)query.execute();
            assertEquals(1, c.size());
            assertEquals("ip", ((CompoundSourceL1)c.iterator().next()).getId());

            //test root compare to pk
            CompoundSourceL1.Id id = new CompoundSourceL1.Id("iq");
            query = pm.newQuery(CompoundSourceL1.class,"id == :p.id"); // NOT PART OF JDO2 SPEC
            c = (Collection)query.execute(id);
            assertEquals(1, c.size());
            assertEquals("iq", ((CompoundSourceL1)c.iterator().next()).getId());

            query = pm.newQuery(CompoundSourceL1.class,"id == :p.id"); // NOT PART OF JDO2 SPEC
            c = (Collection)query.execute(pm.getObjectId(q[0]));
            assertEquals(1, c.size());
            assertEquals("iq", ((CompoundSourceL1)c.iterator().next()).getId());

            //test II
            id = new CompoundSourceL1.Id("iq");
            CompoundSourceL2.Id idII = new CompoundSourceL2.Id();
            idII.source = id;
            query = pm.newQuery(CompoundSourceL2.class,"JDOHelper.getObjectId(source) == :p");
            c = (Collection)query.execute(idII.source);
            assertEquals(1, c.size());
            assertEquals("iq", ((CompoundSourceL2)c.iterator().next()).getSource().getId());

            query = pm.newQuery(CompoundSourceL2.class,"JDOHelper.getObjectId(source) == :p");
            c = (Collection)query.execute(pm.getObjectId(q[0]));
            assertEquals(1, c.size());
            assertEquals("iq", ((CompoundSourceL2)c.iterator().next()).getSource().getId());

            query = pm.newQuery(CompoundSourceL5.class,"JDOHelper.getObjectId(this) == :p");
            c = (Collection)query.execute(pm.getObjectId(q[4]));
            assertEquals(1, c.size());
            assertEquals(JDOHelper.getObjectId(q[4]), JDOHelper.getObjectId(c.iterator().next()));

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception occurred during query", e);
            fail(e.getMessage());
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

    /**
     * Test of detach-attach of multiple level structures.
     */
    public void testMultipleLevelDetachAttach()
    {
        try
        {
            CompoundSourceL1 l1 = null;
            CompoundSourceL2 l2 = null;
            CompoundSourceL3 l3 = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                l1 = new CompoundSourceL1("io");
                l2 = new CompoundSourceL2(l1, "iio");
                pm.makePersistentAll(l2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in persist+detach", e);
                fail(e.getMessage());
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
            pm.setCopyOnAttach(true);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                l3 = new CompoundSourceL3(l2);
                pm.makePersistent(l3); // Persist new object with detached relation
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in persist+attach", e);
                fail(e.getMessage());
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
            clean(CompoundSourceL3.class);
            clean(CompoundSourceL2.class);
            clean(CompoundSourceL1.class);
        }
    }
}