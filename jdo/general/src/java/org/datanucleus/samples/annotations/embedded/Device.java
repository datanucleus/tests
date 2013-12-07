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
package org.datanucleus.samples.annotations.embedded;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a Device on a network.
 */
@PersistenceCapable(detachable="true")
@EmbeddedOnly
public class Device
{
    @Persistent
    private String name;

    @Persistent
    private String ipAddr;

    @Persistent(defaultFetchGroup="false")
    private String description;

    @Persistent
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
        return toString().hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof Device))
        {
            return false;
        }
        return toString().equals(obj.toString());
    }

    public String toString()
    {
        return name + " (" + ipAddr + ")";
    }
}