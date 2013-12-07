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

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.datanucleus.tests.JDOPersistenceTestCase;
import org.jpox.samples.types.stringbuffer.StringBufferHolder;

/**
 * Tests for SCO mutable type java.util.StringBuffer.
 * StringBuffer is a final class and does not allow subclassing it
 * for implementing the SCO wrapper class
 * @version $Revision: 1.1 $
 */
public class StringBufferTest extends JDOPersistenceTestCase
{
    private static boolean initialised = false;

    /**
     * @param name
     */
    public StringBufferTest(String name)
    {
        super(name);
        if (!initialised)
        {
            addClassesToSchema(new Class[]
                {
                    StringBufferHolder.class
                }
            );
            initialised = true;
        }
    }

    /**
     * Test for the persistence and retrieval of a StringBuffer mutable SCO type.
     * @throws Exception
     */
    public void testBasicPersistence()
    throws Exception
    {
        try
        {
            StringBufferHolder container = new StringBufferHolder();
            container.appendText("text1");
            
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(container);
                id = JDOHelper.getObjectId(container);
                
                StringBufferHolder container2 = (StringBufferHolder) pm.getObjectById(id, true);
                pm.refresh(container2);
                assertEquals("text1", container2.getText());
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
                StringBufferHolder container2 = (StringBufferHolder) pm.getObjectById(id, true);
                assertEquals("text1", container2.getText());
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

            // Check the mutability of text
            // Note that "appendText" actually does a replace of the StringBuffer since we don't support updates
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
            try
            {
                tx.begin();
                StringBufferHolder container2 = (StringBufferHolder) pm.getObjectById(id, true);
                assertNotNull("StringBufferContainer class had a null text but should have had a value", container2.getStringBuffer());
                assertEquals("text1", container2.getText());
                container2.appendText("ttt");
                tx.commit();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                LOG.error(e);
                fail("Error updating the text of the StringBuffer : " + e.getMessage());
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
                StringBufferHolder container2 = (StringBufferHolder) pm.getObjectById(id, true);
                assertEquals("text1ttt", container2.getText());
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
            clean(StringBufferHolder.class);
        }
    }

    /**
     * Test for the persistence and retrieval of a StringBuffer mutable SCO type.
     * @throws Exception
     */
    public void testQuery()
    throws Exception
    {
        try
        {
            StringBufferHolder container = new StringBufferHolder();
            container.appendText("text1");
            
            Object id;
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                
                pm.makePersistent(container);
                id = JDOHelper.getObjectId(container);
                
                StringBufferHolder container2 = (StringBufferHolder) pm.getObjectById(id, true);
                pm.refresh(container2);
                assertEquals("text1", container2.getText());
                Query q = pm.newQuery(StringBufferHolder.class);
                q.setOrdering("sb ascending");
                q.execute();
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
            clean(StringBufferHolder.class);
        }
    }
}