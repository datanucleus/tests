/**********************************************************************
Copyright (c) 2011 Erik Bengtson and others. All rights reserved.
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
2011 Andy Jefferson - added subclass test and tearDown
    ...
**********************************************************************/
package org.datanucleus.tests;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.datanucleus.api.rest.RestServlet;
import org.datanucleus.api.rest.orgjson.JSONArray;
import org.datanucleus.api.rest.orgjson.JSONObject;
import org.datanucleus.samples.ClassUsingDatastoreId;
import org.datanucleus.samples.ClassWithSimpleMap;
import org.datanucleus.samples.ClassWithStringCollection;
import org.datanucleus.samples.ClassWithValueStrategy;
import org.datanucleus.util.NucleusLogger;
import org.jpox.samples.embedded.Network;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;
import org.jpox.samples.one_many.map_fk.MapFKHolder;
import org.jpox.samples.one_many.map_fk.MapFKValue;
import org.jpox.samples.one_many.unidir.DesktopComputer;
import org.jpox.samples.one_many.unidir.LaptopComputer;
import org.jpox.samples.one_many.unidir.Office;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Test of basic REST capabilities using simple models.
 * TODO Change so we create the server at construction and stop it after the last test (i.e only one PMF)
 */
public class BasicTest extends TestCase
{
    /** Log for unit testing. */
    protected static final NucleusLogger LOG = NucleusLogger.getLoggerInstance("DataNucleus.Test");

    public static int PORT = 8765;

    Server server = null;

    public BasicTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        LOG.info("********** " + toString() + " [setUp] **********");
        super.setUp();
        server = new Server(PORT);
        Context root = new Context(server,"/",Context.SESSIONS);
        ServletHolder holder = new ServletHolder(new RestServlet());
        holder.setInitParameter("persistence-context", "h2-backend"); // TODO Set this based on which datastore in use
        root.addServlet(holder, "/dn/*");
        server.start();
    }

    protected void tearDown() throws Exception
    {
        LOG.info("********** " + toString() + " [tearDown] **********");
        server.stop();
        server.destroy();
    }

    public void testPersist() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244744";
        int personNum = 1;
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");

            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));
        
            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                get.setMethod("GET");
                obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                delete.setMethod("DELETE");
                JSONObject obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testPersistAndQuery() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244744";
        int personNum = 1;
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));
        
            try
            {
                ContentExchange get = new ContentExchange();
                String encodedQuery = URLEncoder.encode("SELECT FROM " + Person.class.getName(), "UTF-8");
                get.setURL("http://localhost:"+PORT+"/dn/jdoql?" + encodedQuery);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                JSONArray arr = new JSONArray(get.getResponseContent());
                assertEquals(1, arr.length());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                delete.setMethod("DELETE");
                JSONObject obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testPersistAndJPQLQuery() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244744";
        int personNum = 1;
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));
        
            try
            {
                ContentExchange get = new ContentExchange();
                String encodedQuery = URLEncoder.encode("SELECT p FROM " + Person.class.getName() + " p", "UTF-8");
                get.setURL("http://localhost:"+PORT+"/dn/jpql?" + encodedQuery);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                JSONArray arr = new JSONArray(get.getResponseContent());
                assertEquals(1, arr.length());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                delete.setMethod("DELETE");
                JSONObject obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testPersistSubclass() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244745";
        int personNum = 2;
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Employee.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            obj.put("salary", 123.45);
            obj.put("serialNo", 12345);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));
        
            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Employee.class.getName());
                get.setMethod("GET");
                obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Employee.class.getName());
                delete.setMethod("DELETE");
                JSONObject obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testPersistWithValueStrategy() throws IOException
    {
        HttpClient client = new HttpClient();
        String idValue = null;
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+ClassWithValueStrategy.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject(); // No need to set "id" since is generated
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            idValue = obj.getString("id");

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+ClassWithValueStrategy.class.getName() + "/" + idValue);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+ClassWithValueStrategy.class.getName() + "/" + idValue);
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testUpdate() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244744";
        int personNum = 1;
        try
        {
            // Persist the object
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));
        
            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                get.setMethod("GET");
                obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            // Update the object
            post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("PUT");
            obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",personNum);
            obj.put("age",15);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(personNum,obj.getLong("personNum"));

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                get.setMethod("GET");
                obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
                assertEquals(15, obj.getInt("age"));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                delete.setMethod("DELETE");
                JSONObject obj = new JSONObject();
                obj.put("globalNum",globalNum);
                obj.put("personNum",personNum);
                delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testDatastoreIdPersistUpdate() throws IOException
    {
        HttpClient client = new HttpClient();
        long idValue = 0;
        try
        {
            // Persist the object
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+ClassUsingDatastoreId.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            String name = "First Name";
            obj.put("name", name);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(name, obj.getString("name"));
            idValue = obj.getLong("_id");

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+ClassUsingDatastoreId.class.getName() + "/" + idValue);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            // Update the object
            post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+ClassUsingDatastoreId.class.getName() + "/" + idValue);
            post.setMethod("PUT");
            obj = new JSONObject();
            obj.put("name", "Second Name");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals("Second Name", obj.getString("name"));

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+ClassUsingDatastoreId.class.getName() + "/" + idValue);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
                assertEquals("Second Name", obj.getString("name"));
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+ClassUsingDatastoreId.class.getName() + "/" + idValue);
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testBulkDelete() throws IOException
    {
        HttpClient client = new HttpClient();
        String globalNum ="global:1786244744";
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",1);
            obj.put("lastName","lastName");
            obj.put("age",0);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum,obj.getString("globalNum"));
            assertEquals(1, obj.getLong("personNum"));

            post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            post.setMethod("POST");
            obj = new JSONObject();
            obj.put("globalNum",globalNum);
            obj.put("personNum",2);
            obj.put("lastName","lastName");
            obj.put("age",15);
            obj.put("emailAddress","email");
            obj.put("firstName","firstName");
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(globalNum, obj.getString("globalNum"));
            assertEquals(2, obj.getLong("personNum"));

            // Do bulk delete of both objects
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }

            // Check results
            try
            {
                ContentExchange get = new ContentExchange();
                String encodedQuery = URLEncoder.encode("SELECT FROM " + Person.class.getName(), "UTF-8");
                get.setURL("http://localhost:"+PORT+"/dn/jdoql?" + encodedQuery);
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                JSONArray arr = new JSONArray(get.getResponseContent());
                assertEquals(0, arr.length());
            }
            catch (Exception e)
            {
                try
                {
                    ContentExchange delete = new ContentExchange();
                    delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                    delete.setMethod("DELETE");
                    obj = new JSONObject();
                    obj.put("globalNum",globalNum);
                    obj.put("personNum", 1);
                    delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                    client.send(delete);
                    delete.waitForDone();
                    delete = new ContentExchange();
                    delete.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
                    delete.setMethod("DELETE");
                    obj = new JSONObject();
                    obj.put("globalNum",globalNum);
                    obj.put("personNum", 2);
                    delete.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
                    client.send(delete);
                    delete.waitForDone();
                }
                catch (Exception e2)
                {
                    fail(e2.getMessage());
                }
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
        }
    }

    /**
     * Example using an embedded collection field, with persist then get then delete
     * @throws IOException
     */
    public void testPersistEmbeddedCollection() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Network.class.getName());
            post.setMethod("POST");
            JSONObject obj = new JSONObject();
            obj.put("id", 1);
            obj.put("name", "Home Network");
            Collection<JSONObject> devs = new HashSet<JSONObject>();
            JSONObject dev1 = new JSONObject();
            dev1.put("name", "Toaster");
            dev1.put("description", "Kitchen Toaster");
            devs.add(dev1);
            JSONArray jsonarr = new JSONArray(devs);
            obj.put("devices", jsonarr);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(1, obj.getLong("id"));

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Network.class.getName() + "/1?fetch=all");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
                assertEquals("Home Network", obj.getString("name"));

                Object devObjs = obj.get("devices");
                assertNotNull(devObjs);
                assertTrue(devObjs instanceof JSONArray);
                JSONArray devArr = (JSONArray)devObjs;
                assertEquals(1, devArr.length());
                Object devObj = devArr.get(0);
                assertTrue(devObj instanceof JSONObject);
                JSONObject dev = (JSONObject)devObj;
                assertEquals("Toaster", dev.getString("name"));
                assertEquals("Kitchen Toaster", dev.getString("description"));
            }
            catch (Exception e)
            {
                LOG.error("Exception in validate", e);
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in test", e);
            fail("Exception in test : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Network.class.getName() + "/1");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Test of 1-N unidir join table relation persistence/retrieval.
     * @throws IOException
     */
    public void testOneToManyJoin() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            // Persist Office plus 2 computers
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName());
            post.setMethod("POST");

            JSONObject obj = new JSONObject();
            obj.put("name", "Headquarters");
            Collection<JSONObject> computers = new HashSet<JSONObject>();
            JSONObject dev1 = new JSONObject();
            dev1.put("class", DesktopComputer.class.getName());
            dev1.put("id", 1);
            dev1.put("ipAddress", "192.168.1.2");
            dev1.put("operatingSystem", "Linux");
            dev1.put("numberOfProcessors", 4);
            computers.add(dev1);
            JSONObject dev2 = new JSONObject();
            dev2.put("class", LaptopComputer.class.getName());
            dev2.put("id", 2);
            dev2.put("ipAddress", "192.168.1.3");
            dev2.put("operatingSystem", "Windows");
            dev2.put("batteryLife", 5);
            dev2.put("numberOfPcmcia", 0);
            computers.add(dev2);
            JSONArray jsonarr = new JSONArray(computers);
            obj.put("computers", jsonarr);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());

            obj = new JSONObject(post.getResponseContent());
            assertEquals("Headquarters", obj.getString("name"));

            try
            {
                // Retrieve objects to check persistence
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName() + "/Headquarters?fetchGroup=all&maxFetchDepth=2");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());

                Object devObjs = obj.get("computers");
                assertNotNull(devObjs);
                assertTrue(devObjs instanceof JSONArray);
                JSONArray devArr = (JSONArray)devObjs;
                assertEquals(2, devArr.length());

                boolean laptopFound = false;
                boolean pcFound = false;
                for (int i=0;i<devArr.length();i++)
                {
                    Object devObj = devArr.get(i);
                    assertTrue(devObj instanceof JSONObject);
                    JSONObject dev = (JSONObject)devObj;
                    if (dev.getLong("id") == 1)
                    {
                        assertEquals(DesktopComputer.class.getName(), dev.getString("class"));
                        assertEquals("192.168.1.2", dev.getString("ipAddress"));
                        assertEquals("Linux", dev.getString("operatingSystem"));
                        assertEquals(4, dev.getInt("numberOfProcessors"));
                        pcFound = true;
                    }
                    else if (dev.getLong("id") == 2)
                    {
                        assertEquals(LaptopComputer.class.getName(), dev.getString("class"));
                        assertEquals("192.168.1.3", dev.getString("ipAddress"));
                        assertEquals("Windows", dev.getString("operatingSystem"));
                        assertEquals(5, dev.getLong("batteryLife"));
                        assertEquals(0, dev.getInt("numberOfPcmcia"));
                        laptopFound = true;
                    }
                }

                assertTrue(pcFound);
                assertTrue(laptopFound);
            }
            catch (Exception e)
            {
                LOG.error("Exception in validate", e);
                fail(e.getMessage());
            }

            try
            {
                // Retrieve Office and add new Computer
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName() + "/Headquarters?fetchGroup=all&maxFetchDepth=2");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());

                obj = new JSONObject(get.getResponseContent());
                Object devObjs = obj.get("computers");
                assertNotNull(devObjs);
                assertTrue(devObjs instanceof JSONArray);
                JSONArray devArr = (JSONArray)devObjs;
                Collection coll = new HashSet();
                for (int i=0;i<devArr.length();i++)
                {
                    coll.add(devArr.get(i));
                }
                JSONObject dev3 = new JSONObject();
                dev3.put("class", LaptopComputer.class.getName());
                dev3.put("id", 3);
                dev3.put("ipAddress", "192.168.1.4");
                dev3.put("operatingSystem", "Linux");
                dev3.put("batteryLife", 8);
                dev3.put("numberOfPcmcia", 0);
                coll.add(dev3);
                obj.put("computers", coll);

                post = new ContentExchange();
                post.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName()+"/Headquarters");
                post.setMethod("POST");
                post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

                client.start();
                client.send(post);
                post.waitForDone();

                //validate
                assertEquals(201, post.getResponseStatus());
                assertNotNull(post.getResponseContent());

            }
            catch (Exception e)
            {
                LOG.error("Exception in update", e);
                fail(e.getMessage());
            }

            try
            {
                // Retrieve objects to check persistence
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName() + "/Headquarters?fetchGroup=all&maxFetchDepth=2");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());

                Object devObjs = obj.get("computers");
                assertNotNull(devObjs);
                assertTrue(devObjs instanceof JSONArray);
                JSONArray devArr = (JSONArray)devObjs;
                assertEquals(3, devArr.length());

                boolean laptopFound = false;
                boolean laptop2Found = false;
                boolean pcFound = false;
                for (int i=0;i<devArr.length();i++)
                {
                    Object devObj = devArr.get(i);
                    assertTrue(devObj instanceof JSONObject);
                    JSONObject dev = (JSONObject)devObj;
                    if (dev.getLong("id") == 1)
                    {
                        assertEquals(DesktopComputer.class.getName(), dev.getString("class"));
                        assertEquals("192.168.1.2", dev.getString("ipAddress"));
                        assertEquals("Linux", dev.getString("operatingSystem"));
                        assertEquals(4, dev.getInt("numberOfProcessors"));
                        pcFound = true;
                    }
                    else if (dev.getLong("id") == 2)
                    {
                        assertEquals(LaptopComputer.class.getName(), dev.getString("class"));
                        assertEquals("192.168.1.3", dev.getString("ipAddress"));
                        assertEquals("Windows", dev.getString("operatingSystem"));
                        assertEquals(5, dev.getLong("batteryLife"));
                        assertEquals(0, dev.getInt("numberOfPcmcia"));
                        laptopFound = true;
                    }
                    else if (dev.getLong("id") == 3)
                    {
                        assertEquals(LaptopComputer.class.getName(), dev.getString("class"));
                        assertEquals("192.168.1.4", dev.getString("ipAddress"));
                        assertEquals("Linux", dev.getString("operatingSystem"));
                        assertEquals(8, dev.getLong("batteryLife"));
                        assertEquals(0, dev.getInt("numberOfPcmcia"));
                        laptop2Found = true;
                    }
                }

                assertTrue(pcFound);
                assertTrue(laptopFound);
                assertTrue(laptop2Found);
            }
            catch (Exception e)
            {
                LOG.error("Exception in validate", e);
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in persist", e);
            fail(e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+Office.class.getName() + "/Headquarters");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());

                delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+LaptopComputer.class.getName() + "/2");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());

                delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+DesktopComputer.class.getName() + "/1");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
                delete = new ContentExchange();

                delete.setURL("http://localhost:"+PORT+"/dn/"+LaptopComputer.class.getName() + "/3");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testClassWithNonPersistableCollection() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+ClassWithStringCollection.class.getName());
            post.setMethod("POST");

            JSONObject obj = new JSONObject();
            obj.put("id", 101);
            obj.put("name", "Name of Object1");
            Set<String> strings = new HashSet<String>();
            strings.add("FirstString");
            strings.add("SecondString");
            strings.add("ThirdString");
            obj.put("strings", strings);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(101, obj.getLong("id"));

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+ClassWithStringCollection.class.getName() + "/101?fetchGroup=all");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
                Object stringsObj = obj.get("strings");
                assertTrue(stringsObj instanceof JSONArray);
                JSONArray stringsArr = (JSONArray)stringsObj;
                assertEquals(3, stringsArr.length());
                boolean[] present = new boolean[3];
                present[0] = false;
                present[1] = false;
                present[2] = false;
                for (int i=0;i<stringsArr.length();i++)
                {
                    if (stringsArr.get(i).equals("FirstString"))
                    {
                        present[0] = true;
                    }
                    else if (stringsArr.get(i).equals("SecondString"))
                    {
                        present[1] = true;
                    }
                    else if (stringsArr.get(i).equals("ThirdString"))
                    {
                        present[2] = true;
                    }
                }
                for (int i=0;i<3;i++)
                {
                    assertTrue("String " + i + " is not present on retrieval", present[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception validating data", e);
                fail("Exception validating data : " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception persisting/checking data", e);
            fail("Exception in persist/check of data : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+ClassWithStringCollection.class.getName() + "/101");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    public void testClassWithNonPersistableMap() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+ClassWithSimpleMap.class.getName());
            post.setMethod("POST");

            JSONObject obj = new JSONObject();
            obj.put("id", 101);
            obj.put("name", "Name of Object1");
            Map<Integer, String> map = new HashMap<>();
            map.put(new Integer(1), "First");
            map.put(new Integer(2), "Second");
            map.put(new Integer(3), "Third");
            obj.put("map", new JSONObject(map));
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            //persist
            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());
            obj = new JSONObject(post.getResponseContent());
            assertEquals(101, obj.getLong("id"));

            try
            {
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+ClassWithSimpleMap.class.getName() + "/101?fetchGroup=all");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());
                Object mapObj = obj.get("map");
                assertTrue(mapObj instanceof JSONObject);
                JSONObject theMap = (JSONObject)mapObj;
                assertEquals(3, theMap.length());
                boolean[] present = new boolean[3];
                present[0] = false;
                present[1] = false;
                present[2] = false;
                Iterator keyIter = theMap.keys();
                while (keyIter.hasNext())
                {
                    Object key = keyIter.next();
                    Object val = theMap.get("" + key);
                    if (val.equals("First"))
                    {
                        present[0] = true;
                    }
                    else if (val.equals("Second"))
                    {
                        present[1] = true;
                    }
                    else if (val.equals("Third"))
                    {
                        present[2] = true;
                    }
                }
                for (int i=0;i<3;i++)
                {
                    assertTrue("Map Key " + i + " is not present on retrieval", present[i]);
                }
            }
            catch (Exception e)
            {
                LOG.error("Exception validating data", e);
                fail("Exception validating data : " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception persisting/checking data", e);
            fail("Exception in persist/check of data : " + e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+ClassWithSimpleMap.class.getName() + "/101");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();

                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }

    /**
     * Check the correct response status when a non-existent object is passed to GET.
     * @throws IOException
     */
    public void testGetOfNonExistentObject() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            ContentExchange get = new ContentExchange();
            get.setURL("http://localhost:"+PORT+"/dn/"+ClassWithStringCollection.class.getName() + "/101?fetchGroup=all");
            get.setMethod("GET");
            client.start();
            client.send(get);
            get.waitForDone();

            assertEquals(404, get.getResponseStatus());
            assertNull(get.getResponseContent());

            get = new ContentExchange();
            get.setURL("http://localhost:"+PORT+"/dn/"+Person.class.getName());
            get.setMethod("GET");
            JSONObject obj = new JSONObject();
            obj.put("globalNum", "global:1786244744");
            obj.put("personNum", 1);
            get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
            client.send(get);
            get.waitForDone();

            assertEquals(404, get.getResponseStatus());
            assertNull(get.getResponseContent());
        }
        catch (Exception e)
        {
            LOG.error("Exception on get of non-existent data", e);
            fail("Exception on get of data : " + e.getMessage());
        }
        finally
        {
        }
    }

    /**
     * Test of 1-N Map using FK.
     * @throws IOException
     */
    public void testOneToManyMapFK() throws IOException
    {
        HttpClient client = new HttpClient();
        try
        {
            // Persist Office plus 2 computers
            ContentExchange post = new ContentExchange();
            post.setURL("http://localhost:"+PORT+"/dn/"+MapFKHolder.class.getName());
            post.setMethod("POST");

            JSONObject obj = new JSONObject();
            obj.put("id", 101);
            obj.put("name", "First Map Holder");
            Map map = new HashMap();
            JSONObject val1 = new JSONObject();
            val1.put("id", 1);
            val1.put("key", "First");
            val1.put("name", "First Value");
            val1.put("description", "The first description");
            map.put("First", val1);
            JSONObject val2 = new JSONObject();
            val2.put("id", 2);
            val2.put("key", "Second");
            val2.put("name", "Second Value");
            val2.put("description", "The second description");
            map.put("Second", val2);
            obj.put("map", map);
            post.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));

            client.start();
            client.send(post);
            post.waitForDone();

            //validate
            assertEquals(201, post.getResponseStatus());
            assertNotNull(post.getResponseContent());

            obj = new JSONObject(post.getResponseContent());
            assertEquals(101, obj.getLong("id"));

            try
            {
                // Retrieve objects to check persistence
                ContentExchange get = new ContentExchange();
                get.setURL("http://localhost:"+PORT+"/dn/"+MapFKHolder.class.getName() + "/101?fetchGroup=all");
                get.setMethod("GET");
                client.send(get);
                get.waitForDone();

                assertEquals(200, get.getResponseStatus());
                assertNotNull(get.getResponseContent());
                obj = new JSONObject(get.getResponseContent());

                Object mapObjs = obj.get("map");
                assertNotNull(mapObjs);
                assertTrue(mapObjs instanceof JSONObject);
                JSONObject mapObj = (JSONObject)mapObjs;
                assertEquals(2, mapObj.length());
                boolean firstPresent = false;
                boolean secondPresent = false;
                Iterator<String> mapKeys = mapObj.keys();
                while (mapKeys.hasNext())
                {
                    String mapKey = mapKeys.next();
                    Object mapVal = mapObj.get(mapKey);
                    assertTrue(mapVal instanceof JSONObject);
                    JSONObject jsonVal = (JSONObject)mapVal;
                    if (mapKey.equals("First"))
                    {
                        assertEquals(1, jsonVal.getLong("id"));
                        assertEquals("First", jsonVal.getString("key"));
                        assertEquals("First Value", jsonVal.getString("name"));
                        assertEquals("The first description", jsonVal.getString("description"));
                        firstPresent = true;
                    }
                    else if (mapKey.equals("Second"))
                    {
                        assertEquals(2, jsonVal.getLong("id"));
                        assertEquals("Second", jsonVal.getString("key"));
                        assertEquals("Second Value", jsonVal.getString("name"));
                        assertEquals("The second description", jsonVal.getString("description"));
                        secondPresent = true;
                    }
                }
                assertTrue(firstPresent);
                assertTrue(secondPresent);
            }
            catch (Exception e)
            {
                LOG.error("Exception in validate", e);
                fail(e.getMessage());
            }
        }
        catch (Exception e)
        {
            LOG.error("Exception in persist", e);
            fail(e.getMessage());
        }
        finally
        {
            try
            {
                ContentExchange delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+MapFKHolder.class.getName() + "/101");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());

                delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+MapFKValue.class.getName() + "/1");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());

                delete = new ContentExchange();
                delete.setURL("http://localhost:"+PORT+"/dn/"+MapFKValue.class.getName() + "/2");
                delete.setMethod("DELETE");
                client.send(delete);
                delete.waitForDone();
                assertEquals(204, delete.getResponseStatus());
                assertNull(delete.getResponseContent());
            }
            catch (Exception e)
            {
                fail(e.getMessage());
            }
        }
    }
}
