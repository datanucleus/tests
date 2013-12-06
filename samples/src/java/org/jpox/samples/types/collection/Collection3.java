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
package org.jpox.samples.types.collection;

import java.util.Random;

import org.jpox.samples.types.container.CollectionHolder;

/**
 * Container object for join Collection serialised tests.
 *
 * @version $Revision: 1.1 $    
 **/
public class Collection3 implements CollectionHolder
{
    private int identifier;

    java.util.Collection items=new java.util.HashSet();

    public Collection3()
    {
        Random r = new Random(0);
        identifier = r.nextInt();
    }

    public java.util.Collection  getItems()
    {
        return items;
    }

    public int getNoOfItems()
    {
        return items.size();
    }

    public void addItem(Object item)
    {
        items.add(item);
    }

    public void addItems(java.util.Collection c)
    {
        items.addAll(c);
    }

    public void removeItem(Object item)
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

    public void clear()
    {
        items.clear();
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public int getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(int identifier)
    {
        this.identifier = identifier;
    }

    public void setItems(java.util.Collection items)
    {
        this.items = items;
    }

    public String toString()
    {
        return getClass().getName() + " : [" + items.size() + " items]";
    }

    public boolean contains(Object value)
    {
        return items.contains(value);
    }

    public boolean containsAll(java.util.Collection values)
    {
        return items.containsAll(values);
    }
}