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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.abstractclasses.AbstractSimpleClassHolder;
import org.jpox.samples.abstractclasses.AbstractSimpleBase;
import org.jpox.samples.abstractclasses.ConcreteSimpleSub1;
import org.jpox.samples.abstractclasses.ConcreteSimpleSub2;

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
                    AbstractSimpleBase.class,
                    ConcreteSimpleSub1.class,
                    ConcreteSimpleSub2.class,
                    AbstractSimpleClassHolder.class,
                }
            );
            initialised = true;
        }
    }

    /**
     * Check the persistence of abstract objects and their holders.
     **/
    public void testCreationSimple()
    throws Exception
    {
        try
        {
            perform1to1CreationSimple();
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test of the retrieval of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    public void testRetrievalSimple()
    throws Exception
    {
        try
        {
            perform1to1CreationSimple();
            perform1to1Retrieval(AbstractSimpleClassHolder.class, AbstractSimpleBase.class, 
                ConcreteSimpleSub1.class, ConcreteSimpleSub2.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test of the query of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    public void testQuerySimple()
    throws Exception
    {
        try
        {
            perform1to1CreationSimple();
            perform1to1Query(AbstractSimpleClassHolder.class, AbstractSimpleBase.class,
                ConcreteSimpleSub2.class, ConcreteSimpleSub2.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test of the deletion of the abstract contained objects and its holder (App Identity).
     */
    public void testDeletionSimple()
    throws Exception
    {
        try
        {
            perform1to1CreationSimple();
            perform1to1Deletion(AbstractSimpleClassHolder.class, AbstractSimpleBase.class, 
                ConcreteSimpleSub1.class, ConcreteSimpleSub2.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test for having abstract elements in a join-table Set, and creating container/elements.
     */
    public void testJoinTableSetCreationSimple()
    throws Exception
    {
        try
        {
            perform1toNJoinTableSetCreationSimple();
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(AbstractSimpleBase.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test of the retrieval of the abstract contained objects within a Set.
     */
    public void testJoinTableSetRetrievalSimple()
    throws Exception
    {
        try
        {
            perform1toNJoinTableSetCreationSimple();
            perform1toNJoinTableSetRetrieval(AbstractSimpleClassHolder.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(AbstractSimpleBase.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test for having abstract elements in a join-table List, and creating container/elements.
     */
    public void testJoinTableListCreationSimple()
    throws Exception
    {
        try
        {
            perform1toNJoinTableListCreationSimple();
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(AbstractSimpleBase.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    /**
     * Test of the retrieval of the abstract contained objects within a List.
     */
    public void testJoinTableListRetrievalSimple()
    throws Exception
    {
        try
        {
            perform1toNJoinTableListCreationSimple();
            perform1toNJoinTableListRetrieval(AbstractSimpleClassHolder.class,
                ConcreteSimpleSub1.class, ConcreteSimpleSub2.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(AbstractSimpleBase.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }
/*
    *//**
     * Check the persistence of abstract objects and their holders.
     **//*
    public void testCreationComposite()
    throws Exception
    {
        try
        {
            perform1to1CreationComposite();
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test of the retrieval of the abstract contained objects and of the 
     * holder with its related abstract object.
     *//*
    public void testRetrievalComposite()
    throws Exception
    {
        try
        {
            perform1to1CreationComposite();
            perform1to1Retrieval(AbstractCompositeClassHolder.class, AbstractCompositeBase.class, 
                ConcreteCompositeSub1.class, ConcreteCompositeSub2.class);
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test of the query of the abstract contained objects and of the 
     * holder with its related abstract object.
     *//*
    public void testQueryComposite()
    throws Exception
    {
        try
        {
            perform1to1CreationComposite();
            perform1to1Query(AbstractCompositeClassHolder.class, AbstractCompositeBase.class,
                ConcreteCompositeSub2.class, ConcreteCompositeSub2.class);
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test of the deletion of the abstract contained objects and its holder.
     *//*
    public void testDeletionComposite()
    throws Exception
    {
        try
        {
            perform1to1CreationComposite();
            perform1to1Deletion(AbstractCompositeClassHolder.class, AbstractCompositeBase.class, 
                ConcreteCompositeSub1.class, ConcreteCompositeSub2.class);
        }
        finally
        {
            clean(AbstractSimpleClassHolder.class);
            clean(ConcreteSimpleSub1.class);
            clean(ConcreteSimpleSub2.class);
        }
    }

    *//**
     * Test for having abstract elements in a join-table Set, and creating container/elements.
     *//*
    public void testJoinTableSetCreationComposite()
    throws Exception
    {
        try
        {
            perform1toNJoinTableSetCreationComposite();
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(AbstractCompositeBase.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test of the retrieval of the abstract contained objects within a Set.
     *//*
    public void testJoinTableSetRetrievalComposite()
    throws Exception
    {
        try
        {
            perform1toNJoinTableSetCreationComposite();
            perform1toNJoinTableSetRetrieval(AbstractCompositeClassHolder.class);
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(AbstractCompositeBase.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test for having abstract elements in a join-table List, and creating container/elements.
     *//*
    public void testJoinTableListCreationComposite()
    throws Exception
    {
        try
        {
            perform1toNJoinTableListCreationComposite();
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(AbstractCompositeBase.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }

    *//**
     * Test of the retrieval of the abstract contained objects within a List.
     *//*
    public void testJoinTableListRetrievalComposite()
    throws Exception
    {
        try
        {
            perform1toNJoinTableListCreationComposite();
            perform1toNJoinTableListRetrieval(AbstractCompositeClassHolder.class,
                ConcreteCompositeSub1.class, ConcreteCompositeSub2.class);
        }
        finally
        {
            clean(AbstractCompositeClassHolder.class);
            clean(AbstractCompositeBase.class);
            clean(ConcreteCompositeSub1.class);
            clean(ConcreteCompositeSub2.class);
        }
    }
*/
    // ------------------------------------------- Utility Methods ---------------------------------------------

    /**
     * Convenience method to create 2 holders with ConcreteSimpleSub1, ConcreteSimpleSub2 related objects.
     */
    private void perform1to1CreationSimple()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist 2 1-1 relations using both concrete subclass types
            tx.begin();
            AbstractSimpleClassHolder holder1 = new AbstractSimpleClassHolder(1);
            ConcreteSimpleSub1 sub1 = new ConcreteSimpleSub1(1);
            sub1.setBaseField("Base1");
            sub1.setSub1Field("Sub1");
            holder1.setAbstract1(sub1);
            pm.makePersistent(holder1);

            AbstractSimpleClassHolder holder2 = new AbstractSimpleClassHolder(2);
            ConcreteSimpleSub2 sub2 = new ConcreteSimpleSub2(2);
            sub2.setBaseField("Base2");
            sub2.setSub2Field("Sub2");
            holder2.setAbstract1(sub2);
            pm.makePersistent(holder2);

            tx.commit();
            assertTrue("Id of Abstract object ConcreteSimpleSub1 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of Abstract object ConcreteSimpleSub2 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of holder1 was null!", pm.getObjectId(holder1) != null);
            assertTrue("Id of holder2 was null!", pm.getObjectId(holder2) != null);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown persisting objects with abstract superclass", e);
            e.printStackTrace();
            fail("Exception thrown during persistence : " + e.getMessage());
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
     * Test of the retrieval of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    private void perform1to1Retrieval(Class holderClass, Class abstractBaseClass, Class concreteClass1, Class concreteClass2)
    throws Exception
    {
        PersistenceManager pm=pmf.getPersistenceManager();

        // Retrieve the abstract objects including (concrete) subclasses
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(abstractBaseClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            assertTrue("Exception thrown during retrieval of abstract objects.",false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }

        // Retrieve the abstract objects excluding (concrete) subclasses
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(abstractBaseClass, false);
            Iterator iter = e.iterator();
            assertTrue("Retrieval of Extent for abstract class without subclasses did not return 0!", !iter.hasNext());
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            assertTrue("Exception thrown during retrieval of abstract objects.",false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }

        // Retrieve the holders
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(holderClass, false);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                Object holder = iter.next();
                
                Method getNameMethod = holderClass.getMethod("getAbstract1", new Class[] {});
                getNameMethod.invoke(holder, new Object[] {});
            }
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            assertTrue("Exception thrown during retrieval of holder of abstract objects", false);
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
     * Test of the query of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    private void perform1to1Query(Class holderClass, Class abstractBaseClass, Class concreteClass1, Class concreteClass2)
    throws Exception
    {
        PersistenceManager pm=pmf.getPersistenceManager();

        // Retrieve the abstract objects including (concrete) subclasses
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(pm.getExtent(abstractBaseClass, true), "baseField == \"Base1\"");
            Collection c = (Collection)q.execute();
            assertTrue("Number of abstract objects retrieved from query was incorrect : was " + c.size() + " but should have been 1",
                c.size() == 1);
            Iterator iter = c.iterator();
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error(ue);
            assertTrue("Exception thrown during query of abstract objects.",false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }

        // Retrieve the abstract objects excluding (concrete) subclasses
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(pm.getExtent(abstractBaseClass, false), "baseField == \"Base1\"");
            Collection c = (Collection)q.execute();
            assertTrue("Number of abstract objects retrieved from query was incorrect : was " + c.size() + " but should have been 0",
                c.size() == 0);
            Iterator iter = c.iterator();
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error(ue);
            assertTrue("Exception thrown during retrieval of abstract objects.",false);
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
     * Method to perform a test for deletion of abstract objects
     * @param holderClass The holder
     * @param abstractBaseClass The abstract base class
     * @param concreteClass1 Concrete class 1
     * @param concreteClass2 Concrete class 2
     * @throws Exception Thrown if an error occurs
     */
    private void perform1to1Deletion(Class holderClass, Class abstractBaseClass, Class concreteClass1, Class concreteClass2)
    throws Exception
    {
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Retrieve the ids of the abstract objects
            Object abstract_id_1 = null;
            Object abstract_id_2 = null;
            tx.begin();

            Extent e1 = pm.getExtent(abstractBaseClass, true);
            Iterator iter1 = e1.iterator();
            while (iter1.hasNext())
            {
                Object base = iter1.next();
                if (abstract_id_1 == null)
                {
                    abstract_id_1 = pm.getObjectId(base);
                }
                else
                {
                    abstract_id_2 = pm.getObjectId(base);
                }
            }

            tx.commit();

            // Retrieve the ids of the holder objects
            Object holder_id_1 = null;
            Object holder_id_2 = null;
            tx.begin();

            Extent e2 = pm.getExtent(holderClass, false);
            Iterator iter2 = e2.iterator();
            while (iter2.hasNext())
            {
                Object holder = iter2.next();
                if (holder_id_1 == null)
                {
                    holder_id_1 = pm.getObjectId(holder);
                }
                else
                {
                    holder_id_2 = pm.getObjectId(holder);
                }
            }
            tx.commit();

            // Delete the objects
            tx.begin();

            Object holder_1 = pm.getObjectById(holder_id_1, false);
            pm.deletePersistent(holder_1);

            Object holder_2 = pm.getObjectById(holder_id_2, false);
            pm.deletePersistent(holder_2);

            Object abstract_1 = pm.getObjectById(abstract_id_1, false);
            pm.deletePersistent(abstract_1);

            Object abstract_2 = pm.getObjectById(abstract_id_2, false);
            pm.deletePersistent(abstract_2);

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            ue.printStackTrace();
            LOG.error(ue);
            assertTrue("Exception thrown during retrieval and deletion of holder and abstract objects", false);
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
     * Convenience method to persist a holder with 2 elements in the set field.
     */
    private void perform1toNJoinTableSetCreationSimple()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist holder with 2 elements in the Set field
            tx.begin();
            AbstractSimpleClassHolder holder = new AbstractSimpleClassHolder(1);

            ConcreteSimpleSub1 sub1 = new ConcreteSimpleSub1(1);
            sub1.setBaseField("Base1");
            sub1.setSub1Field("Sub1");
            holder.getAbstractSet1().add(sub1);

            ConcreteSimpleSub2 sub2 = new ConcreteSimpleSub2(2);
            sub2.setBaseField("Base2");
            sub2.setSub2Field("Sub2");
            holder.getAbstractSet1().add(sub2);

            pm.makePersistent(holder);

            tx.commit();
            assertTrue("Id of Abstract object ConcreteSimpleSub1 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of Abstract object ConcreteSimpleSub2 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of holder was null!", pm.getObjectId(holder) != null);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown persisting objects with abstract superclass", e);
            e.printStackTrace();
            fail("Exception thrown during persistence : " + e.getMessage());
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
     * Test of the retrieval of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    private void perform1toNJoinTableSetRetrieval(Class holderClass)
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Find the holder
            Extent e = pm.getExtent(holderClass, true);
            int numberOfHolders = 0;
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                Object holder = iter.next();
                numberOfHolders++;

                // Extract the Set of elements
                Method getSetMethod = holderClass.getMethod("getAbstractSet1", new Class[] {});
                Object obj = getSetMethod.invoke(holder, new Object[] {});
                assertTrue("Elements for holder is NULL, but shouldn't be", obj != null);
                assertTrue("Elements type is not a Set", obj instanceof Set);

                Collection elements = (Collection)obj;
                assertEquals("Number of elements is incorrect", 2, elements.size());
                Iterator elementsIter = elements.iterator();
                while (elementsIter.hasNext())
                {
                    elementsIter.next();
                }
            }
            assertEquals("Number of container objects was incorrect.", 1, numberOfHolders);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown retrieving objects with abstract superclass", e);
            e.printStackTrace();
            fail("Exception thrown during persistence : " + e.getMessage());
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
     * Convenience method to persist a holder with 2 elements in the list field.
     */
    private void perform1toNJoinTableListCreationSimple()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Persist holder with 2 elements in the List field
            tx.begin();
            AbstractSimpleClassHolder holder = new AbstractSimpleClassHolder(1);

            ConcreteSimpleSub1 sub1 = new ConcreteSimpleSub1(1);
            sub1.setBaseField("Base1");
            sub1.setSub1Field("Sub1");
            holder.getAbstractList1().add(sub1);

            ConcreteSimpleSub2 sub2 = new ConcreteSimpleSub2(2);
            sub2.setBaseField("Base2");
            sub2.setSub2Field("Sub2");
            holder.getAbstractList1().add(sub2);

            pm.makePersistent(holder);

            tx.commit();
            assertTrue("Id of Abstract object ConcreteSimpleSub1 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of Abstract object ConcreteSimpleSub2 was null!", pm.getObjectId(sub1) != null);
            assertTrue("Id of holder was null!", pm.getObjectId(holder) != null);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown persisting objects with abstract superclass", e);
            e.printStackTrace();
            fail("Exception thrown during persistence : " + e.getMessage());
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
     * Test of the retrieval of the abstract contained objects and of the 
     * holder with its related abstract object.
     */
    private void perform1toNJoinTableListRetrieval(Class holderClass, Class element1Class, Class element2Class)
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Find the holder
            Extent e = pm.getExtent(holderClass, true);
            int numberOfHolders = 0;
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                Object holder = iter.next();
                numberOfHolders++;

                // Extract the Set of elements
                Method getSetMethod = holderClass.getMethod("getAbstractList1", new Class[] {});
                Object obj = getSetMethod.invoke(holder, new Object[] {});
                assertTrue("Elements for holder is NULL, but shouldn't be", obj != null);
                assertTrue("Elements type is not a List", obj instanceof List);

                List elements = (List)obj;
                assertEquals("Number of elements is incorrect", 2, elements.size());
                Object elem1 = elements.get(0);
                Object elem2 = elements.get(1);
                assertEquals("First element is of incorrect type", element1Class.getName(),
                    elem1.getClass().getName());
                assertEquals("First element is of incorrect type", element2Class.getName(),
                    elem2.getClass().getName());
            }
            assertEquals("Number of container objects was incorrect.", 1, numberOfHolders);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown retrieving objects with abstract superclass", e);
            e.printStackTrace();
            fail("Exception thrown during persistence : " + e.getMessage());
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