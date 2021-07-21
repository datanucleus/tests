/**********************************************************************
Copyright (c) 2013 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.one_many.map_fk;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder of a Map stored by FK, with the key a field in the value.
 */
public class MapFKHolder
{
    private long id; // Used for app identity
    private String name;

    private Map<String, MapFKValue> map;

    public MapFKHolder()
    {
    }

    public MapFKHolder(long id)
    {
        this.id = id;
    }

    public MapFKHolder(String name)
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

    public Map<String, MapFKValue> getMap()
    {
        if (map == null)
        {
            map = new HashMap<String, MapFKValue>();
        }
        return map;
    }
}