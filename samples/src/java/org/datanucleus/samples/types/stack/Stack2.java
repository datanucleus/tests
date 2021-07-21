/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.stack;

import java.io.Serializable;
import java.util.Random;

import org.datanucleus.samples.types.container.ListHolder;

/**
 * Container object for FK Stack tests.
 */
public class Stack2 implements ListHolder<Stack2Item>
{
    private int identifierA;
    private String identifierB;

    java.util.Stack<Stack2Item> items=new java.util.Stack<>();

    public Stack2()
    {
        Random r = new Random(0);
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public java.util.Collection<Stack2Item> getItems()
    {
        return items;
    }

    public Stack2Item getItem(int position)
    {
        return items.get(position);
    }

    public int getNoOfItems()
    {
        return items.size();
    }

    public void addItem(Stack2Item item)
    {
        items.add(item);
    }

    public void addItems(java.util.Collection<Stack2Item> c)
    {
        items.addAll(c);
    }

    public void removeItem(Stack2Item item)
    {
        items.remove(item);
    }

    public void removeItems(java.util.Collection c)
    {
        items.removeAll(c);
    }

    public void retainItems(java.util.Collection c)
    {
        items.retainAll(c);
    }

    public void removeItem(int position)
    {
        if (position < items.size() &&
            position >= 0)
        {
            items.remove(position);
        }
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

    public void setItems(java.util.Collection<Stack2Item> items)
    {
        this.items.clear();
        this.items.addAll(items);
    }

    public boolean contains(Stack2Item value)
    {
        return items.contains(value);
    }

    public boolean containsAll(java.util.Collection<Stack2Item> values)
    {
        return items.containsAll(values);
    }

    public void addItem(Stack2Item item, int position)
    {
        this.items.add(position,item);        
    }

    public String toString()
    {
        return getClass().getName() + " : [" + items.size() + " items]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = 8861806166562701150L;
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