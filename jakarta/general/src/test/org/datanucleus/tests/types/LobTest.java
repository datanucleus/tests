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

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.types.basic.TypeHolder;
import org.datanucleus.tests.JakartaPersistenceTestCase;

/**
 * Test case for storage of different LOB types.
 */
public class LobTest extends JakartaPersistenceTestCase
{
    public LobTest(String name)
    {
        super(name);
    }

    /**
     * Test of CLOB
     */
    public void testClob()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();

            // Sample description 300 chars in length (i.e bigger than the default 255 for a VARCHAR)
            String desc =
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
            try
            {
                tx.begin();

                TypeHolder holder = new TypeHolder();
                holder.setString1(desc);
                em.persist(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while creating object with large string : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                em.close();
            }

            // Check the contents of the datastore
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                List result = em.createQuery("SELECT T FROM " + TypeHolder.class.getName() + " T").getResultList();
                assertEquals(1, result.size());
                TypeHolder holder = (TypeHolder)result.get(0);
                assertEquals("The LOB-stored description is incorrect", desc, holder.getString1());

                tx.rollback();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Exception thrown while retrieving object with large string : " + e.getMessage());
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
            clean(TypeHolder.class);
        }
    }
}