/**********************************************************************
Copyright (c) 2003 Mike Martin (TJDO) and others. All rights reserved.
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
2003 Erik Bengtson - fix many tests
2004 Andy Jefferson - updated makeTransientOwnerAndElements to use correct calls
2004 Andy Jefferson - added test for refresh()
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.JDOUserCallbackException;
import javax.jdo.JDOUserException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;
import javax.jdo.identity.LongIdentity;

import junit.framework.Assert;

import org.datanucleus.TransactionEventListener;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.exceptions.TransactionNotActiveException;
import org.datanucleus.api.jdo.exceptions.TransactionNotReadableException;
import org.datanucleus.api.jdo.exceptions.TransactionNotWritableException;
import org.datanucleus.samples.identity.ComposedIntID;
import org.datanucleus.samples.identity.SimpleDatastoreID;
import org.datanucleus.samples.identity.SingleFieldLongID;
import org.datanucleus.samples.instancecallback.AttachDetachCallbackTester;
import org.datanucleus.samples.instancecallback.InstanceCallbackContainer;
import org.datanucleus.samples.instancecallback.InstanceCallbackTester;
import org.datanucleus.samples.lifecyclelistener.BasicListener;
import org.datanucleus.samples.lifecyclelistener.LifecycleListenerSpecification;
import org.datanucleus.samples.metadata.animal.Dog;
import org.datanucleus.samples.widget.CollectionFieldTester;
import org.datanucleus.samples.widget.InversePrimitive;
import org.datanucleus.samples.widget.InverseSetFieldTester;
import org.datanucleus.samples.widget.InverseSetValue;
import org.datanucleus.samples.widget.Primitive;
import org.datanucleus.samples.widget.Widget;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Project;
import org.jpox.samples.persistentinterfaces.Country;

/**
 * This class is a JUnit test class for unit testing "org.jpox.PersistenceManagerImpl".
 *
 * This tests the persistence of all of the basic FCO types as well as
 * collection handling of various types of fields.
 */
public class PersistenceManagerTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    // Person data
    private static String EMAIL[] = {"jon.doe@msn.com", "jane.smith@msn.com", "tom.jones@aol.com"};
    private static String FIRSTNAME[] = {"Jon", "Jane", "Tom"};
    private static String LASTNAME[] = {"Doe", "Smith", "Jones"};

    // Employee data
    private static float EMP_SALARY[] = {75000.00F, 40000.00F, 35000.00F, 25000.00F};
    private static String EMP_SERIAL[] = {"683687A", "439293A", "384018D", "102938X"};

    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the TestCase. 
     */
    public PersistenceManagerTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[] { 
                        InversePrimitive.class, 
                        CollectionFieldTester.class, 
                        InstanceCallbackTester.class, 
                        Person.class, 
                        Manager.class,
                        Primitive.class});
            initialised = true;
        }
    }

    /**
     * Invoked after each test is run
     */
    public void tearDown() throws java.lang.Exception
    {
        super.tearDown();

        // TODO Remove all of this when each test cleans out its own data.
        Extent ext = null;
        java.util.Iterator it = null;
        PersistenceManager pm = pmf.getPersistenceManager();

        try
        {
            // delete all InstanceCallbackContainer objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(InstanceCallbackContainer.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                InstanceCallbackContainer owner = (InstanceCallbackContainer) it.next();
                pm.deletePersistent(owner);
            }

            pm.currentTransaction().commit();

            // delete all InstanceCallbackTester objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(InstanceCallbackTester.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                InstanceCallbackTester tester = (InstanceCallbackTester) it.next();
                tester.setTransientValue(""); // necesaary to avoid exception from jdoPreDelete() for this class only
                pm.deletePersistent(tester);
            }

            pm.currentTransaction().commit();

            // delete all InversePrimitive objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(org.datanucleus.samples.widget.InversePrimitive.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                InversePrimitive ip = (InversePrimitive) it.next();
                pm.deletePersistent(ip);
            }

            pm.currentTransaction().commit();

            // delete all CollectionFieldTester objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(CollectionFieldTester.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                CollectionFieldTester t = (CollectionFieldTester) it.next();
                if( t.getPrimitiveCollection() != null )
                {
                    t.getPrimitiveCollection().clear();
                }
                pm.deletePersistent(t);
            }

            pm.currentTransaction().commit();

            // delete all Primative objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                Primitive p = (Primitive) it.next();
                pm.deletePersistent(p);
            }

            pm.currentTransaction().commit();

            // disassociate all Employees and Departments from their Managers
            pm.currentTransaction().begin();

            ext = pm.getExtent(Manager.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                Manager mgr = (Manager) it.next();
                if (mgr.getSubordinates() != null)
                {
                    mgr.getSubordinates().clear();
                }
                if (mgr.getDepartments() != null)
                {
                    mgr.getDepartments().clear();
                }
            }

            pm.currentTransaction().commit();

            // delete all Employee objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(Employee.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                Employee emp = (Employee) it.next();
                pm.deletePersistent(emp);
            }

            pm.currentTransaction().commit();
            pm.currentTransaction().begin();

            // dekete all Department objects
            ext = pm.getExtent(Department.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                Department d = (Department) it.next();
                pm.deletePersistent(d);
            }

            pm.currentTransaction().commit();

            // delete all Manager objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(Manager.class, false);
            it = ext.iterator();

            while (it.hasNext())
            {
                Manager mgr = (Manager) it.next();
                pm.deletePersistent(mgr);
            }

            pm.currentTransaction().commit();

            // delete all Person objects
            pm.currentTransaction().begin();

            ext = pm.getExtent(Person.class, true);
            it = ext.iterator();

            while (it.hasNext())
            {
                Person person = (Person) it.next();
                pm.deletePersistent(person);
            }

            pm.currentTransaction().commit();

            // delete all objects
            pm.currentTransaction().begin();

            it = pm.getExtent(InverseSetValue.class, true).iterator();

            while (it.hasNext())
            {
                pm.deletePersistent(it.next());
            }

            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
                pm.currentTransaction().commit();

            pm.close();
        }
    }

    /**
     * Test specification of properties on the PM.
     */
    public void testProperties() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        Set<String> pmSupportedProps = pm.getSupportedProperties();
        assertTrue(pmSupportedProps.contains("javax.jdo.option.DetachAllOnCommit"));
        assertTrue(pmSupportedProps.contains("javax.jdo.option.CopyOnAttach"));
        assertTrue(pmSupportedProps.contains("javax.jdo.option.DatastoreWriteTimeoutMillis"));
        assertTrue(pmSupportedProps.contains("javax.jdo.option.DatastoreReadTimeoutMillis"));
        assertTrue(pmSupportedProps.contains("javax.jdo.option.Multithreaded"));
        assertTrue(pmSupportedProps.contains("javax.jdo.option.IgnoreCache"));

        Map<String, Object> pmProps = pm.getProperties();
        assertTrue(pmProps.containsKey("javax.jdo.option.DetachAllOnCommit"));
        assertTrue(pmProps.containsKey("javax.jdo.option.CopyOnAttach"));
        assertTrue(pmProps.containsKey("javax.jdo.option.DatastoreWriteTimeoutMillis"));
        assertTrue(pmProps.containsKey("javax.jdo.option.DatastoreReadTimeoutMillis"));
        assertTrue(pmProps.containsKey("javax.jdo.option.Multithreaded"));
        assertTrue(pmProps.containsKey("javax.jdo.option.IgnoreCache"));
        assertEquals(pmProps.get("javax.jdo.option.DetachAllOnCommit"), false);
        assertEquals(pmProps.get("javax.jdo.option.DatastoreReadTimeoutMillis"), 0);

        pm.setDetachAllOnCommit(true);
        pm.setDatastoreReadTimeoutMillis(100);

        pmProps = pm.getProperties();
        assertTrue(pmProps.containsKey("javax.jdo.option.DetachAllOnCommit"));
        assertTrue(pmProps.containsKey("javax.jdo.option.CopyOnAttach"));
        assertTrue(pmProps.containsKey("javax.jdo.option.DatastoreWriteTimeoutMillis"));
        assertTrue(pmProps.containsKey("javax.jdo.option.DatastoreReadTimeoutMillis"));
        assertTrue(pmProps.containsKey("javax.jdo.option.Multithreaded"));
        assertTrue(pmProps.containsKey("javax.jdo.option.IgnoreCache"));
        assertEquals(pmProps.get("javax.jdo.option.DetachAllOnCommit"), true);
        assertEquals(pmProps.get("javax.jdo.option.DatastoreReadTimeoutMillis"), 100);

        // Check setting of invalid property (this will log a warning currently)
        pm.setProperty("myproperty", "someValue");
    }

    /**
     * Tests storage of various datatypes using the makePersistent method.
     */
    public void testMakePersistent() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Test insert of native and native wrapper data types
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.commit();

                p = null;
                tx.begin();

                // Find the Primitive and check that it was persisted correctly
                Extent clnPrimitive = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
                p = (Primitive) clnPrimitive.iterator().next();
                assertNotNull(p);
                assertPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                Object id = pm.getObjectId(p);
                tx.commit();

                tx.begin();
                pm.makePersistent(p);
                tx.commit();
                Object id2 = pm.getObjectId(p);
                assertEquals(id, id2);
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
            clean(Primitive.class);
        }
    }

    /**
     * Test use of makePersistent with nontransactionalWrite.
     */
    public void testNontransactionalPersist() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);
            tx.setNontransactionalWrite(true);
            Object id = null;
            try
            {
                Employee emp = new Employee(101, "George", "Green", "george.green@mydomain.com", (float)123.5, "12346");
                pm.makePersistent(emp);
                id = pm.getObjectId(emp);
            }
            catch (Exception e)
            {
                fail("Exception thrown while calling makePersistent with nontransactionalWrite " + e.getMessage());
            }
            finally
            {
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);
            try
            {
                tx.begin();
                Employee emp = (Employee)pm.getObjectById(id);
                assertNotNull("Object persisted using nontransactionalWrite is null so wasnt persisted", emp);
                assertEquals("First name is incorrect", "George", emp.getFirstName());
                assertEquals("Second name is incorrect", "Green", emp.getLastName());
                assertEquals("Email is incorrect", "george.green@mydomain.com", emp.getEmailAddress());
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Exception thrown while querying data persisted using nontransactionalWrite " + e.getMessage());
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
            clean(Employee.class);
        }
    }

    /**
     * Test for NontransactionalWrite updates.
     */
    public void testNontransactionalUpdate() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Primitive p = null;
            try
            {
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.setRetainValues(true);
                tx.commit();

                // Update the data without a transaction
                tx.setNontransactionalRead(true);
                tx.setNontransactionalWrite(true);
                p.setIntObject(new Integer(1));
                p.setShortObject(new Short((short)2));
                p = null;
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
            tx.setNontransactionalRead(true);
            tx.setNontransactionalWrite(true);
            try
            {
                // Check read of persisted Primitive making sure that the previous write was persisted ok
                Extent clnPrimitive = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
                p = (Primitive) clnPrimitive.iterator().next();
                assertNotNull(p);
                assertEquals(1, p.getIntObject().intValue());
                assertEquals(2, p.getShortObject().shortValue());
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
            clean(Primitive.class);
        }
    }

    /**
     * Test for NontransactionalWrite and use of rollback after an update.
     */
    public void testNonTransactionalUpdateWithRollback() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Primitive p = null;
            Integer intValue = null;
            Short shortValue = null;
            try
            {
                // Test insert of native and native wrapper data types
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456,
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                intValue = p.getIntObject();
                shortValue = p.getShortObject();
                tx.setRetainValues(true);
                tx.setNontransactionalRead(true);
                tx.setNontransactionalWrite(true);
                tx.begin();
                pm.makePersistent(p);
                tx.commit();

                // Make a nontransactional update
                p.setIntObject(new Integer(1));
                p.setShortObject(new Short((short)2));
                intValue = 1; // Necessary when using nontx.atomic for updates
                shortValue = 2; // Necessary when using nontx.atomic for updates

                // Make a change and roll it back
                tx.setRestoreValues(true);
                tx.begin();
                p.setIntObject(new Integer(3));
                tx.rollback();
                assertEquals(1,p.getIntObject().intValue());
                assertEquals(2,p.getShortObject().shortValue());
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
            tx.setNontransactionalRead(true);
            tx.setNontransactionalWrite(true);
            try
            {
                // Check the datastore for the effect of the updates
                Extent clnPrimitive = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
                p = (Primitive) clnPrimitive.iterator().next();
                assertNotNull(p);
                assertEquals(intValue.intValue(),p.getIntObject().intValue());
                assertEquals(shortValue.shortValue(),p.getShortObject().shortValue());
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
            clean(Primitive.class);
        }
    }

    /**
     * Test of creation of object ids for various types of identities.
     * @throws Exception
     */
    public void testNewObjectIdInstance()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        // Datastore Identity
        try
        {
            pm.newObjectIdInstance(SimpleDatastoreID.class, "1[OID]4[OID]org.jpox.samples.identity.IdentityBoxDatastore");
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Failed to create a new id instance for datastore identity class");
        }

        // Application Identity
        try
        {
            pm.newObjectIdInstance(ComposedIntID.class, "123::456");
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Failed to create a new id instance for application identity class");
        }

        // SingleField Identity
        try
        {
            pm.newObjectIdInstance(SingleFieldLongID.class, new Long(124));
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Failed to create a new id instance for SingleField identity class");
        }
    }

    /**
     * Test for getObjectId() method returning the identity of an object.
     */
    public void testGetObjectId() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Test insert of native and native wrapper data types
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.commit();

                tx.begin();
                Primitive p1 = (Primitive) pm.detachCopy(p);
                tx.commit();

                Object id = pm.getObjectId(p);
                Object id1 = pm.getObjectId(null);
                Object id2 = pm.getObjectId(new Integer(1));
                Object id3 = pm.getObjectId(p1);
                Object id4 = pm.getObjectId(new Primitive());
                assertNotNull(id);
                assertNull(id1);
                assertNull(id2);
                assertNotNull(id3);
                assertNull(id4);

                tx.begin();
                p.setBoolean(false);
                p.setBoolean(true);
                Object id5 = pm.getObjectId(p);
                assertNotNull(id5);
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
            clean(Primitive.class);
        }
    }

    /**
     * Test for getObjectById()
     * @throws Exception
     */
    public void testGetObjectById() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Test insert of native and native wrapper data types
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.commit();

                Object id = pm.getObjectId(p);

                tx.begin();

                // Check the identity before and after retrieval
                Primitive p1 = (Primitive) pm.getObjectById(id);
                Object id1 = pm.getObjectId(p1);
                assertEquals(id,id1);

                boolean success = false;

                // Check the throwing of an error with an invalid id
                try
                {
                    pm.getObjectById(new Integer(1));
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                assertTrue("Expected JDOObjectNotFoundException",success);

                // Check the throwing of an error with a null id
                try
                {
                    success = false;
                    pm.getObjectById(null);
                }
                catch (JDOUserException ex)
                {
                    success = true;
                }
                assertTrue("Expected JDOUserException",success);

                // Check the throwing of an error with invalid id
                try
                {
                    pm.getObjectById(new LongIdentity(Primitive.class,"111"));
                    fail("Expected JDOUserException");
                }
                catch (JDOUserException ex)
                {
                    //
                }

                try
                {
                    success = false;
                    pm.getObjectById(p1);
                }
                catch (JDOObjectNotFoundException ex)
                {
                    success = true;
                }
                assertTrue("Expected JDOObjectNotFoundException",success);
                tx.rollback();

                p = new Primitive();
                tx.begin();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                id = pm.getObjectId(p);
                assertTrue(p == pm.getObjectById(id));
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
            clean(Primitive.class);
        }
    }

    /**
     * Test for getEvict().
     * @throws Exception
     */
    public void testEvict() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);
            try
            {
                // Test insert of native and native wrapper data types
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                Object id = pm.getObjectId(p);
                pm.evict(p);
                tx.commit();

                assertTrue(JDOHelper.isPersistent(p));
                assertFalse(JDOHelper.isNew(p));
                assertFalse(JDOHelper.isTransactional(p));
                assertFalse(JDOHelper.isDirty(p));

                Object o = pm.getObjectById(id,false);
                assertTrue(p==o);
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
            clean(Primitive.class);
        }
    }

    /**
     * Test for getObjectById() when the object is retrieved from the L2 cache.
     * @throws Exception
     */
    public void testGetObjectByIdNonTransactional()
    throws Exception
    {
        try
        {
            Object id = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Insert a record for use later
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456,
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.commit();

                id = pm.getObjectId(p);

                tx.begin();
                Primitive p1 = (Primitive) pm.getObjectById(id);
                Object id1 = pm.getObjectId(p1);
                assertEquals(id, id1);
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

            // Retrieve the object in a nontransactionalRead PMF from L2 cache
            PersistenceManager pm2 = null;
            try
            {
                pm = pmf.getPersistenceManager();
                try
                {
                    // first getObjectById() will result in hollow instance that
                    // is put in L2 cache
                    Object pc = pm.getObjectById(id);

                    // pin pc in the L2 cache to make sure it is not garbage collected
                    pmf.getDataStoreCache().pin(pc);

                    // take a new PM to have an empty L1 cache
                    pm2 =  pmf.getPersistenceManager();

                    // take PC from L2 cache
                    pm2.getObjectById(id);
                }
                catch (TransactionNotActiveException e)
                {
                    LOG.error("Exception thrown in test", e);
                    fail("TransactionNotActiveException thrown even though nontransactionRead is true");
                }
            }
            finally 
            {
                if(!pm.isClosed())
                {
                    pm.close();
                }
                if (pm2 != null)
                {
                    pm2.close();
                }
            }
        }
        finally
        {
            // Clean out our data
            clean(Primitive.class);
        }
    }

    /**
     * Test of closure of the PM.
     */
    public void testClose()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.currentTransaction().begin();

        boolean success = false;
        try
        {
            try
            {
                pm.close();
            }
            catch (JDOException ex)
            {
                success = true;
            }
        }
        finally
        {
            pm.currentTransaction().rollback();
            pm.close();
        }
        assertTrue("Should have raised an exception", success);

        // test close twice
        pm = pmf.getPersistenceManager();

        assertFalse("Before close(), isClosed() should return false", pm.isClosed());
        pm.close();
        assertTrue("After close(), isClosed() should return true", pm.isClosed());

        try
        {
            pm.close();
        }
        catch (JDOUserException e)
        {
            // JDO2.3 spec 12.6 allows redundant calls to close().
            fail("Should not have raised an exception when closing a closed PM");
        }
    }

    /**
     * Test that updates of collection and inverse collection fields update
     * the underlying collection
     */
    public void testMakeCollectionFieldsPersistent() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        CollectionFieldTester collectionTester = new CollectionFieldTester();

        Transaction tx = pm.currentTransaction();

        try
        {
            // data for testing collections
            Person[] personArray = new Person[3];
            InversePrimitive[] inversePrimitiveArray = new InversePrimitive[3];

            personArray[0] = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
            personArray[1] = new Person(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1]);
            personArray[2] = new Person(2, FIRSTNAME[2], LASTNAME[2], EMAIL[2]);

            inversePrimitiveArray[0] = new InversePrimitive(collectionTester);
            inversePrimitiveArray[1] = new InversePrimitive(collectionTester);
            inversePrimitiveArray[2] = new InversePrimitive(collectionTester);

            tx.begin();
            pm.makePersistent(collectionTester);
            tx.commit();

            tx.begin();

            // set up Set field data
            {
                HashSet<Person> s = new HashSet<Person>(3);
                s.add(personArray[0]);
                s.add(personArray[1]);
                s.add(personArray[2]);
                collectionTester.setPersonSet(s);
            }

            // set up inverse element data
            {
                HashSet<InversePrimitive> s = new HashSet<InversePrimitive>(3);
                s.add(inversePrimitiveArray[0]);
                s.add(inversePrimitiveArray[1]);
                s.add(inversePrimitiveArray[2]);

                collectionTester.setInversePrimitiveCollection(s);
            }

            tx.commit();

            collectionTester = null;

            // test results
            tx.begin();

            {
                Extent clnTest = pm.getExtent(CollectionFieldTester.class, false);
                java.util.Iterator it = clnTest.iterator(); // should only have one Primitive object
                collectionTester = (CollectionFieldTester) it.next();
            }

            assertNotNull(collectionTester);

            // test that Set data was stored
            {
                Set s = collectionTester.getPersonSet();
                assertTrue(s.contains(personArray[0]));
                assertTrue(s.contains(personArray[1]));
                assertTrue(s.contains(personArray[2]));
                assertEquals(3, s.size());

                Collection c = collectionTester.getInversePrimitiveCollection();

                assertTrue(c.contains(inversePrimitiveArray[0]));
                assertTrue(c.contains(inversePrimitiveArray[1]));
                assertTrue(c.contains(inversePrimitiveArray[2]));
                assertEquals(3, c.size());
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            pm.close();
        }
    }

    /**
     * Simple test of transactional PC field updates.
     */
    public void testUpdatePersistentFields() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                BigDecimal bd = new BigDecimal("12345.12345");
                BigInteger bi = new BigInteger("12345");
                java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
                java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
                java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
                java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

                tx.begin();
                Primitive p = new Primitive();
                setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, 
                    "fixed", "normal", "huge", bd, bi, date1, date2, time3, timestamp);
                pm.makePersistent(p);
                tx.commit();

                p = null;

                // Retrieve the object and update some fields
                tx.begin();
                Extent clnPrimitive = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
                p = (Primitive) clnPrimitive.iterator().next();
                date1 = new java.util.Date(date1.getTime() + 10000);
                date2 = java.sql.Date.valueOf("2001-01-02");
                time3 = java.sql.Time.valueOf("10:01:59");
                timestamp = java.sql.Timestamp.valueOf("2001-01-02 23:23:23.050500000");
                setPrimitiveValues(p, false, (byte) 22, 'a', 34, (short) 44, 1234567890, 456.456F, 456.456, 
                    "fixedlength", "normalsize", "hugexyzabc", bd, bi, date1, date2, time3, timestamp);
                tx.commit();

                // test the results
                tx.begin();
                clnPrimitive = pm.getExtent(org.datanucleus.samples.widget.Primitive.class, false);
                p = (Primitive) clnPrimitive.iterator().next();
                assertPrimitiveValues(p, false, (byte) 22, 'a', 34, (short) 44, 1234567890, 456.456F, 456.456, 
                    "fixedlength", "normalsize", "hugexyzabc", bd, bi, date1, date2, time3, timestamp);
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
            clean(Primitive.class);
        }
    }

    /**
     * Tests attempts to update PC fields on a P-new-del or P-del PC object
     */
    public void testUpdatePersistentFieldsExceptions() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                Person p = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);

                // test attempt to update P-new-del PC
                tx.begin();
                pm.makePersistent(p);
                pm.deletePersistent(p);
                try
                {
                    p.setLastName(LASTNAME[1]);
                    fail("should have thrown JDOUserException");
                }
                catch (JDOUserException e)
                {
                }
                tx.rollback();

                // test attempt to update P-del
                tx.begin();
                pm.makePersistent(p);
                tx.commit();

                tx.begin();
                pm.deletePersistent(p);
                try
                {
                    p.setLastName(LASTNAME[1]);
                    fail("should have thrown JDOUserException");
                }
                catch (JDOUserException e)
                {
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
            // Clean out our data
            clean(Person.class);
        }
    }

    /**
     * Simple test of makeTransient()
     */
    public void testMakeTransient() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = null;
            try
            {
                tx = pm.currentTransaction();
                Person x = createNewPerson(pm, 0);
                Person y = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);

                // test making transient object transient (no effect)
                tx.begin();
                pm.makeTransient(y);
                tx.commit();

                // test making persistent object transient
                tx.begin();
                pm.makeTransient(x);
                x.setLastName(LASTNAME[1]);
                tx.commit();

                assertNull(JDOHelper.getObjectId(x));
                Person queryResult = queryPerson(0, pm);

                tx.begin();
                assertEquals(LASTNAME[0], queryResult.getLastName());
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
            clean(Person.class);
        }
    }

    /**
     * Simple test of makeTransientAll()
     */
    public void testMakeTransientAll() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = null;
            try
            {
                tx = pm.currentTransaction();
                Person x = createNewPerson(pm, 0);
                Person y = createNewPerson(pm, 1);
                Person[] personArray = new Person[2];

                personArray[0] = x;
                personArray[1] = y;

                // test making persistent object transient
                tx.begin();
                pm.makeTransientAll((Object[])personArray);

                // Make some changes to the transient objects (should not get to the datastore)
                x.setLastName(LASTNAME[1]);
                y.setLastName(LASTNAME[0]);
                tx.commit();

                // changes applied after objects were made transient should not have persisted
                x = queryPerson(0, pm);
                y = queryPerson(1, pm);
                tx.begin();
                assertEquals(LASTNAME[0], x.getLastName());
                assertEquals(LASTNAME[1], y.getLastName());
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
            clean(Person.class);
        }
    }

    /**
     * Tests attempts to call makeTransient must raise errors
     * 
     * current state : new state/behaviour 
     * ---------------- --------------------------
     * Transient : unchanged
     * P-new : error (IN TEST)
     * P-clean : Transient
     * P-dirty : error (IN TEST)
     * Hollow : Transient
     * T-clean : unchanged
     * T-dirty : unchanged
     * P-new-del : error (IN TEST)
     * P-del : error (IN TEST)
     * P-nontrans : Transient
     */
    public void testMakeTransientExceptions() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                int dataset = 0;
                Person p = new Person(dataset, FIRSTNAME[dataset], LASTNAME[dataset], EMAIL[dataset]);
                boolean success;

                //-----------------------
                // test P-new : error     
                //-----------------------
                success = false;
                tx.begin();
                pm.makePersistent(p);
                try
                {
                    pm.makeTransient(p);
                    success = false;
                }
                catch (JDOException ex)
                {
                    success = true;
                }
                finally
                {
                    tx.rollback();
                }
                if (!success)
                {
                    fail("persistent object in P-new state should have not been made transient");
                }

                //-----------------------
                // P-new-del : error    
                //-----------------------
                success = false;
                tx = pm.currentTransaction();
                tx.begin();
                pm.makePersistent(p);
                pm.deletePersistent(p);
                try
                {
                    pm.makeTransient(p);
                    success = false;
                }
                catch (JDOException ex)
                {
                    success = true;
                }
                finally
                {
                    tx.rollback();
                }
                if (!success)
                {
                    fail("persistent object in P-new-del state should have not been made transient");
                }

                //-----------------------
                // prepare data for the next tests
                //-----------------------            
                // create object in database
                tx.begin();
                pm.makePersistent(p);
                tx.commit();
                Object id = pm.getObjectId(p);

                //-----------------------
                // P-dirty : error  
                //-----------------------
                success = false;
                tx = pm.currentTransaction();
                tx.begin();
                p = (Person) pm.getObjectById(id, true);
                p.setEmailAddress("new email");
                try
                {
                    pm.makeTransient(p);
                    success = false;
                }
                catch (JDOException ex)
                {
                    success = true;
                }
                finally
                {
                    tx.rollback();
                }
                if (!success)
                {
                    fail("persistent object in P-dirty state should have not been made transient");
                }
                //-----------------------
                // test P-del : error    
                //-----------------------
                success = false;
                tx = pm.currentTransaction();
                tx.begin();
                p = (Person) pm.getObjectById(id, true);
                pm.deletePersistent(p);
                try
                {
                    pm.makeTransient(p);
                    success = false;
                }
                catch (JDOException ex)
                {
                    success = true;
                }
                finally
                {
                    tx.commit();
                }
                if (!success)
                {
                    fail("persistent object in P-del state should have not been made transient");
                }
            }
            finally
            {
                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            clean(Person.class);
        }
    }

    /**
     * Test of makeTransient() for owner and makeTransientAll() for the  owner elements.
     */
    public void testMakeTransientOwnerAndElements() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // JoinTable 1-N relationship
                // Create a Manager with subordinate number 0
                createNewManager(pm, 0);

                // Make the Manager transient with all subordinates
                tx.begin();
                Manager m = (Manager) pm.getExtent(Manager.class, true).iterator().next();
                pm.retrieveAll(m.getSubordinates());
                pm.retrieveAll(m.getDepartments());
                pm.makeTransient(m);
                pm.makeTransientAll(m.getSubordinates());
                pm.makeTransientAll(m.getDepartments());
                tx.commit();

                // Check the result
                assertNull(JDOHelper.getObjectId(m));

                // Compare the managers
                Manager m1 = queryManager(0, pm);
                tx.begin();
                Assert.assertEquals(m.getBestFriend(), m1.getBestFriend());
                Assert.assertEquals(m.getLastName(), m1.getLastName());
                Assert.assertEquals(m.getFirstName(), m1.getFirstName());
                Assert.assertEquals(m.getEmailAddress(), m1.getEmailAddress());
                Assert.assertEquals(m.getPersonNum(), m1.getPersonNum());
                Assert.assertTrue("subordinates are not the same",
                    Manager.compareSet(m.getSubordinates(), m1.getSubordinates()));
                Assert.assertTrue("departments are not the same",
                    Manager.compareSet(m.getDepartments(), m1.getDepartments()));
                tx.commit();

                // FK 1-N relationship
                // Create owner with 2 elements.
                tx = pm.currentTransaction();
                tx.begin();
                InverseSetFieldTester owner = new InverseSetFieldTester();
                InverseSetValue value = new InverseSetValue();
                value.fillRandom();
                owner.addValue(value);
                InverseSetValue value1 = new InverseSetValue();
                value1.fillRandom();
                owner.addValue(value1);

                String[] valueStrings = new String[2];
                valueStrings[0] = value.getStrField();
                valueStrings[1] = value1.getStrField();

                pm.makePersistent(owner);
                tx.commit();
                Object id = pm.getObjectId(owner);

                // Make the owner and its elements transient
                tx = pm.currentTransaction();
                tx.begin();
                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                InverseSetFieldTester transientOwner = (InverseSetFieldTester) pm.getObjectById(id, true);
                pm.retrieveAll(transientOwner.getElements());
                pm.makeTransient(transientOwner);
                pm.makeTransientAll(transientOwner.getElements());
                tx.commit();

                // Check the result
                Set transientElements = transientOwner.getElements();
                assertFalse("Owner object is not transient!", JDOHelper.isPersistent(transientOwner));
                assertEquals("Number of elements in transient owner is incorrect", transientElements.size(), 2);

                Iterator transientElementsIter = transientElements.iterator();
                boolean value1Present = false;
                boolean value2Present = false;
                while (transientElementsIter.hasNext())
                {
                    InverseSetValue transientValue = (InverseSetValue)transientElementsIter.next();
                    assertFalse("Element object is not transient!", JDOHelper.isPersistent(transientValue));

                    // Check that the String field of the element is the same as that persisted
                    // We should really check for other things, on the Widget field of the value but that is
                    // not transient and so we cant do straight comparisons
                    if (transientValue.getStrField() == null)
                    {
                        if (valueStrings[0] == null && !value1Present)
                        {
                            value1Present = true;
                        }
                        else if (valueStrings[1] == null && !value2Present)
                        {
                            value2Present = true;
                        }
                    }
                    else
                    {
                        if (transientValue.getStrField().equals(valueStrings[0]))
                        {
                            value1Present = true;
                        }
                        else if (transientValue.getStrField().equals(valueStrings[1]))
                        {
                            value2Present = true;
                        }
                    }

                    // Check that the owner is the transient one
                    assertEquals("Owner of transient set element is incorrect", StringUtils.toJVMIDString(transientValue.getOwner()), StringUtils.toJVMIDString(transientOwner));
                }
                assertTrue("First value element is not present in the transient set", value1Present);
                assertTrue("Second value element is not present in the transient set", value2Present);
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
            clean(InverseSetFieldTester.class);
            clean(InverseSetValue.class);
            clean(Manager.class);
            clean(Widget.class);
        }
    }

    /**
     * Test of makeTransient(Object, boolean) to use the fetchplan for makeTransient operation.
     */
    public void testMakeTransientOwnerAndElementsUsingFetchPlan() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object mgrId = null;
            try
            {
                // Persist Manager -> Departments -> Projects
                tx.begin();
                Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
                Department dept1 = new Department("Sales");
                Department dept2 = new Department("Marketing");
                dept1.setManager(mgr);
                dept2.setManager(mgr);
                mgr.addDepartment(dept1);
                mgr.addDepartment(dept2);
                Project prj1 = new Project("Christmas Sales drive", 100000);
                Project prj2 = new Project("XFactor special offer", 30000);
                Project prj3 = new Project("Press Releases", 25000);
                dept1.addProject(prj1);
                dept1.addProject(prj2);
                dept2.addProject(prj3);
                pm.makePersistent(mgr);
                tx.commit();
                mgrId = JDOHelper.getObjectId(mgr);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown setting up data for makeTransient test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            Manager mgr = null;
            try
            {
                // Make the Manager transient with all subordinates
                pm.getFetchPlan().addGroup("all").setMaxFetchDepth(3); // Large enough depth for all of graph
                tx.begin();
                mgr = (Manager) pm.getObjectById(mgrId);
                ((JDOPersistenceManager)pm).makeTransient(mgr, true);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown making graph transient " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                // Check the result
                assertNotNull("Transient manager is null!", mgr);
                assertEquals("Transient manager is in wrong state", 
                    ObjectState.TRANSIENT, JDOHelper.getObjectState(mgr));
                assertEquals("Transient manager has wrong first name", mgr.getFirstName(), FIRSTNAME[0]);
                assertEquals("Transient manager has wrong last name", mgr.getLastName(), LASTNAME[0]);
                Set depts = mgr.getDepartments();
                assertNotNull("Transient manager has no departments!", depts);
                assertEquals("Transient manager has incorrect number of departments", 2, depts.size());
                Iterator deptIter = depts.iterator();
                Department dept1 = (Department)deptIter.next();
                Department dept2 = (Department)deptIter.next();
                Department sales = null;
                Department marketing = null;
                if (dept1.getName().equals("Sales"))
                {
                    sales = dept1;
                    if (dept2.getName().equals("Marketing"))
                    {
                        marketing = dept2;
                    }
                    else
                    {
                        fail("Marketing department not found");
                    }
                }
                else if (dept1.getName().equals("Marketing"))
                {
                    marketing = dept1;
                    if (dept2.getName().equals("Sales"))
                    {
                        sales = dept2;
                    }
                    else
                    {
                        fail("Sales department not found");
                    }
                }

                // Sales dept
                assertEquals("Transient Sales Department is in wrong state", 
                    ObjectState.TRANSIENT, JDOHelper.getObjectState(sales));
                Set projects = sales.getProjects();
                assertNotNull("Projects of sales department is null", projects);
                assertEquals("Number of projects of sales department is incorrect", 2, projects.size());
                Iterator iter = projects.iterator();
                boolean hasPrj1 = false;
                boolean hasPrj2 = false;
                boolean hasPrj3 = false;
                while (iter.hasNext())
                {
                    Project prj = (Project)iter.next();
                    assertEquals("State of project of sales dept is wrong",
                        ObjectState.TRANSIENT, JDOHelper.getObjectState(prj));
                    if (prj.getName().equals("Christmas Sales drive"))
                    {
                        hasPrj1 = true;
                        assertEquals("Budget of project 1 is incorrect", 100000, prj.getBudget());
                    }
                    else if (prj.getName().equals("XFactor special offer"))
                    {
                        hasPrj2 = true;
                        assertEquals("Budget of project 2 is incorrect", 30000, prj.getBudget());
                    }
                }
                if (!hasPrj1 || !hasPrj2)
                {
                    fail("One of two projects in Sales department was missing!");
                }

                // Marketing dept
                assertEquals("Transient Marketing Department is in wrong state", 
                    ObjectState.TRANSIENT, JDOHelper.getObjectState(marketing));
                projects = marketing.getProjects();
                assertNotNull("Projects of marketing department is null", projects);
                assertEquals("Number of projects of marketing department is incorrect", 1, projects.size());
                iter = projects.iterator();
                while (iter.hasNext())
                {
                    Project prj = (Project)iter.next();
                    assertEquals("State of project of marketing dept is wrong",
                        ObjectState.TRANSIENT, JDOHelper.getObjectState(prj));
                    if (prj.getName().equals("Press Releases"))
                    {
                        hasPrj3 = true;
                        assertEquals("Budget of project 1 is incorrect", 25000, prj.getBudget());
                    }
                }
                if (!hasPrj3)
                {
                    fail("Project in marketing department was missing!");
                }
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown checking transient graph " + e.getMessage());
            }
            finally
            {
                pm.close();
            }
        }
        finally
        {
            clean(Manager.class);
            clean(Department.class);
            clean(Project.class);
        }
    }

    /**
     * test of retrieve()
     */
    public void testRetrieve() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = null;
            Object id = null;

            // Create a Department with its Manager (1-1 relationship)
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                Department d = new Department("dept1");
                d.setManager(new Manager(new Random().nextLong(), "mgrFN", "mgrLN", "mgr@mgr.com", 
                    (float) 100.10, "mgrSERIAL"));
                pm.makePersistent(d);
                tx.commit();

                id = pm.getObjectId(d);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Try making it transient without specifying the FetchPlan
            // The Manager shouldn't become transient here since it isn't in the default "FetchPlan" for Department.
            pm = pmf.getPersistenceManager();
            Department d = null;
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                d = (Department) pm.getObjectById(id, true);
                pm.retrieve(d); // Will retrieve all fields in "d"
                pm.makeTransient(d);
                pm.makeTransient(d.getManager());
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
            assertTrue("The name attribute of Department wasn't retrieved", d.getName().trim().equals("dept1"));

            // Make Department and Manager transient, using FetchPlan and check after the close of the PM.
            pm = pmf.getPersistenceManager();
            Department d2 = null;
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                d2 = (Department) pm.getObjectById(id, true);
                pm.retrieve(d2); // Will retrieve all fields in "d2"
                pm.retrieve(d2.getManager()); // Retrieve all fields in "d2.getManager()"
                pm.makeTransient(d2);
                pm.makeTransient(d2.getManager());
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
            assertTrue("The name attribute of Department wasn't retrieved", d2.getName().trim().equals("dept1"));
            assertTrue("The Manager attribute of Department wasn't retrieved", d2.getManager() != null);
            assertTrue("The serial number attribute of Department wasnt retrieved", d2.getManager().getSerialNo() != null);
            assertTrue("The Manager attribute of Department wasn't retrieved correctly", d2.getManager().getSerialNo().trim().equals("mgrSERIAL"));

            // Make Department and Manager transient, using FetchPlan and check after end of transaction but before close of PM.
            pm = pmf.getPersistenceManager();
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                d2 = (Department) pm.getObjectById(id, true);
                pm.retrieve(d2);
                pm.retrieve(d2.getManager());
                pm.makeTransient(d2);
                pm.makeTransient(d2.getManager());
                tx.commit();

                assertTrue("The name attribute of Department wasn't retrieved", 
                    d2.getName().trim().equals("dept1"));
                assertTrue("The Manager attribute of Department wasn't retrieved", d2.getManager() != null);
                assertTrue("The serial number attribute of Department wasnt retrieved", 
                    d2.getManager().getSerialNo() != null);
                assertTrue("The Manager attribute of Department wasn't retrieved correctly", 
                    d2.getManager().getSerialNo().trim().equals("mgrSERIAL"));
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
            clean(Manager.class);
            clean(Department.class);
        }
    }

    /**
     * Test of refresh().
     */
    public void testRefresh() 
    throws Exception
    {
        try
        {
            PersistenceManager pm1 = pmf.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            Object id = null;
            Department d = null;

            // Create a Department object
            try
            {
                tx1.begin();
                d = new Department("Nobody's Department");
                pm1.makePersistent(d);
                tx1.commit();

                id = pm1.getObjectId(d);
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
            }

            // Create a new PM and retrieve/update the name of the Department
            PersistenceManager pm2 = pmf.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();
                Department d2 = (Department) pm2.getObjectById(id, true);
                d2.setName("Fred's Department");
                tx2.commit();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
            }

            // Do a refresh and check that we have the updated Department value
            Transaction tx3 = pm1.currentTransaction();
            try
            {
                tx3.begin();
                // This will refresh a HOLLOW object and should pull in the updated DB values
                pm1.refresh(d);
                assertTrue("Name of department had been updated in a separate PM, but refresh() hasn't retrieved the updated value", 
                    d.getName().equals("Fred's Department"));
                tx3.commit();
            }
            finally
            {
                if (tx3.isActive())
                {
                    tx3.rollback();
                }
                pm1.close();
                pm2.close();
            }
        }
        finally
        {
            clean(Department.class);
        }
    }

    /**
     * Simple test of deletePersistent method.
     */
    public void testDeletePersistent() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // test deletion of transient object
                tx.begin();
                try
                {
                    pm.deletePersistent(new Person());
                    fail("Expected exception in delete persistent of non pc instances");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }

                // test deletion of transactional object
                Person y = new Person();
                pm.makeTransactional(y);
                try
                {
                    pm.deletePersistent(y);
                    fail("Expected exception in delete persistent of transactional instances");
                }
                catch(JDOUserException ex)
                {
                    //expected
                }
                tx.commit();

                // test deletion of persistent-clean object
                Person x = createNewPerson(pm, 0);

                tx.begin();
                pm.deletePersistent(x);
                tx.commit();

                Person queryResult = queryPerson(0, pm);
                assertNull(queryResult); // Should be deleted

                // test deletion of persistent-new objects
                tx.begin();
                x = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);
                pm.makePersistent(x);
                pm.deletePersistent(x);
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
            clean(Person.class);
        }
    }

    /**
     * Simple test of deletePersistent method when used outside of transactions.
     */
    public void testDeletePersistentNonTransactional() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalWrite(true);
            tx.setNontransactionalRead(true);
            try
            {
                // test deletion of persistent-clean object
                Person x = createNewPerson(pm, 0);
                pm.deletePersistent(x);

                Person queryResult = queryPerson(0, pm);
                assertNull(queryResult);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown deleting object nontransactional ", e);
                fail("Exception thrown deleting object nontransactional " + e.getMessage());
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
            clean(Person.class);
        }
    }

    /**
     * Test attempts to delete a transient object.
     */
    public void testDeletePersistentExceptions() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                Person p = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);

                // test attempt to redelete transient object
                try
                {
                    tx.begin();
                    pm.deletePersistent(p);
                    fail("calling deletePersistent on transient object should fail");
                }
                catch (JDOUserException e)
                {
                }
                finally
                {
                    tx.rollback();
                }

                // test attempt to delete a P-new-del
                tx.begin();
                pm.makePersistent(p);
                pm.deletePersistent(p);
                try
                {
                    pm.deletePersistent(p);
                }
                catch (JDOUserException e)
                {
                    fail("calling deletePersistent on a P-del object should work");
                }
                tx.rollback();

                // test attempt to delete a P-del
                p = createNewPerson(pm, 0);
                tx.begin();
                pm.deletePersistent(p);
                try
                {
                    pm.deletePersistent(p);
                }
                catch (JDOUserException e)
                {
                    fail("calling deletePersistent on a P-del object should work");
                }

                tx.rollback();
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
            clean(Person.class);
        }
    }

    /**
     * Test that inherited fields are persisted.
     */
    public void testInheritedFieldsPersisted()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Manager mgr = null;

            try
            {
                tx.begin();
                mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
                pm.makePersistent(mgr);
                tx.commit();

                tx.begin();
                Extent ext = pm.getExtent(Manager.class, false);
                java.util.Iterator it = ext.iterator();
                assertTrue(it.hasNext());
                mgr = (Manager) it.next();
                assertEquals(FIRSTNAME[0], mgr.getFirstName());
                assertEquals(LASTNAME[0], mgr.getLastName());
                assertEquals(EMP_SALARY[0], mgr.getSalary(), 0.0F);
                pm.currentTransaction().commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                    pm.close();
                    fail();
                }

                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // get a fresh pm to ensure that data is coming from the store,
                // not the cache
                tx.begin();
                Extent ext = pm.getExtent(Manager.class, false);
                java.util.Iterator it = ext.iterator();
                assertTrue(it.hasNext());
                mgr = (Manager) it.next();
                assertEquals(FIRSTNAME[0], mgr.getFirstName());
                assertEquals(LASTNAME[0], mgr.getLastName());
                assertEquals(EMP_SALARY[0], mgr.getSalary(), 0.0F);
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
            clean(Manager.class);
        }
    }

    /**
     * Test that persisting a PC will also persist all PCs that are referenced.
     */
    public void testPersistenceOfOneToOneRelations()
    {
        try
        {
            Manager manager = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
            Employee employee = new Employee(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1], EMP_SALARY[1], EMP_SERIAL[1]);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object managerID = null;
            try
            {
                tx.begin();
                pm.makePersistent(employee);
                employee.setManager(manager);
                tx.commit();
                managerID = pm.getObjectId(manager);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                    pm.close();
                    fail();
                }
                pm.close();
            }

            // Check the contents
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent clnManager = pm.getExtent(Manager.class, false);
                Iterator it = clnManager.iterator(); // should only have one Primitive object
                assertTrue(it.hasNext());
                Manager m = (Manager) it.next();
                assertEquals(managerID, pm.getObjectId(m));
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
     * Test that transient fields are accessible both inside and outside transactions.
     */
    public void testPCFieldAccess() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                Primitive p = new Primitive();
                p.setTransient(100);

                tx.begin();
                p.setTransient(200);
                pm.makePersistent(p);
                p.setTransient(300);
                tx.commit();

                p.setTransient(400);
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
            clean(Primitive.class);
        }
    }

    /**
     * Test that getObjectId() returns the same value when called on a newly persistent PC as when 
     * called on a query result that should return that same persistent object.
     */
    public void testJavaIdentity()
    {
        try
        {
            Person p = new Person(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0]);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(p);
            Object oid = pm.getObjectId(p);
            tx.commit();

            try
            {
                tx.begin();
                Extent ext = pm.getExtent(Person.class, false);
                Iterator it = ext.iterator(); // should only have one Person object
                assertTrue(it.hasNext());
                p = (Person) it.next();
                assertTrue(!it.hasNext());
                assertEquals(oid, pm.getObjectId(p));
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
        }
        finally
        {
            clean(Person.class);
        }
    }

    /**
     * Test that FCOs added to a Collection field are persisted when the owning PC is persisted.
     */
    public void testNormalFCOCollectionFieldPersistence1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
        Employee emp1 = new Employee(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1], EMP_SALARY[1], EMP_SERIAL[1]);

        try
        {
            pm.currentTransaction().begin();
            mgr.addSubordinate(emp1);
            pm.makePersistent(mgr);
            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
            {
                pm.currentTransaction().rollback();
                pm.close();
                fail();
            }

            pm.close();
        }

        // get a fresh PM to ensure that any results aren't coming from the cache
        try
        {
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            Extent ext = pm.getExtent(Manager.class, false);
            java.util.Iterator it = ext.iterator();

            assertTrue(it.hasNext());
            mgr = (Manager) it.next();
            Collection c = mgr.getSubordinates();
            assertEquals(1, c.size());

            ext = pm.getExtent(Employee.class, false);
            it = ext.iterator();
            assertTrue(it.hasNext());
            Employee emp = (Employee) it.next();

            assertTrue(c.contains(emp));
        }
        finally
        {
            if (pm.currentTransaction().isActive())
                pm.currentTransaction().rollback();

            pm.close();
        }
    }

    /**
     * Test that deleting an object that is a member of a Collection field
     * throws an exception
     */
    public void testNormalFCOCollectionFieldPersistence2()
    {
        /*
         * If constraints aren't being used then the assumptions of this test don't hold.
         */
        if (!getConfigurationForPMF(pmf).getBooleanProperty("datanucleus.validateConstraints"))
        {
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
        Employee emp1 = new Employee(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1], EMP_SALARY[1], EMP_SERIAL[1]);

        try
        {
            mgr.addSubordinate(emp1);
            tx.begin();
            pm.makePersistent(mgr);
            tx.commit();

            tx.begin();

            try
            {
                pm.deletePersistent(emp1);
                tx.commit();
                fail();
            }
            catch (javax.jdo.JDODataStoreException e)
            {
                if (tx.isActive())
                    tx.rollback();
            }
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
                pm.close();
                fail();
            }

            pm.close();
        }
    }

    /**
     * Test that removing a member of a normal Collection field does NOT delete
     * the object that was removed
     */
    public void testNormalFCOCollectionFieldPersistence3()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
        Employee emp1 = new Employee(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1], EMP_SALARY[1], EMP_SERIAL[1]);

        try
        {
            mgr.addSubordinate(emp1);
            tx.begin();
            pm.makePersistent(mgr);
            tx.commit();

            tx.begin();
            mgr.getSubordinates().remove(emp1);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
                pm.close();
                fail();
            }

            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ex = pm.getExtent(Employee.class, false);
            java.util.Iterator it = ex.iterator();
            assertTrue(it.hasNext());
            Employee emp = (Employee) it.next();

            assertEquals(1, emp.getPersonNum());
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

    /**
     * Test adding and removing elements from Collections whose members are
     * defined as a non-PC superclass or interface
     */
    @SuppressWarnings("unchecked")
    public void testNormalFCOCollectionFieldPersistence4()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
        Primitive p = new Primitive();
        setPrimitiveValues(p);
        CollectionFieldTester tester = new CollectionFieldTester();

        try
        {
            tester.getObjectCollection().add(mgr);
            //tester.getInterfaceCollection().add(p);
            tx.begin();
            pm.makePersistent(tester);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
                pm.close();
                fail();
            }

            pm.close();
        }

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ex = pm.getExtent(CollectionFieldTester.class, true);
            java.util.Iterator it = ex.iterator();
            assertTrue(it.hasNext());
            tester = (CollectionFieldTester) it.next();

            assertEquals(1, tester.getObjectCollection().size());
            mgr = (Manager) tester.getObjectCollection().iterator().next();
            assertEquals(0, mgr.getPersonNum());

            // assertEquals(1, tester.getInterfaceCollection().size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Exception thrown in ObjectCollection test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
                pm.close();
                fail();
            }

            pm.close();
        }
    }

    /**
     * Test that when a FK collection is persisted the element is persisted also.
     * TODO Move to reachability tests.
     */
    public void testFKCollectionFieldPersistenceByReachability1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);

        try
        {
            Department d = new Department("Engineering");
            d.setManager(mgr);
            mgr.addDepartment(d);

            tx.begin();
            pm.makePersistent(mgr);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown when persisting FK collection using reachability", e);
            fail("Exception thrown when persisting FK collection using reachability " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // get a fresh PM to ensure that any results aren't coming from the cache
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ext = pm.getExtent(Manager.class, false);
            java.util.Iterator it = ext.iterator();

            assertTrue(it.hasNext());
            mgr = (Manager) it.next();
            Collection c = mgr.getDepartments();
            assertEquals(1, c.size());

            ext = pm.getExtent(Department.class, false);
            it = ext.iterator();
            assertTrue(it.hasNext());
            Department d = (Department) it.next();

            assertTrue(c.contains(d));
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Test that when an element with N-1 relation with FK collection is persisted, the owning PC is persisted also.
     * TODO Move to reachability tests.
     */
    public void testFKCollectionFieldPersistenceByReachability2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);

        try
        {
            Department d = new Department("Engineering");
            d.setManager(mgr);
            mgr.addDepartment(d);

            tx.begin();
            pm.makePersistent(d);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown when persisting FK collection using reachability", e);
            fail("Exception thrown when persisting FK collection using reachability " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // get a fresh PM to ensure that any results aren't coming from the cache
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ext = pm.getExtent(Manager.class, false);
            java.util.Iterator it = ext.iterator();

            assertTrue(it.hasNext());
            mgr = (Manager) it.next();
            Collection c = mgr.getDepartments();
            assertEquals(1, c.size());

            ext = pm.getExtent(Department.class, false);
            it = ext.iterator();
            assertTrue(it.hasNext());
            Department d = (Department) it.next();

            assertTrue(c.contains(d));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Test that deleting an object that is a member of a FK Collection also removes it from the Collection.
     */
    public void testElementDeletionRemovesFromFKCollection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);

        try
        {
            Department d = new Department("Engineering");
            d.setManager(mgr);

            tx.begin();
            mgr.addDepartment(d);

            pm.makePersistent(d);
            tx.commit();

            tx.begin();
            pm.deletePersistent(d);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown when deleting member of a FK collection", e);
            fail("Exception thrown when deleting a member of a FK collection " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // get a fresh PM to ensure that any results aren't coming from the cache
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ext = pm.getExtent(Manager.class, false);
            java.util.Iterator it = ext.iterator();

            assertTrue(it.hasNext());
            mgr = (Manager) it.next();
            Collection c = mgr.getDepartments();
            assertTrue("Departments should have been null or empty", c == null || c.size() == 0);

            ext = pm.getExtent(Department.class, false);
            it = ext.iterator();
            assertTrue(!(it.hasNext()));
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Test that setting the inverse reference of an object implicitly adds it
     * to the inverse collection of the "owning" object
     */
    public void testInverseFCOCollectionFieldPersistence4()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);

        try
        {
            Department d = new Department("Engineering");
            d.setManager(mgr);

            tx.begin();
            pm.makePersistent(d);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
                pm.close();
                fail();
            }

            pm.close();
        }

        // get a fresh PM to ensure that any results aren't coming from the cache
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ext = pm.getExtent(Manager.class, false);
            java.util.Iterator it = ext.iterator();

            assertTrue(it.hasNext());
            mgr = (Manager) it.next();
            Collection c = mgr.getDepartments();
            assertEquals(1, c.size());

            ext = pm.getExtent(Department.class, false);
            it = ext.iterator();
            assertTrue(it.hasNext());
            Department d = (Department) it.next();
            assertTrue(c.contains(d));
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Tests that the persistence manager used to persist an object is the same
     * as returned by jdoGetPersistenceManager()
     */
    public void testQueryPM()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Manager mgr = null;
            try
            {
                tx.begin();
                Department d = new Department("Engineering");
                mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
                mgr.addDepartment(d);
                pm.makePersistent(mgr);
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                    pm.close();
                    fail();
                }

                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ext = pm.getExtent(Manager.class, false);
                java.util.Iterator it = ext.iterator();
                mgr = (Manager) it.next();
                assertSame(pm, JDOHelper.getPersistenceManager(mgr));
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
            clean(Manager.class);
            clean(Department.class);
        }
    }

    /**
     * Test of basic lifecycle listener behaviour, listeneing to the changes in the lifecycle
     * of a simple object (with no relationships). The object is persisted, then updated, then detached
     * then updated (whilst detached), then attached, and finally deleted. This exercises all listener
     * event types.
     */
    public void testLifecycleListenerForSimpleObjects()
    {
        BasicListener listener = new BasicListener(true);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        int i = 0;
        try
        {
            pm.addInstanceLifecycleListener(listener, new Class[] {Person.class});
            Object person_id;

            tx.begin();

            // Persist an object and check the events
            Person person = new Person(12345, "Fred", "Smith", "Fred.Smith@jpox.org");
            pm.makePersistent(person);
            pm.flush();
            Integer[] events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue());

            // Save the object id
            person_id = pm.getObjectId(person);

            // Clean the cache and retrieve the object from datastore
            tx.begin();
            pm.evictAll();

            // Update a field on the object and check the events
            person = (Person) pm.getObjectById(person_id);
            person.setEmailAddress("Fred.Smith@aol.com");
            pm.flush();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue());

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue());

            assertTrue("Total number of lifecycle events received was incorrect : should have been " + i + " but was " + events.length,
                events.length == i);

            tx.begin();
            pm.evictAll();

            // Retrieve the object and detach it
            person = (Person)pm.getObjectById(person_id);
            Person detachedPerson = (Person)pm.detachCopy(person);
            pm.flush();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_DETACH, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_DETACH, events[i++].intValue());

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            if (tx.getOptimistic())
            {
                // Nothing here. TODO Why are there no preClear/postClear for optimistic yet there are for pessimistic?
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue());
            }

            // Update the detached object
            detachedPerson.setLastName("Green");

            tx.begin();

            // Attach the detached object
            pm.makePersistent(detachedPerson);
            pm.flush();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_ATTACH, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_ATTACH, events[i++].intValue());

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue());
            assertTrue("Total number of lifecycle events received was incorrect : should have been " + i + " but was " + events.length,
                events.length == i);

            pmf.getDataStoreCache().evictAll(); pm.evictAll(); // Make sure the get goes to the DB
            tx.begin();

            // Delete the object and check the events
            person = (Person)pm.getObjectById(person_id);
            pm.deletePersistent(person);
            pm.flush();
            events = listener.getRegisteredEventsAsArray();
            if (tx.getOptimistic())
            {
                // TODO Why 2 preDelete here?
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DELETE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DELETE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DELETE, events[i++].intValue());
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DELETE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DELETE, events[i++].intValue());
            }

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue());
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue());
            assertTrue("Total number of lifecycle events received was incorrect : should have been " + i + " but was " + events.length,
                events.length == i);
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Exception thrown while running lifecycle listener simple object test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            listener.getRegisteredEvents().clear();
        }
    }

    /**
     * Test of basic lifecycle listener behaviour, listeneing to the changes in the lifecycle
     * of an object with a collection of other objects.
     */
    public void testLifecycleListenerForCollections()
    {
        BasicListener listener = new BasicListener(true);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        int i = 0;
        try
        {
            pm.addInstanceLifecycleListener(listener, new Class[] {Manager.class, Department.class});
            Object managerId;
            Object dept2Id;

            tx.begin();

            // Persist related objects and check the events
            // Manager has a 1-N (FK) with Department
            Manager manager = new Manager(12346, "George", "Bush", "george.bush@thewhitehouse.com", 2000000, "ABC-DEF");
            Department dept1 = new Department("Invasions");
            Department dept2 = new Department("Propaganda");
            Department dept3 = new Department("Lies");
            manager.addDepartment(dept1);
            manager.addDepartment(dept2);
            manager.addDepartment(dept3);
            dept1.setManager(manager);
            dept2.setManager(manager);
            dept3.setManager(manager);

            pm.makePersistent(manager);
            pm.flush();
            Integer[] events = listener.getRegisteredEventsAsArray();
            if (tx.getOptimistic())
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
            }

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Manager
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Manager
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Department 1
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Department 1
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Department 2
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Department 2
            assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Department 3
            assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Department 3

            assertTrue("Total number of lifecycle events received was incorrect : should have been " + i + " but was " + events.length,
                events.length == i);

            // Evict anything in the L2 cache so we know we are going to the
            // datastore, and hence get predictable callback ordering
            pmf.getDataStoreCache().evictAll();

            // Save the object ids
            managerId = pm.getObjectId(manager);
            dept2Id = pm.getObjectId(dept2);

            pmf.getDataStoreCache().evictAll(); pm.evictAll(); // Make sure the get goes to the DB
            tx.begin();

            dept2 = (Department)pm.getObjectById(dept2Id);
            manager = (Manager)pm.getObjectById(managerId);

            // Remove manager of dept2 and check the events
            dept2.setManager(null);
            manager.removeDepartment(dept2);
            pm.flush();
            events = listener.getRegisteredEventsAsArray();
            if (tx.getOptimistic())
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue()); // Manager

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue()); // Manager

                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue()); // Manager

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_DIRTY, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_DIRTY, events[i++].intValue()); // Manager

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
            }

            // Commit the changes and check the events
            tx.commit();
            events = listener.getRegisteredEventsAsArray();
            if (tx.getOptimistic())
            {
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Department 2
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_LOAD, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_CLEAR, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CLEAR, events[i++].intValue()); // Department 2
            }

            assertTrue("Total number of lifecycle events received was incorrect : should have been " + i + " but was " + events.length,
                events.length == i);

            // TODO Add attach/detach of the Manager and its Departments.
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Exception thrown while running lifecycle listener collection test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            listener.getRegisteredEvents().clear();
        }
    }

    /**
     * Test of lifecycle listener registered for all classes
     */
    public void testLifecycleListenerRegisteredInPMFforAllClasses()
    {
        BasicListener listener = new BasicListener(true);

        PersistenceManagerFactory pmf = TestHelper.getConfigurablePMF(1, null);
        pmf.addInstanceLifecycleListener(listener, null);
        TestHelper.freezePMF(pmf);

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        int i = 0;
        try
        {
            tx.begin();

            // Persist an object and check the events
            Person person = new Person(12345, "Fred", "Smith", "Fred.Smith@jpox.org");
            pm.makePersistent(person);

            // Persist related objects and check the events
            // Manager has a 1-N (FK) with Department
            Manager manager = new Manager(12346, "George", "Bush", "george.bush@thewhitehouse.com", 2000000, "ABC-DEF");
            Department dept1 = new Department("Invasions");
            Department dept2 = new Department("Propaganda");
            Department dept3 = new Department("Lies");
            manager.addDepartment(dept1);
            manager.addDepartment(dept2);
            manager.addDepartment(dept3);
            dept1.setManager(manager);
            dept2.setManager(manager);
            dept3.setManager(manager);

            pm.makePersistent(manager);
            pm.flush();
            Integer[] events = listener.getRegisteredEventsAsArray();
            assertEquals("Wrong number of lifecycle events", 15, events.length);

            if (tx.getOptimistic())
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());

                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
            }
            else
            {
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());
                
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Manager
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 1
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 2
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Department 3
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue()); // Manager
            }

            tx.rollback();
            listener.getRegisteredEvents().clear();

            PersistenceManager pm2 = pmf.getPersistenceManager();
            Transaction tx2 = pm.currentTransaction();
            try
            {
    
                tx2.begin();
    
                // Persist an object and check the events
                pm.makePersistent(person);
                pm.flush();
                events = listener.getRegisteredEventsAsArray();
                i=0;
                assertEquals(LifecycleListenerSpecification.EVENT_POST_CREATE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_PRE_STORE, events[i++].intValue());
                assertEquals(LifecycleListenerSpecification.EVENT_POST_STORE, events[i++].intValue());
                
                tx2.rollback();
            }
            finally
            {
                if (tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Exception thrown while running lifecycle listener simple object test : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
            listener.getRegisteredEvents().clear();
            pmf.close();
        }
    }
    
    /**
     * Tests the InstanceCallback interface methods
     * @todo Add test for jdoPreClear()
     */
    public void testInstanceCallbacks()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        InstanceCallbackTester tester = new InstanceCallbackTester();
        tester.setPersistentValue("value");

        try
        {
            tx.begin();
            pm.makePersistent(tester);
            tx.commit();

            Query q = pm.newQuery(pm.getExtent(InstanceCallbackTester.class, false));
            tx.begin();

            Collection c = (Collection) q.execute();

            //////////////////////////////////////////
            // test jdPreStore
            // test jdoPostLoad using refresh for calling the post load
            //////////////////////////////////////////
            try
            {
                assertEquals(1, c.size());
                tester = (InstanceCallbackTester) c.iterator().next();

                // test jdPreStore()
                assertNull(tester.getPersistentValue());

                pm.refresh(tester);
                // test jdoPostLoad()
                assertNotNull(tester.getTransientValue());
                assertEquals(InstanceCallbackTester.POST_LOAD_VALUE, tester.getTransientValue());
            }
            finally
            {
                q.closeAll();
            }

            //////////////////////////////////////////
            // test jdoPostLoad using refresh for calling the post load in a persistent-dirty instance
            //////////////////////////////////////////

            q = pm.newQuery(pm.getExtent(InstanceCallbackTester.class, false));

            c = (Collection) q.execute();
            try
            {
                assertEquals(1, c.size());
                tester = (InstanceCallbackTester) c.iterator().next();
                tester.setPersistentValue("dirty pers");
                tester.setTransientValue("dirty trans");

                pm.refresh(tester);

                assertNull(tester.getPersistentValue());

                // test jdoPostLoad()
                assertNotNull(tester.getTransientValue());
                assertEquals(InstanceCallbackTester.POST_LOAD_VALUE, tester.getTransientValue());
            }
            finally
            {
                q.closeAll();
            }
            //////////////////////////////////////////
            // test jdoPostLoad using ignoreCache = true on query
            //////////////////////////////////////////
            q = pm.newQuery(pm.getExtent(InstanceCallbackTester.class, false));
            q.setIgnoreCache(true);

            c = (Collection) q.execute();

            try
            {
                assertEquals(1, c.size());
                tester = (InstanceCallbackTester) c.iterator().next();

                // test jdoPostLoad()
                assertNotNull(tester.getTransientValue());
                assertEquals(InstanceCallbackTester.POST_LOAD_VALUE, tester.getTransientValue());

            }
            finally
            {
                q.closeAll();
            }

            //////////////////////////////////////////
            // test jdoPreDelete
            //////////////////////////////////////////
            q = pm.newQuery(pm.getExtent(InstanceCallbackTester.class, false));

            c = (Collection) q.execute();

            try
            {
                assertEquals(1, c.size());
                tester = (InstanceCallbackTester) c.iterator().next();

                // test jdoPreDelete()
                try
                {
                    tester.setTransientValue(null);
                    pm.deletePersistent(tester);
                    fail();
                }
                catch (JDOUserCallbackException e)
                {
                    // callback throws exception with PreDeleteException as the nested exception
                }
            }
            finally
            {
                q.closeAll();
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Tests the InstanceCallback interface methods for elements in SCO fields
     */
    public void testInstanceCallbacksInSCO()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        InstanceCallbackContainer owner = new InstanceCallbackContainer();

        InstanceCallbackTester tester;

        tester = new InstanceCallbackTester();
        tester.setPersistentValue("value Set 1");
        owner.addIcTesterToSet(tester);

        tester = new InstanceCallbackTester();
        tester.setPersistentValue("value Set 2");
        owner.addIcTesterToSet(tester);

        tester = new InstanceCallbackTester();
        tester.setPersistentValue("value Map 1");
        owner.addIcTesterToMap(tester);

        tester = new InstanceCallbackTester();
        tester.setPersistentValue("value Map 2");
        owner.addIcTesterToMap(tester);

        try
        {
            tx.begin();
            pm.makePersistent(owner);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();

            tx = pm.currentTransaction();
            //////////////////////////////////////////
            // test jdoPostLoad with evicted instances
            //////////////////////////////////////////
            tx.begin();

            Query q = pm.newQuery(pm.getExtent(InstanceCallbackContainer.class, false));

            Collection c = (Collection) q.execute();
            try
            {
                assertEquals(1, c.size());
                owner = (InstanceCallbackContainer) c.iterator().next();
                Iterator it = owner.getIcTesters().iterator();
                while (it.hasNext())
                {
                    tester = ((InstanceCallbackTester) it.next());
                    assertNull(tester.getPersistentValue());
                    assertNotNull(tester.getTransientValue());
                    assertEquals(InstanceCallbackTester.POST_LOAD_VALUE, tester.getTransientValue());
                }
                Iterator itMap = owner.getIcTestersByPersistentValue().values().iterator();
                while (itMap.hasNext())
                {
                    tester = ((InstanceCallbackTester) itMap.next());
                    assertNull(tester.getPersistentValue());
                    assertNotNull(tester.getTransientValue());
                    assertEquals(InstanceCallbackTester.POST_LOAD_VALUE, tester.getTransientValue());
                }
            }
            finally
            {
                q.closeAll();
            }

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
    }

    /**
     * Tests the AttachDetachCallback interface methods
     */
    public void testAttachDetachCallbacks()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            AttachDetachCallbackTester detachedTester = null;
            AttachDetachCallbackTester tester = new AttachDetachCallbackTester();
            tester.setValue("value");

            try
            {
                // Persist an object to test the callbacks
                tx.begin();
                pm.newQuery(AttachDetachCallbackTester.class).deletePersistentAll();
                pm.makePersistent(tester);
                tx.commit();

                // Query the object
                tx.begin();
                Query q = pm.newQuery(pm.getExtent(AttachDetachCallbackTester.class, false));
                Collection c = (Collection) q.execute();

                try
                {
                    assertEquals(1, c.size());
                    tester = (AttachDetachCallbackTester) c.iterator().next();

                    assertTrue("preStatus should have been null since we haven't performed any attach/detach yet, but isnt", 
                        AttachDetachCallbackTester.preStatus == null);
                    assertTrue("postStatus should have been null since we haven't performed any attach/detach yet, but isnt", 
                        AttachDetachCallbackTester.postStatus == null);

                    // Detach the object
                    detachedTester = (AttachDetachCallbackTester)pm.detachCopy(tester);

                    assertTrue("preStatus should have been detach since we've just detached, but isn't",
                        AttachDetachCallbackTester.preStatus != null);
                    assertTrue("preStatus should have been detach since we've just detached, but isn't",
                        AttachDetachCallbackTester.postStatus != null);
                    assertTrue("preStatus should have been detach since we've just detached, but isn't",
                        AttachDetachCallbackTester.preStatus.equals("detach"));
                    assertTrue("postStatus should have been detach since we've just detached, but isn't",
                        AttachDetachCallbackTester.postStatus.equals("detach"));
                    assertTrue("postObject was incorrect", AttachDetachCallbackTester.postObject == tester);
                }
                finally
                {
                    q.closeAll();
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while persisting and detaching object : " + e.getMessage());
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

                tester = (AttachDetachCallbackTester)pm.makePersistent(detachedTester);
                assertTrue("preStatus should have been attach since we've just attached, but isn't",
                    AttachDetachCallbackTester.preStatus != null);
                assertTrue("preStatus should have been attach since we've just attached, but isn't",
                    AttachDetachCallbackTester.postStatus != null);
                assertTrue("preStatus should have been attach since we've just attached, but isn't",
                    AttachDetachCallbackTester.preStatus.equals("attach"));
                assertTrue("postStatus should have been attach since we've just attached, but isn't",
                    AttachDetachCallbackTester.postStatus.equals("attach"));
                assertTrue("postObject was incorrect", AttachDetachCallbackTester.postObject == detachedTester);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown in test", e);
                fail("Exception thrown while attaching object : " + e.getMessage());
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
            clean(AttachDetachCallbackTester.class);
        }
    }

    /**
     * This test was written as a result of a bug that was found. Test
     * persisting an object with a collection field, add objects to the
     * collection, make the owning object transient, and then persist it again.
     */
    @SuppressWarnings("unchecked")
    public void testJoinTableCollectionFieldPersistence1()
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        CollectionFieldTester tester = new CollectionFieldTester();

        for (int i = 0; i < 3; i++)
        {
            Primitive p = new Primitive();
            setPrimitiveValues(p);
            tester.getPrimitiveCollection().add(p);
        }

        try
        {
            pm.currentTransaction().begin();
            pm.makePersistent(tester);
            pm.currentTransaction().commit();

            pm.currentTransaction().begin();
            pm.makeTransient(tester);
            pm.makePersistent(tester);

            pm.currentTransaction().commit();
        }
        finally
        {
            if (pm.currentTransaction().isActive())
                pm.currentTransaction().rollback();

            pm.close();
        }
    }

    /**
     * Test for access to the JDO connection using its JDO interface acccessor.
     * TODO parts of this are RDBMS-specific currently
     */
    public void testJDOConnection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        JDOConnection jdoConn = null;
        if (vendorID == null)
        {
            // This is not an SQL-based datastore so omit
            return;
        }

        try
        {
            //---------------------------------------------------
            //test normal scenario
            //---------------------------------------------------
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            Connection sqlConn = (Connection) jdoConn;
            try
            {
                sqlConn.close();
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("not expected JDOUserException");
            }
            catch (SQLException e)
            {
                fail("not expected SQLException");
            }
            assertFalse("tx should not be active", tx.isActive());

            //---------------------------------------------------
            //test commit with datastore txn and no close
            //---------------------------------------------------
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            try
            {
                tx.commit();
                fail("expected JDOUserException");
            }
            catch (JDOUserException e)
            {
            }
            assertTrue("tx should be active", tx.isActive());

            try
            {
                tx.rollback();
                fail("expected JDOUserException");
            }
            catch(JDOUserException e)
            {
            }
            assertTrue("tx should be active", tx.isActive());

            sqlConn = (Connection) jdoConn;
            try
            {
                sqlConn.close();
            }
            catch (SQLException e)
            {
                LOG.error(">> Exception thrown in test", e);
            }
            tx.commit();

            //---------------------------------------------------
            //test commit with optimistic txn and no close
            //---------------------------------------------------
            tx.setOptimistic(true);
            tx.begin();
            jdoConn = pm.getDataStoreConnection();
            try
            {
                tx.commit();
                fail("expected JDOUserException");
            }
            catch (JDOUserException e)
            {
                //expected
            }
            assertTrue("tx should be active", pm.currentTransaction().isActive());
        }
        finally
        {
            if (tx.isActive())
            {
                try
                {
                    if (jdoConn != null)
                    {
                        jdoConn.close();
                    }
                }
                catch (Exception e)
                {
                }
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Tests that objects can be added to a Collection owned by a persistent object made transient.
     */
    public void testTransientObjectCollections()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        Employee emp = new Employee(1, FIRSTNAME[1], LASTNAME[1], EMAIL[1], EMP_SALARY[1], EMP_SERIAL[1]);
        Manager mgr = new Manager(0, FIRSTNAME[0], LASTNAME[0], EMAIL[0], EMP_SALARY[0], EMP_SERIAL[0]);
        try
        {
            tx.begin();
            pm.makePersistent(mgr);
            tx.commit();

            tx = pm.currentTransaction();
            tx.begin();
            pm.retrieve(mgr);
            pm.retrieveAll(mgr.getSubordinates());
            pm.retrieveAll(mgr.getDepartments());
            pm.makeTransient(mgr);
            mgr.addSubordinate(emp);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown in test", e);
            fail("Exception thrown while making object with collection transient : " + e.getMessage());
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
     * Test of PM.getServerDate(). Added in JDO 2.1
     */
    public void testGetServerDate()
    {
        JDOPersistenceManager pm = (JDOPersistenceManager)pmf.getPersistenceManager();
        //cast to abstract pm since jdo2 jars not yet available with this method
        try
        {
            pm.getServerDate();
        }
        catch (Exception e)
        {
            fail("Attempt to retrieve serverDate resulted in an Exception : " + e.getMessage());
        }
    }

    /**
     * Check for non active transactions and non-transactional read is false.
     */
    public void testNonTransactionReadNegative()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();

            Transaction tx = pm.currentTransaction();

            Dog rex = new Dog();
            rex.setName("rex");
            rex.setId("rex"+new Random().nextInt());
            rex.setColor("blue");
            Object id = null;
            try
            {
                tx.begin();
                pm.makePersistent(rex);
                id = pm.getObjectId(rex);
                tx.commit();
                tx.setNontransactionalRead(false);
                rex.getColor();
                fail("Expected TransactionNotReadableException");
            }
            catch (TransactionNotReadableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalRead(false);
                Dog d = (Dog)pm.getObjectById(id,false);
                d.getId();
            }
            catch(TransactionNotReadableException ex)
            {
                fail("Unexpected TransactionNotReadableException");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalRead(false);
                Dog d = (Dog)pm.getObjectById(id);
                d.getColor();
                fail("Expected TransactionNotReadableException");
            }
            catch(TransactionNotReadableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalRead(false);
                pm.newQuery(Dog.class).execute();
                fail("Expected TransactionNotReadableException");
            }
            catch(TransactionNotReadableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalRead(false);
                Iterator it = ((Collection)pm.newQuery(Dog.class).execute()).iterator();
                Dog d = (Dog)it.next();
                d.getColor();
                fail("Expected TransactionNotReadableException");
            }
            catch (TransactionNotReadableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            pmf.getDataStoreCache().evictAll();
            pm.evictAll();
            try
            {
                tx.setNontransactionalRead(false);
                pm.getObjectById(id, true); // Validation so needs to go to datastore
                fail("Expected TransactionNotReadableException");
            }
            catch(TransactionNotReadableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }        
            try
            {
                tx.setNontransactionalRead(false);
                Iterator it = ((Collection)pm.newQuery(Dog.class).execute()).iterator();
                Dog d = (Dog)it.next();
                d.getColor();
                fail("Expected TransactionNotReadableException");
            }
            catch(TransactionNotReadableException ex)
            {
                //expected
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
            clean(Dog.class);
        }
    }

    /**
     * Check for non active transactions and non-transactional write is false.
     */
    public void testNonTransactionWriteNegative()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Dog rex = new Dog();
            rex.setName("rex");
            rex.setId("rex"+new Random().nextInt());
            rex.setColor("blue");
            Object id = null;
            try
            {
                tx.begin();
                pm.makePersistent(rex);
                id = pm.getObjectId(rex);
                tx.commit();
                tx.setNontransactionalWrite(false);
                //check updating field
                rex.setColor("yellow");
                fail("Expected TransactionNotWritableException");
            }
            catch(TransactionNotWritableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            //check updating application id field
            try
            {
                tx.setNontransactionalWrite(false);
                Dog d = (Dog)pm.getObjectById(id,false);
                d.setId("newid");
                fail("Expected TransactionNotWritableException");
            }
            catch(TransactionNotWritableException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalWrite(false);
                pm.newQuery(Dog.class).deletePersistentAll();
                fail("Expected TransactionNotActiveException");
            }
            catch(TransactionNotActiveException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalWrite(false);
                pm.newQuery(Dog.class).deletePersistentAll((Object[])new String[] {"stupidarg"});
                fail("Expected TransactionNotActiveException");
            }
            catch(TransactionNotActiveException ex)
            {
                //expected
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            try
            {
                tx.setNontransactionalWrite(false);
                Iterator it = ((Collection)pm.newQuery(Dog.class).execute()).iterator();
                Dog d = (Dog)it.next();
                d.setId("newid");
                fail("Expected TransactionNotActiveException");
            }
            catch(TransactionNotActiveException ex)
            {
                //expected
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
            clean(Dog.class);
        }
    }

    /**
     * test of newInstance() with persistence-capable concrete classes.
     */
    public void testNewInstancePCClass() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Country france = (Country) pm.newInstance(Country.class);
        france.setName("France");
        assertEquals("France",france.getName());
    }

    public void testTransactionListener()
    throws Exception
    {
        final List<String> txEvents = new ArrayList<String>();
        PersistenceManager pm = pmf.getPersistenceManager();
        ((JDOPersistenceManager)pm).addTransactionEventListener(new TransactionEventListener()
        {
            public void transactionStarted()
            {
                txEvents.add("started");
            }
            public void transactionEnded()
            {
                txEvents.add("ended");
            }
            public void transactionPreFlush()
            {
                txEvents.add("preFlush");
            }
            public void transactionFlushed()
            {
                txEvents.add("flushed");
            }
            public void transactionPreCommit()
            {
                txEvents.add("preCommit");
            }
            public void transactionCommitted()
            {
                txEvents.add("committed");
            }
            public void transactionPreRollBack()
            {
                txEvents.add("preRollback");
            }
            public void transactionRolledBack()
            {
                txEvents.add("rolledback");
            }
        });

        try
        {
            Transaction tx = pm.currentTransaction();
            assertEquals(0, txEvents.size());
            try
            {
                tx.begin();
                assertEquals(1, txEvents.size());
                assertEquals("started", txEvents.get(0));

                Person p = new Person(1, "Charlie", "Chaplin", "charlie.chaplin@hollywood.com");
                pm.makePersistent(p);

                assertEquals(1, txEvents.size());
                pm.flush();
                assertEquals(3, txEvents.size());
                assertEquals("preFlush", txEvents.get(1));
                assertEquals("flushed", txEvents.get(2));

                tx.commit();
                assertEquals(10, txEvents.size());

                // TODO Update this when we fix core to not do the extra "flush" call before preCommit
                // See TransactionImpl.commit/rollback
                assertEquals("flushed", txEvents.get(3)); // From TransactionImpl.commit
                assertEquals("preCommit", txEvents.get(4)); // From TransactionImpl.commit

                assertEquals("preFlush", txEvents.get(5)); // From TransactionImpl.preCommit -> OM.preCommit
                assertEquals("flushed", txEvents.get(6)); // From TransactionImpl.preCommit -> OM.preCommit

                assertEquals("preFlush", txEvents.get(7)); // From TransactionImpl.preCommit -> OM.preCommit -> reachability
                assertEquals("flushed", txEvents.get(8)); // From TransactionImpl.preCommit -> OM.preCommit -> reachability

                assertEquals("committed", txEvents.get(9));
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
            clean(Person.class);
        }
    }

    // ----------------------------------- Convenience Methods --------------------------------------------------

    private Person createNewPerson(PersistenceManager pm, int dataset) throws Exception
    {
        boolean successful = false;

        Person p = new Person(dataset, FIRSTNAME[dataset], LASTNAME[dataset], EMAIL[dataset]);
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            pm.makePersistent(p);

            successful = true;
        }
        finally
        {
            if (successful)
                tx.commit();
            else
                tx.rollback();
        }

        return p;
    }

    private Manager createNewManager(PersistenceManager pm, int dataset) throws Exception
    {
        boolean successful = false;

        Manager p = new Manager(dataset, FIRSTNAME[dataset], LASTNAME[dataset], EMAIL[dataset], EMP_SALARY[dataset], EMP_SERIAL[dataset]);
        // add many employees as available, excluding the manager previously created
        for (int i = 0; i < FIRSTNAME.length; i++)
        {
            if (i != dataset)
            {
                p.addSubordinate(new Employee(i, FIRSTNAME[i], LASTNAME[i], EMAIL[i], EMP_SALARY[i], EMP_SERIAL[i]));
            }
        }
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();
            pm.makePersistent(p);

            successful = true;
        }
        finally
        {
            if (successful)
                tx.commit();
            else
                tx.rollback();
        }

        return p;
    }

    /**
     * Convenience method to get a Person with a particular number.
     * @param personNum Number of the person
     * @param pm The PersistenceManager
     * @return The Person (if found)
     */
    private Person queryPerson(long personNum, PersistenceManager pm)
    {
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(pm.getExtent(Person.class, true));
            try
            {
                Collection people = (Collection) q.execute();
                Iterator i = people.iterator();
                while (i.hasNext())
                {
                    Person p = (Person) i.next();
                    if (p.getPersonNum() == personNum)
                    {
                        return p;
                    }
                }
            }
            finally
            {
                q.closeAll();
            }
        }
        finally
        {
            tx.commit();
        }

        return null;
    }

    private Manager queryManager(long managerNum, PersistenceManager pm)
    {
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.setRetainValues(true);
            tx.begin();

            Extent clnManager = pm.getExtent(Manager.class, true);
            Query q = pm.newQuery(clnManager);

            try
            {
                Collection managers = (Collection) q.execute();

                Iterator i = managers.iterator();

                while (i.hasNext())
                {
                    Manager p = (Manager) i.next();

                    if (p.getPersonNum() == managerNum)
                    {
                        return p;
                    }
                }
            }
            finally
            {
                q.closeAll();
            }
        }
        finally
        {
            tx.commit();
        }

        return null;
    }

    static void setPrimitiveValues(Primitive p, boolean b, byte y, char c, int i, short s, long l, float f, double d, String fstr, String nstr, String hstr,
            BigDecimal bd, BigInteger bi, java.util.Date dt1, java.sql.Date dt2, java.sql.Time dt3, java.sql.Timestamp tm)
    {
        p.setBoolean(b);
        p.setBooleanObject(new Boolean(b));
        p.setByte(y);
        p.setByteObject(new Byte(y));
        p.setChar(c);
        p.setCharObject(new Character(c));
        p.setInt(i);
        p.setIntObject(new Integer(i));
        p.setShort(s);
        p.setShortObject(new Short(s));
        p.setLong(l);
        p.setLongObject(new Long(l));
        p.setFloat(f);
        p.setFloatObject(new Float(f));
        p.setDouble(d);
        p.setDoubleObject(new Double(d));

        p.setFixedLengthString(fstr);
        p.setNormalString(nstr);

        p.setBigDecimal(bd);
        p.setBigInteger(bi);

        p.setUtilDate(dt1);
        p.setSqlDate(dt2);
        p.setSqlTime(dt3);
        p.setSqlTimestamp(tm);
    }

    /**
     * assign default values
     */
    private void setPrimitiveValues(Primitive p)
    {
        BigDecimal bd = new BigDecimal("12345.12345");
        BigInteger bi = new BigInteger("12345");
        java.util.Date date1 = (new java.util.GregorianCalendar()).getTime();
        java.sql.Date date2 = java.sql.Date.valueOf("2001-01-01");
        java.sql.Time time3 = java.sql.Time.valueOf("10:01:59");
        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2001-01-01 23:23:23.050500000");

        setPrimitiveValues(p, true, (byte) 23, 'z', 33, (short) 43, 123456789L, 123.456F, 123.456, "fixed", "normal", "huge", bd, bi, date1, date2, time3,
            timestamp);
    }

    private void assertPrimitiveValues(Primitive p, boolean b, byte y, char c, int i, short s, long l, float f, double d, String fstr, String nstr,
            String hstr, BigDecimal bd, BigInteger bi, java.util.Date dt1, java.sql.Date dt2, java.sql.Time dt3, java.sql.Timestamp tm)
    {
        assertEquals(b, p.getBoolean());
        assertEquals(new Boolean(b), p.getBooleanObject());
        assertEquals(y, p.getByte());
        assertEquals(new Byte(y), p.getByteObject());
        assertEquals(c, p.getChar());
        assertEquals(new Character(c), p.getCharObject());
        assertEquals(i, p.getInt());
        assertEquals(new Integer(i), p.getIntObject());
        assertEquals(s, p.getShort());
        assertEquals(new Short(s), p.getShortObject());
        assertEquals(l, p.getLong());
        assertEquals(new Long(l), p.getLongObject());
        assertEquals(f, p.getFloat(), 0.0F);
        assertEquals(f, p.getFloatObject().floatValue(), 0.0F);
        assertEquals(d, p.getDouble(), 0.0);
        assertEquals(d, p.getDoubleObject().doubleValue(), 0.0);

        assertEquals(fstr, p.getFixedLengthString().trim());
        assertEquals(nstr, p.getNormalString());

        assertEquals(bd, p.getBigDecimal());
        assertEquals(bi, p.getBigInteger());

        // note: date storage is only accurate to the second
        // - the rest is truncated
        assertEquals(dt1.getTime() / 1000, p.getUtilDate().getTime() / 1000);
        assertEquals(dt2.getTime(), p.getSqlDate().getTime());
        assertEquals(dt3.getTime(), p.getSqlTime().getTime());
        assertEquals(tm.getTime() / 1000, p.getSqlTimestamp().getTime() / 1000);
    }

    /**
     * Test for use of rollback and RestoreValues
     */
    public void testRestoreValues() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                Person p = new Person(101, "Billy", "Nomates", "billy.nomates@nowhere.com");
                pm.makePersistent(p);
                tx.commit();
                id = pm.getObjectId(p);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Try restoreValues=true
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setRestoreValues(true);
            try
            {
                tx.begin();
                Person p = (Person)pm.getObjectById(id);
                assertEquals("Billy", p.getFirstName());
                assertEquals("Nomates", p.getLastName());

                p.setFirstName("Joey");
                tx.rollback();
                assertEquals("Billy", p.getFirstName());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Try restoreValues=false
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setRestoreValues(false);
            try
            {
                tx.begin();
                Person p = (Person)pm.getObjectById(id);
                assertEquals("Billy", p.getFirstName());
                assertEquals("Nomates", p.getLastName());

                p.setFirstName("Joey");
                tx.rollback();
                assertEquals("Billy", p.getFirstName());
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
            clean(Person.class);
        }
    }
}
