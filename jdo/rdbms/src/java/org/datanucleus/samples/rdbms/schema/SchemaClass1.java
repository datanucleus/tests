/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.samples.rdbms.schema;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Test class for schema handling.
 */
public class SchemaClass1
{
    private long id1; // PK
    private long id2; // PK

    private SchemaClass2 other;
    private String name;

    public long getId1()
    {
        return id1;
    }

    public long getId2()
    {
        return id2;
    }

    public SchemaClass2 getOther()
    {
        return other;
    }

    public String getName()
    {
        return name;
    }

    public static class Key implements Serializable
    {
        private static final long serialVersionUID = 8042077487147187057L;
        public long id1;
        public long id2;

        public Key()
        {
        }

        public Key(String str)
        {
            StringTokenizer toke = new StringTokenizer(str, "::");
            str = toke.nextToken();
            this.id1 = Integer.parseInt(str);
            str = toke.nextToken();
            this.id2 = Integer.parseInt(str);
        }

        public boolean equals(Object ob)
        {
            if (this == ob)
            {
                return true;
            }
            if (!(ob instanceof Key))
            {
                return false;
            }
            Key other = (Key) ob;
            return ((this.id1 == other.id1) && (this.id2 == other.id2));
        }

        public int hashCode()
        {
            return (int)this.id1 ^ (int)this.id2;
        }

        public String toString()
        {
            return String.valueOf(this.id1) + "::" + String.valueOf(this.id2);
        }
    }
}