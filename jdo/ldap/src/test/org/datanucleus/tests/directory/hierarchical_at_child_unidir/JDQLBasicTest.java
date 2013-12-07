/**********************************************************************
Copyright (c) 2008 Stefan Seelmann and others. All rights reserved.
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

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests hierarchical mapping of N-1 relationship, using the following test data:
 * 
 * <pre>
 * ou=AR
 *   uid=bbunny
 *   uid=ahicks
 * ou=BR
 *   uid=lpuxa
 *</pre>
 */
public class JDQLBasicTest extends JDOPersistenceTestCase
{

    TestHelper helper = new TestHelper();

    public JDQLBasicTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Account.class);
        clean(Person.class);
        clean(OrgUnit.class);
        helper.setUp(pmf);
    }

    protected void tearDown() throws Exception
    {
        clean(Account.class);
        clean(Person.class);
        clean(OrgUnit.class);
        super.tearDown();
    }

    public void testBasicQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Collection c = (Collection) pm.newQuery(Person.class).execute();
            assertEquals(3, c.size());
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

    public void testOrdering()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            OrgUnit ar = pm.getObjectById(OrgUnit.class, "AR");
            OrgUnit br = pm.getObjectById(OrgUnit.class, "BR");

            Query q = pm.newQuery(Person.class);
            q.setOrdering("firstName");
            Collection c = (Collection) q.execute();
            assertEquals(3, c.size());

            Iterator iterator = c.iterator();
            Person ahicks = (Person) iterator.next();
            assertEquals("Ana Hicks", ahicks.getFullName());
            assertNotNull(ahicks.getOrgUnit());
            assertEquals(ar, ahicks.getOrgUnit());

            Person bbunny = (Person) iterator.next();
            assertEquals("Bugs Bunny", bbunny.getFullName());
            assertNotNull(bbunny.getOrgUnit());
            assertEquals(ar, bbunny.getOrgUnit());

            Person lpuxa = (Person) iterator.next();
            assertEquals("Lami Puxa", lpuxa.getFullName());
            assertNotNull(lpuxa.getOrgUnit());
            assertEquals(br, lpuxa.getOrgUnit());

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
