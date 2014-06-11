/**********************************************************************
Copyright (c) 2010 Andy Jefferson and others. All rights reserved.
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
 */
public class SimpleDependentElement
{
    private int id;

    public SimpleDependentElement()
    {
        super();
    }

    public SimpleDependentElement(int id)
    {
        super();
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean equals(Object arg0)
    {
        if (arg0 == null || !(arg0 instanceof SimpleDependentElement ))
        {
            return false;
        }
        SimpleDependentElement df = (SimpleDependentElement) arg0;
        return this.id == df.id;
    }

    /**
     * Inner class representing Primary Key
     */
    public static class PK implements Serializable
    {
        private static final long serialVersionUID = 8470763171576711388L;
        public int id;

        public PK()
        {
        }

        public PK(String s)
        {
            this.id = Integer.valueOf(s).intValue();
        }

        public String toString()
        {
            return "" + id;
        }

        public int hashCode()
        {
            return (int)id;
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof PK))
            {
                PK otherPK = (PK)other;
                return otherPK.id == this.id;
            }
            return false;
        }
    }
}