/**********************************************************************
Copyright (c) 21-Apr-2004 Andy Jefferson and others.
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
package org.jpox.samples.types.hashmap;

import java.io.Serializable;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.jpox.samples.types.container.MapHolder;

/**
 * Container object for HashMap tests.
 **/
public class HashMap2 implements MapHolder<String, HashMap2Item>
{
	private static Random r = new Random(0);
	
    private int identifierA;
    private String identifierB;

    java.util.HashMap<String, HashMap2Item> items=new java.util.HashMap<>();

    public HashMap2()
    {
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public java.util.Map<String, HashMap2Item> getItems()
    {
        return items;
    }

    public int getNoOfItems()
    {
        return items.size();
    }

    public HashMap2Item getItem(String key)
    {
        return items.get(key);
    }

    public Set<Entry<String, HashMap2Item>> getEntrySet()
    {
        return items.entrySet();
    }

    public Set<String> getKeySet()
    {
        return items.keySet();
    }    

    public Collection<HashMap2Item> getValues()
    {
        return items.values();
    }

    public void putItem(String key,HashMap2Item item)
    {
        items.put(key,item);
    }

    public void putItems(java.util.Map<String, HashMap2Item> m)
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

    public void setItems(java.util.Map<String, HashMap2Item> items)
    {
        this.items.clear();
        this.items.putAll(items);
    }

    public String toString()
    {
        return "HashMapInverse : [" + items.size() + " items]";
    }

    public boolean containsKey(String key)
    {
        return items.containsKey(key);
    }

    public boolean containsValue(HashMap2Item value)
    {
        return items.containsValue(value);
    }
    
    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = 1970047877903329016L;
        public int identifierA;
        public String identifierB;

        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer toke = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = toke.nextToken ();
            s = toke.nextToken ();
            this.identifierA = Integer.valueOf(s).intValue();
            s = toke.nextToken ();
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