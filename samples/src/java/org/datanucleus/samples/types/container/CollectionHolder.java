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
package org.datanucleus.samples.types.container;

import java.util.Collection;


public interface CollectionHolder<E> extends ContainerHolder
{    
    /**
     * Check if contains a value
     * @param value the value to search for
     * @return true if contains
     */
    public boolean contains(E value);

    /**
     * Check if contains a value
     * @param values the values to search for
     * @return true if contains
     */
    public boolean containsAll(Collection<E> values);

    /**
     * Method to add an item to the container.
     * @param item Item to add.
     **/
    public void addItem(E item);
    
    /**
     * Method to add a collection of items to the container.
     * @param c Collection of items to add.
     **/
    public void addItems(Collection<E> c);    
    
    /**
     * Accessor for the items in the container.
     * @return The items in the container.
     **/
    public Collection<E> getItems();
    
    /**
     * Method to remove an item from the container.
     * @param item Item to remove.
     **/
    public void removeItem(E item);

    /**
     * Method to remove a collection of items from the container.
     * @param c Collection of items to remove.
     **/
    public void removeItems(Collection<E> c);

    /**
     * Method to retain a collection of items in the container.
     * @param c Collection of items to retain.
     **/
    public void retainItems(Collection<E> c);
}