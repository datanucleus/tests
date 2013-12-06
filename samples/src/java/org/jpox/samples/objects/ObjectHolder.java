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
package org.jpox.samples.objects;

import java.util.Collection;
import java.util.HashSet;

/**
 * Sample class containing Object fields of various types.
 * 
 * @version $Revision: 1.1 $
 */
public class ObjectHolder
{
    long id; // Used for application identity
    String name;
    private Object object1;
    private Object object2;
    private Object object3;
    private Collection set1 = new HashSet();

    public ObjectHolder(String name)
    {
        this.name = name;
    }

    public Object getObject1()
    {
        return object1;
    }

    public Object getObject2()
    {
        return object2;
    }

    public Object getObject3()
    {
        return object3;
    }

    public void setObject1(Object obj)
    {
        object1 = obj;
    }

    public void setObject2(Object obj)
    {
        object2 = obj;
    }

    public void setObject3(Object obj)
    {
        object3 = obj;
    }

    public Collection getSet1()
    {
        return set1;
    }
}