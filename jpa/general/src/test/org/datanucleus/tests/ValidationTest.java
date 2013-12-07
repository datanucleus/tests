/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
2012 Andy Jefferson - tests for non-persistent field
    ...
**********************************************************************/
package org.datanucleus.tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.ConstraintViolationException;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.validation.ValidatedOwner;
import org.jpox.samples.validation.ValidatedPerson;
import org.jpox.samples.validation.ValidatedPerson2;
import org.jpox.samples.validation.ValidatedPet;

/**
 * Tests for validation of JPA entities (javax.validation JSR303).
 */
public class ValidationTest extends JPAPersistenceTestCase
{
    public ValidationTest(String name)
    {
        super(name);
    }

    public void testInsertNotNull()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            ValidatedPerson p1 = new ValidatedPerson(1, "Fred", null);
            em.persist(p1);
            tx.commit();
            fail("Should have thrown ConstraintViolationException");
        }
        catch (ConstraintViolationException cve)
        {
            // Expected
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

    public void testUpdateNotNull()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            ValidatedPerson p1 = new ValidatedPerson(1, "Fred", "Smith");
            em.persist(p1);
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
            ValidatedPerson p1 = (ValidatedPerson)em.find(ValidatedPerson.class, new Long(1));
            p1.setSurname(null);
            tx.commit();
            fail("Should have thrown a ConstraintViolationException on update");
        }
        catch (ConstraintViolationException cve)
        {
            // Expected
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

    public void testInsertSize()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            ValidatedPerson p1 = new ValidatedPerson(1, "Fred", "Smith");
            p1.setLogin("012345678901");
            em.persist(p1);
            tx.commit();
            fail("Should have thrown ConstraintViolationException");
        }
        catch (ConstraintViolationException cve)
        {
            // Expected
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

    public void testInsertTransientNotNull()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                ValidatedPerson2 p1 = new ValidatedPerson2(1, "Fred", "Bloggs");
                em.persist(p1);
                tx.commit();
                fail("Should have thrown exception with null transient field, but persisted!");
            }
            catch (ConstraintViolationException cve)
            {
                // Expected
                LOG.info("Threw exception when attempting persist of object with null NotNull transient field");
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
                ValidatedPerson2 p1 = new ValidatedPerson2(1, "Fred", "Bloggs");
                p1.setPassword("topsecret");
                em.persist(p1);
                tx.commit();
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception in persist of object with transient not null field", cve);
                fail("Should have persisted ok but exception thrown : " + cve.getMessage());
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
            clean(ValidatedPerson2.class);
        }
    }


    public void testInsertWithEmbedded()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                ValidatedOwner owner = new ValidatedOwner(1, "First Owner");
                ValidatedPet pet = new ValidatedPet(null);
                owner.setPet(pet);
                em.persist(owner);
                tx.commit();
                fail("Should have thrown exception with null embedded field, but persisted!");
            }
            catch (ConstraintViolationException cve)
            {
                // Expected
                LOG.info("Threw exception when attempting persist of object with null NotNull embedded field");
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
                ValidatedOwner owner = new ValidatedOwner(1, "First Owner");
                ValidatedPet pet = new ValidatedPet("Fluffy");
                owner.setPet(pet);
                em.persist(owner);
                tx.commit();
            }
            catch (ConstraintViolationException cve)
            {
                LOG.error("Exception in persist of object with transient not null field", cve);
                fail("Should have persisted ok but exception thrown : " + cve.getMessage());
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
            clean(ValidatedOwner.class);
        }
    }
}