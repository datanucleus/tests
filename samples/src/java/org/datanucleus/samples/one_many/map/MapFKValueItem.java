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
package org.datanucleus.samples.one_many.map;

/**
 * Sample item that is stored as the value in a Map and that has a field used as the Map key.
 * 
 * @version $Revision: 1.1 $
 */
public class MapFKValueItem
{
    long id; // Used for app identity
    String name;
    String description;
    String key;

    public MapFKValueItem(String name, String description, String key)
    {
        this.name = name;
        this.description = description;
        this.key = key;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getKey()
    {
        return key;
    }
}