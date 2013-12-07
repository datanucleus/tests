package org.datanucleus.tests;
/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.linkedlist.DoubleLink;
import org.jpox.samples.linkedlist.ParentChildLink;
import org.jpox.samples.linkedlist.SingleLink;
import org.jpox.samples.many_many.AccountCustomer;
import org.jpox.samples.many_many.GasSupplier;
import org.jpox.samples.many_many.OilSupplier;
import org.jpox.samples.many_many.OneOffCustomer;
import org.jpox.samples.many_many.PetroleumCustomer;
import org.jpox.samples.many_many.PetroleumSupplier;
import org.jpox.samples.many_one.unidir.CarRental;
import org.jpox.samples.many_one.unidir.HireCar;
import org.jpox.samples.models.currency.Currency;
import org.jpox.samples.models.currency.Rate;
import org.jpox.samples.models.graph.JdoAttribute;
import org.jpox.samples.models.graph.JdoGraph;
import org.jpox.samples.models.graph.JdoGraphEntity;
import org.jpox.samples.models.graph.JdoGraphEntityClass;
import org.jpox.samples.models.graph.JdoNode;
import org.jpox.samples.models.leftright.LeftBase;
import org.jpox.samples.models.leftright.LeftSub;
import org.jpox.samples.models.leftright.RightBase;
import org.jpox.samples.models.leftright.RightSub;
import org.jpox.samples.one_many.bidir.Animal;
import org.jpox.samples.one_many.bidir.DairyFarm;
import org.jpox.samples.one_many.bidir.Farm;
import org.jpox.samples.one_many.bidir_2.House;
import org.jpox.samples.one_many.bidir_2.Window;
import org.jpox.samples.one_many.bidir_3.Cloud;
import org.jpox.samples.one_many.bidir_3.Sky;
import org.jpox.samples.one_many.collection.ListHolder;
import org.jpox.samples.one_many.collection.PCFKListElement;
import org.jpox.samples.one_many.collection.PCFKListElementShared;
import org.jpox.samples.one_many.collection.PCFKListElementSub1;
import org.jpox.samples.one_many.collection.PCFKListElementSub2;
import org.jpox.samples.one_many.collection.PCFKSetElement;
import org.jpox.samples.one_many.collection.PCFKSetElementShared;
import org.jpox.samples.one_many.collection.PCFKSetElementSub1;
import org.jpox.samples.one_many.collection.PCFKSetElementSub2;
import org.jpox.samples.one_many.collection.PCJoinElement;
import org.jpox.samples.one_many.collection.SetHolder;
import org.jpox.samples.one_many.map.MapFKValueItem;
import org.jpox.samples.one_many.map.MapHolder;
import org.jpox.samples.one_many.map_fk.MapFKHolder;
import org.jpox.samples.one_many.map_fk.MapFKValue;
import org.jpox.samples.one_many.unidir_list.Donation;
import org.jpox.samples.one_many.unidir_list.SoftwareProject;
import org.jpox.samples.one_one.bidir.Boiler;
import org.jpox.samples.one_one.bidir.Timer;
import org.jpox.samples.one_one.bidir_2.DomesticDeliveryAddress;
import org.jpox.samples.one_one.bidir_2.ExpressMail;
import org.jpox.samples.one_one.bidir_2.InternationalDeliveryAddress;
import org.jpox.samples.one_one.bidir_2.Mail;
import org.jpox.samples.one_one.bidir_2.MailDeliveryAddress;
import org.jpox.samples.one_one.bidir_2.PriorityMail;
import org.jpox.samples.one_one.bidir_3.AbstractJournal;
import org.jpox.samples.one_one.bidir_3.ElectronicJournal;
import org.jpox.samples.one_one.bidir_3.PrintJournal;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;

/**
 * Test case to test use of Relationships.
 **/
public class RelationshipTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    boolean manageRelationships = true;

    public RelationshipTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                {
                    SetHolder.class,
                    PCFKSetElement.class,
                    PCFKSetElementSub1.class,
                    PCFKSetElementSub2.class,
                    ListHolder.class,
                    PCFKListElement.class,
                    PCFKListElementSub1.class,
                    PCFKListElementSub2.class,
                    MapHolder.class,
                    MapFKValueItem.class,
                    Farm.class,
                    Animal.class,
                    PetroleumSupplier.class,
                    PetroleumCustomer.class, 
                    GasSupplier.class,
                    OilSupplier.class,
                    OneOffCustomer.class,
                    AccountCustomer.class,
                    LeftBase.class,
                    RightBase.class,
                    LeftSub.class,
                    RightSub.class,
                    MapFKHolder.class, MapFKValue.class,
                    Sky.class, Cloud.class,
                });
            if (storeMgr.supportsValueStrategy("identity"))
            {
                addClassesToSchema(new Class[] {Rate.class, Currency.class});
            }
            initialised = true;
        }
    }

    /**
     * Test case for 1-1 unidirectional relationships.
     **/
    public void test1to1Unidir()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        // Create sample data
        Object acctId = null; 
        try
        {
            tx.begin();

            LoginAccount acct = new LoginAccount("JPOX", "User", "jpox", "password");
            pm.makePersistent(acct);
            acctId = JDOHelper.getObjectId(acct);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Exception thrown while creating 1-1 unidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Retrieve the record and check the data
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            LoginAccount acct = (LoginAccount)pm.getObjectById(acctId);
            assertTrue("LoginAccount \"firstName\" is incorrect",acct.getFirstName().equals("JPOX"));
            assertTrue("LoginAccount \"lastName\" is incorrect",acct.getLastName().equals("User"));

            Login login = acct.getLogin();
            assertEquals("Login \"login\" is incorrect", login.getUserName(), "jpox");
            assertEquals("Login \"password\" is incorrect", login.getPassword(), "password");

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(e);
            fail("Exception thrown while interrogating 1-1 unidirectional relationship data : " + e.getMessage());
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
     * Test case for nulling out a 1-1 bidirectional relationship with deletionPolicy DataNucleus, 
     * where the previous value has been deleted.
     **/
    public void test1to1BidirNullOut()
    throws Exception
    {
        // Create sample data
        Timer timer = null;
        Boiler boiler = null;

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_DELETION_POLICY, "DataNucleus");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            boiler = new Boiler("Baxi", "Superwarm");
            timer = new Timer("Servogas", true, boiler);
            boiler.setTimer(timer);
            pm.makePersistent(boiler);
 
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Error in setting up data for null out test " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }

        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            boiler = timer.getBoiler();
            pm.deletePersistent(boiler); // Boiler goes to P_DELETED state
            // the user should not be forced to obey any order of doing intuitively indepedent things,
            // so should be able to null out link that currently points to deleted object
            timer.setBoiler(null); // Null out link to deleted boiler 

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while nulling out 1-1 bidirectional relationship with deleted old value: " + e.getMessage());
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
     * Test case for 1-1 bidirectional relationships, using single FK (via mapped-by).
     **/
    public void test1to1Bidir()
    throws Exception
    {
        Object idBoiler = null;
        Object idTimer = null;

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        Transaction tx = pm.currentTransaction();
        try
        {
            // Create sample data
            tx.begin();

            Boiler boiler = new Boiler("Baxi", "Superwarm");
            Timer timer = new Timer("Servogas", true, boiler);
            boiler.setTimer(timer);
            pm.makePersistent(boiler);
 
            tx.commit();
            idBoiler = pm.getObjectId(boiler);
            idTimer = pm.getObjectId(timer);
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        pm=pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx=pm.currentTransaction();
        try
        {
            // Retrieve the objects and check the linkage
            tx.begin();

            Boiler boiler = (Boiler)pm.getObjectById(idBoiler, true);
            Timer timer = (Timer)pm.getObjectById(idTimer, true);
            assertEquals(timer, boiler.getTimer());
            assertEquals(boiler, timer.getBoiler());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while retrieving 1-1 bidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Test updating the link from the "mapped-by" side
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Make some updates, including nulling the relation
            tx.begin();

            Timer timer = new Timer("SuperCaliente", false, null);
            Boiler boiler = (Boiler)pm.getObjectById(idBoiler, true);
            boiler.setTimer(timer);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while updating 1-1 bidirectional relationship data : " + e.getMessage());
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
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Retrieve the Boiler and check that the Timer is null
            tx.begin();

            Boiler boiler = (Boiler)pm.getObjectById(idBoiler, true);
            assertTrue("Boiler should have a Timer related, but has none", boiler.getTimer() != null);
            assertTrue("Boiler Timer has wrong make. Should be SuperCaliente, but is " + boiler.getTimer().getMake(), boiler.getTimer().getMake().equals("SuperCaliente"));

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while updating 1-1 bidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Test nulling out the link from the "mapped-by" side
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Make some updates, including nulling the relation
            tx.begin();

            Boiler boiler = (Boiler)pm.getObjectById(idBoiler, true);
            boiler.setTimer(null);
            boiler.setMake("Ravenheat");

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while updating 1-1 bidirectional relationship data : " + e.getMessage());
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
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Retrieve the Boiler and check that the Timer is null
            tx.begin();

            Boiler boiler = (Boiler)pm.getObjectById(idBoiler, true);
            assertTrue("Boiler should have had no Timer related, but has", boiler.getTimer() == null);
            assertTrue("Boiler has wrong make. Should be Ravenheat, but is " + boiler.getMake(), boiler.getMake().equals("Ravenheat"));

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while updating 1-1 bidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Try persisting the 1-1 objects in the opposite order (Timer first)
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Create sample data
            tx.begin();

            Boiler boiler = new Boiler("Baxi", "Superwarm");
            Timer timer = new Timer("Servogas", true, boiler);
            boiler.setTimer(timer);
            pm.makePersistent(timer);
 
            tx.commit();
            idBoiler = pm.getObjectId(boiler);
            idTimer = pm.getObjectId(timer);
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while creating 1-1 bidirectional relationship data the reverse way : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Try persisting the 1-1 objects with a null present
        pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
        tx = pm.currentTransaction();
        try
        {
            // Create sample data
            tx.begin();

            Boiler boiler = new Boiler("Baxi", "Superwarm");
            boiler.setTimer(null);
            pm.makePersistent(boiler);
 
            tx.commit();
            idBoiler = pm.getObjectId(boiler);
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while creating 1-1 bidirectional relationship data with null link : " + e.getMessage());
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

    public void test1to1Bidir_noManagedRelations() throws Exception
    {
        manageRelationships = false;
        try
        {
            test1to1Bidir();
        }
        finally
        {
            manageRelationships = true;
        }
    }

    /**
     * Test case for 1-1 bidirectional relationships with single FK performing various queries
     * across the relation.
     **/
    public void test1to1BidirQuery()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // Clean out classes and create sample data
            tx.begin();
            clean(Timer.class);
            clean(Boiler.class);
            tx.commit();

            tx.begin();
            Boiler boiler = new Boiler("Baxi", "Superwarm");
            Timer timer = new Timer("Servogas", true, boiler);
            boiler.setTimer(timer);
            pm.makePersistent(boiler);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
        }

        // Perform a query on a field in the Boiler side of the relation.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            pm.getFetchPlan().addGroup(FetchPlan.ALL); // Fetch all fields (including FK to Timer)
            Query query = pm.newQuery(Boiler.class);
            query.setFilter("make == \"Baxi\"");
            List results = (List) query.execute();
            Iterator resultsIter = results.iterator();
            while (resultsIter.hasNext())
            {
                resultsIter.next();
            }
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while querying class with mapped-by attribute using fetch plan: " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Try a query using a field in the Boiler
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Timer.class, "boiler.make == \"Baxi\"");
            Collection c = (Collection)q.execute();
            assertEquals(1, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while querying class with FK to related class (1-1 bidir single FK) : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Try a query using a field in the Timer
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Boiler.class, "timer.make == \"Servogas\"");
            Collection c = (Collection)q.execute();
            assertEquals(1, c.size());
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception thrown while querying class with field in related class (1-1 bidir single FK) : " + e.getMessage());
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
     * Test case for 1-1 bidirectional relationships using 2 FK's.
     * This is really tested as BasicQuery and DatastoreId test. 
     **/
    /*public void test1to1BidirectionalMultiFK()
    throws Exception
    {
        try
        {
            Object idUser = null;
            Object idUserDetail = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            
            // Create sample data
            try
            {
                tx.begin();
                
                User user=new User("andy","password");
                UserDetails details=new UserDetails("Andy","Jefferson");
                user.setDetails(details);
                details.setUser(user);
                
                pm.makePersistent(user);
                
                tx.commit();
                idUser = pm.getObjectId(user);
                idUserDetail = pm.getObjectId(details);
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            pm=pmf.getPersistenceManager();
            tx=pm.currentTransaction();
            
            // Create sample data
            try
            {
                tx.begin();
                
                User user=(User)pm.getObjectById(idUser,true);
                UserDetails details=(UserDetails)pm.getObjectById(idUserDetail,true);
                assertEquals(details,user.getUserDetails());
                assertEquals(user,details.getUser());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
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
            clean(User.class);
            clean(UserDetails.class);
        }
    }*/

    /**
     * Test case for 1-1 bidirectional relationships, using single FK (via mapped-by) with inheritance.
     **/
    public void test1to1BidirInheritance()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {MailDeliveryAddress.class, DomesticDeliveryAddress.class, 
                    InternationalDeliveryAddress.class,
                    Mail.class, ExpressMail.class, PriorityMail.class});

            Object mailID[] = new Object[3];
            Object deliveryID[] = new Object[3];
            MailDeliveryAddress d[] = new MailDeliveryAddress[3];
            Mail m[] = new Mail[3];
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create sample data
                tx.begin();
                
                d[0] = new MailDeliveryAddress("d1");
                d[1] = new DomesticDeliveryAddress("d2");
                d[2] = new InternationalDeliveryAddress("d3");
                m[0] = new Mail("m1");
                m[1] = new ExpressMail("m2");
                m[2] = new PriorityMail("m3");
                d[0].setMail(m[0]);
                d[1].setMail(m[1]);
                d[2].setMail(m[2]);
                pm.makePersistentAll(d);
                
                tx.commit();
                mailID[0] = pm.getObjectId(m[0]);
                mailID[1] = pm.getObjectId(m[1]);
                mailID[2] = pm.getObjectId(m[2]);
                deliveryID[0] = pm.getObjectId(d[0]);
                deliveryID[1] = pm.getObjectId(d[1]);
                deliveryID[2] = pm.getObjectId(d[2]);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            pm=pmf.getPersistenceManager();
            tx=pm.currentTransaction();
            
            try
            {
                tx.begin();
                m[0] = (Mail) pm.getObjectById(mailID[0]);
                m[1] = (Mail) pm.getObjectById(mailID[1]);
                m[2] = (Mail) pm.getObjectById(mailID[2]);
                pm.getFetchPlan().setGroups(new String[]{FetchPlan.ALL});
                pm.detachCopyAll(m);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while retrieving 1-1 bidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }        
            pm=pmf.getPersistenceManager();
            tx=pm.currentTransaction();
            
            try
            {
                tx.begin();
                d[0] = (MailDeliveryAddress) pm.getObjectById(deliveryID[0]);
                d[1] = (MailDeliveryAddress) pm.getObjectById(deliveryID[1]);
                d[2] = (MailDeliveryAddress) pm.getObjectById(deliveryID[2]);
                pm.getFetchPlan().setGroups(new String[]{FetchPlan.ALL});
                pm.detachCopyAll(d);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while retrieving 1-1 bidirectional relationship data : " + e.getMessage());
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
                pm.getFetchPlan().addGroup(FetchPlan.ALL); // Fetch all fields (including FK)
                Query query = pm.newQuery(Mail.class);
                List results = (List) query.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    resultsIter.next();
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying class with mapped-by attribute using fetch plan: " + e.getMessage());
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
                pm.getFetchPlan().addGroup(FetchPlan.ALL); // Fetch all fields (including FK)
                Query query = pm.newQuery(MailDeliveryAddress.class);
                List results = (List) query.execute();
                Iterator resultsIter = results.iterator();
                while (resultsIter.hasNext())
                {
                    resultsIter.next();
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying class with mapped-by attribute using fetch plan: " + e.getMessage());
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
            clean(InternationalDeliveryAddress.class);
            clean(DomesticDeliveryAddress.class);
            clean(MailDeliveryAddress.class);
            clean(ExpressMail.class);
            clean(PriorityMail.class);
            clean(Mail.class);
        }
    }
    
    public void test1toNBidirJoin_noManagedRelations() throws Exception
    {
        manageRelationships = false;
        try
        {
            test1toNBidirJoin();
        }
        finally
        {
            manageRelationships = true;
        }
    }
    
    /**
     * Test case for 1-N bidir join table relationship.
     **/
    public void test1toNBidirJoin()
    throws Exception
    {
        try
        {
            Object houseId = null;
            Object[] windowIds = null;
            Object[] deletedWindowIds = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create some data
                House house = new House(16, "Coronation Street");
                Window w1 = new Window(2000, 1500, house);
                Window w2 = new Window(1000, 1500, house);
                Window w3 = new Window(4000, 1000, house);
                house.addWindow(w1);
                house.addWindow(w2);
                house.addWindow(w3);
                pm.makePersistent(house);
                
                houseId = JDOHelper.getObjectId(house);
                windowIds = new Object[3];
                windowIds[0] = JDOHelper.getObjectId(w1);
                windowIds[1] = JDOHelper.getObjectId(w2);
                windowIds[2] = JDOHelper.getObjectId(w3);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Test the retrieval of objects by id
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Check the House
                House house = (House)pm.getObjectById(houseId);
                assertTrue("House was not retrieved via getObjectById", house != null);
                assertTrue("House obtained by getObjectById was incorrect : has wrong street/number", house.getStreet().equals("Coronation Street") && house.getNumber() == 16);
                assertTrue("House obtained by getObjectById was incorrect : has null collection of windows", house.getWindows() != null);
                assertTrue("House obtained by getObjectById was incorrect : has incorrect number of windows : was " + house.getNumberOfWindows() + " but should be 3",
                    house.getNumberOfWindows() == 3);
                
                // Check a Window
                Window window = (Window)pm.getObjectById(windowIds[0]);
                assertTrue("Window was not retrieved via getObjectById", window != null);
                assertTrue("Window obtained by getObjectById was incorrect : has wrong width/height", window.getWidth() == 2000 && window.getHeight() == 1500);
                assertTrue("Window obtained by getObjectById was incorrect : has null house", window.getHouse() != null);
                assertTrue("Window obtained by getObjectById was incorrect : has incorrect House",
                    JDOHelper.getObjectId(window.getHouse()).equals(houseId));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Test add/remove of windows
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Retrieve the House
                House house = (House)pm.getObjectById(houseId);
                
                // Remove 2 windows and replace them with a much larger one
                Window w1 = (Window)pm.getObjectById(windowIds[0]);
                Window w3 = (Window)pm.getObjectById(windowIds[2]);
                house.removeWindow(w1);
                house.removeWindow(w3);
                w1.setHouse(null);
                w3.setHouse(null);
                
                Window w4 = new Window(6000, 1000, house);
                house.addWindow(w4);
                
                tx.commit();
                
                deletedWindowIds = new Object[2];
                deletedWindowIds[0] = windowIds[0];
                deletedWindowIds[1] = windowIds[2];
                
                Object windowId2 = windowIds[1];
                windowIds = new Object[2];
                windowIds[0] = windowId2;
                windowIds[1] = JDOHelper.getObjectId(w4);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while updating 1-N bidirectional Join Table relationship data : " + e.getMessage());
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
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Check the House
                House house = (House)pm.getObjectById(houseId);
                assertTrue("House was not retrieved via getObjectById", house != null);
                assertTrue("House obtained by getObjectById was incorrect : has wrong street/number", house.getStreet().equals("Coronation Street") && house.getNumber() == 16);
                assertTrue("House obtained by getObjectById was incorrect : has null collection of windows", house.getWindows() != null);
                assertTrue("House obtained by getObjectById was incorrect : has incorrect number of windows : was " + house.getNumberOfWindows() + " but should be 2",
                    house.getNumberOfWindows() == 2);
                
                // Check the Windows
                Window w1 = (Window)pm.getObjectById(deletedWindowIds[0]);
                assertTrue("Window was not retrieved via getObjectById", w1 != null);
                assertTrue("Window obtained by getObjectById was incorrect : has wrong width/height", w1.getWidth() == 2000 && w1.getHeight() == 1500);
                assertTrue("Window obtained by getObjectById was incorrect : has house but should be null", w1.getHouse() == null);
                Window w2 = (Window)pm.getObjectById(deletedWindowIds[1]);
                assertTrue("Window was not retrieved via getObjectById", w2 != null);
                assertTrue("Window obtained by getObjectById was incorrect : has wrong width/height", w2.getWidth() == 4000 && w2.getHeight() == 1000);
                assertTrue("Window obtained by getObjectById was incorrect : has house but should be null", w1.getHouse() == null);
                Window w3 = (Window)pm.getObjectById(windowIds[0]);
                assertTrue("Window was not retrieved via getObjectById", w3 != null);
                assertTrue("Window obtained by getObjectById was incorrect : has wrong width/height", w3.getWidth() == 1000 && w3.getHeight() == 1500);
                assertTrue("Window obtained by getObjectById was incorrect : has null house", w3.getHouse() != null);
                assertTrue("Window obtained by getObjectById was incorrect : has incorrect House",
                    JDOHelper.getObjectId(w3.getHouse()).equals(houseId));
                Window w4 = (Window)pm.getObjectById(windowIds[1]);
                assertTrue("Window was not retrieved via getObjectById", w4 != null);
                assertTrue("Window obtained by getObjectById was incorrect : has wrong width/height", w4.getWidth() == 6000 && w4.getHeight() == 1000);
                assertTrue("Window obtained by getObjectById was incorrect : has null house", w4.getHouse() != null);
                assertTrue("Window obtained by getObjectById was incorrect : has incorrect House",
                    JDOHelper.getObjectId(w4.getHouse()).equals(houseId));
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Perform a query on the N side with the 1 side in the fetch group
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getFetchPlan().addGroup(FetchPlan.ALL); // Fetch all fields (including FK to House)
                Query query = pm.newQuery(Window.class);
                query.setFilter("width == 6000");
                List results = (List) query.execute();
                assertEquals(results.size(), 1);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying class at N side using fetch plan to include 1 side : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Perform a query on the N side of the relation referring to the 1 side in the query
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(Window.class);
                query.setFilter("house.street == \"Coronation Street\"");
                List results = (List) query.execute();
                assertEquals(results.size(), 2);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying class at N side of relation : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Persist data from the "N" end.
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create some data
                House house = new House(25, "Albert Road");
                Window w1 = new Window(600, 300, house);
                Window w2 = new Window(800, 300, house);
                Window w3 = new Window(900, 200, house);
                house.addWindow(w1);
                house.addWindow(w2);
                house.addWindow(w3);
                pm.makePersistent(w1);
                
                houseId = JDOHelper.getObjectId(house);
                windowIds = new Object[3];
                windowIds[0] = JDOHelper.getObjectId(w1);
                windowIds[1] = JDOHelper.getObjectId(w2);
                windowIds[2] = JDOHelper.getObjectId(w3);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data from the N side : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Retrieve the objects
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Check the House
                House house = (House)pm.getObjectById(houseId);
                assertTrue("House was not retrieved via getObjectById", house != null);
                assertTrue("House obtained by getObjectById was incorrect : has wrong street/number", house.getStreet().equals("Albert Road") && house.getNumber() == 25);
                assertTrue("House obtained by getObjectById was incorrect : has null collection of windows", house.getWindows() != null);
                assertTrue("House obtained by getObjectById was incorrect : has incorrect number of windows : was " + house.getNumberOfWindows() + " but should be 3",
                    house.getNumberOfWindows() == 3);
   
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidirectional Join Table relationship data : " + e.getMessage());
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
            clean(House.class);
            clean(Window.class);
        }
    }

    public void testDeletionWithInherited1toN() 
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            DairyFarm farm = null;
            Object farmId = null;
            try
            {
                tx.begin();

                farm = new DairyFarm("Animal farm");
                pm.makePersistent(farm);
                farmId = JDOHelper.getObjectId(farm); 
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating Farm: " + e.getMessage());
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
                tx.begin();

                pm.deletePersistent(farm);
                
                tx.commit();
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
                tx.begin();
                assertNull(pm.getObjectById(farmId));
                
                tx.commit();
                fail("Expected JDOObjectNotFoundException thrown while retrieving farm");
            }
            catch (JDOObjectNotFoundException e)
            {
                // should come through here
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
            clean(DairyFarm.class);
        }
    }
    
    public void testMtoN_noManagedRelationships() throws Exception
    {
        manageRelationships = false;
        try
        {
            testMtoN();
        }
        finally
        {
            manageRelationships = true;
        }
    }
    
    /**
     * Test case for M-N relationships.
     **/
    public void testMtoN()
    throws Exception
    {
        try
        {
            Object[] supplier_ids=null;
            Object[] customer_ids=null;

            PersistenceManager pm=pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();

                // Create a few Suppliers
                PetroleumSupplier supplier1 = new PetroleumSupplier("Smegma Enterprises");
                pm.makePersistent(supplier1);
                PetroleumSupplier supplier2 = new PetroleumSupplier("Amazonia");
                pm.makePersistent(supplier2);
                PetroleumSupplier supplier3 = new PetroleumSupplier("Chorus plc");
                pm.makePersistent(supplier3);
                PetroleumSupplier supplier4 = new PetroleumSupplier("Hnos Hernandez s.a.");
                pm.makePersistent(supplier4);

                supplier_ids = new Object[4];
                supplier_ids[0] = JDOHelper.getObjectId(supplier1);
                supplier_ids[1] = JDOHelper.getObjectId(supplier2);
                supplier_ids[2] = JDOHelper.getObjectId(supplier3);
                supplier_ids[3] = JDOHelper.getObjectId(supplier4);

                // Create a few Customers
                PetroleumCustomer customer1 = new PetroleumCustomer("Joe Smith");
                pm.makePersistent(customer1);
                PetroleumCustomer customer2 = new PetroleumCustomer("Juan Fernandez");
                pm.makePersistent(customer2);
                PetroleumCustomer customer3 = new PetroleumCustomer("Marie-Claude Bezier");
                pm.makePersistent(customer3);

                customer_ids = new Object[3];
                customer_ids[0] = JDOHelper.getObjectId(customer1);
                customer_ids[1] = JDOHelper.getObjectId(customer2);
                customer_ids[2] = JDOHelper.getObjectId(customer3);

                // Assign a few relationships
                // Since we are sharing the join table we add only once
                supplier1.addCustomer(customer1);
                supplier1.addCustomer(customer3);
                supplier2.addCustomer(customer1);
                supplier4.addCustomer(customer2);
                supplier4.addCustomer(customer3);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve a few customers/suppliers and check the data
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                int[] supplier_customer_numbers=new int[] {2,1,0,2};
                for (int i=0;i<4;i++)
                {
                    PetroleumSupplier supplier=(PetroleumSupplier)pm.getObjectById(supplier_ids[i],true);

                    Collection customers=supplier.getCustomers();
                    assertTrue("Supplier " + (i+1) + " has wrong number of customers (" + customers.size() + ") : should have been " + supplier_customer_numbers[i],customers.size() == supplier_customer_numbers[i]);

                    Iterator iter=customers.iterator();
                    while (iter.hasNext())
                    {
                        PetroleumCustomer customer = (PetroleumCustomer)iter.next();
                        int no_of_suppliers = customer.getNoOfSuppliers();
                        if (JDOHelper.getObjectId(customer) == customer_ids[0])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                        }
                        else if (JDOHelper.getObjectId(customer) == customer_ids[1])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 1",no_of_suppliers == 1);
                        }
                        else if (JDOHelper.getObjectId(customer) == customer_ids[2])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                        }
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while interrogating M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Remove a few relationships.
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                PetroleumSupplier supplier4 = (PetroleumSupplier)pm.getObjectById(supplier_ids[3],true);

                Collection customers = supplier4.getCustomers();
                assertTrue("Supplier has wrong number of customers (" + customers.size() + ") : should have been 2",customers.size() == 2);

                Iterator iter=customers.iterator();
                int i=0;
                while (iter.hasNext())
                {
                    PetroleumCustomer customer = (PetroleumCustomer)iter.next();
                    int      no_of_suppliers=customer.getNoOfSuppliers();
                    if (JDOHelper.getObjectId(customer) == customer_ids[0])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                    }
                    else if (JDOHelper.getObjectId(customer) == customer_ids[1])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 1",no_of_suppliers == 1);
                    }
                    else if (JDOHelper.getObjectId(customer) == customer_ids[2])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                    }

                    // Delete the first customer
                    if (i == 0)
                    {
                        supplier4.removeCustomer(customer);
                    }

                    i++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while removing M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve supplier and check new number of customers
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            pm.getFetchPlan().addGroup("customer_default");
            pm.getFetchPlan().addGroup("supplier_default");
            pm.getFetchPlan().addGroup("delivery_default");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PetroleumSupplier supplier4 = (PetroleumSupplier)pm.getObjectById(supplier_ids[3],true);

                Collection customers=supplier4.getCustomers();
                assertTrue("Supplier 4 has wrong number of customers (" + customers.size() + ") : should have been 1",customers.size() == 1);
                Iterator iter = customers.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while interrogating M-N relationship data : " + e.getMessage());
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
            clean(PetroleumCustomer.class);
            clean(PetroleumSupplier.class);
        }
    }
    
    /**
     * Test case for M-N relationships.
     **/
    public void testMtoNInheritance()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {PetroleumSupplier.class, PetroleumCustomer.class, 
                    GasSupplier.class, OilSupplier.class,
                    OneOffCustomer.class, AccountCustomer.class});

            Object[] supplier_ids=null;
            Object[] customer_ids=null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few Suppliers
                PetroleumSupplier supplier1 = new GasSupplier("Smegma Enterprises");
                pm.makePersistent(supplier1);
                PetroleumSupplier supplier2 = new GasSupplier("Amazonia");
                pm.makePersistent(supplier2);
                PetroleumSupplier supplier3 = new OilSupplier("Chorus plc");
                pm.makePersistent(supplier3);
                PetroleumSupplier supplier4 = new OilSupplier("Hnos Hernandez s.a.");
                pm.makePersistent(supplier4);
                
                supplier_ids = new Object[4];
                supplier_ids[0] = JDOHelper.getObjectId(supplier1);
                supplier_ids[1] = JDOHelper.getObjectId(supplier2);
                supplier_ids[2] = JDOHelper.getObjectId(supplier3);
                supplier_ids[3] = JDOHelper.getObjectId(supplier4);
                
                // Create a few Customers
                PetroleumCustomer customer1 = new OneOffCustomer("Joe Smith");
                pm.makePersistent(customer1);
                PetroleumCustomer customer2 = new PetroleumCustomer("Juan Fernandez");
                pm.makePersistent(customer2);
                PetroleumCustomer customer3 = new AccountCustomer("Marie-Claude Bezier");
                pm.makePersistent(customer3);
                
                customer_ids = new Object[3];
                customer_ids[0] = JDOHelper.getObjectId(customer1);
                customer_ids[1] = JDOHelper.getObjectId(customer2);
                customer_ids[2] = JDOHelper.getObjectId(customer3);
                
                // Assign a few relationships
                // Since we are sharing the join table we add only once
                supplier1.addCustomer(customer1);
                supplier1.addCustomer(customer3);
                supplier2.addCustomer(customer1);
                supplier4.addCustomer(customer2);
                supplier4.addCustomer(customer3);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve a few customers/suppliers and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                int[] supplier_customer_numbers=new int[] {2,1,0,2};
                for (int i=0;i<4;i++)
                {
                    PetroleumSupplier supplier=(PetroleumSupplier)pm.getObjectById(supplier_ids[i],true);
                    
                    Collection customers=supplier.getCustomers();
                    assertTrue("Supplier " + (i+1) + " has wrong number of customers (" + customers.size() + ") : should have been " + supplier_customer_numbers[i],customers.size() == supplier_customer_numbers[i]);
                    
                    Iterator iter=customers.iterator();
                    while (iter.hasNext())
                    {
                        PetroleumCustomer customer=(PetroleumCustomer)iter.next();
                        int      no_of_suppliers=customer.getNoOfSuppliers();
                        if (JDOHelper.getObjectId(customer) == customer_ids[0])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                        }
                        else if (JDOHelper.getObjectId(customer) == customer_ids[1])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 1",no_of_suppliers == 1);
                        }
                        else if (JDOHelper.getObjectId(customer) == customer_ids[2])
                        {
                            assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                        }
                    }
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while interrogating M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Remove a few relationships.
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                PetroleumSupplier supplier4=(PetroleumSupplier)pm.getObjectById(supplier_ids[3],true);
                
                Collection customers=supplier4.getCustomers();
                assertTrue("Supplier has wrong number of customers (" + customers.size() + ") : should have been 2",customers.size() == 2);
                
                Iterator iter=customers.iterator();
                int i=0;
                while (iter.hasNext())
                {
                    PetroleumCustomer customer=(PetroleumCustomer)iter.next();
                    int      no_of_suppliers=customer.getNoOfSuppliers();
                    if (JDOHelper.getObjectId(customer) == customer_ids[0])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                    }
                    else if (JDOHelper.getObjectId(customer) == customer_ids[1])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 1",no_of_suppliers == 1);
                    }
                    else if (JDOHelper.getObjectId(customer) == customer_ids[2])
                    {
                        assertTrue("Customer has wrong number of suppliers (" + no_of_suppliers + ") : should have been 2",no_of_suppliers == 2);
                    }
                    
                    // Delete the first customer
                    if (i == 0)
                    {
                        supplier4.removeCustomer(customer);
                    }
                    
                    i++;
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while removing M-N relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve supplier and check new number of customers
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("customer_default");
            pm.getFetchPlan().addGroup("supplier_default");
            pm.getFetchPlan().addGroup("delivery_default");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PetroleumSupplier supplier4=(PetroleumSupplier)pm.getObjectById(supplier_ids[3],true);
                
                Collection customers=supplier4.getCustomers();
                assertTrue("Supplier 4 has wrong number of customers (" + customers.size() + ") : should have been 1",customers.size() == 1);
                Iterator iter = customers.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while interrogating M-N relationship data : " + e.getMessage());
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
            clean(GasSupplier.class);
            clean(OilSupplier.class);
            clean(PetroleumSupplier.class);
            clean(OneOffCustomer.class);
            clean(AccountCustomer.class);
            clean(PetroleumCustomer.class);
        }
    }

    /**
     * Test case for 1-1 inheritance relationships.
     * TODO Is this JoinTable, or FK - change name of test to suit
     **/
    public void test1toNInheritance()
    throws Exception
    {
        try
        {
            //prepare data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();        
                LeftBase base1 = new LeftBase(1);
                LeftBase base2 = new LeftBase(2);
                LeftBase base3 = new LeftBase(3);
                LeftSub group1 = new LeftSub(4, new LeftBase[] { base1, base2 });
                LeftSub group2 = new LeftSub(5, new LeftBase[] { group1, base3 });
                RightSub gr1 = new RightSub(1, group2);
                pm.makePersistent(gr1);
                tx.commit();
                id = pm.getObjectId(gr1);
            }
            catch( Exception e )
            {
                LOG.error("Exception in test", e);
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

            //test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                RightSub gr2 = (RightSub) pm.getObjectById(id,true);

                assertEquals("Expect id == 5",5,gr2.getBase().getId());
                assertEquals("Expect Group class instance.",LeftSub.class,gr2.getBase().getClass());

                assertEquals("Expect 2 members",2,((LeftSub)gr2.getBase()).getMembers().size());

                List members = ((LeftSub)gr2.getBase()).getMembers();
                assertEquals("Expect id == 4",4,((LeftBase)members.get(0)).getId());
                assertEquals("Expect id == 3",3,((LeftBase)members.get(1)).getId());

                assertEquals("Expect Group class instance in member.",LeftSub.class,members.get(0).getClass());
                assertEquals("Expect Base class instance in member.",LeftBase.class,members.get(1).getClass());

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
            // Clean out all data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.begin();
            for (Iterator iter = ((Collection) pm.newQuery(LeftSub.class).execute()).iterator(); iter.hasNext();)
            {
                LeftSub grp = (LeftSub)iter.next();
                grp.getMembers().clear();
            }
            tx.commit();
            pm.close();

            clean(RightSub.class);
            clean(RightBase.class);
            clean(LeftSub.class);
            clean(LeftBase.class);
        }
    }

    /**
     * Test case for ordering with inheritance involved.
     * (This test is included in RelationshipTest because it uses
     * the same classes as another "real" relationship test.)
     **/
    public void testQueryWithInheritanceAndOrdering()
    throws Exception
    {
        try
        {
            //prepare data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();        
                LeftBase baseA = new LeftBase(1);
                LeftSub groupB = new LeftSub(3, new LeftBase[] { });
                LeftBase baseC = new LeftBase(2);
                LeftSub groupD = new LeftSub(4, new LeftBase[] { });
                baseA.setName("aaa");
                groupB.setName("bbb");
                baseC.setName("ccc");
                groupD.setName("ddd");
                pm.makePersistent(baseA);
                pm.makePersistent(baseC);
                pm.makePersistent(groupB);
                pm.makePersistent(groupD);
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
            //test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(pm.getExtent(LeftBase.class, true));
                query.setOrdering("name ascending");
                Collection result = (Collection)query.execute();
                Iterator iter = result.iterator();
                {
                    LeftBase base = (LeftBase)iter.next();
                    assertEquals("aaa", base.getName());
                }
                {
                    LeftBase base = (LeftBase)iter.next();
                    assertEquals("bbb", base.getName());
                }
                {
                    LeftBase base = (LeftBase)iter.next();
                    assertEquals("ccc", base.getName());
                }
                {
                    LeftBase base = (LeftBase)iter.next();
                    assertEquals("ddd", base.getName());
                }
                assertFalse("result set unexpectedly too large.", iter.hasNext());
                query.closeAll();
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
            // Clean out all data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.begin();
            for (Iterator iter = ((Collection) pm.newQuery(LeftSub.class).execute()).iterator(); iter.hasNext();)
            {
                LeftSub grp = (LeftSub)iter.next();
                grp.getMembers().clear();
            }
            tx.commit();
            pm.close();

            clean(RightSub.class);
            clean(RightBase.class);
            clean(LeftSub.class);
            clean(LeftBase.class);
        }
    }

    /**
     * Test case for a bidirectional 1-1 relationship to a class using "superclass-table" inheritance strategy,
     * with both classes involved being mapped to the same superclass table.
     **/
    public void test1to1BidirInheritanceSuperclassTable()
    throws Exception
    {
        try
        {
            // Create the necessary schema
            try
            {
                addClassesToSchema(new Class[]
                   {
                        AbstractJournal.class,
                        PrintJournal.class,
                        ElectronicJournal.class
                   });
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail ("Exception thrown while adding classes for 1-1 relation using superclass-table : " + e.getMessage());
            }

            // Check the persistence of data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object pjId = null;
            Object ejId = null;
            try
            {
                tx.begin();
                PrintJournal pj = new PrintJournal(1, "The PrintJournal");
                ElectronicJournal ej = new ElectronicJournal(2, "The ElectronicJournal");
                pm.makePersistent(pj);
                pm.makePersistent(ej);
                
                pj.setElectronicJournal(ej);
                ej.setPrintJournal(pj);
                
                tx.commit();
                pjId = pm.getObjectId(pj);
                ejId = pm.getObjectId(ej);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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

            // Check the retrieval of the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PrintJournal pj = (PrintJournal) pm.getObjectById(pjId, true);
                ElectronicJournal ej = (ElectronicJournal) pm.getObjectById(ejId, true);
                assertTrue("ej.getPrintJournal() must not be null", ej.getPrintJournal()!=null);
                assertTrue("pj.getElectronicJournal() must not be null", pj.getElectronicJournal()!=null);
                
                assertTrue("PrintJournal has wrong associated ElectronicJournal", pj.getElectronicJournal()==ej);
                assertTrue("ElectronicJournal has wrong associated PrintJournal", ej.getPrintJournal()==pj);
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

            // Check a query
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q1 = pm.newQuery(PrintJournal.class, "electronicJournal != null");
                List results1 = (List)q1.execute();
                assertEquals("Number of PrintJournals with ElectronicJournal was incorrect", results1.size(), 1);

                Query q2 = pm.newQuery(ElectronicJournal.class, "printJournal != null");
                List results2 = (List)q2.execute();
                assertEquals("Number of ElectronicJournals with PrintJournal was incorrect", results2.size(), 1);

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
            // Clean out all data
            clean(PrintJournal.class);
            clean(ElectronicJournal.class);
        }
    }

    /**
     * Test case for 1-N bidir relationships + 1-1 unidir relationships
     **/
    public void test1toNBidir_1to1Unidir()
    throws Exception
    {
        // This test currently relies on identity fields
        if (!storeMgr.supportsValueStrategy("identity"))
        {
            // Lets just say it passed :-)
            return;
        }

        try
        {
            // prepare data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object idEUR = null;
            Object idUSD = null;
            Object idCHF = null;
            try
            {
                tx.begin();
                List cs = new ArrayList();
                Currency chf = new Currency("CHF");
                Currency eur = new Currency("EUR");
                Currency usd = new Currency("USD");
                cs.add(chf);
                cs.add(eur);
                cs.add(usd);

                List c = new ArrayList();
                c.add(new Rate(usd, eur, 0.9));
                c.add(new Rate(usd, chf, 1.3));
                usd.setRates(c);
                c = new ArrayList();
                c.add(new Rate(eur, chf, 1.5));
                c.add(new Rate(eur, usd, 1.1));
                eur.setRates(c);
                c = new ArrayList();
                c.add(new Rate(chf, eur, 0.7));
                c.add(new Rate(chf, usd, 0.8));
                chf.setRates(c);

                pm.makePersistentAll(cs);
                tx.commit();
                idEUR = pm.getObjectId(eur);
                idUSD = pm.getObjectId(usd);
                idCHF = pm.getObjectId(chf);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bi-directional relationships + 1-1 unidirecional relationships data : " + e.getMessage());
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
                Currency chf = (Currency) pm.getObjectById(idCHF, true);
                Currency eur = (Currency) pm.getObjectById(idEUR, true);
                Currency usd = (Currency) pm.getObjectById(idUSD, true);

                assertEquals("CHF", "CHF", chf.getCurrencyCode());
                assertEquals("CHF", 2, chf.getRates().size());
                assertEquals("CHF", "EUR", ((Rate) chf.getRates().get(0)).getTarget().getCurrencyCode());
                assertEquals("CHF", 0.7, ((Rate) chf.getRates().get(0)).getRate(), 0);
                assertEquals("CHF", "USD", ((Rate) chf.getRates().get(1)).getTarget().getCurrencyCode());
                assertEquals("CHF", 0.8, ((Rate) chf.getRates().get(1)).getRate(), 0);

                assertEquals("EUR", "EUR", eur.getCurrencyCode());
                assertEquals("EUR", 2, eur.getRates().size());
                assertEquals("EUR", "CHF", ((Rate) eur.getRates().get(0)).getTarget().getCurrencyCode());
                assertEquals("EUR", 1.5, ((Rate) eur.getRates().get(0)).getRate(), 0);
                assertEquals("EUR", "USD", ((Rate) eur.getRates().get(1)).getTarget().getCurrencyCode());
                assertEquals("EUR", 1.1, ((Rate) eur.getRates().get(1)).getRate(), 0);

                assertEquals("USD", "USD", usd.getCurrencyCode());
                assertEquals("USD", 2, usd.getRates().size());
                assertEquals("USD", "EUR", ((Rate) usd.getRates().get(0)).getTarget().getCurrencyCode());
                assertEquals("USD", 0.9, ((Rate) usd.getRates().get(0)).getRate(), 0);
                assertEquals("USD", "CHF", ((Rate) usd.getRates().get(1)).getTarget().getCurrencyCode());
                assertEquals("USD", 1.3, ((Rate) usd.getRates().get(1)).getRate(), 0);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bi-directional relationships + 1-1 unidirecional relationships data : " + e.getMessage());
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
            // Clean out any created data
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
    }

    /**
     * Test case for 1-N unidir using FK.
     **/
    public void test1toNunidirFK()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                // Create some data
                SetHolder holder = new SetHolder("First");
                PCFKSetElement elem1 = new PCFKSetElement("Element 1");
                PCFKSetElement elem2 = new PCFKSetElement("Element 2");
                holder.getFkSetPC().add(elem1);
                holder.getFkSetPC().add(elem2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N uni FK relationships : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the retrieval of the owner and elements
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                SetHolder holder = (SetHolder)pm.getObjectById(holderId);
                assertNotNull("Unable to retrieve container object for 1-N uni FK relationship", holder);
                Collection elements = holder.getFkSetPC();
                assertNotNull("Elements in holder are null!", elements);
                assertEquals("Number of elements in holder is incorrect", 2, elements.size());
                Iterator roomsIter = elements.iterator();
                while (roomsIter.hasNext())
                {
                    roomsIter.next();
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N uni FK relationships : " + e.getMessage());
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
            // Clean out our data
            clean(SetHolder.class);
            clean(PCFKSetElement.class);
        }
    }

    /**
     * Test case for 1-N inverse unidirectional to itself.
     * Hotel has a collection of Hotels. innerHotels knows nothing about Hotel owner.
     * This should add a FK on the Hotel table.
     **/
    public void test1toNUnidirFKToSelf()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holder1Id = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                SetHolder holder1 = new SetHolder("First");
                SetHolder holder2 = new SetHolder("Second");
                SetHolder holder3 = new SetHolder("Third");
                pm.makePersistent(holder1);
                pm.makePersistent(holder2);
                pm.makePersistent(holder3);
                holder1.getFkSetPC2().add(holder2);
                holder1.getFkSetPC2().add(holder3);

                tx.commit();
                holder1Id = pm.getObjectId(holder1);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N unidir FK self-referring relationships : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pm.close();
            pm = pmf.getPersistenceManager();
            
            // Check the retrieval of the owner and elements
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                SetHolder holder = (SetHolder)pm.getObjectById(holder1Id, true);
                assertNotNull("Unable to retrieve container object for 1-N unidir FK relationship", holder);
                
                Collection innerHolders = holder.getFkSetPC2();
                assertEquals("Number of elements in 1-N unidir FK relationship is incorrect", 2, innerHolders.size());
                Iterator iter = innerHolders.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N unidir FK self-referring relationships : " + e.getMessage());
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
            // Clean out our data
            clean(SetHolder.class);
        }
    }

    /**
     * Test case for 1-N inverse unidirectional, using a List.
     **/
    public void test1toNUnidirFKList()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();
                
                // Create some data
                ListHolder holder = new ListHolder();
                PCFKListElement elem1 = new PCFKListElement("Element 1");
                PCFKListElement elem2 = new PCFKListElement("Element 2");
                holder.getFkListPC().add(elem1);
                holder.getFkListPC().add(elem2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N unidir FK relationships (List) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            // Check the retrieval of the owner and elements
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                assertNotNull("Unable to retrieve container object for 1-N unidir FK relationship (List)", holder);

                Collection elements = holder.getFkListPC();
                assertNotNull("Elements in Holder 1-N unidir FK List is null", elements);
                assertEquals("Number of elements in 1-N unidir FK relationship (List) is wrong", 2, elements.size());
                Iterator iter = elements.iterator();
                int i = 0;
                while (iter.hasNext())
                {
                    PCFKListElement elem = (PCFKListElement)iter.next();
                    if (i == 0)
                    {
                        assertEquals("The first element in the List is wrong", elem.getName(), "Element 1");
                    }
                    else if (i == 1)
                    {
                        assertEquals("The second element in the List is wrong", elem.getName(), "Element 2");
                    }
                    i++;
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while querying 1-N unidir FK relationships (List) : " + e.getMessage());
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
            // Clean out our data
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }

    /**
     * Test case for 1-N inverse unidirectional, using a List.
     **/
    public void test1toNUnidirFKListToSelf()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holder1Id = null;
            Object holder2Id = null;
            Object holder3Id = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                ListHolder holder1 = new ListHolder();
                ListHolder holder2 = new ListHolder();
                ListHolder holder3 = new ListHolder();
                holder1.getFkListPC2().add(holder2);
                holder1.getFkListPC2().add(holder3);
                pm.makePersistent(holder1);
                
                tx.commit();
                holder1Id = pm.getObjectId(holder1);
                holder2Id = pm.getObjectId(holder2);
                holder3Id = pm.getObjectId(holder3);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N unidir FK self-referring relationships (List) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            // Check the retrieval of the owner and elements
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                ListHolder holder = (ListHolder)pm.getObjectById(holder1Id);
                assertNotNull("Unable to retrieve container object for 1-N unidir FK relationship (List)", holder);
                
                Collection innerHolders = holder.getFkListPC2();
                assertEquals("Number of elements in 1-N unidir FK relationship (List) is incorrect", 2, innerHolders.size());
                Iterator iter = innerHolders.iterator();
                int i = 0;
                while (iter.hasNext())
                {
                    ListHolder innerHolder = (ListHolder)iter.next();
                    if (i == 0)
                    {
                        assertEquals("The first element in the List is wrong", 
                            holder2Id, JDOHelper.getObjectId(innerHolder));
                    }
                    else if (i == 1)
                    {
                        assertEquals("The second element in the List is wrong", 
                            holder3Id, JDOHelper.getObjectId(innerHolder));
                    }
                    i++;
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N unidir FK relationships (List) : " + e.getMessage());
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
            // Clean out our data
            clean(ListHolder.class);
        }
    }

    /**
     * Test case for 1-N unidir FK, using a Map<String, PC>.
     **/
    public void test1toNUnidirFKMap()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();
                
                // Create some data
                MapHolder holder = new MapHolder("First");
                MapFKValueItem value1 = new MapFKValueItem("Value 1", "First value", "1");
                MapFKValueItem value2 = new MapFKValueItem("Value 2", "Second value", "2");
                holder.getFkMapKey().put(value1.getKey(), value1);
                holder.getFkMapKey().put(value2.getKey(), value2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N unidir FK relationships (Map) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the retrieval of the owner and values
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                MapHolder holder = (MapHolder)pm.getObjectById(holderId);
                assertNotNull("Unable to retrieve container object for 1-N unidir FK relationship (Map)", holder);

                Collection values = holder.getFkMapKey().values();
                Iterator iter = values.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                assertEquals("Number of values in 1-N unidir FK relationship (Map) is wrong", 2, values.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N unidir FK relationships (Map) : " + e.getMessage());
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
            // Clean out our data
            clean(MapHolder.class);
            clean(MapFKValueItem.class);
        }
    }

    /**
     * Test case for 1-N inverse unidirectional, using a Map.
     * Hotel has a collection of hotels. innerHotels knows nothing about Hotel.
     * This should add a FK on the Hotel table.
     **/
    public void test1toNUnidirFKMapToSelf()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holder1Id = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                MapHolder holder1 = new MapHolder("First");
                MapHolder holder2 = new MapHolder("Second");
                MapHolder holder3 = new MapHolder("Third");
                holder1.getFkMapKey2().put(holder2.getName(), holder2);
                holder1.getFkMapKey2().put(holder3.getName(), holder3);
                pm.makePersistent(holder1);

                tx.commit();
                holder1Id = pm.getObjectId(holder1);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N unidir FK self-referring relationships (Map) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            
            // Check the retrieval of the owner and values
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                MapHolder holder = (MapHolder)pm.getObjectById(holder1Id);
                assertNotNull("Unable to retrieve container object for 1-N unidir FK relationship (Map)", holder);

                Collection innerHolders = holder.getFkMapKey2().values();
                assertEquals("Number of values in 1-N unidir FK relationship (Map) is incorrect", 2, innerHolders.size());
                Iterator iter = innerHolders.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N unidir FK relationships (Map) : " + e.getMessage());
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
            // Clean out our data
            clean(MapHolder.class);
        }
    }

    public void test1toNBidirFk_noManagedRelations() throws Exception
    {
        manageRelationships = false;
        try
        {
            test1toNBidirFK();
        }
        finally
        {
            manageRelationships = true;
        }
    }
    
    /**
     * Test case for 1-N bidir FK relations.
     **/
    public void test1toNBidirFK()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_MANAGE_RELATIONSHIPS, "" + manageRelationships);
            Transaction tx = pm.currentTransaction();
            Object farmId = null;
            
            // Check the persistence of owner and elements
            try
            {
                tx.begin();
                
                // Create some data
                Farm farm = new Farm("Giles Farm");
                Animal animal1 = new Animal("Shep");
                Animal animal2 = new Animal("Grunter");
                farm.addAnimal(animal1);
                farm.addAnimal(animal2);
                animal1.setFarm(farm);
                animal2.setFarm(farm);
                pm.makePersistent(farm);
                
                tx.commit();
                farmId = pm.getObjectId(farm);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating 1-N bidir FK relationships : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Check the retrieval of owner and elements
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Farm farm = (Farm)pm.getObjectById(farmId);
                assertNotNull("Unable to retrieve container object for 1-N bidir FK relationship", farm);

                Collection animals = farm.getAnimals();
                assertNotNull("Holder of elements with 1-N bidir FK relation is null!", farm);
                assertEquals("Number of elements in 1-N bidir FK relationship is wrong", 2, animals.size());
                Iterator iter = animals.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N bidir FK relationships : " + e.getMessage());
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
            // Clean out our data
            clean(Farm.class);
            clean(Animal.class);
        }
    }

    /**
     * Test 1toN on Map.
     */
    public void test1toNMap()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            JdoGraph g = null;
            JdoAttribute attrName = null;
            JdoAttribute attrKey = null;
            
            // Persist some objects
            try
            {
                tx.begin();
                
                // Create graph entity classes an make them persistent.
                JdoGraphEntityClass gcA = new JdoGraphEntityClass("GC_A");
                JdoGraphEntityClass ncA = new JdoGraphEntityClass("NC_A");
                pm.makePersistent(gcA);
                pm.makePersistent(ncA);
                
                // Declare two attributes for NC_A
                attrName = ncA.declareAttribute("name", String.class);
                attrKey = ncA.declareAttribute("key", Integer.class);
                
                // Create a graph and make it persistent.
                g = new JdoGraph(gcA);
                pm.makePersistent(g);
                
                // Create a couple of nodes and store some attribute in them.
                JdoNode n1 = g.createNode(gcA);
                n1.putAttributeValue(attrName, "John");
                n1.putAttributeValue(attrKey, new Integer(1));
                
                JdoNode n2 = g.createNode(gcA);
                n2.putAttributeValue(attrName, "John");
                n2.putAttributeValue(attrKey, new Integer(1));
                
                JdoNode n3 = g.createNode(gcA);
                n3.putAttributeValue(attrName, "Smith ");
                n3.putAttributeValue(attrKey, new Integer(1));
                //attrName = (JdoAttribute)pm.detachCopy(attrName);

                pm.flush();

                // Query for the attribute
                Query q = pm.newQuery(JdoGraphEntity.class, 
                    "((JdoAttributeHolder)attributes.get(attr)).hashedValue == value");
                q.declareImports("import org.jpox.samples.models.graph.JdoAttribute; import org.jpox.samples.models.graph.JdoAttributeHolder");
                q.declareParameters("JdoAttribute attr, int value");
                Collection possibleGEs = (Collection) q.execute(attrName, new Integer("John".hashCode()));

                // Select only those graph entities whose attribute values are really equal.
                Collection result = new HashSet();
                Iterator iter = possibleGEs.iterator();
                while (iter.hasNext())
                {
                    JdoGraphEntity ge = (JdoGraphEntity) iter.next();
                    if (ge.getAttributeValue(attrName) != null && ge.getAttributeValue(attrName).equals("John"))
                    {
                        result.add(ge);
                    }
                }
                q.closeAll();
                assertEquals(2, result.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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
            // Clean out our data
            clean(JdoNode.class);
            clean(JdoGraphEntity.class);
            clean(JdoGraphEntityClass.class);
            clean(JdoGraph.class);
        }
    }

    /**
     * Test 1-N uni FK Set relation having subclasses on the target side (1=source;N=target)
     */
    public void test1toNUnidirFKSetInheritanceTarget()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;

            // Persist some objects
            try
            {
                tx.begin();

                SetHolder holder = new SetHolder("First");
                PCFKSetElementSub1 sub1 = new PCFKSetElementSub1("Element 1");
                PCFKSetElementSub2 sub2 = new PCFKSetElementSub2("Element 2");
                holder.getFkSetPC().add(sub1);
                holder.getFkSetPC().add(sub2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                SetHolder holder = (SetHolder)pm.getObjectById(holderId, true);
                Collection elements = holder.getFkSetPC();
                assertEquals("Number of elements in 1-N unidir FK with inheritance is incorrect", 2, elements.size());
                boolean containsElem1 = false;
                boolean containsElem2 = false;
                for (Iterator iter = elements.iterator(); iter.hasNext();)
                {
                    Object element = iter.next();
                    if (element instanceof PCFKSetElementSub1)
                    {
                        containsElem1 = true;
                    }
                    else if (element instanceof PCFKSetElementSub2)
                    {
                        containsElem2 = true;
                    }
                }
                assertTrue(containsElem1);
                assertTrue(containsElem2);
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
            clean(SetHolder.class);
            clean(PCFKSetElementSub1.class);
            clean(PCFKSetElementSub1.class);
        }
    }

    /**
     * Test 1-N uni FK List relation having subclasses on the target side (1=source;N=target)
     */
    public void test1toNUnidirFKListInheritanceTarget()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;

            // Persist some objects
            try
            {
                tx.begin();

                ListHolder holder = new ListHolder();
                PCFKListElementSub1 sub1 = new PCFKListElementSub1("Element 1");
                PCFKListElementSub2 sub2 = new PCFKListElementSub2("Element 2");
                holder.getFkListPC().add(sub1);
                holder.getFkListPC().add(sub2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Query the objects
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                ListHolder holder = (ListHolder)pm.getObjectById(holderId, true);
                List elements = holder.getFkListPC();
                assertEquals("Number of elements in 1-N unidir FK with inheritance is incorrect", 2, elements.size());

                Object element = null;
                element = elements.get(0);
                assertTrue("First element of 1-N uni FK list is incorrect type", 
                    element instanceof PCFKListElementSub1);
                element = elements.get(1);
                assertTrue("Second element of 1-N uni FK list is incorrect type", 
                    element instanceof PCFKListElementSub2);

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
            clean(ListHolder.class);
            clean(PCFKListElementSub1.class);
            clean(PCFKListElementSub1.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using a shared join table (Set).
     **/
    public void test1toNUnidirSetSharedJoin()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object holderId = null;
            try
            {
                tx.begin();
                
                SetHolder holder = new SetHolder();
                PCJoinElement elem1 = new PCJoinElement("First");
                PCJoinElement elem2 = new PCJoinElement("Second");
                PCJoinElement elem3 = new PCJoinElement("Third");
                holder.getJoinSetPCShared1().add(elem1);
                holder.getJoinSetPCShared1().add(elem3);
                holder.getJoinSetPCShared2().add(elem2);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional Set with shared join table : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                SetHolder holder = (SetHolder)pm.getObjectById(holderId);
                Set coll1 = holder.getJoinSetPCShared1();
                Set coll2 = holder.getJoinSetPCShared2();
                assertTrue("Collection 1 should have elements but is null!", coll1 != null);
                assertEquals("Collection 1 has incorrect number of elements", coll1.size(), 2);
                assertTrue("Collection 2 should have elements but is null!", coll2 != null);
                assertEquals("Collection 2 has incorrect number of elements", coll2.size(), 1);
                
                boolean hasElem1 = false;
                boolean hasElem2 = false;
                boolean hasElem3 = false;
                Iterator iter1 = coll1.iterator();
                while (iter1.hasNext())
                {
                    PCJoinElement elem = (PCJoinElement)iter1.next();
                    if (elem.getName().equals("First"))
                    {
                        hasElem1 = true;
                    }
                    else if (elem.getName().equals("Third"))
                    {
                        hasElem3 = true;
                    }
                }
                Iterator iter2 = coll2.iterator();
                while (iter2.hasNext())
                {
                    PCJoinElement elem = (PCJoinElement)iter2.next();
                    if (elem.getName().equals("Second"))
                    {
                        hasElem2 = true;
                    }
                }
                assertTrue("Collection 1 is missing element 1", hasElem1);
                assertTrue("Collection 1 is missing element 3", hasElem3);
                assertTrue("Collection 2 is missing element 2", hasElem2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional Set with shared join table : " + e.getMessage());
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
            clean(SetHolder.class);
            clean(PCJoinElement.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using a shared foreign-key (Set).
     **/
    public void test1toNUnidirSetSharedFK()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object holderId = null;
            try
            {
                tx.begin();
                
                SetHolder holder = new SetHolder();
                PCFKSetElementShared elem1 = new PCFKSetElementShared("First");
                PCFKSetElementShared elem2 = new PCFKSetElementShared("Second");
                PCFKSetElementShared elem3 = new PCFKSetElementShared("Third");
                holder.getFkSetPCShared1().add(elem1);
                holder.getFkSetPCShared1().add(elem3);
                holder.getFkSetPCShared2().add(elem2);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional Set with shared foreign-key : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                SetHolder holder = (SetHolder)pm.getObjectById(holderId);
                Set coll1 = holder.getFkSetPCShared1();
                Set coll2 = holder.getFkSetPCShared2();
                assertTrue("Collection 1 should have elements but is null!", coll1 != null);
                assertEquals("Collection 1 has incorrect number of elements", coll1.size(), 2);
                assertTrue("Collection 2 should have elements but is null!", coll2 != null);
                assertEquals("Collection 2 has incorrect number of elements", coll2.size(), 1);
                
                boolean hasElem1 = false;
                boolean hasElem2 = false;
                boolean hasElem3 = false;
                Iterator iter1 = coll1.iterator();
                while (iter1.hasNext())
                {
                    PCFKSetElementShared elem = (PCFKSetElementShared)iter1.next();
                    if (elem.getName().equals("First"))
                    {
                        hasElem1 = true;
                    }
                    else if (elem.getName().equals("Third"))
                    {
                        hasElem3 = true;
                    }
                }
                Iterator iter2 = coll2.iterator();
                while (iter2.hasNext())
                {
                    PCFKSetElementShared elem = (PCFKSetElementShared)iter2.next();
                    if (elem.getName().equals("Second"))
                    {
                        hasElem2 = true;
                    }
                }
                assertTrue("Collection 1 is missing element 1", hasElem1);
                assertTrue("Collection 1 is missing element 3", hasElem3);
                assertTrue("Collection 2 is missing element 2", hasElem2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional Set with shared foreign-key : " + e.getMessage());
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
            clean(SetHolder.class);
            clean(PCFKSetElementShared.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using a shared join table (List).
     **/
    public void test1toNUnidirListSharedJoin()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object holderId = null;
            try
            {
                tx.begin();
                
                ListHolder holder = new ListHolder();
                PCJoinElement elem1 = new PCJoinElement("First");
                PCJoinElement elem2 = new PCJoinElement("Second");
                PCJoinElement elem3 = new PCJoinElement("Third");
                holder.getJoinListPCShared1().add(elem1);
                holder.getJoinListPCShared1().add(elem3);
                holder.getJoinListPCShared2().add(elem2);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional List with shared join table : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                List coll1 = holder.getJoinListPCShared1();
                List coll2 = holder.getJoinListPCShared2();
                assertTrue("Collection 1 should have elements but is null!", coll1 != null);
                assertEquals("Collection 1 has incorrect number of elements", coll1.size(), 2);
                assertTrue("Collection 2 should have elements but is null!", coll2 != null);
                assertEquals("Collection 2 has incorrect number of elements", coll2.size(), 1);
                
                boolean hasElem1 = false;
                boolean hasElem2 = false;
                boolean hasElem3 = false;
                Iterator iter1 = coll1.iterator();
                while (iter1.hasNext())
                {
                    PCJoinElement elem = (PCJoinElement)iter1.next();
                    if (elem.getName().equals("First"))
                    {
                        hasElem1 = true;
                    }
                    else if (elem.getName().equals("Third"))
                    {
                        hasElem3 = true;
                    }
                }
                Iterator iter2 = coll2.iterator();
                while (iter2.hasNext())
                {
                    PCJoinElement elem = (PCJoinElement)iter2.next();
                    if (elem.getName().equals("Second"))
                    {
                        hasElem2 = true;
                    }
                }
                assertTrue("Collection 1 is missing element 1", hasElem1);
                assertTrue("Collection 1 is missing element 3", hasElem3);
                assertTrue("Collection 2 is missing element 2", hasElem2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional List with shared join table : " + e.getMessage());
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
            clean(ListHolder.class);
            clean(PCJoinElement.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using a shared foreign-key (List).
     **/
    public void test1toNUnidirListSharedFK()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object holderId = null;
            try
            {
                tx.begin();
                
                ListHolder holder = new ListHolder();
                PCFKListElementShared elem1 = new PCFKListElementShared("First");
                PCFKListElementShared elem2 = new PCFKListElementShared("Second");
                PCFKListElementShared elem3 = new PCFKListElementShared("Third");
                holder.getFkListPCShared1().add(elem1);
                holder.getFkListPCShared1().add(elem3);
                holder.getFkListPCShared2().add(elem2);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional List with shared foreign-key : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                List coll1 = holder.getFkListPCShared1();
                List coll2 = holder.getFkListPCShared2();
                assertTrue("Collection 1 should have elements but is null!", coll1 != null);
                assertEquals("Collection 1 has incorrect number of elements", coll1.size(), 2);
                assertTrue("Collection 2 should have elements but is null!", coll2 != null);
                assertEquals("Collection 2 has incorrect number of elements", coll2.size(), 1);
                
                boolean hasElem1 = false;
                boolean hasElem2 = false;
                boolean hasElem3 = false;
                Iterator iter1 = coll1.iterator();
                while (iter1.hasNext())
                {
                    PCFKListElementShared elem = (PCFKListElementShared)iter1.next();
                    if (elem.getName().equals("First"))
                    {
                        hasElem1 = true;
                    }
                    else if (elem.getName().equals("Third"))
                    {
                        hasElem3 = true;
                    }
                }
                Iterator iter2 = coll2.iterator();
                while (iter2.hasNext())
                {
                    PCFKListElementShared elem = (PCFKListElementShared)iter2.next();
                    if (elem.getName().equals("Second"))
                    {
                        hasElem2 = true;
                    }
                }
                assertTrue("Collection 1 is missing element 1", hasElem1);
                assertTrue("Collection 1 is missing element 3", hasElem3);
                assertTrue("Collection 2 is missing element 2", hasElem2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional List with shared foreign-key : " + e.getMessage());
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
            clean(ListHolder.class);
            clean(PCFKListElementShared.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using an ordered List.
     **/
    public void test1toNUnidirOrderedList()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object holderId = null;
            try
            {
                tx.begin();
                
                ListHolder holder = new ListHolder();
                PCFKListElement elem1 = new PCFKListElement("First");
                PCFKListElement elem2 = new PCFKListElement("Middle");
                PCFKListElement elem3 = new PCFKListElement("Last");
                holder.getFkListPCOrdered().add(elem1);
                holder.getFkListPCOrdered().add(elem2);
                holder.getFkListPCOrdered().add(elem3);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional ordered List : " + e.getMessage());
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

                // Retrieve the holder and check the ordered data
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                List coll1 = holder.getFkListPCOrdered();
                assertTrue("Collection should have elements but is null!", coll1 != null);
                assertEquals("Collection has incorrect number of elements", coll1.size(), 3);

                PCFKListElement elem1 = (PCFKListElement)coll1.get(0);
                PCFKListElement elem2 = (PCFKListElement)coll1.get(1);
                PCFKListElement elem3 = (PCFKListElement)coll1.get(2);
                assertEquals("First retrieved element is incorrect", "First", elem1.getName());
                assertEquals("Second retrieved element is incorrect", "Last", elem2.getName());
                assertEquals("Third retrieved element is incorrect", "Middle", elem3.getName());

                // Remove an element
                coll1.remove(elem1);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional ordered List : " + e.getMessage());
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
            Object elem1Id = null;
            Object elem2Id = null;
            try
            {
                tx.begin();

                // Retrieve the holder and check the ordered data
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                List coll1 = holder.getFkListPCOrdered();
                assertTrue("Collection should have elements but is null!", coll1 != null);
                assertEquals("Collection has incorrect number of elements", coll1.size(), 2);

                PCFKListElement elem1 = (PCFKListElement)coll1.get(0);
                PCFKListElement elem2 = (PCFKListElement)coll1.get(1);
                assertEquals("First retrieved element (after remove) is incorrect", "Last", elem1.getName());
                assertEquals("Second retrieved element (after remove) is incorrect", "Middle", elem2.getName());
                elem1Id = pm.getObjectId(elem1);
                elem2Id = pm.getObjectId(elem2);

                // Remove the holder
                pm.deletePersistent(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional ordered List : " + e.getMessage());
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

                // Try to retrieve the holder
                try
                {
                    pm.getObjectById(holderId);
                    fail("Retrieved ListHolder yet should have been deleted");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                try
                {
                    PCFKListElement elem1 = (PCFKListElement)pm.getObjectById(elem1Id);
                    assertEquals("Element 1 has incorrect name", "Last", elem1.getName());
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    fail("Failed to retrieve list element1 when should still be persistent");
                }

                try
                {
                    PCFKListElement elem2 = (PCFKListElement)pm.getObjectById(elem2Id);
                    assertEquals("Element 2 has incorrect name", "Middle", elem2.getName());
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    fail("Failed to retrieve list element2 when should still be persistent");
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional ordered List : " + e.getMessage());
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
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }

    /**
     * Test case for 1-N unidirectional relationships using an ordered List with detachment.
     **/
    public void test1toNUnidirOrderedListDetachAttach()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data and detach it
            Object holderId = null;
            ListHolder detachedHolder = null;
            try
            {
                tx.begin();
                
                ListHolder holder = new ListHolder();
                PCFKListElement elem1 = new PCFKListElement("First");
                PCFKListElement elem2 = new PCFKListElement("Middle");
                PCFKListElement elem3 = new PCFKListElement("Last");
                holder.getFkListPCOrdered().add(elem1);
                holder.getFkListPCOrdered().add(elem2);
                holder.getFkListPCOrdered().add(elem3);
                pm.makePersistent(holder);
                holderId = JDOHelper.getObjectId(holder);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating data for 1-N unidirectional ordered List : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Retrieve holder and detach it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.getFetchPlan().setMaxFetchDepth(-1);
                pm.getFetchPlan().setGroup(FetchPlan.ALL);
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                detachedHolder = (ListHolder)pm.detachCopy(holder);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while detaching data for 1-N unidirectional ordered List : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Delete an element while detached
            assertEquals("Number of elements whilst detached is wrong", 3, detachedHolder.getFkListPCOrdered().size());
            detachedHolder.getFkListPCOrdered().remove(1);

            // Reattach
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
                LOG.error("Exception thrown during reattach", e);
                fail("Exception thrown while attaching data for 1-N unidirectional ordered List : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Retrieve and check it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the holder and check the ordered data
                ListHolder holder = (ListHolder)pm.getObjectById(holderId);
                List coll1 = holder.getFkListPCOrdered();
                assertTrue("Collection should have elements but is null!", coll1 != null);
                assertEquals("Collection has incorrect number of elements", coll1.size(), 2);

                PCFKListElement elem1 = (PCFKListElement)coll1.get(0);
                PCFKListElement elem2 = (PCFKListElement)coll1.get(1);
                assertEquals("First retrieved element (after remove) is incorrect", "First", elem1.getName());
                assertEquals("Second retrieved element (after remove) is incorrect", "Middle", elem2.getName());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while interrogating data for 1-N unidirectional ordered List : " + e.getMessage());
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
            clean(ListHolder.class);
            clean(PCFKListElement.class);
        }
    }

    /**
     * Test of the persistence of 1-1 bidirectional relations between objects of the same type.
     * This is effectively a doubly-linked list.
     */
    public void testDoublyLinkedList()
    {
        Object tomId = null;
        Object dickId = null;
        Object harryId = null;
        Object georgeId = null;
        Object peterId = null;

        try
        {
            // Create the schema
            addClassesToSchema(new Class[] {DoubleLink.class});

            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DoubleLink tom = new DoubleLink(1, "Tom");
                DoubleLink dick = new DoubleLink(2, "Dick");
                DoubleLink harry = new DoubleLink(3, "Harry");
                DoubleLink george = new DoubleLink(4, "George");
                DoubleLink peter = new DoubleLink(5, "Peter");
                tom.setBack(peter);
                tom.setFront(dick);
                dick.setBack(tom);
                dick.setFront(harry);
                harry.setBack(dick);
                harry.setFront(george);
                george.setBack(harry);
                george.setFront(peter);
                peter.setBack(george);
                peter.setFront(tom);

                pm.makePersistent(tom);
                tx.commit();

                tomId = pm.getObjectId(tom);
                dickId = pm.getObjectId(dick);
                harryId = pm.getObjectId(harry);
                georgeId = pm.getObjectId(george);
                peterId = pm.getObjectId(peter);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting doubly-linked objects : ", e);
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

            // Retrieve the data and check it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                DoubleLink tom = (DoubleLink)pm.getObjectById(tomId);
                DoubleLink dick = (DoubleLink)pm.getObjectById(dickId);
                DoubleLink harry = (DoubleLink)pm.getObjectById(harryId);
                DoubleLink george = (DoubleLink)pm.getObjectById(georgeId);
                DoubleLink peter = (DoubleLink)pm.getObjectById(peterId);

                assertTrue("Double-linked object 'tom' was null!", tom != null);
                assertTrue("Double-linked object 'dick' was null!", dick != null);
                assertTrue("Double-linked object 'harry' was null!", harry != null);
                assertTrue("Double-linked object 'george' was null!", george != null);
                assertTrue("Double-linked object 'peter' was null!", peter != null);

                assertEquals("Double-linked object 'tom' has incorrect front", dick, tom.getFront());
                assertEquals("Double-linked object 'dick' has incorrect front", harry, dick.getFront());
                assertEquals("Double-linked object 'harry' has incorrect front", george, harry.getFront());
                assertEquals("Double-linked object 'george' has incorrect front", peter, george.getFront());
                assertEquals("Double-linked object 'peter' has incorrect front", tom, peter.getFront());

                assertEquals("Double-linked object 'tom' has incorrect back", peter, tom.getBack());
                assertEquals("Double-linked object 'dick' has incorrect back", tom, dick.getBack());
                assertEquals("Double-linked object 'harry' has incorrect back", dick, harry.getBack());
                assertEquals("Double-linked object 'george' has incorrect back", harry, george.getBack());
                assertEquals("Double-linked object 'peter' has incorrect back", george, peter.getBack());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown retrieving doubly-linked objects : ", e);
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
            // Clean out our data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                DoubleLink tom = (DoubleLink)pm.getObjectById(tomId);
                DoubleLink dick = (DoubleLink)pm.getObjectById(dickId);
                DoubleLink harry = (DoubleLink)pm.getObjectById(harryId);
                DoubleLink george = (DoubleLink)pm.getObjectById(georgeId);
                DoubleLink peter = (DoubleLink)pm.getObjectById(peterId);
                tom.setBack(null);
                tom.setFront(null);
                dick.setBack(null);
                dick.setFront(null);
                harry.setBack(null);
                harry.setFront(null);
                george.setBack(null);
                george.setFront(null);
                peter.setBack(null);
                peter.setFront(null);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown retrieving singly-linked objects : ", e);
                fail();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            clean(DoubleLink.class);
        }
    }

    /**
     * Test of the persistence of 1-1 unidirectional relations between objects of the same type.
     * This is effectively a singly-linked list.
     */
    public void testSinglyLinkedList()
    {
        Object tomId = null;
        Object dickId = null;
        Object harryId = null;
        Object georgeId = null;
        Object peterId = null;
        try
        {
            // Create the schema
            addClassesToSchema(new Class[] {SingleLink.class});

            // Persist some objects
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SingleLink tom = new SingleLink(1, "Tom");
                SingleLink dick = new SingleLink(2, "Dick");
                SingleLink harry = new SingleLink(3, "Harry");
                SingleLink george = new SingleLink(4, "George");
                SingleLink peter = new SingleLink(5, "Peter");
                tom.setFront(dick);
                dick.setFront(harry);
                harry.setFront(george);
                george.setFront(peter);
                peter.setFront(tom);

                pm.makePersistent(tom);
                tx.commit();

                tomId = pm.getObjectId(tom);
                dickId = pm.getObjectId(dick);
                harryId = pm.getObjectId(harry);
                georgeId = pm.getObjectId(george);
                peterId = pm.getObjectId(peter);
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown persisting singly-linked objects : ", e);
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

            // Retrieve the data and check it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                SingleLink tom = (SingleLink)pm.getObjectById(tomId);
                SingleLink dick = (SingleLink)pm.getObjectById(dickId);
                SingleLink harry = (SingleLink)pm.getObjectById(harryId);
                SingleLink george = (SingleLink)pm.getObjectById(georgeId);
                SingleLink peter = (SingleLink)pm.getObjectById(peterId);

                assertTrue("Single-linked object 'tom' was null!", tom != null);
                assertTrue("Single-linked object 'dick' was null!", dick != null);
                assertTrue("Single-linked object 'harry' was null!", harry != null);
                assertTrue("Single-linked object 'george' was null!", george != null);
                assertTrue("Single-linked object 'peter' was null!", peter != null);

                assertEquals("Single-linked object 'tom' has incorrect front", dick, tom.getFront());
                assertEquals("Single-linked object 'dick' has incorrect front", harry, dick.getFront());
                assertEquals("Single-linked object 'harry' has incorrect front", george, harry.getFront());
                assertEquals("Single-linked object 'george' has incorrect front", peter, george.getFront());
                assertEquals("Single-linked object 'peter' has incorrect front", tom, peter.getFront());

                tx.commit();
            }
            catch (Exception e)
            {
                fail(e.getMessage());
                LOG.error(">> Exception thrown retrieving singly-linked objects : ", e);
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
                // Remove the linkage
                tx.begin();
                SingleLink tom = (SingleLink)pm.getObjectById(tomId);
                SingleLink dick = (SingleLink)pm.getObjectById(dickId);
                SingleLink harry = (SingleLink)pm.getObjectById(harryId);
                SingleLink george = (SingleLink)pm.getObjectById(georgeId);
                SingleLink peter = (SingleLink)pm.getObjectById(peterId);
                tom.setFront(null);
                dick.setFront(null);
                harry.setFront(null);
                george.setFront(null);
                peter.setFront(null);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown retrieving singly-linked objects : ", e);
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

            clean(SingleLink.class);
        }
    }

    /**
     * Test case for 1-N inverse unidirectional to itself.
     **/
    public void testParentChildLinkRelation()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object primaryObjId = null;

            // Check the persistence of owner and elements
            try
            {
                tx.begin();

                ParentChildLink secondaryObj = new ParentChildLink("secondaryObj", null);
                ParentChildLink primaryObj = new ParentChildLink("primaryObj", secondaryObj);
                ParentChildLink childA = new ParentChildLink("childA", null);
                ParentChildLink childB = new ParentChildLink("childB", null);
                pm.makePersistent(primaryObj);
                pm.makePersistent(secondaryObj);
                pm.makePersistent(childA);
                pm.makePersistent(childB);

                primaryObj.addChild(childA);
                primaryObj.addChild(childB);

                tx.commit();
                primaryObjId = pm.getObjectId(primaryObj);
            }

            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-N unidirectional relationships : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the retrieval of the owner and elements
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ParentChildLink toSelf = (ParentChildLink)pm.getObjectById(primaryObjId, true);
                assertNotNull("Unable to retrieve container object for unidirectional 1-N FK relationship", toSelf);
                assertEquals("Number of elements in unidirectional 1-N FK relationship is wrong : was " + toSelf.getChildren().size() + " but should have been 2",toSelf.getChildren().size(),2);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while querying 1-N unidirectional relationships : " + e.getMessage());
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
                Extent ex = pm.getExtent(ParentChildLink.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    ParentChildLink link = (ParentChildLink)iter.next();
                    link.getChildren().clear();
                    link.clearNextObject();
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
            clean(ParentChildLink.class);
        }
    }

    /**
     * Test case for 1-N unidir FK List relationships and a mapped-by on the ordering
     **/
    public void test1toNListFKUsingOrderMappedBy()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            // Create sample data
            Object projectId = null;
            try
            {
                tx.begin();
                
                SoftwareProject project = new SoftwareProject("JPOX");
                Donation don1 = new Donation("Oracle", 20000.00);
                Donation don2 = new Donation("IBM", 15000.00);
                project.addDonation(don1);
                project.addDonation(don2);
                pm.makePersistent(project);
                projectId = JDOHelper.getObjectId(project);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while creating data for 1-N unidirectional FK List with order mapped-by : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve the record and check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                SoftwareProject project = (SoftwareProject)pm.getObjectById(projectId);
                assertEquals("Name of project is wrong", project.getName(), "JPOX");
                assertEquals("Number of donations is wrong", project.getNumberOfDonations(), 2);
                List donations = project.getDonations();
                Iterator iter = donations.iterator();
                int position = 0;
                while (iter.hasNext())
                {
                    Donation don = (Donation)iter.next();
                    assertEquals("Position of donation " + don.toString() + " is wrong", don.getDonationNumber(), position);
                    position++;
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while interrogating data for 1-N unidirectional FK List with order mapped-by : " + e.getMessage());
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
            clean(SoftwareProject.class);
            clean(Donation.class);
        }
    }

    /**
     * Test case for 1-N bidir FK, using a Map<String, PC>.
     **/
    public void test1toNBidirFKMapWithInheritedValue()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holderId = null;

            // Check the persistence of owner and elements
            try
            {
                tx.begin();
                
                // Create some data
                MapFKHolder holder = new MapFKHolder("First");
                MapFKValue value1 = new MapFKValue("Value 1", "First value", "1");
                value1.setHolder(holder);
                MapFKValue value2 = new MapFKValue("Value 2", "Second value", "2");
                value2.setHolder(holder);
                holder.getMap().put(value1.getKey(), value1);
                holder.getMap().put(value2.getKey(), value2);
                pm.makePersistent(holder);

                tx.commit();
                holderId = pm.getObjectId(holder);
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting data", e);
                fail("Exception thrown while creating 1-N (Map) bidir FK relationships with inherited value : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            pmf.getDataStoreCache().evictAll();

            // Check the retrieval of the owner and values
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                MapFKHolder holder = (MapFKHolder)pm.getObjectById(holderId);
                assertNotNull("Unable to retrieve container object for 1-N bidir FK relationship (Map)", holder);

                // Force the retrieval of the entrySet
                Collection<Map.Entry<String, MapFKValue>> mapEntries = holder.getMap().entrySet();
                for (Map.Entry<String, MapFKValue> entry : mapEntries)
                {
                    String key = entry.getKey();
                    MapFKValue val = entry.getValue();
                    LOG.debug("Map key="+ key + " value=" + val);
                }

                Collection values = holder.getMap().values();
                Iterator iter = values.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                assertEquals("Number of values in 1-N bidir FK relationship (Map) is wrong", 2, values.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail("Exception thrown while querying 1-N bidir FK relationships (Map) : " + e.getMessage());
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
            // Clean out our data
            clean(MapFKHolder.class);
            clean(MapFKValue.class);
        }
    }

    /**
     * Test case for 1-N relations with base container/element using subclass-table and single subclass.
     */
    public void test1toNAbstractBaseUsingSubclassTable()
    throws Exception
    {
        try
        {
            //prepare data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();

                Sky s = new Sky();
                Cloud cl1 = new Cloud();
                cl1.setId(101);
                Cloud cl2 = new Cloud();
                cl2.setId(102);
                s.addCloud(cl1);
                s.addCloud(cl2);
                cl1.setSky(s);
                cl2.setSky(s);

                pm.makePersistent(s);
                tx.commit();
                id = JDOHelper.getObjectId(s);
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
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

            //test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Sky s = (Sky)pm.getObjectById(id);
                List<Cloud> clouds = s.getClouds();
                assertEquals("Number of clouds in sky was wrong", 2, clouds.size());
                Cloud cl1 = clouds.get(0);
                assertEquals("First cloud is wrong", 101, cl1.getId().longValue());
                Cloud cl2 = clouds.get(1);
                assertEquals("Second cloud is wrong", 102, cl2.getId().longValue());
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

            // getExtent of Sky
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(Sky.class);
                Iterator iter = ex.iterator();
                int size = 0;
                while (iter.hasNext())
                {
                    iter.next();
                    size++;
                }
                assertEquals("Number of sky objects is incorrect from Extent", 1, size);
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

            // getExtent of Cloud
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(Cloud.class);
                Iterator iter = ex.iterator();
                int size = 0;
                while (iter.hasNext())
                {
                    iter.next();
                    size++;
                }
                assertEquals("Number of clouds is incorrect from Extent", 2, size);
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
            // Clean out all data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            tx.begin();
            for (Iterator iter = ((Collection) pm.newQuery(Sky.class).execute()).iterator(); iter.hasNext();)
            {
                Sky sky = (Sky)iter.next();
                Iterator<Cloud> clIter = sky.getClouds().iterator();
                while (clIter.hasNext())
                {
                    Cloud cl = clIter.next();
                    cl.setSky(null);
                    sky.removeCloud(cl);
                }
            }
            tx.commit();
            pm.close();

            clean(Sky.class);
            clean(Cloud.class);
        }
    }

    /**
     * Test case for N-1 unidirectional relationships.
     */
    public void testNto1Unidir()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            addClassesToSchema(new Class[] {CarRental.class, HireCar.class});
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown setting up N-1 sample classes schema", e);
            fail("Exception thrown setting up N-1 sample classes schema : " + e.getMessage());
        }

        try
        {
            // Create sample data
            Object rentalId = null;
            Object car1Id = null;
            Object rental2Id = null;
            Object car3Id = null;
            try
            {
                tx.begin();

                HireCar car1 = new HireCar(123457, "Volkswagen", "Golf");
                HireCar car2 = new HireCar(123456, "Ford", "Fiesta");
                HireCar car3 = new HireCar(123455, "Toyota", "Corolla");
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.YEAR, 2010);
                cal.set(Calendar.MONTH, 6);
                cal.set(Calendar.DAY_OF_MONTH, 15);
                Date start = cal.getTime();
                cal.set(Calendar.YEAR, 2010);
                cal.set(Calendar.MONTH, 6);
                cal.set(Calendar.DAY_OF_MONTH, 17);
                Date end = cal.getTime();
                CarRental rental = new CarRental(10001, start, end, car2);
                pm.makePersistent(rental);
                pm.makePersistent(car1);
                CarRental rental2 = new CarRental(10002, start, end, car3);
                pm.makePersistent(rental2);

                tx.commit();
                rentalId = JDOHelper.getObjectId(rental);
                car1Id = JDOHelper.getObjectId(car1);
                rental2Id = JDOHelper.getObjectId(rental2);
                car3Id = JDOHelper.getObjectId(car3);
            }
            catch (Exception e)
            {
                LOG.error("Exception persisting N-1 data", e);
                fail("Exception thrown while creating N-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the record, check the data, and update the relation
            pmf.getDataStoreCache().evictAll(); // Make sure we retrieve from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CarRental rental = (CarRental)pm.getObjectById(rentalId);
                assertEquals("CarRental \"customerId\" is incorrect", 10001, rental.getCustomerId());

                HireCar car = rental.getHireCar();
                assertNotNull("CarRental \"hireCar\" is null!", car);
                assertEquals("HireCar \"registrationId\" is incorrect", 123456, car.getRegistrationId());
                assertEquals("HireCar \"make\" is incorrect", "Ford", car.getMake());
                assertEquals("HireCar \"model\" is incorrect", "Fiesta", car.getModel());

                // Update the car (renter had an upgrade)
                HireCar replacementCar = (HireCar)pm.getObjectById(car1Id);
                rental.setHireCar(replacementCar);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while interrogating/updating N-1 unidirectional relationship data", e);
                fail("Exception thrown while interrogating/updating N-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the record and check the data
            pmf.getDataStoreCache().evictAll(); // Make sure we retrieve from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CarRental rental = (CarRental)pm.getObjectById(rentalId);
                assertEquals("CarRental \"customerId\" is incorrect", 10001, rental.getCustomerId());

                HireCar car = rental.getHireCar();
                assertNotNull("CarRental \"hireCar\" is null!", car);
                assertEquals("HireCar \"registrationId\" is incorrect", 123457, car.getRegistrationId());
                assertEquals("HireCar \"make\" is incorrect", "Volkswagen", car.getMake());
                assertEquals("HireCar \"model\" is incorrect", "Golf", car.getModel());

                // Remove the relation
                rental.setHireCar(null);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while interrogating/updating N-1 unidirectional relationship data", e);
                fail("Exception thrown while interrogating/updating N-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the record and check the data
            pmf.getDataStoreCache().evictAll(); // Make sure we retrieve from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CarRental rental = (CarRental)pm.getObjectById(rentalId);
                assertEquals("CarRental \"customerId\" is incorrect", 10001, rental.getCustomerId());

                HireCar car = rental.getHireCar();
                assertNull("CarRental \"hireCar\" is not null!", car);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while interrogating/updating N-1 unidirectional relationship data", e);
                fail("Exception thrown while interrogating/updating N-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the second record, check the data, and delete the rental
            pmf.getDataStoreCache().evictAll(); // Make sure we retrieve from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                CarRental rental2 = (CarRental)pm.getObjectById(rental2Id);
                assertEquals("CarRental \"customerId\" is incorrect", 10002, rental2.getCustomerId());

                HireCar car = rental2.getHireCar();
                assertNotNull("CarRental \"hireCar\" is null!", car);
                assertEquals("HireCar \"registrationId\" is incorrect", 123455, car.getRegistrationId());
                assertEquals("HireCar \"make\" is incorrect", "Toyota", car.getMake());
                assertEquals("HireCar \"model\" is incorrect", "Corolla", car.getModel());

                // Delete the rental (should remove the relation but leave the car untouched)
                pm.deletePersistent(rental2);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while interrogating/deleting N-1 unidirectional relationship data", e);
                fail("Exception thrown while interrogating/deleting N-1 unidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

                pm.close();
            }

            // Retrieve the record and check the data
            pmf.getDataStoreCache().evictAll(); // Make sure we retrieve from the datastore
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                try
                {
                    pm.getObjectById(rental2Id);
                    fail("Deleted CarRental but still found in next transaction!");
                }
                catch (JDOObjectNotFoundException onfe)
                {
                    // Expected
                }

                HireCar car3 = (HireCar)pm.getObjectById(car3Id);
                assertEquals("HireCar \"registrationId\" is incorrect", 123455, car3.getRegistrationId());
                assertEquals("HireCar \"make\" is incorrect", "Toyota", car3.getMake());
                assertEquals("HireCar \"model\" is incorrect", "Corolla", car3.getModel());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown while interrogating N-1 unidirectional relationship data", e);
                fail("Exception thrown while interrogating N-1 unidirectional relationship data : " + e.getMessage());
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
            clean(CarRental.class);
            clean(HireCar.class);
        }
    }

    /**
     * Test case for 1-1 bidirectional relationships using 2 FK's.
     * This is really tested as BasicQuery and DatastoreId test. 
     **/
    /*public void test1to1BidirectionalMultiFK()
    throws Exception
    {
        try
        {
            Object idUser = null;
            Object idUserDetail = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            
            // Create sample data
            try
            {
                tx.begin();
                
                User user=new User("andy","password");
                UserDetails details=new UserDetails("Andy","Jefferson");
                user.setDetails(details);
                details.setUser(user);
                
                pm.makePersistent(user);
                
                tx.commit();
                idUser = pm.getObjectId(user);
                idUserDetail = pm.getObjectId(details);
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            pm=pmf.getPersistenceManager();
            tx=pm.currentTransaction();
            
            // Create sample data
            try
            {
                tx.begin();
                
                User user=(User)pm.getObjectById(idUser,true);
                UserDetails details=(UserDetails)pm.getObjectById(idUserDetail,true);
                assertEquals(details,user.getUserDetails());
                assertEquals(user,details.getUser());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                fail("Exception thrown while creating 1-1 bidirectional relationship data : " + e.getMessage());
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
            clean(User.class);
            clean(UserDetails.class);
        }
    }*/
}