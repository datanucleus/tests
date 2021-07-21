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


Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.types.collection;

import java.io.Serializable;
import java.util.Random;

import org.datanucleus.samples.types.container.CollectionHolder;

/**
 * Container object for FK Collection tests.
 */
public class Collection2 implements CollectionHolder<Collection2Item>
{
    private int identifierA;
    private String identifierB;

    java.util.Collection<Collection2Item> items = new java.util.HashSet<>();

    public Collection2()
    {
        Random r = new Random(0);
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public java.util.Collection<Collection2Item> getItems()
    {
        return items;
    }

    public int getNoOfItems()
    {
        return items.size();
    }

    public void addItem(Collection2Item item)
    {
        items.add(item);
    }

    public void addItems(java.util.Collection<Collection2Item> c)
    {
        items.addAll(c);
    }

    public void removeItem(Collection2Item item)
    {
        items.remove(item);
    }

    public void removeItems(java.util.Collection<Collection2Item> c)
    {
        items.removeAll(c);
    }

    public void retainItems(java.util.Collection<Collection2Item> c)
    {
        items.retainAll(c);
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

    public void setItems(java.util.Collection<Collection2Item> items)
    {
        this.items = items;
    }

    public String toString()
    {
        return getClass().getName() + " : [" + items.size() + " items]";
    }

    public boolean contains(Collection2Item value)
    {
        return items.contains(value);
    }

    public boolean containsAll(java.util.Collection<Collection2Item> values)
    {
        return items.containsAll(values);
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -9191362483078424212L;
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