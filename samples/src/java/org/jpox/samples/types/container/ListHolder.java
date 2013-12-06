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
package org.jpox.samples.types.container;

public interface ListHolder extends CollectionHolder
{
    /**
     * Accessor for an item in the container.
     * @param position The position in the container.
     * @return The item
     **/
    public Object getItem(int position);

    /**
     * Method to retrieve the number of items in the container.
     * @return   The number of items in the container.
     **/
    public int getNoOfItems();

    /**
     * Method to add an item to the container.
     * @param item Item to add.
     * @param position The position
     **/
    public void addItem(Object item, int position);

    /**
     * Method to remove an item from the container at a position.
     * @param position The position to remove
     **/
    public void removeItem(int position);
}