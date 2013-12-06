/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.one_many.map;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample class with various Map fields, testing all combinations of the types of Map field possible.
 * 
 * @version $Revision: 1.3 $
 */
public class MapHolder
{
    private long id; // Used for app identity
    private String name;

    private Map joinMapNonNon; // Map<String,String> using join table
    private Map joinMapNonNon2; // Map<String,String> using join table (in case 2 ways of mapping it)
    private Map joinMapNonPC; // Map<String,MapValueItem> using join table
    private Map joinMapPCNon; // Map<MapKeyitem,String> using join table
    private Map joinMapPCPC; // Map<MapKeyItem,MapValueItem> using join table
    private Map joinMapNonPCSerial; // Map<String,MapValueItem> using join table with value serialised into join table

    private Map fkMapKey; // Map<String,MapFKValueItem> with key stored in value
    private Map fkMapKey2; // Map<String,MapHolder> with key stored in value
    private Map fkMapValue; // Map<MapFKKeyItem,String> with value stored in key

    private Map mapNonNon; // Map<String,String> with no join table specified ... so serialised

    private Map mapSerial; // Map<String,String> serialised into single column

    public MapHolder()
    {
    }

    public MapHolder(long id)
    {
        this.id = id;
    }

    public MapHolder(String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Map getJoinMapNonNon()
    {
        if (joinMapNonNon == null)
        {
            joinMapNonNon = new HashMap();
        }
        return joinMapNonNon;
    }

    public Map getJoinMapNonNon2()
    {
        if (joinMapNonNon2 == null)
        {
            joinMapNonNon2 = new HashMap();
        }
        return joinMapNonNon2;
    }

    public Map getJoinMapNonPC()
    {
        if (joinMapNonPC == null)
        {
            joinMapNonPC = new HashMap();
        }
        return joinMapNonPC;
    }

    public Map getJoinMapPCNon()
    {
        if (joinMapPCNon == null)
        {
            joinMapPCNon = new HashMap();
        }
        return joinMapPCNon;
    }

    public Map getJoinMapPCPC()
    {
        if (joinMapPCPC == null)
        {
            joinMapPCPC = new HashMap();
        }
        return joinMapPCPC;
    }

    public Map getJoinMapNonPCSerial()
    {
        if (joinMapNonPCSerial == null)
        {
            joinMapNonPCSerial = new HashMap();
        }
        return joinMapNonPCSerial;
    }

    public Map getFkMapKey()
    {
        if (fkMapKey == null)
        {
            fkMapKey = new HashMap();
        }
        return fkMapKey;
    }

    public Map getFkMapKey2()
    {
        if (fkMapKey2 == null)
        {
            fkMapKey2 = new HashMap();
        }
        return fkMapKey2;
    }

    public Map getFkMapValue()
    {
        if (fkMapValue == null)
        {
            fkMapValue = new HashMap();
        }
        return fkMapValue;
    }

    public Map getMapNonNon()
    {
        if (mapNonNon == null)
        {
            mapNonNon = new HashMap();
        }
        return mapNonNon;
    }

    public Map getMapSerial()
    {
        if (mapSerial == null)
        {
            mapSerial = new HashMap();
        }
        return mapSerial;
    }
}