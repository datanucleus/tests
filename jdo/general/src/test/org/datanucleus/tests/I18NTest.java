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

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.i18n.ISO8859_1;
import org.jpox.samples.i18n.ISO8859_2;
import org.jpox.samples.i18n.UTF8;

/**
 * Test the use of Internationalised code with JPOX.
 * @version $Revision: 1.3 $
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

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
 
    /**
     * Simple test to load up the ISO8859-1 class.
     * This class contains fields that have names using accented characters
     * such as tose found in Locale "es".
     **/
    public void testISO8859_1()
    throws Exception
    {
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

                ISO8859_1 obj=new ISO8859_1("lower","UPPER",12345,"·ÈÌÛ⁄Ò");
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

                Extent e=pm.getExtent(org.jpox.samples.i18n.ISO8859_1.class,false);
                Query q=pm.newQuery(e);
                Collection c=(Collection)q.execute();
                LOG.info("No of ISO8859_1 objects=" + c.size());

                assertTrue("Number of ISO8859_1 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator iter=c.iterator();
                while (iter.hasNext())
                {
                    ISO8859_1   obj=(ISO8859_1)iter.next();
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

                ISO8859_2 obj=new ISO8859_2("‰ÚÈ","« ’…");
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

                Extent e=pm.getExtent(org.jpox.samples.i18n.ISO8859_2.class,false);
                Query q=pm.newQuery(e);
                Collection c=(Collection)q.execute();

                assertTrue("Number of ISO8859_2 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator iter=c.iterator();
                while (iter.hasNext())
                {
                    ISO8859_2   obj=(ISO8859_2)iter.next();
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
     * This class contains fields that have names using accented characters
     * such as tose found in Locale "es".
     **/
    public void testUTF8()
    throws Exception
    {
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

                UTF8 obj=new UTF8("lower","UPPER",12345,"·ÈÌÛ⁄Ò");
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

                Extent e=pm.getExtent(org.jpox.samples.i18n.UTF8.class,false);
                Query q=pm.newQuery(e);
                Collection c=(Collection)q.execute();
                LOG.info("No of UTF8 objects=" + c.size());

                assertTrue("Number of UTF8 objects is incorrect (" + c.size() + ") : should have been 1",c.size() == 1);

                Iterator iter=c.iterator();
                while (iter.hasNext())
                {
                    UTF8   obj=(UTF8)iter.next();
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
