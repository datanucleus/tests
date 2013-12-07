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
 * Master object
 * @version $Revision: 1.1 $
 */
public class Master implements Serializable
{
    private String id;

    private HashSet details = new HashSet();
    private HashSet otherDetails = new HashSet();
    private Circon circon;

    public Master()
    {
    }

    public HashSet getDetails()
    {
        return details;
    }

    public HashSet getOtherDetails()
    {
        return otherDetails;
    }
    
    public void addDetail(Detail detail)
    {
        details.add(detail);
    }

    public void removeDetail(Detail detail)
    {
        details.remove(detail);
    }

    public void addOtherDetail(OtherDetail detail)
    {
        otherDetails.add(detail);
    }

    public void removeOtherDetail(OtherDetail detail)
    {
        otherDetails.remove(detail);
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Circon getCircon()
    {
        return circon;
    }

    public void setCircon(Circon circon)
    {
        this.circon = circon;
    } 
}