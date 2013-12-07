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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class BasicTest extends JDOPersistenceTestCase
{
    Object id;
    Object id2;
    public BasicTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(GroupUnique.class);
        clean(Person.class);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(1);
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setAge(34);
            p.setGender(Gender.male);
            pm.makePersistent(p);
            id = pm.getObjectId(p);
            PersonWithPassword pwp = new PersonWithPassword();
            pwp.setPersonNum(2);
            pwp.setFirstName("Daffy");
            pwp.setLastName("Duck");
            pwp.setAge(43);
            pwp.setPassword("secret");
            pm.makePersistent(pwp);
            pwp.setGender(Gender.male);
            id2 = pm.getObjectId(pwp);
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
        clean(GroupUnique.class);
        clean(Person.class);
        clean(PersonWithPassword.class);
        super.tearDown();
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
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("Age persisted is incorrect", 34, p1.getAge());
            assertEquals(Gender.male, p1.getGender());
            p1.setFirstName("BBB");
            p1.setGender(Gender.female);
            PersonWithPassword p2 = (PersonWithPassword) pm.getObjectById(id2);
            assertEquals(Gender.male, p2.getGender());
            p2.setFirstName("DDD");
            p2.setGender(Gender.female);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            p1 = (Person) pm.getObjectById(id);
            assertEquals("BBB", p1.getFirstName());
            assertEquals(Gender.female, p1.getGender());
            p1.setFirstName("Bugs");
            p2 = (PersonWithPassword) pm.getObjectById(id2);
            assertEquals("DDD", p2.getFirstName());
            assertEquals(Gender.female, p2.getGender());
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
     * Test that adds a new attribute to LDAP
     */
    public void testUpdateFieldNoLongerNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id);
            p.setEmailAddress("Bugs.Bunny@example.com");
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("Bugs.Bunny@example.com", p1.getEmailAddress());
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
     * Test setting a field to null, removes the attribute from LDAP
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
            if (tx.isActive())
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
            assertEquals(1L, p1.getPersonNum());
            assertEquals(34, p1.getAge());
            assertEquals(Gender.male, p1.getGender());
            PersonWithPassword p2 = (PersonWithPassword) pm.getObjectById(id2);
            assertEquals("Daffy", p2.getFirstName());
            assertEquals("Duck", p2.getLastName());
            assertEquals(2L, p2.getPersonNum());
            assertEquals(43, p2.getAge());
            assertEquals("secret", p2.getPassword());
            assertEquals(Gender.male, p2.getGender());
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
     * Test that adds a new attribute to LDAP
     */
    public void testInsertReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(3);
            p.setFirstName("Bart");
            p.setLastName("Simpson");
            GroupUnique gu = new GroupUnique();
            gu.setMember(p);
            pm.makePersistent(gu);
            Object id = pm.getObjectId(gu);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            gu = (GroupUnique) pm.getObjectById(id);
            assertNotNull(gu.getMember());
            assertEquals(gu.getMember().getFirstName(), "Bart");
            pm.deletePersistent(gu.getMember());
            pm.deletePersistent(gu);
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
     * Test update a reference field
     */
    public void testUpdateReference()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = new Person();
            p.setPersonNum(4);
            p.setFirstName("Homer");
            p.setLastName("Simpson");
            GroupUnique gu = new GroupUnique();
            gu.setMember(p);
            pm.makePersistent(gu);
            Object id = pm.getObjectId(gu);
            Object idp = pm.getObjectId(p);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            gu = (GroupUnique) pm.getObjectById(id);
            assertEquals("Homer", gu.getMember().getFirstName());
            p = new Person();
            p.setPersonNum(5);
            p.setFirstName("Lisa");
            p.setLastName("Simpson");
            gu.setMember(p);
            tx.commit();
            pm.close();

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            gu = (GroupUnique) pm.getObjectById(id);
            assertEquals("Lisa", gu.getMember().getFirstName());
            pm.deletePersistent(gu.getMember());
            pm.deletePersistent(gu);
            p = (Person) pm.getObjectById(idp);
            assertEquals("Homer", p.getFirstName());
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

    public void testDetach()
    {
        Person p = new Person();
        p.setPersonNum(5);
        p.setFirstName("Donald");
        p.setLastName("Duck");
        p.setAge(42);

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setDetachAllOnCommit(true);
        Transaction tx = pm.currentTransaction();
        Object id = null;
        try
        {
            tx.begin();
            assertTrue("New object is persistent!", !JDOHelper.isPersistent(p));
            LOG.info(">> Persisting Person");
            pm.makePersistent(p);
            assertTrue("Newly persisted object is not persistent!", JDOHelper.isPersistent(p));
            tx.commit();
            assertTrue("Newly persisted object is not detached!", JDOHelper.isDetached(p));
            id = JDOHelper.getObjectId(p);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        pm.setDetachAllOnCommit(true);
        tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p2 = (Person)pm.getObjectById(id);
            assertTrue("Retrieved object is not persistent", JDOHelper.isPersistent(p2));
            tx.commit();
            assertTrue("Retrieved object is not detached", JDOHelper.isDetached(p2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    
    public void testDetach2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person p = (Person) pm.getObjectById(id);
            assertEquals("Age persisted is incorrect", 34, p.getAge());
            Person detachedP = pm.detachCopy(p);
            tx.commit();
            pm.close();

            detachedP.setFirstName("BBB");
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedP);
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
}