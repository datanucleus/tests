/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.hierarchical_at_child_unidir;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDODataStoreException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests hierarchical mapping of recursive object graph
 * 
 * <pre>
 * ou=Dep-1
 *   ou=Dep-1-1
 *     ou=Dep-1-1-1
 *     ou=Dep-1-1-2
 *   cn=Dep-1-2
 * ou=Dep-2
 *</pre>
 */
public class RecursiveTest extends JDOPersistenceTestCase
{

    public RecursiveTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Department.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Department.class);
        super.tearDown();
    }

    public void testPersist()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Department dep1 = new Department("Dep-1", null);
            Department dep11 = new Department("Dep-1-1", dep1);
            Department dep111 = new Department("Dep-1-1-1", dep11);
            Department dep2 = new Department("Dep-2", null);
            pm.makePersistent(dep111);
            pm.makePersistent(dep2);
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

    public void testLoop()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Department dep1 = new Department("Dep-1", null);
            Department dep2 = new Department("Dep-2", null);
            dep1.setSuperDepartment(dep2);
            dep2.setSuperDepartment(dep1);
            try
            {
                pm.makePersistent(dep1);
                pm.makePersistent(dep2);
                tx.commit();
                fail("Should fail, recursive loop in hierarchical mapping.");
            }
            catch (JDODataStoreException e)
            {
                // expected
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

    public void testCRUD()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // create
            tx.begin();
            Department dep1 = new Department("Dep-1", null);
            Department dep11 = new Department("Dep-1-1", dep1);
            Department dep111 = new Department("Dep-1-1-1", dep11);
            Department dep112 = new Department("Dep-1-1-2", dep11);
            Department dep12 = new Department("Dep-1-2", dep1);
            Department dep2 = new Department("Dep-2", null);
            pm.makePersistent(dep111);
            pm.makePersistent(dep112);
            pm.makePersistent(dep12);
            pm.makePersistent(dep2);
            tx.commit();
            pm.close();

            // read all using query and extend
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Collection<Department> c = (Collection<Department>) pm.newQuery(Department.class).execute();
            assertEquals(6, c.size());
            Extent<Department> extent = pm.getExtent(Department.class);
            int count = 0;
            Iterator iter = extent.iterator();
            while (iter.hasNext())
            {
                iter.next();
                count++;
            }
            assertEquals(6, count);
            tx.commit();
            pm.close();

            // get and update
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            assertNull(dep111.getDescription());
            assertNotNull(dep111.getSuperDepartment());
            assertNotNull(dep111.getSuperDepartment().getSuperDepartment());
            assertNull(dep111.getSuperDepartment().getSuperDepartment().getSuperDepartment());
            dep111.setDescription("Description of Dep-1-1-1");
            dep111.getSuperDepartment().setDescription("Description of Dep-1-1");
            dep111.getSuperDepartment().getSuperDepartment().setDescription("Description of Dep-1");
            tx.commit();
            pm.close();

            // assert updated
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep1 = pm.getObjectById(Department.class, "Dep-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            assertEquals("Description of Dep-1", dep1.getDescription());
            assertEquals("Description of Dep-1-1", dep11.getDescription());
            assertEquals("Description of Dep-1-1-1", dep111.getDescription());
            tx.commit();
            pm.close();

            // delete
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            pm.deletePersistent(dep11);
            tx.commit();

            // assert deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                pm.getObjectById(Department.class, "Dep-1-1");
                fail("Dep-1-1 is deleted");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            pm.getObjectById(Department.class, "Dep-1");
            pm.getObjectById(Department.class, "Dep-1-2");
            pm.getObjectById(Department.class, "Dep-2");
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

    public void _testUpdateParentReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // create
            tx.begin();
            Department dep1 = new Department("Dep-1", null);
            Department dep11 = new Department("Dep-1-1", dep1);
            Department dep111 = new Department("Dep-1-1-1", dep11);
            Department dep112 = new Department("Dep-1-1-2", dep11);
            Department dep12 = new Department("Dep-1-2", dep1);
            Department dep2 = new Department("Dep-2", null);
            pm.makePersistent(dep111);
            pm.makePersistent(dep112);
            pm.makePersistent(dep12);
            pm.makePersistent(dep2);
            tx.commit();
            pm.close();

            // assert initial references
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep1 = pm.getObjectById(Department.class, "Dep-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            dep112 = pm.getObjectById(Department.class, "Dep-1-1-2");
            dep12 = pm.getObjectById(Department.class, "Dep-1-2");
            dep2 = pm.getObjectById(Department.class, "Dep-2");
            assertNull(dep1.getSuperDepartment());
            assertEquals(dep1, dep11.getSuperDepartment());
            assertEquals(dep11, dep111.getSuperDepartment());
            assertEquals(dep11, dep112.getSuperDepartment());
            assertEquals(dep1, dep12.getSuperDepartment());
            assertNull(dep2.getSuperDepartment());

            // move Dep-1-1 from Dep-1 to Dep-2
            dep11.setSuperDepartment(dep2);
            tx.commit();
            pm.close();

            // assert changed reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep1 = pm.getObjectById(Department.class, "Dep-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            dep112 = pm.getObjectById(Department.class, "Dep-1-1-2");
            dep12 = pm.getObjectById(Department.class, "Dep-1-2");
            dep2 = pm.getObjectById(Department.class, "Dep-2");
            assertNull(dep1.getSuperDepartment());
            assertEquals(dep2, dep11.getSuperDepartment()); // this has changed
            assertEquals(dep11, dep111.getSuperDepartment());
            assertEquals(dep11, dep112.getSuperDepartment());
            assertEquals(dep1, dep12.getSuperDepartment());
            assertNull(dep2.getSuperDepartment());

            // move to top
            dep11.setSuperDepartment(null);
            tx.commit();

            // assert changed reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep1 = pm.getObjectById(Department.class, "Dep-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            dep112 = pm.getObjectById(Department.class, "Dep-1-1-2");
            dep12 = pm.getObjectById(Department.class, "Dep-1-2");
            dep2 = pm.getObjectById(Department.class, "Dep-2");
            assertNull(dep1.getSuperDepartment());
            assertNull(dep11.getSuperDepartment()); // this has changed
            assertEquals(dep11, dep111.getSuperDepartment());
            assertEquals(dep11, dep112.getSuperDepartment());
            assertEquals(dep1, dep12.getSuperDepartment());
            assertNull(dep2.getSuperDepartment());

            // move to leaf
            dep11.setSuperDepartment(dep12);
            tx.commit();

            // assert changed reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            dep1 = pm.getObjectById(Department.class, "Dep-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            dep112 = pm.getObjectById(Department.class, "Dep-1-1-2");
            dep12 = pm.getObjectById(Department.class, "Dep-1-2");
            dep2 = pm.getObjectById(Department.class, "Dep-2");
            assertNull(dep1.getSuperDepartment());
            assertEquals(dep12, dep11.getSuperDepartment()); // this has changed
            assertEquals(dep11, dep111.getSuperDepartment());
            assertEquals(dep11, dep112.getSuperDepartment());
            assertEquals(dep1, dep12.getSuperDepartment());
            assertNull(dep2.getSuperDepartment());

            // create cycle: move into own???
            // TODO: activate later, error in ApacheDS 1.5.5 corrupts its database
            // dep11.setSuperDepartment(dep111);
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

    public void _testUpdateParentReferenceDetached()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // create
            tx.begin();
            Department dep1 = new Department("Dep-1", null);
            Department dep11 = new Department("Dep-1-1", dep1);
            Department dep111 = new Department("Dep-1-1-1", dep11);
            Department dep112 = new Department("Dep-1-1-2", dep11);
            Department dep12 = new Department("Dep-1-2", dep1);
            Department dep2 = new Department("Dep-2", null);
            pm.makePersistent(dep111);
            pm.makePersistent(dep112);
            pm.makePersistent(dep12);
            pm.makePersistent(dep2);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(10);
            tx = pm.currentTransaction();
            tx.begin();
            dep111 = pm.getObjectById(Department.class, "Dep-1-1-1");
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            dep2 = pm.getObjectById(Department.class, "Dep-2");
            Department detachedDep111 = pm.detachCopy(dep111);
            Department detachedDep11 = pm.detachCopy(dep11);
            Department detachedDep2 = pm.detachCopy(dep2);
            tx.commit();
            pm.close();

            // assert all super departments fields were detached recursively
            assertNull(detachedDep111.getDescription());
            assertNotNull(detachedDep111.getSuperDepartment());
            assertNotNull(detachedDep111.getSuperDepartment().getSuperDepartment());
            assertNull(detachedDep111.getSuperDepartment().getSuperDepartment().getSuperDepartment());

            // assert Dep-1 is super department of Dep-1-1
            assertNotNull(detachedDep11.getSuperDepartment());
            assertEquals("Dep-1", detachedDep11.getSuperDepartment().getName());

            // move Dep-1-1 from Dep-1 to Dep-2
            detachedDep11.setSuperDepartment(detachedDep2);

            // attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(10);
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDep11);
            tx.commit();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(10);
            tx = pm.currentTransaction();
            tx.begin();
            dep11 = pm.getObjectById(Department.class, "Dep-1-1");
            detachedDep11 = pm.detachCopy(dep11);
            tx.commit();

            // assert Dep-1-1 was really moved
            assertNotNull(detachedDep11.getSuperDepartment());
            assertEquals("Dep-2", detachedDep11.getSuperDepartment().getName());
            assertNull(detachedDep11.getSuperDepartment().getSuperDepartment());
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
