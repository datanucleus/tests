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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.samples.models.company.CompanyHelper;
import org.datanucleus.samples.models.company.Developer;
import org.datanucleus.samples.models.company.Employee;
import org.datanucleus.samples.models.company.Manager;

/**
 * Tests for JDO when run in a multithreaded environment (PersistenceManager per Thread).
 */
public class MultithreadedTest extends JDOPersistenceTestCase
{
    public MultithreadedTest(String name)
    {
        super(name);
    }

    /**
     * Test that populates the datastore, and then starts many threads querying and detaching the data
     * and trying to access the detached data (checking for undetached fields that should have been detached).
     */
    public void testQueryAndDetach()
    {
        addClassesToSchema(new Class[] {Employee.class, Manager.class, Developer.class});
        try
        {
            // Persist some data
            LOG.debug(">> Persisting data");
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_DETACH_ON_CLOSE, "true");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Manager mgr = new Manager(1, "The", "Boss", "the.boss@datanucleus.com", 200000, "100000");
                pm.makePersistent(mgr);
                for (int i=0;i<100;i++)
                {
                    Employee emp = new Employee(i+2, "FirstName"+i, "LastName"+i,
                        "first.last." + i + "@datanucleus.com", 100000+i, "12345" + i);
                    emp.setManager(mgr);
                    mgr.addSubordinate(emp);
                    pm.makePersistent(emp);
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
                pm.close();
            }
            LOG.debug(">> Persisted data");

            // Verify the persistence
            pm = pmf.getPersistenceManager();
            pm.setProperty(PropertyNames.PROPERTY_DETACH_ON_CLOSE, "true");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LOG.debug(">> Querying Employees");
                Query<Employee> q = pm.newQuery(Employee.class);
                List<Employee> emps = q.executeList();
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
                pm.close();
            }

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
                        String errorMsg = processQueryAndDetach(true);
                        threadErrors[threadNo] = errorMsg;
                    }
                });
            }

            // Run the Threads
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    protected String processQueryAndDetach(boolean transaction)
    {
        List<Employee> results = null;
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_DETACH_ON_CLOSE, "true");
        pm.getFetchPlan().setGroup("all");
        pm.getFetchPlan().setMaxFetchDepth(-1);
        Transaction tx = pm.currentTransaction();
        try
        {
            if (transaction)
            {
                tx.begin();
            }
            Query<Employee> q = pm.newQuery(Employee.class);
            List<Employee> emps = q.executeList();
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
            pm.close(); // Detached the Employees and their loaded fields
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
                return "Exception thrown : " + exc.getMessage();
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
            LOG.debug(">> Persisting data");
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object mgrId = null;
            try
            {
                tx.begin();
                Manager mgr = new Manager(1, "The", "Boss", "the.boss@datanucleus.com", 200000, "100000");
                pm.makePersistent(mgr);
                for (int i=0;i<100;i++)
                {
                    Employee emp = new Employee(i+2, "FirstName"+i, "LastName"+i,
                        "first.last." + i + "@datanucleus.com", 100000+i, "12345" + i);
                    emp.setManager(mgr);
                    mgr.addSubordinate(emp);
                    pm.makePersistent(emp);
                }

                tx.commit();
                mgrId = JDOHelper.getObjectId(mgr);
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
                pm.close();
            }
            LOG.debug(">> Persisted data");

            // Verify the persistence
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query<Employee> q = pm.newQuery(Employee.class);
                List<Employee> emps = q.executeList();
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
                pm.close();
            }

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

            // Run the Threads
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
            CompanyHelper.clearCompanyData(pmf);
        }
    }

    protected String processFind(Object mgrId, boolean transaction)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pmf.getDataStoreCache().evictAll();
        Transaction tx = pm.currentTransaction();
        try
        {
            if (transaction)
            {
                tx.begin();
            }
            Manager mgr = (Manager)pm.getObjectById(mgrId);
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
            pm.close();
        }
        return null;
    }
}