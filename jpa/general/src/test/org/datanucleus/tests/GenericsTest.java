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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.datanucleus.samples.annotations.generics.GenericEnumType;
import org.datanucleus.samples.annotations.generics.GenericIdPropSub1;
import org.datanucleus.samples.annotations.generics.GenericIdPropSub2Sub;
import org.datanucleus.samples.annotations.generics.GenericOneOneRelated1;
import org.datanucleus.samples.annotations.generics.GenericOneOneRelated2;
import org.datanucleus.samples.annotations.generics.GenericOneOneSub1;
import org.datanucleus.samples.annotations.generics.GenericOneOneSub2;

/**
 * Testcases for use of generics, and in particular TypeVariable usage.
 */
public class GenericsTest extends JPAPersistenceTestCase
{
    public GenericsTest(String name)
    {
        super(name);
    }

    public void testOneToOneTypeVariableUsingProperties()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                GenericOneOneRelated1 r1 = new GenericOneOneRelated1("First Related");
                r1.setId(new Long(1));
                r1.setAge(33);
                GenericOneOneSub1 s1 = new GenericOneOneSub1("First Object");
                s1.setType(GenericEnumType.TYPE_1);
                s1.setId(new Long(101));
                s1.setAge(10);
                s1.setOwner(r1);
                r1.getRelatedObjects().add(s1);
                em.persist(r1);
                em.persist(s1);

                GenericOneOneRelated1 r2 = new GenericOneOneRelated1("Second Related");
                r2.setId(new Long(2));
                r2.setAge(25);
                GenericOneOneSub1 s2 = new GenericOneOneSub1("Second Object");
                s2.setType(GenericEnumType.TYPE_2);
                s2.setId(new Long(102));
                s2.setAge(28);
                s2.setOwner(r2);
                r2.getRelatedObjects().add(s2);
                em.persist(r2);
                em.persist(s2);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // First query, using join
                Query q1 = em.createQuery("SELECT s1 FROM GenericOneOneSub1 s1 JOIN s1.owner owner_1 WHERE s1.age < owner_1.age");
                List<GenericOneOneSub1> subs1 = q1.getResultList();
                assertEquals(1, subs1.size());

                // Query using enum
                Query q2 = em.createQuery("SELECT s1 FROM GenericOneOneSub1 s1 WHERE s1.type = org.datanucleus.samples.annotations.generics.GenericEnumType.TYPE_1");
                List<GenericOneOneSub1> subs2 = q2.getResultList();
                assertEquals(1, subs2.size());

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
            clean(GenericOneOneRelated1.class);
            clean(GenericOneOneSub1.class);
        }
    }


    public void testOneToOneTypeVariableUsingFields()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                GenericOneOneRelated2 r1 = new GenericOneOneRelated2("First Related");
                r1.setId(new Long(1));
                r1.setAge(33);
                GenericOneOneSub2 s1 = new GenericOneOneSub2("First Object");
                s1.setType(GenericEnumType.TYPE_1);
                s1.setId(new Long(101));
                s1.setAge(10);
                s1.setOwner(r1);
                r1.getRelatedObjects().add(s1);
                em.persist(r1);
                em.persist(s1);

                GenericOneOneRelated2 r2 = new GenericOneOneRelated2("Second Related");
                r2.setId(new Long(2));
                r2.setAge(25);
                GenericOneOneSub2 s2 = new GenericOneOneSub2("Second Object");
                s2.setType(GenericEnumType.TYPE_2);
                s2.setId(new Long(102));
                s2.setAge(28);
                s2.setOwner(r2);
                r2.getRelatedObjects().add(s2);
                em.persist(r2);
                em.persist(s2);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // First query, using join
                Query q1 = em.createQuery("SELECT s1 FROM GenericOneOneSub2 s1 JOIN s1.owner owner_1 WHERE s1.age < owner_1.age");
                List<GenericOneOneSub2> subs1 = q1.getResultList();
                assertEquals(1, subs1.size());

                // Query using enum
                Query q2 = em.createQuery("SELECT s1 FROM GenericOneOneSub2 s1 WHERE s1.type = org.datanucleus.samples.annotations.generics.GenericEnumType.TYPE_1");
                List<GenericOneOneSub2> subs2 = q2.getResultList();
                assertEquals(1, subs2.size());

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
            clean(GenericOneOneRelated2.class);
            clean(GenericOneOneSub2.class);
        }
    }

    public void testGenericIdProps()
    {
        try
        {
            // Try Sub1
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                
                GenericIdPropSub1 sub1 = new GenericIdPropSub1("First sub1");
                em.persist(sub1);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Check Sub1 objects
                Query q1 = em.createQuery("SELECT s1 FROM GenericIdPropSub1 s1");
                List<GenericIdPropSub1> subs1 = q1.getResultList();
                assertEquals(1, subs1.size());

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

            // Try Sub2
            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                
                GenericIdPropSub2Sub sub2 = new GenericIdPropSub2Sub("First sub2", 1);
                em.persist(sub2);

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

            em = getEM();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                // Check Sub1 objects
                Query q1 = em.createQuery("SELECT s2 FROM GenericIdPropSub2Sub s2");
                List<GenericIdPropSub2Sub> subs2 = q1.getResultList();
                assertEquals(1, subs2.size());

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
            clean(GenericIdPropSub1.class);
            clean(GenericIdPropSub2Sub.class);
        }
    }
}