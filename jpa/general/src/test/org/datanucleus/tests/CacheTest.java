/**********************************************************************
Copyright (c) 2019 Andy Jefferson and others. All rights reserved.
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

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.simple.SimpleClass;

/**
 * Series of tests for L2 Cache.
 */
public class CacheTest extends JPAPersistenceTestCase
{
    public CacheTest(String name)
    {
        super(name);
    }

    /**
     * Test for caching of object using single-field id.
     */
    public void testSingleFieldId()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                SimpleClass simple1 = new SimpleClass();
                simple1.setId(1001);

                em.persist(simple1);
                em.flush();

                // Should not be L2 cached yet, since only done at commit
                Cache jpaCache = emf.getCache();
                Object puutilId = emf.getPersistenceUnitUtil().getIdentifier(simple1);
                assertFalse(jpaCache.contains(SimpleClass.class, Long.valueOf(1001)));
                assertFalse(jpaCache.contains(SimpleClass.class, puutilId));

                tx.commit();

                // Should be L2 cached now. Note that this assumes that they aren't GCed yet
                puutilId = emf.getPersistenceUnitUtil().getIdentifier(simple1);
                assertTrue(jpaCache.contains(SimpleClass.class, Long.valueOf(1001)));
                assertTrue(jpaCache.contains(SimpleClass.class, puutilId));
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
            clean(SimpleClass.class);
        }
    }
}