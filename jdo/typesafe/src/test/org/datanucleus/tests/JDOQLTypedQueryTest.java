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
***********************************************************************/
package org.datanucleus.tests;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOQLTypedQuery;
import javax.jdo.JDOQLTypedSubquery;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.jdo.query.NumericExpression;

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.query.JDOQLTypedQueryImpl;
import org.datanucleus.samples.jdo.query.Appointment;
import org.datanucleus.samples.jdo.query.Coach;
import org.datanucleus.samples.jdo.query.League;
import org.datanucleus.samples.jdo.query.Manager;
import org.datanucleus.samples.jdo.query.Player;
import org.datanucleus.samples.jdo.query.QAppointment;
import org.datanucleus.samples.jdo.query.QCoach;
import org.datanucleus.samples.jdo.query.QManager;
import org.datanucleus.samples.jdo.query.QPlayer;
import org.datanucleus.samples.jdo.query.QTeam;
import org.datanucleus.samples.jdo.query.Team;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.NucleusLogger;

/**
 * Tests for JDOQL typesafe operations.
 */
public class JDOQLTypedQueryTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public JDOQLTypedQueryTest(String name)
    {
        super(name);

        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    Team.class,
                    Coach.class,
                    Player.class,
                    Manager.class,
                });
            initialised = true;
        }        
    }

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        createSampleData();

        super.setUp();
    }

    private void createSampleData()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Team team1 = new Team(1, "Real Madrid");
            team1.setWebsite(new URL("http://www.realmadrid.com"));
            team1.setLeague(League.PREMIER);
            Manager mgr1 = new Manager(1, "Jose", "Mourinho", 8);
            mgr1.setTeam(team1);
            team1.setManager(mgr1);
            pm.makePersistent(team1);

            Team team2 = new Team(2, "Barcelona");
            team2.setLeague(League.CHAMPIONSHIP);
            Manager mgr2 = new Manager(2, "Pep", "Guardiola", 3);
            mgr2.setTeam(team2);
            team2.setManager(mgr2);
            pm.makePersistent(team2);

            LocalDateTime dt = LocalDateTime.of(2008, 3, 14, 10, 9, 26, 0);
            Appointment appt = new Appointment(1, "Weekly Meetup", dt);
            pm.makePersistent(appt);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown creating test data", e);
            fail("Exception thrown creating data " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /* (non-Javadoc)
     * @see org.datanucleus.tests.PersistenceTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query<Team> q = pm.newQuery(Team.class);
            List<Team> teams = q.executeList();
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                Manager mgr = team.getManager();
                mgr.setTeam(null);
                team.setManager(null);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown deleting test data", e);
            fail("Exception thrown deleting data " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        clean(Appointment.class);
        clean(Team.class);
        clean(Manager.class);

        super.tearDown();
    }

    /**
     * Test basic querying for a candidate
     */
    public void testCandidateQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            List<Team> teams = tq.executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 2, teams.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate, but specifying the result as the candidate
     */
    public void testCandidateQueryWithCandidateResult()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            tq.result(false, QTeam.candidate()); // Really this is redundant since the candidate is selected by default, but people are allowed to do it
            List<Team> teams = tq.executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 2, teams.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate without its subclasses.
     */
    public void testCandidateQueryWithoutSubclasses()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Coach> tq = pm.newJDOQLTypedQuery(Coach.class);
            tq.excludeSubclasses();
            List<Coach> coaches = tq.executeList();
            assertNotNull("Coaches is null!", coaches);
            assertEquals("Number of teams is wrong", 0, coaches.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter
     */
    public void testFilter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.name.eq("Barcelona")).executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 1, teams.size());
            Team tm = teams.get(0);
            assertEquals("Barcelona", tm.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter
     */
    public void testFilterNullValue()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.name.eq((String)null)).executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 0, teams.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter
     */
    public void testFilter2()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            List<Manager> managers = tq.filter(cand.yearsExperience.eq(8)).executeList();
            assertNotNull("Managers is null!", managers);
            assertEquals("Number of managers is wrong", 1, managers.size());
            Manager mgr = managers.get(0);
            assertEquals("Mourinho", mgr.getLastName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter where the comparison is against null.
     */
    public void testFilterComparisonAgainstNull()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.website.eq((URL)null)).executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 1, teams.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter when navigating 1-1 relation.
     */
    public void testFilter3()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.manager.firstName.eq("Jose")).executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 1, teams.size());
            Team team = teams.get(0);
            assertEquals("Mourinho", team.getManager().getLastName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying using an enum function call.
     */
    public void testFilterEnumOrdinal()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.league.ordinal().eq(1)).executeList(); // Can only use ordinal when persisted as integer
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 1, teams.size());
            Team team = teams.get(0);
            assertEquals("Barcelona", team.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying with a filter that uses CollectionExpr.isEmpty
     */
    public void testFilterWithIsEmpty()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            tq.filter(cand.players.isEmpty().not());

            List<Team> teams = tq.executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 0, teams.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying using a variable in the filter
     */
    public void testVariable()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            NucleusLogger.GENERAL.info(">> testVariable");
            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            QPlayer varPlayer = QPlayer.variable("v");
            tq.filter(cand.players.contains(varPlayer).and(varPlayer.firstName.eq("Fred")));
            tq.result(false, varPlayer.firstName, varPlayer.lastName);
            assertEquals("SELECT v.firstName,v.lastName FROM org.datanucleus.samples.jdo.query.Team WHERE (this.players.contains(v) && (v.firstName == 'Fred'))", tq.toString());

            List<Object> players = tq.executeResultList();
            assertNotNull("Players is null!", players);
            assertEquals("Number of players is wrong", 0, players.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying with a result.
     */
    public void testResult()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            tq.result(false, cand.firstName, cand.lastName);
            List<Object> results = tq.executeResultList();

            assertNotNull("Results is null!", results);
            assertEquals("Number of results is wrong", 2, results.size());
            boolean mourinho = false;
            boolean guardiola = false;
            Iterator<Object> resultIter = results.iterator();
            while (resultIter.hasNext())
            {
                Object[] result = (Object[])resultIter.next();
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

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying with a result using aggregates.
     */
    public void testResultWithAggregates()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            tq.result(false, cand.yearsExperience.min(), cand.yearsExperience.max(), cand.yearsExperience.avg());
            Object[] results = (Object[]) tq.executeResultUnique();

            assertNotNull("Results is null!", results);
            Object[] resultComps = (Object[])results;
            assertEquals("Number of results is wrong", 3, resultComps.length);
            assertEquals("Min is incorrect", 3, resultComps[0]);
            assertEquals("Max is incorrect", 8, resultComps[1]);
            assertEquals("Avg is incorrect", 5.5, resultComps[2]); // H2 seems to treat AVG as returning the type of the expression, rather than double

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying with a result using count.
     */
    public void testResultCount()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            tq.result(false, cand.count());
            Object results = tq.executeResultUnique();
            assertEquals("Count is incorrect", 2l, results);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying with a result using count.
     */
    public void testResultCountDistinct()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            tq.result(false, cand.countDistinct());
            Object results = tq.executeResultUnique();
            assertEquals("Count is incorrect", 2l, results);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with an order.
     */
    public void testFilterWithOrder()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            List<Manager> managers = tq.orderBy(cand.yearsExperience.asc()).executeList();
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

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test basic querying for a candidate with a filter and using a parameter.
     */
    public void testFilterParameter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.name.eq(tq.stringParameter("TeamName"))).setParameter("TeamName", "Barcelona").executeList();
            assertNotNull("Teams is null!", teams);
            assertEquals("Number of teams is wrong", 1, teams.size());
            Team tm = teams.get(0);
            assertEquals("Barcelona", tm.getName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test use of subquery.
     */
    public void testSubquery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            JDOQLTypedSubquery<Manager> subq = tq.subquery(Manager.class, "m");
            QManager subCand = QManager.candidate("m");
            tq.filter(cand.yearsExperience.gt(subq.selectUnique(subCand.yearsExperience.avg())));
            List<Manager> managers = tq.executeList();
            assertNotNull("Result is null!", managers);
            assertEquals("Number of managers is wrong", 1, managers.size());
            Manager mgr = managers.get(0);
            assertEquals("Mourinho", mgr.getLastName());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test use of subquery.
     */
    public void testSubqueryReferringToOuter()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Manager> tq = pm.newJDOQLTypedQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;

            // "SELECT AVG(m.yearsExperience) FROM Manager m WHERE m.lastName = this.lastName"
            JDOQLTypedSubquery<Manager> subq = tq.subquery(Manager.class, "m");
            QManager subCand = QManager.candidate("m");
            subq.filter(subCand.lastName.eq(cand.lastName));
            NumericExpression<?> subqSelectExpr = subq.selectUnique(subCand.yearsExperience.avg());

            tq.filter(cand.yearsExperience.gt(subqSelectExpr));

            List<Manager> managers = tq.executeList();
            assertNotNull("Result is null!", managers);
            assertEquals("Number of managers is wrong", 0, managers.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test use of instanceof.
     */
    public void testInstanceof()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Coach> tq = pm.newJDOQLTypedQuery(Coach.class);
            QCoach cand = QCoach.jdoCandidate;
            tq.filter(cand.instanceOf(Manager.class));
            
            assertEquals("SELECT FROM " + Coach.class.getName() + " WHERE (this instanceof " + Manager.class.getName() + ")", tq.toString());

            List<Coach> coaches = tq.executeList();
            assertNotNull(coaches);
            assertEquals(2, coaches.size());

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test bulk update.
     */
    public void testBulkUpdate()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            tq.filter(cand.name.eq("Barcelona"));
            JDOQLTypedQueryImpl<Team> dntq = (JDOQLTypedQueryImpl<Team>)tq;
            dntq.set(cand.name, "Barcelona FC");
            long number = dntq.update();
            assertEquals("Number of records updated was wrong", 1, number);
            tq.closeAll();

            Query<Team> q = pm.newQuery(Team.class, "id == 2");
            List<Team> results = q.executeList();
            assertNotNull(results);
            assertEquals(1, results.size());
            Team team = results.iterator().next();
            assertEquals("Team name is wrong", "Barcelona FC", team.getName());
            q.closeAll();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Test bulk update.
     */
    public void testBulkDelete()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Team> tq = pm.newJDOQLTypedQuery(Team.class).filter(QTeam.jdoCandidate.name.eq("Barcelona"));
            long number = ((JDOQLTypedQueryImpl<Team>)tq).delete();
            assertEquals("Number of records deleted was wrong", 1, number);
            tq.closeAll();

            Query<Team> q = pm.newQuery(Team.class, "id == 2");
            List<Team> results = q.executeList();
            assertEquals(0, results.size());
            q.closeAll();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }


    /**
     * Test java 8 expressions.
     */
    public void testLocalDateTime()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            JDOQLTypedQuery<Appointment> tq = pm.newJDOQLTypedQuery(Appointment.class).filter(QAppointment.jdoCandidate.date.getHour().eq(10));
            List<Appointment> appts = tq.executeList();
            assertEquals("Number of appointments was wrong", 1, appts.size());
            tq.closeAll();

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Error in test", e);
            fail("Error in test :" + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
}
