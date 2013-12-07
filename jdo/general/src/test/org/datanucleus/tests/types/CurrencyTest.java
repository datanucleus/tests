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

import java.util.Currency;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.currency.CurrencyHolder;

/**
 * Tests for SCO mutable type java.util.Currency.
 */
public class CurrencyTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public CurrencyTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    CurrencyHolder.class
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
            CurrencyHolder myCurr = new CurrencyHolder();
            myCurr.setCurrencyField(Currency.getInstance("GBP"));
            Object id = null;

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myCurr);
                pm.flush();

                id = JDOHelper.getObjectId(myCurr);
                CurrencyHolder myCurr2 = (CurrencyHolder) pm.getObjectById(id, true);
                pm.refresh(myCurr2);
                assertNotNull(myCurr2.getCurrencyField());
                assertEquals("GBP", myCurr2.getCurrencyField().getCurrencyCode());
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
                CurrencyHolder myCurr2 = (CurrencyHolder) pm.getObjectById(id, true);
                assertNotNull("Currency is null on retrieval", myCurr2.getCurrencyField());
                assertEquals("GBP", myCurr2.getCurrencyField().getCurrencyCode());
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
                CurrencyHolder myCurr2 = (CurrencyHolder) pm.getObjectById(id, true);
                assertNotNull("CurrencyHolder class had a null currency but should have had a value", myCurr2.getCurrencyField());
                myCurr2.setCurrencyField(Currency.getInstance("EUR"));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on update", e);
                fail("Error updating the Currency : " + e.getMessage());
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
                CurrencyHolder myCurr2 = (CurrencyHolder) pm.getObjectById(id, true);
                assertNotNull("Currency is null on retrieval", myCurr2.getCurrencyField());
                assertEquals("EUR", myCurr2.getCurrencyField().getCurrencyCode());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on retrieval", e);
                fail("Error retrieving the Currency : " + e.getMessage());
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
            clean(CurrencyHolder.class);
        }
    }

    /**
     * Test of the attach/detach process for an object that contains a SCO Point.
     * @throws Exception
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            CurrencyHolder detachedCurr = null;
            Object id = null;
            
            // Persist an object containing a Point
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                CurrencyHolder curr = new CurrencyHolder();
                curr.setCurrencyField(Currency.getInstance("USD"));
                pm.makePersistent(curr);
                
                detachedCurr = (CurrencyHolder)pm.detachCopy(curr);
                
                tx.commit();
                id = pm.getObjectId(curr);
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

            assertNotNull("Currency is null, but should have been detached", detachedCurr.getCurrencyField());
            assertEquals("Currency is incorrect (detached)", "USD", detachedCurr.getCurrencyField().getCurrencyCode());
            
            // Perform an update to the currency
            detachedCurr.setCurrencyField(Currency.getInstance("CAD"));
            
            // Attach it
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LOG.info(">> Currency being attached");
                CurrencyHolder attachedCurr = (CurrencyHolder)pm.makePersistent(detachedCurr);
                assertNotNull("Currency is null, but should have been attached", attachedCurr.getCurrencyField());
                assertEquals("Currency is incorrect (attached)", "CAD", attachedCurr.getCurrencyField().getCurrencyCode());
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
LOG.info(">> Currency being retrieved for checking");
                CurrencyHolder curr = (CurrencyHolder)pm.getObjectById(id);
                assertNotNull("Currency is null, but should have been detached", curr.getCurrencyField());
                assertEquals("Currency is incorrect (detached)", "CAD", curr.getCurrencyField().getCurrencyCode());
                detachedCurr = (CurrencyHolder)pm.detachCopy(curr);

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
            clean(CurrencyHolder.class);
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
                CurrencyHolder curr = new CurrencyHolder();
                curr.setCurrencyField(Currency.getInstance("USD"));
                pm.makePersistent(curr);
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
                Query q = pm.newQuery(CurrencyHolder.class, "currencyField == :p");
                List<CurrencyHolder> results = (List<CurrencyHolder>) q.execute(Currency.getInstance("USD"));
                assertEquals(1, results.size());
                CurrencyHolder curr = results.get(0);
                assertNotNull("Currency field is null", curr.getCurrencyField());
                assertEquals("Currency is incorrect", "USD", curr.getCurrencyField().getCurrencyCode());
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
            clean(CurrencyHolder.class);
        }
    }
}