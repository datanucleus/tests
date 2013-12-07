/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others
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
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jdo.Extent;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.NucleusContext;
import org.datanucleus.api.jdo.metadata.JDOMetaDataManager;
import org.jpox.samples.inheritance.ABase;
import org.jpox.samples.inheritance.ASub1;
import org.jpox.samples.inheritance.ASub2;
import org.jpox.samples.inheritance.BBase;
import org.jpox.samples.inheritance.BSub;
import org.jpox.samples.inheritance.BSubSub;
import org.jpox.samples.inheritance.CBase;
import org.jpox.samples.inheritance.CSub1;
import org.jpox.samples.inheritance.CSub2;
import org.jpox.samples.inheritance.DBase;
import org.jpox.samples.inheritance.DElemBase;
import org.jpox.samples.inheritance.DElemSub;
import org.jpox.samples.inheritance.DSub;
import org.jpox.samples.inheritance.EBase;
import org.jpox.samples.inheritance.EElemBase;
import org.jpox.samples.inheritance.EElemSub;
import org.jpox.samples.inheritance.ESub;
import org.jpox.samples.inheritance.FBase;
import org.jpox.samples.inheritance.FSub1;
import org.jpox.samples.inheritance.FSub1Sub;
import org.jpox.samples.inheritance.FSub2;
import org.jpox.samples.inheritance.GBase;
import org.jpox.samples.inheritance.GSub1;
import org.jpox.samples.inheritance.GSub2;
import org.jpox.samples.inheritance.HBase;
import org.jpox.samples.inheritance.JBase;
import org.jpox.samples.inheritance.JBowl;
import org.jpox.samples.inheritance.JMarble;
import org.jpox.samples.inheritance.JSpottedMarble;
import org.jpox.samples.inheritance.JTransparentMarble;
import org.jpox.samples.inheritance.KSub1;
import org.jpox.samples.inheritance.LBase;
import org.jpox.samples.inheritance.LSub;
import org.jpox.samples.inheritance.MBase;
import org.jpox.samples.inheritance.MSub1;
import org.jpox.samples.inheritance.QASub;
import org.jpox.samples.inheritance.QBSub;

/**
 * Test the handling of inheritance strategies.
 *
 * @version $Revision: 1.2 $ 
 */
public class InheritanceTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * Constructor.
     * @param name Name of the test (not used)
     */
    public InheritanceTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    ABase.class,
                    ASub1.class,
                    ASub2.class,
                    BBase.class,
                    BSub.class,
                    BSubSub.class,
                    CBase.class,
                    CSub1.class,
                    CSub2.class,
                    DBase.class,
                    DSub.class,
                    DElemBase.class,
                    DElemSub.class,
                    EBase.class,
                    ESub.class,
                    EElemBase.class,
                    EElemSub.class,
                    FBase.class,
                    FSub1.class,
                    FSub2.class,
                    FSub1Sub.class,
                    GBase.class,
                    GSub1.class,
                    GSub2.class,
                    HBase.class,
                    JBowl.class,
                    JMarble.class,
                    JSpottedMarble.class,
                    JTransparentMarble.class,
                    KSub1.class,
                    LSub.class,
                    LBase.class,
                    MSub1.class,
                    MBase.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test for "subclass-table" inheritance strategy.
     */
    public void testSubclassTable()
    {
        try
        {
            performSubclassTableNewTableTest(CBase.class, CSub1.class);
        }
        finally
        {
            // Clean out our data
            clean(CSub2.class);
            clean(CSub1.class);
            clean(CBase.class);
        }
    }

    /**
     * Test for inheritance where the base has "new-table", sub class has "subclass-table" and 
     * sub-subclass has "new-table".
     */
    public void testSubclassTableNewTable()
    {
        try
        {
            performSubclassTableNewTableTest(BSub.class, BSubSub.class);
        }
        finally
        {
            // Clean out our data
            clean(BSubSub.class);
            clean(BSub.class);
        }
    }

    /**
     * Test when a class is using subclass-table and no super/sub class define a table,
     * a JDOUserException is expected.
     */
    public void testInheritanceWithNoTable()
    {
        try
        {
            HBase single;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            boolean success = false;
            try
            {
                tx.begin();
                single = new HBase();
                single.setId(new Integer(1));
                pm.makePersistent(single);
                JDOHelper.getObjectId(single);
                tx.commit();
            }
            catch(JDOUserException ex)
            {
                success = true;
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
            assertTrue("Expected JDOUserException",success);
        }
        finally
        {
            // Nothing to do since nothing created
        }
    }

    /**
     * Test for "superclass-table" where the base class has its own table and the sub class
     * has "superclass-table". Uses "value-map" discriminator strategy
     */
    public void testNewTableSuperclassTableValueMap()
    {
        try
        {
            performNewTableSuperclassTableTest(GBase.class, GSub1.class, GSub2.class);
        }
        finally
        {
            clean(GSub2.class);
            clean(GSub1.class);
            clean(GBase.class);
        }
    }

    /**
     * Test for "superclass-table" where the base class has its own table and the sub class
     * has "superclass-table". Uses "class-name" discriminator strategy
     */
    public void testNewTableSuperclassTableClassName()
    {
        try
        {
            performNewTableSuperclassTableTest(FBase.class, FSub1.class, FSub2.class);
        }
        finally
        {
            clean(FSub2.class);
            clean(FSub1.class);
            clean(FBase.class);
        }
    }

    /**
     * Test for "new-table" where the base class has "new-table" and the sub class
     * has "new-table".
     */
    public void testNewTableNewTable()
    {
        try
        {
            performNewTableNewTableTest(ABase.class, ASub1.class);
        }
        finally
        {
            clean(ASub1.class);
            clean(ABase.class);
        }
    }

    /**
     * Test for the use of "newTable" strategy inheritance performing detailed checks on the
     * objects returned from various extents.
     **/
    public void testNewTableExtentResults()
    throws Exception
    {
        try
        {
            // Create some inherited data
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                ABase base = null;
                base = new ASub1();
                base.setName("First");
                base.setRevision(1);
                base.setSeat("Rearward");
                pm.makePersistent(base);
                
                base = new ABase();
                base.setName("Second");
                base.setRevision(2);
                base.setSeat("Rearward");
                pm.makePersistent(base);
                
                base = new ABase();
                base.setName("Third");
                base.setRevision(6);
                base.setSeat("Forward");
                pm.makePersistent(base);
                
                base = new ASub2();
                base.setName("Fourth");
                base.setRevision(2);
                base.setSeat("Aisle");
                pm.makePersistent(base);
                
                base = new ASub2();
                base.setName("Fifth");
                base.setRevision(3);
                base.setSeat("Window");
                pm.makePersistent(base);
                
                base = new ASub1();
                base.setName("Sixth");
                base.setRevision(2);
                base.setSeat("Rearward");
                pm.makePersistent(base);
                
                base = new ASub1();
                base.setName("Seventh");
                base.setRevision(1);
                base.setSeat("Forward");
                pm.makePersistent(base);
                
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }
            
            // Query the inherited data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                Query q = null;
                Collection c=null;
                Iterator iter=null;

                // All ABase's with revision < 3 ORDER BY name (ascending)
                q = pm.newQuery(pm.getExtent(ABase.class, true), "revision < 3");
                q.setOrdering("name ascending");
                c = (Collection)q.execute();
                assertEquals("Inheritance-NewTable : Number of ABase's retrieved (with revision below 3) is incorrect",
                    5, c.size());

                int i = 0;
                iter = c.iterator();
                while (iter.hasNext())
                {
                    // Check on the ordering and types
                    Object o = iter.next();
                    if ((i == 0 && !(o instanceof ASub1)) ||
                        (i == 1 && !(o instanceof ASub2)) ||
                        (i == 2 && !(o instanceof ABase)) ||
                        (i == 3 && !(o instanceof ASub1)) ||
                        (i == 4 && !(o instanceof ASub1)))
                    {
                        fail("Inheritance-NewTable : Item " + i + " is of incorrect type - " + o);
                    }
                    else
                    {
                        LOG.debug(o.toString());
                    }
                    
                    i++;
                }
                
                // All ASub1
                q = pm.newQuery(pm.getExtent(ASub1.class, true));
                c = (Collection)q.execute();
                assertEquals("Inheritance-NewTable : Number of ASub1s retrieved is incorrect", 3, c.size()); 
                
                // All ASub2
                q = pm.newQuery(pm.getExtent(ASub2.class, true));
                c = (Collection)q.execute();
                assertEquals("Inheritance-NewTable : Number of ASub2s retrieved is incorrect", 2, c.size());
                
                // All ABase
                q = pm.newQuery(pm.getExtent(ABase.class, true));
                c = (Collection)q.execute();
                assertEquals("Inheritance-NewTable : Number of ABases+subclass retrieved is incorrect",
                    7, c.size());

                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Inheritance-NewTable : Exception thrown during test " + ue.getMessage(),false);
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
            clean(ASub1.class);
            clean(ASub2.class);
            clean(ABase.class);
        }
    }

    /**
     * Test for "new-table" where the base class has "new-table" and the sub class
     * has "subclass-table", and it has a subclass using "new-table"
     */
    public void testNewTableSubclassTableNewTable()
    {
        try
        {
            performNewTableSubclassTableNewTableTest(BBase.class, BSub.class, BSubSub.class);
        }
        finally
        {
            clean(BSubSub.class);
            clean(BSub.class);
            clean(BBase.class);
        }
    }

    /**
     * Test of a container using superclass-table having elements also using superclass-table - join table 1-N.
     */
    public void testSuperclassTable1toNJoinTableRelationship()
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                ESub container = new ESub();
                container.setName("SampleContainer");
                container.setValue(300.00);

                EElemSub element = new EElemSub();
                element.setName("SampleElement");
                element.setRevision(1);
                element.setValue(12.50);
                element.setDescription("A sample element");
                
                container.addElement(element);
                pm.makePersistent(container);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while persisting join-table 1-N container (using superclass-table) with element (using superclass-table) : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(ESub.class);
            clean(EElemSub.class);
        }
    }

    /**
     * Test of a container using superclass-table having elements also using superclass-table - FK 1-N.
     */
    public void testSuperclassTable1toNFKRelationship()
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx = pm.currentTransaction();
                tx.begin();
                DSub container = new DSub();
                container.setName("SampleContainer");
                container.setValue(300.00);
                
                DElemSub element = new DElemSub();
                element.setName("SampleElement");
                element.setRevision(1);
                element.setValue(12.50);
                element.setDescription("A sample element");
                
                container.addElement(element);
                pm.makePersistent(container);
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                LOG.error(ue);
                fail("Exception thrown while persisting FK 1-N container (using superclass-table) with element (using superclass-table) : " + ue.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }
        }
        finally
        {
            clean(DSub.class);
            clean(DElemSub.class);
        }
    }

    /**
     * Test of a collection of objects which are of type "new-table" (base) with 2 subclasses
     * both using "superclass-table".
     */
    public void testInverseCollectionWithElementUsingSuperclassTable()
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            Object id[] = new Object[4];
            try
            {
                tx.begin();
                
                // Create an assortment of bowls
                JBowl bowl[] = new JBowl[4];
                bowl[0] = new JBowl(1,"Bowl of Spotted Marbles");
                bowl[1] = new JBowl(2,"Bowl of Transparent Marbles");
                bowl[2] = new JBowl(3,"Bowl of Marbles");
                bowl[3] = new JBowl(4,"Bowl of Plain Marbles");
                
                // Create some marbles
                JMarble marble[] = new JMarble[18]; 
                marble[0] = new JSpottedMarble(1,"purple", "yellow");
                marble[1] = new JSpottedMarble(2,"navy", "red");
                marble[2] = new JSpottedMarble(3,"burgandy", "blue");
                marble[3] = new JSpottedMarble(4,"teal", "green");
                
                marble[4] = new JSpottedMarble(5,"purple", "yellow");
                marble[5] = new JSpottedMarble(6,"navy", "red");
                marble[6] = new JSpottedMarble(7,"burgandy", "blue");
                marble[7] = new JSpottedMarble(8,"teal", "green");
                
                // Assign marbles to bowls
                bowl[0].addMarble(marble[0]);
                bowl[0].addMarble(marble[1]);
                bowl[0].addMarble(marble[2]);
                bowl[0].addMarble(marble[3]);
                
                bowl[2].addMarble(marble[4]);
                bowl[2].addMarble(marble[5]);
                bowl[2].addMarble(marble[6]);
                bowl[2].addMarble(marble[7]);
                
                marble[8] = new JMarble(100,"burgandy");
                marble[9] = new JMarble(101,"burgandy");

                //bowl[3].getSpottedMarbles().add(marble[8]);
                //bowl[3].getSpottedMarbles().add(marble[9]);

                marble[10] = new JTransparentMarble(11,"indigo", 95);
                marble[11] = new JTransparentMarble(12,"taupe", 64);
                marble[12] = new JTransparentMarble(13,"cyan", 41);
                marble[13] = new JTransparentMarble(14,"magenta", 17);

                bowl[1].addMarble(marble[10]);
                bowl[1].addMarble(marble[11]);
                bowl[1].addMarble(marble[12]);
                bowl[1].addMarble(marble[13]);

                marble[14] = new JTransparentMarble(15,"indigo", 59);
                marble[15] = new JTransparentMarble(16,"taupe", 46);
                marble[16] = new JTransparentMarble(17,"cyan", 14);
                marble[17] = new JTransparentMarble(18,"magenta", 71);

                bowl[2].addMarble(marble[14]);
                bowl[2].addMarble(marble[15]);
                bowl[2].addMarble(marble[16]);
                bowl[2].addMarble(marble[17]);
                
                pm.makePersistentAll(bowl);
                
                tx.commit();
                
                id[0] = pm.getObjectId(bowl[0]);
                id[1] = pm.getObjectId(bowl[1]);
                id[2] = pm.getObjectId(bowl[2]);
                id[3] = pm.getObjectId(bowl[3]);
                
                // Query the bowls
                tx.begin();
                
                bowl[0] = (JBowl) pm.getObjectById(id[0],true);
                bowl[1] = (JBowl) pm.getObjectById(id[1],true);
                bowl[2] = (JBowl) pm.getObjectById(id[2],true);
                bowl[3] = (JBowl) pm.getObjectById(id[3],true);

                assertEquals(4,bowl[0].getSpottedMarbles().size());
                assertEquals(0,bowl[0].getTransparentMarbles().size());
                assertEquals(0,bowl[1].getSpottedMarbles().size());
                assertEquals(4,bowl[1].getTransparentMarbles().size());
                assertEquals(4,bowl[2].getSpottedMarbles().size());
                assertEquals(4,bowl[2].getTransparentMarbles().size());
                assertEquals(0,bowl[3].getSpottedMarbles().size());
                assertEquals(0,bowl[3].getTransparentMarbles().size());
                
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
            
            PersistenceManager pm2 = pmf.getPersistenceManager();
            tx = pm2.currentTransaction();
            try
            {
                tx.begin();
                
                JBowl bowl = (JBowl)pm2.getObjectById(id[2]);
                
                // Check the correct determination of number of marbles of the 2 types
                assertTrue("Number of spotted marbles should have been 4 but was " + bowl.getNumberOfSpottedMarbles(), 
                    bowl.getNumberOfSpottedMarbles() == 4);
                assertTrue("Number of transparent marbles should have been 4 but was " + bowl.getNumberOfTransparentMarbles(), 
                    bowl.getNumberOfTransparentMarbles() == 4);
                
                // Check the spotted marbles
                Set spottedMarbles = bowl.getSpottedMarbles();
                assertTrue("Number of spotted marbles retrieved via collection should have been 4 but was " + spottedMarbles.size(),
                    spottedMarbles.size() == 4);
                Iterator spottedIter = spottedMarbles.iterator();
                while (spottedIter.hasNext())
                {
                    spottedIter.next();
                }
                
                // Check the transparent marbles
                Set transparentMarbles = bowl.getTransparentMarbles();
                assertTrue("Number of transparent marbles retrieved via collection should have been 4 but was " + transparentMarbles.size(),
                    transparentMarbles.size() == 4);
                Iterator transparentIter = transparentMarbles.iterator();
                while (transparentIter.hasNext())
                {
                    transparentIter.next();
                }
                
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while querying container with superclass-table objects : " + e.getMessage());
            }
            finally
            {
                if (tx != null && tx.isActive())
                {
                    tx.rollback();
                }
                pm2.close();
            }
        }
        finally
        {
            clean(JBowl.class);
            clean(JSpottedMarble.class);
            clean(JTransparentMarble.class);
            clean(JMarble.class);
        }
    }

    /**
     * Test for the use of "newTable" strategy inheritance performing detailed checks on the
     * objects returned from various extents.
     **/
    /*public void testNewTableExtentResults()
    throws Exception
    {
        try
        {
            // Create some inherited data
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                Product product=null;
                product = new MyCompactDisc("P_001","Depende by Jarabe de Palo","Second CD by Jarabe de Palo","http://www.emilatin.com","GBP",12.99,0.00,12.99,17.5,0,"Jarabe de Palo","Depende",1998,"Joe Dworniak","EMI Latin","Main warehouse");
                pm.makePersistent(product);
                
                product = new Product("P_002","NEX IIe","NEX IIe MP3 player from Frontier Labs","http://www.frontierlabs.com","GBP",70.00,0.00,85.00,17.5,0);
                pm.makePersistent(product);
                
                product = new Product("P_003","SliMP3","SliMP3 Network audio player","http://www.slimdevices.com","GBP",209.00,0.00,209.00,17.5,0);
                pm.makePersistent(product);
                
                product = new Book("P_004","Spanish Dictionary","A Spanish to English dictionary from Collins","http://www.amazon.com","GBP",5.99,0.00,5.99,0.00,0,"0-00-470787-7","Collins","Spanish Dictionary plus Grammar",2,"Harper Collins");
                pm.makePersistent(product);
                
                product = new Book("P_005","La hojarasca by Gabriel Garcia Marquez","First novel by Gabriel Garcia Marquez","http://www.amazon.com","GBP",10.99,0.00,10.99,0.00,0,"950-07-0087-5","Gabriel Garcia Marquez","La hojarasca",36,"Editorial Sudamericana");
                pm.makePersistent(product);
                
                product = new CompactDisc("P_006","The Man Who by Travis","Second CD by Travis","http://www.emi.com","GBP",13.99,0.00,13.99,17.5,0,"Travis","The Man Who",2000,"Unknown","EMI");
                pm.makePersistent(product);
                
                product = new YourCompactDisc("P_007","Fugazi by Marillion","Second CD by Marillion","http://www.emi.com","GBP",15.00,0.00,15.00,17.5,0,"Marillion","Fugazi",1984,"Unknown","EMI");
                pm.makePersistent(product);
                
                tx.commit();
            }
            finally
            {
                if (tx.isActive())
                    tx.rollback();
                
                pm.close();
            }
            
            // Query the inherited data
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                Extent e=null;
                Query  q=null;
                Collection c=null;
                Iterator   iter=null;
                
                // All Product's with price < 150 ORDER BY Price (ascending)
                e = pm.getExtent(org.jpox.samples.store.Product.class,true);
                q = pm.newQuery(e,"price < 150.00");
                q.setOrdering("price ascending");
                c = (Collection)q.execute();
                LOG.debug("Inheritance-NewTable : Query for all Product's with price < 150.00 - no of items=" + c.size());
                
                // Check on the number of items
                assertTrue("Inheritance-NewTable : Number of Products retrieved (with price below 150) is incorrect (" + c.size() + ") : should have been 6",c.size() == 6); 
                
                int i=0;
                iter = c.iterator();
                while (iter.hasNext())
                {
                    // Check on the ordering and types
                    Object o=iter.next();
                    if ((i == 0 && !(o instanceof org.jpox.samples.store.Book)) ||
                            (i == 1 && !(o instanceof org.jpox.samples.store.Book)) ||
                            (i == 2 && !(o instanceof org.jpox.samples.store.MyCompactDisc)) ||
                            (i == 3 && !(o instanceof org.jpox.samples.store.CompactDisc)) ||
                            (i == 4 && !(o instanceof org.jpox.samples.store.YourCompactDisc)) ||
                            (i == 5 && !(o instanceof org.jpox.samples.store.Product)))
                    {
                        assertTrue("Inheritance-NewTable : Item " + i + " is of incorrect type - " + o,false);
                    }
                    else
                    {
                        LOG.debug(o.toString());
                    }
                    
                    i++;
                }
                
                // All Books
                e = pm.getExtent(org.jpox.samples.store.Book.class,true);
                q = pm.newQuery(e);
                c = (Collection)q.execute();
                LOG.debug("Inheritance-NewTable : Query for all Book's - no of items=" + c.size());
                
                // Check on the number of items
                assertTrue("Inheritance-NewTable : Number of Books retrieved is incorrect (" + c.size() + ") : should have been 2",c.size() == 2); 
                
                iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    LOG.debug(o.toString());
                }
                
                // All CompactDiscs
                e = pm.getExtent(org.jpox.samples.store.CompactDisc.class,true);
                q = pm.newQuery(e);
                c = (Collection)q.execute();
                LOG.debug("Inheritance-NewTable : Query for all CompactDisc's - no of items=" + c.size());
                
                // Check on the number of items
                assertTrue("Inheritance-NewTable : Number of CompactDiscs retrieved is incorrect (" + c.size() + ") : should have been 3",c.size() == 3); 
                
                iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    LOG.debug(o.toString());
                }
                
                // All Products
                e = pm.getExtent(org.jpox.samples.store.Product.class,true);
                q = pm.newQuery(e);
                c = (Collection)q.execute();
                iter = c.iterator();
                LOG.debug("Inheritance-NewTable : Query for all Product's - no of items=" + c.size());
                if (c.size() == 7)
                {
                    while (iter.hasNext())
                    {
                        Object o=iter.next();
                        LOG.info(o.toString());
                    }
                }
                else
                {
                    assertTrue("Inheritance-NewTable : Error retrieving Products",false);
                }
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Inheritance-NewTable : Exception thrown during test " + ue.getMessage(),false);
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
            clean(MyCompactDisc.class);
            clean(CompactDisc.class);
            clean(Book.class);
            clean(Product.class);
        }
    }*/

    /**
     * Test the use of overriding of fields in the superclass, and storing the
     * field in the subclass.
     */
    public void testNewTableOverrideFields()
    {
        try
        {
            Object baseId = null;
            Object subId = null;
            
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                LBase base = new LBase("Base", 1);
                LSub sub = new LSub("Sub", 2, 10.0);

                pm.makePersistent(base);
                pm.makePersistent(sub);
                
                tx.commit();
                baseId = pm.getObjectId(base);
                subId = pm.getObjectId(sub);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while persisting basic objects with overridden fields : " + e.getMessage());
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

                LBase base = (LBase)pm.getObjectById(baseId);
                LSub sub = (LSub)pm.getObjectById(subId);
                assertEquals("Base object names are not equal", base.getName(), "Base");
                assertEquals("Base object levels are not equal", base.getLevel(), 1);
                assertEquals("Sub object names are not equal", sub.getName(), "Sub");
                assertEquals("Sub object levels are not equal", sub.getLevel(), 2);
                assertEquals("Sub object values are not equal", sub.getValue(), 10.0, 0.01);

                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Exception thrown while retrieving objects with overridden fields : " + e.getMessage());
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
            clean(LSub.class);
            clean(LBase.class);
        }
    }

    /**
     * Test for use of "superclass-table" using "value-map" discriminator and using INTEGER based storage.
     */
    public void testSuperclassTableValueMapInteger()
    {
        try
        {
            // Create objects
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                KSub1 sub = new KSub1();
                pm.makePersistent(sub);
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

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                Query q = pm.newQuery(KSub1.class);
                q.setUnique(true);
                Object obj = q.execute();
                assertNotNull("Query has returned null, so likely the discriminator was not correctly stored", obj);
                assertEquals("Query result is of the wrong type", KSub1.class.getName(), obj.getClass().getName());
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
            clean(KSub1.class);
        }
    }

    /**
     * Test of relation between base classes using subclass-table and having single subclass (using new-table).
     */
    public void testSubclassTableNewTableRelation()
    {
        try
        {
            // Create objects
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            Object id = null;
            try
            {
                tx.begin();
                QASub a = new QASub();
                QBSub b1 = new QBSub();
                QBSub b2 = new QBSub();
                a.getBs().add(b1);
                a.getBs().add(b2);
                b1.setA(a);
                b2.setA(a);

                pm.makePersistent(a);
                tx.commit();
                id = JDOHelper.getObjectId(a);
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                pm.close();
            }

            // Check the results
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                QASub a = (QASub)pm.getObjectById(id);
                List bs = a.getBs();
                assertEquals("Number of B elements in A is incorrect", 2, bs.size());
                // TODO Check the elements?
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
            clean(QASub.class);
            clean(QBSub.class);
        }
    }

    // --------------------------------- Perform the tests -----------------------------------------

    /**
     * Method to perform the test for "subclass-table" strategies.
     * All classes passed into this should fit the idea of a baseclass
     * with "subclass-table" strategy and subclass with "new-table" strategy.
     * @param baseClass The base class that uses "subclass-table"
     * @param subClass The sub class that uses "new-table"
     */
    protected void performSubclassTableNewTableTest(Class baseClass, Class subClass)
    {
        // Create object
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object oid=null;
        try
        {
            tx.begin();

            Object obj = subClass.newInstance();
            Method setNameMethod = subClass.getMethod("setName", new Class[] {String.class});
            setNameMethod.invoke(obj, new Object[] {"My Example"});
            Method setValueMethod = subClass.getMethod("setValue", new Class[] {double.class});
            setValueMethod.invoke(obj, new Object[] {new Double(1234.56)});

            pm.makePersistent(obj);
            pm.flush();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q1 = pm.newQuery(subClass, "name == \"My Example\"");
            Collection coll1 = (Collection)q1.execute();
            assertTrue("Unable to find an object of type " + subClass.getName() + " when one should have been found", coll1 != null);
            assertTrue("Should have found a single object of type " + subClass.getName() + ", but found " + coll1.size(), coll1.size() == 1);

            tx.commit();
            oid = pm.getObjectId(obj);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown during create of object of class " + subClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using getObjectById
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Object obj = pm.getObjectById(oid, false);
            if (obj == null)
            {
                fail("pm.getObjectById returned null when attempting to retrieve an object of type InheritSubNoTable");
            }
            LOG.info(">> Object retrieved is of type " + obj.getClass().getName());

            Method getNameMethod = subClass.getMethod("getName", new Class[] {});
            String name = (String)getNameMethod.invoke(obj, new Object[] {});
            Method getValueMethod = subClass.getMethod("getValue", new Class[] {});
            Double value = (Double)getValueMethod.invoke(obj, new Object[] {});

            assertTrue(subClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Example\"", 
                name.equals("My Example"));
            assertTrue(subClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                value.doubleValue() == 1234.56);

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown during retrieval of object of type " + subClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using Query, starting from base type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q = pm.newQuery(baseClass);
            Collection coll = (Collection)q.execute();
            assertTrue("Unable to find a " + baseClass.getName() + " object when one should have been found", coll != null);
            assertTrue("Should have found a single " + baseClass.getName() + " object, but found " + coll.size(), coll.size() == 1);
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();

                Method getNameMethod = subClass.getMethod("getName", new Class[] {});
                String name = (String)getNameMethod.invoke(obj, new Object[] {});
                Method getValueMethod = subClass.getMethod("getValue", new Class[] {});
                Double value = (Double)getValueMethod.invoke(obj, new Object[] {});

                assertTrue(subClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Example\"", 
                    name.equals("My Example"));
                assertTrue(subClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                    value.doubleValue() == 1234.56);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            fail("Exception thrown during retrieval of object of type " + baseClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using Query, starting from actual object type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q = pm.newQuery(subClass, "value == 1234.56");
            Collection coll = (Collection)q.execute();
            assertTrue("Unable to find an " + subClass.getName() + " object when one should have been found", coll != null);
            assertTrue("Should have found a single " + subClass.getName() + " object, but found " + coll.size(), coll.size() == 1);
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                
                Method getNameMethod = subClass.getMethod("getName", new Class[] {});
                String name = (String)getNameMethod.invoke(obj, new Object[] {});
                Method getValueMethod = subClass.getMethod("getValue", new Class[] {});
                Double value = (Double)getValueMethod.invoke(obj, new Object[] {});

                assertTrue(subClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Example\"", 
                    name.equals("My Example"));
                assertTrue(subClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                    value.doubleValue() == 1234.56);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            fail("Exception thrown during retrieval of object of type " + subClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the sub class.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(subClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                LOG.info("Extent => " + iter.next());
            }
 
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            ue.printStackTrace();
            fail("Exception thrown during creation of Extent for type " + subClass.getName() + " including subclasses : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the base class to see if this is possible. Should be possible, but will need to find
        // all sub-classes for the extent and give the results of (potentiall) multiple base tables in the extent.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(baseClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                LOG.info("Extent => " + iter.next());
            }
 
            // This should be possible since we have included instances of subclasses so should get some objects
 
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            ue.printStackTrace();
            fail("Exception thrown during creation of Extent for type " + baseClass.getName() + " including subclasses : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Update the object

        // Delete the object

    }

    /**
     * Method to perform the test for strategies with "new-table", "subclass-table", "new-table" strategies.
     * @param baseClass The base class that uses "new-table"
     * @param noTableSubClass The sub class that uses "subclass-table"
     * @param newTableSubSubClass The subsubclass that uses "new-table"
     */
    protected void performNewTableSubclassTableNewTableTest(Class baseClass, Class noTableSubClass, Class newTableSubSubClass)
    {
        // Create object
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object base_oid=null;
        Object sub_oid=null;
        try
        {
            tx.begin();

            Object baseobject = baseClass.newInstance();
            Method setNameMethod = baseClass.getMethod("setName", new Class[] {String.class});
            setNameMethod.invoke(baseobject, new Object[] {"My Base Example"});

            pm.makePersistent(baseobject);
            pm.flush();

            Object subobject = newTableSubSubClass.newInstance();
            Method subSetNameMethod = newTableSubSubClass.getMethod("setName", new Class[] {String.class});
            subSetNameMethod.invoke(subobject, new Object[] {"My Sub Example"});
            Method subSetValueMethod = newTableSubSubClass.getMethod("setValue", new Class[] {double.class});
            subSetValueMethod.invoke(subobject, new Object[] {new Double(1234.56)});

            pm.makePersistent(subobject);
            pm.flush();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q1 = pm.newQuery(baseClass, "name == \"My Sub Example\"");
            Collection coll1 = (Collection)q1.execute();
            assertTrue("Unable to find an object of type " + baseClass.getName() + " when one should have been found", coll1 != null);
            assertTrue("Should have found a single object of type " + baseClass.getName() + ", but found " + coll1.size(), coll1.size() == 1);

            tx.commit();
            base_oid = pm.getObjectId(baseobject);
            sub_oid = pm.getObjectId(subobject);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during query", e);
            fail("Exception thrown during create of objects of classes " + baseClass.getName() + "," + newTableSubSubClass.getName() + 
                " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the objects using getObjectById
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Object baseobject = pm.getObjectById(base_oid, false);
            if (baseobject == null)
            {
                fail("pm.getObjectById returned null when attempting to retrieve an object of type " + baseClass.getName());
            }
            assertTrue("Base object should have been of type " + baseClass.getName() + " but was of type " + baseobject.getClass().getName(),
                baseobject.getClass().getName().equals(baseClass.getName()));

            Object subobject = pm.getObjectById(sub_oid, false);
            if (subobject == null)
            {
                fail("pm.getObjectById returned null when attempting to retrieve an object of type " + newTableSubSubClass.getName());
            }
            assertTrue("Base object should have been of type " + newTableSubSubClass.getName() + " but was of type " + subobject.getClass().getName(),
                subobject.getClass().getName().equals(newTableSubSubClass.getName()));

            Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
            String base_name = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
            Method subGetNameMethod = newTableSubSubClass.getMethod("getName", new Class[] {});
            String sub_name = (String)subGetNameMethod.invoke(subobject, new Object[] {});
            Method getValueMethod = newTableSubSubClass.getMethod("getValue", new Class[] {});
            Double value = (Double)getValueMethod.invoke(subobject, new Object[] {});
            assertTrue(baseClass.getName() + " object \"name\" attribute is incorrect : is \"" + base_name + "\" but should have been \"My Base Example\"", 
                base_name.equals("My Base Example"));
            assertTrue(newTableSubSubClass.getName() + " object \"name\" attribute is incorrect : is \"" + sub_name + "\" but should have been \"My Sub Example\"", 
                sub_name.equals("My Sub Example"));
            assertTrue(newTableSubSubClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                value.doubleValue() == 1234.56);

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown during query", e);
            fail("Exception thrown during retrieval of objects of type " + baseClass.getName() + "," + newTableSubSubClass.getName() + 
                " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using Query, starting from base type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q = pm.newQuery(baseClass);
            LOG.info(">> Query being performed on baseClass");
            Collection coll = (Collection)q.execute();
            LOG.info(">> Query for base class performed");
            assertTrue("Unable to find objects when 2 should have been found", coll != null);
            assertTrue("Should have found 2 objects, but found " + coll.size(), coll.size() == 2);
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                if (obj.getClass().getName().equals(baseClass.getName()))
                {
                    Method getNameMethod = baseClass.getMethod("getName", new Class[] {});
                    String name = (String)getNameMethod.invoke(obj, new Object[] {});
                    assertTrue(baseClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Base Example\"", 
                        name.equals("My Base Example"));
                }
                else if (obj.getClass().getName().equals(newTableSubSubClass.getName()))
                {
                    Method getNameMethod = newTableSubSubClass.getMethod("getName", new Class[] {});
                    String name = (String)getNameMethod.invoke(obj, new Object[] {});
                    Method getValueMethod = newTableSubSubClass.getMethod("getValue", new Class[] {});
                    Double value = (Double)getValueMethod.invoke(obj, new Object[] {});
                    assertTrue(newTableSubSubClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Sub Example\"", 
                        name.equals("My Sub Example"));
                    assertTrue(newTableSubSubClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                        value.doubleValue() == 1234.56);
                }
                else
                {
                    fail("Query retrieved object of type " + obj.getClass().getName() + " !!!");
                }
            }

            tx.commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error(e);
            fail("Exception thrown during retrieval of object of type " + baseClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using Query, starting from sub type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q = pm.newQuery(newTableSubSubClass, "value == 1234.56");
            Collection coll = (Collection)q.execute();
            assertTrue("Unable to find an " + newTableSubSubClass.getName() + " object when one should have been found", coll != null);
            assertTrue("Should have found a single " + newTableSubSubClass.getName() + " object, but found " + coll.size(), coll.size() == 1);
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                
                Method getNameMethod = newTableSubSubClass.getMethod("getName", new Class[] {});
                String name = (String)getNameMethod.invoke(obj, new Object[] {});
                Method getValueMethod = newTableSubSubClass.getMethod("getValue", new Class[] {});
                Double value = (Double)getValueMethod.invoke(obj, new Object[] {});

                assertTrue(newTableSubSubClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Sub Example\"", 
                    name.equals("My Sub Example"));
                assertTrue(newTableSubSubClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                    value.doubleValue() == 1234.56);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            fail("Exception thrown during retrieval of object of type " + newTableSubSubClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the object using Query, starting from no table type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q = pm.newQuery(noTableSubClass, "value == 1234.56");
            Collection coll = (Collection)q.execute();
            assertTrue("Unable to find an " + noTableSubClass.getName() + " object when one should have been found", coll != null);
            assertTrue("Should have found a single " + noTableSubClass.getName() + " object, but found " + coll.size(), coll.size() == 1);
            Iterator iter = coll.iterator();
            while (iter.hasNext())
            {
                Object obj = iter.next();
                
                Method getNameMethod = noTableSubClass.getMethod("getName", new Class[] {});
                String name = (String)getNameMethod.invoke(obj, new Object[] {});
                Method getValueMethod = noTableSubClass.getMethod("getValue", new Class[] {});
                Double value = (Double)getValueMethod.invoke(obj, new Object[] {});

                assertTrue(newTableSubSubClass.getName() + " object \"name\" attribute is incorrect : is \"" + name + "\" but should have been \"My Sub Example\"", 
                    name.equals("My Sub Example"));
                assertTrue(newTableSubSubClass.getName() + " object \"value\" attribute is incorrect : is " + value.doubleValue() + " but should have been 1234.56",
                    value.doubleValue() == 1234.56);
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.info(">> Exception thrown in execution of query", e);
            fail("Exception thrown during retrieval of object of type " + noTableSubClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the base class.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(baseClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                LOG.info("Extent => " + iter.next());
            }
 
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for type " + baseClass.getName() + " including subclasses : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the sub class.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(newTableSubSubClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                LOG.info("Extent => " + iter.next());
            }
 
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for type " + newTableSubSubClass.getName() + " including subclasses : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the subclass-table class to see if this is possible. Should be possible, but will need to find
        // all sub-classes for the extent and give the results of (potentiall) multiple base tables in the extent.
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(noTableSubClass, true);
            Iterator iter = e.iterator();
            while (iter.hasNext())
            {
                LOG.info("Extent => " + iter.next());
            }
 
            // This should be possible since we have included instances of subclasses so should get some objects
 
            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for type " + baseClass.getName() + " including subclasses : " + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Update the object

        // Delete the object

    }

    /**
     * Method to perform the test for "superclass-table" strategies.
     * @param baseClass The base class that uses "new-table"
     * @param subClass1 The sub class that uses "superclass-table"
     * @param subClass2 The second sub class using "superclass-table"
     */
    private void performNewTableSuperclassTableTest(Class baseClass, Class subClass1, Class subClass2)
    {
        // Create base object
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object oid_base=null;
        Object oid_sub1=null;
        Object oid_sub2=null;
        try
        {
            tx.begin();

            // Create a base object
            Object obj = baseClass.newInstance();
            Method setNameMethod = baseClass.getMethod("setName", new Class[] {String.class});
            setNameMethod.invoke(obj, new Object[] {"Base Object"});

            pm.makePersistent(obj);

            tx.commit();
            oid_base = pm.getObjectId(obj);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during create of object of type " + baseClass.getName() + " for \"superclass-table\" strategy inheritance tree : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Create sub objects
        // This is done in separate transaction since if we do it in same tx
        // we get a deadlock due to an insert on the table before a modification to the table structure
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Create a sub object
            Object obj1 = subClass1.newInstance();
            Method setNameMethod = subClass1.getMethod("setName", new Class[] {String.class});
            setNameMethod.invoke(obj1, new Object[] {"Sub Object 1"});
            Method setValueMethod = subClass1.getMethod("setValue", new Class[] {double.class});
            setValueMethod.invoke(obj1, new Object[] {new Double(1234.56)});

            pm.makePersistent(obj1);

            tx.commit();
            oid_sub1 = pm.getObjectId(obj1);

            tx = pm.currentTransaction();
            tx.begin();

            // Create a sub object
            Object obj2 = subClass2.newInstance();
            Method setNameMethod2 = subClass2.getMethod("setName", new Class[] {String.class});
            setNameMethod2.invoke(obj2, new Object[] {"Sub Object 2"});
            Method setValueMethod2 = subClass2.getMethod("setValue2", new Class[] {float.class});
            setValueMethod2.invoke(obj2, new Object[] {new Float(2345.67)});

            pm.makePersistent(obj2);

            tx.commit();
            oid_sub2 = pm.getObjectId(obj2);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during create of object of type " + subClass1.getName() + " for \"superclass-table\" strategy inheritance tree : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the objects
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            // Base
            Object baseobject = pm.getObjectById(oid_base, false);
            assertTrue("Unable to retrieve object of type " + baseClass.getName(), baseobject != null);

            Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
            String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
            assertTrue("The retrieved " + baseClass.getName() + " object is incorrect : name=" + baseName, 
                baseName.equals("Base Object"));
            assertTrue("The retrieved " + baseClass.getName() + " object is a " + subClass1.getName() + " object which is incorrect", 
                !(subClass1.isAssignableFrom(baseobject.getClass())));

            // Sub 1
            Object subobject1 = pm.getObjectById(oid_sub1, false);
            assertTrue("Unable to retrieve object of type " + subClass1.getName(), subobject1 != null);

            Method subGetNameMethod = subClass1.getMethod("getName", new Class[] {});
            String subName = (String)subGetNameMethod.invoke(subobject1, new Object[] {});
            assertTrue("The retrieved object of type " + subClass1.getName() + " is incorrect : name=" + subName, 
                subName.equals("Sub Object 1"));

            // Sub 2
            Object subobject2 = pm.getObjectById(oid_sub2, false);
            assertTrue("Unable to retrieve object of type " + subClass2.getName(), subobject2 != null);

            Method subGetNameMethod2 = subClass2.getMethod("getName", new Class[] {});
            String subName2 = (String)subGetNameMethod2.invoke(subobject2, new Object[] {});
            assertTrue("The retrieved object of type " + subClass2.getName() + " is incorrect : name=" + subName2, 
                subName2.equals("Sub Object 2"));

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during update of base and subclass objects for \"superclass-table\" strategy inheritance tree : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the base object using Query, starting from actual object type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q1 = pm.newQuery(baseClass,"name == \"Base Object\"");
            Collection coll1 = (Collection)q1.execute();
            assertTrue("Unable to find an " + baseClass.getName() + " object when one should have been found", coll1 != null);
            assertTrue("Should have found a single " + baseClass.getName() + " object, but found " + coll1.size(), coll1.size() == 1);
            Iterator iter1 = coll1.iterator();
            while (iter1.hasNext())
            {
                Object baseobject = iter1.next();
                Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
                String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
                assertTrue(baseClass.getName() + " object \"name\" attribute is incorrect : is \"" + baseName + "\" but should have been \"Base Object\"", 
                    baseName.equals("Base Object"));
            }

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q2 = pm.newQuery(baseClass,"name == \"Sub Object 1\"");
            Collection coll2 = (Collection)q2.execute();
            assertTrue("Unable to find an " + subClass1.getName() + " object when one should have been found", coll2 != null);
            assertTrue("Should have found a single " + subClass1.getName() + " object, but found " + coll2.size(), coll2.size() == 1);
            Iterator iter2 = coll2.iterator();
            while (iter2.hasNext())
            {
                Object baseobject = iter2.next();
                Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
                String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
                assertTrue(subClass1.getName() + " object \"name\" attribute is incorrect : is \"" + baseName + "\" but should have been \"Sub Object 1\"", 
                    baseName.equals("Sub Object 1"));
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during retrieval of object of type " + baseClass.getName() + " : "+ e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        
        // Obtain an Extent of the base class
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getExtent(baseClass, true);
            
            Extent e = pm.getExtent(baseClass, true);
            Iterator iter=e.iterator();
            int size = 0;
            while (iter.hasNext())
            {
                iter.next();
                size++;
            }
            assertTrue("Number of elements returned by Extent of base class in newtable-superclasstable hierarchy was wrong :" + 
                " is " + size + " but should have been 3", size == 3);

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for " + baseClass.getName() + " including subclasses" + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        
        // Obtain an Extent of the sub class 1
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getExtent(subClass1, true);

            Extent e = pm.getExtent(subClass1, true);
            Iterator iter=e.iterator();
            int size = 0;
            while (iter.hasNext())
            {
                iter.next();
                size++;
            }
            assertTrue("Number of elements returned by Extent of sub class in newtable-superclasstable hierarchy was wrong :" + 
                " is " + size + " but should have been 1", size == 1);

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for " + subClass1.getName() + " including subclasses" + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Obtain an Extent of the sub class 2
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getExtent(subClass2, true);

            Extent e = pm.getExtent(subClass2, true);
            Iterator iter=e.iterator();
            int size = 0;
            while (iter.hasNext())
            {
                iter.next();
                size++;
            }
            assertTrue("Number of elements returned by Extent of sub class in newtable-superclasstable hierarchy was wrong :" + 
                " is " + size + " but should have been 1", size == 1);

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for " + subClass2.getName() + " including subclasses" + ue.getMessage());
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
     * Method to perform the test for "new-table" strategies.
     * @param baseClass The base class that uses "new-table"
     * @param subClass The sub class that uses "new-table"
     */
    private void performNewTableNewTableTest(Class baseClass, Class subClass)
    {
        // Create objects
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Object oid_base=null;
        Object oid_sub=null;
        try
        {
            tx.begin();

            // Create a base object
            Object baseobject = baseClass.newInstance();
            Method baseSetNameMethod = baseClass.getMethod("setName", new Class[] {String.class});
            baseSetNameMethod.invoke(baseobject, new Object[] {"Base Object"});

            pm.makePersistent(baseobject);

            // Create a sub object
            Object subobject = subClass.newInstance();
            Method subSetNameMethod = subClass.getMethod("setName", new Class[] {String.class});
            subSetNameMethod.invoke(subobject, new Object[] {"Sub Object"});
            Method subSetValueMethod = subClass.getMethod("setValue", new Class[] {double.class});
            subSetValueMethod.invoke(subobject, new Object[] {new Double(1234.56)});

            pm.makePersistent(subobject);

            tx.commit();

            oid_base = pm.getObjectId(baseobject);
            oid_sub = pm.getObjectId(subobject);
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during create of base and subclass objects for \"new-table\" strategy inheritance tree : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }

        // Retrieve the objects
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Object baseobject = pm.getObjectById(oid_base, false);
            assertTrue("Unable to retrieve object of type " + baseClass.getName(), baseobject != null);
            Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
            String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
            assertTrue("The retrieved object of type " + baseClass.getName() + " is incorrect : name=" + baseName, 
                baseName.equals("Base Object"));
            assertTrue("The retrieved object of type " + baseClass.getName() + " is an instance of " + subClass.getName() + " which is incorrect", 
                !(subClass.isAssignableFrom(baseobject.getClass())));

            Object subobject = pm.getObjectById(oid_sub, false);
            assertTrue("Unable to retrieve object of type " + subClass.getName(), subobject != null);
            Method subGetNameMethod = subClass.getMethod("getName", new Class[] {});
            String subName = (String)subGetNameMethod.invoke(subobject, new Object[] {});
            assertTrue("The retrieved object of type " + subClass.getName() + " is incorrect : name=" + subName, 
                subName.equals("Sub Object"));

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during update of base and subclass objects for \"new-table\" strategy inheritance tree : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        
        // Retrieve the base object using Query, starting from actual object type
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q1 = pm.newQuery(baseClass, "name == \"Base Object\"");
            Collection coll1 = (Collection)q1.execute();
            assertTrue("Unable to find an object of type " + baseClass.getName() + " when one should have been found", coll1 != null);
            assertTrue("Should have found a single object of type " + baseClass.getName() + ", but found " + coll1.size(), coll1.size() == 1);
            Iterator iter1 = coll1.iterator();
            while (iter1.hasNext())
            {
                Object baseobject = iter1.next();
                Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
                String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
                assertTrue(baseClass.getName() + " \"name\" attribute is incorrect : is \"" + baseName + "\" but should have been \"Base Object\"", 
                    baseName.equals("Base Object"));
            }

            pm.getFetchPlan().addGroup(FetchPlan.ALL);
            Query q2 = pm.newQuery(baseClass, "name == \"Sub Object\"");
            Collection coll2 = (Collection)q2.execute();
            assertTrue("Unable to find an object of type " + subClass.getName() + " when one should have been found", coll2 != null);
            assertTrue("Should have found a single object of type " + subClass.getName() + ", but found " + coll2.size(), coll2.size() == 1);
            Iterator iter2 = coll2.iterator();
            while (iter2.hasNext())
            {
                Object baseobject = iter2.next();
                Method baseGetNameMethod = baseClass.getMethod("getName", new Class[] {});
                String baseName = (String)baseGetNameMethod.invoke(baseobject, new Object[] {});
                assertTrue(subClass.getName() + " \"name\" attribute is incorrect : is \"" + baseName + "\" but should have been \"Sub Object\"", 
                    baseName.equals("Sub Object"));
            }

            tx.commit();
        }
        catch (Exception e)
        {
            LOG.error("Exception thrown", e);
            fail("Exception thrown during retrieval of object of type " + baseClass.getName() + " : " + e.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        
        // Obtain an Extent of the base class
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(baseClass, true);
            Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for " + baseClass.getName() + " including subclasses" + ue.getMessage());
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
        
        // Obtain an Extent of the sub class
        pm = pmf.getPersistenceManager();
        tx = pm.currentTransaction();
        try
        {
            tx.begin();

            Extent e = pm.getExtent(subClass, true);
            Iterator iter=e.iterator();
            while (iter.hasNext())
            {
                iter.next();
            }

            tx.commit();
        }
        catch (JDOUserException ue)
        {
            LOG.error("Exception thrown", ue);
            fail("Exception thrown during creation of Extent for " + subClass.getName() + " including subclasses" + ue.getMessage());
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
     * Some test for subclass information.
     */
    public void testSubclassInformation()
    {
        JDOMetaDataManager mgr = new JDOMetaDataManager(new NucleusContext("JDO", null));

        // initialize metadata
        mgr.getMetaDataForClass(JBase.class, new ClassLoaderResolverImpl());
        mgr.getMetaDataForClass(JBowl.class, new ClassLoaderResolverImpl());
        mgr.getMetaDataForClass(JSpottedMarble.class, new ClassLoaderResolverImpl());
        mgr.getMetaDataForClass(JTransparentMarble.class, new ClassLoaderResolverImpl());
        
        mgr.getSubclassesForClass(JBase.class.getName(), false);
        Set test = getSet(mgr.getSubclassesForClass(JBase.class.getName(), false));
        assertTrue(test.contains(JBowl.class.getName()));
        assertTrue(test.contains(JMarble.class.getName()));
        
        test = getSet(mgr.getSubclassesForClass(JMarble.class.getName(), false));
        assertTrue(test.contains(JSpottedMarble.class.getName()));
        assertTrue(test.contains(JTransparentMarble.class.getName()));
        
        test = getSet(mgr.getSubclassesForClass(JBase.class.getName(), true));
        assertTrue(test.contains(JBowl.class.getName()));
        assertTrue(test.contains(JMarble.class.getName()));
        assertTrue(test.contains(JSpottedMarble.class.getName()));
        assertTrue(test.contains(JTransparentMarble.class.getName()));
    }

    private Set getSet(String[] thearray)
    {
        Set result = new HashSet();
        for (int i=0;i<thearray.length;i++)
        {
            result.add(thearray[i]);
        }
        return result;
    }
}