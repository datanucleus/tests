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
package org.jpox.samples.linkedlist;

/**
 * Sample object that is doubly-linked with objects of its own type.
 * 
 * @version $Revision: 1.1 $
 */
public class DoubleLink
{
    long id;
    String name;
    DoubleLink front;
    DoubleLink back;

    public DoubleLink(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public void setFront(DoubleLink front)
    {
        this.front = front;
    }

    public void setBack(DoubleLink back)
    {
        this.back = back;
    }

    public DoubleLink getFront()
    {
        return front;
    }

    public DoubleLink getBack()
    {
        return back;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}