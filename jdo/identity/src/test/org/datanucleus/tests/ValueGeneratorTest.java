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

Contributors :
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.jdo.JDOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.valuegeneration.AUIDGeneratorItem;
import org.jpox.samples.valuegeneration.IdentityGeneratorItem;
import org.jpox.samples.valuegeneration.IdentityGeneratorItemContainer;
import org.jpox.samples.valuegeneration.IdentityGeneratorItemNoField;
import org.jpox.samples.valuegeneration.IdentityGeneratorItemSub1;
import org.jpox.samples.valuegeneration.IdentityGeneratorItemSub2;
import org.jpox.samples.valuegeneration.MaxGeneratorItem;
import org.jpox.samples.valuegeneration.MixedGeneratorItem;
import org.jpox.samples.valuegeneration.MixedGeneratorItemSub;
import org.jpox.samples.valuegeneration.SequenceGeneratorItem;
import org.jpox.samples.valuegeneration.TableGeneratorItem;
import org.jpox.samples.valuegeneration.UUIDGeneratorItem;
import org.jpox.samples.valuegeneration.UUIDHexGeneratorItem;
import org.jpox.samples.valuegeneration.UUIDStringGeneratorItem;

/**
 * Test the use of all forms of Id generators.
 * If a datastore doesn't support a particular id generator, the test is not run.
 */
public class ValueGeneratorTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    public ValueGeneratorTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    TableGeneratorItem.class,
                    AUIDGeneratorItem.class,
                    UUIDHexGeneratorItem.class,
                    UUIDStringGeneratorItem.class,
                    UUIDGeneratorItem.class,
                    MaxGeneratorItem.class,
                    MixedGeneratorItem.class,
                    MixedGeneratorItemSub.class
                }
            );
            
            if (storeMgr.supportsValueStrategy("sequence"))
            {
                addClassesToSchema(new Class[]
                    {
                        SequenceGeneratorItem.class
                    }
                );
            }
            if (storeMgr.supportsValueStrategy("identity"))
            {
                addClassesToSchema(new Class[]
                    {
                        IdentityGeneratorItem.class,
                        IdentityGeneratorItemContainer.class,
                        IdentityGeneratorItemNoField.class,
                        IdentityGeneratorItemSub1.class,
                        IdentityGeneratorItemSub2.class
                    }
                );
            }
            initialised = true;
        }
    }

    /**
     * Test the use of TablePoidGenerator. This is for all datastores.
     * Note : if this impacts on subsequent tests, comment it out.
     */
    public void testTablePoidMultiThreadedNewPM()
    throws Exception
    {
        try
        {
            Thread[] t = new Thread[20];
            TableInsert[] s = new TableInsert[t.length];
            for (int i = 0; i < t.length; i++)
            {
                s[i] = new TableInsert();
                t[i] = new Thread(s[i]);
            }
            for (int i = 0; i < t.length; i++)
            {
                t[i].start();
            }
            for (int i = 0; i < t.length; i++)
            {
                t[i].join();
            }
            for (int i = 0; i < t.length; i++)
            {
                assertEquals(s[i].getErrors().toString(), 0, s[i].getErrors().size());
            }
        }
        finally
        {
            clean(TableGeneratorItem.class);
        }
    }

    private static class TableInsert implements Runnable
    {
        List errors = new ArrayList();
        public TableInsert()
        {
        }
        public void run()
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            for (int i=0; i<5; i++)
            {
                try
                {
                    tx.begin();

                    // Create a few objects.
                    TableGeneratorItem item=null;
                    item = new TableGeneratorItem("First item");
                    pm.makePersistent(item);
                    item = new TableGeneratorItem("Second item");
                    pm.makePersistent(item);
                    item = new TableGeneratorItem("Third item");
                    pm.makePersistent(item);
                    item = new TableGeneratorItem("Fourth item");
                    pm.makePersistent(item);

                    tx.commit();
                }
                catch (JDOException e)
                {
                    errors.add(e);
                    LOG.error("Exception while performing insert in multi-thread test", e);
                }
                finally
                {
                    if (tx.isActive())
                    {
                        tx.rollback();
                    }
                }
            }
        }
        
        public List getErrors()
        {
            return errors;
        }
    }

    /**
     * Test the use of "table" generator. This is for all datastores.
     */
    public void testTableGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                TableGeneratorItem item=null;
                item = new TableGeneratorItem("First item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Second item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Third item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                item = new TableGeneratorItem("Fourth item");
                pm.makePersistent(item);
                idSet.add(new Integer(item.getIdentifier()));
                
                tx.commit();
                idClass = JDOHelper.getObjectId(item).getClass();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"table\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q = pm.newQuery(pm.getExtent(TableGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertEquals("Number of TableGeneratorItem's retrieved is incorrect", 4, c.size());
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (TableGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(new Integer(((TableGeneratorItem)o).getIdentifier()));
                    }
                }
                
                tx.commit();
                
                if (TableGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any created data
            clean(TableGeneratorItem.class);
        }
    }

    /**
     * Test the use of "auid" generator. This is for all datastores.
     **/
    public void testAUIDGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                AUIDGeneratorItem item=null;
                
                item = new AUIDGeneratorItem("First item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new AUIDGeneratorItem("Second item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new AUIDGeneratorItem("Third item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new AUIDGeneratorItem("Fourth item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                
                tx.commit();
                idClass = JDOHelper.getObjectId(item).getClass();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"auid\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q=pm.newQuery(pm.getExtent(AUIDGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertTrue("Number of AUIDGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 
                
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (AUIDGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(((AUIDGeneratorItem)o).getIdentifier());
                    }
                }
                
                tx.commit();
                
                if (AUIDGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            clean(AUIDGeneratorItem.class);
        }
    }

    /**
     * Test the use of "uuid-string" generator. This is for all datastores.
     */
    public void testUUIDStringGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                UUIDStringGeneratorItem item=null;
                item = new UUIDStringGeneratorItem("First item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDStringGeneratorItem("Second item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDStringGeneratorItem("Third item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDStringGeneratorItem("Fourth item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                
                tx.commit();
                idClass = JDOHelper.getObjectId(item).getClass();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"uuid-string\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q = pm.newQuery(pm.getExtent(UUIDStringGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertTrue("Number of UUIDStringGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 
                
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (UUIDStringGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(((UUIDStringGeneratorItem)o).getIdentifier());
                    }
                }
                
                tx.commit();
                
                if (UUIDStringGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any created data
            clean(UUIDStringGeneratorItem.class);
        }
    }

    /**
     * Test the use of "uuid-hex" generator. This is for all datastores.
     **/
    public void testUUIDHexGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                UUIDHexGeneratorItem item=null;
                
                item = new UUIDHexGeneratorItem("First item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDHexGeneratorItem("Second item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDHexGeneratorItem("Third item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                item = new UUIDHexGeneratorItem("Fourth item");
                pm.makePersistent(item);
                idSet.add(item.getIdentifier());
                
                tx.commit();
                idClass = JDOHelper.getObjectId(item).getClass();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"uuid-hex\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q=pm.newQuery(pm.getExtent(UUIDHexGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertTrue("Number of UUIDHexGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 
                
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (UUIDHexGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(((UUIDHexGeneratorItem)o).getIdentifier());
                    }
                }
                
                tx.commit();
                
                if (UUIDHexGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any created data
            clean(UUIDHexGeneratorItem.class);
        }
    }

    /**
     * Test the use of "uuid" generator. This is for all datastores.
     */
    public void testUUIDGenerator()
    throws Exception
    {
        try
        {
            HashSet idSet = new HashSet();
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                UUIDGeneratorItem item=null;

                item = new UUIDGeneratorItem("First item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(pm.getObjectId(item));
                
                item = new UUIDGeneratorItem("Second item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(pm.getObjectId(item));
                
                item = new UUIDGeneratorItem("Third item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(pm.getObjectId(item));

                item = new UUIDGeneratorItem("Fourth item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(pm.getObjectId(item));
                
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"uuid\" generator test " + e.getMessage());
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
                HashSet idSetCopy = new HashSet(idSet);

                Query q = pm.newQuery(pm.getExtent(UUIDGeneratorItem.class, true));
                Collection c=(Collection)q.execute();
                assertEquals("Number of items is wrong", 4, c.size()); 

                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    idSetCopy.remove(JDOHelper.getObjectId(o));
                }

                tx.commit();

                assertEquals("Wrong number of different IDs", 4, idSet.size());
                assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
            }
            catch (JDOUserException ue)
            {
                fail("Exception thrown during test " + ue.getMessage());
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
            // Clean out any created data
            clean(UUIDGeneratorItem.class);
        }
    }

    /**
     * Test the use of "max" generator. This is for all datastores.
     **/
    public void testMaxGenerator()
    throws Exception
    {
        try
        {
            // must use PM connection, otherwise would cause deadlock
            getConfigurationForPMF(pmf).setProperty("datanucleus.valuegeneration.transactionAttribute", "UsePM");

            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                MaxGeneratorItem item=null;
                
                item = new MaxGeneratorItem("First item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new MaxGeneratorItem("Second item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new MaxGeneratorItem("Third item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new MaxGeneratorItem("Fourth item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"max\" generator test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                getConfigurationForPMF(pmf).setProperty("datanucleus.valuegeneration.transactionAttribute", "New");
                pm.close();
            }
            
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q=pm.newQuery(pm.getExtent(MaxGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertTrue("Number of MaxGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 
                
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (MaxGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(new Integer(((MaxGeneratorItem)o).getIdentifier()));
                    }
                }
                
                tx.commit();
                
                if (MaxGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any data created
            clean(MaxGeneratorItem.class);
        }
    }

    /**
     * Test the use of "sequence" generator. Run if supported by the datastore.
     */
    public void testSequenceGenerator()
    throws Exception
    {
        if (!storeMgr.supportsValueStrategy("sequence"))
        {
            // Lets just say it passed :-)
            return;
        }

        try
        {
            HashSet idSet = new HashSet();
            Class idClass = null;
            
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                tx.begin();
                
                // Create a few objects.
                SequenceGeneratorItem item=null;
                
                item = new SequenceGeneratorItem("First item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new SequenceGeneratorItem("Second item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new SequenceGeneratorItem("Third item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                item = new SequenceGeneratorItem("Fourth item");
                pm.makePersistent(item);
                pm.flush();
                idSet.add(new Integer(item.getIdentifier()));
                
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in \"sequence\" generator test " + e.getMessage());
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
                
                HashSet idSetCopy = new HashSet(idSet);
                
                // Retrieve the items
                Query q=pm.newQuery(pm.getExtent(SequenceGeneratorItem.class,true));
                Collection c=(Collection)q.execute();
                
                // Check on the number of items
                assertTrue("Number of SequenceGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 
                
                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    Object o=iter.next();
                    if (SequenceGeneratorItem.Oid.class.equals(idClass))
                    {
                        idSetCopy.remove(new Integer(((SequenceGeneratorItem)o).getIdentifier()));
                    }
                }
                
                tx.commit();
                
                if (SequenceGeneratorItem.Oid.class.equals(idClass))
                {
                    assertEquals("Wrong number of different IDs", 4, idSet.size());
                    assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
                }
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any created data
            clean(SequenceGeneratorItem.class);
        }
    }

    /**
     * Test the use of "identity" generator.
     * Only for datastores that support autoassign/identity strategies.
     */
    public void testIdentityGenerator()
    throws Exception
    {
        if (!storeMgr.supportsValueStrategy("identity"))
        {
            // Adapter doesnt support autoincrement so lets just say it passed :-)
            return;
        }

        HashSet idSet = new HashSet();
        Class idClass = null;
        
        PersistenceManager pm=pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
 
            // Create a few objects.
            IdentityGeneratorItem item=null;

            item = new IdentityGeneratorItem("First item");
            pm.makePersistent(item);
            pm.flush();
            idSet.add(new Integer(item.getIdentifier()));

            item = new IdentityGeneratorItem("Second item");
            pm.makePersistent(item);
            pm.flush();
            idSet.add(new Integer(item.getIdentifier()));

            item = new IdentityGeneratorItem("Third item");
            pm.makePersistent(item);
            pm.flush();
            idSet.add(new Integer(item.getIdentifier()));

            item = new IdentityGeneratorItem("Fourth item");
            pm.makePersistent(item);
            pm.flush();
            idSet.add(new Integer(item.getIdentifier()));

            idClass = JDOHelper.getObjectId(item).getClass();

            tx.commit();
        }
        catch (JDOUserException e)
        {
            fail("Exception thrown during insert of objects with \"identity\" generator : " + e.getMessage());
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

            HashSet idSetCopy = new HashSet(idSet);

            // Retrieve the items
            Query q=pm.newQuery(pm.getExtent(IdentityGeneratorItem.class,true));
            Collection c=(Collection)q.execute();

            // Check on the number of items
            assertTrue("Number of IdentityGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 

            Iterator iter = c.iterator();
            while (iter.hasNext())
            {
                Object o=iter.next();
                if (IdentityGeneratorItem.Oid.class.equals(idClass))
                {
                    idSetCopy.remove(new Integer(((IdentityGeneratorItem)o).getIdentifier()));
                }
            }

            tx.commit();

            if (IdentityGeneratorItem.Oid.class.equals(idClass))
            {
                assertEquals("Wrong number of different IDs", 4, idSet.size());
                assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
            }
        }
        catch (JDOUserException ue)
        {
            assertTrue("Exception thrown during test " + ue.getMessage(),false);
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }

            pm.close();
            
            clean(IdentityGeneratorItem.class);
        }
    }

    /**
     * Test the use of AutoIncrementGenerator, where there are no other fields in the class.
     * Only for use with adapters supporting autoassign/identity strategies.
     **/
    public void testAutoIncrementWithNoOtherFields()
    throws Exception
    {
        if (!storeMgr.supportsValueStrategy("identity"))
        {
            // Adapter doesnt support autoincrement so let's just say it passed :-)
            return;
        }

        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Create a few objects so we test the persistence
                IdentityGeneratorItemNoField item = null;
                item = new IdentityGeneratorItemNoField();
                pm.makePersistent(item);
                item = new IdentityGeneratorItemNoField();
                pm.makePersistent(item);

                tx.commit();
            }
            catch (Exception e)
            {
                fail("Exception thrown during insert of objects with \"identity\" with no other fields " + e.getMessage());
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
            clean(IdentityGeneratorItemNoField.class);
        }
    }

    /**
     * Test of autoincrementing an inheritance tree. The base class will be autoincremented
     * and subclasses will just take the id from the base.
     * Only for use with adapters supporting autoassign/identity strategies.
     */
    public void testAutoIncrementWithInheritance()
    throws Exception
    {
        if (!storeMgr.supportsValueStrategy("identity"))
        {
            // Adapter doesnt support autoincrement so let's just say it passed :-)
            return;
        }

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try
        {
            tx.begin();
            IdentityGeneratorItem item1 = new IdentityGeneratorItem("First");
            IdentityGeneratorItem item2 = new IdentityGeneratorItem("Second");
            IdentityGeneratorItem item3 = new IdentityGeneratorItem("Third");
            IdentityGeneratorItemSub1 sub1 = new IdentityGeneratorItemSub1("Fourth", "A", "B");
            IdentityGeneratorItemSub2 sub2 = new IdentityGeneratorItemSub2("Fifth", "C", "D");
            IdentityGeneratorItemContainer c = new IdentityGeneratorItemContainer(new IdentityGeneratorItem[]{item1, item2, item3, sub1, sub2});
            pm.makePersistent(c);
            tx.commit();

            tx.begin();
            Query q = pm.newQuery(IdentityGeneratorItem.class);
            List results = (List)q.execute();
            assertTrue(results.size() == 5);
            boolean sub1Exists = false;
            boolean sub2Exists = false;
            Iterator iter = results.iterator();
            while (iter.hasNext())
            {
                IdentityGeneratorItem item = (IdentityGeneratorItem)iter.next();
                if (item.getName().equals("Fourth") && item instanceof IdentityGeneratorItemSub1)
                {
                    sub1Exists = true;
                }
                else if (item.getName().equals("Fifth") && item instanceof IdentityGeneratorItemSub2)
                {
                    sub2Exists = true;
                }
            }
            assertTrue("Sub1 object not found", sub1Exists);
            assertTrue("Sub2 object not found", sub2Exists);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();

            // Clean out any created data
            clean(IdentityGeneratorItemContainer.class);
            clean(IdentityGeneratorItemSub1.class);
            clean(IdentityGeneratorItemSub2.class);
            clean(IdentityGeneratorItem.class);
        }
    }

    /**
     * Test the use of multiple "value-strategy" fields with inheritance
     */
    public void testMixedGeneratorInherited()
    throws Exception
    {
        try
        {
            PersistenceManager pm=pmf.getPersistenceManager();
            Transaction tx=pm.currentTransaction();
            try
            {
                getConfigurationForPMF(pmf).setProperty("datanucleus.valuegeneration.transactionAttribute", "UsePM");
                tx.begin();

                // Create a few objects.
                MixedGeneratorItemSub item = null;
                item = new MixedGeneratorItemSub();
                pm.makePersistent(item);
                item = new MixedGeneratorItemSub();
                pm.makePersistent(item);
                item = new MixedGeneratorItemSub();
                pm.makePersistent(item);
                item = new MixedGeneratorItemSub();
                pm.makePersistent(item);

                tx.commit();
                JDOHelper.getObjectId(item).getClass();
            }
            catch (JDOUserException e)
            {
                fail("Exception thrown during insert of objects in mixed generators test " + e.getMessage());
            }
            finally
            {
                if (tx.isActive())
                {
                    tx.rollback();
                }
                getConfigurationForPMF(pmf).setProperty("datanucleus.valuegeneration.transactionAttribute", "New");
                pm.close();
            }

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                // Retrieve the items
                Query q=pm.newQuery(pm.getExtent(MixedGeneratorItemSub.class,true));
                Collection c=(Collection)q.execute();

                // Check on the number of items
                assertTrue("Number of MixedGeneratorItemSub's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 

                Iterator iter = c.iterator();
                while (iter.hasNext())
                {
                    iter.next();
                }
                
                tx.commit();
            }
            catch (JDOUserException ue)
            {
                assertTrue("Exception thrown during test " + ue.getMessage(),false);
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
            // Clean out any data we have created
            clean(MixedGeneratorItemSub.class);
        }
    }
}