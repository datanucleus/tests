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
package org.datanucleus.samples.compoundidentity;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Sample target class with another PC object as part of the PK (1-1/1-N uni relation).
 */
public class CompoundSingleTarget
{
    private int id; // PK
    private CompoundHolder holder; // PK

    private double value;

    public CompoundSingleTarget(CompoundHolder source, double value)
    {
        super();
        this.holder = source;
        this.value = value;
    }

    public final int getId()
    {
        return id;
    }

    public final void setId(int id)
    {
        this.id = id;
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
        private static final long serialVersionUID = -3337471931525836066L;
        public int id;
        public CompoundHolder.Id holder;

        public Id()
        {
        }

        public Id(String s)
        {
            StringTokenizer token = new StringTokenizer(s,"::");
            this.id = Integer.valueOf(token.nextToken()).intValue();
            this.holder = new CompoundHolder.Id(token.nextToken());
        }

        public String toString()
        {
            return "" + id + "::" + holder.toString();
        }

        public int hashCode()
        {
            return id ^ holder.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.id == this.id && this.holder.equals(k.holder);
            }
            return false;
        }
    }
}