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
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.PropertyNames;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.query.JDOTypesafeQuery;
import org.datanucleus.query.typesafe.TypesafeQuery;
import org.datanucleus.samples.jdo.query.Coach;
import org.datanucleus.samples.jdo.query.Manager;
import org.datanucleus.samples.jdo.query.Player;
import org.datanucleus.samples.jdo.query.QManager;
import org.datanucleus.samples.jdo.query.QTeam;
import org.datanucleus.samples.jdo.query.Team;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Tests for JDOQL typesafe operations.
 */
public class TypesafeTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public TypesafeTest(String name)
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
            Manager mgr1 = new Manager(1, "Jose", "Mourinho", 8);
            mgr1.setTeam(team1);
            team1.setManager(mgr1);
            pm.makePersistent(team1);

            Team team2 = new Team(2, "Barcelona");
            Manager mgr2 = new Manager(2, "Pep", "Guardiola", 3);
            mgr2.setTeam(team2);
            team2.setManager(mgr2);
            pm.makePersistent(team2);

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

            Query q = pm.newQuery(Team.class);
            List<Team> teams = (List<Team>) q.execute();
            Iterator<Team> teamIter = teams.iterator();
            while (teamIter.hasNext())
            {
                Team team = teamIter.next();
                Manager mgr = team.getManager();
                mgr.setTeam(null);
                team.setManager(null);
            }
            pm.flush();

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

        clean(Team.class);
        clean(Manager.class);

        super.tearDown();
    }

    /**
     * Test basic querying for a candidate
     */
    public void testCandidateQuery()
    {
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Coach> tq = pm.newTypesafeQuery(Coach.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Manager.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
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
     * Test basic querying with a result.
     */
    public void testResult()
    {
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Manager> tq = pm.newTypesafeQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            List<Object[]> results = tq.executeResultList(false, cand.firstName, cand.lastName);

            assertNotNull("Results is null!", results);
            assertEquals("Number of results is wrong", 2, results.size());
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Manager> tq = pm.newTypesafeQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            Object[] results = tq.executeResultUnique(false, cand.yearsExperience.min(),
                cand.yearsExperience.max(), cand.yearsExperience.avg());

            assertNotNull("Results is null!", results);
            assertEquals("Number of results is wrong", 3, results.length);
            assertEquals("Min is incorrect", 3, results[0]);
            assertEquals("Max is incorrect", 8, results[1]);
            assertEquals("Avg is incorrect", 5.0, results[2]);

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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Manager> tq = pm.newTypesafeQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            Object results = tq.executeResultUnique(false, cand.count());
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Manager> tq = pm.newTypesafeQuery(Manager.class);
            QManager cand = QManager.jdoCandidate;
            Object results = tq.executeResultUnique(false, cand.countDistinct());
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Manager> tq = pm.newTypesafeQuery(Manager.class);
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            List<Team> teams = tq.filter(cand.name.eq(tq.stringParameter("TeamName")))
                .setParameter("TeamName", "Barcelona").executeList();
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
     * Test bulk update.
     */
    public void testBulkUpdate()
    {
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            JDOTypesafeQuery jdotq = (JDOTypesafeQuery) tq.filter(cand.name.eq("Barcelona"));
            long number = ((JDOTypesafeQuery)jdotq.set(cand.name, "Barcelona FC")).update();
            assertEquals("Number of records updated was wrong", 1, number);
            tq.closeAll();

            Query q = pm.newQuery("SELECT FROM " + Team.class.getName() + " WHERE id == 2");
            List<Team> results = (List<Team>) q.execute();
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
        JDOPersistenceManager pm = (JDOPersistenceManager) pmf.getPersistenceManager();
        pm.setProperty(PropertyNames.PROPERTY_QUERY_JDOQL_ALLOWALL, "true");
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            TypesafeQuery<Team> tq = pm.newTypesafeQuery(Team.class);
            QTeam cand = QTeam.jdoCandidate;
            JDOTypesafeQuery jdotq = (JDOTypesafeQuery) tq.filter(cand.name.eq("Barcelona"));
            long number = ((JDOTypesafeQuery)jdotq).delete();
            assertEquals("Number of records deleted was wrong", 1, number);
            tq.closeAll();

            Query q = pm.newQuery("SELECT FROM " + Team.class.getName() + " WHERE id == 2");
            List<Team> results = (List<Team>) q.execute();
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
}