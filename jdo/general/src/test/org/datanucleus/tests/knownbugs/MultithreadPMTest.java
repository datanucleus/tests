/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.tests.knownbugs;

import java.util.Properties;

import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.models.company.CompanyHelper;
import org.datanucleus.samples.models.company.Developer;
import org.datanucleus.samples.models.company.Employee;
import org.datanucleus.samples.models.company.Manager;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for multithreading capabilities.
 * Note that this tests multi-threaded PM capabilities, not multi-threaded PMF capabilities.
 */
public class MultithreadPMTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public MultithreadPMTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Employee.class,
                    Manager.class,
                    Developer.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test changing the state
     */
    public void testMultipleTransitionRead()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            final Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();
            pm.currentTransaction().begin();
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            woody.getLastName();
                            woody.getManager().getLastName();
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].start();
                }            
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                    }
                    catch (InterruptedException e)
                    {   
                        fail(e.getMessage());
                    }
                }
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }

    /**
     * Test changing the state
     */
    public void testMultipleNonTransactionalRead()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);

        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            final Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();
            pm.currentTransaction().setNontransactionalRead(true);
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            woody.getLastName();
                            woody.getManager().getLastName();
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].start();
                }            
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                    }
                    catch (InterruptedException e)
                    {   
                        fail(e.getMessage());
                    }
                }
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }

    /**
     * Test changing the state
     */
    public void testMultipleTransitionWrite()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            final Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            final Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();
            pm.currentTransaction().begin();
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            woody.setLastName("name");
                            woody.setManager(boss);
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].start();
                }            
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                    }
                    catch (InterruptedException e)
                    {   
                        fail(e.getMessage());
                    }
                }
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }

    /**
     * Test changing the state
     */
    public void testEvictAllAndWrites()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            Thread[] threads2 = new Thread[THREAD_SIZE];
            
            final PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            final Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            final Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            woody.setManager(bart);
            pm.makePersistent(woody);
            final Object id = pm.getObjectId(woody);
            pm.currentTransaction().commit();
            pm.currentTransaction().begin();
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            pm.getObjectById(id, true);
                            woody.setLastName("name");
                            woody.setManager(boss);
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads2[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            pm.evictAll();
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].start();
                    threads2[i].start();
                }            
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                        threads2[i].join();
                    }
                    catch (InterruptedException e)
                    {   
                        fail(e.getMessage());
                    }
                }
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }

    /**
     * Test changing the state
     */
    public void testMultipleNonTransitionWrite()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            final Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            final Manager boss = new Manager(3,"Boss","WakesUp","boss@wakes.up",4,"serial 3");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();
            pm.currentTransaction().setNontransactionalWrite(true);
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i] = new Thread( new Runnable()
                        {
                        public void run()
                        {
                            woody.setLastName("name");
                            woody.setManager(boss);
                        }
                        });
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].start();
                }            
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    try
                    {
                        threads[i].join();
                    }
                    catch (InterruptedException e)
                    {   
                        fail(e.getMessage());
                    }
                }
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }

    public void testMultipleDetachCopy()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            MultithreadDetachRunner[] runner = new MultithreadDetachRunner[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();

            pm.currentTransaction().begin();
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    runner[i] = new MultithreadDetachRunner(pm, woody);
                    threads[i] = new Thread(runner[i]);
                    threads[i].start();
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].join();
                    if (runner[i].getException() != null)
                    {
                        LOG.error("Exception during test", runner[i].getException());
                        fail("Exception thrown during test : " + runner[i].getException());
                    }
                }
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }
    
    private class MultithreadDetachRunner implements Runnable
    {
        PersistenceManager threadPM;
        Object obj;
        Exception exception = null;

        public MultithreadDetachRunner(PersistenceManager pm, Object obj)
        {
            this.threadPM = pm;
            this.obj = obj;
        }

        public Exception getException()
        {
            return exception;
        }

        public void run()
        {
            Employee woody = (Employee) obj;
            Employee woodyDetached = null;
            try
            {
                //test detach and attach
                threadPM.getFetchPlan().addGroup("groupA");
                threadPM.getFetchPlan().setDetachmentOptions(FetchPlan.DETACH_LOAD_FIELDS);
//                pm.getFetchPlan().removeGroup(FetchPlan.DEFAULT);
                woodyDetached = (Employee)threadPM.detachCopy(woody);
                assertEquals(woody.getLastName(), woodyDetached.getLastName());
                assertEquals(woody.getManager().getLastName(), woodyDetached.getManager().getLastName());
            }
            catch (Exception e)
            {
                exception = e;
                LOG.error("Exception thrown during detachCopy process", e);
            }
        }
    }

    public void testMultipleDetachCopyAndFetchPlanModification()
    {
        Properties multiProps = new Properties();
        multiProps.setProperty(PropertyNames.PROPERTY_MULTITHREADED, "true");
        PersistenceManagerFactory myPMF = getPMF(1, multiProps);
        
        try
        {
            int THREAD_SIZE = 1000;
            Thread[] threads = new Thread[THREAD_SIZE];
            Runnable[] runner = new Runnable[THREAD_SIZE];
            
            PersistenceManager pm = myPMF.getPersistenceManager();
            pm.currentTransaction().begin();
            Employee woody = new Employee(1,"Woody","Woodpecker","woody@woodpecker.com",13,"serial 1", Integer.valueOf(10));
            Manager bart = new Manager(2,"Bart","Simpson","bart@simpson.com",2,"serial 2");
            woody.setManager(bart);
            pm.makePersistent(woody);
            pm.currentTransaction().commit();

            pm.currentTransaction().begin();
            try
            {
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    if (i % 2 == 0)
                    {
                        runner[i] = new MultithreadDetachRunner(pm,woody);
                    }
                    else
                    {
                        runner[i] = new MultithreadFetchPlanRunner(pm);
                    }
                    threads[i] = new Thread(runner[i]);
                    threads[i].start();
                }
                for (int i=0; i<THREAD_SIZE; i++)
                {
                    threads[i].join();

                    Exception e = null;
                    if (runner[i] instanceof MultithreadDetachRunner)
                    {
                        e = ((MultithreadDetachRunner)runner[i]).getException();
                    }
                    else if (runner[i] instanceof MultithreadFetchPlanRunner)
                    {
                        e = ((MultithreadFetchPlanRunner)runner[i]).getException();
                    }
                    if (e != null)
                    {
                        LOG.error("Exception during test", e);
                        fail("Exception thrown during test : " + e);
                    }
                }
            }
            catch (Exception e)
            {
                fail(e.getMessage());
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
            CompanyHelper.clearCompanyData(myPMF);
            myPMF.close();
        }
    }
    
    private class MultithreadFetchPlanRunner implements Runnable
    {
        PersistenceManager pm;
        Exception exception = null;

        public MultithreadFetchPlanRunner(PersistenceManager pm)
        {
            this.pm = pm;
        }

        public Exception getException()
        {
            return exception;
        }

        public void run()
        {
            try
            {
                // clear the fetch groups
                pm.getFetchPlan().clearGroups();
            }
            catch (Exception e)
            {
                LOG.error("Exception during clear of groups on FetchPlan");
                fail(e.toString());
            }
        }
    }
}