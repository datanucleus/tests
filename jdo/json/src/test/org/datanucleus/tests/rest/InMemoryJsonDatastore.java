/**********************************************************************
Copyright (c) 2008 Erik Bengtson and others. All rights reserved.
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
package org.datanucleus.tests.rest;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// The Java class will be hosted at the URI path "/"
@Path("/")
public class InMemoryJsonDatastore {
    

    static Map objects = new Hashtable();

    @Context Response response;

    @POST
    @Path("/{clazz}/{itemid}")
    public void post(@PathParam("clazz") String clazz, @PathParam("itemid") String itemid, String content)
    {
        put(clazz,itemid,content);
    }

    @PUT
    @Path("/{clazz}/{itemid}")
    public void put(@PathParam("clazz") String clazz, @PathParam("itemid") String itemid, String content)
    {
        try
        {
            JSONObject obj = new JSONObject(content);
            update(obj, clazz, itemid);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    @GET
    @Produces("application/json")
    @Path("/{clazz}/{itemid}")
    public String get(@PathParam("clazz") String clazz, @PathParam("itemid") String itemid)
    {
        JSONObject obj;
        obj = (JSONObject)objects.get(clazz + "/" + itemid);
        if (obj==null)
        {
            throw new WebApplicationException(Status.NOT_FOUND.getStatusCode());
        }
        return obj.toString();
    }
    
    @GET
    @Produces("application/json")
    @Path("/{clazz}")
    public String extent(@PathParam("clazz") String clazz)
    {
        JSONArray jsonarray = new JSONArray();
        synchronized (objects)
        {
            Iterator it = objects.keySet().iterator(); 
            while (it.hasNext())
            {
                String key = (String) it.next();
                if(key.startsWith(clazz))
                {
                    jsonarray.put((JSONObject)objects.get(key));
                }
            }
        }
        return jsonarray.toString();
    }

    @DELETE
    @Path("/{clazz}/{itemid}")
    public void delete(@PathParam("clazz") String clazz, @PathParam("itemid") String itemid)
    {
        objects.remove(clazz + "/"+ itemid);
    }
    
    private void update(JSONObject obj, String clazz, String key) throws JSONException
    {
        synchronized (objects)
        {
            JSONObject objectInMap = (JSONObject) objects.get(clazz+ "/"+key);
            if (objectInMap==null)
            {
                objects.put(clazz+ "/"+key, obj);
            }
            else
            {
                String[] names = JSONObject.getNames(obj);
                for( int i=0; i<names.length; i++)
                {
                    try
                    {
                        objectInMap.put(names[i], obj.get(names[i]));
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
