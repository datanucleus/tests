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
package org.jpox.samples.one_many.map_fk;

import java.io.Serializable;

/**
 * Sample persistable item that is stored as the value in a Map, with the key to the map being a field in the superclass.
 */
public class MapFKValue extends MapFKValueBase implements Serializable
{
    private static final long serialVersionUID = 140210967289168477L;

    MapFKHolder holder;

    String name;
    String description;

    public MapFKValue(String key, String name, String description)
    {
        super(key);
        this.name = name;
        this.description = description;
    }

    public MapFKHolder getHolder()
    {
        return holder;
    }

    public void setHolder(MapFKHolder holder)
    {
        this.holder = holder;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }
}