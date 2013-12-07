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
2006 Andy Jefferson - added tests for serialisation of collection, map etc
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.serialised.SerialisedHolder;
import org.datanucleus.samples.serialised.SerialisedObject;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.interfaces.Circle;
import org.jpox.samples.interfaces.ShapeHolder;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.one_many.collection.PCJoinElement;
import org.jpox.samples.one_many.collection.SetHolder;
import org.jpox.samples.one_many.map.MapHolder;
import org.jpox.samples.one_many.map.MapValueItem;

/**
 * Tests for Serialisation of persistable objects.
 */
public class SerializationTest extends JDOPersistenceTestCase
{
    public SerializationTest(String name)
    {
        super(name);
    }

    /**
     * Test for the serialisation of OIDs.
     */
    public void testOIDSerialization()
    {
        Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
        Object id = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            //test detach and attach
            tx.begin();
            pm.makePersistent(woody);
            tx.commit();

            id = pm.getObjectId(woody);
            byte[] serialized = serialise(id);
            Object deserialized = deserialise(serialized);
            assertEquals("Deserialized is not equals original object",id,deserialized);
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail(e.toString());
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }

        // Clean out any data created
        clean(Employee.class);
    }

    /**
     * Test for serialisation of PC fields
     */
    public void testSerialisedPC()
    {
        try
        {
            Object holderId = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist the object with serialised fields
            try
            {
                tx.begin();

                SerialisedHolder holder = new SerialisedHolder("Holder(1)", new SerialisedObject("My Description(1)"));
                pm.makePersistent(holder);

                // Update holder and serialised object fields to check that they get to the datastore
                holder.setName("Holder(2)");
                holder.getSerialisedPC().setDescription("My Description(2)");

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisted object with serialised PC field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SerialisedHolder holder = (SerialisedHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(2)"));
                assertTrue("Retrieved holder has null serialised object!", holder.getSerialisedPC() != null);
                assertEquals("Retrieved serialised object description is incorrect : ",holder.getSerialisedPC().getDescription(), "My Description(2)");

                // Update holder and serialised object fields to check that they get to the datastore
                holder.getSerialisedPC().setDescription("My Description(3)");
                holder.setName("Holder(3)");

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised PC field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the most recent update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SerialisedHolder holder = (SerialisedHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(3)"));
                assertTrue("Retrieved holder has null serialised object!", holder.getSerialisedPC() != null);
                assertEquals("Retrieved serialised object description is incorrect : ",holder.getSerialisedPC().getDescription(), "My Description(3)");

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised PC field : " + e.getMessage());
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
            clean(SerialisedHolder.class);
        }
    }

    /**
     * Test for attach/detach of serialised PC fields
     */
    public void testSerialisedPCDetach()
    {
        try
        {
            Object holderId = null;
            SerialisedHolder detachedHolder = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist the object with serialised fields
            try
            {
                tx.begin();

                SerialisedHolder holder = new SerialisedHolder("Holder(1)", new SerialisedObject("My Description(1)"));
                pm.makePersistent(holder);

                // Update holder and serialised object fields to check that they get to the datastore
                holder.setName("Holder(2)");
                holder.getSerialisedPC().setDescription("My Description(2)");

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisted object with serialised PC field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the most recent update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SerialisedHolder holder = (SerialisedHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(2)"));
                assertTrue("Retrieved holder has null serialised object!", holder.getSerialisedPC() != null);
                assertEquals("Retrieved serialised object description is incorrect : ",holder.getSerialisedPC().getDescription(), "My Description(2)");

                // Detach the holder
                detachedHolder = (SerialisedHolder)pm.detachCopy(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised PC field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the detached object
            detachedHolder.setName("Holder(3)");
            detachedHolder.getSerialisedPC().setDescription("My Description(3)");

            // Attach the detached object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
     
                pm.makePersistent(detachedHolder);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while attaching object with serialised PC field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the attach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SerialisedHolder holder = (SerialisedHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(3)"));
                assertTrue("Retrieved holder has null serialised object!", holder.getSerialisedPC() != null);
                assertEquals("Retrieved serialised object description is incorrect : ",holder.getSerialisedPC().getDescription(), "My Description(3)");

                detachedHolder = (SerialisedHolder)pm.detachCopy(holder);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised PC field : " + e.getMessage());
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
            clean(SerialisedHolder.class);
        }
    }

    /**
     * Test for serialisation of Interface fields
     */
    public void testSerialisedInterface()
    {
        try
        {
            Object holderId = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist the object with serialised fields
            try
            {
                tx.begin();
                ShapeHolder holder = new ShapeHolder(1);
                holder.setShape2(new Circle(1, 25.0));
                pm.makePersistent(holder);

                // Update holder and serialised object fields to check that they get to the datastore
                holder.setId(2);
                ((Circle)holder.getShape1()).setRadius(40.0);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisted object with serialised Interface field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ShapeHolder holder = (ShapeHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder 'id' field is incorrect", holder.getId() == 2);
                assertTrue("Retrieved holder has null serialised object!", holder.getShape1() != null);
                assertTrue("Retrieved holder has serialised object of incorrect type!", holder.getShape1() instanceof Circle);
                assertEquals("Retrieved serialised object description is incorrect : ", ((Circle)holder.getShape1()).getRadius(), 40.0, 0.01);

                // Update holder and serialised object fields to check that they get to the datastore
                ((Circle)holder.getShape1()).setId(40);
                holder.setId(7);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised Interface field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the most recent update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ShapeHolder holder = (ShapeHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised PC could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getId() == 7);
                assertTrue("Retrieved holder has null serialised object!", holder.getShape1() != null);
                assertTrue("Retrieved holder has serialised object of incorrect type!", holder.getShape1() instanceof Circle);
                assertEquals("Retrieved serialised object description is incorrect : ", ((Circle)holder.getShape1()).getId(), 40);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised Interface field : " + e.getMessage());
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
            clean(ShapeHolder.class);
            clean(Circle.class);
        }
    }

    /**
     * Test for serialisation of collection elements.
     */
    public void testSerialisedCollectionElements()
    {
        try
        {
            Object holderId = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist the object with serialised fields
            try
            {
                tx.begin();

                SetHolder holder = new SetHolder("Holder(3)");
                PCJoinElement elem = new PCJoinElement("Element 1");
                holder.getJoinSetPCSerial().add(elem);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisted object with serialised collection elements field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SetHolder holder = (SetHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised collection elements could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(3)"));
                assertEquals("Number of serialised elements is incorrect", holder.getJoinSetPCSerial().size(), 1);

                PCJoinElement elem = (PCJoinElement)holder.getJoinSetPCSerial().iterator().next();
                assertEquals("Serialised collection element has incorrect description", elem.getName(), "Element 1");

                // Add 2 new elements and remove original
                holder.getJoinSetPCSerial().clear();
                holder.getJoinSetPCSerial().add(new PCJoinElement("Element 2"));
                holder.getJoinSetPCSerial().add(new PCJoinElement("Element 3"));

                holder.setName("Holder(4)");

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised collection elements : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the most recent update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SetHolder holder = (SetHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised collection elements could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(4)"));
                assertEquals("Number of serialised elements is incorrect", holder.getJoinSetPCSerial().size(), 2);

                Iterator elementsIter = holder.getJoinSetPCSerial().iterator();
                boolean containsElem2 = false;
                boolean containsElem3 = false;
                while (elementsIter.hasNext())
                {
                    PCJoinElement elem = (PCJoinElement)elementsIter.next();
                    if (elem.getName().equals("Element 2"))
                    {
                        containsElem2 = true;
                    }
                    else if  (elem.getName().equals("Element 3"))
                    {
                        containsElem3 = true;
                    }
                }
                assertTrue("Element 2 is missing from collection with serialised elements", containsElem2);
                assertTrue("Element 3 is missing from collection with serialised elements", containsElem3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised collection elements field : " + e.getMessage());
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
            clean(SetHolder.class);
        }
    }

    /**
     * Test for serialisation of map values.
     */
    public void testSerialisedMapValues()
    {
        try
        {
            Object holderId = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist the object with serialised fields
            try
            {
                tx.begin();

                MapHolder holder = new MapHolder("Holder(4)");
                MapValueItem val = new MapValueItem("Value 1", "Value 1 Desc");
                holder.getJoinMapNonPCSerial().put("1", val);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisted object with serialised map values field : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                MapHolder holder = (MapHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised map values could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(4)"));
                assertEquals("Number of serialised values is incorrect", holder.getJoinMapNonPCSerial().size(), 1);

                MapValueItem val = (MapValueItem)holder.getJoinMapNonPCSerial().values().iterator().next();
                assertEquals("Serialised map value has incorrect description", val.getName(), "Value 1");

                // Add 2 new values and remove original
                holder.getJoinMapNonPCSerial().clear();
                holder.getJoinMapNonPCSerial().put("2", new MapValueItem("Value 2", "Value 2 desc"));
                holder.getJoinMapNonPCSerial().put("3", new MapValueItem("Value 3", "Value 3 desc"));

                holder.setName("Holder(5)");

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised map values : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve the object again to check the most recent update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                MapHolder holder = (MapHolder)pm.getObjectById(holderId);
                assertTrue("Holder of serialised map values could not be retrieved!", holder != null);
                assertTrue("Holder name is incorrect", holder.getName().equals("Holder(5)"));
                assertEquals("Number of serialised values is incorrect", holder.getJoinMapNonPCSerial().size(), 2);

                Iterator valuesIter = holder.getJoinMapNonPCSerial().values().iterator();
                boolean containsVal2 = false;
                boolean containsVal3 = false;
                while (valuesIter.hasNext())
                {
                    MapValueItem val = (MapValueItem)valuesIter.next();
                    if (val.getName().equals("Value 2"))
                    {
                        containsVal2 = true;
                    }
                    else if  (val.getName().equals("Value 3"))
                    {
                        containsVal3 = true;
                    }
                }
                assertTrue("Element 2 is missing from map with serialised values", containsVal2);
                assertTrue("Element 3 is missing from map with serialised values", containsVal3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while retrieving object with serialised map values field : " + e.getMessage());
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
            clean(MapHolder.class);
            clean(MapValueItem.class);
        }
    }

    /**
     * Method to test the serialisation of a PC object and then deserialisation results in a detached object.
     */
    public void testSerialiseDetach()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            // Persist an object and then serialise it
            Object serialisedTransient = null;
            Object serialisedPersistentNew = null;
            Object serialisedHollow = null;
            try
            {
                tx.begin();
                // Create a transient object, and serialise it
                Employee emp1 = new Employee(1, "Bugs", "Bunny", "bugs.bunny@warnerbros.com", (float)100.0, "12345");
                serialisedTransient = serialise(emp1);

                // Persist the object and serialise it
                pm.makePersistent(emp1);
                serialisedPersistentNew = serialise(emp1);

                tx.commit();
                serialisedHollow = serialise(emp1);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisting and serialising an object : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Deserialise the objects and check the result
            try
            {
                Object deserialisedTransient = deserialise((byte[])serialisedTransient);
                Object deserialisedPersistentNew = deserialise((byte[])serialisedPersistentNew);
                Object deserialisedHollow = deserialise((byte[])serialisedHollow);

                assertTrue("Serialise-deserialise of transient object should have resulted in a non-detached object but didn't", 
                    !JDOHelper.isDetached(deserialisedTransient));
                assertTrue("Serialise-deserialise of transient object should have resulted in a non-persistent object but didn't",
                    !JDOHelper.isPersistent(deserialisedTransient));
                assertTrue("Serialise-deserialise of P_NEW object should have resulted in a detached object but didn't", 
                    JDOHelper.isDetached(deserialisedPersistentNew));
                assertTrue("Serialise-deserialise of P_NEW-new object should have resulted in a non-persistent object but didn't",
                    !JDOHelper.isPersistent(deserialisedPersistentNew));
                assertTrue("Serialise-deserialise of HOLLOW object should have resulted in a detached object but didn't", 
                    JDOHelper.isDetached(deserialisedHollow));
                assertTrue("Serialise-deserialise of HOLLOW object should have resulted in a non-persistent object but didn't",
                    !JDOHelper.isPersistent(deserialisedHollow));
            }
            catch (Exception e)
            {
                fail("Exception thrown while deserialising serialised objects : " + e.getMessage());
            }
        }
        finally
        {
            // Clean out our data
            clean(Employee.class);
        }
    }

    // ------------------------------------------- Utilities -------------------------------------------------

    public static byte[] serialise(Object obj) throws IOException
    {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        ObjectOutputStream outStream = new ObjectOutputStream(new BufferedOutputStream(byteOutStream));

        outStream.writeObject(obj);

        outStream.close();

        byte[] byteArray = byteOutStream.toByteArray();
        byteOutStream.close();
        return byteArray;
    }

    public static Object deserialise(byte[] byteArray) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteInStream));

        Object obj = inputStream.readObject();
        byteInStream.close();
        inputStream.close();
        return obj;
    }
}