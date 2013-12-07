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

import java.net.URI;
import java.util.Collection;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.uri.URIHolder;

/**
 * Tests for SCO mutable type java.net.URI.
 */
public class URITest extends AbstractTypeTestCase
{
    
    public URITest()
    {
        super("URITest");
    }
    
    public URITest(String name)
    {
        super(name);
    }

    /**
     * Test for querying of URI fields.
     * @throws Exception
     */
    public void testQuery()
    throws Exception
    {
        try
        {
            PersistenceManager pm = pmf.getPersistenceManager();
            Transaction tx = pm.currentTransaction();
            try
            {
                tx.begin();
                pm.makePersistent(getOneObject());
                pm.makePersistent(getOneObject());
                pm.makePersistent(getOneObject());
                URIHolder uri = new URIHolder();
                uri.setKey(URI.create("http://www.com/"+System.currentTimeMillis() + "_" + counter++));
                uri.setUri(URI.create("http://www.jpox.org/"));
                pm.makePersistent(uri);
                pm.flush();

                Query q = pm.newQuery(getSimpleClass(),"uri == p");
                q.declareImports("import java.net.URI");
                q.declareParameters("URI p");
                Collection c = (Collection) q.execute(uri.getUri());
                assertEquals(1,c.size());
                assertEquals("http://www.jpox.org/",((URIHolder)c.iterator().next()).getUri().toString());
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
            clean(URIHolder.class);
        }
    }

    URIHolder uri;
    static long counter = 1;

    protected Class getSimpleClass()
    {
        return URIHolder.class;
    }

    protected Object getOneObject()
    {
        uri = new URIHolder();
        uri.setUri(URI.create("http://www.com/"+System.currentTimeMillis() + "_" + counter));
        uri.getStrings().add("http://www.jpox.org/" + counter);
        counter++;

        URIHolder u = new URIHolder();
        int rnd = new Random().nextInt();
        u.setKey(URI.create("http://www.com/"+ rnd + "_" + counter));
        u.setUri(URI.create(uri.getUri().toString()));
        u.getStrings().add(uri.getStrings().get(0));
        return u;
    }

    protected void assertCorrectValues(Object obj)
    {
        URIHolder u = (URIHolder) obj;
        assertEquals(uri.getUri().toString(), u.getUri().toString());
        assertEquals(uri.getStrings().size(), u.getStrings().size());
        for (int index = uri.getStrings().size(); --index >= 0; )
        {
            assertEquals(uri.getStrings().get(index), u.getStrings().get(index));
        }
    }

    protected void changeObject(Object obj)
    {
        URIHolder u = (URIHolder) obj;
        u.setUri(URI.create("http://www.com/"+System.currentTimeMillis() + "_" + counter));
        u.getStrings().add("http://www.jpox.org/" + counter);
        counter++;

        //update local uri
        uri.setUri(URI.create(u.getUri().toString()));
        uri.getStrings().add(u.getStrings().get(u.getStrings().size() - 1));
    }

    protected void replaceObject(Object obj)
    {
        URIHolder u = (URIHolder) obj;
        u.setUri(URI.create("http://www.com/"+System.currentTimeMillis() + "_" + counter));
        u.getStrings().clear();
        u.getStrings().add("http://www.jpox.org/" + counter);
        counter++;

        //update local uri
        uri.setUri(URI.create(u.getUri().toString()));
        uri.getStrings().clear();
        uri.getStrings().add(u.getStrings().get(0));
    }

    protected int getNumberOfMutabilityChecks()
    {
        return 0;
    }
}