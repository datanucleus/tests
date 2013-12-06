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
package org.jpox.samples.array;

import java.io.Serializable;

/**
 * Sample object that is stored in an array.
 *
 * @version $Revision: 1.2 $  
 */
public class ArrayElement implements Cloneable, Serializable
{
    protected String id = null;
    protected String name = null;

    public ArrayElement(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            return null;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        ArrayElement other = (ArrayElement)obj;
        return id.equals(other.id) && name.equals(other.name);
    }

    public String toString()
    {
        return "ArrayElement [id=" + id + ", name=" + name + "]";
    }
}