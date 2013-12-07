package org.datanucleus.tests;
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
 

Contributions :
    ...
***********************************************************************/


import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.objects.ObjectHolder;
import org.jpox.samples.objects.ObjectImpl1;
import org.jpox.samples.models.referenceMapping.Customer;
import org.jpox.samples.models.referenceMapping.Folder;

/**
 * Series of tests for the use of java.lang.Object.
 * 
 * @version $Revision: 1.1 $
 */
public class ObjectsTest extends JDOPersistenceTestCase
{
    public ObjectsTest(String name)
    {
        super(name);
    }

    /**
     * Test for 1-1 relations where 1 side is marked as an Object, though is
     * really PersistenceCapable.
     */
    public void testOneToOneRelation()
    {
        try
        {
            Object holderId = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects.
                tx.begin();

                ObjectImpl1 impl1 = new ObjectImpl1("First implementation");
                ObjectHolder holder = new ObjectHolder("First Holder");
                holder.setObject3(impl1);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of java.lang.Object objects.",false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the holder and check its contents
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ObjectHolder holder = (ObjectHolder)pm.getObjectById(holderId);
                assertTrue("Holder was not retrieved correctly", holder != null);
                assertTrue("Holder nonserialised object is null!", holder.getObject3() != null);
                assertEquals("Holder nonserialised object was of incorrect type", 
                    holder.getObject3().getClass().getName(), "org.jpox.samples.objects.ObjectImpl1");
                assertEquals("Holder nonserialised object has incorrect name!", 
                    ((ObjectImpl1)holder.getObject3()).getName(),"First implementation");

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during retrieval of java.lang.Object objects.",false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Update the object field to use a different object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ObjectHolder holder = (ObjectHolder)pm.getObjectById(holderId);
                holder.setObject3(new ObjectImpl1("Second implementation"));

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during update of object container with new object reference : " + ue.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the container and check its contents
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ObjectHolder holder = (ObjectHolder)pm.getObjectById(holderId);
                assertTrue("Holder was not retrieved correctly", holder != null);
                assertTrue("Holder nonserialised object is null!", holder.getObject3() != null);
                assertEquals("Holder nonserialised object was of incorrect type", 
                    holder.getObject3().getClass().getName(), "org.jpox.samples.objects.ObjectImpl1");
                assertEquals("Holder nonserialised object has incorrect name!", 
                    ((ObjectImpl1)holder.getObject3()).getName(),"Second implementation");

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during retrieval of java.lang.Object objects.",false);
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
            clean(ObjectHolder.class);
            clean(ObjectImpl1.class);
        }
    }

    /**
     * Test for 1-N relations the collection is of Object (not embedded) using a Join table.
     */
    public void testOneToManyRelationWithJoinTable()
    {
        try
        {
            Object holderId = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects.
                tx.begin();

                ObjectImpl1 impl1 = new ObjectImpl1("Second implementation");
                ObjectHolder holder = new ObjectHolder("First Holder");
                holder.getSet1().add(impl1);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of Collection of java.lang.Object objects.",false);
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

                ObjectHolder holder = (ObjectHolder)pm.getObjectById(holderId);
                assertTrue("Holder was not retrieved correctly", holder != null);
                assertEquals("Holder has incorrect number of objects", holder.getSet1().size(), 1);
                Object obj = holder.getSet1().iterator().next();
                assertEquals("Object contained in container is of incorrect type", 
                    obj.getClass(), org.jpox.samples.objects.ObjectImpl1.class);
                ObjectImpl1 obj1 = (ObjectImpl1)obj;
                assertEquals("Object container in container has incorrect name", 
                    obj1.getName(), "Second implementation");

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during retrieval of container of java.lang.Object objects.",false);
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
            clean(ObjectHolder.class);
            clean(ObjectImpl1.class);
        }
    }

    /**
     * Test for 1-1 relations where 1 side is marked as an Object, though is
     * really PersistenceCapable using xcalia mapping
     */
    public void testOneToOneXcalia()
    {
        try
        {
            Object folderId = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects.
                tx.begin();

                Customer customer = new Customer(1);

                Folder folder = new Folder(1);
                folder.setCustomer(customer);

                pm.makePersistent(folder);


                tx.commit();
                folderId = pm.getObjectId(folder);
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of Folder and Customer.",false);
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

                Folder folder = (Folder)pm.getObjectById(folderId);
                assertTrue("Folder was not retrieved correctly", folder != null);

                Customer customer = (Customer) folder.getCustomer();
                assertTrue("Customer was not retrieved correctly", customer != null);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during retrieval.",false);
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
            clean(Folder.class);
            clean(Customer.class);
        }
    }
}