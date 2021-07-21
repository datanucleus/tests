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
import java.util.HashSet;
import java.util.Set;

/**
 * Object in 4 link chain of compound identities.
 * @version $Revision$
 */
public class CompoundX2
{
    protected String x2NameA; // PK
    protected String x2NameB; // PK
    protected CompoundX1 x1; // PK

    protected Set setX3 = new HashSet();

    protected CompoundX2() {}

    public CompoundX2(String name1, String name2, CompoundX1 x1)
    {
        this.x2NameA = name1;
        this.x2NameB = name2;
        this.x1 = x1;
    }

    public String getX2NameA()
    {
        return x2NameA;
    }

    public String getX2NameB()
    {
        return x2NameB;
    }

    public CompoundX1 getX1()
    {
        return x1;
    }

    public Set getSetX3()
    {
        return setX3;
    }

    public void setX2NameA(String name)
    {
        x2NameA = name;
    }

    public void setX2NameB(String name)
    {
        x2NameB = name;
    }

    public void setX1(CompoundX1 x1)
    {
        this.x1 = x1;
    }

    static String separator = "::";
    public static class Id implements Serializable
    {
        private static final long serialVersionUID = -5943912600526768428L;
        public String x2NameA;
        public String x2NameB;
        public CompoundX1.Id x1;

        public Id()
        {
        }

        public Id(String clave) throws Exception
        {
            String[] partes = clave.split(separator);
            if (partes.length != 4)
                throw new Exception("Error, illegal params");
            this.x2NameA = partes[0];
            this.x2NameB = partes[1];
            this.x1 = new CompoundX1.Id(partes[2] + separator + partes[3]);
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.x2NameA) + separator;
            str += java.lang.String.valueOf(this.x2NameB) + separator;
            str += java.lang.String.valueOf(this.x1.toString());
            return str;
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
            return ((this.x2NameA == null ? objToCompare.x2NameA == null : this.x2NameA.equals(objToCompare.x2NameA)) && 
                    (this.x2NameB == null ? objToCompare.x2NameB == null : this.x2NameB.equals(objToCompare.x2NameB)) && 
                    (this.x1 == null ? objToCompare.x1 == null : this.x1.equals(objToCompare.x1)));
        }

        public int hashCode()
        {
            return this.x2NameA.hashCode() ^ this.x2NameB.hashCode() ^ this.x1.hashCode();
        }
    }
}