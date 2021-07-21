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
package org.datanucleus.samples.linkedlist;

/**
 * Sample object that is singly-linked with objects of its own type.
 * 
 * @version $Revision: 1.1 $
 */
public class SingleLink
{
    long id;
    String name;
    SingleLink front;

    public SingleLink(int id, String name)
    {
        this.name = name;
    }

    public void setFront(SingleLink front)
    {
        this.front = front;
    }

    public SingleLink getFront()
    {
        return front;
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