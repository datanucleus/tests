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
public class CompoundX3
{
    protected String x3Task; // PK
    protected CompoundX2 x2; // PK

    protected Set setX4 = new HashSet();

    protected CompoundX3()
    {
    }

    public CompoundX3(String task, CompoundX2 x2)
    {
        this.x3Task = task;
        this.x2 = x2;
    }

    public String getX3Task()
    {
        return x3Task;
    }

    public CompoundX2 getX2()
    {
        return x2;
    }

    public Set getSetX4()
    {
        return setX4;
    }

    public void setX3Task(String task)
    {
        x3Task = task;
    }

    public void setX2(CompoundX2 x2)
    {
        this.x2 = x2;
    }

    static String separator = "::";
    public static class Id implements Serializable
    {
        private static final long serialVersionUID = 5854970855730619320L;
        public String x3Task;
        public CompoundX2.Id x2;

        public Id()
        {
        }

        public Id(String clave) throws Exception
        {
            String[] partes = clave.split(separator);
            if (partes.length != 5)
                throw new Exception("Error, illegal params");
            this.x3Task = partes[0];
            this.x2 = new CompoundX2.Id(partes[1] + separator + partes[2] + separator + partes[3] + separator + partes[4]);
        }

        public java.lang.String toString()
        {
            java.lang.String str = "";
            str += java.lang.String.valueOf(this.x3Task) + separator;
            str += java.lang.String.valueOf(this.x2.toString());
            return str;
        }

        public int hashCode()
        {
            return this.x3Task.hashCode() ^ this.x2.hashCode();
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
            return ((this.x3Task == null ? objToCompare.x3Task == null : this.x3Task.equals(objToCompare.x3Task)) && 
                    (this.x2 == null ? objToCompare.x2 == null : this.x2.equals(objToCompare.x2)));
        }
    }
}