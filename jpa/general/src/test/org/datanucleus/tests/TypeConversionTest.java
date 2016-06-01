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

import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.datanucleus.samples.typeconversion.CollectionConverterHolder;
import org.datanucleus.samples.typeconversion.ComplicatedType;
import org.datanucleus.samples.typeconversion.ComplicatedType2;
import org.datanucleus.samples.typeconversion.MapConverterHolder;
import org.datanucleus.samples.typeconversion.MyType1;
import org.datanucleus.samples.typeconversion.MyType2;
import org.datanucleus.samples.typeconversion.TypeHolder;
import org.datanucleus.tests.JPAPersistenceTestCase;

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
        try
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
        finally
        {
            clean(TypeHolder.class);
        }
    }

    public void testMapValueConversion()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                MapConverterHolder h = new MapConverterHolder(1);
                h.getMap1().put("First", new MyType1("A", "P"));
                h.getMap1().put("Second", new MyType1("B", "S"));
                em.persist(h);
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
                MapConverterHolder p1 = em.find(MapConverterHolder.class, 1);
                assertNotNull(p1);
                Map<String, MyType1> map = p1.getMap1();
                assertNotNull(map);
                assertEquals(2, map.size());
                MyType1 val1 = map.get("First");
                assertNotNull(val1);
                assertEquals("A", val1.getName1());
                assertEquals("P", val1.getName2());
                MyType1 val2 = map.get("Second");
                assertNotNull(val2);
                assertEquals("B", val2.getName1());
                assertEquals("S", val2.getName2());

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
        finally
        {
            clean(MapConverterHolder.class);
        }
    }

    public void testMapKeyConversion()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                MapConverterHolder h = new MapConverterHolder(1);
                h.getMap2().put(new MyType2("A", 1), "First");
                h.getMap2().put(new MyType2("B", 2), "Second");
                em.persist(h);
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
                MapConverterHolder p1 = em.find(MapConverterHolder.class, 1);
                assertNotNull(p1);
                Map<MyType2, String> map = p1.getMap2();
                assertNotNull(map);
                assertEquals(2, map.size());
                MyType2 key1 = new MyType2("A", 1);
                String val1 = map.get(key1);
                assertNotNull(val1);
                assertEquals("First", val1);
                MyType2 key2 = new MyType2("B", 2);
                String val2 = map.get(key2);
                assertNotNull(val2);
                assertEquals("Second", val2);

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
        finally
        {
            clean(MapConverterHolder.class);
        }
    }

    public void testCollectionElementConversion()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                CollectionConverterHolder h = new CollectionConverterHolder(1);
                h.getSet1().add(new MyType1("A", "J"));
                h.getSet1().add(new MyType1("E", "B"));
                h.getSet2().add(new MyType1("JJJ", "KKK"));
                h.getSet2().add(new MyType1("LL", "MM"));
                em.persist(h);
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
                CollectionConverterHolder p1 = em.find(CollectionConverterHolder.class, 1);
                assertNotNull(p1);
                Set<MyType1> set1 = p1.getSet1();
                assertNotNull(set1);
                assertEquals(2, set1.size());
                boolean elem1Present = false;
                boolean elem2Present = false;
                for (MyType1 elem : set1)
                {
                    if (elem.getName1().equals("A") && elem.getName2().equals("J"))
                    {
                        elem1Present = true;
                    }
                    else if (elem.getName1().equals("E") && elem.getName2().equals("B"))
                    {
                        elem2Present = true;
                    }
                }
                assertTrue(elem1Present);
                assertTrue(elem2Present);

                Set<MyType1> set2 = p1.getSet2();
                assertNotNull(set2);
                assertEquals(2, set2.size());
                boolean elem3Present = false;
                boolean elem4Present = false;
                for (MyType1 elem : set2)
                {
                    if (elem.getName1().equals("JJJ") && elem.getName2().equals("KKK"))
                    {
                        elem3Present = true;
                    }
                    else if (elem.getName1().equals("LL") && elem.getName2().equals("MM"))
                    {
                        elem4Present = true;
                    }
                }
                assertTrue(elem3Present);
                assertTrue(elem4Present);

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
        finally
        {
            clean(CollectionConverterHolder.class);
        }
    }
}