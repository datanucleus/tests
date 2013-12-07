/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.tests.types;

import java.awt.Point;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.point.PointHolder;

/**
 * Tests for SCO mutable type java.awt.Point.
 */
public class PointTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public PointTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    PointHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test of the basic persistence and retrieval of java.awt.Point mutable SCO type.
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            PointHolder myPoint = new PointHolder(0,0);
            myPoint.setPoint(new Point(50, 75));
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myPoint);
                id = JDOHelper.getObjectId(myPoint);
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                pm.refresh(myPoint2);
                assertEquals(50, (int)myPoint2.getPoint().getX());
                assertEquals(75, (int)myPoint2.getPoint().getY());
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
            
            // Check retrieval with new PM (so we go to the datastore)
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertNotNull("Point is null on retrieval", myPoint2.getPoint());
                assertEquals(50, (int)myPoint2.getPoint().getX());
                assertEquals(75, (int)myPoint2.getPoint().getY());
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
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertTrue("MyPoint class had a null point but should have had a value", myPoint2.getPoint() != null);
                
                myPoint2.setLocation(100, 125);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the location of the Point : " + e.getMessage());
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
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertEquals(100, (int)myPoint2.getPoint().getX());
                assertEquals(125, (int)myPoint2.getPoint().getY());
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
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertTrue("MyPoint class had a null point but should have had a value", myPoint2.getPoint() != null);
                
                myPoint2.move(100, 125);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the location of the Point : " + e.getMessage());
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
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertEquals(100, (int)myPoint2.getPoint().getX());
                assertEquals(125, (int)myPoint2.getPoint().getY());
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
            
            // Check the mutability
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertTrue("MyPoint class had a null point but should have had a value", myPoint2.getPoint() != null);
                
                myPoint2.translate(100, 125);
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the location of the Point : " + e.getMessage());
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
                PointHolder myPoint2 = (PointHolder) pm.getObjectById(id,true);
                assertEquals(200, (int)myPoint2.getPoint().getX());
                assertEquals(250, (int)myPoint2.getPoint().getY());
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
            clean(PointHolder.class);
        }
    }

    /**
     * Test of the attach/detach process for an object that contains a SCO Point.
     * @throws Exception
     */
    public void testDetachAttach()
    throws Exception
    {
        try
        {
            PointHolder detachedPoint = null;
            Object pointId = null;
            
            // Persist an object containing a Point
            PersistenceManager pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("point");
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                PointHolder point = new PointHolder(0, 0);
                pm.makePersistent(point);
                
                detachedPoint = (PointHolder)pm.detachCopy(point);
                
                tx.commit();
                pointId = pm.getObjectId(point);
            }
            catch (Exception e)
            {
                fail("Error whilst persisting and detaching object containing SCO Point : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            assertTrue("Value of the java.awt.Point that was detached is incorrect : X should have been 0 but was " + detachedPoint.getPoint().getX(),
                (int)detachedPoint.getPoint().getX() == 0);
            assertTrue("Value of the java.awt.Point that was detached is incorrect : Y should have been 0 but was " + detachedPoint.getPoint().getY(),
                (int)detachedPoint.getPoint().getY() == 0);
            
            // Perform an update to the contents of the Point
            int detachedX = 400;
            int attachedY = 300;
            detachedPoint.setPointX(detachedX);
            
            // Attach the Point
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("point");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                PointHolder attachedPoint = (PointHolder)pm.makePersistent(detachedPoint);
                pm.flush();
                
                // Update the contents of the Point now attached (test that it uses SCO wrappers)
                attachedPoint.setPointY(attachedY);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO Point : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("point");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                PointHolder point = (PointHolder)pm.getObjectById(pointId);
                
                assertTrue("Value of the java.awt.Point that was retrieved is incorrect : X should have been " + detachedX + " but was " + 
                    point.getPoint().getX(), (int)point.getPoint().getX() == detachedX);
                assertTrue("Value of the java.awt.Point that was retrieved is incorrect : Y should have been " + attachedY + " but was " +
                    point.getPoint().getY(), (int)point.getPoint().getY() == attachedY);
                
                detachedPoint = (PointHolder)pm.detachCopy(point);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO Point : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Replace the point
            detachedPoint.setPoint(new java.awt.Point(200, 200));
            
            // Attach the point
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("point");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(detachedPoint);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst attaching object containing SCO Point : " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // Retrieve and check the results
            pm = pmf.getPersistenceManager();
            pm.getFetchPlan().addGroup("point");
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                PointHolder point = (PointHolder)pm.getObjectById(pointId);
                
                assertTrue("Value of the java.awt.Point that was retrieved is incorrect : X should have been 200 but was " + 
                    point.getPoint().getX(), (int)point.getPoint().getX() == 200);
                assertTrue("Value of the java.awt.Point that was retrieved is incorrect : Y should have been 200 but was " +
                    point.getPoint().getY(), (int)point.getPoint().getY() == 200);
                
                detachedPoint = (PointHolder)pm.detachCopy(point);
                
                tx.commit();
            }
            catch (Exception e)
            {
                fail("Error whilst retrieving object containing SCO Point : " + e.getMessage());
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
            clean(PointHolder.class);
        }
    }

    /**
     * Test of querying capabilities for Point.
     */
    public void testQuery()
    throws Exception
    {
        try
        {
            PointHolder myPoint = new PointHolder(0,0);
            myPoint.setPoint(new Point(50, 75));
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(myPoint);
                tx.commit();
                id = pm.getObjectId(myPoint);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // Perform some queries
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = pm.newQuery(PointHolder.class);
                List<PointHolder> holders = (List<PointHolder>)q.execute();
                assertNotNull(holders);
                assertEquals(1, holders.size());
                assertEquals(JDOHelper.getObjectId(holders.get(0)), id);

                PointHolder holder1 = (PointHolder)pm.getObjectById(id);
                Query q2 = pm.newQuery("SELECT FROM " + PointHolder.class.getName() + " WHERE point == :pt");
                List<PointHolder> holders2 = (List<PointHolder>)q2.execute(holder1.getPoint());
                assertNotNull(holders2);
                assertEquals(1, holders2.size());

                Query q3 = pm.newQuery("SELECT FROM " + PointHolder.class.getName() + " WHERE point.getX() == 50 && point.getY() == 75");
                List<PointHolder> holders3 = (List<PointHolder>)q3.execute();
                assertNotNull(holders3);
                assertEquals(1, holders3.size());

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
            clean(PointHolder.class);
        }
    }
}