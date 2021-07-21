/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.types.stack;

import java.io.Serializable;
import java.util.Random;

import org.datanucleus.samples.types.container.AppIdUtilities;

/**
 * An item to store in an FK Stack container.
 *
 * @version $Revision: 1.1 $  
 **/
public class Stack2Item
{
    private int identifierA;
    private String identifierB;

    protected String name=null;
    protected double value=0.0;
    protected int status=-1;
    protected Stack2 container;

    protected Stack2Item()
    {
        Random r = new Random(0);
        r.setSeed(AppIdUtilities.getSeed());
        identifierA = r.nextInt();
        identifierB = String.valueOf(r.nextInt());
    }

    public Stack2Item(String name, double value, int status)
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

    public Stack2 getContainer()
    {
        return container;
    }

    public void setContainer(Stack2 container)
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

    public String toString()
    {
        return getClass().getName() + " - value=" + value + " [status=" + status + "]";
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -3195269670661555446L;
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