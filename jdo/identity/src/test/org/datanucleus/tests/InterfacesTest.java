package org.datanucleus.tests;
/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 

Contributions :
2004 Barry Haddow - extended to give rigorous test suite
2004 Andy Jefferson - added test of collection of interfaces
    ...
***********************************************************************/


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.interfaces.Cereal;
import org.jpox.samples.interfaces.Circle;
import org.jpox.samples.interfaces.Circle3;
import org.jpox.samples.interfaces.Diet;
import org.jpox.samples.interfaces.Food;
import org.jpox.samples.interfaces.Rectangle;
import org.jpox.samples.interfaces.Rectangle3;
import org.jpox.samples.interfaces.Salad;
import org.jpox.samples.interfaces.Shape;
import org.jpox.samples.interfaces.Shape5;
import org.jpox.samples.interfaces.Shape5Circle;
import org.jpox.samples.interfaces.Shape5Holder;
import org.jpox.samples.interfaces.Shape5Rectangle;
import org.jpox.samples.interfaces.Shape5Square;
import org.jpox.samples.interfaces.ShapeHolder;
import org.jpox.samples.interfaces.ShapeHolder2;
import org.jpox.samples.interfaces.ShapeHolder3;
import org.jpox.samples.interfaces.ShapeHolder4;
import org.jpox.samples.interfaces.Square;
import org.jpox.samples.interfaces.Square3;
import org.jpox.samples.interfaces.Steak;
import org.jpox.samples.interfaces.Triangle;

/**
 * Series of tests for the use of interfaces.
 */
public class InterfacesTest extends JDOPersistenceTestCase
{
    private Random r = new Random();

    public InterfacesTest(String name)
    {
        super(name);
    }

    /**
     * Check that we can create a persistent object with an interface FCO.
     **/ 
    public void testCreation()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {ShapeHolder.class});

            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects.
                tx.begin();
                ShapeHolder holder = new ShapeHolder(r.nextInt());
                pm.makePersistent(holder);
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of interface objects.",false);
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
            clean(ShapeHolder.class);
        }
    }

    /**
     * Check that ShapeHolder objects can be read back again using an Extent.
     */
    public void testReadAllShapeHolders() 
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {ShapeHolder.class, Rectangle.class, Circle.class, Square.class, Triangle.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                tx.begin();    
                
                
                Rectangle rectangle = new Rectangle(r.nextInt(), 1.0, 2.0);
                ShapeHolder rectangleHolder = new ShapeHolder(r.nextInt());
                rectangleHolder.setShape1(rectangle);
                pm.makePersistent(rectangleHolder);
                
                ShapeHolder circleHolder = new ShapeHolder(r.nextInt());
                Circle circle = (Circle)circleHolder.getShape1();
                pm.makePersistent(circleHolder);
                
                ShapeHolder squareHolder = new ShapeHolder(r.nextInt());
                Square square = new Square(r.nextInt(), 2.0, 3.5);
                squareHolder.setShape1(square);      
                pm.makePersistent(squareHolder);
                
                ShapeHolder triangleHolder = new ShapeHolder(r.nextInt());
                Triangle triangle = new Triangle(r.nextInt(), "tri "+r.nextInt(), 2.1, 3.2);
                triangleHolder.setShape1(triangle);      
                pm.makePersistent(triangleHolder);
                
                tx.commit();
                
                tx.begin();
                Extent extent = pm.getExtent(ShapeHolder.class,false);
                for (Iterator i = extent.iterator(); i.hasNext();)
                {
                    ShapeHolder holder = (ShapeHolder)(i.next());
                    Shape shape = holder.getShape1();
                    if  (holder.getShape1() instanceof Circle)
                    {
                        //found the circle
                        assertNotNull("Two ShapeHolders with circles found in database, only one inserted.", circle);
                        assertEquals("Circle read back in ShapeHolder does not match the one inserted.", circle,shape );
                        circle = null;
                    } 
                    else if (holder.getShape1() instanceof Rectangle)
                    {
                        // found the rectangle
                        assertNotNull("Two ShapeHolders with rectangles found in database, only one inserted.", rectangle);
                        assertEquals("Rectangle read back in ShapeHolder does not match the one inserted.", rectangle,shape );
                        rectangle = null;
                    }
                    else if (holder.getShape1() instanceof Square)
                    {
                        // found the square
                        assertNotNull("Two ShapeHolders with squares found in database, only one inserted.", square);
                        assertEquals("Square read back in ShapeHolder does not match the one inserted.", square,shape );
                        square = null;
                    }    
                    else if (holder.getShape1() instanceof Triangle)
                    {
                        // found the triangle
                        assertNotNull("Two ShapeHolders with triangles found in database, only one inserted.", triangle);
                        assertEquals("Triangle read back in ShapeHolder does not match the one inserted.", triangle,shape );
                        triangle = null;
                    }
                    else
                    {
                        fail("Unknown shape [" + shape + "] found in ShapeHolder" );
                    }
                }
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test.",false);
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
            clean(ShapeHolder.class);
            clean(Circle.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Square.class);
        }
    }
    
    /**
     * Persist a ShapeHolder with one implementation of Shape, then change to
     * a different implementation and check that the saved version also has 
     * its implementation updated.
     */
    public void testChangeImplementation()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            
            try
            {
                //Create the ShapeHolder record
                tx.begin();
                
                ShapeHolder holder = new ShapeHolder(r.nextInt());
                Shape shape = new Rectangle(r.nextInt(), 5.0, 3.4);
                holder.setShape1(shape);
                pm.makePersistent(holder);
                
                tx.commit();
                
                //Load it back and change the Shape to a Circle
                tx.begin();
                
                Extent extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                assertEquals("The inserted rectangle", shape,holder.getShape1());
                shape = new Circle(r.nextInt(), 1.75);
                holder.setShape1(shape);
                
                extent.closeAll();
                tx.commit();
                
                //Load back the holder and check that its Shape is a Circle, then change
                //it back to a Rectangle
                tx.begin();
                
                extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                assertEquals("The inserted circle", shape,holder.getShape1());
                shape = new Rectangle(r.nextInt(), 3.897, 1.18);
                holder.setShape1(shape);
                
                extent.closeAll();
                tx.commit();
                
                //check that the holder's shape is a Rectangle again
                tx.begin();
                
                extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                assertEquals("The re-inserted rectangle", shape,holder.getShape1());
                
                extent.closeAll();
                tx.commit();
                
                //Do it again with application identity classes
                tx.begin();
                
                extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                shape = new Triangle(r.nextInt(), "tri"+r.nextInt(), 4.97, 7.29);
                holder.setShape1(shape);
                
                extent.closeAll();
                tx.commit();
                
                //check that the holder's shape is a Triangle again
                tx.begin();
                
                extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                assertEquals("The re-inserted triangle", shape,holder.getShape1());
                
                extent.closeAll();        
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test.",false);
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
            clean(ShapeHolder.class);
            clean(Circle.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Square.class);
        }
    }
 
    /**
     * Check that null shapes work correctly.
     */
    public void testNullValues()
    throws Exception
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                ShapeHolder holder = new ShapeHolder(r.nextInt());
                holder.setShape1(null);
                pm.makePersistent(holder);
                tx.commit();
                
                tx.begin();
                Extent extent = pm.getExtent(ShapeHolder.class,false);
                holder = (ShapeHolder)(extent.iterator().next());
                assertNull("Shape should be read back from database as null", holder.getShape1());
                extent.closeAll();
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create/query of interface objects",false);
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
            clean(ShapeHolder.class);
        }
    }

    /**
     * Insert a ShapeHolder object, delete it, then check that it's gone.
     */
    public void testInsertThenDelete()
    throws Exception
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create objects
                tx.begin();
                ShapeHolder holder = new ShapeHolder(r.nextInt());
                pm.makePersistent(holder);
                tx.commit();
                
                // Delete objects
                tx.begin();
                Extent extent = pm.getExtent(ShapeHolder.class,false);
                assertTrue("ShapeHolder should be available in extent.",extent.iterator().hasNext());
                pm.deletePersistent(extent.iterator().next());
                extent.closeAll();
                tx.commit();
                
                tx.begin();
                extent = pm.getExtent(ShapeHolder.class,false);
                assertFalse("There should be no more ShapeHolder objects.",extent.iterator().hasNext());
                extent.closeAll();
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during creation/deletion of interface objects.",false);
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
            clean(ShapeHolder.class);
        }
    }

    /**
     * Test for the creation of a set for interface objects using Join table.
     **/
    public void testSetJoinUnidir()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create container and some shapes
                tx.begin();
                
                ShapeHolder container = new ShapeHolder(r.nextInt());

                Circle circle = new Circle(r.nextInt(), 1.75);
                container.getShapeSet1().add(circle);
                Rectangle rectangle = new Rectangle(r.nextInt(), 1.0, 2.0);
                container.getShapeSet1().add(rectangle);
                
                pm.makePersistent(container);
                
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during creation of set of interface objects  using join table: ", e);
                assertTrue("Exception thrown during create of set of interface objects using join table : " + e.getMessage(), false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // TODO Extend this to then query the elements in the collection
        }
        finally
        {
            clean(ShapeHolder.class);
            clean(Circle.class);
            clean(Rectangle.class);
        }
    }

    /**
     * Test for the creation of a set for interface objects using Join table.
     **/
    public void testListJoin()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create container and some shapes
                tx.begin();
                
                ShapeHolder container = new ShapeHolder(r.nextInt());

                Circle circle = new Circle(r.nextInt(), 1.75);
                container.getShapeList1().add(circle);
                Rectangle rectangle = new Rectangle(r.nextInt(), 1.0, 2.0);
                container.getShapeList1().add(rectangle);
                
                pm.makePersistent(container);
                pm.flush();
                
                Query q = pm.newQuery(ShapeHolder.class);

                q.setFilter("shapeList1.contains(c)");
                q.declareParameters("Circle c");
                Collection results = (Collection) q.execute(circle);
                assertEquals(1, results.size());

                q = pm.newQuery(ShapeHolder.class);
                q.setFilter("shapeList1.contains(c) && c.id == -1");
                q.declareVariables("Circle c");
                results = (Collection) q.execute();
                assertEquals(0, results.size());
                
                q = pm.newQuery(ShapeHolder.class);
                q.setFilter("shapeList1.contains(c) && c.id == " + circle.getId());
                q.declareVariables("Circle c");
                results = (Collection) q.execute();
                assertEquals(1, results.size());

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during creation of list of interface objects  using join table", e);
                assertTrue("Exception thrown during create of list of interface objects using join table : " + e.getMessage(), false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                
                pm.close();
            }
            
            // TODO Extend this to then query the elements in the collection
        }
        finally
        {
            clean(ShapeHolder.class);
            clean(Circle.class);
            clean(Rectangle.class);
        }
    }

    /**
     * Test the specification of multiple implementations. One field has duplicate impls, and another
     * has full specification including columns (ORM only).
     **/
    public void testMultipleImplementations()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create container and some shapes
                tx.begin();

                ShapeHolder2 holder = new ShapeHolder2(r.nextInt());
                Circle circle = new Circle(1, 102.0);
                holder.setShape1(circle);
                Rectangle rect = new Rectangle(2, 250, 200);
                holder.setShape2(rect);

                pm.makePersistent(holder);

                tx.commit();
            }
            catch (Exception e)
            {
                fail(e.getLocalizedMessage());
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
            clean(ShapeHolder2.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }
    
    /**
     * Test a scenario that all columns are declared for an interface field
     * that has ColumnMetaData specified for only 1 implementation.
     **/
    public void testMultipleImplWithMissingColumns()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                // Create container and some shapes
                tx.begin();
                ShapeHolder4 holder = new ShapeHolder4(101);
                Circle circle = new Circle(1, 102.0);
                holder.setShape1(circle);
                pm.makePersistent(holder);

                tx.commit();
                fail("Expected exception, for wrong declared columns");
            }
            catch (Exception e)
            {
                // Do nothing
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
        }
    }

    /**
     * Run a simple query on a set of ShapeHolder objects whose fields are
     * interfaces with datastore identity.
     */
    public void testQueryOnInterfaceFields()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query query = pm.newQuery(pm.getExtent(ShapeHolder.class, true));
                query.declareImports("import org.jpox.samples.interfaces.Circle");
                query.declareParameters("double radiusBound");
                //set filter to return all circles with radius greater than given value
                query.setFilter("((Circle)shape1).radius >= radiusBound");

                Collection results = (Collection)query.execute(new Double(10));
                assertEquals("Expected number of large circles returned by the query", 5, results.size());

                for (Iterator i = results.iterator(); i.hasNext(); )
                {
                    ShapeHolder result = (ShapeHolder)(i.next());
                    assertEquals("Query should only return circles", Circle.class, result.getShape1().getClass());
                    assertTrue("Query should only return large circles", 
                        ((Circle)(result.getShape1())).getRadius() >= 10.0);
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying of objects : ", e);
                assertTrue("Exception thrown during querying of objects : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }

    /**
     * Combine expressions involving the interface fields using boolean
     * operators.
     */
    public void testQueryOnInterfaceFieldsBooleanOps()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(pm.getExtent(ShapeHolder.class, true));
                query.declareImports("import org.jpox.samples.interfaces.Circle; import org.jpox.samples.interfaces.Rectangle");

                // set filter to return large squares and circles
                query.setFilter("((Circle)shape1).radius >= 10 || ((Rectangle)shape1).length >= 10");
                Collection results = (Collection) query.execute();
                assertEquals("Expected number of large circles and rectangles in the query", 10, results.size());

                for (Iterator i = results.iterator(); i.hasNext();)
                {
                    ShapeHolder result = (ShapeHolder) (i.next());
                    if (result.getShape1() instanceof Circle)
                    {
                        assertTrue("Query should only return large circles", ((Circle) (result.getShape1())).getRadius() >= 10.0);
                    }
                    else if (result.getShape1() instanceof Rectangle)
                    {
                        assertTrue("Query should only return large rectangles", ((Rectangle) (result.getShape1())).getArea() >= 100.0);
                    }
                    else
                    {
                        fail("Query returned a " + result.getShape1().getClass() + " when only circles and rectangles were expected");
                    }
                }

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying of objects : ", e);
                assertTrue("Exception thrown during querying of objects : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }

    public void testCorrectCastOnInterfaceField()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(pm.getExtent(ShapeHolder.class, true));
                query.declareImports("import org.jpox.samples.interfaces.Circle; import org.jpox.samples.interfaces.Rectangle");

                // set filter to return large squares and circles
                query.setFilter("((InvalidCircle)shape1).radius >= 10");
                boolean success = false;
                try
                {
                    query.execute();
                }
                catch (JDOUserException ex)
                {
                    success = true;
                }
                assertTrue("Expected JDOUserException", success);

                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying of objects : ", e);
                assertTrue("Exception thrown during querying of objects : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }    

    /**
     * Access to fields in interfaces raises JDOUserException
     */
    public void testAccessToFieldsInInterfaces()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();        

                Query q = pm.newQuery(ShapeHolder.class);
                q.declareParameters("int the_width");
                q.setFilter("shape1.width == the_width");
                boolean success = false;
                try
                {
                    q.execute(new Integer(100));
                }
                catch (JDOUserException ex)
                {
                    success = true;
                }
                assertTrue("Access to fields in interfaces must raise JDOUserException",success);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying of objects : ", e);
                assertTrue("Exception thrown during querying of objects : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }

    /**
     * Query ShapeHolder objects with a null Shape.
     */
    public void testQueryForNullShape()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(pm.getExtent(ShapeHolder.class, true));
                query.setFilter("shape1 == null");
                Collection results = (Collection)query.execute();
                assertEquals("Expected number of ShapeHolder objects with null Shape is wrong", 5, results.size());
                for (Iterator i  = results.iterator(); i.hasNext(); )
                {
                    ShapeHolder result = (ShapeHolder)(i.next());
                    assertNull("ShapeHolder has non-null Shape!", result.getShape1());
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying for 'null' shape", e);
                assertTrue("Exception thrown during querying for 'null' shape : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }
    /**
     * Query ShapeHolder objects with a non-null Shape.
     */
    public void testQueryForNonNullShape()
    {
        try
        {
            // Create sample data for querying
            createSampleDataForQueryTests();

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query query = pm.newQuery(pm.getExtent(ShapeHolder.class, true));
                query.setFilter("shape1 != null");
                Collection results = (Collection)query.execute();
                assertEquals("Expected number of ShapeHolder objects with null Shape is wrong", 
                    20, results.size());
                for (Iterator i  = results.iterator(); i.hasNext(); )
                {
                    ShapeHolder result = (ShapeHolder)(i.next());
                    assertNotNull("ShapeHolder has null Shape!", result.getShape1());
                }
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying for not-null shape", e);
                assertTrue("Exception thrown during querying for not-null shape : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Square.class);
            clean(Triangle.class);
            clean(Rectangle.class);
            clean(Circle.class);
        }
    }

    /**
     * Convenience method to create data for querying.
     */
    protected void createSampleDataForQueryTests()
    {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        // Create some objects for our query tests
        try
        {
            tx.begin();

            //create some shapes
            ShapeHolder[] shapeHolders = new ShapeHolder[25];
            for (int i = 0; i < 5; ++i) 
            {
                //ShapeHolder with small circle
                shapeHolders[i*5] = new ShapeHolder(r.nextInt());
                shapeHolders[i*5].setShape1(new Circle(r.nextInt(), 1+ i*0.1));

                //ShapeHolder with large circle
                shapeHolders[i*5+1] = new ShapeHolder(r.nextInt());
                shapeHolders[i*5+1].setShape1(new Circle(r.nextInt(), 10 + i*0.1));

                //ShapeHolder with small rectangle
                shapeHolders[i*5+2] = new ShapeHolder(r.nextInt());
                shapeHolders[i*5+2].setShape1(new Rectangle(r.nextInt(), 1 + i*0.1, 2+ i*0.1 ));

                //ShapeHolder with large rectangle
                shapeHolders[i*5+3] = new ShapeHolder(r.nextInt());
                shapeHolders[i*5+3].setShape1(new Rectangle(r.nextInt(), 10 + i*0.1, 11+i*.01));

                //ShapeHolder with null
                shapeHolders[i*5+4] = new ShapeHolder(r.nextInt());
                shapeHolders[i*5+4].setShape1(null);
            }
            pm.makePersistentAll(shapeHolders);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during creation of test objects : ", e);
            assertTrue("Exception thrown during creation of test objects : " + e.getMessage(), false);
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
     * Query ShapeHolder objects that have a particular shape in the 1-1 field.
     */
    public void testQueryForOneToOneShape()
    {
        try
        {
            // Create sample data for querying
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object holder1Id = null;
            Object rect1Id = null;
            try
            {
                tx.begin();

                ShapeHolder holder1 = new ShapeHolder(101);
                Rectangle rect1 = new Rectangle(200, 140, 250);
                Circle circ = new Circle(300, 45);
                holder1.setShape1(rect1);
                holder1.setShape2(circ);
                pm.makePersistent(holder1);

                ShapeHolder holder2 = new ShapeHolder(102);
                Rectangle rect2 = new Rectangle(201, 250, 140);
                Square sq = new Square(400, 500, 500);
                holder2.setShape1(rect2);
                holder2.setShape2(sq);
                pm.makePersistent(holder2);

                tx.commit();
                holder1Id = pm.getObjectId(holder1);
                rect1Id = pm.getObjectId(rect1);
            }
            catch (Exception e)
            {
                LOG.error("Exception during persist of sample data", e);
                fail("Exception creating sample data : " + e.getMessage());
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
                Rectangle rect1 = (Rectangle)pm.getObjectById(rect1Id);

                Query query = pm.newQuery("SELECT FROM " + ShapeHolder.class.getName() +
                    " WHERE shape1 == :myShape");
                Map params = new HashMap();
                params.put("myShape", rect1);
                Collection results = (Collection)query.executeWithMap(params);
                assertEquals("Number of ShapeHolders with 1-1 field as our Rectangle was wrong", 1, results.size());
                ShapeHolder holder = (ShapeHolder)results.iterator().next();
                assertEquals("Id of ShapeHolder was wrong", holder1Id, pm.getObjectId(holder));
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during querying for 1-1 shape", e);
                assertTrue("Exception thrown during querying for 1-1 shape : " + e.getMessage(), false);
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
            // Clean out data
            clean(ShapeHolder.class);
            clean(Rectangle.class);
            clean(Circle.class);
            clean(Square.class);
        }
    }

    /**
     * Test for use of mapping-strategy="identity" for 1-1 relation
     */ 
    public void testMappingStrategyIdentity1To1()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {Diet.class});

            Object id = null;

            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects
                tx.begin();
                Diet diet = new Diet(1);
                Food fave = new Steak();
                diet.setFavouriteFood(fave);
                pm.makePersistent(diet);
                tx.commit();
                id = JDOHelper.getObjectId(diet);
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during create of interface objects.");
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
                Diet diet = (Diet)pm.getObjectById(id);
                Food fave = diet.getFavouriteFood();
                assertNotNull("Favourite food is null!!", fave);
                LOG.info(">> fave=" + fave);
                assertTrue("Favourite should be Steak!", fave instanceof Steak);

                // Set to null
                diet.setFavouriteFood(null);
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during retrieval of objects");
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
                Diet diet = (Diet)pm.getObjectById(id);
                Food fave = diet.getFavouriteFood();
                assertNull("Favourite food is not null!!", fave);
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during retrieval of objects");
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
            clean(Diet.class);
            clean(Steak.class);
        }
    }

    /**
     * Test for use of mapping-strategy="identity" for 1-N relation
     */ 
    public void testMappingStrategyIdentity1ToN()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {Diet.class});

            Object id = null;

            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                // Create some objects.
                tx.begin();
                Diet diet = new Diet(1);
                Steak steak = new Steak();
                Salad salad = new Salad();
                diet.getFoods().add(steak);
                diet.getFoods().add(salad);
                pm.makePersistent(diet);
                tx.commit();
                id = JDOHelper.getObjectId(diet);
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of interface objects.", false);
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
                Diet diet = (Diet)pm.getObjectById(id);
                assertNotNull("Diet foods is null!!", diet.getFoods());
                assertEquals("Number of foods in diet is wrong", 2, diet.getFoods().size());
                Set foods = diet.getFoods();
                boolean steakFound = false;
                boolean saladFound = false;
                Iterator iter = foods.iterator();
                while (iter.hasNext())
                {
                    Food food = (Food)iter.next();
                    if (food instanceof Steak)
                    {
                        steakFound = true;
                    }
                    if (food instanceof Salad)
                    {
                        saladFound = true;
                    }
                }
                assertTrue("Steak was not retrieved as a food!", steakFound);
                assertTrue("Salad was not retrieved as a food!", saladFound);

                // Go on a healthy diet!
                foods.clear();
                foods.add(new Cereal());
                foods.add(new Salad());

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during retrieval of interface objects.", false);
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
                Diet diet = (Diet)pm.getObjectById(id);
                assertNotNull("Diet foods is null!!", diet.getFoods());
                assertEquals("Number of foods in diet is wrong", 2, diet.getFoods().size());
                Set foods = diet.getFoods();
                boolean cerealFound = false;
                boolean saladFound = false;
                Iterator iter = foods.iterator();
                while (iter.hasNext())
                {
                    Food food = (Food)iter.next();
                    if (food instanceof Cereal)
                    {
                        cerealFound = true;
                    }
                    if (food instanceof Salad)
                    {
                        saladFound = true;
                    }
                }
                assertTrue("Cereal was not retrieved as a food!", cerealFound);
                assertTrue("Salad was not retrieved as a food!", saladFound);

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during create of interface objects.", false);
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
            clean(Diet.class);
            clean(Steak.class);
            clean(Salad.class);
            clean(Cereal.class);
        }
    }

    /**
     * Test for the creation of an set for interface objects using FK.
     */
    public void testSetFK()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {ShapeHolder3.class, Rectangle3.class, Circle3.class, Square3.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id;
            try
            {
                // Create container and some shapes
                tx.begin();
                ShapeHolder3 container = new ShapeHolder3(r.nextInt());
                Circle3 circle = new Circle3(r.nextInt(), 1.75);
                container.getShapeSet().add(circle);
                Rectangle3 rectangle = new Rectangle3(r.nextInt(), 1.0, 2.0);
                container.getShapeSet().add(rectangle);
                assertEquals(2,container.getShapeSet().size());
                pm.makePersistent(container);
                tx.commit();
                id = pm.getObjectId(container);
                pm.close();

                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                ShapeHolder3 actual = (ShapeHolder3) pm.getObjectById(id);
                assertEquals(2,actual.getShapeSet().size());
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during creation of set of interface objects using FK : ", e);
                assertTrue("Exception thrown during create of set of interface objects using FK : " + e.getMessage(), false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // TODO Extend this to then query the elements in the collection
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test of interface objects using FK : ", e);
            assertTrue("Exception thrown during test of interface objects using FK : " + e.getMessage(), false);
        }
        finally
        {
            clean(Circle3.class);
            clean(Rectangle3.class);
            clean(ShapeHolder3.class);
        }
    }

    /**
     * Test for the creation of a set of interface objects using join table and bidirectional relation.
     */
    public void testSetJoinBidir()
    throws Exception
    {
        try
        {
            addClassesToSchema(new Class[] {Shape5Holder.class, Shape5Rectangle.class, Shape5Circle.class, Shape5Square.class});

            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id;
            try
            {
                // Create container and some shapes
                tx.begin();
                Shape5Holder container = new Shape5Holder(r.nextInt());
                Shape5Circle circle = new Shape5Circle(r.nextInt(), 1.75);
                container.getShapeSet().add(circle);
                Shape5Rectangle rectangle = new Shape5Rectangle(r.nextInt(), 1.0, 2.0);
                container.getShapeSet().add(rectangle);
                assertEquals(2,container.getShapeSet().size());
                pm.makePersistent(container);
                tx.commit();
                id = pm.getObjectId(container);
                pm.close();

                pm = pmf.getPersistenceManager();
                tx = pm.currentTransaction();
                tx.begin();
                Shape5Holder actual = (Shape5Holder) pm.getObjectById(id);
                assertEquals(2,actual.getShapeSet().size());
                Iterator<Shape5> shIter = actual.getShapeSet().iterator();
                boolean circlePresent = false;
                boolean rectPresent = false;
                while (shIter.hasNext())
                {
                    Shape5 sh = shIter.next();
                    if (sh instanceof Shape5Circle)
                    {
                        circlePresent = true;
                        assertEquals(actual, sh.getShapeHolder());
                        assertEquals(1.75, ((Shape5Circle) sh).getRadius());
                    }
                    else if (sh instanceof Shape5Rectangle)
                    {
                        rectPresent = true;
                        assertEquals(actual, sh.getShapeHolder());
                        assertEquals(1.0, ((Shape5Rectangle) sh).getWidth());
                        assertEquals(2.0, ((Shape5Rectangle) sh).getLength());
                    }
                }
                assertTrue(circlePresent);
                assertTrue(rectPresent);
                tx.commit();
            }
            catch (Exception e)
            {
                LOG.error("Exception thrown during creation of set of interface objects using FK : ", e);
                assertTrue("Exception thrown during create of set of interface objects using FK : " + e.getMessage(), false);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            
            // TODO Extend this to then query the elements in the collection
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during test of interface objects using FK : ", e);
            assertTrue("Exception thrown during test of interface objects using FK : " + e.getMessage(), false);
        }
        finally
        {
            clean(Shape5Holder.class);
            clean(Shape5Circle.class);
            clean(Shape5Rectangle.class);
        }
    }
}