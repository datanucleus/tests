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
package org.jpox.samples.compoundidentity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Object in 4 link chain of compound identities.
 * @version $Revision$
 */
public class CompoundX1
{
    protected String x1Name; // PK
    protected long x1Version; // PK

    protected Set setX2 = new HashSet();

    protected CompoundX1()
    {
    }

    public CompoundX1(String name, long version)
    {
        this.x1Name = name;
        this.x1Version = version;
    }

    public String getX1Name()
    {
        return x1Name;
    }

    public long getX1Version()
    {
        return x1Version;
    }

    public Set getSetX2()
    {
        return setX2;
    }

    public void setXaName(String name)
    {
        this.x1Name = name;
    }

    public void setXaVersion(long ver)
    {
        this.x1Version = ver;
    }
 
    static String separator = "::";
    public static class Id implements Serializable
    {
        public String x1Name;
        public long x1Version;

        public Id()
        {
        }

        public Id(java.lang.String clave) throws Exception
        {
            String[] partes = clave.split(separator);
            if (partes.length != 2)
                throw new Exception("Error, parámetros ilegales");
            this.x1Name = partes[0];
            this.x1Version = Long.parseLong(partes[1]);
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.x1Name) + separator;
            str += this.x1Version;
            return str;
        }

        public int hashCode()
        {
            return this.x1Name.hashCode() ^ new Long(this.x1Version).intValue();
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
            Id objToCompare = (Id) o;
            return ((this.x1Name == null ? objToCompare.x1Name == null : this.x1Name.equals(objToCompare.x1Name)) && 
                    (this.x1Version == objToCompare.x1Version));
        }
    }
}