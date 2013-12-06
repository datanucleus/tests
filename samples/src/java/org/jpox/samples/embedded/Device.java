/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved. 
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
package org.jpox.samples.embedded;

/**
 * Representation of a Device on a network.
 */
public class Device
{
    private String name;

    private String ipAddr;

    private String description;

    private Network network;

    public Device(String name,
                  String addr,
                  String description,
                  Network network)
    {
        this.name = name;
        this.ipAddr = addr;
        this.description = description;
        this.network = network;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getName()
    {
        return name;
    }

    public String getIPAddress()
    {
        return ipAddr;
    }

    public Network getNetwork()
    {
        return network;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setIPAddress(String addr)
    {
        this.ipAddr = addr;
    }

    public int hashCode()
    {
        return (name != null ? name.hashCode() : 0) ^ 
            (ipAddr != null ? ipAddr.hashCode() : 0) ^ 
            (description != null ? description.hashCode() : 0);
    }

    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (!(other instanceof Device))
        {
            return false;
        }
        Device dev2 = (Device)other;
        if (stringEquals(ipAddr, dev2.ipAddr) && stringEquals(name, dev2.name) && stringEquals(description, dev2.description))
        {
            return true;
        }

        return false;
    }

    private boolean stringEquals(String str1, String str2)
    {
        if ((str1 == null && str2 != null) || (str1 != null && str2 == null))
        {
            return false;
        }
        if (str1 == null && str2 == null)
        {
            return true;
        }
        if (str1.equals(str2))
        {
            return true;
        }
        return false;
    }

    public String toString()
    {
        return name + " (" + ipAddr + ")";
    }
}