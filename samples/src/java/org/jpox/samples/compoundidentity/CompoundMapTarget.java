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
package org.jpox.samples.compoundidentity;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Sample target class with another PC object as part of the PK (1-N map bi relation).
 * @version $Revision: 1.1 $
 */
public class CompoundMapTarget
{
    private String name; // PK
    private CompoundHolder holder; // PK

    private double value;

    public CompoundMapTarget(String name, CompoundHolder source, double value)
    {
        super();
        this.name = name;
        this.holder = source;
        this.value = value;
    }

    public final String getName()
    {
        return name;
    }

    public final void setName(String name)
    {
        this.name = name;
    }

    public final CompoundHolder getHolder()
    {
        return holder;
    }

    public final void setHolder(CompoundHolder holder)
    {
        this.holder = holder;
    }

    public final double getValue()
    {
        return value;
    }

    public final void setValue(double value)
    {
        this.value = value;
    }

    public static class Id implements Serializable
    {
        public String name;
        public CompoundHolder.Id holder;

        public Id()
        {
        }

        public Id(String s)
        {
            StringTokenizer token = new StringTokenizer(s,"::");
            this.name = token.nextToken();
            this.holder = new CompoundHolder.Id(token.nextToken());
        }

        public String toString()
        {
            return name + "::" + holder.toString();
        }

        public int hashCode()
        {
            return name.hashCode() ^ holder.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.name.equals(this.name) && this.holder.equals(k.holder);
            }
            return false;
        }
    }
}