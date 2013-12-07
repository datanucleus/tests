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

import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.datanucleus.samples.jpa.query.AbstractPerson;
import org.datanucleus.samples.jpa.query.Manager;
import org.datanucleus.samples.jpa.query.Manager_;
import org.datanucleus.samples.jpa.query.Person;
import org.datanucleus.samples.jpa.query.Player;
import org.datanucleus.samples.jpa.query.Player_;
import org.datanucleus.samples.jpa.query.Team;
import org.datanucleus.samples.jpa.query.Team_;
import org.datanucleus.tests.JPAPersistenceTestCase;

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
            }

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

            CriteriaBuilder qb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = qb.createQuery(Team.class);
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
     * Test basic querying for a candidate with a filter
     */
    public void testFilter()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = qb.lessThan(candidate.get(Manager_.yearsExperience), 5);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Player> crit = qb.createQuery(Player.class);
            Root<Player> candidate = crit.from(Player.class);
            candidate.alias("p");
            crit.select(candidate);

            Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.YEAR, 1987);
            cal.set(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Predicate ageGreaterThan = qb.greaterThan(candidate.get(Player_.startDate), cal.getTime());
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate yrsUpper = qb.lessThan(candidate.get(Manager_.yearsExperience), 5);
            Predicate yrsLower = qb.greaterThanOrEqualTo(candidate.get(Manager_.yearsExperience), 2);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
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
     * Test basic querying with a result with order.
     */
    public void testResultWithOrder()
    {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try
        {
            tx.begin();

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.multiselect(candidate.get(Manager_.firstName), candidate.get(Manager_.lastName));

            crit.orderBy(qb.desc(candidate.get(Manager_.yearsExperience)));

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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = qb.greaterThan(candidate.get(Manager_.yearsExperience), 1);
            crit.where(ageLessThan);

            crit.orderBy(qb.asc(candidate.get(Manager_.yearsExperience)));

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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Predicate ageLessThan = qb.greaterThan(candidate.get(Manager_.yearsExperience), 1);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            ParameterExpression<String> param = qb.parameter(String.class, "myParam");
            Predicate ageLessThan = qb.like(candidate.get(Manager_.firstName), param);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            ParameterExpression<String> param = qb.parameter(String.class);
            Predicate ageLessThan = qb.like(candidate.get(Manager_.firstName), param);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();
            CriteriaQuery<Team> crit = qb.createQuery(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            crit.select(candidate);
            Join<Team, Manager> mgrRoot = candidate.join(Team_.manager, JoinType.INNER);
            mgrRoot.alias("m");
            Predicate onCond = qb.equal(mgrRoot.get(Manager_.lastName), "Mourinho");
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();
            CriteriaDelete<Team> crit = qb.createCriteriaDelete(Team.class);
            Root<Team> candidate = crit.from(Team.class);
            candidate.alias("t");
            Predicate teamName = qb.equal(candidate.get(Team_.name), "Barcelona");
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();
            CriteriaUpdate<Team> crit = qb.createCriteriaUpdate(Team.class);
            Root<Team> candidate = crit.from(Team.class);

            candidate.alias("t");
            crit.set(candidate.get(Team_.name), "Barcelona FC");

            Predicate teamName = qb.equal(candidate.get(Team_.id), 2);
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

            CriteriaBuilder qb = emf.getCriteriaBuilder();

            CriteriaQuery<Manager> crit = qb.createQuery(Manager.class);
            Root<Manager> candidate = crit.from(Manager.class);
            candidate.alias("m");
            crit.select(candidate);

            Expression<String> upperExpr = qb.function("UPPER", String.class, candidate.get(Manager_.firstName));
            Predicate firstNameUpperCaseEquals = qb.equal(upperExpr, "PEP");
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

                CriteriaBuilder qb = emf.getCriteriaBuilder();
                CriteriaQuery<Team> crit = qb.createQuery(Team.class);
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
}