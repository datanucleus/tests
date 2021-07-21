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


Contributors:
    ...
**********************************************************************/
package org.datanucleus.samples.types.treeset;

import java.util.Comparator;

import org.datanucleus.samples.types.container.ContainerItem;

/**
 * Example of a hand-written comparator.
 * 
 * @version $Revision: 1.1 $
 */
public class MyComparator implements Comparator
{
    /**
     * Default constructor.
     */
    public MyComparator()
    {
        // Nothing to do
    }

    /**
     * Method defining the ordering of objects.
     * Places all nulls at the end, and orders based on SCONormalItem otherwise.
     * @param o1 First object
     * @param o2 Second object
     * @return The comparison result
     */
    public int compare(Object o1, Object o2)
    {
        if (o1 == null && o2 == null)
        {
            return 0;
        }
        else if (o1 == null && o2 != null)
        {
            return -1;
        }
        else if (o1 != null && o2 == null)
        {
            return -1;
        }
        ContainerItem obj1 = (ContainerItem)o1;
        ContainerItem obj2 = (ContainerItem)o2;
        return obj1.compareTo(obj2);
    }
}