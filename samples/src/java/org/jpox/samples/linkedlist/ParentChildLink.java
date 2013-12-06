/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
package org.jpox.samples.linkedlist;

import java.util.HashSet;

/**
 * Class with a set of children (of the same type), and a 1-1 uni relation with the next object in the list.
 *
 * @version $Revision: 1.1 $
 */
public class ParentChildLink
{
    private int id; // May be used as PK
    public String name;
    public ParentChildLink nextObj;
    public HashSet children;

    public ParentChildLink(String name, ParentChildLink next)
    {
        nextObj = next;
        children = new HashSet();

        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public HashSet getChildren()
    {
        return children;
    }

    public void addChild(ParentChildLink child)
    {
        children.add(child);
    }

    public void clearNextObject()
    {
        nextObj = null;
    }
}