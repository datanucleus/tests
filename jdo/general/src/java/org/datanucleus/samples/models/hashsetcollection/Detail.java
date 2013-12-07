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
import java.io.Serializable;

/**
 * Detail object
 * @version $Revision: 1.1 $
 */
public class Detail implements Serializable
{
    private String id;
    private Master master;
    private HashSet circons = new HashSet();
    
    public Detail()
    {
    }

    public HashSet getCircons()
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
