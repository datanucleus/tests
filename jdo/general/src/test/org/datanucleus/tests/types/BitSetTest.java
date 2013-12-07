/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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

import java.util.BitSet;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.bitset.BitSetHolder;

/**
 * Tests for SCO mutable type java.util.BitSet.
 */
public class BitSetTest  extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public BitSetTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    BitSetHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence and retrieval of java.util.BitSet mutable SCO type.
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            BitSetHolder mySCO = new BitSetHolder();
            mySCO.getSet().set(1);
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                tx.setRetainValues(true);
                pm.makePersistent(mySCO);
                id = JDOHelper.getObjectId(mySCO);
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);
                pm.refresh(mySCO2);
                assertEquals(mySCO.getSet(), mySCO2.getSet());
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
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);
                assertNotNull("BitSet1 is null", mySCO.getSet());
                assertNotNull("BitSet2 is null", mySCO2.getSet());
                assertEquals(mySCO.getSet().get(0), mySCO2.getSet().get(0));
                assertEquals(mySCO.getSet().get(1), mySCO2.getSet().get(1));
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
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);
                assertTrue("mySCO2 class had a null but should have had a value", mySCO2.getSet() != null);

                mySCO2.getSet().set(2);
                mySCO.getSet().set(2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating : " + e.getMessage());
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
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);

                assertEquals(mySCO.getSet(),mySCO2.getSet());
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
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);
                assertTrue("mySCO2 class had a null but should have had a value", mySCO2.getSet() != null);
                
                mySCO2.getSet().flip(1,2);
                mySCO.getSet().flip(1,2);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating : " + e.getMessage());
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
                BitSetHolder mySCO2 = (BitSetHolder) pm.getObjectById(id,true);
                assertEquals(mySCO.getSet(),mySCO2.getSet());
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
            clean(BitSetHolder.class);
        }
    }

    /**
     * Test of the attach/detach process for an object that contains a SCO.
     * @throws Exception
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            BitSetHolder detachedSCO = null;
            Object scoId = null;
            
            // Persist an object
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                tx.setRetainValues(true);

                BitSetHolder mySCO = new BitSetHolder();
                mySCO.getSet().set(1);
                pm.makePersistent(mySCO);
                
                detachedSCO = (BitSetHolder)pm.detachCopy(mySCO);
                
                tx.commit();
                scoId = pm.getObjectId(mySCO);
                assertEquals(mySCO.getSet(), detachedSCO.getSet());
            }
            catch (Exception e)
            {
                LOG.error("Exception in detach", e);
                fail("Error whilst persisting and detaching object containing SCO : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Perform an update to the contents
            detachedSCO.getSet().set(2);
            
            // Attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                BitSetHolder attachedSCO = (BitSetHolder)pm.makePersistent(detachedSCO);
                
                // Update the contents now attached (test that it uses SCO wrappers)
                attachedSCO.getSet().set(3);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                BitSetHolder mySCO2 = (BitSetHolder)pm.getObjectById(scoId);

                assertNotNull("BitSet is null", mySCO2.getSet());
                assertTrue(mySCO2.getSet().get(1));
                assertTrue(mySCO2.getSet().get(2));
                assertTrue(mySCO2.getSet().get(3));
                
                detachedSCO = (BitSetHolder)pm.detachCopy(mySCO2);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Replace
            BitSet bitSet = new BitSet();
            bitSet.set(4);
            detachedSCO.setSet(bitSet);
            
            // Attach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(detachedSCO);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO : " + e.getMessage());
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
            pm.getFetchPlan().addGroup("group");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                BitSetHolder mySCO2 = (BitSetHolder)pm.getObjectById(scoId);
                
                assertFalse(mySCO2.getSet().get(1));
                assertFalse(mySCO2.getSet().get(2));
                assertFalse(mySCO2.getSet().get(3));
                assertTrue(mySCO2.getSet().get(4));
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO : " + e.getMessage());
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
            clean(BitSetHolder.class);
        }
    }
}