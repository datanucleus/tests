/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.dependentfield;

import java.io.Serializable;

/**
 * Related object for dependent field testing.
 * @version $Revision: 1.1 $
 */
public class DependentElement
{
    private int id;
    String name;
    DependentHolder owner;
    DependentElement key;

    public DependentElement()
    {
        super();
    }

    public DependentElement(int id,String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public final DependentHolder getOwner()
    {
        return owner;
    }

    public final void setOwner(DependentHolder owner)
    {
        this.owner = owner;
    }

    public DependentElement getKey()
    {
        return key;
    }

    public void setKey(DependentElement key)
    {
        this.key = key;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public final String getName()
    {
        return name;
    }

    public final void setName(String name)
    {
        this.name = name;
    }

	public boolean equals(Object arg0)
	{
	    if( arg0 == null || !(arg0 instanceof DependentElement ))
	    {
	        return false;
	    }
	    DependentElement df = (DependentElement) arg0;
	    return this.id == df.id;
	}

	public static class Id implements Serializable
    {
        public int id;

        public Id()
        {
        }

        public Id(String s)
        {
            this.id = Integer.valueOf(s).intValue();
        }

        public String toString()
        {
            return "" + id;
        }

        public int hashCode()
        {
            return id;
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.id == this.id;
            }
            return false;
        }
    }    
}