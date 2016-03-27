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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.api.jpa.NucleusJPAHelper;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.nondurableidentity.NonDurableIDHolder;

/**
 * Series of tests for Datastore Identity.
 */
public class NonDurableIdentityTest extends JPAPersistenceTestCase
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
                id = NucleusJPAHelper.getObjectId(holder);
                assertNull(id);
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

        }
        finally
        {
            clean(NonDurableIDHolder.class);
        }
    }
}