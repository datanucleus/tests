/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.reachability;

import java.util.HashSet;
import java.util.Set;

/**
 * Sample class used for testing reachability.
 * Certain fields are marked as not being persisted by reachability.
 */
public class ReachableHolder
{
    long id; // PK for app id
    String name;

    ReachableItem item1 = null; // Not persisted by reachability

    Set<ReachableItem> set1 = new HashSet(); // Not persisted by reachability

    public ReachableHolder(String name)
    {
        this.name = name;
    }

    public void setItem1(ReachableItem item)
    {
        this.item1 = item;
    }

    public ReachableItem getItem1()
    {
        return item1;
    }

    public Set getSet1()
    {
        return set1;
    }
}