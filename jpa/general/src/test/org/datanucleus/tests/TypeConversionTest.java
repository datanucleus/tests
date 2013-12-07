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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.tests.JPAPersistenceTestCase;
import org.jpox.samples.typeconversion.ComplicatedType;
import org.jpox.samples.typeconversion.ComplicatedType2;
import org.jpox.samples.typeconversion.TypeHolder;

/**
 * Tests for JPA 2.1 type conversion.
 */
public class TypeConversionTest extends JPAPersistenceTestCase
{
    public TypeConversionTest(String name)
    {
        super(name);
    }

    public void testBasicConversion()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();
            TypeHolder holder = new TypeHolder(1, "First holder");
            ComplicatedType com = new ComplicatedType("String 45", "Number 23");
            holder.setDetails(com);
            ComplicatedType2 com2 = new ComplicatedType2("String 78", "Number 34");
            holder.setDetails2(com2);
            ComplicatedType com3 = new ComplicatedType("String 90", "Number 45");
            holder.setDetails3(com3);
            em.persist(holder);
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown during persist when using type converter", e);
            fail("Failure on persist with type converter : " + e.getMessage());
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
            TypeHolder p1 = em.find(TypeHolder.class, 1);
            ComplicatedType comp = p1.getDetails();
            assertNotNull(comp);
            assertEquals("String 45", comp.getName1());
            assertEquals("Number 23", comp.getName2());
            ComplicatedType2 comp2 = p1.getDetails2();
            assertNotNull(comp2);
            assertEquals("String 78", comp2.getName1());
            assertEquals("Number 34", comp2.getName2());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error(">> Exception thrown during retrieve when using type converter", e);
            fail("Failure on retrieve with type converter : " + e.getMessage());
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
}