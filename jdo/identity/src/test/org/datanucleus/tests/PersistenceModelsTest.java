/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.datanucleus.samples.models.nightlabs_inheritance.AbstractSub1;
import org.datanucleus.samples.models.nightlabs_inheritance.Base;
import org.datanucleus.samples.models.nightlabs_inheritance.BaseID;
import org.datanucleus.samples.models.nightlabs_inheritance.ConcreteSub1;
import org.datanucleus.samples.models.nightlabs_inheritance.ConcreteSub2;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.models.leftright.LeftBase;
import org.jpox.samples.models.leftright.LeftSub;
import org.jpox.samples.models.leftright.RightBase;
import org.jpox.samples.models.leftright.RightSub;
import org.jpox.samples.models.voting.Category;
import org.jpox.samples.models.voting.Meeting;
import org.jpox.samples.models.voting.Topic;
import org.jpox.samples.models.voting.Vote;
import org.jpox.samples.one_many.collection.ListHolder;
import org.jpox.samples.valuegeneration.IdentityGeneratorItemNoField;

/**
 * Series of tests for persistence of "complicated" object models.
 * @version $Revision: 1.4 $
 */
public class PersistenceModelsTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public PersistenceModelsTest(String name)
    {
        super(name);
        
        if (!initialised)
        {
            addClassesToSchema(
                new Class[]
                {
                    LeftBase.class,
                    RightBase.class,
                    LeftSub.class,
                    RightSub.class,
                    Base.class,
                    AbstractSub1.class,
                    ConcreteSub1.class,
                    ConcreteSub2.class
                });
            initialised = true;
        }
    }

    /**
     * test if a class without persistent fields works with
     * query, delete, getObjectById, persistent
     */
    public void testClassWithoutFields()
    {
        try
        {
            IdentityGeneratorItemNoField objs[] = new IdentityGeneratorItemNoField[5];
            objs[0] = new IdentityGeneratorItemNoField(1);
            objs[1] = new IdentityGeneratorItemNoField(2);
            objs[2] = new IdentityGeneratorItemNoField(3);
            objs[3] = new IdentityGeneratorItemNoField(4);
            objs[4] = new IdentityGeneratorItemNoField(5);
            Object ids[] = new Object[objs.length];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();

            try
            {
                tx.begin();
                pm.makePersistentAll(objs);
                tx.commit();

                for( int i=0; i<objs.length; i++)
                {
                    ids[i] = pm.getObjectId(objs[i]);
                }

                tx.begin();

                for( int i=0; i<objs.length; i++)
                {
                    assertEquals(JDOHelper.getObjectId(objs[i]).toString(),JDOHelper.getObjectId(pm.getObjectById(ids[i],true)).toString());
                }

                tx.commit();

                tx.begin();

                Collection c = (Collection) pm.newQuery(IdentityGeneratorItemNoField.class).execute();

                assertEquals(c.size(),objs.length);

                tx.commit();

                IdentityGeneratorItemNoField objToDel = new IdentityGeneratorItemNoField(6);
                tx.begin();
                pm.makePersistent(objToDel);
                tx.commit();            
                Object idToDel = pm.getObjectId(objToDel);

                tx.begin();
                pm.deletePersistent(pm.getObjectById(idToDel,true));
                tx.commit();            

                tx.begin();
                boolean success = false;
                try
                {
                    pm.getObjectById(idToDel,true);
                }
                catch (JDOObjectNotFoundException e)
                {
                    success = true;
                }
                assertTrue("should have been raised exception",success);
                tx.commit();

            }
            catch( Exception e )
            {
                e.printStackTrace();
                fail(e.toString());
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
        finally
        {
            // Clean out our data
            clean(IdentityGeneratorItemNoField.class);
        }
    }

    /**
     * Test if a class with only a container (Collection or List) fields works with 
     * query, delete, getObjectById, persistent.
     */
    public void testClassWithOnlyContainerFields()
    {
        try
        {
            ListHolder objs[] = new ListHolder[5];
            for (int i = 0; i < objs.length; i++)
            {
                objs[i] = new ListHolder(i + 1);
            }
            Object ids[] = new Object[objs.length];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Persist the objects
                tx.begin();
                pm.makePersistentAll(objs);
                tx.commit();
                for (int i = 0; i < objs.length; i++)
                {
                    ids[i] = pm.getObjectId(objs[i]);
                }

                // Retrieve an object (getObjectById)
                tx.begin();
                for (int i = 0; i < objs.length; i++)
                {
                    assertEquals(JDOHelper.getObjectId(objs[i]).toString(), JDOHelper.getObjectId(pm.getObjectById(ids[i], true)).toString());
                }
                tx.commit();

                // Query the objects
                tx.begin();
                Collection c = (Collection) pm.newQuery(ListHolder.class).execute();
                assertEquals(c.size(), objs.length);
                tx.commit();

                // Delete an object
                ListHolder objToDel = new ListHolder(6);
                tx.begin();
                pm.makePersistent(objToDel);
                tx.commit();
                Object idToDel = pm.getObjectId(objToDel);

                tx.begin();
                pm.deletePersistent(pm.getObjectById(idToDel, true));
                tx.commit();

                tx.begin();
                boolean success = false;
                try
                {
                    pm.getObjectById(idToDel, true);
                }
                catch (JDOObjectNotFoundException e)
                {
                    success = true;
                }
                assertTrue("should have been raised exception", success);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
        finally
        {
            // Clean out our data
            clean(ListHolder.class);
        }
    }

    /**
     * Test if a class with only a container (Collection or List) fields works with 
     * query, delete, getObjectById, persistent.
     */
    public void testClassWithOnlyContainerFieldsInFetchPlan()
    {
        try
        {
            ListHolder objs[] = new ListHolder[5];
            for (int i = 0; i < objs.length; i++)
            {
                objs[i] = new ListHolder(i + 1);
            }
            Object ids[] = new Object[objs.length];

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            try
            {
                tx.begin();
                pm.makePersistentAll(objs);
                tx.commit();

                for (int i = 0; i < objs.length; i++)
                {
                    ids[i] = pm.getObjectId(objs[i]);
                }

                // Retrieve an object (getObjectById)
                tx.begin();
                for (int i = 0; i < objs.length; i++)
                {
                    assertEquals(JDOHelper.getObjectId(objs[i]).toString(), JDOHelper.getObjectId(pm.getObjectById(ids[i], true)).toString());
                }
                tx.commit();

                // Query the objects
                tx.begin();
                Collection c = (Collection) pm.newQuery(ListHolder.class).execute();
                assertEquals(c.size(), objs.length);
                tx.commit();

                // Delete an object
                ListHolder objToDel = new ListHolder(6);
                tx.begin();
                pm.makePersistent(objToDel);
                tx.commit();
                Object idToDel = pm.getObjectId(objToDel);

                tx.begin();
                pm.deletePersistent(pm.getObjectById(idToDel, true));
                tx.commit();

                tx.begin();
                boolean success = false;
                try
                {
                    pm.getObjectById(idToDel, true);
                }
                catch (JDOObjectNotFoundException e)
                {
                    success = true;
                }
                assertTrue("should have been raised exception", success);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
        finally
        {
            // Clean out our data
            clean(ListHolder.class);
        }
    }

    /**
     * Test the persistence of 2 1-N FK Lists.
     * <pre>
     * 1 Topic - N Votes, 1 Meeting - N Votes
     * votes[0] <-> meetings[0], votes[0] <-> topics[0]
     * votes[1] <-> meetings[0], votes[1] <-> topics[0]
     * votes[2] <-> meetings[1], votes[2] <-> topics[1]
     * votes[3] <-> meetings[1], votes[3] <-> topics[1]
     * votes[4] <-> meetings[2], votes[4] <-> topics[2]
     * votes[5] <-> meetings[2], votes[5] <-> topics[2]
     * votes[6] <-> meetings[3], votes[6] <-> topics[3]
     * votes[7] <-> meetings[3], votes[7] <-> topics[3]
     * votes[8] <-> meetings[4], votes[8] <-> topics[4]
     * votes[9] <-> meetings[5], votes[9] <-> topics[4]
     * </pre>
     */
    public void testPersistenceOfFKListFKListStructures()
    {
        try
        {
            Topic topics[] = new Topic[5];
            Object topicsIds[] = new Object[5];
            for (int i=0; i<topics.length; i++)
            {
                topics[i] = new Topic(i,"Topic "+i);
            }

            Vote votes[] = new Vote[10];
            Object votesIds[] = new Object[10];
            for (int i=0; i<votes.length; i++)
            {
                votes[i] = new Vote(i,"Vote "+i);
            }

            Meeting meetings[] = new Meeting[5];
            Object meetingsIds[] = new Object[5];
            for (int i=0; i<meetings.length; i++)
            {
                meetings[i] = new Meeting(i,"Meeting "+i);
            }

            for (int i=0, j=0; i<votes.length; i++)
            {
                // Relate votes[i] to meetings[j]
                votes[i].setMeeting(meetings[j]);
                meetings[j].getVotes().add(votes[i]);

                // Relate votes[i] to topics[j]
                votes[i].setTopic(topics[j]);
                topics[j].getVoteHistory().add(votes[i]);

                if (((i-1) % 2) == 0)
                {
                    j++;
                }
            }

            // Persist and get the identities
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistentAll(topics);
                tx.commit();
                for (int i=0; i<topics.length; i++)
                {
                    topicsIds[i] = pm.getObjectId(topics[i]);
                }
                for (int i=0; i<votes.length; i++)
                {
                    votesIds[i] = pm.getObjectId(votes[i]);
                }
                for (int i=0; i<meetings.length; i++)
                {
                    meetingsIds[i] = pm.getObjectId(meetings[i]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                for( int i=0, j=0, p=0; i<topics.length; i++)
                {
                    topics[i] = (Topic) pm.getObjectById(topicsIds[i], true);
                    assertEquals(2, topics[i].getVoteHistory().size());
                    assertEquals(votesIds[j++], 
                        JDOHelper.getObjectId(topics[i].getVoteHistory().get(0)));
                    assertEquals(meetingsIds[i],
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(0)).getMeeting()));

                    assertEquals(votesIds[j++],
                        JDOHelper.getObjectId(topics[i].getVoteHistory().get(1)));
                    assertEquals(meetingsIds[i], 
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(1)).getMeeting()));

                    assertEquals(votesIds[p],
                        JDOHelper.getObjectId( ((Vote)topics[i].getVoteHistory().get(0)).getMeeting().getVotes().get(0)));
                    assertEquals(votesIds[p++],
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(1)).getMeeting().getVotes().get(0)));

                    assertEquals(votesIds[p],
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(0)).getMeeting().getVotes().get(1)));
                    assertEquals(votesIds[p++],
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(1)).getMeeting().getVotes().get(1)));
                }
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
        finally
        {
            // Clean out our data
            clean(Topic.class);
            clean(Vote.class);
            clean(Meeting.class);
            clean(Category.class);
        }
    }

    /**
     * Test the persistence of a 1-N FK Set, and 1-N FK List.
     * <pre>
     * 1 Topic - N Votes FK List
     * 1 Category - N Votes FK Set
     * </pre> 
     */
    public void testPersistenceOfFKSetFKListStructures()
    {
        try
        {
            Topic topics[] = new Topic[5];
            Object topicsIds[] = new Object[5];
            for (int i = 0; i < topics.length; i++)
            {
                topics[i] = new Topic(i, "Topic " + i);
            }

            Category categories[] = new Category[5];
            Object categoriesIds[] = new Object[5];
            for (int i = 0; i < categories.length; i++)
            {
                categories[i] = new Category(i, "Category " + i);
            }

            Vote votes[] = new Vote[10];
            Object votesIds[] = new Object[10];
            for (int i = 0; i < votes.length; i++)
            {
                votes[i] = new Vote(i, "Vote " + i);
            }

            Meeting meetings[] = new Meeting[5];
            Object meetingsIds[] = new Object[5];
            for (int i = 0; i < meetings.length; i++)
            {
                meetings[i] = new Meeting(i, "Meeting " + i);
            }

            for (int i = 0, j = 0; i < votes.length; i++)
            {
                votes[i].setCategory(categories[j]);
                categories[j].getVotes().add(votes[i]);

                votes[i].setTopic(topics[j]);
                topics[j].getVoteHistory().add(votes[i]);

                meetings[j].getVotes().add(votes[i]);

                if (((i - 1) % 2) == 0)
                {
                    j++;
                }
            }

            // Persist the data and get the object identities
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistentAll(topics);
                tx.commit();
                for (int i = 0; i < topics.length; i++)
                {
                    topicsIds[i] = pm.getObjectId(topics[i]);
                }
                for (int i = 0; i < votes.length; i++)
                {
                    votesIds[i] = pm.getObjectId(votes[i]);
                }
                for (int i = 0; i < meetings.length; i++)
                {
                    meetingsIds[i] = pm.getObjectId(meetings[i]);
                }
                for (int i = 0; i < categories.length; i++)
                {
                    categoriesIds[i] = pm.getObjectId(categories[i]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                for (int i=0, j=0; i<topics.length; i++)
                {
                    topics[i] = (Topic) pm.getObjectById(topicsIds[i],true);
                    assertEquals(2, topics[i].getVoteHistory().size());

                    assertEquals(votesIds[j++],
                        JDOHelper.getObjectId(topics[i].getVoteHistory().get(0)));
                    assertEquals(meetingsIds[i], 
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(0)).getMeeting()));

                    assertEquals(votesIds[j++], 
                        JDOHelper.getObjectId(topics[i].getVoteHistory().get(1)));
                    assertEquals(meetingsIds[i], 
                        JDOHelper.getObjectId(((Vote)topics[i].getVoteHistory().get(1)).getMeeting()));

                    assertEquals(2, ((Vote)topics[i].getVoteHistory().get(0)).getCategory().getVotes().size());
                    assertEquals(2, ((Vote)topics[i].getVoteHistory().get(1)).getCategory().getVotes().size());
                }
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail(e.toString());
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
        finally
        {
            // Clean out our data
            clean(Topic.class);
            clean(Vote.class);
            clean(Meeting.class);
            clean(Category.class);
        }
    }

    /**
     * Test case for 1-N inheritance relationships.
     **/
    public void test1toNInheritance()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                LeftBase base1 = new LeftBase(1);
                LeftBase base2 = new LeftBase(2);
                LeftBase base3 = new LeftBase(3);
                LeftSub group1 = new LeftSub(4, new LeftBase[] { base1, base2 });
                LeftSub group2 = new LeftSub(5, new LeftBase[] { group1, base3 });
                RightSub gr1 = new RightSub(1, group2);
                pm.makePersistent(gr1);
                tx.commit();
                id = pm.getObjectId(gr1);
            }
            catch( Exception e )
            {
                e.printStackTrace();
                fail(e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            //test
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                RightSub gr2 = (RightSub) pm.getObjectById(id,true);

                assertEquals("Expect id == 5",5,gr2.getBase().getId());
                assertEquals("Expect Group class instance.",LeftSub.class,gr2.getBase().getClass());

                assertEquals("Expect 2 members",2,((LeftSub)gr2.getBase()).getMembers().size());

                List members = ((LeftSub)gr2.getBase()).getMembers();
                assertEquals("Expect id == 4",4,((LeftBase)members.get(0)).getId());
                assertEquals("Expect id == 3",3,((LeftBase)members.get(1)).getId());

                assertEquals("Expect Group class instance in member.",LeftSub.class,members.get(0).getClass());
                assertEquals("Expect Base class instance in member.",LeftBase.class,members.get(1).getClass());

                tx.commit();
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
        finally
        {
            // Clean out all data
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Extent ex = pm.getExtent(LeftSub.class);
                Iterator iter = ex.iterator();
                while (iter.hasNext())
                {
                    LeftSub lsub = (LeftSub)iter.next();
                    lsub.getMembers().clear();
                }
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
            }
            clean(RightSub.class);
            clean(RightBase.class);
            clean(LeftSub.class);
            clean(LeftBase.class);
        }
    }

    /**
     * Test for persistence and retrieval by id of composite PK classes.
     */
    //TODO Run for application identity only to avoid running it twice
    public void testInheritanceGetObjectById()
    {
        BaseID legalSubID = BaseID.create("datanucleus.com",
            "org.datanucleus.samples.models.nightlabs_inheritance.ConcreteSub1", "Person");
        BaseID propertySubID = BaseID.create("datanucleus.com",
            "org.datanucleus.samples.models.nightlabs_inheritance.ConcreteSub2", "Person");

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                ConcreteSub1 legalSub = new ConcreteSub1(legalSubID.organisationID, legalSubID.configModuleClassName,
                    legalSubID.configModuleInitialiserID);
                legalSub = pm.makePersistent(legalSub);

                ConcreteSub2 propertySub = new ConcreteSub2(propertySubID.organisationID,
                    propertySubID.configModuleClassName, propertySubID.configModuleInitialiserID);
                propertySub = pm.makePersistent(propertySub);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown persisting objects", e);
                fail("Exception on persist");
            }
            finally
            {
                pm.close();
            }

            // Try to retrieve the LegalSub by its id
            pmf.getDataStoreCache().evictAll();
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LOG.info(">> Retrieving subclass by id");
                ConcreteSub1 sub = (ConcreteSub1)pm.getObjectById(legalSubID);
                assertNotNull("Object is null", sub);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown on retrieval of object by id", e);
                fail("Exception thrown on retrieval : " + e.getMessage());
            }

            // Try to retrieve the PropertySub by its id
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                LOG.info(">> Retrieving subclass by id");
                ConcreteSub2 sub = (ConcreteSub2)pm.getObjectById(propertySubID);
                assertNotNull("Object is null", sub);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown on retrieval of object by id", e);
                fail("Exception thrown on retrieval : " + e.getMessage());
            }
        }
        finally
        {
            clean(ConcreteSub1.class);
            clean(ConcreteSub2.class);
        }
    }
}