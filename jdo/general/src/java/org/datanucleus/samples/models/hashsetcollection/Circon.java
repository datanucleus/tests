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
 * Object for circular references in graph
 * @version $Revision: 1.1 $
 */
public class Circon implements Serializable
{
    private String id;

    private HashSet masters = new HashSet();
    private Detail detail;

    public Circon()
    {
    }

    public HashSet getMasters()
    {
        return masters;
    }
    
    public void addMaster(Master master)
    {
        masters.add(master);
    }

    public void removeMaster(Master master)
    {
        masters.remove(master);
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
   
    public Detail getDetail()
    {
        return detail;
    }
    
    public void setDetail(Detail detail)
    {
        this.detail=detail;
    }
}