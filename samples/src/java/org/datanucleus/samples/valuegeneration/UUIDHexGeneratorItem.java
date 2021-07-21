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
package org.datanucleus.samples.valuegeneration;

import java.io.Serializable;

/**
 * Test case for the "uuid-hex" generator.
 *
 * @version $Revision: 1.1 $  
 */
public class UUIDHexGeneratorItem
{
    private String identifier; // Generated field

    protected String name=null;

    protected UUIDHexGeneratorItem()
    {
    }

    public UUIDHexGeneratorItem(String name)
    {
        this.name   = name;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return "UUIDHexPoidItem : " + name;
    }

    public static class Oid implements Serializable
    {
        private static final long serialVersionUID = -7926057609023921660L;
        public String identifier;

        public Oid()
        {
        }

        public Oid(String s)
        {
            this.identifier = s;
        }

        public String toString()
        {
            return identifier;
        }

        public int hashCode()
        {
            return identifier.hashCode();
        }

        public boolean equals(Object other)
        {
            if (other != null && (other instanceof Oid))
            {
                Oid k = (Oid)other;
                return k.identifier.equals(this.identifier);
            }
            return false;
        }
    }
}