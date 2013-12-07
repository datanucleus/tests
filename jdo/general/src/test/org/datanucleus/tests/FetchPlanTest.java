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
2007 Andy Jefferson - tests for dynamic fetch groups, and named fetch plans
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.ExecutionContext;
import org.datanucleus.FetchPlan;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.api.jdo.JDOFetchPlan;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.samples.detach.DetachChildA;
import org.datanucleus.samples.detach.DetachChildB;
import org.datanucleus.samples.detach.DetachParent;
import org.datanucleus.samples.fetchplan.FP1Sub;
import org.datanucleus.samples.fetchplan.FP2Base;
import org.datanucleus.samples.fetchplan.FP2Sub;
import org.datanucleus.samples.fetchplan.FP3Base;
import org.datanucleus.tests.JDOPersistenceTestCase;

/**
 * Series of tests for the use of FetchPlans.
 */
public class FetchPlanTest extends JDOPersistenceTestCase
{
    public FetchPlanTest(String name)
    {
        super(name);
    }

    /**
     * Test the addition of fetch groups.
     */
    public void testAddGroup()
    {
        FetchPlan fp = getFetchPlan();

        fp.addGroup("grp1");
        assertEquals(2, fp.getGroups().size());

        fp.addGroup("grp1");
        assertEquals(2, fp.getGroups().size());

        fp.addGroup("grp2");
        assertEquals(3, fp.getGroups().size());
    }

    /**
     * Test the removal of fetch groups.
     */
    public void testRemoveGroup()
    {
        FetchPlan fp = getFetchPlan();

        fp.removeGroup("grp1");
        assertEquals(1, fp.getGroups().size());
        assertEquals(FetchPlan.DEFAULT, fp.getGroups().iterator().next());

        fp.addGroup("grp1");
        fp.removeGroup("grp1");
        assertEquals(1, fp.getGroups().size());
        assertEquals(FetchPlan.DEFAULT, fp.getGroups().iterator().next());

        fp.addGroup("grp1");
        fp.removeGroup(FetchPlan.DEFAULT);
        assertEquals(1, fp.getGroups().size());
        assertEquals("grp1", fp.getGroups().iterator().next());

        fp.removeGroup(FetchPlan.DEFAULT);
        assertEquals(1, fp.getGroups().size());
        assertEquals("grp1", fp.getGroups().iterator().next());

        fp.clearGroups();
        assertEquals(0, fp.getGroups().size());
    }

    /**
     * Test the clearing of fetch groups.
     */
    public void testClearGroups()
    {
        FetchPlan fp = getFetchPlan();
        fp.addGroup("grp1");
        fp.clearGroups();
        assertEquals(0, fp.getGroups().size());
    }

    /**
     * Test the setting of fetch groups.
     */
    public void testSetGroups()
    {
        FetchPlan fp = getFetchPlan();

        // setGroups(Collection)
        HashSet set = new HashSet();
        set.add("grp1");

        fp.setGroups(set);
        assertEquals(1, fp.getGroups().size());
        assertEquals("grp1", fp.getGroups().iterator().next());

        set.clear();
        set.add(FetchPlan.DEFAULT);
        fp.setGroups(set);
        assertEquals(1, fp.getGroups().size());
        assertEquals(FetchPlan.DEFAULT, fp.getGroups().iterator().next());

        fp.clearGroups();
        set.add("grp1");
        fp.setGroups(set);
        assertEquals(2, fp.getGroups().size());

        fp.setGroups(fp.getGroups());
        assertEquals(2, fp.getGroups().size());
        
        // setGroup(String)
        fp.setGroup((String)null);
        assertEquals(0, fp.getGroups().size());

        // setGroups(String[])
        String[] grps = new String[] {"grp1"};
        fp.setGroups(grps);
        assertEquals(1, fp.getGroups().size());
        assertEquals("grp1", fp.getGroups().iterator().next());
    }
    
    public void testGetGroups()
    {
        FetchPlan fp = getFetchPlan();

        HashSet set = new HashSet();

        fp.clearGroups();
        set.add("grp1");
        fp.setGroups(set);
        assertEquals(1, fp.getGroups().size());
        
        try
        {
            fp.getGroups().add("XXX");
            fail("expected UnsupportedOperationException");
        }
        catch(UnsupportedOperationException e)
        {
            //expected
        }
    }

    /**
     * Test the detachment roots
     */
    public void testDetachmentRoots()
    {
        FetchPlan fp = getFetchPlan();

        assertEquals(0, fp.getDetachmentRoots().size());
        //verify immutable
        try
        {
            fp.getDetachmentRoots().add(Class.class);
            fail("exception UnsupportedOperationException");
        }
        catch( UnsupportedOperationException ex)
        {
            //expected
        }
        List elms = new ArrayList();
        elms.add(Class.class);
        fp.setDetachmentRoots(elms);
        assertEquals(1, fp.getDetachmentRoots().size());
        //verify if immutable by changing original collection
        elms.add(FetchPlan.class);
        assertEquals(1, fp.getDetachmentRoots().size());
        //verify immutable after setting roots
        try
        {
            fp.getDetachmentRoots().add(Class.class);
            fail("exception UnsupportedOperationException");
        }
        catch( UnsupportedOperationException ex)
        {
            //expected
        }
    }

    /**
     * Test the detachment root classes
     */
    public void testDetachmentRootsClasses()
    {
        FetchPlan fp = getFetchPlan();

        assertEquals(0, fp.getDetachmentRootClasses().length);
        assertEquals(Class.class, fp.getDetachmentRootClasses().getClass().getComponentType());
        List elms = new ArrayList();
        elms.add(Class.class);
        fp.setDetachmentRootClasses((Class[])elms.toArray(new Class[elms.size()]));
        assertEquals(1, fp.getDetachmentRootClasses().length);
    }
    
    /**
     * Test the use of the fetch plan.
     */
    public void testFetchPlan()
    {
        FetchPlan fp = getFetchPlan();
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager metaMgr = new JDOMetaDataManager(nucleusCtx);
        AbstractClassMetaData cmd = metaMgr.getMetaDataForClass(FP2Base.class, new ClassLoaderResolverImpl());

        // --------------------------------------
        // all fields in DFG
        // --------------------------------------
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        int[] fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 11 fields in fetchplan", 11, fieldsInFP.length);

        // check if fields are in the FP
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece6 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertTrue("piece11 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // one FG + DFG
        // --------------------------------------
        fp.addGroup("groupA");
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 11 fields in fetchplan", 11, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece6 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertTrue("piece11 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // one FG
        // --------------------------------------
        fp.addGroup("groupA");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 3 fields in fetchplan", 3, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertFalse("piece4 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertFalse("piece5 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertFalse("piece6 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertFalse("piece7 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertFalse("piece8 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertFalse("piece9 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // two FG
        // --------------------------------------
        fp.addGroup("groupA");
        fp.addGroup("groupC");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 6 fields in fetchplan", 6, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertFalse("piece4 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertFalse("piece5 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertFalse("piece6 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // three FG
        // --------------------------------------
        fp.addGroup("groupA");
        fp.addGroup("groupB");
        fp.addGroup("groupC");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 9 fields in fetchplan", 9, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece6 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // four FG
        // --------------------------------------
        fp.addGroup("groupA");
        fp.addGroup("groupB");
        fp.addGroup("groupC");
        fp.addGroup("groupD");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 12 fields in fetchplan", 12, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece6 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertTrue("piece11 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertTrue("piece12 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // three FG
        // --------------------------------------
        fp.removeGroup("groupD");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 9 fields in fetchplan", 9, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece6 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // two FG
        // --------------------------------------
        fp.removeGroup("groupB");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 6 fields in fetchplan", 6, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertFalse("piece4 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertFalse("piece5 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertFalse("piece6 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece9 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // one FG
        // --------------------------------------
        fp.removeGroup("groupC");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 3 fields in fetchplan", 3, fieldsInFP.length);

        // check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));
        assertFalse("piece4 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertFalse("piece5 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertFalse("piece6 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));
        assertFalse("piece7 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertFalse("piece8 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertFalse("piece9 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));
        assertFalse("piece10 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
        assertFalse("piece11 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));
        assertFalse("piece12 should not be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));

        // --------------------------------------
        // removed all groups, so use DFG = all fields in DFG
        // --------------------------------------
        fp.removeGroup("groupA");
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 1 fields in fetchplan", 1, fieldsInFP.length);

        // check if fields are in the FP
        fp.addGroup(FetchPlan.DEFAULT);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 11 fields in fetchplan", 11, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece2 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));
        assertTrue("piece3 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertTrue("piece4 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertTrue("piece5 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertTrue("piece6 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertTrue("piece7 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertTrue("piece8 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertTrue("piece9 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertTrue("piece10 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertTrue("piece11 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));            
        assertFalse("piece12 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12"))); 

        //--------------------------------------
        //fetch group with nested fetch groups
        //--------------------------------------
        fp.addGroup("groupAll");
        fp.removeGroup("groupA");
        fp.removeGroup("groupB");
        fp.removeGroup("groupC");
        fp.removeGroup("groupD");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 12 fields in fetchplan",12,fieldsInFP.length);
        
        //check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));            
        assertTrue("piece2 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));            
        assertTrue("piece3 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertTrue("piece4 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertTrue("piece5 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertTrue("piece6 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertTrue("piece7 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertTrue("piece8 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertTrue("piece9 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertTrue("piece10 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertTrue("piece11 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));               
        assertTrue("piece12 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));             
        
        //--------------------------------------
        //test an intersection, first add all groups, and then remove all, except the one with fields in intersection
        //--------------------------------------
        fp.addGroup("groupA");
        fp.addGroup("groupB");
        fp.addGroup("groupC");
        fp.addGroup("groupD");
        fp.addGroup("groupAll");
        fp.addGroup("groupIntersection");
        fp.removeGroup("groupA");
        fp.removeGroup("groupB");
        fp.removeGroup("groupC");
        fp.removeGroup("groupD");
        fp.removeGroup("groupAll");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 4 fields in fetchplan",4,fieldsInFP.length);
        
        //check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));            
        assertFalse("piece2 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));            
        assertFalse("piece3 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertTrue("piece4 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertFalse("piece5 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertFalse("piece6 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertTrue("piece7 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertFalse("piece8 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertFalse("piece9 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertTrue("piece10 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertFalse("piece11 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));            
        assertFalse("piece12 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));             

        //--------------------------------------
        //use FetchPlan.ALL
        //--------------------------------------
        fp.clearGroups();
        fp.addGroup(FetchPlan.ALL);
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 13 fields in fetchplan",13,fieldsInFP.length);
        
        //check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));            
        assertTrue("piece2 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));            
        assertTrue("piece3 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertTrue("piece4 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertTrue("piece5 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertTrue("piece6 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertTrue("piece7 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertTrue("piece8 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertTrue("piece9 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertTrue("piece10 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertTrue("piece11 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));            
        assertTrue("piece12 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));         
        assertTrue("piece14 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece14")));         

        //--------------------------------------
        //use FetchPlan.NONE
        //--------------------------------------
        fp.clearGroups();
        fp.addGroup(FetchPlan.NONE);
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 1 fields in fetchplan",1,fieldsInFP.length);
        
        //check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));            
        assertFalse("piece2 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));            
        assertFalse("piece3 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertFalse("piece4 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertFalse("piece5 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertFalse("piece6 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertFalse("piece7 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertFalse("piece8 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertFalse("piece9 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertFalse("piece10 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertFalse("piece11 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));            
        assertFalse("piece12 should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));     
    }

    /**
     * Test the use of "postload"
     */
    public void testPostLoad()
    throws Exception
    {
        FetchPlan fp = getFetchPlan();
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager metaMgr = new JDOMetaDataManager(nucleusCtx);
        AbstractClassMetaData cmd = metaMgr.getMetaDataForClass(FP2Base.class, new ClassLoaderResolverImpl());
        
        //--------------------------------------
        //all fields in DFG
        //--------------------------------------
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        boolean loadedFields[] = new boolean[14];
        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        //--------------------------------------
        //fetch group width postLoad + DFG
        //--------------------------------------
        fp.addGroup("groupD");
        fpc = fp.getFetchPlanForClass(cmd);

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));
        
        //--------------------------------------
        //fetch group width postLoad
        //--------------------------------------
        fp.addGroup("groupD");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));
        
        //--------------------------------------
        //fetch group without postLoad
        //--------------------------------------
        fp.addGroup("groupC");
        fp.removeGroup("groupD");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertFalse("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        //--------------------------------------
        //fetch group width postLoad
        //--------------------------------------
        fp.addGroup("groupAll");
        fp.removeGroup("groupC");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = false;
        assertTrue("Should call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));

        loadedFields[cmd.getAbsolutePositionOfMember("piece1")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece2")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece3")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece4")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece5")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece6")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece7")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece8")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece9")] = false;
        loadedFields[cmd.getAbsolutePositionOfMember("piece10")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece11")] = true;
        loadedFields[cmd.getAbsolutePositionOfMember("piece12")] = true;
        assertFalse("Should not call postLoad callback",fpc.isToCallPostLoadFetchPlan(loadedFields));
    }

    /**
     * Test the use of fetch plans with inherited objects.
     */
    public void testFetchPlanInheritance()
    {
        FetchPlan fp = getFetchPlan();
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager metaMgr = new JDOMetaDataManager(nucleusCtx);
        //test parent with fetch-group = group1
        AbstractClassMetaData cmd = metaMgr.getMetaDataForClass(DetachParent.class, new ClassLoaderResolverImpl());
        fp.addGroup("group1");
        fp.removeGroup(FetchPlan.DEFAULT);
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        int[] fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 2 fields in fetchplan",2,fieldsInFP.length);
        
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("fieldA should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldA")));            
        assertTrue("fieldB should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldB")));            

        //test child with fetch-group = group1
        cmd = metaMgr.getMetaDataForClass(DetachChildA.class, new ClassLoaderResolverImpl());
        fp.addGroup("group1");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 3 fields in fetchplan",3,fieldsInFP.length);
        
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("fieldA should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldA")));            
        assertTrue("fieldB should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldB")));            
        assertFalse("fieldC should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldC")));            
        assertFalse("fieldD should not be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldD")));            
        assertTrue("fieldE should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldE")));            

        //test child with no fetch-groups
        cmd = metaMgr.getMetaDataForClass(DetachChildB.class, new ClassLoaderResolverImpl());
        fp.addGroup("group1");
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 2 fields in fetchplan",2,fieldsInFP.length);
        
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("fieldA should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldA")));            
        assertTrue("fieldB should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("fieldB")));            
        
        cmd = metaMgr.getMetaDataForClass(FP2Sub.class, new ClassLoaderResolverImpl());
        
        //--------------------------------------
        //use FetchPlan.ALL
        //--------------------------------------
        fp.clearGroups();
        fp.addGroup(FetchPlan.ALL);
        fp.removeGroup(FetchPlan.DEFAULT);
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 17 fields in fetchplan",17,fieldsInFP.length);
        
        //check if fields are in the FP
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));            
        assertTrue("piece2 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece2")));            
        assertTrue("piece3 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece3")));            
        assertTrue("piece4 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));            
        assertTrue("piece5 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));            
        assertTrue("piece6 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece6")));            
        assertTrue("piece7 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));            
        assertTrue("piece8 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));            
        assertTrue("piece9 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece9")));            
        assertTrue("piece10 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));            
        assertTrue("piece11 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece11")));            
        assertTrue("piece12 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12"))); 
        assertTrue("piece14 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece14"))); 
        assertTrue("piece20 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece20"))); 
        assertTrue("piece21 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece21"))); 
        assertTrue("piece22 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece22"))); 
        assertTrue("piece23 should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece23")));
    }

    /**
     * Test of nested fetch plans
     */
    public void testNestedFetchPlans()
    throws Exception
    {
        FetchPlan fp = getFetchPlan();
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager metaMgr = new JDOMetaDataManager(nucleusCtx);
        AbstractClassMetaData cmd = metaMgr.getMetaDataForClass(FP2Base.class, new ClassLoaderResolverImpl());
        fp.addGroup("defaultPlus12");

        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece12 should be in fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece12")));
    }

    /**
     * Test nested fetch groups.
     * TODO Merge this with the test above
     */
    public void testNestedFetchGroupsAgain()
    {
        FetchPlan fp = getFetchPlan();
        NucleusContext nucleusCtx = new NucleusContext("JDO", null);
        MetaDataManager metaMgr = new JDOMetaDataManager(nucleusCtx);

        //test parent with fetch-group = group1
        AbstractClassMetaData cmd = metaMgr.getMetaDataForClass(FP1Sub.class, new ClassLoaderResolverImpl());
        fp.addGroup("1");
        fp.removeGroup(FetchPlan.DEFAULT);
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        int[] fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 2 fields in fetchplan", 2, fieldsInFP.length);

        //check if fields are in the FP
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("name should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("name")));            
        assertTrue("room should be in the fetchplan",fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("room")));            
    }

    /**
     * Test the use of the dynamic fetch plans via PMF.
     */
    public void testDynamicFetchPlanViaPMF()
    {
        JDOPersistenceManagerFactory myPMF = (JDOPersistenceManagerFactory)pmf;
        AbstractClassMetaData cmd = 
            myPMF.getNucleusContext().getMetaDataManager().getMetaDataForClass(FP2Base.class, new ClassLoaderResolverImpl());
        FetchPlan fp = getFetchPlan();

        // Test initial without DFG - should only have PK in fetch plan for PlaneA
        fp.removeGroup(FetchPlan.DEFAULT);
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        int[] fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 1 fields in fetchplan (PK)", 1, fieldsInFP.length);
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test empty group - should only have PK in fetch plan for PlaneA
        javax.jdo.FetchGroup grp1 = myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF");
        fp.addGroup("MyGroupPMF");

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 1, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test group for other class
        javax.jdo.FetchGroup grp2 = myPMF.getFetchGroup(FP3Base.class, "MyGroupPMF2");
        grp2.addMember("room2");

        fp.clearGroups();
        fp.removeGroup(FetchPlan.DEFAULT);
        fp.addGroup("MyGroupPMF2");

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 1, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test one group
        grp1 = myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF");
        grp1.addMember("piece4").addMember("piece7").addMember("piece10");
        fp.setGroup("MyGroupPMF");

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 4, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));

        // Test dynamic update to existing group after use
        myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF").removeMember("piece7");
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 3, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));

        // Test multiple groups for same class
        grp1 = myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF1");
        grp1.addMember("piece4").addMember("piece5");
        grp2 = myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF2");
        grp2.addMember("piece7").addMember("piece8");
        javax.jdo.FetchGroup grp3 = myPMF.getFetchGroup(FP2Base.class, "MyGroupPMF3");
        grp3.addMember("piece10");

        myPMF.removeAllFetchGroups();
        myPMF.addFetchGroups(new javax.jdo.FetchGroup[] {grp1, grp2, grp3});

        fp.setGroups(new String[]{"MyGroupPMF1", "MyGroupPMF2", "MyGroupPMF3"});

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 6, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
    }

    /**
     * Test the use of the dynamic fetch plans via PM.
     */
    public void testDynamicFetchPlanViaPM()
    {
        JDOPersistenceManagerFactory myPMF = (JDOPersistenceManagerFactory)pmf;
        AbstractClassMetaData cmd = 
            myPMF.getNucleusContext().getMetaDataManager().getMetaDataForClass(FP2Base.class, new ClassLoaderResolverImpl());
        JDOPersistenceManager myPM = (JDOPersistenceManager) myPMF.getPersistenceManager();

        FetchPlan fp = ((JDOFetchPlan)myPM.getFetchPlan()).getInternalFetchPlan();

        // Test initial without DFG - should only have PK in fetch plan for PlaneA
        fp.removeGroup(FetchPlan.DEFAULT);
        FetchPlanForClass fpc = fp.getFetchPlanForClass(cmd);
        int[] fieldsInFP = fpc.getMemberNumbers();
        assertEquals("should have 1 fields in fetchplan (PK)", 1, fieldsInFP.length);
        BitSet fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test empty group - should only have PK in fetch plan for PlaneA
        javax.jdo.FetchGroup grp1 = myPM.getFetchGroup(FP2Base.class, "MyGroupPM");
        fp.addGroup("MyGroupPM");

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 1, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test group for other class
        javax.jdo.FetchGroup grp2 = myPM.getFetchGroup(FP3Base.class, "MyGroupPM2");
        grp2.addMember("room2");
        fp.clearGroups();
        fp.removeGroup(FetchPlan.DEFAULT);
        fp.addGroup("MyGroupPM2");
        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 1, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));

        // Test one group
        grp1 = myPM.getFetchGroup(FP2Base.class, "MyGroupPM");
        grp1.addMember("piece4").addMember("piece7").addMember("piece10");
        fp.setGroup("MyGroupPM");

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 4, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));

        // Test dynamic update to existing group after use
        myPM.getFetchGroup(FP2Base.class, "MyGroupPM").removeMember("piece7");
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 3, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));

        // Test multiple groups for same class
        grp1 = myPM.getFetchGroup(FP2Base.class, "MyGroupPM1");
        grp1.addMember("piece4").addMember("piece5");
        grp2 = myPM.getFetchGroup(FP2Base.class, "MyGroupPM2");
        grp2.addMember("piece7").addMember("piece8");
        javax.jdo.FetchGroup grp3 = myPM.getFetchGroup(FP2Base.class, "MyGroupPM3");
        grp3.addMember("piece10");

        myPMF.removeAllFetchGroups();
        myPMF.addFetchGroups(new javax.jdo.FetchGroup[] {grp1, grp2, grp3});

        fp.setGroups(new String[]{"MyGroupPM1", "MyGroupPM2", "MyGroupPM3"});

        fpc = fp.getFetchPlanForClass(cmd);
        fieldsInFP = fpc.getMemberNumbers();
        assertEquals("Incorrect number of fields in FetchPlan", 6, fieldsInFP.length);
        fieldsInFPBitSet = fpc.getMemberNumbersByBitSet();
        assertTrue("piece1 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece1")));
        assertTrue("piece4 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece4")));
        assertTrue("piece5 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece5")));
        assertTrue("piece7 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece7")));
        assertTrue("piece8 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece8")));
        assertTrue("piece10 should be in the fetchplan", fieldsInFPBitSet.get(cmd.getAbsolutePositionOfMember("piece10")));
    }

    /**
     * Test the use of named fetch plans specified via named queries.
     */
    public void testNamedFetchPlanViaQuery()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Extract a named query
            Query q = pm.newNamedQuery(FP2Base.class, "FP2_ALL");
            javax.jdo.FetchPlan queryFP = q.getFetchPlan();

            // Check the FetchPlan being used by the query
            Set queryFPGroups = queryFP.getGroups();
            if (!queryFPGroups.contains("groupA"))
            {
                fail("Named Query with named FetchPlan is missing fetch-group 'groupA'");
            }
            if (!queryFPGroups.contains("groupB"))
            {
                fail("Named Query with named FetchPlan is missing fetch-group 'groupB'");
            }

            // Execute the query
            q.execute();

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception thrown executing named query with named fetch plan " + e.getMessage());
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
     * Convenience accessor for a new FetchPlan.
     * @return The FetchPlan
     */
    private FetchPlan getFetchPlan()
    {
        JDOPersistenceManager pm = (JDOPersistenceManager)pmf.getPersistenceManager();
        ExecutionContext ec = pm.getExecutionContext();
        return new FetchPlan(ec, ec.getClassLoaderResolver());
    }
}