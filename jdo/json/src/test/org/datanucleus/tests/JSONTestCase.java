package org.datanucleus.tests;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JSONTestCase extends JDOPersistenceTestCase
{
    static Server server;

    public JSONTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        if (storeMgr.getStoreManagerKey().equals("json"))
        {
            if (server == null)
            {
                server = new Server(21212);
                Context root = new Context(server,"/",Context.SESSIONS);
                Map params = new HashMap();
                params.put("com.sun.jersey.config.property.packages", "org.datanucleus.tests.rest");
                root.setInitParams(params);
                ServletHolder holder = new ServletHolder(ServletContainer.class);
                holder.setInitParameters(params);
                root.addServlet(holder, "/*");
                server.start();
                holder.start();
                while(!holder.isStarted())
                {
                    Thread.sleep(2000);
                }
            }
        }
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        /*
        if (server !=null)
        {
            server.stop();
            server = null;
        }
        */
    }
}