/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.application;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.StorageTester;
import org.jpox.samples.identity.application.Car;
import org.jpox.samples.identity.application.ComposedIntIDBase;
import org.jpox.samples.identity.application.ComposedIntIDSub;
import org.jpox.samples.identity.application.ComposedMixedIDBase;
import org.jpox.samples.identity.application.ComposedMixedIDSub;
import org.jpox.samples.identity.application.ComposedStringIDBase;
import org.jpox.samples.identity.application.ComposedStringIDSub;
import org.jpox.samples.identity.application.FourByFour;
import org.jpox.samples.identity.application.SFAIDCharIdentity;
import org.jpox.samples.identity.application.SFAIDCharObjIdentity;
import org.jpox.samples.identity.application.SFAIDIntegerIdentity;
import org.jpox.samples.identity.application.SFAIDIntegerObjIdentity;
import org.jpox.samples.identity.application.SFAIDLongIdentity;
import org.jpox.samples.identity.application.SFAIDLongObjIdentity;
import org.jpox.samples.identity.application.SFAIDShortIdentity;
import org.jpox.samples.identity.application.SFAIDShortObjIdentity;
import org.jpox.samples.identity.application.SFAIDStringIdentity;
import org.jpox.samples.identity.application.SimpleIntIDBase;
import org.jpox.samples.identity.application.SimpleIntIDSub;
import org.jpox.samples.identity.application.SimpleStringIDBase;
import org.jpox.samples.identity.application.SimpleStringIDSub;
import org.jpox.samples.identity.application.UnorderedPrimaryKeyFields;
import org.jpox.samples.interfaces.Triangle;
import org.jpox.samples.types.container.ContainerItem;
import org.jpox.samples.types.set.Set1;

/**
 * Test the storage using application identity.
 */
public class ApplicationIdentityTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    StorageTester tester = null;

    public ApplicationIdentityTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[] 
               {
                    SFAIDStringIdentity.class,
                    SFAIDLongIdentity.class,
                    SFAIDLongObjIdentity.class,
                    SFAIDIntegerIdentity.class,
                    SFAIDIntegerObjIdentity.class,
                    SFAIDShortIdentity.class,
                    SFAIDShortObjIdentity.class,
                    SFAIDCharIdentity.class,
                    SFAIDCharObjIdentity.class,
                    ComposedMixedIDBase.class,
                    ComposedIntIDBase.class,
                    ComposedStringIDBase.class,
                    ComposedMixedIDSub.class,
                    ComposedIntIDSub.class,
                    ComposedStringIDSub.class,
                    SimpleIntIDSub.class,
                    SimpleStringIDSub.class,
                    SimpleIntIDBase.class,
                    SimpleStringIDBase.class,
                    Set1.class,
                    Car.class,
                    FourByFour.class,
                    UnorderedPrimaryKeyFields.class,
                    Triangle.class,
                });
            initialised = true;
        }
        tester = new StorageTester(pmf);
    }

    /**
     * Test for SingleField StringIdentity.
     * @throws Exception
     */
    public void testStringSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDStringIdentity.class);
        }
        finally
        {
            clean(SFAIDStringIdentity.class);
        }
    }

    /**
     * Test for SingleField CharIdentity.
     * @throws Exception
     */
    public void testCharSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDCharIdentity.class);
        }
        finally
        {
            clean(SFAIDCharIdentity.class);
        }
    }

    /**
     * Test for SingleField CharIdentity (object form).
     * @throws Exception
     */
    public void testCharObjSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDCharObjIdentity.class);
        }
        finally
        {
            clean(SFAIDCharObjIdentity.class);
        }
    }

    /**
     * Test for SingleField LongIdentity.
     * @throws Exception
     */
    public void testLongSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDLongIdentity.class);
        }
        finally
        {
            clean(SFAIDLongIdentity.class);
        }
    }

    /**
     * Test for SingleField LongIdentity (object form).
     * @throws Exception
     */
    public void testLongObjSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDLongObjIdentity.class);
        }
        finally
        {
            clean(SFAIDLongObjIdentity.class);
        }
    }

    /**
     * Test for SingleField IntegerIdentity.
     * @throws Exception
     */
    public void testIntegerSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDIntegerIdentity.class);
        }
        finally
        {
            clean(SFAIDIntegerIdentity.class);
        }
    }

    /**
     * Test for SingleField IntegerIdentity (object form).
     * @throws Exception
     */
    public void testIntegerObjSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDIntegerObjIdentity.class);
        }
        finally
        {
            clean(SFAIDIntegerObjIdentity.class);
        }
    }

    /**
     * Test for SingleField ShortIdentity.
     * @throws Exception
     */
    public void testShortSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDShortIdentity.class);
        }
        finally
        {
            clean(SFAIDShortIdentity.class);
        }
    }

    /**
     * Test for SingleField ShortIdentity (object form).
     * @throws Exception
     */
    public void testShortObjSingleFieldIdentity()
    throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SFAIDShortObjIdentity.class);
        }
        finally
        {
            clean(SFAIDShortObjIdentity.class);
        }
    }

    /**
     * Test for SingleField ObjectIdentity.
     * NOTE THAT THIS IS NOT RUN SINCE IT IS UNRELIABLE TO RELY ON MILLISECS FOR PK ID ON A FAST MACHINE.
     * @throws Exception
     */
    /*public void testObjectSingleFieldIdentity()
    throws Exception
    {
        if (storeMgr instanceof RDBMSStoreManager)
        {
            DatastoreAdapter dba = ((RDBMSStoreManager)storeMgr).getDatastoreAdapter();
            if (!dba.supportsOption(DatastoreAdapter.DATETIME_STORES_MILLISECS))
            {
                LOG.warn("Database doesnt support storing milliseconds in DATETIME fields so omitting test");
                return;
            }
        }
        try
        {
            tester.runStorageTestForClass(SFAIDDateIdentity.class);
        }
        finally
        {
            clean(SFAIDDateIdentity.class);
        }
    }*/

    /**
     * Test for PK using int.
     * @throws Exception
     */
    public void testSimpleIntID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SimpleIntIDBase.class);
        }
        finally
        {
            clean(SimpleIntIDBase.class);
        }
    }    

    /**
     * Test for PK using String.
     * @throws Exception
     */
    public void testSimpleStringID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SimpleStringIDBase.class);
        }
        finally
        {
            clean(SimpleStringIDBase.class);
        }
    }

    /**
     * Test for PK using (int,int).
     * @throws Exception
     */
    public void testComposedIntID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedIntIDBase.class);
        }
        finally
        {
            clean(ComposedIntIDBase.class);
        }
    }

    /**
     * Test for PK using (String,String).
     * @throws Exception
     */
    public void testComposedStringID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedStringIDBase.class);
        }
        finally
        {
            clean(ComposedStringIDBase.class);
        }
    }

    /**
     * Test for PK using (int,String,Double).
     * @throws Exception
     */
    public void testComposedMixedID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedMixedIDBase.class);
        }
        finally
        {
            clean(ComposedMixedIDBase.class);
        }
    }

    /**
     * Test for PK using int + inheritance.
     * @throws Exception
     */
    public void testChildSimpleIntID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SimpleIntIDSub.class);
        }
        finally
        {
            clean(SimpleIntIDSub.class);
        }
    }

    /**
     * Test for PK using String + inheritance.
     * @throws Exception
     */
    public void testChildSimpleStringID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(SimpleStringIDSub.class);
        }
        finally
        {
            clean(SimpleStringIDSub.class);
        }
    }

    /**
     * Test for PK using (int,int) + inheritance.
     * @throws Exception
     */
    public void testChildComposedIntID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedIntIDSub.class);
        }
        finally
        {
            clean(ComposedIntIDSub.class);
        }
    }

    /**
     * Test for PK using (String,String) + inheritance.
     * @throws Exception
     */
    public void testChildComposedStringID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedStringIDSub.class);
        }
        finally
        {
            clean(ComposedStringIDSub.class);
        }
    }

    /**
     * Test for PK using (int,String,Double) + inheritance.
     * @throws Exception
     */
    public void testChildComposedMixedID() throws Exception
    {
        try
        {
            tester.runStorageTestForClass(ComposedMixedIDSub.class);
        }
        finally
        {
            clean(ComposedMixedIDSub.class);
        }
    }

    /**
     * Test querying of application id 1-N relation via contains().
     */
    public void testQueryUsingContains()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                // populate data to test
                Set1 normal1 = new Set1();
                normal1.setIdentifierA(1);
                normal1.setIdentifierB("id1");
                ContainerItem normal1Item1 = new ContainerItem("normal1Item1", 11.0, 101);
                normal1Item1.setIdentifierA(1001);
                normal1Item1.setIdentifierB("id1001");
                ContainerItem normal1Item2 = new ContainerItem("normal1Item2", 12.0, 102);
                normal1Item2.setIdentifierA(1002);
                normal1Item2.setIdentifierB("id1002");
                normal1.addItem(normal1Item1);
                normal1.addItem(normal1Item2);

                Set1 normal2 = new Set1();
                normal2.setIdentifierA(2);
                normal2.setIdentifierB("id2");
                ContainerItem normal2Item1 = new ContainerItem("normal2Item1", 21.0, 201);
                normal2Item1.setIdentifierA(2001);
                normal2Item1.setIdentifierB("id2001");
                ContainerItem normal2Item2 = new ContainerItem("normal2Item2", 22.0, 202);
                normal2Item2.setIdentifierA(2002);
                normal2Item2.setIdentifierB("id2002");
                normal2.addItem(normal2Item1);
                normal2.addItem(normal2Item2);

                pm.makePersistent(normal1);
                pm.makePersistent(normal2);
                tx.commit();

                Object normal2Id = pm.getObjectId(normal2);

                tx.begin();

                Query query = pm.newQuery(pm.getExtent(Set1.class, true));
                query.declareImports("import org.jpox.samples.types.container.ContainerItem");
                query.declareParameters("ContainerItem item");
                query.setFilter("items.contains(item)");
                Collection c = (Collection) query.execute(normal2Item1);
                Iterator it = c.iterator();
                int expectedReturnedElements = 1;
                int countReturnedElements = 0;
                while (it.hasNext())
                {
                    countReturnedElements++;
                    Set1 returnedOwner = (Set1) it.next();
                    assertEquals("Object returned was not the expected", normal2Id, pm.getObjectId(returnedOwner));
                    assertEquals("ID for object returned is not the expected", returnedOwner.getIdentifierA(), 2);
                    assertEquals("ID for object returned is not the expected", returnedOwner.getIdentifierB(), "id2");
                }
                assertEquals("number of elements returned are not the expected", 
                    countReturnedElements, expectedReturnedElements);
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
            clean(Set1.class);
        }
    }

    /**
     * Test for querying of PK fields of a composite PK.
     * Query of candidate class as well as 1-1 relation FK fields.
     */
    public void testQueryUsingPrimaryKeyFields()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();
                Car audi = new Car("A6", "O6");
                Car mercedes = new Car("Class E", "OE");
                Car volks = new Car("Beetle", "OB");
                FourByFour c1 = new FourByFour("C1", "O1");
                FourByFour c2 = new FourByFour("C2", "O2");
                audi.setTowedCar(c1);
                mercedes.setTowedCar(c2);
                pm.makePersistentAll(new Car[]{audi, mercedes, volks});
                pm.flush();

                Query q = pm.newQuery(Car.class);
                q.declareParameters("String pOwnerID, String pCarID");
                q.setFilter("ownerID == pOwnerID && carID == pCarID");
                Collection c = (Collection) q.execute("O6", "A6");
                assertEquals(1, c.size());
                assertEquals("A6", ((Car) c.iterator().next()).getCarID());
                assertEquals("O6", ((Car) c.iterator().next()).getOwnerID());

                q = pm.newQuery(Car.class);
                q.declareVariables("Car tow");
                q.declareParameters("String pOwnerID, String pCarID");
                q.setFilter("towedCar == tow && tow.ownerID == pOwnerID && tow.carID == pCarID");
                c = (Collection) q.execute("O1", "C1");
                assertEquals(1, c.size());
                assertEquals("A6", ((Car) c.iterator().next()).getCarID());
                assertEquals("O6", ((Car) c.iterator().next()).getOwnerID());

                q = pm.newQuery(Car.class);
                q.declareParameters("String pOwnerID, String pCarID");
                q.setFilter("towedCar.ownerID == pOwnerID && towedCar.carID == pCarID");
                c = (Collection) q.execute("O1", "C1");
                assertEquals(1, c.size());
                assertEquals("A6", ((Car) c.iterator().next()).getCarID());
                assertEquals("O6", ((Car) c.iterator().next()).getOwnerID());

                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.commit();
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
                Extent ex = pm.getExtent(Car.class, true);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    Car car = (Car)iter.next();
                    car.setTowedCar(null);
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
            clean(Car.class);
            clean(FourByFour.class);
        }
    }

    /**
     * check if primary key fields are never null after loading from database
     */
    public void testNonNullPrimaryKeyFieldsAfterQuery()
    {
        try
        {
            Object tid1;
            Object tid2;
            Object upkfid1;
            Object upkfid2;
            boolean retainValues;
            
            PersistenceManager pm = pmf.getPersistenceManager();
            try
            {
                retainValues = false;
                pm.currentTransaction().setRetainValues(retainValues);
                pm.currentTransaction().begin();
                
                Triangle t1 = new Triangle(1, "t1", 7.0, 8.0);
                Triangle t2 = new Triangle(2, "t2", 9.0, 7.0);
                pm.makePersistent(t1);
                pm.makePersistent(t2);
                pm.flush();
                
                UnorderedPrimaryKeyFields upkf1 = new UnorderedPrimaryKeyFields(1,"upkf1","first1","medium1","last1");
                UnorderedPrimaryKeyFields upkf2 = new UnorderedPrimaryKeyFields(2,"upkf2","first2","medium2","last2");
                pm.makePersistent(upkf1);
                pm.makePersistent(upkf2);
                pm.flush();
                
                verifyTriangleByQueryInMakePersistentTransaction(pm);
                verifyUPKFieldsByQueryInMakePersistentTransaction(pm);
                
                pm.currentTransaction().commit();
                assertTriangleT2AfterCommit(t2,retainValues);
                assertUPKFields2AfterCommit(upkf2,retainValues);
                
                tid1 = pm.getObjectId(t1);
                tid2 = pm.getObjectId(t2);
                
                upkfid1 = pm.getObjectId(upkf1);
                upkfid2 = pm.getObjectId(upkf2);
                
                /////////////////
                // retainValues = false 
                /////////////////
                retainValues = false;
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                System.gc();
                // verify after garbage collector
                verifyTriangleByQuery(pm,retainValues);
                
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                /////////////////
                // retainValues = true 
                /////////////////
                retainValues = true;
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                System.gc();
                // verify after garbage collector
                verifyTriangleByQuery(pm,retainValues);
                
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
            }
            finally
            {
                if (pm.currentTransaction().isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
            
            System.gc();
            
            //test it all again with a new PM
            pm = pmf.getPersistenceManager();
            try
            {            
                /////////////////
                // retainValues = false 
                /////////////////
                retainValues = false;
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                System.gc();
                // verify after garbage collector
                verifyTriangleByQuery(pm,retainValues);
                
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                /////////////////
                // retainValues = true 
                /////////////////
                retainValues = true;
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
                
                System.gc();
                // verify after garbage collector
                verifyTriangleByQuery(pm,retainValues);
                
                //try 1
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                // do iy again
                verifyTriangleByQuery(pm,retainValues);
                verifyUPKFieldsByQuery(pm,retainValues);
                
                //by objectid validate=false
                verifyTriangleByGetObjectById(pm,tid1,tid2,false,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,false,retainValues);
                
                //by objectid validate=true
                verifyTriangleByGetObjectById(pm,tid1,tid2,true,retainValues);
                verifyUPKFieldsByGetObjectById(pm,upkfid1,upkfid2,true,retainValues);
            }
            finally
            {
                if (pm.currentTransaction().isActive())
                {
                    pm.currentTransaction().rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(Triangle.class);
            clean(UnorderedPrimaryKeyFields.class);
        }
    }

    private void assertTriangleT1(Triangle t)
    {
        assertEquals(1, t.getId());
        assertEquals("t1", t.getComposed());
        assertTrue("area triangle 1 is not the same", ((7.0 * 8.0) / 2) == t.getArea());

        // until the before above assert, the "Triangle t" could be in hollow
        // state, and after that it might be in persi
        assertEquals(1, t.getId());
        assertEquals("t1", t.getComposed());
    }

    private void assertTriangleT2(Triangle t)
    {
        assertEquals(2, t.getId());
        assertEquals("t2", t.getComposed());
        assertTrue("area triangle 2 is not the same", ((9.0 * 7.0) / 2) == t.getArea());
        assertEquals(2, t.getId());
        assertEquals("t2", t.getComposed());
    }

    private void assertTriangleT2AfterCommit(Triangle t, boolean retainValues)
    {
        assertEquals(2, t.getId());
        assertEquals("t2", t.getComposed());
        if (retainValues)
        {
            assertEquals("t2", t.getComposed());
            assertTrue("area triangle 2 is not the same", ((9.0 * 7.0) / 2) == t.getArea());
        }
    }

    private void verifyTriangleByQuery(PersistenceManager pm, boolean retainValues)
    {
        Query query;
        Collection c;
        Iterator it;
        Triangle t;
        pm.currentTransaction().setRetainValues(retainValues);
        pm.currentTransaction().begin();
        query = pm.newQuery(pm.getExtent(Triangle.class, true));
        query.setOrdering("width ascending");
        c = (Collection) query.execute();
        it = c.iterator();
        t = (Triangle) it.next();
        assertTriangleT1(t);
        t = (Triangle) it.next();
        assertTriangleT2(t);
        pm.currentTransaction().commit();
        assertTriangleT2AfterCommit(t, retainValues);
    }

    private void verifyTriangleByQueryInMakePersistentTransaction(PersistenceManager pm)
    {
        Query query;
        Collection c;
        Iterator it;
        Triangle t;
        query = pm.newQuery(pm.getExtent(Triangle.class, true));
        query.setOrdering("width ascending");
        c = (Collection) query.execute();
        it = c.iterator();
        t = (Triangle) it.next();
        assertTriangleT1(t);
        t = (Triangle) it.next();
        assertTriangleT2(t);
    }

    private void verifyTriangleByGetObjectById(PersistenceManager pm, Object id1, Object id2, boolean validate, boolean retainValues)
    {
        Triangle t;
        pm.currentTransaction().setRetainValues(retainValues);
        pm.currentTransaction().begin();
        t = (Triangle) pm.getObjectById(id1, validate);
        assertTriangleT1(t);
        t = (Triangle) pm.getObjectById(id2, validate);
        assertTriangleT2(t);
        pm.currentTransaction().commit();
        assertTriangleT2AfterCommit(t, retainValues);
    }

    private void assertUPKFields1(UnorderedPrimaryKeyFields upkf)
    {
        assertEquals(1, upkf.getId());
        assertEquals("upkf1", upkf.getComposed());
        assertEquals("firstField is not the same", "first1", upkf.getFirstField());
        assertEquals("mediumField is not the same", "medium1", upkf.getMediumField());
        assertEquals("lastField is not the same", "last1", upkf.getLastField());
        assertEquals(1, upkf.getId());
        assertEquals("upkf1", upkf.getComposed());
    }

    private void assertUPKFields2(UnorderedPrimaryKeyFields upkf)
    {
        assertEquals(2, upkf.getId());
        assertEquals("upkf2", upkf.getComposed());
        assertEquals("firstField is not the same", "first2", upkf.getFirstField());
        assertEquals("mediumField is not the same", "medium2", upkf.getMediumField());
        assertEquals("lastField is not the same", "last2", upkf.getLastField());
        assertEquals(2, upkf.getId());
        assertEquals("upkf2", upkf.getComposed());
    }

    private void assertUPKFields2AfterCommit(UnorderedPrimaryKeyFields upkf, boolean retainValues)
    {
        assertEquals(2, upkf.getId());
        assertEquals("upkf2", upkf.getComposed());
        if (retainValues)
        {
            assertEquals("firstField is not the same", "first2", upkf.getFirstField());
            assertEquals("mediumField is not the same", "medium2", upkf.getMediumField());
            assertEquals("lastField is not the same", "last2", upkf.getLastField());
        }
    }

    private void verifyUPKFieldsByQueryInMakePersistentTransaction(PersistenceManager pm)
    {
        Query query;
        Collection c;
        Iterator it;
        UnorderedPrimaryKeyFields obj;
        query = pm.newQuery(pm.getExtent(UnorderedPrimaryKeyFields.class, true));
        query.setOrdering("firstField ascending");
        c = (Collection) query.execute();
        it = c.iterator();
        obj = (UnorderedPrimaryKeyFields) it.next();
        assertUPKFields1(obj);
        obj = (UnorderedPrimaryKeyFields) it.next();
        assertUPKFields2(obj);
    }

    private void verifyUPKFieldsByQuery(PersistenceManager pm, boolean retainValues)
    {
        Query query;
        Collection c;
        Iterator it;
        UnorderedPrimaryKeyFields obj;
        pm.currentTransaction().setRetainValues(retainValues);
        pm.currentTransaction().begin();
        query = pm.newQuery(pm.getExtent(UnorderedPrimaryKeyFields.class, true));
        query.setOrdering("firstField ascending");
        c = (Collection) query.execute();
        it = c.iterator();
        obj = (UnorderedPrimaryKeyFields) it.next();
        assertUPKFields1(obj);
        obj = (UnorderedPrimaryKeyFields) it.next();
        assertUPKFields2(obj);
        pm.currentTransaction().commit();
        assertUPKFields2AfterCommit(obj, retainValues);
    }

    private void verifyUPKFieldsByGetObjectById(PersistenceManager pm, Object id1, Object id2, boolean validate, boolean retainValues)
    {
        UnorderedPrimaryKeyFields obj;
        pm.currentTransaction().setRetainValues(retainValues);
        pm.currentTransaction().begin();
        obj = (UnorderedPrimaryKeyFields) pm.getObjectById(id1, validate);
        assertUPKFields1(obj);
        obj = (UnorderedPrimaryKeyFields) pm.getObjectById(id2, validate);
        assertUPKFields2(obj);
        pm.currentTransaction().commit();
        assertUPKFields2AfterCommit(obj, retainValues);
    }
}