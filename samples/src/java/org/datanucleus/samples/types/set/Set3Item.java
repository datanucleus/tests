/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others. All rights reserved. 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 


Contributions
    ...
***********************************************************************/
package org.datanucleus.samples.types.set;

import java.io.Serializable;
import java.util.Random;

import org.datanucleus.samples.types.container.AppIdUtilities;

/**
 * An item to store in an FK Set container.
 *
 * @version $Revision: 1.1 $  
 **/
public class Set3Item implements Comparable
{
    private int identifierA;
    private String identifierB;

    protected String name=null;
    protected double value=0.0;
    protected int status=-1;
    protected Set3 container;

    protected Set3Item()
    {
        Random r = new Random(0);
        r.setSeed(AppIdUtilities.getSeed());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public Set3Item(String name, double value, int status)
    {
        this.name   = name;
        this.value  = value;
        this.status = status;
        Random r = new Random(0);
        r.setSeed(AppIdUtilities.getSeed());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public String getName()
    {
        return name;
    }

    public double getValue()
    {
        return value;
    }

    public int getStatus()
    {
        return status;
    }

    public void setContainer(Set3 cont)
    {
        container = cont;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getIdentifierA()
    {
        return identifierA;
    }

    public void setIdentifierA(int identifierA)
    {
        this.identifierA = identifierA;
    }

    public String getIdentifierB()
    {
        return identifierB;
    }

    public void setIdentifierB(String identifierB)
    {
        this.identifierB = identifierB;
    }

    public Set3 getContainer()
    {
        return container;
    }

    public int compareTo(Object o)
    {
        return compareTo((Set3Item)o);
    }

    public int compareTo(Set3Item other)
    {
        if (other.hashCode() > hashCode())
        {
            return -1;
        }
        else if (other.hashCode() == hashCode())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    public int hashCode()
    {
        if (identifierB != null)
        {
            return identifierA ^ identifierB.hashCode();
        }
        else
        {
            return identifierA;
        }
    }

    public boolean equals(Object obj)
    {
        if (obj == null || !(obj instanceof Set3Item))
        {
            return false;
        }

        Set3Item item = (Set3Item)obj;
        if (Double.compare(item.value, value) == 0 &&
            item.status == status && 
            (name == null ? item.name == null : name.equals(item.name)))
        {
            return true;
        }
        return false;
    }

    public String toString()
    {
        return getClass().getName() + " - value=" + value + " [status=" + status + "]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -7746625091903843655L;
        public int identifierA;
        public String identifierB;

        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer token = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = token.nextToken ();
            s = token.nextToken ();
            this.identifierA = Integer.valueOf(s).intValue();
            s = token.nextToken ();
            this.identifierB = s;
        }

        public String toString()
        {
            return this.getClass().getName() + "::"  + identifierA + "::" + identifierB;
        }

        public int hashCode()
        {
            if (identifierB != null)
            {
                return identifierA ^ identifierB.hashCode();
            }
            else
            {
                return identifierA;
            }
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.identifierA == this.identifierA && k.identifierB.equals(this.identifierB);
            }
            return false;
        }
    }
}