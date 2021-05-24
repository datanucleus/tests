/**********************************************************************
Copyright (c) 2007 Guido Anzuoni and others. All rights reserved.
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
package org.datanucleus.tests.jta.util;

import java.sql.Driver;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * 
 */
public class DataSourceDriverAdapterObjectFactory implements ObjectFactory
{
    public Object getObjectInstance(Object obj, Name n, Context nameCtx, Hashtable environment) 
    throws Exception
    {
        Reference ref = (Reference) obj;
        String drivercls = null;
        String driverurl = null;
        String username = null;
        String passwd = null;
        Enumeration addrs = ref.getAll();
        while (addrs.hasMoreElements())
        {
            RefAddr addr = (RefAddr) addrs.nextElement();
            String name = addr.getType();
            String value = (String) addr.getContent();
            if (name.equals("driverClassName"))
            {
                drivercls = value;
            }
            else if (name.equals("url"))
            {
                driverurl = value;
            }
            else if (name.equals("username"))
            {
                username = value;
            }
            else if (name.equals("password"))
            {
                passwd = value;
            }
        }
        Driver drv = (Driver) Class.forName(drivercls).newInstance();
        DataSourceDriverAdapter dsda = new DataSourceDriverAdapter();
        dsda.setDriver(drv);
        dsda.setUrl(driverurl);
        dsda.setUsername(username);
        dsda.setPassword(passwd);
        return dsda;
    }
}