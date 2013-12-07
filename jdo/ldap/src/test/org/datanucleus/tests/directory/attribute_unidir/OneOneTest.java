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
package org.datanucleus.tests.directory.attribute_unidir;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class OneOneTest extends JDOPersistenceTestCase
{
    public OneOneTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Address.class);
        clean(Computer.class);
        clean(Person.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Address.class);
        clean(Computer.class);
        clean(Person.class);
        super.tearDown();
    }

    public void testPersistPersonWithoutRef()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, null);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNull(daffyDuck.getAddress());
            assertNull(daffyDuck.getComputer());
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());
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

    public void testPersistPersonWithRef()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist using persistence-by-reachability
            tx.begin();
            Address address = new Address("D-City", "D-Street");
            Computer computer = new Computer("PC-1234", "Daffy Duck's computer");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", address, computer);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAddress());
            assertNotNull(daffyDuck.getComputer());
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());
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
     * Person-(1)----------------------------(1)-Address
     * <ul>
     * <li>The Person class has a reference to Address
     * <li>In LDAP the relation is stored at the Person side (attribute seeAlso)
     * </ul>
     */
    public void testOwnerAtReferencingSide()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            // persist
            tx.begin();
            Address address = new Address("D-City", "D-Street");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", address, null);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(address);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAddress());
            assertEquals("D-City", daffyDuck.getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getAddress().getStreet());
            assertNull(daffyDuck.getComputer());
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());

            // set reference to null
            daffyDuck.setAddress(null);
            tx.commit();
            pm.close();

            // test removed reference
            // ensure address was deleted as it is dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            address = null;
            try
            {
                pm.getObjectById(Address.class, "D-City");
                fail("Object 'D-City' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            assertNull(daffyDuck.getAddress());
            assertEquals("Daffy Duck", daffyDuck.getFullName());

            // set new reference
            Address address2 = new Address("X-City", "X-Street");
            daffyDuck.setAddress(address2);
            tx.commit();
            pm.close();

            // test new reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAddress());
            assertEquals("X-City", daffyDuck.getAddress().getCity());
            assertEquals("X-Street", daffyDuck.getAddress().getStreet());

            // set old reference again
            address = new Address("D-City", "D-Street");
            daffyDuck.setAddress(null);
            daffyDuck.setAddress(address);
            tx.commit();
            pm.close();

            // test old reference
            // ensure second address was deleted as it is dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            address2 = null;
            try
            {
                pm.getObjectById(Address.class, "X-City");
                fail("Object 'X-City' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            assertNotNull(daffyDuck.getAddress());
            assertEquals("D-City", daffyDuck.getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getAddress().getStreet());

            // delete address
            address = pm.getObjectById(Address.class, "D-City");
            pm.deletePersistent(address);
            tx.commit();
            pm.close();

            // test object is deleted and reference is removed
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            address = null;
            try
            {
                pm.getObjectById(Address.class, "D-City");
                fail("Object 'D-City' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            assertNull(daffyDuck.getAddress());

            // set new reference
            Address address3 = new Address("Z-City", "Z-Street");
            daffyDuck.setAddress(address3);
            tx.commit();
            pm.close();

            // test new reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getAddress());
            assertEquals("Z-City", daffyDuck.getAddress().getCity());
            assertEquals("Z-Street", daffyDuck.getAddress().getStreet());

            // delete person
            pm.deletePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test person is deleted
            // ensure dependent address is also deleted
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            try
            {
                pm.getObjectById(Address.class, "Z-City");
                fail("Object 'Z-City' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
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
     * Person-(1)----------------------------(1)-Computer
     * <ul>
     * <li>The Person class has a reference to Computer
     * <li>In LDAP the relation is stored at the *Computer* side (attribute owner)
     * </ul>
     */
    public void testOwnerAtReferencedSide()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Computer computer = new Computer("PC-1234", "Daffy Duck's computer");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", null, computer);
            pm.makePersistent(daffyDuck);
            pm.makePersistent(computer);
            tx.commit();
            pm.close();

            // test fetch
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNull(daffyDuck.getAddress());
            assertNotNull(daffyDuck.getComputer());
            assertEquals("PC-1234", daffyDuck.getComputer().getSerialNumber());
            assertNotNull(daffyDuck.getAccounts());
            assertTrue(daffyDuck.getAccounts().isEmpty());

            // set reference to null
            daffyDuck.setComputer(null);
            tx.commit();
            pm.close();

            // test nulled-out reference, referenced object must still exist
            // ensure computer wasn't deleted as it isn't dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            computer = pm.getObjectById(Computer.class, "PC-1234");
            assertNull(daffyDuck.getComputer());
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("PC-1234", computer.getSerialNumber());

            // set new reference
            Computer computer2 = new Computer("PC-2345", "Daffy Duck's new computer");
            daffyDuck.setComputer(computer2);
            tx.commit();
            pm.close();

            // test new reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertNotNull(daffyDuck.getComputer());
            assertEquals("PC-2345", daffyDuck.getComputer().getSerialNumber());
            assertEquals("Daffy Duck's new computer", daffyDuck.getComputer().getName());

            // set old reference again
            computer = pm.getObjectById(Computer.class, "PC-1234");
            daffyDuck.setComputer(computer);
            tx.commit();
            pm.close();

            // test old reference
            // ensure 2nd computer wasn't deleted as it isn't dependent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            computer = pm.getObjectById(Computer.class, "PC-1234");
            assertNotNull(daffyDuck.getComputer());
            assertEquals(computer, daffyDuck.getComputer());
            assertEquals("PC-1234", daffyDuck.getComputer().getSerialNumber());
            assertEquals("Daffy Duck's computer", daffyDuck.getComputer().getName());
            computer2 = pm.getObjectById(Computer.class, "PC-2345");

            // delete computer
            pm.deletePersistent(computer);
            tx.commit();
            pm.close();

            // test computer was deleted and reference was removed
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            try
            {
                pm.getObjectById(Computer.class, "PC-1234");
                fail("Object 'PC-1234' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            assertNull(daffyDuck.getComputer());

            // set another reference again
            Computer computer3 = new Computer("PC-3456", "Daffy Duck's 3rd computer");
            daffyDuck.setComputer(computer3);
            tx.commit();
            pm.close();

            // test new reference
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            computer2 = pm.getObjectById(Computer.class, "PC-2345");
            computer3 = pm.getObjectById(Computer.class, "PC-3456");
            assertEquals(computer3, daffyDuck.getComputer());
            assertEquals("PC-3456", daffyDuck.getComputer().getSerialNumber());
            assertEquals("Daffy Duck's 3rd computer", daffyDuck.getComputer().getName());

            // delete person
            pm.deletePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // test person is deleted
            // computers still exist
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            try
            {
                pm.getObjectById(Person.class, "Daffy Duck");
                fail("Object 'Daffy Duck' should not exist any more!");
            }
            catch (JDOObjectNotFoundException e)
            {
                // expected
            }
            computer2 = pm.getObjectById(Computer.class, "PC-2345");
            computer3 = pm.getObjectById(Computer.class, "PC-3456");
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
