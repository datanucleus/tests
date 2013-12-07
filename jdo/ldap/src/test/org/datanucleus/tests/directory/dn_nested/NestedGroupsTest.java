/**********************************************************************
Copyright (c) 2009 Stefan Seelmann and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


Contributors :
 ...
 ***********************************************************************/
package org.datanucleus.tests.directory.dn_nested;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;

public class NestedGroupsTest extends JDOPersistenceTestCase
{
    public NestedGroupsTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        clean(Group.class);
        clean(User.class);
    }

    protected void tearDown() throws Exception
    {
        clean(Group.class);
        clean(User.class);
        super.tearDown();
    }

    public void testPersistNoReferences() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            pm.makePersistent(g3);

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

    public void testPersistReferences() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            g1.getMembers().add(u1);
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            g2.getMembers().add(u2);
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            g3.getMembers().add(u3);
            pm.makePersistent(g3);

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

    /**
     * G1(U1), G2(U2,G1), G3(U3,G2)
     * @throws Exception
     */
    public void testPersistNestedReferences() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            g1.getMembers().add(u1);
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            g2.getMembers().add(u2);
            g2.getMembers().add(g1);
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            pm.makePersistent(g3);

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

    /**
     * G1(U1), G2(U2,G1), G3(U3,G2)
     * @throws Exception
     */
    public void testRetrieveNestedReferences() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            g1.getMembers().add(u1);
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            g2.getMembers().add(u2);
            g2.getMembers().add(g1);
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            pm.makePersistent(g3);

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(Group.class);
            q.setFilter("id == 'G2'");
            Collection<Group> c = (Collection<Group>) q.execute();
            assertNotNull(c);
            assertEquals(1, c.size());
            Group g2 = c.iterator().next();
            assertEquals("G2", g2.getId());

            assertNotNull(g2.getMemberOf());
            assertEquals(1, g2.getMemberOf().size());
            Group g3 = g2.getMemberOf().iterator().next();
            assertEquals("G3", g3.getId());
            assertTrue(g3.getMembers().contains(g2));

            assertNotNull(g2.getMembers());
            assertEquals(2, g2.getMembers().size());
            for (GroupMember member : g2.getMembers())
            {
                if (member instanceof User)
                {
                    assertEquals("U2", member.getId());
                    assertTrue(member.getMemberOf().contains(g2));
                }
                else if (member instanceof Group)
                {
                    assertEquals("G1", ((Group) member).getId());
                    assertTrue(member.getMemberOf().contains(g2));
                    assertEquals(1, ((Group) member).getMembers().size());
                    assertEquals("U1", ((Group) member).getMembers().iterator().next().getId());
                }
                else
                {
                    fail("Unexpected member type");
                }
            }

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

    /**
     * G1(U1), G2(U2,G1), G3(U3,G2)
     * @throws Exception
     */
    public void testRetrieveNestedReferencesDetached() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            g1.getMembers().add(u1);
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            g2.getMembers().add(u2);
            g2.getMembers().add(g1);
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            pm.makePersistent(g3);

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        Group g2 = null;
        try
        {
            tx.begin();

            pm.getFetchPlan().setMaxFetchDepth(2);
            Query q = pm.newQuery(Group.class);
            q.setFilter("id == 'G2'");
            Collection<Group> c = (Collection<Group>) q.execute();
            assertNotNull(c);
            assertEquals(1, c.size());
            g2 = c.iterator().next();
            g2 = pm.detachCopy(g2);
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

        assertEquals("G2", g2.getId());
        assertNotNull(g2.getMemberOf());
        assertEquals(1, g2.getMemberOf().size());
        Group g3 = g2.getMemberOf().iterator().next();
        assertEquals("G3", g3.getId());
        assertTrue(g3.getMembers().contains(g2));

        assertNotNull(g2.getMembers());
        assertEquals(2, g2.getMembers().size());
        for (GroupMember member : g2.getMembers())
        {
            if (member instanceof User)
            {
                assertEquals("U2", member.getId());
                assertTrue(member.getMemberOf().contains(g2));
            }
            else if (member instanceof Group)
            {
                assertEquals("G1", ((Group) member).getId());
                assertTrue(member.getMemberOf().contains(g2));
                assertEquals(1, ((Group) member).getMembers().size());
                assertEquals("U1", ((Group) member).getMembers().iterator().next().getId());
            }
            else
            {
                fail("Unexpected member type");
            }
        }
    }

    public void testCRUD() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);
            Group g1 = new Group("G1");
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            pm.makePersistent(g3);
            tx.commit();
            pm.close();

            // assert no relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.getObjectById(User.class, "U1");
            u2 = pm.getObjectById(User.class, "U2");
            u3 = pm.getObjectById(User.class, "U3");
            g1 = pm.getObjectById(Group.class, "G1");
            g2 = pm.getObjectById(Group.class, "G2");
            g3 = pm.getObjectById(Group.class, "G3");
            assertTrue(u1.getMemberOf().isEmpty());
            assertTrue(u2.getMemberOf().isEmpty());
            assertTrue(u3.getMemberOf().isEmpty());
            assertTrue(g1.getMemberOf().isEmpty());
            assertTrue(g1.getMembers().isEmpty());
            assertTrue(g2.getMemberOf().isEmpty());
            assertTrue(g2.getMembers().isEmpty());
            assertTrue(g3.getMemberOf().isEmpty());
            assertTrue(g3.getMembers().isEmpty());

            // add some relationships
            g1.getMembers().add(u1);
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            g2.getMembers().add(g1);
            tx.commit();
            pm.close();

            // assert new relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.getObjectById(User.class, "U1");
            u2 = pm.getObjectById(User.class, "U2");
            u3 = pm.getObjectById(User.class, "U3");
            g1 = pm.getObjectById(Group.class, "G1");
            g2 = pm.getObjectById(Group.class, "G2");
            g3 = pm.getObjectById(Group.class, "G3");
            assertEquals(1, u1.getMemberOf().size());
            assertEquals(0, u2.getMemberOf().size());
            assertEquals(1, u3.getMemberOf().size());
            assertEquals(1, g1.getMemberOf().size());
            assertEquals(1, g1.getMembers().size());
            assertEquals(1, g2.getMemberOf().size());
            assertTrue(g2.getMemberOf().contains(g3));
            assertEquals(1, g2.getMembers().size());
            assertTrue(g2.getMembers().contains(g1));
            assertEquals(0, g3.getMemberOf().size());
            assertEquals(2, g3.getMembers().size());
            assertTrue(g3.getMembers().contains(u3));
            assertTrue(g3.getMembers().contains(g2));

            // change relationships
            g3.getMembers().clear();
            u2.getMemberOf().add(g2);
            tx.commit();

            // assert new relationships
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.getObjectById(User.class, "U1");
            u2 = pm.getObjectById(User.class, "U2");
            u3 = pm.getObjectById(User.class, "U3");
            g1 = pm.getObjectById(Group.class, "G1");
            g2 = pm.getObjectById(Group.class, "G2");
            g3 = pm.getObjectById(Group.class, "G3");
            assertEquals(1, u1.getMemberOf().size());
            assertEquals(1, u2.getMemberOf().size());
            assertEquals(0, u3.getMemberOf().size());
            assertEquals(1, g1.getMemberOf().size());
            assertEquals(1, g1.getMembers().size());
            assertEquals(0, g2.getMemberOf().size());
            assertEquals(2, g2.getMembers().size());
            assertTrue(g2.getMembers().contains(u2));
            assertTrue(g2.getMembers().contains(g1));
            assertEquals(0, g3.getMemberOf().size());
            assertEquals(0, g3.getMembers().size());
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

    public void testCRUDDetached() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);
            Group g1 = new Group("G1");
            pm.makePersistent(g1);
            Group g2 = new Group("G2");
            pm.makePersistent(g2);
            Group g3 = new Group("G3");
            pm.makePersistent(g3);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.detachCopy(pm.getObjectById(User.class, "U1"));
            u2 = pm.detachCopy(pm.getObjectById(User.class, "U2"));
            u3 = pm.detachCopy(pm.getObjectById(User.class, "U3"));
            g1 = pm.detachCopy(pm.getObjectById(Group.class, "G1"));
            g2 = pm.detachCopy(pm.getObjectById(Group.class, "G2"));
            g3 = pm.detachCopy(pm.getObjectById(Group.class, "G3"));
            tx.commit();
            pm.close();

            // assert no relationships
            assertTrue(u1.getMemberOf().isEmpty());
            assertTrue(u2.getMemberOf().isEmpty());
            assertTrue(u3.getMemberOf().isEmpty());
            assertTrue(g1.getMemberOf().isEmpty());
            assertTrue(g1.getMembers().isEmpty());
            assertTrue(g2.getMemberOf().isEmpty());
            assertTrue(g2.getMembers().isEmpty());
            assertTrue(g3.getMemberOf().isEmpty());
            assertTrue(g3.getMembers().isEmpty());

            // add some relationships
            g1.getMembers().add(u1);
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            g2.getMembers().add(g1);

            // attach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(g1);
            pm.makePersistent(g2);
            pm.makePersistent(g3);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.detachCopy(pm.getObjectById(User.class, "U1"));
            u2 = pm.detachCopy(pm.getObjectById(User.class, "U2"));
            u3 = pm.detachCopy(pm.getObjectById(User.class, "U3"));
            g1 = pm.detachCopy(pm.getObjectById(Group.class, "G1"));
            g2 = pm.detachCopy(pm.getObjectById(Group.class, "G2"));
            g3 = pm.detachCopy(pm.getObjectById(Group.class, "G3"));
            tx.commit();
            pm.close();

            // assert new relationships
            assertEquals(1, u1.getMemberOf().size());
            assertEquals(0, u2.getMemberOf().size());
            assertEquals(1, u3.getMemberOf().size());
            assertEquals(1, g1.getMemberOf().size());
            assertEquals(1, g1.getMembers().size());
            assertEquals(1, g2.getMemberOf().size());
            assertTrue(g2.getMemberOf().contains(g3));
            assertEquals(1, g2.getMembers().size());
            assertTrue(g2.getMembers().contains(g1));
            assertEquals(0, g3.getMemberOf().size());
            assertEquals(2, g3.getMembers().size());
            assertTrue(g3.getMembers().contains(u3));
            assertTrue(g3.getMembers().contains(g2));

            // change relationships
            g3.getMembers().clear();
            u2.getMemberOf().add(g2);

            // attach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            pm.makePersistent(u2);
            pm.makePersistent(g3);
            tx.commit();
            pm.close();

            // fetch and detach
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.detachCopy(pm.getObjectById(User.class, "U1"));
            u2 = pm.detachCopy(pm.getObjectById(User.class, "U2"));
            u3 = pm.detachCopy(pm.getObjectById(User.class, "U3"));
            g1 = pm.detachCopy(pm.getObjectById(Group.class, "G1"));
            g2 = pm.detachCopy(pm.getObjectById(Group.class, "G2"));
            g3 = pm.detachCopy(pm.getObjectById(Group.class, "G3"));
            tx.commit();
            pm.close();

            // assert new relationships
            assertEquals(1, u1.getMemberOf().size());
            assertEquals(1, u2.getMemberOf().size());
            assertEquals(0, u3.getMemberOf().size());
            assertEquals(1, g1.getMemberOf().size());
            assertEquals(1, g1.getMembers().size());
            assertEquals(0, g2.getMemberOf().size());
            assertEquals(2, g2.getMembers().size());
            assertTrue(g2.getMembers().contains(u2));
            assertTrue(g2.getMembers().contains(g1));
            assertEquals(0, g3.getMemberOf().size());
            assertEquals(0, g3.getMembers().size());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }

    }

    /**
     * Nested group with a loop + self-reference. G1(U1,G3,G1), G2(U2,G1), G3(U3,G2).
     * @throws Exception
     */
    public void testNestedReferencesWithLoop() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            User u1 = new User("U1", "U1", "U1");
            pm.makePersistent(u1);
            User u2 = new User("U2", "U2", "U2");
            pm.makePersistent(u2);
            User u3 = new User("U3", "U3", "U3");
            pm.makePersistent(u3);

            Group g1 = new Group("G1");
            Group g2 = new Group("G2");
            Group g3 = new Group("G3");
            g1.getMembers().add(u1);
            g1.getMembers().add(g3);
            g1.getMembers().add(g1); // <-- self reference
            g2.getMembers().add(u2);
            g2.getMembers().add(g1);
            g3.getMembers().add(u3);
            g3.getMembers().add(g2);
            pm.makePersistent(g1);
            pm.makePersistent(g2);
            pm.makePersistent(g3);

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

        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Query q = pm.newQuery(Group.class);
            q.setFilter("id == 'G2'");
            Collection<Group> c = (Collection<Group>) q.execute();
            assertNotNull(c);
            assertEquals(1, c.size());
            Group g2 = c.iterator().next();
            assertEquals("G2", g2.getId());

            assertNotNull(g2.getMemberOf());
            assertEquals(1, g2.getMemberOf().size());
            Group g3 = g2.getMemberOf().iterator().next();
            assertEquals("G3", g3.getId());
            assertTrue(g3.getMembers().contains(g2));

            assertNotNull(g2.getMembers());
            assertEquals(2, g2.getMembers().size());
            for (GroupMember member : g2.getMembers())
            {
                if (member instanceof User)
                {
                    assertEquals("U2", member.getId());
                    assertTrue(member.getMemberOf().contains(g2));
                }
                else if (member instanceof Group)
                {
                    Group g1 = (Group) member;
                    assertEquals("G1", g1.getId());
                    assertEquals(2, g1.getMemberOf().size());
                    assertTrue(g1.getMemberOf().contains(g2));
                    assertTrue(g1.getMemberOf().contains(g1));

                    assertNotNull(g1.getMembers());
                    assertEquals(3, g1.getMembers().size());
                    assertTrue(g1.getMembers().contains(g3));
                    assertTrue(g1.getMembers().contains(g1));
                }
                else
                {
                    fail("Unexpected member type");
                }
            }

            // get all groups U1 is member of
            User u1 = pm.getObjectById(User.class, "U1");
            Set<Group> groupsToProcess = new HashSet<Group>(u1.getMemberOf());
            Set<Group> groups = new HashSet<Group>();
            while (!groupsToProcess.isEmpty())
            {
                Group group = groupsToProcess.iterator().next();
                groupsToProcess.remove(group);
                groups.add(group);
                for (Group g : group.getMemberOf())
                {
                    if (!groupsToProcess.contains(g) && !groups.contains(g))
                    {
                        groupsToProcess.add(g);
                    }
                }
            }
            assertEquals(3, groups.size());

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

    // G1
    // G11
    //   
    public void testMemberOfDetached() throws Exception
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            User u1 = new User("U1", "U1", "U1");
            Group g1 = new Group("G1");
            Group g2 = new Group("G2");
            Group g3 = new Group("G3");
            Group g4 = new Group("G4");
            Group g5 = new Group("G5");
            u1.getMemberOf().add(g1);
            g1.getMemberOf().add(g2);
            g1.getMemberOf().add(g3);
            g2.getMemberOf().add(g4);
            g3.getMemberOf().add(g4);
            pm.makePersistent(u1); // persists G1,G2,G3,G4
            pm.makePersistent(g5);
            tx.commit();
            pm.close();
    
            // fetch and detach
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().setMaxFetchDepth(-1);
            tx = pm.currentTransaction();
            tx.begin();
            u1 = pm.detachCopy(pm.getObjectById(User.class, "U1"));
            tx.commit();
            pm.close();

            // get all groups U1 is member of
            Set<Group> groupsToProcess = new HashSet<Group>(u1.getMemberOf());
            Set<Group> groups = new HashSet<Group>();
            while (!groupsToProcess.isEmpty())
            {
                Group group = groupsToProcess.iterator().next();
                groupsToProcess.remove(group);
                groups.add(group);
                for (Group g : group.getMemberOf())
                {
                    if (!groupsToProcess.contains(g) && !groups.contains(g))
                    {
                        groupsToProcess.add(g);
                    }
                }
            }
            assertEquals(4, groups.size());
           
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            // pm.close();
        }
    
    }
}