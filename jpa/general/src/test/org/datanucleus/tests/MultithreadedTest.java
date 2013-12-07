/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.models.company.Employee;
import org.jpox.samples.annotations.models.company.Manager;
import org.jpox.samples.annotations.one_many.bidir.Animal;
import org.jpox.samples.annotations.one_many.bidir.Farm;

/**
 * Tests for JPA when run in a multithreaded environment (EntityManager per Thread).
 */
public class MultithreadedTest extends JPAPersistenceTestCase
{
    public MultithreadedTest(String name)
    {
        super(name);
    }

    /**
     * Test that populates the datastore, and then starts many threads querying and detaching the data
     * and trying to access the detached data (checking for undetached fields that should have been detached).
     */
    public void testQueryAndDetachOneManyJoinBidir()
    {
        try
        {
            // Persist some data
            persistManagerWithEmployees();

            // Create the Threads
            int THREAD_SIZE = 500;
            final String[] threadErrors = new String[THREAD_SIZE];
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                final int threadNo = i;
                threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        String errorMsg = processQueryAndDetachOneManyJoinBidir(true);
                        threadErrors[threadNo] = errorMsg;
                    }
                });
            }

            // Run the threads
            LOG.debug(">> Starting threads");
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                threads[i].start();
            }
            for (int i = 0; i < THREAD_SIZE; i++)
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
            LOG.debug(">> Completed threads");

            // Process any errors and fail the test if any threads failed present
            for (String error : threadErrors)
            {
                if (error != null)
                {
                    fail(error);
                }
            }
        }
        finally
        {
            // Clean out data
            clean(Manager.class);
            clean(Employee.class);
        }
    }

    protected String processQueryAndDetachOneManyJoinBidir(boolean transaction)
    {
        List<Employee> results = null;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            if (transaction)
            {
                tx.begin();
            }
            Query q = em.createQuery("SELECT e FROM " + Employee.class.getName() + " e");
            List<Employee> emps = q.getResultList();
            for (Employee e : emps)
            {
                Manager mgr = e.getManager();
                if (e instanceof Manager)
                {
                    // Get our subordinates loaded
                    ((Manager)e).getSubordinates();
                }
                else
                {
                    // Get subordinates of our Manager loaded
                    mgr.getSubordinates();
                }
                e.getBestFriend();
            }
            results = new ArrayList<Employee>(emps);
            if (transaction)
            {
                tx.commit();
            }
        }
        catch (Throwable thr)
        {
            LOG.error("Exception query objects", thr);
            return "Exception in query : " + thr.getMessage();
        }
        finally
        {
            if (transaction && tx.isActive())
            {
                tx.rollback();
            }
            em.close(); // Detached the Employees and their loaded fields
        }

        for (Employee e : results)
        {
            try
            {
                LOG.debug(">> Employee: " + e.getFirstName() + " " + e.getLastName() + " bestFriend=" + e.getBestFriend());
                if (e instanceof Manager)
                {
                    Set subs = ((Manager)e).getSubordinates();
                    if (subs == null)
                    {
                        return "Manager object didnt have its subordinates detached!";
                    }
                    else if (subs.size() != 100)
                    {
                        return "Manager had " + subs.size() + " subordinates instead of 100";
                    }
                }
                else
                {
                    Manager mgr = e.getManager();
                    if (mgr == null)
                    {
                        return "Employee=" + e + " didnt have its manager set!";
                    }
                    else
                    {
                        Set<Employee> subs = mgr.getSubordinates();
                        if (subs == null)
                        {
                            return "Employee=" + e + " didnt have its subordinates set!";
                        }
                        else if (subs.size() != 100)
                        {
                            return "Employee=" + e + " has Manager with " + subs.size() + " subordinates instead of 100";
                        }
                        for (Employee subE : subs)
                        {
                            subE.toString();
                        }
                    }
                }
            }
            catch (Exception exc)
            {
                LOG.error(">> Exception thrown on check of results", exc);
                return "Exception checking results : " + exc.getMessage();
            }
        }
        return null;
    }

    /**
     * Test that populates the datastore, and then starts many threads querying and detaching the data
     * and trying to access the detached data (checking for undetached fields that should have been detached).
     */
    public void testQueryAndDetachOneManyFKBidir()
    {
        try
        {
            // Persist some data
            persistFarmWithAnimals();

            // Create the Threads
            int THREAD_SIZE = 500;
            final String[] threadErrors = new String[THREAD_SIZE];
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                final int threadNo = i;
                threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        String errorMsg = processQueryAndDetachOneManyFKBidir(true);
                        threadErrors[threadNo] = errorMsg;
                    }
                });
            }

            // Run the threads
            LOG.debug(">> Starting threads");
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                threads[i].start();
            }
            for (int i = 0; i < THREAD_SIZE; i++)
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
            LOG.debug(">> Completed threads");

            // Process any errors and fail the test if any threads failed present
            for (String error : threadErrors)
            {
                if (error != null)
                {
                    fail(error);
                }
            }
        }
        finally
        {
            // Clean out data
            clean(Animal.class);
            clean(Farm.class);
        }
    }

    protected String processQueryAndDetachOneManyFKBidir(boolean transaction)
    {
        List<Animal> results = null;
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try
        {
            if (transaction)
            {
                tx.begin();
            }
            Query q = em.createQuery("SELECT a FROM " + Animal.class.getName() + " a");
            List<Animal> animals = q.getResultList();
            for (Animal a : animals)
            {
                Farm f = a.getFarm();
                f.getAnimals();
            }
            results = new ArrayList<Animal>(animals);
            if (transaction)
            {
                tx.commit();
            }
        }
        catch (Throwable thr)
        {
            LOG.error("Exception query objects", thr);
            return "Exception in query : " + thr.getMessage();
        }
        finally
        {
            if (transaction && tx.isActive())
            {
                tx.rollback();
            }
            em.close(); // Detached the Animals and their loaded fields
        }

        for (Animal a : results)
        {
            try
            {
                LOG.debug(">> Animal: " + a);
                if (a.getFarm() == null)
                {
                    return "Animal has null Farm";
                }
                Farm f = a.getFarm();
                if (f.getAnimals() == null || f.getAnimals().size() != 100)
                {
                    return "Animal has Farm with incorrect Animals";
                }
            }
            catch (Exception exc)
            {
                LOG.error(">> Exception thrown on check of results", exc);
                return "Exception checking results : " + exc.getMessage();
            }
        }
        return null;
    }

    /**
     * Test that populates the datastore, and then starts many threads retrieving objects.
     */
    public void testFind()
    {
        try
        {
            // Persist some data
            Object mgrId = persistManagerWithEmployees();

            // Create the Threads
            int THREAD_SIZE = 500;
            final Object managerId = mgrId;
            final String[] threadErrors = new String[THREAD_SIZE];
            Thread[] threads = new Thread[THREAD_SIZE];
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                final int threadNo = i;
                threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        String errorMsg = processFind(managerId, true);
                        threadErrors[threadNo] = errorMsg;
                    }
                });
            }

            // Run the threads
            LOG.debug(">> Starting threads");
            for (int i = 0; i < THREAD_SIZE; i++)
            {
                threads[i].start();
            }
            for (int i = 0; i < THREAD_SIZE; i++)
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
            LOG.debug(">> Completed threads");

            // Process any errors and fail the test if any threads failed present
            for (String error : threadErrors)
            {
                if (error != null)
                {
                    fail(error);
                }
            }
        }
        finally
        {
            // Clean out data
            clean(Manager.class);
            clean(Employee.class);
        }
    }

    protected String processFind(Object mgrId, boolean transaction)
    {
        EntityManager em = emf.createEntityManager();
        emf.getCache().evictAll();
        EntityTransaction tx = em.getTransaction();
        try
        {
            if (transaction)
            {
                tx.begin();
            }
            Manager mgr = em.find(Manager.class, mgrId);
            if (mgr == null)
            {
                return "Manager not found!";
            }
            if (!("The".equals(mgr.getFirstName())))
            {
                return "Manager first name is wrong";
            }
            if (!("Boss".equals(mgr.getLastName())))
            {
                return "Manager last name is wrong";
            }
            Set<Employee> emps = mgr.getSubordinates();
            if (emps == null)
            {
                return "Manager has null subordinates!";
            }
            else if (emps.size() != 100)
            {
                return "Manager has incorrect number of subordinates (" + emps.size() + ")";
            }

            if (transaction)
            {
                tx.commit();
            }
        }
        catch (Throwable thr)
        {
            LOG.error("Exception in find", thr);
            return "Exception in find : " + thr.getMessage();
        }
        finally
        {
            if (transaction && tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        return null;
    }

    /**
     * Convenience method to create a Manager with 100 Employees. Used by various tests in this file
     * @return The id of the manager object
     */
    private Object persistManagerWithEmployees()
    {
        // Persist some data
        LOG.debug(">> Persisting data");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Object mgrId = null;
        try
        {
            tx.begin();
            Manager mgr = new Manager(1, "The", "Boss", "the.boss@datanucleus.com", 200000, "100000");
            em.persist(mgr);
            for (int i=0;i<100;i++)
            {
                Employee emp = new Employee(i+2, "FirstName"+i, "LastName"+i,
                    "first.last." + i + "@datanucleus.com", 100000+i, "12345" + i);
                emp.setManager(mgr);
                mgr.addSubordinate(emp);
                em.persist(emp);
            }

            tx.commit();
            mgrId = mgr.getPK();
        }
        catch (Throwable thr)
        {
            LOG.error("Exception persisting objects", thr);
            fail("Exception persisting data : " + thr.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        LOG.debug(">> Persisted data");

        // Verify the persistence
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try
        {
            tx.begin();
            Query q = em.createQuery("SELECT e FROM " + Employee.class.getName() + " e");
            List<Employee> emps = q.getResultList();
            for (Employee e : emps)
            {
                LOG.debug(">> emp=" + e + " e.mgr=" + e.getManager());
            }
            LOG.debug(">> Queried Employees");
            tx.commit();
        }
        catch (Throwable thr)
        {
            LOG.error("Exception checking objects", thr);
            fail("Exception checking data : " + thr.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }

        return mgrId;
    }

    /**
     * Convenience method to create a Farm with 100 Animals. Used by various tests in this file
     * @return The id of the Farm object
     */
    private Object persistFarmWithAnimals()
    {
        LOG.debug(">> Persisting data");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        Object farmId = "Jones Farm";
        try
        {
            tx.begin();
            Farm farm = new Farm("Jones Farm");
            em.persist(farm);
            for (int i=0;i<100;i++)
            {
                Animal an = new Animal("Sheep" + i);
                an.setFarm(farm);
                farm.getAnimals().add(an);
                em.persist(an);
            }

            tx.commit();
        }
        catch (Throwable thr)
        {
            LOG.error("Exception persisting objects", thr);
            fail("Exception persisting data : " + thr.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        LOG.debug(">> Persisted data");

        // Verify the persistence
        em = emf.createEntityManager();
        tx = em.getTransaction();
        try
        {
            tx.begin();
            LOG.debug(">> Querying Animals");
            Query q = em.createQuery("SELECT a FROM " + Animal.class.getName() + " a");
            List<Animal> ans = q.getResultList();
            for (Animal a : ans)
            {
                LOG.debug(">> animal=" + a + " farm=" + a.getFarm());
            }
            LOG.debug(">> Queried Animals");
            tx.commit();
        }
        catch (Throwable thr)
        {
            LOG.error("Exception checking objects", thr);
            fail("Exception checking data : " + thr.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        return farmId;
    }
}