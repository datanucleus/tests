/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.datanucleus.api.jakarta.DataNucleusHelperJakarta;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.samples.annotations.datastoreidentity.DSIDHolder;

/**
 * Series of tests for Datastore Identity.
 */
public class DatastoreIdentityTest extends JakartaPersistenceTestCase
{
    public DatastoreIdentityTest(String name)
    {
        super(name);
    }

    /**
     * Basic test.
     */
    public void testBasic()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            Object id = null;
            try
            {      
                tx.begin();
                DSIDHolder holder = new DSIDHolder("First Holder");
                em.persist(holder);
                em.flush();
                id = DataNucleusHelperJakarta.getObjectId(holder);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                Object key = IdentityUtils.getTargetKeyForDatastoreIdentity(id);
                DSIDHolder holder = em.find(DSIDHolder.class, key);
                assertNotNull(holder);
                assertEquals("First Holder", holder.getName());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(e);
                e.printStackTrace();
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
            clean(DSIDHolder.class);
        }
    }
}