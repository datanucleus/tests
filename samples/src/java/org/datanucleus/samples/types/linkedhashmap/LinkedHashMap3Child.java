/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.types.linkedhashmap;

/**
 * Container object for LinkedHashMap tests using primitive key/value in the Map.
 * This provides testing of an inherited container.
 *
 * @version $Revision: 1.1 $    
 **/
public class LinkedHashMap3Child extends LinkedHashMap3
{
    String name;

    public LinkedHashMap3Child()
    {
        super();
    }

    public LinkedHashMap3Child(String name)
    {
        super();
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return getClass().getName() + " : " + name + " [" + items.size() + " items]";
    }
}