/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.datanucleus.tests.types;

import java.util.Locale;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.locale.LocaleHolder;

/**
 * Tests for SCO mutable type java.util.Locale.
 */
public class LocaleTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public LocaleTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    LocaleHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence and retrieval.
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            LocaleHolder myLocale = new LocaleHolder();
            myLocale.setLocale(Locale.ENGLISH);
            Object id = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myLocale);
                pm.flush();

                id = JDOHelper.getObjectId(myLocale);
                LocaleHolder myLocale2 = (LocaleHolder) pm.getObjectById(id, true);
                pm.refresh(myLocale2);
                assertNotNull(myLocale2.getLocale());
                assertEquals("en", myLocale2.getLocale().getLanguage());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist", e);
                fail("Exception on persist");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LocaleHolder myLocale2 = (LocaleHolder) pm.getObjectById(id, true);
                assertNotNull("Locale is null on retrieval", myLocale2.getLocale());
                assertEquals("en", myLocale2.getLocale().getLanguage());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieval", e);
                fail("Exception on retrieval");
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LocaleHolder myLocale2 = (LocaleHolder) pm.getObjectById(id, true);
                assertNotNull("LocaleHolder class had a null locale but should have had a value", myLocale2.getLocale());
                myLocale2.setLocale(Locale.FRENCH);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on update", e);
                fail("Error updating the Zone : " + e.getMessage());
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
                LocaleHolder myLocale2 = (LocaleHolder) pm.getObjectById(id, true);
                assertNotNull("Locale is null on retrieval", myLocale2.getLocale());
                assertEquals("fr", myLocale2.getLocale().getLanguage());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieval", e);
                fail("Error on retrieval : " + e.getMessage());
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
            clean(LocaleHolder.class);
        }
    }

    /**
     * Test of the attach/detach process.
     * @throws Exception
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            LocaleHolder detachedLocale = null;
            Object id = null;
            
            // Persist an object
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                LocaleHolder locale = new LocaleHolder();
                locale.setLocale(Locale.ENGLISH);
                pm.makePersistent(locale);
                
                detachedLocale = (LocaleHolder)pm.detachCopy(locale);
                
                tx.commit();
                id = pm.getObjectId(locale);
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+detach", e);
                fail("Error on persist+detach : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }

            assertNotNull("TimeZone is null, but should have been detached", detachedLocale.getLocale());
            assertEquals("TimeZone is incorrect (detached)", "en", detachedLocale.getLocale().getLanguage());

            // Perform an update
            detachedLocale.setLocale(Locale.GERMAN);

            // Attach it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LocaleHolder attachedLocale = (LocaleHolder)pm.makePersistent(detachedLocale);
                assertNotNull("Locale is null, but should have been attached", attachedLocale.getLocale());
                assertEquals("Locale is incorrect (attached)", "de", attachedLocale.getLocale().getLanguage());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on attach", e);
                fail("Error on attach : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                LocaleHolder locale = (LocaleHolder)pm.getObjectById(id);
                assertNotNull("TimeZone is null, but should have been detached", locale.getLocale());
                assertEquals("TimeZone is incorrect (detached)", "de", locale.getLocale().getLanguage());
                detachedLocale = (LocaleHolder)pm.detachCopy(locale);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieve+detach(2)", e);
                fail("Error on retrieve+detach(2) : " + e.getMessage());
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
            clean(LocaleHolder.class);
        }
    }

    public void testQuery() throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LocaleHolder locale = new LocaleHolder();
                locale.setLocale(Locale.ENGLISH);
                pm.makePersistent(locale);
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
                Query q = pm.newQuery(LocaleHolder.class, "locale == :p");
                List<LocaleHolder> results = (List<LocaleHolder>) q.execute(Locale.ENGLISH);
                assertEquals(1, results.size());
                LocaleHolder locale = results.get(0);
                assertNotNull("Locale field is null", locale.getLocale());
                assertEquals("Locale is incorrect", "en", locale.getLocale().getLanguage());
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
            clean(LocaleHolder.class);
        }
    }
}