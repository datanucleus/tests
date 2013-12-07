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
package org.datanucleus.tests.directory.embedded;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class EmbeddedTest extends JDOPersistenceTestCase
{
    public EmbeddedTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Person.class);
    }

    protected void tearDown() throws Exception
    {
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
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

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

    public void testPersistPersonWithEmbeddedAndNestedEmbedded()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
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
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

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

    public void testUpdateEmbeddedObject()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
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
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            // set an new embedded objects, null out the other
            Address address2 = new Address(23456, "D-City2", "D-Street2");
            ContactData contactData2 = new ContactData(address2, daffyDuck);
            contactData2.getPhoneNumbers().add("+49 123 456 7890");
            contactData2.getPhoneNumbers().add("+49 987 654 3210");
            daffyDuck.setAccount(null);
            daffyDuck.setContactData(contactData2);
            tx.commit();
            pm.close();

            // verify that new embedded objects were set
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNull(daffyDuck.getAccount());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City2", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street2", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49 123 456 7890"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49 987 654 3210"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            // null out the one, set a new embedded object to the other
            Account account2 = new Account("dduck2", "secret34");
            daffyDuck.setAccount(account2);
            daffyDuck.setContactData(null);
            tx.commit();
            pm.close();

            // verify that new embedded objects were set
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck2", daffyDuck.getAccount().getUid());
            assertEquals("secret34", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNull(daffyDuck.getContactData().getAddress());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

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

    public void testUpdateOfEmbeddedObjectValues()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
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
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret12", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(12345, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            // update values
            daffyDuck.getAccount().setPassword("56secret");
            daffyDuck.getContactData().getPhoneNumbers().add("+49-000-000-000");
            daffyDuck.getContactData().getAddress().setZip(23456);
            daffyDuck.getContactData().getAddress().setStreet("D-Street2");
            tx.commit();
            pm.close();

            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("56secret", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street2", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(3, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-000-000-000"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            // null-out values
            daffyDuck.getAccount().setPassword(null);
            daffyDuck.getContactData().getPhoneNumbers().clear();
            daffyDuck.getContactData().getAddress().setZip(0);
            daffyDuck.getContactData().getAddress().setStreet(null);
            tx.commit();
            pm.close();

            // verify updated values
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertNull(daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(0, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertNull(daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(0, daffyDuck.getContactData().getPhoneNumbers().size());
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

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

    public void testAttachDetach()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Account account = new Account("dduck", "secret12");
            Address address = new Address(12345, "D-City", "D-Street");
            ContactData contactData = new ContactData(address, null);
            contactData.getPhoneNumbers().add("+49-123-456-789");
            contactData.getPhoneNumbers().add("+49-987-654-321");
            Person daffyDuck = new Person("Daffy", "Duck", "Daffy Duck", account, contactData);
            contactData.setPerson(daffyDuck);
            pm.makePersistent(daffyDuck);
            tx.commit();
            pm.close();

            // detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            Person detachedDaffyDuck = pm.detachCopy(daffyDuck);
            tx.commit();
            pm.close();

            // test values of detached object
            assertEquals("Daffy Duck", detachedDaffyDuck.getFullName());
            assertEquals("Daffy", detachedDaffyDuck.getFirstName());
            assertEquals("Duck", detachedDaffyDuck.getLastName());
            assertNotNull(detachedDaffyDuck.getAccount());
            assertEquals("dduck", detachedDaffyDuck.getAccount().getUid());
            assertEquals("secret12", detachedDaffyDuck.getAccount().getPassword());
            assertNotNull(detachedDaffyDuck.getContactData());
            assertNotNull(detachedDaffyDuck.getContactData().getAddress());
            assertEquals(12345, detachedDaffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", detachedDaffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street", detachedDaffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(detachedDaffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, detachedDaffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(detachedDaffyDuck.getContactData().getPhoneNumbers().contains("+49-123-456-789"));
            assertTrue(detachedDaffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(detachedDaffyDuck, detachedDaffyDuck.getContactData().getPerson());

            // update the detached object
            detachedDaffyDuck.getContactData().getAddress().setZip(23456);
            detachedDaffyDuck.getContactData().getAddress().setStreet("D-Street2");
            detachedDaffyDuck.getContactData().getPhoneNumbers().remove("+49-123-456-789");
            detachedDaffyDuck.getContactData().getPhoneNumbers().add("+49-000-000-000");
            detachedDaffyDuck.getAccount().setPassword("secret90");
            detachedDaffyDuck.setFirstName("Daffy2");
            JDOHelper.makeDirty(detachedDaffyDuck.getContactData(), "address");

            // attach the object
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(detachedDaffyDuck);
            tx.commit();
            pm.close();

            // verify that the modifications are persistent
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            daffyDuck = pm.getObjectById(Person.class, "Daffy Duck");
            assertEquals("Daffy Duck", daffyDuck.getFullName());
            assertEquals("Daffy2", daffyDuck.getFirstName());
            assertEquals("Duck", daffyDuck.getLastName());
            assertNotNull(daffyDuck.getAccount());
            assertEquals("dduck", daffyDuck.getAccount().getUid());
            assertEquals("secret90", daffyDuck.getAccount().getPassword());
            assertNotNull(daffyDuck.getContactData());
            assertNotNull(daffyDuck.getContactData().getAddress());
            assertEquals(23456, daffyDuck.getContactData().getAddress().getZip());
            assertEquals("D-City", daffyDuck.getContactData().getAddress().getCity());
            assertEquals("D-Street2", daffyDuck.getContactData().getAddress().getStreet());
            assertNotNull(daffyDuck.getContactData().getPhoneNumbers());
            assertEquals(2, daffyDuck.getContactData().getPhoneNumbers().size());
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-000-000-000"));
            assertTrue(daffyDuck.getContactData().getPhoneNumbers().contains("+49-987-654-321"));
            assertEquals(daffyDuck, daffyDuck.getContactData().getPerson());

            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }
    }

    // TODO: query

}
