/**********************************************************************
Copyright (c) 2006 Erik Bengtson and others. All rights reserved.
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
 * Abstract class with compound identity.
 * Used to test inheritance and compound relationships.
 */
public abstract class CompoundAbstractBase implements Serializable
{
    private static final long serialVersionUID = 4545756491773985841L;
    private CompoundRelated related; // PK
    private String name; // PK

    public CompoundAbstractBase(CompoundRelated related, String name)
    {
        this.related = related;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public CompoundRelated getRelated()
    {
        return related;
    }

    public void setRelated(CompoundRelated rel)
    {
        this.related = rel;
    }

    public abstract String getValue();

    public static class Id implements Serializable
    {
        private static final long serialVersionUID = -5614234543789506821L;
        public CompoundRelated.Id related;
        public String name;

        public Id()
        {
        }

        public Id(String id)
        {
            StringTokenizer st = new StringTokenizer(id, "::");
            this.related = new CompoundRelated.Id(st.nextToken());
            this.name = st.nextToken();
        }

        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null)
            {
                return false;
            }
            if (o.getClass() != getClass())
            {
                return false;
            }

            Id other = (Id) o;
            return (this.name.equals(other.name) && this.related.equals(other.related));
        }

        public int hashCode()
        {
            return (name.hashCode() ^ related.hashCode());
        }

        public String toString()
        {
            return (this.related.toString() + "::" + this.name);
        }
    }
}