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

public interface ContainerHolder
{
    /**
     * Method to retrieve the number of items in the map.
     * @return The number of items in the map.
     **/
    public int getNoOfItems();
    
    /**
     * Method to clear the map.
     **/
    public void clear();

    /**
     * Check if there are no items in the map.
     * @return true if is empty
     **/
    public boolean isEmpty();    
}