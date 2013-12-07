/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.samples.directory;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.datanucleus.tests.JDOPersistenceTestCase;


public class CollectionsTest extends JDOPersistenceTestCase
{
    Object id;
    Object[] pid = new Object[2];
    public CollectionsTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Group.class);
        clean(Person.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Group g = new Group();
            g.setName("group1");
            Person p = new Person();
            p.setPersonNum(1);
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            g.getUsers().add(p);
            Person p1 = new Person();
            p1.setPersonNum(2);
            p1.setFirstName("Fred");
            p1.setLastName("Beng");
            g.getUsers().add(p1);
            pm.makePersistent(g);
            pid[0] = pm.getObjectId(p);
            pid[1] = pm.getObjectId(p1);
            
            id = pm.getObjectId(g);
            pm.currentTransaction().commit();
        }
        finally
        {
            if( pm.currentTransaction().isActive() )
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    protected void tearDown() throws Exception
    {
        clean(Group.class);
        clean(Person.class);
        super.tearDown();
    }
    
    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Group g = (Group) pm.getObjectById(id);
            assertEquals(2, g.getUsers().size());
            Set expected = new HashSet();
            expected.add(pm.getObjectById(pid[0]));
            expected.add(pm.getObjectById(pid[1]));
            assertTrue(g.getUsers().containsAll(expected));
            pm.currentTransaction().commit();
        }
        finally
        {
            if( pm.currentTransaction().isActive() )
            {
                pm.currentTransaction().rollback();
            }
            pm.close();
        }
    }

    public void testModify()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Group g = (Group) pm.getObjectById(id);
            Person p0 = (Person) pm.getObjectById(pid[0]);
            Person p1 = (Person) pm.getObjectById(pid[1]);
            assertEquals(2, g.getUsers().size());

            // remove user1
            g.getUsers().remove(p1);
            pm.currentTransaction().commit();
            
            // check
            pm.currentTransaction().begin();
            g = (Group) pm.getObjectById(id);
            p0 = (Person) pm.getObjectById(pid[0]);
            p1 = (Person) pm.getObjectById(pid[1]);
            assertEquals(1, g.getUsers().size());
            
            // add user1 again
            g.getUsers().add(p1);
            pm.currentTransaction().commit();
            
            // check
            pm.currentTransaction().begin();
            g = (Group) pm.getObjectById(id);
            p0 = (Person) pm.getObjectById(pid[0]);
            p1 = (Person) pm.getObjectById(pid[1]);
            assertEquals(2, g.getUsers().size());

            p0.getAge(); // Added to avoid the unused variable compiler warning
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
    
}
