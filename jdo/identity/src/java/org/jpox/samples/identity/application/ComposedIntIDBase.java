/**********************************************************************
Copyright (c) 2005 Erik Bengtson and others. All rights reserved.
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
package org.jpox.samples.identity.application;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.jdo.InstanceCallbacks;

import org.datanucleus.tests.TestObject;

/**
 * Class with identity using two fields of type int.
 * @version $Revision: 1.1 $
 */
public class ComposedIntIDBase extends TestObject implements InstanceCallbacks
{
    private int code; // PK
    private int composed; // PK

    private String name;
    private String description;

    public int getComposed()
    {
        return composed;
    }

    public void setComposed(int composed)
    {
        this.composed = composed;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void jdoPostLoad()
    {
    }

    public void jdoPreStore()
    {
    }

    public void jdoPreClear()
    {
    }

    public void jdoPreDelete()
    {
    }

    public void fillRandom()
    {
        code = r.nextInt();
        composed = r.nextInt();
        fillUpdateRandom();
    }

    public void fillUpdateRandom()
    {
        name = String.valueOf(r.nextDouble() * 1000);
        description = "Description " + this.getClass().toString() + " random: " + String.valueOf(r.nextDouble() * 1000);
    }

    public boolean compareTo(Object obj)
    {
        if (obj == this)
            return true;

        if (!(obj instanceof ComposedIntIDBase))
            return false;

        ComposedIntIDBase other = (ComposedIntIDBase) obj;

        return code == other.code && name.equals(other.name) && composed == other.composed && description.equals(other.description);
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer(super.toString());
        s.append("  code = ").append(code);
        s.append('\n');
        s.append("  name = ").append(name);
        s.append('\n');
        s.append("  composed = ").append(composed);
        s.append('\n');
        s.append("  description = ").append(description);
        s.append('\n');
        return s.toString();
    }

    public static class Key implements Serializable
    {
        public int code;

        public int composed;

        public Key()
        {
        }

        public Key(String str)
        {
            StringTokenizer toke = new StringTokenizer(str, "::");
            str = toke.nextToken();
            this.code = Integer.parseInt(str);
            str = toke.nextToken();
            this.composed = Integer.parseInt(str);
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
            return ((this.code == other.code) && (this.composed == other.composed));
        }

        public int hashCode()
        {
            return this.code ^ this.composed;
        }

        public String toString()
        {
            return String.valueOf(this.code) + "::" + String.valueOf(this.composed);
        }
    }
}