/**********************************************************************
Copyright (c) 2005 Maciej Wegorkiewicz and others.
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
package org.datanucleus.samples.models.hashsetcollection;

import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

/**
 * Detail object
 */
public class Detail implements Serializable
{
    private static final long serialVersionUID = -1948694805364569431L;
    private String id;
    private Master master;
    private Set<Circon> circons = new HashSet<>();
    
    public Detail()
    {
    }

    public Set<Circon> getCircons()
    {
        return circons;
    }
    public void addCircon(Circon circon)
    {
        circons.add(circon);
    }

    public void removeCircon(Circon circon)
    {
        circons.remove(circon);
    }

    public Master getMaster()
    {
        return master;
    }
    
    public void setMaster(Master master)
    {
        this.master=master;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
}
