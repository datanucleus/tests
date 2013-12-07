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
2012 Andy Jefferson - 1-1 test
   ...
***********************************************************************/
package org.datanucleus.tests;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.samples.directory.Group;
import org.datanucleus.samples.directory.GroupUnique;
import org.datanucleus.samples.directory.Person;
import org.datanucleus.samples.directory.PhoneNumber;

public class BasicTest extends JSONTestCase
{
    Object id;
    public BasicTest(String name) throws IOException
    {
        super(name);
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
            p.setPersonNum(1);
            p.setGlobalNum("1");
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setAge(7);
            p.setBigint(BigInteger.TEN);
            p.setDecimal(BigDecimal.ONE);
            pm.makePersistent(p);
            id = pm.getObjectId(p);
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
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Person p = (Person) pm.getObjectById(id);
                pm.deletePersistent(p);
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
        finally
        {
            super.tearDown();
        }
    }

    /**
     * Test update a field to another value
     */
    public void testUpdateFieldToAnotherValue()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id);
            p.setFirstName("BBB");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("BBB", p1.getFirstName());
            p1.setFirstName("Bugs");
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
    
    /**
     * Test delete
     */
    public void testDelete()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(1);
            p.setGlobalNum("8");
            p.setFirstName("G8");
            p.setLastName("B8");
            pm.makePersistent(p);
            Object id = pm.getObjectId(p);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p = (Person) pm.getObjectById(id);
            pm.deletePersistent(p);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                Person p1 = (Person) pm.getObjectById(id);
                fail("Expected JDOObjectNotFoundException, but object is: "+p1.getLastName());
            }
            catch(JDOObjectNotFoundException ex)
            {
                //expected
            }
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
    

    /**
     * Test that sets a field that was previously null.
     */
    public void testUpdateFieldNoLongerNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id);
            p.setGlobalNum("1");
            p.setEmailAddress("ppp");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("1", p1.getGlobalNum());
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

    /**
     * Test setting a field to null.
     */
    public void testUpdateFieldSetNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id);
            p.setEmailAddress("ppp");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("ppp", p1.getEmailAddress());
            p1.setEmailAddress(null);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p1 = (Person) pm.getObjectById(id);
            assertNull(p1.getEmailAddress());
            p.setEmailAddress(null);
            tx.commit();
        }
        finally
        {
            if( tx.isActive() )
            {
                tx.rollback();
            }
            pm.close();
        }
    }
    
    /**
     * Fetch objects and assert basic values
     */
    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("Bugs", p1.getFirstName());
            assertEquals("Bunny", p1.getLastName());
            assertEquals(7, p1.getAge());
            assertEquals(BigInteger.TEN, p1.getBigint());
            assertEquals(BigDecimal.ONE, p1.getDecimal());
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

    /**
     * Test persist multiple records.
     */
    public void testPersistMultiple()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object[] ids = new Object[4];
        try
        {
            tx.begin();
            for (int i=2;i<6;i++)
            {
                Person p = new Person();
                p.setPersonNum(i);
                p.setGlobalNum("" + i);
                p.setFirstName("Bugs"+i);
                p.setLastName("Bunny");
                p.setAge(7+i);
                p.setBigint(BigInteger.TEN);
                p.setDecimal(BigDecimal.ONE);
                pm.makePersistent(p);
                ids[i-2] = pm.getObjectId(p);
            }
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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Query q = pm.newQuery(Person.class);
            List<Person> people = (List<Person>) q.execute();
            assertEquals(5, people.size());
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            pm = pmf.getPersistenceManager();
            for (int i=0;i<4;i++)
            {
                pm.deletePersistent(pm.getObjectById(ids[i]));
            }
            pm.close();
        }
    }

    public void testPersistOneToOne()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object p1Id = null;
        Object p2Id = null;
        try
        {
            tx.begin();
            Person p1 = new Person();
            p1.setPersonNum(2);
            p1.setGlobalNum("2");
            p1.setFirstName("Roger");
            p1.setLastName("Rabbit");
            p1.setAge(15);
            Person p2 = new Person();
            p2.setPersonNum(3);
            p2.setGlobalNum("3");
            p2.setFirstName("Donald");
            p2.setLastName("Duck");
            p2.setAge(16);
            p1.setBestFriend(p2);
            pm.makePersistent(p1);
            tx.commit();
            p1Id = pm.getObjectId(p1);
            p2Id = pm.getObjectId(p2);
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1a = (Person) pm.getObjectById(p1Id);
            Person p2a = p1a.getBestFriend();
            assertEquals("Roger", p1a.getFirstName());
            assertEquals("Rabbit", p1a.getLastName());
            assertNotNull("BestFriend of first object is null!", p2a);
            assertEquals("Donald", p2a.getFirstName());
            assertEquals("Duck", p2a.getLastName());
            assertNull("BestFriend of second object is not null!", p2a.getBestFriend());
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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p1 = (Person)pm.getObjectById(p1Id);
            Person p2 = (Person)pm.getObjectById(p2Id);
            pm.deletePersistent(p1);
            pm.deletePersistent(p2);
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

    public void testPersistOneToManyCollection()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object g1Id = null;
        Object p1Id = null;
        Object p2Id = null;
        try
        {
            tx.begin();
            Group g1 = new Group();
            g1.setName("FirstGroup");

            Person p1 = new Person();
            p1.setPersonNum(2);
            p1.setGlobalNum("2");
            p1.setFirstName("Roger");
            p1.setLastName("Rabbit");
            p1.setAge(15);

            Person p2 = new Person();
            p2.setPersonNum(3);
            p2.setGlobalNum("3");
            p2.setFirstName("Donald");
            p2.setLastName("Duck");
            p2.setAge(16);

            g1.addUser(p1);
            g1.addUser(p2);

            pm.makePersistent(g1);
            tx.commit();
            g1Id = pm.getObjectId(g1);
            p1Id = pm.getObjectId(p1);
            p2Id = pm.getObjectId(p2);
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Group g1a = (Group) pm.getObjectById(g1Id);
            assertEquals("FirstGroup", g1a.getName());
            Collection<Person> users = g1a.getUsers();
            assertNotNull("Users of Group was null!", users);
            assertEquals(2, users.size());

            Person p1a = (Person) pm.getObjectById(p1Id);
            assertEquals("Roger", p1a.getFirstName());
            assertEquals("Rabbit", p1a.getLastName());
            assertTrue(users.contains(p1a));

            Person p2a = (Person) pm.getObjectById(p2Id);
            assertEquals("Donald", p2a.getFirstName());
            assertEquals("Duck", p2a.getLastName());
            assertTrue(users.contains(p2a));

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Group g1 = (Group)pm.getObjectById(g1Id);
            Person p1 = (Person)pm.getObjectById(p1Id);
            Person p2 = (Person)pm.getObjectById(p2Id);
            pm.deletePersistent(g1);
            pm.deletePersistent(p1);
            pm.deletePersistent(p2);
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

    public void testPersistCollectionNonPC()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object g1Id = null;
        try
        {
            tx.begin();
            Group g1 = new Group();
            g1.setName("FirstGroup");
            g1.addRole("Role1");
            g1.addRole("Role2");
            pm.makePersistent(g1);
            tx.commit();
            g1Id = pm.getObjectId(g1);
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Group g1a = (Group) pm.getObjectById(g1Id);
            assertEquals("FirstGroup", g1a.getName());
            Collection<String> roles = g1a.getRoles();
            assertNotNull("Roles of Group was null!", roles);
            assertEquals(2, roles.size());
            assertTrue("Role1 not present", roles.contains("Role1"));
            assertTrue("Role2 not present", roles.contains("Role2"));

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Group g1 = (Group)pm.getObjectById(g1Id);
            pm.deletePersistent(g1);
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

    public void testPersistMapNonPCNonPC()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object g1Id = null;
        try
        {
            tx.begin();
            Group g1 = new Group();
            g1.setName("FirstGroup");
            g1.addRoleMapEntry("Role1", "Value1");
            g1.addRoleMapEntry("Role2", "Value2");

            pm.makePersistent(g1);
            tx.commit();
            g1Id = pm.getObjectId(g1);
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Group g1a = (Group) pm.getObjectById(g1Id);
            assertEquals("FirstGroup", g1a.getName());
            Map<String,String> roleMap = g1a.getRoleMap();
            assertNotNull("RoleMap of Group was null!", roleMap);
            assertEquals(2, roleMap.size());
            assertTrue("Role1 not present", roleMap.containsKey("Role1"));
            assertTrue("Role2 not present", roleMap.containsKey("Role2"));
            assertEquals("Role1 incorrect", "Value1", roleMap.get("Role1"));
            assertEquals("Role2 not present", "Value2", roleMap.get("Role2"));

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Group g1 = (Group)pm.getObjectById(g1Id);
            pm.deletePersistent(g1);
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

    public void testPersistOneToManyMap()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Object p1Id = null;
        Object ph1Id = null;
        Object ph2Id = null;
        try
        {
            tx.begin();
            Person p1 = new Person();
            p1.setPersonNum(2);
            p1.setGlobalNum("2");
            p1.setFirstName("Roger");
            p1.setLastName("Rabbit");
            p1.setAge(15);

            PhoneNumber ph1 = new PhoneNumber("Donald Duck", "123456789");
            ph1.setId(1);
            PhoneNumber ph2 = new PhoneNumber("Goofy", "999999999");
            ph2.setId(2);
            p1.getPhoneNumbers().put(ph1.getName(), ph1);
            p1.getPhoneNumbers().put(ph2.getName(), ph2);
            pm.makePersistent(p1);
            tx.commit();
            p1Id = pm.getObjectId(p1);
            ph1Id = pm.getObjectId(ph1);
            ph2Id = pm.getObjectId(ph2);
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1a = (Person) pm.getObjectById(p1Id);
            assertEquals("Roger", p1a.getFirstName());
            assertEquals("Rabbit", p1a.getLastName());
            Map<String, PhoneNumber> numbers = p1a.getPhoneNumbers();
            assertNotNull("PhoneNumbers of Person was null!", numbers);
            assertEquals(2, numbers.size());

            PhoneNumber ph1a = (PhoneNumber) pm.getObjectById(ph1Id);
            assertEquals("Donald Duck", ph1a.getName());
            assertEquals("123456789", ph1a.getNumber());
            assertTrue(numbers.containsKey("Donald Duck"));

            PhoneNumber ph2a = (PhoneNumber) pm.getObjectById(ph2Id);
            assertEquals("Goofy", ph2a.getName());
            assertEquals("999999999", ph2a.getNumber());
            assertTrue(numbers.containsKey("Goofy"));

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p1 = (Person)pm.getObjectById(p1Id);
            PhoneNumber ph1 = (PhoneNumber)pm.getObjectById(ph1Id);
            PhoneNumber ph2 = (PhoneNumber)pm.getObjectById(ph2Id);
            pm.deletePersistent(p1);
            pm.deletePersistent(ph1);
            pm.deletePersistent(ph2);
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

    /**
     * Test datastore id.
     */
    public void testDatastoreIdentity()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            GroupUnique gr1 = new GroupUnique();
            gr1.setName("First Group");
            pm.makePersistent(gr1);
            GroupUnique gr2 = new GroupUnique();
            gr2.setName("Second Group");
            pm.makePersistent(gr2);
            Object id1 = pm.getObjectId(gr1);
            Object id2 = pm.getObjectId(gr2);
            tx.commit();
            pm.close();
            pmf.getDataStoreCache().evictAll();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            gr1 = (GroupUnique) pm.getObjectById(id1);
            assertEquals("First Group", gr1.getName());

            gr2 = (GroupUnique) pm.getObjectById(id2);
            assertEquals("Second Group", gr2.getName());
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                gr1 = (GroupUnique) pm.getObjectById(id1);
                gr2 = (GroupUnique) pm.getObjectById(id2);
                pm.deletePersistent(gr1);
                pm.deletePersistent(gr2);
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
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
        }
    }
}
