/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.compoundidentity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Holder for compound identity relationships.
 */
public class CompoundHolder
{
    private long id;
    private String name;

    private List list1 = new ArrayList();
    private List list2 = new ArrayList();
    private List list3 = new ArrayList();
    private Map map1 = new HashMap(); // 1-N bi map with key stored in value
    private Map map2 = new HashMap(); // 1-N bi map with value stored in key

    public CompoundHolder(String name)
    {
        this.name = name;
    }

    public List getList1()
    {
        return list1;
    }

    public List getList2()
    {
        return list2;
    }

    public List getList3()
    {
        return list3;
    }

    public Map getMap1()
    {
        return map1;
    }

    public Map getMap2()
    {
        return map2;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 3360811259780214732L;
        public long id;
        public String name;

        public Id()
        {
        }

        public Id(String s)
        {
            StringTokenizer token = new StringTokenizer(s, "-");
            this.id = Integer.valueOf(token.nextToken()).intValue();
            this.name = token.nextToken();
        }

        public String toString()
        {
            return "" + id + "-" + name;
        }

        public int hashCode()
        {
            return (int)id ^ name.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.id == this.id && k.name.equals(this.name);
            }
            return false;
        }
    }
}