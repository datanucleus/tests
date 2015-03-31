/**********************************************************************
Copyright (c) 2015 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Example of a class with a simple Map (nonPC, nonPC) field.
 */
@PersistenceCapable(table="CLASSWITHSIMPLEMAP")
public class ClassWithSimpleMap
{
    @PrimaryKey
    long id;

    String name;

    @Join(table="CLASSWITHSIMPLEMAP_MAP")
    Map<Integer, String> map = new HashMap<Integer, String>();

    public ClassWithSimpleMap(long id, String name)
    {
        this.id = id;
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

    public Map<Integer, String> getStrings()
    {
        return map;
    }
}
