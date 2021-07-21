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
package org.datanucleus.samples.compoundidentity;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Related object for compound identity relationships.
 * @version $Revision: 1.1 $
 */
public class CompoundRelated
{
    private long id;
    private String name;

    public CompoundRelated(String name)
    {
        this.name = name;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 1763677284291657521L;
        public long id;
        public String name;

        public Id()
        {
        }

        public Id(String s)
        {
            StringTokenizer token = new StringTokenizer(s, "_");
            this.id = Integer.valueOf(token.nextToken()).intValue();
            this.name = token.nextToken();
        }

        public String toString()
        {
            return "" + id + "_" + name;
        }

        public int hashCode()
        {
            return (int)id ^ name.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Id))
            {
                Id k = (Id)other;
                return k.id == this.id && k.name.equals(this.name);
            }
            return false;
        }
    }
}