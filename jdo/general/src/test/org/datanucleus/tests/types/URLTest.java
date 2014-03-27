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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.samples.types.url.URLHolder;

/**
 * Tests for SCO type java.net.URL.
 */
public class URLTest extends AbstractTypeTestCase
{

    public URLTest()
    {
        super("URLTest");
    }

    public URLTest(String name)
    {
        super(name);
    }

    public void testQuery() throws Exception
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
                URLHolder url = new URLHolder();
                url.setUrl(new URL("http://www.jpox.org/"));
                pm.makePersistent(url);
                pm.flush();

                Query q = pm.newQuery(getSimpleClass(), "url == p");
                q.declareImports("import java.net.URL");
                q.declareParameters("URL p");
                Collection c = (Collection) q.execute(url.getUrl());
                assertEquals(1, c.size());
                assertEquals("http://www.jpox.org/", ((URLHolder) c.iterator().next()).getUrl().toString());
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
            clean(URLHolder.class);
        }
    }

    URLHolder url;
    static long counter = 1;

    protected Class getSimpleClass()
    {
        return URLHolder.class;
    }

    protected Object getOneObject()
    {
        url = new URLHolder();
        try
        {
            url.setUrl(new URL("http://www.com/" + System.currentTimeMillis() + "_" + counter));
            counter++;

            URLHolder u = new URLHolder();
            u.setUrl(new URL(url.getUrl().toString()));
            return u;
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    protected void assertCorrectValues(Object obj)
    {
        URLHolder u = (URLHolder) obj;
        assertEquals(url.getUrl().toString(), u.getUrl().toString());
    }

    protected void changeObject(Object obj)
    {
        URLHolder u = (URLHolder) obj;
        try
        {
            u.setUrl(new URL("http://www.com/" + System.currentTimeMillis() + "_" + counter));
            counter++;

            // update local uri
            url.setUrl(new URL(u.getUrl().toString()));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    protected void replaceObject(Object obj)
    {
        URLHolder u = (URLHolder) obj;
        try
        {
            u.setUrl(new URL("http://www.com/" + System.currentTimeMillis() + "_" + counter));
            counter++;

            // update local uri
            url.setUrl(new URL(u.getUrl().toString()));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    protected int getNumberOfMutabilityChecks()
    {
        return 0;
    }
}