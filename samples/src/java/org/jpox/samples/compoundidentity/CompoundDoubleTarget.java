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
 * Sample target class with two PC objects both as part of the PK (1-1 uni relations).
 * @version $Revision: 1.1 $
 */
public class CompoundDoubleTarget
{
    private int id; // PK
    private CompoundHolder holder; // PK
    private CompoundRelated related; // PK

    private double value;

    public CompoundDoubleTarget(CompoundHolder holder, CompoundRelated related, double value)
    {
        super();
        this.holder = holder;
        this.related = related;
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

    public final double getValue()
    {
        return value;
    }

    public final void setValue(double value)
    {
        this.value = value;
    }

    public final CompoundHolder getHolder()
    {
        return holder;
    }

    public final void setHolder(CompoundHolder hld)
    {
        this.holder = hld;
    }

    public final CompoundRelated getRelated()
    {
        return related;
    }

    public final void setRelated(CompoundRelated rel)
    {
        this.related = rel;
    }

    public static class Id implements Serializable
    {
        public int id;
        public CompoundHolder.Id holder;
        public CompoundRelated.Id related;

        public Id()
        {
        }

        public Id(String s)
        {
            StringTokenizer token = new StringTokenizer(s,"::");
            
            this.id = Integer.valueOf(token.nextToken()).intValue();
            this.holder = new CompoundHolder.Id(token.nextToken());
            this.related = new CompoundRelated.Id(token.nextToken());
        }

        public String toString()
        {
            return "" + id + "::" + holder.toString() + "::" + related.toString();
        }

        public int hashCode()
        {
            return id ^ holder.hashCode() ^ related.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.id == this.id && this.holder.equals(k.holder) && this.related.equals(k.related);
            }
            return false;
        }
    }
}