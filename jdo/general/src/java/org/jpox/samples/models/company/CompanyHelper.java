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
package org.jpox.samples.models.company;

import java.util.Iterator;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.tests.TestHelper;
import org.datanucleus.util.NucleusLogger;

public class CompanyHelper
{
    /** Log for unit testing. */
    protected static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.TEST");

    /**
     * Convenience method to clean out all Company data
     * @param pmf The PMF managing the company data
     */
    public static void clearCompanyData(PersistenceManagerFactory pmf)
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            // disassociate all Employees and Departments from their Managers
            tx.begin();

            Extent ext = pm.getExtent(Manager.class, false);
            Iterator it = ext.iterator();
            while (it.hasNext())
            {
                Manager mgr = (Manager) it.next();
                Set<Employee> subordinates = mgr.getSubordinates();
                if (subordinates != null && subordinates.size() > 0)
                {
                    Iterator<Employee> empIter = subordinates.iterator();
                    while (empIter.hasNext())
                    {
                        Employee emp = empIter.next();
                        emp.setManager(null);
                    }
                    mgr.clearSubordinates();
                }

                Iterator deptIter = mgr.getDepartments().iterator();
                while (deptIter.hasNext())
                {
                    Department dept = (Department) deptIter.next();
                    dept.setManager(null);
                    dept.getProjects().clear();
                }
                mgr.clearDepartments();
            }
            tx.commit();

            tx.begin();
            ext = pm.getExtent(Employee.class, true);
            it = ext.iterator();
            while (it.hasNext())
            {
                Employee emp = (Employee)it.next();
                emp.setAccount(null);
                emp.setManager(null);
            }
            tx.commit();

            tx.begin();
            ext = pm.getExtent(Person.class, true);
            it = ext.iterator();
            while (it.hasNext())
            {
                Person pers = (Person)it.next();
                pers.setBestFriend(null); // Clear link to best friend
            }
            tx.commit();

            // disassociate the Qualification and Person objects
            tx.begin();
            ext = pm.getExtent(Qualification.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                Qualification q = (Qualification) it.next();
                q.setPerson(null);
            }
            tx.commit();

            // disassociate the Manager objects
            tx.begin();
            ext = pm.getExtent(Manager.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                Manager mgr = (Manager) it.next();
                mgr.setManager(null);
            }
            tx.commit();

            // disassociate the Departments from the Offices
            tx.begin();
            ext = pm.getExtent(Office.class, false);
            it = ext.iterator();
            while (it.hasNext())
            {
                Office off = (Office) it.next();
                off.clearDepartments();
            }
            tx.commit();

            TestHelper.clean(pmf, Manager.class);
            TestHelper.clean(pmf, Employee.class);
            TestHelper.clean(pmf, Qualification.class);
            TestHelper.clean(pmf, Department.class);
            TestHelper.clean(pmf, Person.class);
            TestHelper.clean(pmf, Office.class);
            TestHelper.clean(pmf, Account.class);
            TestHelper.clean(pmf, Project.class);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during clear", e);
            throw new NucleusUserException("Exception thrown during clear() : " + e.getMessage());
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