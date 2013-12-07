/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeNode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.Subgraph;

import org.datanucleus.FetchGroup;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jpa.JPAEntityGraph;
import org.datanucleus.api.jpa.JPAEntityManagerFactory;
import org.datanucleus.api.jpa.JPAQuery;
import org.datanucleus.samples.annotations.entitygraph.GraphBase;
import org.datanucleus.samples.annotations.entitygraph.GraphRelated;
import org.datanucleus.tests.JPAPersistenceTestCase;
import org.datanucleus.util.StringUtils;

/**
 * Testcases for EntityGraph.
 */
public class EntityGraphTest extends JPAPersistenceTestCase
{
    public EntityGraphTest(String name)
    {
        super(name);
    }

    /**
     * Test of specification and registering of a named EntityGraph.
     */
    public void testNamedGraphSpecification()
    {
        try
        {
            Set<String> graphNames = ((JPAEntityManagerFactory)emf).getEntityGraphNames();
            assertNotNull(graphNames);
            assertEquals("Number of EntityGraphs is wrong", 1, graphNames.size());
            JPAEntityGraph eg = (JPAEntityGraph)((JPAEntityManagerFactory)emf).getNamedEntityGraph(graphNames.iterator().next());
            assertEquals("baseGraph", eg.getName());
            assertEquals(GraphBase.class, eg.getClassType());
            assertFalse(eg.getIncludeAllAttributes());
            List<AttributeNode> egNodes = eg.getAttributeNodes();
            assertNotNull(egNodes);
            assertEquals(3, egNodes.size());
            boolean idPresent = false;
            boolean namePresent = false;
            boolean relationPresent = false;
            for (AttributeNode node : egNodes)
            {
                if (node.getAttributeName().equals("id"))
                {
                    idPresent = true;
                }
                else if (node.getAttributeName().equals("name"))
                {
                    namePresent = true;
                }
                else if (node.getAttributeName().equals("relation"))
                {
                    relationPresent = true;
                    Map<Class, Subgraph> subgraphsByClass = node.getSubgraphs();
                    assertNotNull(subgraphsByClass);
                    assertEquals(1, subgraphsByClass.size());
                    Map.Entry<Class, Subgraph> subgraphEntry = subgraphsByClass.entrySet().iterator().next();
                    assertEquals(GraphRelated.class, subgraphEntry.getKey());
                    Subgraph subgraph = subgraphEntry.getValue();
                    List<AttributeNode> subNodes = subgraph.getAttributeNodes();
                    assertNotNull(subNodes);
                    assertEquals(1, subNodes.size());
                    AttributeNode subNode = subNodes.iterator().next();
                    assertEquals("id", subNode.getAttributeName());
                }
            }
            assertTrue("id not present", idPresent);
            assertTrue("name not present", namePresent);
            assertTrue("relation not present", relationPresent);

            // Should have been registered as dynamic FetchGroups
            NucleusContext nucCtx = emf.unwrap(NucleusContext.class);
            Set<FetchGroup> fgs = nucCtx.getFetchGroupManager().getFetchGroupsWithName("baseGraph");
            assertEquals(2, fgs.size());
        }
        finally
        {
        }
    }

    /**
     * Test of specification and registering of a defined EntityGraph.
     */
    public void testQueryDefinedEntityGraph_FetchGraph()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                GraphBase base = new GraphBase(1, "First Base");
                GraphRelated related = new GraphRelated(101);
                base.setRelation(related);
                em.persist(base);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist", e);
                fail("Exception in test : " + e.getMessage());
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

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();

                EntityGraph<GraphBase> eg = em.createEntityGraph(GraphBase.class);
                eg.addAttributeNodes("id");
                eg.addAttributeNodes("name");
                eg.addAttributeNodes("relation");
                assertNull(eg.getName());

                Query q = em.createQuery("SELECT b FROM GraphBase b");
                q.setHint("javax.persistence.fetchgraph", eg);
                List<GraphBase> results = q.getResultList();

                LOG.info(">> FetchPlan=" + ((JPAQuery)q).getFetchPlan());
                Set<String> fpgroups = ((JPAQuery)q).getFetchPlan().getGroups();
                assertEquals(1, fpgroups.size());
                LOG.info(">> FPgroups=" + StringUtils.collectionToString(fpgroups));

                GraphBase result = results.get(0);
                PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
                assertTrue(util.isLoaded(result, "id"));
                assertTrue(util.isLoaded(result, "name"));
                assertTrue(util.isLoaded(result, "relation"));
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+query", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(GraphBase.class);
            clean(GraphRelated.class);
        }
    }

    public void testQueryNamedEntityGraph_FetchGraph()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                GraphBase base = new GraphBase(1, "First Base");
                GraphRelated related = new GraphRelated(101);
                base.setRelation(related);
                em.persist(base);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+query", e);
                fail("Exception in test : " + e.getMessage());
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

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                EntityGraph eg = em.getEntityGraph("baseGraph");

                Query q = em.createQuery("SELECT b FROM GraphBase b");
                q.setHint("javax.persistence.fetchgraph", eg);
                List<GraphBase> results = q.getResultList();

                // Check internal implementation setting groups to just this group
                LOG.info(">> Query : FetchPlan=" + ((JPAQuery)q).getFetchPlan());
                Set<String> fpgroups = ((JPAQuery)q).getFetchPlan().getGroups();
                assertEquals(1, fpgroups.size());
                assertTrue(fpgroups.contains("baseGraph"));

                GraphBase result = results.get(0);
                PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
                assertTrue(util.isLoaded(result, "id"));
                assertTrue(util.isLoaded(result, "name"));
                assertTrue(util.isLoaded(result, "relation"));
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+query", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(GraphBase.class);
            clean(GraphRelated.class);
        }
    }

    public void testQueryNamedEntityGraph_LoadGraph()
    {
        try
        {
            EntityManager em = emf.createEntityManager();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();
                GraphBase base = new GraphBase(1, "First Base");
                GraphRelated related = new GraphRelated(101);
                base.setRelation(related);
                em.persist(base);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+query", e);
                fail("Exception in test : " + e.getMessage());
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

            em = emf.createEntityManager();
            tx = em.getTransaction();
            try
            {
                tx.begin();
                EntityGraph eg = em.getEntityGraph("baseGraph");

                Query q = em.createQuery("SELECT b FROM GraphBase b");
                q.setHint("javax.persistence.loadgraph", eg);
                List<GraphBase> results = q.getResultList();

                // Check internal implementation setting groups to just this group
                Set<String> fpgroups = ((JPAQuery)q).getFetchPlan().getGroups();
                assertEquals(2, fpgroups.size());
                assertTrue(fpgroups.contains("default"));
                assertTrue(fpgroups.contains("baseGraph"));

                GraphBase result = results.get(0);
                PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
                assertTrue(util.isLoaded(result, "id"));
                assertTrue(util.isLoaded(result, "name"));
                assertTrue(util.isLoaded(result, "relation"));
                tx.rollback();
            }
            catch (Exception e)
            {
                LOG.error("Exception on persist+query", e);
                fail("Exception in test : " + e.getMessage());
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
            clean(GraphBase.class);
            clean(GraphRelated.class);
        }
    }
}