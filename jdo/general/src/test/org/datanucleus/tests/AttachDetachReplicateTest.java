/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved. 
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.detach.fetchdepth.A;
import org.datanucleus.samples.detach.fetchdepth.B;
import org.datanucleus.samples.detach.fetchdepth.C;
import org.datanucleus.samples.jfire.organisation.JFireOrganisation;
import org.datanucleus.samples.jfire.organisation.JFireOrganisationID;
import org.datanucleus.samples.models.hashsetcollection.Detail;
import org.datanucleus.samples.models.hashsetcollection.Master;
import org.datanucleus.samples.models.hashsetcollection.OtherDetail;
import org.datanucleus.samples.store.Product;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.TestHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Developer;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.one_many.unidir_2.ExpertGroupMember;
import org.jpox.samples.one_many.unidir_2.GroupMember;
import org.jpox.samples.one_many.unidir_2.ModeratedUserGroup;
import org.jpox.samples.one_many.unidir_2.UserGroup;

/**
 * Series of tests for Attach/Detach replication functionality.
 *
 * NOTE : THIS IS ONLY RUN WITH RDBMS.
 */
public class AttachDetachReplicateTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    private static Properties PMF_PROPS;
    static
    {
        PMF_PROPS = new Properties();
        PMF_PROPS.put(PropertyNames.PROPERTY_ATTACH_SAME_DATASTORE, "false");
    }

    /**
     * @param name
     */
    public AttachDetachReplicateTest(String name)
    {
        super(name, PMF_PROPS);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Manager.class,
                    Employee.class,
                    Department.class,
                    Developer.class,
                    Product.class,
                    A.class,
                    B.class,
                    C.class,
                }
            );
            initialised = true;
        }
    }
    
    /**
     * Another test of replication, with no relations. Just the object
     */
    public void testReplicateSimple2()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            PersistenceManager pm1 = pmf.getPersistenceManager();
            JFireOrganisation organisation = null;
            JFireOrganisationID organisationID = JFireOrganisationID.create("datanucleus.jfire.org");

            // Persist in first DB
            Transaction tx = null;
            try
            {
                tx = pm1.currentTransaction();
                tx.begin();
                JFireOrganisation org1 = new JFireOrganisation(organisationID.organisationID);
                org1 = pm1.makePersistent(org1);

                // Detach it for copying
                organisation = pm1.detachCopy(org1);

                tx.commit();
            }
            catch (JDOException ue)
            {
                LOG.error("Exception thrown persisting/detaching object", ue);
                fail("Exception thrown while creating object in first datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm1.close();
            }

            // Check the detached object
            if (!JDOHelper.isDetached(organisation))
            {
                fail("Organisation has not been detached!");
            }

            // Copy to other DB
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            tx = pm2.currentTransaction();
            try
            {
                tx.begin();
                pm2.makePersistent(organisation);
                tx.commit();
            }
            catch (JDOException ue)
            {
                LOG.error("Exception thrown replicating object", ue);
                fail("Exception thrown while copying object into second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the persistence in the second datastore
            try
            {
                tx = pm2.currentTransaction();
                tx.begin();

                // Use Extent since PM may have just put object in cache.
                Extent e = pm2.getExtent(JFireOrganisation.class);
                Iterator iter = e.iterator();
                boolean copied = false;
                while (iter.hasNext())
                {
                    JFireOrganisation o = (JFireOrganisation) iter.next();
                    if (pm2.getObjectId(o).equals(organisationID))
                    {
                        copied = true;
                        break;
                    }
                }
                assertTrue("Organisation was not copied to second datastore!", copied);

                tx.commit();
            }
            catch (JDOException ue)
            {
                LOG.error("Exception thrown checking results", ue);
                fail("Exception thrown while querying object in second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            PersistenceManagerFactory[] pmfs = new PersistenceManagerFactory[] {pmf, pmf2};
            for (int i = 0; i < pmfs.length; ++i)
            {
                clean(pmf, JFireOrganisation.class);
            }
        }
    }

    /**
     * Test of replication, with no relations.
     * Just detaches simple objectsm abd attaches them to a different datastore
     * and so should retain the ids.
     */
    public void testReplicateSimple()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            PersistenceManager pm1 = pmf.getPersistenceManager();
            Product prod = null;
            Object id = null;

            // Persist in first DB
            Transaction tx = null;
            try
            {
                tx = pm1.currentTransaction();
                tx.begin();
                Product prod1 = new Product("1", "Cup", "A tea cup", "http://www.jpox.org", "GBP", 12.50, 0.00, 0.00, 17.5, 1);
                prod1 = (Product)pm1.makePersistent(prod1);

                // Detach it for copying
                prod = (Product) pm1.detachCopy(prod1);

                tx.commit();
                id = pm1.getObjectId(prod1);
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while creating object in first datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm1.close();
            }

            // Check the detached object
            if (!JDOHelper.isDetached(prod))
            {
                fail("Product has not been detached!");
            }

            // Copy to other DB
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            try
            {
                tx = pm2.currentTransaction();
                tx.begin();

                pm2.makePersistent(prod);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while copying object into second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the persistence in the second datastore
            try
            {
                tx = pm2.currentTransaction();
                tx.begin();

                // Use Extent since PM may have just put object in cache.
                Extent e = pm2.getExtent(Product.class);
                Iterator iter = e.iterator();
                boolean copied = false;
                while (iter.hasNext())
                {
                    Product p = (Product) iter.next();
                    if (pm2.getObjectId(p).equals(id))
                    {
                        copied = true;
                        break;
                    }
                }
                assertTrue("Product was not copied to second datastore!", copied);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while querying object in second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            PersistenceManagerFactory[] pmfs = new PersistenceManagerFactory[] {pmf, pmf2};
            for (int i = 0; i < pmfs.length; ++i)
            {
                clean(pmf, Product.class);
            }
        }
    }

    /**
     * This test creates a circular object-chain with unidirectional 1-1-relation (A.B.C.A,
     * where second A is the same as first A). After persisting it into datastore1,
     * it detaches A with FetchPlan.ALL and persists it into datastore2 using makePersistent.
     */
    public void testReplicateRelation_1to1_circular()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            Transaction tx1 = null;
            Transaction tx2 = null;

            // Persist the object A with two 1-1-relations (A-B-C) into the first datastore.
            PersistenceManager pm1 = pmf.getPersistenceManager();
            try
            {
                tx1 = pm1.currentTransaction();
                tx1.begin();

                A a = new A("a");
                B b = new B("b");
                C c = new C("c");

                a.setB(b);
                b.setC(c);
                pm1.makePersistent(a);

                a.getB().getC().setA(a);

                tx1.commit();
            }
            finally
            {
                if (tx1 != null && tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Detach A with FetchPlan.ALL from datastore 1.
            A detachedA = null;
            pm1 = pmf.getPersistenceManager();
            try
            {
                tx1 = pm1.currentTransaction();
                tx1.begin();

                try
                {
                    pm1.getFetchPlan().setGroup(FetchPlan.ALL);
                    pm1.getFetchPlan().setMaxFetchDepth(2);
                    pm1.getFetchPlan().addGroup("includingB"); // So we have fetch-depth allowing the detach of all objects
                    A a = (A) pm1.getExtent(A.class).iterator().next();
                    detachedA = (A) pm1.detachCopy(a);
                }
                catch (Exception x)
                {
                    LOG.error("Loading instance of A from datastore 1 or detaching it with FetchPlan.ALL failed!", x);
                    fail("Loading instance of A from datastore 1 or detaching it with FetchPlan.ALL failed: " + x.getMessage());
                }

                tx1.commit();
            }
            finally
            {
                if (tx1 != null && tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // check, whether A.b and A.b.c exist and are correct in detachedA
            try
            {
                if (!"a".equals(detachedA.getName()))
                    fail("detachedA.name was corrupted somewhere after creation; either during makePersistent or detachCopy! Should be \"a\", but is \"" + detachedA.getName() + "\"!");

                if (!"b".equals(detachedA.getB().getName()))
                    fail("detachedA.b.name was corrupted somewhere after creation; either during makePersistent or detachCopy! Should be \"b\", but is \"" + detachedA.getB().getName() + "\"!");

                if (!"c".equals(detachedA.getB().getC().getName()))
                    fail("detachedA.b.c.name was corrupted somewhere after creation; either during makePersistent or detachCopy! Should be \"c\", but is \"" + detachedA.getB().getC().getName() + "\"!");
            }
            catch (Exception x)
            {
                LOG.error("Accessing object graph detached from datastore1 failed!", x);
                fail("Accessing object graph detached from datastore1 failed: " + x.getMessage());
            }

            // Store detachedA into datastore 2 using makePersistent (the object does NOT yet exist there and should be created)
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            try
            {
                tx2 = pm2.currentTransaction();
                tx2.begin();

                try
                {
                    pm2.makePersistent(detachedA);
                }
                catch (Exception x)
                {
                    LOG.error("makePersistent with object detached from datastore1 failed on datastore2!", x);
                    fail("makePersistent with object detached from datastore1 failed on datastore2: " + x.getMessage());
                }

                tx2.commit();
            }
            finally
            {
                if (tx2 != null && tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }

            // check, whether A.b and A.b.c exist and have been stored correctly into datastore2.
            pm2 = pmf2.getPersistenceManager();
            try
            {
                tx2 = pm2.currentTransaction();
                tx2.begin();

                try
                {
                    A a = (A) pm2.getExtent(A.class).iterator().next();
                    if (!"a".equals(a.getName()))
                        fail("a.name was corrupted during makePersistent on datastore2! Should be \"a\", but is \"" + a.getName() + "\"!");

                    if (!"b".equals(a.getB().getName()))
                        fail("a.b.name was corrupted during makePersistent on datastore2! Should be \"b\", but is \"" + a.getB().getName() + "\"!");

                    if (!"c".equals(a.getB().getC().getName()))
                        fail("a.b.c.name was corrupted during makePersistent on datastore2! Should be \"c\", but is \"" + a.getB().getC().getName() + "\"!");
                }
                catch (Exception x)
                {
                    LOG.error("Accessing datastore2 failed!", x);
                    fail("Accessing datastore2 failed: " + x.getMessage());
                }

                tx2.commit();
            }
            finally
            {
                if (tx2 != null && tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            // Clean out our data
            PersistenceManagerFactory[] pmfs = new PersistenceManagerFactory[] {pmf, pmf2};
            for (int i = 0; i < pmfs.length; ++i)
            {
                PersistenceManagerFactory pmf = pmfs[i];
                PersistenceManager pm = pmf.getPersistenceManager();
                Transaction tx = pm.currentTransaction();
    
                tx.begin();
                Extent ext = pm.getExtent(A.class, false);
                Iterator it = ext.iterator();
                while (it.hasNext())
                {
                    A a = (A) it.next();
                    a.setB(null);
                }
                tx.commit();
    
                tx.begin();
                ext = pm.getExtent(B.class, false);
                it = ext.iterator();
                while (it.hasNext())
                {
                    B b = (B) it.next();
                    b.setC(null);
                }
                tx.commit();
    
                tx.begin();
                ext = pm.getExtent(C.class, false);
                it = ext.iterator();
                while (it.hasNext())
                {
                    C c = (C) it.next();
                    c.setA(null);
                }
                tx.commit();

                pm.close();

                clean(pmf, A.class);
                clean(pmf, B.class);
                clean(pmf, C.class);
            }
        }
    }

    /**
     * This is a complex testcase using the classes from org.jpox.samples.models.company.
     * It stores a Manager into datastore 1. This Manager has a mapped-by-Set of
     * his employees and a join-Set of his departments. Hence, this test checks for
     * the behaviour of Sets when copied from one 
     */
    public void testMoveAcrossDatastores_company()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            Transaction tx1 = null;
            Transaction tx2 = null;

            // Create a Manager with two Departments and two Employees and persist it
            // into datastore 1.
            PersistenceManager pm1 = pmf.getPersistenceManager();
            Object managerId = null;
            try
            {
                tx1 = pm1.currentTransaction();
                tx1.begin();

                Manager ds1_manager = new Manager(1L, "Lucifer", "Satan", "Lucifer.Satan@microsoft.hell", 33666.99f, "jsdkhf8z23");
                Employee ds1_employee1 = new Employee(9593L, "McCreevy", "Charlie", "Charlie.McCreevy@microsoft.hell", 9948.57f, "8967bjjhg", new Integer(94));
                Employee ds1_employee2 = new Employee(8723L, "Gates", "Bill", "Bill.Gates@microsoft.hell", 11835.17f, "3894lknsd", new Integer(42));

                Department ds1_department1 = new Department("Brainwashing");
                ds1_department1.setManager(ds1_manager);
                ds1_manager.addDepartment(ds1_department1);

                // TODO Change this to be an inherited type to show up an error
                Department ds1_department2 = new Department("Torture");
                ds1_department2.setManager(ds1_manager);
                ds1_manager.addDepartment(ds1_department2);

                ds1_employee1.setManager(ds1_manager);
                ds1_manager.addSubordinate(ds1_employee1);
                
                ds1_employee2.setManager(ds1_manager);
                ds1_manager.addSubordinate(ds1_employee2);

                try
                {
                    pm1.makePersistent(ds1_manager);
                }
                catch (Exception x)
                {
                    LOG.error("Persisting Manager with Departments and Employees into datastore1 failed!", x);
                    fail("Persisting Manager with Departments and Employees into datastore1 failed: " + x.getMessage());
                }   

                tx1.commit();
                managerId = JDOHelper.getObjectId(ds1_manager);
            }
            finally
            {
                if (tx1 != null && tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Detach the Manager (with FetchPlan.ALL) from datastore 1.
            Manager detached_manager = null;

            pm1 = pmf.getPersistenceManager();
            try
            {
                tx1 = pm1.currentTransaction();
                tx1.begin();

                pm1.getFetchPlan().setGroup(FetchPlan.ALL);
                pm1.getFetchPlan().setMaxFetchDepth(-1);
                try
                {
                    Manager ds1_manager = (Manager)pm1.getObjectById(managerId);
                    detached_manager = (Manager)pm1.detachCopy(ds1_manager);
                }
                catch (Exception x)
                {
                    LOG.error("Loading and detaching Manager from datastore1 failed!", x);
                    fail("Loading and detaching Manager from datastore1 failed: " + x.getMessage());
                }

                tx1.commit();
            }
            finally
            {
                if (tx1 != null && tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // check, whether the detached data equals the original data
            testMoveAcrossDatastores_company_check("makePersistent or detachCopy (with datastore1) has corrupted data: ", detached_manager, true);

            // put the detached manager into datastore2 using makePersistent
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            try
            {
                tx2 = pm2.currentTransaction();
                tx2.begin();

                try
                {
                    pm2.makePersistent(detached_manager);
                }
                catch (Exception x)
                {
                    LOG.error("makePersistent failed on datastore2!", x);
                    fail("makePersistent failed on datastore2: " + x.getMessage());
                }

                tx2.commit();
            }
            finally
            {
                if (tx2 != null && tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }

            // load the manager from datastore2 and check whether data is still correct
            pm2 = pmf2.getPersistenceManager();
            try
            {
                tx2 = pm2.currentTransaction();
                tx2.begin();

                Manager ds2_manager = null;
                try
                {
                    Extent ex = pm2.getExtent(Manager.class);
                    Iterator exIter = ex.iterator();
                    ds2_manager = (Manager)exIter.next();
                    if (exIter.hasNext())
                    {
                        fail("Returned more than 1 Manager object in second datastore when should only have 1");
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LOG.error("Loading Manager from datastore2 failed!", e);
                    fail("Loading Manager from datastore2 failed: " + e.getMessage());
                }

                testMoveAcrossDatastores_company_check("makePersistent on datastore2 has corrupted data: ", ds2_manager, false);

                tx2.commit();
            }
            finally
            {
                if (tx2 != null && tx2.isActive())
                {
                    tx2.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            // Clean out our data
            PersistenceManagerFactory[] pmfs = new PersistenceManagerFactory[] {pmf, pmf2};
            for (int i = 0; i < pmfs.length; ++i)
            {
                PersistenceManagerFactory pmf = pmfs[i];
                PersistenceManager pm = pmf.getPersistenceManager();
                Transaction tx = pm.currentTransaction();

                tx.begin();
                for (Iterator it = pm.getExtent(Employee.class, false).iterator(); it.hasNext(); )
                {
                    Employee e = (Employee) it.next();
                    e.setManager(null);
                }
                tx.commit();

                tx.begin();
                for (Iterator it = pm.getExtent(Department.class, false).iterator(); it.hasNext(); )
                {
                    Department d = (Department) it.next();
                    d.setManager(null);
                }
                tx.commit();

                pm.close();

                clean(pmf, Manager.class);
                clean(pmf, Employee.class);
                clean(pmf, Department.class);
            }
        }
    }

    /**
     * Convenience method to check the detached state of a graph of "Company" objects.
     * @param messagePrefix Prefix to any failure messages
     * @param manager The detached manager that starts the graph
     * @param detached Whether we should test for detached objects
     */
    private void testMoveAcrossDatastores_company_check(String messagePrefix, Manager manager, boolean detached)
    {
        Employee detached_employee1 = null;
        Employee detached_employee2 = null;
        Department detached_department1 = null;
        Department detached_department2 = null;

        if (!JDOHelper.isDetached(manager) && detached)
        {
            fail(messagePrefix + "Manager is not detached!");
        }

        assertEquals("Manager.personNum is not correct", 1L, manager.getPersonNum());
        assertEquals("Manager.firstName is not correct", "Lucifer", manager.getFirstName());
        assertEquals("Manager.firstName is not correct", "Satan", manager.getLastName());
        assertEquals("Manager.emailAddress is not correct", "Lucifer.Satan@microsoft.hell", manager.getEmailAddress());
        assertEquals("Manager.serialNo is not correct", "jsdkhf8z23", manager.getSerialNo().trim());
        assertTrue("Manager.salary is not correct : expected " + 33666.99f + " but is " + manager.getSalary(),
            Math.abs(33666.99f - manager.getSalary()) <= 0.02f);

        assertEquals("Number of manager's departments has been changed", 2, manager.getDepartments().size());
        assertEquals("Number of manager's subordinates has been changed", 2, manager.getSubordinates().size());

        for (Iterator it = manager.getDepartments().iterator(); it.hasNext(); )
        {
            Department dep = (Department) it.next();
            if (!JDOHelper.isDetached(dep) && detached)
            {
                fail(messagePrefix + "Department is not detached!");
            }
            if ("Brainwashing".equals(dep.getName()))
            {
                detached_department1 = dep;
            }
            else if ("Torture".equals(dep.getName()))
            {
                detached_department2 = dep;
            }
            else
            {
                fail(messagePrefix + "The Name of at least one Department is wrong: " + dep.getName());
            }
        }

        if (detached_department1 == null || detached_department2 == null)
        {
            fail(messagePrefix + "Both detached departments have the same name!");
        }

        for (Iterator it = manager.getSubordinates().iterator(); it.hasNext(); )
        {
            Employee emp = (Employee) it.next();
            if (!JDOHelper.isDetached(emp) && detached)
            {
                fail(messagePrefix + "Employee is not detached!");
            }
            if (!JDOHelper.isDetached(emp.getManager()) && detached)
            {
                fail(messagePrefix + "Manager reference of Employee is not detached!");
            }
            switch ((int)emp.getPersonNum())
            {
                case 9593:
                    detached_employee1 = emp;
                    break;
                case 8723:
                    detached_employee2 = emp;
                    break;
                default:
                    fail(messagePrefix + "Employee number is wrong: " + emp.getPersonNum());
            }
        }

        if (detached_employee1 == null || detached_employee2 == null)
        {
            fail(messagePrefix + "Both employees have the same personNum!");
        }
    }

    private PersistenceManagerFactory _pmf2 = null;

    protected PersistenceManagerFactory getPersistenceManagerFactory2()
    {
        if (_pmf2 == null)
        {
            // Use the second datastore
            _pmf2 = TestHelper.getPMF(2, PMF_PROPS);
        }
        return _pmf2;
    }

    /**
     * Test of detaching from one datastore and persisting the objects to another.
     * As we copy a group of related objects JPOX should serialize SQLs in the
     * way that do not break foreign key constraints.
     */
    public void testMoveAcrossDatastoresWithRelation()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            PersistenceManager pm1 = pmf.getPersistenceManager();
            Detail detail = null;

            // Persist in first DB
            Transaction tx = null;
            try
            {
                tx = pm1.currentTransaction();
                tx.begin();

                // Create some dummy records, so we offset the identity values and make a valid test
                pm1.makePersistent(new Master());
                pm1.makePersistent(new Master());
                pm1.makePersistent(new Master());
                pm1.makePersistent(new Detail());
                pm1.makePersistent(new Detail());
                pm1.makePersistent(new Detail());
                pm1.makePersistent(new OtherDetail());
                pm1.makePersistent(new OtherDetail());
                pm1.makePersistent(new OtherDetail());

                // Create our test objects, Master with related Detail and OtherDetail
                Master master1 = new Master();
                master1.setId("master");

                Detail detail1 = new Detail();
                detail1.setId("detail");
                master1.addDetail(detail1);
                detail1.setMaster(master1);

                OtherDetail otherDetail1 = new OtherDetail();
                otherDetail1.setId("otherDetail1");
                master1.addOtherDetail(otherDetail1);
                otherDetail1.setMaster(master1);

                pm1.makePersistent(detail1);

                // Detach it for copying
                pm1.getFetchPlan().addGroup("all");
                pm1.getFetchPlan().setMaxFetchDepth(2);
                detail = (Detail) pm1.detachCopy(detail1);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while creating object in first datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm1.close();
            }

            // Check our detached objects
            assertNotNull(detail.getMaster());
            assertEquals(detail.getMaster().getOtherDetails().size(), 1);

            // Copy to other DB
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            try
            {
                tx = pm2.currentTransaction();

                tx.begin();

                // Persist graph of three transient objects to see if this datastore works
                Master master2 = new Master();
                master2.setId("master2");

                Detail detail2 = new Detail();
                detail2.setId("detail2");
                master2.addDetail(detail2);
                detail2.setMaster(master2);

                OtherDetail otherDetail2 = new OtherDetail();
                otherDetail2.setId("otherDetail2");
                master2.addOtherDetail(otherDetail2);
                otherDetail2.setMaster(master2);

                pm2.makePersistent(detail2);

                // Replicate object graph of three detached objects from datastore 1
                pm2.makePersistent(detail);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while copying object into second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the persistence in the second datastore
            try
            {
                tx = pm2.currentTransaction();

                tx.begin();

                // Use Extent since PM may have just put object in cache.
                Extent e = pm2.getExtent(Master.class);
                Iterator iter = e.iterator();
                int noOfMasters = 0;
                while (iter.hasNext())
                {
                    noOfMasters++;
                    iter.next();
                }
                assertTrue("Number of masters retrieved from second datastore is incorrect : was " + noOfMasters + " but should have been 2", noOfMasters == 2);

                e = pm2.getExtent(OtherDetail.class);
                iter = e.iterator();
                int noOfOtherDetails = 0;
                while (iter.hasNext())
                {
                    noOfOtherDetails++;
                    iter.next();
                }
                assertTrue("Number of otherdetails retrieved from second datastore is incorrect : was " + noOfOtherDetails + " but should have been 2",
                    noOfOtherDetails == 2);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while querying object in second datastore : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            // Clean up our data in the main DB
            clean(Detail.class);
            clean(OtherDetail.class);
            clean(Master.class);

            // Clean up data in the other DB
            PersistenceManager pm = pmf2.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // delete all Detail objects
                tx.begin();
                Extent ext = pm.getExtent(Detail.class, true);
                Iterator it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
                }
                tx.commit();

                // delete all OtherDetail objects
                tx.begin();
                ext = pm.getExtent(OtherDetail.class, true);
                it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
                }
                tx.commit();

                // delete all Master objects
                tx.begin();
                ext = pm.getExtent(Master.class, true);
                it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
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
    }

    /**
     * Test checks if we can detach a graph of objects where there are
     * persistent and/or detached objects inside this graph
     * 
     * Test adding a detached object to a PC_NEW object graph (after it has been made persistent)
     * TODO Change the sample to be Company
     */
    public void testSetDetachedObjectOnFieldInPCNewObject()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf2 = getPersistenceManagerFactory2();
        try
        {
            PersistenceManager pm = null;
            Transaction tx = null;

            Master detachedMaster = null;
            try
            {
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();

                tx.begin();

                Master master = new Master();
                master.setId("Master3");
                pm.makePersistent(master);
                pm.getFetchPlan().setMaxFetchDepth(2);
                detachedMaster = (Master) pm.detachCopy(master);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while performing test : " + ue.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Associate with the detached master in a different datastore
            try
            {
                pm = pmf2.getPersistenceManager();
                tx = pm.currentTransaction();

                tx.begin();
                tx.setRetainValues(true);

                Detail detail = new Detail();
                detail.setId("Detail3");
                pm.makePersistent(detail);

                OtherDetail otherDetail = new OtherDetail();
                otherDetail.setId("OtherDetail3");
                pm.makePersistent(otherDetail);
                assertTrue(JDOHelper.isDetached(detachedMaster));

                // set a detached object to a field in a PC_NEW instance
                detail.setMaster(detachedMaster);
                detachedMaster.addDetail(detail);
                otherDetail.setMaster(detachedMaster);
                detachedMaster.addOtherDetail(otherDetail);
                tx.commit();

                assertFalse("detail object is still detached, but should have been attached", JDOHelper.isDetached(detail));
                assertNotNull("detail object has master which is null!", detail.getMaster());
                assertFalse("detached has master that has not been attached", JDOHelper.isDetached(detail.getMaster()));
                assertTrue("detail object has master but number of other details is 0!", detail.getMaster().getOtherDetails().size() > 0);
                assertFalse("detail object has master that has otherdetail that is detached still!",
                    JDOHelper.isDetached(detail.getMaster().getOtherDetails().iterator().next()));

                assertFalse("otherdetail object is still detached, but should have been attached", JDOHelper.isDetached(otherDetail));
                assertNotNull("otherdetail object has master which is null!", otherDetail.getMaster());
                assertFalse("otherdetail has master that has not been attached!",JDOHelper.isDetached(otherDetail.getMaster()));

                // Detach the detail
                tx.begin();
                pm.getFetchPlan().addGroup("all");
                pm.getFetchPlan().setMaxFetchDepth(2);
                Detail detachedDetail = (Detail) pm.detachCopy(detail);
                tx.commit();

                assertTrue(JDOHelper.isDetached(detachedDetail));
                assertTrue(JDOHelper.isDetached(detachedDetail.getMaster()));
                assertTrue(JDOHelper.isDetached(detachedDetail.getMaster().getOtherDetails().iterator().next()));
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while performing test : " + ue.getMessage());
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
            // Clean up our data in the main DB
            clean(OtherDetail.class);
            clean(Detail.class);
            clean(Master.class);

            // Clean up data in the other DB
            PersistenceManager pm = pmf2.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // delete all Detail objects
                tx.begin();
                Extent ext = pm.getExtent(Detail.class, true);
                Iterator it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
                }
                tx.commit();

                // delete all OtherDetail objects
                tx.begin();
                ext = pm.getExtent(OtherDetail.class, true);
                it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
                }
                tx.commit();

                // delete all Master objects
                tx.begin();
                ext = pm.getExtent(Master.class, true);
                it = ext.iterator();
                while (it.hasNext())
                {
                    Object o = it.next();
                    pm.deletePersistent(o);
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
    }

    public void testReplicateApplicationIdentityWith1toN()
    {
        if (vendorID == null)
        {
            return;
        }

        PersistenceManagerFactory pmf1 = null;
        PersistenceManagerFactory pmf2 = null;

        try
        {
            // Connect to "datastore1"
            Properties props = new Properties();
            props.setProperty(PropertyNames.PROPERTY_ATTACH_SAME_DATASTORE, "false");
            props.setProperty(PropertyNames.PROPERTY_AUTOSTART_MECHANISM,"Classes");
            props.setProperty(PropertyNames.PROPERTY_AUTOSTART_CLASSNAMES,
                "org.jpox.samples.relations.sides.SideAParent,org.jpox.samples.relations.sides.SideAChild,org.jpox.samples.relations.sides.SideBParent,org.jpox.samples.relations.sides.SideBChild");
            pmf1 = TestHelper.getPMF(1, props);

            // Persist data to "datastore1"
            PersistenceManager pm1 = pmf1.getPersistenceManager();
            Transaction tx1 = pm1.currentTransaction();
            Object holderId = null;
            try
            {
                tx1.begin();

                ModeratedUserGroup holder = new ModeratedUserGroup(1, "HolderA", "First A");

                List<GroupMember> elements = new ArrayList<GroupMember>();
                elements.add(new ExpertGroupMember(25, "ElementB1", "First B"));
                elements.add(new ExpertGroupMember(26, "ElementB2", "Second B"));
                elements.add(new GroupMember(27, "ElementB3"));
                elements.add(new GroupMember(28, "ElementB4"));
                holder.setMembers(elements);

                pm1.makePersistent(holder);

                tx1.commit();
                holderId = JDOHelper.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown creating data in datastore 1", e);
                e.printStackTrace();
                return;
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Detach holder from "datastore1"
            UserGroup detachedHolder = null;
            pm1 = pmf1.getPersistenceManager();
            tx1 = pm1.currentTransaction();
            try
            {
                pm1.getFetchPlan().setGroups(new String[] {FetchPlan.DEFAULT, FetchPlan.ALL});
                pm1.getFetchPlan().setMaxFetchDepth(-1);

                tx1.begin();

                ModeratedUserGroup holder = (ModeratedUserGroup) pm1.getObjectById(holderId);
                detachedHolder = (ModeratedUserGroup) pm1.detachCopy(holder);

                tx1.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown detaching data from datastore 1", e);
                fail("Exception in detach with datastore 1 : " + e.getMessage());
            }
            finally
            {
                if (tx1.isActive())
                {
                    tx1.rollback();
                }
                pm1.close();
            }

            // Connect to "datastore2"
            pmf2 = TestHelper.getPMF(2, props);

            // Attach data to "datastore2"
            PersistenceManager pm2 = pmf2.getPersistenceManager();
            Transaction tx2 = pm2.currentTransaction();
            try
            {
                tx2.begin();

                pm2.makePersistent(detachedHolder);

                tx2.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown attaching data to datastore 2", e);
                fail("Exception in attach with datastore 2 : " + e.getMessage());
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
            fail("Exception on attach to datastore 2 : " + e.getMessage());
        }
        finally
        {
            // Clean out our data
            clean(ModeratedUserGroup.class);
            clean(UserGroup.class);
            clean(ExpertGroupMember.class);
            clean(GroupMember.class);
        }
    }
}