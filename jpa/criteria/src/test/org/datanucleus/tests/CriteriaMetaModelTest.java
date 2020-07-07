/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.datanucleus.samples.jpa.criteria.ConcreteEntity105;
import org.datanucleus.samples.jpa.criteria.ConcreteEntity105_;
import org.datanucleus.samples.jpa.criteria.OtherEntity105;
import org.datanucleus.samples.jpa.criteria.embedded.A;
import org.datanucleus.samples.jpa.criteria.embedded.A_;
import org.datanucleus.samples.jpa.criteria.embedded.B;
import org.datanucleus.samples.jpa.criteria.embedded.B_;
import org.datanucleus.samples.jpa.criteria.embedded.C;
import org.datanucleus.samples.jpa.query.AbstractPerson;
import org.datanucleus.samples.jpa.query.Manager;
import org.datanucleus.samples.jpa.query.Manager_;
import org.datanucleus.samples.jpa.query.Person;
import org.datanucleus.samples.jpa.query.Player;
import org.datanucleus.samples.jpa.query.Player_;
import org.datanucleus.samples.jpa.query.Team;
import org.datanucleus.samples.jpa.query.Team_;

/**
 * Tests for the Criteria API in JPA using generated criteria classes.
 */
public class CriteriaMetaModelTest extends JPAPersistenceTestCase
{
    private static boolean initialised = false;

    public CriteriaMetaModelTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                });
        }
    }

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Team team1 = new Team(1, "Real Madrid");
            Manager mgr1 = new Manager(1, "Jose", "Mourinho", 8);
            mgr1.setTeam(team1);
            team1.setManager(mgr1);
            em.persist(team1);

            Team team2 = new Team(2, "Barcelona");
            Manager mgr2 = new Manager(2, "Pep", "Guardiola", 3);
            mgr2.setTeam(team2);
            team2.setManager(mgr2);
            em.persist(team2);

            Player pl1 = new Player(101, "David", "Beckham");
            Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.YEAR, 1989);
            cal.set(Calendar.MONTH, 8);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            pl1.setStartDate(cal.getTime());
            pl1.setTeam(team1);
            pl1.setDateTime(LocalDateTime.now());
            em.persist(pl1);

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

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Query q = em.createQuery("SELECT t FROM Team t");
            List<Team> teams = q.getResultList();
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                Manager mgr = team.getManager();
                mgr.setTeam(null);
                team.setManager(null);
            }

            Query q2 = em.createQuery("SELECT p FROM Player p");
            List<Player> players = q2.getResultList();
            Iterator<Player> playerIter = players.iterator();
            while (playerIter.hasNext())
            {
                Player pl = playerIter.next();
                Team t = pl.getTeam();
                t.getPlayers().remove(pl);
                pl.setTeam(null);
            }
            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception in tearDown", e);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            em.close();
        }
        clean(Player.class);
        clean(Team.class);
        clean(Manager.class);
        clean(Person.class);

        super.tearDown();
    }

    /**
     * Test some simple metamodel requests.
     */
    public void testMetamodel()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            Metamodel mm = em.getMetamodel();
            Set<EntityType<?>> ets = mm.getEntities();
            boolean personPresent = false;
            boolean abstractPersonPresent = false;
            for (EntityType<?> ent : ets)
            {
                if (ent.getJavaType().getName().equals(Person.class.getName()))
                {
                    personPresent = true;
                }
                if (ent.getJavaType().getName().equals(AbstractPerson.class.getName()))
                {
                    abstractPersonPresent = true;
                }
            }
            assertTrue(Person.class.getName() + " is not listed as Entity in MetaModel", personPresent);
            assertFalse(AbstractPerson.class.getName() + " is listed as Entity in MetaModel!", abstractPersonPresent);

            // Query constructed using JPA Metamodel - why would anyone do this when they have a STATIC Metamodel and type-safety I've no idea
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery crit = cb.createQuery();
            Root<Team> candidate = crit.from(mm.entity(Team.class));
            candidate.alias("t");
            crit.select(candidate);

            // DN extension
            assertEquals("Generated JPQL query is incorrect", "SELECT t FROM org.datanucleus.samples.jpa.query.Team t", crit.toString());

            Query q = em.createQuery(crit);
            List<Team> teams = q.getResultList();

            assertNotNull("Null results returned!", teams);
            assertEquals("Number of results is incorrect", 2, teams.size());
            boolean realmadrid = false;
            boolean barcelona = false;
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                if (team.getName().equals("Barcelona"))
                {
                    barcelona = true;
                }
                else if (team.getName().equals("Real Madrid"))
                {
                    realmadrid = true;
                }
            }
            assertTrue("Barcelona not returned", barcelona);
            assertTrue("Real Madrid not returned", realmadrid);

            tx.rollback();
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

    /**
     * Test basic generation of query with candidate and alias.
     */
    public void testCandidate()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = cb.createQuery(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            crit.select(candidate);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT t FROM org.datanucleus.samples.jpa.query.Team t", crit.toString());

            Query q = em.createQuery(crit);
            List<Team> teams = q.getResultList();

            assertNotNull("Null results returned!", teams);
            assertEquals("Number of results is incorrect", 2, teams.size());
            boolean realmadrid = false;
            boolean barcelona = false;
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                if (team.getName().equals("Barcelona"))
                {
                    barcelona = true;
                }
                else if (team.getName().equals("Real Madrid"))
                {
                    realmadrid = true;
                }
            }
            assertTrue("Barcelona not returned", barcelona);
            assertTrue("Real Madrid not returned", realmadrid);

            tx.rollback();
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

    /**
     * Test basic generation of query with candidate and alias.
     */
    public void testCandidateWithoutSelect()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = cb.createQuery(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT t FROM org.datanucleus.samples.jpa.query.Team t", crit.toString());

            Query q = em.createQuery(crit);
            List<Team> teams = q.getResultList();

            assertNotNull("Null results returned!", teams);
            assertEquals("Number of results is incorrect", 2, teams.size());
            boolean realmadrid = false;
            boolean barcelona = false;
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                if (team.getName().equals("Barcelona"))
                {
                    barcelona = true;
                }
                else if (team.getName().equals("Real Madrid"))
                {
                    realmadrid = true;
                }
            }
            assertTrue("Barcelona not returned", barcelona);
            assertTrue("Real Madrid not returned", realmadrid);

            tx.rollback();
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

    /**
     * Test basic generation of query with candidate and alias.
     */
    public void testCandidateDistinct()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = cb.createQuery(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            crit.select(candidate).distinct(true);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT DISTINCT t FROM org.datanucleus.samples.jpa.query.Team t", crit.toString());

            Query q = em.createQuery(crit);
            List<Team> teams = q.getResultList();

            assertNotNull("Null results returned!", teams);
            assertEquals("Number of results is incorrect", 2, teams.size());
            boolean realmadrid = false;
            boolean barcelona = false;
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                if (team.getName().equals("Barcelona"))
                {
                    barcelona = true;
                }
                else if (team.getName().equals("Real Madrid"))
                {
                    realmadrid = true;
                }
            }
            assertTrue("Barcelona not returned", barcelona);
            assertTrue("Real Madrid not returned", realmadrid);

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with a filter
     */
    public void testFilter()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = cb.lessThan(candidate.get(Manager_.yearsExperience), 5);
            crit.where(ageLessThan);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE (m.yearsExperience < 5)", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Manager> iter = results.iterator();
            boolean guardiola = false;
            while (iter.hasNext())
            {
                Manager mgr = iter.next();
                if (mgr.getFirstName().equals("Pep") && mgr.getLastName().equals("Guardiola"))
                {
                    guardiola = true;
                }
            }
            assertTrue("Guardiola was not returned!", guardiola);

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with a filter
     */
    public void testFilterWithDate()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Player> crit = cb.createQuery(Player.class);
            Root<Player> candidate = crit.from(Player.class);
            candidate.alias("p");
            crit.select(candidate);

            Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.YEAR, 1987);
            cal.set(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Predicate ageGreaterThan = cb.greaterThan(candidate.get(Player_.startDate), cal.getTime());
            crit.where(ageGreaterThan);

            // DN extension
//            assertEquals("Generated JPQL query is incorrect",
//                "SELECT p FROM org.datanucleus.samples.jpa.query.Player p WHERE (p.startDate > 'date')", crit.toString());

            Query q = em.createQuery(crit);
            List<Player> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Player> iter = results.iterator();
            boolean beckham = false;
            while (iter.hasNext())
            {
                Player plyr = iter.next();
                if (plyr.getFirstName().equals("David") && plyr.getLastName().equals("Beckham"))
                {
                    beckham = true;
                }
            }
            assertTrue("Beckham was not returned!", beckham);

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with a filter with 2 clauses
     */
    public void testFilter2()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate yrsUpper = cb.lessThan(candidate.get(Manager_.yearsExperience), 5);
            Predicate yrsLower = cb.greaterThanOrEqualTo(candidate.get(Manager_.yearsExperience), 2);
            crit.where(yrsUpper, yrsLower);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE (m.yearsExperience < 5) AND (m.yearsExperience >= 2)", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());
            Iterator<Manager> iter = results.iterator();
            boolean guardiola = false;
            while (iter.hasNext())
            {
                Manager mgr = iter.next();
                if (mgr.getFirstName().equals("Pep") && mgr.getLastName().equals("Guardiola"))
                {
                    guardiola = true;
                }
            }
            assertTrue("Guardiola was not returned!", guardiola);

            tx.rollback();
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

    /**
     * Test basic querying with a result.
     */
    public void testResult()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            Set<Join<Manager, ?>> joins = candidate.getJoins();
            assertNotNull(joins); // Make sure joins returns empty set
            assertEquals(0, joins.size());
            Set<Fetch<Manager, ?>> fetches = candidate.getFetches();
            assertNotNull(fetches); // Make sure fetches returns empty set
            assertEquals(0, fetches.size());

            candidate.alias("m");
            crit.multiselect(candidate.get(Manager_.firstName), candidate.get(Manager_.lastName));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m.firstName,m.lastName FROM org.datanucleus.samples.jpa.query.Manager m", crit.toString());

            Query q = em.createQuery(crit);
            List<Object[]> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            boolean mourinho = false;
            boolean guardiola = false;
            Iterator<Object[]> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Object[] result = resultIter.next();
                assertEquals(2, result.length);
                if (result[0].equals("Jose") && result[1].equals("Mourinho"))
                {
                    mourinho = true;
                }
                else if (result[0].equals("Pep") && result[1].equals("Guardiola"))
                {
                    guardiola = true;
                }
            }
            if (!mourinho)
            {
                fail("Jose Mourinho not returned");
            }
            if (!guardiola)
            {
                fail("Pep Guardiola not returned");
            }

            tx.rollback();
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

    /**
     * Test basic querying with a result of a value type.
     */
    public void testResultOfValueType()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<LocalDateTime> crit = cb.createQuery(LocalDateTime.class);
            Root<Player> candidate = crit.from(Player.class);
            candidate.alias("p");
            crit.multiselect(candidate.get(Player_.dateTime));

            // DN extension
            assertEquals("Generated JPQL query is incorrect", "SELECT p.dateTime FROM org.datanucleus.samples.jpa.query.Player p", crit.toString());

            TypedQuery<LocalDateTime> q = em.createQuery(crit);
            List<LocalDateTime> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 1, results.size());

            Iterator<LocalDateTime> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Object result = resultIter.next();
                LOG.debug(">> result=" + result);
            }

            tx.rollback();
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

    /**
     * Test basic querying with a result as a Tuple.
     */
    public void testResultTuple()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Tuple> crit = cb.createTupleQuery();
            Root<Manager> candidate = crit.from(Manager.class);
            Set<Join<Manager, ?>> joins = candidate.getJoins();
            assertNotNull(joins); // Make sure joins returns empty set
            assertEquals(0, joins.size());
            Set<Fetch<Manager, ?>> fetches = candidate.getFetches();
            assertNotNull(fetches); // Make sure fetches returns empty set
            assertEquals(0, fetches.size());

            candidate.alias("m");
            crit.multiselect(candidate.get(Manager_.firstName), candidate.get(Manager_.lastName));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m.firstName,m.lastName FROM org.datanucleus.samples.jpa.query.Manager m", crit.toString());

            Query q = em.createQuery(crit);
            List<Tuple> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            boolean mourinho = false;
            boolean guardiola = false;
            Iterator<Tuple> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Tuple result = resultIter.next();
                List<TupleElement<?>> tupleElements = result.getElements(); 
                assertEquals(2, tupleElements.size());
                Object elem0 = result.get(0);
                Object elem1 = result.get(1);
                assertTrue(elem0 instanceof String);
                assertTrue(elem1 instanceof String);
                String first = (String)elem0;
                String last = (String)elem1;
                if (first.equals("Jose") && last.equals("Mourinho"))
                {
                    mourinho = true;
                }
                else if (first.equals("Pep") && last.equals("Guardiola"))
                {
                    guardiola = true;
                }
            }
            if (!mourinho)
            {
                fail("Jose Mourinho not returned");
            }
            if (!guardiola)
            {
                fail("Pep Guardiola not returned");
            }

            tx.rollback();
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

    /**
     * Test basic querying with a result including CASE expression.
     */
    public void testResultCase()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            Set<Join<Manager, ?>> joins = candidate.getJoins();
            assertNotNull(joins); // Make sure joins returns empty set
            assertEquals(0, joins.size());
            Set<Fetch<Manager, ?>> fetches = candidate.getFetches();
            assertNotNull(fetches); // Make sure fetches returns empty set
            assertEquals(0, fetches.size());
            candidate.alias("m");

            Path<Integer> yrsVar = candidate.get(Manager_.yearsExperience);
            Predicate lessThan2 = cb.lessThan(yrsVar, 5);
            crit.multiselect(candidate.get(Manager_.firstName), candidate.get(Manager_.lastName), cb.selectCase().when(lessThan2, "Junior").otherwise("Senior"));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m.firstName,m.lastName,CASE WHEN (m.yearsExperience < 5) THEN 'Junior' ELSE 'Senior' END FROM org.datanucleus.samples.jpa.query.Manager m", crit.toString());

            Query q = em.createQuery(crit);
            List<Object[]> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            boolean mourinho = false;
            boolean guardiola = false;
            Iterator<Object[]> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Object[] result = resultIter.next();
                assertEquals(3, result.length);
                if (result[0].equals("Jose") && result[1].equals("Mourinho"))
                {
                    mourinho = true;
                    assertEquals("Senior", result[2]);
                }
                else if (result[0].equals("Pep") && result[1].equals("Guardiola"))
                {
                    guardiola = true;
                    assertEquals("Junior", result[2]);
                }
            }
            if (!mourinho)
            {
                fail("Jose Mourinho not returned");
            }
            if (!guardiola)
            {
                fail("Pep Guardiola not returned");
            }

            tx.rollback();
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

    /**
     * Test basic querying with a result with order.
     */
    public void testResultWithOrder()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.multiselect(candidate.get(Manager_.firstName), candidate.get(Manager_.lastName));

            crit.orderBy(cb.desc(candidate.get(Manager_.yearsExperience)));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m.firstName,m.lastName FROM org.datanucleus.samples.jpa.query.Manager m ORDER BY m.yearsExperience DESC", crit.toString());

            Query q = em.createQuery(crit);
            List<Object[]> results = q.getResultList();

            assertNotNull("Null results returned!", results);
            assertEquals("Number of results is incorrect", 2, results.size());
            boolean mourinho = false;
            boolean guardiola = false;
            Iterator<Object[]> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Object[] result = resultIter.next();
                assertEquals(2, result.length);
                if (result[0].equals("Jose") && result[1].equals("Mourinho"))
                {
                    mourinho = true;
                }
                else if (result[0].equals("Pep") && result[1].equals("Guardiola"))
                {
                    guardiola = true;
                }
            }
            if (!mourinho)
            {
                fail("Jose Mourinho not returned");
            }
            if (!guardiola)
            {
                fail("Pep Guardiola not returned");
            }

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with an order.
     */
    public void testFilterWithOrder()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = cb.greaterThan(candidate.get(Manager_.yearsExperience), 1);
            crit.where(ageLessThan);

            crit.orderBy(cb.asc(candidate.get(Manager_.yearsExperience)));

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE (m.yearsExperience > 1) ORDER BY m.yearsExperience ASC", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> managers = q.getResultList();

            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 2, managers.size());
            Manager mgr1 = managers.get(0);
            Manager mgr2 = managers.get(1);
            assertEquals("Pep", mgr1.getFirstName());
            assertEquals("Guardiola", mgr1.getLastName());
            assertEquals(3, mgr1.getYearsExperience());
            assertEquals("Jose", mgr2.getFirstName());
            assertEquals("Mourinho", mgr2.getLastName());
            assertEquals(8, mgr2.getYearsExperience());

            tx.rollback();
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

    /**
     * Test basic querying specifying null order clause
     */
    public void testFilterWithNullOrdering()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = cb.greaterThan(candidate.get(Manager_.yearsExperience), 1);
            crit.where(ageLessThan);

            crit.orderBy(Collections.EMPTY_LIST);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE (m.yearsExperience > 1)", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> managers = q.getResultList();

            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 2, managers.size());

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with a filter.
     */
    public void testFilterWithMethodAndNamedParameter()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            ParameterExpression<String> param = cb.parameter(String.class, "myParam");
            Predicate ageLessThan = cb.like(candidate.get(Manager_.firstName), param);
            crit.where(ageLessThan);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE m.firstName LIKE :myParam", crit.toString());

            Query q = em.createQuery(crit);
            q.setParameter("myParam", "Jo%");
            List<Manager> managers = q.getResultList();

            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 1, managers.size());
            Manager mgr1 = managers.get(0);
            assertEquals("Jose", mgr1.getFirstName());
            assertEquals("Mourinho", mgr1.getLastName());
            assertEquals(8, mgr1.getYearsExperience());

            tx.rollback();
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

    /**
     * Test basic querying for a candidate with a filter.
     */
    public void testFilterWithMethodAndUnnamedParameter()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            ParameterExpression<String> param = cb.parameter(String.class);
            Predicate ageLessThan = cb.like(candidate.get(Manager_.firstName), param);
            crit.where(ageLessThan);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE m.firstName LIKE :DN_PARAM_0", crit.toString());

            Query q = em.createQuery(crit);
            q.setParameter(param, "Jo%");
            List<Manager> managers = q.getResultList();

            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 1, managers.size());
            Manager mgr1 = managers.get(0);
            assertEquals("Jose", mgr1.getFirstName());
            assertEquals("Mourinho", mgr1.getLastName());
            assertEquals(8, mgr1.getYearsExperience());

            tx.rollback();
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

    /**
     * Test generation of FROM with left outer join and ON clause.
     */
    public void testLeftOuterJoinOnQuery()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

//          "SELECT Object(T) FROM " + Team.class.getName() + " T " +
//          "JOIN T.manager M ON M.lastName = 'Mourinho'").getResultList();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = cb.createQuery(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            crit.select(candidate);
            Join<Team, Manager> mgrRoot = candidate.join(Team_.manager, JoinType.INNER);
            mgrRoot.alias("m");
            Predicate onCond = cb.equal(mgrRoot.get(Manager_.lastName), "Mourinho");
            mgrRoot.on(onCond);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT t FROM org.datanucleus.samples.jpa.query.Team t JOIN t.manager m ON (m.lastName = 'Mourinho')", crit.toString());

            Query q = em.createQuery(crit);
            List<Team> teams = q.getResultList();

            assertNotNull("Null results returned!", teams);
            assertEquals("Number of results is incorrect", 1, teams.size());
            boolean realmadrid = false;
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                if (team.getName().equals("Real Madrid"))
                {
                    realmadrid = true;
                }
            }
            assertTrue("Real Madrid not returned", realmadrid);

            tx.rollback();
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

    /**
     * Test generation of a delete Criteria query
     */
    public void testDelete()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaDelete<Team> crit = cb.createCriteriaDelete(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            Predicate teamName = cb.equal(candidate.get(Team_.name), "Barcelona");
            crit.where(teamName);

            // DN extension
            assertEquals("Generated JPQL delete query is incorrect",
                "DELETE FROM org.datanucleus.samples.jpa.query.Team t WHERE (t.name = 'Barcelona')", crit.toString());
            Query q = em.createQuery(crit);
            int num = q.executeUpdate();
            assertEquals("Number of objects deleted is wrong",  1, num);

            tx.rollback();
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

    /**
     * Test generation of an update Criteria query
     */
    public void testUpdate()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaUpdate<Team> crit = cb.createCriteriaUpdate(Team.class);
            Root<Team> candidate = crit.from(Team.class);

            candidate.alias("t");
            crit.set(candidate.get(Team_.name), "Barcelona FC");

            Predicate teamName = cb.equal(candidate.get(Team_.id), 2);
            crit.where(teamName);

            // DN extension
            assertEquals("Generated JPQL update query is incorrect",
                "UPDATE org.datanucleus.samples.jpa.query.Team t SET t.name = 'Barcelona FC' WHERE (t.id = 2)", crit.toString());
            Query q = em.createQuery(crit);
            int num = q.executeUpdate();
            assertEquals("Number of objects updated is wrong",  1, num);

            tx.rollback();
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

    /**
     * Test criteria query with a function (SQL function).
     */
    public void testFunction()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Expression<String> upperExpr = cb.function("UPPER", String.class, candidate.get(Manager_.firstName));
            Predicate firstNameUpperCaseEquals = cb.equal(upperExpr, "PEP");
            crit.where(firstNameUpperCaseEquals);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE (FUNCTION('UPPER',m.firstName) = 'PEP')", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> managers = q.getResultList();

            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 1, managers.size());
            Manager mgr1 = managers.get(0);
            assertEquals("Pep", mgr1.getFirstName());
            assertEquals("Guardiola", mgr1.getLastName());

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception during query", e);
            fail("Exception during test : " + e.getMessage());
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

    /**
     * Test generation of FROM with multiple joins.
     */
    public void testMultipleJoin()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Player> crit = cb.createQuery(Player.class);
            Root<Player> candidate = crit.from(Player.class);
            candidate.alias("p");
            crit.select(candidate);
            Join<Player, Team> teamRoot = candidate.join(Player_.team, JoinType.INNER);
            teamRoot.alias("t");
            Join<Team, Manager> mgrRoot = teamRoot.join(Team_.manager, JoinType.INNER);
            mgrRoot.alias("m");

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT p FROM org.datanucleus.samples.jpa.query.Player p JOIN p.team t JOIN t.manager m", crit.toString());

            Query q = em.createQuery(crit);
            List<Player> players = q.getResultList();

            assertNotNull("Null results returned!", players);
            assertEquals("Number of results is incorrect", 1, players.size());
            boolean beckham = false;
            Iterator<Player> playerIter = players.iterator();
            while (playerIter.hasNext())
            {
                Player pl = playerIter.next();
                if (pl.getFirstName().equals("David"))
                {
                    beckham = true;
                }
            }
            assertTrue("Beckham not returned", beckham);

            tx.rollback();
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

    /**
     * Test serialisation of Criteria query.
     */
    /*public void testSerialise()
    {
        try
        {
            EntityManager em = getEM();
            EntityTransaction tx = em.getTransaction();
            try
            {
                tx.begin();

                CriteriaBuilder cb = emf.getCriteriaBuilder();
                CriteriaQuery<Team> crit = cb.createQuery(Team.class);
                Root<Team> candidate = crit.from(Team.class);
                candidate.alias("t");
                crit.select(candidate);

                // DN extension
                assertEquals("Generated JPQL query is incorrect",
                    "SELECT t FROM org.datanucleus.samples.jpa.query.Team t", crit.toString());

                try
                {
                    // Serialise the Expression
                    FileOutputStream fileStream = new FileOutputStream("criteria.ser");
                    ObjectOutputStream os = new ObjectOutputStream(fileStream);
                    os.writeObject(crit);
                    os.close();
                }
                catch (Exception e)
                {
                    NucleusLogger.GENERAL.error(">> Exception in serialise", e);
                    fail("Failed to serialise " + StringUtils.toJVMIDString(crit));
                }

                tx.rollback();
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

                try
                {
                    // Deserialise the Expression
                    FileInputStream fileInputStream = new FileInputStream("criteria.ser");
                    ObjectInputStream oInputStream = new ObjectInputStream(fileInputStream);
                    Object obj = oInputStream.readObject();
                    if (obj instanceof CriteriaQuery)
                    {
                        CriteriaQuery crit1 = (CriteriaQuery)obj;

                        Query q = em.createQuery(crit1);
                        List<Team> teams = q.getResultList();

                        assertNotNull("Null results returned!", teams);
                        assertEquals("Number of results is incorrect", 2, teams.size());
                        boolean realmadrid = false;
                        boolean barcelona = false;
                        Iterator<Team> teamIter = teams.iterator();
                        while (teamIter.hasNext())
                        {
                            Team team = teamIter.next();
                            if (team.getName().equals("Barcelona"))
                            {
                                barcelona = true;
                            }
                            else if (team.getName().equals("Real Madrid"))
                            {
                                realmadrid = true;
                            }
                        }
                        assertTrue("Barcelona not returned", barcelona);
                        assertTrue("Real Madrid not returned", realmadrid);
                    }
                    else
                    {
                        fail("Deserialised object is " + obj.getClass().getName() + " not CriteriaQuery");
                    }
                    oInputStream.close();
                }
                catch (Exception e)
                {
                    NucleusLogger.GENERAL.error(">> Exception in deserialise", e);
                    fail("Failed to deserialise CriteriaQuery");
                }

                tx.rollback();
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

            // Delete the file
            File file = new File("criteria.ser");
            if (file.exists())
            {
                file.delete();
            }
        }
    }*/

    /**
     * Test basic generation of query with candidate and alias, plus FROM joins across embedded 1-1.
     */
    public void testJoinAcrossEmbeddedOneToOne()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            A a = new A();
            a.setId(Long.valueOf(1));
            a.setName("First A");
            B b = a.getB();
            b.setName("First B");
            C c = new C(101, BigDecimal.valueOf(123.45), "GBP");
            b.setC(c);
            em.persist(a);
            em.persist(c);
            em.flush();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<A> crit1 = cb.createQuery(A.class);
            Root<A> cand1 = crit1.from(A.class);
            cand1.alias("a");
            crit1.select(cand1);
            cand1.join(A_.b).join(B_.c);

            // DN extension
            assertEquals("Generated JPQL query is incorrect",
                "SELECT a FROM org.datanucleus.samples.jpa.criteria.embedded.A a JOIN a.b DN_JOIN_0 JOIN DN_JOIN_0.c DN_JOIN_1",
                crit1.toString());

            Query q1 = em.createQuery(crit1);
            List<A> results1 = q1.getResultList();

            assertNotNull("Null results returned!", results1);
            assertEquals("Number of results is incorrect", 1, results1.size());
            A resultA1 = results1.get(0);
            assertEquals(Long.valueOf(1), resultA1.getId());
            assertEquals("First A", resultA1.getName());

            tx.rollback();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test", e);
            fail("Exception caught during test : " + e.getMessage());
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

    /**
     * Test basic querying using IN clause.
     */
    public void testFilterIn()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            // Test 1 : IN
            CriteriaQuery<Manager> crit = cb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);
            Predicate pred = candidate.get(Manager_.firstName).in("Jose", "Pep");
            crit.where(pred);
            assertEquals("Generated JPQL query is incorrect", "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE m.firstName IN ('Jose','Pep')", crit.toString());

            Query q = em.createQuery(crit);
            List<Manager> managers = q.getResultList();
            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 2, managers.size());

            // Test 2 : NOT IN
            CriteriaQuery<Manager> crit2 = cb.createQuery(Manager.class);
            Root<Manager> candidate2 = crit2.from(Manager.class);
            candidate2.alias("m");
            crit2.select(candidate2);
            Predicate pred2 = candidate2.get(Manager_.firstName).in("Jose", "Pep").not();
            crit2.where(pred2);
            assertEquals("Generated JPQL query is incorrect", "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE !(m.firstName IN ('Jose','Pep'))", crit2.toString());

            Query q2 = em.createQuery(crit2);
            List<Manager> managers2 = q2.getResultList();
            assertNotNull("Managers is null!", managers2);
            assertEquals("Number of managers is wrong", 0, managers2.size());

            // Test 3 : IN {collParameter}
            CriteriaQuery<Manager> crit3 = cb.createQuery(Manager.class);
            Root<Manager> candidate3 = crit3.from(Manager.class);
            candidate3.alias("m");
            crit3.select(candidate3);
            ParameterExpression paramExpr = cb.parameter(List.class, "inParam");
            Predicate pred3 = candidate3.get(Manager_.firstName).in(paramExpr);
            crit3.where(pred3);
            assertEquals("Generated JPQL query is incorrect", "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE m.firstName IN (:inParam)", crit3.toString());

            Query q3 = em.createQuery(crit3);
            List<String> inParamColl = new ArrayList();
            inParamColl.add("Jose");
            inParamColl.add("Pep");
            q3.setParameter("inParam", inParamColl);
            List<Manager> managers3 = q3.getResultList();
            assertNotNull("Managers is null!", managers3);
            assertEquals("Number of managers is wrong", 2, managers3.size());

            // Test 3 : IN {stringArrayParameter}
            CriteriaQuery<Manager> crit4 = cb.createQuery(Manager.class);
            Root<Manager> candidate4 = crit4.from(Manager.class);
            candidate4.alias("m");
            crit4.select(candidate4);
            ParameterExpression paramExpr4 = cb.parameter(String[].class, "inParam");
            Predicate pred4 = candidate4.get(Manager_.firstName).in(paramExpr4);
            crit4.where(pred4);
            assertEquals("Generated JPQL query is incorrect", "SELECT m FROM org.datanucleus.samples.jpa.query.Manager m WHERE m.firstName IN (:inParam)", crit4.toString());

            Query q4 = em.createQuery(crit4);
            q4.setParameter("inParam", new String[] {"Jose", "Pep"});
            List<Manager> managers4 = q4.getResultList();
            assertNotNull("Managers is null!", managers4);
            assertEquals("Number of managers is wrong", 2, managers4.size());

            tx.rollback();
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

    /**
     * Test for api-jpa-105.
     */
    public void testApiJpa105()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            ConcreteEntity105 c1 = new ConcreteEntity105(1, "First CE", "Prop1");
            OtherEntity105 u1 = new OtherEntity105(1, "First User");
            c1.getOthers().add(u1);
            em.persist(c1);
            em.persist(u1);
            em.flush();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ConcreteEntity105> criteria = cb.createQuery(ConcreteEntity105.class);
            Root<ConcreteEntity105> concreteRoot = criteria.from(ConcreteEntity105.class);
            criteria.select(concreteRoot);
            OtherEntity105 user = u1;
            criteria.where(cb.isNotMember(user, concreteRoot.get(ConcreteEntity105_.others)));
            TypedQuery<ConcreteEntity105> query = em.createQuery(criteria);
            List<ConcreteEntity105> result = query.getResultList();
            assertEquals(0, result.size());

            tx.rollback();
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