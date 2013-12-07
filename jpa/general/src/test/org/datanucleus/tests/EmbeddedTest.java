/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.embedded.Job;
import org.jpox.samples.annotations.embedded.Processor;
import org.jpox.samples.annotations.models.company.Department;
import org.jpox.samples.annotations.models.company.DepartmentPK;
import org.jpox.samples.annotations.models.company.Project;

/**
 * Tests for embedded persistence in JPA.
 * This includes embedded objects, and embedded identity.
 */
public class EmbeddedTest extends JPAPersistenceTestCase
{
    public EmbeddedTest(String name)
    {
        super(name);
    }

    /**
     * Test for basic persistence and deletion with a class using @EmbeddedId.
     */
    public void testPersistenceAndDeletionWithEmbeddedId()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                // Persist the object and query it
                tx.begin();
                Department d = new Department("Bureaucracy");
                DepartmentPK pk = new DepartmentPK(Integer.valueOf(1), "1");
                d.setPrimaryKey(pk);
                em.persist(d);

                List result = em.createQuery(
                    "SELECT Object(T) FROM " + Department.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                assertEquals(pk.getIdString(), ((Department)result.get(0)).getPrimaryKey().getIdString());
                tx.commit();

                // Retrieve the object and delete it
                tx.begin();
                result = em.createQuery("SELECT Object(T) FROM " + Department.class.getName() + " T").getResultList();
                d = (Department)result.get(0);
                em.remove(d);
                em.flush();
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist and query", e);
                fail("Exception during persist + query : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Department.class);
        }
    }    

    /**
     * Test of 1-N relation when the owner has an embedded id.
     */
    public void testOneToManyWithEmbeddedId()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Department dept = new Department("Marketing");
                DepartmentPK deptPK = new DepartmentPK(101, "Mkt");
                dept.setPrimaryKey(deptPK);
                Project prj1 = new Project("DN 2.0", 100000);
                dept.getProjects().add(prj1);
                em.persist(dept);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown creating data", e);
                fail("Exception thrown while creating data (see log for details) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Query q = em.createQuery("SELECT d FROM " + Department.class.getName() + " d");
                List<Department> depts = q.getResultList();
                assertNotNull("Returned Department List is null", depts);
                assertEquals("Number of Departments is incorrect", 1, depts.size());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown retrieving data", e);
                fail("Exception thrown while retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Department.class);
            clean(Project.class);
        }
    }

    /**
     * Test of 1-N relation with embedded elements.
     */
    public void testOneToManyEmbeddedElements()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                Processor proc = new Processor(1, "RISC");
                Job job1 = new Job("Cron backup", 1);
                proc.addJob(job1);
                Job job2 = new Job("Jenkins", 2);
                proc.addJob(job2);
                em.persist(proc);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown creating data", e);
                fail("Exception thrown while creating data (see log for details) : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Processor proc = em.find(Processor.class, 1);
                assertNotNull(proc);
                List<Job> jobs = proc.getJobs();
                assertNotNull(jobs);
                assertEquals(2, jobs.size());

                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown retrieving data", e);
                fail("Exception thrown while retrieving data " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(Processor.class);
        }
    }
}