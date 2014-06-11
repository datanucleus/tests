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
2004 Andy Jefferson - added PK initialisation
    ...
**********************************************************************/
package org.jpox.samples.abstractclasses;

import java.io.Serializable;

/**
 * Sample Abstract class with composite identity. Extended by AbstractCompositeSub1, AbstractCompositeSub2.
 * 
 * @version $Revision: 1.1 $
 */
public abstract class AbstractCompositeBase
{
    public int id;
    public String name;

    public String baseField;

    public AbstractCompositeBase(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getBaseField()
    {
        return baseField;
    }

    public void setBaseField(String baseField)
    {
        this.baseField = baseField;
    }    

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -4317945971814944582L;
        public int id;
        public String name;
        public Oid()
        {
        }

        public Oid(String s)
        {
            java.util.StringTokenizer toke = new java.util.StringTokenizer (s, "::");
    		//ignore first token
    		s = toke.nextToken ();
    		s = toke.nextToken ();
    		this.id = Integer.valueOf(s).intValue();
    		s = toke.nextToken ();
    		this.name = s;
        }

        public String toString()
        {
            return this.getClass().getName() + "::"  + id + "::" + name;
        }

        public int hashCode()
        {
            return id ^ name.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.id == this.id && k.name.equals(this.name);
            }
            return false;
        }
    }
}