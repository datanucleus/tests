/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.datastore.Sequence;

import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Simple tests for the PM Sequence capability.
 */
public class SequenceTest extends JDOPersistenceTestCase
{
    /**
     * Used by the JUnit framework to construct tests.
     * @param name Name of the <tt>TestCase</tt>.
     */
    public SequenceTest(String name)
    {
        super(name);
    }

    /**
     * Test the attempted retrieval of a non-existent sequence.
     * @throws Exception
     */
    public void testInvalidSequenceName()
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try
        {
            tx.begin();

            pm.getSequence("org.datanucleus.samples.store.ProductNonexistentSequence");
            fail("Attempt to obtain non-existent sequence succeeded !");
        }
        catch (JDOUserException jdoe)
        {
            // This is the expected result
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing sequence test : " + e.getMessage());
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
     * Basic test for a transactional contiguous sequence.
     * @throws Exception
     */
    public void testContiguousSequence() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        if (!storeMgr.supportsValueStrategy("sequence"))
        {
            // Doesn't support SEQUENCE strategy so test doesn't apply
            return;
        }
        try
        {
            tx.begin();

            Sequence seq = pm.getSequence("org.datanucleus.samples.store.ProductSequence");
            seq.allocate(2);
            try
            {
                long id1 = seq.nextValue();
                long id2 = seq.nextValue();
                assertTrue("The ids obtained from a transactional contiguous sequence were not sequential!",
                    id1+1 == id2);
            }
            catch (Exception e)
            {
                fail("Attempt to retrieve the ids of an allocated sequence failed : " + e.getMessage());
            }
            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown while performing sequence test : " + e.getMessage());
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
     * Basic test for a nontransactional sequence.
     * @throws Exception
     */
    public void testNontransactionalSequence() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        if (!storeMgr.supportsValueStrategy("sequence"))
        {
            // Doesn't support SEQUENCE strategy so test doesn't apply
            return;
        }

        Sequence seq = pm.getSequence("org.datanucleus.samples.store.ProductSequenceNontrans");
        seq.allocate(2);
        try
        {
            long id1 = seq.nextValue();
            long id2 = seq.nextValue();
            assertTrue("The ids obtained from a nontransactional sequence were not sequential!",
                id1+1 == id2);
        }
        catch (Exception e)
        {
            fail("Attempt to retrieve the ids of a nontransactional sequence failed : " + e.getMessage());
        }

        pm.close();
    }

    /**
     * Basic test for a sequence generated using a factory that is nontransactional.
     * @throws Exception
     */
    public void testFactorySequence() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        Sequence seq = pm.getSequence("org.datanucleus.samples.store.ProductSequenceFactory");
        try
        {
            long id1 = seq.nextValue();
            long id2 = seq.nextValue();
            assertTrue("The ids obtained from a factory sequence were not different!",
                id1 != id2);
        }
        catch (Exception e)
        {
            fail("Attempt to retrieve the ids of a factory sequence failed : " + e.getMessage());
        }

        pm.close();
    }

    /**
     * Test using the simple sequence factory included in the JDO2 API jar.
     * TODO Enable this if the JDO2 API jar ever includes the proposed sequence.
     * @throws Exception
     */
    /*public void testJDO2SimpleSequenceFactory() 
    throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();

        Sequence seq = pm.getSequence("org.datanucleus.samples.store.JDO2SimpleSequenceFactory");
        try
        {
            long id1 = seq.nextValue();
            long id2 = seq.nextValue();
            assertTrue("The ids obtained from a factory sequence were not different!",
                id1 != id2);
        }
        catch (Exception e)
        {
            fail("Attempt to retrieve the ids of a factory sequence failed : " + e.getMessage());
        }

        pm.close();
    }*/
}