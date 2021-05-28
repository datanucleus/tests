/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
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

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import org.datanucleus.api.jakarta.DataNucleusHelperJakarta;
import org.datanucleus.samples.annotations.nondurableidentity.NonDurableIDHolder;
import org.datanucleus.store.StoreManager;

/**
 * Series of tests for Datastore Identity.
 */
public class NonDurableIdentityTest extends JakartaPersistenceTestCase
{
    public NonDurableIdentityTest(String name)
    {
        super(name);
    }

    /**
     * Basic test.
     */
    public void testBasic()
    {
        if (!storeMgr.getSupportedOptions().contains(StoreManager.OPTION_NONDURABLE_ID))
        {
            return;
        }

        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            Object id = null;
            try
            {      
                tx.begin();
                NonDurableIDHolder holder = new NonDurableIDHolder("First Holder");
                em.persist(holder);
                em.flush();
                id = DataNucleusHelperJakarta.getObjectId(holder);
                assertNull(id);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            emf.getCache().evictAll();

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {      
                tx.begin();
                Query q = em.createQuery("SELECT h FROM NonDurableIDHolder h");
                List<NonDurableIDHolder> results = q.getResultList();
                assertNotNull(results);
                assertEquals(1, results.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception in test", e);
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
        }
        finally
        {
            clean(NonDurableIDHolder.class);
        }
    }
}