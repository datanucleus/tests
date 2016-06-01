/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.valuegenerator.CustomUUIDHolder;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for DN ValueGenerator extension
 */
public class ValueGeneratorTest extends JPAPersistenceTestCase
{
    public ValueGeneratorTest(String name)
    {
        super(name);
    }

    public void testUUID()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            String uid = null;
            try
            {
                tx.begin();
                CustomUUIDHolder holder = new CustomUUIDHolder();
                holder.setNameField("First holder");
                em.persist(holder);
                tx.commit();
                uid = holder.getUid();
                assertNotNull(uid);
                try
                {
                    UUID.fromString(uid);
                }
                catch (IllegalArgumentException iae)
                {
                    fail("The generated uid was not a real UUID : " + iae.getMessage());
                }
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown during persist when using ValueGenerator", e);
                fail("Failure on persist with ValueGenerator : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }
            if (emf.getCache() != null)
            {
                emf.getCache().evictAll();
            }

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                CustomUUIDHolder h1 = em.find(CustomUUIDHolder.class, uid);
                assertNotNull(h1);
                assertEquals("First holder", h1.getName());
                assertEquals(uid, h1.getUid());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error(">> Exception thrown during retrieve when using ValueGenerator", e);
                fail("Failure on retrieve with ValueGenerator : " + e.getMessage());
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
            clean(CustomUUIDHolder.class);
        }
    }
}