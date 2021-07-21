/**********************************************************************
Copyright (c) 2010 Erik Bengtson and others. All rights reserved.
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

import java.util.Iterator;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.ObjectState;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.samples.models.company.Department;
import org.datanucleus.samples.models.company.Manager;
import org.datanucleus.samples.models.company.Project;

/**
 * This class is a JUnit test class for unit testing the PersistenceManager.
 *
 * This tests the persistence of all of the basic FCO types as well as collection handling of various types of fields.
 */
public class PersistenceManagerTest extends JDOPersistenceTestCase
{

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
                LOG.error("Exception indata setup for makeTransient", e);
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
                LOG.error("Exception in makeTransient for graph", e);
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
                LOG.error("Exception in check of makeTransient", e);
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
}