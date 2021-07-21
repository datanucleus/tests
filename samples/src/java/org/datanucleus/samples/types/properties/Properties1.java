/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others.
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
package org.datanucleus.samples.types.properties;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.datanucleus.samples.types.container.MapHolder;

import java.util.Random;
import java.util.Set;

/**
 * Container object for Properties.
 **/
public class Properties1 implements MapHolder<String, String>
{
    private int identifierA;
    private String identifierB;

    java.util.Properties items=new java.util.Properties();

    public Properties1()
    {
        Random r = new Random(System.currentTimeMillis());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public java.util.Map<String, String> getItems()
    {
        Map<String, String> dummy = new HashMap<>();
        for (Entry<Object, Object> entry : items.entrySet())
        {
            dummy.put("" + entry.getKey(), "" + entry.getValue());
        }
        return dummy;
    }

    public int getNoOfItems()
    {
        return items.size();
    }

    public String getItem(String key)
    {
        return items.getProperty(key);
    }

    public Set<Entry<String, String>> getEntrySet()
    {
        Map<String, String> dummy = new HashMap<>();
        for (Entry<Object, Object> entry : items.entrySet())
        {
            dummy.put("" + entry.getKey(), "" + entry.getValue());
        }
        return dummy.entrySet();
    }

    public Set<String> getKeySet()
    {
        Set<String> keys = new HashSet<>();
        Collection itemsKeys = items.keySet();
        for (Object key : itemsKeys)
        {
            keys.add("" + key);
        }
        return keys;
    }    

    public Collection<String> getValues()
    {
        Collection<String> vals = new HashSet<>();
        Collection itemsVals = items.values();
        for (Object val : itemsVals)
        {
            vals.add("" + val);
        }
        return vals;
    }

    public void putItem(String key,String item)
    {
        items.setProperty(key, item);
    }

    public void putItems(java.util.Map<String, String> m)
    {
        items.putAll(m);
    }

    public void removeItem(String key)
    {
        items.remove(key);
    }

    public void clear()
    {
        items.clear();
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public int getIdentifierA()
    {
        return identifierA;
    }

    public void setIdentifierA(int identifierA)
    {
        this.identifierA = identifierA;
    }

    public String getIdentifierB()
    {
        return identifierB;
    }

    public void setIdentifierB(String identifierB)
    {
        this.identifierB = identifierB;
    }

    public void setItems(java.util.Map<String, String> items)
    {
        this.items.clear();
        this.items.putAll(items);
    }

    public String toString()
    {
        return getClass().getName() + " : [" + items.size() + " items]";
    }

    public boolean containsKey(String key)
    {
        return items.containsKey(key);
    }

    public boolean containsValue(String value)
    {
        return items.containsValue(value);
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -1672624205431254112L;
        public int identifierA;
        public String identifierB;

        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = token.nextToken ();
            s = token.nextToken ();
            this.identifierA = Integer.valueOf(s).intValue();
            s = token.nextToken ();
            this.identifierB = s;
        }

        public String toString()
        {
            return this.getClass().getName() + "::"  + identifierA + "::" + identifierB;
        }

        public int hashCode()
        {
            if (identifierB != null)
            {
                return identifierA ^ identifierB.hashCode();
            }
            else
            {
                return identifierA;
            }
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.identifierA == this.identifierA && k.identifierB.equals(this.identifierB);
            }
            return false;
        }
    }     
}