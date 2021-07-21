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

/**
 * Object in 4 link chain of compound identities.
 * @version $Revision$
 */
public class CompoundX4
{
    protected String x4Name; // PK
    protected CompoundX3 x3; // PK

    public CompoundX4()
    {
    }

    public CompoundX4(String name, CompoundX3 x3)
    {
        this.x3 = x3;
        x4Name = name;
    }

    public CompoundX3 getX3()
    {
        return x3;
    }

    public String getX4Name()
    {
        return x4Name;
    }

    public void setX3(CompoundX3 x3)
    {
        this.x3 = x3;
    }

    public void setX4Name(String name)
    {
        x4Name = name;
    }

    static String separator = "::";
    public static class Id implements Serializable
    {
        private static final long serialVersionUID = -8350837627647190112L;
        public String x4Name;
        public CompoundX3.Id x3;

        public Id()
        {
        }

        public Id(String clave) throws Exception
        {
            String[] partes = clave.split(separator);
            if (partes.length != 6)
                throw new Exception("Error, par�metros ilegales");

            this.x4Name = partes[0];
            this.x3 = new CompoundX3.Id(partes[1] + separator + partes[2] + separator + partes[3] + separator + partes[4] + separator + partes[5]);
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.x4Name) + separator;
            str += java.lang.String.valueOf(this.x3.toString());
            return str;
        }

        public int hashCode()
        {
            return this.x4Name.hashCode() ^ this.x3.hashCode();
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
            return ((this.x4Name == null ? objToCompare.x4Name == null : this.x4Name.equals(objToCompare.x4Name)) && 
                    (this.x3 == null ? objToCompare.x3 == null : this.x3.equals(objToCompare.x3)));
        }
    }
}