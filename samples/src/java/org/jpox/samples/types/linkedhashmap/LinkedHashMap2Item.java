/**********************************************************************
Copyright (c) 2005 Andy Jefferson and others.
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
package org.jpox.samples.types.linkedhashmap;

import java.io.Serializable;
import java.util.Random;

import org.jpox.samples.types.container.AppIdUtilities;

/**
 * An item to store in an LinkedHashMap container.
 *
 * @version $Revision: 1.1 $  
 **/
public class LinkedHashMap2Item
{
    private int identifierA;
    private String identifierB;

    protected String name=null;
    protected double value=0.0;
    protected int status=-1;
    protected LinkedHashMap2 container;
    protected String key;

    protected LinkedHashMap2Item()
    {
        Random r = new Random(0);
        r.setSeed(AppIdUtilities.getSeed());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public LinkedHashMap2Item(String name, double value, int status)
    {
        this.name   = name;
        this.value  = value;
        this.status = status;
        Random r = new Random(0);
        r.setSeed(AppIdUtilities.getSeed());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public boolean equals(Object arg0)
    {
        if (arg0 == null || !(arg0 instanceof LinkedHashMap2Item))
        {
            return false;
        }
        LinkedHashMap2Item item = (LinkedHashMap2Item)arg0;
        if (Double.compare(item.value,value)==0 && item.status == status && 
            (name == null ? item.name == null : name.equals(item.name)))
        {
            return true;
        }
        return false;
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

    public LinkedHashMap2 getContainer()
    {
        return container;
    }

    public void setContainer(LinkedHashMap2 container)
    {
        this.container = container;
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

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String toString()
    {
        return getClass().getName() + " - value=" + value + " [status=" + status + "]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -5013376131413935895L;
        public int identifierA;
        public String identifierB;

        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer toke = new java.util.StringTokenizer (s, "::");
            //ignore first token
            s = toke.nextToken ();
            s = toke.nextToken ();
            this.identifierA = Integer.valueOf(s).intValue();
            s = toke.nextToken ();
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