/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.knownbugs;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Department;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.models.company.Project;
import org.neodatis.odb.OdbConfiguration;

public class JDOQLBasicTest extends JDOPersistenceTestCase
{
    Object[] id = new Object[3];

    public JDOQLBasicTest(String name) throws Exception
    {
        super(name);
        OdbConfiguration.setLogServerStartupAndShutdown(false);
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(4);
            p.setGlobalNum("4");
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setAge(34);
            pm.makePersistent(p);
            //id[0] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(5);
            p.setGlobalNum("5");
            p.setFirstName("Ana");
            p.setLastName("Hick");
            p.setAge(27);
            pm.makePersistent(p);
            //id[1] = pm.getObjectId(p);
            p = new Person();
            p.setPersonNum(3);
            p.setGlobalNum("3");
            p.setFirstName("Lami");
            p.setLastName("Puxa");
            p.setAge(23);
            pm.makePersistent(p);
            //id[2] = pm.getObjectId(p);
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

    protected void tearDown() throws Exception
    {
        super.tearDown();
        clean(Person.class);
    }

    public void testCollectionContains()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Department dept1 = new Department("Marketing");
            Department dept2 = new Department("Sales");
            Project prj1 = new Project("Project X", 150000);
            Project prj2 = new Project("Project Y", 200000);
            Project prj3 = new Project("Project Z", 500000);
            dept1.addProject(prj1);
            dept2.addProject(prj2);
            dept2.addProject(prj3);
            pm.makePersistent(dept1);
            pm.makePersistent(dept2);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(Department.class);
            q.setFilter("projects.contains(prj) && prj.name == 'Project Y'");
            q.declareVariables(Project.class.getName() + " prj");
            Collection c = (Collection) q.execute();
            assertEquals(1, c.size());
            Iterator it = c.iterator();
            assertEquals("Sales", ((Department)it.next()).getName());
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
}