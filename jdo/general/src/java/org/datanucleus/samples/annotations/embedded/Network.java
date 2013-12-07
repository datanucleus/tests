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
package org.datanucleus.samples.annotations.embedded;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Representation of a Network of devices, using JDO annotations.
 */
@PersistenceCapable(table="JDO_AN_NETWORK")
public class Network
{
    @Persistent
    private long id; // Used by application identity

    @Persistent
    private String name;

    @Persistent(table="JDO_AN_NETWORK_DEVICES")
    @Element(types=Device.class,
        embeddedMapping=@Embedded(ownerMember="network",
            members={
                @Persistent(name="name", columns=@Column(name="DEVICE_NAME", allowsNull="true")),
                @Persistent(name="ipAddr", columns=@Column(name="DEVICE_IP_ADDR", allowsNull="true"))}))
    @Join(column="NETWORK_ID")
    private Collection<Device> devices = new HashSet<Device>();

    public Network(String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void addDevice(Device device)
    {
        devices.add(device);
    }

    public void removeDevice(Device device)
    {
        devices.remove(device);
    }

    public int getNumberOfDevices()
    {
        return devices.size();
    }

    public boolean containsDevice(Device dev)
    {
        return devices.contains(dev);
    }

    public Device[] getDevices()
    {
        Device[] devs = new Device[devices.size()];
        Iterator iter = devices.iterator();
        int i = 0;
        while (iter.hasNext())
        {
            devs[i++] = (Device)iter.next();
        }
        return devs;
    }

    public String toString()
    {
        return name;
    }
}