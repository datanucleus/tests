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
2005 Andy Jefferson - added Farm/Animal recursive test
2005 Andy Jefferson - added multiple datastore copy test
2005 Maciej Wegorkiewicz - added relation building between detached test
2005 Andy Jefferson - added tests for fetch-depth, object states
2005 Marco Schulze - added some tests for multiple-datastore-copies with relations
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.StoreLifecycleListener;

import junit.framework.Assert;

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.samples.detach.ClassElements;
import org.datanucleus.samples.detach.ClassOwner;
import org.datanucleus.samples.detach.ClassWithNonPCCollection;
import org.datanucleus.samples.detach.ClassWithTransactionalField;
import org.datanucleus.samples.detach.DetachDates;
import org.datanucleus.samples.detach.DetachList;
import org.datanucleus.samples.detach.DetachListElement;
import org.datanucleus.samples.detach.Owner;
import org.datanucleus.samples.detach.fetchdepth.Directory;
import org.datanucleus.samples.models.hashsetcollection.Circon;
import org.datanucleus.samples.models.hashsetcollection.Detail;
import org.datanucleus.samples.models.hashsetcollection.Master;
import org.datanucleus.samples.store.Customer;
import org.datanucleus.samples.store.Supplier;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.StringUtils;
import org.jpox.samples.models.company.Account;
import org.jpox.samples.models.company.CompanyHelper;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.fitness.Gym;
import org.jpox.samples.models.fitness.Wardrobe;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.Farm;
import org.jpox.samples.one_many.map.MapHolder;
import org.jpox.samples.one_many.map.MapValueItem;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;

/**
 * Series of tests for Attach/Detach functionality.
 */
public class AttachDetachTest extends JDOPersistenceTestCase
{
    /**
     * @param name
     */
    public AttachDetachTest(String name)
    {
        super(name);
    }

    /**
     * Test for the states of objects in the detach-attach cycle.
     * Performs checks on isDetached, and isDirty to test the lifecycle.
     */
    public void testDetachStates()
    {
        try
        {
            Employee woodyDetached = null;

            // Persist an object and detach it
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
                pm.makePersistent(woody);
                woodyDetached = (Employee)pm.detachCopy(woody);

                assertTrue("JDOHelper.isDetached returns false on an object detached in the current transaction!", 
                    JDOHelper.isDetached(woodyDetached));
                assertFalse("JDOHelper.isDirty returns true on an object detached in the current transaction!", 
                    JDOHelper.isDirty(woodyDetached));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            assertTrue("JDOHelper.isDetached returns false on an object recently detached!", 
                JDOHelper.isDetached(woodyDetached));
            assertFalse("JDOHelper.isDirty returns true on an object recently detached", 
                JDOHelper.isDirty(woodyDetached));

            // Make some updates
            woodyDetached.setSalary(15);
            assertTrue("JDOHelper.isDetached returns false on an object recently detached and modified!", 
                JDOHelper.isDetached(woodyDetached));
            assertTrue("JDOHelper.isDirty returns false on an object recently detached and modified", 
                JDOHelper.isDirty(woodyDetached));

            // Attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = (Employee)pm.makePersistent(woodyDetached);

                assertFalse("JDOHelper.isDetached returns true on an object just attached!", 
                    JDOHelper.isDetached(woody));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * test if simple detachment works. to avoid problems like "No metadata has been registered for class..."
     * TODO Change this to use Company model data
     */
    public void testSimpleDetach()
    {
        try
        {
            ClassOwner owner1 = new ClassOwner("Owner1");
            ClassElements elementA = new ClassElements("ElementA");
            ClassElements elementB = new ClassElements("ElementB");
            ClassElements elementC = new ClassElements("ElementC");
            ClassElements elementD = new ClassElements("ElementD");
            owner1.addElement(elementA);
            owner1.addElement(elementB);
            owner1.getMapToCheckPrefetch().put("C",elementC);
            owner1.getListToCheckPrefetch().add(elementD);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(owner1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            pmf.getDataStoreCache().evictAll();

            //make sure we have nothing in cache (metadata in cache)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                //test detach and attach
                tx.begin();
                Query query = pm.newQuery(ClassOwner.class);

                Collection result = (Collection) query.execute();
                //add a group just above the detach, so we can see if the detachCopyAll retrieves the fields
                pm.getFetchPlan().addGroup("collection");
                pm.getFetchPlan().setMaxFetchDepth(2);
                result = pm.detachCopyAll(result);

                tx.commit();
                ClassOwner ownerResult = (ClassOwner)result.iterator().next();
                assertEquals("Expected 2 elements",2,ownerResult.getElements().size());
            }
            catch( Exception e )
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }
        }
        finally
        {
            clean(ClassOwner.class);
            clean(ClassElements.class);
        }
    }

    /**
     * Basic test of detach-attach process.
     */
    public void testBasicDetachAttach()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1",new Integer(10));
            Employee woodyDetached = null;
            Employee woody2;
            Employee woodyAttached = null;
            Object id = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                //test detach and attach
                tx.begin();
                pm.makePersistent(woody);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                id = pm.getObjectId(woody);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
                woodyDetached.setLastName("Woodpecker0");
                
                tx.begin();
                pm.makePersistent(woodyDetached);
                tx.commit();
                
                tx.begin();
                woody2 = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", "Woodpecker0", woody2.getLastName());
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // test pc are the same after attach
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                woodyDetached.setLastName("Woodpecker1");
                
                tx.begin();
                woody2 = (Employee) pm.getObjectById(id, true);
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                tx.commit();
                
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached,woody2);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
                // test pc are the same after attach, now in different order, first attach and later get object
                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                woodyDetached.setLastName("Woodpecker1");
                
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id, true);
                tx.commit();
                
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS | FetchPlan.DETACH_UNLOAD_FIELDS);
            tx = pm.currentTransaction();
            try
            {
                // test reading non copied field raises exception - DetachedClean
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
                
                boolean success = false;
                try
                {
                    woodyDetached.getYearsInCompany();
                }
                catch (JDODetachedFieldAccessException detex)
                {
                    success = true;
                }
                assertTrue("Expected JDODetachedFieldAccessException on reading a non-detached field", success);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS | FetchPlan.DETACH_UNLOAD_FIELDS);
            tx = pm.currentTransaction();
            try
            {
                // test reading non copied field raises exception - DetachedDirty
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                // makes it dirty
                woodyDetached.setFirstName("00");
                assertTrue("Detached instance should be dirty", JDOHelper.isDirty(woodyDetached));
                boolean success = false;
                try
                {
                    woodyDetached.getYearsInCompany();
                }
                catch (JDODetachedFieldAccessException detex)
                {
                    success = true;
                }
                assertTrue("Expected JDODetachedFieldAccessException on reading a non-detached field", success);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    /**
     * test pc objects aggregating other pcs. associations N-1
     */
    public void testDetachAttach_ManyToOne()
    {
        try
        {
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1",new Integer(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss3 = new Manager(5,"Boss","WakesUp3","boss3@wakes.up",6,"serial 5");
            woody.setManager(bart);
            Department deptB = new Department("DeptB");
            deptB.setManager(bart);
            
            Employee woodyDetached;
            Employee bossDetached;
            Object id;
            Object idBoss;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                //-----------------------------------------------------------------------------------------
                //test 1 - test detach and attach
                //-----------------------------------------------------------------------------------------
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(boss);
                pm.makePersistent(deptB);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                id = pm.getObjectId(woody);
                idBoss = pm.getObjectId(boss);
                woodyDetached.getManager().setLastName("Simpson0");
                
                tx.begin();
                pm.makePersistent(woodyDetached);
                tx.commit();
                
                System.gc();
                
                tx.begin();
                Employee woody2 = (Employee) pm.getObjectById(id,true);
                assertEquals("expected change in attached instance","Simpson0",woody2.getManager().getLastName());
                tx.commit();
                
                //-----------------------------------------------------------------------------------------
                //test 2 - test pc are the same after attach
                //-----------------------------------------------------------------------------------------
                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                woodyDetached.setLastName("Simpson1");
                
                tx.begin();
                woody2 = (Employee) pm.getObjectById(id,true);
                Employee woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached,woody2);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached.getManager(),woody2.getManager());
                tx.commit();
                
                //-----------------------------------------------------------------------------------------
                //test 3 - test pc are the same after attach, now in different order, first attach and later get object
                //-----------------------------------------------------------------------------------------
                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                woodyDetached.setLastName("Simpson1");
                
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id,true);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached,woody2);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached.getManager(),woody2.getManager());
                tx.commit();
                
                //-----------------------------------------------------------------------------------------
                //test 4 - test changing aggregated pc. aggregated pc is not yet persistent
                //-----------------------------------------------------------------------------------------
                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                woodyDetached.setLastName("Simpson1");
                assertTrue("pc instance should not be already persistent",!JDOHelper.isPersistent(boss3));
                woodyDetached.setManager(boss3);
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id,true);                
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached, woody2);
                assertEquals("changed aggregated pc instance was not applied to the datastore",woodyAttached.getManager(), boss3);
                assertTrue("aggregated pc instance was expected to be made persistent",JDOHelper.isPersistent(boss3));
                tx.commit(); 

                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                assertEquals("changed aggregated pc instance was not applied to the datastore",woody.getManager(), boss3);
                tx.commit();                

                //-----------------------------------------------------------------------------------------
                //test 5 - test changing aggregated pc. aggregated pc is already persistent
                //-----------------------------------------------------------------------------------------
                tx.begin();
                pm.makePersistent(boss2);
                tx.commit();

                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();

                woodyDetached.setLastName("Simpson1");
                woodyDetached.setManager(boss2);

                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id,true);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached, woody2);
                assertEquals("changed aggregated pc instance was not applied to the datastore",woodyAttached.getManager(), boss2);
                assertTrue("aggregated pc instance was expected to be made persistent",JDOHelper.isPersistent(boss2));
                tx.commit();

                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                assertEquals("changed aggregated pc instance was not applied to the datastore",woody.getManager(), boss2);
                tx.commit();
                
                //-----------------------------------------------------------------------------------------
                //test 6 - test setting aggregated pc to null
                //-----------------------------------------------------------------------------------------
                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
                
                woodyDetached.setLastName("Simpson1");
                woodyDetached.setManager(null);

                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id,true);
                assertEquals("attached instance returned must be the one already enlisted in the PM",woodyAttached,woody2);
                assertEquals("attached instance returned has incorrect last name", "Simpson1", woody2.getLastName());
                assertNull("changed aggregated pc instance was not applied to the datastore. it should be null",woodyAttached.getManager());
                tx.commit();

                tx.begin();
                woody = (Employee) pm.getObjectById(id,true);
                assertNull("changed aggregated pc instance was not applied to the datastore. it should be null",woody.getManager());
                tx.commit();
                
                //-----------------------------------------------------------------------------------------
                //test 7 - test detach and read aggregated pc field when its null
                //-----------------------------------------------------------------------------------------
                tx.begin();
                boss = (Manager) pm.getObjectById(idBoss,true);
                bossDetached = (Manager)pm.detachCopy(boss);
                tx.commit();
                
                assertNull("pc field should be null",bossDetached.getManager());
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test detach/attach using N-1 relations and a new PM for each operation.
     */
    public void testDetachAttach_ManyToOne_NewPM()
    {
        try
        {
            Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
            Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
            woody.setManager(bart);
            Department deptB = new Department("DeptB");
            deptB.setManager(bart);

            Employee woodyDetached = null;
            Employee woodyAttached = null;
            Employee woody2 = null;
            Object id = null;

            // -----------------------------------------------------------------------------------------
            // test 1 - test detach and attach
            // -----------------------------------------------------------------------------------------

            // store and detach objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(deptB);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
                id = pm.getObjectId(woody);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // change detached objects
            woodyDetached.getManager().setLastName("Simpson0");

            // attach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(woodyDetached);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }
            // check attach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woody2 = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", "Simpson0", woody2.getManager().getLastName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // -----------------------------------------------------------------------------------------
            // test 2 - test pc are the same after attach
            // -----------------------------------------------------------------------------------------
            // detach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // change detached objects
            woodyDetached.setLastName("Simpson1");

            // attach objects and check objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {

                tx.begin();
                woody2 = (Employee) pm.getObjectById(id, true);
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached.getManager(), woody2.getManager());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // -----------------------------------------------------------------------------------------
            // test 3 - test pc are the same after attach, now in different
            // order, first attach and later get object
            // -----------------------------------------------------------------------------------------

            // detach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // change detached objects
            woodyDetached.setLastName("Simpson1");

            // attach and check objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id, true);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached.getManager(), woody2.getManager());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // -----------------------------------------------------------------------------------------
            // test 4 - test changing aggregated pc. aggregated pc is not yet persistent
            // -----------------------------------------------------------------------------------------

            // detach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // change detached objects
            woodyDetached.setLastName("Simpson1");
            Manager boss = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
            assertTrue("pc instance should not be already persistent", !JDOHelper.isPersistent(boss));
            woodyDetached.setManager(boss);

            // attach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id, true);

                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                assertEquals("changed aggregated pc instance was not applied to the datastore", woodyAttached.getManager(), boss);
                assertTrue("aggregated pc instance was expected to be made persistent", JDOHelper.isPersistent(boss));

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // -----------------------------------------------------------------------------------------
            // test 5 - test changing aggregated pc. aggregated pc is already persistent
            // -----------------------------------------------------------------------------------------

            // detach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager boss2 = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
                pm.makePersistent(boss2);
                tx.commit();

                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();

                woodyDetached.setLastName("Simpson1");
                woodyDetached.setManager(boss2);
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id, true);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                assertEquals("changed aggregated pc instance was not applied to the datastore", woodyAttached.getManager(), boss2);
                assertTrue("aggregated pc instance was expected to be made persistent", JDOHelper.isPersistent(boss2));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // -----------------------------------------------------------------------------------------
            // test 6 - test setting aggregated pc to null
            // -----------------------------------------------------------------------------------------

            // detach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woody = (Employee) pm.getObjectById(id, true);
                woodyDetached = (Employee) pm.detachCopy(woody);
                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }

            // change detached objects
            woodyDetached.setLastName("Simpson1");
            woodyDetached.setManager(null);

            // attach objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                woodyAttached = (Employee) pm.makePersistent(woodyDetached);
                woody2 = (Employee) pm.getObjectById(id, true);
                assertEquals("attached instance returned must be the one already enlisted in the PM", woodyAttached, woody2);
                assertNull("changed aggregated pc instance was not applied to the datastore. it should be null", woodyAttached.getManager());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }    

    /**
     * test pc objects aggregating other pcs. associations 1-n
     */
    public void testDetachAttach_OneToMany()
    {
        try
        {
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            Manager boss2 = new Manager(4,"Boss","WakesUp2","boss2@wakes.up",5,"serial 4");
            Manager boss3 = new Manager(5,"Boss","WakesUp3","boss3@wakes.up",6,"serial 5");
            Manager boss4 = new Manager(6,"Boss","WakesUp4","boss4@wakes.up",7,"serial 6");
            Manager boss5 = new Manager(7,"Boss","WakesUp5","boss5@wakes.up",8,"serial 7");
            bart.addSubordinate(boss);
            bart.addSubordinate(boss2);
            Department deptB = new Department("DeptB");
            bart.addDepartment(deptB);
            
            Manager bartDetached;
            Manager bart2;
            Object id;
            Object id2;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");

            // This is a fix for the fact that Person hashCode/equals rely on non-PK fields and so use of
            // this class outside of a txn will try to load these fields
            tx.setNontransactionalRead(true);
            try
            {
                //test detach and attach
                tx.begin();
                pm.makePersistent(bart);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                id = pm.getObjectId(bart);
                Employee employeeChanged = (Employee) bartDetached.getSubordinates().iterator().next();
                employeeChanged.setLastName("Simpson0");
                id2 = JDOHelper.getObjectId(employeeChanged);
                
                tx.begin();
                bart2 = (Manager) pm.makePersistent(bartDetached);
                tx.commit();
                
                System.gc();
                
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id,true);
                employeeChanged = (Employee) pm.getObjectById(id2,true);
                assertEquals(bart2.getSubordinates().size(),2);
                assertEquals("expected change in attached instance","Simpson0",employeeChanged.getLastName());
                tx.commit();
                
                //test pc are the same after attach
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id,true);
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("attached instance returned must be the one already enlisted in the PM",bartDetached.getSubordinates().containsAll(bart2.getSubordinates()));
                tx.commit();
                
                
                //test pc are the same after attach, now in different order, first attach and later get object
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id,true);
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("attached instance returned must be the one already enlisted in the PM",bartDetached.getSubordinates().containsAll(bart2.getSubordinates()));
                tx.commit();            
                
                //test changing aggregated pc. add element pc which is not yet persistent
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                assertTrue("pc instance should not be already persistent",!JDOHelper.isPersistent(boss3));
                bartDetached.addSubordinate(boss3);
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                pm.flush();
                bart2 = (Manager) pm.getObjectById(id,true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("add element to collection was not applied to the datastore",bartDetached.getSubordinates().contains(boss3));
                assertTrue("aggregated pc instance was expected to be made persistent",JDOHelper.isPersistent(boss3));
                //verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss));
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss2));
                tx.commit();            
                
                //test changing aggregated pc. add element pc which is already persistent
                tx.begin();
                pm.makePersistent(boss4);
                tx.commit();
                
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                bartDetached.addSubordinate(boss4);
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id,true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("add element to collection was not applied to the datastore",bartDetached.getSubordinates().contains(boss4));
                assertTrue("aggregated pc instance was expected to be made persistent",JDOHelper.isPersistent(boss4));
                //verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss));
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss2));
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss3));
                tx.commit();            
                
                //test changing aggregated pc. remove element
                tx.begin();
                pm.makePersistent(boss4);
                tx.commit();
                
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                bartDetached.removeSubordinate(boss4);
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id,true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("remove element in aggregated pc instance was not applied to the datastore",!bartDetached.getSubordinates().contains(boss4));
                assertTrue("aggregated pc instance was expected to be made persistent",JDOHelper.isPersistent(boss4));
                //verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss));
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss2));
                assertTrue("previous aggregated pc instances were lost",bartDetached.getSubordinates().contains(boss3));
                tx.commit();            
                
                //test changing aggregated pc. aggregated pc is cleared
                tx.begin();
                pm.makePersistent(boss4);
                tx.commit();
                
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.setLastName("Simpson1");
                bartDetached.clearSubordinates();
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id,true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM",bartDetached,bart2);
                assertTrue("clear Collection with aggregated pc instance was not applied to the datastore",bartDetached.getSubordinates().size()==0);
                tx.commit();            
                
                //test sco fields made dirty
                tx.begin();
                pm.makePersistent(boss5);
                tx.commit();
                
                tx.begin();
                bart = (Manager) pm.getObjectById(id,true);
                bartDetached = (Manager)pm.detachCopy(bart);
                tx.commit();
                
                bartDetached.addSubordinate(boss5);
                JDOHelper.makeDirty(bartDetached,"subordinates");
                tx.begin();
                bartDetached = (Manager) pm.makePersistent(bartDetached);
                tx.commit();            
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id,true);
                
                assertEquals(1,bart2.getSubordinates().size());
                assertTrue("SCO field should is missing element",bart2.getSubordinates().contains(boss5));
                assertTrue("element of SCO field is not persistent",JDOHelper.isPersistent(boss5));
                
                tx.commit();            
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }    

    /**
     * test pc objects aggregating other pcs. associations 1-n, with a new PM for each operation
     */
    public void testDetachAttach_OneToMany_NewPM()
    {
        try
        {
            Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
            Manager boss[] = new Manager[5];
            boss[0] = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
            boss[1] = new Manager(4, "Boss", "WakesUp2", "boss2@wakes.up", 5, "serial 4");
            boss[2] = new Manager(5, "Boss", "WakesUp3", "boss3@wakes.up", 6, "serial 5");
            boss[3] = new Manager(6, "Boss", "WakesUp4", "boss4@wakes.up", 7, "serial 6");
            boss[4] = new Manager(7, "Boss", "WakesUp5", "boss5@wakes.up", 8, "serial 7");
            bart.addSubordinate(boss[0]);
            bart.addSubordinate(boss[1]);
            Department deptB = new Department("DeptB");
            bart.addDepartment(deptB);

            Manager bartAttached = null;
            Manager bartDetached = null;
            Manager bart2;
            Object id = null;
            Object bossIds[] = new Object[boss.length];
            Object id2;

            // -----------------------------------------------------------------------------------------
            // start data for tests
            // -----------------------------------------------------------------------------------------
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                pm.makePersistent(bart);
                tx.commit();
                
                id = pm.getObjectId(bart);
                bossIds[0] = pm.getObjectId(boss[0]);
                bossIds[1] = pm.getObjectId(boss[1]);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 1 - test detach and attach
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            Employee employeeChanged = (Employee) bartDetached.getSubordinates().iterator().next();
            employeeChanged.setLastName("Simpson0");
            id2 = JDOHelper.getObjectId(employeeChanged);
            
            attachDetachedManager(bartDetached);
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            
            try
            {
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id, true);
                employeeChanged = (Employee) pm.getObjectById(id2, true);
                assertEquals(2, bart2.getSubordinates().size());
                assertEquals("expected change in attached instance", "Simpson0", employeeChanged.getLastName());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 2 - test pc are the same after attach
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            
            try
            {
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id, true);
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue(Manager.compareElementsContained(bartDetached.getSubordinates(), bart2.getSubordinates()));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 3 - test pc are the same after attach, now in different order,
            // first attach and later get object
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id, true);
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue("attached instance returned must be the one already enlisted in the PM", bartDetached.getSubordinates().containsAll(
                    bart2.getSubordinates()));
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 4 - test changing aggregated pc. add element pc which is not yet
            // persistent
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            assertTrue("pc instance should not be already persistent", !JDOHelper.isPersistent(boss[2]));
            bartDetached.addSubordinate(boss[2]);
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                pm.flush();
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue("add element to collection was not applied to the datastore", bartAttached.getSubordinates().contains(boss[2]));
                assertTrue("aggregated pc instance was expected to be made persistent", JDOHelper.isPersistent(boss[2]));
                // verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost", bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[0], false)));
                assertTrue("previous aggregated pc instances were lost", bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[1], false)));
                tx.commit();
                bossIds[2] = pm.getObjectId(boss[2]);
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 5 - test changing aggregated pc. add element pc which is already
            // persistent
            // -----------------------------------------------------------------------------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            Employee detachedBoss3 = null;
            try
            {
                tx.begin();
                pm.makePersistent(boss[3]);
                detachedBoss3 = (Employee)pm.detachCopy(boss[3]);
                tx.commit();
                bossIds[3] = pm.getObjectId(boss[3]);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            bartDetached.addSubordinate(detachedBoss3);
            JDOHelper.makeDirty(bartDetached, "subordinates");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue("add element to collection was not applied to the datastore", 
                    bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[3], false)));
                assertTrue("aggregated pc instance was expected to be made persistent", 
                    JDOHelper.isPersistent(pm.getObjectById(bossIds[3], false)));
                // verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost", 
                    bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[0], false)));
                assertTrue("previous aggregated pc instances were lost", 
                    bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[1], false)));
                assertTrue("previous aggregated pc instances were lost", 
                    bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[2], false)));
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 6 - test changing aggregated pc. remove element
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            bartDetached.removeSubordinate(getDetachedManager(bossIds[3], "groupSubordinates"));
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue("remove element in aggregated pc instance was not applied to the datastore", !bartAttached.getSubordinates().contains(
                    pm.getObjectById(bossIds[3], false)));
                assertTrue("aggregated pc instance was expected to be made persistent", JDOHelper.isPersistent(pm.getObjectById(bossIds[3], false)));
                // verify if previous boss were not lost
                assertTrue("previous aggregated pc instances were lost", bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[0], false)));
                assertTrue("previous aggregated pc instances were lost", bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[1], false)));
                assertTrue("previous aggregated pc instances were lost", bartAttached.getSubordinates().contains(pm.getObjectById(bossIds[2], false)));
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 7 - test changing aggregated pc. aggregated pc is cleared
            // -----------------------------------------------------------------------------------------
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.setLastName("Simpson1");
            bartDetached.clearSubordinates();
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bartAttached = (Manager) pm.makePersistent(bartDetached);
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertEquals("attached instance returned must be the one already enlisted in the PM", bartAttached, bart2);
                assertTrue("clear Collection with aggregated pc instance was not applied to the datastore", bartAttached.getSubordinates().size() == 0);
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertTrue("clear Collection with aggregated pc instance was not applied to the datastore", bart2.getSubordinates().size() == 0);
                tx.commit();
                
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // -----------------------------------------------------------------------------------------
            // test 8 - test sco fields made dirty
            // -----------------------------------------------------------------------------------------
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup("groupSubordinates");
            
            try
            {
                tx.begin();
                pm.makePersistent(boss[4]);
                tx.commit();
                bossIds[4] = pm.getObjectId(boss[4]);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            bartDetached = getDetachedManager(id, "groupSubordinates");
            
            bartDetached.addSubordinate(getDetachedManager(bossIds[4], "groupSubordinates"));
            JDOHelper.makeDirty(bartDetached, "subordinates");
            
            attachDetachedManager(bartDetached);
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                bart2 = (Manager) pm.getObjectById(id, true);
                
                assertEquals(1, bart2.getSubordinates().size());
                assertTrue("SCO field should is missing element", bart2.getSubordinates().contains(pm.getObjectById(bossIds[4], false)));
                assertTrue("element of SCO field is not persistent", JDOHelper.isPersistent(pm.getObjectById(bossIds[4], false)));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }    

    /**
     * Test of detach-attach with a transactional field in the class.
     */
    public void testDetachTransactionalField()
    {
        try
        {
            Object objectId = null;
            ClassWithTransactionalField obj1 = new ClassWithTransactionalField("name","transactional");
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                //test detach and attach
                tx.begin();
                pm.makePersistent(obj1);
                tx.commit();
                objectId = pm.getObjectId(obj1);
                
                //test detach and attach
                tx.begin();
                obj1 = (ClassWithTransactionalField) pm.getObjectById(objectId,true);
                pm.getFetchPlan().addGroup("detach");
                obj1 = (ClassWithTransactionalField) pm.detachCopy(obj1);
                assertNull("transactional field not correct",obj1.getTransactional());
                tx.commit();
                
                //test detach and attach
                tx.begin();
                obj1 = (ClassWithTransactionalField) pm.getObjectById(objectId,true);
                pm.getFetchPlan().addGroup("detach");
                obj1.setTransactional("transactional");
                obj1 = (ClassWithTransactionalField) pm.detachCopy(obj1);
                assertEquals("transactional field not correct","transactional",obj1.getTransactional());
                tx.commit();
            }
            catch( Exception e )
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }
        }
        finally
        {
            clean(ClassWithTransactionalField.class);
        }
    }

    /**
     * Test of detach/attach with a Collection of non-PC objects.
     * TODO Change the sample to a generic collection of non-PC to be used everywhere
     */
    public void testDetachCollectionWithNonPCElements()
    {
        try
        {
            Object objectId = null;
            ClassWithNonPCCollection obj1 = new ClassWithNonPCCollection();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // test detach and attach
                tx.begin();
                obj1.getElements().add("elem1");
                obj1.getElements().add("elem2");
                pm.makePersistent(obj1);
                tx.commit();
                objectId = pm.getObjectId(obj1);

                // test detach and attach
                tx.begin();
                obj1 = (ClassWithNonPCCollection) pm.getObjectById(objectId, true);
                pm.getFetchPlan().addGroup("detach");
                obj1 = (ClassWithNonPCCollection) pm.detachCopy(obj1);
                assertEquals("wrong number of detached non pc elements", 2, obj1.getElements().size());
                assertEquals("wrong element of detached non pc element", "elem1", obj1.getElements().get(0));
                assertEquals("wrong element of detached non pc element", "elem2", obj1.getElements().get(1));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }
        }
        finally
        {
            clean(ClassWithNonPCCollection.class);
        }
    }

    public void testAttachDetachNonTransactionalRead()
    {
        try
        {
            Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
            Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
            Manager boss = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");

            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);

            try
            {
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(boss);
                pm.makePersistent(bart);
                tx.commit();

                // non transactional read
                Collection c = (Collection) pm.newQuery(Employee.class).execute();
                Employee detachedEmployee = (Employee) pm.detachCopy(c.iterator().next());

                // test with String

                detachedEmployee.setFirstName("detached guy");

                id = JDOHelper.getObjectId(detachedEmployee);

                tx.begin();
                pm.makePersistent(detachedEmployee);
                tx.commit();

                System.gc();

                tx.begin();
                Employee emp = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", "detached guy", emp.getFirstName());
                tx.commit();

                // non transactional read
                c = (Collection) pm.newQuery(Employee.class).execute();
                detachedEmployee = (Employee) pm.detachCopy(c.iterator().next());

                // test with Integer (Object) fields non DFG
                detachedEmployee.setYearsInCompany(new Integer(33));

                id = JDOHelper.getObjectId(detachedEmployee);

                tx.begin();
                pm.makePersistent(detachedEmployee);
                tx.commit();

                System.gc();

                tx.begin();
                emp = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", 33, emp.getYearsInCompany().intValue());
                tx.commit();

                // non transactional read
                c = (Collection) pm.newQuery(Employee.class).execute();
                detachedEmployee = (Employee) pm.detachCopy(c.iterator().next());

                // test with long (Primitive)
                detachedEmployee.setPersonNum(546);

                id = JDOHelper.getObjectId(detachedEmployee);

                tx.begin();
                pm.makePersistent(detachedEmployee);
                tx.commit();

                System.gc();

                tx.begin();
                emp = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", 546, emp.getPersonNum());
                tx.commit();

                // non transactional read
                c = (Collection) pm.newQuery(Employee.class).execute();
                detachedEmployee = (Employee) pm.detachCopy(c.iterator().next());

                // test with PC (Object)
                detachedEmployee.setManager(bart);

                id = JDOHelper.getObjectId(detachedEmployee);

                tx.begin();
                pm.makePersistent(detachedEmployee);
                tx.commit();

                System.gc();

                tx.begin();
                emp = (Employee) pm.getObjectById(id, true);
                assertEquals("expected change in attached instance", bart.getFirstName(), emp.getManager().getFirstName());
                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for a 1-N bidirectional relationship which, by its nature,
     * provides a recursive detach test.
     */
    public void testDetachAttach_OneToManyBidir()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.setNontransactionalRead(true);

            Object id = null;
            try
            {
                // Persist some objects
                tx.begin();

                Farm farm = new Farm("North End Farm");
                Animal duck = new Animal("Donald");
                Animal cow = new Animal("Gertrude");
                Animal horse = new Animal("Shergar");
                farm.addAnimal(duck);
                farm.addAnimal(cow);
                farm.addAnimal(horse);
                
                pm.makePersistent(farm);
                
                tx.commit();
                id = pm.getObjectId(farm);
            }
            catch (Exception e)
            {
                fail("Exception thrown while persisting 1-N bidirectional objects for the recursive attach/detach test " + e.getMessage());
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
            Farm detachedFarm = null;
            try
            {
                tx.begin();

                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                pm.getFetchPlan().setMaxFetchDepth(2);

                Farm farm = (Farm)pm.getObjectById(id, false);
                assertTrue("Error retrieving the Farm object that was just persisted", farm != null);
                LOG.info("Retrieved Farm \"" + farm.toString() + "\"");
                
                detachedFarm = (Farm)pm.detachCopy(farm);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Exception thrown while detaching 1-N bidrectional objects in the recursive attach/detach test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Play with the farm
            try
            {
                LOG.info("Detached farm : " + detachedFarm);
                HashSet animals = detachedFarm.getAnimals();
                Iterator animalsIter = animals.iterator();
                while (animalsIter.hasNext())
                {
                    LOG.info("Detached animal : " + animalsIter.next());
                }
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown on use of allegedly detached farm " + e.getMessage());
            }
        }
        finally
        {
            // Clean out created data
            clean(Farm.class);
            clean(Animal.class);
        }
    }
    
    /**
     * test pc objects aggregating other pcs. associations 1-N FK
     */
    public void testDetachAttach_OneToManyFK()
    {
        try
        {
            Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
            Manager bart = new Manager(2, "Bart", "Simpson", "bart@simpson.com", 2, "serial 2");
            Manager boss = new Manager(3, "Boss", "WakesUp", "boss@wakes.up", 4, "serial 3");
            woody.setManager(bart);

            Manager bossDetached;
            Object idBoss;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // -----------------------------------------------------------------------------------------
                // test 1 - test detach and attach
                // -----------------------------------------------------------------------------------------
                tx.begin();
                pm.makePersistent(woody);
                pm.makePersistent(boss);
                pm.getFetchPlan().addGroup("groupDepartments");
                bossDetached = (Manager) pm.detachCopy(boss);
                tx.commit();

                idBoss = pm.getObjectId(boss);
                Department deptB = new Department("DeptB");
                deptB.setManager(bossDetached);
                bossDetached.addDepartment(deptB);

                tx.begin();
                pm.makePersistent(bossDetached);
                tx.commit();

                System.gc();

                tx.begin();
                Manager theBoss = (Manager) pm.getObjectById(idBoss, true);
                Assert.assertEquals(1, theBoss.getDepartments().size());
                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();

                pm.close();
            }
        }
        finally
        {
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Tests attach-detach with an M-N relation
     */
    public void testDetachAttach_ManyToMany()
    {
        try
        {
            Customer customer1 = new Customer("Joe Smith");
            Supplier supplier1 = new Supplier("Smegma Enterprises");
            Supplier supplier2 = new Supplier("Amazonia");
            customer1.addSupplier(supplier1);
            supplier1.addCustomer(customer1);
            customer1.addSupplier(supplier2);
            supplier2.addCustomer(customer1);

            Customer custDetached = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // Persist and detach the Customer (and its Suppliers)
                tx.begin();
                customer1 = (Customer)pm.makePersistent(customer1);
                custDetached = (Customer)pm.detachCopy(customer1);
                tx.commit();

                // Create a new Supplier and add it to the Customer
                Supplier supplier3 = new Supplier("Hnos Hernandez s.a.");
                custDetached.addSupplier(supplier3);
                supplier3.addCustomer(custDetached);

                // Attach the new Supplier
                tx.begin();
                pm.makePersistent(supplier3);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            clean(Customer.class);
            clean(Supplier.class);
        }
    }

    /**
     * Test checks if we can connect detached objects outside pm context
     */
    public void testRelationManaging()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                Master m = new Master();
                m.setId("Master");
                pm.makePersistent(m);
                Detail d = new Detail();
                d.setId("Detail");
                d.setMaster(m);
                Master m1 = new Master();
                m1.setId("Master1");
                pm.makePersistent(d);
                pm.makePersistent(m1);

                pm.detachCopy(m);
                Detail detachedD = (Detail) pm.detachCopy(d);

                detachedD.setMaster(m1);
                Master chM = detachedD.getMaster();

                assertEquals(chM.getId(), "Master1");
                Detail attachedD = (Detail) pm.makePersistent(detachedD);
                assertEquals(attachedD.getMaster().getId(), "Master1");

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
            // Clean up our data
            clean(Detail.class);
            clean(Master.class);
        }
    }

    /**
     * Test of detaching object graph with circular references
     */
    public void testCircularDetach()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                Master m = new Master();
                m.setId("tcMaster");
                Detail d = new Detail();
                d.setId("tcDetail");
                Circon c = new Circon();
                c.setId("tcConnector");

                m.addDetail(d);
                m.setCircon(c);
                d.setMaster(m);
                d.addCircon(c);
                c.setDetail(d);
                c.addMaster(m);

                pm.makePersistent(m);
                assertTrue(JDOHelper.isPersistent(m));
                assertTrue(JDOHelper.isPersistent(d));
                assertTrue(JDOHelper.isPersistent(c));

                pm.getFetchPlan().addGroup("all");
                pm.getFetchPlan().setMaxFetchDepth(2);
                Master dm = (Master) pm.detachCopy(m);
                assertTrue(JDOHelper.isDetached(dm));
                assertTrue(JDOHelper.isDetached(dm.getCircon()));
                assertTrue(JDOHelper.isDetached(dm.getCircon().getDetail()));
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
            // Clean up our data
            clean(Circon.class);
            clean(Detail.class);
            clean(Master.class);
        }
    }

    /**
     * Test of detaching object and attaching relation between objects
     */
    public void testDetachAttach_OneToMany_RelationConsistency()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();

                // Persist 1-N bidir relation
                Manager m = new Manager(1, "Homer", "Simpson", "homer@fox.com", 4, "serial 1");
                Department d = new Department("Nuclear");
                d.setManager(m);
                m.addDepartment(d);

                pm.makePersistent(m);

                pm.getFetchPlan().setMaxFetchDepth(2);
                Manager dm = (Manager) pm.detachCopy(m);
                Department dd = (Department) pm.detachCopy(d);

                dd.setManager(dm);

                Department ad = (Department) pm.makePersistent(dd);
                assertTrue(m.equals(ad.getManager()));

                tx.commit();
                tx.begin();
                pm.refresh(ad);

                assertTrue(m.equals(ad.getManager()));
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
            // Clean up our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * TODO Change to use a generic collection<nonPC> sample
     *
     */
    public void testAttachDetachNonPCCollectionElements()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();
                pm.getFetchPlan().addGroup("collection");

                Owner o = new Owner();
                o.getElements().add("Elm 1");
                o.getElements().add("Elm 2");
                o.getElements().add("Elm 3");
                o.getElements().add("Elm 4");
                o.getSetElements().add("Elm 1");
                o.getSetElements().add("Elm 2");
                pm.makePersistent(o);

                Owner o1 = (Owner) pm.detachCopy(o);
                tx.commit();

                pm.close();
                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                o1.getElements().add("Elm 5");
                o1.getSetElements().add("Elm 3");
                Owner o2 = (Owner) pm.makePersistent(o1);
                assertEquals(5, o2.getElements().size());
                assertEquals(3, o2.getSetElements().size());

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
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
            // clean up our data
            clean(Owner.class);
        }
    }

    /**
     * Test that persists an object, and then detaches it. Then creates an object and adds a relation
     * to the detached object and persists the new object. The related object should be connected to
     * the new object (shouldn't create a new version of the object).
     * @throws Exception Thrown if an error occurs.
     */
    public void testPersistWithDetachedRelative() 
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            Account detachedAcct = null;
            try
            {
                tx.begin();
                
                Account acct = new Account();
                acct.setEnabled(true);
                acct.setUsername("john");
                pm.makePersistent(acct);
                
                detachedAcct = (Account)pm.detachCopy(acct);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception in test", ue);
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
            
            // Persist the new object with the related detached
            // and detachCopy the new object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));
                woody.setAccount(detachedAcct);
                
                pm.makePersistent(woody);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
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
            // Clean up our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test using a Map<String,PC> and tests the persistence of a value on its own
     * then the attach of the value in a Map, as well as detecting new values added
     * while the map was detached.
     */
    public void testPersistWithDetachedRelativeInMap() throws Exception
    {
        try
        {
            PersistenceManager pm;
            Transaction tx;

            MapValueItem detachedItem1 = null;
            
            // Create an instance, persist it and detach it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                MapValueItem item1 = new MapValueItem("First", "First item");
                pm.makePersistent(item1);
                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                detachedItem1 = (MapValueItem) pm.detachCopy(item1);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
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
            
            MapHolder container = new MapHolder();
            container.getJoinMapNonPC().put("Original", detachedItem1);
            // To complete the test, we add a new - not yet persisted - item
            container.getJoinMapNonPC().put("New", new MapValueItem("Second", "Second item"));

            MapValueItem detachedItem3 = null;
            MapHolder detachedContainer = null;
            Object containerId = null;

            // Persist the new container, with the detached item, and the new item
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(container);

                // Detach the container and check its contents
                pm.getFetchPlan().addGroup(FetchPlan.ALL);
                detachedContainer = (MapHolder) pm.detachCopy(container);
                assertTrue("Detached container doesnt contain first item ", detachedContainer.getJoinMapNonPC().get("Original") != null);
                assertTrue("Detached container doesnt contain second item ", detachedContainer.getJoinMapNonPC().get("New") != null);

                // Persist and detach a new item
                MapValueItem item3 = new MapValueItem("Third", "Third Item");
                pm.makePersistent(item3);
                detachedItem3 = (MapValueItem) pm.detachCopy(item3);

                tx.commit();
                containerId = JDOHelper.getObjectId(container);
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
                fail("Exception thrown while performing test : " + ue.getMessage());
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }
            
            // Add a detached object to the Map, and add a new (transient) object to the Map.
            // Attach the updated container object with its 4 map values.
            detachedContainer.getJoinMapNonPC().put("Next", detachedItem3);
            detachedContainer.getJoinMapNonPC().put("Latest", new MapValueItem("Fourth", "Fourth Item"));

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(detachedContainer);
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
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

            // Check, whether our container has all map values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                container = (MapHolder) pm.getObjectById(containerId);
                assertTrue("Container doesnt contain first item ", container.getJoinMapNonPC().get("Original") != null);
                assertTrue("Container doesnt contain second item ", container.getJoinMapNonPC().get("New") != null);
                assertTrue("Container doesnt contain second item ", container.getJoinMapNonPC().get("Next") != null);
                assertTrue("Container doesnt contain second item ", container.getJoinMapNonPC().get("Latest") != null);
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error("Exception during test", ue);
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
            // Clean up our data
            clean(MapHolder.class);
            clean(MapValueItem.class);
        }
    }

    /**
     * Test the detachment of a non-persistent object.
     */
    public void testDetachOfNonPersistentPC()
    throws Exception
    {
        try
        {
            PersistenceManager pm;
            Transaction tx;

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@woodpecker.com", 13, "serial 1", new Integer(10));

                // This should persist it and then detach it
                pm.detachCopy(woody);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                fail("Calling pm.detachCopy on an unpersisted object failed!");
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
            clean(Employee.class);
        }
    }

    /**
     * Test of the attach/detach process for an object that contains the various types of SCO dates.
     * @throws Exception
     */
    public void testDetachAttachOfSCODate()
    throws Exception
    {
        try
        {
            DetachDates detachedDate = null;
            Object dateId = null;

            // Persist an object containing a Date
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("dates");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DetachDates date = new DetachDates(0);
                pm.makePersistent(date);

                detachedDate = (DetachDates) pm.detachCopy(date);

                tx.commit();
                dateId = pm.getObjectId(date);
            }
            catch (Exception e)
            {
                fail("Error whilst persisting and detaching object containing SCO dates : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            assertEquals("Value of the java.util.Date that was detached is incorrect",
                0, detachedDate.getUtilDate().getTime());
            assertEquals("Value of the java.sql.Date that was detached is incorrect",
                0, detachedDate.getSqlDate().getTime());
            assertEquals("Value of the java.sql.Time that was detached is incorrect",
                0, detachedDate.getSqlTime().getTime());
            assertEquals("Value of the java.sql.Timestamp that was detached is incorrect",
                0, detachedDate.getSqlTimestamp().getTime());

            // Create set of milliseconds for updates
            long javaUtilDateMillis = 10000000;
            long javaSqlDateMillis = 172800000; // 3 Jan 1970 00:00:00 (GMT)
            long javaSqlTimestampMillis = 30000000;
            long javaSqlTimeMillis = 42345000; // This is rounded to be consistent with java.sql.Time
            long javaSqlTimestampMillis2 = 50000000;
            SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fmt3 = new SimpleDateFormat("HH:mm:ss");

            java.sql.Date sqlDateTmp = new java.sql.Date(0);
            sqlDateTmp.setTime(javaSqlDateMillis);
            String sqlDateString = fmt2.format(sqlDateTmp);
            java.sql.Time sqlTimeTmp = new java.sql.Time(0);
            sqlTimeTmp.setTime(javaSqlTimeMillis);
            String sqlTimeString = fmt3.format(sqlTimeTmp);

            // Perform an update to the contents of some of the dates
            detachedDate.setUtilDate(javaUtilDateMillis);
            detachedDate.setSqlDate(javaSqlDateMillis);
            detachedDate.setSqlTimestamp(javaSqlTimestampMillis);

            // Attach the date
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("dates");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DetachDates attachedDate = (DetachDates) pm.makePersistent(detachedDate);

                // Update some of the attached objects directly (test that it uses SCO wrappers)
                attachedDate.setSqlTime(javaSqlTimeMillis);
                attachedDate.setSqlTimestamp(javaSqlTimestampMillis2);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO dates : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("dates");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DetachDates date = (DetachDates) pm.getObjectById(dateId);
                assertEquals("Value of the java.util.Date in the datastore is incorrect",
                    javaUtilDateMillis, date.getUtilDate().getTime());
                assertEquals("Value of the java.sql.Date in the datastore is incorrect",
                    sqlDateString, fmt2.format(date.getSqlDate()));
                assertEquals("Value of the java.sql.Time in the datastore is incorrect",
                    sqlTimeString, fmt3.format(date.getSqlTime()));
                assertEquals("Value of the java.sql.Timestamp in the datastore is incorrect",
                    javaSqlTimestampMillis2, date.getSqlTimestamp().getTime());

                detachedDate = (DetachDates) pm.detachCopy(date);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO dates : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Replace the util Date
            detachedDate.replaceUtilDate(new java.util.Date(50000));

            // Attach the date
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("dates");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                pm.makePersistent(detachedDate);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO dates : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("dates");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DetachDates date = (DetachDates) pm.getObjectById(dateId);
                assertEquals("Value of the java.util.Date in the datastore is incorrect",
                    50000, date.getUtilDate().getTime());
                assertEquals("Value of the java.sql.Date in the datastore is incorrect",
                    sqlDateString, fmt2.format(date.getSqlDate()));
                assertEquals("Value of the java.sql.Time in the datastore is incorrect",
                    sqlTimeString, fmt3.format(date.getSqlTime()));
                assertEquals("Value of the java.sql.Timestamp in the datastore is incorrect",
                    javaSqlTimestampMillis2, date.getSqlTimestamp().getTime());

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO dates : " + e.getMessage());
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
            clean(DetachDates.class);
        }
    }

    /**
     * test pc objects aggregating other pcs. associations 1-n with default fetch group
     */
    public void testAggregatedDetachAttachFieldMap()
    {
        try
        {
            // Create a Gym with 3 Wardrobes
            Gym testGym = new Gym();
            
            Map wardrobes = new HashMap();
            Wardrobe wSmall = new Wardrobe();
            wSmall.setModel("small");
            Wardrobe wMedium = new Wardrobe();
            wMedium.setModel("medium");
            Wardrobe wLarge = new Wardrobe();
            wMedium.setModel("large");
            
            wardrobes.put("small", wSmall);
            wardrobes.put("medium", wMedium);
            wardrobes.put("large", wLarge);
            
            testGym.setWardrobes(wardrobes);
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            Gym detachedGym;
            Gym detachedGym2;
            Gym detachedGym3;
            
            Object gymID = null;
            
            try
            {
                // Persist the objects and detach them all
                tx.begin();
                pm.makePersistent(testGym);
                pm.getFetchPlan().clearGroups();
                pm.getFetchPlan().setGroup("Gym.wardrobes");
                detachedGym = (Gym)pm.detachCopy(testGym);
                tx.commit();
                
                assertEquals("1) Gym.wardrobes.size()", 3, detachedGym.getWardrobes().size());
                
                gymID = pm.getObjectId(detachedGym);
                
                // Attach the (unchanged) objects, and detach just the Gym since we're only updating that
                tx.begin();
                pm.makePersistent(detachedGym);
                pm.getFetchPlan().clearGroups();
                pm.getFetchPlan().setGroup(FetchPlan.DEFAULT);
                detachedGym2 = (Gym)pm.detachCopy(pm.getObjectById(gymID));
                tx.commit();
                
                try
                {
                    Map testMap = detachedGym2.getWardrobes();
                    assertEquals("X) Gym.wardrobes.size() == 3", testMap.size(), 3);
                }
                catch (JDODetachedFieldAccessException e)
                {
                    fail("Field 'Gym.wardrobes' should have been detached since was loaded at detach!");
                }
                
                // Update a field in Gym
                detachedGym2.setLocation("Freiburg");
                
                // Attach the objects, and detach them once more
                tx.begin();
                pm.makePersistent(detachedGym2);
                pm.getFetchPlan().clearGroups();
                pm.getFetchPlan().setGroup("Gym.wardrobes");
                detachedGym3 = (Gym)pm.detachCopy(pm.getObjectById(gymID));
                tx.commit();
                
                assertEquals("2) Gym.wardrobes.size()", 3, detachedGym3.getWardrobes().size());
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // again with another pm
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                if (gymID != null)
                {
                    tx.begin();
                    pm.getFetchPlan().clearGroups();
                    pm.getFetchPlan().setGroup(FetchPlan.DEFAULT);
                    detachedGym = (Gym)pm.detachCopy(pm.getObjectById(gymID));
                    tx.commit();
                    
                    detachedGym.setLocation("Basel");
                    
                    tx.begin();
                    pm.makePersistent(detachedGym);
                    tx.commit();
                    
                    tx.begin();
                    pm.getFetchPlan().clearGroups();
                    pm.getFetchPlan().setGroup("Gym.wardrobes");
                    detachedGym2 = (Gym)pm.detachCopy(pm.getObjectById(gymID));
                    tx.commit();
                    
                    assertEquals("3) Gym.wardrobes.size()", 3, detachedGym2.getWardrobes().size());
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            clean(Gym.class);
            clean(Wardrobe.class);
        }
    }

    /**
     * Test "DetachOnClose" when we close the PM we detach all L1 cached objects.
     */
    public void testDetachOnClose()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("groupSubordinates");
            pm.getFetchPlan().addGroup("groupA");
            ((JDOPersistenceManager)pm).getExecutionContext().setProperty(PropertyNames.PROPERTY_DETACH_ON_CLOSE, true);
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@warnerbros.com", 125, "123409");
                Employee bugs = new Employee(2, "Bugs", "Bunny", "bugs@warnerbros.com", 200, "123410");
                Manager donald = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");
                donald.addSubordinate(woody);
                donald.addSubordinate(bugs);
                woody.setManager(donald);
                bugs.setManager(donald);
                pm.makePersistent(donald);
                
                tx.commit();

                // Close the PM and we should get our objects detached
                pm.close();
                
                // Check that all are now detached
                if (!JDOHelper.isDetached(bugs) || JDOHelper.getObjectId(bugs) == null)
                {
                    fail("Bugs Bunny is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(woody) || JDOHelper.getObjectId(woody) == null)
                {
                    fail("Woody Woodpecker is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(donald) || JDOHelper.getObjectId(donald) == null)
                {
                    fail("Donald Duck is not detached or hasn't been detached correctly after closing the PM");
                }
                
                // Check that the relationships are intact
                if (!woody.getFirstName().equals("Woody") || !woody.getLastName().equals("Woodpecker"))
                {
                    fail("Woody Woodpecker has lost his name after closing the PM");
                }
                if (woody.getManager() != donald)
                {
                    fail("Woody Woodpecker has lost his relation to its Manager after closing the PM");
                }
                if (!bugs.getFirstName().equals("Bugs") || !bugs.getLastName().equals("Bunny"))
                {
                    fail("Bugs Bunny has lost his name after closing the PM");
                }
                if (bugs.getManager() != donald)
                {
                    fail("Bugs Bunny has lost his relation to its Manager after closing the PM");
                }
                if (!donald.getFirstName().equals("Donald") || !donald.getLastName().equals("Duck"))
                {
                    fail("Donald Duck has lost his name after closing the PM");
                }
                if (donald.getSubordinates().size() != 2)
                {
                    fail("Donald Duck has lost some or all of his employees after closing the PM");
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and closing PM with detachOnClose : " + e.getMessage());
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
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test "DetachAllOnCommit" when we commit a transaction and all enlisted (detachable) objects are detached.
     */
    public void testDetachAllOnCommit()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("groupSubordinates");
            pm.getFetchPlan().addGroup("groupA");
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@warnerbros.com", 125, "123409");
                Employee bugs = new Employee(2, "Bugs", "Bunny", "bugs@warnerbros.com", 200, "123410");
                Manager donald = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");
                donald.addSubordinate(woody);
                donald.addSubordinate(bugs);
                woody.setManager(donald);
                bugs.setManager(donald);
                pm.makePersistent(donald);

                // Calling this should detach all tx-enlisted objects (Manager + 2 Employee)
                tx.commit();
                
                // Check that all are now detached
                if (!JDOHelper.isDetached(bugs) || JDOHelper.getObjectId(bugs) == null)
                {
                    fail("Bugs Bunny is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(woody) || JDOHelper.getObjectId(woody) == null)
                {
                    fail("Woody Woodpecker is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(donald) || JDOHelper.getObjectId(donald) == null)
                {
                    fail("Donald Duck is not detached or hasn't been detached correctly after closing the PM");
                }
                
                // Check that the relationships are intact
                if (!woody.getFirstName().equals("Woody") || !woody.getLastName().equals("Woodpecker"))
                {
                    fail("Woody Woodpecker has lost his name after closing the PM");
                }
                if (woody.getManager() != donald)
                {
                    fail("Woody Woodpecker has lost his relation to its Manager after closing the PM");
                }
                if (!bugs.getFirstName().equals("Bugs") || !bugs.getLastName().equals("Bunny"))
                {
                    fail("Bugs Bunny has lost his name after closing the PM");
                }
                if (bugs.getManager() != donald)
                {
                    fail("Bugs Bunny has lost his relation to its Manager after closing the PM");
                }
                if (!donald.getFirstName().equals("Donald") || !donald.getLastName().equals("Duck"))
                {
                    fail("Donald Duck has lost his name after closing the PM");
                }
                if (donald.getSubordinates().size() != 2)
                {
                    fail("Donald Duck has lost some or all of his employees after closing the PM");
                }
                tx.begin();
                pm.makePersistent(donald);
                tx.commit();
                donald.setFirstName("Donaldo");
                tx.begin();
                pm.makePersistent(donald);
                tx.commit();

            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and committing transaction with detachAllOnCommit : " + e.getMessage());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }
    
    /**
     * Test "DetachAllOnCommit" when not "CopyOnAttach" on an unchanged object.
     */
    public void testCopyOnAttachFalseForUnchangedObject()
    {
    	try
    	{
    		Manager mgr = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");

            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            pm.setCopyOnAttach(false);
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin(); // mgr is transient-clean
                pm.makePersistent(mgr); // mgr is now persistent-clean
                tx.commit(); // mgr is now detached-clean
                if (!JDOHelper.isDetached(mgr))
                {
                    fail("The object is not detached or hasn't been detached correctly after committing the TX");
                }

                // Attach the UNCHANGED (detached) object
                tx.begin(); // mgr is detached-clean
                pm.makePersistent(mgr); // mgr is now persistent-clean
                tx.commit(); // mgr is now detached-clean
                if (!JDOHelper.isDetached(mgr))
                {
                    fail("The object is not detached or hasn't been detached correctly after committing the TX");
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and committing transaction with detachAllOnCommit : " + e.getMessage());
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
     * Test "DetachAllOnCommit" when we retrieve an object and commit the txn
     */
    public void testDetachAllOnCommitViaFetch()
    {
        try
        {
            Object id = null;

            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@warnerbros.com", 125, "123409");
                Employee bugs = new Employee(2, "Bugs", "Bunny", "bugs@warnerbros.com", 200, "123410");
                Account bugsAcct = new Account();
                bugsAcct.setUsername("bugs");
                bugsAcct.setEnabled(true);
                bugs.setAccount(bugsAcct);
                Manager donald = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");
                donald.addSubordinate(woody);
                donald.addSubordinate(bugs);
                woody.setManager(donald);
                bugs.setManager(donald);

                pm.makePersistent(donald);

                tx.commit();
                id = JDOHelper.getObjectId(donald);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and committing transaction with detachAllOnCommit : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve an object and check the detached states
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("groupSubordinates");
            pm.getFetchPlan().addGroup("groupA");
            pm.getFetchPlan().addGroup("groupC");
            pm.getFetchPlan().setMaxFetchDepth(3);
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Manager donald = (Manager)pm.getObjectById(id);
                tx.commit();

                Set employees = donald.getSubordinates();
                assertNotNull("Donald was not detached and should have been at commit", donald);
                assertNotNull("Employees of Donald were not detached and should have been at commit", employees);
                assertEquals("Donald Duck has incorrect number of employees after detach at commit", 2, donald.getSubordinates().size());
                Employee bugs = null;
                Employee woody = null;
                Account bugsAcct = null;
                Iterator emplIter = employees.iterator();
                while (emplIter.hasNext())
                {
                    Employee emp = (Employee)emplIter.next();
                    if (emp.getFirstName().equals("Bugs"))
                    {
                        bugs = emp;
                    }
                    else if (emp.getFirstName().equals("Woody"))
                    {
                        woody = emp;
                    }
                }

                assertNotNull("Bugs Bunny was not detached and should have been at commit", bugs);
                assertNotNull("Woody Woodpecker was not detached and should have been at commit", woody);
                bugsAcct = bugs.getAccount();
                assertNotNull("Account of Bugs Bunny was not detached and should have been at commit", bugsAcct);

                // Check that all are now detached
                if (!JDOHelper.isDetached(bugs) || JDOHelper.getObjectId(bugs) == null)
                {
                    fail("Bugs Bunny is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(woody) || JDOHelper.getObjectId(woody) == null)
                {
                    fail("Woody Woodpecker is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(donald) || JDOHelper.getObjectId(donald) == null)
                {
                    fail("Donald Duck is not detached or hasn't been detached correctly after closing the PM");
                }

                // Check that the relationships are intact
                if (!woody.getFirstName().equals("Woody") || !woody.getLastName().equals("Woodpecker"))
                {
                    fail("Woody Woodpecker has lost his name after closing the PM");
                }
                assertNotNull("Woody has a null Manager after detach", woody.getManager());
                assertEquals("Woody has lost the relation to his detached Manager after commit", woody.getManager(), donald);
                if (!bugs.getFirstName().equals("Bugs") || !bugs.getLastName().equals("Bunny"))
                {
                    fail("Bugs Bunny has lost his name after closing the PM");
                }
                assertNotNull("Bugs has a null Manager after detach", bugs.getManager());
                assertEquals("Bugs has lost the relation to his detached Manager after commit", bugs.getManager(), donald);
                if (!donald.getFirstName().equals("Donald") || !donald.getLastName().equals("Duck"))
                {
                    fail("Donald Duck has lost his name after closing the PM");
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while retrieving objects with detachAllOnCommit : " + e.getMessage());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test "DetachAllOnCommit" when we retrieve an object and commit the txn.
     * Slight variation on previous test in that this uses a max fetch depth of -1 so testing for stack overflow
     */
    public void testDetachAllOnCommitViaFetchUnlimited()
    {
        try
        {
            Object id = null;

            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@warnerbros.com", 125, "123409");
                Employee bugs = new Employee(2, "Bugs", "Bunny", "bugs@warnerbros.com", 200, "123410");
                Account bugsAcct = new Account();
                bugsAcct.setUsername("bugs");
                bugsAcct.setEnabled(true);
                bugs.setAccount(bugsAcct);
                Manager donald = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");
                donald.addSubordinate(woody);
                donald.addSubordinate(bugs);
                woody.setManager(donald);
                bugs.setManager(donald);

                pm.makePersistent(donald);

                tx.commit();
                id = JDOHelper.getObjectId(donald);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and committing transaction with detachAllOnCommit : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve an object and check the detached states
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("groupSubordinates");
            pm.getFetchPlan().addGroup("groupA");
            pm.getFetchPlan().addGroup("groupC");
            pm.getFetchPlan().setMaxFetchDepth(-1);
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Manager donald = (Manager)pm.getObjectById(id);
                tx.commit();

                Set employees = donald.getSubordinates();
                assertNotNull("Donald was not detached and should have been at commit", donald);
                assertNotNull("Employees of Donald were not detached and should have been at commit", employees);
                assertEquals("Donald Duck has incorrect number of employees after detach at commit", 2, donald.getSubordinates().size());
                Employee bugs = null;
                Employee woody = null;
                Account bugsAcct = null;
                Iterator emplIter = employees.iterator();
                while (emplIter.hasNext())
                {
                    Employee emp = (Employee)emplIter.next();
                    if (emp.getFirstName().equals("Bugs"))
                    {
                        bugs = emp;
                    }
                    else if (emp.getFirstName().equals("Woody"))
                    {
                        woody = emp;
                    }
                }

                assertNotNull("Bugs Bunny was not detached and should have been at commit", bugs);
                assertNotNull("Woody Woodpecker was not detached and should have been at commit", woody);
                bugsAcct = bugs.getAccount();
                assertNotNull("Account of Bugs Bunny was not detached and should have been at commit", bugsAcct);

                // Check that all are now detached
                if (!JDOHelper.isDetached(bugs) || JDOHelper.getObjectId(bugs) == null)
                {
                    fail("Bugs Bunny is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(woody) || JDOHelper.getObjectId(woody) == null)
                {
                    fail("Woody Woodpecker is not detached or hasn't been detached correctly after closing the PM");
                }
                if (!JDOHelper.isDetached(donald) || JDOHelper.getObjectId(donald) == null)
                {
                    fail("Donald Duck is not detached or hasn't been detached correctly after closing the PM");
                }

                // Check that the relationships are intact
                if (!woody.getFirstName().equals("Woody") || !woody.getLastName().equals("Woodpecker"))
                {
                    fail("Woody Woodpecker has lost his name after closing the PM");
                }
                assertNotNull("Woody has a null Manager after detach", woody.getManager());
                assertEquals("Woody has lost the relation to his detached Manager after commit", woody.getManager(), donald);
                if (!bugs.getFirstName().equals("Bugs") || !bugs.getLastName().equals("Bunny"))
                {
                    fail("Bugs Bunny has lost his name after closing the PM");
                }
                assertNotNull("Bugs has a null Manager after detach", bugs.getManager());
                assertEquals("Bugs has lost the relation to his detached Manager after commit", bugs.getManager(), donald);
                if (!donald.getFirstName().equals("Donald") || !donald.getLastName().equals("Duck"))
                {
                    fail("Donald Duck has lost his name after closing the PM");
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while retrieving objects with detachAllOnCommit : " + e.getMessage());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test "DetachAllOnCommit" when we retrieve objects via query and commit the txn
     */
    public void testDetachAllOnCommitViaQuery()
    {
        try
        {
            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1, "Woody", "Woodpecker", "woody@warnerbros.com", 125, "123409");
                Employee bugs = new Employee(2, "Bugs", "Bunny", "bugs@warnerbros.com", 200, "123410");
                Account bugsAcct = new Account();
                bugsAcct.setUsername("bugs");
                bugsAcct.setEnabled(true);
                bugs.setAccount(bugsAcct);
                Manager donald = new Manager(3, "Donald", "Duck", "donald@warnerbros.com", 400, "123400");
                donald.addSubordinate(woody);
                donald.addSubordinate(bugs);
                woody.setManager(donald);
                bugs.setManager(donald);
                pm.makePersistent(donald);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects and committing transaction with detachAllOnCommit : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve Employees and check the detached states
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("groupSubordinates");
            pm.getFetchPlan().addGroup("groupA");
            pm.getFetchPlan().addGroup("groupC");
            pm.getFetchPlan().setMaxFetchDepth(3);
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(Employee.class);
                Collection results = (Collection)q.execute();
                tx.commit(); // Detach all of Employees on commit

                assertEquals("Number of Employees retrieved is incorrect", 3, results.size());
                Iterator empIter = results.iterator();
                while (empIter.hasNext())
                {
                    Employee emp = (Employee)empIter.next();
                    assertTrue("Employee " + StringUtils.toJVMIDString(emp) + " is not detached!", JDOHelper.isDetached(emp));
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while retrieving objects with detachAllOnCommit : " + e.getMessage());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Tests the detach of duplicates of the same object, and whether we
     * get copies, or a single detached object.
     */
    public void testDetachDuplicates()
    {
        try
        {
            Object listId = null;
            Object[] elmId = new Object[4];
            DetachList detachedList = null;

            // Persist some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                DetachList list = new DetachList("MyList");
                DetachListElement elem1 = new DetachListElement("First");
                DetachListElement elem2 = new DetachListElement("Second");
                list.addElement(elem1);
                list.addElement(elem2);
                list.addElement(elem1);
                list.addElement(elem1);
                pm.makePersistent(list);

                tx.commit();
                listId = pm.getObjectId(list);
                elmId[0] = pm.getObjectId(elem1);
                elmId[1] = pm.getObjectId(elem2);
                elmId[2] = pm.getObjectId(elem1);
                elmId[3] = pm.getObjectId(elem1);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            List detachedAloneElements = new ArrayList();
            // Retrieve the objects and detach them
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DetachList list = (DetachList)pm.getObjectById(listId);
                assertEquals("List persisted has incorrect number of elements", list.getNumberOfElements(), 4);
                detachedAloneElements = new ArrayList(pm.detachCopyAll(new ArrayList(list.getElements())));
                detachedList = (DetachList)pm.detachCopy(list);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while retrieving and detaching objects : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the objects for dups.
            List detachedElements = detachedList.getElements();
            assertEquals("Detached list elements 0 and 2 are not equal!", detachedElements.get(0), detachedElements.get(2));
            assertEquals("Detached list elements 0 and 3 are not equal!", detachedElements.get(0), detachedElements.get(3));
            assertEquals("Detached alone list elements 0 and 2 are not equal!", detachedAloneElements.get(0), detachedAloneElements.get(2));
            assertEquals("Detached alone list elements 0 and 3 are not equal!", detachedAloneElements.get(0), detachedAloneElements.get(3));
            assertNotSame("Detached list elements and detached alone elements are equal!", detachedElements.get(0), detachedAloneElements.get(0));
            assertNotSame("Detached list elements and detached alone elements are equal!", detachedElements.get(1), detachedAloneElements.get(1));
            assertNotSame("Detached list elements and detached alone elements are equal!", detachedElements.get(2), detachedAloneElements.get(2));
            assertNotSame("Detached list elements and detached alone elements are equal!", detachedElements.get(3), detachedAloneElements.get(3));
        }
        finally
        {
            // Clean out our data
            clean(DetachList.class);
            clean(DetachListElement.class);
        }
    }

    /**
     * Test the specification of maximum fetch depth.
     */
    public void testMaxFetchDepth()
    {
        try
        {
            Object e1Id = null;

            // Persist some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e1 = new Employee(1, "Yogi", "Bear", "yogi@warnerbros.com", 124, "10123");
                Employee e2 = new Employee(2, "Fred", "Flintstone", "fred.flintstone@hannabarbara.com", 167, "10019");
                Manager m1 = new Manager(3, "Wily", "Coyote", "wily.coyote@warnerbros.com", 202, "10067");
                Manager m2 = new Manager(4, "Mickey", "Mouse", "mickey.mouse@hollywood.com", 203, "10066");
                Manager m3 = new Manager(5, "Donald", "Duck", "donald.duck@hollywood.com", 204, "10065");
                m1.addSubordinate(e1);
                m1.addSubordinate(e2);
                e1.setManager(m1);
                e2.setManager(m1);
                m2.addSubordinate(m1);
                m1.setManager(m2);
                m3.addSubordinate(m2);
                m2.setManager(m3);
                Department d1 = new Department("Cartoon");
                m1.addDepartment(d1);
                d1.setManager(m1);

                // This should persist all objects
                pm.makePersistent(e1);

                tx.commit();
                e1Id = pm.getObjectId(e1);
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while persisting test data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve and detach some objects using default fetch-depth
            Employee e1Detached = null;
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e1 = (Employee)pm.getObjectById(e1Id);
                e1Detached = (Employee)pm.detachCopy(e1);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while retrieving/detaching test data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the detached objects (Employee.manager is in the DFG)
            try
            {
                // Basic fields of the detached object
                e1Detached.getSerialNo();
                e1Detached.getManager();
            }
            catch (JDODetachedFieldAccessException dfea)
            {
                fail("Detach of Employee has not detached its DFG fields! Should have been detached");
            }
            try
            {
                // Second level relation of the detached object
                e1Detached.getManager().getManager();
                fail("Detach of Employee has also detached Manager of the Manager! Should not have been detached since outside fetch-depth of 1");
            }
            catch (JDODetachedFieldAccessException ndfe)
            {
                // Expected
            }

            // Retrieve and detach some objects using extra level of fetch-depth
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(2);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee e1 = (Employee)pm.getObjectById(e1Id);
                e1Detached = (Employee)pm.detachCopy(e1);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while retrieving/detaching test data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the detached objects
            try
            {
                // Basic fields of the detached object
                e1Detached.getSerialNo();
                e1Detached.getManager();
            }
            catch (JDODetachedFieldAccessException dfea)
            {
                fail("Detach of Employee has not detached its DFG fields! Should have been detached");
            }
            try
            {
                // Basic fields of the detached object
                e1Detached.getManager().getManager();
            }
            catch (JDODetachedFieldAccessException dfea)
            {
                fail("Detach of Employee has not detached Manager of Manager! Should have been detached");
            }
            try
            {
                // Third level relation of the detached object
                e1Detached.getManager().getManager().getManager();
                fail("Detach of Employee has also detached Manager of the Manager of the Manager! Should not have been detached since outside fetch-depth of 1");
            }
            catch (JDODetachedFieldAccessException ndfe)
            {
                // Expected
            }
        }
        finally
        {
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test for recursive relations in fetching (use of recursion-depth)
     */
    public void testFetchRecurse()
    {
        try
        {
            Object dir1id = null;
            Object dir5id = null;
            Directory detachedDir = null;

            // Persist sample data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Directory dir1 = new Directory("/usr"); // Directory /usr
                Directory dir2 = new Directory("/local"); // Directory /usr/local
                dir2.setParent(dir1);
                dir1.addChild(dir2);
                Directory dir3 = new Directory("/audio"); // Directory /usr/local/audio
                dir3.setParent(dir2);
                dir2.addChild(dir3);
                Directory dir4 = new Directory("/mp3"); // Directory /usr/local/audio/mp3
                dir4.setParent(dir3);
                dir3.addChild(dir4);
                Directory dir5 = new Directory("/flac"); // Directory /usr/local/audio/flac
                dir5.setParent(dir3);
                dir3.addChild(dir5);
                pm.makePersistent(dir1);

                tx.commit();
                dir1id = pm.getObjectId(dir1);
                dir5id = pm.getObjectId(dir5);
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating sample data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Test 1 - detach and check the detach from the bottom directory ... checking the parents (unlimited recursion)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Directory dir = (Directory)pm.getObjectById(dir5id);
                pm.getFetchPlan().addGroup("groupA");
                pm.getFetchPlan().setMaxFetchDepth(5); // Max big enough to not cause any limit
                detachedDir = (Directory)pm.detachCopy(dir);
                
                tx.commit();
            }
            catch (Exception e)
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
                fail("Exception thrown while detaching top-down recursive tree : " + e.getMessage());
            }

            try
            {
                // Check that all parents are detached
                Directory parent = detachedDir;
                while (parent != null)
                {
                    parent = parent.getParent();
                }
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Exception thrown while inspecting detached bottom-up recursive tree : " + dfae.getMessage());
            }

            // Test 2 - detach and check the detach from the main parent ... checking the children (limited recursion)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Directory dir = (Directory)pm.getObjectById(dir1id);
                pm.getFetchPlan().addGroup("groupA");
                pm.getFetchPlan().setMaxFetchDepth(5); // Max big enough to not cause any limit
                detachedDir = (Directory)pm.detachCopy(dir);
                
                tx.commit();
            }
            catch (Exception e)
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
                fail("Exception thrown while detaching top-down recursive tree : " + e.getMessage());
            }
            
            try
            {
                // Check that we have the 1st level of children
                HashSet level1children = detachedDir.getChildren();
                Iterator level1childIter = level1children.iterator();
                while (level1childIter.hasNext())
                {
                    Directory child = (Directory)level1childIter.next();
                    child.getParent();

                    try
                    {
                        // Check that we have the 2nd level of children
                        HashSet level2children = child.getChildren();
                        Iterator level2childIter = level2children.iterator();
                        while (level2childIter.hasNext())
                        {
                            Directory grandchild = (Directory)level2childIter.next();

                            try
                            {
                                // Check that we dont have the 3rd level of children
                                HashSet level3children = grandchild.getChildren();
                                Iterator level3childIter = level3children.iterator();
                                while (level3childIter.hasNext())
                                {
                                    Directory greatgrandchild = (Directory)level3childIter.next();
                                    LOG.info(">> Detached 3rd level child " + greatgrandchild.getName()); // Should throw exception
                                    fail("Managed to detach 3rd level of directories children - recursion should have stopped at 2 levels");
                                }
                            }
                            catch (JDODetachedFieldAccessException e)
                            {
                                // To be expected
                            }
                        }
                    }
                    catch (JDODetachedFieldAccessException e)
                    {
                        LOG.error(e);
                        LOG.error("Object threw exception when accessing : " + e.getMessage());
                        fail("One of the objects in a recursive relation that should have been detached hasn't! : " + e.getMessage());
                    }
                }
            }
            catch (JDODetachedFieldAccessException e)
            {
                LOG.error(e);
                LOG.error("Object threw exception when accessing : " + e.getMessage());
                fail("One of the objects in a recursive relation that should have been detached hasn't! : " + e.getMessage());
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
                Extent ext = pm.getExtent(Directory.class, false);
                Iterator it = ext.iterator();
                while (it.hasNext())
                {
                    Directory dir = (Directory) it.next();
                    dir.setParent(null);
                    dir.clearChildren();
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

            clean(Directory.class);
        }
    }

    /**
     * Test of DETACH_LOAD_FIELDS, DETACH_UNLOAD_FIELDS flags.
     */
    public void testDetachLoadUnloadFields()
    {
        try
        {
            Manager detachedM1a = null;
            Manager detachedM1b = null;

            // Persist some data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Employee e1 = new Employee(1, "Yogi", "Bear", "yogi@warnerbros.com", 124, "10123");
                Employee e2 = new Employee(2, "Fred", "Flintstone", "fred.flintstone@hannabarbara.com", 167, "10019");
                Manager m1 = new Manager(3, "Wily", "Coyote", "wily.coyote@warnerbros.com", 202, "10067");
                m1.addSubordinate(e1);
                m1.addSubordinate(e2);
                e1.setManager(m1);
                e2.setManager(m1);
                Department d1 = new Department("Cartoon");
                m1.addDepartment(d1);
                d1.setManager(m1);

                // This should persist all objects
                pm.makePersistent(e1);

                // Detach just the FetchPlan fields
                pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS | FetchPlan.DETACH_UNLOAD_FIELDS);
                detachedM1a = (Manager)pm.detachCopy(m1);

                pm.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
                detachedM1b = (Manager)pm.detachCopy(m1);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                LOG.error(e);
                fail("Exception thrown while persisting test data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check what has been detached - when detaching just fetch plan fields
            try
            {
                detachedM1a.getEmailAddress();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.emailAddress hasn't been detached yet this should have been since was in fetch-plan");
            }
            try
            {
                detachedM1a.getSerialNo();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.serialNo hasn't been detached yet this should have been since was in fetch-plan");
            }

            try
            {
                detachedM1a.getDepartments();
                fail("Field Manager.departments has been detached yet this should not have been since wasn't in fetch-plan");
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                // Expected
            }

            try
            {
                detachedM1a.getSubordinates();
                fail("Field Manager.subordinates has been detached yet this should not have been since wasn't in fetch-plan");
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                // Expected
            }

            // Check what has been detached - when detaching all loaded fields
            try
            {
                detachedM1b.getEmailAddress();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.emailAddress hasn't been detached yet this should have been since was in fetch-plan");
            }
            try
            {
                detachedM1b.getSerialNo();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.serialNo hasn't been detached yet this should have been since was in fetch-plan");
            }

            try
            {
                detachedM1b.getDepartments();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.departments hasn't been detached yet this should have been since was loaded at detach");
            }

            try
            {
                detachedM1b.getSubordinates();
            }
            catch (JDODetachedFieldAccessException dfae)
            {
                fail("Field Manager.subordinates hasn't been detached yet this should have been since was loaded at detach");
            }
        }
        finally
        {
            // Clean out our data
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    /**
     * Test of attaching an object with a collection that has no changed elements and
     * where the elements of the collection are non-PC.
     */
    public void testAttachCleanCollectionWithNonPCElements()
    {
        try
        {
            Object objectId = null;
            ClassWithNonPCCollection obj1 = new ClassWithNonPCCollection();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // test detach and attach
                tx.begin();
                obj1.getElements().add("elem1");
                obj1.getElements().add("elem2");
                pm.makePersistent(obj1);
                tx.commit();
                objectId = pm.getObjectId(obj1);

                // detach
                tx.begin();
                obj1 = (ClassWithNonPCCollection) pm.getObjectById(objectId, true);
                pm.getFetchPlan().addGroup("detach");
                obj1 = (ClassWithNonPCCollection) pm.detachCopy(obj1);
                tx.commit();

                // add a storeLifeCycleListener to check what gets stored
                final Collection storedElements = new ArrayList();
                pm.addInstanceLifecycleListener(new StoreLifecycleListener()
                {
                    public void preStore(InstanceLifecycleEvent event)
                    {
                    }

                    public void postStore(InstanceLifecycleEvent event)
                    {
                        storedElements.add(event.getSource());
                    }
                });

                // attach the unchanged Object
                tx.begin();
                obj1 = (ClassWithNonPCCollection) pm.makePersistent(obj1);
                tx.commit();

                // test that nothing has been stored
                assertTrue(storedElements.isEmpty());

            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            clean(ClassWithNonPCCollection.class);
        }
    }

    /**
     * Test of persisting a new object that has a N-1 bidir relation with a detached object.
     */
    public void testAttachOneManyBidirFromNewManySide()
    {
        try
        {
            Farm detachedFarm = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist the 1 side
                tx.begin();
                Farm farm = new Farm("Sunnybrook Farm");
                pm.makePersistent(farm);
                tx.commit();
                detachedFarm = farm;
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Create the N side and link to the detached 1 side
            Animal animal = new Animal("Porky");
            animal.setFarm(detachedFarm);
            detachedFarm.addAnimal(animal);

            pm = pmf.getPersistenceManager();
            ((JDOPersistenceManager)pm).setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            try
            {
                // Persist from the N side
                tx.begin();
                pm.makePersistent(animal);
                tx.commit();

                // Check the detached results
                assertTrue("Animal should be detached but isnt", JDOHelper.isDetached(animal));
                Farm farm = animal.getFarm();
                assertNotNull("Animal should have its Farm field set but is null", farm);
                assertTrue("Animal.farm should be detached but isnt", JDOHelper.isDetached(farm));
                assertNotNull("Animal.farm.animals should be set but is null", farm.getAnimals());
                assertEquals("Animal.farm.animals has incorrect number of animals", 1, farm.getAnimals().size());
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            clean(Animal.class);
            clean(Farm.class);
        }
    }

    /**
     * Test for use of CopyOnAttach=false with a 1-N bidir relation.
     */
    public void testCopyOnAttachFalseOneToManyBidir()
    {
        try
        {
            Farm farm1 = null;

            // Persist the owner object
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            pm.setCopyOnAttach(false);
            pm.getFetchPlan().setGroup(FetchPlan.ALL).setMaxFetchDepth(2);
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist 
                tx.begin();
                farm1 = new Farm("Sunnybrook Farm");
                pm.makePersistent(farm1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the detached owner with new elements
            assertEquals("Owner object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(farm1));
            Animal dog = new Animal("Patch");
            Animal cat = new Animal("Tabby");
            farm1.addAnimal(dog);
            dog.setFarm(farm1);
            farm1.addAnimal(cat);
            cat.setFarm(farm1);

            // Attach the owner object
            pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            pm.setCopyOnAttach(false);
            pm.getFetchPlan().setGroup(FetchPlan.ALL).setMaxFetchDepth(2);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Farm farm2 = (Farm)pm.makePersistent(farm1);
                pm.flush();
                assertEquals("Attached owner is different object to detached, yet with CopyOnAttach=false should be same",
                    farm1, farm2);
                assertTrue("Farm should be persistent but isnt", JDOHelper.isPersistent(farm1));
                assertTrue("Cat should be persistent but isnt", JDOHelper.isPersistent(cat));
                assertTrue("Dog should be persistent but isnt", JDOHelper.isPersistent(dog));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            assertEquals("Owner object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(farm1));
        }
        finally
        {
            // Clean out our data
            clean(Animal.class);
            clean(Farm.class);
        }
    }

    /**
     * Test for use of CopyOnAttach=false with a 1-1 unidir relation.
     */
    public void testCopyOnAttachFalseOneToOne()
    {
        try
        {
            LoginAccount acct = null;
            Login login = null;
            Object acctId = null;
            Object loginId = null;

            // Persist the owner object
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setDetachAllOnCommit(true);
            pm.setCopyOnAttach(false);
            pm.getFetchPlan().setGroup(FetchPlan.ALL).setMaxFetchDepth(2);
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist 
                tx.begin();
                acct = new LoginAccount("George", "Bush", "bush", "iraq");
                login = acct.getLogin();
                pm.makePersistent(acct);
                tx.commit();
                acctId = pm.getObjectId(acct);
                loginId = pm.getObjectId(login);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the detached objects
            assertEquals("Owner object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(acct));
            assertEquals("Owner object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(login));
            acct.setFirstName("George W");
            login.setPassword("vietnam");

            // Attach the owner object
            pm = pmf.getPersistenceManager();
            pm.setCopyOnAttach(false);
            pm.setDetachAllOnCommit(true);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct2 = (LoginAccount)pm.makePersistent(acct);
                pm.flush();
                assertEquals("Attached LoginAccount is different object to detached, yet with CopyOnAttach=false should be same",
                    acct, acct2);
                assertTrue("LoginAccount should be persistent but isnt", JDOHelper.isPersistent(acct));
                assertTrue("Login should be persistent but isnt", JDOHelper.isPersistent(login));
                tx.commit();
                assertTrue("LoginAccount should not be dirty but is", JDOHelper.isDetached(acct));
                assertFalse("LoginAccount should not be dirty but is", JDOHelper.isDirty(acct));
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Retrieve and check the datastore state
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setGroup(FetchPlan.ALL);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LoginAccount acct2 = (LoginAccount)pm.getObjectById(acctId);
                Login login2 = (Login)pm.getObjectById(loginId);
                assertEquals("Account has incorrect first name", "George W", acct2.getFirstName());
                assertEquals("Account has incorrect last name", "Bush", acct2.getLastName());
                assertEquals("Login has incorrect username", "bush", login2.getUserName());
                assertEquals("Login has incorrect password", "vietnam", login2.getPassword());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            clean(LoginAccount.class);
        }
    }

    /**
     * Test for use of CopyOnAttach=false with a 1-1 unidir relation and where we detach more than 1 of an object
     * and try to attach them in the same txn.
     */
    public void testCopyOnAttachFalseMultipleDetach()
    {
        try
        {
            LoginAccount detachedAcct1 = null;
            LoginAccount detachedAcct2 = null;

            // Persist the owner object
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setCopyOnAttach(false);
            pm.getFetchPlan().setGroup(FetchPlan.ALL).setMaxFetchDepth(2);
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist 
                tx.begin();
                LoginAccount acct = new LoginAccount("George", "Bush", "bush", "iraq");
                pm.makePersistent(acct);

                // Detach 2 copies
                detachedAcct1 = (LoginAccount)pm.detachCopy(acct);
                detachedAcct2 = (LoginAccount)pm.detachCopy(acct);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Update the detached objects
            assertEquals("Account1 object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(detachedAcct1));
            assertEquals("Account2 object is in incorrect state",
                ObjectState.DETACHED_CLEAN, JDOHelper.getObjectState(detachedAcct2));
            detachedAcct1.setFirstName("George W");
            detachedAcct2.setFirstName("George B");

            // Attach the detached objects
            pm = pmf.getPersistenceManager();
            pm.setCopyOnAttach(false);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(detachedAcct1);
                pm.flush();
                try
                {
                    pm.makePersistent(detachedAcct2);
                    LOG.error("Attach of second version of object succeeded!");
                    fail("Attach of second version of an object succeeded but should have thrown exception since CopyOnAttach=false");
                }
                catch (JDOUserException ue)
                {
                    // Expected so rethrow it
                    throw ue;
                }
                tx.commit();
            }
            catch (Exception e)
            {
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
            clean(LoginAccount.class);
        }
    }

    /**
     * Test of detachCopyAll with fetch-depth. See JIRA "NUCCORE-24"
     */
    public void testFetchDepthOnDetachCopyAll()
    {
        try
        {
            Manager bob = new Manager(1, "Bob", "Woodpecker", "bob@woodpecker.com", 13, "serial 1");
            Manager dave = new Manager(1, "Dave", "Woodpecker", "dave@woodpecker.com", 13, "serial 2");
            Employee mary = new Employee(1, "Mary", "Woodpecker", "mary@woodpecker.com", 13, "serial 3");
            mary.setManager(dave);
            dave.setManager(bob);

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                // Save Mary will also save Dave and Bob.
                tx.begin();
                pm.makePersistent(mary);
                tx.commit();

                tx.begin();
                // Group A has firstName and manager, amount other things.
                // No max fetch-depth is defined, so they default to 1.
                pm.getFetchPlan().setGroup("groupA");

                Query query = pm.newQuery(pm.getExtent(Employee.class, true));
                query.setOrdering("firstName descending");
                Collection c = (Collection) query.execute();
                c = pm.detachCopyAll(c);
                tx.commit();

                assertEquals("c.size() == 3", c.size(), 3);
                for (Iterator i = c.iterator(); i.hasNext();)
                {
                    Employee em = (Employee) i.next();
                    try
                    {
                        em.getManager();
                    }
                    catch (Exception e)
                    {
                        fail("Manager must be returned for maxFetchDepth of 1 : employee " + em.getFirstName() + " has no manager");
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
     * Helper method for giving a detached Manager object
     * @param id the manager object id
     * @param fetchPlanName the fetch plan
     * @return a detached Manager
     */
    private Manager getDetachedManager(Object id, String fetchPlanName)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        pm.getFetchPlan().addGroup(fetchPlanName);
        Object detached = null;

        try
        {
            tx.begin();
            detached = pm.detachCopy(pm.getObjectById(id, true));
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail(e.toString());
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();

            pm.close();
        }
        return (Manager) detached;
    }

    /**
     * Helper method for attaching a detached Manager object
     * @param detached
     * @return the attached object
     */
    private Manager attachDetachedManager(Manager detached)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object attached = null;

        try
        {
            tx.begin();
            attached = pm.makePersistent(detached);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail(e.toString());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }
        return (Manager) attached;
    }

    /**
     * Test detach and then attach with no changes, and the effect on lifecycle states.
     */
    public void testDetachAttachWithNoChangeLifecycle()
    {
        try
        {
            // Create object and detach it
            Employee woodyDetached = null;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1",new Integer(10));
                Account acct = new Account();
                acct.setId(101);
                acct.setEnabled(true);
                acct.setUsername("woodyw");
                woody.setAccount(acct);

                pm.makePersistent(woody);
                woodyDetached = (Employee)pm.detachCopy(woody);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.setOptimistic(true); // Delay all persistence ops til flush/commit
            try
            {
                tx.begin();
                Employee woody = pm.makePersistent(woodyDetached);
                Account acct = woody.getAccount();
                assertFalse("Attached object is dirty but shouldn't be", JDOHelper.isDirty(woody));
                assertFalse("Attached Account is dirty but shouldn't be", JDOHelper.isDirty(acct));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.toString());
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }
}