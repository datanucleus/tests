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
import java.util.HashSet;

import junit.framework.TestCase;

import org.datanucleus.api.rest.RestServlet;
import org.datanucleus.api.rest.orgjson.JSONArray;
import org.datanucleus.api.rest.orgjson.JSONObject;
import org.datanucleus.samples.ClassUsingDatastoreId;
import org.datanucleus.samples.ClassWithValueStrategy;
import org.datanucleus.util.NucleusLogger;
import org.jpox.samples.embedded.Network;
import org.jpox.samples.models.company.Employee;
import org.jpox.samples.models.company.Person;
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
        	e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
            e.printStackTrace();
            fail(e.getMessage());
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
                get.setRequestContent(new ByteArrayBuffer(obj.toString().getBytes()));
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
            LOG.error("Exception in persist", e);
            fail(e.getMessage());
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
}
