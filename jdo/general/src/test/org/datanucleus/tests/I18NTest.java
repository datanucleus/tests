/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others
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
package org.datanucleus.tests;

import java.util.Collection;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.i18n.ISO8859_1;
import org.jpox.samples.i18n.ISO8859_2;
import org.jpox.samples.i18n.UTF8;

/**
 * Test the use of Internationalised code (classes/fields with accented characters).
 */
public class I18NTest extends JDOPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the test case.
     */
    public I18NTest(String name)
    {
        super(name);
    }

    /**
     * Simple test to load up the ISO8859-1 class.
     * This class contains fields that have names using accented characters such as those found in Locale "es".
     */
    public void testISO8859_1()
    throws Exception
    {
        if (vendorID != null && vendorID.equals("postgresql"))
        {
            // Dont run on Postgresql
            return;
        }
        String datastoreProtocol = TestHelper.getDatastorePluginProtocol(1);
        if (datastoreProtocol != null && datastoreProtocol.equals("cassandra"))
        {
            // Don't run on Cassandra
            return;
        }

        try
        {
            try
            {
                addClassesToSchema(new Class[] {ISO8859_1.class} );
            }
            catch (Exception e)
            {
                fail("Failed to load class with ISO8859-1 field names : " + e.getMessage());
            }
            finally
            {
            }

            // Test persistence of an object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ISO8859_1 obj=new ISO8859_1("lower","UPPER",12345,"������");
                pm.makePersistent(obj);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to persist an object of class with ISO8859-1 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Test retrieval of object
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Extent<ISO8859_1> e=pm.getExtent(org.jpox.samples.i18n.ISO8859_1.class,false);
                Query<ISO8859_1> q = pm.newQuery(e);
                Collection<ISO8859_1> c = q.executeList();
                LOG.info("No of ISO8859_1 objects=" + c.size());

                assertTrue("Number of ISO8859_1 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator<ISO8859_1> iter = c.iterator();
                while (iter.hasNext())
                {
                    ISO8859_1 obj = iter.next();
                    LOG.info(obj);
                }

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to retrieve an object of class with ISO8859-1 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

            }
            pm.close();
        }
        finally
        {
            clean(ISO8859_1.class);
        }
    }
 
    /**
     * Simple test to load up the ISO8859-2 class.
     **/
    public void testISO8859_2()
    throws Exception
    {
        if (vendorID != null && vendorID.equals("postgresql"))
        {
            // Dont run on Postgresql
            return;
        }
        String datastoreProtocol = TestHelper.getDatastorePluginProtocol(1);
        if (datastoreProtocol != null && datastoreProtocol.equals("cassandra"))
        {
            // Don't run on Cassandra
            return;
        }

        try
        {
            try
            {
                addClassesToSchema(new Class[] {ISO8859_2.class} );
            }
            catch (Exception e)
            {
                fail("Failed to load class with ISO8859-2 field names : " + e.getMessage());
            }
            finally
            {
            }

            // Test persistence of an object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ISO8859_2 obj = new ISO8859_2("����","����");
                pm.makePersistent(obj);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to persist an object of class with ISO8859-2 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Test retrieval of object
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Extent<ISO8859_2> e=pm.getExtent(org.jpox.samples.i18n.ISO8859_2.class,false);
                Query<ISO8859_2> q = pm.newQuery(e);
                Collection<ISO8859_2> c = q.executeList();

                assertTrue("Number of ISO8859_2 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator<ISO8859_2> iter=c.iterator();
                while (iter.hasNext())
                {
                    ISO8859_2 obj = iter.next();
                    LOG.info(obj);
                }

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to retrieve an object of class with ISO8859-2 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

            }
            pm.close();
        }
        finally
        {
            clean(ISO8859_2.class);
        }
    }

    /**
     * Simple test to load up the UTF8 class.
     * This class contains fields that have names using accented characters such as those found in Locale "es".
     **/
    public void testUTF8()
    throws Exception
    {
        String datastoreProtocol = TestHelper.getDatastorePluginProtocol(1);
        if (datastoreProtocol != null && datastoreProtocol.equals("cassandra"))
        {
            // Don't run on Cassandra
            return;
        }

        try
        {
            try
            {
                addClassesToSchema(new Class[] {UTF8.class} );
            }
            catch (Exception e)
            {
                fail("Failed to load class with UTF8 field names : " + e.getMessage());
            }
            finally
            {
            }

            // Test persistence of an object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                UTF8 obj=new UTF8("lower","UPPER",12345,"������");
                pm.makePersistent(obj);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to persist an object of class with UTF8 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }

            // Test retrieval of object
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Extent<UTF8> e=pm.getExtent(org.jpox.samples.i18n.UTF8.class,false);
                Query<UTF8> q = pm.newQuery(e);
                Collection<UTF8> c = q.executeList();
                LOG.info("No of UTF8 objects=" + c.size());

                assertTrue("Number of UTF8 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator<UTF8> iter=c.iterator();
                while (iter.hasNext())
                {
                    UTF8 obj = iter.next();
                    LOG.info(obj);
                }

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Failed to retrieve an object of class with UTF8 field names : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }

            }
            pm.close();
        }
        finally
        {
            clean(UTF8.class);
        }
    } 
}