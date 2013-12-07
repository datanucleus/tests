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
package org.datanucleus.tests;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.annotations.compoundidentity.CompoundHolder;
import org.jpox.samples.annotations.compoundidentity.CompoundSingleTarget;

/**
 * Series of tests for Compound Identity (identifying) relations.
 * 
 * @version $Revision: 1.2 $
 */
public class CompoundIdentityTest extends JPAPersistenceTestCase
{
    public CompoundIdentityTest(String name)
    {
        super(name);
    }

    /**
     * Basic test of 1-1 uni relation using compound identity relation and JPA annotations.
     */
    public void testOneToOneUniSingleAnnotations()
    {
        EntityManagerFactory emf = TestHelper.getEMF(1, "JPATest", null); // Swap to "JPATest" EMF
        try
        {
            CompoundSingleTarget targets[] = new CompoundSingleTarget[6];

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {      
                tx.begin();
                CompoundHolder holder1 = new CompoundHolder("First Holder");
                CompoundHolder holder2 = new CompoundHolder("Second Holder");
                CompoundHolder holder3 = new CompoundHolder("Third Holder");
                targets[0] = new CompoundSingleTarget(holder3, 1.0);
                targets[1] = new CompoundSingleTarget(holder3, 2.0);
                targets[2] = new CompoundSingleTarget(holder2, 3.0);
                targets[3] = new CompoundSingleTarget(holder2, 4.0);
                targets[4] = new CompoundSingleTarget(holder1, 5.0);
                targets[5] = new CompoundSingleTarget(holder1, 6.0);
                for (int i=0;i<6;i++)
                {
                    em.persist(targets[i]);
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
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

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                List result = em.createQuery(
                    "SELECT Object(T) FROM " + CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'First Holder'").getResultList();
                CompoundHolder holder1 = (CompoundHolder)result.get(0);
                result = em.createQuery(
                    "SELECT Object(T) FROM " + CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'Second Holder'").getResultList();
                CompoundHolder holder2 = (CompoundHolder)result.get(0);
                result = em.createQuery(
                    "SELECT Object(T) FROM " + CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'Third Holder'").getResultList();
                CompoundHolder holder3 = (CompoundHolder)result.get(0);
                assertEquals("Name of holder was incorrect", "First Holder", holder1.getName());
                assertEquals("Name of holder was incorrect", "Second Holder", holder2.getName());
                assertEquals("Name of holder was incorrect", "Third Holder", holder3.getName());

                for (int i = 0; i < targets.length; i++)
                {
                    result = em.createQuery(
                        "SELECT Object(T) FROM " + CompoundSingleTarget.class.getName() + 
                        " T WHERE T.value = " + (float)((i+1)*1.0)).getResultList();
                    CompoundSingleTarget target = (CompoundSingleTarget)result.get(0);
                    assertEquals(i + 1, target.getValue(), 0);
                    if (i == 0 || i == 1)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Third Holder", target.getHolder().getName());
                    }
                    if (i == 2 || i == 3)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Second Holder", target.getHolder().getName());
                    }
                    if (i == 4 || i == 5)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "First Holder", target.getHolder().getName());
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
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
            clean(emf, CompoundSingleTarget.class);
            clean(emf, CompoundHolder.class);
            emf.close();
        }
    }

    /**
     * Basic test of 1-1 uni relation using compound identity relation and JPA XML.
     */
    public void testOneToOneUniSingleXML()
    {
        EntityManagerFactory emf = TestHelper.getEMF(1, "JPATest", null); // Swap to "JPATest" EMF
        try
        {
            org.jpox.samples.compoundidentity.CompoundSingleTarget targets[] = 
                new org.jpox.samples.compoundidentity.CompoundSingleTarget[6];

            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {      
                tx.begin();
                org.jpox.samples.compoundidentity.CompoundHolder holder1 = 
                    new org.jpox.samples.compoundidentity.CompoundHolder("First Holder");
                org.jpox.samples.compoundidentity.CompoundHolder holder2 = 
                    new org.jpox.samples.compoundidentity.CompoundHolder("Second Holder");
                org.jpox.samples.compoundidentity.CompoundHolder holder3 = 
                    new org.jpox.samples.compoundidentity.CompoundHolder("Third Holder");
                targets[0] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder3, 1.0);
                targets[1] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder3, 2.0);
                targets[2] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder2, 3.0);
                targets[3] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder2, 4.0);
                targets[4] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder1, 5.0);
                targets[5] = new org.jpox.samples.compoundidentity.CompoundSingleTarget(holder1, 6.0);
                for (int i=0;i<6;i++)
                {
                    em.persist(targets[i]);
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
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

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                List result = em.createQuery(
                    "SELECT Object(T) FROM " + org.jpox.samples.compoundidentity.CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'First Holder'").getResultList();
                org.jpox.samples.compoundidentity.CompoundHolder holder1 = 
                    (org.jpox.samples.compoundidentity.CompoundHolder)result.get(0);
                result = em.createQuery(
                    "SELECT Object(T) FROM " + org.jpox.samples.compoundidentity.CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'Second Holder'").getResultList();
                org.jpox.samples.compoundidentity.CompoundHolder holder2 = 
                    (org.jpox.samples.compoundidentity.CompoundHolder)result.get(0);
                result = em.createQuery(
                    "SELECT Object(T) FROM " + org.jpox.samples.compoundidentity.CompoundHolder.class.getName() + 
                    " T WHERE T.name = 'Third Holder'").getResultList();
                org.jpox.samples.compoundidentity.CompoundHolder holder3 =
                    (org.jpox.samples.compoundidentity.CompoundHolder)result.get(0);
                assertEquals("Name of holder was incorrect", "First Holder", holder1.getName());
                assertEquals("Name of holder was incorrect", "Second Holder", holder2.getName());
                assertEquals("Name of holder was incorrect", "Third Holder", holder3.getName());

                for (int i = 0; i < targets.length; i++)
                {
                    result = em.createQuery(
                        "SELECT Object(T) FROM " + org.jpox.samples.compoundidentity.CompoundSingleTarget.class.getName() + 
                        " T WHERE T.value = " + (float)((i+1)*1.0)).getResultList();
                    org.jpox.samples.compoundidentity.CompoundSingleTarget target = 
                        (org.jpox.samples.compoundidentity.CompoundSingleTarget)result.get(0);
                    assertEquals(i + 1, target.getValue(), 0);
                    if (i == 0 || i == 1)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Third Holder", target.getHolder().getName());
                    }
                    if (i == 2 || i == 3)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "Second Holder", target.getHolder().getName());
                    }
                    if (i == 4 || i == 5)
                    {
                        assertEquals("Name of holder of target is incorrect", 
                            "First Holder", target.getHolder().getName());
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception during test", e);
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
            clean(emf, org.jpox.samples.compoundidentity.CompoundSingleTarget.class);
            clean(emf, org.jpox.samples.compoundidentity.CompoundHolder.class);
            emf.close();
        }
    }
}