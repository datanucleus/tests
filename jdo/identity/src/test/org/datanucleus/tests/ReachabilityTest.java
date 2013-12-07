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

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.interfaces.ShapeHolder;
import org.jpox.samples.interfaces.ShapeHolder2;
import org.jpox.samples.interfaces.Square;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Organisation;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Qualification;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.jpox.samples.reachability.ReachableHolder;
import org.jpox.samples.reachability.ReachableItem;

/**
 * Series of tests for "persistence-by-reachability".
 */
public class ReachabilityTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ReachabilityTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[] {
                    Boiler.class,
                    Timer.class,
                    Organisation.class,
                    Qualification.class,
                    ReachableHolder.class,
                    ReachableItem.class,
                });
        }
    }

    /**
     * Test the use of the "cascade-persist" extension tag to prevent persistence-by-reachability
     * of particular relationship fields.
     */
    public void testPersistCascadeFalse()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Try to persist object with 1-1 relation and with that field not cascade-persist
                // Should throw exception
                tx.begin();

                ReachableHolder holder = new ReachableHolder("MyHolder");
                ReachableItem item1 = new ReachableItem("Item1");
                holder.setItem1(item1);
                pm.makePersistent(holder);

                tx.commit();
                fail("Was allowed to persist an object with a related non-persistent object when the related object was in a field that was not cascade-persist (1-1)");
            }
            catch (Exception e)
            {
                LOG.info(e.getMessage());
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
                // Try to persist object with 1-N relation and with that field not cascade-persist
                // Should throw exception
                tx.begin();

                ReachableHolder holder = new ReachableHolder("MyHolder");
                ReachableItem item2 = new ReachableItem("Item2");
                ReachableItem item3 = new ReachableItem("Item3");
                holder.getSet1().add(item2);
                holder.getSet1().add(item3);
                pm.makePersistent(holder);

                tx.commit();
                fail("Was allowed to persist an object with a related non-persistent object when the related object was in a field that was not cascade-persist (1-N)");
            }
            catch (Exception e)
            {
                LOG.info(e.getMessage());
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
            // Clear out our data
            clean(ReachableHolder.class);
            clean(ReachableItem.class);
        }
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniClassPessimistic()
    {
        performOneToOneUniClass(false);
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniClassOptimistic()
    {
        performOneToOneUniClass(true);
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the related object is also persisted.
     * @param optimisticTxn Whether to use optimistic txns
     */
    protected void performOneToOneUniClass(boolean optimisticTxn)
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setOptimistic(optimisticTxn);
            Object qual1Id = null;
            Object org1Id = null;
            Object qual2Id = null;
            Object org2Id = null;
            try
            {
                // A). persist two objects without relation
                tx.begin();
                Qualification qual1 = new Qualification("ISO 2003 certificate number 34512");
                Organisation org1 = new Organisation("JPOX Consulting");
                pm.makePersistent(org1);
                pm.makePersistent(qual1);
                tx.commit();
                qual1Id = JDOHelper.getObjectId(qual1);
                org1Id = JDOHelper.getObjectId(org1);

                tx.begin();
                // B). Relate the previous objects
                qual1.setOrganisation(org1);

                // c). Create and relate two new objects
                Qualification qual2 = new Qualification("ISO 2001 certificate number 123045");
                Organisation org2 = new Organisation("JPOX Corporation");
                qual2.setOrganisation(org2);

                // Check that both are transient
                assertTrue("Object state of new Qualification is incorrect", 
                    !JDOHelper.isPersistent(qual2) && !JDOHelper.isNew(qual2) && !JDOHelper.isDirty(qual2));
                assertTrue("Object state of new Organisation is incorrect", 
                    !JDOHelper.isPersistent(org2) && !JDOHelper.isNew(org2) && !JDOHelper.isDirty(org2));

                // Persist the Qualification (so the Organisation should be persisted too)
                pm.makePersistent(qual2);

                // Check that both are persistent-new (JDO2 spec 12.6.7)
                assertTrue("Object state of newly persisted Qualification is incorrect",
                    JDOHelper.isPersistent(qual2) && JDOHelper.isNew(qual2) && JDOHelper.isDirty(qual2));
                assertTrue("Object state of newly persisted (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org2) && JDOHelper.isNew(org2) && JDOHelper.isDirty(org2));

                tx.commit();

                // Check that both are clean/hollow
                assertTrue("Object state of committed Qualification is incorrect",
                    JDOHelper.isPersistent(qual2) && !JDOHelper.isNew(qual2) && !JDOHelper.isDirty(qual2));
                assertTrue("Object state of committed (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org2) && !JDOHelper.isNew(org2) && !JDOHelper.isDirty(org2));
                qual2Id = pm.getObjectId(qual2);
                org2Id = pm.getObjectId(org2);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that the objects exist in the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Organisation org1 = (Organisation)pm.getObjectById(org1Id);
                assertTrue("Organisation 1 is not in the datastore!", org1 != null);
                Qualification qual1 = (Qualification)pm.getObjectById(qual1Id);
                assertTrue("Qualification 1 is not in the datastore!", qual1 != null);
                Organisation org2 = (Organisation)pm.getObjectById(org2Id);
                assertTrue("Organisation 2 is not in the datastore!", org2 != null);
                Qualification qual2 = (Qualification)pm.getObjectById(qual2Id);
                assertTrue("Qualification 2 is not in the datastore!", qual2 != null);

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
            clean(Qualification.class);
            clean(Organisation.class);
        }
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between a class and an interface.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniInterfacePessimistic()
    {
        performOneToOneUniInterface(false);
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between a class and an interface.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniInterfaceOptimistic()
    {
        performOneToOneUniInterface(true);
    }

    /**
     * Test for reachability using a 1-1 unidirectional relation between a class and an interface.
     * Tests that when persisting the owner object the related object is also persisted.
     * @param optimisticTxn Whether to use optimistic txns
     */
    protected void performOneToOneUniInterface(boolean optimisticTxn)
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;
            Object squareId = null;
            try
            {
                tx.setOptimistic(optimisticTxn);
                tx.begin();

                // Create the objects of the 1-1 uni relation
                ShapeHolder holder = new ShapeHolder(101);
                Square square = new Square(1, 100, 200);
                holder.setShape1(square);

                // Check that both are transient
                assertTrue("Object state of new ShapeHolder is incorrect", 
                    !JDOHelper.isPersistent(holder) && !JDOHelper.isNew(holder) && !JDOHelper.isDirty(holder));
                assertTrue("Object state of new Square is incorrect", 
                    !JDOHelper.isPersistent(square) && !JDOHelper.isNew(square) && !JDOHelper.isDirty(square));

                // Persist the ShapeHolder (so the Square should be persisted too)
                pm.makePersistent(holder);

                // Check that both are persistent-new (JDO2 spec 12.6.7)
                assertTrue("Object state of newly persisted ShapeHolder is incorrect",
                    JDOHelper.isPersistent(holder) && JDOHelper.isNew(holder) && JDOHelper.isDirty(holder));
                assertTrue("Object state of newly persisted (by reachability) Square is incorrect",
                    JDOHelper.isPersistent(square) && JDOHelper.isNew(square) && JDOHelper.isDirty(square));

                // Commit
                tx.commit();

                // Check that both are clean/hollow
                assertTrue("Object state of committed Qualification is incorrect",
                    JDOHelper.isPersistent(holder) && !JDOHelper.isNew(holder) && !JDOHelper.isDirty(holder));
                assertTrue("Object state of committed (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(square) && !JDOHelper.isNew(square) && !JDOHelper.isDirty(square));
                holderId = pm.getObjectId(holder);
                squareId = pm.getObjectId(square);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that the objects exist in the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Square square = (Square)pm.getObjectById(squareId);
                assertTrue("Square is not in the datastore!", square != null);
                ShapeHolder holder = (ShapeHolder)pm.getObjectById(holderId);
                assertTrue("ShapeeHolder is not in the datastore!", holder != null);

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
            clean(ShapeHolder.class);
            clean(Square.class);
        }
    }

    /**
     * Test for temporary reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniClassTemporaryPessimistic()
    {
        performOneToOneUniClassTemporary(false);
    }

    /**
     * Test for temporary reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the related object is also persisted.
     */
    public void testOneToOneUniClassTemporaryOptimistic()
    {
        performOneToOneUniClassTemporary(true);
    }

    /**
     * Test for temporary reachability using a 1-1 unidirectional relation between 2 classes.
     * Tests that when persisting the owner object the initially reachable related object is NOT persisted.
     * See JDO2 spec 12.6.7
     * @param optimisticTxn Whether to use optimistic txns
     */
    protected void performOneToOneUniClassTemporary(boolean optimisticTxn)
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object qualId = null;
            Object orgId = null;
            try
            {
                tx.setOptimistic(optimisticTxn);
                tx.begin();

                // Create the objects of the 1-1 uni relation
                Qualification qual = new Qualification("ISO 2001 certificate number 123045");
                Organisation org = new Organisation("JPOX Corporation");
                qual.setOrganisation(org);

                // Check that both are transient
                assertTrue("Object state of new Qualification is incorrect", 
                    !JDOHelper.isPersistent(qual) && !JDOHelper.isNew(qual) && !JDOHelper.isDirty(qual));
                assertTrue("Object state of new Organisation is incorrect", 
                    !JDOHelper.isPersistent(org) && !JDOHelper.isNew(org) && !JDOHelper.isDirty(org));

                // Persist the Qualification (so the Organisation should be persisted too)
                pm.makePersistent(qual);

                // Check that both are persistent-new (JDO2 spec 12.6.7)
                assertTrue("Object state of newly persisted Qualification is incorrect",
                    JDOHelper.isPersistent(qual) && JDOHelper.isNew(qual) && JDOHelper.isDirty(qual));
                assertTrue("Object state of newly persisted (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org) && JDOHelper.isNew(org) && JDOHelper.isDirty(org));

                Organisation org2 = new Organisation("JPOX Consulting");
                qual.setOrganisation(org2);

                // Commit
                tx.commit();

                // Check that both are clean/hollow
                assertTrue("Object state of committed Qualification is incorrect",
                    JDOHelper.isPersistent(qual) && !JDOHelper.isNew(qual) && !JDOHelper.isDirty(qual));
                assertFalse("Object state of committed (by temp reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org) && !JDOHelper.isNew(org) && !JDOHelper.isDirty(org));
                assertTrue("Object state of committed (by reachability) Organisation is incorrect",
                    JDOHelper.isPersistent(org2) && !JDOHelper.isNew(org2) && !JDOHelper.isDirty(org2));
                qualId = pm.getObjectId(qual);
                orgId = pm.getObjectId(org2);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check that the objects exist in the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Organisation org = (Organisation)queryByName(pm, Organisation.class, "JPOX Corporation");
                assertTrue("Organisation is in the datastore!", org == null);
                Organisation org2 = (Organisation)pm.getObjectById(orgId);
                assertTrue("Organisation 2 is not in the datastore!", org2 != null);
                Qualification qual = (Qualification)pm.getObjectById(qualId);
                assertTrue("Qualification is not in the datastore!", qual != null);

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
            clean(Qualification.class);
            clean(Organisation.class);
        }
    }

    /**
     * Tests if reachability on commit does not try to persist an object that was reachable then deleted.
     * Uses 1-1 unidir relation.
     * Requires a JPOX extension so is outside the JDO2 spec.
     */
    public void testOneToOneUniClassNewDeleted()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_FLUSH_MODE, "manual");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Organisation org = new Organisation("JPOX Corporation");
                assertTrue("Object state of Organisation is incorrect",
                    !JDOHelper.isPersistent(org) && !JDOHelper.isNew(org) && !JDOHelper.isDirty(org));
                Qualification qual = new Qualification("ISO 4002 Certificate 17001");

                // persist Qualification
                pm.makePersistent(qual);

                // Relate the objects so persisting Organisation by reachability
                qual.setOrganisation(org);
                pm.flush();

                // Remove the reference to the organisation and delete the organisation
                qual.setOrganisation(null);
                // Adding pm.flush(); here will make it work
                pm.deletePersistent(org);

                // Clear references and wait so that the references are GCed
                org = null;
                qual = null;
                Thread.sleep(2000);

                // now commit, should not fail but can if it gets the updates in the wrong order (since is UNIDIRECTIONAL relation)
                tx.commit();
            }
            catch (Exception e)
            {
                // This can happen if the commit processes the delete of the object before the update of the owner
                e.printStackTrace();
                fail("Exception thrown when commiting deleted object which should not have affected commit");
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
            // Clear out our data
            clean(Qualification.class);
            clean(Organisation.class);
        }
    }

    /**
     * Tests if reachability on commit does not try to persist an object that was reachable then deleted.
     * Uses 1-1 bidir relation.
     * Requires a JPOX extension so is outside the JDO2 spec.
     */
    public void testOneToOneBiClassNewDeleted()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_FLUSH_MODE, "manual");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Boiler boiler = new Boiler("Baxi", "Heatflow");
                assertTrue("Object state of Boiler is incorrect",
                    !JDOHelper.isPersistent(boiler) && !JDOHelper.isNew(boiler) && !JDOHelper.isDirty(boiler));
                Timer timer = new Timer("SureCast", true, null);

                // persist Timer
                pm.makePersistent(timer);

                // Relate the objects so persisting Boiler by reachability
                boiler.setTimer(timer);
                timer.setBoiler(boiler);
                pm.flush();

                // Remove the relation and delete the Boiler
                timer.setBoiler(null);
                boiler.setTimer(null);
                pm.deletePersistent(boiler);

                // Clear references and wait so that the references are GCed
                timer = null;
                boiler = null;
                Thread.sleep(2000);

                // now commit, should not fail
                tx.commit();
            }
            catch (Exception e)
            {
                // This can happen if the commit processes the delete of the object before the update of the owner
                e.printStackTrace();
                fail("Exception thrown when commiting deleted object which should not have affected commit");
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
            clean(Timer.class);
            clean(Boiler.class);
        }
    }

    /**
     * Tests if a BaseItem is reachable through a BaseContainer by interface
     * field and does have the correct states at different times.
     */
    /*public void testSimpleReachabilityOnOptimisticTxsDatastoreAttributedId()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.setOptimistic(true);
                tx.begin();
                //test with datastore attributed ids
                Rate rate0 = new Rate();
                Currency usd0 = new Currency("USD0");
                pm.makePersistent(rate0);
                pm.makePersistent(usd0);
                tx.commit();
                tx.begin();
                rate0.setTarget(usd0);
                //test with datastore attributed ids
                Rate rate1 = new Rate();
                Currency usd1 = new Currency("USD1");
                rate1.setTarget(usd1);
                pm.makePersistent(rate1);
                tx.commit();

                assertTrue("reachable persistent rate: isPersistent() == false", JDOHelper.isPersistent(rate1));
                assertTrue("reachable persistent usd1: isPersistent() == false", JDOHelper.isPersistent(usd1));
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
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(Rate.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Rate rate = (Rate)iter.next();
                    if (rate.getSource() != null)
                    {
                        Currency source = rate.getSource();
                        rate.setSource(null);
                        source.setRates(null);
                    }
                    if (rate.getTarget() != null)
                    {
                        rate.setTarget(null);
                    }
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
            clean(Rate.class);
            clean(Currency.class);
        }
    }*/

    /**
     * Test if a Person is reachable through an Employee through a Manager
     * and that it has the correct state at various times.
     */
    public void testDeepReachabilityByClass()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object per1Id = null;
            Object emp1Id = null;
            Object mgr1Id = null;
            try
            {
                tx.begin();
                Employee emp1 = new Employee(101, "Barney", "Rubble", "barney.rubble@jpox.com", (float)123.45, "12346");
                Manager mgr1 = new Manager(102, "Fred", "Flintstone", "fred.flintstone@jpox.com", (float)240.00, "12348");
                Person per1 = new Person(103, "Rob", "Rock", "rob.rock@yahoo.com");

                assertFalse("newly created Person: isPersistent() == true", JDOHelper.isPersistent(per1));
                assertFalse("newly created Person: isNew() == true", JDOHelper.isNew(per1));
                assertFalse("newly created Person: isDirty() == true", JDOHelper.isDirty(per1));

                // persist chain of objects Manager -> Employee -> Person
                emp1.setBestFriend(per1);
                mgr1.addSubordinate(emp1);
                emp1.setManager(mgr1);
                pm.makePersistent(mgr1);
                assertTrue("reachable persistent Person: isPersistent() == false", JDOHelper.isPersistent(per1));
                assertTrue("reachable persistent Person: isNew() == false", JDOHelper.isNew(per1));
                assertTrue("reachable persistent Person: isDirty() == false", JDOHelper.isDirty(per1));
                tx.commit();

                // assert Person is now persistent clean or hollow
                assertTrue("committed Person: isPersistent() == false", JDOHelper.isPersistent(per1));
                assertFalse("committed Person: isNew() == true", JDOHelper.isNew(per1));
                assertFalse("committed Person: isDirty() == true", JDOHelper.isDirty(per1));
                per1Id = JDOHelper.getObjectId(per1);
                emp1Id = JDOHelper.getObjectId(emp1);
                mgr1Id = JDOHelper.getObjectId(mgr1);
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
                // assert the DB contains the correct data
                tx.begin();
                Person checkPer1 = (Person)pm.getObjectById(per1Id);
                assertTrue("Person not in database", checkPer1 != null);
                Employee checkEmp1 = (Employee)pm.getObjectById(emp1Id);
                assertTrue("Employee not in database", checkEmp1 != null);
                Manager checkMgr1 = (Manager)pm.getObjectById(mgr1Id);
                assertTrue("Manager not in database", checkMgr1 != null);
                assertSame("Employee by query not the same as Employee by navigation", 
                    checkEmp1, checkMgr1.getSubordinates().iterator().next());
                assertSame("Person by query not the same as Person by navigation", 
                    checkPer1, checkEmp1.getBestFriend());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Tests if a Square is reachable through a ShapeHolder at makePersistent 
     * and unreachable upon commit and does have the correct states at different times.
     * Spec section 12.6.7.
     */
    public void testNewObjectUnreachableWithDirtyObjectOwner()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Square square1 = new Square(101, 200.0, 350.5);

                // assert alpha is indeed transient
                assertFalse("newly created alpha: isPersistent() == true", JDOHelper.isPersistent(square1));
                assertFalse("newly created alpha: isNew() == true", JDOHelper.isNew(square1));
                assertFalse("newly created alpha: isDirty() == true", JDOHelper.isDirty(square1));
                Square square2 = new Square(102, 400.0, 550.0);
                ShapeHolder holder = new ShapeHolder(201);

                // now persist holder with reference to object
                holder.setShape1(square1);
                pm.makePersistent(holder);

                // assert object is persistent new
                assertTrue("reachable persistent : isPersistent() == false", JDOHelper.isPersistent(square1));
                assertTrue("reachable persistent : isNew() == false", JDOHelper.isNew(square1));
                assertTrue("reachable persistent : isDirty() == false", JDOHelper.isDirty(square1));

                // now replace reference to square1 with reference to square2 and commit
                holder.setShape1(square2);

                tx.commit();
                assertFalse("unreachable committed : isPersistent() == true", JDOHelper.isPersistent(square1));
                assertFalse("unreachable committed : isNew() == true", JDOHelper.isNew(square1));
                assertFalse("unreachable committed : isDirty() == true", JDOHelper.isDirty(square1));

                tx.begin();
                Square square3 = new Square(103, 120.0, 240.0);
                holder.setShape1(square3);
                pm.flush();
                holder.setShape1(null);
                tx.commit();

                // assert object reverted to transient - JDO spec 12.6.7
                // reachability algorithm should be run again at commit() to check if instances
                // are no longer reachable and so should revert to transient
                assertFalse("unreachable committed delta: isPersistent() == true", JDOHelper.isPersistent(square3));
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
                // assert there is no "alpha" record in DB
                tx.begin();
                Query q = pm.newQuery(Square.class, "id == :val");
                Collection c = (Collection)q.execute(new Integer(101));
                assertTrue("unexpectedly encountered Square that shouldnt have been persisted", c.size() == 0);
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
            clean(ShapeHolder.class);
            clean(Square.class);
        }
    }
    
    /**
     * Tests wether BaseItems are reachable through interface and base class
     * fields and correctly persisted.
     */
    public void testBaseOnQuery()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Square square1 = new Square(101, 240.9, 310.9);
                Square square2 = new Square(102, 303.7, 211.7);
                ShapeHolder holder1 = new ShapeHolder(303);
                holder1.setShape1(square2);
                Square square3 = new Square(103, 120.0, 120.0);
                ShapeHolder holder2 = new ShapeHolder(304);
                holder2.setShape1(square3);
                Square square4 = new Square(104, 101.0, 102.0);
                ShapeHolder holder3 = new ShapeHolder(305);
                holder3.setShape1(square4);
                // now persist square1, square2 (through holder1), square3 (through holder3) and
                // square4 (through holder4)
                pm.makePersistent(square1);
                pm.makePersistent(holder1);
                pm.makePersistent(holder2);
                pm.makePersistent(holder3);
                tx.commit();
                // assert square1, square2, square3 and square4 are persistent
                assertTrue("committed square1: isPersistent() == false", JDOHelper.isPersistent(square1));
                assertTrue("committed square2: isPersistent() == false", JDOHelper.isPersistent(square2));
                assertTrue("committed square3: isPersistent() == false", JDOHelper.isPersistent(square3));
                assertTrue("committed square4: isPersistent() == false", JDOHelper.isPersistent(square4));
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
                // assert the DB contains the correct data
                tx.begin();
                Query q = pm.newQuery(Square.class, "id == :val");
                Collection c = (Collection)q.execute(new Integer(101));
                assertTrue("Square1 wasnt persisted but should have been", c.size() == 1);
                c = (Collection)q.execute(new Integer(102));
                assertTrue("Square2 wasnt persisted but should have been", c.size() == 1);
                c = (Collection)q.execute(new Integer(103));
                assertTrue("Square3 wasnt persisted but should have been", c.size() == 1);
                c = (Collection)q.execute(new Integer(104));
                assertTrue("Square4 wasnt persisted but should have been", c.size() == 1);
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
            clean(ShapeHolder.class);
            clean(Square.class);
        }
    }

    /**
     * Tests whether a ClassCastException is thrown upon incorrect (that is undeclared) assignment 
     * to a interface field. See JDO spec 1.0 �6.4.3 Object Class type: "If an implementation 
     * restricts instances to be assigned to the field, a ClassCastException must be thrown at the 
     * time of any incorrect assignment."
     */
    public void testIncorrectAssignment()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Square square1 = new Square(101, 250.5, 650.0);
                ShapeHolder2 holder = new ShapeHolder2(301);
                holder.setShape1(square1);
                holder.setShape2(null);
                try
                {
                    // Try to set "shape2" field to this Square - should be prohibited.
                    LOG.info(">> Persisting ShapeHolder.shape2 as Square");
                    pm.makePersistent(holder);
                    LOG.info(">> makePersistent called");
                    tx.commit();
                    LOG.info(">> committed");
                    assertTrue("incorrect assignment of Square instance was accepted", false);
                }
                catch (ClassCastException e)
                {
                    // this exception is expected
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
            clean(ShapeHolder2.class);
            clean(Square.class);
        }
    }

    /**
     * Generic query for instances of the candidate class and its subclasses which have the specified name.
     * @param pm PersistenceManager to use
     * @param candidateClass the candidate class
     * @param name the name to find
     * @return the first instance found or <code>null</code> if none exists
     */
    private static Object queryByName(PersistenceManager pm, Class candidateClass, String name)
    {
        Query query = null;
        try
        {
            query = pm.newQuery(candidateClass, "name == param");
            query.declareImports("import java.lang.String;");
            query.declareParameters("java.lang.String param");
            Collection result = (Collection)query.execute(name);
            Iterator iter = result.iterator();
            if (iter.hasNext())
            {
                return iter.next();
            }
            else
            {
                return null;
            }
        }
        finally
        {
            if (query != null)
            {
                query.closeAll();
            }
        }
    }
}