/**********************************************************************
Copyright (c) 2003 Andy Jefferson and others. All rights reserved
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributions :
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.store.Book;
import org.datanucleus.samples.store.CompactDisc;
import org.datanucleus.samples.store.Product;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.company.Developer;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Manager;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.one_one.unidir.Login;
import org.jpox.samples.one_one.unidir.LoginAccount;
import org.jpox.samples.one_one.unidir_2.Magazine;
import org.jpox.samples.one_one.unidir_2.MediaWork;
import org.jpox.samples.one_one.unidir_2.Newspaper;

/**
 * Test the use of Extent.
 **/
public class ExtentTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ExtentTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    LoginAccount.class,
                    CompactDisc.class,
                    Book.class,
                    Newspaper.class,
                    Magazine.class,
                    Developer.class,
                    Person.class,
                    Manager.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of "requires-extent" being false.
     **/
    public void testRequiresExtent()
    throws Exception
    {
        try
        {
            // Create some objects.
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                LoginAccount acct = new LoginAccount("Andy", "Jefferson", "andy", "pwd");
                pm.makePersistent(acct);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of objects including one with requires-extent=false",false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Try to get extent of "Login"
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.getExtent(Login.class,true);
                
                tx.commit();
                
                assertTrue("Should have thrown JDOUserException when getting Extent for class which has no extent defined.",false);
            }
            catch (JDOUserException ue)
            {
                LOG.info(ue);
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
            // Clean out created data
            clean(LoginAccount.class);
        }
    }

    /**
     * Test the closure of Extent iterators.
     **/
    public void testCloseAll() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            Extent ex = pm.getExtent(org.datanucleus.samples.store.Product.class, false);
            ex.closeAll();	// none open

            Iterator i = ex.iterator();
            ex.closeAll();	// one open
            assertEquals("iterator.hasNext() after extent.closeAll", false, i.hasNext());

            ex.iterator();
            ex.iterator();
            ex.closeAll();	// two open

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
     * Test the use of extents with a class using "new-table" inheritance strategy.
     **/
    public void testExtentOfNewTable()
    throws Exception
    {
        try
        {
            // Create some objects.
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();

                CompactDisc cd = new CompactDisc("ABC", "Greatest Hits", "CD of our greatest hits", "http://www.jpox.org","GBP", 
                    10.00, 10.00, 10.00, 17.5, 1, "JPOX", "Greatest Hits", 2006, "JPOX Production", "JPOX Publishing");
                Book book = new Book("DEF", "Long Stories", "Book of long stories", "http://www.amazon.com", "EUR", 5.99, 5.99, 
                    5.99, 12.0, 2, "54321672578", "John Storyteller", "Long stories", 1, "McGraw Hill");
                pm.makePersistent(cd);
                pm.makePersistent(book);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of objects using new-table inheritance", false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Try to get extent of base class
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Extent e = pm.getExtent(Product.class, true);
                int number = 0;
                Iterator iter = e.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                    number++;
                }
                assertEquals("Extent for classes with new-table inheritance strategy returned incorrect number of objects", number, 2);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Exception was thrown when requesting Extent of class using new-table inheritance strategy");
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
            // Clean out created data
            clean(CompactDisc.class);
            clean(Book.class);
        }
    }

    /**
     * Test the use of extents with a class using "subclass-table" inheritance strategy.
     **/
    public void testExtentOfSubclassTable()
    throws Exception
    {
        try
        {
            // Create some objects.
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();

                Newspaper paper = new Newspaper("Daily Vermin", 2, "George Bush", "Sleaze");
                Magazine mag = new Magazine("Buenos Dias", 4, "Cachorro sa");
                pm.makePersistent(paper);
                pm.makePersistent(mag);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of objects using subclass-table inheritance", false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            // Try to get extent of ExtentSub (superclass of both objects, using subclass-table)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Extent e = pm.getExtent(MediaWork.class, true);
                int number = 0;
                Iterator iter = e.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                    number++;
                }
                assertEquals("Extent for classes with subclass-table inheritance strategy returned incorrect number of objects", number, 2);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown for Extent of class using subclass-table", e);
                e.printStackTrace();
                fail("Exception was thrown when requesting Extent of class using subclass-table inheritance strategy");
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
            // Clean out created data
            clean(Newspaper.class);
            clean(Magazine.class);
        }
    }

    /**
     * Test use of pm.getExtent and use of the subclasses flag.
     */
    public void testExtentSubclasses() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();
                Employee empl = new Employee(0, "Homer", "Simpson", "homer@simpsons.com", (float)200000.0, "123");
                Manager mgr = new Manager(1, "Matt", "Groening", "matt@simpsons.com", (float)500000.0, "1");
                pm.makePersistent(empl);
                pm.makePersistent(mgr);
                pm.flush();

                // test subclasses argument == false (should contain Employee only)
                Extent extent = pm.getExtent(Employee.class, false);
                java.util.Iterator it = extent.iterator();
                Employee empl2 = (Employee) it.next();
                assertEquals(empl.getPersonNum(), empl2.getPersonNum());
                assertEquals(false, it.hasNext());

                tx.commit();

                // test subclasses argument == true (should contain Employee AND Manager)
                tx.begin();
                extent = pm.getExtent(Employee.class, true);
                it = extent.iterator();
                empl2 = (Employee) it.next();
                if (empl2 instanceof Manager)
                {
                    assertEquals(1, empl2.getPersonNum());
                    pm.deletePersistent(empl2);

                    empl2 = (Employee) it.next();
                    assertEquals(0, empl2.getPersonNum());
                    pm.deletePersistent(empl2);
                }
                else
                {
                    assertEquals(0, empl2.getPersonNum());
                    pm.deletePersistent(empl2);

                    empl2 = (Manager) it.next();
                    assertEquals(1, empl2.getPersonNum());
                    pm.deletePersistent(empl2);
                }
                assertEquals(false, it.hasNext());

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
            clean(Manager.class);
            clean(Employee.class);
        }
    }
}