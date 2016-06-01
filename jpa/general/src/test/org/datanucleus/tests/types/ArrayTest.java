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
package org.datanucleus.tests.types;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.annotations.array.ArrayHolder;
import org.datanucleus.samples.annotations.array.Permission;
import org.datanucleus.tests.JPAPersistenceTestCase;

/**
 * Tests for array field persistence.
 */
public class ArrayTest extends JPAPersistenceTestCase
{
    public ArrayTest(String name)
    {
        super(name);
    }

    public void testArrayOfIntViaLob()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ArrayHolder h1 = new ArrayHolder(1);
                int[] ints = new int[] {123, 6789, 25000};
                h1.setIntArray(ints);
                em.persist(h1);

                tx.commit();
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

                ArrayHolder h1 = em.find(ArrayHolder.class, 1);
                assertNotNull(h1);
                int[] ints = h1.getIntArray();
                assertNotNull(ints);
                assertEquals(3, ints.length);
                assertEquals(123, ints[0]);
                assertEquals(6789, ints[1]);
                assertEquals(25000, ints[2]);

                tx.commit();
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
            clean(ArrayHolder.class);
        }
    }

    public void testArrayOfLongViaJoin()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ArrayHolder h1 = new ArrayHolder(1);
                long[] longs = new long[] {123, 6789, 25000};
                h1.setLongArray(longs);
                em.persist(h1);

                tx.commit();
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

                ArrayHolder h1 = em.find(ArrayHolder.class, 1);
                assertNotNull(h1);
                long[] longs = h1.getLongArray();
                assertNotNull(longs);
                assertEquals(3, longs.length);
                assertEquals(123, longs[0]);
                assertEquals(6789, longs[1]);
                assertEquals(25000, longs[2]);

                tx.commit();
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
            clean(ArrayHolder.class);
        }
    }

    public void testArrayOfPersistablesViaJoin()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                ArrayHolder h1 = new ArrayHolder(1);
                Permission p1 = new Permission(101, "Admin");
                Permission p2 = new Permission(102, "Developer");
                Permission[] perms = new Permission[] {p1, p2};
                h1.setPermissions(perms);
                em.persist(h1);
                em.persist(p1);
                em.persist(p2);

                tx.commit();
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

                ArrayHolder h1 = em.find(ArrayHolder.class, 1);
                assertNotNull(h1);
                Permission[] perms = h1.getPermissions();
                assertNotNull(perms);
                assertEquals(2, perms.length);
                assertEquals(101, perms[0].getId());
                assertEquals(102, perms[1].getId());

                tx.commit();
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
            clean(ArrayHolder.class);
            clean(Permission.class);
        }
    }
}