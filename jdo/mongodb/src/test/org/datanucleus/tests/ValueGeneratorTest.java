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

Contributors :
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.JDOHelper;
import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.tests.applicationid.AppIdStringGeneratorItem;
import org.jpox.samples.valuegeneration.AUIDGeneratorItem;
import org.jpox.samples.valuegeneration.IdentityStringGeneratorItem;
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
                    AUIDGeneratorItem.class,
                    UUIDHexGeneratorItem.class,
                    UUIDStringGeneratorItem.class,
                    UUIDGeneratorItem.class,
                    IdentityStringGeneratorItem.class,
                    AppIdStringGeneratorItem.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test the use of "auid" generator. This is for all datastores.
     */
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
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
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
    public void xxxtestUUIDStringGenerator()
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
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
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
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
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
     * Test the use of "identity" generator.
     */
    public void testIdentityGenerator()
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
                for (int i=0;i<4;i++)
                {
                    IdentityStringGeneratorItem item = new IdentityStringGeneratorItem("First item");
                    pm.makePersistent(item);
                    pm.flush();
                    idSet.add(JDOHelper.getObjectId(item));
                }

                tx.commit();
                assertEquals("Number of unique ids persisted is wrong", 4, idSet.size());
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

                Iterator idIter = idSet.iterator();
                while (idIter.hasNext())
                {
                    Object id = idIter.next();
                    IdentityStringGeneratorItem obj = (IdentityStringGeneratorItem) pm.getObjectById(id);
                    assertEquals("Incorrect name", "First item", obj.getName());
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                HashSet idSetCopy = new HashSet(idSet);

                // Retrieve the items
                Query q = pm.newQuery(pm.getExtent(IdentityStringGeneratorItem.class,true));
                Collection c=(Collection)q.execute();

                // Check on the number of items
                assertTrue("Number of IdentityStringGeneratorItem's retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 

                Iterator<IdentityStringGeneratorItem> iter = c.iterator();
                while (iter.hasNext())
                {
                    IdentityStringGeneratorItem item = iter.next();
                    Object id = JDOHelper.getObjectId(item);
                    if (idSetCopy.contains(id))
                    {
                        idSetCopy.remove(id);
                    }
                }

                tx.commit();

                assertEquals("Wrong number of different IDs", 4, idSet.size());
                assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
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
            clean(IdentityStringGeneratorItem.class);
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
                idClass = JDOHelper.getObjectId(item).getClass();
                
                tx.commit();
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
     * Test the use of "identity" generator for application-identity.
     */
    public void testAppIdentityGenerator()
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
                for (int i=0;i<4;i++)
                {
                    AppIdStringGeneratorItem item = new AppIdStringGeneratorItem("First item");
                    pm.makePersistent(item);
                    pm.flush();
                    idSet.add(JDOHelper.getObjectId(item));
                }

                tx.commit();
                assertEquals("Number of unique ids persisted is wrong", 4, idSet.size());
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

                Iterator idIter = idSet.iterator();
                while (idIter.hasNext())
                {
                    Object id = idIter.next();
                    AppIdStringGeneratorItem obj = (AppIdStringGeneratorItem) pm.getObjectById(id);
                    assertEquals("Incorrect name", "First item", obj.getName());
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

            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();

                HashSet idSetCopy = new HashSet(idSet);

                // Retrieve the items
                Query q = pm.newQuery(pm.getExtent(AppIdStringGeneratorItem.class,true));
                Collection c=(Collection)q.execute();

                // Check on the number of items
                assertTrue("Number of AppIdStringGeneratorItems retrieved is incorrect (" + c.size() + ") : should have been 4",c.size() == 4); 

                Iterator<AppIdStringGeneratorItem> iter = c.iterator();
                while (iter.hasNext())
                {
                    AppIdStringGeneratorItem item = iter.next();
                    Object id = JDOHelper.getObjectId(item);
                    if (idSetCopy.contains(id))
                    {
                        idSetCopy.remove(id);
                    }
                }

                tx.commit();

                assertEquals("Wrong number of different IDs", 4, idSet.size());
                assertTrue("Retrieved IDs did not match created IDs", 0 == idSetCopy.size());
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
            clean(AppIdStringGeneratorItem.class);
        }
    }
}