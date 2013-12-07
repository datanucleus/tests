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
package org.datanucleus.samples.detach.fetchdepth;

import java.util.HashSet;

/**
 * Sample representation of a directory.
 * Used to demonstrate fetching of objects and the use of recursive fetch policies.
 */
public class Directory
{
    String name;
    Directory parent;
    HashSet children = new HashSet();

    public Directory(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setParent(Directory parent)
    {
        this.parent = parent;
    }

    public void addChild(Directory child)
    {
        children.add(child);
    }

    public void clearChildren()
    {
        children.clear();
    }

    public Directory getParent()
    {
        return parent;
    }

    public HashSet getChildren()
    {
        return children;
    }

    public String toString()
    {
        return "Directory '" + name + "' : parent=" + (parent != null ? parent.getName() : "(null)") + " - " + children.size() + " children";
    }
}