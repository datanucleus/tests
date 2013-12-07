package org.datanucleus.tests;

import java.io.IOException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.jpox.samples.models.company.Person;

public class BasicTest extends JDOPersistenceTestCase
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
        try
        {
            pm.currentTransaction().begin();
            Person p = new Person();
            p.setPersonNum(1);
            p.setGlobalNum("1");
            p.setFirstName("Bugs");
            p.setLastName("Bunny");
            p.setAge(7);
            pm.makePersistent(p);
            id = pm.getObjectId(p);
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
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            try
            {
                pm.currentTransaction().begin();
                Person p = (Person) pm.getObjectById(id);
                pm.deletePersistent(p);
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
        try
        {
            pm.currentTransaction().begin();
            Person p = (Person) pm.getObjectById(id);
            p.setFirstName("BBB");
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("BBB", p1.getFirstName());
            p1.setFirstName("Bugs");
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
    
    /**
     * Test delete
     */
    public void testDelete()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p = new Person();
            p.setPersonNum(1);
            p.setGlobalNum("8");
            p.setFirstName("G8");
            p.setLastName("B8");
            pm.makePersistent(p);
            Object id = pm.getObjectId(p);
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            p = (Person) pm.getObjectById(id);
            pm.deletePersistent(p);
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            try
            {
                Person p1 = (Person) pm.getObjectById(id);
                fail("Expected JDOObjectNotFoundException, but object is: "+p1.getLastName());
            }
            catch(JDOObjectNotFoundException ex)
            {
                //expected
            }
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
    

    /**
     * Test that adds a new attribute to LDAP
     */
    public void testUpdateFieldNoLongerNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p = (Person) pm.getObjectById(id);
            p.setGlobalNum("1");
            p.setEmailAddress("ppp");
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("1", p1.getGlobalNum());
            assertEquals("ppp", p1.getEmailAddress());            
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

    /**
     * Test that adds a new attribute to LDAP
     */
    public void testUpdateFieldNoLongerNullWithRetrieve()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p = (Person) pm.getObjectById(id);
            pm.retrieve(p);
            assertEquals("Bugs", p.getFirstName());   
            p.setEmailAddress("ppp2");
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("1", p1.getGlobalNum());
            assertEquals("ppp2", p1.getEmailAddress());            
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
    /**
     * Test setting a field to null, removes the attribute from LDAP
     */
    public void testUpdateFieldSetNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p = (Person) pm.getObjectById(id);
            p.setEmailAddress("ppp");
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("ppp", p1.getEmailAddress());
            p1.setEmailAddress(null);
            pm.currentTransaction().commit();
            pm.close();
            pm = pmf.getPersistenceManager();
            pm.currentTransaction().begin();
            p1 = (Person) pm.getObjectById(id);
            assertNull(p1.getEmailAddress());
            p.setEmailAddress(null);
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
    
    /**
     * Fetch objects and assert basic values
     */
    public void testFetch()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        try
        {
            pm.currentTransaction().begin();
            Person p1 = (Person) pm.getObjectById(id);
            assertEquals("Bugs", p1.getFirstName());
            assertEquals("Bunny", p1.getLastName());
            assertEquals(7, p1.getAge());
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
}
